package com.cboe.application.order;

import static com.cboe.application.order.common.UserOrderServiceUtil.checkFinishedMap;
import static com.cboe.application.order.common.UserOrderServiceUtil.checkNewQueue;
import static com.cboe.application.order.common.UserOrderServiceUtil.getFinishedOrderMinLifeTime;
import static com.cboe.application.order.common.UserOrderServiceUtil.processNewOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Collections;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.cboe.application.cache.UserCacheListener;
import com.cboe.application.inprocess.consumer.proxy.OrderStatusConsumerProxy;
import com.cboe.application.order.common.UserOrderServiceUtil;
import com.cboe.application.shared.IOrderAckConstraints;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.supplier.OrderStatusCollectorSupplier;
import com.cboe.application.supplier.OrderStatusCollectorSupplierFactory;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.util.CboeOrderIdStructContainer;
import com.cboe.domain.util.CmiOrderIdStructContainer;
import com.cboe.domain.util.CompleteOrderIdStructContainer;
import com.cboe.domain.util.ExchangeFirmStructContainer;
import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.domain.util.GroupCancelReportContainer;
import com.cboe.domain.util.GroupOrderIdFillReportContainer;
import com.cboe.domain.util.GroupOrderStructContainer;
import com.cboe.domain.util.GroupOrderStructSequenceContainer;
import com.cboe.domain.util.InternalExtensionFields;
import com.cboe.domain.util.OrderIdBustStructContainer;
import com.cboe.domain.util.OrderIdCancelReportContainer;
import com.cboe.domain.util.OrderIdReinstateStructContainer;
import com.cboe.domain.util.OrderIdStructContainerFactory;
import com.cboe.domain.util.OrderQueryExceptionStructContainer;
import com.cboe.domain.util.OrderStructBuilder;
import com.cboe.domain.util.RoutingGroupOrderStructContainer;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiOrder.BustReinstateReportStruct;
import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.inprocess.CasSession;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.OrderStatusConsumer;
import com.cboe.interfaces.application.inprocess.PendingCancelCacheElement;
import com.cboe.interfaces.domain.BaseOrderIdStructContainer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

/**
 * A copy of the original OrderQueryCache which has the behavior used by FIX processing - publishNew on inbound thread
 * as well as async processing without IEC.
 *
 * @author Andy Faibishenko
 */
public class FixOrderQueryCache extends UserCacheListener implements
		IOrderQueryCache {

	// userOrderMap is the map containing only active orders. This is an orderId keyed collection
	private Map<CompleteOrderIdStructContainer, OrderStruct> userOrderMap;
	// userOrderCrossReferenceMap contains mapping between <cboeId or cmiId> and <CompleteOrderIdStructContainer>
	// and is used to xref the userOrderMap
	private Map<BaseOrderIdStructContainer, CompleteOrderIdStructContainer> userOrderCrossReferenceMap;
	// finishedOrderMap contains orders those are fully filled or canceled. This is an orderId keyed collection
	private Map<CompleteOrderIdStructContainer, OrderStruct> finishedOrderMap;
	// finishedOrderQueue contains orders those are fully filled or canceled.
	private Queue<FinishedOrder> finishedOrderQueue;

	private int orderCollectionDefaultSize; //Default size if no configuration size is available

	private static final int DEFAULT_SIZE = 100;
	private static final float DEFAULT_LOAD_FACTOR = (float) 0.75;

	private OrderStatusCollectorSupplier orderQuerySupplier = null;
	private String userId = null;
	private ExchangeFirmStruct firmKey = null;
	private ExchangeFirmStructContainer firmKeyContainer = null;

	// A new map for keeping lockable object for each orderId.
	// This map is protected using a ReadWriteLock.
	private Map<CompleteOrderIdStructContainer, Object> orderLockMap;
	private final ReadWriteLock rwOrderLockMap = new ReentrantReadWriteLock();
	private final Lock rOrderLock = rwOrderLockMap.readLock();
	private final Lock wOrderLock = rwOrderLockMap.writeLock();

	// newMsgQueue is a queue for NEW events.
	private BlockingQueue<ChannelEvent> newMsgQueue;
	private volatile boolean shuttingDown = false;
	//private ProcessThread processThread;
	private CacheCleanupThread cacheCleanupThread;
    private Hashtable cleanupTable = null;

    private final String logPrefix;
	private AtomicReference<ChannelEvent> currentNewEventInProgress = new AtomicReference<ChannelEvent>();
	private AtomicLong newMsgCount = new AtomicLong(0L);
	private AtomicLong processedNewMsgCount = new AtomicLong(0L);
	private List<String> groupMembers;
	private boolean isTradingFirm = false;
	private ConcurrentEventChannelAdapter internalEventChannel;

    public enum OrderState {
		PENDING, CACHED, FAILED, FAILED_MAYBE, NOT_FOUND
	}

	OrderState orderState_PENDING = OrderState.PENDING;
	OrderState orderState_CACHED = OrderState.CACHED;
	OrderState orderState_FAILED = OrderState.FAILED;
	OrderState orderState_FAILED_MAYBE = OrderState.FAILED_MAYBE;
	OrderState orderState_NOT_FOUND = OrderState.NOT_FOUND; // used to return if order not in pending cache

	public static class ProductKeyOrderState {
		volatile OrderState orderState;
		int pKey;

		ProductKeyOrderState(int pKey, OrderState orderState) {
			this.orderState = orderState;
			this.pKey = pKey;
		}
	}

	private ConcurrentHashMap<CmiOrderIdStructContainer, ProductKeyOrderState> orderEntryCache;
	private static final OrderStruct[] EMPTY_OrderStruct_ARRAY = new OrderStruct[0];
	private static final ChannelEvent[] EMPTY_ChannelEvent_ARRAY = new ChannelEvent[0];
	private CasSession fixCasSession;
	private OrderStatusConsumer orderStatusConsumer;
	private OrderStatusConsumerProxy orderStatusConsumerProxy;
	private Map<CmiOrderIdStructContainer, List<PendingCancelCacheElement>> pendingCancelCache;
	private ThreadPoolExecutor inboundMessageExecutor;

    public FixOrderQueryCache(String userId) {
		super(userId);
		this.userId = userId;
        StringBuilder sb = new StringBuilder(userId.length() + 20);
		sb.append("FixOrderQueryCache -> ").append(userId);
		logPrefix = sb.toString();
        initFixOrderQueryCache(userId);
    }

    public FixOrderQueryCache(String userId, Hashtable cleanupTable) {
		super(userId);
		this.userId = userId;
        this.cleanupTable = cleanupTable;
        StringBuilder sb = new StringBuilder(userId.length() + 20);
		sb.append("FixOrderQueryCache -> ").append(userId);
		logPrefix = sb.toString();
        initFixOrderQueryCache(userId);
    }

    public void setCleanupTable(Hashtable cleanupTable) {
        this.cleanupTable = cleanupTable;
        StringBuilder cleanupUpdate = new StringBuilder(logPrefix.length() + 20);
        cleanupUpdate.append(logPrefix).append(" cleanupTable added");
        Log.information(cleanupUpdate.toString());
        initFixOrderQueryCache(userId);
    }

    public Hashtable getCleanupTable() {
        return this.cleanupTable;
    }

	public void setPendingCancelCache(
			Map<CmiOrderIdStructContainer, List<PendingCancelCacheElement>> pendingCancelCache,
			ThreadPoolExecutor executor) {
		this.pendingCancelCache = pendingCancelCache;
		this.inboundMessageExecutor = executor;
	}

	public List<PendingCancelCacheElement> getPendingCancels(
			CmiOrderIdStructContainer orderIdStruct) {
		return pendingCancelCache.get(orderIdStruct);
	}

	public void processPendingCancels(OrderStruct newOrder) {
		// process any pending cancels
		CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory
				.createCmiOrderIdStructContainer(newOrder.orderId);
		List<PendingCancelCacheElement> pendingCancels = getPendingCancels(cmiOrderId);
		if (Log.isDebugOn()) {
			if (pendingCancels == null) {
				Log.debug("No pending cxls for:" + newOrder.orderId.branch
						+ ":" + newOrder.orderId.branchSequenceNumber + " at: "
						+ System.nanoTime());
			} else {
				Log.debug("dispatching " + pendingCancels.size()
						+ "pending cxls for:" + newOrder.orderId.branch + ":"
						+ newOrder.orderId.branchSequenceNumber + " at: "
						+ System.nanoTime());
			}
		}
		if (pendingCancels != null && pendingCancels.size() > 0) {
			//PendingCancelProcessingTask task = new PendingCancelProcessingTask(fixCasSession, pendingCancels);
			//ThreadPoolExecutor executor = fixCasSession.getAppiaAdapter().getInboundExecutorForSession(fixCasSession.getConnectionID());
			if (Log.isDebugOn()) {
				Log.debug("dispatching pending cancel for:"
						+ newOrder.orderId.branch + ":"
						+ newOrder.orderId.branchSequenceNumber + " at: "
						+ System.nanoTime());
			}
			inboundMessageExecutor.execute(fixCasSession
					.createPendingCancelProcessingTask(pendingCancels));
			pendingCancelCache.remove(cmiOrderId);
		}
	}

	/**
	 * Initialize the order collections.
	 */
	private FixOrderQueryCache initFixOrderQueryCache(String userId) {
		initialize(DEFAULT_SIZE);
        // mwm PITS 80100 debug
        StringBuilder mmInit = new StringBuilder(logPrefix.length() + 20);
        mmInit.append(logPrefix).append(" Cache created");
        StringBuilder cleanupThreadData = new StringBuilder(30)
                .append(" [Cleanup: ")
                .append(cacheCleanupThread.getName())
                .append("/")
                .append(cacheCleanupThread.getId())
                .append("]");
        // if (Log.isDebugOn()) {
            mmInit.append(cleanupThreadData);
        // }
        Log.information(mmInit.toString());
        // no need to check - userId will NOT be in cleanupTable
        if (null != cleanupTable) {
            cleanupTable.put(userId, cleanupThreadData.toString());
        } else {
            StringBuilder sb = new StringBuilder(logPrefix.length() + 40);
            sb.append(logPrefix).append(" NO cleanupTable at Cache creation");
            Log.information(sb.toString());
            Log.alarm(sb.toString());
        }
        // mwm PITS 80100 debug
        subscribeOrderQueryCacheForUser(userId);
        return this;
	}

	public void setFixCasSession(CasSession fixCasSession) {
		this.fixCasSession = fixCasSession;
	}

	public void setOrderStatusConsumer(OrderStatusConsumer orderStatusConsumer,
			InProcessSessionManager sessionManager,
			IOrderAckConstraints orderAckConstraints) {
		this.orderStatusConsumerProxy = new OrderStatusConsumerProxy(
				orderStatusConsumer, sessionManager, orderAckConstraints);
		this.orderStatusConsumer = orderStatusConsumer;
	}

	private void subscribeOrderQueryCacheForUser(String userId) {
		ChannelKey channelKey;

		channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.CANCEL_REPORT, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ORDER_UPDATE, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.NEW_ORDER, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ORDER_QUERY_EXCEPTION, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT,
				userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ORDER_STATUS_UPDATE, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);
	}

	private void subscribeOrderQueryCacheForTradingFirm(String userId) {
		ChannelKey channelKey;

		channelKey = new ChannelKey(
				ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(
				ChannelType.ORDER_BUST_REPORT_BY_TRADING_FIRM, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(
				ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_TRADING_FIRM, userId);
		internalEventChannel.addChannelListener(this, this, channelKey);
	}

	private void subscribeOrderQueryCacheForFirm(
			ExchangeFirmStructContainer firmKeyContainer) {
		ChannelKey channelKey;

		channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM,
				firmKeyContainer);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.CANCEL_REPORT_BY_FIRM,
				firmKeyContainer);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM,
				firmKeyContainer);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ORDER_UPDATE_BY_FIRM,
				firmKeyContainer);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.NEW_ORDER_BY_FIRM,
				firmKeyContainer);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS_BY_FIRM,
				firmKeyContainer);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT_BY_FIRM,
				firmKeyContainer);
		internalEventChannel.addChannelListener(this, this, channelKey);

		channelKey = new ChannelKey(
				ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM,
				firmKeyContainer);
		internalEventChannel.addChannelListener(this, this, channelKey);
	}

	public void setFirmKey(ExchangeFirmStruct firmKey) {
		this.firmKey = firmKey;
		this.firmKeyContainer = new ExchangeFirmStructContainer(firmKey);
		subscribeOrderQueryCacheForFirm(firmKeyContainer);
	}

	/*
	 * Subscribe all order status event for each group members.
	 * Only be called for a firm display user.
	 */

	public void setFirmGroupMembers(List<String> grpMembers) {
		groupMembers = grpMembers;
		isTradingFirm = true;
		for (String user : grpMembers) {
			subscribeOrderQueryCacheForTradingFirm(user);
		}
		subscribeOrderQueryCacheForTradingFirm(userId);
	}

	public List<String> getGroupMembers() {
		return groupMembers;
	}

	public ExchangeFirmStruct getFirmKey() {
		return this.firmKey;
	}

	private void initialize(int defaultSize) {

		orderCollectionDefaultSize = defaultSize;
		//Using ConcurrentHashMap instead of hashmap to avoid explicit synchronization ...Arun 05/06/2011
		//orderEntryCache = new HashMap<CmiOrderIdStructContainer, ProductKeyOrderState>(orderCollectionDefaultSize);
		orderEntryCache = new ConcurrentHashMap<CmiOrderIdStructContainer, ProductKeyOrderState>(orderCollectionDefaultSize);
		userOrderMap = new ConcurrentHashMap<CompleteOrderIdStructContainer, OrderStruct>(
				orderCollectionDefaultSize, DEFAULT_LOAD_FACTOR, 2);
		userOrderCrossReferenceMap = new ConcurrentHashMap<BaseOrderIdStructContainer, CompleteOrderIdStructContainer>(
				orderCollectionDefaultSize, DEFAULT_LOAD_FACTOR, 3);
		finishedOrderMap = new ConcurrentHashMap<CompleteOrderIdStructContainer, OrderStruct>(
				orderCollectionDefaultSize, DEFAULT_LOAD_FACTOR, 3);
		finishedOrderQueue = new ConcurrentLinkedQueue<FinishedOrder>();
		orderLockMap = new HashMap<CompleteOrderIdStructContainer, Object>(
				orderCollectionDefaultSize);
		newMsgQueue = new LinkedBlockingQueue<ChannelEvent>();
		orderQuerySupplier = OrderStatusCollectorSupplierFactory.create();
		try {
			internalEventChannel = ClientIECFactory
					.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
			/*
			// set up the thread pool for outbound reports
			this.userOutboundThreadSize = getUserOutboundThreadSize();
			for (int i = 0; i < userOutboundThreadSize; i++) {
			    LinkedBlockingQueue reportQueue = new LinkedBlockingQueue();
			    Integer threadNo = i;
			    Log.information(new StringBuilder(60).append("Creating Outbound Report ThreadPoolExecutor no: ").append(i).toString());
			    orderCacheReportExecutor.put(threadNo, new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, reportQueue));
			    orderCacheReportExecutor.get(threadNo).prestartAllCoreThreads();
			} */
		} catch (Exception e) {
			Log.exception("Exception getting CAS_INSTRUMENTED_IEC!", e);
		}

		// start the cacheCleanupThread
		//processThread = new ProcessThread();
		cacheCleanupThread = new CacheCleanupThread();
		//processThread.start();
		cacheCleanupThread.start();
	}
    
	public synchronized OrderStatusCollectorSupplier getOrderStatusCollectorSupplier() {
		return orderQuerySupplier;
	}

	/**
	 * Helper method to package up all orders into an array.
	 *
	 * @return theData OrderStruct[]
	 */
	private OrderStruct[] packageDetails(List<OrderStruct> orders) {
		OrderStruct[] theData = new OrderStruct[orders.size()];
		return orders.toArray(theData);
	}

	/**
	 * Helper method to package up all orders into an array.
	 *
	 * @param orders Order detail structures.
	 * @return theData OrderStruct[]
	 */
	private OrderStruct[] packageDetails(Collection<OrderStruct> orders) {
		OrderStruct[] theData;
		if (null == orders) {
			theData = EMPTY_OrderStruct_ARRAY;
		} else {
			theData = orders.toArray(new OrderStruct[orders.size()]);
		}
		return theData;
	}

	private boolean isPresent(String[] list, String item) {
		for (String s : list) {
			if (s.equals(item))
				return true;
		}
		return false;
	}

	/**
	 * Returns the user(s) orders from the back office order collection hashmap.
	 *
	 * @param userIds
	 * @return theData OrderStruct[]
	 */
	public OrderStruct[] getOrdersByUser(String[] userIds) {
		ArrayList<OrderStruct> orders = new ArrayList<OrderStruct>(userOrderMap
				.size());
		Iterator<OrderStruct> iter = userOrderMap.values().iterator();
		while (iter.hasNext()) {
			OrderStruct os = iter.next();
			if (isPresent(userIds, os.userId)) {
				orders.add(os);
			}
		}
		return packageDetails(orders);
	}

	/**
	 * This returns all orders for all product types for this user.
	 *
	 * @return com.cboe.idl.cmiOrder.OrderStruct[]
	 */
	public OrderStruct[] publishAllOrders() {
		return packageDetails(userOrderMap.values());
	}

	/**
	 * @return Returns all orders in the cache for this cache's user ID.
	 */
	public OrderStruct[] publishUserOrders() {
		ArrayList<OrderStruct> orders = new ArrayList<OrderStruct>(userOrderMap
				.size());
		Iterator<OrderStruct> iter = userOrderMap.values().iterator();
		while (iter.hasNext()) {
			OrderStruct os = iter.next();
			if (userId.equals(os.userId)) {
				orders.add(os);
			}
		}
		return packageDetails(orders);
	}

	/**
	 * This returns all orders for this product type.
	 *
	 * @param type int  The desired Order "Type".
	 * @return com.cboe.idl.cmiOrder.OrderStruct[]
	 */
	public OrderStruct[] getAllOrdersForType(int type) {
		ArrayList<OrderStruct> orders = new ArrayList<OrderStruct>(userOrderMap
				.size());
		Iterator<OrderStruct> iter = userOrderMap.values().iterator();
		while (iter.hasNext()) {
			OrderStruct os = iter.next();
			if (type == os.productType) {
				orders.add(os);
			}
		}
		return packageDetails(orders);
	}

	/**
	 * This returns all orders for this class.
	 *
	 * @param productClass int
	 * @return com.cboe.idl.cmiOrder.OrderStruct[]
	 */
	public OrderStruct[] getOrdersByClass(int productClass) {
		ArrayList<OrderStruct> orders = new ArrayList<OrderStruct>(userOrderMap
				.size());
		Iterator<OrderStruct> iter = userOrderMap.values().iterator();
		while (iter.hasNext()) {
			OrderStruct os = iter.next();
			if (productClass == os.classKey) {
				orders.add(os);
			}
		}
		return packageDetails(orders);
	}

	/**
	 * This returns all orders for this product.
	 *
	 * @param productKey The product Key to index on.
	 * @return com.cboe.idl.cmiOrder.OrderStruct[]
	 */
	public OrderStruct[] getOrdersForProduct(int productKey) {
		ArrayList<OrderStruct> orders = new ArrayList<OrderStruct>(userOrderMap
				.size());
		Iterator<OrderStruct> iter = userOrderMap.values().iterator();
		while (iter.hasNext()) {
			OrderStruct os = iter.next();
			if (productKey == os.productKey) {
				orders.add(os);
			}
		}
		return packageDetails(orders);
	}

	/**
	 * Get orders submitted to a specified trading session.
	 *
	 * @param sessionName Name of the trading session.
	 * @return Array of orders (could be 0 length).
	 */
	public OrderStruct[] getOrdersForSession(String sessionName) {
		ArrayList<OrderStruct> orders = new ArrayList<OrderStruct>(userOrderMap
				.size());
		Iterator<OrderStruct> iter = userOrderMap.values().iterator();
		while (iter.hasNext()) {
			OrderStruct os = iter.next();
			if (os.activeSession.equals(sessionName)) {
				orders.add(os);
			}
		}
		return packageDetails(orders);
	}

	/**
	 * Returns the individual order requested.
	 *
	 * @param orderId
	 * @return OrderStruct or null
	 */
	public OrderStruct getOrder(OrderIdStruct orderId, short statusChange) {
		return getOrderFromOrderCache(orderId);
	}

	private OrderStruct getActiveOrderByCompleteId(
			CompleteOrderIdStructContainer orderId) {
		if (orderId != null) {
			return userOrderMap.get(orderId);
		}
		return null;
	}

	/**
	 * Returns the orderstruct in the NEW event if it matches the supplied orderId.
	 *
	 * @param orderId
	 * @return OrderStruct or null
	 */
	private OrderStruct returnIfMatching(ChannelEvent newEvent,
			OrderIdStruct orderId) {
		OrderStruct matchOrder = null;
		try {
			if (newEvent != null && orderId != null) {
				Object channelData = newEvent.getEventData();
				OrderStruct order1 = ((GroupOrderStructContainer) channelData)
						.getOrderStruct();

				if (orderId.highCboeId == 0 && orderId.lowCboeId == 0) {
					if (order1.orderId.branchSequenceNumber == orderId.branchSequenceNumber
							&& order1.orderId.branch.equals(orderId.branch)
							&& order1.orderId.executingOrGiveUpFirm.firmNumber
									.equals(orderId.executingOrGiveUpFirm.firmNumber)
							&& order1.orderId.executingOrGiveUpFirm.exchange
									.equals(orderId.executingOrGiveUpFirm.exchange)
							&& order1.orderId.correspondentFirm
									.equals(orderId.correspondentFirm)
							&& order1.orderId.orderDate
									.equals(orderId.orderDate)) {
						matchOrder = order1;
					}

				} else {
					if (orderId.highCboeId == order1.orderId.highCboeId
							&& orderId.lowCboeId == order1.orderId.lowCboeId) {
						matchOrder = order1;
					}
				}
			}
		} catch (Exception e) {
		}
		return matchOrder;
	}

	private OrderStruct getNewOrderByOrderId(OrderIdStruct orderId) {
		OrderStruct order = null;
		if (!checkNewQueue())
			return order;

		try {
			StringBuilder matched = new StringBuilder(logPrefix.length() + 45);
			order = returnIfMatching(currentNewEventInProgress.get(), orderId);
			if (order == null) {
				ChannelEvent newEvents[] = EMPTY_ChannelEvent_ARRAY;
				newEvents = newMsgQueue.toArray(newEvents);
				for (int i = 0; i < newEvents.length; i++) {
					order = returnIfMatching(newEvents[i], orderId);
					if (order != null) {
						matched.setLength(0);
						matched.append(logPrefix).append(
								" matched with NewQueue. length=").append(
								newEvents.length);
						Log.information(matched.toString());
						break;
					}
				}
			} else {
				matched.append(logPrefix).append(
						" matched with currentNewEventInProgress");
				Log.information(matched.toString());
			}
		} catch (Exception e) {
		}
		return order;
	}

	private void addToCrossReferenceMaps(OrderStruct order,
			CompleteOrderIdStructContainer orderIndex) {
		// Add the CMi and Cboe IDs to the x-ref map
		CmiOrderIdStructContainer cmiOrderIndex = OrderIdStructContainerFactory
				.createCmiOrderIdStructContainer(order.orderId);
		CboeOrderIdStructContainer cboeOrderIndex = OrderIdStructContainerFactory
				.createCboeOrderIdStructContainer(order.orderId);
		userOrderCrossReferenceMap.put(cmiOrderIndex, orderIndex);
		userOrderCrossReferenceMap.put(cboeOrderIndex, orderIndex);
	}

	private void removeFromCrossReferenceMaps(OrderStruct order) {
		CmiOrderIdStructContainer cmiOrderIndex = OrderIdStructContainerFactory
				.createCmiOrderIdStructContainer(order.orderId);
		CboeOrderIdStructContainer cboeOrderIndex = OrderIdStructContainerFactory
				.createCboeOrderIdStructContainer(order.orderId);

		// Remove the CMi and Cboe IDs from the x-ref hmap
		userOrderCrossReferenceMap.remove(cmiOrderIndex);
		userOrderCrossReferenceMap.remove(cboeOrderIndex);
	}

	/**
	 * Add/update the order in cache
	 *
	 * @param order OrderStruct  Contains the order object to be added.
	 */
	public void put(OrderStruct order) {
		CompleteOrderIdStructContainer orderIndex;

		boolean added = false;
		orderIndex = createCompleteOrderIdStructContainer(order.orderId);
		synchronized (getOrderLock(orderIndex)) {
			//Add/Update the userOrderMap.
			if (userOrderMap.put(orderIndex, order) == null) {
				// Add the CMi and Cboe IDs to the x-ref map
				// This map need not be updated each time an orderstruct is updated.
				addToCrossReferenceMaps(order, orderIndex);

				// The order could be there in the orderEntryCache. If so, market it as CACHED.
				orderEntryComplete(order.orderId);
				added = true;
			}
		}
		if (added && Log.isDebugOn()) {
			Log.debug(logPrefix + " : added to active Order Cache "
					+ orderIndex);
		}
	}

	private void remove(OrderStruct orderStruct) {
		CompleteOrderIdStructContainer orderIndex = createCompleteOrderIdStructContainer(orderStruct.orderId);

		synchronized (getOrderLock(orderIndex)) {
			if (finishedOrderMap.containsKey(orderIndex)) {
				// We're now processing message from 2nd FE.
				// Or we're processing a Cancel after a Fill, and we'll get those
				// messages again and we'll need the transactionSequenceNumber;
				// So, replacing the previous orderStruct with the latest.
				finishedOrderMap.put(orderIndex, orderStruct);
				return;
			}
			// This order is no longer active.
			// Remove from active order cache and other secondary caches;
			// But first, add it to finished order cache to handle late cancel requests as well as
			// status messages from second FE. The orders in the finished cache will be
			// cleared after a predetermined interval.
			// Now, the order/orderIndex be there only in finishedOrderMap, finishedOrderQueue
			// and in the userOrderCrossReferenceMap.
			finishedOrderMap.put(orderIndex, orderStruct);
			finishedOrderQueue.add(new FinishedOrder(orderStruct));
			if (userOrderMap.remove(orderIndex) == null) {
				// This order was not in the order map before.
				// Seems like a fill or cancel came before NEW.
				// Any how, we need it in the cross reference map until
				// removed from the finished order map.

				// Add the CMi and Cboe IDs to the x-ref map
				addToCrossReferenceMaps(orderStruct, orderIndex);

				// The order could be there in the orderEntryCache. If so, market it as CACHED.
				orderEntryComplete(orderStruct.orderId);
			}
		}
		if (Log.isDebugOn()) {
			Log.debug(logPrefix
					+ " : moved from active to finished Order Cache "
					+ orderIndex);
		}
	}

	/**
	 * Determine whether this map contains an order for the specified orderId
	 *
	 * @param orderId      com.cboe.application.sbtApplications.shared.OrderIdStructContainer
	 * @param statusChange not used
	 * @return boolean
	 */
	public boolean containsOrderIdKey(OrderIdStruct orderId, short statusChange) {
		CompleteOrderIdStructContainer anOrderId = getValidOrderIdStructContainer(orderId);
		return (anOrderId == null) ? false : userOrderMap
				.containsKey(anOrderId);
	}

	//////////////////////////// Order Status Collector Interface /////////////////

	/**
	 * This sends a bust order struct to any registered listeners.
	 *
	 * @param busted The order bust report.
	 */
	private void acceptOrderBustReport(int channelType,
			OrderStruct orderStruct, BustReportStruct[] busted,
			short statusChange, boolean bUser) {

        StringBuilder sb = new StringBuilder(logPrefix.length() + 60);
        sb.append(logPrefix).append(" : Bust Report received. OrderId: ")
            .append(getOrderIdString(orderStruct.orderId));
        Log.information(sb.toString());

		boolean publishBust = true;
		if (StatusUpdateReasons.POSSIBLE_RESEND != statusChange) // Possible resends are always dispatched
		{
			CompleteOrderIdStructContainer completeOrderId = createCompleteOrderIdStructContainer(orderStruct.orderId);
			synchronized (getOrderLock(completeOrderId)) {
				// never apply bust to the order struct if the sequence number is older in the report
				int oldTransactionSequenceNumber = getOrderTransactionSequenceNumber(completeOrderId);
				if (oldTransactionSequenceNumber < orderStruct.transactionSequenceNumber) {
					this.put(orderStruct);
				} else {
					publishBust = false;
				}
			}
		}
		if (publishBust) {
			if (processAsyncServerReport(orderStruct, bUser)) {
				this.orderStatusConsumerProxy.processOrderBust(busted,
						orderStruct, statusChange);
			}
			//OrderIdBustStructContainer container = new OrderIdBustStructContainer(CollectionHelper.EMPTY_int_ARRAY,statusChange,orderStruct,busted);
			//dispatchOrderStatus(container, channelType, orderStruct.userId);
		}
	}

	/**
	 * This sends a bust reinstate order struct to any registered listeners.
	 *
	 * @param reinstated The order bust reinstate report.
	 */
	private void acceptOrderBustReinstateReport(int channelType,
			OrderStruct orderStruct, BustReinstateReportStruct reinstated,
			short statusChange, boolean bUser) {

        StringBuilder sb = new StringBuilder(logPrefix.length() + 60);
        sb.append(logPrefix).append(" : Bust Reinstate Report received. OrderId: ")
            .append(getOrderIdString(orderStruct.orderId));
        Log.information(sb.toString());

		boolean publishReinstate = true;
		if (StatusUpdateReasons.POSSIBLE_RESEND != statusChange) // Possible resends are always dispatched
		{
			CompleteOrderIdStructContainer completeOrderId = createCompleteOrderIdStructContainer(orderStruct.orderId);
			synchronized (getOrderLock(completeOrderId)) {
				// never apply reinstate to the order struct if the sequence number is older in the report
				int oldTransactionSequenceNumber = getOrderTransactionSequenceNumber(completeOrderId);

				if (oldTransactionSequenceNumber < orderStruct.transactionSequenceNumber) {
					this.put(orderStruct);
				} else {
					publishReinstate = false;
				}
			}
		}
		if (publishReinstate) {
			if (processAsyncServerReport(orderStruct, bUser)) {
				this.orderStatusConsumerProxy.processOrderBustReinstate(
						reinstated, orderStruct, statusChange);
			}
			//OrderIdReinstateStructContainer container =
			//        new OrderIdReinstateStructContainer(CollectionHelper.EMPTY_int_ARRAY, orderStruct.userId, statusChange, orderStruct, reinstated);
			//dispatchOrderStatus(container, channelType, orderStruct.userId);
		}
	}

	/**
	 * This refreshes the cached order information and sends a new copy of
	 * the order detail structure to any registered listeners.
	 *
	 * @param report CancelReportStruct   The published cancel report.
	 */
	private void acceptCancelReport(OrderStruct orderStruct,
			CancelReportStruct[] report, short statusChange, boolean bUser) {
		if (Log.isDebugOn())
			Log.debug(logPrefix + " : Cancel Report received. OrderId: "
					+ getOrderIdString(orderStruct.orderId));
		boolean publishCancel = true;
		boolean publishNew = false;

		// Possible resends are always dispatched
		if (StatusUpdateReasons.POSSIBLE_RESEND != statusChange) {
			CompleteOrderIdStructContainer completeOrderId = createCompleteOrderIdStructContainer(orderStruct.orderId);
			synchronized (getOrderLock(completeOrderId)) {
				// never apply cancel to the order struct if the sequence number
				// is older in the report
				int oldTransactionSequenceNumber = getOrderTransactionSequenceNumber(completeOrderId);
				if(Log.isDebugOn())
                {
                	Log.debug("FixOrderqueryCache:oldTransactionSequenceNumber: "+oldTransactionSequenceNumber);
                	Log.debug("FixOrderqueryCache:orderStruct.transactionSequenceNumber: "+orderStruct.transactionSequenceNumber);
                }	
				// if the current seq number is 0, then check to see if a NEW
				// needs to be generated or not.
				if (0 == oldTransactionSequenceNumber
						&& generateNewReport(orderStruct)) {
					publishNew = true;
				}
				if (oldTransactionSequenceNumber < orderStruct.transactionSequenceNumber) {
					if (eligibleForCleanup(orderStruct)) {
						remove(orderStruct);
					} else {
						this.put(orderStruct);
					}
				} else if (oldTransactionSequenceNumber == orderStruct.transactionSequenceNumber) {
					if (UserOrderServiceUtil
							.isCasGeneratedCancellReport(report[0].orsId)) {
						if (eligibleForCleanup(orderStruct)) {
							remove(orderStruct);
						} else {
							this.put(orderStruct);
						}
					}
				} else {
					publishCancel = false;

                    StringBuilder sb = new StringBuilder(logPrefix.length() + 60);
                    sb.append(logPrefix).append("publishCancel:false for: ")
                        .append(getOrderIdString(orderStruct.orderId))
                        .append(" oldTranSeq: ").append(oldTransactionSequenceNumber)
                        .append(" newTranSeq: ").append(orderStruct.transactionSequenceNumber);
                    Log.information(sb.toString());
				}
			}
		}
		ProductStruct product = null;
		if (publishNew) {
			CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory
					.createCmiOrderIdStructContainer(orderStruct.orderId);

            StringBuilder sb1 = new StringBuilder(logPrefix.length() + 60);
            sb1.append(logPrefix).append("Faking NEW before CANCEL for:").append(cmiOrderId)
                .append(" at: ").append(System.nanoTime());
            Log.information(sb1.toString());

			product = publishNew("CANCEL", orderStruct);

            StringBuilder sb2 = new StringBuilder(logPrefix.length() + 60);
            sb2.append(logPrefix).append("Calling processPendingCancels for:").append(cmiOrderId)
                .append(" at: ").append(System.nanoTime());
            Log.information(sb2.toString());

			processPendingCancels(orderStruct);
		}

		if (publishCancel) {
			// put on threadpool to process
			/*
			 * int i = orderStruct.orderId.branchSequenceNumber; BaseTask ct =
			 * taskFactory.createCancelTask(fixCasSession, orderStruct, product,
			 * statusChange, 0, report, i % userOutboundThreadSize);
			 * processAsyncServerReport(orderStruct, ct, bUser);
			 */
			if (processAsyncServerReport(orderStruct, bUser)) {
				this.orderStatusConsumerProxy.processOrderCancel(report,
						orderStruct, statusChange);
			}
        }
	}

	private boolean processAsyncServerReport(OrderStruct orderStruct,
	/* BaseTask task, */
	boolean bUser) {

		if (bUser && (userId.equals(orderStruct.userId) || isTradingFirm)
				|| (!bUser && (firmKey != null))) {
			/*
			int i = orderStruct.orderId.branchSequenceNumber;
			this.orderCacheReportExecutor.get((Integer.valueOf(orderStruct.orderId.branchSequenceNumber % userOutboundThreadSize))).execute(task);
			 */
			return true;
		}

		return false;
	}

	private boolean generateNewReport(OrderStruct orderStruct) {
		// Evaluate Order.extensions for the SEND_NEW value
		boolean sendNew = true;
		try {
			ExtensionsHelper eh = new ExtensionsHelper(orderStruct.extensions);
			String sendNewString = eh
					.getValue(InternalExtensionFields.SEND_NEW);
			if (sendNewString != null && sendNewString.length() > 0) {
				sendNew = Boolean.parseBoolean(eh
						.getValue(InternalExtensionFields.SEND_NEW));
				// remove the key before sending the order to the firm
				eh.removeKey(InternalExtensionFields.SEND_NEW);
				orderStruct.extensions = eh.toString();
			}
		} catch (java.text.ParseException pe) {
			Log
					.information("mapExtensionFields ParseError while evaluating SEND_NEW for generateNewReport()");
			Log.exception(pe);
		}

		// If we see the orderId still in the orderEntryCache, that says the NEW hasn't been generated yet.
		return (checkOrderEntryCache(orderStruct.orderId)
				&& userId.equals(orderStruct.userId) && sendNew);
	}

	/**
	 * This sends a filled order struct to any registered listeners.
	 *
	 * @param filled The order filled report.
	 */
	private void acceptOrderFillReport(int channelType,
			OrderStruct orderStruct, FilledReportStruct[] filled,
			short statusChange, boolean bUser) {
		if (Log.isDebugOn())
			Log.debug(logPrefix + " : Fill Report received. OrderId: "
					+ getOrderIdString(orderStruct.orderId));
		boolean publishNew = false;
		//boolean publishFill = true; // Commented because we are planning to send fill reports irrespective of the sequence numbers.....ARUN 05/03/2011

		if (StatusUpdateReasons.POSSIBLE_RESEND != statusChange) // Possible resends are always dispatched
		{
			CompleteOrderIdStructContainer completeOrderId = createCompleteOrderIdStructContainer(orderStruct.orderId);
			synchronized (getOrderLock(completeOrderId)) {
				// never apply fill to the order struct if the sequence number is older in the report
				int oldTransactionSequenceNumber = getOrderTransactionSequenceNumber(completeOrderId);
				// This is to fix the race condition resulting in a fill report being delivered before the new
				if (0 == oldTransactionSequenceNumber
						&& generateNewReport(orderStruct)) {
					publishNew = true;
				}
				if (oldTransactionSequenceNumber < orderStruct.transactionSequenceNumber) {
					if (eligibleForCleanup(orderStruct)) {
						remove(orderStruct);
					} else {
						this.put(orderStruct);
					}
				} else {
					/*StringBuilder alarmText = new StringBuilder(110);
					alarmText
							.append("FILL Report Unpublished due to Seq # test.");
					Log.alarm(alarmText.toString());
					alarmText.append(' ').append(oldTransactionSequenceNumber)
							.append(" < ").append(
									orderStruct.transactionSequenceNumber);
					alarmText.append(" OrderId: ");
					alarmText.append(getOrderIdString(orderStruct.orderId));
					Log.information(alarmText.toString());
					publishFill = false;*/
					// Commented above lines because we are planning to send fill reports irrespective of the sequence numbers.....ARUN 05/03/2011
					StringBuilder logText = new StringBuilder(110);
                    logText.append("OrderFillReport Failed Seq # test. OrderId:")                    	
                    	   .append(getOrderIdString(orderStruct.orderId));                   
                    Log.information(logText.toString());       
				}
			}
		}

		if (publishNew) {
			CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory
					.createCmiOrderIdStructContainer(orderStruct.orderId);

            StringBuilder sb1 = new StringBuilder(logPrefix.length() + 60);
            sb1.append(logPrefix).append("Faking NEW before FILL for:").append(cmiOrderId)
                .append(" at: ").append(System.nanoTime());
            Log.information(sb1.toString());

			publishNew("FILL", orderStruct);

            StringBuilder sb2 = new StringBuilder(logPrefix.length() + 60);
            sb2.append(logPrefix).append("Calling processPendingCancels for:").append(cmiOrderId)
                .append(" at: ").append(System.nanoTime());
            Log.information(sb2.toString());

			//processPendingCancels(orderStruct, fixCasSession.createPendingCancelProcessingTask(getPendingCancels(cmiOrderId)));
			processPendingCancels(orderStruct);
		}
		//if (publishFill) {// Commented because we are planning to send fill reports irrespective of the sequence numbers.....ARUN 05/03/2011
			if (processAsyncServerReport(orderStruct, bUser)) {
				this.orderStatusConsumerProxy.processOrderFill(filled,
						orderStruct, statusChange);
			}
			//GroupOrderIdFillReportContainer container;
			//container = new GroupOrderIdFillReportContainer(CollectionHelper.EMPTY_int_ARRAY,statusChange,orderStruct,filled);
			//dispatchOrderStatus(container, channelType, orderStruct.userId);
		//}
	}

	/**
	 * @param channelType
	 */
	private void acceptOrderAcceptedByBook(int channelType,
			OrderStruct orderStruct) {
		Log
				.alarm(logPrefix
						+ " : THIS METHOD SHOULD NOT HAVE BEEN CALLED - OrderAcceptedByBook. OrderId: "
						+ getOrderIdString(orderStruct.orderId) + " User Key:"
						+ orderStruct.userId + " ORSID: " + orderStruct.orsId);
		/*
		if(Log.isDebugOn())
		    Log.debug(logPrefix + " : Order accepted by book received. OrderId: " + getOrderIdString(orderStruct.orderId));

		boolean publishNew = false;
		boolean publishBooked = true;
		short statusChange = StatusUpdateReasons.NEW;

		if (StatusUpdateReasons.POSSIBLE_RESEND != statusChange) // Possible resends are always dispatched
		{
		    CompleteOrderIdStructContainer completeOrderId = createCompleteOrderIdStructContainer(orderStruct.orderId);
		    synchronized(getOrderLock(completeOrderId))
		    {
		        int oldTransactionSequenceNumber = getOrderTransactionSequenceNumber(completeOrderId);
		        // if the current seq number is 0, then check to see if a NEW needs to be generated or not.
		        if (0 == oldTransactionSequenceNumber && generateNewReport(orderStruct))
		        {
		            publishNew = true;
		        }
		        if (oldTransactionSequenceNumber < orderStruct.transactionSequenceNumber)
		        {
		            this.put(orderStruct);
		        }
		        else
		        {
		            publishBooked = false;
		        }
		    }
		}
		if (publishNew)
		{
			publishNew("BOOKED", orderStruct);
		}
		if (publishBooked)
		{
		    dispatchOrderStatus(orderStruct, channelType, orderStruct.userId);
		}
		 */
	}

	// this is the method which publishes "faked" NEW messages.

	private ProductStruct publishNew(String event, OrderStruct orderStruct) {
		StringBuilder fakeNew = new StringBuilder(logPrefix.length()
				+ event.length() + 30);
		fakeNew.append(logPrefix).append(": creating fake NEW before ").append(
				event);
		Log.information(fakeNew.toString());
		OrderStruct clonedOrder = OrderStructBuilder
				.cloneOrderStruct(orderStruct);
		// check for strategy and reset the quantities of legs
		if (clonedOrder.productType == ProductTypes.STRATEGY) {
			for (LegOrderDetailStruct legDetails : clonedOrder.legOrderDetails) {
				legDetails.leavesQuantity = legDetails.originalQuantity;
				legDetails.tradedQuantity = 0;
				legDetails.cancelledQuantity = 0;
			}
		}
		//GroupOrderStructContainer container = createNewMessage(clonedOrder);
		//dispatchOrderStatus(container, ChannelType.NEW_ORDER, orderStruct.userId);
		ProductStruct product = null;
		try {
			product = ServicesHelper.getProductQueryServiceAdapter()
					.getProductByKey(clonedOrder.productKey);
		} catch (Exception e) {
			Log.exception(e);
		}
		orderStatusConsumer.acceptNewOrder(processNewOrder(clonedOrder),
				product, StatusUpdateReasons.NEW, 0);
		return product;
	}

	/**
	 * This refreshes the cached order information and sends a new copy of
	 * the order detail structure to any registered listeners.
	 *
	 * @param orderStruct
	 */
	private void acceptOrderUpdate(int channelType, OrderStruct orderStruct,
			boolean bUser) {
		if (Log.isDebugOn())
			Log.debug(logPrefix + " : Order update received. OrderId: "
					+ getOrderIdString(orderStruct.orderId));
		short statusChange = StatusUpdateReasons.NEW;
		boolean publishOrderUpdate = true;
		if (StatusUpdateReasons.POSSIBLE_RESEND != statusChange) // Possible resends are always dispatched
		{
			CompleteOrderIdStructContainer completeOrderId = createCompleteOrderIdStructContainer(orderStruct.orderId);
			synchronized (getOrderLock(completeOrderId)) {
				// never apply bust to the order struct if the sequence number is older in the report
				int oldTransactionSequenceNumber = getOrderTransactionSequenceNumber(completeOrderId);
				if (oldTransactionSequenceNumber < orderStruct.transactionSequenceNumber) {
					this.put(orderStruct); //Will replace the existing order copy
				} else {
					publishOrderUpdate = false;
				}
			}
		}
		if (publishOrderUpdate) {
			// put on threadpool to process
			//int i = orderStruct.orderId.branchSequenceNumber;
			// BaseTask ct = taskFactory.createOrderStatusTask(fixCasSession, orderStruct, StatusUpdateReasons.UPDATE, 0, i % userOutboundThreadSize);
			// processAsyncServerReport(orderStruct, ct, bUser);
			//GroupOrderStructContainer container = new GroupOrderStructContainer(CollectionHelper.EMPTY_int_ARRAY, statusChange, orderStruct );
			//dispatchOrderStatus(container, channelType, orderStruct.userId);
		}
	}

	/**
	 * This refreshes the cached order information and sends a new copy of
	 * the order detail structure to any registered listeners.
	 *
	 * @param orderStruct
	 */
	private void acceptOrderStatusUpdate(int channelType,
			OrderStruct orderStruct, short statusChange, boolean bUser) {
		if (Log.isDebugOn())
			Log.debug(logPrefix + " : Order status update received. OrderId: "
					+ getOrderIdString(orderStruct.orderId));
		boolean publishStatusUpdate = true;
		if (StatusUpdateReasons.POSSIBLE_RESEND != statusChange) // Possible resends are always dispatched
		{
			CompleteOrderIdStructContainer completeOrderId = createCompleteOrderIdStructContainer(orderStruct.orderId);
			synchronized (getOrderLock(completeOrderId)) {
				// never apply bust to the order struct if the sequence number is older in the report
				int oldTransactionSequenceNumber = getOrderTransactionSequenceNumber(completeOrderId);
				if (oldTransactionSequenceNumber < orderStruct.transactionSequenceNumber) {
					this.put(orderStruct); //Will replace the existing order copy
				} else {
					publishStatusUpdate = false;
				}
			}
		}
		if (publishStatusUpdate) {
			// int i = orderStruct.orderId.branchSequenceNumber;
			// BaseTask ct = taskFactory.createOrderStatusUpdateTask(fixCasSession, orderStruct, statusChange, 0, i % userOutboundThreadSize);
			// processAsyncServerReport(orderStruct, ct, bUser);
			//GroupOrderStructContainer container = new GroupOrderStructContainer(CollectionHelper.EMPTY_int_ARRAY, statusChange, orderStruct);
			//dispatchOrderStatus(container, channelType, orderStruct.userId);
		}
	}

	/**
	 * This adds the cached order information and sends a new copy of
	 * the order detail structure to any registered listeners.
	 *
	 * @param orderStruct
	 */
	private void acceptNewOrder(int channelType, OrderStruct orderStruct,
			short statusChange) {
		Log
				.alarm(logPrefix
						+ " : THIS METHOD SHOULD NOT HAVE BEEN CALLED - New order received. OrderId: "
						+ getOrderIdString(orderStruct.orderId) + " User Key:"
						+ orderStruct.userId + " ORSID: " + orderStruct.orsId);
		/*
		if(Log.isDebugOn())
		{
		    Log.debug( logPrefix + " : New order received. OrderId: " +
		    getOrderIdString(orderStruct.orderId) + " User Key:"+ orderStruct.userId + " ORSID: " + orderStruct.orsId);
		}
		    
		boolean publishNew = true;
		if (StatusUpdateReasons.POSSIBLE_RESEND != statusChange) // Possible resends are always dispatched
		{
		    CompleteOrderIdStructContainer completeOrderId = createCompleteOrderIdStructContainer(orderStruct.orderId);
		    synchronized(getOrderLock(completeOrderId))
		    {
		        int oldTransactionSequenceNumber = getOrderTransactionSequenceNumber(completeOrderId);
		        if (oldTransactionSequenceNumber < orderStruct.transactionSequenceNumber)
		        {
		            this.put(orderStruct);
		        }
		        else
		        {
		            publishNew = false;
		        }
		    }
		}
		if (publishNew)
		{
		    GroupOrderStructContainer  container = new GroupOrderStructContainer(CollectionHelper.EMPTY_int_ARRAY, statusChange, orderStruct );
		    dispatchOrderStatus(container,channelType,orderStruct.userId);
		}
		 */
	}

	/**
	 * This handles the acceptOrder message from IEC.
	 *
	 * @param orders OrderStructs
	 */
	private void acceptOrders(int channelType, OrderStruct[] orders,
			boolean bUser) {
		if (Log.isDebugOn()) {
			Log.debug(logPrefix + " : acceptOrders in OrderQueryCache : "
					+ orders.length);
		}

		boolean newOrderReceived = false;
		OrderStruct oldOrder = null;
		short statusChange = StatusUpdateReasons.NEW;
		String inUserId = "";
		ArrayList<OrderStruct> orderStructs = new ArrayList<OrderStruct>();
		for (OrderStruct newOrder : orders) {
			orderStructs.add(newOrder);
			oldOrder = getOrderFromOrderCache(newOrder.orderId);
			if (oldOrder == null) {
				inUserId = newOrder.userId;
				this.put(newOrder); //Will replace the existing order copy
				newOrderReceived = true;
			}
		}

		if (newOrderReceived) {
			for (int i = 0; i < orderStructs.size(); i++) {
				OrderStruct orderStruct = orderStructs.get(i);
				//int j = orderStruct.orderId.branchSequenceNumber;
				//BaseTask ct = taskFactory.createOrderStatusTask(fixCasSession, orderStruct, StatusUpdateReasons.QUERY, 0, j % userOutboundThreadSize);
				//processAsyncServerReport(orderStruct, ct, bUser);
			}

			//GroupOrderStructSequenceContainer container;
			//container = new GroupOrderStructSequenceContainer(CollectionHelper.EMPTY_int_ARRAY, statusChange, validOrders);
			//dispatchOrderStatus(container, channelType, inUserId);
		}
	}

	/* public void acceptConsumerException(OrderStruct order, String text, short statusChange, int queueDepth) {

	            if (Log.isDebugOn()) {
	                Log.debug(
	                    "Received callback on FixOrderStatusConsumer.acceptConsumerException(OrderStruct)");
	                Log.debug(
	                    " OrderId: " + order.orderId.branch + ":" + order.orderId.branchSequenceNumber +
	                    " orderHiLo: " + order.orderId.highCboeId + ":" + order.orderId.lowCboeId +
	                    " productKey: " + order.productKey + " text: " + text + " statusChange: " + statusChange);
	            }
	            // create an Appia Email MessagObject
	            try {
	                Email email = com.cboe.fix.appia.FixMessageFactory.makeEmail();
	                // populate the email message
	                email.EmailType = FixConstants.Email.NEW;
	                email.OrigTime = FixDateTimeFormatter.time( Calendar.getInstance());
	                email.EmailThreadID = String.valueOf(FixMapper.getNextExecId());

	                CasClOrdIDWrapper clOrdIDWrapper = new CasClOrdIDWrapper(order.orderId);
	                email.ClOrdID = clOrdIDWrapper.getClOrdID();

	                email.Subject = "Processing Error";
	                email.LinesOfText = 1;
	                String fullText = new StringBuilder(160).
	                    append("A processing error was encountered when trying to deliver a ").append(text)
	                    .append(" Message concerning productkey ").append(order.productKey)
	                    .append(" for the ClOrdID field in this message.").toString();
	                email.Text = new String[] { (fullText) };
	                // send the email
	                fixCasSession.sendFixMessage(email);

	            } catch (SystemException se) {
	                fixCasSession.debug( "Consumer Exception Email Report will not be sent because of an exeception during creation." );
	                Log.alarm("Consumer Exception Email Report will not be sent because of an exeception during creation.");
	                Log.exception(se);
	            }

	        }
	 */

	/**
	 * This handles the accept order exception message from IEC.
	 *
	 * @param description exception description
	 */
	private void acceptException(int channelType, String description) {
		// this status was not being propagated to the FIX OrderStatusCallbackHandler
		Log
				.alarm(logPrefix
						+ " Received ORDER_QUERY_EXCEPTION status in FixOrderQueryCache: "
						+ description);
		//dispatchOrderStatus(description, channelType, userId);
	}

	/**
	 * Cleaning up of cache data
	 */
	public synchronized void cacheCleanUp() {
        // mwm PITS 80100 debug
        StringBuilder mmQuit = new StringBuilder(logPrefix.length() + 25);
        mmQuit.append(logPrefix).append(" cacheCleanup invoked");
        Log.information(mmQuit.toString());
        // mwm PITS 80100 debug
		try {
			shuttingDown = true;
			//processThread.interrupt();
			cacheCleanupThread.interrupt();
			internalEventChannel.removeChannelListener(this);
			userOrderMap.clear();
			finishedOrderMap.clear();
			userOrderCrossReferenceMap.clear();
			newMsgQueue.clear();
			finishedOrderQueue.clear();
			orderEntryCache.clear();
			/*try
			{
			    processThread.join();
			}
			catch (InterruptedException ie)
			{
			}
			 */
			try {
				cacheCleanupThread.join();
			} catch (InterruptedException ie) {
			}
		} catch (Exception e) {
			Log.exception(logPrefix + " Exception during cacheCleanup()", e);
		}
        // mwm PITS 80100 debug
        mmQuit.append("/done");
        Log.information(mmQuit.toString());
        // mwm PITS 80100 debug
	}

	public void channelUpdate(ChannelEvent event) {
		process(event);
	}

	private void process(ChannelEvent event) {
		try {
			ChannelKey channelKey = (ChannelKey) event.getChannel();
			Object channelData = (Object) event.getEventData();

			if (Log.isDebugOn()) {
				Log.debug(logPrefix + " : received event " + event);
			}
			// Using this flag to determine whether the processing will be done in ClearingFirm mode.
			// Order of case statements below is important since it relies on dropping through and setting
			// the boolean.
			boolean bUser = false;
			switch (channelKey.channelType) {
			case ChannelType.ORDER_FILL_REPORT:
			case ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM:
				bUser = true;
			case ChannelType.ORDER_FILL_REPORT_BY_FIRM:

				GroupOrderIdFillReportContainer orderFilledContainer = (GroupOrderIdFillReportContainer) channelData;
				acceptOrderFillReport(channelKey.channelType,
						orderFilledContainer.getOrderStruct(),
						orderFilledContainer.getFilledReportStruct(),
						orderFilledContainer.getStatusChange(), bUser);
				break;

			case ChannelType.CANCEL_REPORT:
			case ChannelType.CANCEL_REPORT_BY_FIRM:
				bUser = true;
				GroupCancelReportContainer cancelContainer = (GroupCancelReportContainer) channelData;
				OrderIdCancelReportContainer cancelReportContainer = cancelContainer
						.getCancelReport();
				acceptCancelReport(cancelReportContainer.getOrderStruct(),
						cancelReportContainer.getCancelReportStruct(),
						cancelContainer.getStatusChange(), bUser);
				break;

			// this should no longer be sent - the accept method below will log error.
			case ChannelType.ORDER_ACCEPTED_BY_BOOK:
				bUser = true;
			case ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM:
				OrderStruct order = (OrderStruct) channelData;
				acceptOrderAcceptedByBook(channelKey.channelType, order);
				break;

			case ChannelType.ORDER_UPDATE:
				bUser = true;
			case ChannelType.ORDER_UPDATE_BY_FIRM:
				GroupOrderStructContainer orderStructContainer = (GroupOrderStructContainer) channelData;
				acceptOrderUpdate(channelKey.channelType, orderStructContainer
						.getOrderStruct(), bUser);
				break;

			// this should no longer be sent - the accept method below will log error
			case ChannelType.NEW_ORDER:
				bUser = true;
			case ChannelType.NEW_ORDER_BY_FIRM:
				orderStructContainer = (GroupOrderStructContainer) channelData;
				acceptNewOrder(channelKey.channelType, orderStructContainer
						.getOrderStruct(), orderStructContainer
						.getStatusChange());
				break;

			case ChannelType.ACCEPT_ORDERS:
				bUser = true;
			case ChannelType.ACCEPT_ORDERS_BY_FIRM:
				GroupOrderStructSequenceContainer orderStructSequence = (GroupOrderStructSequenceContainer) channelData;
				acceptOrders(channelKey.channelType, orderStructSequence
						.getOrderStructSequence(), bUser);
				break;

			case ChannelType.ORDER_QUERY_EXCEPTION:
				bUser = true;
				OrderQueryExceptionStructContainer orderQueryException = (OrderQueryExceptionStructContainer) channelData;
				acceptException(channelKey.channelType, orderQueryException
						.getDescription());
				break;

			case ChannelType.ORDER_BUST_REPORT:
			case ChannelType.ORDER_BUST_REPORT_BY_TRADING_FIRM:
				bUser = true;
			case ChannelType.ORDER_BUST_REPORT_BY_FIRM:
				OrderIdBustStructContainer orderBustReportContainer = (OrderIdBustStructContainer) channelData;
				acceptOrderBustReport(channelKey.channelType,
						orderBustReportContainer.getOrderStruct(),
						orderBustReportContainer.getBustReportStruct(),
						orderBustReportContainer.getStatusChange(), bUser);
				break;

			case ChannelType.ORDER_BUST_REINSTATE_REPORT:
			case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_TRADING_FIRM:
				bUser = true;
			case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM:
				OrderIdReinstateStructContainer reinstatedOrderContainer = (OrderIdReinstateStructContainer) channelData;
				acceptOrderBustReinstateReport(
						channelKey.channelType,
						reinstatedOrderContainer.getOrderStruct(),
						reinstatedOrderContainer.getBustReinstateReportStruct(),
						reinstatedOrderContainer.getStatusChange(), bUser);
				break;
			case ChannelType.ORDER_STATUS_UPDATE:
				bUser = true;
				RoutingGroupOrderStructContainer orderStatusContainer = (RoutingGroupOrderStructContainer) channelData;
				acceptOrderStatusUpdate(channelKey.channelType,
						orderStatusContainer.getOrderStruct(),
						orderStatusContainer.getStatusChange(), bUser);
				break;
			default:
				if (Log.isDebugOn()) {
					Log.debug(logPrefix + " Wrong Channel : "
							+ channelKey.channelType);
				}
				break;
			}
		} catch (Exception e) {
			Log.exception(logPrefix + "Exception processing event:" + event, e);
		}
	}

	private boolean isProductValid(int productKey) {
		boolean isValid = false;
		try {
			ProductStruct product = ServicesHelper
					.getProductQueryServiceAdapter()
					.getProductByKey(productKey);
			if (product != null) {
				isValid = true;
			}
		} catch (org.omg.CORBA.UserException e) {
			Log.exception(logPrefix, e);
		}
		return isValid;
	}

	private String getOrderIdString(OrderIdStruct orderId) {
		StringBuilder toStr = new StringBuilder(50);
		//Printed in this format -> CBOE:690:PPO:12:h=398:l=3264
		toStr.append(orderId.executingOrGiveUpFirm.exchange).append(':');
		toStr.append(orderId.executingOrGiveUpFirm.firmNumber).append(':');
		toStr.append(orderId.branch).append(':').append(
				orderId.branchSequenceNumber);
		toStr.append(":h=").append(orderId.highCboeId).append(":l=").append(
				orderId.lowCboeId);

		return toStr.toString();
	}

	/*private void dispatchOrderStatus(Object channalData, int channelType, String orderUserId) {
	    ChannelKey dispatchChannelKey;
	    ChannelEvent event;
	    switch (channelType) {
	        case ChannelType.ORDER_FILL_REPORT:
	        case ChannelType.CANCEL_REPORT:
	        case ChannelType.ORDER_ACCEPTED_BY_BOOK:
	        case ChannelType.ORDER_UPDATE:
	        case ChannelType.ORDER_STATUS_UPDATE:
	        case ChannelType.NEW_ORDER:
	        case ChannelType.ACCEPT_ORDERS:
	        case ChannelType.ORDER_QUERY_EXCEPTION:
	        case ChannelType.ORDER_BUST_REPORT:
	        case ChannelType.ORDER_BUST_REINSTATE_REPORT:
	        case ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM:
	        case ChannelType.ORDER_BUST_REPORT_BY_TRADING_FIRM:
	            if (userId.equals(orderUserId) || isTradingFirm) {
	                dispatchChannelKey = new ChannelKey(channelType, userId);
	                event = internalEventChannel.getChannelEvent(this, dispatchChannelKey, channalData);
	                orderQuerySupplier.dispatch(event);
	            }
	            break;
	        case ChannelType.ORDER_FILL_REPORT_BY_FIRM:
	        case ChannelType.CANCEL_REPORT_BY_FIRM:
	        case ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM:
	        case ChannelType.ORDER_UPDATE_BY_FIRM:
	        case ChannelType.NEW_ORDER_BY_FIRM:
	        case ChannelType.ACCEPT_ORDERS_BY_FIRM:
	        case ChannelType.ORDER_BUST_REPORT_BY_FIRM:
	        case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM:
	            if (firmKey != null) {
	                dispatchChannelKey = new ChannelKey(channelType, firmKeyContainer);
	                event = internalEventChannel.getChannelEvent(this, dispatchChannelKey, channalData);
	                orderQuerySupplier.dispatch(event);
	            }
	            break;
	        default:
	            Log.alarm(logPrefix + " Calling dispatchOrderStatus, Wrong Channel : " + channelType);
	            break;
	    }
	}*/

	/**
	 * Find last transactionSequenceNumber for a cached order.
	 *
	 * @param orderIndex of the order.
	 * @return transactionSequenceNumber, or 0 (less than any valid value) if not found.
	 */
	public int getOrderTransactionSequenceNumber(
			CompleteOrderIdStructContainer orderIndex) {
		if (null != orderIndex) {
			OrderStruct order = getFinishedOrderByCompleteId(orderIndex);
			if (null == order) {
				order = getActiveOrderByCompleteId(orderIndex);
			}
			if (null != order) {
				return order.transactionSequenceNumber;
			}
		}
		return 0; // nothing found
	}

	/**
	 * This method creates a valid base OrderIdStructContainer ( Cmi or Cboe ) for use
	 * to lookup against the x-ref map and retrieve a CompleteOrderIdStructContainer
	 */
	private CompleteOrderIdStructContainer getValidOrderIdStructContainer(
			OrderIdStruct orderId) {
		// get the "most" valid xref Map key
		BaseOrderIdStructContainer baseOrderIndex = OrderIdStructContainerFactory
				.createValidOrderIdStructContainer(orderId);

		return userOrderCrossReferenceMap.get(baseOrderIndex);
	}

	/**
	 * Changing the behavior to make all those orders eligible for cleanup
	 * those are completely filled or cancelled.
	 *
	 * @param orderStruct
	 * @return true if eligible; else false.
	 */
	private boolean eligibleForCleanup(OrderStruct orderStruct) {
		if (0 == orderStruct.leavesQuantity) {
			return true;
		}
		return false;
	}

	/**
	 * A convenience method to create a CompleteOrderIdStructContainer
	 *
	 * @param orderId
	 * @return CompleteOrderIdStructContainer
	 */
	public CompleteOrderIdStructContainer createCompleteOrderIdStructContainer(
			OrderIdStruct orderId) {
		return OrderIdStructContainerFactory
				.createCompleteOrderIdStructContainer(orderId);
	}

	/**
	 * Gets the lock for the given orderId. If no lock exists, a new one is created.
	 *
	 * @param orderIndex
	 * @return lock Object
	 */
	public Object getOrderLock(CompleteOrderIdStructContainer orderIndex) {
		boolean rLockReleased = false;
		rOrderLock.lock();
		try {
			Object lock = orderLockMap.get(orderIndex);
			if (lock == null) {
				rOrderLock.unlock();
				rLockReleased = true;
				wOrderLock.lock();
				try {
					lock = orderLockMap.get(orderIndex);
					if (lock == null) {
						lock = new Object();
						orderLockMap.put(orderIndex, lock);
					}
				} finally {
					wOrderLock.unlock();
				}
			}
			return lock;
		} finally {
			if (!rLockReleased) {
				rOrderLock.unlock();
			}
		}
	}

	/**
	 * removes the lock corresponding to the given orderId.
	 *
	 * @param orderIndex
	 * @return lock Object
	 */
	public Object removeOrderLock(CompleteOrderIdStructContainer orderIndex) {
		wOrderLock.lock();
		try {
			return orderLockMap.remove(orderIndex);
		} finally {
			wOrderLock.unlock();
		}
	}

	/**
	 * Get the order from the finishedOrderMap.
	 *
	 * @param orderId
	 * @return OrderStruct or null if not found
	 */
	private OrderStruct getFinishedOrderByCompleteId(
			CompleteOrderIdStructContainer orderId) {
		if (orderId != null) {
			return finishedOrderMap.get(orderId);
		}
		return null;
	}

	/**
	 * @param orderId
	 * @return OrderStruct or null if not found
	 */
	public OrderStruct getOrderFromOrderCache(OrderIdStruct orderId) {
		OrderStruct order = null;
		CompleteOrderIdStructContainer completeOrderId = getValidOrderIdStructContainer(orderId);
		if (completeOrderId != null) {
			order = getMatchingOrderFromCache(completeOrderId);
		}
		return order;
	}

	/**
	 * Get the corresponding order from the userOrderMap(active orders). If not found there,
	 * it also checks the finishedOrderMap if configured to do so.
	 *
	 * @param completeOrderId
	 * @return OrderStruct or null if not found
	 */
	private OrderStruct getMatchingOrderFromCache(
			CompleteOrderIdStructContainer completeOrderId) {
		OrderStruct order = null;
		if (completeOrderId != null) {
			order = getActiveOrderByCompleteId(completeOrderId);
			// if not found, check also the inactive order map
			if (order == null && checkFinishedMap()) {
				order = getFinishedOrderByCompleteId(completeOrderId);
			}
		}
		return order;
	}

	/**
	 * Enqueues the (NEW) event to the internal newMsgQueue
	 *
	 * @param event
	 */
	public void enqueue(ChannelEvent event) {
		try {
			newMsgQueue.put(event);
			newMsgCount.getAndIncrement();
		} catch (Exception e) {
			Log
					.exception(
							logPrefix
									+ "Exception while queuing the event into OrderCache! event="
									+ event.toString(), e);
		}
	}

	/**
	 * Dequeues the (NEW) event from the internal newMsgQueue
	 */
	private ChannelEvent dequeue() throws InterruptedException {
		return newMsgQueue.take();
	}

	/*
	 * A utility method to print the cache sizes, for debugging purposes
	 */

	public void printCacheSizes() {
		
		try {
			StringBuilder sb = new StringBuilder(logPrefix.length() + 185);
			sb.append(logPrefix);
			sb.append(" Cache sizes:: userOrderMap:").append(
					userOrderMap.size());
			sb.append(" orderLockMap:").append(orderLockMap.size());
			sb.append(" finishedOrderMap:").append(finishedOrderMap.size());
			sb.append(" finishedOrderQueue:").append(finishedOrderQueue.size());
			sb.append(" newMsgQueue:").append(newMsgQueue.size());
			sb.append(" userOrderCrossReferenceMap:").append(
					userOrderCrossReferenceMap.size());
			Log.information(sb.toString());
		} catch (Exception e) {
		}
		
	}

	public String getCacheSizes() {
		StringBuilder sb = new StringBuilder(logPrefix.length() + 185);
		try {
			sb.append(logPrefix);
			sb.append(" Cache sizes:: \n\tuserOrderMap\t:").append(
					userOrderMap.size());
			sb.append(" \n\tfinishedOrdMap\t:").append(finishedOrderMap.size());
			sb.append(" \n\tnewMsgQueue\t:").append(newMsgQueue.size());
			sb.append("\n\tpenCxlCache\t:").append(pendingCancelCache.size());
			
		} catch (Exception e) {
		}
		return sb.toString();
	}

	// order entry cache methods - START

	public void addToOrderEntryCache(CmiOrderIdStructContainer cmiOrderId,
			int pKey) {
		ProductKeyOrderState pKeyOrderState = new ProductKeyOrderState(pKey,
				orderState_PENDING);
		//synchronized (orderEntryCache) {
			orderEntryCache.put(cmiOrderId, pKeyOrderState);
			//Log.information("size:" + orderEntryCache.size() + " add:" + System.nanoTime());
		//}
	}

	public void orderEntryComplete(OrderIdStruct orderId) {
		CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory
				.createCmiOrderIdStructContainer(orderId);
		orderEntryComplete(cmiOrderId);
	}

	public void orderEntryComplete(CmiOrderIdStructContainer cmiOrderId) {
		//synchronized (orderEntryCache) {
			ProductKeyOrderState pKeyOrderState = orderEntryCache
					.get(cmiOrderId);
			if (pKeyOrderState != null) {
				pKeyOrderState.orderState = orderState_CACHED;

				// remove this entry from orderEntryCache
				orderEntryCache.remove(cmiOrderId);
				//Log.information("size:" + orderEntryCache.size() + " remove:" + System.nanoTime());
			}
		//}
	}

	public void orderEntryFailed(CmiOrderIdStructContainer cmiOrderId,
			boolean maybe) {
		//synchronized (orderEntryCache) {
			ProductKeyOrderState pKeyOrderState = orderEntryCache
					.get(cmiOrderId);
			if (pKeyOrderState != null
					&& pKeyOrderState.orderState == orderState_PENDING) {
				pKeyOrderState.orderState = maybe ? orderState_FAILED_MAYBE
						: orderState_FAILED;

				// remove this entry from orderEntryCache if the order entry was failed for sure.
				if (!maybe) {
					orderEntryCache.remove(cmiOrderId);
				}
			}
		//}
	}

	// returns the product key if  matching entry is found and the state is either PENDING or FAILED_MAYBE; else return -1

	public int getProdKeyFromOrderEntryCache(OrderIdStruct orderId) {
		int pKey = -1;
		CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory
				.createCmiOrderIdStructContainer(orderId);
		ProductKeyOrderState pKeyOrderState = null;
		//synchronized (orderEntryCache) {
			pKeyOrderState = orderEntryCache.get(cmiOrderId);
			if (pKeyOrderState != null
					&& (pKeyOrderState.orderState == orderState_FAILED_MAYBE || pKeyOrderState.orderState == orderState_PENDING)) {
				pKey = pKeyOrderState.pKey;
			}
			return pKey;
		//}
	}

	public OrderState getOrderStateFromOrderEntryCache(OrderIdStruct orderId) {
		OrderState state = orderState_FAILED;
		CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory
				.createCmiOrderIdStructContainer(orderId);
		ProductKeyOrderState pKeyOrderState = null;
		//synchronized (orderEntryCache) {
			pKeyOrderState = orderEntryCache.get(cmiOrderId);
			if (pKeyOrderState != null) {
				state = pKeyOrderState.orderState;
			} else {
				state = orderState_NOT_FOUND;
			}

		//}
		return state;
	}

	// returns true if  matching entry is found in PENDING or FAILED_MAYBE; else return false

	public boolean checkOrderEntryCache(OrderIdStruct orderId) {
		CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory
				.createCmiOrderIdStructContainer(orderId);
		ProductKeyOrderState pKeyOrderState = null;
		synchronized (orderEntryCache) {
			pKeyOrderState = orderEntryCache.get(cmiOrderId);
			if (pKeyOrderState != null
					&& (pKeyOrderState.orderState == orderState_FAILED_MAYBE || pKeyOrderState.orderState == orderState_PENDING)) {
				return true;
			} else {
				return false;
			}
		}
	}

	// order entry cache methods - END

	/**
	 * This thread waits for events from the internal newMsgQueue for NEW events and processes them
	 */
	class ProcessThread extends Thread {
		public void run() {
			StringBuilder started = new StringBuilder(logPrefix.length() + 25);
			started.append(logPrefix).append("ProcessThread started!");
			Log.information(started.toString());
			started = null; // this method will go for a long time; let this object be garbage collected

			while (!shuttingDown) {
				try {
					ChannelEvent newEvent = dequeue();
					currentNewEventInProgress.set(newEvent);
					process(newEvent);
					processedNewMsgCount.getAndIncrement();
					currentNewEventInProgress.set(null);
				} catch (Exception e) {
					// only exception expected here is an interrupted exception after setting shuttingDown;
					// just catching all for safety. No need to log as they are already logged in process().
				}
			}
			// thread finished
			StringBuilder finished = new StringBuilder(logPrefix.length() + 30);
			finished.append(logPrefix).append(" ProcessThread finished!");
			Log.information(finished.toString());
		}
	}

	/*
	 * A wrapper to the OrderStruct to include the order has been completely filled or cancelled.
	 */

	class FinishedOrder {
		private final OrderStruct orderStruct;
		private final long timeExpired;

		public FinishedOrder(OrderStruct orderStruct) {
			this.orderStruct = orderStruct;
			timeExpired = System.currentTimeMillis();
		}

		public OrderStruct getOrderStruct() {
			return orderStruct;
		}

		public long getTimeElapsedInMillis() {
			return (System.currentTimeMillis() - timeExpired);
		}
	}

	/*
	 * Removes a finished order when from finishedOrderMap, finishedOrderQueue and the CrossReferenceMaps.
	 */

	private void removeFinishedOrder(FinishedOrder fo) {
		CompleteOrderIdStructContainer orderIndex = createCompleteOrderIdStructContainer(fo
				.getOrderStruct().orderId);
		synchronized (getOrderLock(orderIndex)) {
			removeFromCrossReferenceMaps(fo.getOrderStruct());
			finishedOrderMap.remove(orderIndex);
			finishedOrderQueue.remove(fo);
			removeOrderLock(orderIndex);
		}
		if (Log.isDebugOn()) {
			Log.debug(logPrefix + " : removed from finished Order Cache "
					+ orderIndex);
		}
	}

	/**
	 * This thread checks and removes those orders from finishedOrderQueue that exceeded FINISHED_ORDER_LIFE_TIME
	 * after being inactive (cancelled or completely filled). When done, it sleeps for FINISHED_ORDER_LIFE_TIME
	 * and repeats. The maximum time an order can stay as inactive is less than 2 * FINISHED_ORDER_LIFE_TIME.
	 */
	class CacheCleanupThread extends Thread {
		public void run() {
			while (!shuttingDown) {
				try {
					boolean done = false;
					while (!done) {
						// check if the oldest order to see if 'finishedOrderMinLifeTime' has
						// elapased since the order has been filled/cancelled. If so, remove
						// it. Once all such orders are removed, sleep for finishedOrderMinLifeTime
						// before checking again. A finished order could be present in
						// the finishedOrder cache up to a max of 2 * finishedOrderMinLifeTime.
						FinishedOrder fo = finishedOrderQueue.peek();
						if (fo == null
								|| fo.getTimeElapsedInMillis() < getFinishedOrderMinLifeTime()) {
							done = true;
						} else {
							removeFinishedOrder(fo);
						}
					}
					if (Log.isDebugOn()) {
						printCacheSizes();
					}
					sleep(getFinishedOrderMinLifeTime());
				} catch (InterruptedException ie) {
				} catch (Exception e) {
					Log.exception(
							logPrefix + " CacheCleanupThread: Exception:", e);
				}
			}
            // mwm PITS 80100 debug
            // StringBuilder mmPaused = new StringBuilder(logPrefix.length() + 40);
            // mmPaused.append(logPrefix).append(" waiting 60 secs in CacheCleanupThread");
            // Log.information(mmPaused.toString());
            // try {
            //    sleep(60000L); // wait a minute
            // } catch (InterruptedException e) {
            //    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            // }
            // mwm PITS 80100 debug
            // mwm PITS 80100 debug
			StringBuilder finished = new StringBuilder(logPrefix.length() + 50);
			finished.append(logPrefix).append(" CacheCleanupThread finished!");
            if (Log.isDebugOn()) {
			    finished.append(" [Cleanup: ") 
                    .append(this.getName()).append("/")
                    .append(this.getId()).append("]");
            }
			Log.information(finished.toString());
            // mwm PITS 80100 debug
            // no need to check - userId WILL be in cleanupTable
            cleanupTable.remove(userId);
		}
	}

	public String getOrderByBranchAndSequenceNumber(String branchSeqNum) {

        StringBuilder sb = new StringBuilder();
        boolean listAllOrders = branchSeqNum.equals("_all_");
        ArrayList allOrders = null;
        if (listAllOrders) {
            allOrders = new ArrayList();
        }
        try {
            //Order Entry Cache
            for (Iterator<CmiOrderIdStructContainer> it = orderEntryCache.keySet().iterator(); it.hasNext();) {
                CmiOrderIdStructContainer cosc = it.next();
                if (branchSeqNum.equals(cosc.getBranchId() + cosc.getBranchSeqNumber())) {
                    sb.append("OrdCacheName\t= ").append("orderEnryCache / pendingOrder").append("\n");
                    sb.append("ClOrderId\t= ").append(branchSeqNum).append("\n");
                    sb.append("OrderState\t= ").append(orderEntryCache.get(cosc)).append("\n");
                } else if (listAllOrders) {
                    allOrders.add(cosc.getBranchId() + cosc.getBranchSeqNumber());
                } else {
                    sb.append("Order Not Found in orderEnryCache / pendingOrder \n");
                }
            }

            // first check userOrderMap
            for (Iterator<CompleteOrderIdStructContainer> it = userOrderMap.keySet().iterator(); it.hasNext();) {
                CompleteOrderIdStructContainer cosc = it.next();
                if (branchSeqNum.equals(cosc.getBranchId() + cosc.getBranchSeqNumber())) {
                    OrderStruct ordStruct = userOrderMap.get(cosc);
                    if (ordStruct != null) {
                        formatString(ordStruct, sb, "activeOrdCache");
                    } else {
                        sb.append("Order Not Found in activeOrderCache \n");
                    }
                } else if (listAllOrders) {
                    allOrders.add(cosc.getBranchId() + cosc.getBranchSeqNumber());
                }
            }
            //Second check finished Map
            for (Iterator<CompleteOrderIdStructContainer> it = finishedOrderMap.keySet().iterator(); it.hasNext();) {
                CompleteOrderIdStructContainer cosc = it.next();
                if (branchSeqNum.equals(cosc.getBranchId() + cosc.getBranchSeqNumber())) {
                    OrderStruct ordStruct = finishedOrderMap.get(cosc);
                    if (ordStruct != null) {
                        formatString(ordStruct, sb, "finOrdCache");
                    } else {
                        sb.append("Order Not Found in finishedOrderCache \n");
                    }
                } else if (listAllOrders) {
                    allOrders.add(cosc.getBranchId() + cosc.getBranchSeqNumber());
                }
            }
            //pending cancel
            for (Iterator<CmiOrderIdStructContainer> it = pendingCancelCache.keySet().iterator(); it.hasNext();) {
                CmiOrderIdStructContainer cosc = it.next();
                if (branchSeqNum.equals(cosc.getBranchId() + cosc.getBranchSeqNumber())) {
                    sb.append("OrdCacheName\t= ").append("pendingCancelCache").append("\n");
                    sb.append("ClOrderId\t= ").append(branchSeqNum).append("\n");
                    sb.append("OrderState\t= ").append(pendingCancelCache.get(cosc).toString()).append("\n");
                } else if (listAllOrders) {
                    allOrders.add(cosc.getBranchId() + cosc.getBranchSeqNumber());
                } else {
                    sb.append("Order Not Found in pendingCancelCache \n");
                }
            }

        } catch (Exception e) {
            Log.exception("Error processing AR Command \"queryOrderCache\" for :<" + branchSeqNum + ">", e);
        }

        if (listAllOrders) {
            Collections.sort(allOrders);
            for (Iterator it = allOrders.iterator(); it.hasNext();) {
                sb.append(it.next()).append(" ");
            }
        }
        return sb.append("\n").toString();
    }

	private void formatString(OrderStruct ordStruct, StringBuilder sb, String cacheName) {
		sb.append("OrdCacheName\t= ").append(cacheName).append("\n");
		sb.append("ClOrderId\t= ").append(ordStruct.orderId.branch).append(ordStruct.orderId.branchSequenceNumber).append("\n");
		sb.append("ClassKey\t= ").append(ordStruct.classKey).append("\n");
		sb.append("ProductKey\t= ").append(ordStruct.productKey).append("\n");
		sb.append("ExecutingFirm\t= ").append(ordStruct.orderId.executingOrGiveUpFirm.exchange+":"+ordStruct.orderId.executingOrGiveUpFirm.firmNumber).append("\n");
		sb.append("CorrFirm\t= ").append(ordStruct.orderId.correspondentFirm).append("\n");
		sb.append("OriginalQty\t= ").append(ordStruct.originalQuantity).append("\n");
		sb.append("TradedQty\t= ").append(ordStruct.tradedQuantity).append("\n");
		sb.append("CancelQty\t= ").append(ordStruct.cancelledQuantity).append("\n");
		sb.append("LeavesQty\t= ").append(ordStruct.leavesQuantity).append("\n");
	}

}
