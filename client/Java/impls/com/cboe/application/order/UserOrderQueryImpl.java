package com.cboe.application.order;

import com.cboe.application.order.common.UserOrderServiceUtil;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.AuctionProcessor;
import com.cboe.application.shared.consumer.AuctionProcessorFactory;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.supplier.*;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.supplier.proxy.GMDSupplierProxy;
import com.cboe.domain.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;
import com.cboe.idl.cmiCallbackV3.CMIAuctionConsumer;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiConstants.UserRoles;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.NotAcceptedCodes; 
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.OperationResultStruct;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListenerProxy;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.proxy.BaseChannelListenerProxy;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;

import java.util.*;

/*
 * The UserOrderQuery Interface is one half of the Order functionality for the SBT
 * system.  The other is the Trader Interface.  The UserOrderqueryImpl implements
 * the OrderQuery interface and the OrderStatusCollector interface.  The order status
 * collector interface conatains all callback methods used to receive changes to orders
 * from the CBOE servers.  This impl receives the CBOE messages from the CAS event channel
 * listeners, updates the cached copy of the order and sends the proper related CAS order
 * message to all registered listeners of the CAS internal event channel (IEC).
 *
 * @author Thomas Lynch
 */
public class UserOrderQueryImpl extends BObject implements OrderQueryV6, OrderStatusCollector, UserSessionLogoutCollector, AuctionCollector
{
    private UserOrderService           userOrderService;
    private OrderStatusSupplier         orderStatusSupplier;
    private OrderStatusV2Supplier       orderStatusV2Supplier;
    private AuctionSupplier             auctionSupplier;
    private AuctionProcessor            auctionProcessor;

    private ProductQueryServiceAdapter pqAdapter;
    private OrderQueryCache            orderQueryCache;

    protected OrderStatusCollectorSupplier orderStatusCollectorSupplier = null;
    protected ChannelListener orderStatusCollectorProxy    = null;

    private UserSessionLogoutProcessor  logoutProcessor;

    private SessionManager              currentSession; /* the current session manager */
    private String thisUserId;             //Single user or main Baxk Office signature (memberKey).
    private String thisExchange;
    private String thisAcronym;
    private ExchangeFirmStructContainer       thisFirmKeyContainer;  // container for firm key struct

    // Indicate which channel will deliver Auction events: true for AUCTION_USER
    // channel, false for AUCTION channel.
    private boolean filterAuctionByUser;
    // Number of milliseconds to wait before considering an Auction message
    // to have timed out.
    private int auctionCallbackTimeout;

    // Objects that are listening to us. Used as a set (key is important, data
    // irrelevant). See com.cboe.application.marketData.MarketQueryBaseImpl.
    protected Map auctionListeners;

    protected SubscriptionService subscriptionService;

    private static final Integer INTEGER_0 = 0;
    private short DAIM_AUCTION = 8;

    private ConcurrentEventChannelAdapter internalEventChannel;

    /* This is the extensive UserOrderQueryImpl constructor. */
    public UserOrderQueryImpl()
    {
        try
        {
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception(this, "Exception getting CAS_INSTRUMENTED_IEC!", e);
        }
    }

    /** Set this object's Auction Callback Timeout parameter.
     * @param auctionCallbackTimeout Milliseconds to wait before we consider
     *     an Auction message to have timed out.
     */
    public void setAuctionCallbackTimeout(int auctionCallbackTimeout)
    {
        this.auctionCallbackTimeout = auctionCallbackTimeout;
    }

    public void create(String name)
    {
        super.create(name);
        getOrderQueryCache();
    }

    /*
      * Helper method to build a CAS order detail struct from the CBOE order struct.
      * @param order OrderStruct  The CBOE Business order object.
      * @return OrderDetailStruct
      */
    private OrderDetailStruct buildOrderDetailStruct( OrderStruct order, short statusUpdateReason)
    {
        ProductNameStruct productName = null;
        try {
//PQRefactor: this used to be synchronized
//            productName = ProductQueryManagerImpl.getProduct(order.productKey).productName;
            productName = getProductQueryServiceAdapter().getProductByKey(order.productKey).productName;
        }
        catch (Exception e) {
            Log.exception(this, "session : " + currentSession, e);
            return null;
        }

        return new OrderDetailStruct( productName, statusUpdateReason, order);
    }

    private OrderDetailStruct[] buildOrderDetailStruct( OrderStruct[] orders, short statusUpdateReason )
    {
        OrderDetailStruct[] orderDetails = new OrderDetailStruct[ orders.length ];
        for ( int i = 0; i < orderDetails.length; i++ )
        {
            orderDetails[ i ] = buildOrderDetailStruct( orders[ i ], statusUpdateReason );
        }

        return orderDetails;
    }

    /*
     * Internal helper method to dispatch order objects for CBOE events.
     * @param channel int               The channel type to publish to.
     * @param index Integer             The channel index.
     * @param order OrderDetailStruct   The order to send.
     */
    private void dispatch( int channel, Object index, OrderDetailStruct order)
    {
        OrderDetailStruct orderClone = OrderStructBuilder.cloneOrderDetailStruct(order);
        OrderDetailStruct[] orders = {orderClone};
        ChannelKey key = new ChannelKey(channel, index);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, orders);
        orderStatusSupplier.dispatch(event);
    }

    /*
     * Internal helper method to dispatch order objects for CBOE events.
     * @param channel int               The channel type to publish to.
     * @param index Integer             The channel index.
     * @param order OrderDetailStruct   The order to send.
     */
    private void dispatchV2( int channel, Object index, OrderDetailStruct order)
    {
        OrderDetailStruct orderClone = OrderStructBuilder.cloneOrderDetailStruct(order);
        OrderDetailStruct[] orders = {orderClone};
        ChannelKey key = new ChannelKey(channel, index);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, orders);
        orderStatusV2Supplier.dispatch(event);
    }

    /*
     * Internal helper method to dispatch order objects for CBOE events.
     * @param order OrderDetailStruct[]  The orders to send.
     */
    private void dispatchAllOrders( OrderDetailStruct[] orders)
    {
        int numOfOrders = orders.length;
        String smgr = currentSession.toString();
        StringBuilder dispatching = new StringBuilder(smgr.length()+65);
        dispatching.append("session : ").append(currentSession)
                   .append(" : About to Dispatch Orders to the GUI : ").append(numOfOrders);
        Log.information(this, dispatching.toString());
        if ( numOfOrders > 0 )
        {
            for (int i = 0; i < numOfOrders; i++)
            {
                orders[i].statusChange = StatusUpdateReasons.QUERY;
            }

            OrderDetailStruct[] ordersClone = OrderStructBuilder.cloneOrderDetailStructs(orders);

            ChannelKey key = new ChannelKey(ChannelType.CB_ALL_ORDERS, orders[0].orderStruct.userId);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, ordersClone);
            orderStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ALL_ORDERS_V2, orders[0].orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, key, ordersClone);
            orderStatusV2Supplier.dispatch(event);

            if (!thisUserId.equals(orders[0].orderStruct.userId))
            {
                ExchangeFirmStructContainer  firmKeyContainer;
                firmKeyContainer = new  ExchangeFirmStructContainer(orders[0].orderStruct.orderId.executingOrGiveUpFirm);

                key = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, firmKeyContainer);
                event = internalEventChannel.getChannelEvent(this, key, ordersClone);
                orderStatusSupplier.dispatch(event);

                key = new ChannelKey(ChannelType.CB_ORDERS_FOR_FIRM_V2, firmKeyContainer);
                event = internalEventChannel.getChannelEvent(this, key, ordersClone);
                orderStatusV2Supplier.dispatch(event);
            }
        }
    }

    /*
     * Internal helper method to dispatch order objects for CBOE events.
     * @param order OrderDetailStruct[]  The orders to send.
     */
    private void dispatchUserOrders(OrderDetailStruct[] orders)
    {
        int numOrders = orders.length;

        String smgr = currentSession.toString();
        StringBuilder dispatching = new StringBuilder(smgr.length()+55);
        dispatching.append("session: ").append(currentSession).append("  About to dispatch ").append(numOrders).append(" user orders.");
        Log.information(this, dispatching.toString());

        if (numOrders > 0)
        {
            for (int i = 0; i < numOrders; ++i)
            {
                orders[i].statusChange = StatusUpdateReasons.QUERY;
            }

            OrderDetailStruct[] ordersClone = OrderStructBuilder.cloneOrderDetailStructs(orders);
            ChannelKey key = new ChannelKey(ChannelType.CB_ALL_ORDERS, thisUserId);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, ordersClone);

            orderStatusSupplier.dispatch(event);
        }
    }

    /*
     * Internal helper method to dispatch order objects for CBOE events.
     * @param order OrderDetailStruct[]  The orders to send.
     */
    private void dispatchFirmOrders(OrderDetailStruct[] orders)
    {
        int numOrders = orders.length;

        String smgr = currentSession.toString();
        StringBuilder dispatching = new StringBuilder(smgr.length()+55);
        dispatching.append("session: ").append(smgr).append("  About to dispatch ").append(numOrders).append(" firm orders.");
        Log.information(this, dispatching.toString());

        if (numOrders > 0)
        {
            for (int i = 0; i < numOrders; ++i)
            {
                orders[i].statusChange = StatusUpdateReasons.QUERY;
            }

            OrderDetailStruct[] ordersClone = OrderStructBuilder.cloneOrderDetailStructs(orders);
            ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, thisFirmKeyContainer);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, ordersClone);

            orderStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDERS_FOR_FIRM_V2, thisFirmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, ordersClone);

            orderStatusV2Supplier.dispatch(event);
        }
    }

    /**
     * Helper methods that dispatches the given orders on the specified channel.
     */
    private void dispatchOrdersV2(int channel, Object index, OrderDetailStruct[] orderDetails)
    {
        if (orderDetails.length > 0)
        {
            OrderDetailStruct[] orders = OrderStructBuilder.cloneOrderDetailStructs(orderDetails);
            ChannelKey key = new ChannelKey(channel, index);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, key, orders);

            orderStatusV2Supplier.dispatch(event);
        }
    }

    /*
     * Internal helper method to dispatch exception objects.
     * @param description String
     */
    private void dispatchException(String description)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_ORDERS, INTEGER_0);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, description);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ALL_ORDERS_V2, INTEGER_0);
        event = internalEventChannel.getChannelEvent(this, key, description);
        orderStatusV2Supplier.dispatch(event);
    }

    /*
     * Internal helper method to publish order detail structs for the CBOE events.
     * @param order OrderDetailStruct   The order information to be published.
     */
    private void publishOnOrderChannels(OrderDetailStruct order) {
        // Send to this Product Key Order listeners
        dispatch( ChannelType.CB_ORDERS_FOR_PRODUCT, Integer.valueOf(order.orderStruct.productKey), order);
        dispatchV2( ChannelType.CB_ORDERS_FOR_PRODUCT_V2, Integer.valueOf(order.orderStruct.productKey), order);

        // Send to User Order listeners
        dispatch( ChannelType.CB_ORDERS_BY_USER, order.orderStruct.userId, order);
        dispatchV2( ChannelType.CB_ORDERS_FOR_USER_V2, order.orderStruct.userId, order);

        // Send to Firm Order listeners
        if (!thisUserId.equals(order.orderStruct.userId))
        {
            dispatch( ChannelType.CB_ORDERS_BY_FIRM, thisFirmKeyContainer, order);
            dispatchV2( ChannelType.CB_ORDERS_FOR_FIRM_V2, thisFirmKeyContainer, order);
            dispatchV2( ChannelType.CB_ORDERS_FOR_FIRM_FOR_CLASS_V2, new FirmClassContainer(
                    thisFirmKeyContainer.getExchange(), thisFirmKeyContainer.getFirmNumber(), order.orderStruct.classKey), order);
        }

        // Send to All Order listeners
        dispatch( ChannelType.CB_ALL_ORDERS, order.orderStruct.userId, order);
        dispatchV2( ChannelType.CB_ALL_ORDERS_V2, order.orderStruct.userId, order);

        // Send to this Product Type Order listeners
        dispatch( ChannelType.CB_ALL_ORDERS_FOR_TYPE, Integer.valueOf(order.orderStruct.productType), order);
        dispatchV2( ChannelType.CB_ORDERS_FOR_TYPE_V2, Integer.valueOf(order.orderStruct.productType), order);

        // Send to Product Class Order listeners
        dispatch( ChannelType.CB_ORDERS_BY_CLASS, Integer.valueOf(order.orderStruct.classKey), order);
        dispatchV2( ChannelType.CB_ORDERS_FOR_CLASS_V2, Integer.valueOf(order.orderStruct.classKey), order);

        // Send to Product Class Order listeners
        dispatch( ChannelType.CB_ORDERS_FOR_SESSION, order.orderStruct.activeSession, order);
        dispatchV2( ChannelType.CB_ORDERS_FOR_SESSION_V2, order.orderStruct.activeSession, order);
    }

    //////////////////////////// Order Status Collector Interface /////////////////
    /**
      * This sends a bust order struct to any registered listeners.
      */
    public void acceptOrderBustReport(OrderStruct orderStruct, BustReportStruct[] busted, short statusChange )
    {
        if(Log.isDebugOn())
            Log.debug(this, "calling acceptOrderBustReport for " + currentSession + " orderId:" + getOrderIdString(orderStruct.orderId));

        OrderDetailStruct          order = null;
        ChannelKey                 key;
        ChannelEvent               event;

        if ( statusChange == StatusUpdateReasons.POSSIBLE_RESEND ) {
            order = buildOrderDetailStruct(orderStruct, StatusUpdateReasons.POSSIBLE_RESEND);
        } else {
            order = buildOrderDetailStruct(orderStruct, StatusUpdateReasons.BUST);
        }

        if (order == null)
        {
        	Log.alarm(this, orderStruct.userId + " received BUST report for " + UserOrderServiceUtil.getOrderIdString(orderStruct.orderId) +  
        			" with invalid productKey:" + orderStruct.productKey);
        	return;
        }


        OrderBustReportStruct bustedOrder = new OrderBustReportStruct(  order, busted );

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT, orderStruct.userId);
        event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_V2, orderStruct.userId);
        event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
        orderStatusV2Supplier.dispatch(event);

        if (!thisUserId.equals(orderStruct.userId))
        {
            ExchangeFirmStructContainer firmKeyContainer =
                new  ExchangeFirmStructContainer(orderStruct.orderId.executingOrGiveUpFirm);

            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
            orderStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_V2, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
            orderStatusV2Supplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_FOR_CLASS_V2, new FirmClassContainer(firmKeyContainer.getExchange(),firmKeyContainer.getFirmNumber(),orderStruct.classKey));
            event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
            orderStatusV2Supplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_TRADING_FIRM, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
            orderStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_TRADING_FIRM, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
            orderStatusV2Supplier.dispatch(event);
        }

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_CLASS, Integer.valueOf(orderStruct.classKey));
        event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_CLASS_V2, Integer.valueOf(orderStruct.classKey));
        event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_TYPE, Integer.valueOf(orderStruct.productType));
        event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_TYPE_V2, Integer.valueOf(orderStruct.productType));
        event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_PRODUCT, Integer.valueOf(orderStruct.productKey));
        event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_PRODUCT_V2, Integer.valueOf(orderStruct.productKey));
        event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_SESSION, orderStruct.activeSession);
        event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_SESSION_V2, orderStruct.activeSession);
        event = internalEventChannel.getChannelEvent(this, key, bustedOrder);
        orderStatusV2Supplier.dispatch(event);
    }

    /**
      * This sends a bust reinstate order struct to any registered listeners.
      * @param reinstated The order bust reinstate report.
      */
    public void acceptOrderBustReinstateReport(OrderStruct orderStruct, BustReinstateReportStruct reinstated, short statusChange )
    {
        if(Log.isDebugOn())
            Log.debug(this, "calling acceptOrderBustReinstateReport for " + currentSession + " orderId:" + getOrderIdString(orderStruct.orderId));

        OrderDetailStruct          order = null;
        ChannelKey                 key;
        ChannelEvent               event;

        if ( statusChange == StatusUpdateReasons.POSSIBLE_RESEND ) {
            order = buildOrderDetailStruct(orderStruct, StatusUpdateReasons.POSSIBLE_RESEND);
        } else {
            order = buildOrderDetailStruct(orderStruct, StatusUpdateReasons.REINSTATE);
        }

        if (order == null)
        {
        	Log.alarm(this, orderStruct.userId + " received BUST_REINSTATE report for " + UserOrderServiceUtil.getOrderIdString(orderStruct.orderId) +  
        			" with invalid productKey:" + orderStruct.productKey);
        	return;
        }


        OrderBustReinstateReportStruct bustReinstatedOrder = new OrderBustReinstateReportStruct(order, reinstated);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT, orderStruct.userId);
        event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_V2, orderStruct.userId);
        event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
        orderStatusV2Supplier.dispatch(event);

        if (!thisUserId.equals(orderStruct.userId))
        {
            ExchangeFirmStructContainer firmKeyContainer =
                new ExchangeFirmStructContainer(orderStruct.orderId.executingOrGiveUpFirm);

            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
            orderStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_V2, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
            orderStatusV2Supplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_FOR_CLASS_V2, new FirmClassContainer
                    (firmKeyContainer.getExchange(), firmKeyContainer.getFirmNumber() ,orderStruct.classKey));
            event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
            orderStatusV2Supplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TRADING_FIRM, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
            orderStatusSupplier.dispatch(event);

        }

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_PRODUCT, Integer.valueOf(orderStruct.productKey));
        event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_PRODUCT_V2, Integer.valueOf(orderStruct.productKey));
        event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TYPE, Integer.valueOf(orderStruct.productType));
        event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TYPE_V2, Integer.valueOf(orderStruct.productType));
        event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_CLASS, Integer.valueOf(orderStruct.classKey));
        event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_CLASS_V2, Integer.valueOf(orderStruct.classKey));
        event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_SESSION, orderStruct.activeSession);
        event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_SESSION_V2, orderStruct.activeSession);
        event = internalEventChannel.getChannelEvent(this, key, bustReinstatedOrder);
        orderStatusV2Supplier.dispatch(event);
    }

    /**
     * This refreshes the cached order information and sends a new copy of
     * the order detail structure to any registered listeners.
     * @param report CancelReportStruct   The published cancel report.
     */
    public void acceptCancelReport(OrderStruct orderStruct, CancelReportStruct[] report, short statusChange )
    {
        String smgr = currentSession.toString();
        String oid = getOrderIdString(orderStruct.orderId);
        StringBuilder calling = new StringBuilder(smgr.length()+oid.length()+45);
        calling.append("calling acceptCancelReport for ").append(smgr).append(" orderId:").append(oid);
        Log.information(this, calling.toString());

        OrderDetailStruct      order = null;
        ChannelKey             key;
        ChannelEvent           event;

        if ( statusChange == StatusUpdateReasons.POSSIBLE_RESEND )
        {
            order = buildOrderDetailStruct(orderStruct, StatusUpdateReasons.POSSIBLE_RESEND);
        }
        else
        {
            order = buildOrderDetailStruct(orderStruct, StatusUpdateReasons.CANCEL);
        }
        
        if (order == null)
        {
        	Log.information(this, orderStruct.userId + " received CANCEL report for " + UserOrderServiceUtil.getOrderIdString(orderStruct.orderId) +  
        			" with invalid productKey:" + orderStruct.productKey);
        	return;
        }

        //Construct OrderCanceledReportStruct
        OrderCancelReportStruct orderReport = new OrderCancelReportStruct(order, report);

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT, orderStruct.userId);
        event = internalEventChannel.getChannelEvent(this, key, orderReport);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_V2, orderStruct.userId);
        event = internalEventChannel.getChannelEvent(this, key, orderReport);
        orderStatusV2Supplier.dispatch(event);

        if (!thisUserId.equals(orderStruct.userId))
        {
            ExchangeFirmStructContainer firmKeyContainer =
                new ExchangeFirmStructContainer(orderStruct.orderId.executingOrGiveUpFirm);

            key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, orderReport);
            orderStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_V2, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, orderReport);
            orderStatusV2Supplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_FOR_CLASS_V2, new FirmClassContainer(
                    firmKeyContainer.getExchange(), firmKeyContainer.getFirmNumber(), orderStruct.classKey));
            event = internalEventChannel.getChannelEvent(this, key, orderReport);
            orderStatusV2Supplier.dispatch(event);
        }

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_FOR_PRODUCT, Integer.valueOf(order.orderStruct.productKey));
        event = internalEventChannel.getChannelEvent(this, key, orderReport);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_PRODUCT_V2, Integer.valueOf(order.orderStruct.productKey));
        event = internalEventChannel.getChannelEvent(this, key, orderReport);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_FOR_TYPE, Integer.valueOf(order.orderStruct.productType));
        event = internalEventChannel.getChannelEvent(this, key, orderReport);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_TYPE_V2, Integer.valueOf(order.orderStruct.productType));
        event = internalEventChannel.getChannelEvent(this, key, orderReport);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_CLASS, Integer.valueOf(order.orderStruct.classKey));
        event = internalEventChannel.getChannelEvent(this, key, orderReport);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_CLASS_V2, Integer.valueOf(order.orderStruct.classKey));
        event = internalEventChannel.getChannelEvent(this, key, orderReport);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_FOR_SESSION, orderStruct.activeSession);
        event = internalEventChannel.getChannelEvent(this, key, orderReport);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_SESSION_V2, orderStruct.activeSession);
        event = internalEventChannel.getChannelEvent(this, key, orderReport);
        orderStatusV2Supplier.dispatch(event);
    }

    /**
      * This sends a filled order struct to any registered listeners.
      */
    public void acceptOrderFillReport(OrderStruct orderStruct, FilledReportStruct[] filled, short statusChange )
     {
        String smgr = currentSession.toString();
        String oid = getOrderIdString(orderStruct.orderId);
        StringBuilder calling = new StringBuilder(smgr.length()+oid.length()+45);
        calling.append("calling acceptOrderFillReport for ").append(smgr).append(" orderId:").append(oid);
        Log.information(this, calling.toString());

        OrderDetailStruct      order = null;
        ChannelKey             key;
        ChannelEvent           event;

        if ( statusChange == StatusUpdateReasons.POSSIBLE_RESEND ) {
            order = buildOrderDetailStruct(orderStruct, StatusUpdateReasons.POSSIBLE_RESEND);
        } else {
            order = buildOrderDetailStruct(orderStruct, StatusUpdateReasons.FILL);
        }
        
        if (order == null)
        {
        	Log.alarm(this, orderStruct.userId + " received FILL report for " + UserOrderServiceUtil.getOrderIdString(orderStruct.orderId) +  
        			" with invalid productKey:" + orderStruct.productKey);
        	return;
        }

        OrderFilledReportStruct filledOrder = new OrderFilledReportStruct(order, filled);

        key = new ChannelKey(ChannelType.CB_FILLED_REPORT, orderStruct.userId);
        event = internalEventChannel.getChannelEvent(this, key, filledOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_V2, orderStruct.userId);
        event = internalEventChannel.getChannelEvent(this, key, filledOrder);
        orderStatusV2Supplier.dispatch(event);

        if (!thisUserId.equals(orderStruct.userId))
        {
            ExchangeFirmStructContainer firmKeyContainer =
                new  ExchangeFirmStructContainer(orderStruct.orderId.executingOrGiveUpFirm);

            key = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, filledOrder);
            orderStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_V2, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, filledOrder);
            orderStatusV2Supplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_FOR_CLASS_V2, new FirmClassContainer
                    (firmKeyContainer.getExchange(), firmKeyContainer.getFirmNumber() ,orderStruct.classKey));
            event = internalEventChannel.getChannelEvent(this, key, filledOrder);
            orderStatusV2Supplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_TRADING_FIRM, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, key, filledOrder);
            orderStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_TRADING_FIRM, orderStruct.userId);
            event = internalEventChannel.getChannelEvent(this, key, filledOrder);
            orderStatusV2Supplier.dispatch(event);
        }

        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_FOR_PRODUCT, Integer.valueOf(orderStruct.productKey));
        event = internalEventChannel.getChannelEvent(this, key, filledOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_PRODUCT_V2, Integer.valueOf(orderStruct.productKey));
        event = internalEventChannel.getChannelEvent(this, key, filledOrder);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_CLASS, Integer.valueOf(orderStruct.classKey));
        event = internalEventChannel.getChannelEvent(this, key, filledOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_CLASS_V2, Integer.valueOf(orderStruct.classKey));
        event = internalEventChannel.getChannelEvent(this, key, filledOrder);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_FOR_TYPE, Integer.valueOf(orderStruct.productType));
        event = internalEventChannel.getChannelEvent(this, key, filledOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_TYPE_V2, Integer.valueOf(orderStruct.productType));
        event = internalEventChannel.getChannelEvent(this, key, filledOrder);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_FOR_SESSION, orderStruct.activeSession);
        event = internalEventChannel.getChannelEvent(this, key, filledOrder);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_SESSION_V2, orderStruct.activeSession);
        event = internalEventChannel.getChannelEvent(this, key, filledOrder);
        orderStatusV2Supplier.dispatch(event);
    }

    public void acceptOrderStatusUpdate(OrderStruct orderStruct, short statusChange )
    {
        String smgr = currentSession.toString();
        String oid = getOrderIdString(orderStruct.orderId);
        StringBuilder calling = new StringBuilder(smgr.length()+oid.length()+50);
        calling.append("calling acceptOrderStatusUpdate for ").append(smgr).append(" orderId:").append(oid);
        Log.information(this, calling.toString());

        OrderDetailStruct orderDetail = buildOrderDetailStruct(orderStruct, statusChange);
        if (orderDetail == null)
        {
        	Log.alarm(this, orderStruct.userId + " received ORDER_STATUS_UPDATE report for " + UserOrderServiceUtil.getOrderIdString(orderStruct.orderId) +  
        			" with invalid productKey:" + orderStruct.productKey);
        	return;
        }

        OrderDetailStruct[] orderDetailStructs = {orderDetail};

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDER_STATUS_UPDATE, orderDetailStructs[0].orderStruct.userId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, key, orderDetailStructs);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_STATUS_UPDATE_V2, orderDetailStructs[0].orderStruct.userId);
        event = internalEventChannel.getChannelEvent(this, key, orderDetailStructs);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_STATUS_UPDATE_FOR_CLASS_V2, Integer.valueOf(orderDetailStructs[0].orderStruct.classKey));
        event = internalEventChannel.getChannelEvent(this, key, orderDetailStructs);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_ORDER_STATUS_UPDATE_FOR_CLASS_V2, Integer.valueOf(orderDetailStructs[0].orderStruct.classKey));
        event = internalEventChannel.getChannelEvent(this, key, orderDetailStructs);
        orderStatusV2Supplier.dispatch(event);
    }

     /**
      *
      */
    public void acceptOrderAcceptedByBook(OrderStruct orderStruct)
    {
        String smgr = currentSession.toString();
        String oid = getOrderIdString(orderStruct.orderId);
        StringBuilder calling = new StringBuilder(smgr.length()+oid.length()+50);
        calling.append("calling acceptOrderAcceptedByBook for ").append(smgr).append(" orderId:").append(oid);
        Log.information(this, calling.toString());

        OrderDetailStruct order = null;
        if (order == null)
        {
        	Log.alarm(this, orderStruct.userId + " received ORDER_ACCEPTEDBYBOOK report for " + UserOrderServiceUtil.getOrderIdString(orderStruct.orderId) +  
        			" with invalid productKey:" + orderStruct.productKey);
        	return;
        }

        order = buildOrderDetailStruct(orderStruct, StatusUpdateReasons.BOOKED);

        publishOnOrderChannels(order);
    }

    /**
      * This refreshes the cached order information and sends a new copy of
      * the order detail structure to any registered listeners.
      */
    public void acceptOrderUpdate(OrderStruct orderStruct)
    {
        String smgr = currentSession.toString();
        String oid = getOrderIdString(orderStruct.orderId);
        StringBuilder calling = new StringBuilder(smgr.length()+oid.length()+40);
        calling.append("calling acceptOrderUpdate for ").append(smgr).append(" orderId:").append(oid);
        Log.information(this, calling.toString());
        if (0 == orderStruct.leavesQuantity)
        {
            // Update can't change order quantity now; if that changes in the future, we should
            // remove a 0-quantity order from the order cache. This message says that Update has
            // changed and we forgot to change this method.
            Log.alarm(this, "acceptOrderUpdate reports order.leavesQuantity=0");
        }

        OrderDetailStruct order = null;

        order = buildOrderDetailStruct(orderStruct, StatusUpdateReasons.UPDATE);
        if (order == null)
        {
        	Log.alarm(this, orderStruct.userId + " received ORDER_UPDATE report for " + UserOrderServiceUtil.getOrderIdString(orderStruct.orderId) +  
        			" with invalid productKey:" + orderStruct.productKey);
        	return;
        }
        publishOnOrderChannels(order);
    }

    /**
      * This adds the cached order information and sends a new copy of
      * the order detail structure to any registered listeners.
      * @param order OrderStruct
      */
    public void acceptNewOrder(OrderStruct order, short statusChange )
    {
        ChannelKey             key;
        ChannelEvent           event;

        String smgr = currentSession.toString();
        String oid = getOrderIdString(order.orderId);
        StringBuilder calling = new StringBuilder(smgr.length()+oid.length()+40);
        calling.append("calling acceptNewOrder for ").append(smgr).append(" orderId:").append(oid);
        Log.information(this, calling.toString());

        OrderDetailStruct   orderDetail = null;
        if ( statusChange == StatusUpdateReasons.POSSIBLE_RESEND )
        {
            orderDetail = buildOrderDetailStruct(order, StatusUpdateReasons.POSSIBLE_RESEND);
        }
        else
        {
            orderDetail = buildOrderDetailStruct(order, statusChange);
        }

        if (orderDetail == null)
        {
        	Log.alarm(this, order.userId + " received NEW report for " + UserOrderServiceUtil.getOrderIdString(order.orderId) +  
        			" with invalid productKey:" + order.productKey);
        	return;
        }

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT, order.userId);
        event = internalEventChannel.getChannelEvent(this, key, orderDetail);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_V2, order.userId);
        event = internalEventChannel.getChannelEvent(this, key, orderDetail);
        orderStatusV2Supplier.dispatch(event);

        if (!thisUserId.equals(order.userId))
        {
            ExchangeFirmStructContainer firmKeyContainer =
                new ExchangeFirmStructContainer(order.orderId.executingOrGiveUpFirm);

            key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_BY_FIRM, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, orderDetail);
            orderStatusSupplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_FIRM_V2, firmKeyContainer);
            event = internalEventChannel.getChannelEvent(this, key, orderDetail);
            orderStatusV2Supplier.dispatch(event);

            key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_FIRM_FOR_CLASS_V2, new FirmClassContainer
                    (firmKeyContainer.getExchange(), firmKeyContainer.getFirmNumber(), order.classKey));
            event = internalEventChannel.getChannelEvent(this, key, orderDetail);
            orderStatusV2Supplier.dispatch(event);
        }

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_PRODUCT, Integer.valueOf(order.productKey));
        event = internalEventChannel.getChannelEvent(this, key, orderDetail);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_PRODUCT_V2, Integer.valueOf(order.productKey));
        event = internalEventChannel.getChannelEvent(this, key, orderDetail);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_BY_CLASS, Integer.valueOf(order.classKey));
        event = internalEventChannel.getChannelEvent(this, key, orderDetail);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_CLASS_V2, Integer.valueOf(order.classKey));
        event = internalEventChannel.getChannelEvent(this, key, orderDetail);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_TYPE, Integer.valueOf(order.productType));
        event = internalEventChannel.getChannelEvent(this, key, orderDetail);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_TYPE_V2, Integer.valueOf(order.productType));
        event = internalEventChannel.getChannelEvent(this, key, orderDetail);
        orderStatusV2Supplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_SESSION, order.activeSession);
        event = internalEventChannel.getChannelEvent(this, key, orderDetail);
        orderStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_SESSION_V2, order.activeSession);
        event = internalEventChannel.getChannelEvent(this, key, orderDetail);
        orderStatusV2Supplier.dispatch(event);
    }

    /**
      * This handles the acceptOrder message from IEC.
      * @param orders OrderStructs
      */
    public void acceptOrders(OrderStruct[] orders)
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+40);
        calling.append("calling acceptOrders for ").append(smgr).append(" count:").append(orders.length);
        Log.information(this, calling.toString());

        OrderDetailStruct[] orderDetails = new OrderDetailStruct[orders.length];

        for ( int i = 0; i < orders.length; i++)
        {
            orderDetails[i] = buildOrderDetailStruct(orders[i], StatusUpdateReasons.QUERY);
        }

        dispatchAllOrders( orderDetails ); //Creates channel keys & events & publishes.
    }

    /**
      * This handles the accept order exception message from IEC.
      * @param description exception description
      */
    public void acceptException(String description)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptException for " + currentSession);
        }
        dispatchException(description);
    }

    /**
      * This returns all orders for the requested userIds and registers the listener object
      * future data updates.  If the caller does not want further updates, pass a null listener object.
      * @param clientListener com.cboe.idl.cmiCallback.ClientOrderStatusConsumer
      */
    public void subscribeOrdersByFirm(
            CMIOrderStatusConsumer clientListener,
            boolean gmdCallback)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling subscribeOrdersByFirm for " + currentSession + " gmd:" + gmdCallback);
    	}

        ChannelKey channelKey;
        if (clientListener != null)
        {
             ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    gmdCallback);

            if(!currentSession.isTradingFirmEnabled())
            {
                checkProxy(orderStatusSupplier, proxyListener, gmdCallback, false, null);

                channelKey = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, thisFirmKeyContainer);
                orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);
            }
        }
        else
        {
            Log.alarm(this, "null clientListener in subscribeOrdersByFirm" + currentSession);
        }

        subscribeOrdersByFirmWithoutPublish(clientListener, gmdCallback);

        OrderStruct[] orders = getOrderQueryCache().publishAllOrders();
        dispatchFirmOrders( buildOrderDetailStruct( orders, StatusUpdateReasons.QUERY ) );
    }

    /**
      * @param clientListener com.cboe.idl.cmiCallback.ClientOrderStatusConsumer
      */
    public void subscribeOrdersByFirmWithoutPublish(
            CMIOrderStatusConsumer clientListener,
            boolean gmdCallback)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling subscribeOrdersByFirmWithoutPublish for " + currentSession + " gmd:" + gmdCallback);
    	}

        if (currentSession.getValidUser().role == UserRoles.FIRM_DISPLAY) {
            if (clientListener != null) {
                ChannelKey channelKey;
                ChannelListener proxyListener =
                        ServicesHelper.getOrderStatusConsumerProxy(clientListener,
                                currentSession,
                                gmdCallback);

                if(currentSession.isTradingFirmEnabled())
                {
                    subscribeOrderStatusForTradingFirm(proxyListener);
                    return;
                }

                checkProxy(orderStatusSupplier, proxyListener, gmdCallback, false, null);

                channelKey = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                subscriptionService.addFirmInterest(proxyListener);
            } else {
                Log.alarm("null clientListener in subscribeOrdersByFirmWithoutPublish " + currentSession);
            }
        }
        else {
            if (clientListener != null) {
                ChannelKey channelKey;
                ChannelListener proxyListener =
                        ServicesHelper.getOrderStatusConsumerProxy(clientListener,
                                currentSession,
                                gmdCallback);

                checkProxy(orderStatusSupplier, proxyListener, gmdCallback, false, null);

//            channelKey = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, thisFirmKeyContainer);
//            addConsumer(channelKey, proxyListener);
                channelKey = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.CANCEL_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_UPDATE_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.NEW_ORDER_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                subscriptionService.addFirmInterest(proxyListener);
            } else {
                Log.alarm("null clientListener in subscribeOrdersByFirmWithoutPublish " + currentSession);
            }

        }

    }

    /**
      * @param clientListener com.cboe.idl.cmiCallback.ClientOrderStatusConsumer
      */
    private void subscribeOrdersByFirmWithoutPublishV2(
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer clientListener,
            boolean gmdCallback)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling subscribeOrdersByFirmWithoutPublishV2 for " + currentSession + " gmd:" + gmdCallback);
    	}

        if (clientListener != null) {
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    gmdCallback);

            if(!currentSession.isTradingFirmEnabled())
	    {
                initGmdStuff(proxyListener, false, null);
	    }

            ChannelKey channelKey;
            if (currentSession.getValidUser().role == UserRoles.FIRM_DISPLAY) {

                if(currentSession.isTradingFirmEnabled())
                {
                    subscribeOrderStatusForTradingFirmV2(proxyListener);
                    return;
                }

                String userId = currentSession.getUserId();
                StringBuilder subscribing = new StringBuilder(userId.length()+35);
                subscribing.append("Subscribing for FIRM Display user ").append(userId);
                Log.information(this, subscribing.toString());

                channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

            } else {

                channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

                channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.CANCEL_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_UPDATE_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.NEW_ORDER_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

                channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM, thisFirmKeyContainer);
                orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);
                
            }
		subscriptionService.addFirmInterest(proxyListener);


        }
        else
        {
            Log.alarm("null clientListener in subscribeOrdersByFirmWithoutPublishV2 " + currentSession);
        }
    }

    private void subscribeOrderStatusForTradingFirm(ChannelListener proxyListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeOrderStatusForTradingFirmGroup for " + currentSession);
        }

        List<String> users = new ArrayList<String>();
        users.add(currentSession.getUserId());
        users.addAll(currentSession.getTradingFirmGroup());
        String userId = currentSession.getUserId();
        StringBuilder subscribing = new StringBuilder(userId.length()+35);
        subscribing.append("Subscribing for TRADING FIRM user ").append(userId);
        Log.information(this,subscribing.toString());
        for(String user : users)
        {
            ChannelKey channelKey;
            channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_TRADING_FIRM, user);
            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, user);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_TRADING_FIRM, user);
            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, user);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TRADING_FIRM, user);
            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, user);

            channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT_BY_TRADING_FIRM, user);
            orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM, user);
            orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_TRADING_FIRM, user);
            orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);
        }

        subscriptionService.addTradingFirmInterest(proxyListener, users);
    }

    private void subscribeOrderStatusForTradingFirmV2(ChannelListener proxyListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeOrderStatusForTradingFirmGroupV2 for " + currentSession);
        }

        List<String> users = new ArrayList<String>();
        users.add(currentSession.getUserId());
        users.addAll(currentSession.getTradingFirmGroup());
        String userId = currentSession.getUserId();
        StringBuilder subscribing = new StringBuilder(userId.length()+35);
        subscribing.append("Subscribing for TRADING FIRM user ").append(userId);
        Log.information(this, subscribing.toString());
        for(String user : users)
        {
            subscribing.setLength(0);
            subscribing.append("Subscribing for TRADING user (V2)").append(user);
            Log.information(this, subscribing.toString());
            ChannelKey channelKey;
            channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_TRADING_FIRM, user);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, user);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_TRADING_FIRM, user);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, user);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TRADING_FIRM, user);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, user);

            channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT_BY_TRADING_FIRM, user);
            orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM, user);
            orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_TRADING_FIRM, user);
            orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);
        }

        subscriptionService.addTradingFirmInterest(proxyListener, users);
    }

    private void subscribeOrderEventsInternal()
     {
        ChannelKey channelKey;
        channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK, thisUserId);
        orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT, thisUserId);
        orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_BUST_REPORT, thisUserId);
        orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_BUST_REINSTATE_REPORT, thisUserId);
        orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

        channelKey = new ChannelKey(ChannelType.CANCEL_REPORT, thisUserId);
        orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_UPDATE, thisUserId);
        orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

        channelKey = new ChannelKey(ChannelType.NEW_ORDER, thisUserId);
        orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_STATUS_UPDATE, thisUserId);
        orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);
    }
    /**
      * This asks the server for all orders (for this user) and registers the listener object for
      * future data updates.  A null listener object is not valid.
      *
      * @param clientListener com.cboe.idl.cmiCallback.ClientOrderStatusConsumer
      */
    public void subscribeOrders(
            CMIOrderStatusConsumer clientListener,
            boolean gmdCallback)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling subscribeOrders for " + currentSession + " gmd:" + gmdCallback);
    	}

        if (clientListener != null)
        {
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    gmdCallback);

            checkProxy(orderStatusSupplier, proxyListener, gmdCallback, true, null);

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_ALL_ORDERS, thisUserId);
            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisUserId);
        }
        else
        {
            Log.alarm("null clientListener in subscribeOrders " + currentSession);
        }

        subscribeOrdersWithoutPublish(clientListener, gmdCallback);

        OrderStruct[] orders = getOrderQueryCache().publishUserOrders();
        dispatchUserOrders( buildOrderDetailStruct( orders, StatusUpdateReasons.QUERY) );
    }

    /**
      * @param clientListener com.cboe.idl.cmiCallback.ClientOrderStatusConsumer
      */
    public void subscribeOrdersWithoutPublish(
            CMIOrderStatusConsumer clientListener,
            boolean gmdCallback)
        throws  SystemException,
                CommunicationException,
                AuthorizationException,
                DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling subscribeOrdersWithoutPublish for " + currentSession + " gmd:" + gmdCallback);
    	}

        if (clientListener != null)
        {
            ChannelKey channelKey;
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    gmdCallback);

            checkProxy(orderStatusSupplier, proxyListener, gmdCallback, true, null);

//            channelKey = new ChannelKey(ChannelType.CB_ALL_ORDERS, thisUserId);
//            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_FILLED_REPORT, thisUserId);
            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_CANCELED_REPORT, thisUserId);
            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT, thisUserId);
            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT, thisUserId);
            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT, thisUserId);
            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_STATUS_UPDATE, thisUserId);
            orderStatusSupplier.addChannelListener(this, proxyListener, channelKey, thisUserId);
            // not need to subscribe to order events on CBOE side since is called when starts up

            // have OSSS/OSS publish any/all unAck order events
            getUserOrderService().publishUnAckedOrders();
        }
        else
        {
            Log.alarm("null clientListener in subscribeOrdersWithoutPublish " + currentSession);
        }
    }
    /**
      * This returns all cached orders for the requested product type and registers the listener object
      * future data updates.  If the caller does not want further updates, pass a null listener object.
      * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
      */
    public OrderDetailStruct[] getOrdersForType(short type)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // delegate to userOrderHandling
        return getUserOrderService().getOrdersForType(type);
    }

    public void unsubscribeAllOrderStatusForType(short type, CMIOrderStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling unsubscribeAllOrderStatusForType for " + currentSession);
    	}
    }

    /**
      * This returns the cached order for the requested order id.
      * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
      * @param orderId com.cboe.idl.cmiOrder.OrderIdStruct
      */
    public OrderDetailStruct getOrderById(OrderIdStruct orderId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        // delegate to userOrderHandling
        return getUserOrderService().getOrderById(orderId);
    }

    /**
      * This returns all cached orders for the requested order product class and registers the listener object
      * future data updates.  If the caller does not want further updates, pass a null listener object.
      * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
      * @param productClass com.cboe.idl.cmiProduct.ClassKey
      */
    public OrderDetailStruct[] getOrdersForClass(int productClass)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // delegate to userOrderHandling
        return getUserOrderService().getOrdersForClass(productClass);
    }

    /**
      * This returns all cached orders for the requested order product key (and current user)
      * and registers the listener object future data updates.  If the caller does not want
      * further updates, pass a null listener object.
      * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
      */
    public OrderDetailStruct[] getOrdersForProduct(int pKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // delegate to userOrderHandling
        return getUserOrderService().getOrdersForProduct(pKey);
    }

    /**
      * This returns all cached orders for the requested order product key (and current user)
      * and registers the listener object future data updates.  If the caller does not want
      * further updates, pass a null listener object.
      * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
      */
    public OrderDetailStruct[] getOrdersForSession(String sessionName)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // delegate to userOrderHandling
        return getUserOrderService().getOrdersForSession(sessionName);
    }

    public PendingOrderStruct[] getPendingAdjustmentOrdersByProduct(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // delegate to userOrderHandling
        return getUserOrderService().getPendingAdjustmentOrdersByProduct(sessionName, productKey);
    }

    public PendingOrderStruct[] getPendingAdjustmentOrdersByClass(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // delegate to userOrderHandling
        return getUserOrderService().getPendingAdjustmentOrdersByClass(sessionName, classKey);
    }

    /**
      * Unsubscribe the supplied member from the callback.
      * @param clientListener com.cboe.idl.cmiCallback.CMIOrderFilledReportConsumer
      */
    public void unsubscribeOrderStatusByClass( int classKey, CMIOrderStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling unsubscribeOrderStatusByClass for " + currentSession);
    	}
    }

    /**
      * Unsubscribe the supplied member from the callback.
      * @param clientListener com.cboe.idl.cmiCallback.CMIOrderFilledReportConsumer
      */
    public void unsubscribeOrderStatusForProduct( int productKey, CMIOrderStatusConsumer clientListener)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling unsubscribeOrderStatusForProduct for " + currentSession);
    	}
    }

    /**
      * Unsubscribe the supplied member from the callback.
      * @param clientListener com.cboe.idl.cmiCallback.CMIOrderFilledReportConsumer
      */
    public void unsubscribeOrderStatusForSession( String sessionName, CMIOrderStatusConsumer clientListener)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling unsubscribeOrderStatusForSession for " + currentSession);
    	}
    }
    /**
      * Unsubscribe the supplied member from the callback.
      * @param clientListener com.cboe.idl.cmiCallback.CMIOrderFilledReportConsumer
      */
    public void unsubscribeOrderStatusForFirm( CMIOrderStatusConsumer clientListener)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling unsubscribeOrderStatusForFirm for " + currentSession);
    	}
        ChannelKey channelKey = null;

        if (clientListener != null) {
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    false);

            if(currentSession.isTradingFirmEnabled())
            {
                unsubscribeOrderStatusForTradingFirm(proxyListener);
                return;
            }

            channelKey = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, thisFirmKeyContainer);
            orderStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, thisFirmKeyContainer);
            orderStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_FIRM, thisFirmKeyContainer);
            orderStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, thisFirmKeyContainer);
            orderStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, thisFirmKeyContainer);
            orderStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_BY_FIRM, thisFirmKeyContainer);
            orderStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.CANCEL_REPORT_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.ORDER_UPDATE_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.NEW_ORDER_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            removeOrderStatusConsumerProxy(
                proxyListener,
                false,  // false == for firm user (not normal user)
                null);  // null == not for a specific class
        }
        else
        {
            Log.alarm("null clientListener in unsubscribeOrderStatusForFirm " + currentSession);
        }
    }

    private void unsubscribeOrderStatusForTradingFirm(ChannelListener proxyListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling unsubscribeOrderStatusForTradingFirm for " + currentSession);
    	}

        ChannelKey channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_TRADING_FIRM, thisFirmKeyContainer);
        orderStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

        channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_TRADING_FIRM, thisFirmKeyContainer);
        orderStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

        channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TRADING_FIRM, thisFirmKeyContainer);
        orderStatusSupplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

        channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_TRADING_FIRM, thisFirmKeyContainer);
        orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

        removeOrderStatusConsumerProxy(proxyListener, false, null);
    }

    /**
      * The Order history is a pass through request that is not cached at this time.
      * This will be implemented in a future increment.
      * @param orderId com.cboe.idl.cmiOrder.OrderIdStruct
      */
    public ActivityHistoryStruct queryOrderHistory(OrderIdStruct orderId)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling queryOrderHistory for " + currentSession);
    	}
        OrderStruct order = getOrderById(orderId).orderStruct;
        return getUserOrderService().queryOrderHistory(order.activeSession, order.productKey, orderId);
    }


    /**
      * Sets the current session manager.
      * This needs to be called after the creation of this object in the home impl
      */
    public void setSessionManager(SessionManager theSession)
    {
        try
        {
            currentSession  = theSession;
            thisUserId = currentSession.getValidSessionProfileUser().userId; //used by single user logins.
            thisExchange = currentSession.getValidSessionProfileUser().userAcronym.exchange;
            thisAcronym = currentSession.getValidSessionProfileUser().userAcronym.acronym;
            ExchangeFirmStruct thisFirmKeyStruct = currentSession.getValidSessionProfileUser().defaultProfile.executingGiveupFirm;
            thisFirmKeyContainer  = new ExchangeFirmStructContainer(thisFirmKeyStruct);
            logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
            EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, theSession);
            LogoutServiceFactory.find().addLogoutListener(theSession, this);

            orderStatusCollectorProxy = ServicesHelper.getOrderStatusCollectorProxy(this, theSession);
            subscriptionService = ServicesHelper.getSubscriptionService(currentSession);
        }
        catch(org.omg.CORBA.UserException e)
        {
            Log.exception(this, "session : " + currentSession, e);
        }
    }

    /** Tell us how to get Auction events. Called by our Home.
     * @param filterByUser true if events will arrive on channel AUCTION_USER,
     *     false if events will arrive on channel AUCTION.
     */
    public void setUserAuctionFilter(boolean filterByUser)
    {
        filterAuctionByUser = filterByUser;
    }

    /**
     * Returns a reference to user order service.
     */
    private UserOrderService getUserOrderService()
    {
        if (userOrderService == null)
        {
            userOrderService = ServicesHelper.getUserOrderService(currentSession);
            //Will need to catch/throw an exception in a future increment
        }
        return userOrderService;
    }

    /**
     * This determines which method to use to actually initialize the order collection.
     * @param inTestMode true for testing, false for production
     */
    public void initialize(boolean inTestMode)
    {
        //ToDo ...Read user configuration to determine proper initialization routine for Back Office Users
        //Need to get this from the user service for this login id.

        orderStatusSupplier = OrderStatusSupplierFactory.create(currentSession);
        orderStatusSupplier.setDynamicChannels(true);

        orderStatusV2Supplier = OrderStatusV2SupplierFactory.create(currentSession);
        orderStatusV2Supplier.setDynamicChannels(true);

        auctionSupplier = AuctionSupplierFactory.create();
        auctionSupplier.setDynamicChannels(true);

        auctionListeners = Collections.synchronizedMap(new HashMap(11));
        auctionProcessor = AuctionProcessorFactory.create(this);

        try
        {
            orderStatusCollectorSupplier = getOrderQueryCache().getOrderStatusCollectorSupplier();
            orderStatusCollectorSupplier.setDynamicChannels(true);

            subscribeOrderEventsInternal();
            subscribeOrderQueryInternal(thisUserId);
        }
        catch( Exception e)
        {
            Log.exception(this, "session : " + currentSession + " : User Order Query unable to subscribe to suppliers", e );
        }
    } //initialize()

    /**
     * Subscribes to the order query event for internal use only
     */
    private void subscribeOrderQueryInternal(String userId)
    {
        // Register for with the IEC
        ChannelKey channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS, userId);
        orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);

        channelKey = new ChannelKey(ChannelType.ORDER_QUERY_EXCEPTION, userId);
        orderStatusCollectorSupplier.addChannelListener(this, orderStatusCollectorProxy, channelKey);
    }

    private String getOrderIdString(OrderIdStruct orderId)
    {
        StringBuilder toStr = new StringBuilder(50);
//Printed in this format -> CBOE:690:PPO:12:h=398:l=3264
        toStr.append(orderId.executingOrGiveUpFirm.exchange).append(':');
        toStr.append(orderId.executingOrGiveUpFirm.firmNumber).append(':');
        toStr.append(orderId.branch).append(':').append(orderId.branchSequenceNumber);
        toStr.append(":h=").append(orderId.highCboeId).append(":l=").append(orderId.lowCboeId);

        return toStr.toString();
    }

    public void acceptUserSessionLogout() {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling acceptUserSessionLogout for " + currentSession);
    	}

        OrderStatusSupplierFactory.remove(currentSession);

        internalEventChannel.removeListenerGroup(this);
        orderStatusSupplier.removeListenerGroup(this);
        orderStatusV2Supplier.removeListenerGroup(this);
        auctionSupplier.removeListenerGroup(this);

        LogoutServiceFactory.find().logoutComplete(currentSession,this);


        orderStatusCollectorSupplier.removeListenerGroup(this);
        orderStatusCollectorProxy = null;

        logoutProcessor.setParent(null);
        logoutProcessor = null;
        auctionProcessor.setParent(null);
        auctionProcessor = null;

        userOrderService = null;
        orderStatusSupplier = null;
        orderStatusV2Supplier = null;
        orderStatusCollectorSupplier = null;
        auctionSupplier = null;
        pqAdapter = null;

        auctionListeners = null;

        orderStatusCollectorProxy = null;
        currentSession = null;
        thisUserId = null;
        thisFirmKeyContainer = null;
    }


    private OrderQueryCache getOrderQueryCache()
    {
        if (null == orderQueryCache)
        {
            try
            {
                orderQueryCache = OrderQueryCacheFactory.find(thisUserId);
            }
            catch( Exception e )
            {
                Log.exception(this, "session : " + currentSession
                        + " : Fatal error retrieving OrderQueryCache!"
                        + thisUserId +":"+ thisFirmKeyContainer.getExchange()
                        + ":"+ thisFirmKeyContainer.getFirmNumber(), e );
            }
        }
        return orderQueryCache;
    }

    //--------------------------------------------------------------------------
    // com.cboe.idl.cmiV2.OrderQuery operations
    //--------------------------------------------------------------------------
    /**
     * @param clientListener
     * @param publishOnSubscribe
     * @param gmdCallback
     *
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     */
    public void subscribeOrderStatusV2(
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer clientListener,
            boolean publishOnSubscribe,
            boolean gmdCallback)
        throws  com.cboe.exceptions.SystemException,
                com.cboe.exceptions.CommunicationException,
                com.cboe.exceptions.AuthorizationException,
                com.cboe.exceptions.DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling subscribeOrderStatusV2 for " + currentSession + " gmd:" + gmdCallback);
    	}

        if (clientListener != null)
        {
            ChannelKey channelKey;
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    gmdCallback);

            checkProxy(orderStatusV2Supplier, proxyListener, gmdCallback, true, null);

            channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_V2, thisUserId);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_V2, thisUserId);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_V2, thisUserId);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_V2, thisUserId);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_V2, thisUserId);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_STATUS_UPDATE_V2, thisUserId);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

            // have OSSS/OSS publish any/all unAck order events
            getUserOrderService().publishUnAckedOrders();
            if (publishOnSubscribe)
            {
                // In keeping with the logic in the 'subscribeOrders()' and
                // 'subscribeOrdersWithoutPublish()' methods in this class, we
                // only subscribe this listener to the CB_ALL_ORDERS_V2 channel
                // if we're going to publish all the existing orders to it.  If
                // not, we'll never subscribe the listener to that channel.
                channelKey = new ChannelKey(ChannelType.CB_ALL_ORDERS_V2, thisUserId);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisUserId);

                OrderStruct[] orders = getOrderQueryCache().publishUserOrders();
                OrderDetailStruct[] orderDetails = buildOrderDetailStruct(orders, StatusUpdateReasons.QUERY);
                dispatchOrdersV2(ChannelType.CB_ALL_ORDERS_V2, thisUserId, orderDetails);
            }
        }
        else
        {
            Log.alarm("null clientListener in subscribeOrderStatusV2 " + currentSession);
        }
    }

    /**
     *
     * @param clientListener
     *
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     */
    public void unsubscribeOrderStatusV2(
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer clientListener)
        throws  com.cboe.exceptions.SystemException,
                com.cboe.exceptions.CommunicationException,
                com.cboe.exceptions.AuthorizationException,
                com.cboe.exceptions.DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling unsubscribeOrderStatusV2 for " + currentSession);
    	}

        if (clientListener != null)
        {
            ChannelKey channelKey;
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    false);

            channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_V2, thisUserId);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_V2, thisUserId);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_V2, thisUserId);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_V2, thisUserId);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_V2, thisUserId);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ALL_ORDERS_V2, thisUserId);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisUserId);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_STATUS_UPDATE_V2, thisUserId);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisUserId);

            removeOrderStatusConsumerProxy(
                proxyListener,
                true,   // true == for normal user (not firm user)
                null);  // null == not for a specific class
        }
        else
        {
            Log.alarm("null clientListener in unsubscribeOrderStatusV2 " + currentSession);
        }
    }

    /**
     *
     * @param classKey
     * @param clientListener
     * @param publishOnSubscribe
     * @param gmdCallback
     *
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     */
    public void subscribeOrderStatusForClassV2(
            int classKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer clientListener,
            boolean publishOnSubscribe,
            boolean gmdCallback)
        throws  com.cboe.exceptions.SystemException,
                com.cboe.exceptions.CommunicationException,
                com.cboe.exceptions.AuthorizationException,
                com.cboe.exceptions.DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling subscribeOrderStatusForClassV2 for " + currentSession + " gmd:" + gmdCallback);
    	}

        ServicesHelper.getProductQueryServiceAdapter().checkProductCacheLoaded(classKey);
        if (clientListener != null)
        {
            ChannelKey channelKey;
            Integer key = Integer.valueOf(classKey);
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    gmdCallback);

            checkProxy(orderStatusV2Supplier, proxyListener, gmdCallback, true, key);

            //Only need to add this once -- so we'll hang on to the IEC proxy this time and do the user data addition
            channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_CLASS_V2, key);
            ChannelListenerProxy iecProxy = orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);
            try
            {
                ((InstrumentedChannelListenerProxy)iecProxy).addUserData(UserDataTypes.CLASS, key.toString());
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "Unable to cast for user data addition.", e);
            }

            channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_CLASS_V2, key);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_CLASS_V2, key);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_CLASS_V2, key);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_CLASS_V2, key);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_STATUS_UPDATE_FOR_CLASS_V2, key);
            orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

            // have OSSS/OSS publish any/all unAck order events
            getUserOrderService().publishUnAckedOrdersForClass(classKey);

            if (publishOnSubscribe)
            {
                // In keeping with the logic in the 'subscribeOrders()' and
                // 'subscribeOrdersWithoutPublish()' methods in this class, we
                // only subscribe this listener to the CB_ALL_ORDERS_V2 channel
                // if we're going to publish all the existing orders to it.  If
                // not, we'll never subscribe the listener to that channel.
                channelKey = new ChannelKey(ChannelType.CB_ORDERS_FOR_CLASS_V2, key);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

                // The getOrdersByClass() method on the order cache will return
                // every order for this class, meaning this user's orders plus
                // other user's orders (if this user is a firm user).  In this
                // situation, we only want this user's orders, so we need to
                // filter out all other users' orders.
                OrderStruct[] allUsersOrders = getOrderQueryCache().getOrdersByClass(classKey);
                OrderStruct[] thisUsersOrders = filterOrders(allUsersOrders);
                OrderDetailStruct[] orderDetails = buildOrderDetailStruct(thisUsersOrders, StatusUpdateReasons.QUERY);

                dispatchOrdersV2(ChannelType.CB_ORDERS_FOR_CLASS_V2, key, orderDetails);
            }
        }
        else
        {
            Log.alarm("null clientListener in subscribeOrderStatusForClassV2 " + currentSession);
        }
    }

    /**
     *
     * @param classKey
     * @param clientListener
     *
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     */
    public void unsubscribeOrderStatusForClassV2(
            int classKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer clientListener)
        throws  com.cboe.exceptions.SystemException,
                com.cboe.exceptions.CommunicationException,
                com.cboe.exceptions.AuthorizationException,
                com.cboe.exceptions.DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling unsubscribeOrderStatusForClass for " + currentSession);
    	}

        if (clientListener != null)
        {
            ChannelKey channelKey;
            Integer key = Integer.valueOf(classKey);
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    false);

            channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDERS_FOR_CLASS_V2, key);
            ChannelListenerProxy iecProxy =
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);
            try
            {
                ((InstrumentedChannelListenerProxy) iecProxy).removeUserData(UserDataTypes.CLASS, key.toString());
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "Cannot remove user data info.", e);
            }

            channelKey = new ChannelKey(ChannelType.CB_ORDER_STATUS_UPDATE_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            removeOrderStatusConsumerProxy(
                proxyListener,
                true,   // true == for normal user (not firm user)
                key);
        }
        else
        {
            Log.alarm("null clientListener in unsubscribeOrderStatusForClassV2 " + currentSession);
        }
    }

    /**
     *
     * @param classKey
     * @param clientListener
     * @param publishOnSubscribe
     * @param gmdCallback
     *
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     */
    public void subscribeOrderStatusForFirmForClassV2(
            int classKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer clientListener,
            boolean publishOnSubscribe,
            boolean gmdCallback)
        throws  com.cboe.exceptions.SystemException,
                com.cboe.exceptions.CommunicationException,
                com.cboe.exceptions.AuthorizationException,
                com.cboe.exceptions.DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling subscribeOrderStatusForFirmForClassV2 for " + currentSession + " gmd:" + gmdCallback);
    	}

        ServicesHelper.getProductQueryServiceAdapter().checkProductCacheLoaded(classKey);
        if (clientListener != null)
        {
            if (currentSession.getValidUser().role == UserRoles.FIRM_DISPLAY) {
                ChannelKey channelKey;
                FirmClassContainer key = new FirmClassContainer(thisFirmKeyContainer.getExchange(), thisFirmKeyContainer.getFirmNumber(), classKey);
                ChannelListener proxyListener =
                        ServicesHelper.getOrderStatusConsumerProxy(clientListener,
                                currentSession,
                                gmdCallback);

                checkProxy(orderStatusV2Supplier, proxyListener, gmdCallback, false, Integer.valueOf(classKey));

                //Only need to add this once -- so we'll hang on to the IEC proxy this time and do the user data addition
                channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
                ChannelListenerProxy iecProxy = orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);
                try {
                    ((InstrumentedChannelListenerProxy) iecProxy).addUserData(UserDataTypes.CLASS, key.toString());
                } catch (ClassCastException e) {
                    Log.exception(this, "Unable to cast for user data addition.", e);
                }

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

                subscriptionService.addFirmInterest(proxyListener);

                if (publishOnSubscribe) {
                    // In keeping with the logic in the 'subscribeOrders()' and
                    // 'subscribeOrdersWithoutPublish()' methods in this class, we
                    // only subscribe this listener to the
                    // CB_ORDERS_FOR_FIRM_FOR_CLASS_V2 channel if we're going to
                    // publish all the existing orders to it.  If not, we'll never
                    // subscribe the listener to that channel.
                    channelKey = new ChannelKey(ChannelType.CB_ORDERS_FOR_FIRM_FOR_CLASS_V2, key);
                    orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

                    // The getOrdersByClass() method on the order cache will return
                    // every order for this class, meaning this user's orders plus
                    // other users' orders (if this user is a firm user).  In this
                    // situation, this is exactly what we want.
                    OrderStruct[] orders = getOrderQueryCache().getOrdersByClass(classKey);
                    OrderDetailStruct[] orderDetails = buildOrderDetailStruct(orders, StatusUpdateReasons.QUERY);

                    dispatchOrdersV2(ChannelType.CB_ORDERS_FOR_FIRM_FOR_CLASS_V2, key, orderDetails);
                }
            }
            else{
                ChannelKey channelKey;
                FirmClassContainer key = new FirmClassContainer(thisFirmKeyContainer.getExchange(), thisFirmKeyContainer.getFirmNumber(), classKey);
                ChannelListener proxyListener =
                        ServicesHelper.getOrderStatusConsumerProxy(clientListener,
                                currentSession,
                                gmdCallback);

                checkProxy(orderStatusV2Supplier, proxyListener, gmdCallback, false, Integer.valueOf(classKey));

                //Only need to add this once -- so we'll hang on to the IEC proxy this time and do the user data addition
                channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
                ChannelListenerProxy iecProxy = orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);
                try {
                    ((InstrumentedChannelListenerProxy) iecProxy).addUserData(UserDataTypes.CLASS, key.toString());
                } catch (ClassCastException e) {
                    Log.exception(this, "Unable to cast for user data addition.", e);
                }

                channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

                channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

                subscriptionService.addFirmInterest(proxyListener);

                if (publishOnSubscribe) {
                    // In keeping with the logic in the 'subscribeOrders()' and
                    // 'subscribeOrdersWithoutPublish()' methods in this class, we
                    // only subscribe this listener to the
                    // CB_ORDERS_FOR_FIRM_FOR_CLASS_V2 channel if we're going to
                    // publish all the existing orders to it.  If not, we'll never
                    // subscribe the listener to that channel.
                    channelKey = new ChannelKey(ChannelType.CB_ORDERS_FOR_FIRM_FOR_CLASS_V2, key);
                    orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, key);

                    // The getOrdersByClass() method on the order cache will return
                    // every order for this class, meaning this user's orders plus
                    // other users' orders (if this user is a firm user).  In this
                    // situation, this is exactly what we want.
                    OrderStruct[] orders = getOrderQueryCache().getOrdersByClass(classKey);
                    OrderDetailStruct[] orderDetails = buildOrderDetailStruct(orders, StatusUpdateReasons.QUERY);

                    dispatchOrdersV2(ChannelType.CB_ORDERS_FOR_FIRM_FOR_CLASS_V2, key, orderDetails);
                }
            }
        }
        else
        {
            Log.alarm("null clientListener in subscribeOrderStatusForFirmForClassV2 " + currentSession);
        }
    }

    public void subscribeOrderStatusForFirmV2( com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer clientListener, boolean publishOnSubscribe, boolean gmdCallback)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling subscribeOrderStatusForFirmV2 for " + currentSession + " gmd:" + gmdCallback);
    	}

        if (clientListener != null)
        {
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    gmdCallback);

            if(!currentSession.isTradingFirmEnabled())
            {
                checkProxy(orderStatusV2Supplier, proxyListener, gmdCallback, false, null);

                ChannelKey channelKey = new ChannelKey(ChannelType.CB_ORDERS_FOR_FIRM_V2, thisFirmKeyContainer);
                orderStatusV2Supplier.addChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);
            }
        }
        else
        {
            Log.alarm("null clientListener in subscribeOrderStatusForFirmV2 " + currentSession);
        }

        // On this call to subscribeOrdersByFirmWithoutPublishV2(), we must pass
        // a value of 'false' for the 'gmdCallback' parameter.  This is because
        // we've already gone through the process of ensuring a single GMD
        // callback above (in this method).
	// Ammendment: if this is for trading firm user, pass original gmdCallback value
        if(currentSession.isTradingFirmEnabled())
	{
            subscribeOrdersByFirmWithoutPublishV2(clientListener, gmdCallback);
	}
	else
	{
	    subscribeOrdersByFirmWithoutPublishV2(clientListener, false);
	}

        if (publishOnSubscribe)
        {
            OrderStruct[] orders = getOrderQueryCache().publishAllOrders();
            dispatchFirmOrders( buildOrderDetailStruct( orders, StatusUpdateReasons.QUERY ) );
        }
    }

    public void unsubscribeOrderStatusForFirmV2( com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling unsubscribeOrderStatusForFirmV2 for " + currentSession);
    	}

        if (clientListener != null) {
            ChannelKey channelKey;
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    false);

            if(currentSession.isTradingFirmEnabled())
            {
                unsubscribeOrderStatusForTradingFirm(proxyListener);
                return;
            }

            channelKey = new ChannelKey(ChannelType.CB_ORDERS_FOR_FIRM_V2, thisFirmKeyContainer);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);

            channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.CANCEL_REPORT_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.ORDER_UPDATE_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.NEW_ORDER_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS_BY_FIRM, thisFirmKeyContainer);
            orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);

            removeOrderStatusConsumerProxy(
                proxyListener,
                false,  // false == for firm user (not normal user)
                null);  // null  == not for a specific class
        }
        else
        {
            Log.alarm("null clientListener in unsubscribeOrderStatusForFirmV2 " + currentSession);
        }
    }


    /*public void unsubscribeOrderStatusForFirmV2( com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer clientListener)
	    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
	{
	    Log.debug(this, "calling unsubscribeOrderStatusForFirmV2 for " + currentSession);
	
	    if (clientListener != null) {
	        ChannelKey channelKey;
	        ChannelListener proxyListener =
	            ServicesHelper.getOrderStatusConsumerProxy(
	                clientListener,
	                currentSession,
	                false);
	
	        if(currentSession.isTradingFirmEnabled())
	        {
	            unsubscribeOrderStatusForTradingFirm(proxyListener);
	            return;
	        }
	
	        channelKey = new ChannelKey(ChannelType.CB_ORDERS_FOR_FIRM_V2, thisFirmKeyContainer);
	        orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);
	
	        channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
	        orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);
	
	        channelKey = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
	        orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);
	
	        channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
	        orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);
	
	        channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
	        orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);
	
	        channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_FIRM_V2, thisFirmKeyContainer);
	        orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, thisFirmKeyContainer);
	
	        channelKey = new ChannelKey(ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM, thisFirmKeyContainer);
	        orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);
	
	        channelKey = new ChannelKey(ChannelType.ORDER_FILL_REPORT_BY_FIRM, thisFirmKeyContainer);
	        orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);
	
	        channelKey = new ChannelKey(ChannelType.CANCEL_REPORT_BY_FIRM, thisFirmKeyContainer);
	        orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);
	
	        channelKey = new ChannelKey(ChannelType.ORDER_UPDATE_BY_FIRM, thisFirmKeyContainer);
	        orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);
	
	        channelKey = new ChannelKey(ChannelType.NEW_ORDER_BY_FIRM, thisFirmKeyContainer);
	        orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);
	
	        channelKey = new ChannelKey(ChannelType.ACCEPT_ORDERS_BY_FIRM, thisFirmKeyContainer);
	        orderStatusCollectorSupplier.removeChannelListener(this, orderStatusCollectorProxy, channelKey);
	
	        removeOrderStatusConsumerProxy(
	            proxyListener,
	            false,  // false == for firm user (not normal user)
	            null);  // null  == not for a specific class
	    }
	    else
	    {
	        Log.alarm("null clientListener in unsubscribeOrderStatusForFirmV2 " + currentSession);
	    }
	}*/

    /**
     *
     * @param classKey
     * @param clientListener
     *
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     */
    public void unsubscribeOrderStatusForFirmForClassV2( int classKey, com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer clientListener)
        throws  com.cboe.exceptions.SystemException,
                com.cboe.exceptions.CommunicationException,
                com.cboe.exceptions.AuthorizationException,
                com.cboe.exceptions.DataValidationException
    {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "calling unsubscribeOrderStatusForFirmForClass for " + currentSession);
    	}

        if (clientListener != null)
        {
            ChannelKey channelKey;
            FirmClassContainer key = new FirmClassContainer(thisFirmKeyContainer.getExchange(), thisFirmKeyContainer.getFirmNumber(), classKey);
            ChannelListener proxyListener =
                ServicesHelper.getOrderStatusConsumerProxy(
                    clientListener,
                    currentSession,
                    false);

            channelKey = new ChannelKey(ChannelType.CB_NEW_ORDER_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_FILLED_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_CANCELED_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_FIRM_FOR_CLASS_V2, key);
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_ORDERS_FOR_FIRM_FOR_CLASS_V2, key);
            ChannelListenerProxy iecProxy =
            orderStatusV2Supplier.removeChannelListener(this, proxyListener, channelKey, key);
            try
            {
                ((InstrumentedChannelListenerProxy)iecProxy).removeUserData(UserDataTypes.CLASS, key.toString());
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "Unable to remove user data.", e);
            }

            removeOrderStatusConsumerProxy(
                proxyListener,
                false,  // false == for firm user (not normal user)
                Integer.valueOf(classKey));
        }
        else
        {
            Log.alarm("null clientListener in unsubscribeOrderStatusForFirmForClassV2 " + currentSession);
        }
    }

    /**
     * Filters out an orders in the input array that are not this user's orders.
     *
     * @param in The array of orders that is to be filtered.
     *
     * @return A filtered array that only contains this user's orders.
     */
    private OrderStruct[] filterOrders(OrderStruct[] in)
    {
        // Build a list that contains the indexes of every order in the 'in'
        // array that belongs to this user ID.  Once we've gone through the
        // entire input array, we can simply access this user's order elements
        // directly and copy them to a new array.
        List list = new ArrayList(in.length);

        for (int i = 0; i < in.length; ++i) {
            // Find out how many of the incoming orders belong to this user.
            if (in[i].userId.equals(thisUserId))
            {
                list.add(Integer.valueOf(i));
            }
        }

        int numMatches = list.size();
        OrderStruct[] userOrders;

        if (numMatches == in.length)
        {
            // Every order in the incoming array belongs to this user...
            userOrders = in;
        }
        else
        {
            // Only a subset of the incoming orders belong to this user.  We
            // need to extract those specific orders into a new array.
            userOrders = new OrderStruct[numMatches];

            Iterator iter = list.iterator();
            int i = 0;

            while (iter.hasNext())
            {
                Integer index = (Integer) iter.next();
                userOrders[i++] = in[index.intValue()];
            }
        }

        return userOrders;
    }

    /**
     * Cleans up the given consumer from the home's maps of registered
     * consumers.
     */
    private void removeOrderStatusConsumerProxy(
            ChannelListener proxy,
            boolean forUser,
            Integer classKey)
    {
        try
        {
            OrderStatusConsumerProxyHome home =
                (OrderStatusConsumerProxyHome) HomeFactory.getInstance().findHome(
                    OrderStatusConsumerProxyHome.HOME_NAME);

            home.removeGMDProxy(proxy, forUser, classKey);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    /**
     *
     */
    private void initGmdStuff(
            ChannelListener proxy,
            boolean forUser,
            Integer classKey)
        throws DataValidationException
    {
        try
        {
            OrderStatusConsumerProxyHome home =
                (OrderStatusConsumerProxyHome) HomeFactory.getInstance().findHome(
                    OrderStatusConsumerProxyHome.HOME_NAME);

            home.addGMDProxy(proxy, forUser, classKey);
        }
        catch (DataValidationException dve)
        {
            throw dve;
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    /**
     *
     */
    private ChannelListener checkProxy(
            UserSessionBaseSupplier supplier,
            ChannelListener newProxy,
            boolean gmd,
            boolean forUser,
            Integer classKey)
        throws DataValidationException
    {
        BaseChannelListenerProxy baseProxy =
            (BaseChannelListenerProxy) supplier.getProxyForDelegate(newProxy);

        ChannelListener existingProxy = null;
        ChannelListener proxy = null;

        if (baseProxy != null)
        {
            existingProxy = baseProxy.getDelegateListener();
        }

        if (existingProxy != null)
        {
            GMDSupplierProxy gmdProxy = (GMDSupplierProxy) existingProxy;

            if (gmdProxy.getGMDStatus() != gmd)
            {
                throw ExceptionBuilder.dataValidationException(
                    "GMD flag does not match existing GMD flag",
                    DataValidationCodes.GMD_LISTENER_ALREADY_REGISTERED);
            }

            proxy = existingProxy;
        }
        else
        {
            proxy = newProxy;
        }

        try
        {
            OrderStatusConsumerProxyHome home =
                (OrderStatusConsumerProxyHome) HomeFactory.getInstance().findHome(
                    OrderStatusConsumerProxyHome.HOME_NAME);

            home.addGMDProxy(proxy, forUser, classKey);
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            Log.exception(e);
        }

        return proxy;
    }

    protected ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(pqAdapter == null)
        {
            pqAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqAdapter;
    }

    /** Client indicates interest in receiving notifications for auctions.
     * @param sessionName Which trading session.
     * @param classKey Class of products for auction subscription.
     * @param clientListener Client to notify.
     */
    public AuctionSubscriptionResultStruct[] subscribeAuctionForClass(String sessionName, int classKey, short[] auctionTypes, CMIAuctionConsumer clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeAuctionForClass for " + currentSession
                    + " sessionName:" + sessionName + " classKey:" + classKey);
        }

    	
        ServicesHelper.getUserEnablementService(thisUserId, thisExchange, thisAcronym).verifyUserEnablement(sessionName, classKey, OperationTypes.AUCTION);
        ServicesHelper.getProductQueryServiceAdapter().checkProductCacheLoaded(classKey);

        if(auctionTypes == null || auctionTypes.length == 0)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Auction Type parameter.", DataValidationCodes.INVALID_FIELD_LENGTH);
        }

        // The proxy listener is what is subscribed on the callback channels; it is a proxy to the user's
        // callback object.  If the user's callback is null we will log it (for now) and we will not have
        // a proxy listener object.  If the proxyListener is null, we won't bother adding it to the callback
        // channels (see inside for loop).  We are using one proxy listener for all auction types submitted in
        // this subscription attempt.
        ChannelListener proxyListener = null;
        if(clientListener == null)
        {
            Log.alarm(this, "Client listener for auction subscription is null.");
        }
        else
        {
            CMIAuctionConsumer timeoutListener = null;
            timeoutListener = com.cboe.idl.cmiCallbackV3.CMIAuctionConsumerHelper.narrow(
                    (org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(clientListener, auctionCallbackTimeout));
            proxyListener = ServicesHelper.getAuctionConsumerProxy(timeoutListener, currentSession);
        }

        // A couple of useful objects for later.  The AuctionSubscriptionResultStruct array will be populated with
        // the results of the validation of each auction type.  The SessionKeyContainer is used in subscription logic
        // and the StringBuilder is for our Log.information call later (to indicate what auction types were
        // submitted.
        SessionProfileUserStructV2 thisUserStructV2 = currentSession.getValidSessionProfileUserV2();
        AuctionSubscriptionResultStruct[] result = new AuctionSubscriptionResultStruct[auctionTypes.length];

        StringBuilder auctionTypeStringBuilder = new StringBuilder();
        for(int i = 0; i < auctionTypes.length; i++)
        {
            // All validation of auction types has been removed (critical fix).
            // There really is no reason now for the AuctionSubscriptionResultStruct.
            // Originally Tom thought we should return some kind of "unknown" indication
            // for "unknown" auction types but subscribe the client listener for the unknown type;
            // later we removed all checking because the server may have a new type before the CAS/FIX engine
            // (i.e. a rollout situation). -- Eric 7/19/2005
            result[i] = new AuctionSubscriptionResultStruct();
            result[i].auctionType = auctionTypes[i];
            result[i].subscriptionResult = new OperationResultStruct();
            result[i].subscriptionResult.errorCode = 0;
            result[i].subscriptionResult.errorMessage = "";
            auctionTypeStringBuilder.append(auctionTypes[i]);
            auctionTypeStringBuilder.append(", ");
	            /*This is temporary code to register for directed AIM. This code need to remove when merge with Market Maker Handheld which introduce CMI V6 idl.
	              CMI V6 idl will introduce new method calll registerFordirectedAIM.
	            if (auctionTypes[i] == DAIM_AUCTION)
	            {
	            	if (Log.isDebugOn())
	                {
	            		Log.debug(this,"registerForDirectedAIM for sessionName:"+sessionName+" classKey:"+classKey+" userId:"+thisUserId); 
	                }
	            	try
	        		{		
	        			ServicesHelper.getFirmService().registerForDirectedAIM(sessionName, classKey, thisUserId);	        			
	        		}
	            	
	            	catch(NotAcceptedException nae)
	        		{
	            		
	        			switch (nae.details.error)
	        			//We will introduce new API in MMHH.
	        			{		
	        				case NotAcceptedCodes.USER_NOT_AFFILIATED_TO_ANY_FIRM:
	        				
	        					Log.exception(this, "NotAcceptedException: register to Directed AIM fail for "+ currentSession +"USER_NOT_AFFILIATED_TO_ANY_FIRM", nae);	
	        				    throw ExceptionBuilder.dataValidationException(
	        		                    "registerForDirectedAIM Failed",
	        		                    NotAcceptedCodes.USER_NOT_AFFILIATED_TO_ANY_FIRM);
	        				    
	        				  
	                 	    case NotAcceptedCodes.INVALID_AFFILIATED_FIRM:
	        					Log.exception(this, "NotAcceptedException: register to Directed AIM fail for "+ currentSession +"INVALID_AFFILIATED_FIRM", nae);
	        					throw ExceptionBuilder.dataValidationException(
	        		                    "registerForDirectedAIM Failed",
	        		                    NotAcceptedCodes.INVALID_AFFILIATED_FIRM);
	        					
	        				case NotAcceptedCodes.ALREADY_UPDATED_AS_REGISTERED:
	        					Log.exception(this, "NotAcceptedException: register to Directed AIM fail for "+ currentSession +"ALREADY_UPDATED_AS_REGISTERED", nae);		        
	        					//continue with subscription.
	        					break;
	        				
	        				case NotAcceptedCodes.ALREADY_UPDATED_AS_UNREGISTERED: 
	        					Log.exception(this, "NotAcceptedException: register to Directed AIM fail for "+ currentSession +"ALREADY_UPDATED_AS_UNREGISTERED", nae);
	        					//continue with subscription.
	        					break;
	        				
	        				default:
	        					Log.exception(this, "NotAcceptedException: register to Directed AIM fail for "+ currentSession, nae);
	        					throw ExceptionBuilder.dataValidationException(
		        		              "registerForDirectedAIM Failed",NotAcceptedCodes.NOT_REGISTERED_FOR_DIRECTED_AIM);
	        					
		        		              
	        			}
	        			
	        		}catch(TransactionFailedException tfe)
	        		{
        				Log.exception(this, "TransactionFailedException register to Directed AIM fail for "+ currentSession, tfe);
        				throw ExceptionBuilder.dataValidationException(
	        		          "registerForDirectedAIM Failed",NotAcceptedCodes.NOT_REGISTERED_FOR_DIRECTED_AIM);
	        		              
	        		}
        			
	            }*/
            if(proxyListener != null)  // proxy listener is not null only if the callback object is not null
            {
                // First, for each auction type, set up CB_AUCTION channel (from CAS to client).
                // Then set up AUCTION or AUCTION_USER channel (from server to CAS) so that if
                // we get a message from the server immediately, the entire message
                // path will already be set up.

                // AuctionSupplier is a singleton in the CAS, so we must include the
                // user session as part of the channel key. If it inherited from
                // UserSessionBaseSupplier there would be one object per user
                // session and we could use just auction type, (trading) sessionName
                // and classKey in the ChannelKey.
                TypeSessionClassContainer typeSessionClass = new TypeSessionClassContainer(auctionTypes[i], sessionName, classKey);
                UserSessionKey userSessionKey = new UserSessionKey(currentSession, typeSessionClass);
	                ChannelKey cbChannelKey;
	                if (auctionTypes[i]==DAIM_AUCTION)
	                {
	                	cbChannelKey = new ChannelKey(ChannelType.CB_DAIM, userSessionKey);
	                }else
	                {
	                	cbChannelKey = new ChannelKey(ChannelType.CB_AUCTION, userSessionKey);
	                }	
	                
                ChannelListenerProxy channelListenerProxy = auctionSupplier.addChannelListener(this, proxyListener, cbChannelKey, typeSessionClass);

                try
                {
                    String typeSessionClassKeyString = TypeSessionClassUserDataHelper.encode(typeSessionClass);
                    InstrumentedChannelListenerProxy instrumentedCLProxy = (InstrumentedChannelListenerProxy) channelListenerProxy;
                    instrumentedCLProxy.addUserData(UserDataTypes.TYPE_SESSION_CLASS, typeSessionClassKeyString);
                }
                catch (ClassCastException e)
                {
                    Log.exception(this, "ClassCastException during addition of user data.", e);
                }

                // CAS will be configured to filter by user or filter by class ("filter" means "Infra Filter").
                // The IEC code will always publish using both channel keys, but we only add processor as
                // listener to the channel we are configured for.
	                if (filterAuctionByUser || auctionTypes[i]==DAIM_AUCTION)
                {
	                	
	                    TypeUserSessionClassContainer typeUserSessionClass = new TypeUserSessionClassContainer(auctionTypes[i], thisUserStructV2.userKey, sessionName, classKey);
	                    ChannelKey auChannelKey;
	                    if (auctionTypes[i]==DAIM_AUCTION)
		                {
	      
		                	auChannelKey = new ChannelKey(ChannelType.DAIM_USER, typeUserSessionClass);
		                }else
		                {
		                	auChannelKey = new ChannelKey(ChannelType.AUCTION_USER, typeUserSessionClass);
		                }
                    internalEventChannel.addChannelListener(this, auctionProcessor, auChannelKey);
                    Object source = new Pair(typeSessionClass, proxyListener);
                    subscriptionService.addSessionClassInterest(source, sessionName, classKey);
                    subscriptionService.addAuctionUserInterest(source);
                }
                else
                {
                    ChannelKey aChannelKey = new ChannelKey(ChannelType.AUCTION, typeSessionClass);
                    internalEventChannel.addChannelListener(this, auctionProcessor, aChannelKey);
                    subscriptionService.addAuctionClassInterest(proxyListener, sessionName, classKey);
                }
            }
        }

        auctionListeners.put(proxyListener, proxyListener);

        String auctionTypeString = auctionTypeStringBuilder.toString();
        String smgr = currentSession.toString();
        String listenerName = proxyListener.toString();
        StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+sessionName.length()+auctionTypeString.length()+60);
        suboid.append("Sub:oid for ").append(smgr).append(' ').append(listenerName)
              .append(" sessionName:").append(sessionName)
              .append(" classKey:").append(classKey)
              .append(" auctionTypes=").append(auctionTypeString);
        Log.information(this, suboid.toString());

        return result;
    	
    }


    /** Client rescinds interest in receiving notifications for auctions.
     * @param sessionName Which trading session.
     * @param classKey Class of products to drop subscription for.
     * @param clientListener Client to stop notifying.
     */
    public AuctionSubscriptionResultStruct[] unsubscribeAuctionForClass(String sessionName, int classKey, short[] auctionTypes, CMIAuctionConsumer clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeAuctionForClass for " + currentSession
                    + " sessionName:" + sessionName + " classKey:" + classKey);
        }

        ServicesHelper.getUserEnablementService(thisUserId, thisExchange, thisAcronym).verifyUserEnablement(sessionName, classKey, OperationTypes.AUCTION);

        if(auctionTypes == null || auctionTypes.length == 0)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Auction Type parameter.", DataValidationCodes.INVALID_FIELD_LENGTH);
        }

        ChannelListener proxyListener = null;
        if(clientListener == null)
        {
            Log.alarm(this, "Client listener for auction unsubscription is null.");
        }
        else
        {
            proxyListener = ServicesHelper.getAuctionConsumerProxy(clientListener, currentSession);
        }

        AuctionSubscriptionResultStruct[] result = new AuctionSubscriptionResultStruct[auctionTypes.length];
        SessionProfileUserStructV2 thisUserStructV2 = currentSession.getValidSessionProfileUserV2();
        StringBuilder auctionTypeStringBuilder = new StringBuilder();
        for(int i = 0; i < auctionTypes.length; i++)
        {
            // All validation of auction types has been removed (critical fix).
            // There really is no reason now for the AuctionSubscriptionResultStruct.
            // Originally Tom thought we should return some kind of "unknown" indication
            // for "unknown" auction types but subscribe the client listener for the unknown type;
            // later we removed all checking because the server may have a new type before the CAS/FIX engine
            // (i.e. a rollout situation). -- Eric 7/19/2005
            result[i] = new AuctionSubscriptionResultStruct();
            result[i].auctionType = auctionTypes[i];
            result[i].subscriptionResult = new OperationResultStruct();
            result[i].subscriptionResult.errorCode = 0;
            result[i].subscriptionResult.errorMessage = "";
            auctionTypeStringBuilder.append(auctionTypes[i]);
            auctionTypeStringBuilder.append(", ");
            TypeSessionClassContainer typeSessionClass = new TypeSessionClassContainer(auctionTypes[i], sessionName, classKey);

            if (proxyListener != null)
            {
                if (filterAuctionByUser || auctionTypes[i]==DAIM_AUCTION)
                {
                    Object source = new Pair(typeSessionClass, proxyListener);
                    subscriptionService.removeSessionClassInterest(source, sessionName, classKey);
                    subscriptionService.removeAuctionUserInterest(source);
                    TypeUserSessionClassContainer typeUserSessionClass = new TypeUserSessionClassContainer(auctionTypes[i], thisUserStructV2.userKey, sessionName, classKey);
                    ChannelKey auChannelKey;
                    if (auctionTypes[i]==DAIM_AUCTION)
	                {
	                	auChannelKey = new ChannelKey(ChannelType.DAIM_USER, typeUserSessionClass);
	                }else
	                {
	                	auChannelKey = new ChannelKey(ChannelType.AUCTION_USER, typeUserSessionClass);
	                }
                    internalEventChannel.removeChannelListener(this, auctionProcessor, auChannelKey);
                }
                else
                {
                    subscriptionService.removeAuctionClassInterest(proxyListener, sessionName, classKey);
                    ChannelKey aChannelKey = new ChannelKey(ChannelType.AUCTION, typeSessionClass);
                    internalEventChannel.removeChannelListener(this, auctionProcessor, aChannelKey);
                }

                UserSessionKey userSessionKey = new UserSessionKey(currentSession, typeSessionClass);
                ChannelKey cbChannelKey;
                if (auctionTypes[i]==DAIM_AUCTION)
                {
                	cbChannelKey = new ChannelKey(ChannelType.CB_DAIM, typeSessionClass);
                }else
                {
                	cbChannelKey = new ChannelKey(ChannelType.CB_AUCTION, typeSessionClass);
                }
                
                ChannelListenerProxy removedCLProxy = auctionSupplier.removeChannelListener(this, proxyListener, cbChannelKey, typeSessionClass);

                try
                {
                    String typeSessionClassKeyString = TypeSessionClassUserDataHelper.encode(typeSessionClass);
                    InstrumentedChannelListenerProxy instrumentedCLProxyListener = (InstrumentedChannelListenerProxy) removedCLProxy;
                    instrumentedCLProxyListener.removeUserData(UserDataTypes.TYPE_SESSION_CLASS, typeSessionClassKeyString);
                }
                catch (ClassCastException e)
                {
                    Log.exception(this, "ClassCastException when removing typeSessionClass="
                            + typeSessionClass + " from user data", e);
                }
            }
        }

        // We don't remove the proxyListener from auctionListeners. A proxyListener could be used
        // multiple times so if we kept a count we could remove it when its count went to zero.
        // Instead, we keep the unused proxyListeners in the auctionListeners map until the user
        // logs out, then we get rid of auctionListeners altogether.

        String auctionTypeString = auctionTypeStringBuilder.toString();
        String smgr = currentSession.toString();
        String listenerName = proxyListener.toString();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+sessionName.length()+auctionTypeString.length()+62);
        unsuboid.append("UnSub:oid for ").append(smgr).append(' ').append(listenerName)
                .append(" sessionName:").append(sessionName)
                .append(" classKey:").append(classKey)
                .append(" auctionTypes=").append(auctionTypeString);
        Log.information(this, unsuboid.toString());

        return result;
    }

    
    /** Put the Auction announcement onto the per-user callback IEC.
     * @param auctionStruct Details of the auction.
     */
    public void acceptAuction(AuctionStruct auctionStruct)
    {
    	if (Log.isDebugOn())
        {
    		Log.debug(this, "acceptAuction id:h="
                + auctionStruct.auctionId.highCboeId
                + ",l=" + auctionStruct.auctionId.lowCboeId
                + " productKey:" + auctionStruct.productKey
                + " classKey:" + auctionStruct.classKey
                + " auctionType:" + auctionStruct.auctionType);
        }
        TypeSessionClassContainer typeSessionClass = new TypeSessionClassContainer(auctionStruct.auctionType,
            auctionStruct.sessionName, auctionStruct.classKey);

        UserSessionKey userSessionKey = new UserSessionKey(currentSession, typeSessionClass);
        ChannelKey channelKey = new ChannelKey(ChannelType.CB_AUCTION, userSessionKey);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, auctionStruct);
        auctionSupplier.dispatch(event);
    }
    /** Put the Directed AIM Auction announcement onto the per-user callback IEC.
     * @param auctionStruct Details of the auction.
     */
    public void acceptDirectedAIMAuction(AuctionStruct auctionStruct)
    {
    	
    	if (Log.isDebugOn())
        {
    		Log.debug(this, "acceptDirectedAIMAuction id:h="
                + auctionStruct.auctionId.highCboeId
                + ",l=" + auctionStruct.auctionId.lowCboeId
                + " productKey:" + auctionStruct.productKey
                + " classKey:" + auctionStruct.classKey
                + " auctionType:" + auctionStruct.auctionType);
        }
        TypeSessionClassContainer typeSession = new TypeSessionClassContainer(auctionStruct.auctionType,
            auctionStruct.sessionName, auctionStruct.classKey);

        UserSessionKey userSessionKey = new UserSessionKey(currentSession, typeSession);
        ChannelKey channelKey = new ChannelKey(ChannelType.CB_DAIM, userSessionKey);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, auctionStruct);
        auctionSupplier.dispatch(event);
    }
    
    
    public void registerForDirectedAIM(String sessionName, int classKey) 
    throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException 
	{
    	if (Log.isDebugOn())
        {
    		Log.debug(this,"registerForDirectedAIM for sessionName:"+sessionName+" classKey:"+classKey+" userId:"+thisUserId); 
        }
    	try
		{		
			ServicesHelper.getFirmService().registerForDirectedAIM(sessionName, classKey, thisUserId);
			
		}catch(NotAcceptedException nae)
		{			
			switch (nae.details.error)
			{		
				case NotAcceptedCodes.USER_NOT_AFFILIATED_TO_ANY_FIRM:
					Log.exception(this, "register to Directed AIM fail for "+ currentSession +"USER_NOT_AFFILIATED_TO_ANY_FIRM", nae);	
				    throw ExceptionBuilder.notAcceptedException(
		                    "registerForDirectedAIM Failed",
		                    NotAcceptedCodes.USER_NOT_AFFILIATED_TO_ANY_FIRM);
				
				case NotAcceptedCodes.INVALID_AFFILIATED_FIRM:
					Log.exception(this, "register to Directed AIM fail for "+ currentSession +"INVALID_AFFILIATED_FIRM", nae);
					throw ExceptionBuilder.notAcceptedException(
		                    "registerForDirectedAIM Failed",
		                    NotAcceptedCodes.INVALID_AFFILIATED_FIRM);
					
				case NotAcceptedCodes.ALREADY_UPDATED_AS_REGISTERED:
					Log.exception(this, "register to Directed AIM fail for "+ currentSession +"ALREADY_UPDATED_AS_REGISTERED", nae);
					throw ExceptionBuilder.notAcceptedException(
		                    "registerForDirectedAIM Failed",
		                    NotAcceptedCodes.ALREADY_UPDATED_AS_REGISTERED);
					
				case NotAcceptedCodes.ALREADY_UPDATED_AS_UNREGISTERED: 
					Log.exception(this, "register to Directed AIM fail for "+ currentSession +"ALREADY_UPDATED_AS_UNREGISTERED", nae);
					throw ExceptionBuilder.notAcceptedException(
		                    "registerForDirectedAIM Failed",
		                    NotAcceptedCodes.ALREADY_UPDATED_AS_UNREGISTERED);
					
				default:
					Log.exception(this, "register to Directed AIM fail for "+ currentSession, nae);
					throw nae;
			}
		
		}catch(TransactionFailedException tfe)
		{
		Log.exception(this, "TransactionFailedException register to Directed AIM fail for "+ currentSession, tfe);
		throw ExceptionBuilder.dataValidationException(
	          "registerForDirectedAIM Failed",NotAcceptedCodes.NOT_REGISTERED_FOR_DIRECTED_AIM);	              
		}
    }

    public BaseSessionManager getSessionManager()
    {
        return currentSession;
    }
}
