package com.cboe.proxy.businessServicesClient;

import java.util.concurrent.Future;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.TransactionTimingRegistration;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.client.util.ClientFederatedServiceHelper;
import com.cboe.domain.util.TradingSessionNameHelper;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.businessServices.OrderHandlingService;
import com.cboe.idl.cmiErrorCodes.CommunicationFailureCodes;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.PendingOrderStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.constants.ServerResponseCodes;
import com.cboe.idl.order.CrossOrderIdStruct;
import com.cboe.idl.order.ManualCancelReportStruct;
import com.cboe.idl.order.ManualCancelRequestStruct;
import com.cboe.idl.order.ManualFillStruct;
import com.cboe.idl.order.ManualFillStructV2;
import com.cboe.idl.order.ManualMarketBrokerDataStruct;
import com.cboe.idl.order.OrderHandlingInstructionStruct;
import com.cboe.idl.order.OrderQueryResultStruct;
import com.cboe.idl.order.OrderRoutingParameterStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.util.ServerResponseStruct;
import com.cboe.idl.util.ServerResponseStructV2;
import com.cboe.idl.quote.ManualQuoteStruct;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;
import com.cboe.util.ExceptionBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.client.util.ClientFederatedServiceHelper;
import com.cboe.domain.util.TradingSessionNameHelper;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.businessServices.OrderHandlingService;
import com.cboe.idl.cmiErrorCodes.CommunicationFailureCodes;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.PendingOrderStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.constants.ServerResponseCodes;
import com.cboe.idl.order.CrossOrderIdStruct;
import com.cboe.idl.order.ManualFillStruct;
import com.cboe.idl.order.OrderQueryResultStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.util.ServerResponseStruct;
import com.cboe.idl.util.ServerResponseStructV2;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;
import com.cboe.util.ExceptionBuilder;

/**
 * This class is a routing proxy that delgates the incoming requests
 * to the appropriate order handling service. The class maintains a table which
 * maps every service to its respective process ( route ).
 *
 * @date January 12, 2009
 * @athor Gijo Joseph 
 * 		  10/21/09: modified some federated calls to go to only the BCs corresponding to the enabled sessions. 
 */

public 	class OrderHandlingServiceClientRoutingProxy extends NonGlobalServiceClientRoutingProxy
	    implements com.cboe.interfaces.businessServices.OrderHandlingService
{
    
	/*protected Object createDecoratorForOutboundCalls(Object anObjectReference)
	{
		return new com.cboe.proxy.businessServices.OrderHandlingServiceRoutingProxyDecoratorInterceptor(anObjectReference);
	}*/

	private int GET_ORDERS_QUERY_POOL_SIZE;
    private static final int OMT_LOGIN_MSGPUBLISHER_THREADS = 5;
	private	long replyHandlerTimeout;

    private Map<String, ExecutorService> omtLoginMsgPublishers;
    private ClientQueryOrderByIdExecutor queryOrderByIdExecutor;
    private ClientQueryOrderByIdExecutor queryOrderByORSIdExecutor;        
    private List<String> serviceRoutes;
    
    private final boolean orderByIdQueryRouteFilterEnabled;
    private final String orderByIdQueryRouteFilter;
    private static final String ORDER_BY_ID_ROUTE_FILTER_STRING = "orderByIdQueryRouteFilter";
    private static final String CANCEL_ORDERS = "cancelOrderForUsers";
    private static final String GET_ORDERS = "getOrderCountForUsers";
	
    /**
     * constructor
    */
    public OrderHandlingServiceClientRoutingProxy( int maxThreadPoolSize )
    {        
        super();
        GET_ORDERS_QUERY_POOL_SIZE = maxThreadPoolSize;
    	 
        orderByIdQueryRouteFilter = System.getProperty(ORDER_BY_ID_ROUTE_FILTER_STRING);
        if (orderByIdQueryRouteFilter == null || orderByIdQueryRouteFilter.length() == 0)
        {
        	orderByIdQueryRouteFilterEnabled = false;
        }
        else
        {
        	orderByIdQueryRouteFilterEnabled = true;
        }
        StringBuilder sb = new StringBuilder(orderByIdQueryRouteFilter.length()+85);
        sb.append("OrderHandlingServiceClientRoutingProxy>>>orderByIdQueryRouteFilterEnabled: ")
          .append(orderByIdQueryRouteFilterEnabled).append(" ").append(orderByIdQueryRouteFilter);
        Log.information(this, sb.toString());
    }

    public void initialize()
    {        
        super.initialize();
        initializeOrderForDestinationThreadPool();
    }
        
    /**
     * Initializes the Thread Pool for publishing order for destination requests.
     */
    public synchronized void initializeOrderForDestinationThreadPool()
    {        
        this.serviceRoutes = ClientFederatedServiceHelper.getServiceRoutes(uniqueServiceRefByProcess);
        queryOrderByIdExecutor = new ClientQueryOrderByIdExecutor(GET_ORDERS_QUERY_POOL_SIZE);
        queryOrderByORSIdExecutor = new ClientQueryOrderByIdExecutor(GET_ORDERS_QUERY_POOL_SIZE);
        omtLoginMsgPublishers = new ConcurrentHashMap<String, ExecutorService>();
        for (String aServiceRoute : serviceRoutes)
        {
            ExecutorService anOmtLoginMsgPublisher = Executors.newFixedThreadPool(OMT_LOGIN_MSGPUBLISHER_THREADS);
            omtLoginMsgPublishers.put(aServiceRoute, anOmtLoginMsgPublisher);
        }
    }

    /**
     * Initialize the timeout property for the reply handler from configuration property file
     *
     * @param name
     */
    public void create( String name )
    {        
    	super.create( name );
        replyHandlerManager = new OrderHandlingServiceClientReplyHandlerManager();
        ConfigurationService configService = FoundationFramework.getInstance().getConfigService();
    	try 
    	{
	        replyHandlerTimeout = configService.getLong(".replyHandlerTimeOut");
        }
        catch ( Exception e ) 
        {
	        Log.information( this, e.getMessage() );
	        Log.information( this, "Reply Handler timeout defaulting to 10 ms");
	        replyHandlerTimeout = 10;
        }
 }

    /**
     * Forwards request to delegate
     *
     * @param buyCrossingOrder,
     * @param sellCrossingOrder
     */
    public void acceptCrossingOrder(OrderStruct buyCrossingOrder, OrderStruct sellCrossingOrder)
            throws  SystemException,
    		CommunicationException,
    		DataValidationException,
    		AuthorizationException,
    		NotAcceptedException,
    		AlreadyExistsException,
    		TransactionFailedException
    {     
    	
    	long buyCrossingOrderid = TransactionTimingUtil.generateOrderMetricId(buyCrossingOrder.orderId, buyCrossingOrder.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, buyCrossingOrderid, 0);

        long sellCrossingOrderid = TransactionTimingUtil.generateOrderMetricId(sellCrossingOrder.orderId, sellCrossingOrder.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, sellCrossingOrderid, 0);

        String sessionName = buyCrossingOrder.sessionNames[0];
        int productKey = buyCrossingOrder.productKey;
        
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for cross order before server call.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
      	
        try
        {
        	OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName,productKey);
            service.acceptCrossingOrder( buyCrossingOrder, sellCrossingOrder );
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, buyCrossingOrderid, 1);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, sellCrossingOrderid, 1);
            exceptionWasThrown = false;
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptCrossingOrder Failed for pKey:" + buyCrossingOrder.productKey);
        }
        finally
      	{
      		// exit TTE emitpoint for cross order after server call.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
    }

    /**
     *
     */
    public CrossOrderIdStruct acceptCrossingOrderV2(OrderStruct p_buyCrossingOrder, OrderStruct p_sellCrossingOrder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException 
    {
    	long buyCrossingOrderid  = TransactionTimingUtil.generateOrderMetricId(p_buyCrossingOrder.orderId, p_buyCrossingOrder.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, buyCrossingOrderid, 0);
        long sellCrossingOrderid = TransactionTimingUtil.generateOrderMetricId(p_sellCrossingOrder.orderId,p_sellCrossingOrder.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, sellCrossingOrderid, 0);
        long entityId = 0L;
        
        try
        {
            entityId = TransactionTimingUtil.getEntityID();
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }

    	String sessionName = p_buyCrossingOrder.sessionNames[0];
        int productKey = p_buyCrossingOrder.productKey;

        boolean exceptionWasThrown = true;
        try
        {       	
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName,productKey);
            CrossOrderIdStruct crossOrderIdStruct = service.acceptCrossingOrderV2( p_buyCrossingOrder, p_sellCrossingOrder);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, buyCrossingOrderid, 1);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, sellCrossingOrderid, 1);
            exceptionWasThrown = false;
            return crossOrderIdStruct;
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptCrossingOrderV2 Failed for pKey:" +p_buyCrossingOrder.productKey);
        }
        finally
        {
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId,
                    exceptionWasThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);            
        }
    }

    public InternalizationOrderResultStruct acceptInternalizationStrategyOrders(
            OrderStruct primaryOrder, LegOrderEntryStruct[] p_primayLegEntries,
            OrderStruct matchOrder, LegOrderEntryStruct[] p_matchlegEntries,
            short matchType) throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException,
            NotAcceptedException, TransactionFailedException
    {   
    	long buyCrossingOrderid  = TransactionTimingUtil.generateOrderMetricId(primaryOrder.orderId, primaryOrder.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, buyCrossingOrderid, 0);
        long sellCrossingOrderid = TransactionTimingUtil.generateOrderMetricId(matchOrder.orderId,matchOrder.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, sellCrossingOrderid, 0);
        
    	
        String sessionName = primaryOrder.sessionNames[0];
        int productKey = primaryOrder.productKey;
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        InternalizationOrderResultStruct internalizationOrderResultStruct = null;
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for acceptInternalizationStrategyOrders before server call.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
      	
        try
        {
        	OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey );
            internalizationOrderResultStruct = service.acceptInternalizationStrategyOrders(primaryOrder, p_primayLegEntries, matchOrder, p_matchlegEntries, matchType);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, buyCrossingOrderid, 1);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, sellCrossingOrderid, 1);
            exceptionWasThrown = false;
            
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptInternalizationStrategyOrders Failed for order branch:seq#::" + primaryOrder.orderId.branch + ":" + primaryOrder.orderId.branchSequenceNumber);
        }
        finally
        {
        	//exit TTE emitpoint for acceptInternalizationStrategyOrders after server call.
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId,
                    exceptionWasThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);            
        }
        return internalizationOrderResultStruct;
    }

    // ToDo - Vivek B - Implement this method -
    public InternalizationOrderResultStruct acceptInternalizationStrategyOrdersV2(
            OrderStruct primaryOrder, LegOrderEntryStructV2[] p_primayLegEntries,
            OrderStruct matchOrder, LegOrderEntryStructV2[] p_matchlegEntries,
            short matchType) throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException,
            NotAcceptedException, TransactionFailedException
    {
    	long buyCrossingOrderid  = TransactionTimingUtil.generateOrderMetricId(primaryOrder.orderId, primaryOrder.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, buyCrossingOrderid, 0);
        long sellCrossingOrderid = TransactionTimingUtil.generateOrderMetricId(matchOrder.orderId,matchOrder.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, sellCrossingOrderid, 0);


        String sessionName = primaryOrder.sessionNames[0];
        int productKey = primaryOrder.productKey;

        try
        {
        	OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey );
            InternalizationOrderResultStruct internalizationOrderResultStruct = service.acceptInternalizationStrategyOrdersV2(primaryOrder, p_primayLegEntries, matchOrder, p_matchlegEntries, matchType);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, buyCrossingOrderid, 1);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, sellCrossingOrderid, 1);
            return internalizationOrderResultStruct;
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptInternalizationStrategyOrders Failed for order branch:seq#::" + primaryOrder.orderId.branch + ":" + primaryOrder.orderId.branchSequenceNumber);
        }
    }


    public InternalizationOrderResultStruct acceptInternalizationOrders(OrderStruct primaryOrder, OrderStruct matchOrder, short matchType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException 
    {   
    	long buyCrossingOrderid  = TransactionTimingUtil.generateOrderMetricId(primaryOrder.orderId, primaryOrder.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, buyCrossingOrderid, 0);
        long sellCrossingOrderid = TransactionTimingUtil.generateOrderMetricId(matchOrder.orderId,matchOrder.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, sellCrossingOrderid, 0);
        long entityId = 0L;
        try
        {
            entityId = TransactionTimingUtil.getEntityID();
            
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }        
    	
        String sessionName = primaryOrder.sessionNames[0];
        int productKey = primaryOrder.productKey;       
        boolean exceptionWasThrown = true;
        InternalizationOrderResultStruct internalizationOrderResultStruct = null;
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey );
            internalizationOrderResultStruct = service.acceptInternalizationOrders(primaryOrder, matchOrder, matchType);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, buyCrossingOrderid, 1);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, sellCrossingOrderid, 1);
            exceptionWasThrown = false;
            
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptInternalizationOrders Failed for order branch:seq#::" + primaryOrder.orderId.branch + ":" + primaryOrder.orderId.branchSequenceNumber);
        }
        finally
        {
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId,
                    exceptionWasThrown ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);
        }
        return internalizationOrderResultStruct;
    }

        /**
         * Routes an acceptCancel request to the OrderHandlingService available
     *
         * @param userId
         * @param cancelRequest
         * @param productKeys
         */
    public void acceptCancel(String userId, CancelRequestStruct cancelRequest, ProductKeysStruct productKeys)
            throws  SystemException,
    		        CommunicationException,
    		        DataValidationException,
    		        TransactionFailedException,
    		        NotAcceptedException,
    		        AuthorizationException
    {
    	long id = TransactionTimingUtil.generateOrderMetricId(cancelRequest.orderId, cancelRequest.sessionName);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_CANCEL_ORDER_COLLECTOR_TYPE, id, 0);
 
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for cancel request before server call.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
        try
        {
            OrderHandlingService service = (OrderHandlingService)
                    getServiceByProduct(cancelRequest.sessionName, productKeys.productKey );
            service.acceptCancel( userId, cancelRequest, productKeys );
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_CANCEL_ORDER_COLLECTOR_TYPE, id, 1);
            exceptionWasThrown = false;
            
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptCancel Failed for user:pKey::" + userId + ":" + productKeys.productKey);
        }
      	finally
      	{
      		// exit TTE emitpoint for cancel request after server call.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
    }
    
    
    public boolean acceptCancelV2(String userId, CancelRequestStruct cancelRequest, ProductKeysStruct productKeys)
    throws  SystemException,
	        CommunicationException,
	        DataValidationException,
	        TransactionFailedException,
	        NotAcceptedException,
	        AuthorizationException
	        {
    	        if (Log.isDebugOn())
    	        {
    	        	Log.debug("OrderHandlingServiceClientRoutingProxy: calling acceptCancelV2.");
    	        }
				long id = TransactionTimingUtil.generateOrderMetricId(cancelRequest.orderId, cancelRequest.sessionName);
				TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_CANCEL_ORDER_COLLECTOR_TYPE, id, 0);
				
				long entityId = 0L;
				boolean exceptionWasThrown = true; 
				boolean serverPublishCancelReport = false;
				try
				{
					entityId = TransactionTimingUtil.getEntityID();
				}
				catch (Exception e)
				{
					Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
				}
				//enter TTE emitpoint for cancel request before server call.
				TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
				try
				{
				    OrderHandlingService service = (OrderHandlingService)
				            getServiceByProduct(cancelRequest.sessionName, productKeys.productKey );
				    serverPublishCancelReport = service.acceptCancelV2( userId, cancelRequest, productKeys );
				    TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_CANCEL_ORDER_COLLECTOR_TYPE, id, 1);
				    exceptionWasThrown = false;
				}
				catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
				{
				    throw convertToNotAcceptedException(e, "acceptCancel Failed for user:pKey::" + userId + ":" + productKeys.productKey);
				}
					finally
				{
						// exit TTE emitpoint for cancel request after server call.
						TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelOrderRoutingProxyEmitPoint(), entityId,
						        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
				}
				return serverPublishCancelReport;
	 		}


    /**
     * Delegate the cancel order request to Order Handling Service.
     *
     * @param routingParams           - Routing info
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param userIds                - A <code>java.lang.String</code> array of userIds whose Order need  be cancelled
     * @param transactionId          - The unique identifier for the request, as defined by the SAGUI (used to to identify this request).
     * @param timestamp              - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties             - The <code>KeyValueStruct</code> instance
     * @param operationType          - Type of Operation
     * @return Returns ServerResponseStruct
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws SystemException
     * @throws AuthorizationException
     */
    public ServerResponseStruct[] cancelOrderForUsers(
            RoutingParameterStruct routingParams, String userIdRequestingCancel,
            String[] userIds, String transactionId, DateTimeStruct timestamp,
            KeyValueStruct[] properties, short operationType)
        throws DataValidationException, CommunicationException, SystemException, AuthorizationException
    {
    	return executeBulkServiceRequest(routingParams, userIdRequestingCancel, userIds, transactionId, timestamp,
                                         properties, operationType, CANCEL_ORDERS);
    }

   /**
     * Delegate the  Order count request for user to Order Handling Service.
     *
     * @param routingParams           - Routing info
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param userIds                - A <code>java.lang.String</code> array of userIds whose Order Count has been requested.
     * @param transactionId          - The unique identifier for the request, as defined by the SAGUI (used to identify this request).
     * @param timestamp              - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties             - The <code>KeyValueStruct</code> instance
     * @param operationType          - Type of Operation
     * @return      Returns ServerResponseStruct
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws SystemException
     * @throws AuthorizationException
     */
   public ServerResponseStruct[] getOrderCountForUsers(
           RoutingParameterStruct routingParams, String userIdRequestingCancel,
           String[] userIds, String transactionId, DateTimeStruct timestamp,
           KeyValueStruct[] properties, short operationType)
       throws DataValidationException, CommunicationException, SystemException, AuthorizationException
    {
	    return executeBulkServiceRequest(routingParams, userIdRequestingCancel, userIds, transactionId, timestamp,
                                         properties, operationType, GET_ORDERS);
    }

/**
     * Delegate the  task [Order Cancel,Order Count] to Order Handling Service.
     *
     * @param routingParams           - Routing info
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param userIds                - A <code>java.lang.String</code> array of userIds whose [Order Cancel,Order Count] has been requested.
     * @param transactionId          - The unique identifier for the request, as defined by the SAGUI (used to identify this request).
     * @param timestamp              - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties             - The <code>KeyValueStruct</code> instance
     * @param operationType          - The operation type
     * @param requestType            - Type of request[Cancel order,Count Order]
     * @return      Returns ServerResponseStruct
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws SystemException
     * @throws AuthorizationException
     */
    private ServerResponseStruct[] executeBulkServiceRequest(
        RoutingParameterStruct routingParams, String userIdRequestingCancel,
        String[] userIds, String transactionId, DateTimeStruct timestamp,
        KeyValueStruct[] properties, short operationType, String requestType)
    {        
        List<String> serviceRoutes = ClientFederatedServiceHelper.getServiceRoutes(uniqueServiceRefByProcess);
        ServerResponseStruct[] serverResponseStruct = new ServerResponseStruct[serviceRoutes.size()];
        int index = 0;
        ServerResponseStruct[] responseStructs = null;
        for (String serviceRoute : serviceRoutes)
        {
            String exceptionMsg = ClientFederatedServiceHelper.EMPTY_STRING;
            OrderHandlingService targetService = (OrderHandlingService) uniqueServiceRefByProcess.get(serviceRoute);

            Log.information(this, new StringBuilder("Sending ").append(requestType).append(" request to service :")
                        .append(serviceRoute).toString());

            try
            {
                if (CANCEL_ORDERS.equals(requestType))
                {
                    responseStructs = targetService.cancelOrderForUsers(routingParams, userIdRequestingCancel, userIds,
                                                                         transactionId, timestamp, properties,
                                                                         operationType);
                }
                else if (GET_ORDERS.equals(requestType))
                {
                    responseStructs = targetService.getOrderCountForUsers(routingParams, userIdRequestingCancel, userIds,
                                                   transactionId, timestamp, properties, operationType);
                }
                serverResponseStruct[index] = responseStructs[0];

            }

            catch (Exception e)
            {
                //exceptionMsg = e.getMessage();
                Log.exception(this, new StringBuilder("Exception on sending ").append(requestType)
                        .append(" request to service ").append(serviceRoute).toString(), e);
            }
            finally
            {
                if (!ClientFederatedServiceHelper.isStringEmpty(exceptionMsg))
                {
                    serverResponseStruct[index] = ClientFederatedServiceHelper.getServerResponseStruct(serviceRoute,
                                                                                                 ServerResponseCodes.SYSTEM_EXCEPTION,
                                                                                                 exceptionMsg);
                }

                index++;
            }
        }
        return serverResponseStruct;
    }

    /**
     * Routes the acceptCancelReplace request to the OrderHandlingService
     */
    public OrderIdStruct acceptCancelReplace(CancelRequestStruct cancelReq, OrderStruct order )
            throws  SystemException,
                    CommunicationException,
                    DataValidationException,
                    TransactionFailedException,
                    NotAcceptedException,
                    AuthorizationException
    {
    	long id = TransactionTimingUtil.generateOrderMetricId(cancelReq.orderId, cancelReq.sessionName);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_CANCEL_ORDER_COLLECTOR_TYPE, id, 0);

        id = TransactionTimingUtil.generateOrderMetricId(order.orderId, order.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_CANCEL_REPLACE_COLLECTOR_TYPE, id, 0);

    	
        String sessionName = order.sessionNames[0];
        int productKey = order.productKey;
        
        
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        OrderIdStruct theNewOrderIdStruct = null;
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for cancel replace before server call.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey );
            theNewOrderIdStruct = service.acceptCancelReplace( cancelReq, order );
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_CANCEL_ORDER_COLLECTOR_TYPE, id, 1);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_CANCEL_REPLACE_COLLECTOR_TYPE, id, 1);
            exceptionWasThrown = false;
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptCancelReplace Failed for order branch:seq#::" + cancelReq.orderId.branch + ":" + cancelReq.orderId.branchSequenceNumber);
        }
      	finally
      	{
      		// exit TTE emitpoint for cancel replace after server call.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
      	return theNewOrderIdStruct; 
    }

    /**
     * acceptStrategyCancelReplace - Routes the acceptStrategyCancelReplace request to the
     *      OrderHandlingService
     */
    public OrderIdStruct acceptStrategyCancelReplace(CancelRequestStruct cancelReq, OrderStruct order, LegOrderEntryStruct[] legDetails)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
    	long id = TransactionTimingUtil.generateOrderMetricId(cancelReq.orderId, cancelReq.sessionName);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_CANCEL_COLLECTOR_TYPE, id, 0);

        id = TransactionTimingUtil.generateOrderMetricId(order.orderId, order.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_CANCEL_REPLACE_COLLECTOR_TYPE, id, 0);
        
    	
        String sessionName = order.sessionNames[0];
        int productKey = order.productKey;
        
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        OrderIdStruct orderIdStruct = null;
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for cancel replace before server call.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
        
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey);
            orderIdStruct = service.acceptStrategyCancelReplace(cancelReq, order, legDetails);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_CANCEL_COLLECTOR_TYPE, id, 1);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_CANCEL_REPLACE_COLLECTOR_TYPE, id, 1);
            exceptionWasThrown = false;
            
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptStrategyCancelReplace Failed for order branch:seq#::" + cancelReq.orderId.branch + ":" + cancelReq.orderId.branchSequenceNumber);
        }
        finally
      	{
      		// exit TTE emitpoint for cancel replace after server call.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelReplaceOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
        return orderIdStruct;
    }

    /**
     * acceptStrategyCancelReplaceV2 - Routes the acceptStrategyCancelReplace request to the
     *      OrderHandlingService
     */
    // ToDo - VivekB - Implement this new server method
    public OrderIdStruct acceptStrategyCancelReplaceV2(CancelRequestStruct cancelReq, OrderStruct order, LegOrderEntryStructV2[] legDetails)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        long id = TransactionTimingUtil.generateOrderMetricId(cancelReq.orderId, cancelReq.sessionName);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_CANCEL_COLLECTOR_TYPE, id, 0);

        id = TransactionTimingUtil.generateOrderMetricId(order.orderId, order.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_CANCEL_REPLACE_COLLECTOR_TYPE, id, 0);


        String sessionName = order.sessionNames[0];
        int productKey = order.productKey;

        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey);
            OrderIdStruct orderIdStruct = service.acceptStrategyCancelReplaceV2(cancelReq, order, legDetails);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_CANCEL_COLLECTOR_TYPE, id, 1);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_CANCEL_REPLACE_COLLECTOR_TYPE, id, 1);
            return orderIdStruct;
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptStrategyCancelReplace Failed for order branch:seq#::" + cancelReq.orderId.branch + ":" + cancelReq.orderId.branchSequenceNumber);
        }
    }


    public OrderStruct[] getAssociatedOrders(String requestingUserId, String sessionName, int productKey, OrderIdStruct orderId)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException 
    {
        OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey);
        return service.getAssociatedOrders(requestingUserId, sessionName, productKey, orderId );
    }

    public OrderStruct[] getOrdersByOrderTypeAndClass(String sessionName, int classKey, String requestingUserId, String[] exchanges, char[] orderTypes, short orderFlowDirection)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException 
    {        
        OrderHandlingService service = (OrderHandlingService) getServiceByClass(sessionName, classKey);
        return service.getOrdersByOrderTypeAndClass(sessionName, classKey, requestingUserId, exchanges, orderTypes, orderFlowDirection);
    }


    public OrderStruct[] getOrdersByOrderTypeAndProduct(String sessionName, int productKey, String requestingUserId, String[] exchanges, char[] orderTypes, short orderFlowDirection)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException 
    {
        OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey);
        return service.getOrdersByOrderTypeAndProduct(sessionName, productKey, requestingUserId, exchanges, orderTypes, orderFlowDirection);
    }

    /**
     * Routes the acceptOrder request to the OrderHandlingService
     *
     * @param order - Order data
     */

    public OrderIdStruct acceptOrder( OrderStruct order )
	        throws  CommunicationException,
                    DataValidationException,
			        TransactionFailedException,
		            NotAcceptedException,
			        AuthorizationException,
                    SystemException
    {  
    	OrderIdStruct orderIdStruct = null;
    	String sessionName = order.sessionNames[0];
        int productKey = order.productKey;
           	
    	long id = TransactionTimingUtil.generateOrderMetricId(order.orderId, sessionName);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, id, 0);
        
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        boolean exceptionThrown = true;
        OrderHandlingService service = null;
  		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getRoutingBCLookupEmitPoint(), entityId, TransactionTimer.Enter );
        try {
        	service =(OrderHandlingService) getServiceByProduct(sessionName, productKey );
        	exceptionThrown = false;
        }finally {
        	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getRoutingBCLookupEmitPoint(), entityId, 
      				exceptionThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );	
        }
        
        

      	try
        {
          //enter TTE emitpoint for order before server call.
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
            orderIdStruct = service.acceptOrder( order );
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_NEW_ORDER_COLLECTOR_TYPE, id, 1);
            exceptionWasThrown = false;
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptOrder Failed for order branch:seq#::" + order.orderId.branch + ":" + order.orderId.branchSequenceNumber);
        }
      	finally
      	{
      		// exit TTE emitpoint for order after server call.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
        
      	return orderIdStruct;
    }

    /**
     * Routes the acceptStrategyOrder request to the OrderHandlingService
     */
    public OrderIdStruct acceptStrategyOrder(OrderStruct order, LegOrderEntryStruct[] legDetails)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {  
    	long id = TransactionTimingUtil.generateOrderMetricId(order.orderId, order.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, id, 0);

        String sessionName = order.sessionNames[0];
        int productKey = order.productKey;
        
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        OrderIdStruct orderIdStruct = null;
        //enter TTE emitpoint for order before server call.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
      	
        
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey );
            orderIdStruct = service.acceptStrategyOrder(order, legDetails);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, id, 1);
            exceptionWasThrown = false;
            
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptStrategyOrder Failed for order branch:seq#::" + order.orderId.branch + ":" + order.orderId.branchSequenceNumber);
        }
        finally
      	{
      		// exit TTE emitpoint for order after server call.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
        return orderIdStruct;
    }

    /**
     * Routes the acceptStrategyOrderv2 request to the OrderHandlingService
     * OrderIdStruct acceptStrategyOrderV2(OrderStruct orderStruct, LegOrderEntryStructV2[] legOrderEntryStructV2s) throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException;
     */
    // ToDo - VivekB - Implement this new server method
    public OrderIdStruct acceptStrategyOrderV2(OrderStruct order, LegOrderEntryStructV2[] legDetails)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
    	long id = TransactionTimingUtil.generateOrderMetricId(order.orderId, order.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, id, 0);

        String sessionName = order.sessionNames[0];
        int productKey = order.productKey;

        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey );
            OrderIdStruct orderIdStruct = service.acceptStrategyOrderV2(order, legDetails);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_ORDER_COLLECTOR_TYPE, id, 1);
            return orderIdStruct;
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptStrategyOrder Failed for order branch:seq#::" + order.orderId.branch + ":" + order.orderId.branchSequenceNumber);
        }
    }


    /**
     * Routes the acceptUpdate request to the OrderHandlingService
     *
     * @param remainingQuantity - Remaining quantity
     * @param order - Order data
     */

    public void acceptUpdate( int remainingQuantity, OrderStruct order)
        throws  CommunicationException,
		        DataValidationException,
		        TransactionFailedException,
		        NotAcceptedException,
		        AuthorizationException,
		        SystemException
    {        
    	long id = TransactionTimingUtil.generateOrderMetricId(order.orderId, order.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_ORDER_UPDATE_COLLECTOR_TYPE, id, 0);

        String sessionName = order.sessionNames[0];
        int productKey = order.productKey;
        
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for update order before server call.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUpdateOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
      	
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey );
            service.acceptUpdate( remainingQuantity, order );
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_ORDER_UPDATE_COLLECTOR_TYPE, id, 1 );
            exceptionWasThrown = false;
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptUpdate Failed for order branch:seq#::" + order.orderId.branch + ":" + order.orderId.branchSequenceNumber);
        }
        finally
      	{
      		// exit TTE emitpoint for update order after server call.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUpdateOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
    }

    /**
     * Routes the acceptUpdate request to the OrderHandlingService
     */
    public void acceptStrategyUpdate(int remainingQuantity, OrderStruct order, LegOrderEntryStruct[] legDetails)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
    	long id = TransactionTimingUtil.generateOrderMetricId(order.orderId, order.sessionNames[0]);
        TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_UPDATE_COLLECTOR_TYPE, id, 0);

        String sessionName = order.sessionNames[0];
        int productKey = order.productKey;
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for update order before server call.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUpdateOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
      	
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey );
            service.acceptStrategyUpdate(remainingQuantity, order, legDetails);
            TransactionTimingUtil.sendMethodEvent(TransactionTimingRegistration.FE_STRATEGY_UPDATE_COLLECTOR_TYPE, id, 1);
            exceptionWasThrown = false;
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptStrategyUpdate Failed for order branch:seq#::" + order.orderId.branch + ":" + order.orderId.branchSequenceNumber);
        }
        finally
      	{
      		// exit TTE emitpoint for update order after server call.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUpdateOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
    }

    /**
     * Adds elements of order struct array to a list
     */
    private void addOrdersToList(ArrayList combinedOrders, OrderStruct[] currentResult) 
    {        
        combinedOrders.ensureCapacity(combinedOrders.size() + currentResult.length);
        for (OrderStruct o : currentResult)
        {
            combinedOrders.add(o);
        }
    }

    /**
     * Routes a getOrderById request to every OrderHandlingService available
     * across different sessions. Throws a NotFoundException if all requests
     * return with the same exception. Exits after the first service returns
     * successfully or an exception other than a NotFoundException is thrown.
     */
    public OrderStruct getOrderById(String userId, OrderIdStruct order)
        throws  CommunicationException,
                DataValidationException,
                NotFoundException,
                AuthorizationException,
                SystemException
    {
        // changed the async calls to sync calls

        boolean serviceNotFound = false;
        String serviceNotFoundMessage = "";

    	Set<String> enabledSessions = 
    		ServicesHelper.getUserEnablementHome().getUserEnablement(userId).getSessionsWithAnyEnablements();
        for (String serviceRoute : uniqueServiceRefByProcess.keySet() )
        {
        	StringTokenizer st = new StringTokenizer(serviceRoute, ":");
            String strSessionName = st.nextToken();
        	if (enabledSessions.contains(strSessionName))
        	{
                if (!orderByIdQueryRouteFilterEnabled || serviceRoute.indexOf(orderByIdQueryRouteFilter) >= 0)
                {
                    if (Log.isDebugOn())
                    {
	                    Log.debug(this, "Sending request to service (getOrderById): " + serviceRoute);
	                }
	                OrderHandlingService targetService = (OrderHandlingService) uniqueServiceRefByProcess.get(serviceRoute);
                    try
                    {
	                    return targetService.getOrderById(userId, order);
                    }
                    catch (com.cboe.exceptions.NotFoundException notfe)
                    {
                        if (Log.isDebugOn())
                        {
	                        Log.debug(this, "(getOrderById, NotFoundException) Error sending request to service: " + serviceRoute);
	                    }
                    }
                    catch (com.cboe.exceptions.DataValidationException e)
                    {
                        if (Log.isDebugOn())
                        {
	                        Log.debug(this, "(getOrderById, DataValidationException) Error on Sending request to service " + serviceRoute);
	                    }
                    }
                    catch (com.cboe.exceptions.CommunicationException e)
                    {
                        if (Log.isDebugOn())
                        {
	                        Log.debug(this, "(getOrderById, CommunicationException) Failure on Sending request to service " + serviceRoute);
	                    }
                    }
                    catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
                    {
	                    serviceNotFound = true;
	                    serviceNotFoundMessage = serviceNotFoundMessage + " Object not found for service: " + serviceRoute;
	                }
	            }
        	}
        }

        if (serviceNotFound)
        {
            if (order.branchSequenceNumber > 0)
            {
                Log.alarm(this,
                        "(getOrderById, serviceNotFound), No order had been found for user:branch:seq#::" + userId + ":" + order.branch + ":" + order.branchSequenceNumber + " with error:" + serviceNotFoundMessage);
            }
            else
            {
                Log.alarm(this,
                        "(getOrderById, serviceNotFound), No order had been found for user:high:low::" + userId + ":" + order.highCboeId + ":" + order.lowCboeId + " with error:" + serviceNotFoundMessage);
            }
            throw ExceptionBuilder.communicationException("getOrderById, No order has been found due to:" + serviceNotFoundMessage, CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        else
        {
            if (order.branchSequenceNumber > 0)
            {
                Log.alarm(this,
                        "(getOrderById, serviceNotFound), No order had been found for user:branch:seq#::" + userId + ":" + order.branch + ":" + order.branchSequenceNumber);
            }
            else
            {
                Log.alarm(this,
                        "(getOrderById, serviceNotFound), No order had been found for user:high:low::" + userId + ":" + order.highCboeId + ":" + order.lowCboeId);
            }
            throw ExceptionBuilder.notFoundException("getOrderById, No order has been found", 0);

        }

    }

    /**
     * Routes a getOrderByIdV2 request to every OrderHandlingService available
     * across different sessions. Throws a NotFoundException if all requests
     * return with the same exception. Exits after the first service returns
     * successfully or an exception other than a NotFoundException is thrown.
     */
    public OrderStruct getOrderByIdV2(String userId, OrderIdStruct order) throws CommunicationException, DataValidationException, NotFoundException, AuthorizationException, SystemException
    {
    	Set<String> enabledSessions = 
    		ServicesHelper.getUserEnablementHome().getUserEnablement(userId).getSessionsWithAnyEnablements();
        List<Future<OrderStruct>> services = dispatchQueryToAllOHS(userId, order,ClientQueryOrderByIdTypes.QUERY_ORDER_BY_ID_V2, enabledSessions);
        
        for (Future<OrderStruct> theService : services)
        {        	
            try 
            {
            	OrderStruct theOrderStruct = queryOrderByORSIdExecutor.getResult(theService);
                if ( theOrderStruct != null) 
                {
                    return theOrderStruct;
                }
            }
            catch (Exception exp) 
            {
                String msg = exp.getMessage();
                if (msg != null && msg.contains("AuthorizationException")) 
                {
                    throw ExceptionBuilder.authorizationException(msg, 0);
                }
                else 
                {
                    StringBuilder orderIdName = new StringBuilder();
                    if (userId != null && !userId.equals(""))
                    {
                        orderIdName.append(userId).append(':');
                    }
                    if (order.branchSequenceNumber > 0)
                    {
                        orderIdName.append(order.branch).append(':').append(order.branchSequenceNumber);
                    }
                    else
                    {
                        orderIdName.append(order.highCboeId).append(':').append(order.lowCboeId);
                    }

                    Log.alarm(this, "getOrderByIdV2, No order has been found (" + msg + ") "
                        + orderIdName);
	            }
        	}
        }
        
        throw ExceptionBuilder.notFoundException("getOrderByIdV2, No order has been found", 0);
    }
        
    private List<Future<OrderStruct>> dispatchQueryToAllOHS(String userId, OrderIdStruct order, ClientQueryOrderByIdTypes queryType, Set<String> enabledSessions) throws  CommunicationException,
                    DataValidationException, NotFoundException, AuthorizationException, SystemException
    {        
        boolean serviceNotFound = false;
        String serviceNotFoundMessage = "";

        List<Future<OrderStruct>> proxies = new ArrayList<Future<OrderStruct>>();

        for (String serviceRoute : uniqueServiceRefByProcess.keySet())
        {
        	StringTokenizer st = new StringTokenizer(serviceRoute, ":");
            String strSessionName = st.nextToken();
        	if (enabledSessions.contains(strSessionName))
        	{
	            if(!orderByIdQueryRouteFilterEnabled || serviceRoute.indexOf(orderByIdQueryRouteFilter)>=0)
	            {
	                if (Log.isDebugOn())
	                {
	                    Log.debug( this, "Sending request to service (getOrderByIdV2): " + serviceRoute );
	                }
	                OrderHandlingService targetService = (OrderHandlingService)uniqueServiceRefByProcess.get(serviceRoute);
	                try
	                {
	                    proxies.add(queryOrderByIdExecutor.query(targetService, userId, order, queryType));
	                }
	                catch (com.cboe.exceptions.NotFoundException notfe)
	                {
	                    if (Log.isDebugOn())
	                    {
	                        Log.debug( this, "(getOrderByIdV2, NotFoundException) Error sending request to service: " + serviceRoute );
	                    }
	                }
	                catch (com.cboe.exceptions.DataValidationException e)
	                {
	                    if (Log.isDebugOn())
	                    {
	                        Log.debug( this, "(getOrderByIdV2, DataValidationException) Error on Sending request to service " + serviceRoute );
	                    }
	                }
	                catch (com.cboe.exceptions.CommunicationException e)
	                {
	                    if (Log.isDebugOn())
	                    {
	                        Log.debug( this, "(getOrderByIdV2, CommunicationException) Failure on Sending request to service " + serviceRoute);
	                    }
	                }
	                catch (com.cboe.exceptions.AuthorizationException e)
	                {
	                    if (Log.isDebugOn())
	                    {
	                        Log.debug( this, "(getOrderByIdV2, AuthorizationException) Failure on Sending request to service " + serviceRoute);
	                    }
	                    throw e;
	                }                    
	                catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
	                {
	                    serviceNotFound = true;
	                    serviceNotFoundMessage = serviceNotFoundMessage + " Object not found for service: " + serviceRoute;
	                }
                    catch (Exception exp)
                    {
	                    if (Log.isDebugOn())
	                    {
	                        Log.exception(exp);
	                        Log.debug( this, "(getOrderByIdV2, Exception) Failure on Sending request to service " + serviceRoute);
	                    }
	                }
	            }
        	}
        }

        if(serviceNotFound)
        {
            if (order.branchSequenceNumber > 0)
            {
                Log.alarm(this, "(getOrderByIdV2, serviceNotFound), No order had been found for user:branch:seq#::" + userId + ":"  + order.branch + ":" + order.branchSequenceNumber + " with error:" +  serviceNotFoundMessage);
            }
            else
            {
                Log.alarm(this, "(getOrderByIdV2, serviceNotFound), No order had been found for user:high:low::" + userId + ":"  + order.highCboeId + ":" + order.lowCboeId + " with error:" +  serviceNotFoundMessage);
            }
            throw ExceptionBuilder.communicationException("getOrderByIdV2, No order has been found due to:" + serviceNotFoundMessage, CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
//            else
//            {
//                if (order.branchSequenceNumber > 0)
//                    Log.alarm(this, "(getOrderByIdV2, serviceNotFound), No order had been found for user:branch:seq#::" + userId + ":"  + order.branch + ":" + order.branchSequenceNumber);
//                else
//                    Log.alarm(this, "(getOrderByIdV2, serviceNotFound), No order had been found for user:high:low::" + userId + ":"  + order.highCboeId + ":" + order.lowCboeId);
//                throw ExceptionBuilder.notFoundException("getOrderByIdV2, No order has been found", 0);
//            }
        return proxies;
        
    }

        
    private List<Future<OrderStruct>> dispatchQueryToAllOHS(String userId, String orsId) throws  CommunicationException, DataValidationException, NotFoundException, AuthorizationException, SystemException
    {
    // changed the async calls to sync calls

        String serviceRoute = null;
        boolean serviceNotFound = false;
        String serviceNotFoundMessage = "";
        Iterator keys = uniqueServiceRefByProcess.keySet().iterator();
        List<Future<OrderStruct>> proxies = new ArrayList<Future<OrderStruct>>();        
        while ( keys.hasNext() )
        {
            serviceRoute = (String)keys.next();
            if(!orderByIdQueryRouteFilterEnabled || (orderByIdQueryRouteFilterEnabled && serviceRoute.indexOf(orderByIdQueryRouteFilter)>=0))
	            {
	                if (Log.isDebugOn())
	                {
	                    Log.debug( this, "Sending request to service (getOrderByORSID): " + serviceRoute );
	                }
	                OrderHandlingService targetService = (OrderHandlingService)uniqueServiceRefByProcess.get(serviceRoute);
	                try
	                {
                    proxies.add(queryOrderByORSIdExecutor.query(targetService, userId, orsId, ClientQueryOrderByIdTypes.QUERY_ORDER_BY_ORSID));
	                }
	                catch (com.cboe.exceptions.NotFoundException notfe)
	                {
	                    if (Log.isDebugOn())
	                    {
	                        Log.debug( this, "(getOrderByORSID, NotFoundException) Error sending request to service: " + serviceRoute );
	                    }
	                }
	                catch (com.cboe.exceptions.DataValidationException e)
	                {
	                    if (Log.isDebugOn())
	                    {
	                        Log.debug( this, "(getOrderByORSID, DataValidationException) Error on Sending request to service " + serviceRoute );
	                    }
	                }
	                catch (com.cboe.exceptions.CommunicationException e)
	                {
	                    if (Log.isDebugOn())
	                    {
	                        Log.debug( this, "(getOrderByORSID, CommunicationException) Failure on Sending request to service " + serviceRoute);
	                    }
	                }
	                catch (com.cboe.exceptions.AuthorizationException e)
	                {
	                    if (Log.isDebugOn())
	                    {
                        Log.debug( this, "(getOrderByIdV2, AuthorizationException) Failure on Sending request to service " + serviceRoute);
	                    }
	                    throw e;
	                }                    
	                catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
	                {
	                    serviceNotFound = true;
	                    serviceNotFoundMessage = serviceNotFoundMessage + " Object not found for service: " + serviceRoute;
	                }
                catch (Exception exp)
                {
	                    if (Log.isDebugOn())
	                    {
	                        Log.debug( this, "(getOrderByORSID, Exception) Failure on Sending request to service " + serviceRoute);
	                    }
	                }
	            }
        	}

        if(serviceNotFound)
        {
           
            Log.alarm(this, "(getOrderByORSID, serviceNotFound), No order had been found for user:orsid " + userId + ":"  + orsId + " with error:" +  serviceNotFoundMessage);
            throw ExceptionBuilder.communicationException("getOrderByORSID, No order has been found due to:" + serviceNotFoundMessage, CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        
        return proxies;
    }
    
    public OrderStruct getOrderByORSID(String userId, String orsId)
    throws  CommunicationException,
            DataValidationException,
            NotFoundException,
            AuthorizationException,
            SystemException
	{
	    List<Future<OrderStruct>> services = dispatchQueryToAllOHS(userId, orsId);
	    for (Future<OrderStruct> theService : services)
	    {
            try
            {
                OrderStruct theOrderStruct = queryOrderByORSIdExecutor.getResult(theService);
                if (theOrderStruct != null)
                {
                    return theOrderStruct;
                }
            }
            catch (Exception exp)
            {
                if (exp.getMessage().contains("AuthorizationException"))
                {
	                throw ExceptionBuilder.authorizationException(exp.getMessage(), 0);
	            }
                else
                {
	                Log.alarm(this, "getOrderById, No order has been found");
	            }
	        }
	    }
	    throw ExceptionBuilder.notFoundException("getOrderByORSId, No order has been found", 0);
	}        
   
        
    /**
     * Routes a getOrderByIdForProduct request to every OrderHandlingService that
     * trades the given product across different sessions. Throws a NotFoundException if all requests
     * return with the same exception. Exits after the first service returns
     * successfully or an exception other than a NotFoundException is thrown.
     *
     * @param userId
     * @param orderId
     * @param productKey
     * @return
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws NotFoundException
     * @throws AuthorizationException
     */
    public OrderStruct getOrderByIdForProduct(String userId, OrderIdStruct orderId, int productKey)
            throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException 
    {
        boolean serviceNotFound = false;
        String serviceNotFoundMessage = "";

        ArrayList services = getServicesByProduct(productKey);
        if(services.isEmpty())
        {
            String errorString = buildErrorMsg(userId, productKey);
            String orderIdString = buildOrderIdString(orderId);

            Log.alarm(this, "(getOrderByIdForProduct, serviceNotFound) " + errorString + orderIdString);
            throw ExceptionBuilder.dataValidationException("getOrderByIdForProduct," + errorString + orderIdString, 0);
        }

        for (java.lang.Object ts : services)
        {
            OrderHandlingService targetService = null;
            try
            {
                targetService = (OrderHandlingService)ts;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "(getOrderByIdForProduct)Sending request to service " + targetService.toString());
                }
                return targetService.getOrderByIdForProduct(userId, orderId, productKey);
            }
            catch (com.cboe.exceptions.NotFoundException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrderByIdForProduct) NotFoundException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch (com.cboe.exceptions.DataValidationException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrderByIdForProduct) DataValidationException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch (com.cboe.exceptions.CommunicationException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrderByIdForProduct) CommunicationException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
            {
                serviceNotFound = true;
                serviceNotFoundMessage = serviceNotFoundMessage + " Object not found for service: " + targetService;
            }
        }

        String orderIdString = buildOrderIdString(orderId);
        String errorString = buildErrorMsg(userId, productKey);

        if(serviceNotFound)
        {
            Log.alarm(this, "(getOrderByIdForProduct, serviceNotFound)," + errorString + orderIdString +  " for user " + userId + " with error:" +  serviceNotFoundMessage);
            throw ExceptionBuilder.communicationException("getOrderByIdForProduct, No order has been found due to:" + serviceNotFoundMessage +
                    "for order " + orderIdString + " userid=" + userId, CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        else
        {
            Log.alarm(this, "(getOrderByIdForProduct, serviceNotFound), " + errorString + orderIdString);
            throw ExceptionBuilder.notFoundException("getOrderByIdForProduct, No order has been found " +
                    "for order " + orderIdString + " userid=" + userId, 0);
        }
    }

    /**
    * Routes a getOrderByIdForClass request to every OrderHandlingService that
    * trades the given class across different sessions. Throws a NotFoundException if all requests
    * return with the same exception. Exits after the first service returns
    * successfully or an exception other than a NotFoundException is thrown.
     *
    * @param userId
    * @param orderId
    * @param classKey
    * @return
    * @throws SystemException
    * @throws CommunicationException
    * @throws DataValidationException
    * @throws NotFoundException
    * @throws AuthorizationException
    */
    public OrderStruct getOrderByIdForClass(String userId, OrderIdStruct orderId, int classKey)
            throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException 
    {
        boolean serviceNotFound = false;
        String serviceNotFoundMessage = "";
        ArrayList services = getServicesByClass(classKey);
        if(services.isEmpty())
        {
            String errorString = buildErrorMsg(userId, classKey);
            String orderIdString = buildOrderIdString(orderId);

            Log.alarm(this, "(getOrderByIdForClass, serviceNotFound) " + errorString + orderIdString);
            throw ExceptionBuilder.dataValidationException("getOrderByIdForClass," + errorString, 0);
        }

        for (java.lang.Object ts : services)
        {
            OrderHandlingService targetService = null;
            try
            {
                targetService = (OrderHandlingService)ts;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "(getOrderByIdForClass)Sending request to service " + targetService.toString());
                }
                return targetService.getOrderByIdForClass(userId, orderId, classKey);
            }
            catch (com.cboe.exceptions.NotFoundException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrderByIdForClass) NotFoundException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch (com.cboe.exceptions.DataValidationException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrderByIdForClass) DataValidationException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch (com.cboe.exceptions.CommunicationException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrderByIdForClass) CommunicationException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
            {
                serviceNotFound = true;
                serviceNotFoundMessage = serviceNotFoundMessage + " Object not found for service: " + targetService;
            }
        }

        String orderIdString = buildOrderIdString(orderId);
        String errorString = buildErrorMsg(userId, classKey);

        if(serviceNotFound)
        {
            Log.alarm(this, "(getOrderByIdForClass, serviceNotFound)," + errorString + orderIdString +  " for user " + userId + " with error:" +  serviceNotFoundMessage);
            throw ExceptionBuilder.communicationException("getOrderByIdForClass, No order has been found due to:" + serviceNotFoundMessage +
                    "for order " + orderIdString + " userid=" + userId, CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
        }
        else
        {
            Log.alarm(this, "(getOrderByIdForClass, serviceNotFound), " + errorString + orderIdString);
            throw ExceptionBuilder.notFoundException("getOrderByIdClass, No order has been found " +
                    "for order " + orderIdString + " userid=" + userId, 0);
        }
    }


    public OrderStruct getOrderForProduct(String sessionName, int productKey, CboeIdStruct cboeId)
	        throws  SystemException,
        	    	CommunicationException,
        	    	DataValidationException,
        	    	NotFoundException,
        	    	AuthorizationException
    {        
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey);
            return service.getOrderForProduct(sessionName, productKey, cboeId);
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToCommunicationException(e, "Query to getOrderForProduct Failed for cboeId high:low::" + cboeId.highCboeId + ":" + cboeId.lowCboeId);
        }

    }

    /**
     *	Forwards getPendingAdjustmentOrdersByClass to the appropriate OrderHandlingService
     */
    public OrderStruct[] getPendingAdjustmentOrdersByClass(String sessionName, int classKey, String userId)
        throws	SystemException, CommunicationException, DataValidationException, AuthorizationException
    {        
        List<String> serviceRoutes = getServiceRoutes(sessionName);
        List<OrderStruct[]> orders = new ArrayList<OrderStruct[]>();
        int totalOrdersSize = 0;
        
        for (String serviceRoute : serviceRoutes)
        {
            try
            {           
                OrderHandlingService targetService = (OrderHandlingService) uniqueServiceRefByProcess.get(serviceRoute);
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Sending request to service :" + serviceRoute);
                }
                OrderStruct[] newOrders = (targetService.getPendingAdjustmentOrdersByClass(sessionName, classKey, userId));
                totalOrdersSize += newOrders.length;
                orders.add(newOrders);
            }
            catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
            {
                throw convertToCommunicationException(e, "Query to getPendingAdjustmentOrdersByClass Failed for user: classKey::" + userId + ":" + classKey);
            }
        }
        
        return getTotalOrders(orders, totalOrdersSize);
    }
        
    /**
     *	Forwards getPendingAdjustmentOrdersByProduct to the appropriate OrderHandlingService
     */
    public OrderStruct[] getPendingAdjustmentOrdersByProduct(String sessionName, ProductKeysStruct productKeys, String userId)
        throws 	SystemException, CommunicationException, AuthorizationException, DataValidationException
    {        
        List<String> serviceRoutes = getServiceRoutes(sessionName);
        List<OrderStruct[]> orders = new ArrayList<OrderStruct[]>();
        int totalOrdersSize = 0;
        
        for (String serviceRoute : serviceRoutes)
        {
            try
            {           
                OrderHandlingService targetService = (OrderHandlingService) uniqueServiceRefByProcess.get(serviceRoute);
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Sending request to service :" + serviceRoute);
                }
                OrderStruct[] newOrders = targetService.getPendingAdjustmentOrdersByProduct(sessionName, productKeys, userId);
                totalOrdersSize += newOrders.length;
                orders.add(newOrders);
            }
            catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
            {
                throw convertToCommunicationException(e, "Query to getPendingAdjustmentOrdersByProduct Failed for user:pKey::" + userId + ":" + productKeys.productKey);
            }
        }
        
        return getTotalOrders(orders, totalOrdersSize);
    }
        
    /*
     * returns valid service routes for the session.
     */
    private List<String> getServiceRoutes(String sessionName)
    {        
        List<String> serviceRoutes = new ArrayList<String>();
        for (String serviceRoute : uniqueServiceRefByProcess.keySet())
        {
            // check for not calling underlying service
            if(isValidBCCall(serviceRoute))
            {
                StringTokenizer st = new StringTokenizer(serviceRoute,":");
                String routeSessionName = st.nextToken();
                // add service route for this session.
                if(routeSessionName.equals(sessionName)) 
                {
                    serviceRoutes.add(serviceRoute);
                }
            }
        }
        return serviceRoutes;
    }

    private boolean isValidBCCall(String serviceRoute) 
    {        
        StringTokenizer st = new StringTokenizer(serviceRoute,":");
        String strSessionName = st.nextToken();
        return !TradingSessionNameHelper.isUnderlyingSession(strSessionName);
    }

    /*
     * return total order structs from list
     */
    private OrderStruct[] getTotalOrders(List<OrderStruct[]> orders, int totalOrdersSize)
    {        
        OrderStruct[] returnOrders = new OrderStruct[totalOrdersSize];            
        for (OrderStruct[] osa : orders)
        {
            totalOrdersSize = 0;  // todo is it correct to reset the sum inside the loop?
            System.arraycopy(osa, 0, returnOrders, totalOrdersSize, osa.length);
            totalOrdersSize += osa.length;
        }
        return returnOrders;
    }

    /**
     * Forwards request to all known services asynchronously. Waits for all responses
     * before returning
     */
    public com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct queryOrderHistory
            (String userId, String sessionName, int productKey, OrderIdStruct orderId)
	        throws	CommunicationException,
    	        DataValidationException,
            	AuthorizationException,
            	SystemException
    {
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey);
            return service.queryOrderHistory(userId, sessionName, productKey, orderId);
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToCommunicationException(e, "Query to queryOrderHistory Failed for user:pKey::" + userId + ":" + productKey);
        }
        catch(CommunicationException ce)
        {
        	Log.exception(this,"Query to queryOrderHistory Failed for user:pKey::" + userId + ":" + productKey, ce );
        	throw ce;
        }
        catch(DataValidationException dve)
        { 
        	Log.exception(this,"Query to queryOrderHistory Failed for user:pKey::" + userId + ":" + productKey, dve );
        	throw dve;
        }
        catch(AuthorizationException ae)
        {
        	Log.exception(this,"Query to queryOrderHistory Failed for user:pKey::" + userId + ":" + productKey, ae );
        	throw ae;
        }
        catch(SystemException se)
        {
        	Log.exception(this,"Query to queryOrderHistory Failed for user:pKey::" + userId + ":" + productKey, se );
        	throw se;
        }        
        catch (Exception e) 
        {
            Log.exception(this,"Query to queryOrderHistory Failed for user:pKey::" + userId + ":" + productKey, e);
            throw ExceptionBuilder.systemException("Query to queryOrderHistory Failed for user:pKey::" + userId + ":" + productKey, 0);
        }       
    }

    /**
     *	Forwards request to every known delegate ( service )
     */
    public void publishOrdersForFirm( com.cboe.idl.cmiUser.ExchangeFirmStruct firmId )
    {        
        OrderHandlingServiceClientReplyHandler replyHandler = (OrderHandlingServiceClientReplyHandler)replyHandlerManager.findReplyHandler();
        for (String serviceRoute : uniqueServiceRefByProcess.keySet())
        {
            if (Log.isDebugOn())
            {
	            Log.debug( this, "Forwarding request to service " + serviceRoute );
            }
	        OrderHandlingService targetService = (OrderHandlingService)uniqueServiceRefByProcess.get( serviceRoute );
	        try 
	        {
		        targetService.sendc_publishOrdersForFirm( replyHandler.getAMIHandler(), firmId );
	        }
    		catch( org.omg.CORBA.SystemException e )
    		{
                if (Log.isDebugOn())
                {
                    // todo is this redundant?
                    Log.debug( this, "Error forwarding request to service " + serviceRoute );
                }
        		Log.exception( this, "Error forwarding request to service " + serviceRoute, e );
	        }
        }
    }

    /**
     * Forwards request to every known delegate service
     */
    public void publishOrdersForUser( String userId )
    {
       final String myUserId = userId;
        new Thread()
        {
            public void run()
            {
               publishOrdersForUserThread (myUserId);
            }
        }.start();
    }

    private void publishOrdersForUserThread (String userId)
    {
        Long startTime= System.nanoTime();
           Set<String> enabledSessions =
    		ServicesHelper.getUserEnablementHome().getUserEnablement(userId).getSessionsWithAnyEnablements();
        for (String serviceRoute : uniqueServiceRefByProcess.keySet())
        {
        	StringTokenizer st = new StringTokenizer(serviceRoute, ":");
            String strSessionName = st.nextToken();
        	if (enabledSessions.contains(strSessionName))
        	{
	            if (Log.isDebugOn())
	            {
	    		    Log.debug( this, "Forwarding request to service " + serviceRoute );
	            }
	        	OrderHandlingService targetService = (OrderHandlingService)uniqueServiceRefByProcess.get( serviceRoute );
		        try 
		        {
                    StringBuilder sb = new StringBuilder(100);
                    sb.append("publishOrdersForUser: ").append(userId).append(" serviceRoute: ").append(serviceRoute);
                    Log.information(this, sb.toString());

                    targetService.publishOrdersForUser(userId);
                }
	        	catch( org.omg.CORBA.SystemException e )
	        	{
	                if (Log.isDebugOn())
	                {
	    			    Log.debug( this, "Error forwarding request to service " + serviceRoute );
	                }
	        		Log.exception( this, "Error forwarding request to service " + serviceRoute, e );
		        }
                catch (SystemException se)
                {
                    Log.exception(this, "SystemException to service " + serviceRoute, se);
                }
                catch (CommunicationException ce)
                {
                    Log.exception(this, "CommunicationException to service " + serviceRoute, ce);
                }
                catch (DataValidationException dve)
                {
                    Log.exception(this, "DataValidationException to service " + serviceRoute, dve);
                }
                catch (AuthorizationException ae)
                {
                    Log.exception(this, "AuthorizationException to service " + serviceRoute, ae);
                }
        	}
        }
        Long elapsedTime = System.nanoTime() - startTime;
        StringBuilder sb2 = new StringBuilder(100);
        sb2.append("publishOrdersForUser: ").append(userId). append(" ElapsedTime: ").append(elapsedTime).append(" ns");
        Log.information(this, sb2.toString());
    }

    /**
     * Forward the requests to every known service for the product asynchronously, and wait for all the responses
     */
    public OrderStruct[] getOrdersForProduct(String userId, int productKey)
        throws com.cboe.exceptions.SystemException,
                com.cboe.exceptions.CommunicationException,
                com.cboe.exceptions.DataValidationException,
                com.cboe.exceptions.AuthorizationException,
                com.cboe.exceptions.NotFoundException
    {
        ArrayList services = getServicesByProduct(productKey);
        OrderHandlingService targetService = null;
        if (services.size() == 1)
        {
            try
            {
                targetService = (OrderHandlingService) services.get(0);
                return targetService.getOrdersForProduct(userId, productKey);
            }
            catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
            {
                Log.alarm( this, "(getOrdersForProduct) Failure on Sending request to service " + targetService.toString());
                throw ExceptionBuilder.communicationException("getOrdersForProduct, No order has been found due to: Failure on Sending request to service " + targetService.toString(), CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
            }
        }
        ArrayList returnOrdersList = new ArrayList();
        for (java.lang.Object ts : services)
        {
            try
            {
                targetService = (OrderHandlingService)ts;
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrdersForProduct)Sending request to service " + targetService.toString() );
                }
                addOrdersToList(returnOrdersList, targetService.getOrdersForProduct(userId, productKey));
	        }
            catch (com.cboe.exceptions.NotFoundException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrdersForProduct) NotFoundException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch (com.cboe.exceptions.DataValidationException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrdersForProduct) DataValidationException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch (com.cboe.exceptions.CommunicationException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrdersForProduct) CommunicationException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
            {
                Log.exception( this, "(getOrdersForProduct) OBJECT_NOT_EXIST Failure on Sending request to service " + targetService.toString(), e );
            }
        } //end of while

        OrderStruct[] result = new OrderStruct[returnOrdersList.size()];
        returnOrdersList.toArray(result);
        return result;
    }


    /**
     * This method is not used
     */
    public	void shutdown()
    {
    }

    /**
     * Check if replyHandler is ready. If not, wait on it
     *
     * @param replyHandler
     */
     private void waitForReply(OrderHandlingServiceClientReplyHandler replyHandler)
     {
         synchronized( replyHandler )
         {
            if ( !replyHandler.isReady() )
            {
	            try 
	            {
	                replyHandler.wait();
		        }
    		    catch ( InterruptedException e )
    		    {
		            Log.exception( this, e );
			    }
            }
            if (Log.isDebugOn())
            {
  			   Log.debug( this, "Received " + replyHandler.getNumberOfResponses() + " responses" );
	           Log.debug( this, "Reveived " + replyHandler.getNumberOfExceptions() + " exceptions" );
            }
         }
     }

    /**
     * Return the Service Helper class name
     *
     * @return String, the service helper class name related to this proxy
     */
    protected String getHelperClassName()
    {
        return "com.cboe.idl.businessServices.OrderHandlingServiceHelper";
    }

    private String buildErrorMsg(String userId, int key)
    {
        StringBuilder errorString = new StringBuilder(96);

        errorString.append("No service has been found for key=").append(key);
        errorString.append(". The userId=").append(userId);

        return errorString.toString();
    }

    private String buildOrderIdString(OrderIdStruct orderId)
    {        
        StringBuilder orderIdStr = new StringBuilder(384);

        orderIdStr.append(" branch/seq/correspondent/giveup/date=");
        orderIdStr.append(orderId.branch).append('/');
        orderIdStr.append(orderId.branchSequenceNumber).append('/');
        orderIdStr.append(orderId.correspondentFirm).append('/');
        orderIdStr.append(orderId.executingOrGiveUpFirm).append('/');
        orderIdStr.append(orderId.orderDate);

        return orderIdStr.toString();
    }

    /**
     * Cancels orders  for given users according to order types (GTC,DAY,GTD)
     * ,correspondentFirm, classKeys, and currentDate as requested by Help Desk user.
     *
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param userIds                - A <code>java.lang.String</code> array of userIds whose Order need  be cancelled
     * @param transactionId          - The unique identifier for the request, as defined by the SAGUI (used to to identify this request).
     * @param timestamp              - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties             - The <code>KeyValueStruct</code> instance
     * @param operationType          - Type of Operation
     * @param orderTypes             - Either DAY, GTC, GTD
     * @param correspondentFirmValues - String array of correspondent firms
     * @param classKeys              - int array of classKeys
     * @param currentDate            - true if requested date is current or false.
     * @return ServerResponseStruct[]
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws TransactionFailedException
     * @throws NotAcceptedException
     * @throws AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    public ServerResponseStruct[] cancelOrderForUsersV2(String userIdRequestingCancel, String[] userIds,
    String transactionId, DateTimeStruct timestamp,KeyValueStruct[] properties, short operationType,
    char[] orderTypes, String[] correspondentFirmValues, int[] classKeys, boolean currentDate)
    throws DataValidationException, CommunicationException, SystemException, AuthorizationException
    {        
        return executeBulkServiceRequestV2(userIdRequestingCancel, userIds, transactionId,timestamp, properties,
                operationType, CANCEL_ORDERS, orderTypes, correspondentFirmValues,classKeys,currentDate);
    }

    /**
     * Checks for the valid BC's and delegates the cancel request to
     * corresponding BC.
     *
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param userIds                - A <code>java.lang.String</code> array of userIds whose [Order Cancel,Order Count] has been requested.
     * @param transactionId          - The unique identifier for the request, as defined by the SAGUI (used to identify this request).
     * @param timestamp              - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties             - The <code>KeyValueStruct</code> instance
     * @param operationType          - The operation type
     * @param requestType            - Type of request[Cancel order]
     * @param orderTypes             - Order type char array [G, D, T]
     * @param correspondentFirmValues - String array of correspondent firms
     * @param classKeys              - int array of classKeys
     * @param currentDate            - true if requested date is current date else false.
     * @return ServerResponseStruct[]
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws TransactionFailedException
     * @throws NotAcceptedException
     * @throws AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    private ServerResponseStruct[] executeBulkServiceRequestV2 (final String userIdRequestingCancel,
    final String[] userIds, final String transactionId, final DateTimeStruct timestamp,
    final KeyValueStruct[] properties, final short operationType, final String requestType,
    final char[] orderTypes , final String[] correspondentFirmValues, final int[] classKeys, final boolean currentDate)
    throws   SystemException, CommunicationException, DataValidationException,AuthorizationException
    {
        List<String> serviceRoutes = ClientFederatedServiceHelper.getServiceRoutes(uniqueServiceRefByProcess);
        /*
         * If request is for classKeys then get BCs for given classKeys.
         * Otherwise the normal flow.
         */
        if(classKeys.length > 0)
        {
            serviceRoutes = getBCRoutesForClasses(classKeys, serviceRoutes);
        }

        ServerResponseStruct[] serverResponseStruct = new ServerResponseStruct[serviceRoutes.size()];
        int index = 0;
        ServerResponseStruct[] responseStructs = null;

        for (String serviceRoute : serviceRoutes)
        {
            String exceptionMsg = ClientFederatedServiceHelper.EMPTY_STRING;
            OrderHandlingService targetService = (OrderHandlingService) uniqueServiceRefByProcess.get(serviceRoute);

            Log.information(this, new StringBuilder("Sending ").append(requestType).append(" request to service :")
                .append(serviceRoute).toString());
            try
            {
                responseStructs = targetService.cancelOrderForUsersV2(userIdRequestingCancel, userIds,
                                                         transactionId, timestamp, properties,
                                                         operationType, orderTypes, correspondentFirmValues, classKeys, currentDate);
                serverResponseStruct[index] = responseStructs[0];
            }
            catch (Exception e)
            {
                //exceptionMsg = e.getMessage();
                Log.exception(this, new StringBuilder("Exception on sending ").append(requestType)
                    .append(" request to service ").append(serviceRoute).toString(), e);
            }
            finally
            {
                if (!ClientFederatedServiceHelper.isStringEmpty(exceptionMsg))
                {
                    serverResponseStruct[index] = ClientFederatedServiceHelper.getServerResponseStruct(serviceRoute,
                                                                         ServerResponseCodes.SYSTEM_EXCEPTION,
                                                                         exceptionMsg);
                }
                index++;
            }
        }
        return serverResponseStruct;
    }

    /**
     * Filters out the requested BC's from the list of all valid BC's. 
     *
     * @param serviceRoutes - contains ALL BC names
     * @param serverRouteNames - Requested by HD user
     * @return List (requested BC List)
     * @author Cognizant Technology Solutions.
     */
    private List <String> filterBCRoutes (final List<String> serviceRoutes,  final String[] serverRouteNames)
    {
        ArrayList<String> requestedServerRoutesList = new ArrayList<String>(serverRouteNames.length);
        for( String serverRouteName : serverRouteNames )
        {
            inner:
            for (String serviceRoute : serviceRoutes)
            {
                /*
                 * serviceRoute is in the form of sessionName:serverName. Request comes in the form serverName.
                 * serviceRoute is split into two and later part is compared with the requested serverName.
                 */
                StringTokenizer tokenizer = new StringTokenizer(serviceRoute, ":");
                String tokenizedServiceRoute = null;
                while(tokenizer.hasMoreTokens())
                {
                    try
                    {
                        tokenizedServiceRoute = tokenizer.nextToken();
                    }
                    catch(NoSuchElementException e)
                    {
                        Log.exception(this, "Exception occurred in parsing serverRouteName.", e);
                    }
                    if(tokenizedServiceRoute.equals(serverRouteName))
                    {
                        requestedServerRoutesList.add(serviceRoute);
                        break inner; // No need to iterate further as desired serviceRoute is obtained.
                    }
                }
            }
        }
        requestedServerRoutesList.trimToSize();
        return requestedServerRoutesList;
    }

    /**
     * Finds the BCs for the given classKeys and returns a list of BCs
     * corresponding to requested classKeys.
     *
     * @param classKeys - int[] of classKeys as requested by HD user
     * @param serviceRoutes - List of BCs obtained from uniqueServiceRefByProcess
     * @return List<String> - List of BCs for requested classKeys
     * @throws DataValidationException
     * @author Cognizant Technology Solutions.
     */
    private List<String> getBCRoutesForClasses(int[] classKeys, List<String> serviceRoutes) throws DataValidationException
    {
        //BCSet-HashSet containing routeNames corresponding to requested BCs
        Set<String> BCSet = new HashSet<String>();
        for(int classKey : classKeys)
        {
            HashSet<String> routeNames = routingService.getRouteNamesByClass(classKey);
            BCSet.addAll(routeNames);
        }

        //List containing BCs
        String[] serverRouteNames = BCSet.toArray(new String[BCSet.size()]);
        //Gets only those BC as requested by the user in (sessionName:routeName) format.
        serviceRoutes = filterBCRoutes(serviceRoutes, serverRouteNames);

        return serviceRoutes;
    }

    /**
     * Cancels orders of all users on requested BCs.
     *
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param transactionId - The unique identifier for the request, as defined by the SAGUI (used to identify this request).
     * @param timestamp - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties - The <code>KeyValueStruct</code> instance
     * @param operationType - The operation type
     * @param orderTypes - Char array of type of order i.e. G,T,D for GTC,GTD, Day respectively
     * @param serverRouteNames - String array of BCs on which orders are to be canceled.
     * @param currentDate - true if requested date is current or false.
     * @return ServerResponseStruct[]
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws TransactionFailedException
     * @throws NotAcceptedException
     * @throws AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    public ServerResponseStruct[] cancelOrdersForRoutingGroups (String userIdRequestingCancel, String transactionId, DateTimeStruct timestamp, KeyValueStruct[] properties,
    short operationType, char[] orderTypes, String[] serverRouteNames, boolean currentDate)
    throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        return executeBulkServiceRequestForRoutingGroups(userIdRequestingCancel, transactionId, timestamp, properties, operationType, CANCEL_ORDERS, orderTypes, serverRouteNames, currentDate);
    }

    /**
     * Checks for the valid BC's, filters only requested BCs from all BCs
     * and delegates the cancel request to corresponding BC.
     *
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param transactionId - The unique identifier for the request, as defined by the SAGUI (used to identify this request).
     * @param timestamp - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties - The <code>KeyValueStruct</code> instance
     * @param operationType - The operation type
     * @param requestType - cancel orders
     * @param orderTypes - Char array of type of order i.e. G,T,D for GTC,GTD, Day respectively
     * @param serverRouteNames - String array of requested BCs
     * @param currentDate - true(for currentDate) and false for all dates.
     * @return ServerResponseStruct[]
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    private ServerResponseStruct[] executeBulkServiceRequestForRoutingGroups(final String userIdRequestingCancel ,final String transactionId, final DateTimeStruct timestamp,
    final KeyValueStruct[] properties,  final short operationType,  final String requestType, final char[] orderTypes, final String[] serverRouteNames,
    final boolean currentDate) throws   SystemException, CommunicationException, DataValidationException,AuthorizationException
    {
        List<String> serviceRoutes = ClientFederatedServiceHelper.getServiceRoutes(uniqueServiceRefByProcess);

        /*
         * Getting the requested BCs from list of all BCs in (sessionName:routeName) format.
         */
        serviceRoutes = filterBCRoutes(serviceRoutes, serverRouteNames);

        ServerResponseStruct[] serverResponseStruct = new ServerResponseStruct[serviceRoutes.size()];
        int index = 0;
        ServerResponseStruct[] responseStructs = null;

        for (String serviceRoute : serviceRoutes)
        {
            String exceptionMsg = ClientFederatedServiceHelper.EMPTY_STRING;
            OrderHandlingService targetService = (OrderHandlingService) uniqueServiceRefByProcess.get(serviceRoute);

            Log.information(this, new StringBuilder("Sending ").append(requestType).append(" request to service :")
                    .append(serviceRoute).toString());
            try
            {
                responseStructs = targetService.cancelOrdersForRoutingGroups(userIdRequestingCancel, transactionId, timestamp, properties, operationType, orderTypes, serverRouteNames, currentDate);
                serverResponseStruct[index] = responseStructs[0];
            }
            catch (Exception e)
            {
                //exceptionMsg = e.getMessage();
                Log.exception(this, new StringBuilder("Exception on sending ").append(requestType)
                        .append(" request to service ").append(serviceRoute).toString(), e);
            }
            finally
            {
                if (!ClientFederatedServiceHelper.isStringEmpty(exceptionMsg))
                {
                    serverResponseStruct[index] = ClientFederatedServiceHelper.getServerResponseStruct(serviceRoute,
                                                                                                ServerResponseCodes.SYSTEM_EXCEPTION,
                                                                                                 exceptionMsg);
                }
                 index++;
             }
         }
        return serverResponseStruct;
    }

    /**
     * Cancels orders of a given user based on branch and BranchSequenceNumber obtained from
     * orderIdStruct.
     *
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param userId - Id of a users whose order/orders are to be cancelled.
     * @param transactionId - The unique identifier for the request, as defined by the SAGUI (used to identify this request).
     * @param timeStamp - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties - The <code>KeyValueStruct</code> instance
     * @param operationType - The operationType
     * @param orderIdStructs - Array of OrderIdStruct
     * @return ServerResponseStruct[]
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws TransactionFailedException
     * @throws NotAcceptedException
     * @throws AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    public ServerResponseStruct[] cancelOrdersForUser(String userIdRequestingCancel,String userId, String transactionId, DateTimeStruct timeStamp,
    KeyValueStruct[] properties,short operationType, OrderIdStruct[] orderIdStructs, boolean currentDate)
    throws   SystemException, CommunicationException, DataValidationException,AuthorizationException
    {
        return executeBulkServiceRequestForUser(userIdRequestingCancel, userId, transactionId, timeStamp, properties, operationType, CANCEL_ORDERS, orderIdStructs, currentDate);
    }

    /**
     * Validate routeNames and Cancels orders of a given user based on branch sequence number obtained from
     * orderIdStruct.
     *
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param userId - Id of a users whose order/orders are to be canceled.
     * @param transactionId - The unique identifier for the request, as defined by the SAGUI (used to identify this request).
     * @param timeStamp - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties - The <code>KeyValueStruct</code> instance
     * @param operationType - The operationType
     * @param orderIdStructs - Array of OrderIdStruct
     * @param requestType - CANCEL_ORDERS
     * @return ServerResponseStruct[]
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws TransactionFailedException
     * @throws NotAcceptedException
     * @throws AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    private ServerResponseStruct[] executeBulkServiceRequestForUser(final String userIdRequestingCancel ,String userId,final String transactionId, final DateTimeStruct timeStamp,
    final KeyValueStruct[] properties,  final short operationType,  final String requestType,OrderIdStruct[] orderIdStructs, boolean currentDate)
    throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        List<String> serviceRoutes = ClientFederatedServiceHelper.getServiceRoutes(uniqueServiceRefByProcess);
        ServerResponseStruct[] serverResponseStruct = new ServerResponseStruct[serviceRoutes.size()];
        int index = 0;
        ServerResponseStruct[] responseStructs = null;
        for (String serviceRoute : serviceRoutes)
        {
            String exceptionMsg = ClientFederatedServiceHelper.EMPTY_STRING;
            OrderHandlingService targetService = (OrderHandlingService) uniqueServiceRefByProcess.get(serviceRoute);

            Log.information(this, new StringBuilder("Sending ").append(requestType).append(" request to service :")
                        .append(serviceRoute).toString());

            try
            {
                responseStructs = targetService.cancelOrdersForUser(userIdRequestingCancel, userId, transactionId, timeStamp, properties, operationType, orderIdStructs, currentDate);
                serverResponseStruct[index] = responseStructs[0];
            }
            catch (Exception e)
            {
                //exceptionMsg = e.getMessage();
                Log.exception(this, new StringBuilder("Exception on sending ").append(requestType)
                        .append(" request to service ").append(serviceRoute).toString(), e);
            }
            finally
            {
                if (!ClientFederatedServiceHelper.isStringEmpty(exceptionMsg))
                {
                    serverResponseStruct[index] = ClientFederatedServiceHelper.getServerResponseStruct(serviceRoute,
                                                                                                 ServerResponseCodes.SYSTEM_EXCEPTION,
                                                                                                 exceptionMsg);
                }
                index++;
            }
        }
        return serverResponseStruct;
    }

    public OrderQueryResultStruct getOrdersByClassAndTime(String p_userId, int p_classKey, DateTimeStruct p_startTime, short p_direction) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        boolean serviceNotFound = false;
        String serviceNotFoundMessage = "";

        ArrayList services = getServicesByClass(p_classKey);
        if (services.size() == 0)
        {
            String errorString =  buildErrorMsg(p_userId, p_classKey);


            Log.alarm(this, "(getOrdersByClassAndTime, serviceNotFound) " + errorString );
            throw ExceptionBuilder.dataValidationException("getOrdersByClassAndTime," + errorString , 0);
        }

        OrderHandlingService targetService = null;
        Iterator serviceIterator = services.iterator();
        ArrayList<OrderQueryResultStruct> allOrders = new ArrayList<OrderQueryResultStruct>();
        while ( serviceIterator.hasNext() )
        {
            OrderQueryResultStruct result = new OrderQueryResultStruct();
            try
            {
                targetService = (OrderHandlingService)serviceIterator.next();
                if (Log.isDebugOn())
                {
                    Log.debug(this, "(getOrdersByClassAndTime)Sending request to service " + targetService.toString());
                }
                result = targetService.getOrdersByClassAndTime(p_userId, p_classKey, p_startTime,p_direction);
                allOrders.add(result);
            }

            catch (com.cboe.exceptions.DataValidationException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrdersByClassAndTime) DataValidationException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch (com.cboe.exceptions.CommunicationException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrdersByClassAndTime) CommunicationException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
            {
                serviceNotFound = true;
                serviceNotFoundMessage = serviceNotFoundMessage + " Object not found for service: " + targetService;
            }
        }

        OrderQueryResultStruct allResponses = buildOrderQueryResult(allOrders);

        if (allResponses.totalOrdersCount == 0)
        {
            String errorString = buildErrorMsg(p_userId, p_classKey);

            if(serviceNotFound)
            {
                Log.alarm(this, "(getOrdersByClassAndTime, serviceNotFound)," + errorString +  " for user " + p_userId + " with error:" +  serviceNotFoundMessage);
                throw ExceptionBuilder.communicationException("getOrdersByClassAndTime, No order has been found due to:" + serviceNotFoundMessage +
                       " userid=" + p_userId, CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
            }
        }

        return allResponses;

    }

    public ServerResponseStructV2[] getOrdersByLocationType(short[] orderLocationTypes, String transactionId, String userIdRequesting)
    throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        ServerResponseStructV2[] allSvrResults = new ServerResponseStructV2[serviceRoutes.size()];
        int index = 0;
        for (String serviceRoute : serviceRoutes)
        {
            boolean callStatus = false;
            ExceptionDetails expDetails = null; 
            short respCode = 0;
            if (Log.isDebugOn())
            {
                Log.debug(this, "Sending request to service :" + serviceRoute);
            }
            try
            {
                OrderHandlingService targetService = (OrderHandlingService) uniqueServiceRefByProcess.get(serviceRoute);
                ServerResponseStructV2[] aServerRsp = targetService.getOrdersByLocationType(orderLocationTypes, transactionId, userIdRequesting);
                if (aServerRsp.length > 0)
                {
                    allSvrResults[index++] = aServerRsp[0];
                    callStatus = true;
                }
                else 
                {
                    respCode = ServerResponseCodes.NOT_FOUND_EXCEPTION;
                    expDetails = ExceptionBuilder.createDetails("!! Empty result !!", respCode);
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "Service " + serviceRoute + " provided invalid (empty) result.");
                    }
                }                    
            }
            catch (DataValidationException dve)
            {
                expDetails = dve.details;
                respCode = ServerResponseCodes.DATA_VALIDATION_EXCEPTION;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "DataValidationException on sending getOrdersByLocationType request to service " + serviceRoute);
                }
            }
            catch (CommunicationException ce)
            {
                expDetails = ce.details;
                respCode = ServerResponseCodes.COMMUNICATION_EXCEPTION;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "CommunicationException on sending getOrdersByLocationType request to service " + serviceRoute);
                }
            }
            catch (SystemException se)
            {
                expDetails = se.details;
                respCode = ServerResponseCodes.SYSTEM_EXCEPTION;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "SystemException on sending getOrdersByLocationType request to service " + serviceRoute);
                }
            }
            catch (AuthorizationException ae)
            {
                expDetails = ae.details;
                respCode = ServerResponseCodes.AUTHENTICATION_EXCEPTION;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "AuthorizationException on sending getOrdersByLocationType request to service " + serviceRoute);
                }
            }
            catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
            {
                String msg = "Server not found: " + serviceRoute;
                respCode = ServerResponseCodes.COMMUNICATION_EXCEPTION;
                expDetails = ExceptionBuilder.createDetails(msg, respCode);
                if (Log.isDebugOn())
                {
                    Log.debug(this, "CORBA exception: OBJECT_NOT_EXIST for " + serviceRoute);
                }                    
            }
            catch(Exception e)
            {
                String msg = "Exception on getOrdersByLocationType request to service " + serviceRoute;
                respCode = ServerResponseCodes.COMMUNICATION_EXCEPTION;
                expDetails = ExceptionBuilder.createDetails(msg, respCode);
                if (Log.isDebugOn())
                {
                    Log.debug(this, expDetails.message);
                }
            }
            if (!callStatus) 
            {
                ServerResponseStructV2 svrRespStruct = new ServerResponseStructV2();
                svrRespStruct.serverId = serviceRoute;
                svrRespStruct.errorCode = ServerResponseCodes.SYSTEM_EXCEPTION;
                svrRespStruct.exceptionDetails = expDetails;
                allSvrResults[index++] = svrRespStruct;
            }
        }
        return allSvrResults;            
    }

    public ServerResponseStructV2[] getOrdersByLocation(String p_location, String p_transactionId, String p_userIdRequesting, short[] p_filterTypes) throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException
    {
        ServerResponseStructV2[] allSvrResults = new ServerResponseStructV2[serviceRoutes.size()];
        int index = 0;
        for (String serviceRoute : serviceRoutes)
        {
            boolean callStatus = false;
            ExceptionDetails expDetails = null; 
            short respCode = 0;
            if (Log.isDebugOn())
            {
                Log.debug(this, "Sending request to service :" + serviceRoute);
            }
            try
            {
                OrderHandlingService targetService = (OrderHandlingService) uniqueServiceRefByProcess.get(serviceRoute);
                ServerResponseStructV2[] aServerRsp = targetService.getOrdersByLocation(p_location, p_transactionId, p_userIdRequesting, p_filterTypes);
                if (aServerRsp.length > 0)
                {
                    allSvrResults[index++] = aServerRsp[0];
                    callStatus = true;
                }
                else 
                {
                    respCode = ServerResponseCodes.NOT_FOUND_EXCEPTION;
                    expDetails = ExceptionBuilder.createDetails("!! Empty result !!", respCode);
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "Service " + serviceRoute + " provided invalid (empty) result.");
                    }
                }                    
            }
            catch (DataValidationException dve)
            {
                expDetails = dve.details;
                respCode = ServerResponseCodes.DATA_VALIDATION_EXCEPTION;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "DataValidationException on getOrdersByLocation request to service " + serviceRoute);
                }
            }
            catch (NotAcceptedException nae)
            {
                expDetails = nae.details;
                respCode = ServerResponseCodes.NOT_ACCEPTED_EXCEPTION;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "DataValidationException on getOrdersByLocation request to service " + serviceRoute);
                }
            }
            
            catch (CommunicationException ce)
            {
                expDetails = ce.details;
                respCode = ServerResponseCodes.COMMUNICATION_EXCEPTION;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "CommunicationException on getOrdersByLocation request to service " + serviceRoute);
                }
            }
            catch (SystemException se)
            {
                expDetails = se.details;
                respCode = ServerResponseCodes.SYSTEM_EXCEPTION;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "SystemException on sending getOrdersByLocation request to service " + serviceRoute);
                }
            }
            catch (AuthorizationException ae)
            {
                expDetails = ae.details;
                respCode = ServerResponseCodes.AUTHENTICATION_EXCEPTION;
                if (Log.isDebugOn())
                {
                    Log.debug(this, "AuthorizationException on getOrdersByLocation request to service " + serviceRoute);
                }
            }
            catch(org.omg.CORBA.OBJECT_NOT_EXIST e) 
            {
                String msg = "Server not found: " + serviceRoute;
                respCode = ServerResponseCodes.COMMUNICATION_EXCEPTION;
                expDetails = ExceptionBuilder.createDetails(msg, respCode);
                                     
                if (Log.isDebugOn())
                {
                    Log.debug(this, "CORBA exception: OBJECT_NOT_EXIST for " + serviceRoute);
                }                    
            }
            catch(Exception e)
            {
                String msg = "Exception on getOrdersByLocation request to service " + serviceRoute;
                respCode = ServerResponseCodes.COMMUNICATION_EXCEPTION;
                expDetails = ExceptionBuilder.createDetails(msg, respCode);
                if (Log.isDebugOn())
                {
                    Log.debug(this, expDetails.message);
                }
            }
            if (!callStatus) 
            {
                ServerResponseStructV2 svrRespStruct = new ServerResponseStructV2();
                svrRespStruct.serverId = serviceRoute;
                svrRespStruct.errorCode = respCode;
                svrRespStruct.exceptionDetails = expDetails;
                allSvrResults[index++] = svrRespStruct;
            }
        }
        return allSvrResults;
    }

    public OrderQueryResultStruct getOrdersByProductAndTime(String p_userId, int p_productKey, DateTimeStruct p_startTime, short p_direction) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        boolean serviceNotFound = false;
        String serviceNotFoundMessage = "";

        ArrayList services = getServicesByProduct(p_productKey);
        if (services.size() == 0)
        {
            String errorString = buildErrorMsg(p_userId, p_productKey);


            Log.alarm(this, "(getOrdersByProductAndTime, serviceNotFound) " + errorString );
            throw ExceptionBuilder.dataValidationException("getOrdersByProductAndTime," + errorString , 0);
        }

        OrderHandlingService targetService = null;
        Iterator serviceIterator = services.iterator();
        ArrayList<OrderQueryResultStruct> allOrders = new ArrayList<OrderQueryResultStruct>();

        while ( serviceIterator.hasNext() )
        {
            OrderQueryResultStruct result = new OrderQueryResultStruct();
            try
            {
                targetService = (OrderHandlingService)serviceIterator.next();
                if (Log.isDebugOn())
                {
                    Log.debug(this, "(getOrdersByProductAndTime)Sending request to service " + targetService.toString());
                }
                result = targetService.getOrdersByProductAndTime(p_userId, p_productKey, p_startTime,p_direction);
                allOrders.add(result);
            }
            catch (com.cboe.exceptions.DataValidationException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrdersByProductAndTime) DataValidationException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch (com.cboe.exceptions.CommunicationException e)
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "(getOrdersByProductAndTime) CommunicationException Failure on Sending request to service " + targetService.toString() );
                }
            }
            catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
            {
                serviceNotFound = true;
                serviceNotFoundMessage = serviceNotFoundMessage + " Object not found for service: " + targetService;
            }
        }

        OrderQueryResultStruct allResponses = buildOrderQueryResult(allOrders);

        if (allResponses.totalOrdersCount == 0)
        {
            String errorString = buildErrorMsg(p_userId, p_productKey);

            if(serviceNotFound)
            {
                Log.alarm(this, "(getOrdersByProductAndTime, serviceNotFound)," + errorString +  " for user " + p_userId + " with error:" +  serviceNotFoundMessage);
                throw ExceptionBuilder.communicationException("getOrdersByProductAndTime, No order has been found due to:" + serviceNotFoundMessage +
                        " userid=" + p_userId, CommunicationFailureCodes.SERVER_NOT_AVAILABLE);
            }
        }
        return allResponses;
    }

    private OrderQueryResultStruct buildOrderQueryResult(ArrayList<OrderQueryResultStruct> allResponses)
    {
        OrderQueryResultStruct combinedResult = new OrderQueryResultStruct();
        
	try
	{
	    int totalOrderCount = 0;
	    int totalErrorCount = 0;
	    for (OrderQueryResultStruct theResult: allResponses)
	    {
		totalOrderCount += theResult.orderStructSequence.length;
		totalErrorCount += theResult.serverResponseStructSequence.length;
		combinedResult.totalOrdersCount = theResult.totalOrdersCount;
	    }
	    combinedResult.orderStructSequence = new OrderStruct[totalOrderCount];
	    combinedResult.serverResponseStructSequence = new ServerResponseStruct[totalErrorCount];
	    int index = 0;
	    int errorCount = 0;
	    for (OrderQueryResultStruct theOrders: allResponses)
	    {
	        if (theOrders.totalOrdersCount > 0)
		{
		    if(Log.isDebugOn())
		    {
		        Log.debug(this,"OrderQueryResult theOrders.totalOrderCount = " + theOrders.totalOrdersCount +
		            " theOrders.orderStructSequence.length = " + theOrders.orderStructSequence.length);
		    }
		    System.arraycopy(theOrders.orderStructSequence, 0, combinedResult.orderStructSequence, index, theOrders.orderStructSequence.length);
		    index += theOrders.orderStructSequence.length;
		}
		if (theOrders.serverResponseStructSequence.length > 0)
		{
                    System.arraycopy(theOrders.serverResponseStructSequence, 0, combinedResult.serverResponseStructSequence,
			    errorCount, theOrders.serverResponseStructSequence.length);
		    errorCount += theOrders.serverResponseStructSequence.length;
		}
	    }
	}
	catch(Exception e)
	{
	    Log.exception(this, "error while building the combinedResult in buildOrderQueryResult ", e);
	}
        if(Log.isDebugOn())
	{
		Log.debug(this,"CombinedResult, combinedResult.totalOrderCount = " + combinedResult.totalOrdersCount +
			" combinedResult.orderStructSequence.length = " + combinedResult.orderStructSequence.length + 
			" combinedResult.serverResponseStructSequence.length = " + combinedResult.serverResponseStructSequence.length);
	}
	return combinedResult;
    }

    private class PublishOrdersForDetination implements Runnable
    {
        private final String destination;
        private final OrderHandlingService ordHandlingService;
        
        PublishOrdersForDetination(String p_userId, OrderHandlingService ordHandlingService)
        {
            destination = p_userId;
            this.ordHandlingService = ordHandlingService;
        }
       
        public void run()
        {
            try 
            {
                ordHandlingService.publishAllMessagesForDestination(destination);
            }
            catch (Exception ex)
            {
                Log.information("OrderHandlingServiceRoutingProxy::PublishOrdersForDetination: Unable to reach the OHS: " + ordHandlingService + " with exception: " + ex);
            }
        }
    }

    public void publishAllMessagesForDestination(String p_destination) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        for (String aServiceRoute : serviceRoutes)
        {
            ExecutorService anOmtLoginMsgPublisher =  omtLoginMsgPublishers.get(aServiceRoute);
            OrderHandlingService ohs = (OrderHandlingService) uniqueServiceRefByProcess.get(aServiceRoute);
            anOmtLoginMsgPublisher.execute(new PublishOrdersForDetination(p_destination, ohs));
        }
    }
        
    protected void finalize() throws Throwable
    {
        for (String aServiceRoute : serviceRoutes)
        {
            ExecutorService anOmtLoginMsgPublisher =  omtLoginMsgPublishers.get(aServiceRoute);
            anOmtLoginMsgPublisher.shutdown();
        }
        queryOrderByIdExecutor.shutdown();
        queryOrderByORSIdExecutor.shutdown();
        super.finalize(); // todo should we catch and ignore Throwable here, not let it be thrown farther?
    }

    public void acceptDirectRoute(String p_destination, OrderStruct p_anOrder, short p_rerouteFlag, String p_userIdRequesting) throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        String sessionName = p_anOrder.sessionNames[0];
        int productKey = p_anOrder.productKey;
        
        OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey );
        
        
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for direct route order from OMT.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtDirectRerouteOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
      	try
      	{	
      		
      		service.acceptDirectRoute(p_destination, p_anOrder, p_rerouteFlag, p_userIdRequesting);
      		exceptionWasThrown = false;
      		
      	}

      	finally
      	{
      		// exit TTE emitpoint for direct route order from OMT.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtDirectRerouteOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
        
    }
    
    public void acceptManualCancel(CancelRequestStruct cancelRequest, ProductKeysStruct productKey, long cancelReqId, String userId) throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException, TransactionFailedException, NotAcceptedException
    {
        String sessionName = cancelRequest.sessionName;
        OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey.productKey );
        
         
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for ManualCancel order from OMT.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtManaulCancelOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
      	try
      	{	
      		
      		service.acceptManualCancel(cancelRequest, productKey, cancelReqId, userId); 
      		exceptionWasThrown = false;
      		
      	}

      	finally
      	{
      		// exit TTE emitpoint for ManualCancel order from OMT.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtManaulCancelOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
    }

    public OrderIdStruct acceptManualCancelReplace(CancelRequestStruct cancelRequest, ProductKeysStruct productKey, OrderStruct anOrder, long cancelReqId, String userId) throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException, TransactionFailedException, NotAcceptedException
    {   
    	OrderIdStruct orderIdStruct = null;
        String sessionName = cancelRequest.sessionName;
        OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey.productKey );
        
        long entityId = 0L;
        boolean exceptionWasThrown = true; /* Prepare for the worst --- clear flag after success */
        try
        {
        	entityId = TransactionTimingUtil.getEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to get EntityID! Exception details: " + e.getMessage());
        }
        //enter TTE emitpoint for ManaulCancelReplaceOrder from OMT.
        TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtManaulCancelReplaceOrderRoutingProxyEmitPoint(), entityId, TransactionTimer.Enter );  		
      	try
      	{	
      		
      		orderIdStruct =  service.acceptManualCancelReplace(cancelRequest, productKey, anOrder, cancelReqId, userId);
      		exceptionWasThrown = false;
      		
      	}

      	finally
      	{
      		// exit TTE emitpoint for ManaulCancelReplaceOrder from OMT.
      		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOmtManaulCancelReplaceOrderRoutingProxyEmitPoint(), entityId,
      		        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );
      	}
      	return orderIdStruct;
    }

    public void markMessageAsRead(String p_userId, String p_sessionName, int p_productKey, long p_messageId) throws SystemException, CommunicationException, NotFoundException, DataValidationException, AuthorizationException
    {
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(p_sessionName, p_productKey);
            service.markMessageAsRead(p_userId, p_sessionName, p_productKey, p_messageId);
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToCommunicationException(e, "markMessageAsRead Failed for user:pKey::" + p_userId + ":" + p_productKey);
        }
    }

    public PendingOrderStruct[] getPendingAdjustmentOrders(String sessionName, int classKey) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        List<String> serviceRoutes = getServiceRoutes(sessionName);
        List<PendingOrderStruct[]> orders = new ArrayList<PendingOrderStruct[]>();
        int totalOrdersSize = 0;

        for (String serviceRoute : serviceRoutes)
        {
            try
            {           
                OrderHandlingService targetService = (OrderHandlingService) uniqueServiceRefByProcess.get(serviceRoute);
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Sending request to service :" + serviceRoute);
                }
                PendingOrderStruct[] newOrders = (targetService.getPendingAdjustmentOrders(sessionName, classKey));
                totalOrdersSize += newOrders.length;
                orders.add(newOrders);
            }
            catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
            {
                throw convertToCommunicationException(e, "Query to getPendingAdjustmentOrders Failed for classKey::" + classKey);
            }
        }
        
        return getTotalPendingOrders(orders, totalOrdersSize);
    }
        
    private PendingOrderStruct[] getTotalPendingOrders(List<PendingOrderStruct[]> orders, int totalOrdersSize)
    {
        PendingOrderStruct[] returnOrders = new PendingOrderStruct[totalOrdersSize];            
        for (PendingOrderStruct[] posa : orders)
        {
            totalOrdersSize = 0; // todo is it correct to reset the sum inside the loop?
            System.arraycopy(posa, 0, returnOrders, totalOrdersSize, posa.length);
            totalOrdersSize += posa.length;
        }
        return returnOrders;
    }

    public void acceptMessageRoute(String p_userId, String p_sessionName, int p_productKey, String p_newLocation, long p_msgId) throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(p_sessionName, p_productKey);
            service.acceptMessageRoute(p_userId, p_sessionName, p_productKey, p_newLocation, p_msgId);
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToCommunicationException(e, "acceptMessageRoute Failed for user:pKey::" + p_userId + ":" + p_productKey);
        }
    }

    public void acceptManualUpdate(int p_remainingQuantity, OrderStruct p_anOrder, String p_userId) throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        String sessionName = p_anOrder.sessionNames[0];
        int productKey = p_anOrder.productKey;
        try
        {
            OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, productKey );
            service.acceptManualUpdate( p_remainingQuantity, p_anOrder, p_userId );
        }
        catch(org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, "acceptManualUpdate Failed for order branch:seq#::" + p_anOrder.orderId.branch + ":" + p_anOrder.orderId.branchSequenceNumber);
        }
    }

    public void acceptManualFillReport(short p_activityType, String p_userId, ManualFillStruct[] p_fillReports, int p_productKey, int p_transactionSequenceNumber) throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException, TransactionFailedException, NotAcceptedException
    {
        String sessionName = p_fillReports[0].sessionName;
        OrderHandlingService service = (OrderHandlingService) getServiceByProduct(sessionName, p_productKey );           
        service.acceptManualFillReport(p_activityType,p_userId,p_fillReports,p_productKey,p_transactionSequenceNumber);            
    }

    /**
     * Cancels orders  for ALL users according to classKeys, order types (GTC,DAY,GTD)
     * and currentDate as requested by Help Desk user.
     *
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param transactionId          - The unique identifier for the request, as defined by the SAGUI (used to to identify this request).
     * @param timestamp              - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties             - The <code>KeyValueStruct</code> instance
     * @param operationType          - Type of Operation
     * @param orderTypes             - Either DAY, GTC, GTD
     * @param sessionName			 - Session name to uniquely identify the classKey across sessions
     * @param classKeys              - int array of classKeys
     * @param currentDate            - true if requested date is current or false.
     * @return ServerResponseStruct[]
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws SystemException
     * @throws AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    public ServerResponseStruct[] cancelOrdersForAllUsersByClass(String userIdRequestingCancel,
            String transactionId, DateTimeStruct timestamp, KeyValueStruct[] properties,
            short operationType, char[] orderTypes, 
            String sessionName, int[] classKeys, boolean currentDate) throws DataValidationException,
                                                                                                                                  CommunicationException, SystemException, AuthorizationException
    {
        // TODO Needs to add Implementation here.
        return null;
    }

    /**
     * Cancels orders  for ALL users according to productKeys, order types (GTC,DAY,GTD)
     * and currentDate as requested by Help Desk user.
     *
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param transactionId          - The unique identifier for the request, as defined by the SAGUI (used to to identify this request).
     * @param timestamp              - The timestamp "millis since 1/1/70 GMT" of the request being invoked from the SAGUI
     * @param properties             - The <code>KeyValueStruct</code> instance
     * @param operationType          - Type of Operation
     * @param orderTypes             - Either DAY, GTC, GTD
     * @param sessionName			 - Session name to uniquely identify the productKey across sessions
     * @param currentDate            - true if requested date is current or false.
     * @return ServerResponseStruct[]
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws SystemException
     * @throws AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    public ServerResponseStruct[] cancelOrdersForAllUsersByProduct(String userIdRequestingCancel,
            String transactionId, DateTimeStruct timestamp, KeyValueStruct[] properties,
            short operationType, char[] orderTypes, 
            String sessionName, int[] prodKeys, boolean currentDate) throws DataValidationException,
                                                                                                                                   CommunicationException, SystemException, AuthorizationException
    {
        // TODO Needs to add Implementation here.
        return null;
    }


    /**
     * Cancels orders of a given user based on OrsIds requested by Help Desk user.
     *
     * @param userIdRequestingCancel - The user id (login) of the help desk user invoking the request
     * @param userId - Id of a users whose order/orders are to be cancelled.
     * @param transactionId - The unique identifier for the request, as defined by the SAGUI (used to identify this request).
     * @param properties - The <code>KeyValueStruct</code> instance
     * @param operationType - The operationType
     * @param orsIds   - String Array of ORSid
     * @return ServerResponseStruct[]
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws SystemException
     * @throws AuthorizationException
     * @author Cognizant Technology Solutions.
     */
    public ServerResponseStruct[] cancelOrdersForUserByOrsId(String userIdRequestingCancel,
            String userId, String transactionId, DateTimeStruct timestamp,
            KeyValueStruct[] properties, short operationType, String[] orsIds
            ) throws DataValidationException, CommunicationException,
             SystemException, AuthorizationException
    {
        // TODO Needs to add Implementation here.
        return null;
    }
        

    /*
    * *******************************************************************
    * PAR entry/query
    * *******************************************************************
    */
    public void acceptManualCancelReport(OrderRoutingParameterStruct orderRoutingParameterStruct, ManualCancelReportStruct[] manualCancelReportStructs, int productKey, int transactionSequenceNumber) throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException, NotAcceptedException, TransactionFailedException
    {
        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(manualCancelReportStructs[0].sessionName, productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for acceptManualCancelReport.");
            orderHandlingService.acceptManualCancelReport(orderRoutingParameterStruct, manualCancelReportStructs, productKey, transactionSequenceNumber);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for acceptManualCancelReport.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("acceptManualCancelReport Failed for order branch:seq#::")
                    .append(manualCancelReportStructs[0].orderId.branch).append(":")
                    .append(manualCancelReportStructs[0].orderId.branchSequenceNumber).toString());
        }
    }

    public void acceptManualFillReportV2(short activityType,
                                         OrderRoutingParameterStruct orderRouting, ManualFillStructV2[] fillReports,
                                         int transactionSequenceNumber)
            throws SystemException, CommunicationException,
                   DataValidationException, NotFoundException, AuthorizationException,
                   NotAcceptedException, TransactionFailedException
    {
        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(fillReports[0].manualFill.sessionName, fillReports[0].manualFill.productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for acceptManualFillReportV2.");
            orderHandlingService.acceptManualFillReportV2(activityType, orderRouting, fillReports, transactionSequenceNumber);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for acceptManualFillReportV2.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("acceptManualFillReportV2 Failed for order branch:seq#::")
                    .append(fillReports[0].manualFill.orderId.branch).append(":")
                    .append(fillReports[0].manualFill.orderId.branchSequenceNumber).toString());      
        }
    }

    public void acceptManualFillTimeout(OrderRoutingParameterStruct orderRoutingStruct,
                                        ManualFillStruct[] fillReport, short activityType, int transactionSequenceNumber)
            throws SystemException, CommunicationException, DataValidationException,
                   NotFoundException, AuthorizationException, NotAcceptedException,
                   TransactionFailedException
    {
        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(fillReport[0].sessionName, fillReport[0].productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for acceptManualFillTimeout.");
            orderHandlingService.acceptManualFillTimeout(orderRoutingStruct, fillReport, activityType, transactionSequenceNumber);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for acceptManualFillTimeout.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("acceptManualFillTimeout Failed for order branch:seq#::")
                    .append(fillReport[0].orderId.branch).append(":")
                    .append(fillReport[0].orderId.branchSequenceNumber).toString());
        }
    }

    public void acceptManualOrderReturn(ManualMarketBrokerDataStruct marketData,
                                        OrderRoutingParameterStruct orderRouting, OrderIdStruct orderID,
                                        String sessionName, int productKey, OrderHandlingInstructionStruct orderHandling,
                                        int[] legMaxExcutionVolume, long requestTime, short activityType)
            throws SystemException, CommunicationException, DataValidationException,
                   NotFoundException, AuthorizationException, NotAcceptedException,
                   TransactionFailedException
    {

        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(sessionName, productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for acceptManualOrderReturn.");
            orderHandlingService.acceptManualOrderReturn(marketData,
                                                         orderRouting, orderID, sessionName, productKey, orderHandling, legMaxExcutionVolume, requestTime, activityType);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for acceptManualOrderReturn.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("acceptManualOrderReturn Failed for order branch:seq#::")
                    .append(orderID.branch).append(":")
                    .append(orderID.branchSequenceNumber).toString());
        }


    }

    public void acceptManualOrderReturnTimeout(ManualMarketBrokerDataStruct marketData,
                                               OrderRoutingParameterStruct orderRouting, OrderIdStruct orderID,
                                               String sessionName, int productKey, long requestTime,
                                               OrderHandlingInstructionStruct orderHandling, int quantity, int[] legQuantities,
                                               short activityType) throws SystemException, CommunicationException,
                                                                          DataValidationException, NotFoundException, AuthorizationException,
                                                                          NotAcceptedException, TransactionFailedException
    {
        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(sessionName, productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for acceptManualOrderReturnTimeout.");
            orderHandlingService.acceptManualOrderReturnTimeout(marketData,
                                                                orderRouting, orderID, sessionName, productKey, requestTime, orderHandling, quantity, legQuantities, activityType);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for acceptManualOrderReturnTimeout.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("acceptManualOrderReturnTimeout Failed for order branch:seq#::")
                    .append(orderID.branch).append(":")
                    .append(orderID.branchSequenceNumber).toString());
        }
    }

    public void acceptManualQuote(ManualQuoteStruct manualQuoteStruct) throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(manualQuoteStruct.sessionName, manualQuoteStruct.productKeys.productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for acceptManualQuote.");
            orderHandlingService.acceptManualQuote(manualQuoteStruct);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for acceptManualQuote.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("acceptManualQuote Failed::")
                    .append(manualQuoteStruct.details.locationId).append(":")
                    .append(manualQuoteStruct.productKeys.productKey).toString());
        }
    }

    public void cancelManualQuote(ManualQuoteStruct manualQuoteStruct) throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException, NotFoundException
    {
        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(manualQuoteStruct.sessionName, manualQuoteStruct.productKeys.productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for cancelManualQuote.");
            orderHandlingService.cancelManualQuote(manualQuoteStruct);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for cancelManualQuote.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("cancelManualQuote Failed::")
                    .append(manualQuoteStruct.details.locationId).append(":")
                    .append(manualQuoteStruct.productKeys.productKey).toString());
        }

    }

    public void acceptPrintCancel(OrderRoutingParameterStruct orderRoutingStruct,
                                  ManualCancelRequestStruct cancelRequest, int productKey) throws SystemException,
                                                                                                  CommunicationException, DataValidationException, NotFoundException,
                                                                                                  AuthorizationException, NotAcceptedException, TransactionFailedException
    {
        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(cancelRequest.sessionName, productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for acceptPrintCancel.");
            orderHandlingService.acceptPrintCancel(orderRoutingStruct, cancelRequest, productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for acceptPrintCancel.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("acceptPrintCancel Failed for order branch:seq#::")
                    .append(cancelRequest.orderId.branch).append(":")
                    .append(cancelRequest.orderId.branchSequenceNumber).toString());
        }

    }

    public void acceptPrintCancelReplace(OrderRoutingParameterStruct orderRoutingStruct,
                                         ManualCancelRequestStruct cancelRequest, OrderIdStruct orderID, int productKey)
            throws SystemException, CommunicationException, DataValidationException,
                   NotFoundException, AuthorizationException, NotAcceptedException,
                   TransactionFailedException
    {
        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(cancelRequest.sessionName, productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for acceptPrintCancelReplace.");
            orderHandlingService.acceptPrintCancelReplace(orderRoutingStruct, cancelRequest, orderID, productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for acceptPrintCancelReplace.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("acceptPrintCancelReplace Failed for order branch:seq#::")
                    .append(cancelRequest.orderId.branch).append(":")
                    .append(cancelRequest.orderId.branchSequenceNumber).toString());
        }

    }

    public void acceptPrintRequest(OrderRoutingParameterStruct orderRouting,
                                   OrderIdStruct orderID, String sessionName, int productKey, long requestTime,
                                   int[] legPrintVolume, int printQuantity, short printRequestType)
            throws SystemException, CommunicationException, DataValidationException,
                   NotFoundException, AuthorizationException, NotAcceptedException,
                   TransactionFailedException
    {
        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(sessionName, productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for acceptPrintRequest.");
            orderHandlingService.acceptPrintRequest(orderRouting, orderID, sessionName, productKey, requestTime,
                                                    legPrintVolume, printQuantity, printRequestType);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for acceptPrintRequest.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("acceptPrintRequest Failed for order branch:seq#::")
                    .append(orderID.branch).append(":")
                    .append(orderID.branchSequenceNumber).toString());
        }
    }

    public void acceptVolumeChange(OrderRoutingParameterStruct orderRouting,
                                   OrderIdStruct orderID, String sessionName, int productKey, int changeVolume,
                                   long requestTime) throws SystemException, CommunicationException,
                                                            DataValidationException, NotFoundException, AuthorizationException,
                                                            NotAcceptedException, TransactionFailedException
    {
        try
        {
            OrderHandlingService orderHandlingService = (OrderHandlingService) getServiceByProduct(sessionName, productKey);
            Log.information(" OrderHandlingServiceClientRoutingProxy calling OHS for acceptVolumeChange.");
            orderHandlingService.acceptVolumeChange(orderRouting, orderID, sessionName, productKey, changeVolume, requestTime);
            Log.information(" OrderHandlingServiceClientRoutingProxy returning from OHS for acceptVolumeChange.");
        }
        catch (org.omg.CORBA.OBJECT_NOT_EXIST e)
        {
            throw convertToNotAcceptedException(e, new StringBuilder(150)
                    .append("acceptPrintRequest Failed for order branch:seq#::")
                    .append(orderID.branch).append(":")
                    .append(orderID.branchSequenceNumber).toString());
        }
    }
    
   
}
