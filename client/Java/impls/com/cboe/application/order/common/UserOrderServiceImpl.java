package com.cboe.application.order.common;

import static com.cboe.application.shared.LoggingUtil.createLightOrderLogSnapshot;
import static com.cboe.application.shared.LoggingUtil.createOrderLog;

import java.util.Map;

import org.omg.CORBA.UserException;

import com.cboe.application.order.FixOrderQueryCache;
import com.cboe.application.order.FixOrderQueryCacheFactory;
import com.cboe.application.order.IOrderQueryCache;
import com.cboe.application.order.OrderQueryCache;
import com.cboe.application.order.OrderQueryCacheFactory;
import com.cboe.application.shared.LoggingUtil;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.TransactionTimingRegistration;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.application.util.OrderCallSnapshot;
import com.cboe.application.util.RateManagerWrapperWithTTE;
import com.cboe.client.util.CollectionHelper;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.rateMonitor.RateManager;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.ExchangeFirmStructContainer;
import com.cboe.domain.util.GroupCancelReportContainer;
import com.cboe.domain.util.OrderIdCancelReportContainer;
import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;
import com.cboe.idl.cmiOrder.LightOrderEntryStruct;
import com.cboe.idl.cmiOrder.LightOrderResultStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.PendingOrderStruct;
import com.cboe.idl.cmiProduct.PendingNameStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.idl.order.CrossOrderIdStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.TCSProcessWatcherManager;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.application.UserOrderService;
import com.cboe.interfaces.businessServices.EnhancedOrderHandlingService;
import com.cboe.interfaces.businessServices.MarketMakerQuoteService;
import com.cboe.interfaces.businessServices.OrderHandlingService;
import com.cboe.interfaces.businessServices.ProductQueryService;
import com.cboe.interfaces.domain.RateMonitorHome;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.events.OrderStatusConsumerV2Home;
import com.cboe.server.lightOrder.LightOrderBuffer;
import com.cboe.server.lightOrder.codec.LightOrderCancelRequestCodec;
import com.cboe.server.lightOrder.codec.LightOrderCancelResponseCodec;
import com.cboe.server.lightOrder.codec.LightOrderNewRequestCodec;
import com.cboe.server.lightOrder.codec.LightOrderNewResponseCodec;
import com.cboe.util.ChannelKey;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
/**
 * This class implements UserOrderService which contains the common user order service that is shared by cmi and non cmi
 * users. It contains the userEnablement, rateMonitor checking of each call and the calls to the server services.
 * @author Jing Chen
 * @author Gijo Joseph
 * @author Piyush Patel
 * @version 6/25/2007
 */
public class UserOrderServiceImpl extends BObject implements UserOrderService
{
    protected OrderHandlingService orderHandlingService;
    protected EnhancedOrderHandlingService lightOrderHandlingService;
    protected MarketMakerQuoteService marketMakerQuoteService;
    protected BaseSessionManager sessionManager;
    protected String thisUserId;
    protected String thisExchange;
    protected String thisAcronym;
    protected RateMonitorHome rateMonitorHome;
    protected UserEnablement userEnablement;
    protected ProductQueryServiceAdapter pqAdapter;
    protected ProductQueryService         productQueryService;
    protected OrderStatusConsumerV2Home orderStatusConsumerV2Home;
    protected RateManager rateManager;
    private IOrderQueryCache orderQueryCache;
    private ConcurrentEventChannelAdapter internalEventChannel;

	private final TCSProcessWatcherManager pwManager;


    public UserOrderServiceImpl(BaseSessionManager session, Map sessionConstraints)
    {
        this.sessionManager = session;
        try
        {
            this.thisUserId = session.getUserId();
            this.thisExchange = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.exchange;
            this.thisAcronym = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.acronym;
            //Unnecessary initialization commenting out..ARUN 03/01/2011
			//orderQueryCache = OrderQueryCacheFactory.find(thisUserId);
            if (FixUtilConstants.CLIENT_TYPES.FIXCAS.equals(System.getenv(FixUtilConstants.CLIENT_TYPES.CLIENT_TYPE))){
            	orderQueryCache = FixOrderQueryCacheFactory.find(thisUserId);
            } else {
                orderQueryCache = OrderQueryCacheFactory.find(thisUserId);
            }
            Short[] monitorTypes = new Short[]{ RateMonitorTypeConstants.ACCEPT_ORDER, RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER};
            //Initialize the new class to emit TTE points to time rate monitor calls...ARUN 03/11/2011
            //rateManager = new RateManager(sessionConstraints,thisUserId,thisExchange,thisAcronym,monitorTypes);
            rateManager = new RateManagerWrapperWithTTE(sessionConstraints,thisUserId,thisExchange,thisAcronym,monitorTypes);
        }catch(UserException e)
        {
            Log.exception(this, e);
        }
        pwManager = getProcessWatcherManager();

        try
        {
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception("Exception getting CAS_INSTRUMENTED_IEC!", e);
        }

    }

    protected OrderHandlingService getOrderHandlingService()
    {
        if (orderHandlingService == null )
        {
            orderHandlingService = ServicesHelper.getOrderHandlingService();
        }
        return orderHandlingService;
    }
    /**
       * Returns the instance of the order handling service.
       */
      private EnhancedOrderHandlingService getLightOrderHandlingService()
      {
          if (lightOrderHandlingService == null )
          {
              lightOrderHandlingService = ServicesHelper.getEnhancedOrderHandlingService();
          }
          return lightOrderHandlingService;
      }

    protected MarketMakerQuoteService getMarketMakerQuoteService()
    {
        if (marketMakerQuoteService == null )
        {
            marketMakerQuoteService = ServicesHelper.getMarketMakerQuoteService();
        }
        return marketMakerQuoteService;
    }

    protected RateMonitorHome getRateMonitorHome()
    {
        if (rateMonitorHome == null )
        {
            try
            {
                rateMonitorHome = (RateMonitorHome)HomeFactory.getInstance().findHome(RateMonitorHome.HOME_NAME);
            }
            catch (CBOELoggableException e)
            {
                Log.exception(this, "session : " + sessionManager, e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find RateMonitor Home");
            }
        }

        return rateMonitorHome;
    }

    protected UserEnablement getUserEnablementService()
        throws SystemException, CommunicationException, AuthorizationException
    {
        if (userEnablement == null)
        {
            userEnablement = ServicesHelper.getUserEnablementService(thisUserId, thisExchange, thisAcronym);
        }
        return userEnablement;
    }

    public RateManager getRateManager()
    {
    	return  rateManager;
    }

    public void verifyUserOrderEntryEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	long entityId = 0l; 
    	boolean exceptionThrown = true;
    	try {
    		entityId = TransactionTimingUtil.getEntityID();
    		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUserEnablementEmitPoint(), entityId,	TransactionTimer.Enter);
	
    	} catch (Exception e) {
    		Log.information(this, "Unable to get EntityID! Exception details: "+ e.getMessage());
    	}
    	try {
    		getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.ORDERENTRY_ORDER);
    		exceptionThrown = false;
    	}finally {
    		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUserEnablementEmitPoint(),	entityId,
    				exceptionThrown ? TransactionTimer.LeaveWithException:TransactionTimer.Leave);
    	}
    }
    
    public void verifyUserOrderQueryEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.ORDERQUERY);
    }
    public void verifyUserOrderQueryEnablementForSession(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablementForSession(sessionName, OperationTypes.ORDERQUERY);
    }
    public void verifyUserOrderRFQEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.ORDERENTRY_RFQ);
    }

    public void verifyUserAuctionEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.AUCTION);
    }

    public void verifyUserOrderEntryEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	long entityId = 0l; 
    	boolean exceptionThrown = true;
    	try {
    		entityId = TransactionTimingUtil.getEntityID();
    		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUserEnablementEmitPoint(), entityId,	TransactionTimer.Enter);
	
    	} catch (Exception e) {
    		Log.information(this, "Unable to get EntityID! Exception details: "+ e.getMessage());
    	}
    	try {
            getUserEnablementService().verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.ORDERENTRY_ORDER);
    		exceptionThrown = false;
    	}finally {
    		TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getUserEnablementEmitPoint(),	entityId,
    				exceptionThrown ? TransactionTimer.LeaveWithException:TransactionTimer.Leave);
    	}


    }
    public void verifyUserOrderQueryEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.ORDERQUERY);
    }
    public void verifyUserOrderRFQEnablementForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.ORDERENTRY_RFQ);
    }
    public void verifyUserLightOrderEntryEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getUserEnablementService().verifyUserEnablement(sessionName, classKey, OperationTypes.ORDERENTRY_LIGHTORDER);
    }

    public void acceptCancel(CancelRequestStruct cancelRequestStruct, ProductKeysStruct productKeysStruct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
    	
       
        Log.information(this, createOrderLog("acceptCancel", 0, sessionManager.toString(), cancelRequestStruct.sessionName, getOrderIdString(cancelRequestStruct.orderId),null,0));
        verifyUserOrderEntryEnablement(cancelRequestStruct.sessionName, productKeysStruct.classKey);
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
              
        OrderStruct orderStruct = orderQueryCache.getOrderFromOrderCache(cancelRequestStruct.orderId);
        if (orderStruct == null)
        {
            // This case of Order is not exist in orderCache. We will check to see if order is in pendingCache for RTT order.
        	// Otherwise user is try to cancel order that is not exist. CAS will send out reject for this cancel.
        	if(orderQueryCache.checkOrderEntryCache(cancelRequestStruct.orderId))
    		{
    			// If order found in orderEntryCache. CAS will send cancel request in old way. Since CAS don't have enough info.
    			// to fake cancel report
        OrderCallSnapshot.startServerCall();
        getOrderHandlingService().acceptCancel(thisUserId, cancelRequestStruct, productKeysStruct );
        OrderCallSnapshot.endServerCall();
    		}else
    		{   // Order is not existing anywhere. Reject the cancel Request
    			throw ExceptionBuilder.dataValidationException("Cancel Order not found in OrderEntryCache for:" + getOrderIdString(cancelRequestStruct.orderId), DataValidationCodes.INVALID_ORDER_ID);
    		}
        }else
        {
        	boolean serverPublishCancelReport = false;
        	OrderCallSnapshot.startServerCall(); 
	        serverPublishCancelReport = getOrderHandlingService().acceptCancelV2(thisUserId, cancelRequestStruct, productKeysStruct );
	        OrderCallSnapshot.endServerCall();
	        if(Log.isDebugOn())
        	{
	        	String logMsg = serverPublishCancelReport?"Server will generate Cancel Report":"This is a fully canceled order. CAS will generate and publish cancel report"; 
        		Log.debug(this,"acceptCancelV2 retuned:<"+serverPublishCancelReport+"> "+logMsg);
        	}
	        if (!serverPublishCancelReport)
	        {
	        	buildCancelReport(cancelRequestStruct,orderStruct);
	        }
	        
        }
               
    }
         

    public OrderIdStruct acceptCancelReplace(CancelRequestStruct cancelRequestStruct, OrderStruct orderStruct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        Log.information(this, createOrderLog("acceptCancelReplace", 0, sessionManager.toString(), cancelRequestStruct.sessionName, getOrderIdString(cancelRequestStruct.orderId),null,0));
        String sessionName = cancelRequestStruct.sessionName;
        long currentTime = System.currentTimeMillis();
        verifyUserOrderEntryEnablement(sessionName, orderStruct.classKey);
        rateManager.monitorRate(sessionName, currentTime, "acceptOrderCancelReplaceRequest", RateMonitorTypeConstants.ACCEPT_ORDER);
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        checkSystemAvailability(orderStruct);
        
        OrderCallSnapshot.startServerCall();
        OrderIdStruct results = getOrderHandlingService().acceptCancelReplace(cancelRequestStruct, orderStruct);
        OrderCallSnapshot.endServerCall();
        return results;
    }

    public OrderIdStruct acceptOrder(OrderStruct orderStruct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        Log.information(this, createOrderLog("acceptOrder", 0, sessionManager.toString(), orderStruct.sessionNames[0], orderStruct.productKey));
        String[] sessionNames = orderStruct.sessionNames;
        long currentTime = System.currentTimeMillis();
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        for(int i = 0; i < sessionNames.length; i++)
        {
            String sessionName = sessionNames[i];
            verifyUserOrderEntryEnablement(sessionName, orderStruct.classKey);
            rateManager.monitorRate(sessionName, currentTime, "acceptOrder", RateMonitorTypeConstants.ACCEPT_ORDER);
        }
        
        //TCS - check the OHS status before sending the order. 
        checkSystemAvailability(orderStruct);
        
        OrderCallSnapshot.startServerCall();
        OrderIdStruct results = getOrderHandlingService().acceptOrder(orderStruct);
        OrderCallSnapshot.endServerCall();
        return results;
    }


    public void acceptCrossingOrder(OrderStruct orderStruct, OrderStruct orderStruct1)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        for ( int i =0 ; i < orderStruct.sessionNames.length; i ++)
        {
           verifyUserOrderEntryEnablement(orderStruct.sessionNames[i], orderStruct.classKey);
        }
        for ( int i =0 ; i < orderStruct1.sessionNames.length; i ++)
        {
            verifyUserOrderEntryEnablement(orderStruct1.sessionNames[i], orderStruct1.classKey);
        }
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        //I guess since both the order go to the same OHS checking availability for one order would suffice
        checkSystemAvailability(orderStruct);
        
        OrderCallSnapshot.startServerCall();
        getOrderHandlingService().acceptCrossingOrder(orderStruct, orderStruct1);
        OrderCallSnapshot.endServerCall();
    }

    /*
     * This method invokes the new acceptCrossingOrderV2 method that returns a CrossOrderIdStruct.
     * -GJ. 6/25/7
     */
    public CrossOrderIdStruct acceptCrossingOrderV2(OrderStruct orderStruct, OrderStruct orderStruct1)
    	throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
	{
		for ( int i =0 ; i < orderStruct.sessionNames.length; i ++)
		{
		   verifyUserOrderEntryEnablement(orderStruct.sessionNames[i], orderStruct.classKey);
		}
		for ( int i =0 ; i < orderStruct1.sessionNames.length; i ++)
		{
		    verifyUserOrderEntryEnablement(orderStruct1.sessionNames[i], orderStruct1.classKey);
		}
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        //I guess since both the order go to the same OHS checking availability for one order would suffice
        checkSystemAvailability(orderStruct);
        
		OrderCallSnapshot.startServerCall();
		CrossOrderIdStruct crossedOrder = getOrderHandlingService().acceptCrossingOrderV2(orderStruct, orderStruct1);
		OrderCallSnapshot.endServerCall();
		return crossedOrder;
	}

    public OrderIdStruct acceptStrategyCancelReplace(CancelRequestStruct cancelRequestStruct, OrderStruct orderStruct, LegOrderEntryStruct[] legOrderEntryStructs)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        Log.information(this, createOrderLog("acceptStrategyOrderCancelReplaceRequest", 0, sessionManager.toString(), orderStruct.sessionNames[0], getOrderIdString(cancelRequestStruct.orderId), null,orderStruct.productKey));
        String sessionName = cancelRequestStruct.sessionName;
        long currentTime = System.currentTimeMillis();
        verifyUserOrderEntryEnablement(cancelRequestStruct.sessionName, orderStruct.classKey);
        rateManager.monitorRate(sessionName, currentTime, "acceptStrategyOrderCancelReplaceRequest", RateMonitorTypeConstants.ACCEPT_ORDER);
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        checkSystemAvailability(orderStruct);
        
        OrderCallSnapshot.startServerCall();
        OrderIdStruct results = getOrderHandlingService().acceptStrategyCancelReplace(cancelRequestStruct, orderStruct, legOrderEntryStructs);
        OrderCallSnapshot.endServerCall();
        return results;
    }

     public OrderIdStruct acceptStrategyCancelReplaceV7(CancelRequestStruct cancelRequestStruct, OrderStruct orderStruct, LegOrderEntryStructV2[] legOrderEntryStructsV2)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        Log.information(this, createOrderLog("acceptStrategyOrderCancelReplaceRequestV7", 0, sessionManager.toString(), orderStruct.sessionNames[0], getOrderIdString(cancelRequestStruct.orderId), null,orderStruct.productKey));
        String sessionName = cancelRequestStruct.sessionName;
        long currentTime = System.currentTimeMillis();
        verifyUserOrderEntryEnablement(cancelRequestStruct.sessionName, orderStruct.classKey);
        rateManager.monitorRate(sessionName, currentTime, "acceptStrategyOrderCancelReplaceRequestV7", RateMonitorTypeConstants.ACCEPT_ORDER);
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        checkSystemAvailability(orderStruct);
        
        OrderCallSnapshot.startServerCall();
        OrderIdStruct results = getOrderHandlingService().acceptStrategyCancelReplaceV2(cancelRequestStruct, orderStruct, legOrderEntryStructsV2);
        OrderCallSnapshot.endServerCall();
        return results;
    }

    public OrderIdStruct acceptStrategyOrder(OrderStruct orderStruct, LegOrderEntryStruct[] legOrderEntryStructs)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        Log.information(this, createOrderLog("acceptStrategyOrder", 0, sessionManager.toString(), orderStruct.sessionNames[0], getOrderIdString(orderStruct.orderId), null,orderStruct.productKey));
        String[] sessionNames = orderStruct.sessionNames;
        long currentTime = System.currentTimeMillis();
        for(int i = 0; i < sessionNames.length; i++)
        {
            String sessionName = sessionNames[i];
            verifyUserOrderEntryEnablement(sessionName, orderStruct.classKey);
            rateManager.monitorRate(sessionName, currentTime, "acceptStrategyOrder", RateMonitorTypeConstants.ACCEPT_ORDER);
        }
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        checkSystemAvailability(orderStruct);
        
        OrderCallSnapshot.startServerCall();
        OrderIdStruct results = getOrderHandlingService().acceptStrategyOrder(orderStruct, legOrderEntryStructs);
        OrderCallSnapshot.endServerCall();
        return results;
    }

    public OrderIdStruct acceptStrategyOrderV7(OrderStruct orderStruct, LegOrderEntryStructV2[] legOrderEntryStructsV2)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        Log.information(this, createOrderLog("acceptStrategyOrderV7", 0, sessionManager.toString(), orderStruct.sessionNames[0], getOrderIdString(orderStruct.orderId), null,orderStruct.productKey));
        String[] sessionNames = orderStruct.sessionNames;
        long currentTime = System.currentTimeMillis();
        for(int i = 0; i < sessionNames.length; i++)
        {
            String sessionName = sessionNames[i];
            verifyUserOrderEntryEnablement(sessionName, orderStruct.classKey);
            rateManager.monitorRate(sessionName, currentTime, "acceptStrategyOrderV7", RateMonitorTypeConstants.ACCEPT_ORDER);
        }
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        checkSystemAvailability(orderStruct);
        
        OrderCallSnapshot.startServerCall();
        OrderIdStruct results = getOrderHandlingService().acceptStrategyOrderV2(orderStruct, legOrderEntryStructsV2);
        OrderCallSnapshot.endServerCall();
        return results;
    }

    public void acceptStrategyUpdate(int remainingQuantity, OrderStruct orderStruct, LegOrderEntryStruct[] legOrderEntryStructs)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        for(int i = 0; i<orderStruct.sessionNames.length; i++)
        {
            verifyUserOrderEntryEnablement(orderStruct.sessionNames[i], orderStruct.classKey);
        }
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        checkSystemAvailability(orderStruct);
        
        OrderCallSnapshot.startServerCall();
        getOrderHandlingService().acceptStrategyUpdate(remainingQuantity, orderStruct, legOrderEntryStructs);
        OrderCallSnapshot.endServerCall();
    }

    public void acceptUpdate(int remainingQuantity, OrderStruct orderStruct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        for(int i = 0; i<orderStruct.sessionNames.length; i++)
        {
            verifyUserOrderEntryEnablement(orderStruct.sessionNames[i], orderStruct.classKey);
        }
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        checkSystemAvailability(orderStruct);
        
        OrderCallSnapshot.startServerCall();
        getOrderHandlingService().acceptUpdate(remainingQuantity, orderStruct);
        OrderCallSnapshot.endServerCall();
    }

    public InternalizationOrderResultStruct acceptInternalizationOrder(OrderStruct primaryOrder, OrderStruct matchedOrder, short matchType)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        Log.information(this, createOrderLog("acceptInternalizationOrder", 0, sessionManager.toString(), primaryOrder.sessionNames[0],
                        getOrderIdString(primaryOrder.orderId), getOrderIdString(matchedOrder.orderId), primaryOrder.productKey));
        String[] sessionNames = primaryOrder.sessionNames;

        for(int i = 0; i < sessionNames.length; i++)
        {
            String sessionName = sessionNames[i];
            verifyUserAuctionEnablement(sessionName, primaryOrder.classKey);
        }

        sessionNames = matchedOrder.sessionNames;
        for(int i = 0; i < sessionNames.length; i++)
        {
            String sessionName = sessionNames[i];
            verifyUserAuctionEnablement(sessionName, matchedOrder.classKey);
        }
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        //I guess since both the order go to the same OHS checking availability for one order would suffice
        checkSystemAvailability(primaryOrder);
        
        OrderCallSnapshot.startServerCall();
        InternalizationOrderResultStruct result = getOrderHandlingService().acceptInternalizationOrders(primaryOrder, matchedOrder, matchType);
        OrderCallSnapshot.endServerCall();
        return result;

    }

    public InternalizationOrderResultStruct acceptInternalizationStrategyOrder(OrderStruct primaryOrder, LegOrderEntryStruct[] primaryLegOrderEntryStruct,
                                                                            OrderStruct matchedOrder, LegOrderEntryStruct[] matchedLegOrderEntryStruct, short matchType)
                throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        String smgr = sessionManager.toString();
        String primary = getOrderIdString(primaryOrder.orderId);
        String match = getOrderIdString(matchedOrder.orderId);
        StringBuilder calling = new StringBuilder(smgr.length()+primary.length()+match.length()+85);
        calling.append("calling acceptInternalizationStrategyOrder for ").append(smgr)
               .append(" primary").append(primary)
               .append(" productKey:").append(primaryOrder.productKey)
               .append(" match").append(match);
        Log.information(this, calling.toString());

        String[] sessionNames = primaryOrder.sessionNames;

        for(int i = 0; i < sessionNames.length; i++)
        {
            String sessionName = sessionNames[i];
            verifyUserAuctionEnablement(sessionName, primaryOrder.classKey);
        }

        sessionNames = matchedOrder.sessionNames;
        for(int i = 0; i < sessionNames.length; i++)
        {
            String sessionName = sessionNames[i];
            verifyUserAuctionEnablement(sessionName, matchedOrder.classKey);
        }
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        //I guess since both the order go to the same OHS checking availability for one order would suffice
        checkSystemAvailability(primaryOrder);
        
        OrderCallSnapshot.startServerCall();
        InternalizationOrderResultStruct result =
                getOrderHandlingService().acceptInternalizationStrategyOrders(primaryOrder, primaryLegOrderEntryStruct,
                                                                    matchedOrder, matchedLegOrderEntryStruct, matchType);
        OrderCallSnapshot.endServerCall();
        return result;
    }

    public InternalizationOrderResultStruct acceptInternalizationStrategyOrderV7(OrderStruct primaryOrder, LegOrderEntryStructV2[] primaryLegOrderEntryStructV2,
                                                                            OrderStruct matchedOrder, LegOrderEntryStructV2[] matchedLegOrderEntryStructV2, short matchType)
                throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        Log.information(this, "calling acceptInternalizationStrategyOrderV7 for " + sessionManager
                + " primary" + getOrderIdString(primaryOrder.orderId) + " productKey:" + primaryOrder.productKey
                + " match" + getOrderIdString(matchedOrder.orderId));


        String[] sessionNames = primaryOrder.sessionNames;

        for(int i = 0; i < sessionNames.length; i++)
        {
            String sessionName = sessionNames[i];
            verifyUserAuctionEnablement(sessionName, primaryOrder.classKey);
        }

        sessionNames = matchedOrder.sessionNames;
        for(int i = 0; i < sessionNames.length; i++)
        {
            String sessionName = sessionNames[i];
            verifyUserAuctionEnablement(sessionName, matchedOrder.classKey);
        }
        try
        {
        	TransactionTimingUtil.resetEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to re-set EntityID! Exception details: " + e.getMessage());
        }
        
        //TCS - check the OHS status before sending the order. 
        //I guess since both the order go to the same OHS checking availability for one order would suffice
        checkSystemAvailability(primaryOrder);
        
        OrderCallSnapshot.startServerCall();
        InternalizationOrderResultStruct result =
                getOrderHandlingService().acceptInternalizationStrategyOrdersV2(primaryOrder, primaryLegOrderEntryStructV2,
                                                                    matchedOrder, matchedLegOrderEntryStructV2, matchType);
        OrderCallSnapshot.endServerCall();
        return result;
    }


    public OrderDetailStruct getOrderById(OrderIdStruct orderId)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "calling getOrderById for " + sessionManager + " orderId:" + getOrderIdString(orderId));
        }
        return doGetOrderById(orderId, false);
    }

    // If cacheOnly is set to false, the call is sent to server if the order is not found in the cache.
    // If cacheOnly is set to true, then the only time we send it down to server is when we see the orderId in
    // the orderEntryCache.
    private OrderDetailStruct doGetOrderById(OrderIdStruct orderId, boolean cacheOnly)
    	throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
	{
        OrderDetailStruct   aOrderDetailStruct  = null;
        long startTime = System.nanoTime();
        long entityId = 0L;
        boolean exceptionWasThrown = true;
	   // first check if the order is in the order cache
	   OrderStruct         anOrderStruct       = orderQueryCache.getOrderFromOrderCache(orderId);
        boolean isCacheMiss = false;
        if (anOrderStruct != null)
        {
		   // order found in order cache
            aOrderDetailStruct = buildOrderDetailStruct(anOrderStruct, StatusUpdateReasons.QUERY);
        }
        else
        {
		   // order not present in order cache
            isCacheMiss = true;
            if (Log.isDebugOn())
            {
                Log.debug(this, "session : " + sessionManager + " : Cache Miss for Order ID (" + getOrderIdString(orderId) + ").");
            }
	       // if the call is local, check if the order is pending/failed_maybe in the orderEntryCache.
	       if (cacheOnly && orderQueryCache.checkOrderEntryCache(orderId) == false)
	       {
	    	   // cacheOnly and order not in orderCache or in orderrEntryCache.
	    	   throw ExceptionBuilder.notFoundException("No Order found for:" + getOrderIdString(orderId), DataValidationCodes.INVALID_ORDER_ID);
	       }
            try
            {
                try
                {
                	entityId = TransactionTimingUtil.getEntityID();
                }
                catch (Exception e)
                {
                	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
                }
                TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );

                anOrderStruct = getOrderHandlingService().getOrderById(thisUserId,orderId);
                anOrderStruct.transactionSequenceNumber = 0;
	           aOrderDetailStruct = buildOrderDetailStruct(anOrderStruct, StatusUpdateReasons.QUERY);
	           orderQueryCache.put(anOrderStruct);
                exceptionWasThrown = false;
            }
            catch (com.cboe.exceptions.NotFoundException e)
            {
                throw ExceptionBuilder.notFoundException("No Order found for:" + getOrderIdString(orderId), DataValidationCodes.INVALID_ORDER_ID);
            }
            finally{
            	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
            	        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

            }
        }
        String oid = getOrderIdString(orderId);
        StringBuilder totaltime = new StringBuilder(oid.length()+53);
        totaltime.append("getOrderById ").append(oid)
                 .append(" total time ").append( (System.nanoTime() - startTime))
                 .append(" ns cacheMiss ").append(isCacheMiss);
        Log.information(this, totaltime.toString());
        return aOrderDetailStruct;
    }

     private OrderDetailStruct doGetOrderByIdForFIX(OrderIdStruct orderId, boolean cacheOnly)
    	throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
	{
        OrderDetailStruct   aOrderDetailStruct  = null;
        long startTime = System.nanoTime();
        long entityId = 0L;
        boolean exceptionWasThrown = true;
	   // first check if the order is in the order cache
	   OrderStruct         anOrderStruct       = orderQueryCache.getOrderFromOrderCache(orderId);
        boolean isCacheMiss = false;
        if (anOrderStruct != null)
        {
		   // order found in order cache
            aOrderDetailStruct = buildOrderDetailStruct(anOrderStruct, StatusUpdateReasons.QUERY);
        }
        else
        {
		   // order not present in order cache
            isCacheMiss = true;
            if (Log.isDebugOn())
            {
                Log.debug(this, "session : " + sessionManager + " : Cache Miss for Order ID (" + getOrderIdString(orderId) + ").");
            }
	       // if the call is local, check if the order is pending/failed_maybe in the orderEntryCache.
           FixOrderQueryCache.OrderState state = orderQueryCache.getOrderStateFromOrderEntryCache(orderId);
	       if (cacheOnly && state == FixOrderQueryCache.OrderState.NOT_FOUND)
	       {
	    	   // cacheOnly and order not in orderCache or in orderEntryCache.
	    	   throw ExceptionBuilder.notFoundException("No Order found for:" + getOrderIdString(orderId), DataValidationCodes.INVALID_ORDER_ID);
	       }
            try
            {
                try
                {
                	entityId = TransactionTimingUtil.getEntityID();
                }
                catch (Exception e)
                {
                	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
                }
                TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId, TransactionTimer.Enter );

                anOrderStruct = getOrderHandlingService().getOrderById(thisUserId,orderId);
                anOrderStruct.transactionSequenceNumber = 0;
	            aOrderDetailStruct = buildOrderDetailStruct(anOrderStruct, StatusUpdateReasons.QUERY);
                if (state == FixOrderQueryCache.OrderState.FAILED_MAYBE){
	                orderQueryCache.put(anOrderStruct);
                }
                exceptionWasThrown = false;
            }
            catch (com.cboe.exceptions.NotFoundException e)
            {
                throw ExceptionBuilder.notFoundException("No Order found for:" + getOrderIdString(orderId), DataValidationCodes.INVALID_ORDER_ID);
            }
            finally{
            	TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getOrderEmitPoint(), entityId,
            	        exceptionWasThrown?TransactionTimer.LeaveWithException:TransactionTimer.Leave );

            }
        }
        String oid = getOrderIdString(orderId);
        StringBuilder totaltime = new StringBuilder(oid.length()+53);
        totaltime.append("getOrderByIdFIX ").append(oid)
                 .append(" total time ").append( (System.nanoTime() - startTime))
                 .append(" ns cacheMiss ").append(isCacheMiss);
        Log.information(this, totaltime.toString());
        return aOrderDetailStruct;
    }

    public OrderDetailStruct getOrderByIdFromCacheForFIX(OrderIdStruct orderId)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
      {
          if(Log.isDebugOn())
          {
              Log.debug(this, "calling getOrderByIdFromCacheforFIX for " + sessionManager + " orderId:" + getOrderIdString(orderId));
          }
          return doGetOrderByIdForFIX(orderId, true);
      }

    public OrderDetailStruct getOrderByIdFromCache(OrderIdStruct orderId)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "calling getOrderByIdFromCache for " + sessionManager + " orderId:" + getOrderIdString(orderId));
        }
        return doGetOrderById(orderId, true);
    }

    public PendingOrderStruct[] getPendingAdjustmentOrdersByProduct(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling getPendingAdjustmentOrdersByProduct for " + sessionManager);
        }

        try
        {
            ProductKeysStruct productKeys = getProductQueryServiceAdapter().getProductByKey(productKey).productKeys;
            verifyUserOrderQueryEnablement(sessionName, productKeys.classKey);

            try
            {
            	TransactionTimingUtil.setEntityID();
            }
            catch (Exception e)
            {
            	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
            }

            OrderStruct[] pOrders = getOrderHandlingService().getPendingAdjustmentOrdersByProduct(sessionName, productKeys, thisUserId);

            PendingOrderStruct[] pendingOrders = new PendingOrderStruct[ pOrders.length ];
            PendingNameStruct thisProductNameStruct = getProductQueryService().getPendingAdjustmentNameByProduct( productKey );
            OrderDetailStruct orderD;
            for ( int i=0; i<pOrders.length; i++)
            {
                    orderD = getOrderByIdFromCache( pOrders[i].orderId );
                    pendingOrders[i] = new PendingOrderStruct();
                    pendingOrders[i].pendingOrder = pOrders[i];
                    pendingOrders[i].currentOrder = orderD.orderStruct;
                    pendingOrders[i].pendingProductName =  thisProductNameStruct;
            }
            return  pendingOrders;
        }
        catch(NotFoundException e)
        {
          Log.exception(this, "session : " + sessionManager, e);
          return new PendingOrderStruct[0];
        }
    }

    public PendingOrderStruct[] getPendingAdjustmentOrdersByClass(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling getPendingAdjustmentOrdersByClass for " + sessionManager);
        }
        verifyUserOrderQueryEnablement(sessionName, classKey);
        OrderStruct[] pOrders = null;

        try
        {
        	TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }

        pOrders = getOrderHandlingService().getPendingAdjustmentOrdersByClass( sessionName, classKey, thisUserId);

        if (Log.isDebugOn())
        {
        Log.debug(this, "session : " + sessionManager + " : the size of the Pending Orders: " + pOrders.length);
        }

        PendingOrderStruct[] pendingOrders = new PendingOrderStruct[ pOrders.length ];
        OrderDetailStruct orderD;
        int productKey = -1;

        for ( int i=0; i<pOrders.length; i++) {
            productKey = pOrders[i].productKey;

            try {
                orderD = getOrderByIdFromCache( pOrders[i].orderId );
                pendingOrders[i] = new PendingOrderStruct();
                pendingOrders[i].pendingOrder = pOrders[i];
                pendingOrders[i].currentOrder = orderD.orderStruct;
                pendingOrders[i].pendingProductName =  getProductQueryService().getPendingAdjustmentNameByProduct( productKey );
            }
            catch (NotFoundException e) {
                    Log.alarm(this, "session : " + sessionManager + " : Invalid Product in Order: (" + getOrderIdString(pOrders[i].orderId) + ")." + e.toString());
                    //Continue on to the next order
            }
        }

        return  pendingOrders;

    }

    public void publishOrdersForFirm(ExchangeFirmStruct exchangeFirmStruct)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {

        try
        {
        	TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }

        getOrderHandlingService().publishOrdersForFirm(exchangeFirmStruct);
    }

    public void publishOrders()
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {

        try
        {
        	TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }

        getOrderHandlingService().publishOrdersForUser(thisUserId);
    }

    public ActivityHistoryStruct queryOrderHistory(String sessionName, int productKey, OrderIdStruct orderIdStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {

        try
        {
        	TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
        	Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }

        return getOrderHandlingService().queryOrderHistory(thisUserId, sessionName, productKey, orderIdStruct);
    }

    public void requestForQuote(RFQStruct aRFQ)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling acceptRequestForQuote for " + sessionManager + " productKey:" + aRFQ.productKeys.productKey);
        }
        verifyUserOrderRFQEnablement(aRFQ.sessionName, aRFQ.productKeys.classKey);
        OrderCallSnapshot.startServerCall();
        getMarketMakerQuoteService().requestForQuote(aRFQ, thisUserId);
        OrderCallSnapshot.endServerCall();
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
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling getOrdersForClass for " + sessionManager);
        }
        OrderStruct[] orders = orderQueryCache.getOrdersByClass(productClass);
        publishUnAckedOrders();
        return buildOrderDetailStruct( orders, StatusUpdateReasons.NEW );
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
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling getOrdersForProduct for " + sessionManager);
        }
        OrderStruct[] orders = orderQueryCache.getOrdersForProduct(pKey);
        publishUnAckedOrders();
        return buildOrderDetailStruct( orders, StatusUpdateReasons.NEW );
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
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling getOrdersForSession for " + sessionManager);
        }
        verifyUserOrderQueryEnablementForSession(sessionName);

        OrderStruct[] orders = orderQueryCache.getOrdersForSession(sessionName);
        publishUnAckedOrders();
        return buildOrderDetailStruct( orders, StatusUpdateReasons.NEW );
    }

    /**
      * This returns all cached orders for the requested product type and registers the listener object
      * future data updates.  If the caller does not want further updates, pass a null listener object.
      * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
      */
    public OrderDetailStruct[] getOrdersForType(short type)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling getOrdersForType for " + sessionManager);
        }

        OrderStruct[] orders = orderQueryCache.getAllOrdersForType(type);
        publishUnAckedOrders();
        return buildOrderDetailStruct( orders, StatusUpdateReasons.NEW );
    }

    public void publishUnAckedOrders()
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ServicesHelper.getOrderStatusAdminPublisher().subscribeOrderStatus(thisUserId);
    }

    public void publishUnAckedOrdersForClass(int classKey)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        int[] groups = ServicesHelper.getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct paramStruct = new RoutingParameterStruct(groups, "UNKNOW_SESSION", classKey, (short)0);
        ServicesHelper.getOrderStatusAdminPublisher().publishUnackedOrderStatus(paramStruct , thisUserId);
    }

    private OrderQueryCache getOrderQueryCache()
    {
        try
        {
            return OrderQueryCacheFactory.find(thisUserId);
        }
        catch( Exception e )
        {
            Log.exception(this, "session : " + sessionManager + " : Fatal error retrieving OrderQueryCache!" + thisUserId, e );
        }

        return null;
    }

    private String getOrderIdString(OrderIdStruct orderId)
    {
        StringBuilder toStr = new StringBuilder(70);
//Printed in this format -> CBOE:690:PPO:12
        toStr.append(" OrderEntry:");
        toStr.append(orderId.executingOrGiveUpFirm.exchange).append(':');
        toStr.append(orderId.executingOrGiveUpFirm.firmNumber).append(':');
        toStr.append(orderId.branch).append(':').append(orderId.branchSequenceNumber);
        toStr.append(':').append(orderId.orderDate);
        toStr.append(':').append(orderId.highCboeId).append(':').append(orderId.lowCboeId);
        return toStr.toString();
    }

    private OrderDetailStruct buildOrderDetailStruct( OrderStruct order, short statusUpdateReason)
    {
        ProductNameStruct productName = null;
        try
        {
            productName = getProductQueryServiceAdapter().getProductByKey(order.productKey).productName;
        }
        catch (org.omg.CORBA.UserException e)
        {
            Log.exception(this, "session : " + sessionManager, e);
            return null;
        }
        return new OrderDetailStruct( productName, statusUpdateReason, order);
    }

    protected ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(pqAdapter == null)
        {
            pqAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqAdapter;
    }

    protected ProductQueryService getProductQueryService()
    {
        if(productQueryService == null)
        {
            productQueryService = ServicesHelper.getProductQueryService();
        }
        return productQueryService;
    }

    /**
     * Returns a reference to order status subscription service.
     *
    private OrderStatusConsumerHome getOrderStatusConsumerHome()
    {
        if (orderStatusConsumerHome == null)
        {
            orderStatusConsumerHome = ServicesHelper.getOrderStatusConsumerHome();
        }
        return orderStatusConsumerHome;
    }/*

    /**
     * Returns a reference to order status subscription service.
     */
    private OrderStatusConsumerV2Home getOrderStatusConsumerV2Home()
    {
        if (orderStatusConsumerV2Home == null)
        {
            orderStatusConsumerV2Home = ServicesHelper.getOrderStatusConsumerV2Home();
        }
        return orderStatusConsumerV2Home;
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



    private int lookupClassKeyFromProductKey(int productKey) throws DataValidationException, SystemException, AuthorizationException, CommunicationException
      {
          try
          {
              return getProductQueryServiceAdapter().getProductByKey(productKey).productKeys.classKey;
          }
          catch (NotFoundException e)
          {
              throw new DataValidationException(new StringBuffer("Product ").append(productKey).append(" Not found").toString(), e.details);
          }

      }

    public LightOrderResultStruct acceptLightOrder(LightOrderEntryStruct orderEntryStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        boolean failed = true;
        OrderCallSnapshot.enter();
        long entityId = setEntityId();
        try
        {

            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getLightOrderEmitPoint(), entityId, TransactionTimer.Enter);
            int classKey = lookupClassKeyFromProductKey(orderEntryStruct.productKey);
            String activeSession=orderEntryStruct.activeSession;
            verifyUserLightOrderEntryEnablement(activeSession, classKey);
            rateManager.monitorRate(activeSession, System.currentTimeMillis(), "acceptLightOrder", RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER);
            LightOrderNewRequestCodec newRequestCodec =
                    (LightOrderNewRequestCodec) LightOrderNewRequestCodec.getInstance().newCopy();
            newRequestCodec.setCmta(orderEntryStruct.cmtaExchange, orderEntryStruct.cmtaFirmNumber);
            newRequestCodec.setOrderAttr(orderEntryStruct.isIOC, orderEntryStruct.isNBBOProtected, orderEntryStruct.coverage, orderEntryStruct.positionEffect, orderEntryStruct.side);
            newRequestCodec.setOrderId(orderEntryStruct.branch, orderEntryStruct.branchSequenceNumber);
            newRequestCodec.setOriginCode((byte) orderEntryStruct.orderOriginType);
            newRequestCodec.setPdpm(orderEntryStruct.pdpm);
            newRequestCodec.setProductKey(orderEntryStruct.productKey);
            newRequestCodec.setQuantity(orderEntryStruct.originalQuantity);
            newRequestCodec.setUserAssignedId(orderEntryStruct.userAssignedId);
            newRequestCodec.setUserId(thisUserId);
            newRequestCodec.setValuedPrice(orderEntryStruct.Price);
            if (Log.isDebugOn())
            {
                StringBuilder msg = new StringBuilder(500);
                msg.append("LightOrderNewRequestCodec:").append(newRequestCodec).append("\n");
                msg.append("LightOrderEntryStruct activeSession = ").append(activeSession).append("\n");
                msg.append("branch = ").append(orderEntryStruct.branch).append("\n");
                msg.append("branchSequenceNumber = ").append(orderEntryStruct.branchSequenceNumber).append("\n");
                msg.append("coverage = ").append(orderEntryStruct.coverage).append("\n");
                msg.append("exchange = ").append(orderEntryStruct.cmtaExchange).append("\n");
                msg.append("firmNumber = ").append(orderEntryStruct.cmtaFirmNumber).append("\n");
                msg.append("isIOC = ").append(orderEntryStruct.isIOC).append("\n");
                msg.append("isNBBOProtected = ").append(orderEntryStruct.isNBBOProtected).append("\n");
                msg.append("orderOriginType = ").append(orderEntryStruct.orderOriginType).append("\n");
                msg.append("originalQuantity = ").append(orderEntryStruct.originalQuantity).append("\n");
                msg.append("pdpm = ").append(orderEntryStruct.pdpm).append("\n");
                msg.append("positionEffect = ").append(orderEntryStruct.positionEffect).append("\n");
                msg.append("Price = ").append(orderEntryStruct.Price).append("\n");
                msg.append("productKey = ").append(orderEntryStruct.productKey).append("\n");
                msg.append("side = ").append(orderEntryStruct.side).append("\n");
                msg.append("userId = ").append(thisUserId).append("\n");
                msg.append("userAssignedId = ").append(orderEntryStruct.userAssignedId).append("\n");
                Log.debug(this, msg.toString());
            }
            LightOrderBuffer buf = LightOrderBuffer.getThreadLocalInstance();
            byte[] request = buf.encode(newRequestCodec);
            byte[] response = getLightOrderHandlingService().acceptOrder(request, orderEntryStruct.productKey, orderEntryStruct.activeSession,entityId);
            LightOrderNewResponseCodec responseCodec = (LightOrderNewResponseCodec) buf.decode(response);
            LightOrderResultStruct result = new LightOrderResultStruct();
            result.branch = responseCodec.getBranch();
            result.branchSequenceNumber = responseCodec.getBranchSequence();
            result.cancelledQuantity = responseCodec.getCanceledQuantity();
            result.leavesQuantity = responseCodec.getLeavesQuantity();
            result.orderHighId = responseCodec.getHighId();
            result.orderLowId = responseCodec.getLowId();
            result.reason = (byte) responseCodec.getReturnCode();
            result.side = orderEntryStruct.side;
            result.time = DateWrapper.convertToDateTime(responseCodec.getTimestamp());
            result.tradedQuantity = responseCodec.getFilledQuantity();
            if (Log.isDebugOn())
            {
                StringBuilder msg = new StringBuilder(400);
                msg.append("LightOrderNewResponseCodec:").append(responseCodec).append("\n");
                msg.append("branch = ").append(result.branch).append("\n");
                msg.append("branchSequenceNumber = ").append(result.branchSequenceNumber).append("\n");
                msg.append("cancelledQuantity = ").append(result.cancelledQuantity).append("\n");
                msg.append("leavesQuantify = ").append(result.leavesQuantity).append("\n");
                msg.append("orderHighId = ").append(result.orderHighId).append("\n");
                msg.append("orderLowId = ").append(result.orderLowId).append("\n");
                msg.append("reason = ").append(result.reason).append("\n");
                msg.append("side = ").append(result.side).append("\n");
                msg.append("time = ").append(result.time).append("\n");
                msg.append("tradedQuantity = ").append(result.tradedQuantity).append("\n");
                Log.debug(this, msg.toString());
            }
            failed = false;
            OrderCallSnapshot.done();
            Log.information(this, createLightOrderLogSnapshot("acceptLightOrder", sessionManager.toString(),orderEntryStruct.activeSession, sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.acronym, classKey, orderEntryStruct, entityId));
            return result;
        }
        finally
        {
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getLightOrderEmitPoint(), entityId,
                                                     failed ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);
        }
    }

    public LightOrderResultStruct acceptLightOrderCancelRequest(String branch, int branchSequenceNumber, int productKey,
                                                                String activeSession, String userAssignedCancelId) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        boolean failed = true;
        long entityId = setEntityId();
        OrderCallSnapshot.enter();
        try
        {
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelLightOrderEmitPoint(), entityId,
                                                     TransactionTimer.Enter);
            int classKey = lookupClassKeyFromProductKey(productKey);
            verifyUserLightOrderEntryEnablement(activeSession, classKey);
            LightOrderCancelRequestCodec requestCodec =
                    (LightOrderCancelRequestCodec) LightOrderCancelRequestCodec.getInstance().newCopy();
            requestCodec.setOrderId(branch, branchSequenceNumber);
            requestCodec.setProductKey(productKey);
            requestCodec.setUserAssignedCancelId(userAssignedCancelId);
            requestCodec.setUserId(thisUserId);
            if (Log.isDebugOn())
            {
                StringBuilder msg = new StringBuilder(400);
                msg.append("LightOrderCancelRequestCodec:").append(requestCodec).append("\n");
                msg.append("branch = ").append(branch).append("\n");
                msg.append("branchSequenceNumber = ").append(branchSequenceNumber).append("\n");
                msg.append("productKey = ").append(productKey).append("\n");
                msg.append("classKey = ").append(classKey).append("\n");
                msg.append("userAssignedCancelId").append(userAssignedCancelId).append("\n");
                msg.append("userId").append(thisUserId).append("\n");
                Log.debug(this, msg.toString());
            }
            LightOrderBuffer buf = LightOrderBuffer.getThreadLocalInstance();
            byte[] request = buf.encode(requestCodec);
            byte[] response = getLightOrderHandlingService().acceptLightOrderCancelRequest(request, productKey, activeSession,entityId);
            LightOrderCancelResponseCodec responseCodec = (LightOrderCancelResponseCodec) buf.decode(response);
            LightOrderResultStruct result = buildLightOrderResultStruct(responseCodec);
            failed = false;
            OrderCallSnapshot.done();
            Log.information(this, LoggingUtil.createLightOrderCancelLogSnapshot("acceptLightOrderCancelRequest", sessionManager.toString(),activeSession, sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.acronym, classKey, productKey, result, entityId));
            return result;
        }
        finally
        {
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelLightOrderEmitPoint(), entityId,
                                                     failed ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);
        }
    }

    public LightOrderResultStruct acceptLightOrderCancelRequestById(int orderHighId, int orderLowId, int productKey,
                                                                    String activeSession, String userAssignedCancelId) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        boolean failed = true;
        long entityId = setEntityId();
        OrderCallSnapshot.enter();
        try
        {
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelLightOrderEmitPoint(), entityId,
                                                     TransactionTimer.Enter);
            int classKey = lookupClassKeyFromProductKey(productKey);
            verifyUserLightOrderEntryEnablement(activeSession, classKey);
            LightOrderCancelRequestCodec requestCodec =
                    (LightOrderCancelRequestCodec) LightOrderCancelRequestCodec.getInstance().newCopy();
            requestCodec.setHighLowId(orderHighId, orderLowId);
            requestCodec.setProductKey(productKey);
            requestCodec.setUserAssignedCancelId(userAssignedCancelId);
            requestCodec.setUserId(thisUserId);
            if (Log.isDebugOn())
            {
                StringBuilder msg = new StringBuilder(400);
                msg.append("orderHighId = ").append(orderHighId).append("\n");
                msg.append("orderLowId = ").append(orderLowId).append("\n");
                msg.append("productKey = ").append(productKey).append("\n");
                msg.append("classKey = ").append(classKey).append("\n");
                msg.append("userAssignedCancelId").append(userAssignedCancelId).append("\n");
                msg.append("userId").append(thisUserId).append("\n");
                msg.append("CODEC").append(requestCodec).append("\n");
                Log.debug(this, msg.toString());
            }
            LightOrderBuffer buf = LightOrderBuffer.getThreadLocalInstance();
            byte[] request = buf.encode(requestCodec);
            byte[] response = getLightOrderHandlingService().acceptLightOrderCancelRequestById(request, productKey, activeSession,entityId);
            LightOrderCancelResponseCodec responseCodec = (LightOrderCancelResponseCodec) buf.decode(response);
            LightOrderResultStruct result = buildLightOrderResultStruct(responseCodec);
            failed = false;
            OrderCallSnapshot.done();
            Log.information(this, LoggingUtil.createLightOrderCancelByIdLogSnapshot("acceptLightOrderCancelRequestById", sessionManager.toString(),activeSession, sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.acronym, classKey, productKey, result, entityId,orderHighId,orderLowId));

            return result;
        }
        finally
        {
            TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getCancelLightOrderEmitPoint(),
                                                     entityId, failed ? TransactionTimer.LeaveWithException : TransactionTimer.Leave);
        }


    }

    private long setEntityId()
    {
        try
        {
            return TransactionTimingUtil.setEntityID();
        }
        catch (Exception e)
        {
            Log.information(this, "Unable to set EntityID! Exception details: " + e.getMessage());
        }
        return 0L;
    }
    private LightOrderResultStruct buildLightOrderResultStruct(LightOrderCancelResponseCodec responseCodec)
    {
        LightOrderResultStruct result = new LightOrderResultStruct();
        result.branch = responseCodec.getBranch();
        result.branchSequenceNumber = responseCodec.getBranchSequence();
        result.cancelledQuantity = responseCodec.getCanceledQuantity();
        result.leavesQuantity = responseCodec.getLeavesQuantity();
        result.orderHighId = responseCodec.getHighId();
        result.orderLowId = responseCodec.getLowId();
        result.reason = (byte) responseCodec.getReturnCode();
        result.side = responseCodec.getSide();
        result.time = DateWrapper.convertToDateTime(responseCodec.getTimestamp());
        result.tradedQuantity = responseCodec.getTradedQuantity();
        if (Log.isDebugOn())
        {
            StringBuilder msg = new StringBuilder(400);
            msg.append("LightOrderCancelResponseCodec:").append(responseCodec).append("\n");
            msg.append("branch = ").append(result.branch).append("\n");
            msg.append("branchSequenceNumber = ").append(result.branchSequenceNumber).append("\n");
            msg.append("cancelledQuantity = ").append(result.cancelledQuantity).append("\n");
            msg.append("leavesQuantify = ").append(result.leavesQuantity).append("\n");
            msg.append("orderHighId = ").append(result.orderHighId).append("\n");
            msg.append("orderLowId = ").append(result.orderLowId).append("\n");
            msg.append("reason = ").append(result.reason).append("\n");
            msg.append("side = ").append(result.side).append("\n");
            msg.append("time = ").append(result.time).append("\n");
            msg.append("tradedQuantity = ").append(result.tradedQuantity).append("\n");
            Log.debug(this, msg.toString());
        }
        return result;
    }


    
    
    private void checkSystemAvailability(OrderStruct order) throws SystemException{
    	if(Log.isDebugOn()) {
    		Log.debug(this, "TCS checkSystemAvailability called for class : "+ order.classKey+" SessionName : "+order.sessionNames[0]);
    	}
    	
    	if(!pwManager.isServerDownListEmpty() && pwManager.isProcessDown(order)) {
    		Log.information("Cluster Down for classkey:<"+order.classKey+"> SystemError thrown to clients");
    		throw ExceptionBuilder.systemException("Cluster Down for classkey:<"+order.classKey+">", -1);
    	}
	}
    
    private TCSProcessWatcherManager getProcessWatcherManager() {
    	try {
    		return ServicesHelper.getTCSProcessWatcherManagerHome().create();
    	}catch(Exception e) {
    		Log.alarm(this, "Exception while creating TCSProcessWatcherManagerHome");
    	}
    	return null;
	}

    
    private void buildCancelReport(CancelRequestStruct cancelRequestStruct,OrderStruct orderStruct)
    {
    	//Cancel Report Type
    	short cancelReportType;
        short REGULAR_REPORT = (short) 1;
        short STRATEGY_REPORT = (short) 2;
        short STRATEGY_LEG_REPORT = (short) 3;
        //groups is pcs group key which tell you where the product is.
        // This field is not use in CAS. CAS is filling in with empty array see in OrderQuerCache
        int[] groups = CollectionHelper.EMPTY_int_ARRAY;                            
        short statusChange = StatusUpdateReasons.NEW; //7
                    
        
        
        
        synchronized(orderStruct){
        	//Format orderStruct
        	//Change quantity of original order for cancel report and cancel reason
        	orderStruct.cancelledQuantity = orderStruct.originalQuantity;
            orderStruct.leavesQuantity = 0;
            orderStruct.state = StatusUpdateReasons.CANCEL; //2
            orderStruct.transactionSequenceNumber = 2 ; // cancel report
            
        	
            //-----------------------Format Main Order Cancel Report Package-----------------------------------------------------------//
        	
            cancelReportType = REGULAR_REPORT;
        	if (orderStruct.legOrderDetails.length > 0)
        	{ 
    		     cancelReportType = STRATEGY_REPORT;
        	}
        	CancelReportStruct[] cancelReport = new CancelReportStruct[1]; 
        	cancelReport[0] = buildCancelReport(cancelRequestStruct, orderStruct.productKey, orderStruct.originalQuantity,cancelReportType);           	
        	OrderIdCancelReportContainer cancelReportContainer = new OrderIdCancelReportContainer(orderStruct, cancelReport);     	
            GroupCancelReportContainer cancelContainer = new GroupCancelReportContainer(orderStruct.userId,  groups, statusChange, cancelReportContainer );
            dispatchCancelReport(cancelContainer, orderStruct.userId, orderStruct.orderId.executingOrGiveUpFirm,
                                 orderStruct.transactionSequenceNumber, orderStruct.orderId);
          
            //------------------------Format Leg's Cancel Report ----------------------------------------------------------------------//
            //Check to see if complex order. Complex order will need cancel report for each leg.
            //Complex order will send out number of cancel report = no. of legs + 1
        	if (orderStruct.legOrderDetails.length > 0)
        	{       		
        		for (int l=0; l < orderStruct.legOrderDetails.length; l++)
        		{  
        			//Format orderStruct
                	//Change leg cancel quantity
        		   
            			cancelReportType = STRATEGY_LEG_REPORT;
                        orderStruct.legOrderDetails[l].cancelledQuantity = orderStruct.legOrderDetails[l].originalQuantity;
                        orderStruct.legOrderDetails[l].leavesQuantity = 0;
                        orderStruct.transactionSequenceNumber++;
            			CancelReportStruct[] legCancelReport = new CancelReportStruct[1]; 
                		legCancelReport[0] = buildCancelReport(cancelRequestStruct, orderStruct.legOrderDetails[l].productKey,  orderStruct.legOrderDetails[l].originalQuantity,cancelReportType);		       		
                		legCancelReport[0].transactionSequenceNumber = l+3; // each leg's transactionSequenceNumber will have bump by one from main package 
                		
                        cancelReportContainer = new OrderIdCancelReportContainer(orderStruct, legCancelReport);               	
                        cancelContainer = new GroupCancelReportContainer(orderStruct.userId,  groups, statusChange, cancelReportContainer );        		
                        
                        dispatchCancelReport(cancelContainer, orderStruct.userId, orderStruct.orderId.executingOrGiveUpFirm,
                                             orderStruct.transactionSequenceNumber, orderStruct.orderId);
        		    
        		}     		
        	} 
        }   
    }

    private void dispatchCancelReport(GroupCancelReportContainer cancelContainer, String userId, com.cboe.idl.cmiUser.ExchangeFirmStruct executingOrGiveUpFirm,
                                      int transactionSequnceNumber,OrderIdStruct orderId )
    {
    	// This event will publish internal CAS channel to OrderQueryCache. 
        // OrderQueryCache will handle remove and put order to proper map.
        ChannelKey channelKey = new ChannelKey(ChannelKey.CANCEL_REPORT, userId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, cancelContainer);    	
        internalEventChannel.dispatch(event);
        
        ExchangeFirmStructContainer firmKeyContainer = new ExchangeFirmStructContainer(executingOrGiveUpFirm);
        channelKey = new ChannelKey(ChannelKey.CANCEL_REPORT_BY_FIRM, firmKeyContainer);
        event = internalEventChannel.getChannelEvent(this, channelKey, cancelContainer);
        internalEventChannel.dispatch(event);
        
        StringBuilder sb = new StringBuilder(110);
        sb.append("internal event received -> CancelReport : Seq # ");
        sb.append(transactionSequnceNumber);
        sb.append(getOrderIdString(orderId));
        sb.append(": statusChange=");
        sb.append(cancelContainer.getStatusChange());
        Log.information(this,sb.toString());
    }
    
    
    private CancelReportStruct buildCancelReport(CancelRequestStruct cancelRequest, int productKey, int cancelledQuantity, short cancelReportType)
    {
    	//Fill out info. for fake cancel report
    	CancelReportStruct cancelReport = new CancelReportStruct();
    	
    	cancelReport.orderId = cancelRequest.orderId;
    	cancelReport.cancelReportType = cancelReportType ; 
    	cancelReport.cancelReason = StatusUpdateReasons.CANCEL; // looking at struct server return
    	cancelReport.productKey = productKey;
    	cancelReport.sessionName = cancelRequest.sessionName;
    	cancelReport.cancelledQuantity = cancelledQuantity;
    	cancelReport.tlcQuantity = 0;
    	cancelReport.mismatchedQuantity = 0;
    	cancelReport.timeSent = TimeServiceWrapper.toDateTimeStruct();
    	cancelReport.orsId = UserOrderServiceUtil.CAS_GENERATED_CANCEL_REPORT; // use this field to differentiate between fake cancelReport vs server generated reprot.
    	cancelReport.totalCancelledQuantity = cancelledQuantity;
    	cancelReport.transactionSequenceNumber = 2;
    	cancelReport.userAssignedCancelId = cancelRequest.userAssignedCancelId;
    	
    	return cancelReport;
    	
    }

}

