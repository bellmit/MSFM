package com.cboe.application.orderEntry;

import static com.cboe.application.order.common.UserOrderServiceUtil.*;
import static com.cboe.application.order.common.UserOrderServiceUtil.createLegOrderDetails;
import com.cboe.application.order.common.UserOrderServiceUtil;
import com.cboe.application.order.OrderQueryCache;
import com.cboe.application.order.OrderQueryCacheFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.application.shared.TransactionTimingRegistration;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.util.OrderCallSnapshot;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.CmiOrderIdStructContainer;
import com.cboe.domain.util.OrderIdStructContainerFactory;
import com.cboe.domain.util.StructBuilder;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiQuote.RFQEntryStruct;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.order.CrossOrderIdStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserOrderService;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelAdapterFactory;
import static com.cboe.application.shared.LoggingUtil.*;
import com.cboe.application.shared.LoggingUtil;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.domain.util.InternalExtensionFields;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;



/**
 * User Trader Service implementation
 * 
 * 6/25/7:	Made changes to generate OrderStatus NEW messages -GJ.
 *
 * @author Derek T. Chambers-Boucher
 * @author Tom Lynch
 * @author Jeff Illian
 * @author Jing Chen
 * @author Gijo Joseph
 * @author Yaowapa Krueya
 * @version 6/25/2007
 */

public class UserOrderEntryImpl extends BObject implements com.cboe.interfaces.application.OrderEntryV9, UserSessionLogoutCollector
{
    private SessionManager sessionManager = null;
    private UserOrderService userOrderHandlingService = null;
    private UserSessionLogoutProcessor logoutProcessor = null;
    private ProductQueryServiceAdapter pqsAdapter = null;
    private String  thisUserId;
    private static final String SEP = ":";
//    private InstrumentedEventChannelAdapter internalEventChannel; // for publishing NEW 
    private OrderQueryCache orderQueryCache;
    private int tltcErrorCode;
    private ConcurrentEventChannelAdapter internalEventChannel;
    private static final LegOrderDetailStruct[] EMPTY_LegOrderDetailStruct_ARRAY = new LegOrderDetailStruct[0];
    public static boolean isShortSaleOn = false;


    static
    {
        try
        {
            String isShortSaleSwitchOn = System.getProperty("isShortSaleSwitchOn");
            isShortSaleOn = isShortSaleSwitchOn != null ? new Boolean(isShortSaleSwitchOn.trim()) : false;

        }
        catch (Exception e)
        {
            Log.exception(new StringBuilder(100).append("Error initializing isShortSaleSwitchOn variable").append(e.getMessage()).toString(),e);
        }
        Log.information(new StringBuilder(100).append("isShortSaleSwitchOn:").append(isShortSaleOn).toString());
    }

    /**
     * UserOrderEntryImpl constructor 
     */
    public UserOrderEntryImpl()
    {
    	super();
        try
        {
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);

        }
        catch (Exception e)
        {
        	Log.exception("Exception getting CAS_INSTRUMENTED_IEC!", e);
        }
        tltcErrorCode = useNoWorkingOrder() ? DataValidationCodes.NO_WORKING_ORDER : DataValidationCodes.INVALID_ORDER_ID;
    }
    

    /**
     * Sets the SessionManager for this User OrderEntry Service.  This operation is
     * primarily performed by the UserOrderEntryHomeImpl.
     */
    public void setSessionManager(SessionManager sessionMgr)
    {
        if (sessionMgr != null)
        {
            sessionManager = sessionMgr;
            logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
            EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, sessionMgr);
            LogoutServiceFactory.find().addLogoutListener(sessionMgr, this);
            try
            {
                thisUserId = sessionMgr.getValidSessionProfileUser().userId;
            }
            catch(org.omg.CORBA.UserException e)
            {
                Log.exception(this, "session : " + sessionManager, e);
            }
           	orderQueryCache = OrderQueryCacheFactory.find(thisUserId);
        }
    }

    /**
     * Sends the order to the Order Handling Service and adds the complete order (retrieved
     * from the remote OHS) to the local cache.
     */
    public OrderIdStruct acceptOrder(OrderEntryStruct anOrder)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
    	OrderCallSnapshot.enter();
    	boolean exceptionWasThrown = true;
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        OrderStruct order = buildOrder(anOrder);
        try
        {
        	entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        
 		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );
       		
        CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(anOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId, anOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
	        OrderIdStruct results = getUserOrderHandlingService().acceptOrder(order);
	       	
	       	exceptionWasThrown = false;
	    	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
	        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	            	
	        if (generateNewMsg())
	        {
	        	order.orderId = results;
	        	if (order.productType == ProductTypes.STRATEGY)
	        	{
	        		order.legOrderDetails  = createLegOrderDetails(order);
	        	}
	        	else
	        	{
	        		order.legOrderDetails  = EMPTY_LegOrderDetailStruct_ARRAY;
	        	}
	        	publishNewMessage(this, internalEventChannel, order);
	        }
	        failed = false;
	    	OrderCallSnapshot.done();
	        Log.information(this, createOrderLogSnapshot("acceptOrder", order, entityId, sessInfo));
	        return results;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
        	maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
            if(maybe){
                StringBuffer infoBuffer = new StringBuffer();
                infoBuffer.append("acceptOrder MAYBE failed for orderId: ").append(cmiOrderId).
                    append(" UserId: ").append(thisUserId).
                    append(" productKey: ").append(anOrder.productKey).
                    append(" userAssignedId: ").append(anOrder.userAssignedId);
                Log.exception("UserOrderEntryImpl>>acceptOrder>>org.omg.CORBA.SystemException: " + infoBuffer.toString(), cse);                
            }
            throw cse;
        }
        catch (SystemException se)
        {
        	maybe = true;
        	throw se;
        }
        catch (CommunicationException ce)
        {
        	maybe = true;
        	throw ce;
        }
        finally
        {
        	if (failed)
        	{        		
        		if (maybe)
        		{
                    Log.alarm(this, "acceptOrder MAYBE failed for orderId: " + cmiOrderId);
                }
        		orderQueryCache.orderEntryFailed(cmiOrderId, maybe);
        	}
        }
    }

    public OrderIdStruct acceptOrderByProductName(ProductNameStruct productName, OrderEntryStruct anOrder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        try
        {
            ProductStruct product = getProductQueryServiceAdapter().getProductByName(productName);
            anOrder.productKey = product.productKeys.productKey;
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
        return acceptOrder(anOrder);
    }

    /**
     * Sends the cancel request to the Order Handling Service.
     */
    public void acceptOrderCancelRequest(CancelRequestStruct cancelRequest)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        
        OrderStruct oldOrder = orderQueryCache.getOrderFromOrderCache(cancelRequest.orderId);
        int pkey;
        if (oldOrder == null )
        {
        	// check if the order is in the orderEntry cache. If not, return Invalid OrderId; else send the request down to server.
            if ((pkey = orderQueryCache.getProdKeyFromOrderEntryCache(cancelRequest.orderId)) < 0)
            {
            throw ExceptionBuilder.dataValidationException("OrderId is invalid! " + getOrderIdString(cancelRequest.orderId), DataValidationCodes.INVALID_ORDER_ID);
        }
        }
        else
        {
        if (oldOrder.leavesQuantity == 0 )
        {        	
        	if (oldOrder.tradedQuantity == 0 || generateTLTC() == false)
        	{
        		// Log?
        		throw ExceptionBuilder.dataValidationException("Order has been filled/cancelled already", tltcErrorCode);
        	}
        	else
        	{
        		// TODO generate TLTC and return. 
        		// The current plan is to not to do TLTC; so, not coding for it.   
        		// publishTLTC(this, channel, cancelRequest, order);
        		return;
        	}
        }
	        pkey = oldOrder.productKey;
        }
        
        String sessInfo = sessionManager.toString();
        try
        {
            ProductStruct product = getProductQueryServiceAdapter().getProductByKey(pkey);
            long entityId = 0L;
            try
            {
                entityId = TransactionTimingUtil.setEntityID();
                
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
         	
         	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelOrderEmitPoint(), entityId, TransactionTimer.Enter );
                    
            getUserOrderHandlingService().acceptCancel(cancelRequest, product.productKeys);
            
            exceptionWasThrown = false;                     	 
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelOrderEmitPoint(), entityId,
                 exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
               
            // We want to remove IDs from the cache after cancel because we don't expect to
            // refer to this order again. For now we won't remove the IDs because we're
            // afraid that users may try to cancel an order multiple times, and cleaning up
            // our cache would cause the extra cancels to go to the server.
            //----> orderProductCache.removeIds(cancelRequest.orderId);
            OrderCallSnapshot.done();
            Log.information(this, createLogSnapshot("acceptOrderCancelRequest", product.productKeys, cancelRequest.sessionName, cancelRequest.orderId, cancelRequest.userAssignedCancelId, cancelRequest.quantity, entityId, sessInfo));
                      
        }
        catch (NotFoundException e)
        {
            Log.exception(this, e);
            throw ExceptionBuilder.dataValidationException("Order has invalid product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    /**
     * Sends the update request to the Order Handling Service.
     */
    public void acceptOrderUpdateRequest(int currentRemainingQuantity, OrderEntryStruct updatedOrder)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
     {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
         long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, createOrderLog("acceptOrderUpdateRequest", entityId, sessInfo, updatedOrder.sessionNames[0]));
        OrderStruct order = buildOrder(updatedOrder);
        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        
     	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUpdateOrderEmitPoint(), entityId, TransactionTimer.Enter );
             
        getUserOrderHandlingService().acceptUpdate(currentRemainingQuantity, order);
        
        exceptionWasThrown = false;     
	    TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUpdateOrderEmitPoint(), entityId,
	        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );             
         
        
        OrderCallSnapshot.done();
        Log.information(this, createOrderLogSnapshot("acceptOrderUpdateRequest", order, entityId, sessInfo));
     }

    /**
     * Sends the cancel replace request to the Order Handling Service.
     */
    public OrderIdStruct acceptOrderCancelReplaceRequest(CancelRequestStruct cancelRequest, OrderEntryStruct newOrder)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        OrderStruct order = buildOrder(newOrder);
        CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(newOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId, newOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
	        try
	        {
	        	entityId = TransactionTimingUtil.setEntityID();
	        }
	        catch (Exception e)
	        {
	        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
	        }
	        
	     	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderEmitPoint(), entityId, TransactionTimer.Enter );
	              
	        OrderIdStruct results = getUserOrderHandlingService().acceptCancelReplace(cancelRequest, order);
	        exceptionWasThrown = false;
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderEmitPoint(), entityId,
	            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	        
	        
	        if (generateNewMsg())
	        {
	        	order.orderId = results;
	        	if (order.productType == ProductTypes.STRATEGY)
	        	{
	        		order.legOrderDetails  = createLegOrderDetails(order);
	        	}
	        	else
	        	{
	        		order.legOrderDetails  = EMPTY_LegOrderDetailStruct_ARRAY;
	        	}
	        	publishNewMessage(this, internalEventChannel, order);
	        }
	        OrderCallSnapshot.done();
	        Log.information(this, createOrderLogSnapshot("acceptOrderCancelReplaceRequest", order, entityId, sessInfo));
	        failed = false;
	        return results;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
        	maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
        	throw cse;
        }
        catch (SystemException se)
        {
        	maybe = true;
        	throw se;
        }
        catch (CommunicationException ce)
        {
        	maybe = true;
        	throw ce;
        }
        finally
        {
        	if (failed)
        	{        		
        		if (maybe)
        		{
        			Log.alarm(this, "acceptOrderCancelReplaceRequest MAYBE failed for orderId: " + cmiOrderId);
        		}
        		orderQueryCache.orderEntryFailed(cmiOrderId, maybe);
        	}
        }
     }

    /**
     * Sends the request for quote to the market maker quote service.
     */
    public void acceptRequestForQuote(RFQEntryStruct aRFQ)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, createOrderLog("acceptRequestForQuote", entityId, sessInfo, aRFQ.sessionName, aRFQ.productKey));
        RFQStruct RFQ = buildRFQ(aRFQ);
        try
        {
        	entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getRFQEmitPoint(), entityId, TransactionTimer.Enter );
        
        getUserOrderHandlingService().requestForQuote(RFQ);
        exceptionWasThrown = false;
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getRFQEmitPoint(), entityId,
            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
         
        
        OrderCallSnapshot.done();
        Log.information(this, createLogSnapshot("acceptRequestForQuote",
                RFQ.productKeys, RFQ.sessionName, null, "", RFQ.quantity, entityId, sessInfo));
    }

    /**
      * Send two Crossing orders to orderHandlingService.
      */
    public void acceptCrossingOrder(OrderEntryStruct crossingOrder1, OrderEntryStruct crossingOrder2)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, createOrderLog("acceptCrossingOrder", entityId, sessInfo, crossingOrder1.sessionNames[0]));
        OrderStruct order1 = buildOrder(crossingOrder1);
        OrderStruct order2 = buildOrder(crossingOrder2);
        CmiOrderIdStructContainer cmiOrderId1 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(crossingOrder1);
        orderQueryCache.addToOrderEntryCache(cmiOrderId1, crossingOrder1.productKey);
        CmiOrderIdStructContainer cmiOrderId2 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(crossingOrder2);
        orderQueryCache.addToOrderEntryCache(cmiOrderId2, crossingOrder2.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
	        try
	        {
	        	entityId = TransactionTimingUtil.setEntityID();
	        }
	        catch (Exception e)
	        {
	        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
	        }
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );
	        
	        CrossOrderIdStruct crossedOrder = getUserOrderHandlingService().acceptCrossingOrderV2(order1, order2);
	        exceptionWasThrown = false;
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
	             exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	      
	        // Can't cache order IDs and product key because we don't have CBOE ID.
	        // Generate OrderStatus NEW messages for both the orders in the cross. 
	        if (generateNewMsg())
	        {
                if ( // compare the entire "gang of 5" to assign OrderId data correctly for "new"  
                    ( 0 == order1.orderId.branch.compareTo(crossedOrder.buySideOrderId.branch)) &&
                    ( order1.orderId.branchSequenceNumber == crossedOrder.buySideOrderId.branchSequenceNumber) &&
                    ( 0 == order1.orderId.orderDate.compareTo(crossedOrder.buySideOrderId.orderDate)) &&
                    ( 0 == order1.orderId.executingOrGiveUpFirm.exchange.compareTo(crossedOrder.buySideOrderId.executingOrGiveUpFirm.exchange)) &&
                    ( 0 == order1.orderId.executingOrGiveUpFirm.firmNumber.compareTo(crossedOrder.buySideOrderId.executingOrGiveUpFirm.firmNumber)) &&
                    ( 0 == order1.orderId.correspondentFirm.compareTo(crossedOrder.buySideOrderId.correspondentFirm))
                ) {
                    order1.orderId = crossedOrder.buySideOrderId;
                    order2.orderId = crossedOrder.sellSideOrderId;       
                } else {
                    order1.orderId = crossedOrder.sellSideOrderId;       
                    order2.orderId = crossedOrder.buySideOrderId;
                }
	        	publishNewMessage(this, internalEventChannel, order1);
	        	publishNewMessage(this, internalEventChannel, order2);
	        }        
	        OrderCallSnapshot.done();
	        Log.information(this, createOrderLogSnapshot("acceptCrossingOrder", order1, entityId, sessInfo));
	        failed = false;
	    }
        catch (org.omg.CORBA.SystemException cse)
        {
        	maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
        	throw cse;
        }
	    catch (SystemException se)
	    {
	    	maybe = true;
	    	throw se;
	    }
	    catch (CommunicationException ce)
	    {
	    	maybe = true;
	    	throw ce;
	    }
	    finally
	    {
	    	if (failed)
	    	{        		
	    		if (maybe)
	    		{
	    			Log.alarm(this, "acceptCrossingOrder MAYBE failed for <" + cmiOrderId1 + " , " + cmiOrderId2 + ">");
	    		}
	    		orderQueryCache.orderEntryFailed(cmiOrderId1, maybe);
	    		orderQueryCache.orderEntryFailed(cmiOrderId2, maybe);
	    	}
	    }
        
    }

    /**
     * Sends the order to the Order Handling Service and adds the complete order (retrieved from the remote OHS) to the local cache.
     */
    public OrderIdStruct acceptStrategyOrder(OrderEntryStruct anOrder, LegOrderEntryStruct[] legEntryDetails)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, createOrderLog("acceptStrategyOrder", entityId, sessInfo, anOrder.sessionNames[0]));
        OrderStruct order = buildOrder(anOrder);
        CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(anOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId, anOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
	        try
	        {
	        	entityId = TransactionTimingUtil.setEntityID();
	        }
	        catch (Exception e)
	        {
	        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
	        }
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );
	        
	        
	        OrderIdStruct results = getUserOrderHandlingService().acceptStrategyOrder(order, legEntryDetails);
	        exceptionWasThrown = false;
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
	            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	        
	        if (generateNewMsg())
	        {
	        	order.orderId = results;
	        	order.legOrderDetails = createLegOrderDetails(order, legEntryDetails);
	        	publishNewMessage(this, internalEventChannel, order);
	        }        
	        OrderCallSnapshot.done();
            String snapshot = createOrderLogSnapshot("acceptStrategyOrder", order, entityId, sessInfo);
            StringBuilder snap = new StringBuilder(snapshot.length()+16);
            snap.append(snapshot).append(" LEGS:").append(legEntryDetails.length);
            Log.information(this, snap.toString());
	        failed = false;
	        return results;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
        	maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
        	throw cse;
        }
        catch (SystemException se)
        {
        	maybe = true;
        	throw se;
        }
        catch (CommunicationException ce)
        {
        	maybe = true;
        	throw ce;
        }
        finally
        {
        	if (failed)
        	{        		
        		if (maybe)
        		{
        			Log.alarm(this, "acceptStrategyOrder MAYBE failed for orderId: " + cmiOrderId);
        		}
        		orderQueryCache.orderEntryFailed(cmiOrderId, maybe);
        	}
        }        
    }

    /**
     * Sends the update request to the Order Handling Service.
     */
    public void acceptStrategyOrderUpdateRequest(int currentRemainingQuantity, OrderEntryStruct updatedOrder, LegOrderEntryStruct[] legEntryDetails)
     throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, createOrderLog("acceptStrategyOrderUpdateRequest", entityId, sessInfo, updatedOrder.sessionNames[0]));
        OrderStruct order = buildOrder(updatedOrder);
        try
        {
        	entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUpdateOrderEmitPoint(), entityId, TransactionTimer.Enter );
        
        getUserOrderHandlingService().acceptStrategyUpdate(currentRemainingQuantity, order, legEntryDetails);
        exceptionWasThrown = false;
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUpdateOrderEmitPoint(), entityId,
            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
         
        OrderCallSnapshot.done();
        String snapshot = createOrderLogSnapshot("acceptStrategyOrderUpdateRequest", order, entityId, sessInfo);
        StringBuilder snap = new StringBuilder(snapshot.length()+16);
        snap.append(snapshot).append(" LEGS:").append(legEntryDetails.length);
        Log.information(this, snap.toString());
    }

    /**
     * Sends the cancel replace request to the Order Handling Service.
     */
    public OrderIdStruct acceptStrategyOrderCancelReplaceRequest(CancelRequestStruct cancelRequest, OrderEntryStruct newOrder, LegOrderEntryStruct[] legEntryDetails)
     throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, createOrderLog("acceptStrategyOrderCancelReplaceRequest", entityId, sessInfo, cancelRequest.sessionName));
        OrderStruct order = buildOrder(newOrder);
        CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(newOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId, newOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
	        try
	        {
	        	entityId = TransactionTimingUtil.setEntityID();
	        }
	        catch (Exception e)
	        {
	        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
	        }
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderEmitPoint(), entityId, TransactionTimer.Enter );
	        
	        OrderIdStruct results = getUserOrderHandlingService().acceptStrategyCancelReplace(cancelRequest, order, legEntryDetails);
	        exceptionWasThrown = false;
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderEmitPoint(), entityId,
	            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	       
	        if (generateNewMsg())
	        {
	        	order.orderId = results;
	        	order.legOrderDetails = createLegOrderDetails(order, legEntryDetails);
	        	publishNewMessage(this, internalEventChannel, order);
	        }
	        OrderCallSnapshot.done();
            String snapshot = createOrderLogSnapshot("acceptStrategyOrderCancelReplaceRequest", order, entityId, sessInfo);
            StringBuilder snap = new StringBuilder(snapshot.length()+16);
            snap.append(snapshot).append(" LEGS:").append(legEntryDetails.length);
            Log.information(this, snap.toString());
	        failed = false;
	        return results;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
        	maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
        	throw cse;
        }
        catch (SystemException se)
        {
        	maybe = true;
        	throw se;
        }
        catch (CommunicationException ce)
        {
        	maybe = true;
        	throw ce;
        }
        finally
        {
        	if (failed)
        	{        		
        		if (maybe)
        		{
        			Log.alarm(this, "acceptStrategyOrderCancelReplaceRequest MAYBE failed for orderId: " + cmiOrderId);
        		}
        		orderQueryCache.orderEntryFailed(cmiOrderId, maybe);
        	}
        }                
    }

    /**
     * Returns the instance of the order handling service.
     */
    private UserOrderService getUserOrderHandlingService()
    {
        if (userOrderHandlingService == null )
        {
            userOrderHandlingService = ServicesHelper.getUserOrderService(sessionManager);
        }
        return userOrderHandlingService;
    }

    /**
     * Get the instance of the ProductQueryServiceAdapter.
     */
    private ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if (null == pqsAdapter)
        {
            pqsAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqsAdapter;
    }

    /**
     * returns a new populated RFQStruct
     */
    private RFQStruct buildRFQ(RFQEntryStruct rfqEntry)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        try
        {
            ProductStruct product = getProductQueryServiceAdapter().getProductByKey(rfqEntry.productKey);
            return  new RFQStruct(product.productKeys,
                                  rfqEntry.sessionName,
                                  rfqEntry.quantity,
                                  0,
                                  (short)0,
                                  StructBuilder.buildTimeStruct());
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    /**
     * returns a new populated OrderStruct
     */
    private OrderStruct buildOrder(OrderEntryStruct orderEntry)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        try
        {
            ProductStruct product = getProductQueryServiceAdapter().getProductByKey(orderEntry.productKey);
            ExchangeAcronymStruct userAcronym = sessionManager.getValidSessionProfileUser().userAcronym;
            return com.cboe.application.order.common.OrderStructBuilder.buildOrderStruct(orderEntry, product.productKeys, thisUserId, userAcronym);
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
    }

    public void acceptUserSessionLogout()
    {
        String smgr = sessionManager.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+40);
        calling.append("calling acceptUserSessionLogout for ").append(smgr);
        Log.information(this, calling.toString());
        // Do any individual service clean up needed for logout
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);

        logoutProcessor.setParent(null);
        logoutProcessor = null;

        sessionManager = null;
        userOrderHandlingService = null;
    }

    /** Submit two orders for an "internal" trade. Client is willing to trade
     * with self if the open market can't match or beat client price.
     * @param primaryOrder Order that client wants to trade with self.
     * @param matchOrder Order that can fulfill primaryOrder.
     * @param matchType cmiConstants::MatchTypes
     * @return IDs or error results for primaryOrder and matchOrder.
     * @see com.cboe.idl.cmiConstants.MatchTypes
     */
    public InternalizationOrderResultStruct acceptInternalizationOrder(OrderEntryStruct primaryOrder, OrderEntryStruct matchOrder, short matchType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        OrderStruct primary = buildOrder(primaryOrder);
        OrderStruct match = buildOrder(matchOrder);
        StringBuilder calling = new StringBuilder(sessInfo.length()+40);
        calling.append("calling acceptInternalizationOrder for ").append(sessInfo);
        Log.information(this, calling.toString());
        CmiOrderIdStructContainer cmiOrderId1 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(primaryOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId1, primaryOrder.productKey);
        CmiOrderIdStructContainer cmiOrderId2 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(matchOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId2, matchOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
	        try
	        {
	        	entityId = TransactionTimingUtil.setEntityID();
	        }
	        catch (Exception e)
	        {
	        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
	        }
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );
	               
	        InternalizationOrderResultStruct result = getUserOrderHandlingService().acceptInternalizationOrder(primary, match, matchType);
	        exceptionWasThrown = false;
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
	            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	       
	        if (generateNewMsg())
	        {
	            if (0 == result.primaryOrderResult.result.errorCode)
	            {
	            	primary.orderId = result.primaryOrderResult.orderId;
	                primary.optionalData = "A:AIM";
	                publishNewMessage(this, internalEventChannel, primary);
	            }
	            if (0 == result.matchOrderResult.result.errorCode)
	            {
	            	match.orderId = result.matchOrderResult.orderId;
		            StringBuilder s =  new StringBuilder(32);
		            s.append("A:RE:");
		            s.append(primary.orderId.executingOrGiveUpFirm.firmNumber).append(":");
		            s.append(primary.orderId.branch).append(":").append(primary.orderId.branchSequenceNumber);
		            matchOrder.optionalData = s.toString();
		        	publishNewMessage(this, internalEventChannel, match);        	
	            }
	        }
	        OrderCallSnapshot.done();
	        Log.information(this, createOrderLogSnapshot("acceptInternalizationOrder", primary, entityId, sessInfo));
	        failed = false;
	        return result;
	    }
        catch (org.omg.CORBA.SystemException cse)
        {
        	maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
        	throw cse;
        }
	    catch (SystemException se)
	    {
	    	maybe = true;
	    	throw se;
	    }
	    catch (CommunicationException ce)
	    {
	    	maybe = true;
	    	throw ce;
	    }
	    finally
	    {
	    	if (failed)
	    	{        		
	    		if (maybe)
	    		{
	    			Log.alarm(this, "InternalizationOrderResultStruct MAYBE failed for <" + cmiOrderId1 + " , " + cmiOrderId2 + ">");
	    		}
	    		orderQueryCache.orderEntryFailed(cmiOrderId1, maybe);
	    		orderQueryCache.orderEntryFailed(cmiOrderId2, maybe);
	    	}
	    }
    }

    public InternalizationOrderResultStruct acceptInternalizationStrategyOrder(OrderEntryStruct primaryOrder,
                                                                               LegOrderEntryStruct[] primaryLegOrderEntryStruct,
                                                                               OrderEntryStruct matchOrder,
                                                                               LegOrderEntryStruct[] matchLegOrderEntryStruct,
                                                                               short matchType)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotAcceptedException, TransactionFailedException
    {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        OrderStruct primary = buildOrder(primaryOrder);
        OrderStruct match = buildOrder(matchOrder);
        StringBuilder sb = new StringBuilder(sessInfo.length()+50);
        sb.append("calling acceptInternalizationStrategyOrder for ").append(sessInfo);
        Log.information(this, sb.toString());
        CmiOrderIdStructContainer cmiOrderId1 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(primaryOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId1, primaryOrder.productKey);
        CmiOrderIdStructContainer cmiOrderId2 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(matchOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId2, matchOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
	        try
	        {
	        	entityId = TransactionTimingUtil.setEntityID();
	        }
	        catch (Exception e)
	        {
	        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
	        }
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );
	             
	        InternalizationOrderResultStruct result = getUserOrderHandlingService().acceptInternalizationStrategyOrder(primary, primaryLegOrderEntryStruct, match, matchLegOrderEntryStruct, matchType );
	        exceptionWasThrown = false;
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
	            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
	        
	        if (generateNewMsg())
	        {
	            if (0 == result.primaryOrderResult.result.errorCode)
	            {
	            	primary.orderId = result.primaryOrderResult.orderId;
	            	primary.legOrderDetails = createLegOrderDetails(primary, primaryLegOrderEntryStruct);
	                primary.optionalData = "A:AIM";
	                publishNewMessage(this, internalEventChannel, primary);
	            }
	            
	            if (0 == result.matchOrderResult.result.errorCode)
	            {
	            	match.orderId = result.matchOrderResult.orderId;
	            	match.legOrderDetails = createLegOrderDetails(match, matchLegOrderEntryStruct);
	            	StringBuilder s =  new StringBuilder();
	            	s.append("A:RE:");
	            	s.append(primary.orderId.executingOrGiveUpFirm.firmNumber).append(":");
	            	s.append(primary.orderId.branch).append(":").append(primary.orderId.branchSequenceNumber);
	            	matchOrder.optionalData = s.toString();
	                publishNewMessage(this, internalEventChannel, match);
	            }
	        }
	        OrderCallSnapshot.done();
            sb.setLength(0);
            sb.append(createOrderLogSnapshot("acceptInternalizationStrategyOrder", primary, entityId, sessInfo))
              .append(" LEGS:").append(primaryLegOrderEntryStruct.length);
            Log.information(this, sb.toString());
	        failed = false;
	        return result;
	    }
        catch (org.omg.CORBA.SystemException cse)
        {
        	maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
        	throw cse;
        }
	    catch (SystemException se)
	    {
	    	maybe = true;
	    	throw se;
	    }
	    catch (CommunicationException ce)
	    {
	    	maybe = true;
	    	throw ce;
	    }
	    finally
	    {
	    	if (failed)
	    	{        		
	    		if (maybe)
	    		{
	    			Log.alarm(this, "acceptInternalizationStrategyOrder MAYBE failed for <" + cmiOrderId1 + " , " + cmiOrderId2 + ">");
	    		}
	    		orderQueryCache.orderEntryFailed(cmiOrderId1, maybe);
	    		orderQueryCache.orderEntryFailed(cmiOrderId2, maybe);
	    	}
	    }
    }

    /****************  OrderEntryV7 Methods **************
 * OrderEntryV7 serves to replace OrderEntry without publishing New messages.  OrderEntryV7
 * methods return all the order processing information directly rather than returning only
 * orderId information and relying on the New message to deliver the processed order information.
 */

    /**
     * Accepts an order and returns a processed OrderStruct.  This method does not publish a New message
     * but simply returns the processed OrderStruct back to the caller.
     */
    public OrderStruct acceptOrderNoAckV7(OrderEntryStruct anOrder) throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        OrderCallSnapshot.enter();
        boolean exceptionWasThrown = true;
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        OrderStruct order = buildOrder(anOrder);
        addSendNewExtensionValue(order, false);

        try
        {
            entityId = TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }

        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );

        CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(anOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId, anOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
            OrderIdStruct results = getUserOrderHandlingService().acceptOrder(order);

            exceptionWasThrown = false;
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

            OrderCallSnapshot.done();
            Log.information(this, LoggingUtil.createOrderLogSnapshot("acceptOrderNoAckV7", order, entityId, sessInfo));

            order.orderId = results;
            if (order.productType == ProductTypes.STRATEGY)
            {
                order.legOrderDetails  = UserOrderServiceUtil.createLegOrderDetails(order);
            }
            else
            {
                order.legOrderDetails  = EMPTY_LegOrderDetailStruct_ARRAY;
            }
            failed = false;

            // remove SendNew extension before returning the order
            removeSendNewExtensionValue(order);
            OrderStruct returnOrder = UserOrderServiceUtil.processNewOrder(order);
            orderQueryCache.put(returnOrder);
            return returnOrder;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
            maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
            throw cse;
        }
        catch (SystemException se)
        {
            maybe = true;
            throw se;
        }
        catch (CommunicationException ce)
        {
            maybe = true;
            throw ce;
        }
        finally
        {
            if (failed)
            {
                if (maybe)
                {
                    Log.alarm(this, "acceptOrderNoAckV7 MAYBE failed for orderId: " + cmiOrderId);
                }
                orderQueryCache.orderEntryFailed(cmiOrderId, maybe);
            }
        }
    }

    public OrderStruct acceptOrderByProductNameNoAckV7(ProductNameStruct productName, OrderEntryStruct anOrder)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException,
                TransactionFailedException, AlreadyExistsException
    {
        try
        {
            com.cboe.idl.cmiProduct.ProductStruct product = getProductQueryServiceAdapter().getProductByName(productName);
            anOrder.productKey = product.productKeys.productKey;
        }
        catch(NotFoundException e)
        {
            throw ExceptionBuilder.dataValidationException("Invalid Product", DataValidationCodes.INVALID_PRODUCT);
        }
        return acceptOrderNoAckV7(anOrder);
    }

    public OrderStruct acceptOrderCancelReplaceRequestNoAckV7(CancelRequestStruct cancelRequest, OrderEntryStruct newOrder)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        OrderStruct order = buildOrder(newOrder);
        addSendNewExtensionValue(order, false);

        CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(newOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId, newOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
            try
            {
                entityId = TransactionTimingUtil.setEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }

            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderEmitPoint(), entityId, TransactionTimer.Enter );

            OrderIdStruct results = getUserOrderHandlingService().acceptCancelReplace(cancelRequest, order);
            exceptionWasThrown = false;
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderEmitPoint(), entityId,
            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

            OrderCallSnapshot.done();
            Log.information(this, LoggingUtil.createOrderLogSnapshot("acceptOrderCancelReplaceRequestNoAckV7", order, entityId, sessInfo));

            order.orderId = results;
            if (order.productType == com.cboe.idl.cmiConstants.ProductTypes.STRATEGY)
            {
                order.legOrderDetails  = UserOrderServiceUtil.createLegOrderDetails(order);
            }
            else
            {
                order.legOrderDetails  = EMPTY_LegOrderDetailStruct_ARRAY;
            }
            failed = false;

            // remove SendNew extension before returning the order
            removeSendNewExtensionValue(order);
            OrderStruct returnOrder = UserOrderServiceUtil.processNewOrder(order);
            orderQueryCache.put(returnOrder);
            return returnOrder;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
            maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
            throw cse;
        }
        catch (SystemException se)
        {
            maybe = true;
            throw se;
        }
        catch (CommunicationException ce)
        {
            maybe = true;
            throw ce;
        }
        finally
        {
            if (failed)
            {
                if (maybe)
                {
                    Log.alarm(this, "acceptOrderCancelReplaceRequestNoAckV7 MAYBE failed for orderId: " + cmiOrderId);
                }
                orderQueryCache.orderEntryFailed(cmiOrderId, maybe);
            }
        }
    }

    public OrderStruct acceptStrategyOrderNoAckV7(OrderEntryStruct anOrder, LegOrderEntryStructV2[] legEntryDetails)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException,
                TransactionFailedException, AlreadyExistsException
    {
        boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, LoggingUtil.createOrderLog("acceptStrategyOrderNoAckV7", entityId, sessInfo, anOrder.sessionNames[0]));
        OrderStruct order = buildOrder(anOrder);
        addSendNewExtensionValue(order, false);

        CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(anOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId, anOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
            try
            {
                entityId = TransactionTimingUtil.setEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );
            OrderIdStruct results;
            if(isShortSaleOn)
            {
                results = getUserOrderHandlingService().acceptStrategyOrderV7(order, legEntryDetails);
                order.legOrderDetails = UserOrderServiceUtil.createLegOrderDetails(order, legEntryDetails);
            }
            else
            {
                LegOrderEntryStruct[] legs = new LegOrderEntryStruct[legEntryDetails.length];
                for (int i = 0; i < legEntryDetails.length; i++)
                {
                    legs[i] = legEntryDetails[i].legOrderEntry;
                }

                results = getUserOrderHandlingService().acceptStrategyOrder(order, legs);
                order.legOrderDetails = UserOrderServiceUtil.createLegOrderDetails(order, legs);
            }
            exceptionWasThrown = false;
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
                    exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

            OrderCallSnapshot.done();
            String snapshot = LoggingUtil.createOrderLogSnapshot("acceptStrategyOrderNoAckV7", order, entityId, sessInfo);
            StringBuilder snap = new StringBuilder(snapshot.length()+16);
            snap.append(snapshot).append(" LEGS:").append(legEntryDetails.length);
            Log.information(this, snap.toString());

            order.orderId = results;

            failed = false;

            // remove SendNew extension before returning the order
            removeSendNewExtensionValue(order);

            OrderStruct returnOrder = UserOrderServiceUtil.processNewOrder(order);
            orderQueryCache.put(returnOrder);
            return returnOrder;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
            maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
            throw cse;
        }
        catch (SystemException se)
        {
            maybe = true;
            throw se;
        }
        catch (CommunicationException ce)
        {
            maybe = true;
            throw ce;
        }
        finally
        {
            if (failed)
            {
                if (maybe)
                {
                    Log.alarm(this, "acceptStrategyOrderNoAckV7 MAYBE failed for orderId: " + cmiOrderId);
                }
                orderQueryCache.orderEntryFailed(cmiOrderId, maybe);
            }
        }
    }

    public OrderStruct acceptStrategyOrderCancelReplaceRequestNoAckV7(CancelRequestStruct cancelRequest, OrderEntryStruct newOrder, LegOrderEntryStructV2[] legEntryDetails)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, LoggingUtil.createOrderLog("acceptStrategyOrderCancelReplaceRequestNoAckV7", entityId, sessInfo, cancelRequest.sessionName));
        OrderStruct order = buildOrder(newOrder);
        addSendNewExtensionValue(order, false);

        CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(newOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId, newOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
            try
            {
                entityId = TransactionTimingUtil.setEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderEmitPoint(), entityId, TransactionTimer.Enter );

            if(isShortSaleOn)
            {
                OrderIdStruct results = getUserOrderHandlingService().acceptStrategyCancelReplaceV7(cancelRequest, order, legEntryDetails);
                order.orderId = results;
                order.legOrderDetails = UserOrderServiceUtil.createLegOrderDetails(order, legEntryDetails);
            }
            else
            {
                LegOrderEntryStruct[] legs = new LegOrderEntryStruct[legEntryDetails.length];
                for (int i = 0; i < legEntryDetails.length; i++)
                {
                    legs[i] = legEntryDetails[i].legOrderEntry;
                }

                OrderIdStruct results = getUserOrderHandlingService().acceptStrategyCancelReplace(cancelRequest, order, legs);
                order.orderId = results;
                order.legOrderDetails = UserOrderServiceUtil.createLegOrderDetails(order, legs);

            }
            exceptionWasThrown = false;
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderEmitPoint(), entityId,
                exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

            OrderCallSnapshot.done();
            String snapshot = LoggingUtil.createOrderLogSnapshot("acceptStrategyOrderCancelReplaceRequestNoAckV7", order, entityId, sessInfo);
            StringBuilder snap = new StringBuilder(snapshot.length()+16);
            snap.append(snapshot).append(" LEGS:").append(legEntryDetails.length);
            Log.information(this, snap.toString());


            failed = false;

            // remove SendNew extension before returning the order
            removeSendNewExtensionValue(order);

            OrderStruct returnOrder = UserOrderServiceUtil.processNewOrder(order);
            orderQueryCache.put(returnOrder);
            return returnOrder;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
            maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
            throw cse;
        }
        catch (SystemException se)
        {
            maybe = true;
            throw se;
        }
        catch (CommunicationException ce)
        {
            maybe = true;
            throw ce;
        }
        finally
        {
            if (failed)
            {
                if (maybe)
                {
                    Log.alarm(this, "acceptStrategyOrderCancelReplaceRequestNoAckV7 MAYBE failed for orderId: " + cmiOrderId);
                }
                orderQueryCache.orderEntryFailed(cmiOrderId, maybe);
            }
        }
    }

    public InternalizationOrderResultStructV2 acceptInternalizationOrderNoAckV7(OrderEntryStruct primaryOrderEntry, OrderEntryStruct matchOrderEntry, short matchType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        OrderStruct primaryOrder = buildOrder(primaryOrderEntry);
        addSendNewExtensionValue(primaryOrder, false);
        OrderStruct matchOrder = buildOrder(matchOrderEntry);
        addSendNewExtensionValue(matchOrder, false);

        StringBuilder sb = new StringBuilder(sessInfo.length()+50);
        sb.append("calling acceptInternalizationOrderNoAckV7 for ").append(sessInfo);
        Log.information(this, sb.toString());

        CmiOrderIdStructContainer cmiOrderId1 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(primaryOrderEntry);
        orderQueryCache.addToOrderEntryCache(cmiOrderId1, primaryOrderEntry.productKey);
        CmiOrderIdStructContainer cmiOrderId2 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(matchOrderEntry);
        orderQueryCache.addToOrderEntryCache(cmiOrderId1, matchOrderEntry.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
            try
            {
                entityId = TransactionTimingUtil.setEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );

            InternalizationOrderResultStruct result =
                    getUserOrderHandlingService().acceptInternalizationOrder(primaryOrder, matchOrder, matchType);

            exceptionWasThrown = false;
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
                exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

            OrderCallSnapshot.done();
            Log.information(this, LoggingUtil.createOrderLogSnapshot("acceptInternalizationOrderNoAckV7",
                    primaryOrder, entityId, sessInfo));

            OrderResultStructV2 primaryOrderResultV2 = new OrderResultStructV2();
            OrderResultStructV2 matchOrderResultV2 = new OrderResultStructV2();
            InternalizationOrderResultStructV2 resultStructV2 =
                    new InternalizationOrderResultStructV2(primaryOrderResultV2, matchOrderResultV2);
            resultStructV2.primaryOrderResult.result = result.primaryOrderResult.result;
            resultStructV2.matchOrderResult.result = result.matchOrderResult.result;

            if (0 != result.primaryOrderResult.result.errorCode)
            {
                sb.setLength(0);
                sb.ensureCapacity(result.primaryOrderResult.result.errorMessage.length()+90);
                sb.append("acceptInternalizationOrderNoAckV7: PrimaryOrderResult errorCode = ")
                  .append(result.primaryOrderResult.result.errorCode)
                  .append(", ErrorMessage: ").append(result.primaryOrderResult.result.errorMessage);
                Log.information(this, sb.toString());
            }
            primaryOrder.orderId = result.primaryOrderResult.orderId;
            primaryOrder.optionalData = "A:AIM";

            // remove SendNew extension before returning the order
            removeSendNewExtensionValue(primaryOrder);
            OrderStruct primaryOrderReturn = UserOrderServiceUtil.processNewOrder(primaryOrder);
            resultStructV2.primaryOrderResult.order = primaryOrderReturn;

            if (0 != result.matchOrderResult.result.errorCode)
            {
                sb.setLength(0);
                sb.ensureCapacity(result.matchOrderResult.result.errorMessage.length()+90);
                sb.append("acceptInternalizationOrderNoAckV7: MatchOrderResult errorCode = ")
                  .append(result.matchOrderResult.result.errorCode)
                  .append(", ErrorMessage: ").append(result.matchOrderResult.result.errorMessage);
                Log.information(this, sb.toString());
            }

            matchOrder.orderId = result.matchOrderResult.orderId;
            sb.setLength(0);
            sb.append("A:RE:");
            sb.append(primaryOrder.orderId.executingOrGiveUpFirm.firmNumber).append(":");
            sb.append(primaryOrder.orderId.branch).append(":").append(primaryOrder.orderId.branchSequenceNumber);
            matchOrder.optionalData = sb.toString();

            // remove SendNew extension before returning the order
            removeSendNewExtensionValue(matchOrder);
            OrderStruct matchOrderReturn = UserOrderServiceUtil.processNewOrder(matchOrder);
            resultStructV2.matchOrderResult.order = matchOrderReturn;

            orderQueryCache.put(primaryOrderReturn);
            orderQueryCache.put(matchOrderReturn);
            failed = false;
            return resultStructV2;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
            maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
            throw cse;
        }
        catch (SystemException se)
        {
            maybe = true;
            throw se;
        }
        catch (CommunicationException ce)
        {
            maybe = true;
            throw ce;
        }
        finally
        {
            if (failed)
            {
                if (maybe)
                {
                    Log.alarm(this, "InternalizationOrderResultStructNoAckV7 MAYBE failed for <" + cmiOrderId1 + " , " + cmiOrderId2 + ">");
                }
                orderQueryCache.orderEntryFailed(cmiOrderId1, maybe);
                orderQueryCache.orderEntryFailed(cmiOrderId2, maybe);
            }
        }
    }

    public InternalizationOrderResultStructV2 acceptInternalizationStrategyOrderNoAckV7(OrderEntryStruct primaryOrderEntry,
                                                                                   LegOrderEntryStructV2[] primaryLegOrderEntryStructs,
                                                                                   OrderEntryStruct matchOrderEntry,
                                                                                   LegOrderEntryStructV2[] matchLegOrderEntryStructs,
                                                                                   short matchType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                NotAcceptedException, TransactionFailedException
    {
        boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        OrderStruct primaryOrder = buildOrder(primaryOrderEntry);
        addSendNewExtensionValue(primaryOrder, false);
        OrderStruct matchOrder = buildOrder(matchOrderEntry);
        addSendNewExtensionValue(matchOrder, false);

        StringBuilder sb = new StringBuilder(sessInfo.length()+55);
        sb.append("calling acceptInternalizationStrategyOrderNoAckV7 for ").append(sessInfo);
        Log.information(this, sb.toString());
        CmiOrderIdStructContainer cmiOrderId1 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(primaryOrderEntry);
        orderQueryCache.addToOrderEntryCache(cmiOrderId1, primaryOrderEntry.productKey);
        CmiOrderIdStructContainer cmiOrderId2 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(matchOrderEntry);
        orderQueryCache.addToOrderEntryCache(cmiOrderId1, matchOrderEntry.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
            try
            {
                entityId = TransactionTimingUtil.setEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );
            InternalizationOrderResultStruct result;
            LegOrderEntryStruct[] matchLegs=null;
            if(isShortSaleOn)
            {
                result = getUserOrderHandlingService().acceptInternalizationStrategyOrderV7(primaryOrder, primaryLegOrderEntryStructs, matchOrder, matchLegOrderEntryStructs, matchType );
                primaryOrder.legOrderDetails = UserOrderServiceUtil.createLegOrderDetails(primaryOrder, primaryLegOrderEntryStructs);
            }
            else
            {
                LegOrderEntryStruct[] primaryLegs = new LegOrderEntryStruct[primaryLegOrderEntryStructs.length];
                for (int i = 0; i < primaryLegOrderEntryStructs.length; i++)
                {
                    primaryLegs[i] = primaryLegOrderEntryStructs[i].legOrderEntry;
                }
                matchLegs = new LegOrderEntryStruct[matchLegOrderEntryStructs.length];
                for (int i = 0; i < matchLegOrderEntryStructs.length; i++)
                {
                    matchLegs[i] = matchLegOrderEntryStructs[i].legOrderEntry;
                }

                 result = getUserOrderHandlingService().acceptInternalizationStrategyOrder(primaryOrder,
                                                                                           primaryLegs, matchOrder, matchLegs, matchType);
                 primaryOrder.legOrderDetails = UserOrderServiceUtil.createLegOrderDetails(primaryOrder, primaryLegs);

            }
            
            exceptionWasThrown = false;
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
                exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

            OrderCallSnapshot.done();
            String snapshot = LoggingUtil.createOrderLogSnapshot("acceptInternalizationStrategyOrderNoAckV7", primaryOrder, entityId, sessInfo);
            sb.setLength(0);
            sb.ensureCapacity(snapshot.length()+16);
            sb.append(snapshot).append(" LEGS:").append(primaryLegOrderEntryStructs.length);
            Log.information(this, sb.toString());

            OrderResultStructV2 primaryOrderResultV2 = new OrderResultStructV2();
            OrderResultStructV2 matchOrderResultV2 = new OrderResultStructV2();
            
            InternalizationOrderResultStructV2 resultStructV2 =
                    new InternalizationOrderResultStructV2(primaryOrderResultV2, matchOrderResultV2);
            
            resultStructV2.primaryOrderResult.result = result.primaryOrderResult.result;
            resultStructV2.matchOrderResult.result = result.matchOrderResult.result;

            if (0 != result.primaryOrderResult.result.errorCode)
            {
                sb.setLength(0);
                sb.ensureCapacity(result.primaryOrderResult.result.errorMessage.length()+100);
                sb.append("acceptInternalizationStrategyOrderNoAckV7: PrimaryOrderResult errorCode = ")
                  .append(result.primaryOrderResult.result.errorCode)
                  .append(", ErrorMessage: ").append(result.primaryOrderResult.result.errorMessage);
                Log.information(this, sb.toString());
            }

            primaryOrder.orderId = result.primaryOrderResult.orderId;

            primaryOrder.optionalData = "A:AIM";

            // remove SendNew extension before returning the order
            removeSendNewExtensionValue(primaryOrder);
            OrderStruct primaryOrderReturn = UserOrderServiceUtil.processNewOrder(primaryOrder);
            resultStructV2.primaryOrderResult.order = primaryOrderReturn;

            if (0 != result.matchOrderResult.result.errorCode)
            {
                sb.setLength(0);
                sb.ensureCapacity(result.matchOrderResult.result.errorMessage.length()+100);
                sb.append("acceptInternalizationStrategyOrderNoAckV7: MatchOrderResult errorCode = ")
                  .append(result.matchOrderResult.result.errorCode)
                  .append(", ErrorMessage: ").append(result.matchOrderResult.result.errorMessage);
                Log.information(this, sb.toString());
            }

            matchOrder.orderId = result.matchOrderResult.orderId;
            if(isShortSaleOn)
            {
                matchOrder.legOrderDetails = UserOrderServiceUtil.createLegOrderDetails(matchOrder, matchLegOrderEntryStructs);
            }
            else
            {
                matchOrder.legOrderDetails = UserOrderServiceUtil.createLegOrderDetails(matchOrder, matchLegs);
            }
            
            sb.setLength(0);
            sb.append("A:RE:");
            sb.append(primaryOrder.orderId.executingOrGiveUpFirm.firmNumber).append(":");
            sb.append(primaryOrder.orderId.branch).append(":").append(primaryOrder.orderId.branchSequenceNumber);
            matchOrder.optionalData = sb.toString();

            // remove SendNew extension before returning the order
            removeSendNewExtensionValue(matchOrder);
            OrderStruct matchOrderReturn = UserOrderServiceUtil.processNewOrder(matchOrder);
            resultStructV2.matchOrderResult.order = matchOrderReturn;

            orderQueryCache.put(primaryOrderReturn);
            orderQueryCache.put(matchOrderReturn);
            failed = false;
            return resultStructV2;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
            maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
            throw cse;
        }
        catch (SystemException se)
        {
            maybe = true;
            throw se;
        }
        catch (CommunicationException ce)
        {
            maybe = true;
            throw ce;
        }
        finally
        {
            if (failed)
            {
                if (maybe)
                {
                    Log.alarm(this, "acceptInternalizationStrategyOrderNoAckV7 MAYBE failed for <" + cmiOrderId1 + " , " + cmiOrderId2 + ">");
                }
                orderQueryCache.orderEntryFailed(cmiOrderId1, maybe);
                orderQueryCache.orderEntryFailed(cmiOrderId2, maybe);
            }
        }
    }

    public CrossOrderStruct acceptCrossingOrderNoAckV7(OrderEntryStruct crossingOrder1, OrderEntryStruct crossingOrder2)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException,
                TransactionFailedException, AlreadyExistsException
    {
        boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, LoggingUtil.createOrderLog("acceptCrossingOrderNoAckV7", entityId, sessInfo, crossingOrder1.sessionNames[0]));

        OrderStruct order1 = buildOrder(crossingOrder1);
        addSendNewExtensionValue(order1, false);
        OrderStruct order2 = buildOrder(crossingOrder2);
        addSendNewExtensionValue(order2, false);

        CmiOrderIdStructContainer cmiOrderId1 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(crossingOrder1);
        orderQueryCache.addToOrderEntryCache(cmiOrderId1, crossingOrder1.productKey);
        CmiOrderIdStructContainer cmiOrderId2 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(crossingOrder2);
        orderQueryCache.addToOrderEntryCache(cmiOrderId2, crossingOrder2.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
            try
            {
                entityId = TransactionTimingUtil.setEntityID();
            }
            catch (Exception e)
            {
                Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );

            com.cboe.idl.order.CrossOrderIdStruct crossedOrder = getUserOrderHandlingService().acceptCrossingOrderV2(order1, order2);
            exceptionWasThrown = false;
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
                exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

            // Can't cache order IDs and product key because we don't have CBOE ID.
            OrderCallSnapshot.done();
            Log.information(this, LoggingUtil.createOrderLogSnapshot("acceptCrossingOrderNoAckV7", order1, entityId, sessInfo));

            order1.orderId = crossedOrder.buySideOrderId;
            order2.orderId = crossedOrder.sellSideOrderId;

            CrossOrderStruct crossOrder = new CrossOrderStruct();

            // remove SendNew extension before returning the order
            removeSendNewExtensionValue(order1);
            OrderStruct buySideOrderReturn = UserOrderServiceUtil.processNewOrder(order1);
            crossOrder.buySideOrder = buySideOrderReturn;

            // remove SendNew extension before returning the order
            removeSendNewExtensionValue(order2);
            OrderStruct sellSideOrderReturn = UserOrderServiceUtil.processNewOrder(order2);
            crossOrder.sellSideOrder = sellSideOrderReturn;

            orderQueryCache.put(buySideOrderReturn);
            orderQueryCache.put(sellSideOrderReturn);
            failed = false;
            return crossOrder;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
            maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
            throw cse;
        }
        catch (SystemException se)
        {
            maybe = true;
            throw se;
        }
        catch (CommunicationException ce)
        {
            maybe = true;
            throw ce;
        }
        finally
        {
            if (failed)
            {
                if (maybe)
                {
                    Log.alarm(this, "acceptCrossingOrderNoAckV7 MAYBE failed for <" + cmiOrderId1 + " , " + cmiOrderId2 + ">");
                }
                orderQueryCache.orderEntryFailed(cmiOrderId1, maybe);
                orderQueryCache.orderEntryFailed(cmiOrderId2, maybe);
            }
        }
    }

    public OrderIdStruct acceptStrategyOrderV7(OrderEntryStruct anOrder, LegOrderEntryStructV2[] legEntryDetailsV2)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, createOrderLog("acceptStrategyOrderV7", entityId, sessInfo, anOrder.sessionNames[0]));
        OrderStruct order = buildOrder(anOrder);
        CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(anOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId, anOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
	        try
	        {
	        	entityId = TransactionTimingUtil.setEntityID();
	        }
	        catch (Exception e)
	        {
	        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
	        }
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );


	        OrderIdStruct results = getUserOrderHandlingService().acceptStrategyOrderV7(order, legEntryDetailsV2);
	        exceptionWasThrown = false;
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
	            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

	        if (generateNewMsg())
	        {
	        	order.orderId = results;
	        	order.legOrderDetails = createLegOrderDetails(order, legEntryDetailsV2);
	        	publishNewMessage(this, internalEventChannel, order);
	        }
	        OrderCallSnapshot.done();
	        Log.information(this, createOrderLogSnapshot("acceptStrategyOrderV7", order, entityId, sessInfo) + " LEGS:" + legEntryDetailsV2.length);
	        failed = false;
	        return results;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
        	maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
        	throw cse;
        }
        catch (SystemException se)
        {
        	maybe = true;
        	throw se;
        }
        catch (CommunicationException ce)
        {
        	maybe = true;
        	throw ce;
        }
        finally
        {
        	if (failed)
        	{
        		if (maybe)
        		{
        			Log.alarm(this, "acceptStrategyOrder MAYBE failed for orderId: " + cmiOrderId);
        		}
        		orderQueryCache.orderEntryFailed(cmiOrderId, maybe);
        	}
        }
    }

    public OrderIdStruct acceptStrategyOrderCancelReplaceRequestV7(CancelRequestStruct cancelRequest, OrderEntryStruct newOrder, LegOrderEntryStructV2[] legEntryDetailsV2)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        Log.information(this, createOrderLog("acceptStrategyOrderCancelReplaceRequestV7", entityId, sessInfo, cancelRequest.sessionName));
        OrderStruct order = buildOrder(newOrder);
        CmiOrderIdStructContainer cmiOrderId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(newOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId, newOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
	        try
	        {
	        	entityId = TransactionTimingUtil.setEntityID();
	        }
	        catch (Exception e)
	        {
	        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
	        }
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderEmitPoint(), entityId, TransactionTimer.Enter );

	        OrderIdStruct results = getUserOrderHandlingService().acceptStrategyCancelReplaceV7(cancelRequest, order, legEntryDetailsV2);
	        exceptionWasThrown = false;
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderEmitPoint(), entityId,
	            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

	        if (generateNewMsg())
	        {
	        	order.orderId = results;
	        	order.legOrderDetails = createLegOrderDetails(order, legEntryDetailsV2);
	        	publishNewMessage(this, internalEventChannel, order);
	        }
	        OrderCallSnapshot.done();
	        Log.information(this, createOrderLogSnapshot("acceptStrategyOrderCancelReplaceRequestV7", order, entityId, sessInfo) + " LEGS:" + legEntryDetailsV2.length);
	        failed = false;
	        return results;
        }
        catch (org.omg.CORBA.SystemException cse)
        {
        	maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
        	throw cse;
        }
        catch (SystemException se)
        {
        	maybe = true;
        	throw se;
        }
        catch (CommunicationException ce)
        {
        	maybe = true;
        	throw ce;
        }
        finally
        {
        	if (failed)
        	{
        		if (maybe)
        		{
        			Log.alarm(this, "acceptStrategyOrderCancelReplaceRequestV7 MAYBE failed for orderId: " + cmiOrderId);
        		}
        		orderQueryCache.orderEntryFailed(cmiOrderId, maybe);
        	}
        }
    }

    public InternalizationOrderResultStruct acceptInternalizationStrategyOrderV7(OrderEntryStruct primaryOrder, LegOrderEntryStructV2[] primaryLegOrderEntryStruct,
                                                                               OrderEntryStruct matchOrder, LegOrderEntryStructV2[] matchLegOrderEntryStruct, short matchType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
    	boolean exceptionWasThrown = true;
        OrderCallSnapshot.enter();
        long entityId = 0L;
        String sessInfo = sessionManager.toString();
        OrderStruct primary = buildOrder(primaryOrder);
        OrderStruct match = buildOrder(matchOrder);
        Log.information(this, "calling acceptInternalizationStrategyOrderV7 for " + sessionManager);
        CmiOrderIdStructContainer cmiOrderId1 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(primaryOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId1, primaryOrder.productKey);
        CmiOrderIdStructContainer cmiOrderId2 = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(matchOrder);
        orderQueryCache.addToOrderEntryCache(cmiOrderId2, matchOrder.productKey);
        boolean failed = true;
        boolean maybe = false;
        try
        {
	        try
	        {
	        	entityId = TransactionTimingUtil.setEntityID();
	        }
	        catch (Exception e)
	        {
	        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
	        }
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );

	        InternalizationOrderResultStruct result = getUserOrderHandlingService().acceptInternalizationStrategyOrderV7(primary, primaryLegOrderEntryStruct, match, matchLegOrderEntryStruct, matchType );
	        exceptionWasThrown = false;
	        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
	            exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

	        if (generateNewMsg())
	        {
	            if (0 == result.primaryOrderResult.result.errorCode)
	            {
	            	primary.orderId = result.primaryOrderResult.orderId;
	            	primary.legOrderDetails = createLegOrderDetails(primary, primaryLegOrderEntryStruct);
	                primary.optionalData = "A:AIM";
	                publishNewMessage(this, internalEventChannel, primary);
	            }

	            if (0 == result.matchOrderResult.result.errorCode)
	            {
	            	match.orderId = result.matchOrderResult.orderId;
	            	match.legOrderDetails = createLegOrderDetails(match, matchLegOrderEntryStruct);
	            	StringBuilder s =  new StringBuilder();
	            	s.append("A:RE:");
	            	s.append(primary.orderId.executingOrGiveUpFirm.firmNumber).append(":");
	            	s.append(primary.orderId.branch).append(":").append(primary.orderId.branchSequenceNumber);
	            	matchOrder.optionalData = s.toString();
	                publishNewMessage(this, internalEventChannel, match);
	            }
	        }
	        OrderCallSnapshot.done();
	        Log.information(this, createOrderLogSnapshot("acceptInternalizationStrategyOrderV7", primary, entityId, sessInfo) + " LEGS:" + primaryLegOrderEntryStruct.length);
	        failed = false;
	        return result;
	    }
        catch (org.omg.CORBA.SystemException cse)
        {
        	maybe = cse.completed.value() == org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE;
        	throw cse;
        }
	    catch (SystemException se)
	    {
	    	maybe = true;
	    	throw se;
	    }
	    catch (CommunicationException ce)
	    {
	    	maybe = true;
	    	throw ce;
	    }
	    finally
	    {
	    	if (failed)
	    	{
	    		if (maybe)
	    		{
	    			Log.alarm(this, "acceptInternalizationStrategyOrderV7 MAYBE failed for <" + cmiOrderId1 + " , " + cmiOrderId2 + ">");
	    		}
	    		orderQueryCache.orderEntryFailed(cmiOrderId1, maybe);
	    		orderQueryCache.orderEntryFailed(cmiOrderId2, maybe);
	    	}
	    }
    }

/***********  End OrderEntryV7 Methods  ***************/

    /**
     * Adds value to extensions of an order to designate it as needing a new order ack
     * @param order the order to add value to
     * @param send whether or not to send a new order ack for this order
     */
    private void addSendNewExtensionValue(OrderStruct order, boolean send)
    {
        try
        {
            ExtensionsHelper eh = new ExtensionsHelper(order.extensions);
            eh.setValue(InternalExtensionFields.SEND_NEW, Boolean.valueOf(send).toString());
            order.extensions = eh.toString();
        }
        catch(java.text.ParseException pe)
        {
            Log.information("mapExtensionFields ParseError during addSendNewExtensionValue");
            Log.exception(pe);
        }
    }

    private void removeSendNewExtensionValue(OrderStruct order)
    {
        try
        {
            ExtensionsHelper eh = new ExtensionsHelper(order.extensions);
            eh.removeKey(InternalExtensionFields.SEND_NEW);
            order.extensions = eh.toString();
        }
        catch(java.text.ParseException pe)
        {
            Log.information("mapExtensionFields ParseError during removeSendNewExtensionValue");
            Log.exception(pe);
        }
    }

    public LightOrderResultStruct acceptLightOrder(LightOrderEntryStruct orderEntryStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        return  getUserOrderHandlingService().acceptLightOrder(orderEntryStruct);
    }


    public LightOrderResultStruct acceptLightOrderCancelRequest(
            String branch,
            int branchSequenceNumber,
            int productKey,
            String activeSession,
            String userAssignedCancelId) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        return getUserOrderHandlingService().acceptLightOrderCancelRequest(branch,branchSequenceNumber,productKey,activeSession,userAssignedCancelId);
    }

    public LightOrderResultStruct acceptLightOrderCancelRequestById(int orderHighId, int orderLowId, int productKey, String activeSession, String userAssignedCancelId) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        return getUserOrderHandlingService().acceptLightOrderCancelRequestById(orderHighId,orderLowId,productKey,activeSession,userAssignedCancelId);
    }


}
