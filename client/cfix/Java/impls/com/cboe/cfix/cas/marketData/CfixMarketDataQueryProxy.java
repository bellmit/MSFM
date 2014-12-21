package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataQueryProxy.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.application.shared.*;
import com.cboe.application.shared.consumer.*;
import com.cboe.idl.constants.*;
import com.cboe.cfix.fix.util.*;
import com.cboe.client.util.collections.*;
import com.cboe.domain.logout.*;
import com.cboe.domain.util.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.domain.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

public class CfixMarketDataQueryProxy extends BObject implements CfixMarketDataQueryIF, UserSessionLogoutCollector
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
    protected Set                        subscribedDispatchers = Collections.synchronizedSet(new HashSet());
    protected Map                        allSessionConstraints;
    protected SessionKeyObjectMap        sessionKeyToCfixSubscriptionHolderMap  = new SessionKeyObjectMap("SessionKeyToCfixSubscriptionHolderMap");
    protected StringObjectMap            mdReqIDToCfixMarketDataStructHolderMap = new StringObjectMap();
    protected List                       methodInstrumentorList = new ArrayList();

    public CfixMarketDataQueryProxy()
    {
        super();
    }

    public CfixMarketDataQueryProxy(Map sessionConstraints)
    {
        this();

        if(Log.isDebugOn())
        {
            Log.debug("Initialized CfixMarketDataQueryProxy. CFIX is Event Channel Enabled.");
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
        if (Log.isDebugOn()) Log.debug(this, "CfixMarketDataQueryProxy : addRegisteredInstrumentor : adding " + mi);
        methodInstrumentorList.add(mi);
    }



    public void snapshotBookDepthByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();
        String               mdReqID              = cfixFixMarketDataConsumerHolder.getOverlayPolicyMarketDataList().getMdReqID();

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
        cfixFixMarketDataConsumerHolder.acceptMarketDataBookDepth(ServicesHelper.getOrderBookService().getBookDepth(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey, false));
    }

//-----------------------------------------------------------------------------------------------------------------------------------------

    public void subscribeBookDepthByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

        if (subscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_BookDepth))
        {
            return;
        }

        cfixFixMarketDataConsumerHolder.acceptMarketDataBookDepth(ServicesHelper.getOrderBookService().getBookDepth(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey,true));

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);
        cfixMarketDataDispatcher.subscribeByProduct(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void subscribeBookDepthByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

        if (subscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_BookDepth))
        {
            return;
        }

        cfixFixMarketDataConsumerHolder.acceptMarketDataBookDepth(ServicesHelper.getOrderBookService().getBookDepthByClass(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey, true));

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);
        cfixMarketDataDispatcher.subscribeByClass(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void unsubscribeBookDepth(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeBookDepth(" + mdReqID + ") -- no such MDReqID", 0);
        }

        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }

        if (cfixFixMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

            unsubscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);
            cfixMarketDataDispatcher.unsubscribeByProduct(cfixFixMarketDataConsumerHolder);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

            unsubscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_BookDepth);
            cfixMarketDataDispatcher.unsubscribeByClass(cfixFixMarketDataConsumerHolder);
        }
    }

    public void subscribeBookDepthUpdateByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        throw ExceptionBuilder.authorizationException("subscribeBookDepthUpdateByProduct NOT IMPLEMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
    }

    public void subscribeBookDepthUpdateByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        throw ExceptionBuilder.authorizationException("subscribeBookDepthUpdateByClass NOT IMPLEMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
    }

    public void unsubscribeBookDepthUpdate(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        throw ExceptionBuilder.authorizationException("unsubscribeBookDepthUpdateByProduct NOT IMPLEMENTED!", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);

//        CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMarketDataStructHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID, 0);
//
//        if (cfixFixMarketDataConsumerHolder == null)
//        {
//            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeBookDepthUpdate(" + mdReqID + ") -- no such MDReqID");
//        }
//
//        if (cfixFixMarketDataConsumerHolder.getSessionStructType() == CfixMarketDataStructHolder.SESSION_PRODUCT_CLASS)
//        {
//            SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();
//
//            unsubscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate);
//
//            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate);
//            cfixMarketDataDispatcher.unsubscribeBookDepthUpdateByProduct(new CfixMarketDataConsumerHolderImpl(cfixFixMarketDataConsumer, cfixFixMarketDataConsumerHolder, cfixSessionManager.getValidUser()));
//        }
//        else
//        {
//            SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();
//
//            unsubscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate);
//
//            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_BookDepthUpdate);
//            cfixMarketDataDispatcher.unsubscribeBookDepthUpdateByClass(new CfixMarketDataConsumerHolderImpl(cfixFixMarketDataConsumer, cfixFixMarketDataConsumerHolder, cfixSessionManager.getValidUser()));
//        }
    }

    public void subscribeCurrentMarketByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

        if (subscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket))
        {
            //Log.information("Returning before calling the get for product on the Market Data Service");
            return;
        }

        CurrentMarketStruct cmStruct = ServicesHelper.getMarketDataService().getCurrentMarketForProduct(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey);
        if (cmStruct != null)
            com.cboe.domain.util.ReflectiveStructBuilder.printStruct(cmStruct, "Subscribed by product");
        cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(cmStruct);
        //cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(ServicesHelper.getMarketDataService().getCurrentMarketForProduct(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey));

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);
        cfixMarketDataDispatcher.subscribeByProduct(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void subscribeCurrentMarketByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

        if (subscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket))
        {
            //Log.information("Returning before calling the get for class on the Market Data Service");
            return;
        }

        CurrentMarketStruct[] cmStruct = ServicesHelper.getMarketDataService().getCurrentMarketForClass(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey);
        if (cmStruct != null)
            com.cboe.domain.util.ReflectiveStructBuilder.printStruct(cmStruct, "Subscribed by class");

        cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(cmStruct);
        //cfixFixMarketDataConsumerHolder.acceptMarketDataCurrentMarket(ServicesHelper.getMarketDataService().getCurrentMarketForClass(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey));

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);
        cfixMarketDataDispatcher.subscribeByClass(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void unsubscribeCurrentMarket(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeCurrentMarket(" + mdReqID + ") -- no such MDReqID", 0);
        }
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
        if (cfixFixMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

            unsubscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);
            cfixMarketDataDispatcher.unsubscribeByProduct(cfixFixMarketDataConsumerHolder);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

            unsubscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_CurrentMarket);
            cfixMarketDataDispatcher.unsubscribeByClass(cfixFixMarketDataConsumerHolder);
        }
    }

    public void subscribeExpectedOpeningPriceByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

        if (subscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice))
        {
            return;
        }

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);
        cfixMarketDataDispatcher.subscribeByClass(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void subscribeExpectedOpeningPriceByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

        if (subscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice))
        {
            return;
        }

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);
        cfixMarketDataDispatcher.subscribeByProduct(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void unsubscribeExpectedOpeningPrice(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeExpectedOpeningPrice(" + mdReqID + ") -- no such MDReqID", 0);
        }

        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
        if (cfixFixMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

            unsubscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);
            cfixMarketDataDispatcher.unsubscribeByProduct(cfixFixMarketDataConsumerHolder);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

            unsubscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_ExpectedOpeningPrice);
            cfixMarketDataDispatcher.unsubscribeByClass(cfixFixMarketDataConsumerHolder);
        }
    }

    public void subscribeNbboByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

        if (subscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Nbbo))
        {
            return;
        }

        cfixFixMarketDataConsumerHolder.acceptMarketDataNbbo(ServicesHelper.getMarketDataService().getNBBOForProduct(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey));

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);
        cfixMarketDataDispatcher.subscribeByProduct(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void subscribeNbboByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

        if (subscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Nbbo))
        {
            return;
        }

        cfixFixMarketDataConsumerHolder.acceptMarketDataNbbo(ServicesHelper.getMarketDataService().getNBBOForClass(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey));

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);
        cfixMarketDataDispatcher.subscribeByClass(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void unsubscribeNbbo(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeNbbo(" + mdReqID + ") -- no such MDReqID", 0);
        }

        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
        if (cfixFixMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

            unsubscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);
            cfixMarketDataDispatcher.unsubscribeByProduct(cfixFixMarketDataConsumerHolder);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

            unsubscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_Nbbo);
            cfixMarketDataDispatcher.unsubscribeByClass(cfixFixMarketDataConsumerHolder);
        }
    }

    public void subscribeRecapByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

        if (subscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Recap))
        {
            return;
        }

        cfixFixMarketDataConsumerHolder.acceptMarketDataRecap(ServicesHelper.getMarketDataService().getRecapForProduct(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey));

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_Recap);
        cfixMarketDataDispatcher.subscribeByProduct(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void subscribeRecapByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

        if (subscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Recap))
        {
            return;
        }

        cfixFixMarketDataConsumerHolder.acceptMarketDataRecap(ServicesHelper.getMarketDataService().getRecapForClass(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey).productRecaps);

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_Recap);
        cfixMarketDataDispatcher.subscribeByClass(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void unsubscribeRecap(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeRecap(" + mdReqID + ") -- no such MDReqID", 0);
        }

        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
        if (cfixFixMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

            unsubscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Recap);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_Recap);
            cfixMarketDataDispatcher.unsubscribeByProduct(cfixFixMarketDataConsumerHolder);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

            unsubscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Recap);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_Recap);
            cfixMarketDataDispatcher.unsubscribeByClass(cfixFixMarketDataConsumerHolder);
        }
    }

    public void subscribeTickerByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

        if (subscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Ticker))
        {
            return;
        }

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_Ticker);
        cfixMarketDataDispatcher.subscribeByProduct(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void unsubscribeTicker(String mdReqID) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder = (CfixMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(mdReqID);

        if (cfixFixMarketDataConsumerHolder == null)
        {
            throw ExceptionBuilder.dataValidationException("CfixMarketDataQueryProxy: Can't unsubscribeTicker(" + mdReqID + ") -- no such MDReqID", 0);
        }

        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().unregister(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
            methodInstrumentorList.remove(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
        if (cfixFixMarketDataConsumerHolder.containsSessionProductStruct())
        {
            SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

            unsubscribeProxyByProduct(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Ticker);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionProductStruct, CfixMarketDataDispatcherIF.MarketDataType_Ticker);
            cfixMarketDataDispatcher.unsubscribeByProduct(cfixFixMarketDataConsumerHolder);
        }
        else
        {
            SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

            unsubscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Ticker);

            CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_Ticker);
            cfixMarketDataDispatcher.unsubscribeByClass(cfixFixMarketDataConsumerHolder);
        }
    }

    public void subscribeTickerByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

        if (subscribeProxyByClass(cfixFixMarketDataConsumerHolder, CfixMarketDataDispatcherIF.MarketDataType_Ticker))
        {
            return;
        }

        CfixMarketDataDispatcherIF cfixMarketDataDispatcher = CfixMarketDataDispatcherHomeImpl.getInstance().find(sessionClassStruct, CfixMarketDataDispatcherIF.MarketDataType_Ticker);
        cfixMarketDataDispatcher.subscribeByClass(cfixFixMarketDataConsumerHolder);
        subscribedDispatchers.add(cfixMarketDataDispatcher);
        if (cfixFixMarketDataConsumerHolder.getMethodInstrumentor() != null)
        {
            addRegisteredInstrumentor(cfixFixMarketDataConsumerHolder.getMethodInstrumentor());
        }
    }

    public void unsubscribeListener() throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        //Log.information(Thread.currentThread().getName() + " BEFORE unsubscribeListenerXXX dispatchers(" + subscribedDispatchers.size() + ")");
        Object[] dispatcherArray = subscribedDispatchers.toArray();
        for (int i=0; i < dispatcherArray.length; i++ )
        {
            try
            {
                ((CfixMarketDataDispatcherIF) dispatcherArray[i]).unsubscribeConsumer(cfixFixMarketDataConsumer);
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
//        mdReqIDToCfixMarketDataStructHolderMap.clear(); // We can't clear this map because FutureResults will have to automatically rollback after this.
    }
//-----------------------------------------------------------------------------------------------------------------------------------------
    protected boolean subscribeProxyByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();
        String             mdReqID            = cfixFixMarketDataConsumerHolder.getOverlayPolicyMarketDataList().getMdReqID();

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
            cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.InsufficientPermissions, "Insufficient Permissions", mdReqID));
            return true;
        }

        SessionProductStruct[] sessionProductStructs = SessionProductStructCache.getSessionProductStructs(sessionClassStruct);

        synchronized(this)
        {
            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " IN SUBSCRIPTION CHECKING MDReqID(" + mdReqID + ")");

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

            mdReqIDToCfixMarketDataStructHolderMap.putKeyValue(mdReqID, cfixFixMarketDataConsumerHolder);

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
                        mdReqID = unsubscribeProxyByProduct((CfixMarketDataConsumerHolder) mdReqIDToCfixMarketDataStructHolderMap.getValueForKey(cfixSubscriptionHolder.getMdReqID(marketDataType)), marketDataType);
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

    protected String unsubscribeProxyByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionClassStruct sessionClassStruct = cfixFixMarketDataConsumerHolder.getSessionClassStruct();

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

    protected boolean subscribeProxyByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();
        String               mdReqID              = cfixFixMarketDataConsumerHolder.getOverlayPolicyMarketDataList().getMdReqID();

        if (userEnablement == null)
        {
            userEnablement = ServicesHelper.getUserEnablementService(cfixSessionManager.getValidUser().userId
                                                                     , cfixSessionManager.getValidUser().userAcronym.exchange
                                                                     , cfixSessionManager.getValidUser().userAcronym.acronym);

        }

        try
        {
            userEnablement.verifyUserEnablement(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey, makeOperationType(marketDataType));
        }
        catch (Exception ex)
        {
            Log.exception(ex);
            cfixFixMarketDataConsumer.acceptMarketDataReject(new FixMarketDataRejectStruct(FixMarketDataRejectStruct.InsufficientPermissions, "Insufficient Permissions", mdReqID));
            return true;
        }

        SessionProductStructCache.getSessionProductStructs(sessionProductStruct);

        synchronized(this)
        {
            if (mdReqIDToCfixMarketDataStructHolderMap.containsKey(mdReqID))
            {
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

//            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SUBSCRIPTION Added MDReqID(" + mdReqID + ")");

            mdReqIDToCfixMarketDataStructHolderMap.putKeyValue(mdReqID, cfixFixMarketDataConsumerHolder);

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

    protected String unsubscribeProxyByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder, int marketDataType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException
    {
        SessionProductStruct sessionProductStruct = cfixFixMarketDataConsumerHolder.getSessionProductStruct();

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

//            if (Log.isDebugOn()) Log.debug(Thread.currentThread().getName() + " SUBSCRIPTION Removed MDReqID(" + mdReqID + ")");

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

}
