//
// -----------------------------------------------------------------------------------
// Source file: InstrumentationTranslatorImpl.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.bind.GIConfigurationResponseType;
import com.cboe.client.xml.bind.GIContextDetailResponseType;
import com.cboe.client.xml.bind.GIUserExceptionType;
import com.cboe.domain.util.CacheClassTrackFactory;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.dateTime.TimeImpl;
import com.cboe.exceptions.*;
import com.cboe.idl.alarm.*;
import com.cboe.idl.clusterInfo.LogicalOrbNameStruct;
import com.cboe.idl.cmi.ProductQueryOperations;
import com.cboe.idl.cmi.TradingSessionOperations;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.idl.alarmConstants.NotificationWatchdogLimitTypes;
import com.cboe.idl.alarmConstants.NotificationWatchdogStates;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.systemsManagementService.asynchronousClient.AdminServiceClientAsync;
import com.cboe.infrastructureServices.systemsManagementService.asynchronousClient.AsynchronousAdminClient;
import com.cboe.infrastructureServices.systemsManagementService.asynchronousClient.InitializationFailedException;
import com.cboe.interfaces.casMonitor.CASConfiguration;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.SystemMonitorCommandMethodNames;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.instrumentation.ContextDetail;
import com.cboe.interfaces.instrumentation.Status;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommand;
import com.cboe.interfaces.instrumentation.adminRequest.ExecuteCommandService;
import com.cboe.interfaces.instrumentation.adminRequest.ExecutionResult;
import com.cboe.interfaces.instrumentation.alarms.*;
import com.cboe.interfaces.instrumentation.api.AlarmsAPI;
import com.cboe.interfaces.instrumentation.api.InstrumentationMonitorAPI;
import com.cboe.interfaces.instrumentation.collector.InstrumentorCollector;
import com.cboe.interfaces.instrumentation.orbNameAlias.OrbNameAliasConsumerHome;
import com.cboe.interfaces.instrumentation.orbNameAlias.OrbNameAliasPublisherHome;
import com.cboe.interfaces.instrumentationCollector.CentralLoggingConsumerHome;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.presentation.common.storage.StorageManager;
import com.cboe.interfaces.presentation.processes.*;
import com.cboe.interfaces.presentation.product.*;
import com.cboe.interfaces.presentation.threading.GUIWorker;
import com.cboe.interfaces.presentation.tradingSession.DefaultTradingSession;
import com.cboe.presentation.adminRequest.ARCommandServiceFactory;
import com.cboe.presentation.adminRequest.CommandFactory;
import com.cboe.presentation.alarms.*;
import com.cboe.presentation.alarms.events.AlarmConsumersHomeImpl;
import com.cboe.presentation.alarms.events.AlarmPublishersHomeImpl;
import com.cboe.presentation.alarms.events.CentralLoggingConsumerHomeImpl;
import com.cboe.presentation.casMonitor.CASConfigurationFactory;
import com.cboe.presentation.collector.InstrumentorCollectorHome;
import com.cboe.presentation.collector.InstrumentorCollectorImpl;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.formatters.InstrumentorTypes;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.properties.InstrumentationProperties;
import com.cboe.presentation.common.storage.OrbNameAliasSynchEventChannelDelegate;
import com.cboe.presentation.common.storage.StorageManagerFactory;
import com.cboe.presentation.environment.EnvironmentManagerFactory;
import com.cboe.presentation.groups.GroupElementCacheGuiImpl;
import com.cboe.presentation.groups.GroupElementModelImpl;
import com.cboe.presentation.groups.ICSGroupServiceAPI;
import com.cboe.presentation.groups.GroupElementModel;
import com.cboe.presentation.groups.GroupElementListener;
import com.cboe.presentation.groups.events.ICSGroupElementConsumerHomeImpl;
import com.cboe.presentation.groups.events.ICSGroupElementPublisherHomeImpl;
import com.cboe.presentation.groups.events.ICSGroupElementConsumerHome;
import com.cboe.presentation.groups.events.ICSGroupElementPublisherHome;
import com.cboe.presentation.instrumentation.ContextDetailFactory;
import com.cboe.presentation.instrumentation.ProcessCache;
import com.cboe.presentation.orbNameAlias.LogicalNameCache;
import com.cboe.presentation.orbNameAlias.LogicalNameFactory;
import com.cboe.presentation.orbNameAlias.OrbNameAliasCache;
import com.cboe.presentation.orbNameAlias.events.OrbNameAliasEventConsumerHomeImpl;
import com.cboe.presentation.orbNameAlias.events.OrbNameAliasEventPublisherHomeImpl;
import com.cboe.presentation.processWatcher.ProcessWatcherManager;
import com.cboe.presentation.processWatcher.ProcessWatcherManagerHome;
import com.cboe.presentation.processWatcher.ProcessWatcherStatusListener;
import com.cboe.presentation.product.*;
import com.cboe.presentation.statusMonitor.InstrumentorMonitor;
import com.cboe.presentation.threading.APIWorkerImpl;
import com.cboe.presentation.threading.NonGUIWorkerImpl;
import com.cboe.presentation.threading.GUIWorkerImpl;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;
import com.cboe.instrumentationCollector.common.watchdog.WithinTimeDataParser;
import org.omg.CORBA.UserException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class InstrumentationTranslatorImpl implements InstrumentationMonitorAPI, ProcessWatcherListenerManager,OrbProcessCache, ICSGroupServiceAPI
{
    public static final String SYSTEM_HEALTH_PROPERTY_SECTION = "SystemHealthMonitor";
    public static final String PROCESS_HASH_INITIAL_SIZE_PROPERTY_KEY = "ProcessHashInitialSize";
    public static final String PREFERRED_SACAS_PROPERTY_KEY = "PreferredSACAS";
    public static final String ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY = "AdminServiceTimeout";
    public static final String ADMIN_SERVICE_MAX_TIMEOUT_PROPERTY_KEY = "AdminServiceMaxTimeout";
    public static final String MAX_SUBSCRIPTIONS_KEY = "MaxSubscriptions";
    public static final String ORBNAME_PROPERTY = "ORB.OrbName";

    public static final String ALARM_DEFINITION_CHANNEL_NAME_PROPERTY = "AlarmDefinition.ChannelName";
    public static final String ALARM_NOTIFICATION_CHANNEL_NAME_PROPERTY = "AlarmNotification.ChannelName";
    public static final String ORB_NAME_ALIAS_CHANNEL_NAME_PROPERTY = "OrbNameAlias.ChannelName";

    //Added on 12/13/2004 - NAS : For XTP Alert Notification channel
    public static final String CENTRAL_LOGGING_SERVICE_CHANNEL_NAME_PROPERTY = "CentralLoggingService.ChannelName";

    public static final String GROUP_ELEMENT_CHANNEL_NAME_PROPERTY = "GroupElement.ChannelName";

    public static final int DEFAULT_PROCESS_CACHE_INITIAL_SIZE = 500;
    public static final int DEFAULT_MAX_SUBSCRIPTIONS = 100;

    private static final int ADMIN_SERVICE_DEFAULT_TIMEOUT = 5000;
    private static final int ADMIN_SERVICE_DEFAULT_MAX_TIMEOUT = 5000;
    private static final String CATEGORY = InstrumentationTranslatorImpl.class.getName();

    private AdminServiceClientAsync adminServiceClientAsync;

    private int heapCollectorSubscriptionCount;

    private int summaryInstrumentorSubscriptionCount;
    private static final Object SUMMARY_LOCK_OBJECT = new Object();

    private int collectorSubscriptionCount;
    private static final Object COLLECTOR_LOCK_OBJECT = new Object();
    private EventChannelAdapter eventChannel;

    protected ProductQueryOperations productQuery;
    protected TradingSessionOperations tradingSession;

    protected ProductSessionProcessor productProcessor = new ProductSessionProcessor();
    protected TradingSessionCache tradingSessionCache;
    private String processForProductQuery;

    private static final Object PROCESS_LOCK_OBJECT = new Object();
    private ProcessCache processCache;
    private Map<String, ProcessInfoModel> processInfoCache;

    private int maxSubscriptions;

    private ProcessWatcherStatusListener pwListener;

    private Map<String, Integer> orbListenerMap = new HashMap<String, Integer>();
    private Map<String, Integer> allOrbListenerMap = new HashMap<String, Integer>();
    private boolean adminServiceInitialized;
    private boolean productQueryServiceInitialized;

    protected StorageManager storageManager;
    protected OrbNameAliasCache orbNameAliasCache;
    protected LogicalNameCache logicalNameCache;

    protected InstrumentorMonitor instrumentorMonitor;

    protected String myOrbName;

    protected AlarmsCache alarmsCache;
    protected AlarmNotificationCache alarmNotificationCache;

    private AlarmConsumersHome alarmConsumer;
    private AlarmPublishersHome alarmPublisher;
    private OrbNameAliasConsumerHome orbNameAliasConsumer;
    private OrbNameAliasPublisherHome orbNameAliasPublisher;
    private OrbNameAliasEventPublisherHomeImpl logicalOrbNamePublisherHome;
    private OrbNameAliasSynchEventChannelDelegate logicalOrbNameDelegate;
    private OrbNameAliasEventConsumerHomeImpl logicalOrbNameEventConsumerHome;


    private CentralLoggingConsumerHome centralLoggingConsumer;

    private SubscriptionRecoveryListener subscriptionRecoveryListener;
    private static final AlarmCalculation[] ZERO_LENGTH_ALARM_CALCULATION = new AlarmCalculation[0];

    /**

     * group service, group element cache and consumer
     */

    protected GroupElementCacheGuiImpl groupElementCache;
    protected ICSGroupElementConsumerHome groupElementConsumer;
    protected ICSGroupElementPublisherHome groupElementPublisher;


    public InstrumentationTranslatorImpl() throws Exception
    {
        initialize();
    }

    private void initialize() throws Exception
    {
        initializeEventChannel();
        initializeConsumers();
        initializePublishers();
        initializeCaches();
    }

    private void initializePublishers() throws Exception
    {
        alarmPublisher = new AlarmPublishersHomeImpl();
        alarmPublisher.initializePublishers(getAlarmsDefinitionChannelName());
        initializeLogicalOrbNamePublishing();

        groupElementPublisher = new ICSGroupElementPublisherHomeImpl();
        groupElementPublisher.initializePublisher(getICSGroupElementChannelName());
    }

    private void initializeConsumers() throws Exception
    {
        alarmConsumer = new AlarmConsumersHomeImpl();
        alarmConsumer.initializeDefinitionConsumer(getAlarmsDefinitionChannelName());

        //Initialize Central Logging Service for XTP Alert Notification
        centralLoggingConsumer =  new CentralLoggingConsumerHomeImpl();
        centralLoggingConsumer.initializeCentralLoggingServiceConsumer( getCentralLoggingServiceChannelName() );

        groupElementConsumer = new ICSGroupElementConsumerHomeImpl();
        groupElementConsumer.initializeConsumer(getICSGroupElementChannelName());
    }

    private void initializeAlarmNotificationConsumer() throws Exception
    {
        alarmConsumer.initializeNotificationConsumer(getAlarmsNotificationChannelName(),
                                                     InstrumentationProperties.isSubscribeForNotifications());
    }

    private void initializeCaches() throws Exception
    {
        heapCollectorSubscriptionCount = 0;
        collectorSubscriptionCount = 0;
        int initialCacheSize = DEFAULT_PROCESS_CACHE_INITIAL_SIZE;
        if( AppPropertiesFileFactory.isAppPropertiesAvailable() )
        {
            String value =
                    AppPropertiesFileFactory.find().getValue(SYSTEM_HEALTH_PROPERTY_SECTION,
                                                             PROCESS_HASH_INITIAL_SIZE_PROPERTY_KEY);
            if( value != null && value.length() > 0 )
            {
                try
                {
                    initialCacheSize = Integer.parseInt(value);
                }
                catch( NumberFormatException e )
                {
                    GUILoggerHome.find().exception(CATEGORY, "Could not parse initial hash size property. " +
                                                             PROCESS_HASH_INITIAL_SIZE_PROPERTY_KEY + "=" + value +
                                                             ". Will use default value.", e);
                }
            }
        }

        processInfoCache = new HashMap<String, ProcessInfoModel>(initialCacheSize);
        processCache = new ProcessCache(initialCacheSize);

        maxSubscriptions = DEFAULT_MAX_SUBSCRIPTIONS;
        if( AppPropertiesFileFactory.isAppPropertiesAvailable() )
        {
            String value =
                    AppPropertiesFileFactory.find().getValue(SYSTEM_HEALTH_PROPERTY_SECTION,
                            MAX_SUBSCRIPTIONS_KEY);
            if( value != null && value.length() > 0 )
            {
                try
                {
                    maxSubscriptions = Integer.parseInt(value);
                }
                catch( NumberFormatException e )
                {
                    GUILoggerHome.find().exception(CATEGORY, "Could not parse max subscriptions property. " +
                            MAX_SUBSCRIPTIONS_KEY +
                            ". Will use default value.", e);
                }
            }
        }

        try
        {
            initializeGroupCache();
        }
        catch(CacheInitializationException e)
        {
            GUILoggerHome.find().alarm(e.getMessage());
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    void initializeAlarmsCache() throws Exception
    {
        alarmsCache = new AlarmsCache(eventChannel, getAdminServiceDefaultTimeout(), alarmPublisher);
        alarmsCache.initialize();
        alarmNotificationCache = new AlarmNotificationCache();
        initializeAlarmNotificationConsumer();
    }

    public boolean isAlarmsCacheInitialized()
    {
       return this.alarmsCache.getAllAlarmConditions().length>0;
    }

    private void initializeEventChannel() throws Exception
    {
        try
        {
            eventChannel = EventChannelAdapterFactory.find();
            eventChannel.setDynamicChannels(true);
        }
        catch( Exception e )
        {
            GUILoggerHome.find().exception(CATEGORY + ": InstrumentationTranslatorImpl.initializeEventChannel", e);
            throw e;
        }
    }

    private void initializeProcessWatcher() throws Exception
    {
        pwListener = new ProcessWatcherListener(PROCESS_LOCK_OBJECT, processInfoCache, this);
        try
        {
            final ProcessWatcherManager pwMan = ProcessWatcherManagerHome.find();
            synchronized( PROCESS_LOCK_OBJECT )
            {
                loadProcessCaches(pwMan.getWatchedProcessList());
            }
            GUIWorker worker = new NonGUIWorkerImpl()
            {
                public void execute() throws Exception
                {
                    pwMan.addProcessWatcherStatusListener(pwListener);
                    pwMan.registerWithProcessWatcher();
                }
                public void handleException(Exception e)
                {
                    GUILoggerHome.find().exception(CATEGORY + ": InstrumentationTranslatorImpl.initializeProcessWatcher:registerWithProcessWatcher", e);
                }
            };
            APIWorkerImpl.run(worker);

            processForProductQuery = findFirstAvailableSACAS();
        }
        catch( Exception e )
        {
            GUILoggerHome.find().exception(CATEGORY + ": InstrumentationTranslatorImpl.initializeProcessWatcher", e);
            throw e;
        }

    }

    /**
     * Initializes all services. The order of initialization is IMPORTANT!!!!!
     * @throws Exception
     */
    void initializeServices() throws Exception
    {
        initializeXmlBinding();
        initializeStorageManager();
        initializeProcessWatcher();
        initializeAdminService();
        initializeOrbNameAlias();
    }

    void initializeStorageManager()
    {
        storageManager = StorageManagerFactory.getStorageManager();
    }
    void initializeOrbNameAlias()
    {
        orbNameAliasCache = new OrbNameAliasCache(storageManager.getOrbNameAliasStorage(),
                                                  storageManager.getLocalStorage());

        orbNameAliasCache.initializeCache();
        logicalNameCache = LogicalNameCache.getInstance();
    }

    public void initializeProcessCache()
    {
        processCache.initializeCache(this);
    }

    void initializeProductQueryService() throws Exception
    {
        try
        {
//            setSACASForProductQuery(getPreferredSACASForProductQuery());
            this.processForProductQuery = getPreferredSACASForProductQuery();
            //Instrumentation Translator implementation of the ProductQuery interface is dependant on the
            //AdminService to be initialized.  If we fail to initialize AdminService we can not use ProductQuery.
            if(isAdminServiceInitialized() && getSACASForProductQuery()!=null )
            {
                this.productQuery = new AdminServiceProductQueryImpl(getAdminService());
                this.tradingSession = new AdminServiceTradingSessionImpl(getAdminService());
                initializeSessionlessProductCache();
                initializeSessionProductCache();
                productQueryServiceInitialized = true;
            }
        }
        catch(Exception e)
        {
            productQueryServiceInitialized = false;
            GUILoggerHome.find().exception(CATEGORY + ".initializeProductQueryService()", "Failed to initialize ProductQueryService", e);
            GUILoggerHome.find().alarm(CATEGORY + ".initializeProductQueryService()", "Failed to initialize ProductQueryService");
        }
    }

    protected void initializeSessionlessProductCache() throws Exception
    {
        try
        {
            ProductTypeStruct[] productTypes = getAllProductTypes(true);
            GUILoggerHome.find().debug(CATEGORY, GUILoggerINBusinessProperty.PRODUCT_QUERY, productTypes);
            ProductClass[] classes = null;
            for (int i = 0; i < productTypes.length; i++)
            {
                classes = getAllClassesForType(productTypes[i].type, true);
                GUILoggerHome.find().debug(CATEGORY, GUILoggerINBusinessProperty.PRODUCT_QUERY, classes);
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(CATEGORY + ": .initializeSessionlessProductCache()", "", e);
            throw e;
        }
    }

    protected void initializeSessionProductCache() throws Exception
    {
        try
        {
            tradingSessionCache = new TradingSessionCache();
            TradingSessionStruct[] sessions = getCurrentTradingSessions(tradingSessionCache,true);
            tradingSessionCache.addTradingSessions(sessions);
            GUILoggerHome.find().debug(CATEGORY, GUILoggerINBusinessProperty.PRODUCT_QUERY, sessions);
            for (int i = 0; i < sessions.length; i++)
            {
                initializeProductCache(sessions[i].sessionName);
// Do not subscribe for Product Events
//                productProcessor.subscribeForSessionProductEvents(sessions[i].sessionName);
// Do not subscribe for BookDepth
//                BookDepthProcessor bookDepthProcessor = new BookDepthProcessor(sessions[i].sessionName);
//                bookDepthProcessor.subscribeForBookDepthEvents(sessions[i].sessionName);
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(CATEGORY + ": .initializeSessionProductCache()", "", e);
            throw e;
        }
    }

    protected void initializeProductCache(String sessionName) throws Exception
    {
        try
        {
            ProductType[] productTypes = getProductTypesForSession(sessionName, true);
            SessionProductClass[] classStructs;
            for (int i = 0; i < productTypes.length; i++)
            {
                loadProductClassesForSession(sessionName, productTypes[i].getType());
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(CATEGORY + ": .initializeProductCache()", "", e);
            throw e;
        }
    }

    protected void initializeInstrumentorMonitor()
    {
        instrumentorMonitor = new InstrumentorMonitor();
    }

    public void initializeSubscriptionRecoveryListener()
    {
        subscriptionRecoveryListener = new SubscriptionRecoveryListener();
    }

    public AlarmConsumersHome getAlarmConsumersHome()
    {
        return alarmConsumer;
    }

    public OrbNameAliasConsumerHome getOrbNameAliasConsumer()
    {
    	return orbNameAliasConsumer;
    }

    public OrbNameAliasPublisherHome getOrbNameAliasPublisher()
    {
    	return orbNameAliasPublisher;
    }

    private SessionProductClass[] loadProductClassesForSession(String sessionName, short productType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        synchronized (SessionProductCacheFactory.find(sessionName))
        {
            SessionClassStruct[] classStructs = tradingSession.getClassesForSession(sessionName, productType, null/*classStatusConsumer*/);
            productProcessor.addClasses(sessionName, classStructs);
            return SessionProductCacheFactory.find(sessionName).getAllClassesForSession();
        }
    }

    /**
     * retrieve all the product classes for a product type and cache them
     * @param productType short
     * @return ProductClass[]
     */
    private ProductClass[] loadProductClasses(short productType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        try{
            ClassStruct[] newClasses = productQuery.getProductClasses(productType);
            if (newClasses != null && newClasses.length > 0)
            {
                ProductClass[] productClasses = new ProductClass[newClasses.length];
                for (int i = 0; i < productClasses.length; i++)
                {
                    productClasses[i] = ProductClassFactoryHome.find().create(newClasses[i]);
                }

                ProductQueryCacheFactory.find().addClasses(productClasses, productType);
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(CATEGORY + ": .loadProductClass for type:"+productType, "", e);
        }
        return ProductQueryCacheFactory.find().getProductClasses(productType, false);
    }
    /**
     * retrieve all the product typs in the system and put them into the product cache
     * @return ProductTypeStruct[]
     */
    private ProductTypeStruct[] loadProductTypes()
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        ProductTypeStruct[] types = productQuery.getProductTypes();
        ProductQueryCacheFactory.find().addProductTypes(types);
        return ProductQueryCacheFactory.find().getProductTypes();
    }

    /**
     * retrieve all the product types in the system for a session and cache them
     * @param sessionName String
     * @return ProductTypeStruct[]
     */
    private ProductTypeStruct[] loadProductTypesForSession(String sessionName)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        ProductTypeStruct[] types = tradingSession.getProductTypesForSession(sessionName);
        productProcessor.addProductTypes(sessionName, types);
        return SessionProductCacheFactory.find(sessionName).getProductTypesForSession();
    }
    /**
     * load all the products in the class based on classKey
     * @param classKey int
     * @return Product[]
     */
    protected Product[] loadProductsForClass(int classKey)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {

        if (!CacheClassTrackFactory.find(new SessionKeyContainer("", classKey)).wasProductsLoaded())
        {
            ProductStruct[] productStructs = productQuery.getProductsByClass(classKey);
            int productLength = productStructs.length;
            Product[] newProducts = new Product[productLength];
            for (int i = 0; i < productLength; i++)
            {
                newProducts[i] = ProductFactoryHome.find().create(productStructs[i]);
            }
            ProductQueryCacheFactory.find().addProducts(newProducts, classKey);
            CacheClassTrackFactory.find(new SessionKeyContainer("", classKey)).setSessionLessProductsLoaded(true);
        }
        return ProductQueryCacheFactory.find().getProducts(classKey, false);
    }

    /**
     * load all the strategies in the class on classKey
     * @param classKey int
     * @return Strategy[]
     */
    protected Strategy[] loadStrategiesForClass(int classKey)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {

        if (!CacheClassTrackFactory.find(new SessionKeyContainer("", classKey)).wasStrategiesLoaded())
        {
            StrategyStruct[] strategyStructs = productQuery.getStrategiesByClass(classKey);
            int strategyLength = strategyStructs.length;
            Strategy[] newStrategies = new Strategy[strategyLength];
            for (int i = 0; i < strategyLength; i++)
            {
                newStrategies[i] = ProductFactoryHome.find().create(strategyStructs[i]);
            }
            ProductQueryCacheFactory.find().addStrategies(newStrategies, classKey);
            CacheClassTrackFactory.find(new SessionKeyContainer("", classKey)).setSessionLessStrategiesLoaded(true);
        }
        return ProductQueryCacheFactory.find().getStrategies(classKey, false);
    }
    /**
     * load all the session strategies in the class based on sessionName and class key
     * @param sessionName String
     * @param classKey int
     * @return SessionStrategy[]
     */
    private SessionStrategy[] loadStrategiesForClassBySession(String sessionName, int classKey)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {

        loadStrategiesForClass(classKey);
        SessionStrategyStruct[] sessionStrategyStructs = null;
        // subscribe for product status
        synchronized (SessionProductCacheFactory.find(sessionName))
        {
            if (!CacheClassTrackFactory.find(new SessionKeyContainer(sessionName, classKey)).wasSessionStrategiesLoaded())
            {
                tradingSession.getProductsForSession(sessionName, classKey, null/*productStatusConsumer*/);
                sessionStrategyStructs = tradingSession.getStrategiesByClassForSession(sessionName, classKey, null/*strategyStatusConsumer*/);
                productProcessor.addStrategies(sessionName, sessionStrategyStructs);
                CacheClassTrackFactory.find(new SessionKeyContainer(sessionName, classKey)).setSessionStrategiesLoaded(true);
            }
            return SessionProductCacheFactory.find(sessionName).getStrategiesForSession(classKey);
        }
    }
    /**
     * load all the session products in the class based on session name and class key
     * @param sessionName String
     * @param classKey int
     * @return SessionProduct[]
     */
    private SessionProduct[] loadProductsForClassBySession(String sessionName, int classKey)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {

        loadProductsForClass(classKey);
        SessionProductStruct[] sessionProductStructs = null;
        synchronized (SessionProductCacheFactory.find(sessionName))
        {
            if (!CacheClassTrackFactory.find(new SessionKeyContainer(sessionName, classKey)).wasSessionProductsLoaded())
            {
                sessionProductStructs = tradingSession.getProductsForSession(sessionName, classKey, null/*productStatusConsumer*/);
                productProcessor.addProducts(sessionName, sessionProductStructs);
                CacheClassTrackFactory.find(new SessionKeyContainer(sessionName, classKey)).setSessionProductsLoaded(true);
            }
            return SessionProductCacheFactory.find(sessionName).getProductsForSession(classKey);
        }
    }
    protected void initializeXmlBinding()
    {
        GUIWorker worker = new NonGUIWorkerImpl()
        {
            public void execute() throws Exception
            {
                // initializes Xml Binding sub-system.
                XmlBindingFacade.getInstance();
            }

            public void handleException(Exception e)
            {
                GUILoggerHome.find().exception(e);
            }
        };
        APIWorkerImpl.run(worker);
    }

    private void initializeAdminService()
    {
        adminServiceClientAsync = null;
        adminServiceInitialized = false;
        try
        {
            adminServiceClientAsync = AsynchronousAdminClient.getInstance();
            if ( adminServiceClientAsync != null )
            {
                adminServiceClientAsync.setMaxTimeout(getAdminServiceDefaultMaxTimeout());
                adminServiceClientAsync.setDefaultTimeout(getAdminServiceDefaultTimeout());
                adminServiceInitialized = true;
            }
            else
            {
                GUILoggerHome.find().alarm(CATEGORY + ": InstrumentationTranslatorImpl.initialize", "got NULL from AsynchronousAdminClient.getInstance()");
            }
        }
        catch (InitializationFailedException e)
        {
            GUILoggerHome.find().alarm(CATEGORY + ": InstrumentationTranslatorImpl.initialize", "Unable to initialize Admin Service");
            GUILoggerHome.find().exception("InstrumentationTranslatorImpl.initializeAdminService", "Unable to initialize Admin Service", e);
        }
    }

    public void cleanUp()
    {
        InstrumentorCollectorHome.find().unsubscribeAllInstrumentors();

        instrumentorMonitor.stop();

        adminServiceClientAsync = null;
        eventChannel = null;

        ProcessWatcherManager pwMan = ProcessWatcherManagerHome.find();
        pwMan.removeProcessWatcherStatusListener(pwListener);
        try
        {
            pwMan.unregisterWithProcessWatcher();
        }
        catch( Exception e )
        {
            GUILoggerHome.find().exception(CATEGORY + ": cleanup",
                                           "Could not unregister from ProcessWatcherManager.", e);
        }

        OrbNameAliasCache.getInstance().saveCache();

        pwListener = null;

        processCache = null;

        instrumentorMonitor = null;

        alarmsCache.cleanup();
    }

    public boolean isAdminServiceInitialized()
    {
        return this.adminServiceInitialized;
    }

    public boolean isProductQueryServiceInitialized()
    {
        return this.productQueryServiceInitialized;
    }

    public CBOEProcess[] getAllSACASes()
    {
        GUILoggerHome.find().debug(CATEGORY + ": getAllSACASes",
                                   GUILoggerINBusinessProperty.PROCESSES, (Object)null);

        CBOEProcess[] sacases = processCache.getAllSACASes();

        return sacases;
    }

    /**
     * Returns an array of SACAS names. This array of names is sorted in the ascending order.
     * @return String[] of SACAS names
     */
    public String[] getAllSACASNames()
    {
        GUILoggerHome.find().debug(CATEGORY + ": getAllSACASNames",
                                   GUILoggerINBusinessProperty.PROCESSES, ( Object ) null);
        String[] sacasNames = processCache.getAllSACASNames();

        return sacasNames;
    }

    public void setSACASForProductQuery(String processOrbName) throws DataValidationException
    {
        GUILoggerHome.find().debug(CATEGORY + ": setSACASForProductQuery",
                                   GUILoggerINBusinessProperty.PRODUCT_QUERY, ( Object ) processOrbName);

        if (isValidSACASForProductQuery(processOrbName))
        {
            processForProductQuery = processOrbName;
        }
        else
        {
            DataValidationException exception =
                    ExceptionBuilder.dataValidationException(processOrbName +
                                                             " is not a valid SACAS for Product Query resolution.",
                                                             DataValidationCodes.INVALID_PROCESS_NAME);
            throw exception;
        }
    }


    public String getPreferredSACASForProductQuery()
    {
        String preferredSACAS = EnvironmentManagerFactory.find().getCurrentEnvironment().getProperty(PREFERRED_SACAS_PROPERTY_KEY);
        if (preferredSACAS==null)
        {
            GUILoggerHome.find().alarm(CATEGORY, "Unable to retrieve Preferred SACAS name. Property " + PREFERRED_SACAS_PROPERTY_KEY + "is not specified.");
        }
        return preferredSACAS;
    }

    public boolean isValidSACASForProductQuery(String processOrbName)
    {
        return processCache.isValidSACASForProductQuery(processOrbName);
    }

    private int getAdminServiceDefaultTimeout()
    {
        int defaultTimeout = ADMIN_SERVICE_DEFAULT_TIMEOUT;
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String value = AppPropertiesFileFactory.find().getValue(SYSTEM_HEALTH_PROPERTY_SECTION,
                                                                    ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY);
            if (value != null && value.length() > 0)
            {
                try
                {
                    defaultTimeout = Integer.parseInt(value);
                }
                catch(NumberFormatException e)
                {
                    GUILoggerHome.find().exception(CATEGORY, "Could not parse Admin Service timeout property. " +
                                                             ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY + "=" + value +
                                                             ". Will use default value.", e);
                }
            }
            else
            {
                GUILoggerHome.find().alarm(CATEGORY, "Unable to retrieve Admin Service timeout property. Property " + ADMIN_SERVICE_TIMEOUT_PROPERTY_KEY + "is not specified.");
            }
        }
        else
        {
            GUILoggerHome.find().alarm(CATEGORY, "Unable to retrieve Admin Service timeout property. Application Properties Not Available.");
        }

        return defaultTimeout;
    }

    private int getAdminServiceDefaultMaxTimeout()
    {
        int maxTimeout = ADMIN_SERVICE_DEFAULT_MAX_TIMEOUT;
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String value = AppPropertiesFileFactory.find().getValue(SYSTEM_HEALTH_PROPERTY_SECTION,
                                                                    ADMIN_SERVICE_MAX_TIMEOUT_PROPERTY_KEY);
            if (value != null && value.length() > 0)
            {
                try
                {
                    maxTimeout = Integer.parseInt(value);
                }
                catch (NumberFormatException e)
                {
                    GUILoggerHome.find().exception(CATEGORY, "Could not parse Admin Service timeout property. " +
                                                             ADMIN_SERVICE_MAX_TIMEOUT_PROPERTY_KEY + "=" + value +
                                                             ". Will use default value.", e);
                }
            }
            else
            {
                GUILoggerHome.find().alarm(CATEGORY, "Unable to retrieve Admin Service timeout property. Property " + ADMIN_SERVICE_MAX_TIMEOUT_PROPERTY_KEY + "is not specified.");
            }
        }
        else
        {
            GUILoggerHome.find().alarm(CATEGORY, "Unable to retrieve Admin Service timeout property. Application Properties Not Available.");
        }

        return maxTimeout;
    }

    public String getSACASForProductQuery()
    {
        return processForProductQuery;
    }

    public CBOEProcess[] getAllICSes(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": getAllICSes",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        CBOEProcess[] icses = processCache.getAllICSes();

        if(listener != null)
        {
            subscribeICSProcesses(listener);
        }

        return icses;
    }

    public String[] getAllICSOrbNames()
    {
        String[] icses = processCache.getAllICSNames();
        return icses;
    }

    public String getIcsManagerOrbName()
    {
        return processCache.getIcsManagerOrbName();
    }

    public CBOEProcess getICS(String icsOrbName, EventChannelListener listener)
            throws SystemException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = icsOrbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": getICS",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        CBOEProcess ics = processCache.getICS(icsOrbName);

        if(ics == null)
        {
            DataValidationException exception =
                    ExceptionBuilder.dataValidationException(icsOrbName +
                                                             " is not a valid process ICS ORB name.",
                                                             DataValidationCodes.INVALID_PROCESS_NAME);
            throw exception;
        }

        if(listener != null)
        {
            subscribeICSProcesses(listener);
        }

        return ics;
    }

    public void subscribeICSProcesses(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": subscribeICSProcesses",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if(listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_ICS_UPDATE, new Integer(0));
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeICSProcesses(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": unsubscribeICSProcesses",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if(listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_ICS_UPDATE, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public CBOEProcess[] getAllCASes(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": getAllCASes",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        CBOEProcess[] cases = processCache.getAllCASes();

        if( listener != null )
        {
            subscribeAllCASes(listener);
        }

        return cases;
    }

    public String[] getAllCASNames()
    {
        GUILoggerHome.find().debug(CATEGORY + ": getAllCASNames",
                                   GUILoggerINBusinessProperty.PROCESSES, ( Object ) null);

        String[] casNames = processCache.getAllCASNames();

        return casNames;
    }

    public CBOEProcess getCAS(String casOrbName, EventChannelListener listener) throws SystemException, DataValidationException
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = casOrbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": getCAS",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        CBOEProcess cas = processCache.getCAS(casOrbName);

        if( cas == null )
        {
            DataValidationException exception =
                    ExceptionBuilder.dataValidationException(casOrbName +
                                                             " is not a valid process CAS ORB name.",
                                                             DataValidationCodes.INVALID_PROCESS_NAME);
            throw exception;
        }

        if( listener != null )
        {
            subscribeCAS(casOrbName, listener);
        }

        return cas;
    }

    public void subscribeAllCASes(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": subscribeAllCASes",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_CAS_UPDATE, new Integer(0));
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeAllCASes(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAllCASes",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_CAS_UPDATE, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeCAS(String casOrbName, EventChannelListener listener)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = casOrbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeCAS",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        if( casOrbName == null || casOrbName.length() == 0 )
        {
            throw new IllegalArgumentException("casName may not be null.");
        }
        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_CAS_UPDATE, casOrbName);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

    }

    public void unsubscribeCAS(String casOrbName, EventChannelListener listener)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = casOrbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeCAS",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        if( casOrbName == null || casOrbName.length() == 0 )
        {
            throw new IllegalArgumentException("casName may not be null.");
        }
        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_CAS_UPDATE, casOrbName);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

    }

    public CBOEProcess[] getAllProcesses(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": getAllProcesses",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        CBOEProcess[] processes = processCache.getAllProcesses();

        if(listener != null)
        {
            subscribeAllProcesses(listener);
        }

        return processes;
    }

    public ProcessInfo[] getAllProcessInfos(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": getAllProcessInfos",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        ProcessInfo[] processInfos = new ProcessInfo[0];

        synchronized( PROCESS_LOCK_OBJECT )
        {
            Collection<ProcessInfoModel> values = processInfoCache.values();
            processInfos = values.toArray(processInfos);
        }

        if(listener != null)
        {
            subscribeAllProcessInfos(listener);
        }

        return processInfos;
    }

    public String[] getAllProcessNames()
    {
        GUILoggerHome.find().debug(CATEGORY + ": getAllProcessNames",
                                   GUILoggerINBusinessProperty.PROCESSES, ( Object ) null);

        CBOEProcess[] processes = getAllProcesses(null);
        String[] processNames = new String[processes.length];

        for( int i = 0; i < processes.length; i++ )
        {
            processNames[i] = processes[i].getProcessName();
        }

        return processNames;
    }

    public ProcessInfo getProcessInfo(String processOrbName, EventChannelListener listener)
            throws DataValidationException
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = processOrbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": getProcessInfo",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        ProcessInfo process = null;

        synchronized(PROCESS_LOCK_OBJECT)
        {
            process = processInfoCache.get(processOrbName);
        }

        if(process == null)
        {
            DataValidationException exception =
                    ExceptionBuilder.dataValidationException(processOrbName +
                                                             " is not a valid process ORB name.",
                                                             DataValidationCodes.INVALID_PROCESS_NAME);
            throw exception;
        }

        if( listener != null )
        {
            subscribeProcess(processOrbName, listener);
        }

        return process;
    }

    public CBOEProcess getProcess(String processOrbName, EventChannelListener listener)
            throws DataValidationException
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = processOrbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": getProcess",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        CBOEProcess process = null;

        process = processCache.getProcess(processOrbName);

        if(process == null)
        {
            DataValidationException exception =
                    ExceptionBuilder.dataValidationException(processOrbName +
                                                             " is not a valid process ORB name.",
                                                             DataValidationCodes.INVALID_PROCESS_NAME);
            throw exception;
        }

        if( listener != null )
        {
            subscribeProcess(processOrbName, listener);
        }

        return process;
    }

    public void subscribeLargestQueue(EventChannelListener listener)
    {
        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_LARGEST_QUEUE_UPDATE, new Integer(0));
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        pwListener.publishCurrentStatus();
    }

    public void unsubscribeLargestQueue(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": unsubscribeLargestQueue",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_LARGEST_QUEUE_UPDATE, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeAllProcessInfos(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": subscribeAllProcessInfos",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_INFO_UPDATE, new Integer(0));
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        pwListener.publishCurrentStatus();
    }

    public void unsubscribeAllProcessInfos(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAllProcesses",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_INFO_UPDATE, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeProcessInfo(String processOrbName, EventChannelListener listener)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = processOrbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeProcessInfo",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        if( processOrbName == null || processOrbName.length() == 0)
        {
            throw new IllegalArgumentException("processName may not be null.");
        }
        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_INFO_UPDATE, processOrbName);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        pwListener.publishCurrentStatus(processOrbName);
    }

    public void unsubscribeProcessInfo(String processOrbName, EventChannelListener listener)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = processOrbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeProcessInfo",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        if( processOrbName == null || processOrbName.length() == 0 )
        {
            throw new IllegalArgumentException("processName may not be null.");
        }
        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_INFO_UPDATE, processOrbName);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeOrbNameAlias(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": subscribeOrbNameAlis",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_ORBNAME_ALIAS_UPDATE, new Integer(0));
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeOrbNameAlias(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": unsubscribeOrbNameAlias",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_ORBNAME_ALIAS_UPDATE, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeLogicalOrbName(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": subscribeLogicalOrbName",
                                   GUILoggerINBusinessProperty.ORB_NAME_ALIAS, listener);

        if(listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        eventChannel.addChannelListener(eventChannel, listener, new ChannelKey(ChannelType.INSTRUMENTATION_LOGICAL_ORBNAME_UPDATE, new Integer(0)));
        eventChannel.addChannelListener(eventChannel, listener, new ChannelKey(ChannelType.INSTRUMENTATION_LOGICAL_ORBNAME_DELETE, new Integer(0)));
    }

    public void unsubscribeLogicalOrbName(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": unsubscribeLogicalOrbName",
                                   GUILoggerINBusinessProperty.ORB_NAME_ALIAS, listener);

        if(listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        eventChannel.removeChannelListener(eventChannel, listener, new ChannelKey(ChannelType.INSTRUMENTATION_LOGICAL_ORBNAME_UPDATE, new Integer(0)));
        eventChannel.removeChannelListener(eventChannel, listener, new ChannelKey(ChannelType.INSTRUMENTATION_LOGICAL_ORBNAME_DELETE, new Integer(0)));
    }

    public void subscribeAllProcesses(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": subscribeAllProcesses",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_UPDATE, new Integer(0));
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        pwListener.publishCurrentStatus();
    }

    public void unsubscribeAllProcesses(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAllProcesses",
                                   GUILoggerINBusinessProperty.PROCESSES, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_UPDATE, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeProcess(String processOrbName, EventChannelListener listener)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = processOrbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeProcess",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        if( processOrbName == null || processOrbName.length() == 0)
        {
            throw new IllegalArgumentException("processName may not be null.");
        }
        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_UPDATE, processOrbName);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        pwListener.publishCurrentStatus(processOrbName);
    }

    public void unsubscribeProcess(String processOrbName, EventChannelListener listener)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = processOrbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeProcess",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        if( processOrbName == null || processOrbName.length() == 0 )
        {
            throw new IllegalArgumentException("processName may not be null.");
        }
        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_UPDATE, processOrbName);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    /**
     * This method subscribes for all instrumentors for all orbs.
     *
     * @deprecated
     * @param listener
     */
    public void subscribeInstrumentation(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": subscribeInstrumentation",
                                   GUILoggerINBusinessProperty.INSTRUMENTATION, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, InstrumentorCollector.HEAP_INSTRUMENTOR_KEY);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, InstrumentorCollector.METHOD_INSTRUMENTOR_KEY);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, InstrumentorCollector.NETWORK_INSTRUMENTOR_KEY);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, InstrumentorCollector.QUEUE_INSTRUMENTOR_KEY);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, InstrumentorCollector.THREAD_INSTRUMENTOR_KEY);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, InstrumentorCollector.HEAP_INSTRUMENTOR_KEY);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, InstrumentorCollector.METHOD_INSTRUMENTOR_KEY);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, InstrumentorCollector.NETWORK_INSTRUMENTOR_KEY);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, InstrumentorCollector.QUEUE_INSTRUMENTOR_KEY);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, InstrumentorCollector.THREAD_INSTRUMENTOR_KEY);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        incrementInstrumentorListenerCount();
    }

    /**
     * This method unsubscribes for all instrumentors for all orbs
     *
     * @deprecated
     * @param listener
     */
    public void unsubscribeInstrumentation(EventChannelListener listener)
    {
        GUILoggerHome.find().debug(CATEGORY + ": unsubscribeInstrumentation",
                                   GUILoggerINBusinessProperty.INSTRUMENTATION, listener);

        if( listener == null )
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, InstrumentorCollector.HEAP_INSTRUMENTOR_KEY);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, InstrumentorCollector.METHOD_INSTRUMENTOR_KEY);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, InstrumentorCollector.NETWORK_INSTRUMENTOR_KEY);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, InstrumentorCollector.QUEUE_INSTRUMENTOR_KEY);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, InstrumentorCollector.THREAD_INSTRUMENTOR_KEY);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, InstrumentorCollector.HEAP_INSTRUMENTOR_KEY);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, InstrumentorCollector.METHOD_INSTRUMENTOR_KEY);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, InstrumentorCollector.NETWORK_INSTRUMENTOR_KEY);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, InstrumentorCollector.QUEUE_INSTRUMENTOR_KEY);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, InstrumentorCollector.THREAD_INSTRUMENTOR_KEY);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        decrementInstrumentorListenerCount();
    }

    public void subscribeInstrumentorsForOrb(String orbName, EventChannelListener listener)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = orbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeInstrumentorsForOrb",
                    GUILoggerINBusinessProperty.INSTRUMENTATION, argObj);
        }

        if (orbName == null || orbName.length() == 0)
        {
            throw new IllegalArgumentException("orbName may not be null.");
        }
        if (listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }
        if (orbListenerMap.size() >= maxSubscriptions)
        {
            throw new IllegalArgumentException("Max Subscriptions is reached");
        }

        subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.QUEUE,listener);
        subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.METHOD,listener);
        subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.THREAD,listener);
        subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.NETWORK_CONNECTION,listener);
        subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.COUNT,listener);
        subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.EVENT,listener);

        subscribeInstrumentorsForOrb(orbName, InstrumentorTypes.JMX, listener);
        subscribeInstrumentorsForOrb(orbName, InstrumentorTypes.JSTAT, listener);

        boolean collectorAttached = InstrumentorCollectorHome.find().isCollectorProcessAttached();
        if (collectorAttached)
        {
            subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.HEAP,listener);
        }

        incrementInstrumentorsForOrbListenerCount(orbName);
    }

    public void subscribeInstrumentorsForOrb(String orbName, short instrumentorType, EventChannelListener listener)
    {
        String key = InstrumentorTypes.toString(instrumentorType);
        
        key += InstrumentorCollectorImpl.INSTRUMENTATION_DETAIL;
        
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, key);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, key);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    private void incrementInstrumentorsForOrbListenerCount(String orbName)
    {
        int count;
        synchronized(orbListenerMap)
        {
            Integer value = orbListenerMap.get(orbName);
            if (value == null)
            {
                count = 0;
            }
            else
            {
                count = value.intValue();
            }
            orbListenerMap.put(orbName, new Integer(count + 1));
            if (count == 0)
            {
                subscribeAllInstrumentorsForOrb(orbName);

                processCache.markProcessAsSubscribed(orbName);
            }
        }
    }

	/**
	 * @param orbName
	 */
	private void subscribeAllInstrumentorsForOrb(String orbName) {
		InstrumentorCollectorHome.find().subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.QUEUE);
		InstrumentorCollectorHome.find().subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.METHOD);
		InstrumentorCollectorHome.find().subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.THREAD);
		InstrumentorCollectorHome.find().subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.NETWORK_CONNECTION);
		InstrumentorCollectorHome.find().subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.COUNT);
		InstrumentorCollectorHome.find().subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.EVENT);
        InstrumentorCollectorHome.find().subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.KEY_VALUE);

        boolean collectorAttached = InstrumentorCollectorHome.find().isCollectorProcessAttached();
		if (collectorAttached)
		{
		    InstrumentorCollectorHome.find().subscribeInstrumentorsForOrb(orbName,InstrumentorTypes.HEAP);
		    //InstrumentorCollectorHome.find().blockSummaryPublish(orbName);
		    subscribeCollectorDetailsViaAdmin(orbName);
		}
	}

    public void unsubscribeInstrumentorsForOrb(String orbName, EventChannelListener listener)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = orbName;
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeInstrumentorsForOrb",
                    GUILoggerINBusinessProperty.INSTRUMENTATION, argObj);
        }

        if (orbName == null || orbName.length() == 0)
        {
            throw new IllegalArgumentException("orbName may not be null.");
        }
        if (listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        unsubscribeInstrumentorsForOrb(orbName,InstrumentorTypes.QUEUE,listener);
        unsubscribeInstrumentorsForOrb(orbName,InstrumentorTypes.METHOD,listener);
        unsubscribeInstrumentorsForOrb(orbName,InstrumentorTypes.THREAD,listener);
        unsubscribeInstrumentorsForOrb(orbName,InstrumentorTypes.NETWORK_CONNECTION,listener);
        unsubscribeInstrumentorsForOrb(orbName,InstrumentorTypes.COUNT,listener);
        unsubscribeInstrumentorsForOrb(orbName,InstrumentorTypes.EVENT,listener);
        unsubscribeInstrumentorsForOrb(orbName,InstrumentorTypes.JMX,listener);
        unsubscribeInstrumentorsForOrb(orbName,InstrumentorTypes.JSTAT,listener);

        boolean collectorAttached = InstrumentorCollectorHome.find().isCollectorProcessAttached();
        if (collectorAttached)
        {
            unsubscribeInstrumentorsForOrb(orbName,InstrumentorTypes.HEAP,listener);
        }

        decrementInstrumentorsForOrbListenerCount(orbName);
    }
    
    public void unsubscribeInstrumentorsForOrb(String orbName, short instrumentorType, EventChannelListener listener)
    {
        //String key = orbName + instrumentorType;
        String key = InstrumentorTypes.toString(instrumentorType);
        
        key += InstrumentorCollectorImpl.INSTRUMENTATION_DETAIL;
        
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, key);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, key);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    private void decrementInstrumentorsForOrbListenerCount(String orbName)
    {
        int count;
        synchronized (orbListenerMap)
        {
            Integer value = orbListenerMap.get(orbName);
            if (value != null)
            {
                count = value.intValue();

                if (count == 1)
                {
                    // If we're at a count of 1, it's the last one listening, so unsubscribe.
                    orbListenerMap.remove(orbName);

                    InstrumentorCollectorHome.find().unsubscribeInstrumentorsForOrb(orbName, InstrumentorTypes.QUEUE);
                    InstrumentorCollectorHome.find().unsubscribeInstrumentorsForOrb(orbName, InstrumentorTypes.METHOD);
                    InstrumentorCollectorHome.find().unsubscribeInstrumentorsForOrb(orbName, InstrumentorTypes.THREAD);
                    InstrumentorCollectorHome.find().unsubscribeInstrumentorsForOrb(orbName, InstrumentorTypes.NETWORK_CONNECTION);
                    InstrumentorCollectorHome.find().unsubscribeInstrumentorsForOrb(orbName, InstrumentorTypes.COUNT);
                    InstrumentorCollectorHome.find().unsubscribeInstrumentorsForOrb(orbName, InstrumentorTypes.EVENT);
                    InstrumentorCollectorHome.find().unsubscribeInstrumentorsForOrb(orbName, InstrumentorTypes.KEY_VALUE);

                    boolean collectorAttached = InstrumentorCollectorHome.find().isCollectorProcessAttached();
                    if (collectorAttached)
                    {
                        InstrumentorCollectorHome.find().unsubscribeInstrumentorsForOrb(orbName, InstrumentorTypes.HEAP);
                        unsubscribeCollectorDetailsViaAdmin(orbName);
                    }

                    processCache.removeProcessAsSubscribed(orbName);
                }
                else
                {
                    // There's more than one listener left, decrement the count and store it back in the map.
                    --count;
                    orbListenerMap.put(orbName, new Integer(count));
                }
            }
        }
    }

    /**
     * Subscribe for summary data.  It a collector process is attached, then subscribe for
     * everything on the summary channel.  If there is no collector, then subscribe for all
     * orbs for heap.
     * @param listener
     */
    public void subscribeAllOrbsForSummary(EventChannelListener listener)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAllOrbsForSummary",
                    GUILoggerINBusinessProperty.INSTRUMENTATION, argObj);
        }
        if (listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        boolean collectorAttached = InstrumentorCollectorHome.find().isCollectorProcessAttached();

        if (collectorAttached)
        {
            addSummaryListenerByType(InstrumentorTypes.QUEUE,listener);
            addSummaryListenerByType(InstrumentorTypes.METHOD,listener);
            addSummaryListenerByType(InstrumentorTypes.THREAD,listener);
            addSummaryListenerByType(InstrumentorTypes.NETWORK_CONNECTION,listener);
            addSummaryListenerByType(InstrumentorTypes.HEAP,listener);
            addSummaryListenerByType(InstrumentorTypes.EVENT,listener);
            addSummaryListenerByType(InstrumentorTypes.COUNT,listener);
            addSummaryListenerByType(InstrumentorTypes.JMX,listener);
            addSummaryListenerByType(InstrumentorTypes.JSTAT,listener);

            incrementSummaryListenerCount();
        }
        else
        {
            subscribeAllOrbsByType(InstrumentorTypes.HEAP, listener);
        }
    }

    private void incrementSummaryListenerCount()
    {
        synchronized (SUMMARY_LOCK_OBJECT)
        {
            if (summaryInstrumentorSubscriptionCount < 0)
            {
                summaryInstrumentorSubscriptionCount=0;
            }

            if (summaryInstrumentorSubscriptionCount == 0)
            {
                InstrumentorCollectorHome.find().subscribeAllOrbsForSummary();
            }
            summaryInstrumentorSubscriptionCount++;
        }
    }

    private void addSummaryListenerByType(short instrumentorType, EventChannelListener listener)
    {
        String key = InstrumentorTypes.toString(instrumentorType);

        key += InstrumentorCollectorImpl.INSTRUMENTATION_SUMMARY;
        
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, key);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, key);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    /**
     * UnSubscribe for summary data.  It a collector process is attached, then unsubscribe for
     * everything on the summary channel.  If there is no collector, then unsubscribe for all
     * orbs for heap.
     * @param listener
     */
    public void unsubscribeAllOrbsForSummary(EventChannelListener listener)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAllOrbsForSummary",
                    GUILoggerINBusinessProperty.INSTRUMENTATION, argObj);
        }
        boolean collectorAttached = InstrumentorCollectorHome.find().isCollectorProcessAttached();
        if (collectorAttached)
        {
            removeSummaryListenerByType(InstrumentorTypes.QUEUE,listener);
            removeSummaryListenerByType(InstrumentorTypes.METHOD,listener);
            removeSummaryListenerByType(InstrumentorTypes.THREAD,listener);
            removeSummaryListenerByType(InstrumentorTypes.NETWORK_CONNECTION,listener);
            removeSummaryListenerByType(InstrumentorTypes.HEAP,listener);
            removeSummaryListenerByType(InstrumentorTypes.EVENT,listener);
            removeSummaryListenerByType(InstrumentorTypes.COUNT,listener);
            removeSummaryListenerByType(InstrumentorTypes.JMX,listener);
            removeSummaryListenerByType(InstrumentorTypes.JSTAT,listener);
            decrementSummaryListenerCount();
        }
        else
        {
            unsubscribeAllOrbsByType(InstrumentorTypes.HEAP, listener);
        }
    }
    private void decrementSummaryListenerCount()
    {
        synchronized (SUMMARY_LOCK_OBJECT)
        {
            summaryInstrumentorSubscriptionCount--;
            if (summaryInstrumentorSubscriptionCount == 0)
            {
                InstrumentorCollectorHome.find().unsubscribeAllOrbsForSummary();
            }
        }
    }
    private void removeSummaryListenerByType(short instrumentorType, EventChannelListener listener)
    {
        String key = InstrumentorTypes.toString(instrumentorType);
        
        key+= InstrumentorCollectorImpl.INSTRUMENTATION_SUMMARY;
        
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, key);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, key);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeAllOrbsByType(short instrumentorType, EventChannelListener listener)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Short(instrumentorType);
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAllOrbsByType",
                                       GUILoggerINBusinessProperty.INSTRUMENTATION, argObj);
        }

        if (listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }
        String key = InstrumentorTypes.toString(instrumentorType);
        
        key += InstrumentorCollectorImpl.INSTRUMENTATION_DETAIL;

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, key);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, key);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        incrementAllOrbListenerCount(instrumentorType, key);
    }

    private void incrementAllOrbListenerCount(short instrumentorType, String key)
    {
        int count;
        synchronized (allOrbListenerMap)
        {
            Integer value = allOrbListenerMap.get(key);
            if (value == null)
            {
                count = 0;
            }
            else
            {
                count = value.intValue();
            }
            allOrbListenerMap.put(key, new Integer(count + 1));
            if (count == 0)
            {
                InstrumentorCollectorHome.find().subscribeAllOrbsByType(instrumentorType);
            }
        }
    }

    public void unsubscribeAllOrbsByType(short instrumentorType, EventChannelListener listener)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Short(instrumentorType);
            argObj[1] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAllOrbsByType",
                                       GUILoggerINBusinessProperty.INSTRUMENTATION, argObj);
        }

        if (listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }
        String key = InstrumentorTypes.toString(instrumentorType);
        
        key+= InstrumentorCollectorImpl.INSTRUMENTATION_DETAIL;
        
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE, key);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, key);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        decrementAllOrbListenerCount(instrumentorType, key);
    }

    private void decrementAllOrbListenerCount(short instrumentorType, String key)
    {
        int count;
        synchronized (allOrbListenerMap)
        {
            Integer value = allOrbListenerMap.get(key);
            if (value == null)
            {
                count = 0;
            }
            else
            {
                count = value.intValue();
            }

            count -= 1;
            
            if(count < 0)
            {
            	count = 0;
            }
            
            allOrbListenerMap.put(key, new Integer(count));
            if (count == 0)
            {
                InstrumentorCollectorHome.find().unsubscribeAllOrbsByType(instrumentorType);
            }
        }
    }

    /**
     * Get the list of processes that are subscribed for instrumentation details.
     * @return
     */
    public CBOEProcess[] getSubscribedInstrumentorProcessList()
    {
        CBOEProcess[] subscribedList = processCache.getSubscribedProcesses();
        return subscribedList;
    }

    public int getSubscribedProcessesCount()
    {
        return processCache.getSubscribedProcessesCount();
    }

    public CASConfiguration getCASConfiguration(String casOrbName) throws SystemException, DataValidationException,
            TimedOutException
    {
        String requestXml = XmlBindingFacade.getInstance().createConfigurationRequest(casOrbName);

        // Build command or get it from the factory
        String commandName = SystemMonitorCommandMethodNames.CONFIGURATION_DATA_QUERY;
        ARCommand arCommand = CommandFactory.getInstance().getCommand(casOrbName, commandName);
        Command executeCommand = arCommand.buildExecuteCommand(requestXml);

        try
        {
            CBOEProcess process = getProcess(casOrbName, null);
            casOrbName = process.getProcessName();
        }
        catch(DataValidationException dve)
        {
            // did not find the process name, so try to use the original name.
        }

        //Get data from the AdminService
        ExecuteCommandService service = ARCommandServiceFactory.getExecuteCommandService();
        service.setDestination(casOrbName);

        try
        {
            String responseXml = null;
            ExecutionResult execResult = service.executeCommand(executeCommand);
            if(execResult != null && execResult.isSuccess())
            {
                Command resultCommand = execResult.getCommandResult();
                responseXml = resultCommand.retValues[0].value;
            }
            if(responseXml != null && responseXml.length() > 0)
            {
                Object unmarshalledObject = getXmlBindingObject(responseXml);
                if(unmarshalledObject instanceof GIConfigurationResponseType)
                {
                    GIConfigurationResponseType configurationResponseType = (GIConfigurationResponseType) unmarshalledObject;
                    return CASConfigurationFactory.createCASConfiguration(configurationResponseType);
                }
                else // unknown response
                {
                    String message = "Invalid Response For CASConfigurationRequest";
                    GUILoggerHome.find().alarm("Invalid Response For CASConfigurationRequest", responseXml);
                    StringBuilder buffer =
                            new StringBuilder(responseXml.length() + message.length());
                    buffer.append(message).append('\n');
                    buffer.append(responseXml);
                    //noinspection ThrowCaughtLocally
                    throw ExceptionBuilder.systemException(buffer.toString(), 0);
                }
            }
            else // unknown response
            {
                String message = "Invalid Response For CASConfigurationRequest";
                GUILoggerHome.find()
                        .alarm("Invalid Response For CASConfigurationRequest", responseXml);
                //noinspection ThrowCaughtLocally
                throw ExceptionBuilder.systemException(message, 0);
            }
        }
        catch(UserException e)
        {
            //noinspection InstanceofCatchParameter
            if(e instanceof SystemException)
            {
                throw (SystemException) e;
            }
            else
            {
                SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
                se.initCause(e);
                throw se;
            }
        }
    }

    public ContextDetail getContextDetail(String processOrbName, String contextName)
        throws UserException, CommunicationException, SystemException, AuthorizationException, DataValidationException,
        AlreadyExistsException, AuthenticationException, NotAcceptedException, NotFoundException,
        NotSupportedException, TransactionFailedException, TimedOutException
    {
        ContextDetail[] contextDetails = getContextDetail(processOrbName, new String[] { contextName } );
        if(contextDetails.length > 0)
        {
            return contextDetails[0];
        }
        return null;
    }

    public ContextDetail[] getContextDetail(String processOrbName, String[] contextNames)
        throws UserException, CommunicationException, SystemException, AuthorizationException, DataValidationException,
        AlreadyExistsException, AuthenticationException, NotAcceptedException, NotFoundException,
        NotSupportedException, TransactionFailedException, TimedOutException
    {
        String requestXml = XmlBindingFacade.getInstance().createContextDetailRequest(processOrbName, contextNames);

        // Build command or get it from the factory
        String commandName = SystemMonitorCommandMethodNames.CONTEXT_DETAIL_QUERY;
        ARCommand arCommand = CommandFactory.getInstance().getCommand(processOrbName, commandName);
        Command executeCommand = arCommand.buildExecuteCommand(requestXml);

        try
        {
            CBOEProcess process = getProcess(processOrbName, null);
            processOrbName = process.getProcessName();
        }
        catch(DataValidationException dve)
        {
            // did not find the process name, so try to use the original name.
        }

        ExecuteCommandService service = ARCommandServiceFactory.getExecuteCommandService();
        service.setDestination(processOrbName);

        String responseXml = null;
        ExecutionResult execResult = service.executeCommand(executeCommand);
        if(execResult != null && execResult.isSuccess())
        {
            Command resultCommand = execResult.getCommandResult();
            responseXml = resultCommand.retValues[0].value;
        }

        if(responseXml != null && responseXml.length() > 0)
        {
            Object unmarshalledObject = getXmlBindingObject(responseXml);
            if(unmarshalledObject instanceof GIContextDetailResponseType)
            {
                return ContextDetailFactory.createContextDetails((GIContextDetailResponseType) unmarshalledObject, responseXml);
            }
            else // unknown response
            {
                String message = "Invalid Response For ContextDetailRequest";
                GUILoggerHome.find().alarm("Invalid Response For ContextDetailRequest", responseXml);
                StringBuffer buffer = new StringBuffer(responseXml.length()+message.length());
                buffer.append(message).append('\n');
                buffer.append(responseXml);
                throw ExceptionBuilder.systemException(buffer.toString(), 0);
            }
        }
        else
        {
            String message = "Invalid Response For ContextDetailRequest";
            GUILoggerHome.find().alarm("Invalid Response For ContextDetailRequest", responseXml);
            throw ExceptionBuilder.systemException(message, 0);
        }
    }

    public void addProcessInfoToCache(ProcessInfoModel processInfoModel)
    {
        //used for possible re-creation as a CASModel which extends ProcessInfoModel
        ProcessInfoModel modelToAdd = processInfoModel;


        if( !processInfoCache.containsKey(modelToAdd.getOrbName()) )
        {
            processInfoCache.put(modelToAdd.getOrbName(), modelToAdd);
        }
    }

    public void dispatchProcessInfoEvent(ProcessInfoModel process)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_INFO_UPDATE, process.getOrbName());
        ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKey, process);
        eventChannel.dispatch(channelEvent);

        channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_INFO_UPDATE, new Integer(0));
        channelEvent = eventChannel.getChannelEvent(this, channelKey, process);
        eventChannel.dispatch(channelEvent);
    }

    public Product getAllSelectedProduct()
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllSelectedProduct",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        return ProductFactoryHome.find().createAllSelected();
    }

    public ProductClass getAllSelectedProductClass()
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllSelectedProductClass",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        return ProductClassFactoryHome.find().createAllSelected();
    }

    public SessionProduct getAllSelectedSessionProduct()
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllSelectedSessionProduct",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        return SessionProductFactory.createAllSelected();
    }

    public SessionProduct getAllSelectedSessionProduct(String sessionName)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllSelectedSessionProduct",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, sessionName);
        }
        return SessionProductFactory.createAllSelected(sessionName);
    }

    public SessionProductClass getAllSelectedSessionProductClass()
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllSelectedSessionProductClass",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        return SessionProductClassFactory.createAllSelected();
    }

    public SessionProductClass getAllSelectedSessionProductClass(String sessionName)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllSelectedSessionProductClass",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, sessionName);
        }
        return SessionProductClassFactory.createAllSelected(sessionName);
    }

    public Product getDefaultProduct()
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getDefaultProduct", GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        return ProductFactoryHome.find().createDefault();
    }

    public ProductClass getDefaultProductClass()
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getDefaultProductClass",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        return ProductClassFactoryHome.find().createDefault();
    }

    public SessionProduct getDefaultSessionProduct()
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getDefaultSessionProduct",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        return SessionProductFactory.createDefault();
    }

    public SessionProduct getDefaultSessionProduct(String sessionName)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getDefaultSessionProduct",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, sessionName);
        }
        return SessionProductFactory.createDefault(sessionName);
    }

    public SessionProductClass getDefaultSessionProductClass()
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getDefaultSessionProductClass",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        return SessionProductClassFactory.createDefault();
    }

    public SessionProductClass getDefaultSessionProductClass(String sessionName)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getDefaultSessionProductClass",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, sessionName);
        }
        return SessionProductClassFactory.createDefault(sessionName);
    }

    public SessionStrategy getDefaultSessionStrategy()
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getDefaultSessionStrategy",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        return SessionProductFactory.createDefaultStrategy();
    }

    public Strategy getDefaultStrategy()
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            GUILoggerHome.find().debug(CATEGORY + ": getDefaultStrategy",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        return ProductFactoryHome.find().createDefaultStrategy();
    }

    public ProductClass[] getAllClassesForType(short productType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getAllClassesForType(productType, false);
    }

    private ProductClass[] getAllClassesForType(short productType, boolean init)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!init && !isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        ProductClass[] classes = ProductQueryCacheFactory.find().getProductClasses(productType, false);
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllClassesForType", GUILoggerINBusinessProperty.PRODUCT_QUERY, new Short(productType));
        }
        if (classes.length == 0)
        {                                                      
            classes = loadProductClasses(productType);
        }
        return classes;
    }

    public Product[] getAllProductsForClass(int classKey, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllProductsForClass", GUILoggerINBusinessProperty.PRODUCT_QUERY, new Integer(classKey));
        }
        try
        {
            if (isStrategy(classKey))
            {
                loadStrategiesForClass(classKey);
            }
            else
            {
                loadProductsForClass(classKey);
            }
        }
        catch (NotFoundException e)
        {
            DataValidationException de = new DataValidationException();
            de.details = e.details;
            throw de;
        }
        return ProductQueryCacheFactory.find().getProducts(classKey, activeOnly);
    }

    public ProductType[] getAllProductTypes()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ProductTypeStruct[] structs = getAllProductTypes(false);
        ProductType[] structWrappers = new ProductType[structs.length];

        for(int i = 0; i < structs.length; i++)
        {
            ProductTypeStruct struct = structs[i];
            structWrappers[i] = ProductTypeFactory.create(struct);
        }

        return structWrappers;
    }

    private ProductTypeStruct[] getAllProductTypes(boolean init)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!init && !isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }
        ProductTypeStruct[] types;

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllProductTypes", GUILoggerINBusinessProperty.PRODUCT_QUERY, "");
        }
        types = ProductQueryCacheFactory.find().getProductTypes();
        if (types.length == 0)
        {
            types = loadProductTypes();
        }

        return types;
    }

    public SessionProductClass[] getProductClassesForSession(String sessionName, short productType,
                                                             EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Short(productType);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(CATEGORY + ": getProductClassesForSession", GUILoggerINBusinessProperty.PRODUCT_QUERY, argObj);
        }

        SessionProductClass[] spc;

        if (sessionName.equals(DefaultTradingSession.DEFAULT))
        {
            ProductClass[] pcs = getAllClassesForType(productType);
            spc = new SessionProductClass[pcs.length];
            for (int i = 0; i < pcs.length; i++)
            {
                ProductClass productClass = pcs[i];
                spc[i] = SessionProductClassFactory.create(sessionName, productClass);
            }
        }
        else
        {
            spc = loadProductClassesForSession(sessionName,productType);
//            subscribeSessionClassByType(sessionName, productType, clientListener);
//            spc = SessionProductCacheFactory.find(sessionName).getClassesForSession(productType);
        }

        return spc;
    }

    public SessionProduct[] getProductsForSession(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(CATEGORY + ": getProductsForSession", GUILoggerINBusinessProperty.PRODUCT_QUERY, argObj);
        }
        subscribeProductsForSession(sessionName, classKey, clientListener);
        return SessionProductCacheFactory.find(sessionName).getProductsForSession(classKey);
    }

    public ProductType getProductType(short type)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        ProductType productType = null;

        ProductType[] allTypes = getAllProductTypes();

        for (int i = 0; i < allTypes.length; i++)
        {
            if (allTypes[i].getType() == type)
            {
                productType = allTypes[i];
                break;
            }
        }

        if (productType == null)
        {
            throw ExceptionBuilder.notFoundException("ProductType for passed type:" + type + ". Could not be found.",
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        return productType;
    }

    /**
     * A convenience method to get all SessionProductClass'es for all product types.
     * @param sessionName to get all classes for all types
     * @return and array of SessionProductClass that are contained by passed sessionName for all product types
     */
    public SessionProductClass[] getAllProductClassesForSession(String sessionName)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.PRODUCT_QUERY))
        {
            Object[] argObj = new Object[1];
            argObj[0] = sessionName;

            GUILoggerHome.find().debug(CATEGORY + ": getAllProductClassesForSession",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, argObj);
        }

        SessionProductClass[] spc;

        ProductType[] allTypes = getProductTypesForSession(sessionName);
        List<SessionProductClass> allSPCs = new ArrayList<SessionProductClass>(allTypes.length * 200);

        for(int i = 0; i < allTypes.length; i++)
        {
            ProductType productType = allTypes[i];
            spc = getProductClassesForSession(sessionName, productType.getType(), null);
            for(int j = 0; j < spc.length; j++)
            {
                SessionProductClass sessionProductClass = spc[j];
                allSPCs.add(sessionProductClass);
            }
        }

        spc = new SessionProductClass[allSPCs.size()];
        spc = allSPCs.toArray(spc);

        return spc;
    }

    public ProductType[] getProductTypesForSession(String sessionName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ProductType[] types = getProductTypesForSession(sessionName, false);

        return types;
    }

    private ProductType[] getProductTypesForSession(String sessionName, boolean init)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!init && !isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(CATEGORY + ": getProductTypesForSession",
                                       GUILoggerINBusinessProperty.PRODUCT_QUERY, sessionName);
        }

        ProductType[] types;

        if (sessionName.equals(DefaultTradingSession.DEFAULT))
        {
            types = getAllProductTypes();
        }
        else
        {
            ProductTypeStruct[] structs = SessionProductCacheFactory.find(sessionName).getProductTypesForSession();

            if (structs.length == 0)
            {
                structs = loadProductTypesForSession(sessionName);
            }
            types = new ProductType[structs.length];

            for(int i = 0; i < structs.length; i++)
            {
                ProductTypeStruct struct = structs[i];
                types[i] = ProductTypeFactory.create(struct);
            }
        }

        return types;
    }

    public Strategy getStrategyByKey(int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(CATEGORY + ": getStrategyByKey", GUILoggerINBusinessProperty.PRODUCT_QUERY, new Integer(productKey));
        }
        Strategy strategy = ProductQueryCacheFactory.find().getStrategyByKey(productKey);
        if (strategy == null)
        {
            StrategyStruct strategyStruct = productQuery.getStrategyByKey(productKey);
            ProductQueryCacheFactory.find().updateStrategy(ProductFactoryHome.find().create(strategyStruct));
            loadStrategiesForClass(strategyStruct.product.productKeys.classKey);
            strategy = (Strategy) ProductQueryCacheFactory.find().getStrategyByKey(productKey);
        }
        return strategy;
    }

    public SessionStrategy getStrategyByKeyForSession(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        try
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                Object[] argObj = new Object[2];
                argObj[0] = sessionName;
                argObj[1] = new Integer(productKey);

                GUILoggerHome.find().debug(CATEGORY + ": getStrategyByKeyForSession", GUILoggerINBusinessProperty.PRODUCT_QUERY, argObj);
            }
            SessionStrategy sessionStrategy = SessionProductCacheFactory.find(sessionName).getStrategyByKey(productKey);
            if (sessionStrategy == null)
            {   // try to find in inactive session products cache
                sessionStrategy = SessionProductCacheFactory.find(SessionProduct.NOT_IN_TRADING_SESSION).getStrategyByKey(productKey);
            }
            if (sessionStrategy == null)
            {
                try
                {
                    SessionStrategyStruct sessionStrategyStruct = tradingSession.getStrategyBySessionForKey(sessionName, productKey);
                    productProcessor.updateStrategy(sessionStrategyStruct);
//                    loadStrategiesForClassBySession(sessionName, sessionStrategyStruct.sessionProductStruct.productStruct.productKeys.classKey);
                    sessionStrategy = (SessionStrategy) SessionProductCacheFactory.find(sessionName).getStrategyByKey(productKey);
                    if (sessionStrategy == null)
                    {
                        sessionStrategy = (SessionStrategy) findInactiveSessionProduct(sessionName, productKey);
                    }
                }
                catch (NotFoundException e)
                {   // the product is not part of the session, see if we can create an inactive product
                    sessionStrategy = (SessionStrategy) findInactiveSessionProduct(sessionName, productKey);
                }
                if (sessionStrategy == null)
                {
                    throw ExceptionBuilder.notFoundException("No strategy found with productKey = " + productKey + " in session = " + sessionName + " in translator product cache", 0);
                }
            }
            return sessionStrategy;
        }
        catch (NotFoundException e)
        {
            DataValidationException de = new DataValidationException();
            de.details = e.details;
            throw de;
        }
    }

    public boolean isStrategy(int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        boolean isStrategy = false;
        ProductClass productClass = ProductQueryCacheFactory.find().getProductClassByKey(classKey);
        if (productClass == null)
        {
            ClassStruct classStruct = productQuery.getClassByKey(classKey);

            if (classStruct != null)
            {
                ProductClass rawProductClass = ProductClassFactoryHome.find().create(classStruct);
                ProductQueryCacheFactory.find().addClass(rawProductClass, classStruct.productType);
                productClass = ProductQueryCacheFactory.find().getProductClassByKey(classKey);
            }
        }
        if (productClass != null)
        {
            if (productClass.getProductType() == ProductTypes.STRATEGY)
            {
                isStrategy = true;
            }
        }
        return isStrategy;
    }

    public ProductClass getProductClassByKey(int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        ProductClass productClass = null;

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(CATEGORY + ": getProductClassByKey", GUILoggerINBusinessProperty.PRODUCT_QUERY, new Integer(classKey));
        }

        if (classKey == CustomKeys.DEFAULT_CLASS_KEY)
        {
            productClass = getDefaultProductClass();
        }
        else if (classKey == CustomKeys.ALL_SELECTED_CLASS_KEY)
        {
            productClass = getAllSelectedProductClass();
        }
        else
        {
            productClass = ProductQueryCacheFactory.find().getProductClassByKey(classKey);
        }

        if (productClass == null)
        {
            ClassStruct rawClass = productQuery.getClassByKey(classKey);
            ProductClass rawProductClass = ProductClassFactoryHome.find().create(rawClass);
            ProductQueryCacheFactory.find().addClass(rawProductClass, rawProductClass.getProductType());
            productClass = ProductQueryCacheFactory.find().getProductClassByKey(classKey);
        }

        return productClass;
    }

    public SessionProductClass getClassByKeyForSession(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        SessionProductClass sessionProductClass = null;

        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            GUILoggerHome.find().debug(CATEGORY + ": getClassByKeyForSession", GUILoggerINBusinessProperty.PRODUCT_QUERY, argObj);
        }

        if (classKey == CustomKeys.DEFAULT_CLASS_KEY)
        {
            sessionProductClass = getDefaultSessionProductClass(sessionName);
        }
        else if (classKey == CustomKeys.ALL_SELECTED_CLASS_KEY)
        {
            sessionProductClass = getAllSelectedSessionProductClass(sessionName);
        }
        else if (sessionName.equals(DefaultTradingSession.DEFAULT))
        {
            ProductClass productClass = getProductClassByKey(classKey);
            sessionProductClass = SessionProductClassFactory.create(sessionName, productClass);
        }
        else
        {
            //attempts to find the product from cache
            sessionProductClass = SessionProductCacheFactory.find(sessionName).getClassByKey(classKey);
        }

        // if not found, calls CAS tradingSession to get class and stores it at the cache
        if (sessionProductClass == null)
        {
            SessionClassStruct sessionClassStruct = tradingSession.getClassBySessionForKey(sessionName, classKey);
            sessionProductClass = SessionProductClassFactory.create(sessionClassStruct);
            ProductClass productClass = ProductClassFactoryHome.find().create(sessionClassStruct.classStruct);
            ProductQueryCacheFactory.find().addClass(productClass, sessionClassStruct.classStruct.productType);
            productProcessor.addClass(sessionClassStruct);
        }

        return sessionProductClass;
    }

    public Product getProductByKey(int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(CATEGORY + ": getProductByKey", GUILoggerINBusinessProperty.PRODUCT_QUERY, new Integer(productKey));
        }

        Product product = null;

        if (productKey == CustomKeys.DEFAULT_PRODUCT_KEY)
        {
            product = getDefaultProduct();
        }
        else if (productKey == CustomKeys.ALL_SELECTED_PRODUCT_KEY)
        {
            product = getAllSelectedProduct();
        }
        else if (productKey == CustomKeys.DEFAULT_STRATEGY_PRODUCT_KEY)
        {
            product = getDefaultStrategy();
        }
        else
        {
            product = ProductQueryCacheFactory.find().getProductByKey(productKey);
        }

        if (product == null)
        {
            ProductStruct productStruct = productQuery.getProductByKey(productKey);
            if (productStruct.productKeys.productType == ProductTypes.STRATEGY)
            {
                StrategyStruct strategyStruct = productQuery.getStrategyByKey(productKey);
                ProductQueryCacheFactory.find().updateStrategy(ProductFactoryHome.find().create(strategyStruct));
                loadStrategiesForClass(productStruct.productKeys.classKey);
                product = ProductQueryCacheFactory.find().getStrategyByKey(productKey);
            }
            else
            {
                ProductQueryCacheFactory.find().updateProduct(ProductFactoryHome.find().create(productStruct));
                loadProductsForClass(productStruct.productKeys.classKey);
                product = ProductQueryCacheFactory.find().getProductByKey(productKey);
            }
            if (product == null)
            {
                throw ExceptionBuilder.notFoundException("No product found with key = " + productKey + " in translator product cache", 0);
            }
        }
        return product;
    }

    public SessionProduct getProductByKeyForSession(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);

            GUILoggerHome.find().debug(CATEGORY + ": getProductByKeyForSession", GUILoggerINBusinessProperty.PRODUCT_QUERY, argObj);
        }

        SessionProduct sessionProduct = null;
        if (productKey == CustomKeys.DEFAULT_PRODUCT_KEY)
        {
            sessionProduct = getDefaultSessionProduct(sessionName);
        }
        else if (productKey == CustomKeys.DEFAULT_STRATEGY_PRODUCT_KEY)
        {
            sessionProduct = getDefaultSessionStrategy();
        }
        else if (productKey == CustomKeys.ALL_SELECTED_PRODUCT_KEY)
        {
            sessionProduct = getAllSelectedSessionProduct(sessionName);
        }
        else
        {
            // attempts to find it in cache
            sessionProduct = SessionProductCacheFactory.find(sessionName).getProductByKey(productKey);
            if (sessionProduct == null)
            {   // try to find in inactive session products cache
                sessionProduct = SessionProductCacheFactory.find(SessionProduct.NOT_IN_TRADING_SESSION).getProductByKey(productKey);
            }
        }
        // if not found in cache, call CAS tradingSession to get the product and reload all the products in that class
        if (sessionProduct == null)
        {
            SessionProductStruct sessionProductStruct = null;
            try
            {
                sessionProductStruct = tradingSession.getProductBySessionForKey(sessionName, productKey);
                if (sessionProductStruct.productStruct.productKeys.productType == ProductTypes.STRATEGY)
                {
                    SessionStrategyStruct sessionStrategyStruct = tradingSession.getStrategyBySessionForKey(sessionName, productKey);
                    productProcessor.updateStrategy(sessionStrategyStruct);
//                    loadStrategiesForClassBySession(sessionName, sessionProductStruct.productStruct.productKeys.classKey);
                    sessionProduct = SessionProductCacheFactory.find(sessionName).getStrategyByKey(productKey);
                }
                else
                {
                    productProcessor.updateProduct(sessionProductStruct);
//                    loadProductsForClassBySession(sessionName, sessionProductStruct.productStruct.productKeys.classKey);
                    sessionProduct = SessionProductCacheFactory.find(sessionName).getProductByKey(productKey);
                }
                if (sessionProduct == null)
                {
                    sessionProduct = findInactiveSessionProduct(sessionName, productKey);
                }
            }
            catch (NotFoundException e)
            {   // the product is not part of the session, see if we can create an inactive product
                sessionProduct = findInactiveSessionProduct(sessionName, productKey);
            }
            if (sessionProduct == null)
            {
                throw ExceptionBuilder.notFoundException("No product found with key = " + productKey + " in session:" + sessionName + " in translator product cache", 0);
            }
        }
        return sessionProduct;
    }

    public TradingSessionStruct getAllSessionsTradingSession()
    {
        TradingSessionStruct newStruct = new TradingSessionStruct();
        newStruct.sessionName = DefaultTradingSession.DEFAULT;
        newStruct.sequenceNumber = 0;
        newStruct.state = 0;
        newStruct.endTime = StructBuilder.buildTimeStruct();
        newStruct.startTime = StructBuilder.buildTimeStruct();
        return newStruct;
    }

    public TradingSessionStruct[] getCurrentTradingSessions(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getCurrentTradingSessions(clientListener, false);
    }

    public TradingSessionStruct[] getCurrentTradingSessions(EventChannelListener clientListener, boolean init)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!isAdminServiceInitialized())
        {
            throw ExceptionBuilder.systemException("AdminService is not initialized", 0);
        }
        if (!init && !isProductQueryServiceInitialized())
        {
            throw ExceptionBuilder.systemException("ProductQueryService is not initialized", 0);
        }
        TradingSessionStruct[] sessions = tradingSessionCache.getCurrentTradingSessions();

        // Go to the TradingSessionService only during initialization
        if(init)
        {
            if (sessions == null || sessions.length == 0 )
            {
                sessions = tradingSession.getCurrentTradingSessions(null);
                tradingSessionCache.addTradingSessions(sessions);
            }
        }

        return sessions;
    }


    protected SessionProduct findInactiveSessionProduct(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProduct sessionProduct = null;
        // try to find it in the session cache for inactive session products
        sessionProduct = SessionProductCacheFactory.find(SessionProduct.NOT_IN_TRADING_SESSION).getProductByKey(productKey);
        if (sessionProduct == null) // not in the cache, so create it
        {
            // There are two possibilities.  The product does not exist anymore, or the
            // product is inactive and consequently out of the session.
            // In the second case, we'll create an InactiveSessionProduct to represent the product.

            // need to get the product struct
            ProductStruct productStruct = productQuery.getProductByKey(productKey);
            // Only create an InactiveSessionProduct for existing inactive products
            if (productStruct != null && productStruct.listingState != ListingStates.ACTIVE)
            {
                // now add inactive product
                if (productStruct.productKeys.productType == ProductTypes.STRATEGY)
                {
                    StrategyStruct strategyStruct = productQuery.getStrategyByKey(productKey);
                    sessionProduct = productProcessor.addInactiveSessionStrategy(sessionName, SessionProduct.NOT_IN_TRADING_SESSION, strategyStruct);
                }
                else
                {
                    sessionProduct = productProcessor.addInactiveSessionProduct(sessionName, SessionProduct.NOT_IN_TRADING_SESSION, productStruct);
                }
            }
        }
        return sessionProduct;
    }

    /****************** No implements for ProductQueryAPI *******************************/
    public SessionProduct findATMNearestTermByProductClass(SessionProductClass sessionProductClass)
            throws DataValidationException, AuthorizationException, CommunicationException, SystemException,
            NotFoundException
    {
        throw getUnsupportedException("findATMNearestTermByProductClass(SessionProductClass):SessionProduct");
    }

    public SessionProduct findATMNearestTermByProductClass(int timeout, SessionProductClass productClass)
            throws DataValidationException, AuthorizationException, CommunicationException, SystemException,
            NotFoundException
    {
        throw getUnsupportedException("findATMNearestTermByProductClass(long, SessionProductClass):SessionProduct");
    }

    public SessionProduct findATMNearestTermByReportingClass(SessionReportingClass sessionReportingClass)
            throws DataValidationException, AuthorizationException, CommunicationException, SystemException,
            NotFoundException
    {
        throw getUnsupportedException("findATMNearestTermByReportingClass(SessionReportingClass):SessionProduct");
    }

    public SessionProduct findATMNearestTermByReportingClass(int timeout, SessionReportingClass sessionReportingClass)
            throws DataValidationException, AuthorizationException, CommunicationException, SystemException,
            NotFoundException
    {
        throw getUnsupportedException("findATMNearestTermByReportingClass(long, SessionReportingClass):SessionProduct");
    }

    public SessionProduct findATMNearestTermSessionProduct(Price atmPrice, SessionProduct[] products)
    {
        throw getUnsupportedException("findATMNearestTermSessionProduct(Price, SessionProduct[]):SessionProduct");
    }

    public SessionProduct findATMSessionProduct(Price atmPrice, SessionProduct[] products,
                                                Comparator comparator, Calendar nearestTermMonth)
    {
        throw getUnsupportedException("findATMSessionProduct(Price, SessionProduct[], Comparator, Calendar):SessionProduct");
    }

    public ProductAdjustmentContainer[] getAllPendingAdjustments()
            throws SystemException, CommunicationException, AuthorizationException
    {
        throw getUnsupportedException("getAllPendingAdjustments():ProductAdjustmentContainer[]");
    }

    public ProductClass getClassBySymbol(short productType, String className)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException,
            NotFoundException
    {
        throw getUnsupportedException("getClassBySymbol(short, String):ProductClass");
    }

    public SessionProductClass getClassBySymbolForSession(String sessionName, short productType, String className)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        throw getUnsupportedException("getClassBySymbolForSession(String, short, String):SessionProductClass");
    }

    public PendingNameContainer[] getPendingAdjustmentProducts(int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("getPendingAdjustmentProducts(int):PendingNameContainer[]");
    }

    public ProductAdjustmentContainer[] getPendingAdjustments(int classKey, boolean includeProducts)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("getPendingAdjustments(int, boolean):ProductAdjustmentContainer[]");
    }

    public Product getProductByName(ProductNameStruct productName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        throw getUnsupportedException("getProductByName(ProductNameStruct):Product");
    }

    public SessionProduct getProductByNameForSession(String sessionName, ProductNameStruct productName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        throw getUnsupportedException("getProductByNameForSession(String, ProductNameStruct):SessionProduct");
    }

    public ProductClass[] getProductClassesByAlpha(String prefix)
    {
        throw getUnsupportedException("getProductClassesByAlpha(String):ProductClass[]");
    }

    public ProductNameStruct getProductNameStruct(int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        throw getUnsupportedException("getProductNameStruct(int):ProductNameStruct");
    }

    public Product[] getProductsForReportingClass(ReportingClass reportingClass, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("getProductsForReportingClass(ReportingClass, boolean):Product[]");
    }

    public SessionProduct[] getProductsForReportingClass(SessionReportingClass reportingClass)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("getProductsForReportingClass(SessionReportingClass):SessionProduct[]");
    }

    public ReportingClass getReportingClassByKey(int reportingClassKey) throws NotFoundException
    {
        throw getUnsupportedException("getReportingClassByKey(int):ReportingClass");
    }

    public ReportingClass getReportingClassBySymbol(String symbol) throws NotFoundException
    {
        throw getUnsupportedException("getReportingClassBySymbol(String):ReportingClass");
    }

    public Product getProductBySymbolAndOpraCode(String reportingClassSymbol, char monthCode, char priceCode) 
            throws NotFoundException, UserException
    {
        throw getUnsupportedException("getProductBySymbolAndOpraCode(String):Product");
    }

    public SessionReportingClass getReportingClassByKeyForSession(int reportingClassKey, String sessionName)
            throws NotFoundException, DataValidationException
    {
        throw getUnsupportedException("getReportingClassByKeyForSession(int, String):SessionReportingClass");
    }

    public SessionStrategy[] getStrategiesByComponent(String sessionName, int componentProductKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("getStrategiesByComponent(String, int):SessionStrategy[]");
    }

    public boolean isValidProductName(ProductNameStruct productName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("isValidProductName(ProductNameStruct):boolean");
    }

    public void subscribeProductsForSession(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("subscribeProductsForSession(String, int, EventChannelListener)");
    }

    public void subscribeSessionClassByType(String sessionName, short productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("subscribeSessionClassByType(String, short, EventChannelListener)");
    }

    public void unsubscribeClassesByTypeForSession(String sessionName, short productType,
                                                   EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("unsubscribeClassesByTypeForSession(String, short, EventChannelListener)");
    }

    public void unsubscribeProductsByClassForSession(String sessionName, int classKey,
                                                     EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("unsubscribeProductsByClassForSession(String, int, EventChannelListener)");
    }

    public void unsubscribeStrategiesByClassForSession(String sessionName, int classKey,
                                                       EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("unsubscribeStrategiesByClassForSession(String, int, EventChannelListener)");
    }

    public void unsubscribeTradingSessionStatus(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("unsubscribeTradingSessionStatus(EventChannelListener)");
    }

    public AdminServiceClientAsync getAdminService()
    {
        return this.adminServiceClientAsync;
    }

    /****************** No implements for ProductQueryAPI *******************************/
    public void subscribeTradingSessions(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw getUnsupportedException("subscribeTradingSessions(EventChannelListener)");
    }

    private UnsupportedOperationException getUnsupportedException(String methodNotSupported)
    {
        return new UnsupportedOperationException("The method " + methodNotSupported + " is not supported by this API.");
    }

    private void loadProcessCaches(ProcessInfoModel[] watchedProcessList)
    {
        synchronized(PROCESS_LOCK_OBJECT)
        {
            for( int i = 0; i < watchedProcessList.length; i++ )
            {
                ProcessInfoModel processInfoModel = watchedProcessList[i];
                addProcessInfoToCache(processInfoModel);
            }
        }
    }

    private String findFirstAvailableSACAS()
    {
        return processCache.findFirstAvailableSACAS();
    }


    private void incrementHeapInstrumentorListenerCount()
    {
        int heapCounter;
        synchronized( COLLECTOR_LOCK_OBJECT )
        {
            heapCounter = heapCollectorSubscriptionCount++;
        }
        if(heapCounter == 0)
        {
            // subscribe for heap
            InstrumentorCollectorHome.find().subscribeAllOrbsByType(InstrumentorTypes.HEAP);
        }
    }

    private void decrementHeapInstrumentorListenerCount()
    {
        int heapCounter;
        synchronized( COLLECTOR_LOCK_OBJECT )
        {
            heapCounter = heapCollectorSubscriptionCount--;
        }
        if(heapCounter == 0)
        {
            // unsubscribe for heap
            InstrumentorCollectorHome.find().unsubscribeAllOrbsByType(InstrumentorTypes.HEAP);
        }
    }
    private void incrementInstrumentorListenerCount()
    {
        int counter;
        synchronized( COLLECTOR_LOCK_OBJECT )
        {
            counter = collectorSubscriptionCount++;
        }
        if( counter == 0 )
        {
            // Subscribe for all types except Heap Instrumentors.
            // Heap instrumentors are handled in a separate method
            InstrumentorCollectorHome.find().subscribeAllOrbsByType(InstrumentorTypes.METHOD);
            InstrumentorCollectorHome.find().subscribeAllOrbsByType(InstrumentorTypes.NETWORK_CONNECTION);
            InstrumentorCollectorHome.find().subscribeAllOrbsByType(InstrumentorTypes.QUEUE);
            InstrumentorCollectorHome.find().subscribeAllOrbsByType(InstrumentorTypes.THREAD);
            InstrumentorCollectorHome.find().subscribeAllOrbsByType(InstrumentorTypes.KEY_VALUE);
        }
        incrementHeapInstrumentorListenerCount();
    }

    private void decrementInstrumentorListenerCount()
    {
        int counter;
        synchronized( COLLECTOR_LOCK_OBJECT )
        {
            counter = collectorSubscriptionCount--;
        }
        if( counter == 0 )
        {
            // Unsubscribe for all types except Heap Instrumentors.
            // Heap instrumentors are handled in a separate method
            InstrumentorCollectorHome.find().unsubscribeAllOrbsByType(InstrumentorTypes.METHOD);
            InstrumentorCollectorHome.find().unsubscribeAllOrbsByType(InstrumentorTypes.NETWORK_CONNECTION);
            InstrumentorCollectorHome.find().unsubscribeAllOrbsByType(InstrumentorTypes.QUEUE);
            InstrumentorCollectorHome.find().unsubscribeAllOrbsByType(InstrumentorTypes.THREAD);
            InstrumentorCollectorHome.find().unsubscribeAllOrbsByType(InstrumentorTypes.KEY_VALUE);
        }
        decrementHeapInstrumentorListenerCount();
    }

    protected void createAndThrowException(GIUserExceptionType userExceptionType) throws UserException
    {
        try
        {
            Class exceptionClass = Class.forName(userExceptionType.getClassName());
            if( exceptionClass.getSuperclass() == UserException.class ) // a user exception
            {
                // find the constructor with a String as its parameter
                Constructor constructor = exceptionClass.getConstructor(new Class[] { String.class });
                // instantiate a new exception
                Object exceptionObject = constructor.newInstance(new Object[] { getReason(userExceptionType) });
                UserException userException = (UserException) exceptionObject;
                // find the details field
                Field detailsField = null;
                Field[] fields = exceptionClass.getFields();
                for (int i = 0; i < fields.length; i++)
                {
                    Field field = fields[i];
                    if(field.getType() == ExceptionDetails.class)
                    {
                        detailsField = field;
                        break;
                    }
                }
                if(detailsField != null && userExceptionType.getDetail() != null)
                {
                    ExceptionDetails exceptionDetails = new ExceptionDetails();
                    exceptionDetails.message = userExceptionType.getDetail().getMessage();
                    exceptionDetails.dateTime = userExceptionType.getDetail().getDateTime();
                    exceptionDetails.error = userExceptionType.getDetail().getError();
                    exceptionDetails.severity = userExceptionType.getDetail().getSeverity();
                    detailsField.set(userException, exceptionDetails);
                }

                throw userException;
            }
            else if( exceptionClass.getSuperclass() == Throwable.class)
            {
                // find the constructor with a String as its parameter
                Constructor constructor = exceptionClass.getConstructor(new Class[] { String.class });
                // instantiate a new exception
                Object exceptionObject = constructor.newInstance(new Object[] { getReason(userExceptionType) });
                Throwable throwable = (Throwable) exceptionObject;
                SystemException se = ExceptionBuilder.systemException(getReason(userExceptionType), 0);
                se.initCause(throwable);
                throw se;
            }
            else // not a throwable??? this should never happen
            {
                throw ExceptionBuilder.systemException(getReason(userExceptionType), 0);
            }
        }
        catch (ClassNotFoundException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (InstantiationException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (IllegalAccessException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (NoSuchMethodException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (InvocationTargetException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
    }

    protected String getReason(GIUserExceptionType userExceptionType)
    {
        String reason = userExceptionType.getMessage();
        String stackTrace = userExceptionType.getStackTraceText() ;
        StringBuffer reasonBuffer = new StringBuffer(reason.length());
        if(reason == null || reason.length() == 0)
        {
            reasonBuffer.append(reason);
        }
        if( stackTrace != null && stackTrace.length()>0)
        {
            reasonBuffer.append("\n");
            reasonBuffer.append("--------------- linked to ------------------");
            reasonBuffer.append("\n");
            reasonBuffer.append(stackTrace);
            reasonBuffer.append("\n");
        }
        return reasonBuffer.toString();
    }

    /**
     * Gets an unmarshalled object from the XML
     * @param responseXml XML text to be unmarshalled
     * @return Object representing the XML
     * @throws UserException thrown if the XML represents an Exception
     */
    protected Object getXmlBindingObject(String responseXml) throws UserException
    {
        Object object = XmlBindingFacade.getInstance().unmarshallXmlString(responseXml);
        if(object == null)
        {
            // this is not a known response xml
            GUILoggerHome.find().alarm("Unknown Response", responseXml);
            throw ExceptionBuilder.systemException("Unknown Response", 0);
        }
        else if(object instanceof GIUserExceptionType)
        {
            createAndThrowException((GIUserExceptionType) object);
        }
        return object;
    }

    public StorageManager getStorageManager()
    {
        return storageManager;
    }

    public String[] getFirmNames()
    {
        return processCache.getFirmNames();
    }

    public void subscribeStatusMonitor(EventChannelListener listener)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeStatusMonitor",
                    GUILoggerINBusinessProperty.INSTRUMENTATION, argObj);
        }

        if (listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        Integer key = new Integer(0);
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_INSTRUMENTATION_STATUS_UPDATE, key);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_COLLECTOR_STATUS_UPDATE, key);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeStatusMonitor(EventChannelListener listener)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeStatusMonitor",
                    GUILoggerINBusinessProperty.INSTRUMENTATION, argObj);
        }

        if (listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        Integer key = new Integer(0);
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_INSTRUMENTATION_STATUS_UPDATE, key);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_COLLECTOR_STATUS_UPDATE, key);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    private void subscribeCollectorDetailsViaAdmin(String orbName)
    {
        // Build command or get it from the factory
        String[] collectorOrbNames = getCollectorOrbNames();
        String[] collectorProcessNames = new String[collectorOrbNames.length];

        String[] dataItems = new String[2];
        dataItems[0] = orbName;
        dataItems[1] = getMyOrbName();

        for (int i=0;i < collectorOrbNames.length;i++)
        {
            boolean isUp = false;
            try
            {
                CBOEProcess process = getProcess(collectorOrbNames[i], null);
                collectorProcessNames[i] = process.getProcessName();
                if (process.getOnlineStatus() == Status.UP)
                {
                    isUp = true;
                }
            }
            catch(DataValidationException dve)
            {
                // did not find the process name, so try to use the original name.
            }
            //Get data from the AdminService, if the collector is up
            if (isUp)
            {
                String commandName = SystemMonitorCommandMethodNames.SUBSCRIBE_DETAILS;
                ARCommand arCommand =
                        CommandFactory.getInstance().getCommand(collectorOrbNames[i], commandName);
                Command executeCommand = arCommand.buildExecuteCommand(dataItems);

                ExecuteCommandService service = ARCommandServiceFactory.getExecuteCommandService();
                service.setDestination(collectorProcessNames[i]);

                try
                {
                    service.executeCommand(executeCommand);
                }
                catch (Exception e)
                {
                    if (GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().exception("InstrumentationTranslatorImpl  Error sending details subscription to collector",e);
                    }
                    GUILoggerHome.find().alarm("InstrumentationTranslatorImpl  Error sending details subscription to collector");
                }
            }
        }
    }

    private void unsubscribeCollectorDetailsViaAdmin(String orbName)
    {
        // Build command or get it from the factory
        String[] collectorOrbNames = getCollectorOrbNames();
        String[] collectorProcessNames = new String[collectorOrbNames.length];

        String[] dataItems = new String[2];
        dataItems[0] = orbName;
        dataItems[1] = getMyOrbName();

        for (int i=0;i < collectorOrbNames.length;i++)
        {
	    boolean isUp = false;
            try
            {
                CBOEProcess process = getProcess(collectorOrbNames[i], null);
                collectorProcessNames[i] = process.getProcessName();
                if (process.getOnlineStatus() == Status.UP)
                {
                    isUp = true;
                }
            }
            catch(DataValidationException dve)
            {
                // did not find the process name, so try to use the original name.
            }
            if (isUp)
            {
                String commandName = SystemMonitorCommandMethodNames.UNSUBSCRIBE_DETAILS;
                ARCommand arCommand =
                        CommandFactory.getInstance().getCommand(collectorOrbNames[i], commandName);
                Command executeCommand = arCommand.buildExecuteCommand(dataItems);

                ExecuteCommandService service = ARCommandServiceFactory.getExecuteCommandService();
                service.setDestination(collectorProcessNames[i]);

                try
                {
                    service.executeCommand(executeCommand);
                }
                catch (Exception e)
                {
                    if (GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().exception("InstrumentationTranslatorImpl  Error sending details subscription to collector",e);
                    }
                    GUILoggerHome.find().alarm("InstrumentationTranslatorImpl  Error sending details subscription to collector");
                }
            }
        }
    }

    private String[] getCollectorOrbNames()
    {
        String[] orbNames;

        orbNames = InstrumentorCollectorHome.find().getCollectorOrbNames();

        return orbNames;
    }

    private String getMyOrbName()
    {
        if (myOrbName == null)
        {
            myOrbName = System.getProperty(ORBNAME_PROPERTY);
        }

        return myOrbName;
    }

    public AlarmCondition[] getAllAlarmConditions()
            throws SystemException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllAlarmConditions" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CONDITION);
        }

        AlarmCondition[] conditions = alarmsCache.getAllAlarmConditions();

        if(conditions == null || conditions.length == 0)
        {
            AlarmsAPI api = new AlarmsSynchEventChannelAPI(alarmPublisher, getAdminServiceDefaultTimeout());

            try
            {
                AlarmConditionStruct[] structs = api.publishAllConditions();
                if(structs != null)
                {
                    conditions = new AlarmCondition[structs.length];

                    for(int i = 0; i < structs.length; i++)
                    {
                        AlarmConditionStruct struct = structs[ i ];
                        AlarmCondition condition = AlarmConditionFactory.createAlarmCondition(struct);
                        conditions[i] = condition;
                    }
                }
                else
                {
                    conditions = new AlarmCondition[0];
                }
            }
            catch(TimedOutException e)
            {
                SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
                systemException.initCause(e);
                throw systemException;
            }
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            if(conditions != null && conditions.length > 0)
            {
                Object[] argObj = new Object[1];
                for(int i = 0; i < conditions.length; i++)
                {
                    AlarmCondition condition = conditions[i];
                    argObj[0] = condition.getStruct();
                    GUILoggerHome.find().debug(CATEGORY + ": getAllAlarmConditions" + ":exit",
                                               GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
                }
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAllAlarmConditions" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CONDITION,
                                           "conditions were null or zero length");
            }
        }

        return conditions;
    }

    public AlarmCondition getAlarmConditionById(int conditionId)
            throws NotFoundException, SystemException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = new Integer(conditionId);

            GUILoggerHome.find().debug(CATEGORY + ": getAlarmConditionById" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
        }

        AlarmCondition condition = alarmsCache.findAlarmConditionById(new Integer(conditionId));
        if(condition == null)
        {
            AlarmsAPI api = new AlarmsSynchEventChannelAPI(alarmPublisher, getAdminServiceDefaultTimeout());

            try
            {
                AlarmConditionStruct struct = api.publishConditionById(conditionId);
                if(struct != null)
                {
                    condition = AlarmConditionFactory.createAlarmCondition(struct);
                }
                else
                {
                    throw ExceptionBuilder.systemException("Alarm Condition Id:" + conditionId + " not found by" +
                                                           "cache or API. However API returned NULL.", 0);
                }
            }
            catch(TimedOutException e)
            {
                SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
                systemException.initCause(e);
                throw systemException;
            }
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            if(condition != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = condition.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmConditionById" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmConditionById" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CONDITION,
                                           "condition was null");
            }
        }

        return condition;
    }

    public AlarmCondition[] getAlarmConditionsByName(String name)
            throws NotFoundException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = name;

            GUILoggerHome.find().debug(CATEGORY + ": getAlarmConditionsByName" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
        }

        AlarmCondition[] conditions = alarmsCache.findAlarmConditionsByName(name);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            if(conditions != null && conditions.length > 0)
            {
                Object[] argObj = new Object[1];
                for(int i = 0; i < conditions.length; i++)
                {
                    AlarmCondition condition = conditions[i];
                    argObj[0] = condition.getStruct();
                    GUILoggerHome.find().debug(CATEGORY + ": getAlarmConditionsByName" + ":exit",
                                               GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
                }
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmConditionsByName" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CONDITION,
                                           "conditions were null or zero length");
            }
        }

        if(conditions == null || conditions.length == 0)
        {
            throw ExceptionBuilder.notFoundException("Alarm Conditions do not exist for name:" + name,
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        else
        {
            return conditions;
        }
    }

    public AlarmCondition[] getAllConditionsForCalculation(AlarmCalculation calculation)
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = calculation.getStruct();
            GUILoggerHome.find().debug(CATEGORY + ": getAllConditionsForCalculation" + ":entry",
                    GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
        }

        AlarmCondition[] conditions = alarmsCache.findAlarmConditionsForCalculation(calculation);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            if (conditions != null && conditions.length > 0)
            {
                Object[] argObj = new Object[1];
                for (AlarmCondition condition : conditions)
                {
                    argObj[0] = condition.getStruct();
                    GUILoggerHome.find().debug(CATEGORY + ": getAllConditionsForCalculation" + ":exit",
                            GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
                }
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAllConditionsForCalculation" + ":exit",
                        GUILoggerINBusinessProperty.ALARM_CONDITION,
                        "conditions were null or zero length");
            }
        }

        return conditions;
    }

    public AlarmCondition addAlarmCondition(AlarmCondition condition)
            throws SystemException, CommunicationException, AlreadyExistsException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = condition.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": addAlarmCondition" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
        }

        AlarmCondition addedCondition = alarmsCache.addAlarmCondition(condition);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            if(addedCondition != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = addedCondition.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": addAlarmCondition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": addAlarmCondition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CONDITION,
                                           "addedCondition was null");
            }
        }

        return addedCondition;
    }

    public AlarmCondition removeAlarmCondition(AlarmCondition condition)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = condition.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": removeAlarmCondition" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
        }

        AlarmCondition removedCondition = alarmsCache.removeAlarmCondition(condition);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            if(removedCondition != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = removedCondition.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": removeAlarmCondition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": removeAlarmCondition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CONDITION,
                                           "removedCondition was null");
            }
        }

        return removedCondition;
    }

    public AlarmCondition updateAlarmCondition(AlarmCondition condition)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = condition.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": updateAlarmCondition" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
        }

        AlarmCondition updatedCondition = alarmsCache.updateAlarmCondition(condition);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            if(updatedCondition != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = updatedCondition.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": updateAlarmCondition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": updateAlarmCondition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CONDITION,
                                           "updatedCondition was null");
            }
        }

        return updatedCondition;
    }

    public AlarmCalculation[] getAllAlarmCalculations()
            throws SystemException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllAlarmCalculations" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CALCULATION);
        }

        AlarmCalculation[] calculations = alarmsCache.getAllAlarmCalculations();

        if(calculations == null || calculations.length == 0)
        {
            AlarmsAPI api = new AlarmsSynchEventChannelAPI(alarmPublisher, getAdminServiceDefaultTimeout());

            try
            {
                AlarmCalculationStruct[] structs = api.publishAllCalculations();
                if(structs != null)
                {
                    calculations = new AlarmCalculation[structs.length];

                    for(int i = 0; i < structs.length; i++)
                    {
                        AlarmCalculationStruct struct = structs[ i ];
                        AlarmCalculation calculation = AlarmCalculationFactory.createAlarmCalculation(struct);
                        calculations[i] = calculation;
                    }
                }
                else
                {
                    calculations = ZERO_LENGTH_ALARM_CALCULATION;
                }
            }
            catch(TimedOutException e)
            {
                SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
                systemException.initCause(e);
                throw systemException;
            }
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            if(calculations != null && calculations.length > 0)
            {
                Object[] argObj = new Object[1];
                for (AlarmCalculation calculation : calculations)
                {
                    argObj[0] = calculation.getStruct();
                    GUILoggerHome.find().debug(CATEGORY + ": getAllAlarmCalculations" + ":exit",
                            GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
                }
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAllAlarmCalculations" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CALCULATION,
                                           "calculations were null or zero length");
            }
        }

        return calculations;
    }

    public AlarmCalculation getAlarmCalculationById(int calculationId)
            throws NotFoundException, SystemException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = calculationId;
            GUILoggerHome.find().debug(CATEGORY + ": getAlarmCalculationById" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
        }

        AlarmCalculation calculation = alarmsCache.findAlarmCalculationById(calculationId);
        if(calculation == null)
        {
            AlarmsAPI api = new AlarmsSynchEventChannelAPI(alarmPublisher, getAdminServiceDefaultTimeout());

            try
            {
                AlarmCalculationStruct struct = api.publishCalculationById(calculationId);
                if(struct != null)
                {
                    calculation = AlarmCalculationFactory.createAlarmCalculation(struct);
                }
                else
                {
                    throw ExceptionBuilder.systemException("Alarm Calculation Id:" + calculationId + " not found by" +
                                                           "cache or API. However API returned NULL.", 0);
                }
            }
            catch(TimedOutException e)
            {
                SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
                systemException.initCause(e);
                throw systemException;
            }
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            if(calculation != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = calculation.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmCalculationById" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmCalculationById" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CALCULATION,
                                           "calculation was null");
            }
        }

        return calculation;
    }

    public AlarmCalculation getAlarmCalculationByName(String name)
            throws NotFoundException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAlarmCalculationsByName" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CALCULATION, new Object[] {name});
        }

        AlarmCalculation calculation = alarmsCache.findAlarmCalculationsByName(name);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            if(calculation != null)
            {
                    GUILoggerHome.find().debug(CATEGORY + ": getAlarmCalculationsByName" + ":exit",
                            GUILoggerINBusinessProperty.ALARM_CALCULATION, new Object[]{calculation.getStruct()});
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmCalculationsByName" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CALCULATION,
                                           "calculation was null");
            }
        }

        if(calculation == null)
        {
            throw ExceptionBuilder.notFoundException("Alarm Calculation does not exist for name:" + name,
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        else
        {
            return calculation;
        }
    }

    public AlarmCalculation addAlarmCalculation(AlarmCalculation calculation)
            throws SystemException, CommunicationException, AlreadyExistsException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = calculation.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": addAlarmCalculation" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
        }

        AlarmCalculation addedCalculation = alarmsCache.addAlarmCalculation(calculation);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            if(addedCalculation != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = addedCalculation.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": addAlarmCalculation" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": addAlarmCalculation" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CALCULATION,
                                           "addedCalculation was null");
            }
        }

        return addedCalculation;
    }

    public AlarmCalculation removeAlarmCalculation(AlarmCalculation calculation)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = calculation.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": removeAlarmCalculation" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
        }

        AlarmCalculation removedCalculation = alarmsCache.removeAlarmCalculation(calculation);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            if(removedCalculation != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = removedCalculation.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": removeAlarmCalculation" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": removeAlarmCalculation" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CALCULATION,
                                           "removedCalculation was null");
            }
        }

        return removedCalculation;
    }

    public AlarmCalculation updateAlarmCalculation(AlarmCalculation calculation)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = calculation.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": updateAlarmCalculation" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
        }

        AlarmCalculation updatedCalculation = alarmsCache.updateAlarmCalculation(calculation);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            if(updatedCalculation != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = updatedCalculation.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": updateAlarmCalculation" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": updateAlarmCalculation" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_CALCULATION,
                                           "updatedCalculation was null");
            }
        }

        return updatedCalculation;
    }

    public AlarmDefinition[] getAllAlarmDefinitions()
            throws SystemException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllAlarmDefinitions" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION);
        }

        AlarmDefinition[] definitions = alarmsCache.getAllAlarmDefinitions();

        if(definitions == null || definitions.length == 0)
        {
            AlarmsAPI api = new AlarmsSynchEventChannelAPI(alarmPublisher, getAdminServiceDefaultTimeout());

            try
            {
                AlarmDefinitionStruct[] structs = api.publishAllDefinitions();
                if(structs != null)
                {
                    definitions = new AlarmDefinition[ structs.length ];

                    for(int i = 0; i < structs.length; i++)
                    {
                        AlarmDefinitionStruct struct = structs[ i ];
                        AlarmDefinition definition = AlarmDefinitionFactory.createAlarmDefinition(struct);
                        definitions[ i ] = definition;
                    }
                }
                else
                {
                    definitions = new AlarmDefinition[0];
                }
            }
            catch(TimedOutException e)
            {
                SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
                systemException.initCause(e);
                throw systemException;
            }
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            if(definitions != null && definitions.length > 0)
            {
                Object[] argObj = new Object[1];
                for(int i = 0; i < definitions.length; i++)
                {
                    AlarmDefinition definition = definitions[i];
                    argObj[0] = definition.getStruct();
                    GUILoggerHome.find().debug(CATEGORY + ": getAllAlarmDefinitions" + ":exit",
                                               GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
                }
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAllAlarmDefinitions" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_DEFINITION,
                                           "definitions were null or zero length");
            }
        }

        return definitions;
    }

    public AlarmDefinition getAlarmDefinitionById(int definitionId)
            throws NotFoundException, SystemException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = new Integer(definitionId);

            GUILoggerHome.find().debug(CATEGORY + ": getAlarmDefinitionById" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
        }

        AlarmDefinition definition = alarmsCache.findAlarmDefinitionById(new Integer(definitionId));
        if(definition == null)
        {
            AlarmsAPI api = new AlarmsSynchEventChannelAPI(alarmPublisher, getAdminServiceDefaultTimeout());

            try
            {
                AlarmDefinitionStruct struct = api.publishDefinitionById(definitionId);
                if(struct != null)
                {
                    definition = AlarmDefinitionFactory.createAlarmDefinition(struct);
                }
                else
                {
                    throw ExceptionBuilder.systemException("Alarm Definition Id:" + definitionId + " not found by" +
                                                           "cache or API. However API returned NULL.", 0);
                }
            }
            catch(TimedOutException e)
            {
                SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
                systemException.initCause(e);
                throw systemException;
            }
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            if(definition != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = definition.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmDefinitionById" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmDefinitionById" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_DEFINITION,
                                           "definition was null");
            }
        }

        return definition;
    }

    public AlarmDefinition[] getAlarmDefinitionsByName(String name)
            throws NotFoundException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = name;

            GUILoggerHome.find().debug(CATEGORY + ": getAlarmDefinitionsByName" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
        }

        AlarmDefinition[] definitions = alarmsCache.findAlarmDefinitionsByName(name);
        if(definitions == null || definitions.length == 0)
        {
            throw ExceptionBuilder.notFoundException("Alarm Definitions do not exist for name:" + name,
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            if(definitions != null && definitions.length > 0)
            {
                Object[] argObj = new Object[1];
                for(int i = 0; i < definitions.length; i++)
                {
                    AlarmDefinition definition = definitions[i];
                    argObj[0] = definition.getStruct();
                    GUILoggerHome.find().debug(CATEGORY + ": getAlarmDefinitionsByName" + ":exit",
                                               GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
                }
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmDefinitionsByName" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_DEFINITION,
                                           "definitions were null or zero length");
            }
        }

        return definitions;
    }

    public AlarmDefinition[] getAllDefinitionsForCondition(AlarmCondition condition)
            throws DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = condition.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": getAllDefinitionsForCondition" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
        }

        AlarmDefinition[] definitions = alarmsCache.findAlarmDefinitionsForCondition(condition);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            if(definitions != null && definitions.length > 0)
            {
                Object[] argObj = new Object[1];
                for(int i = 0; i < definitions.length; i++)
                {
                    AlarmDefinition definition = definitions[i];
                    argObj[0] = definition.getStruct();
                    GUILoggerHome.find().debug(CATEGORY + ": getAllDefinitionsForCondition" + ":exit",
                                               GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
                }
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAllDefinitionsForCondition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_DEFINITION,
                                           "definitions were null or zero length");
            }
        }

        return definitions;
    }

    @SuppressWarnings({"OverlyLongMethod"})
    public AlarmDefinition addAlarmDefinition(AlarmDefinitionMutable definition)
            throws SystemException, CommunicationException, AlreadyExistsException, DataValidationException,
                   NotAcceptedException, TransactionFailedException, NotFoundException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = definition.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": addAlarmDefinition" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
        }

        AlarmDefinitionMutable addedDefinition;

        AlarmConditionMutable[] conditions = definition.getAllMutableConditions();
        for(AlarmConditionMutable condition : conditions)
        {
            if(!condition.isSaved())
            {
                AlarmCondition addedCondition = addAlarmCondition(condition);
                definition.removeCondition(condition);
                definition.addCondition(addedCondition);
            }
            else if(condition.isModified())
            {
                AlarmCondition updatedCondition = updateAlarmCondition(condition);
                definition.updateCondition(updatedCondition);
            }
        }

        AlarmActivationMutable[] activations = definition.getAllMutableActivations();

        addedDefinition = (AlarmDefinitionMutable) alarmsCache.addAlarmDefinition(definition);

        if(addedDefinition != null)
        {
            for(AlarmActivationMutable activation : activations)
            {
                try
                {
                    AlarmActivationMutable clonedActivation = (AlarmActivationMutable) activation.clone();
                    AlarmActivationStruct struct = clonedActivation.getStruct();
                    struct.definition = addedDefinition.getStruct();
                    clonedActivation.setStruct(struct);

                    AlarmActivation addedActivation = alarmsCache.addAlarmActivation(clonedActivation);
                    addedDefinition.removeActivation(activation);
                    addedDefinition.addActivation(addedActivation);
                }
                catch(CloneNotSupportedException e)
                {
                    DefaultExceptionHandlerHome.find().process(e);
                }
            }
        }
        else
        {
            throw ExceptionBuilder.systemException("API to create Alarm Definition returned NULL.", 0);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            //noinspection ConstantConditions
            if(addedDefinition != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = addedDefinition.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": addAlarmDefinition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": addAlarmDefinition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_DEFINITION,
                                           "addedDefinition was null");
            }
        }

        return addedDefinition;
    }

    public AlarmDefinition removeAlarmDefinition(AlarmDefinition definition)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            Object[] argObj = {definition.getStruct()};

            GUILoggerHome.find().debug(CATEGORY + ": removeAlarmDefinition" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
        }

        AlarmDefinition removedDefinition = alarmsCache.findAlarmDefinitionById(definition.getId());

        if(removedDefinition != null)
        {
            AlarmActivation[] activations = removedDefinition.getAllActivations();
            for(int i = 0; i < activations.length; i++)
            {
                AlarmActivation activation = activations[i];
                if(activation.isSaved())
                {
                    try
                    {
                        AlarmNotificationWatchdog watchdog = getWatchdogForActivationId(activation.getId());
                        removeWatchdog(watchdog);
                    }
                    catch(NotFoundException e)
                    {
                        //normal exception expected
                    }

                    alarmsCache.removeAlarmActivation(activation);
                }
            }

            alarmsCache.removeAlarmDefinition(removedDefinition);
        }
        else
        {
            throw ExceptionBuilder.notFoundException("Alarm definition with definition Id:" +
                                                     definition.getId() + ", could not be found.",
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            if(removedDefinition != null)
            {
                Object[] argObj = {removedDefinition.getStruct()};
                GUILoggerHome.find().debug(CATEGORY + ": removeAlarmDefinition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": removeAlarmDefinition" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_DEFINITION,
                                           "removedDefinition was null");
            }
        }

        return removedDefinition;
    }

    public AlarmDefinition updateAlarmDefinition(AlarmDefinitionMutable definition)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = definition.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": updateAlarmDefinition" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
        }

        AlarmDefinitionMutable updatedDefinition;

        //used for reference below
        AlarmDefinition originalDefinition = alarmsCache.findAlarmDefinitionById(definition.getId());
        if(originalDefinition != null)
        {
            //persist any conditions that are needed
            AlarmConditionMutable[] conditions = definition.getAllMutableConditions();
            for(int i = 0; i < conditions.length; i++)
            {
                AlarmConditionMutable condition = conditions[ i ];
                if(!condition.isSaved())
                {
                    AlarmCondition addedCondition = addAlarmCondition(condition);
                    definition.removeCondition(condition);
                    definition.addCondition(addedCondition);
                }
                else if(condition.isModified())
                {
                    AlarmCondition updatedCondition = updateAlarmCondition(condition);
                    definition.updateCondition(updatedCondition);
                }
            }

            //get current activations before sending definition to be saved
            AlarmActivationMutable[] activations = definition.getAllMutableActivations();

            //cleanup any old activations
            AlarmActivation[] originalActivations = originalDefinition.getAllActivations();
            for(int i = 0; i < originalActivations.length; i++)
            {
                AlarmActivation originalActivation = originalActivations[i];
                boolean foundInCurrentActivations = false;
                for(int j = 0; j < activations.length; j++)
                {
                    AlarmActivationMutable activation = activations[j];
                    if(originalActivation.getId().equals(activation.getId()))
                    {
                        foundInCurrentActivations = true;
                        break;
                    }
                }
                if(!foundInCurrentActivations)
                {
                    alarmsCache.removeAlarmActivation(originalActivation);
                }
            }

            //persist definition
            if(definition.isModified())
            {
                updatedDefinition = (AlarmDefinitionMutable) alarmsCache.updateAlarmDefinition(definition);
            }
            else
            {
                updatedDefinition = definition;
            }

            //update an current activations to reflect the new definition
            if(updatedDefinition != null)
            {
                for(int i = 0; i < activations.length; i++)
                {
                    AlarmActivationMutable activation = activations[ i ];

                    if(!activation.isSaved())
                    {
                        AlarmActivationStruct struct = activation.getStruct();
                        struct.definition = updatedDefinition.getStruct();
                        activation.setStruct(struct);

                        AlarmActivation addedActivation = alarmsCache.addAlarmActivation(activation);
                        updatedDefinition.removeActivation(activation);
                        updatedDefinition.addActivation(addedActivation);
                    }
                    else if(activation.isModified())
                    {
                        AlarmActivationStruct struct = activation.getStruct();
                        struct.definition = updatedDefinition.getStruct();
                        activation.setStruct(struct);

                        AlarmActivation updatedActivation = alarmsCache.updateAlarmActivation(activation);
                        updatedDefinition.updateActivation(updatedActivation);
                    }
                }
            }
            else
            {
                throw ExceptionBuilder.systemException("API to update Alarm Definition returned NULL.", 0);
            }

            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
            {
                if(updatedDefinition != null)
                {
                    Object[] argObj = new Object[1];
                    argObj[0] = updatedDefinition.getStruct();
                    GUILoggerHome.find().debug(CATEGORY + ": updateAlarmDefinition" + ":exit",
                                               GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
                }
                else
                {
                    GUILoggerHome.find().debug(CATEGORY + ": updateAlarmDefinition" + ":exit",
                                               GUILoggerINBusinessProperty.ALARM_DEFINITION,
                                               "updatedDefinition was null");
                }
            }
        }
        else
        {
            throw ExceptionBuilder.notFoundException("Alarm definition with definition Id:" +
                                                     definition.getId() + ", could not be found.",
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        return updatedDefinition;
    }

    public AlarmActivation activate(AlarmDefinitionMutable definition, AlarmActivationMutable activation)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = definition.getStruct();
            argObj[1] = activation.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": activate" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_ACTIVATION, argObj);
        }

        AlarmActivation updatedActivation = null;

        try
        {
            updatedActivation = activateAlarmActivation(definition, activation, true);
        }
        catch(AlreadyExistsException e)
        {
            //should never occur, but just in case
            DefaultExceptionHandlerHome.find().process(e, "Exceptions updating Alarm Activation");
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            if(updatedActivation != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = updatedActivation.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": activate" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_ACTIVATION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": activate" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_ACTIVATION,
                                           "updatedActivation was null");
            }
        }

        return updatedActivation;
    }

    public AlarmActivation deactivate(AlarmDefinitionMutable definition, AlarmActivationMutable activation)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = definition.getStruct();
            argObj[1] = activation.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": deactivate" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_ACTIVATION, argObj);
        }

        AlarmActivation updatedActivation = null;

        try
        {
            updatedActivation = activateAlarmActivation(definition, activation, false);
        }
        catch(AlreadyExistsException e)
        {
            //should never occur, but just in case
            DefaultExceptionHandlerHome.find().process(e, "Exceptions updating Alarm Activation");
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            if(updatedActivation != null)
            {
                Object[] argObj = new Object[1];
                argObj[0] = updatedActivation.getStruct();
                GUILoggerHome.find().debug(CATEGORY + ": deactivate" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_ACTIVATION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": deactivate" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_ACTIVATION,
                                           "updatedActivation was null");
            }
        }

        return updatedActivation;
    }

    public AlarmActivation[] getAlarmActivationsForDefinitionId(Integer definitionId)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = definitionId;

            GUILoggerHome.find().debug(CATEGORY + ": getAlarmActivationsForDefinitionId" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_ACTIVATION, argObj);
        }

        AlarmActivation[] activations = alarmsCache.findAlarmActivationsByDefinitionId(definitionId);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            if(activations != null && activations.length > 0)
            {
                Object[] argObj = new Object[1];
                for(int i = 0; i < activations.length; i++)
                {
                    AlarmActivation activation = activations[i];
                    argObj[0] = activation.getStruct();
                    GUILoggerHome.find().debug(CATEGORY + ": getAlarmActivationsForDefinitionId" + ":exit",
                                               GUILoggerINBusinessProperty.ALARM_ACTIVATION, argObj);
                }
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmActivationsForDefinitionId" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_ACTIVATION,
                                           "activations were null or zero length");
            }
        }

        return activations;
    }

    public AlarmActivation getAlarmActivationById(int activationId)
            throws NotFoundException, SystemException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            Object[] argObj = {activationId};

            GUILoggerHome.find().debug(CATEGORY + ": getAlarmActivationById" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_ACTIVATION, argObj);
        }

        AlarmActivation activation = alarmsCache.findAlarmActivationById(activationId);
        if(activation == null)
        {
            AlarmsAPI api = new AlarmsSynchEventChannelAPI(alarmPublisher, getAdminServiceDefaultTimeout());

            try
            {
                AlarmActivationStruct struct = api.publishActivationById(activationId);
                if(struct != null)
                {
                    activation = AlarmActivationFactory.createAlarmActivation(struct);
                }
                else
                {
                    throw ExceptionBuilder.systemException("Alarm Activation Id:" + activationId + " not found by" +
                                                           "cache or API. However API returned NULL.", 0);
                }
            }
            catch(TimedOutException e)
            {
                SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
                systemException.initCause(e);
                throw systemException;
            }
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            if(activation != null)
            {
                Object[] argObj = {activation.getStruct()};
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmActivationById" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_ACTIVATION, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAlarmActivationById" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_ACTIVATION,
                                           "activation was null");
            }
        }
        return activation;
    }

    public AlarmNotificationWatchdog getWatchdogForActivationId(Integer activationId)
            throws NotFoundException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            Object[] argObj = {activationId};

            GUILoggerHome.find().debug(CATEGORY + ": getWatchdogForActivationId" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
        }

        AlarmNotificationWatchdog watchdog = alarmsCache.getWatchdogForActivationId(activationId);
        /*todo
        if(watchdog == null)
        {
            AlarmsAPI api = new AlarmsSynchEventChannelAPI(alarmPublisher, getAdminServiceDefaultTimeout());

            try
            {
                AlarmNotificationWatchdogStruct[] structs = api.publishAllWatchdogs();
                if(structs != null)
                {
                    for(int i = 0; i < structs.length; i++)
                    {
                        if(activationId == structs[i].activationId)
                        {
                            watchdog =
                                AlarmNotificationWatchdogFactory.createAlarmNotificationWatchdog(structs[i]);
                            alarmsCache.addWatchdog(watchdog);
                            break;
                        }
                    }
                }
            }
            catch(UserException e)
            {
            }
            catch(TimedOutException e)
            {
            }
        }
        */

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            if(watchdog != null)
            {
                Object[] argObj = {watchdog.getStruct()};
                GUILoggerHome.find().debug(CATEGORY + ": getWatchdogForActivationId" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getWatchdogForActivationId" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG,
                                           "watchdog was null");
            }
        }

        if(watchdog == null)
        {
            throw ExceptionBuilder.notFoundException("Alarm Notification Watchdog does not exist for Activation Id:" +
                                                     activationId, NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        else
        {
            return watchdog;
        }
    }

    public AlarmNotificationWatchdog[] getAllWatchdogs()
            throws SystemException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllWatchdogs" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG);
        }

        AlarmNotificationWatchdog[] watchdogs = alarmsCache.getAllAlarmWatchdogs();

        if(watchdogs == null || watchdogs.length == 0)
        {
            AlarmsAPI api = new AlarmsSynchEventChannelAPI(alarmPublisher, getAdminServiceDefaultTimeout());

            try
            {
                AlarmNotificationWatchdogStruct[] structs = api.publishAllWatchdogs();
                if(structs != null)
                {
                    watchdogs = new AlarmNotificationWatchdog[structs.length];

                    for(int i = 0; i < structs.length; i++)
                    {
                        AlarmNotificationWatchdogStruct struct = structs[i];
                        AlarmNotificationWatchdog watchdog =
                                AlarmNotificationWatchdogFactory.createAlarmNotificationWatchdog(struct);
                        watchdogs[i] = watchdog;
                    }
                }
                else
                {
                    watchdogs = new AlarmNotificationWatchdog[0];
                }
            }
            catch(TimedOutException e)
            {
                SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
                systemException.initCause(e);
                throw systemException;
            }
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            if(watchdogs != null && watchdogs.length > 0)
            {
                Object[] argObj = new Object[1];
                for(int i = 0; i < watchdogs.length; i++)
                {
                    AlarmNotificationWatchdog watchdog = watchdogs[i];
                    argObj[0] = watchdog.getStruct();
                    GUILoggerHome.find().debug(CATEGORY + ": getAllWatchdogs" + ":exit",
                                               GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
                }
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAllWatchdogs" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG,
                                           "watchdogs were null or zero length");
            }
        }

        return watchdogs;
    }

    public AlarmNotificationWatchdog getWatchdogById(int watchdogId)
            throws NotFoundException, SystemException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            Object[] argObj = {watchdogId};

            GUILoggerHome.find().debug(CATEGORY + ": getWatchdogById" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
        }

        AlarmNotificationWatchdog watchdog = alarmsCache.findAlarmWatchdogById(watchdogId);
        if(watchdog == null)
        {
            AlarmsAPI api = new AlarmsSynchEventChannelAPI(alarmPublisher, getAdminServiceDefaultTimeout());

            try
            {
                AlarmNotificationWatchdogStruct struct = api.publishWatchdogById(watchdogId);
                if(struct != null)
                {
                    watchdog = AlarmNotificationWatchdogFactory.createAlarmNotificationWatchdog(struct);
                }
            }
            catch(TimedOutException e)
            {
                SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
                systemException.initCause(e);
                throw systemException;
            }
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            if(watchdog != null)
            {
                Object[] argObj = {watchdog.getStruct()};
                GUILoggerHome.find().debug(CATEGORY + ": getWatchdogById" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getWatchdogById" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG,
                                           "watchdog was null");
            }
        }

        if(watchdog == null)
        {
            throw ExceptionBuilder.systemException("Alarm Notification Watchdog Id:" + watchdogId + " not found by" +
                                                   "cache or API. However API returned NULL.", 0);
        }
        else
        {
            return watchdog;
        }
    }

    public AlarmNotificationWatchdog addWatchdog(AlarmNotificationWatchdogMutable watchdog)
            throws SystemException, CommunicationException, AlreadyExistsException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            Object[] argObj = {watchdog.getStruct()};

            GUILoggerHome.find().debug(CATEGORY + ": addWatchdog" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
        }

        AlarmNotificationWatchdog addedWatchdog = alarmsCache.addWatchdog(watchdog);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            if(addedWatchdog != null)
            {
                Object[] argObj = {addedWatchdog.getStruct()};
                GUILoggerHome.find().debug(CATEGORY + ": addWatchdog" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": addWatchdog" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG,
                                           "addedWatchdog was null");
            }
        }

        return addedWatchdog;
    }

    public AlarmNotificationWatchdog updateWatchdog(AlarmNotificationWatchdogMutable watchdog)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            Object[] argObj = {watchdog.getStruct()};

            GUILoggerHome.find().debug(CATEGORY + ": updateWatchdog" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
        }

        AlarmNotificationWatchdog updatedWatchdog = alarmsCache.updateWatchdog(watchdog);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            if(updatedWatchdog != null)
            {
                Object[] argObj = {updatedWatchdog.getStruct()};
                GUILoggerHome.find().debug(CATEGORY + ": updateWatchdog" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": updateWatchdog" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG,
                                           "updatedWatchdog was null");
            }
        }

        return updatedWatchdog;
    }

    public AlarmNotificationWatchdog removeWatchdog(AlarmNotificationWatchdog watchdog)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            Object[] argObj = {watchdog.getStruct()};

            GUILoggerHome.find().debug(CATEGORY + ": removeWatchdog" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
        }

        AlarmNotificationWatchdog removedWatchdog = alarmsCache.removeWatchdog(watchdog);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            if(removedWatchdog != null)
            {
                Object[] argObj = {removedWatchdog.getStruct()};
                GUILoggerHome.find().debug(CATEGORY + ": removeWatchdog" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": removeWatchdog" + ":exit",
                                           GUILoggerINBusinessProperty.ALARM_WATCHDOG,
                                           "removedWatchdog was null");
            }
        }

        return removedWatchdog;
    }

    public void subscribeAlarmConditionStatus(EventChannelListener listener)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAlarmConditionStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_CONDITION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CONDITION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeAlarmConditionStatus(EventChannelListener listener)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAlarmConditionStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_CONDITION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CONDITION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeAlarmConditionStatus(EventChannelListener listener, AlarmCondition condition)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = listener;
            argObj[1] = condition.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAlarmConditionStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, condition.getId());
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CONDITION, condition.getId());
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeAlarmConditionStatus(EventChannelListener listener, AlarmCondition condition)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CONDITION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = listener;
            argObj[1] = condition.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAlarmConditionStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CONDITION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, condition.getId());
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CONDITION, condition.getId());
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeAlarmCalculationStatus(EventChannelListener listener)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAlarmCalculationStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_CALCULATION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CALCULATION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CALCULATION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeAlarmCalculationStatus(EventChannelListener listener)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAlarmCalculationStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_CALCULATION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CALCULATION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CALCULATION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeAlarmCalculationStatus(EventChannelListener listener, AlarmCalculation calculation)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = listener;
            argObj[1] = calculation.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAlarmCalculationStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CALCULATION, calculation.getId());
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CALCULATION, calculation.getId());
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeAlarmCalculationStatus(EventChannelListener listener, AlarmCalculation calculation)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_CALCULATION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = listener;
            argObj[1] = calculation.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAlarmCalculationStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_CALCULATION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CALCULATION, calculation.getId());
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CALCULATION, calculation.getId());
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeAlarmDefinitionStatus(EventChannelListener listener)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAlarmDefinitionStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_DEFINITION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_DEFINITION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeAlarmDefinitionStatus(EventChannelListener listener)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAlarmDefinitionStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_DEFINITION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_DEFINITION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeAlarmDefinitionStatus(EventChannelListener listener, AlarmDefinition definition)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = listener;
            argObj[1] = definition.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAlarmDefinitionStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, definition.getId());
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_DEFINITION, definition.getId());
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, definition.getId());
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION, definition.getId());
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeAlarmDefinitionStatus(EventChannelListener listener, AlarmDefinition definition)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_DEFINITION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = listener;
            argObj[1] = definition.getStruct();

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAlarmDefinitionStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_DEFINITION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, definition.getId());
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_DEFINITION, definition.getId());
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, definition.getId());
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION, definition.getId());
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeAlarmWatchdogStatus(EventChannelListener listener)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            Object[] argObj = {listener};

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAlarmWatchdogStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_WATCHDOG, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, -1);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeAlarmWatchdogStatus(EventChannelListener listener)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            Object[] argObj = {listener};

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAlarmWatchdogStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_WATCHDOG, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeAlarmWatchdogStatus(EventChannelListener listener, AlarmNotificationWatchdog watchdog)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            Object[] argObj = {listener, watchdog.getStruct()};

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAlarmWatchdogStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG, watchdog.getId());
        eventChannel.addChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, watchdog.getId());
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeAlarmWatchdogStatus(EventChannelListener listener, AlarmNotificationWatchdog watchdog)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            Object[] argObj = {listener, watchdog.getStruct()};

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAlarmWatchdogStatus" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG, watchdog.getId());
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, watchdog.getId());
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    public void subscribeActivationAssignmentStatus(EventChannelListener listener)
    {
    	ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT, -1);
		eventChannel.addChannelListener(eventChannel, listener, channelKey);

		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT, -1);
		eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    public void unsubscribeActivationAssignmentStatus(EventChannelListener listener)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT, -1);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    /**
     * Subscribes the listener to all notification events, for everythings.
     * @param listener to subscribe
     */
    public void subscribeAlarmNotifications(EventChannelListener listener)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_NOTIFICATION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAlarmNotifications" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_NOTIFICATION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_NEW_ALARM_NOTIFICATION, new Integer(0));
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_EXPIRED_ALARM_NOTIFICATION, new Integer(0));
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_VISUAL_EXPIRED_ALARM_NOTIFICATION, new Integer(0));
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    /**
     * Unsubscribes the listener to all notification events, for everythings.
     * @param listener to unsubscribe
     */
    public void unsubscribeAlarmNotifications(EventChannelListener listener)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_NOTIFICATION))
        {
            Object[] argObj = new Object[1];
            argObj[0] = listener;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAlarmNotifications" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_NOTIFICATION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_NEW_ALARM_NOTIFICATION, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_EXPIRED_ALARM_NOTIFICATION, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_VISUAL_EXPIRED_ALARM_NOTIFICATION, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    /**
     * Subscribes the listener to all notification events, for a particular subjectName (ORBName) only.
     * @param listener to subscribe
     */
    public void subscribeAlarmNotificationsForSubjectName(EventChannelListener listener, String subjectName)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_NOTIFICATION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = listener;
            argObj[1] = subjectName;

            GUILoggerHome.find().debug(CATEGORY + ": subscribeAlarmNotificationsForSubjectName" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_NOTIFICATION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_NEW_ALARM_NOTIFICATION, subjectName);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_EXPIRED_ALARM_NOTIFICATION, subjectName);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_VISUAL_EXPIRED_ALARM_NOTIFICATION, subjectName);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    /**
     * Unsubscribes the listener to all notification events, for a particular subjectName (ORBName) only.
     * @param listener to unsubscribe
     */
    public void unsubscribeAlarmNotificationsForSubjectName(EventChannelListener listener, String subjectName)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_NOTIFICATION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = listener;
            argObj[1] = subjectName;

            GUILoggerHome.find().debug(CATEGORY + ": unsubscribeAlarmNotificationsForSubjectName" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_NOTIFICATION, argObj);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.IC_NEW_ALARM_NOTIFICATION, subjectName);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_EXPIRED_ALARM_NOTIFICATION, subjectName);
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_VISUAL_EXPIRED_ALARM_NOTIFICATION, subjectName);
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    private String getAlarmsDefinitionChannelName()
    {
        return System.getProperty(ALARM_DEFINITION_CHANNEL_NAME_PROPERTY);
    }

    private String getAlarmsNotificationChannelName()
    {

        return System.getProperty(ALARM_NOTIFICATION_CHANNEL_NAME_PROPERTY);
    }

    private String getICSGroupElementChannelName()
    {
        return System.getProperty(GROUP_ELEMENT_CHANNEL_NAME_PROPERTY);
    }

    private String getCentralLoggingServiceChannelName()
    {
        return System.getProperty(CENTRAL_LOGGING_SERVICE_CHANNEL_NAME_PROPERTY);
    }

    private AlarmActivation activateAlarmActivation(AlarmDefinitionMutable definition,
                                                    AlarmActivationMutable activation,
                                                    boolean active)
            throws SystemException, CommunicationException, NotFoundException, DataValidationException,
                   NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        AlarmActivation updatedActivation = null;
        AlarmActivationMutable foundActivation =
                (AlarmActivationMutable) definition.getActivationById(activation.getId().intValue());

        if(foundActivation != null)
        {
            foundActivation.setActive(active);

            updatedActivation = alarmsCache.updateAlarmActivation(foundActivation);
        }
        else
        {
            ExceptionBuilder.notFoundException("Alarm Activation was not part of Alarm Definition.",
                                               NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        return updatedActivation;
    }

    // alarm notifications
    public AlarmNotification[] getAlarmNotifications()
    {
        return alarmNotificationCache.getAlarmNotifications();
    }

    public AlarmNotificationInfo[] getAlarmNotificationsInfo()
    {
        return alarmNotificationCache.getAlarmNotificationsInfo();
    }

    public void startAlarmNotificationPublishing(CBOEProcess process, String reason)
    {
        GUILoggerHome.find().audit("Start alarm notifications for ICS "+process.getOrbName()+" Reason:"+reason);
        sendAlarmNotificationPublishCommand(SystemMonitorCommandMethodNames.START_ALARM_NOTIFICATION_PUBLISH, process, reason);
    }

    public void stopAlarmNotificationPublishing(CBOEProcess process, String reason)
    {
        GUILoggerHome.find().audit("Stop alarm notifications for ICS " + process.getOrbName() + " Reason:" + reason);
        sendAlarmNotificationPublishCommand(SystemMonitorCommandMethodNames.STOP_ALARM_NOTIFICATION_PUBLISH, process, reason);
    }

    private void sendAlarmNotificationPublishCommand(String commandName, CBOEProcess process, String reason)
    {
        try
        {
            String guiIdentifier = getUniqueProcessIdentifier();
            String[] dataItems = new String[1];
            dataItems[0] = reason + (guiIdentifier.length() > 0 ? " [" + guiIdentifier + "]" : "");

            ARCommand arCommand = CommandFactory.getInstance().getCommand(process.getOrbName(),
                                                                          commandName);
            Command executeCommand = arCommand.buildExecuteCommand(dataItems);

            ExecuteCommandService service = ARCommandServiceFactory.getExecuteCommandService();
            service.setDestination(process.getProcessName());

            service.executeCommand(executeCommand);
        }
        catch (UserException e)
        {
            GUILoggerHome.find().exception(e);
        }
        catch (TimedOutException e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    public String getUniqueProcessIdentifier()
    {
        return System.getProperty("asyncAdminUniqueProcess","");
    }

    public String getAlarmNotificationPublishStatus(CBOEProcess process)
    {
        String result = null;  // means no response
        try
        {
            String commandName = SystemMonitorCommandMethodNames.ADMIN_SHOW_ALARM_NOTIFICATION_PUBLISH_STATE;

            ARCommand arCommand = CommandFactory.getInstance().getCommand(process.getOrbName(),
                                                                          commandName);
            Command executeCommand = arCommand.buildExecuteCommand((String[]) null);

            ExecuteCommandService service = ARCommandServiceFactory.getExecuteCommandService();
            service.setDestination(process.getProcessName());

            ExecutionResult execResult = service.executeCommand(executeCommand);
            if(execResult != null && execResult.isSuccess())
            {
                Command resultCommand = execResult.getCommandResult();
                result = resultCommand.retValues[0].value;
            }
        }
        catch (UserException e)
        {
            GUILoggerHome.find().exception(e);
        }
        catch (TimedOutException e)
        {
            GUILoggerHome.find().exception(e);
        }
        return result;
    }

    public String getIcsManagerState(CBOEProcess process)
    {
        String result = null;  // means no response
        try
        {
            String commandName = SystemMonitorCommandMethodNames.ICS_MANAGER_STATE_QUERY;

            ARCommand arCommand =
                    CommandFactory.getInstance().getCommand(process.getOrbName(), commandName);
            Command executeCommand = arCommand.buildExecuteCommand((String[]) null);

            ExecuteCommandService service = ARCommandServiceFactory.getExecuteCommandService();
            service.setDestination(process.getProcessName());

            ExecutionResult execResult = service.executeCommand(executeCommand);
            if(execResult != null && execResult.isSuccess())
            {
                Command resultCommand = execResult.getCommandResult();
                result = resultCommand.retValues[0].value;
            }
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(e);
        }
        catch(TimedOutException e)
        {
            GUILoggerHome.find().exception(e);
        }
        return result;
    }

    /* (non-Javadoc)
	 * @see com.cboe.interfaces.presentation.api.ProductQueryAPI#getAllProductsByClassForType(short, int, boolean, com.cboe.util.event.EventChannelListener)
	 */
	public Product[] getAllProductsByClass(int classKey, boolean activeOnly, EventChannelListener arg3) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

		return getAllProductsForClass(classKey,activeOnly);
	}

	/* (non-Javadoc)
	 * @see com.cboe.interfaces.presentation.api.ProductQueryAPI#subscribeProductsByClassForType(short, int, com.cboe.util.event.EventChannelListener)
	 */
	public void subscribeProductsByClass(int classKey, EventChannelListener clientListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        throw getUnsupportedException("subscribeProductsByClass(int classKey, EventChannelListener clientListener)");
	}

	/* (non-Javadoc)
	 * @see com.cboe.interfaces.presentation.api.ProductQueryAPI#unsubscribeProductsByClassForType(short, int, com.cboe.util.event.EventChannelListener)
	 */
	public void unsubscribeProductsByClass(int classKey, EventChannelListener clientListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        throw getUnsupportedException("unsubscribeProductsByClass(int classKey, EventChannelListener clientListener)");

	}

    /**
     * Subscribes the listener to all Xtp alert notification events
     * @param listener to subscribe
     */
    public void subscribeXtpAlertNotifications( EventChannelListener listener )
    {
        ChannelKey channelKey = new ChannelKey( ChannelType.XTP_ALERT_NOTIFICATION_NEW_UPDATE, new Integer( 0 ) );
        eventChannel.addChannelListener( eventChannel, listener, channelKey );
    }

    /**
     * Unsubscribes the listener to all Xtp alert notification events
     * @param listener to unsubscribe
     */
    public void unsubscribeXtpAlertNotifications( EventChannelListener listener )
    {
        if( GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn( GUILoggerINBusinessProperty.XTP) )
        {
            Object[] argObj = new Object[ 1 ];
            argObj[ 0 ] = listener;

            GUILoggerHome.find().debug( CATEGORY + ": unsubscribeXtpAlertNotifications" + ":entry",
                                        GUILoggerINBusinessProperty.XTP, argObj );
        }

        ChannelKey channelKey = new ChannelKey( ChannelType.XTP_ALERT_NOTIFICATION_NEW_UPDATE, new Integer( 0 ) );
        eventChannel.removeChannelListener( eventChannel, listener, channelKey );
    }
    
    /**
     * If this ICS orb was previously subscribed, then went down and has now come back up,
     * resubscribe for all instrumentors, orb name alias, and alarms.
     */
    public void resubscribe() {
    	// Resubsribe for instrumentors, if already subsribed for at least one listener 
        int count;
        synchronized(orbListenerMap)
        {
            if (!orbListenerMap.isEmpty())
            {
    	        Set keySet = orbListenerMap.keySet();
    	        Iterator iterator = keySet.iterator();
    	        Integer value = null;
    	        String key = null;
    	        while (iterator.hasNext()) {
    	            key = (String) iterator.next();
    	            value = orbListenerMap.get(key);
    	            if (value == null)
    	            {
    	                count = 0;
    	            }
    	            else
    	            {
    	                count = value.intValue();
    	            }
    	            if (count > 0)
    	            {
    	            	subscribeCollectorDetailsViaAdmin(key);
    	            }
    	        }	
            }
        }

        boolean collectorAttached = InstrumentorCollectorHome.find().isCollectorProcessAttached();
        if (collectorAttached)
        {
	        synchronized (allOrbListenerMap)
	        {
	            String key = InstrumentorTypes.toString(InstrumentorTypes.HEAP);
	            key += InstrumentorCollectorImpl.INSTRUMENTATION_DETAIL;
	            Integer value = allOrbListenerMap.get(key);
	            if (value == null)
	            {
	                count = 0;
	            }
	            else
	            {
	                count = value.intValue();
	            }
	            if (count > 0)
	            {
	                InstrumentorCollectorHome.find().subscribeAllOrbsByType(InstrumentorTypes.HEAP);
	            }
	        }
        }
        
        synchronized (SUMMARY_LOCK_OBJECT)
        {
            if (summaryInstrumentorSubscriptionCount > 0)
            {
                InstrumentorCollectorHome.find().subscribeAllOrbsForSummary();
            }
        }
    }

    /**********    Group Service Methods  **********/

    /**
     * Method used to initiate the group service cache initialization.
     *
     * @throws com.cboe.exceptions.CacheInitializationException exception during cache initialization.
     */
    public void initializeGroupCache() throws CacheInitializationException
    {
        if (groupElementCache == null)
        {
            groupElementCache = new GroupElementCacheGuiImpl(this, eventChannel);
            groupElementCache.initializeCache();
        }
    }

    /**
     * method to return root group element for a group type.
     */
    public GroupElementModel getRootGroupForGroupType(short groupType)
            throws SystemException, CommunicationException, NotFoundException {
        GroupElementModel rootGroup = groupElementCache.getRootGroupForGroupType(groupType);
        if(rootGroup == null)
        {
            try
            {
                GroupElementSynchEventChannelAPI api = new GroupElementSynchEventChannelAPI(groupElementPublisher, getAdminServiceDefaultTimeout());
                ElementStruct rootStruct = api.publishRootElementForGroupType(groupType);
                if(rootStruct != null)
                {
                    rootGroup = new GroupElementModelImpl(rootStruct);
                }
                else
                {
                    throw ExceptionBuilder.notFoundException("No root group found for type: " + groupType, 0);
                }
            }
            catch (TimedOutException e)
            {
                SystemException toException = ExceptionBuilder.systemException(e.getMessage(), 0);
                toException.initCause(e);
                throw toException;
            }
        }
        return rootGroup;
    }

    /**
     * method to return list of all groups in the tree/cloud.
     */
    public Set<GroupElementModel> getAllGroupsForGroupType(short groupType)
            throws SystemException, CommunicationException, NotFoundException {
        Set<GroupElementModel> allGroups = groupElementCache.getAllGroupsForGroupType(groupType);
        if(allGroups.size() <= 0)
        {
            try
            {
                GroupElementModel rootElement = getRootGroupForGroupType(groupType);

                GroupElementSynchEventChannelAPI api = new GroupElementSynchEventChannelAPI(groupElementPublisher, getAdminServiceDefaultTimeout());
                ElementStruct[] structs = api.publishElementsForGroup(rootElement.getElementKey());

                if(structs != null)
                {
                    GroupElementModel[] groupModels = new GroupElementModel[structs.length];

                    for(int i = 0; i < structs.length; i++)
                    {
                        groupModels[i] = new GroupElementModelImpl(structs[i]);
                        allGroups.add(groupModels[i]);
                    }
                }
            }
            catch (TimedOutException e)
            {
                SystemException toException = ExceptionBuilder.systemException(e.getMessage(), 0);
                toException.initCause(e);
                throw toException;
            }
        }
        return allGroups;
    }

    /**
     * method to return immediate sub groups for a group.
     */
    public Set<GroupElementModel> getSubgroupsForGroup(long groupKey)
            throws SystemException, CommunicationException
    {
        Set<GroupElementModel> subGroups = groupElementCache.getSubGroupsForGroup(groupKey);
        if(subGroups.size() <= 0)
        {
            try
            {
                GroupElementSynchEventChannelAPI api = new GroupElementSynchEventChannelAPI(groupElementPublisher, getAdminServiceDefaultTimeout());
                ElementStruct[] structs = api.publishElementsForGroup(groupKey);

                if(structs != null)
                {
                    GroupElementModel[] groupModels = new GroupElementModel[structs.length];

                    for(int i = 0; i < structs.length; i++)
                    {
                        groupModels[i] = new GroupElementModelImpl(structs[i]);
                        if(groupModels[i].isGroup())
                        {
                            subGroups.add(groupModels[i]);
                        }
                    }
                }
            }
            catch (TimedOutException e)
            {
                SystemException toException = ExceptionBuilder.systemException(e.getMessage(), 0);
                toException.initCause(e);
                throw toException;
            }
        }
        return subGroups;
    }

    /**
     * method to return parent groups for a group element.
     */
    public Set<GroupElementModel> getParentsForGroupElement(long elementKey)
    {
        return groupElementCache.getParentGroupsForGroupElement(elementKey);
    }

    /**
     * Method to return all the immediate/direct leaf children for a group.
     */
    public Set<GroupElementModel> getLeafElementsForGroup(long groupKey)
            throws SystemException, CommunicationException
    {
        Set<GroupElementModel> leafs = groupElementCache.getLeafElementsForGroup(groupKey);
        if(leafs.size() <= 0)
        {
            try
            {
                GroupElementSynchEventChannelAPI api = new GroupElementSynchEventChannelAPI(groupElementPublisher, getAdminServiceDefaultTimeout());
                ElementStruct[] structs = api.publishElementsForGroup(groupKey);

                if(structs != null)
                {
                    GroupElementModel[] groupModels = new GroupElementModel[structs.length];

                    for(int i = 0; i < structs.length; i++)
                    {
                        groupModels[i] = new GroupElementModelImpl(structs[i]);
                        if(groupModels[i].isLeaf())
                        {
                            leafs.add(groupModels[i]);
                        }
                    }
                }
            }
            catch (TimedOutException e)
            {
                SystemException toException = ExceptionBuilder.systemException(e.getMessage(), 0);
                toException.initCause(e);
                throw toException;
            }
        }
        return leafs;
    }

    /**
     * method to return all parent groups for an element. Element can be leaf or group.
     */
    public Set<GroupElementModel> getAllParentGroupsForGroupElement(long groupElementKey)
    {
        return groupElementCache.getAllParentGroupsForGroupElement(groupElementKey);
    }

    /**
     * method to return all leaf elements for a group.
     */
    public Set<GroupElementModel> getAllLeafElementsForGroup(long groupElementKey)
    {
        return groupElementCache.getAllLeafElementsForGroup(groupElementKey);
    }

    /**
     * Finds group element based on 4 unique criteria, 1) name 2) groupType 3) dataType 4) nodeType.
     */
    public GroupElementModel findElementKeyByUniqueSearchHashCode(GroupElementModel groupModel)
    {
        return groupElementCache.findElementKeyByUniqueSearchHashCode(groupModel);
    }

    /**
     * Methods used to convert the model object into structs and call the service.
     */
    public void createElementsForGroup(long groupKey, List<GroupElementModel> newGroupElements)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException
    {
        ElementEntryStruct[] newElements = new ElementEntryStruct[newGroupElements.size()];
        int i = 0;
        for (GroupElementModel newGroupElement : newGroupElements)
        {
            newElements[i++] = newGroupElement.toElementEntryStruct();
        }

        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            guiLogger.debug(CATEGORY + "createElementsForGroup: groupKey:",
                                       GUILoggerINBusinessProperty.GROUPS, groupKey);
            guiLogger.debug(CATEGORY + "createElementsForGroup: elements:",
                                       GUILoggerINBusinessProperty.GROUPS, newElements);
        }

        try
        {
            GroupElementSynchEventChannelAPI api = new GroupElementSynchEventChannelAPI(groupElementPublisher, getAdminServiceDefaultTimeout());
            api.createElementsForGroup(groupKey, newElements);
        }
        catch (TimedOutException e)
        {
            SystemException toException = ExceptionBuilder.systemException(e.getMessage(), 0);
            toException.initCause(e);
            throw toException;
        }
    }


    /**
     * Methos used to convert the model object into structs and call the service.
     */
    public void addElementsToGroup(long groupKey, Set<GroupElementModel> groupElements)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException
    {

        long[] elements = new long[groupElements.size()];
        int i = 0;
        for (GroupElementModel element : groupElements)
        {
            elements[i++] = element.getElementKey();
        }

        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            guiLogger.debug(CATEGORY + "addElementsToGroup: groupKey:",
                                       GUILoggerINBusinessProperty.GROUPS, groupKey);
            guiLogger.debug(CATEGORY + "addElementsToGroup: elements:",
                                       GUILoggerINBusinessProperty.GROUPS, elements);
        }

        try
        {
            GroupElementSynchEventChannelAPI api = new GroupElementSynchEventChannelAPI(groupElementPublisher, getAdminServiceDefaultTimeout());
            api.addElementsToGroup(groupKey, elements);
        }
        catch (TimedOutException e)
        {
            SystemException toException = ExceptionBuilder.systemException(e.getMessage(), 0);
            toException.initCause(e);
            throw toException;
        }
    }

    /**
     * Method used to convert the model object into structs and call the rename service.
     */
    public void  updateElement(GroupElementModel groupElement)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException
    {
        ElementStruct renameElement = groupElement.toElementStruct();

        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            guiLogger.debug(CATEGORY + "updateElement: groupKey:",
                                       GUILoggerINBusinessProperty.GROUPS, renameElement);
        }
        try
        {
            GroupElementSynchEventChannelAPI api = new GroupElementSynchEventChannelAPI(groupElementPublisher, getAdminServiceDefaultTimeout());
            api.updateElement(renameElement);
        }
        catch (TimedOutException e)
        {
            SystemException toException = ExceptionBuilder.systemException(e.getMessage(), 0);
            toException.initCause(e);
            throw toException;
        }
    }


    /**
     * Methods used to convert the model object into structs and call the service.
     */
    public void cloneToGroup(long newGroupKey, GroupElementModel cloneGroupElement, long[] elementKeys)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            guiLogger.debug(CATEGORY +  "cloneGroup: newGroupKey:",
                                       GUILoggerINBusinessProperty.GROUPS, newGroupKey);
            guiLogger.debug(CATEGORY +  "cloneGroup: cloneGroupElementt:",
                                       GUILoggerINBusinessProperty.GROUPS, cloneGroupElement);
            guiLogger.debug(CATEGORY +  "cloneGroup: elementKeys:",
                                       GUILoggerINBusinessProperty.GROUPS, elementKeys);
        }

        try
        {
            GroupElementSynchEventChannelAPI api = new GroupElementSynchEventChannelAPI(groupElementPublisher, getAdminServiceDefaultTimeout());
            api.cloneToGroup(newGroupKey, cloneGroupElement.toElementEntryStruct(), elementKeys);

        }
        catch (TimedOutException e)
        {
            SystemException toException = ExceptionBuilder.systemException(e.getMessage(), 0);
            toException.initCause(e);
            throw toException;
        }
    }

    /**
     * Move
     */
    public void moveToGroup(long currentGroupKey, long newGroupKey, long[] elementKeys)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            guiLogger.debug(CATEGORY +  "moveToGroup: newGroupKey:",
                                       GUILoggerINBusinessProperty.GROUPS, newGroupKey);
            guiLogger.debug(CATEGORY +  "moveToGroup: groupElements:",
                                       GUILoggerINBusinessProperty.GROUPS, elementKeys);
        }

        try
        {
            GroupElementSynchEventChannelAPI api = new GroupElementSynchEventChannelAPI(groupElementPublisher, getAdminServiceDefaultTimeout());
            api.moveToGroup(currentGroupKey, newGroupKey, elementKeys);

        }
        catch (TimedOutException e)
        {
            SystemException toException = ExceptionBuilder.systemException(e.getMessage(), 0);
            toException.initCause(e);
            throw toException;
        }
    }

    /**
     * Methods used to convert the model object into structs and call the service.
     */
    public void removeElementsFromGroup(long groupKey, Set<GroupElementModel> groupElements)
            throws SystemException, TransactionFailedException, DataValidationException, CommunicationException, AuthorizationException
    {
        long[] elements = new long[groupElements.size()];
        int i = 0;
        for (GroupElementModel element : groupElements)
        {
            elements[i++] = element.getElementKey();
        }

        StringBuilder errorString = new StringBuilder(256);

        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            guiLogger.debug(CATEGORY + "removeElementsFromGroup: groupKey:",
                                       GUILoggerINBusinessProperty.GROUPS, groupKey);
            guiLogger.debug(CATEGORY + "removeElementsFromGroup: elements:",
                                       GUILoggerINBusinessProperty.GROUPS, elements);
        }

        try
        {
            GroupElementSynchEventChannelAPI api = new GroupElementSynchEventChannelAPI(groupElementPublisher, getAdminServiceDefaultTimeout());
            api.removeElementsFromGroup(groupKey, elements);
        }
        catch (TimedOutException e)
        {
            SystemException toException = ExceptionBuilder.systemException(e.getMessage(), 0);
            toException.initCause(e);
            throw toException;
        }
    }

    public void subscribeGroupElementCacheEvents(EventListener groupElementListener)
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            guiLogger.debug(CATEGORY + "subscribeGroupElementCacheEvents",
                                       GUILoggerINBusinessProperty.GROUPS, groupElementListener.getClass().getName());

        }

        groupElementCache.addGroupElementListener((GroupElementListener)groupElementListener);
    }

    public void unsubscribeGroupElementCacheEvents(EventListener groupElementListener)
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            guiLogger.debug(CATEGORY + "unsubscribeGroupElementCacheEvents",
                                       GUILoggerINBusinessProperty.GROUPS, groupElementListener.getClass().getName());

        }

        groupElementCache.removeGroupElementListener((GroupElementListener)groupElementListener);
    }

	@Override
    public ActivationAssignment addActivationAssignment(ActivationAssignment assignment) throws SystemException, CommunicationException, AlreadyExistsException, DataValidationException
    {
	    return alarmsCache.addActivationAssignment(assignment);
    }

	@Override
    public ActivationAssignment getActivationAssignmentById(Integer assignmentId)
    {
	    return alarmsCache.findAssignmentById(assignmentId);
    }

	@Override
    public ActivationAssignment getActivationAssignmentForActivationId(Integer activationId)
    {
	    return alarmsCache.findAssignmentByActivationId(activationId);
    }

	@Override
    public ActivationAssignment updateActivationAssignment(ActivationAssignment assignment) throws SystemException, CommunicationException, NotFoundException, DataValidationException
    {
	    return alarmsCache.updateActivationAssignment(assignment);
    }

	@Override
    public ActivationAssignment removeActivationAssignment(ActivationAssignment assignment) throws SystemException, CommunicationException, NotFoundException
    {
	    return alarmsCache.removeActivationAssignment(assignment);
    }

	@Override
    public ActivationAssignment[] getAllActivationAssignments()
    {
	    return alarmsCache.getAllAssignments();
    }

    public LogicalName[] getAllLogicalNames(EventChannelListener listener) throws SystemException, CommunicationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ORB_NAME_ALIAS))
        {
            GUILoggerHome.find().debug(CATEGORY + ": getAllLogicalNames" + ":entry",
                                       GUILoggerINBusinessProperty.ORB_NAME_ALIAS);
        }
        subscribeLogicalOrbName(listener);
        LogicalName[] names = logicalNameCache.getAllLogicalNames();

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ORB_NAME_ALIAS))
        {
            if(names != null && names.length > 0)
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAllLogicalNames" + ":exit",
                                           GUILoggerINBusinessProperty.ORB_NAME_ALIAS, names);
            }
            else
            {
                GUILoggerHome.find().debug(CATEGORY + ": getAllLogicalNames" + ":exit",
                                           GUILoggerINBusinessProperty.ORB_NAME_ALIAS,
                                           "logical names were null or zero length");
            }
        }

        return names;
    }

    public void initializeLogicalOrbNameCache()
    {
        LogicalName[] names;
        try
        {
            LogicalOrbNameStruct[] structs = logicalOrbNameDelegate.publishAllLogicalOrbNames();
            if(structs != null)
            {
                names = new LogicalName[structs.length];

                for(int i = 0; i < structs.length; i++)
                {
                    LogicalOrbNameStruct struct = structs[i];
                    LogicalName name = LogicalNameFactory.createLogicalName(struct);
                    names[i] = name;
                }
                logicalNameCache.initializeCache(names, getIcsManagerOrbName());
            }
            else
            {
                names = new LogicalName[0];
            }
        }
        catch(TimedOutException te)
        {
            DefaultExceptionHandlerHome.find().process(te, "Could not load Logical ORB Names.");
        }
        catch(UserException ue)
        {
            DefaultExceptionHandlerHome.find().process(ue, "Could not load Logical ORB Names.");
        }
    }

    public void initializeLogicalOrbNamePublishing()
    {
        logicalOrbNamePublisherHome = new OrbNameAliasEventPublisherHomeImpl();
        logicalOrbNameEventConsumerHome = new OrbNameAliasEventConsumerHomeImpl();
        String orbNameAliasChannelName = System.getProperty(ORB_NAME_ALIAS_CHANNEL_NAME_PROPERTY);
        try
        {
            logicalOrbNameEventConsumerHome.initializeOrbNameAliasConsumer(orbNameAliasChannelName);
            logicalOrbNamePublisherHome.initializeOrbNameAliasPublisher(orbNameAliasChannelName);
            int defaultAdminServiceTimeout = getAdminServiceDefaultTimeout();
            logicalOrbNameDelegate = new OrbNameAliasSynchEventChannelDelegate(
                    logicalOrbNamePublisherHome.getOrbNameAliasPublisher(),
                    defaultAdminServiceTimeout);
        }
        catch(Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }
    
    public Product getProductByKeyFromCache(int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {
        return getProductByKey(productKey);
    }


    /**
     * snoozeAlarm
     * creates a watchdog with a snooze policy/limit
     * or updates existing watchdog policies with a snooze limit
     */
    public void snoozeAlarm(final AlarmActivation alarmActivation, final long duration)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            Object[] argObj = {alarmActivation.getId()};

            GUILoggerHome.find().debug(CATEGORY + ": snoozeAlarm" + ":entry",
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, argObj);
        }

        GUIWorkerImpl worker = new NonGUIWorkerImpl()
        {
            public void execute() throws Exception
            {
                AlarmNotificationWatchdog existingWatchdog;
                try
                {
                    existingWatchdog = getWatchdogForActivationId(alarmActivation.getId());
                }
                catch (NotFoundException e1)
                {
                    existingWatchdog = null;
                }

                if(existingWatchdog != null)
                {
                    AlarmNotificationWatchdogMutable updatedWatchdog =
                            AlarmNotificationWatchdogFactory.createMutableAlarmNotificationWatchdog(existingWatchdog);

                    boolean update = false;
                    for(int policyIndex = 0; policyIndex < existingWatchdog.getPoliciesSize(); policyIndex++)
                    {
                        NotificationWatchdogPolicyMutable updatedPolicy =
                                AlarmNotificationWatchdogFactory.createMutableNotificationWatchdogPolicy(existingWatchdog.getPolicy(policyIndex));

                        for(int limitIndex = 0; limitIndex < existingWatchdog.getPolicy(policyIndex).getLimitsSize(); limitIndex++)
                        {
                            if(existingWatchdog.getPolicy(policyIndex).getLimit(limitIndex).getLimitType() == NotificationWatchdogLimitTypes.SNOOZE)
                            {
                                update = true;
                                if(duration > 0)
                                {
                                    // update snooze limit
                                    NotificationWatchdogLimitMutable updateLimitType =
                                            AlarmNotificationWatchdogFactory.createMutableNotificationWatchdogLimit(existingWatchdog.getPolicy(policyIndex).getLimit(limitIndex));
                                    long currentTime = System.currentTimeMillis();
                                    Date startDate = new Date(currentTime);
                                    Date endDate = new Date(currentTime + duration);

                                    Time startTime = new TimeImpl( new TimeStruct( (byte)startDate.getHours(), (byte)startDate.getMinutes(), (byte)startDate.getSeconds(), (byte)0));
                                    Time endTime = new TimeImpl( new TimeStruct( (byte)endDate.getHours(), (byte)endDate.getMinutes(), (byte)endDate.getSeconds(), (byte)0));
                                    WithinTimeDataParser withinTimeData = new WithinTimeDataParser(startTime, endTime);
                                    updateLimitType.setTimeBoundaryFields(withinTimeData);
                                    updatedPolicy.setLimit(limitIndex, updateLimitType);
                                }
                                else
                                {
                                    updatedPolicy.removeLimit(limitIndex);
                                }
                            }
                        }
                        if(update)
                        {
                            if(updatedPolicy.getLimitsSize() == 0)
                            {
                                updatedWatchdog.removePolicy(policyIndex);
                            }
                            else
                            {
                                updatedWatchdog.setPolicy(policyIndex, updatedPolicy);
                            }
                        }
                        else if(duration > 0)
                        {
                            NotificationWatchdogLimitMutable addLimitType = AlarmNotificationWatchdogFactory.createNewNotificationWatchdogLimit();
                            addLimitType.setLimitType(NotificationWatchdogLimitTypes.SNOOZE);
                            long currentTime = System.currentTimeMillis();
                            Date startDate = new Date(currentTime);
                            Date endDate = new Date(currentTime + duration);

                            Time startTime = new TimeImpl( new TimeStruct( (byte)startDate.getHours(), (byte)startDate.getMinutes(), (byte)startDate.getSeconds(), (byte)0));
                            Time endTime = new TimeImpl( new TimeStruct( (byte)endDate.getHours(), (byte)endDate.getMinutes(), (byte)endDate.getSeconds(), (byte)0));
                            WithinTimeDataParser withinTimeData = new WithinTimeDataParser(startTime, endTime);
                            addLimitType.setTimeBoundaryFields(withinTimeData);
                            updatedPolicy.addLimit(updatedPolicy.getLimitsSize(), addLimitType);
                            updatedWatchdog.setPolicy(policyIndex, updatedPolicy);
                            update = true;
                        }
                    }
                    if(update)
                    {
                        if(updatedWatchdog.getPoliciesSize() == 0)
                        {
                            removeWatchdog(existingWatchdog);
                        }
                        else
                        {
                            updateWatchdog(updatedWatchdog);
                        }
                    }
                }
                else if(duration > 0)
                {
                   // create a snooze watchdog for the associated activationId
                    NotificationWatchdogLimitMutable newLimitType = AlarmNotificationWatchdogFactory.createNewNotificationWatchdogLimit();
                    newLimitType.setLimitType(NotificationWatchdogLimitTypes.SNOOZE);
                    long currentTime = System.currentTimeMillis();
                    Date startDate = new Date(currentTime);
                    Date endDate = new Date(currentTime + duration);

                    Time startTime = new TimeImpl( new TimeStruct( (byte)startDate.getHours(), (byte)startDate.getMinutes(), (byte)startDate.getSeconds(), (byte)0));
                    Time endTime = new TimeImpl( new TimeStruct( (byte)endDate.getHours(), (byte)endDate.getMinutes(), (byte)endDate.getSeconds(), (byte)0));
                    WithinTimeDataParser withinTimeData = new WithinTimeDataParser(startTime, endTime);
                    newLimitType.setTimeBoundaryFields(withinTimeData);

                    NotificationWatchdogPolicyMutable snoozePolicy = AlarmNotificationWatchdogFactory.createNewNotificationWatchdogPolicy();
                    snoozePolicy.setLimits(new NotificationWatchdogLimit[] {newLimitType});

                    AlarmNotificationWatchdogMutable newWatchdog =
                            AlarmNotificationWatchdogFactory.createNewAlarmNotificationWatchdog(alarmActivation);

                    newWatchdog.setPolicies(new NotificationWatchdogPolicy[] {snoozePolicy});
                    newWatchdog.setState(NotificationWatchdogStates.ACTIVE);
                    addWatchdog(newWatchdog);
                }
            }

            public void handleException(Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Couldn't create Snooze Watchdog");
            }
        };
        APIWorkerImpl.run(worker);
    }

    /**
     * snoozeAlarm
     * creates a watchdog with a snooze policy/limit
     * or updates existing watchdog policies with a snooze limit
     */
    public void snoozeAlarm(final int alarmActivationId, final long duration)
    {
        GUIWorkerImpl worker = new NonGUIWorkerImpl()
        {
            public void execute() throws Exception
            {
                snoozeAlarm(getAlarmActivationById(alarmActivationId), duration);
            }

            public void handleException(Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        };
        APIWorkerImpl.run(worker);
    }

    public String getIcsManagerTime()
    {
        String result = null;  // means no response
        try
        {
            CBOEProcess process = processCache.getIcsManager();
            if(process != null)
            {
                String commandName = SystemMonitorCommandMethodNames.ICS_TIME_QUERY;

                ARCommand arCommand =
                        CommandFactory.getInstance().getCommand(process.getOrbName(), commandName);
                Command executeCommand = arCommand.buildExecuteCommand((String[]) null);

                ExecuteCommandService service = ARCommandServiceFactory.getExecuteCommandService();
                service.setDestination(process.getProcessName());

                ExecutionResult execResult = service.executeCommand(executeCommand);
                if(execResult != null && execResult.isSuccess())
                {
                    Command resultCommand = execResult.getCommandResult();
                    result = resultCommand.retValues[0].value;
                }
            }
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(e);
        }
        catch(TimedOutException e)
        {
            GUILoggerHome.find().exception(e);
        }
        return result;
    }
}
