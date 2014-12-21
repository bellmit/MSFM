package com.cboe.application.tradingSession.adapter;

import com.cboe.interfaces.application.TradingSessionServiceAdapter;
import com.cboe.interfaces.businessServices.TradingSessionService;
import com.cboe.interfaces.events.IECTradingSessionConsumerHome;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.session.BusinessDaySessionStruct;
import com.cboe.idl.session.BusinessDayStruct;
import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.application.cache.CacheFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.tradingSession.cache.TradingSessionCacheKeyFactory;
import com.cboe.application.tradingSession.TradingSessionEventListener;
import com.cboe.domain.util.*;
import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import java.util.*;

public final class TradingSessionServiceAdapterImpl extends BObject implements TradingSessionServiceAdapter
{
    private TradingSessionService tradingSessionService;
    protected IECTradingSessionConsumerHome tradingSessionConsumerHome;
    private HashSet sessionNames;
    private HashMap activeListeners;
    private boolean engineIsStock;

    TradingSessionServiceAdapterImpl()
    {
        super();
        sessionNames = new HashSet();
    }

    public void create(String name)
    {
        super.create(name);
    }

    void foundationFrameworkInitialize()
    {
        getTradingSessionService();
        getTradingSessionConsumerHome();

        boolean fatal = false;

        if(tradingSessionService == null)
        {
            fatal = true;
            Log.alarm(this, "Unable to access trading session service delegate!");
        }

        if(tradingSessionConsumerHome == null)
        {
            fatal = true;
            Log.alarm(this, "Unable to access trading session consumer home!");
        }

        try
        {
            String engineMode = System.getProperty(FixUtilConstants.SbtValues.EngineMode.ENGINE_MODE, FixUtilConstants.SbtValues.EngineMode.HYBRID);
            engineIsStock = engineMode.equalsIgnoreCase("STOCK");
        }
        catch (Exception e)
        {
            Log.exception(this, "Cannot read parameter " + FixUtilConstants.SbtValues.EngineMode.ENGINE_MODE, e);
            engineIsStock = false;
        }

        if(fatal)
        {
            throw new RuntimeException("Fatal exception during initialization of Trading Session Service Adapter.");
        }
    }

    public SessionClassStruct getClassBySessionForKey(String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionClassStruct sessionClass = (SessionClassStruct) CacheFactory.getSessionClassCache(sessionName).find(TradingSessionCacheKeyFactory.getPrimaryClassKey(), Integer.valueOf(classKey));

        if(sessionClass == null)
        {
            sessionClass = getTradingSessionService().getClassBySessionForKey(sessionName,classKey);

            CacheFactory.updateSessionClassCache(sessionName, sessionClass);
        }

        return sessionClass;
    }

    public SessionClassStruct getClassBySessionForSymbol(String sessionName, short productType, String className) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionClassStruct sessionClass = (SessionClassStruct) CacheFactory.getSessionClassCache(sessionName).find(TradingSessionCacheKeyFactory.getClassGroupByTypeKey(), Integer.valueOf(productType), className);

        if(sessionClass == null)
        {
            sessionClass = getTradingSessionService().getClassBySessionForSymbol(sessionName,productType,className);

            CacheFactory.updateSessionClassCache(sessionName,sessionClass);
        }

        return sessionClass;
    }

    public SessionClassStruct[] getClassesForSession(String sessionName, short productType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionClassStruct[] classes = (SessionClassStruct[]) CacheFactory.getSessionClassCache(sessionName).findAllInGroup(TradingSessionCacheKeyFactory.getClassGroupByTypeKey(), Integer.valueOf(productType));

        if(((classes == null) || (classes.length == 0)) && (!isSessionInitialized(sessionName)))
        {
            classes = getTradingSessionService().getClassesBySessionForType(sessionName, productType);
            CacheFactory.loadSessionClassCache(sessionName,classes);
        }

        return classes;
    }

    public TradingSessionStruct[] getCurrentTradingSessions()  throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        com.cboe.idl.session.TradingSessionStruct[] sessionStructs = getTradingSessionService().getTradingSessions();
        TradingSessionStruct[] cmiSessionStructs;
        cmiSessionStructs = TradingSessionStructBuilder.convertCMITradingSessionStructs(sessionStructs);
        return cmiSessionStructs;
    }

    public SessionProductStruct getProductBySessionForKey(String sessionName, int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProduct = null;
        sessionProduct = (SessionProductStruct) CacheFactory.getSessionProductCache(sessionName).find(TradingSessionCacheKeyFactory.getPrimaryProductKey(), Integer.valueOf(productKey));

        if(sessionProduct == null)
        {
            sessionProduct = getTradingSessionService().getProductBySessionForKey(sessionName,productKey);
            productFilterForClass(sessionName, sessionProduct.productStruct.productKeys.classKey, true);

            if(sessionProduct != null)
            {
                checkSessionProductCacheLoaded(sessionName, sessionProduct.productStruct.productKeys.classKey);
            }
        }

        return sessionProduct;
    }

    public SessionProductStruct getProductBySessionForName(String sessionName, ProductNameStruct productName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProduct = null;
        SessionProductStruct sps = new SessionProductStruct();
        sps.productStruct = new ProductStruct();
        sps.productStruct.productName = productName;
        sessionProduct = (SessionProductStruct) CacheFactory.getSessionProductCache(sessionName).find(
                TradingSessionCacheKeyFactory.getProductNameKey(),
                TradingSessionCacheKeyFactory.getProductNameKey().generateKey(sps));

        if(sessionProduct == null)
        {
            sessionProduct = getTradingSessionService().getProductBySessionForName(sessionName,productName);
            productFilterForClass(sessionName, sessionProduct.productStruct.productKeys.classKey, true);

            if(sessionProduct != null)
            {
                checkSessionProductCacheLoaded(sessionName, sessionProduct.productStruct.productKeys.classKey);
            }
        }

        return sessionProduct;
    }

    public ProductTypeStruct[] getProductTypesForSession(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ProductTypeStruct[] types = (ProductTypeStruct[]) CacheFactory.getSessionProductTypeCache(sessionName).findAll(TradingSessionCacheKeyFactory.getPrimaryTypeKey());

        if(((types == null) || (types.length == 0)) && (!isSessionInitialized(sessionName)))
        {
            types = getTradingSessionService().getProductTypesForSession(sessionName);
            CacheFactory.getSessionProductTypeCache(sessionName).loadCache(types);
        }

        return types;
    }

    public SessionProductStruct[] getProductsForSession(String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        checkSessionProductCacheLoaded(sessionName, classKey);
        return (SessionProductStruct[]) CacheFactory.getSessionProductCache(sessionName).findAllInGroup(TradingSessionCacheKeyFactory.getProductGroupByClassKey(), Integer.valueOf(classKey));
    }

    public SessionStrategyStruct[] getStrategiesByClassForSession(String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        checkSessionStrategyCacheLoaded(sessionName, classKey);
        return (SessionStrategyStruct[]) CacheFactory.getSessionStrategyCache(sessionName).findAllInGroup(TradingSessionCacheKeyFactory.getStrategyGroupByClassKey(), Integer.valueOf(classKey));
    }

    public SessionStrategyStruct[] getStrategiesByComponent(int componentKey, String sessionName) throws SystemException, DataValidationException, AuthorizationException, CommunicationException
    {
        return getTradingSessionService().getStrategiesByComponent(componentKey, sessionName);
    }

    public SessionStrategyStruct getStrategyBySessionForKey(String sessionName, int strategyKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionStrategyStruct sessionStrategy = (SessionStrategyStruct) CacheFactory.getSessionStrategyCache(sessionName).find(TradingSessionCacheKeyFactory.getPrimaryStrategyKey(), Integer.valueOf(strategyKey));
        if(sessionStrategy == null)
        {
            sessionStrategy = getTradingSessionService().getStrategyBySessionForKey(sessionName,strategyKey);
            strategyFilterForClass(sessionName, sessionStrategy.sessionProductStruct.productStruct.productKeys.classKey, true);

            if(sessionStrategy != null)
            {
                checkSessionStrategyCacheLoaded(sessionName, sessionStrategy.sessionProductStruct.productStruct.productKeys.classKey);
            }
        }
        return sessionStrategy;
    }

    private TradingSessionService getTradingSessionService()
    {
        if(tradingSessionService == null )
        {
            tradingSessionService = ServicesHelper.getTradingSessionService();
        }

        return tradingSessionService;
    }

    private IECTradingSessionConsumerHome getTradingSessionConsumerHome()
    {
        if(tradingSessionConsumerHome == null)
        {
            tradingSessionConsumerHome = ServicesHelper.getTradingSessionConsumerHome();
        }

        return tradingSessionConsumerHome;
    }

    private void productFilterForClass(String sessionName, int classKey, boolean addFilter) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_BY_CLASS, sessionKey);
        modifyFilter(channelKey, addFilter);
        channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, sessionKey);
        modifyFilter(channelKey, addFilter);
    }

    protected void strategyFilterForClass(String sessionName, int classKey, boolean addFilter) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        ChannelKey channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, sessionKey);
        modifyFilter(channelKey, addFilter);
    }

    private void modifyFilter(ChannelKey channelKey, boolean addFilter) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        if(addFilter)
        {
            getTradingSessionConsumerHome().addFilter(channelKey);
        }
        else
        {
            getTradingSessionConsumerHome().removeFilter(channelKey);
        }
    }

    //TSRefactor: Method used to be declared synchronized when it lived in TradingSessionImpl
    private void checkSessionProductCacheLoaded(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        CacheClassTrackImpl classTrack = CacheClassTrackFactory.find(sessionKey);

        synchronized(classTrack)
        {
            if(!classTrack.wasSessionProductsLoaded())
            {
                if(Log.isDebugOn())
                {
                    Log.debug(this, "*** product NOT loaded for " + sessionName + " where classKey="+classKey);
                }

                loadProductsForClass(sessionName, classKey);
                classTrack.setSessionProductsLoaded(true);
            }
            else
            {
                if(Log.isDebugOn())
                {
                    Log.debug(this, "*** product ALREADY loaded for " + sessionName + " where classKey="+classKey);
                }
            }
        }
    }

    //TSRefactor: Method used to be declared synchronized when it lived in TradingSessionImpl
    private void checkSessionStrategyCacheLoaded(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        CacheClassTrackImpl classTrack = CacheClassTrackFactory.find(sessionKey);

        synchronized(classTrack)
        {
            if(!classTrack.wasSessionStrategiesLoaded())
            {
                if(Log.isDebugOn())
                {
                    Log.debug(this, "*** strategy NOT loaded for " + sessionName + " where classKey="+classKey);
                }

                loadStrategiesForClass(sessionName, classKey);
                classTrack.setSessionStrategiesLoaded(true);
            }
            else
            {
                if(Log.isDebugOn())
                {
                    Log.debug(this, "*** strategy ALREADY loaded for " + sessionName + " where classKey="+classKey);
                }
            }
        }
    }

    //TSRefactor: Method used to be declared synchronized when it lived in TradingSessionImpl
    private SessionProductStruct[] loadProductsForClass(String sessionName, int classKey) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        SessionProductStruct[] products = null;
        if(Log.isDebugOn())
        {
            Log.debug(this, "Loading session product cache where session="+sessionName+" and classKey="+classKey);
        }

        productFilterForClass(sessionName, classKey, true);
        products = getTradingSessionService().getProductsBySessionForClass(sessionName, classKey);

        if(Log.isDebugOn())
        {
            Log.debug(this, "TradingSessionService.loadProductsForClass returned "+products.length+" products for classkey = " + classKey);
        }
        CacheFactory.loadSessionProductCache(sessionName, products);
        return products;
    }

    //TSRefactor: Method used to be declared synchronized when it lived in TradingSessionImpl
    private SessionStrategyStruct[] loadStrategiesForClass(String sessionName, int classKey) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        SessionStrategyStruct[] strategies = null;
        if(Log.isDebugOn())
        {
            Log.debug(this, "Loading session strategy cache where session="+sessionName+" and classKey="+classKey);
        }

        strategies = getTradingSessionService().getStrategiesBySessionForClass(sessionName, classKey);
        strategyFilterForClass(sessionName, classKey, true);

        if(Log.isDebugOn())
        {
            Log.debug(this, "TradingSessionService.loadStrategiesForClass returned "+strategies.length+" products for classkey = " + classKey);
        }

        CacheFactory.loadSessionStrategyCache(sessionName, strategies);
        return strategies;
    }

    private synchronized Map getListeners()
    {
        if(activeListeners == null)
        {
            activeListeners = new HashMap();
        }

        return activeListeners;
    }

    public boolean isSessionInitialized(String sessionName)
    {
        return sessionNames.contains(sessionName);
    }

    private void addSessionName(String sessionName)
    {
        sessionNames.add(sessionName);
    }

    public synchronized void endAllSessions()
    {
        List list = CacheFactory.getSessionNames();
        Map  listeners = getListeners();

        Iterator it = list.iterator();
        while (it.hasNext())
        {
            String sessionName = (String) it.next();

            ProductTypeStruct[] productTypeStructs = (ProductTypeStruct[]) CacheFactory.getSessionProductTypeCache(sessionName).findAll(TradingSessionCacheKeyFactory.getPrimaryTypeKey());

            if(productTypeStructs.length != 0 || productTypeStructs != null)
            {
                for ( int i = 0; i < productTypeStructs.length; i++ )
                {
                    SessionClassStruct[] classes = (SessionClassStruct[]) CacheFactory.getSessionClassCache(sessionName).findAllInGroup(TradingSessionCacheKeyFactory.getClassGroupByTypeKey(), Integer.valueOf(productTypeStructs[i].type));

                    if(classes.length != 0 || classes != null)
                    {
                        try
                        {
                            for ( int j=0 ; j<classes.length; j++)
                            {
                                SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classes[j].classStruct.classKey);
                                CacheClassTrackFactory.remove(sessionKey);
                            }
                        }
                        catch(Exception e)
                        {
                            Log.exception("TradingSessionServiceAdapterImpl.endAllSessions", e);
                        }
                    }
                }
            }

            // Purge the caches for this session
            CacheFactory.getSessionClassCache(sessionName).purgeCache();
            CacheFactory.getSessionProductCache(sessionName).purgeCache();
            CacheFactory.getSessionProductTypeCache(sessionName).purgeCache();
            CacheFactory.getSessionStrategyCache(sessionName).purgeCache();

            // Remove the listener for this session
            TradingSessionEventListener listener = (TradingSessionEventListener) listeners.get(sessionName);
            if(listener != null)
            {
                listener.shutdown();
                listeners.remove(sessionName);
            }
        }
    }

    // Create and initialize caches for all the trading sessions.
    public synchronized void startAllSessions(BusinessDayStruct day)
    {
        Map listeners = getListeners();

        // Create or purge, if needed
        if(listeners == null)
            listeners = new HashMap();
        if(listeners.size() != 0)
            endAllSessions();

        for (int i = 0; i < day.activeSessions.length; i++)
            initializeTradingSession(day.activeSessions[i]);
    }

    // Creates and initializes all the caches for the given session, as well as adding an event channel listener
    // for messages that will cause updates to the contents of those caches.
    private void initializeTradingSession(BusinessDaySessionStruct session)
    {
        try
        {
            String sessionName = session.session.sessionName;
            addSessionName(sessionName);

            // Populate the session caches with data from the business day structure
            CacheFactory.getSessionProductTypeCache(sessionName).loadCache(session.sessionProductTypes);
            CacheFactory.loadSessionClassCache(sessionName, session.sessionClasses);

            // Add a new listener for messages on the IEC
            getListeners().put(sessionName, new TradingSessionEventListener(sessionName));
        }
        catch (Exception e)
        {
            Log.exception("TradingSessionServiceAdapterImpl.initializeAllSessions", e);
        }
    }

    public void startDefaultTradingSessionEventFilters() throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        // This connects the IEC to the CBOE event channels that we're not filtering per-session/class
        SessionKeyContainer getAllEvents = new SessionKeyContainer("", 0);
        modifyFilter(new ChannelKey(ChannelType.BUSINESS_DAY, getAllEvents), true);
        if (!engineIsStock)
        {
            modifyFilter(new ChannelKey(ChannelType.STRATEGY_UPDATE, getAllEvents), true);
        }
        modifyFilter(new ChannelKey(ChannelType.TRADING_SESSION, getAllEvents), true);
        modifyFilter(new ChannelKey(ChannelType.SET_CLASS_STATE, getAllEvents), true);
        modifyFilter(new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, getAllEvents), true);
    }

    /**
     * Returns true if product state change should be republished.
     *
     * @param sessionName
     * @param classKey
     * @return
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @throws NotFoundException
     */
    public boolean refreshCachedSessionProductsForClassKey(String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        CacheClassTrackImpl classTrack = CacheClassTrackFactory.find(sessionKey);

        if(classTrack !=  null)
        {
            boolean republishState = false;
            synchronized(classTrack)
            {
                if(classTrack.wasSessionProductsLoaded())
                {
                    if(Log.isDebugOn())
                    {
                        Log.debug(this, "Products in session/class " + sessionName + "/" + classKey + " are being refreshed.");
                    }

                    refreshSessionProductCacheForClassKey(sessionName, classKey);
                    republishState = true; // can't just return; this could be a strategy...in which case there's more to do...
                }
                else
                {
                    if(Log.isDebugOn())
                    {
                        Log.debug(this, "Products in session/class " + sessionName + "/" + classKey + " NOT refreshed.");
                    }
                }

                if(classTrack.wasSessionStrategiesLoaded())
                {
                    // this is a strategy class key
                    if(Log.isDebugOn())
                    {
                        Log.debug(this, "Strategies in session/class " + sessionName + "/" + classKey + " are being refreshed.");
                    }

                    refreshSessionStrategyCacheForClassKey(sessionName, classKey);
                    // republishState set above
                }
                else
                {
                    if(Log.isDebugOn())
                    {
                        Log.debug(this, "Strategies in session/class " + sessionName + "/" + classKey + " NOT refreshed.");
                    }
                }
            }
            return republishState;
        }
        else
        {
            if(Log.isDebugOn())
            {
                Log.debug(this, "No products or strategies found for refresh for session/class " + sessionName + "/" + classKey);
            }
            return false;
        }
    }

    /**
     * This method reloads the session product cache by class/session.
     * It does *not* load it if it wasn't already loaded.
     *
     * @param sessionName
     * @param classKey
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    private void refreshSessionProductCacheForClassKey(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean reload = unloadSessionProductCache(sessionName, classKey);
        if(reload)
        {
            checkSessionProductCacheLoaded(sessionName, classKey);
        }
    }

    /**
     * This method reloads the session strategy cache by class/session.
     * It does *not* load it if it wasn't already loaded.
     *
     * @param sessionName
     * @param classKey
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    private void refreshSessionStrategyCacheForClassKey(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean reload = unloadSessionStrategyCache(sessionName, classKey);
        if(reload)
        {
            checkSessionStrategyCacheLoaded(sessionName, classKey);
        }
    }

    private boolean unloadSessionProductCache(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        CacheClassTrackImpl classTrack = CacheClassTrackFactory.find(sessionKey);

        boolean previouslyLoaded = false;
        synchronized(classTrack)
        {
            previouslyLoaded = classTrack.wasSessionProductsLoaded();
            if(previouslyLoaded)
            {
                unloadProductsForClass(sessionName, classKey);

                classTrack.setSessionProductsLoaded(false);
            }
        }

        return previouslyLoaded;
    }

    private boolean unloadSessionStrategyCache(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
        CacheClassTrackImpl classTrack = CacheClassTrackFactory.find(sessionKey);

        boolean previouslyLoaded = false;
        synchronized(classTrack)
        {
            previouslyLoaded = classTrack.wasSessionStrategiesLoaded();
            if(previouslyLoaded)
            {
                unloadStrategiesForClass(sessionName, classKey);
                classTrack.setSessionStrategiesLoaded(false);
            }
        }
        return previouslyLoaded;
    }

    private void unloadProductsForClass(String sessionName, int classKey) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Unloading session product cache where session="+sessionName+" and classKey="+classKey);
        }

        productFilterForClass(sessionName, classKey, false);
        CacheFactory.getSessionProductCache(sessionName).removeAllInGroup(TradingSessionCacheKeyFactory.getProductGroupByClassKey(), Integer.valueOf(classKey));
    }

    private void unloadStrategiesForClass(String sessionName, int classKey) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Unloading session strategy cache where session="+sessionName+" and strategyClassKey="+classKey);
        }

        strategyFilterForClass(sessionName, classKey, false);
        CacheFactory.getSessionStrategyCache(sessionName).removeAllInGroup(TradingSessionCacheKeyFactory.getStrategyGroupByClassKey(), Integer.valueOf(classKey));
    }


//TSRefactor: Apparently unused
//    public void unsubscribeClassStatusByKey(String sessionName, int classKey, CMIClassStatusConsumer clientListener)
//        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
//    {
//        Log.debug(this, "calling unsubscribeClassStatusByKey for " + currentSession);
//        if (clientListener != null)
//        {
//            ///////// add the call back consumer to the supplier list/////
//            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
//            UserSessionKey theKey = new UserSessionKey(currentSession, key);
//            ChannelKey channelKey;
//
//            ChannelListener proxy = ServicesHelper.getClassStatusConsumerProxy(clientListener, currentSession);
//
//            channelKey = new ChannelKey(ChannelType.CB_CLASS_STATE, theKey);
//            classStatusSupplier.removeChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.CB_PRODUCT_CLASS_UPDATE, theKey);
//            productStatusSupplier.removeChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, key);
//            productStatusCollectorSupplier.removeChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, key);
//            productStatusCollectorSupplier.removeChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
//            EventChannelAdapterFactory.find().removeChannelListener(this, acceptTextMessageProcessor, channelKey);
//        }
//    }

//TSRefactor: Apparently unused
//    /**
//     * Unsubscribe Product Status for the given product key.
//     *
//     * @param productKey the product key to unsubscribe for.
//     * @param clientListener the client listener to unsubscribe for.
//     *
//     * @return none
//     * @exception SystemException System Error
//     * @exception CommunicationException Communication Error
//     * @exception AuthorizationException Authorization Error
//     * @exception DataValidationException Data Validation Error
//     *
//     * @author Connie Feng
//     */
//    public void unsubscribeProductStatusByKey(String sessionName, int productKey, CMIProductStatusConsumer clientListener)
//        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
//    {
//        Log.debug(this, "calling unsubscribeProductStatusByKey for " + currentSession);
//        if (clientListener != null)
//        {
//            ///////// add the call back consumer to the supplier list/////
//            SessionKeyContainer key = new SessionKeyContainer(sessionName, productKey);
//            UserSessionKey theKey = new UserSessionKey(currentSession, key);
//            ChannelListener     proxy  = ServicesHelper.getProductStatusConsumerProxy(clientListener, currentSession);
//
//            ChannelKey channelKey = new ChannelKey(ChannelType.CB_PRODUCT_STATE, theKey);
//            productStatusSupplier.removeChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE, theKey);
//            productStatusSupplier.removeChannelListener(this, proxy, channelKey);
//
//            // Register for Product change with the IEC
//            channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, key);
//            productStatusCollectorSupplier.removeChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT, key);
//            productStatusCollectorSupplier.removeChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            ProductStruct product;
//            try {
//                product = currentSession.getProductQuery().getProductByKey(productKey);
//            } catch (Exception e) {
//                product = null;
//            }
//
//            if (product != null)
//            {
//                channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(product.productKeys.classKey));
//                EventChannelAdapterFactory.find().removeChannelListener(this, acceptTextMessageProcessor, channelKey);
//            }
//            else
//            {
//                Log.alarm(this, "session: " + currentSession + " : Cache miss on product " + productKey);
//            }
//        }
//    }

//TSRefactor
//    /**
//     * Unsubscribes the Strategy Status Listener for a given class.
//     *
//     * @param class Key the class key unsubscribe for.
//     * @param clientListener the callback listener to unsubscribe for.
//     *
//     * @return StrategyStruct Strategy struct information.
//     * @exception SystemException System Error
//     * @exception CommunicationException Communication Error
//     * @exception AuthorizationException Authorization Error
//     * @exception DataValidationException Data Validation Error
//     *
//     * @author Connie Feng
//     */
//    public void unsubscribeStrategiesByClassForSession(String sessionName, int classKey, CMIStrategyStatusConsumer clientListener)
//        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
//    {
//        Log.debug(this, "calling unsubscribeStrategiesByClassForSession for " + currentSession);
//        if (clientListener != null)
//        {
//            ///////// add the call back consumer to the supplier list/////
//            SessionKeyContainer theKey = new SessionKeyContainer(sessionName, classKey);
//            UserSessionKey key = new UserSessionKey(currentSession, theKey);
//            ChannelKey channelKey;
//
//            ChannelListener proxy = ServicesHelper.getStrategyStatusConsumerProxy(clientListener, currentSession);
//
//            channelKey = new ChannelKey(ChannelType.CB_STRATEGY_UPDATE, key);
//            strategyStatusSupplier.removeChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, theKey);
//            productStatusCollectorSupplier.removeChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
//            EventChannelAdapterFactory.find().removeChannelListener(this, acceptTextMessageProcessor, channelKey);
//        }
//    }

//TSRefactor: Apparently unused
//    /**
//     * Add a listener to the Suplier.
//     * @author Connie Feng
//     * @param cosConsumer CMIProductConsumer
//     */
//    protected void subscribeProductStatusConsumerForProduct(String sessionName, int productKey, CMIProductStatusConsumer clientListener)
//            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
//    {
//        if (clientListener != null)
//        {
//            ///////// add the call back consumer to the supplier list/////
//            SessionKeyContainer key = new SessionKeyContainer(sessionName, productKey);
//            UserSessionKey theKey = new UserSessionKey(currentSession, key);
//            ChannelListener proxy = ServicesHelper.getProductStatusConsumerProxy(clientListener, currentSession);
//
//            ChannelKey channelKey = new ChannelKey(ChannelType.CB_PRODUCT_STATE, theKey);
//            productStatusSupplier.addChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE, theKey);
//            productStatusSupplier.addChannelListener(this, proxy, channelKey);
//
//            // Register for Product change with the IEC
//            channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, key);
//            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT, key);
//            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            ProductStruct product;
//            try {
//                product = currentSession.getProductQuery().getProductByKey(productKey);
//            } catch (Exception e) {
//                product = null;
//            }
//
//            if (product != null)
//            {
//                int classKey = product.productKeys.classKey;
//                channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
//                EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
//                publishMessagesForProductClass(classKey, currentSession.getValidSessionProfileUser().userId);
//            }
//            else
//            {
//                Log.alarm(this, "session: " + currentSession + " : Cache miss on product " + productKey);
//            }
//        }
//    }

//TSRefactor: Apparently unused
//    /**
//     * Add a listener to the Suplier.
//     * @author Connie Feng
//     * @param cosConsumer CMIProductConsumer
//     */
//    protected void subscribeProductStatusConsumerForClass(String sessionName, int classKey, CMIProductStatusConsumer  clientListener)
//            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
//    {
//        if (clientListener != null)
//        {
//            ///////// add the call back consumer to the supplier list/////
//            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
//            UserSessionKey theKey = new UserSessionKey(currentSession, key);
//
//            ChannelListener proxy = ServicesHelper.getProductStatusConsumerProxy(clientListener, currentSession);
//
//            ChannelKey channelKey = new ChannelKey(ChannelType.CB_PRODUCT_STATE_BY_CLASS, theKey);
//            productStatusSupplier.addChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, theKey);
//            productStatusSupplier.addChannelListener(this, proxy, channelKey);
//
//            // Register for Product change with the IEC
//            channelKey = new ChannelKey(ChannelType.SET_PRODUCT_STATE, key);
//            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_BY_CLASS, key);
//            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
//            EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
//            publishMessagesForProductClass( classKey, currentSession.getValidSessionProfileUser().userId );
//        }
//    }// end of subscribeConsumerForClass

//TSRefactor: Apparently unused
//    /**
//     * Add a listener to the Suplier.
//     * @author Connie Feng
//     * @param cosConsumer CMIProductConsumer
//     */
//    protected void subscribeClassStatusConsumerForType(String sessionName, short productType, CMIClassStatusConsumer  clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
//    {
//        if (clientListener != null)
//        {
//            ///////// add the call back consumer to the supplier list/////
//            SessionKeyContainer key = new SessionKeyContainer(sessionName, productType);
//            UserSessionKey theKey = new UserSessionKey(currentSession, key);
//            ChannelKey channelKey;
//            ChannelListener proxy = ServicesHelper.getClassStatusConsumerProxy(clientListener, currentSession);
//
//            channelKey = new ChannelKey(ChannelType.CB_CLASS_STATE_BY_TYPE, theKey);
//            classStatusSupplier.addChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.CB_CLASS_UPDATE_BY_TYPE, theKey);
//            classStatusSupplier.addChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, key);
//            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, key);
//            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_TYPE, Integer.valueOf(productType));
//            EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
//            publishMessagesForProductType( productType, currentSession.getValidSessionProfileUser().userId );
//
//        }
//    }// end of subscribeClassStatusConsumerForClass

//TSRefactor: Apparently unused
//    /**
//     * Add a listener to the Suplier.
//     * @author Connie Feng
//     * @param cosConsumer CMIProductConsumer
//     */
//    protected void subscribeClassStatusConsumerForClass(String sessionName, int classKey, CMIClassStatusConsumer  clientListener)
//           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
//    {
//        if (clientListener != null)
//        {
//            ///////// add the call back consumer to the supplier list/////
//            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);
//            UserSessionKey theKey = new UserSessionKey(currentSession, key);
//            ChannelKey channelKey;
//
//            ChannelListener proxy = ServicesHelper.getClassStatusConsumerProxy(clientListener, currentSession);
//
//            channelKey = new ChannelKey(ChannelType.CB_CLASS_STATE, theKey);
//            classStatusSupplier.addChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.CB_PRODUCT_CLASS_UPDATE, theKey);
//            classStatusSupplier.addChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.SET_CLASS_STATE, key);
//            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, key);
//            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
//            EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
//            publishMessagesForProductClass( classKey, currentSession.getValidSessionProfileUser().userId );
//        }
//    }// end of subscribeClassStatusConsumerForClass

//TSRefactor: Apparently unused
//    /**
//     * Add a listener to the Suplier.
//     * @author Connie Feng
//     * @param clientListener CMIStrategyStatusConsumer
//     */
//    protected void subscribeStrategyConsumerForClass(String sessionName, int classKey, CMIStrategyStatusConsumer  clientListener)
//            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
//    {
//        if (clientListener != null)
//        {
//            ///////// add the call back consumer to the supplier list/////
//            SessionKeyContainer theKey = new SessionKeyContainer(sessionName, classKey);
//            UserSessionKey key = new UserSessionKey(currentSession, theKey);
//            ChannelKey channelKey;
//
//            ChannelListener proxy = ServicesHelper.getStrategyStatusConsumerProxy(clientListener, currentSession);
//
//            channelKey = new ChannelKey(ChannelType.CB_STRATEGY_UPDATE, key);
//            strategyStatusSupplier.addChannelListener(this, proxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, theKey);
//            productStatusCollectorSupplier.addChannelListener(this, productStatusCollectorProxy, channelKey);
//
//            channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_CLASS, Integer.valueOf(classKey));
//            EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
//            publishMessagesForProductClass( classKey, currentSession.getValidSessionProfileUser().userId );
//        }
//    }


//TextMessagingService
//
    /**
     * Request any/all waiting messages be delivered via consumer
     */
    public void publishMessagesForProductClass(int classKey, String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ServicesHelper.getTextMessagingService().publishMessagesForProductClass(classKey,userId);
    }

    /**
     * Request any/all waiting messages be delivered via consumer
     */
    public void publishMessagesForProductType(short productType, String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ServicesHelper.getTextMessagingService().publishMessagesForProductType(productType,userId);
    }
}
