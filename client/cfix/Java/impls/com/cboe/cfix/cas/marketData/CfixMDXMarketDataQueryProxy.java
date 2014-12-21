package com.cboe.cfix.cas.marketData;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.interfaces.domain.RateMonitorHome;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.client.util.collections.SessionKeyObjectMap;
import com.cboe.client.util.collections.StringObjectMap;
import com.cboe.client.util.DateHelper;
import com.cboe.client.util.StringHelper;
import com.cboe.exceptions.*;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ExceptionBuilder;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.RateMonitorKeyContainer;
import com.cboe.domain.util.TimeHelper;
import com.cboe.domain.util.RecapContainerV4;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiConstants.MultiplePartiesIndicators;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.PriceConstants;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.cfix.fix.util.FixMarketDataRejectStruct;

import java.util.*;

/**
 * User: beniwalv
 */
public class CfixMDXMarketDataQueryProxy extends BObject implements CfixMDXMarketDataQueryIF, UserSessionLogoutCollector
{
    protected CfixSessionManager         cfixSessionManager;
    protected CfixMarketDataConsumer     cfixFixMarketDataConsumer;
    protected UserEnablement             userEnablement;
    protected String userID;
    protected String exchange;
    protected String acronym;
    protected RateMonitorHome            rateMonitorHome;
    protected UserSessionLogoutProcessor logoutProcessor;
    protected SubscriptionService        subscriptionService;
    // todo - VivekB: revert to using a single set of dispatchers once we go fully to MDX
    //protected Set                        subscribedDispatchers = Collections.synchronizedSet(new HashSet());
    protected Set                        subscribedMDXDispatchers = Collections.synchronizedSet(new HashSet());
    protected Set                        subscribedChannelDispatchers = Collections.synchronizedSet(new HashSet());
    protected Map                        allSessionConstraints;
    protected SessionKeyObjectMap        sessionKeyToCfixSubscriptionHolderMap  = new SessionKeyObjectMap("SessionKeyToCfixSubscriptionHolderMap");
    protected StringObjectMap            mdReqIDToCfixMarketDataStructHolderMap = new StringObjectMap();
    protected List                       methodInstrumentorList = new ArrayList();

    public static final String W_MAINStr = "W_MAIN";
    public static final String ONE_MAINStr = "ONE_MAIN";
    public static final String EQUITYStr =  "Underlying";

    public CfixMDXMarketDataQueryProxy()
    {
        super();
    }

    public CfixMDXMarketDataQueryProxy(Map sessionConstraints)
    {
        this();

        if(Log.isDebugOn())
        {
            Log.debug("Initialized CfixMDXMarketDataQueryProxy. CFIX is MDX Enabled.");
        }

        this.allSessionConstraints = sessionConstraints;
    }

    public CfixSessionManager getCfixSessionManager() throws SystemException, CommunicationException, AuthorizationException
    {
        return cfixSessionManager;
    }

    public void setCfixSessionManager(CfixSessionManager cfixSessionManager) throws SystemException, CommunicationException, AuthorizationException
    {
        this.cfixSessionManager  = cfixSessionManager;
        logoutProcessor         = UserSessionLogoutProcessorFactory.create(this);
        userID = cfixSessionManager.getValidUser().userId;
        exchange = cfixSessionManager.getValidUser().userAcronym.exchange;
        acronym = cfixSessionManager.getValidUser().userAcronym.acronym;
        subscriptionService     = ServicesHelper.getSubscriptionService(cfixSessionManager);

        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, cfixSessionManager);
        LogoutServiceFactory.find().addLogoutListener(cfixSessionManager, this);
    }

    public void setCfixMarketDataConsumer(CfixMarketDataConsumer cfixFixMarketDataConsumer)
    {
        this.cfixFixMarketDataConsumer = cfixFixMarketDataConsumer;
    }

    public CfixMarketDataConsumer getCfixMarketDataConsumer()
    {
        return cfixFixMarketDataConsumer;
    }

    public void create(String name)
    {
        super.create(name);
    }

    protected RateMonitorHome getRateMonitorHome()
    {
        if (rateMonitorHome == null)
        {
            try
            {
                rateMonitorHome = (RateMonitorHome) HomeFactory.getInstance().findHome(RateMonitorHome.HOME_NAME);
            }
            catch (CBOELoggableException e)
            {
//                Log.exception(this, "session : " + cfixSessionInformation.getUserSessionManager(), e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find RateMonitor Home");
            }
        }

        return rateMonitorHome;
    }

    protected void addRegisteredInstrumentor(MethodInstrumentor mi)
    {
        if (Log.isDebugOn()) Log.debug(this, "CfixMDXMarketDataQueryProxy : addRegisteredInstrumentor : adding " + mi);
        methodInstrumentorList.add(mi);
    }



    public void snapshotBookDepthByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
        String               mdReqID              = cfixFixMDXMarketDataConsumerHolder.getOverlayPolicyMarketDataList().getMdReqID();

        Map sessionConstraints = (Map) allSessionConstraints.get(sessionProductStruct.sessionName);

        int  windowSize = 0;
        long windowMilliSecondPeriod = 0;

        if (sessionConstraints != null)
        {
            Object constraint = sessionConstraints.get(CfixMarketDataQueryProxyHomeImpl.BOOK_DEPTH_CALL_WINDOW_INTERVAL_PROPERTY_NAME);

            if (constraint != null)
            {
               windowMilliSecondPeriod = ((Long)constraint).longValue();
            }

            constraint = sessionConstraints.get(CfixMarketDataQueryProxyHomeImpl.BOOK_DEPTH_CALL_WINDOW_SIZE_PROPERTY_NAME);

            if (constraint != null)
            {
                windowSize = ((Integer)constraint).intValue();
            }
        }

        if (windowSize > 0 && windowMilliSecondPeriod > 0)
        {
            RateMonitorKeyContainer rateMonitorKey = new RateMonitorKeyContainer(userID, exchange, acronym, sessionProductStruct.sessionName
                                                                                 , RateMonitorTypeConstants.GET_BOOK_DEPTH);

            if (!getRateMonitorHome().find(rateMonitorKey, windowSize, windowMilliSecondPeriod).canAccept(System.currentTimeMillis()))
            {
                cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.InsufficientBandwidth, "getBookDepth rejected. Call limit exceeded", mdReqID));

                return;
            }
        }

        try
        {
            if (userEnablement == null)
            {
                userEnablement = ServicesHelper.getUserEnablementService(cfixSessionManager.getValidUser().userId
                                                                         , cfixSessionManager.getValidUser().userAcronym.exchange
                                                                         , cfixSessionManager.getValidUser().userAcronym.acronym);
            }

            userEnablement.verifyUserEnablement(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, makeOperationType(CfixMarketDataDispatcherIF.MarketDataType_BookDepth));
        }
        catch (Exception e)
        {
            cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.InsufficientPermissions, "Insufficient Permissions", mdReqID));
            return;
        }

        // Always return the full book for this direct-call method
        cfixFixMDXMarketDataConsumerHolder.acceptMarketDataBookDepth(ServicesHelper.getOrderBookService().getBookDepth(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey, false));
    }

//-----------------------------------------------------------------------------------------------------------------------------------------


    // Subscribe Book Depth Not Supported - Rejected by the FixMDXMarketDataFutureExecution
    public void subscribeBookDepthByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();

        if (subscribeProxyByProduct(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_BookDepth))
        {
            return;
        }

        try
        {
            cfixFixMDXMarketDataConsumerHolder.acceptMarketDataBookDepth(ServicesHelper.getOrderBookService().getBookDepth(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey,true));
        } catch (Exception e)
        {
            Log.exception(this, "Could not complete the initial get operation for BookDepth. Dynamic updates should continue to work : ExceptionMessage:" + e.getMessage(),e );
        }

        // VivekB: Here, we are still instantiating an object that listens on the channel.
        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);
        cfixMarketDataDispatcher.subscribeByProduct(cfixFixMDXMarketDataConsumerHolder);
        subscribedChannelDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    // Subscribe Book Depth Not Supported - Rejected by the FixMDXMarketDataFutureExecution
    public void subscribeBookDepthByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();

        if (subscribeProxyByClass(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_BookDepth))
        {
            return;
        }

        try
        {
            cfixFixMDXMarketDataConsumerHolder.acceptMarketDataBookDepth(ServicesHelper.getOrderBookService().getBookDepthByClass(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey, true));
        } catch (Exception e)
        {
            Log.exception(this, "Could not complete the initial get operation for BookDepthByClass. Dynamic updates should continue to work : ExceptionMessage:" + e.getMessage(),e );
        }

        // VivekB: Here, we are still instantiating an object that listens on the channel.
        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);
        cfixMarketDataDispatcher.subscribeByClass(cfixFixMDXMarketDataConsumerHolder);
        subscribedChannelDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    // Subscribe Book Depth Not Supported - Rejected by the FixMDXMarketDataFutureExecution
    public void unsubscribeBookDepth(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMDXMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeBookDepth(" + mdReqID + ") -- no such MDReqID", 0);
        }

        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }

        if (cfixFixMDXMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();

            unsubscribeProxyByProduct(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);
            cfixMarketDataDispatcher.unsubscribeByProduct(cfixFixMDXMarketDataConsumerHolder);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();

            unsubscribeProxyByClass(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);
            cfixMarketDataDispatcher.unsubscribeByClass(cfixFixMDXMarketDataConsumerHolder);
        }
    }


    public void subscribeBookDepthUpdateByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        throw ExceptionBuilder.authorizationException("subscribeBookDepthUpdateByProduct NOT IMPLEMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
    }

    public void subscribeBookDepthUpdateByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        throw ExceptionBuilder.authorizationException("subscribeBookDepthUpdateByClass NOT IMPLEMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
    }

    public void unsubscribeBookDepthUpdate(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        throw ExceptionBuilder.authorizationException("unsubscribeBookDepthUpdateByProduct NOT IMPLEMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
    }

    // VivekB: subscribes to MDX
    public void subscribeCurrentMarketByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
        int subClassKey = sessionProductStruct.productStruct.productKeys.classKey;

        if (invalidSubscribeByProductForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket))
        {
            return;
        }
        // VivekB: Uses a dispatcher that is by class, and subscribes to MDX. Initial market data is delivered 
        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, subClassKey,CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);
        cfixMDXMarketDataDispatcher.subscribeByProduct(cfixFixMDXMarketDataConsumerHolder, true);
        subscribedMDXDispatchers.add(cfixMDXMarketDataDispatcher);
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }
    // VivekB: subscribes to MDX
    public void subscribeCurrentMarketByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();
        int subClassKey = sessionClassStruct.classStruct.classKey;

        /** This call checks the following:
         * User Enablements - if user is not enabled, return.
         * Any existing subscriptions - by MDReqID - if you are already subscribed, then return.
         */
        if (invalidSubscribeByClassForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket))
        {
            return;
        }
        // VivekB: uses a dispatcher by class, subscribes to MDX
        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);
        cfixMDXMarketDataDispatcher.subscribeByClass(cfixFixMDXMarketDataConsumerHolder, true);
        subscribedMDXDispatchers.add(cfixMDXMarketDataDispatcher);
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }
    // VivekB: unsubscribes from MDX
    public void unsubscribeCurrentMarket(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMDXMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMDXMarketDataQueryProxy: Can't unsubscribeCurrentMarket(" + mdReqID + ") -- no such MDReqID", 0);
        }
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
        if (cfixFixMDXMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
            int subClassKey = sessionProductStruct.productStruct.productKeys.classKey;

            unsubscribeByProductForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);

            CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);
            cfixMDXMarketDataDispatcher.unsubscribeByProduct(cfixFixMDXMarketDataConsumerHolder, true);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();
            int subClassKey = sessionClassStruct.classStruct.classKey;

            unsubscribeByClassForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);

            CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);
            cfixMDXMarketDataDispatcher.unsubscribeByClass(cfixFixMDXMarketDataConsumerHolder, true);
        }
    }


    // VivekB: subscribes to MDX
    public void subscribeExpectedOpeningPriceByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
        int subClassKey = sessionProductStruct.productStruct.productKeys.classKey;

        if (invalidSubscribeByProductForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice))
        {
            return;
        }

        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);
        cfixMDXMarketDataDispatcher.subscribeByProduct(cfixFixMDXMarketDataConsumerHolder, true);
        subscribedMDXDispatchers.add(cfixMDXMarketDataDispatcher);
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }
    // VivekB: subscribes to MDX
    public void subscribeExpectedOpeningPriceByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();
        int subClassKey = sessionClassStruct.classStruct.classKey;

        if (invalidSubscribeByClassForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice))
        {
            return;
        }

        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);
        cfixMDXMarketDataDispatcher.subscribeByClass(cfixFixMDXMarketDataConsumerHolder, true);
        subscribedMDXDispatchers.add(cfixMDXMarketDataDispatcher);
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }
    // VivekB: unsubscribes from MDX
    public void unsubscribeExpectedOpeningPrice(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMDXMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeExpectedOpeningPrice(" + mdReqID + ") -- no such MDReqID", 0);
        }

        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
        if (cfixFixMDXMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
            int subClassKey = sessionProductStruct.productStruct.productKeys.classKey;

            unsubscribeByProductForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);

            CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);
            cfixMDXMarketDataDispatcher.unsubscribeByProduct(cfixFixMDXMarketDataConsumerHolder, true);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();
            int subClassKey = sessionClassStruct.classStruct.classKey;

            unsubscribeByClassForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);

            CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);
            cfixMDXMarketDataDispatcher.unsubscribeByClass(cfixFixMDXMarketDataConsumerHolder, true);
        }
    }

    // Subscribe NBBO Not Supported - Rejected by the FixMDXMarketDataFutureExecution
    public void subscribeNbboByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();

        if (subscribeProxyByProduct(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Nbbo))
        {
            return;
        }

        try
        {
            cfixFixMDXMarketDataConsumerHolder.acceptMarketDataNbbo(ServicesHelper.getMarketDataService().getNBBOForProduct(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey));
        } catch (Exception e)
        {
            Log.exception(this, "Could not complete the initial get operation for NBBOForProduct. Dynamic updates should continue to work : ExceptionMessage:" + e.getMessage(),e );
        }

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);
        cfixMarketDataDispatcher.subscribeByProduct(cfixFixMDXMarketDataConsumerHolder);
        subscribedChannelDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    // Subscribe NBBO Not Supported - Rejected by the FixMDXMarketDataFutureExecution
    public void subscribeNbboByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();

        if (subscribeProxyByClass(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Nbbo))
        {
            return;
        }

        try
        {
            cfixFixMDXMarketDataConsumerHolder.acceptMarketDataNbbo(ServicesHelper.getMarketDataService().getNBBOForClass(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey));
        } catch (Exception e)
        {
            Log.exception(this, "Could not complete the initial get operation for NBBOForClass. Dynamic updates should continue to work : ExceptionMessage:" + e.getMessage(),e );
        }

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);
        cfixMarketDataDispatcher.subscribeByClass(cfixFixMDXMarketDataConsumerHolder);
        subscribedChannelDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    // Subscribe NBBO Not Supported - Rejected by the FixMDXMarketDataFutureExecution
    public void unsubscribeNbbo(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMDXMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeNbbo(" + mdReqID + ") -- no such MDReqID", 0);
        }

        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
        if (cfixFixMDXMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();

            unsubscribeProxyByProduct(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);
            cfixMarketDataDispatcher.unsubscribeByProduct(cfixFixMDXMarketDataConsumerHolder);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();

            unsubscribeProxyByClass(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);
            cfixMarketDataDispatcher.unsubscribeByClass(cfixFixMDXMarketDataConsumerHolder);
        }
    }

    // VivekB: subscribes to MDX
    public void subscribeRecapByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
        int subClassKey = sessionProductStruct.productStruct.productKeys.classKey;

        /** This call checks the following:
         * User Enablements - if user is not enabled, return.
         * Any existing subscriptions - by MDReqID - if you are already subscribed, then return.
         */
        if (invalidSubscribeByProductForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Recap))
        {
            return;
        }

        //try
        //{
        //    RecapStruct recapStructV1 = ServicesHelper.getMarketDataService().getRecapForProduct(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey);
        //    cfixFixMDXMarketDataConsumerHolder.acceptMarketDataRecap(mapRecapStructV1ToRecapContainerV4(recapStructV1));
        //} catch (Exception e)
        //{
        //    Log.exception(this, "Could not complete the initial get operation for RecapForProduct. Dynamic updates should continue to work : ExceptionMessage:" + e.getMessage(),e );
        //}

        // To build recap, we will need the bid and ask - from the CurrentMarket message - for that product
        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcherCM = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);
        // NOTE - we subscribe the user under the covers for CurrentMarket - this is not an user requested subscritpion - so the "false"
        cfixMDXMarketDataDispatcherCM.subscribeByProduct(cfixFixMDXMarketDataConsumerHolder, false);

        // Then we subscribe for recap -
        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_Recap);
        cfixMDXMarketDataDispatcher.subscribeByProduct(cfixFixMDXMarketDataConsumerHolder, true);

        // Some really ugly code - casting the interface to the specific impl
        ((CfixMDXMarketDataRecapDispatcherImpl) cfixMDXMarketDataDispatcher).setCurrentMarketCallback((CfixMDXMarketDataCurrentMarketDispatcherImpl) cfixMDXMarketDataDispatcherCM);

        subscribedMDXDispatchers.add(cfixMDXMarketDataDispatcher);
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    // VivekB: subscribes to MDX
    public void subscribeRecapByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();
        int subClassKey = sessionClassStruct.classStruct.classKey;

        /** This call checks the following:
         * User Enablements - if user is not enabled, return.
         * Any existing subscriptions - by MDReqID - if you are already subscribed, then return.
         */
        if (invalidSubscribeByClassForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Recap))
        {
            return;
        }

        // todo - turn off the "get"
        //try
        //{
        //    RecapStruct[] recapStructsV1Array = ServicesHelper.getMarketDataService().getRecapForClass(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey).productRecaps;
        //    cfixFixMDXMarketDataConsumerHolder.acceptMarketDataRecap(mapRecapStructsV1ArrayToRecapContainerV4Array(recapStructsV1Array));
        //} catch (Exception e)
        //{
        //    Log.exception(this, "Could not complete the initial get operation for RecapForClass. Dynamic updates should continue to work : ExceptionMessage:" + e.getMessage(),e );
        //}

        // To build recap, we will need the bid and ask - from the CurrentMarket message - for that class.
        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcherCM = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);
        // NOTE - we subscribe the user under the covers for CurrentMarket - this is not an user requested subscritpion - so the "false"
        cfixMDXMarketDataDispatcherCM.subscribeByClass(cfixFixMDXMarketDataConsumerHolder, false);

        // Then we subscribe for recap -
        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_Recap);
        cfixMDXMarketDataDispatcher.subscribeByClass(cfixFixMDXMarketDataConsumerHolder, true);

        // Some really ugly code - casting the interface to the specific impl
        ((CfixMDXMarketDataRecapDispatcherImpl) cfixMDXMarketDataDispatcher).setCurrentMarketCallback((CfixMDXMarketDataCurrentMarketDispatcherImpl) cfixMDXMarketDataDispatcherCM);

        subscribedMDXDispatchers.add(cfixMDXMarketDataDispatcher);
        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    // VivekB: unsubscribes from MDX
    public void unsubscribeRecap(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMDXMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeRecap(" + mdReqID + ") -- no such MDReqID", 0);
        }

        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
        if (cfixFixMDXMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
            int subClassKey = sessionProductStruct.productStruct.productKeys.classKey;

            unsubscribeByProductForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Recap);
            // Note - Once subscribed for CM for Recap, we do not unsubscribe to the CM - this is to let the cache continue to build up
            CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_Recap);
            cfixMDXMarketDataDispatcher.unsubscribeByProduct(cfixFixMDXMarketDataConsumerHolder, true);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();
            int subClassKey = sessionClassStruct.classStruct.classKey;

            unsubscribeByClassForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Recap);
            // Note - Once subscribed for CM for Recap, we do not unsubscribe to the CM - this is to let the cache continue to build up
            CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_Recap);
            cfixMDXMarketDataDispatcher.unsubscribeByClass(cfixFixMDXMarketDataConsumerHolder, true);
        }
    }

    // VivekB: subscribes to MDX
    public void subscribeTickerByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
        int subClassKey = sessionProductStruct.productStruct.productKeys.classKey;

        /** This call checks the following:
         * User Enablements - if user is not enabled, return.
         * Any existing subscriptions - by MDReqID - if you are already subscribed, then return.
         */
        if (invalidSubscribeByProductForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Ticker))
        {
            return;
        }

        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_Ticker);
        cfixMDXMarketDataDispatcher.subscribeByProduct(cfixFixMDXMarketDataConsumerHolder, true);
        subscribedMDXDispatchers.add(cfixMDXMarketDataDispatcher);

        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    // VivekB: subscribes to MDX
    public void subscribeTickerByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();
        int subClassKey = sessionClassStruct.classStruct.classKey;

        /** This call checks the following:
         * User Enablements - if user is not enabled, return.
         * Any existing subscriptions - by MDReqID - if you are already subscribed, then return.
         */
        if (invalidSubscribeByClassForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Ticker))
        {
            return;
        }

        CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_Ticker);
        cfixMDXMarketDataDispatcher.subscribeByClass(cfixFixMDXMarketDataConsumerHolder, true);
        subscribedMDXDispatchers.add(cfixMDXMarketDataDispatcher);

        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    // VivekB: unsubscribes from MDX
    public void unsubscribeTicker(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder = (CfixMDXMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMDXMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMDXMarketDataQueryProxy: Can't unsubscribeTicker(" + mdReqID + ") -- no such MDReqID", 0);
        }

        if (cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMDXMarketDataConsumerHolder.getMethodInstrumentor());
        }
        if (cfixFixMDXMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
            int subClassKey = sessionProductStruct.productStruct.productKeys.classKey;

            unsubscribeByProductForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Ticker);

            CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_Ticker);
            cfixMDXMarketDataDispatcher.unsubscribeByProduct(cfixFixMDXMarketDataConsumerHolder, true);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();
            int subClassKey = sessionClassStruct.classStruct.classKey;

            unsubscribeByClassForMDX(cfixFixMDXMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Ticker);

            CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, subClassKey, CfixMarketDataDispatcherIF.MarketDataType_Ticker);
            cfixMDXMarketDataDispatcher.unsubscribeByClass(cfixFixMDXMarketDataConsumerHolder, true);
        }
    }



    public void unsubscribeListener() throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        //Log.information(Thread.currentThread().getName() + " BEFORE unsubscribeListenerXXX dispatchers(" + subscribedDispatchers.size() + ")");
        Object[] dispatcherArrayMDX = subscribedMDXDispatchers.toArray();
        for (int i=0; i < dispatcherArrayMDX.length; i++ )
        {
            try
            {
                ((CfixMDXMarketDataDispatcherIF) dispatcherArrayMDX[i]).unsubscribeConsumer(cfixFixMarketDataConsumer);
            }
            catch (Exception ex)
            {
                Log.exception(ex);
            }
        }

        Object[] dispatcherArrayChannel = subscribedChannelDispatchers.toArray();
        for (int i=0; i < dispatcherArrayChannel.length; i++ )
        {
            try
            {
                ((CfixMarketDataDispatcherIF) dispatcherArrayChannel[i]).unsubscribeConsumer(cfixFixMarketDataConsumer);
            }
            catch (Exception ex)
            {
                Log.exception(ex);
            }
        }

        //subscribedDispatchers.clear(); -- TODO DO WE NEED THIS???

        //Log.information(Thread.currentThread().getName() + " AFTER unsubscribeListenerXXX dispatchers(" + subscribedDispatchers.size() + ")");
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn()) Log.debug(this, "in acceptUserSessionLogout(CfixMarketDataQueryProxy) for " + cfixSessionManager);

        // Do any individual service clean up needed for logout
        for (int i = 0; i < methodInstrumentorList.size(); i++)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister((MethodInstrumentor)methodInstrumentorList.get(i));
        }
        methodInstrumentorList.clear();

        EventChannelAdapterFactory.find().removeListenerGroup(this);
        EventChannelAdapterFactory.find().removeChannel(this);
        LogoutServiceFactory.find().logoutComplete(cfixSessionManager, this);
        logoutProcessor.setParent(null);
        logoutProcessor = null;
        sessionKeyToCfixSubscriptionHolderMap.clear();
        //mdReqIDToCfixMarketDataStructHolderMap.clear(); // We can't clear this map because FutureResults will have to automatically rollback after this.
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    protected boolean subscribeProxyByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();
        String             mdReqID            = cfixFixMDXMarketDataConsumerHolder.getOverlayPolicyMarketDataList().getMdReqID();

        if (userEnablement == null)
        {
            userEnablement = ServicesHelper.getUserEnablementService(cfixSessionManager.getValidUser().userId
                                                                     , cfixSessionManager.getValidUser().userAcronym.exchange
                                                                     , cfixSessionManager.getValidUser().userAcronym.acronym);
        }

        try
        {
            userEnablement.verifyUserEnablement(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey, makeOperationType(marketDataType));
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            //cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.InsufficientPermissions, "Insufficient Permissions", mdReqID));
            //return true;
        }

        SessionProductStruct[] sessionProductStructs = SessionProductStructCache.getSessionProductStructs(sessionClassStruct);

        synchronized(this)
        {
            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " IN SUBSCRIPTION CHECKING MDReqID(" + mdReqID + ")");

            // VivekB: this check looks fine. Should not need a change... with subscription by classKey
            if (mdReqIDToCfixMarketDataStructHolderMap.containsKey(mdReqID))
            {
                cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.DuplicateMdReqId, "Already Subscribed By This MDReqID", mdReqID));
                return true;
            }

            CfixSubscriptionHolder cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey);
            if (cfixSubscriptionHolder == null)
            {
                cfixSubscriptionHolder = new CfixSubscriptionHolder(marketDataType, mdReqID);

                sessionKeyToCfixSubscriptionHolderMap.putKeyValue(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey, cfixSubscriptionHolder);
            }
            else
            {
                if (cfixSubscriptionHolder.isSubscribed(marketDataType))
                {
                    cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.AlreadySubscribed, "Already Subscribed By This Class", mdReqID));
                    return true;
                }

                cfixSubscriptionHolder.subscribe(marketDataType, mdReqID);
            }

            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SUBSCRIPTION Added MDReqID(" + mdReqID + ")");

            mdReqIDToCfixMarketDataStructHolderMap.putKeyValue(mdReqID, cfixFixMDXMarketDataConsumerHolder);

            switch (marketDataType)
            {
                case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        subscriptionService.addCurrentMarketClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 subscriptionService.addNBBOClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Recap:                subscriptionService.addRecapClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            subscriptionService.addBookDepthClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      subscriptionService.addBookDepthClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               subscriptionService.addTickerClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: subscriptionService.addOpeningPriceClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
            }

            for (int i = 0; i < sessionProductStructs.length; i++)
            {
                try
                {
                    cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionClassStruct.sessionName, sessionProductStructs[i].productStruct.productKeys.productKey);
                    if (cfixSubscriptionHolder != null && cfixSubscriptionHolder.isSubscribed(marketDataType))
                    {
                        mdReqID = unsubscribeProxyByProduct((CfixMDXMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(cfixSubscriptionHolder.getMdReqID(marketDataType)), marketDataType);
                        cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.SubscriptionReplaced, "Unsubscribed By Product Because Now Subscribed By Class", mdReqID));
                    }
                }
                catch (Exception ex)
                {
                    Log.exception(ex);
                }
            }
        }

        return false;
    }

    protected String unsubscribeProxyByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();

        synchronized(this)
        {
            CfixSubscriptionHolder cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey);
            if (cfixSubscriptionHolder == null)
            {
                return null;
            }

            String mdReqID = cfixSubscriptionHolder.unsubscribe(marketDataType);
            if (mdReqID == null)
            {
                return null;
            }

            if (cfixSubscriptionHolder.isEmpty())
            {
                sessionKeyToCfixSubscriptionHolderMap.removeKey(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey);
            }

            mdReqIDToCfixMarketDataStructHolderMap.removeKey(mdReqID);

            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SUBSCRIPTION Removed MDReqID(" + mdReqID + ")");

            switch (marketDataType)
            {
                case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        subscriptionService.removeCurrentMarketClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 subscriptionService.removeNBBOClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Recap:                subscriptionService.removeRecapClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            subscriptionService.removeBookDepthClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      subscriptionService.removeBookDepthClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               subscriptionService.removeTickerClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: subscriptionService.removeOpeningPriceClassInterest(this, sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey); break;
            }
            return mdReqID;
        }
    }

    protected boolean subscribeProxyByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
        String               mdReqID              = cfixFixMDXMarketDataConsumerHolder.getOverlayPolicyMarketDataList().getMdReqID();

        if (userEnablement == null)
        {
            userEnablement = ServicesHelper.getUserEnablementService(cfixSessionManager.getValidUser().userId, cfixSessionManager.getValidUser().userAcronym.exchange, cfixSessionManager.getValidUser().userAcronym.acronym);
        }

        try
        {
            userEnablement.verifyUserEnablement(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, makeOperationType(marketDataType));
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            //cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.InsufficientPermissions, "Insufficient Permissions", mdReqID));
            //return true;
        }

        SessionProductStructCache.getSessionProductStructs(sessionProductStruct);

        synchronized(this)
        {
            if (mdReqIDToCfixMarketDataStructHolderMap.containsKey(mdReqID))
            {
                // Check if you are subscribed for market data by MDReqID
                cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.DuplicateMdReqId, "Already Subscribed By This MDReqID", mdReqID));
                return true;
            }

            CfixSubscriptionHolder cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey);
            if (cfixSubscriptionHolder != null && cfixSubscriptionHolder.isSubscribed(marketDataType))
            {
                cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.AlreadySubscribed, "Already Subscribed By This Product's Class", mdReqID));
                return true;
            }

            cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey);
            if (cfixSubscriptionHolder == null)
            {
                cfixSubscriptionHolder = new CfixSubscriptionHolder(marketDataType, mdReqID);

                sessionKeyToCfixSubscriptionHolderMap.putKeyValue(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey, cfixSubscriptionHolder);
            }
            else
            {
                if (cfixSubscriptionHolder.isSubscribed(marketDataType))
                {
                    cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.AlreadySubscribed, "Already Subscribed By This Product", mdReqID));
                    return true;
                }

                cfixSubscriptionHolder.subscribe(marketDataType, mdReqID);
            }

            // if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SUBSCRIPTION Added MDReqID(" + mdReqID + ")");

            mdReqIDToCfixMarketDataStructHolderMap.putKeyValue(mdReqID, cfixFixMDXMarketDataConsumerHolder);

            switch (marketDataType)
            {
                case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        subscriptionService.addCurrentMarketProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 subscriptionService.addNBBOProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Recap:                subscriptionService.addRecapProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            subscriptionService.addBookDepthProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      subscriptionService.addBookDepthProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               subscriptionService.addTickerProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: subscriptionService.addOpeningPriceProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
            }
        }

        return false;
    }

    protected String unsubscribeProxyByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();

        synchronized(this)
        {
            CfixSubscriptionHolder cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey);
            if (cfixSubscriptionHolder == null)
            {
                return null;
            }

            String mdReqID = cfixSubscriptionHolder.unsubscribe(marketDataType);
            if (mdReqID == null)
            {
                return null;
            }

            if (cfixSubscriptionHolder.isEmpty())
            {
                sessionKeyToCfixSubscriptionHolderMap.removeKey(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey);
            }

            mdReqIDToCfixMarketDataStructHolderMap.removeKey(mdReqID);

            // if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SUBSCRIPTION Removed MDReqID(" + mdReqID + ")");

            switch (marketDataType)
            {
                case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket:        subscriptionService.removeCurrentMarketProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Nbbo:                 subscriptionService.removeNBBOProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Recap:                subscriptionService.removeRecapProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_BookDepth:            subscriptionService.removeBookDepthProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate:      subscriptionService.removeBookDepthProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_Ticker:               subscriptionService.removeTickerProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
                case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice: subscriptionService.removeOpeningPriceProductInterest(this, sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, sessionProductStruct.productStruct.productKeys.productKey); break;
            }
            return mdReqID;
        }
    }



    protected boolean invalidSubscribeByClassForMDX(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();
        String             mdReqID            = cfixFixMDXMarketDataConsumerHolder.getOverlayPolicyMarketDataList().getMdReqID();
        //if(Log.isDebugOn())
        //{
        //    Log.debug("CfixMDXMarketDataQueryProxy: invalidSubscribeByProductForMDX : sessionProductStruct SessionName : " + sessionClassStruct.sessionName + "  classKey : " + sessionClassStruct.classStruct.classKey);
        //}

        if (userEnablement == null)
        {
            //if(Log.isDebugOn())
            //{
            //    Log.debug("CfixMDXMarketDataQueryProxy: invalidSubscribeByProductForMDX : userEnablement is null : calling getUserEnablementService with UserId: "
            //            + cfixSessionManager.getValidUser().userId + "  Exchange : " + cfixSessionManager.getValidUser().userAcronym.exchange
            //            + "  Acronym: " + cfixSessionManager.getValidUser().userAcronym.acronym);
            //}
            userEnablement = ServicesHelper.getUserEnablementService(cfixSessionManager.getValidUser().userId
                                                                     , cfixSessionManager.getValidUser().userAcronym.exchange
                                                                     , cfixSessionManager.getValidUser().userAcronym.acronym);
        }

        try
        {
            //if(Log.isDebugOn())
            //{
            //    Log.debug("CfixMDXMarketDataQueryProxy: invalidSubscribeByProductForMDX : calling verifyUserEnablement with sessionName: "
            //            + sessionClassStruct.sessionName + "  classKey : " + sessionClassStruct.classStruct.classKey + "  OperationType : " + makeOperationType(marketDataType));
            //}
            userEnablement.verifyUserEnablement(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey, makeOperationType(marketDataType));
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            //cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.InsufficientPermissions, "Insufficient Permissions", mdReqID));
            //return true;
        }

        SessionProductStruct[] sessionProductStructs = SessionProductStructCache.getSessionProductStructs(sessionClassStruct);

        synchronized(this)
        {
            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " IN SUBSCRIPTION CHECKING MDReqID(" + mdReqID + ")");

            // VivekB: this check looks fine. Should not need a change... with subscription by classKey
            if (mdReqIDToCfixMarketDataStructHolderMap.containsKey(mdReqID))
            {
                cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.DuplicateMdReqId, "Already Subscribed By This MDReqID", mdReqID));
                return true;
            }

            CfixSubscriptionHolder cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey);
            if (cfixSubscriptionHolder == null)
            {
                cfixSubscriptionHolder = new CfixSubscriptionHolder(marketDataType, mdReqID);

                sessionKeyToCfixSubscriptionHolderMap.putKeyValue(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey, cfixSubscriptionHolder);
            }
            else
            {
                if (cfixSubscriptionHolder.isSubscribed(marketDataType))
                {
                    cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.AlreadySubscribed, "Already Subscribed By This Class", mdReqID));
                    return true;
                }

                cfixSubscriptionHolder.subscribe(marketDataType, mdReqID);
            }

            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SUBSCRIPTION Added MDReqID(" + mdReqID + ")");

            mdReqIDToCfixMarketDataStructHolderMap.putKeyValue(mdReqID, cfixFixMDXMarketDataConsumerHolder);

            for (int i = 0; i < sessionProductStructs.length; i++)
            {
                try
                {
                    cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionClassStruct.sessionName, sessionProductStructs[i].productStruct.productKeys.productKey);
                    if (cfixSubscriptionHolder != null && cfixSubscriptionHolder.isSubscribed(marketDataType))
                    {
                        mdReqID = unsubscribeProxyByProduct((CfixMDXMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(cfixSubscriptionHolder.getMdReqID(marketDataType)), marketDataType);
                        cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.SubscriptionReplaced, "Unsubscribed By Product Because Now Subscribed By Class", mdReqID));
                    }
                }
                catch (Exception ex)
                {
                    Log.exception(ex);
                }
            }
        }

        return false;
    }

    protected String unsubscribeByClassForMDX(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMDXMarketDataConsumerHolder.getSessionClassStruct();

        synchronized(this)
        {
            CfixSubscriptionHolder cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey);
            if (cfixSubscriptionHolder == null)
            {
                return null;
            }

            String mdReqID = cfixSubscriptionHolder.unsubscribe(marketDataType);
            if (mdReqID == null)
            {
                return null;
            }

            if (cfixSubscriptionHolder.isEmpty())
            {
                sessionKeyToCfixSubscriptionHolderMap.removeKey(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey);
            }

            mdReqIDToCfixMarketDataStructHolderMap.removeKey(mdReqID);

            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SUBSCRIPTION Removed MDReqID(" + mdReqID + ")");

            return mdReqID;
        }
    }

    protected boolean invalidSubscribeByProductForMDX(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();
        //if(Log.isDebugOn())
        //{
        //    Log.debug("CfixMDXMarketDataQueryProxy: invalidSubscribeByProductForMDX : sessionProductStruct SessionName : "
        //             + sessionProductStruct.sessionName + "  classKey : " + sessionProductStruct.productStruct.productKeys.classKey);
        //}

        String               mdReqID              = cfixFixMDXMarketDataConsumerHolder.getOverlayPolicyMarketDataList().getMdReqID();

        if (userEnablement == null)
        {
            //if(Log.isDebugOn())
            //{
            //    Log.debug("CfixMDXMarketDataQueryProxy: invalidSubscribeByProductForMDX : userEnablement is null : calling getUserEnablementService with UserId: "
            //            + cfixSessionManager.getValidUser().userId + "  Exchange : " + cfixSessionManager.getValidUser().userAcronym.exchange
            //            + "  Acronym: " + cfixSessionManager.getValidUser().userAcronym.acronym);
            //}
            userEnablement = ServicesHelper.getUserEnablementService(cfixSessionManager.getValidUser().userId, cfixSessionManager.getValidUser().userAcronym.exchange, cfixSessionManager.getValidUser().userAcronym.acronym);
        }

        try
        {
            //if(Log.isDebugOn())
            //{
            //    Log.debug("CfixMDXMarketDataQueryProxy: invalidSubscribeByProductForMDX : calling verifyUserEnablement with sessionName: "
            //            + sessionProductStruct.sessionName + "  classKey : " + sessionProductStruct.productStruct.productKeys.classKey
            //            + "  OperationType : " + makeOperationType(marketDataType));
            //}
            userEnablement.verifyUserEnablement(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, makeOperationType(marketDataType));
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            //cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.InsufficientPermissions, "Insufficient Permissions", mdReqID));
            //return true;
        }

        SessionProductStructCache.getSessionProductStructs(sessionProductStruct);

        synchronized(this)
        {
            if (mdReqIDToCfixMarketDataStructHolderMap.containsKey(mdReqID))
            {
                // Check if you are subscribed for market data by MDReqID
                cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.DuplicateMdReqId, "Already Subscribed By This MDReqID", mdReqID));
                return true;
            }

            CfixSubscriptionHolder cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey);
            if (cfixSubscriptionHolder != null && cfixSubscriptionHolder.isSubscribed(marketDataType))
            {
                cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.AlreadySubscribed, "Already Subscribed By This Product's Class", mdReqID));
                return true;
            }

            cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey);
            if (cfixSubscriptionHolder == null)
            {
                cfixSubscriptionHolder = new CfixSubscriptionHolder(marketDataType, mdReqID);

                sessionKeyToCfixSubscriptionHolderMap.putKeyValue(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey, cfixSubscriptionHolder);
            }
            else
            {
                if (cfixSubscriptionHolder.isSubscribed(marketDataType))
                {
                    cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.AlreadySubscribed, "Already Subscribed By This Product", mdReqID));
                    return true;
                }

                cfixSubscriptionHolder.subscribe(marketDataType, mdReqID);
            }

            // if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SUBSCRIPTION Added MDReqID(" + mdReqID + ")");

            mdReqIDToCfixMarketDataStructHolderMap.putKeyValue(mdReqID, cfixFixMDXMarketDataConsumerHolder);

        }

        return false;
    }

    protected String unsubscribeByProductForMDX(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMDXMarketDataConsumerHolder.getSessionProductStruct();

        synchronized(this)
        {
            CfixSubscriptionHolder cfixSubscriptionHolder = (CfixSubscriptionHolder) sessionKeyToCfixSubscriptionHolderMap.getValueForKey(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey);
            if (cfixSubscriptionHolder == null)
            {
                return null;
            }

            String mdReqID = cfixSubscriptionHolder.unsubscribe(marketDataType);
            if (mdReqID == null)
            {
                return null;
            }

            if (cfixSubscriptionHolder.isEmpty())
            {
                sessionKeyToCfixSubscriptionHolderMap.removeKey(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey);
            }

            mdReqIDToCfixMarketDataStructHolderMap.removeKey(mdReqID);

            // if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SUBSCRIPTION Removed MDReqID(" + mdReqID + ")");

            return mdReqID;
        }
    }


    protected int makeOperationType(int marketDataType)
    {
        switch (marketDataType)
        {
            case CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket        : return OperationTypes.MARKETQUERY_CURRENTMARKET ;
            case CfixMarketDataDispatcherIF.MarketDataType_Nbbo                 : return OperationTypes.MARKETQUERY_NBBO;
            case CfixMarketDataDispatcherIF.MarketDataType_Recap                : return OperationTypes.MARKETQUERY_RECAP;
            case CfixMarketDataDispatcherIF.MarketDataType_BookDepth            : return OperationTypes.DYNAMIC_BOOKDEPTH;
            case CfixMarketDataDispatcherIF.MarketDataType_Ticker               : return OperationTypes.MARKETQUERY_TICKER;
            case CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice : return OperationTypes.MARKETQUERY_EXPECTEDOPENINGPRICE;
            default                                                             : return OperationTypes.DEFAULT_OPERATION;
        }
    }

    private CurrentMarketStructV4[] mapCurrentMarketStructsV1ToV4(CurrentMarketStruct[] cmStructArray)
    {
        CurrentMarketStructV4[] returnStructArray = new CurrentMarketStructV4[cmStructArray.length];

        for (int k = 0; k < cmStructArray.length; k++)
        {
            returnStructArray[k] = new CurrentMarketStructV4();
            returnStructArray[k].exchange = cmStructArray[k].exchange;

            returnStructArray[k].classKey = cmStructArray[k].productKeys.classKey;
            returnStructArray[k].productKey = cmStructArray[k].productKeys.productKey;
            returnStructArray[k].productType = cmStructArray[k].productKeys.productType;
            // returnStructArray[k].productKeys.reportingClass - we do not have this information - but it is not needed.

            returnStructArray[k].askPrice = mapPriceV1ToV4(cmStructArray[k].askPrice);
            returnStructArray[k].bidPrice = mapPriceV1ToV4(cmStructArray[k].bidPrice);
            returnStructArray[k].priceScale = 2; // our mapping above will be such that scale is 2

            returnStructArray[k].askSizeSequence = new MarketVolumeStructV4[cmStructArray[k].askSizeSequence.length];
            for (int l = 0; l < cmStructArray[k].askSizeSequence.length; l++)
            {
                returnStructArray[k].askSizeSequence[l] = new MarketVolumeStructV4();
                returnStructArray[k].askSizeSequence[l].quantity = cmStructArray[k].askSizeSequence[l].quantity;
                returnStructArray[k].askSizeSequence[l].volumeType = cmStructArray[k].askSizeSequence[l].volumeType;
                if (cmStructArray[k].askSizeSequence[l].multipleParties)
                    returnStructArray[k].askSizeSequence[l].multipleParties = MultiplePartiesIndicators.YES;
                else // MultiplePartiesIndicators is NO or UNKNOWN
                    returnStructArray[k].askSizeSequence[l].multipleParties = MultiplePartiesIndicators.NO;
            }

            returnStructArray[k].bidSizeSequence = new MarketVolumeStructV4[cmStructArray[k].bidSizeSequence.length];
            for (int l = 0; l < cmStructArray[k].bidSizeSequence.length; l++)
            {
                returnStructArray[k].bidSizeSequence[l] = new MarketVolumeStructV4();
                returnStructArray[k].bidSizeSequence[l].quantity = cmStructArray[k].bidSizeSequence[l].quantity;
                returnStructArray[k].bidSizeSequence[l].volumeType = cmStructArray[k].bidSizeSequence[l].volumeType;
                if (cmStructArray[k].bidSizeSequence[l].multipleParties)
                    returnStructArray[k].bidSizeSequence[l].multipleParties = MultiplePartiesIndicators.YES;
                else // MultiplePartiesIndicators is NO or UNKNOWN
                    returnStructArray[k].bidSizeSequence[l].multipleParties = MultiplePartiesIndicators.NO;
            }

            TimeStruct tmpTS = cmStructArray[k].sentTime;
            //tmpTS = DateHelper.convertDateToTimeStruct(cmStructV4Array[k].sentTime, DateHelper.TIMEZONE_OFFSET_CST, tmpTS);
            returnStructArray[k].sentTime = (int) ( DateHelper.convertHoursToMilliseconds(cmStructArray[k].sentTime.hour) +
                                                    DateHelper.convertMinutesToMilliseconds(cmStructArray[k].sentTime.minute) +
                                                    DateHelper.convertSecondsToMilliseconds(cmStructArray[k].sentTime.second));
        }

        return returnStructArray;
    }

    private CurrentMarketStructV4 mapCurrentMarketV1ToV4(CurrentMarketStruct cmStruct)
    {
        CurrentMarketStructV4 returnStruct = new CurrentMarketStructV4();

        returnStruct.exchange = cmStruct.exchange;

        returnStruct.classKey = cmStruct.productKeys.classKey;
        returnStruct.productKey = cmStruct.productKeys.productKey;
        returnStruct.productType = cmStruct.productKeys.productType;
        // returnStructArray[k].productKeys.reportingClass - we do not have this information - but it is not needed.


        returnStruct.priceScale = 2;
        // The mapping below should always set prices such that the Price Scale = 2
        returnStruct.askPrice = mapPriceV1ToV4(cmStruct.askPrice);
        returnStruct.bidPrice = mapPriceV1ToV4(cmStruct.bidPrice);

        returnStruct.askSizeSequence = new MarketVolumeStructV4[cmStruct.askSizeSequence.length];
        for (int l = 0; l < cmStruct.askSizeSequence.length; l++)
        {
            returnStruct.askSizeSequence[l] = new MarketVolumeStructV4();
            returnStruct.askSizeSequence[l].quantity = cmStruct.askSizeSequence[l].quantity;
            returnStruct.askSizeSequence[l].volumeType = cmStruct.askSizeSequence[l].volumeType;
            if (cmStruct.askSizeSequence[l].multipleParties)
                returnStruct.askSizeSequence[l].multipleParties = MultiplePartiesIndicators.YES;
            else // MultiplePartiesIndicators is NO or UNKNOWN
                returnStruct.askSizeSequence[l].multipleParties = MultiplePartiesIndicators.NO;
        }

        returnStruct.bidSizeSequence = new MarketVolumeStructV4[cmStruct.bidSizeSequence.length];
        for (int l = 0; l < cmStruct.bidSizeSequence.length; l++)
        {
            returnStruct.bidSizeSequence[l] = new MarketVolumeStructV4();
            returnStruct.bidSizeSequence[l].quantity = cmStruct.bidSizeSequence[l].quantity;
            returnStruct.bidSizeSequence[l].volumeType = cmStruct.bidSizeSequence[l].volumeType;
            if (cmStruct.bidSizeSequence[l].multipleParties)
                returnStruct.bidSizeSequence[l].multipleParties = MultiplePartiesIndicators.YES;
            else // MultiplePartiesIndicators is NO or UNKNOWN
                returnStruct.bidSizeSequence[l].multipleParties = MultiplePartiesIndicators.NO;
        }

        TimeStruct tmpTS = cmStruct.sentTime;
        //tmpTS = DateHelper.convertDateToTimeStruct(cmStructV4Array[k].sentTime, DateHelper.TIMEZONE_OFFSET_CST, tmpTS);
        returnStruct.sentTime = (int) ( DateHelper.convertHoursToMilliseconds(cmStruct.sentTime.hour) +
                                                DateHelper.convertMinutesToMilliseconds(cmStruct.sentTime.minute) +
                                                DateHelper.convertSecondsToMilliseconds(cmStruct.sentTime.second));


        return returnStruct;
    }

    private RecapContainerV4[] mapRecapStructsV1ArrayToRecapContainerV4Array(RecapStruct[] recapStructArray)
    {
        RecapContainerV4[] returnContainerArray = new RecapContainerV4[recapStructArray.length];

        for (int k = 0; k < recapStructArray.length; k++)
        {
            returnContainerArray[k] = new RecapContainerV4();

            returnContainerArray[k].setProductKey(recapStructArray[k].productKeys.productKey);
            returnContainerArray[k].setClassKey(recapStructArray[k].productKeys.classKey);
            returnContainerArray[k].setSessionName(mapSessionName(recapStructArray[k].productKeys.productType));

            returnContainerArray[k].setLastSalePrice(StringHelper.mapPriceStructToPriceCharArray(recapStructArray[k].lastSalePrice));
            returnContainerArray[k].setLastSaleVolume(recapStructArray[k].lastSaleVolume);
            char[] tradeTimeChars = new char[10];
            returnContainerArray[k].setTradeTime( DateHelper.makeHHMMSS(tradeTimeChars, recapStructArray[k].tradeTime) );
            returnContainerArray[k].setTickDirection(recapStructArray[k].tickDirection);

            returnContainerArray[k].setRecapPrefix(recapStructArray[k].recapPrefix);

            returnContainerArray[k].setBidPrice(StringHelper.mapPriceStructToPriceCharArray(recapStructArray[k].bidPrice));
            returnContainerArray[k].setBidSize(recapStructArray[k].bidSize);
            char[] bidTimeChars = new char[10];
            returnContainerArray[k].setBidTime( DateHelper.makeHHMMSS(bidTimeChars, recapStructArray[k].bidTime) );

            returnContainerArray[k].setAskPrice(StringHelper.mapPriceStructToPriceCharArray(recapStructArray[k].askPrice));
            returnContainerArray[k].setAskSize(recapStructArray[k].askSize);
            char[] askTimeChars = new char[10];
            returnContainerArray[k].setAskTime( DateHelper.makeHHMMSS(askTimeChars, recapStructArray[k].askTime) );

            returnContainerArray[k].setLowPrice(StringHelper.mapPriceStructToPriceCharArray(recapStructArray[k].lowPrice));
            returnContainerArray[k].setHighPrice(StringHelper.mapPriceStructToPriceCharArray(recapStructArray[k].highPrice));
            returnContainerArray[k].setOpenPrice(StringHelper.mapPriceStructToPriceCharArray(recapStructArray[k].openPrice));
            returnContainerArray[k].setClosePrice(StringHelper.mapPriceStructToPriceCharArray(recapStructArray[k].closePrice));
            returnContainerArray[k].setPreviousClosePrice(StringHelper.mapPriceStructToPriceCharArray(recapStructArray[k].previousClosePrice));

            returnContainerArray[k].setOpenInterest(recapStructArray[k].openInterest);
        }

        return returnContainerArray;
    }

    private RecapContainerV4 mapRecapStructV1ToRecapContainerV4(RecapStruct recapStruct)
    {
        RecapContainerV4 returnContainer = new RecapContainerV4();

        returnContainer.setProductKey(recapStruct.productKeys.productKey);
        returnContainer.setClassKey(recapStruct.productKeys.classKey);
        returnContainer.setSessionName(mapSessionName(recapStruct.productKeys.productType));

        returnContainer.setLastSalePrice(StringHelper.mapPriceStructToPriceCharArray(recapStruct.lastSalePrice));
        returnContainer.setLastSaleVolume(recapStruct.lastSaleVolume);
        char[] tradeTimeChars = new char[10];
        returnContainer.setTradeTime( DateHelper.makeHHMMSS(tradeTimeChars, recapStruct.tradeTime) );
        returnContainer.setTickDirection(recapStruct.tickDirection);

        returnContainer.setRecapPrefix(recapStruct.recapPrefix);

        returnContainer.setBidPrice(StringHelper.mapPriceStructToPriceCharArray(recapStruct.bidPrice));
        returnContainer.setBidSize(recapStruct.bidSize);
        char[] bidTimeChars = new char[10];
        returnContainer.setBidTime( DateHelper.makeHHMMSS(bidTimeChars, recapStruct.bidTime) );

        returnContainer.setAskPrice(StringHelper.mapPriceStructToPriceCharArray(recapStruct.askPrice));
        returnContainer.setAskSize(recapStruct.askSize);
        char[] askTimeChars = new char[10];
        returnContainer.setAskTime( DateHelper.makeHHMMSS(askTimeChars, recapStruct.askTime) );

        returnContainer.setLowPrice(StringHelper.mapPriceStructToPriceCharArray(recapStruct.lowPrice));
        returnContainer.setHighPrice(StringHelper.mapPriceStructToPriceCharArray(recapStruct.highPrice));
        returnContainer.setOpenPrice(StringHelper.mapPriceStructToPriceCharArray(recapStruct.openPrice));
        returnContainer.setClosePrice(StringHelper.mapPriceStructToPriceCharArray(recapStruct.closePrice));
        returnContainer.setPreviousClosePrice(StringHelper.mapPriceStructToPriceCharArray(recapStruct.previousClosePrice));

        returnContainer.setOpenInterest(recapStruct.openInterest);


        return returnContainer;
    }

    private String mapSessionName(short productType)
    {
        String sessionName;
        switch (productType)
        {
            case ProductTypes.OPTION:
                sessionName = W_MAINStr;
                break;
            case ProductTypes.FUTURE:
                sessionName = ONE_MAINStr;
                break;
            case ProductTypes.STRATEGY:
                sessionName = W_MAINStr;
                break;
            case ProductTypes.EQUITY:
                sessionName = EQUITYStr;
                break;
            case ProductTypes.INDEX:
                sessionName = W_MAINStr;
                break;
            default:
                sessionName = W_MAINStr;
                break;
        }
        return sessionName;
    }

    private int mapPriceV1ToV4(PriceStruct priceStructV1)
    {
        int rtnPrice = PriceConstants.NO_PRICE;

        if (priceStructV1.type == PriceTypes.VALUED)
        {
            try{
                String priceString = StringHelper.mapPriceStructToPriceString(priceStructV1);
                /*
                priceString will look something like one of the following -
                8
                -1.20
                10.45
                -145.45
                1001.11
                 */
                int index = priceString.trim().indexOf(".");
                if (-1 == index)
                {
                    // So, price looks something like 8 or 14 or 107 - i.e. price is whole number only.
                    // In this case, we need to multiply the price by 100.
                    int wholeNumberPrice = Integer.parseInt(priceString);
                    rtnPrice = wholeNumberPrice * 100;
                } else if((priceString.trim().length() - 2) == index)
                {
                    // Price looks something like 8.1 or 10.2 or 107.4
                    // We need to multiply by 10
                    StringBuilder strB = new StringBuilder(priceString.substring(0,index));
                    strB.append(priceString.substring(index + 1));
                    int partialPrice = Integer.parseInt(strB.toString());
                    rtnPrice = partialPrice * 10;
                }
                else {
                    StringBuilder strB = new StringBuilder(priceString.substring(0,index));
                    strB.append(priceString.substring(index + 1));
                    rtnPrice = Integer.parseInt(strB.toString());
                }
            } catch (NumberFormatException nfe)
            {
                Log.debug("NumberFormatException in mapping PriceStruct to V4 Price " + nfe);
            }
        }
        // else - return PriceConstants.NO_PRICE - which gets translated to a Zero on the outbound.
        return rtnPrice;
    }



}

