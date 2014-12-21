package com.cboe.application.inprocess.tradingSession;

import com.cboe.application.inprocess.shared.InProcessServicesHelper;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.AcceptTextMessageProcessor;
import com.cboe.application.shared.consumer.AcceptTextMessageProcessorFactory;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.supplier.ProductStatusCollectorSupplier;
import com.cboe.application.supplier.ProductStatusCollectorSupplierFactory;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.AcceptTextMessageCollector;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.UserTradingSessionService;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.InProcessTradingSession;
import com.cboe.interfaces.application.inprocess.UserSessionAdminConsumer;
import com.cboe.interfaces.callback.ClassStatusConsumer;
import com.cboe.interfaces.callback.ProductStatusConsumer;
import com.cboe.interfaces.callback.StrategyStatusConsumer;
import com.cboe.interfaces.callback.TradingSessionStatusConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;

/**
 * This implements InProcessTradingSession.  All the query calls are delegated to UserTradingSessionService, which
 * is common to both cmi users and inprocess(e.t.fix) users.
 * However, this class does implement all subscription to the events itself like TradingSessionImpl in cmi side.
 * @author Jing Chen
 */
public class InProcessTradingSessionImpl extends BObject implements
        InProcessTradingSession,
        UserSessionLogoutCollector,
        AcceptTextMessageCollector
{
    protected UserSessionAdminConsumer textMessageConsumer;
    protected InProcessSessionManager sessionManager;
    protected UserSessionLogoutProcessor logoutProcessor;
    protected UserTradingSessionService userTradingSessionQueryService;
    protected ProductStatusCollectorSupplier productStatusCollectorSupplier;
    protected AcceptTextMessageProcessor acceptTextMessageProcessor;


    public InProcessTradingSessionImpl(UserSessionAdminConsumer consumer)
    {
        super();
        textMessageConsumer = consumer;
    }

    public void create(String name)
    {
        super.create(name);

        // init the event channal stuff
        acceptTextMessageProcessor = AcceptTextMessageProcessorFactory.create(this);
        productStatusCollectorSupplier = ProductStatusCollectorSupplierFactory.create();
        productStatusCollectorSupplier.setDynamicChannels(true);
    }

    public void setInProcessSessionManager(InProcessSessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, sessionManager);
        LogoutServiceFactory.find().addLogoutListener(sessionManager, this);
        userTradingSessionQueryService = ServicesHelper.getUserTradingSessionService(sessionManager);
    }

    public void acceptTextMessage(MessageStruct message)
    {
        textMessageConsumer.acceptTextMessage(message);
    }

    public ProductTypeStruct[] getProductTypesForSession(String sessionName)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return userTradingSessionQueryService.getProductTypesForSession(sessionName);
    }

    public SessionStrategyStruct[] getStrategiesByComponent(int componentKey, String sessionName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return userTradingSessionQueryService.getStrategiesByComponent(componentKey, sessionName);
    }

    public SessionClassStruct getClassBySessionForKey(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return userTradingSessionQueryService.getClassBySessionForKey(sessionName, classKey);
    }

    public SessionProductStruct getProductBySessionForKey(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return userTradingSessionQueryService.getProductBySessionForKey(sessionName, productKey);
    }

    public SessionStrategyStruct getStrategyBySessionForKey(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return userTradingSessionQueryService.getStrategyBySessionForKey(sessionName, productKey);
    }

    public SessionProductStruct[] subscribeProductForSession(String sessionName, int classKey, int productKey, ProductStatusConsumer productStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        SessionProductStruct[] sessionProducts = userTradingSessionQueryService.getProductsForSession(sessionName, classKey);
        subscribeProductStatusConsumerForProduct(sessionName, classKey, productKey, productStatusConsumer);
        return sessionProducts;
    }

    public SessionProductStruct getProductBySessionForName(String sessionName, ProductNameStruct productName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return userTradingSessionQueryService.getProductBySessionForName(sessionName, productName);
    }

    public SessionClassStruct[] getClassesForSession(String sessionName, short productType, ClassStatusConsumer classStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionClassStruct[] classes = userTradingSessionQueryService.getClassesForSession(sessionName, productType);
        subscribeClassStatusConsumerForType(sessionName, productType, classStatusConsumer);
        return classes;
    }

    public SessionClassStruct getClassBySessionForSymbol(String sessionName, short productType, String className)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return userTradingSessionQueryService.getClassBySessionForSymbol(sessionName, productType, className);
    }

    public TradingSessionStruct[] getCurrentTradingSessions(TradingSessionStatusConsumer tradingSessionStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        TradingSessionStruct[] tradingSessions = userTradingSessionQueryService.getCurrentTradingSessions();
        subscribeTradingSessionStatus(tradingSessionStatusConsumer);
        return tradingSessions;
    }

    public SessionProductStruct[] getProductsForSession(String sessionName, int classKey, ProductStatusConsumer productStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionProductStruct[] sessionProducts = userTradingSessionQueryService.getProductsForSession(sessionName, classKey);
        subscribeProductStatusConsumerForClass(sessionName, classKey, productStatusConsumer);
        return sessionProducts;
    }

    public SessionStrategyStruct[] getStrategiesByClassForSession(String sessionName, int classKey, StrategyStatusConsumer strategyStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionStrategyStruct[] strategies = userTradingSessionQueryService.getStrategiesByClassForSession(sessionName, classKey);
        subscribeStrategyConsumerForClass(sessionName, classKey, strategyStatusConsumer);
        return strategies;
    }

    public void unsubscribeClassesByTypeForSession(String sessionName, short type, ClassStatusConsumer classStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeClassesByTypeForSession for " + sessionManager);
        }
        if (classStatusConsumer != null)
        {
            SessionKeyContainer key = new SessionKeyContainer(sessionName, type);
            ChannelKey channelKey;

            ChannelListener proxy = InProcessServicesHelper.getClassStatusConsumerProxy(classStatusConsumer, sessionManager);

            channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, key);
            productStatusCollectorSupplier.removeChannelListener(this, proxy, channelKey);

            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, key);
            productStatusCollectorSupplier.removeChannelListener(this, proxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_TYPE, Integer.valueOf(type));
            EventChannelAdapterFactory.find().removeChannelListener(this, acceptTextMessageProcessor, channelKey);
        }
        else
        {
            Log.alarm(this, "null classStatusConsumer in unsubscribeClassesByTypeForSession for session:"+sessionManager);
        }
    }

    public void unsubscribeProductsByClassForSession(String sessionName, int classKey, ProductStatusConsumer productStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeProductsByClassForSession for " + sessionManager);
        }
        if (productStatusConsumer != null)
        {
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
            ChannelListener proxy = InProcessServicesHelper.getProductStatusConsumerProxy(productStatusConsumer, sessionManager);

            ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT, key);
            productStatusCollectorSupplier.removeChannelListener(this, proxy, channelKey);

            channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, key);
            productStatusCollectorSupplier.removeChannelListener(this, proxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
            EventChannelAdapterFactory.find().removeChannelListener(this, acceptTextMessageProcessor, channelKey);
        }
        else
        {
            Log.alarm(this, "null productStatusConsumer in unsubscribeProductsByClassForSession for session:"+sessionManager);
        }
    }

    public void unsubscribeStrategiesByClassForSession(String sessionName, int classKey, StrategyStatusConsumer strategyStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeStrategiesByClassForSession for " + sessionManager);
        }
        if (strategyStatusConsumer != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer theKey = new SessionKeyContainer(sessionName, classKey);
            ChannelKey channelKey;

            ChannelListener proxy = InProcessServicesHelper.getStrategyStatusConsumerProxy(strategyStatusConsumer, sessionManager);

            channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, theKey);
            productStatusCollectorSupplier.removeChannelListener(this, proxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
            EventChannelAdapterFactory.find().removeChannelListener(this, acceptTextMessageProcessor, channelKey);
        }
        else
        {
            Log.alarm(this, "null strategyStatusConsumer in unsubscribeStrategiesByClassForSession for session:"+sessionManager);
        }
    }

    public void unsubscribeTradingSessionStatus(TradingSessionStatusConsumer tradingSessionStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTradingSessionStatus for " + sessionManager);
        }
        if ( tradingSessionStatusConsumer != null)
        {
            ///////// add the call back consumer to the supplier list/////
            Integer key = 0;
            ChannelListener proxyListener = InProcessServicesHelper.getTradingSessionStatusConsumerProxy(tradingSessionStatusConsumer, sessionManager);

            ChannelKey channelKey = new ChannelKey(ChannelType.TRADING_SESSION, key);
            EventChannelAdapterFactory.find().removeChannelListener(this, proxyListener, channelKey);
        }
        else
        {
            Log.alarm(this, "null tradingSessionStatusConsumer in unsubscribeTradingSessionStatus for session:"+sessionManager);
        }
    }

    protected void subscribeClassStatusConsumerForType(String sessionName, short productType, ClassStatusConsumer  clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, productType);
            ChannelKey channelKey;
            ChannelListener proxy = InProcessServicesHelper.getClassStatusConsumerProxy(clientListener, sessionManager);

            channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, key);
            productStatusCollectorSupplier.addChannelListener(this, proxy, channelKey);

            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, key);
            productStatusCollectorSupplier.addChannelListener(this, proxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_TYPE, Integer.valueOf(productType));
            EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
            userTradingSessionQueryService.publishMessagesForProductType( productType );
        }
        else
        {
            Log.alarm(this, "null ClassStatusConsumer in subscribeClassStatusConsumerForType for session:"+sessionManager);
        }
    }// end of subscribeClassStatusConsumerForClass

    public void subscribeTradingSessionStatus(TradingSessionStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTradingSessionStatus for " + sessionManager);
        }
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            Integer key = 0;

            ChannelListener proxy = InProcessServicesHelper.getTradingSessionStatusConsumerProxy(clientListener, sessionManager);
            ChannelKey channelKey = new ChannelKey(ChannelType.TRADING_SESSION, key);
            InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, proxy, channelKey);
        }
        else
        {
            Log.alarm(this, "null TradingSessionStatusConsumer in subscribeTradingSessionStatus for session:"+sessionManager);
        }
    }

    protected void subscribeProductStatusConsumerForClass(String sessionName, int classKey, ProductStatusConsumer  clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
            ChannelListener proxy = InProcessServicesHelper.getProductStatusConsumerProxy(clientListener, sessionManager);

            // Register for Product change with the IEC
            ChannelKey channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, key);
            productStatusCollectorSupplier.addChannelListener(this, proxy, channelKey);

            subscribeProductUpdatesByClass(key, proxy, classKey);

        }
        else
        {
            Log.alarm(this, "null ProductStatusConsumer in subscribeProductStatusConsumerForClass for session:"+sessionManager);
        }
    }// end of subscribeConsumerForClass

    private void subscribeProductUpdatesByClass(SessionKeyContainer key, ChannelListener proxy, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        ChannelKey channelKey;
        channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_BY_CLASS, key);
        productStatusCollectorSupplier.addChannelListener(this, proxy, channelKey);

        channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
        EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
        userTradingSessionQueryService.publishMessagesForProductClass( classKey);
    }

    protected void subscribeProductStatusConsumerForProduct(String sessionName, int classKey, int productKey, ProductStatusConsumer  clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, productKey);
            ChannelListener proxy = InProcessServicesHelper.getProductStatusConsumerProxy(clientListener, sessionManager);

            // Register for Product change with the IEC
            ChannelKey channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, key);
            productStatusCollectorSupplier.addChannelListener(this, proxy, channelKey);

            key = new SessionKeyContainer(sessionName, classKey);
            subscribeProductUpdatesByClass(key, proxy, classKey);
        }
        else
        {
            Log.alarm(this, "null ProductStatusConsumer in subscribeProductStatusConsumerForClass for session:"+sessionManager);
        }
    }// end of subscribeConsumerForClass

    public void unsubscribeProductForSession(String sessionName, int classKey, int productKey, ProductStatusConsumer productStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeProductForSession for " + sessionManager);
        }
        if (productStatusConsumer != null)
        {
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
            ChannelListener proxy = InProcessServicesHelper.getProductStatusConsumerProxy(productStatusConsumer, sessionManager);

            ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_BY_CLASS, key);
            productStatusCollectorSupplier.removeChannelListener(this, proxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
            EventChannelAdapterFactory.find().removeChannelListener(this, acceptTextMessageProcessor, channelKey);

            //Unsubscribe the product for the product status
            key = new SessionKeyContainer(sessionName,productKey);

            channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, key);
            productStatusCollectorSupplier.removeChannelListener(this, proxy, channelKey);

        }
        else
        {
            Log.alarm(this, "null productStatusConsumer in unsubscribeProductsByClassForSession for session:"+sessionManager);
        }

    }


    protected void subscribeStrategyConsumerForClass(String sessionName, int classKey, StrategyStatusConsumer  clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer theKey = new SessionKeyContainer(sessionName, classKey);
            ChannelKey channelKey;

            ChannelListener proxy = InProcessServicesHelper.getStrategyStatusConsumerProxy(clientListener, sessionManager);

            channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, theKey);
            productStatusCollectorSupplier.addChannelListener(this, proxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
            EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
            userTradingSessionQueryService.publishMessagesForProductClass( classKey );
        }
        else
        {
            Log.alarm(this, "null StrategyStatusConsumer in subscribeStrategyConsumerForClass for session:"+sessionManager);
        }
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }

        // Do any individual service clean up needed for logout
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        InstrumentedEventChannelAdapterFactory.find().removeListenerGroup(this);

        productStatusCollectorSupplier.removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);

        productStatusCollectorSupplier = null;
        userTradingSessionQueryService = null;
        logoutProcessor.setParent(null);
        acceptTextMessageProcessor.setParent(null);
        logoutProcessor = null;
        acceptTextMessageProcessor = null;

        textMessageConsumer = null;
        sessionManager = null;
    }

}
