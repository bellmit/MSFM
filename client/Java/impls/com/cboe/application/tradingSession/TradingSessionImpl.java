package com.cboe.application.tradingSession;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.*;
import com.cboe.application.supplier.*;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.UserSessionKey;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiCallback.CMIClassStatusConsumer;
import com.cboe.idl.cmiCallback.CMIProductStatusConsumer;
import com.cboe.idl.cmiCallback.CMIStrategyStatusConsumer;
import com.cboe.idl.cmiCallback.CMITradingSessionStatusConsumer;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiSession.*;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;

public class TradingSessionImpl extends BObject implements TradingSession, AcceptTextMessageCollector, TradingSessionStatusCollector, ProductStatusCollector, UserSessionLogoutCollector
{
    protected SessionManager currentSession;

    protected TradingSessionStatusProcessor tradingSessionProcessor;
    protected AcceptTextMessageProcessor acceptTextMessageProcessor;
    protected UserSessionLogoutProcessor logoutProcessor;
    protected ChannelListener productStatusCollectorProxy;

    protected TradingSessionStatusSupplier  tradingSessionSupplier;
    protected ProductStatusSupplier productStatusSupplier;
    protected ClassStatusSupplier classStatusSupplier;
    protected StrategyStatusSupplier strategyStatusSupplier;
    protected UserSessionAdminSupplier adminSupplier;
    protected ProductStatusCollectorSupplier productStatusCollectorSupplier;
    protected UserTradingSessionService userTradingSessionQueryService;

    private static final Integer INT_0 = 0;

    public TradingSessionImpl()
    {
        super();
    }

    public void create(String name)
    {
        super.create(name);

        // init the event channel stuff
        tradingSessionProcessor = TradingSessionStatusProcessorFactory.create(this);
        acceptTextMessageProcessor = AcceptTextMessageProcessorFactory.create(this);

        tradingSessionSupplier = TradingSessionStatusSupplierFactory.create();
        productStatusSupplier = ProductStatusSupplierFactory.create();
        classStatusSupplier = ClassStatusSupplierFactory.create();
        strategyStatusSupplier = StrategyStatusSupplierFactory.create();
        productStatusCollectorSupplier = ProductStatusCollectorSupplierFactory.create();

        // Make Sure all channels are dynamic
        tradingSessionSupplier.setDynamicChannels(true);
        productStatusSupplier.setDynamicChannels(true);
        classStatusSupplier.setDynamicChannels(true);
        strategyStatusSupplier.setDynamicChannels(true);
        productStatusCollectorSupplier.setDynamicChannels(true);
    }

    public void setSessionManager(SessionManager theSession)
    {
        currentSession = theSession;
        adminSupplier = UserSessionAdminSupplierFactory.find( currentSession );
        adminSupplier.setDynamicChannels(true);
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, theSession);
        LogoutServiceFactory.find().addLogoutListener(theSession, this);

        productStatusCollectorProxy = ServicesHelper.getProductStatusCollectorProxy(this, currentSession);
        userTradingSessionQueryService = ServicesHelper.getUserTradingSessionService(theSession);
    }

    public BaseSessionManager getSessionManager()
    {
        return currentSession;
    }

    /////////////// IDL exported methods ////////////////////////////////////

    public ProductTypeStruct[] getProductTypesForSession(String sessionName)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return userTradingSessionQueryService.getProductTypesForSession(sessionName);
    }

    public TradingSessionStruct[] getCurrentTradingSessions(CMITradingSessionStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        TradingSessionStruct[] tradingSessions = userTradingSessionQueryService.getCurrentTradingSessions();
        subscribeTradingSessionStatus(clientListener);
        return tradingSessions;
    }

    public SessionClassStruct[] getClassesForSession(String sessionName, short productType, CMIClassStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionClassStruct[] classes = userTradingSessionQueryService.getClassesForSession(sessionName, productType);
        subscribeClassStatusConsumerForType(sessionName, productType, clientListener);
        return classes;
    }

    public SessionProductStruct[] getProductsForSession(String sessionName, int classKey, CMIProductStatusConsumer clientListener)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionProductStruct[] sessionProducts = userTradingSessionQueryService.getProductsForSession(sessionName, classKey);
        subscribeProductStatusConsumerForClass(sessionName, classKey, clientListener);
        return sessionProducts;
    }

    public SessionStrategyStruct[] getStrategiesByClassForSession(String sessionName, int classKey, CMIStrategyStatusConsumer clientListener)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionStrategyStruct[] strategies = userTradingSessionQueryService.getStrategiesByClassForSession(sessionName, classKey);
        subscribeStrategyConsumerForClass(sessionName, classKey, clientListener);
        return strategies;
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

    public void unsubscribeClassesByTypeForSession(String sessionName, short type, CMIClassStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeClassesByTypeForSession for " + currentSession);
        }
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, type);
            UserSessionKey theKey = new UserSessionKey(currentSession, key);
            ChannelKey channelKey;

            ChannelListener proxy = ServicesHelper.getClassStatusConsumerProxy(clientListener, currentSession);

            channelKey = new ChannelKey(ChannelType.CB_CLASS_STATE_BY_TYPE, theKey);
            classStatusSupplier.removeChannelListener(this, proxy, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_CLASS_UPDATE_BY_TYPE, theKey);
            classStatusSupplier.removeChannelListener(this, proxy, channelKey, key);

            channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, key);
            productStatusCollectorSupplier.removeChannelListener(this, productStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, key);
            productStatusCollectorSupplier.removeChannelListener(this, productStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_TYPE, Integer.valueOf(type));
            EventChannelAdapterFactory.find().removeChannelListener(this, acceptTextMessageProcessor, channelKey);
        }
    }

    public void unsubscribeProductsByClassForSession(String sessionName, int classKey, CMIProductStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeProductsByClassForSession for " + currentSession);
        }
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
            UserSessionKey theKey = new UserSessionKey(currentSession, key);

            ChannelListener proxy = ServicesHelper.getProductStatusConsumerProxy(clientListener, currentSession);

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_PRODUCT_STATE_BY_CLASS, theKey);
            productStatusSupplier.removeChannelListener(this, proxy, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, theKey);
            productStatusSupplier.removeChannelListener(this, proxy, channelKey, key);

            // Register for Product change with the IEC
            channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, key);
            productStatusCollectorSupplier.removeChannelListener(this, productStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT, key);
            productStatusCollectorSupplier.removeChannelListener(this, productStatusCollectorProxy, channelKey);
            
            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
            EventChannelAdapterFactory.find().removeChannelListener(this, acceptTextMessageProcessor, channelKey);
        }
    }

    public void unsubscribeStrategiesByClassForSession(String sessionName, int classKey, CMIStrategyStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeStrategiesByClassForSession for " + currentSession);
        }
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer theKey = new SessionKeyContainer(sessionName, classKey);
            UserSessionKey key = new UserSessionKey(currentSession, theKey);
            ChannelKey channelKey;

            ChannelListener proxy = ServicesHelper.getStrategyStatusConsumerProxy(clientListener, currentSession);

            channelKey = new ChannelKey(ChannelType.CB_STRATEGY_UPDATE, key);
            strategyStatusSupplier.removeChannelListener(this, proxy, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, theKey);
            productStatusCollectorSupplier.removeChannelListener(this, productStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
            EventChannelAdapterFactory.find().removeChannelListener(this, acceptTextMessageProcessor, channelKey);
        }
    }

    public SessionClassStruct getClassBySessionForSymbol(String sessionName, short productType, String className)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return userTradingSessionQueryService.getClassBySessionForSymbol(sessionName, productType, className);
    }

    public SessionProductStruct getProductBySessionForName(String sessionName, ProductNameStruct productName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return userTradingSessionQueryService.getProductBySessionForName(sessionName, productName);
    }

    public void unsubscribeTradingSessionStatus(CMITradingSessionStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTradingSessionStatus for " + currentSession);
        }
        if ( clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            Integer key = INT_0;
            UserSessionKey theKey = new UserSessionKey(currentSession, key);
            ChannelKey channelKey= new ChannelKey(ChannelType.CB_TRADING_SESSION_STATE, theKey);

            if (tradingSessionSupplier != null) {
                ChannelListener proxyListener = ServicesHelper.getTradingSessionStatusConsumerProxy(clientListener, currentSession);
                tradingSessionSupplier.removeChannelListener(this, proxyListener, channelKey);
            }

            channelKey = new ChannelKey(ChannelType.TRADING_SESSION, key);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, tradingSessionProcessor, channelKey);
        }
    }

    private void subscribeClassStatusConsumerForType(String sessionName, short productType, CMIClassStatusConsumer  clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, productType);
            UserSessionKey theKey = new UserSessionKey(currentSession, key);
            ChannelKey channelKey;
            ChannelListener proxy = ServicesHelper.getClassStatusConsumerProxy(clientListener, currentSession);

            channelKey = new ChannelKey(ChannelType.CB_CLASS_STATE_BY_TYPE, theKey);
            classStatusSupplier.addChannelListener(this, proxy, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_CLASS_UPDATE_BY_TYPE, theKey);
            classStatusSupplier.addChannelListener(this, proxy, channelKey, key);

            channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, key);
            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, key);
            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_TYPE, Integer.valueOf(productType));
            EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
            userTradingSessionQueryService.publishMessagesForProductType(productType);
        }
    }// end of subscribeClassStatusConsumerForClass

    protected void subscribeTradingSessionStatus(CMITradingSessionStatusConsumer clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTradingSessionStatus for " + currentSession);
        }
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            Integer key = INT_0;
            UserSessionKey theKey = new UserSessionKey(currentSession, key);
            ChannelKey channelKey= new ChannelKey(ChannelType.CB_TRADING_SESSION_STATE, theKey);

            ChannelListener proxyListener = ServicesHelper.getTradingSessionStatusConsumerProxy(clientListener, currentSession);
            tradingSessionSupplier.addChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.TRADING_SESSION, key);
            InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, tradingSessionProcessor, channelKey);
        }
    }

    protected UserTradingSessionService getTradingSessionQueryService()
    {
        if(userTradingSessionQueryService == null)
        {
            userTradingSessionQueryService = ServicesHelper.getUserTradingSessionService(currentSession);
        }
        return userTradingSessionQueryService;
    }

    public void acceptTextMessage( MessageStruct message )
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptTextMessage for " + currentSession);
        }
        String userId = getValidSessionProfileUser();
        ChannelKey channelKey = new ChannelKey( ChannelType.CB_TEXT_MESSAGE, userId );
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent( this, channelKey, message );

        adminSupplier.dispatch( event );
    }

    private String getValidSessionProfileUser()
    {
        String validUserId = null;
        try
        {
            validUserId = currentSession.getValidSessionProfileUser().userId;
        }
        catch (Exception e)
        {
            Log.exception(this, "session: " + currentSession, e);
        }

        return validUserId;
    }

    public void acceptTradingSessionState( com.cboe.idl.cmiSession.TradingSessionStateStruct sessionState)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptTradingSessionState for " + currentSession + " sessionName=" + sessionState.sessionName + " sessionState=" + sessionState.sessionState);
        }

        Integer key = INT_0;
        UserSessionKey theKey = new UserSessionKey(currentSession, key);
        ChannelKey channelKey= new ChannelKey(ChannelType.CB_TRADING_SESSION_STATE, theKey);
        ChannelEvent myEvent = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, sessionState);
        tradingSessionSupplier.dispatch(myEvent);
    }

    public void setProductState(ProductStateStruct[] productStates)
    {
        ChannelKey key;
        ChannelEvent event;

        if (Log.isDebugOn())
        {
            Log.debug(this, "calling setProductState for " + currentSession);
        }
        if(productStates.length > 0)
        {
            key = new ChannelKey(ChannelType.CB_PRODUCT_STATE_BY_CLASS, new UserSessionKey(currentSession, new SessionKeyContainer(productStates[0].sessionName, productStates[0].productKeys.classKey)));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, productStates);
            productStatusSupplier.dispatch(event);
        }

    }

    public void setClassState(ClassStateStruct newState)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling setClassState for " + currentSession);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_CLASS_STATE, new UserSessionKey(currentSession, new SessionKeyContainer(newState.sessionName, newState.classKey)));
        ClassStateStruct[] tempClass = {newState};

        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, tempClass);
        classStatusSupplier.dispatch(event);

        ClassStruct classStruct;
        try
        {
            classStruct = currentSession.getProductQuery().getClassByKey(newState.classKey);
        }
        catch(Exception e)
        {
            classStruct = null;
        }

        if(classStruct != null)
        {
            key = new ChannelKey(ChannelType.CB_CLASS_STATE_BY_TYPE, new UserSessionKey(currentSession, new SessionKeyContainer(newState.sessionName, classStruct.productType)));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, tempClass);
            classStatusSupplier.dispatch(event);
        }
        else
        {
            Log.alarm(this, "session: " + currentSession + " : Could not find product class for newState.classKey");
        }
    }


    public void updateProduct(SessionProductStruct updatedProduct)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling updateProduct for " + currentSession);
        }

        SessionProductStruct tempProduct = updatedProduct;
        ChannelKey key = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE, new UserSessionKey(currentSession, new SessionKeyContainer(updatedProduct.sessionName, updatedProduct.productStruct.productKeys.productKey)));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, tempProduct);
        productStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, new UserSessionKey(currentSession, new SessionKeyContainer(updatedProduct.sessionName, updatedProduct.productStruct.productKeys.classKey)));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, tempProduct);
        productStatusSupplier.dispatch(event);
    }

    public void updateProductClass(SessionClassStruct updatedClass)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling updateProductClass for " + currentSession);
        }

        SessionClassStruct tempClass = updatedClass;
        ChannelKey key = new ChannelKey(ChannelType.CB_PRODUCT_CLASS_UPDATE, new UserSessionKey(currentSession, new SessionKeyContainer(updatedClass.sessionName, updatedClass.classStruct.classKey)));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, tempClass);
        classStatusSupplier.dispatch(event);

        key = new ChannelKey(ChannelType.CB_CLASS_UPDATE_BY_TYPE, new UserSessionKey(currentSession, new SessionKeyContainer(updatedClass.sessionName, updatedClass.classStruct.productType)));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, tempClass);
        classStatusSupplier.dispatch(event);
    }

    public void updateProductStrategy(SessionStrategyStruct strategyStruct)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling updateProductStrategy for " + currentSession);
        }

        SessionStrategyStruct[] tempStrategy = new SessionStrategyStruct[1];
        ChannelKey key;
        key = new ChannelKey(ChannelType.CB_STRATEGY_UPDATE, new UserSessionKey(currentSession, new SessionKeyContainer(strategyStruct.sessionProductStruct.sessionName, strategyStruct.sessionProductStruct.productStruct.productKeys.classKey)));
        tempStrategy[0] = strategyStruct;
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, tempStrategy);
        strategyStatusSupplier.dispatch(event);
    }

    private void subscribeProductStatusConsumerForClass(String sessionName, int classKey, CMIProductStatusConsumer  clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
            UserSessionKey theKey = new UserSessionKey(currentSession, key);

            ChannelListener proxy = ServicesHelper.getProductStatusConsumerProxy(clientListener, currentSession);

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_PRODUCT_STATE_BY_CLASS, theKey);
            productStatusSupplier.addChannelListener(this, proxy, channelKey, key);

            channelKey = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, theKey);
            productStatusSupplier.addChannelListener(this, proxy, channelKey, key);

            // Register for Product change with the IEC
            channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, key);
            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_BY_CLASS, key);
            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
            EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
            userTradingSessionQueryService.publishMessagesForProductClass(classKey);
        }
    }// end of subscribeConsumerForClass

    protected void subscribeStrategyConsumerForClass(String sessionName, int classKey, CMIStrategyStatusConsumer  clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (clientListener != null)
        {
            ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer theKey = new SessionKeyContainer(sessionName, classKey);
            UserSessionKey key = new UserSessionKey(currentSession, theKey);
            ChannelKey channelKey;

            ChannelListener proxy = ServicesHelper.getStrategyStatusConsumerProxy(clientListener, currentSession);

            channelKey = new ChannelKey(ChannelType.CB_STRATEGY_UPDATE, key);
            strategyStatusSupplier.addChannelListener(this, proxy, channelKey, theKey);

            channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, theKey);
            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);

            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
            EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
            userTradingSessionQueryService.publishMessagesForProductClass( classKey );
        }
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + currentSession);
        }

        // Do any individual service clean up needed for logout
        EventChannelAdapterFactory.find().removeListenerGroup(this);

        productStatusCollectorSupplier.removeListenerGroup(this);
        productStatusSupplier.removeListenerGroup(this);
        tradingSessionSupplier.removeListenerGroup(this);
        classStatusSupplier.removeListenerGroup(this);
        strategyStatusSupplier.removeListenerGroup(this);
        adminSupplier.removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(currentSession,this);

        productStatusCollectorSupplier = null;
        tradingSessionSupplier = null;
        productStatusSupplier = null;
        classStatusSupplier = null;
        strategyStatusSupplier = null;
        adminSupplier = null;
        currentSession = null;
        userTradingSessionQueryService = null;
        logoutProcessor.setParent(null);
        tradingSessionProcessor.setParent(null);
        acceptTextMessageProcessor.setParent(null);

        logoutProcessor = null;
        tradingSessionProcessor = null;
        acceptTextMessageProcessor = null;
        productStatusCollectorProxy = null;
    }
}
