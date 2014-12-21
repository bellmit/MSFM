//
// -----------------------------------------------------------------------------------
// Source file: TraderAPIImpl.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.omg.CORBA.UserException;

import com.cboe.consumers.callback.AuctionConsumerFactory;
import com.cboe.consumers.callback.CMICallbackConsumerCacheFactoryImpl;
import com.cboe.consumers.callback.CMICallbackV2ConsumerCacheFactoryImpl;
import com.cboe.consumers.callback.CMICallbackV3ConsumerCacheFactoryImpl;
import com.cboe.consumers.callback.ClassStatusConsumerFactory;
import com.cboe.consumers.callback.ExpectedOpeningPriceConsumerFactory;
import com.cboe.consumers.callback.OrderBookUpdateV2ConsumerFactory;
import com.cboe.consumers.callback.OrderStatusConsumerFactory;
import com.cboe.consumers.callback.OrderStatusV2ConsumerFactory;
import com.cboe.consumers.callback.ProductStatusConsumerFactory;
import com.cboe.consumers.callback.QuoteStatusV2ConsumerFactory;
import com.cboe.consumers.callback.StrategyStatusConsumerFactory;
import com.cboe.consumers.callback.SubscriptionManagerFactory;
import com.cboe.consumers.callback.TickerConsumerFactory;
import com.cboe.consumers.callback.TickerV2ConsumerFactory;
import com.cboe.consumers.callback.TradingSessionStatusConsumerFactory;
import com.cboe.domain.util.CacheClassTrackFactory;
import com.cboe.domain.util.CurrentMarketProductContainerImpl;
import com.cboe.domain.util.ObjectKeyContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.StructBuilder;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmi.MarketQuery;
import com.cboe.idl.cmi.OrderQuery;
import com.cboe.idl.cmi.ProductDefinition;
import com.cboe.idl.cmi.ProductQuery;
import com.cboe.idl.cmi.TradingSession;
import com.cboe.idl.cmi.UserHistory;
import com.cboe.idl.cmi.UserPreferenceQuery;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiCallback.CMIClassStatusConsumer;
import com.cboe.idl.cmiCallback.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer;
import com.cboe.idl.cmiCallback.CMINBBOConsumer;
import com.cboe.idl.cmiCallback.CMIOrderBookConsumer;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;
import com.cboe.idl.cmiCallback.CMIProductStatusConsumer;
import com.cboe.idl.cmiCallback.CMIRecapConsumer;
import com.cboe.idl.cmiCallback.CMIStrategyStatusConsumer;
import com.cboe.idl.cmiCallback.CMITickerConsumer;
import com.cboe.idl.cmiCallback.CMITradingSessionStatusConsumer;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiConstants.OrderCancelTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiMarketData.BookDepthStructV2;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiOrder.AuctionSubscriptionResultStruct;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;
import com.cboe.idl.cmiOrder.OrderBustReportStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderFilledReportStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.PendingOrderStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.PendingAdjustmentStruct;
import com.cboe.idl.cmiProduct.PendingNameStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiQuote.RFQEntryStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyRequestStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiTrade.FloorTradeEntryStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.PreferenceStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiV2.UserSessionManagerV2;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.idl.cmiV5.UserSessionManagerV5;
import com.cboe.idl.cmiV6.FloorTradeMaintenanceService;
import com.cboe.idl.cmiV6.UserSessionManagerV6;
import com.cboe.idl.cmiV7.UserSessionManagerV7;
import com.cboe.idl.cmiV9.UserSessionManagerV9;
import com.cboe.interfaces.consumers.callback.CallbackConsumerCacheFactory;
import com.cboe.interfaces.consumers.callback.CallbackV2ConsumerCacheFactory;
import com.cboe.interfaces.consumers.callback.CallbackV3ConsumerCacheFactory;
import com.cboe.interfaces.domain.CurrentMarketProductContainer;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.api.OrderEntryFacade;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.api.TraderAPI;
import com.cboe.interfaces.presentation.api.marketDataCache.CurrentMarketV3Cache;
import com.cboe.interfaces.presentation.auction.Auction;
import com.cboe.interfaces.presentation.bookDepth.BookDepth;
import com.cboe.interfaces.presentation.bookDepth.DetailBookDepth;
import com.cboe.interfaces.presentation.common.formatters.OperationResultFormatStrategy;
import com.cboe.interfaces.presentation.marketData.DsmBidAskStruct;
import com.cboe.interfaces.presentation.marketData.PersonalBestBook;
import com.cboe.interfaces.presentation.marketData.StrategyImpliedMarketWrapper;
import com.cboe.interfaces.presentation.marketData.UserMarketDataStruct;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.permissionMatrix.Permission;
import com.cboe.interfaces.presentation.permissionMatrix.UserPermissionMatrix;
import com.cboe.interfaces.presentation.product.PendingNameContainer;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductAdjustmentContainer;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.ProductType;
import com.cboe.interfaces.presentation.product.ReportingClass;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.interfaces.presentation.tradingSession.DefaultTradingSession;
import com.cboe.interfaces.presentation.user.Role;
import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.presentation.api.marketDataCache.MarketDataCacheFactory;
import com.cboe.presentation.api.orderBook.TradersOrderBookInitializationException;
import com.cboe.presentation.api.orderBook.TradersOrderBookManager;
import com.cboe.presentation.auction.AuctionCacheFactory;
import com.cboe.presentation.auction.AuctionTypeContainer;
import com.cboe.presentation.bookDepth.BookDepthImpl;
import com.cboe.presentation.bookDepth.OrderBookFactory;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.MatchOrderTypes;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.*;
import com.cboe.presentation.properties.CommonTranslatorProperties;
import com.cboe.presentation.user.UserStructModelImpl;
import com.cboe.presentation.userSession.UserSessionFactory;
import com.cboe.presentation.marketData.displayrules.StrategyBidAskFlipper;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

/**
 * This class is the implementation of the Trader interface to the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @author Connie Feng
 * @author Jing Chen
 * @version  11/22/00
 */
public class TraderAPIImpl implements TraderAPI, CustomKeys
{
    protected UserStructModel validSessionProfileUser;
    // the caches
    protected OrderQueryCacheProxy orderQueryCache;

    protected UserSessionManager sessionManager;
    protected UserSessionManagerV2 sessionManagerV2;
    protected UserSessionManagerV3 sessionManagerV3;
    //protected UserSessionManagerV4 sessionManagerV4;
    protected UserSessionManagerV5 sessionManagerV5;
    protected UserSessionManagerV6 sessionManagerV6;
    protected UserSessionManagerV7 sessionManagerV7;
    private UserSessionManagerV9 sessionManagerV9;

    protected MarketQuery marketQuery;
    protected OrderQuery orderQuery;
    protected ProductQuery productQuery;
    protected ProductDefinition productDefinition;
    protected UserPreferenceQuery userPreferenceQuery;
    protected TradingSession tradingSession;
    protected EventChannelAdapter eventChannel;
    protected FilledReportCache filledReportCache;
    protected BustReportCache bustReportCache;
    protected TradingSessionCache tradingSessionCache;
    protected TradersOrderBookManager orderBookManager;
    protected UserHistory userHistory;
    
    protected FloorTradeMaintenanceService floorTradeMaintenanceService;

    protected com.cboe.idl.cmi.OrderEntry orderEntry;
    protected com.cboe.idl.cmiV3.OrderEntry orderEntryV3;
    protected com.cboe.idl.cmiV5.OrderEntry orderEntryV5;
    protected com.cboe.idl.cmiV7.OrderEntry orderEntryV7;
    // all future versions of the API should use this order entry api field which
    // will reference a proxy. Further refactoring should be done to move
    // order entry api code to the proxy
    private OrderEntryFacade orderEntryAPI;

    protected CallbackConsumerCacheFactory cmiConsumerCacheFactory;
    protected CallbackV2ConsumerCacheFactory cmiConsumerCacheFactoryV2;
    protected CallbackV3ConsumerCacheFactory cmiConsumerCacheFactoryV3;

    protected CMIExpectedOpeningPriceConsumer expectedOpeningPriceConsumer;
    protected CMIOrderStatusConsumer orderStatusConsumer;
    protected CMIClassStatusConsumer classStatusConsumer;
    protected CMIProductStatusConsumer productStatusConsumer;
    protected CMIStrategyStatusConsumer strategyStatusConsumer;
    protected CMITradingSessionStatusConsumer tradingSessionStatusConsumer;
    protected CMITickerConsumer tickerConsumer;
    protected CMIUserSessionAdmin userSessionAdminConsumer;

    protected ProductSessionProcessor productProcessor = new ProductSessionProcessor();

    protected EventChannelListener userClientListener;
    protected boolean gmdCallback;
    private final String Category = this.getClass().getName();

    //added 6/2002 to count number of eventChannel listeners by sessionName/classKey
    private static Map<String, Map<EventChannelListener, Integer>> userMarketDataListenerCount = new HashMap<String, Map<EventChannelListener, Integer>>();

    protected com.cboe.idl.cmiV3.MarketQuery marketQueryV3;
    protected com.cboe.idl.cmiV2.MarketQuery marketQueryV2;
    protected com.cboe.idl.cmiV2.OrderQuery orderQueryV2;
    protected com.cboe.idl.cmiV3.OrderQuery orderQueryV3;
    protected com.cboe.idl.cmiV6.OrderQuery orderQueryV6;
    protected com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer orderBookUpdateConsumerV2;
    protected com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer orderStatusConsumerV2;
    protected com.cboe.idl.cmiCallbackV2.CMITickerConsumer tickerConsumerV2;
    protected com.cboe.idl.cmiCallbackV3.CMIAuctionConsumer auctionConsumer;
    
    protected com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer quoteStatusConsumerV2;

    public static final short DEFAULT_QUEUE_ACTION = QueueActions.OVERLAY_LAST;
    public static final boolean DEFAULT_PUBLISH_ON_SUBSCRIBE_ACTION = true;
    protected static final int MAP_INITIAL_CAPACITY = 1000;
    protected static final int SET_INITIAL_CAPACITY = 100;
    
    /**
     * Default constructor
     * Does NOTHING but allows subclass to override
     */
    public TraderAPIImpl()
    {
        super();
    }

    public TraderAPIImpl(UserSessionManagerV9 sessionMgr, CMIUserSessionAdmin userListener,EventChannelListener clientListener, boolean gmd)
    {
        setSessionManager(sessionMgr);
        setSessionManagerV2(sessionMgr);
        setSessionManagerV3(sessionMgr);
        setSessionManagerV5(sessionMgr);
        setSessionManagerV6(sessionMgr);
        setSessionManagerV7(sessionMgr);
    	setSessionManagerV9(sessionMgr);
        
        userClientListener          = clientListener;
        userSessionAdminConsumer    = userListener;
        gmdCallback = gmd;
    }

    /**
     * setSessionManager - allows for the proper setting of the sessionManager for
     * CAS vs. SACAS ( sessionManager vs. sysatemAdminSessionManager )
     */
    protected void setSessionManager( UserSessionManager sessionMgr )
    {
        sessionManager = sessionMgr;
    }
    /**
     * setSessionManagerV2 - access to v2 API
     * @param sessionManagerV2
     */
    protected void setSessionManagerV2(UserSessionManagerV2 sessionManagerV2)
    {
        this.sessionManagerV2 = sessionManagerV2;
    }
    /**
     * setSessionManagerV3 - access to v3 API
     * @param sessionManagerV3
     */
    protected void setSessionManagerV3(UserSessionManagerV3 sessionManagerV3)
    {
        this.sessionManagerV3 = sessionManagerV3;
    }

    /**
     * setSessionManagerV3 - access to v5 API
     * @param sessionManagerV5
     */
    protected void setSessionManagerV5(UserSessionManagerV5 sessionManagerV5)
    {
        this.sessionManagerV5 = sessionManagerV5;
    }

    protected void setSessionManagerV6(UserSessionManagerV6 sessionManagerV6)
    {
        this.sessionManagerV6 = sessionManagerV6;
    }
    
    protected void setSessionManagerV7(UserSessionManagerV7 sessionManagerV7)
    {
        this.sessionManagerV7 = sessionManagerV7;
    }

    protected void setSessionManagerV9(UserSessionManagerV9 sessionManagerV9)
    {
    	this.sessionManagerV9 = sessionManagerV9;
    }
    
    public void initialize() throws Exception
    {
        try
        {
            initializeSessionManagerInterfaces();
            initializeSessionManagerV2Interfaces();
            initializeSessionManagerV3Interfaces();
            initializeSessionManagerV5Interfaces();
            initializeSessionManagerV6Interfaces();
            initializeSessionManagerV7Interfaces();
            initializeSessionManagerV9Interfaces();
            
            eventChannel = EventChannelAdapterFactory.find();
            eventChannel.setDynamicChannels(true);
            eventChannel.setListenerCleanup(CommonTranslatorProperties.isCleanChannelAdapterEnabled());

            initializeCallbackConsumers();
            initializeCallbackV2Consumers();
            initializeCallbackV3Consumers();

            initializeCaches();
            initializeCachesV2();
            initializeCachesV3();
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME + ": TraderAPIImpl.initialize", e);
            throw e;
        }
    }

    protected void initializeCaches() throws Exception
    {
        initializeSessionlessProductCache();
        initializeSessionProductCache();
        initializeOrderCache();
    }

    protected void initializeCachesV2() throws Exception
    {}

    protected void initializeCachesV3() throws Exception
    {}

    protected void initializeSessionManagerInterfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        marketQuery = sessionManager.getMarketQuery();
        orderQuery = sessionManager.getOrderQuery();
        productQuery = sessionManager.getProductQuery();
        productDefinition = sessionManager.getProductDefinition();
        orderEntry = sessionManager.getOrderEntry();
        tradingSession = sessionManager.getTradingSession();
        userPreferenceQuery = sessionManager.getUserPreferenceQuery();
        userHistory = sessionManager.getUserHistory();

        validSessionProfileUser = getValidUser();
    }

    protected void initializeSessionManagerV2Interfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        marketQueryV2 = sessionManagerV2.getMarketQueryV2();
        orderQueryV2 = sessionManagerV2.getOrderQueryV2();
    }

    protected void initializeSessionManagerV3Interfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        marketQueryV3 = sessionManagerV3.getMarketQueryV3();

        orderQueryV3 = sessionManagerV3.getOrderQueryV3();
        orderEntryV3 = sessionManagerV3.getOrderEntryV3();
    }

    protected void initializeSessionManagerV5Interfaces() throws SystemException, CommunicationException, AuthorizationException
    {
    	if(sessionManagerV5 != null)
    	{
            orderEntryV5 = sessionManagerV5.getOrderEntryV5();
    	}
    }

    protected void initializeSessionManagerV6Interfaces() throws SystemException, CommunicationException, AuthorizationException
    {
    	if(sessionManagerV6 != null)
    	{
            floorTradeMaintenanceService = sessionManagerV6.getFloorTradeMaintenanceService();
    	}
    }
    
    protected void initializeSessionManagerV7Interfaces() throws SystemException, CommunicationException, AuthorizationException
    {
    	if(sessionManagerV7 != null)
    	{
    		orderEntryV7 = sessionManagerV7.getOrderEntryV7();
    	}
    }

    protected void initializeSessionManagerV9Interfaces() throws SystemException, CommunicationException, AuthorizationException, AuthenticationException, NotFoundException
    {
    	if(sessionManagerV9 != null)
    	{
    		orderEntryAPI = new OrderEntryAPIProxy(sessionManagerV9.getOrderEntryV9());
    	}
    }

    protected void initializeOrderCache() throws Exception {
        try {
            //Create and register these caches with the IEC
            ChannelKey key;

            //Register the Caches directly with the IEC
            orderQueryCache = new OrderQueryCacheProxy();
            key = new ChannelKey(ChannelType.CB_ALL_ORDERS, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), orderQueryCache, key);

            orderBookManager = new TradersOrderBookManager();
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), orderBookManager, key);

            filledReportCache = new FilledReportCache();
            key = new ChannelKey(ChannelType.CB_FILLED_REPORT, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), filledReportCache, key);

            key = new ChannelKey(ChannelType.CB_QUOTE_FILLED_REPORT, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), filledReportCache, key);

            bustReportCache = new BustReportCache();
            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), bustReportCache, key);
        } catch (Exception e) {

            GUILoggerHome.find().exception(TRANSLATOR_NAME+": initializeOrderCache()","",e);
            throw e;
        }
    }

    protected void initializeSessionlessProductCache() throws Exception
    {
        try
        {
            ProductType[] productTypes = getAllProductTypes();
            for(int i = 0 ; i<productTypes.length; i++)
            {
                getAllClassesForType(productTypes[i].getType());
            }
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": .initializeSessionlessProductCache()","",e);
        }
    }

    protected void initializeSessionProductCache() throws Exception
    {
        try {
            tradingSessionCache = new TradingSessionCache();
            TradingSessionStruct[] sessions = getCurrentTradingSessions(tradingSessionCache);
            tradingSessionCache.addTradingSessions(sessions);

            for (int i = 0; i < sessions.length; i++) {
               initializeProductCache(sessions[i].sessionName);
               productProcessor.subscribeForSessionProductEvents(sessions[i].sessionName);
               BookDepthProcessor bookDepthProcessor = new BookDepthProcessor(sessions[i].sessionName);
               bookDepthProcessor.subscribeForBookDepthEvents(sessions[i].sessionName);
            }
        } catch (Exception e) {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": .initializeSessionCache()","",e);
            throw e;
        }
    }

    protected void initializeSessionCaches(String sessionName)
    {
        initializeProductCache(sessionName);
    }

    protected void initializeProductCache(String sessionName)
    {
        try
        {
            ProductType[] productTypes = getProductTypesForSession(sessionName);
            for ( int i = 0; i < productTypes.length; i++ ) {
                  loadProductClassesForSession(sessionName, productTypes[i].getType());
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": .initializeProductCache()","",e);
        }
    }

    protected void initializeCallbackConsumers()
    {
        // create the CMICallback consumer cache factory for the eventChannel
        //  -- CurrentMarket, NBBO, Recap, and BookDepth consumers will be cached by Session/ProductClass
        cmiConsumerCacheFactory = new CMICallbackConsumerCacheFactoryImpl(eventChannel);
        // Create all the CMICallback objects

        expectedOpeningPriceConsumer = ExpectedOpeningPriceConsumerFactory.create(eventChannel);
        orderStatusConsumer = OrderStatusConsumerFactory.create(eventChannel);
        classStatusConsumer = ClassStatusConsumerFactory.create(eventChannel);
        productStatusConsumer = ProductStatusConsumerFactory.create(eventChannel);
        strategyStatusConsumer = StrategyStatusConsumerFactory.create(eventChannel);
        tradingSessionStatusConsumer = TradingSessionStatusConsumerFactory.create(eventChannel);
        tickerConsumer = TickerConsumerFactory.create(eventChannel);
    }

    protected void initializeCallbackV2Consumers()
    {
        // create the CMICallback consumer cache factory for the eventChannel
        //  -- CurrentMarket, NBBO, Recap, and BookDepth consumers will be cached by Session/ProductClass
        cmiConsumerCacheFactoryV2 = new CMICallbackV2ConsumerCacheFactoryImpl(eventChannel);

        // Create V2 callback consumers
        //     orderBookConsumerV2 = OrderBookV2ConsumerFactory.create(eventChannel);
        orderBookUpdateConsumerV2 = OrderBookUpdateV2ConsumerFactory.create(eventChannel);
        orderStatusConsumerV2 = OrderStatusV2ConsumerFactory.create(eventChannel);
        tickerConsumerV2 = TickerV2ConsumerFactory.create(eventChannel);
        
        quoteStatusConsumerV2 = QuoteStatusV2ConsumerFactory.create(eventChannel);
    }

    protected void initializeCallbackV3Consumers()
    {
        cmiConsumerCacheFactoryV3 = new CMICallbackV3ConsumerCacheFactoryImpl(eventChannel);

        // Auction/RFP Consumer or
        auctionConsumer = AuctionConsumerFactory.create(eventChannel);
    }

    /**
     * cleanup()
     * @description This method cleans up all user related stuff ( caches, callbacks, etcc )
     * @param       none
     * @returns     void
     */
    public void cleanUp()
    {
        cleanupCaches();
        cleanupCachesV2();
        cleanupCachesV3();

        cleanupCallbackV2Consumers();
        cleanupCallbackConsumers();
        cleanupCallbackV3Consumers();

        cleanupSessionManagerV2Interfaces();
        cleanupSessionManagerInterfaces();
        cleanupSessionManagerV3Interfaces();

        eventChannel = null;
    }

    protected void cleanupCaches()
    {
        cleanupSessionCache();
        cleanupOrderCache();
    }

    protected void cleanupCachesV2()
    {}

    protected void cleanupCachesV3()
    {}

    protected void cleanupSessionManagerInterfaces()
    {
        marketQuery         = null;
        orderQuery          = null;
        productQuery        = null;
        productDefinition   = null;
        orderEntry          = null;
        tradingSession      = null;
        userPreferenceQuery = null;
        userHistory         = null;
        validSessionProfileUser = null;
    }

    protected void cleanupSessionManagerV2Interfaces()
    {
        marketQueryV2 = null;
        orderQueryV2 = null;
    }

    protected void cleanupSessionManagerV3Interfaces()
    {
        marketQueryV3 = null;
        orderQueryV3  = null;
        orderEntryV3  = null;
        orderEntryV5  = null;
    }

    /**
     * cleanupCallbackConsumers()
     * @description This method cleans up all user related callback consumers
     * @param       none
     * @returns     void
     */
    protected void cleanupCallbackConsumers()
    {
        cmiConsumerCacheFactory.getCurrentMarketConsumerCache().cleanupCallbackConsumers();
        cmiConsumerCacheFactory.getRecapConsumerCache().cleanupCallbackConsumers();
        cmiConsumerCacheFactory.getNBBOConsumerCache().cleanupCallbackConsumers();
        cmiConsumerCacheFactory.getBookDepthConsumerCache().cleanupCallbackConsumers();

        expectedOpeningPriceConsumer    = null;
        orderStatusConsumer             = null;
        classStatusConsumer             = null;
        productStatusConsumer           = null;
        strategyStatusConsumer          = null;
        tradingSessionStatusConsumer    = null;
        tickerConsumer                  = null;
        userSessionAdminConsumer        = null;
    }

    protected void cleanupCallbackV2Consumers()
    {
        cmiConsumerCacheFactoryV2.getCurrentMarketConsumerCache().cleanupCallbackConsumers();
        cmiConsumerCacheFactoryV2.getRecapConsumerCache().cleanupCallbackConsumers();
        cmiConsumerCacheFactoryV2.getNBBOConsumerCache().cleanupCallbackConsumers();
        cmiConsumerCacheFactoryV2.getBookDepthConsumerCache().cleanupCallbackConsumers();
        cmiConsumerCacheFactoryV2.getEOPConsumerCache().cleanupCallbackConsumers();

//       orderBookConsumerV2            = null;
        orderBookUpdateConsumerV2 = null;
        orderStatusConsumerV2 = null;
        tickerConsumerV2 = null;
        
        quoteStatusConsumerV2 = null;
    }

    protected void cleanupCallbackV3Consumers()
    {
        cmiConsumerCacheFactoryV3.getCurrentMarketConsumerCache().cleanupCallbackConsumers();

        auctionConsumer = null;
    }

    /**
     * cleanupSessionCache()
     * @description This method cleans up all user/sessiom related caches
     * @param       none
     * @returns     void
     */
    private void cleanupSessionCache()
    {
        try
        {
            TradingSessionStruct[] sessions = tradingSessionCache.getCurrentTradingSessions();
            for (int i = 0; i < sessions.length; i++)
            {
                cleanupProductCache(sessions[ i ].sessionName);
                sessions[ i ] = null;
            }
            unsubscribeTradingSessionStatus(tradingSessionCache);
            tradingSessionCache = null;
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": cleanupSessionCache()","",e);
        }
    }

    /**
     * cleanupProductCache(String sessionName)
     * @description This method cleans up all user/sessiom related caches
     * @param       none
     * @returns     void
     */
    private void cleanupProductCache(String sessionName)
    {
    }

    /**
     * When all listeners have unsubscribed from the internal eventChannel, cleanUp and unsubscribe the UserMarketDataCache from the CAS.
     * added 6/2002
     */
    private void cleanupProductCache(String sessionName, int classKey)
    {
        getUserMarketDataCacheProxy(sessionName).removeAllInterestForClass(classKey);
    }

    protected UserMarketDataCacheProxy getUserMarketDataCacheProxy(String sessionName)
    {
        return UserMarketDataCacheFactory.find(sessionName, this);
    }

    /**
     * cleanupOrderCache()
     * @description This method cleans up users orders cache object(s)
     * @param       none
     * @returns     void
     */
    private void cleanupOrderCache()
    {
        try {
            //Create and register these caches with the IEC
            ChannelKey key;

            //Un - register the Caches directly with the IEC
            key = new ChannelKey(ChannelType.CB_ALL_ORDERS, new Integer(0));
            EventChannelAdapterFactory.find().removeChannelListener(EventChannelAdapterFactory.find(), orderQueryCache, key);

            EventChannelAdapterFactory.find().removeChannelListener(EventChannelAdapterFactory.find(), orderBookManager, key);

            key = new ChannelKey(ChannelType.CB_FILLED_REPORT, new Integer(0));
            EventChannelAdapterFactory.find().removeChannelListener(EventChannelAdapterFactory.find(), filledReportCache, key);

            key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT, new Integer(0));
            EventChannelAdapterFactory.find().removeChannelListener(EventChannelAdapterFactory.find(), bustReportCache, key);

            // these 2 may cause corba comm failures because the CAS will unsubscribe them for me
            // but it may have a time delay and events may come in an attempt to update each cache.
            orderQueryCache     = null;
            orderBookManager    = null;

            filledReportCache   = null;
            bustReportCache     = null;

        } catch (Exception e) {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": cleanupOrderCache()","",e);
        }
    }

    /**
     * @description retrieves all cached orders and register the event channel listener
     * @param clientListener the listener to subscribe
     * @returns OrderDetailStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public OrderDetailStruct[] getAllOrders(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAllOrders", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);
        }
        subscribeOrders(clientListener);
        return orderQueryCache.getAllOrders();
    }

    /**
     * @description Initialize UserMarketDataCche if not initialized for the given class key.
     *              Returns current UserMarketData for given class key
     *
     * @param classtKey the class key to subscribe user market data to.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @retutn UserMarketDataStruct[] Array of UserMarketDataStruct objects
     */
    public UserMarketDataStruct[] getUserMarketData(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn()&& GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getUserMarketData",GUILoggerBusinessProperty.MARKET_QUERY,argObj);
        }

        // don't allow null listener
        if(clientListener == null)
        {
            throw new IllegalArgumentException(TRANSLATOR_NAME+".getUserMarketData() -- EventChannelListener can't be null");
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_USER_MARKET_DATA,  new SessionKeyContainer(sessionName, classKey));
        // this subscription was moved to addUserMarketDataListener
        //eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        addUserMarketDataListener(clientListener, key, sessionName, classKey);

        return getUserMarketDataCacheProxy(sessionName).getUserMarketDataForClass(classKey);
    }

    /**
     * @description Initialize UserMarketDataCche if not initialized for the given product key.
     *              Returns current UserMarketData for given product key
     *
     * @param classtKey the product key to subscribe user market data to.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @retutn UserMarketDataStruct
     */
    public UserMarketDataStruct getUserMarketDataByProduct( String sessionName, int productKey, EventChannelListener clientListener )
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getUserMarketDataByProduct",GUILoggerBusinessProperty.MARKET_QUERY,argObj);
        }

        SessionKeyContainer productKeyObj = new SessionKeyContainer(sessionName, productKey);

        int classKey = getProductByKey(productKey).getProductKeysStruct().classKey;
        if (clientListener != null) {
            ChannelKey key = new ChannelKey(ChannelType.CB_USER_MARKET_DATA_BY_PRODUCT, productKeyObj);
            // this subscription was moved to addUserMarketDataListener
            //eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);

            // this will add the eventChannel listener and increment the listener count for the Session/Class
            addUserMarketDataListener(clientListener, key, sessionName, classKey);
        }

        return getUserMarketDataCacheProxy(sessionName).getUserMarketDataForProduct(classKey, productKey);
    }

    /**
     * Unsubscribes the client listener from receiving User Market Data info
     * for the given product key.
     *
     * @param productKey the product key to unsubscribe for
     * @param clientListener the client listener to unsubscribe from receiving data
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public void unsubscribeUserMarketDataByProduct(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = productKey;
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeUserMarketDataByProduct",
                                       GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        if(clientListener != null)
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_USER_MARKET_DATA_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
            // this will remove the eventChannel listener and decrement the listener count for the Session/Class
            removeUserMarketDataListener(clientListener, key, sessionName, getProductByKey(productKey).getProductKeysStruct().classKey);
        }
    }

    /**
     * Unsubscribes the client listener from receiving User Market Data info
     * for the given class key.
     *
     * @param classKey the product key to unsubscribe personal best book data from.
     * @param clientListener the client listener to unsubscribe from receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribeUserMarketData(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeUserMarketData",GUILoggerBusinessProperty.MARKET_QUERY,argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_USER_MARKET_DATA, new SessionKeyContainer(sessionName, classKey));
        // this unsubscribe was moved to removeUserMarketDataListener
        //eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);

        // this will remove the eventChannel listener and decrement the listener count for the Session/Class
        removeUserMarketDataListener(clientListener, key, sessionName, classKey);
    }

     //////////////////////////// begin OrderEntryAPI methods ////////////////////////
     /**
         * Gets all order detail information for a product for the connected user.
         *
         * @author Derek T. Chambers-Boucher
         *
         * @return an array of OrderDetailStructs containing order detail information for the productKey.
         *
         * @param productKey the product key to get detail information for.
         * @param clientListener the subscribing listener.
         * @exception SystemException
         * @exception CommunicationException
         * @exception AuthorizationException
         * @exception DataValidationException
         */
    public OrderDetailStruct[] getOrdersForProduct(int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        OrderDetailStruct[] orders;

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(productKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getOrdersForProduct",GUILoggerBusinessProperty.ORDER_QUERY,argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_FOR_PRODUCT, new Integer(productKey));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);

        orders = orderQueryCache.getOrdersForProduct(productKey);

        return orders;
    }

    public OrderDetailStruct[] getOrdersForSession(String sessionName, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        OrderDetailStruct[] orders;
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getOrdersForSession",GUILoggerBusinessProperty.ORDER_QUERY,argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_FOR_SESSION, sessionName);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);

        orders = orderQueryCache.getOrdersForSession(sessionName);

        return orders;
    }

    /**
     * Gets order detail information from an order id.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return OderDetailStruct
     *
     * @param orderId the order id to query for.
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public OrderDetailStruct getOrderById(OrderIdStruct orderId)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getOrderById(orderId, false);
    }

    /**
     * Gets order detail information from an order id.
     *
     * @return OderDetailStruct
     *
     * @param orderId the order id to query for.
     * @param cacheOnly if true only tries to find the order in the local order cache
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public OrderDetailStruct getOrderById(OrderIdStruct orderId, final boolean cacheOnly)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        OrderDetailStruct order = null;

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getOrderById",GUILoggerBusinessProperty.ORDER_QUERY,orderId);
        }

        if(cacheOnly)
        {
            order = orderQueryCache.getOrderById(orderId);
        }
        else if (isOmtRole())
        {
            OrderStruct struct = APIHome.findOrderManagementTerminalAPI().getOrderByIdV2(orderId);
            if(struct != null)
            {
                order = buildOrderDetailStruct(struct);
            }
        }
        else
        {
        	if (!isHDRole())
        	{
        		order = orderQueryCache.getOrderById(orderId);
        	}
            if ( order == null )
            {
           order = orderQuery.getOrderById(orderId);
            }
           orderQueryCache.addOrder(order);
        }
        return order;
    }

    /**
     * Gets all the connected users orders for the given type and registers a
     * client-side callback listener.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return an array of OrderDetailStructs.
     *
     * @param productType the product to get orders for.
     * @param clientListener the subscribing listener.
     *
     * @exception NotFoundException if the product is not found.
     */
    public OrderDetailStruct[] getAllOrdersForType(short productType, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        OrderDetailStruct[] orders;

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Short(productType);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAllOrdersForType",GUILoggerBusinessProperty.ORDER_QUERY,argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_ORDERS_FOR_TYPE, new Short(productType));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);

        orders = orderQueryCache.getAllOrdersForType(productType);

        return orders;
    }

    /**
     * Gets orders by class and registers a client-side listener to continue receiving
     * updates for this class key.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return an array of OrderDetailStructs containing orders for this product class.
     *
     * @param classKey the product class to get orders for.
     * @param clientListener the subscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public OrderDetailStruct[] getOrdersByClass(int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getOrdersByClass",GUILoggerBusinessProperty.ORDER_QUERY,argObj);
        }

        if (clientListener != null) {
            ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_BY_CLASS, new Integer(classKey));
            eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        }

        OrderDetailStruct[] orders = orderQueryCache.getOrdersByClass(classKey);

        return orders;
    }

    ////////////////////////////// end of OrderQueryAPI impl /////////////////////////
    ////////////////////////////// begin ProductQueryAPI impl ///////////////////////
    /**
     * Gets all product classes whose reporting class symbols start with
     * the given String prefix.
     *
     * <B>NOTE:</B>  This method is a ProductQueryAPI local method.  There
     * is no CAS support for this request on the ProductQuery interface.
     *
     * @return a sequence of ClassStructs that met the selection criteria.
     * @param prefix the selection criteria all classes must start with to be included.
     */
    public ProductClass[] getProductClassesByAlpha(String prefix)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getProductClassesByAlpha",GUILoggerBusinessProperty.PRODUCT_QUERY,prefix);
        }

        return ProductQueryCacheFactory.find().getProductClassesByAlpha(prefix);
    }

    public ProductType[] getAllProductTypes()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ProductTypeStruct[] structs = ProductQueryCacheFactory.find().getProductTypes();
        if(structs.length == 0)
        {
            structs = loadProductTypes();
        }

        ProductType[] structWrappers = new ProductType[structs.length];

        for(int i = 0; i < structs.length; i++)
        {
            ProductTypeStruct struct = structs[i];
            structWrappers[i] = ProductTypeFactory.create(struct);
        }

        return structWrappers;
    }

    /**
     * Gets the ProductType that represents the passed short type
     * @param type as a short to get
     * @return ProductType interface representing the full product type
     */
    public ProductType getProductType(short type)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        ProductType productType = null;

        ProductType[] allTypes = getAllProductTypes();

        for(int i = 0; i < allTypes.length; i++)
        {
            if(allTypes[i].getType() == type)
            {
                productType = allTypes[i];
                break;
            }
        }

        if(productType == null)
        {
            throw ExceptionBuilder.notFoundException("ProductType for passed type:" + type + ". Could not be found.",
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        return productType;
    }

    public ProductClass[] getAllClassesForType(short productType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ProductClass[] classes = ProductQueryCacheFactory.find().getProductClasses(productType, false);
        if (classes.length == 0) {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getAllClassesForType loading product classes", GUILoggerBusinessProperty.PRODUCT_QUERY, productType);
            }
            classes = loadProductClasses(productType);
        }
        return classes;
    }

    public Product[] getAllProductsForClass(int classKey, boolean activeOnly)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try {
           if (isStrategy(classKey)) {
               loadStrategiesForClass(classKey);
           } else {
               loadProductsForClass(classKey);
           }
        } catch (NotFoundException e) {
                DataValidationException de = new DataValidationException();
                de.details = e.details;
                throw de;
        }
        return ProductQueryCacheFactory.find().getProducts(classKey, activeOnly);
    }

    public ProductTypeStruct[] getProductTypes()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ProductTypeStruct[] types;
        if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getProductTypes", GUILoggerBusinessProperty.PRODUCT_QUERY,"");
        }
        types = ProductQueryCacheFactory.find().getProductTypes();
        if( types.length == 0 )
        {
            types = loadProductTypes();
        }
        return types;
    }


    /**
     * Gets all product types for a passed sessionName.
     * @param sessionName to get product types for
     * @return an array containing all product types.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public ProductType[] getProductTypesForSession(String sessionName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ProductType[] types;

        if( sessionName.equals(DefaultTradingSession.DEFAULT) )
        {
            types = getAllProductTypes();
        }
        else
        {
            ProductTypeStruct[] structs = SessionProductCacheFactory.find(sessionName).getProductTypesForSession();

            if(structs.length == 0)
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

    /**
     * A convenience method to get all SessionProductClass'es for all product types.
     * @param sessionName to get all classes for all types
     * @return and array of SessionProductClass that are contained by passed sessionName for all product types
     */
    public SessionProductClass[] getAllProductClassesForSession(String sessionName)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
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
        spc = (SessionProductClass[]) allSPCs.toArray(spc);

        return spc;
    }

    /**
     * Gets all SessionProductClass'es of the given product type and sessionName.
     * @param sessionName to get SessionProductClass'es for
     * @param productType to get SessionProductClass'es for
     * @return an array of SessionProductClass containing the SessionProductClass'es of the given product type
     *  and sessionName.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public SessionProductClass[] getProductClassesForSession(String sessionName, short productType,
                                                             EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {

        SessionProductClass[] spc;

        if(sessionName.equals(DefaultTradingSession.DEFAULT))
        {
            ProductClass[] pcs = getAllClassesForType(productType);
            spc = new SessionProductClass[pcs.length];
            for( int i = 0; i < pcs.length; i++ )
            {
                ProductClass productClass = pcs[i];
                spc[i] = SessionProductClassFactory.create(sessionName, productClass);
            }
        }
        else
        {
            subscribeSessionClassByType(sessionName, productType, clientListener);
            spc = SessionProductCacheFactory.find(sessionName).getClassesForSession(productType);
        }

        return spc;
    }

    /**
     * Gets all products for the given product class and registers a client-side
     * callback listener to receive further products of the given class.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return an array of ProductStructs containing all products of the given product class.
     *
     * @param classKey the product class key to get product information for.
     * @param includeActiveOnly true to receive active products only, false to receive all products.
     * @param clientListener the subscribing listener.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @author Jing Chen
     * @version 08/07/01
     */
    public SessionProduct[] getProductsForSession(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        subscribeProductsForSession(sessionName, classKey, clientListener);
        return SessionProductCacheFactory.find(sessionName).getProductsForSession(classKey);
    }

    /*
     * subscribe all products for the given product class and registers a client-side
     * callback listener to receive further products of the given class.
     *
     * @param classKey the product class key to get product information for.
     * @param includeActiveOnly true to receive active products only, false to receive all products.
     * @param clientListener the subscribing listener.
     */
    public void subscribeProductsForSession(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try {
            ChannelKey key = new ChannelKey(ChannelType.CB_PRODUCT_STATE_BY_CLASS, new SessionKeyContainer(sessionName,classKey));
            SubscriptionManagerFactory.find().subscribe(key, clientListener, productStatusConsumer);
            key = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
            SubscriptionManagerFactory.find().subscribe(key, clientListener, productStatusConsumer);
            if ( isStrategy(classKey) ){
                getStrategiesByClassForSession(sessionName, classKey, clientListener);
                return;
            }
            loadProductsForClassBySession(sessionName, classKey);
        } catch (NotFoundException e) {
            DataValidationException de = new DataValidationException();
            de.details = e.details;
            throw de;
        }
    }

    /*
     * subscribe all products for the given product class and registers a client-side
     * callback listener to receive further products of the given class.
     *
     * @param classKey the product class key to get product information for.
     * @param includeActiveOnly true to receive active products only, false to receive all products.
     * @param clientListener the subscribing listener.
     * vista
     */
    public void subscribeSessionClassByType(String sessionName, short productType, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key1 = new ChannelKey(ChannelType.CB_CLASS_UPDATE_BY_TYPE, new SessionKeyContainer(sessionName, productType));
        int subscriptionCount1 = SubscriptionManagerFactory.find().subscribe(key1, clientListener, classStatusConsumer);
        ChannelKey key2 = new ChannelKey(ChannelType.CB_CLASS_STATE_BY_TYPE, new SessionKeyContainer(sessionName, productType));
        int subscriptionCount2 = SubscriptionManagerFactory.find().subscribe(key2, clientListener, classStatusConsumer);
        if ( subscriptionCount1 == 1 && subscriptionCount2 == 1 ) {
            try
            {
                loadProductClassesForSession(sessionName, productType);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key1, clientListener, classStatusConsumer);
                SubscriptionManagerFactory.find().unsubscribe(key2, clientListener, classStatusConsumer);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key1, clientListener, classStatusConsumer);
                SubscriptionManagerFactory.find().unsubscribe(key2, clientListener, classStatusConsumer);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key1, clientListener, classStatusConsumer);
                SubscriptionManagerFactory.find().unsubscribe(key2, clientListener, classStatusConsumer);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key1, clientListener, classStatusConsumer);
                SubscriptionManagerFactory.find().unsubscribe(key2, clientListener, classStatusConsumer);
                throw e;
            }
        }
    }

    /**
     * Gets product information for a given product by its name.  If there is a product miss, load all the products in this class
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a ProductStruct containing information about the given product.
     * @param productName the name of the product.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @author Jing Chen
     * @version 08/07/01
     *
     */
    public Product getProductByName(ProductNameStruct productName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        Product product = ProductQueryCacheFactory.find().getProductByName(productName);
        // Cache miss. Retrieve it via cmi and reload products for that class
        if ( product == null ) {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProductByName calling productQuery.getProductByName()", GUILoggerBusinessProperty.PRODUCT_QUERY, productName);
            }
            ProductStruct productStruct = productQuery.getProductByName(productName);
            if (productStruct.productKeys.productType == ProductTypes.STRATEGY) {
                if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
                {
                    GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProductByName calling productQuery.getStrategyByKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, productStruct.productKeys.productKey);
                }
                StrategyStruct strategyStruct= productQuery.getStrategyByKey(productStruct.productKeys.productKey);
                ProductQueryCacheFactory.find().updateStrategy(ProductFactoryHome.find().create(strategyStruct));
                //loadStrategiesForClass(productStruct.productKeys.classKey);
                product = ProductQueryCacheFactory.find().getStrategyByName(productName);
            } else {
                ProductQueryCacheFactory.find().updateProduct(ProductFactoryHome.find().create(productStruct));
                //loadProductsForClass(productStruct.productKeys.classKey);
                product = ProductQueryCacheFactory.find().getProductByName(productName);
            }
            if (product == null)
            {
                 throw ExceptionBuilder.notFoundException("No product found with productName = " + productName + " in translator product cache", 0);
            }
        }
        return product;
    }

    public SessionProduct getAllSelectedSessionProduct(String sessionName)
    {
        return SessionProductFactory.createAllSelected(sessionName);
    }

    public SessionProductClass getAllSelectedSessionProductClass(String sessionName)
    {
        return SessionProductClassFactory.createAllSelected(sessionName);
    }

    public SessionProduct getDefaultSessionProduct(String sessionName)
    {
        return SessionProductFactory.createDefault(sessionName);
    }

    public SessionProductClass getDefaultSessionProductClass(String sessionName)
    {
        return SessionProductClassFactory.createDefault(sessionName);
    }

    public SessionProductClass getAllSelectedSessionProductClass()
    {
        return SessionProductClassFactory.createAllSelected();
    }

    public SessionProduct getAllSelectedSessionProduct()
    {
        return SessionProductFactory.createAllSelected();
    }

    public ProductClass getAllSelectedProductClass()
    {
        return ProductClassFactoryHome.find().createAllSelected();
    }

    public Product getAllSelectedProduct()
    {
        return ProductFactoryHome.find().createAllSelected();
    }

    public SessionProductClass getDefaultSessionProductClass()
    {
        return SessionProductClassFactory.createDefault();
    }

    public SessionProduct getDefaultSessionProduct()
    {
        return SessionProductFactory.createDefault();
    }

    public ProductClass getDefaultProductClass()
    {
        return ProductClassFactoryHome.find().createDefault();
    }

    public Product getDefaultProduct()
    {
        return ProductFactoryHome.find().createDefault();
    }

    public SessionStrategy getDefaultSessionStrategy()
    {
        return SessionProductFactory.createDefaultStrategy();
    }

    public Strategy getDefaultStrategy()
    {
        return ProductFactoryHome.find().createDefaultStrategy();
    }

    /**
     * Gets the reporting class with the passed key
     * @param reportingClassKey to get reporting class for
     * @return reporting class with matching key
     * @exception NotFoundException a reporting class with the passed key could not be found
     */
    public ReportingClass getReportingClassByKey(int reportingClassKey) throws NotFoundException
    {
        ReportingClass reportingClass = null;

        reportingClass = ProductQueryCacheFactory.find().getReportingClassByKey(reportingClassKey);

        if(reportingClass == null)
        {
            throw ExceptionBuilder.notFoundException("Reporting Class does not exist:" + reportingClassKey,
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        return reportingClass;
    }

    public ReportingClass getReportingClassBySymbol(String symbol) throws NotFoundException
    {
        ReportingClass reportingClass = null;

        reportingClass = ProductQueryCacheFactory.find().getReportingClassBySymbol(symbol);

        if(reportingClass == null)
        {
            throw ExceptionBuilder.notFoundException("Reporting Class does not exist:" + symbol,
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        return reportingClass;
    }

    /**
     * Get the product with the reporting class symbol, month opra code and price opra code
     * @param reportingClassSymbol
     * @param monthCode
     * @param priceCode
     * @return product
     */
    public Product getProductBySymbolAndOpraCode(String reportingClassSymbol, char monthCode, char priceCode)
            throws NotFoundException, UserException
    {
        ReportingClass reportingClass = null;
        try
        {
            reportingClass = getReportingClassBySymbol(reportingClassSymbol);
        }
        catch (NotFoundException e)
        {
            throw e;
        }

        Product[] products = null;
        try
        {
            products = getProductsForReportingClass(reportingClass, false);
        }
        catch (UserException e)
        {
            throw e;
        }

        if (products != null)
        {
            for (int p=0; p<products.length; p++)
            {
                char pMonthCode = products[p].getOpraMonthCode();
                char pPriceCode = products[p].getOpraPriceCode();
                if (pMonthCode == monthCode && pPriceCode == priceCode)
                {
                    return products[p];
                }
            }
        }

        return null;
    }

    /**
     * Gets the session reporting class with the passed key for the passed session
     * @param reportingClassKey to get reporting class for
     * @param sessionName to get reporting class for
     * @return reporting class with matching key
     * @exception NotFoundException a reporting class with the passed key could not be found
     * @exception DataValidationException the passed sessionName was invalid
     */
    public SessionReportingClass getReportingClassByKeyForSession(int reportingClassKey, String sessionName)
            throws NotFoundException, DataValidationException
    {
        SessionReportingClass reportingClass = null;

        TradingSessionStruct[] sessions = tradingSessionCache.getCurrentTradingSessions();
        boolean foundSession = false;
        for(int i = 0; i < sessions.length; i++)
        {
            if(sessions[i].sessionName.equals(sessionName))
            {
                foundSession = true;
                break;
            }
        }

        if(!foundSession)
        {
            throw ExceptionBuilder.dataValidationException("Session name was not a valid session:" + sessionName,
                                                           DataValidationCodes.INVALID_SESSION);
        }

        reportingClass = SessionProductCacheFactory.find(sessionName).getReportingClassByKey(reportingClassKey);

        if(reportingClass == null)
        {
            throw ExceptionBuilder.notFoundException("Reporting Class does not exist:" + reportingClassKey +
                                                     ", for session:" + sessionName,
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        return reportingClass;

    }

    /**
     * Gets all the Products for the passed ReportingClass
     * @param reportingClass to get products for
     * @param activeOnly True to get only active classes, false to get active and inactive.
     * @return only Products that have the passed ReportingClass
     */
    public Product[] getProductsForReportingClass(ReportingClass reportingClass, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Product[] products;

        getAllProductsForClass(reportingClass.getProductClassKey().intValue(), activeOnly);

        products = ProductQueryCacheFactory.find().getProductsForReportingClass(reportingClass, activeOnly);
        return products;
    }

    /**
     * Gets all the SessionProducts for the passed SessionReportingClass
     * @param reportingClass to get products for
     * @return only SessionProducts that have the passed ReportingClass
     */
    public SessionProduct[] getProductsForReportingClass(SessionReportingClass reportingClass)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionProduct[] products;

        products = SessionProductCacheFactory.find(reportingClass.getTradingSessionName()).getProductsForReportingClass(reportingClass);

        return products;
    }

    /**
     * Gets a product class by key.
     *
     * @param classKey the class
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     * @return the product class struct.
     */
    public ProductClass getProductClassByKey(int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {

        ProductClass productClass = null;

        if(classKey == DEFAULT_CLASS_KEY)
        {
            productClass = getDefaultProductClass();
        }
        else if(classKey == ALL_SELECTED_CLASS_KEY)
        {
            productClass = getAllSelectedProductClass();
        }
        else
        {
            productClass = ProductQueryCacheFactory.find().getProductClassByKey(classKey);
        }

        if(productClass == null)
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProductClassByKey calling productQuery.getClassByKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, classKey);
            }
            ClassStruct rawClass = productQuery.getClassByKey(classKey);
            ProductClass rawProductClass = ProductClassFactoryHome.find().create(rawClass);
            ProductQueryCacheFactory.find().addClass(rawProductClass, rawProductClass.getProductType());
            productClass = ProductQueryCacheFactory.find().getProductClassByKey(classKey);
        }

        return productClass;
    }

    /**
     * gets class by sessionName and classKey.
     * @param sessionName   String
     * @param classKey      int
     * @return SessionProductClass
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public SessionProductClass getClassByKeyForSession(String sessionName, int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductClass sessionProductClass = null;

        if(classKey == DEFAULT_CLASS_KEY)
        {
            sessionProductClass = getDefaultSessionProductClass(sessionName);
        }
        else if(classKey == ALL_SELECTED_CLASS_KEY)
        {
            sessionProductClass = getAllSelectedSessionProductClass(sessionName);
        }
        else if(sessionName.equals(DefaultTradingSession.DEFAULT))
        {
            ProductClass productClass = getProductClassByKey(classKey);
            sessionProductClass = SessionProductClassFactory.create(sessionName,  productClass);
        }
        else
        {
            //attempts to find the product from cache
            sessionProductClass = SessionProductCacheFactory.find(sessionName).getClassByKey(classKey);
        }

        // if not found, calls CAS tradingSession to get class and stores it at the cache
        if (sessionProductClass == null)
        {
            Object[] argObj = new Object[]{sessionName, classKey};
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getClassByKeyForSession calling tradingSession.getClassBySessionForKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
            }
            SessionClassStruct sessionClassStruct = tradingSession.getClassBySessionForKey(sessionName, classKey);
            sessionProductClass = SessionProductClassFactory.create(sessionClassStruct);
            ProductClass productClass = ProductClassFactoryHome.find().create(sessionClassStruct.classStruct);
            ProductQueryCacheFactory.find().addClass(productClass, sessionClassStruct.classStruct.productType);
            productProcessor.addClass(sessionClassStruct);
        }

        return sessionProductClass;
    }

    /**
     * Gets the session product by productKey.  It looks into the cache first.  If not found
     * in cache, call CAS TradingSession to get it and store it in cache
     * @param sessionName     String
     * @param productKey      int
     * @return SessionProduct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public SessionProduct getProductByKeyForSession(String sessionName, int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProduct sessionProduct = null;
        if(productKey == DEFAULT_PRODUCT_KEY)
        {
            sessionProduct = getDefaultSessionProduct(sessionName);
        }
        else if(productKey == DEFAULT_STRATEGY_PRODUCT_KEY)
        {
            sessionProduct = getDefaultSessionStrategy();
        }
        else if(productKey == ALL_SELECTED_PRODUCT_KEY)
        {
            sessionProduct = getAllSelectedSessionProduct(sessionName);
        }
        else
        {
            // attempts to find it in cache
            sessionProduct = SessionProductCacheFactory.find(sessionName).getProductByKey(productKey);
            if(sessionProduct == null)
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
                Object[] argObj = new Object[]{sessionName, productKey};
                if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
                {
                    GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProductByKeyForSession calling tradingSession.getProductBySessionForKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
                }
                sessionProductStruct = tradingSession.getProductBySessionForKey(sessionName, productKey);
                if (sessionProductStruct.productStruct.productKeys.productType == ProductTypes.STRATEGY)
                {
                    if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
                    {
                        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProductByKeyForSession calling tradingSession.getStrategyBySessionForKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
                    }
                   SessionStrategyStruct sessionStrategyStruct = tradingSession.getStrategyBySessionForKey(sessionName, productKey);
                   productProcessor.updateStrategy(sessionStrategyStruct);
                   //loadStrategiesForClassBySession(sessionName, sessionProductStruct.productStruct.productKeys.classKey);
                   sessionProduct = SessionProductCacheFactory.find(sessionName).getStrategyByKey(productKey);
                } else {
                   productProcessor.updateProduct(sessionProductStruct);
                   //loadProductsForClassBySession(sessionName, sessionProductStruct.productStruct.productKeys.classKey);
                   sessionProduct = SessionProductCacheFactory.find(sessionName).getProductByKey(productKey);
                }
                // can this ever be reached?? tradingSession.getProductBySessionForKey(sessionName, productKey) would either 
                // find and return the product (so the cache would've been updated above), or it would throw an exception
                if(sessionProduct == null)
                {
                    sessionProduct = findInactiveSessionProduct(sessionName, productKey);
                }
            }
            catch(NotFoundException e)
            {   // the product is not part of the session, see if we can create an inactive product
                sessionProduct = findInactiveSessionProduct(sessionName, productKey);
            }
            if (sessionProduct == null)
            {
                throw ExceptionBuilder.notFoundException("No product found with key = " + productKey +" in session:" + sessionName + " in translator product cache", 0);
            }
        }
        return sessionProduct;
    }

    protected SessionProduct findInactiveSessionProduct(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProduct sessionProduct = null;
        // try to find it in the session cache for inactive session products
        sessionProduct = SessionProductCacheFactory.find(SessionProduct.NOT_IN_TRADING_SESSION).getProductByKey(productKey);
        if(sessionProduct == null) // not in the cache, so create it
        {
            // There are two possibilities.  The product does not exist anymore, or the
            // product is inactive and consequently out of the session.
            // In the second case, we'll create an InactiveSessionProduct to represent the product.

            // need to get the product struct
            ProductStruct productStruct = productQuery.getProductByKey(productKey);
            // Only create an InactiveSessionProduct for existing inactive products
            if(productStruct != null && productStruct.listingState != ListingStates.ACTIVE)
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


    /**
     * Gets the session product by product name.  It looks into the cache first.  If not found
     * in cache, call CAS TradingSession to get it and store it in cache
     * @param sessionName     String
     * @param productName      ProductNameStruct
     * @return SessionProduct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public SessionProduct getProductByNameForSession(String sessionName, ProductNameStruct productName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        // attempts to find it in cache
        SessionProduct sessionProduct = SessionProductCacheFactory.find(sessionName).getProductByName(productName);
        if(sessionProduct == null)
        {   // try to find in inactive session products cache
            sessionProduct = SessionProductCacheFactory.find(SessionProduct.NOT_IN_TRADING_SESSION).getProductByName(productName);
        }
        // if not found in cache, call CAS tradingSession to get the product and add the product into the cache
        if (sessionProduct == null) {
            try
            {
                Object[] argObj = new Object[]{sessionName, productName};

                if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
                {
                    GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProductByNameForSession calling tradingSession.getProductBySessionForName()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
                }

                SessionProductStruct sessionProductStruct = tradingSession.getProductBySessionForName(sessionName, productName);

                if (sessionProductStruct.productStruct.productKeys.productType == ProductTypes.STRATEGY)
                {
                    if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
                    {
                        argObj = new Object[]{sessionName, sessionProductStruct.productStruct.productKeys.productKey};
                        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProductByNameForSession calling tradingSession.getStrategyBySessionForKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
                    }
                    SessionStrategyStruct sessionStrategyStruct = tradingSession.getStrategyBySessionForKey(sessionName,sessionProductStruct.productStruct.productKeys.productKey);
                    productProcessor.updateStrategy(sessionStrategyStruct);
                    //loadStrategiesForClassBySession(sessionName, sessionProductStruct.productStruct.productKeys.classKey);
                    sessionProduct = SessionProductCacheFactory.find(sessionName).getStrategyByKey(sessionProductStruct.productStruct.productKeys.productKey);
                } else {
                    productProcessor.updateProduct(sessionProductStruct);
                    //loadProductsForClassBySession(sessionName, sessionProductStruct.productStruct.productKeys.classKey);
                    sessionProduct = SessionProductCacheFactory.find(sessionName).getProductByKey(sessionProductStruct.productStruct.productKeys.productKey);
                }
                if(sessionProduct == null)
                {
                    ProductStruct productStruct = productQuery.getProductByName(productName);
                    if( productStruct != null )
                    {
                        sessionProduct = findInactiveSessionProduct(sessionName, productStruct.productKeys.productKey);
                    }
                }
            }
            catch(NotFoundException e)
            {   // the product is not part of the session, see if we can create an inactive product
                ProductStruct productStruct = productQuery.getProductByName(productName);
                if( productStruct != null )
                {
                    sessionProduct = findInactiveSessionProduct(sessionName, productStruct.productKeys.productKey);
                }
            }

            if (sessionProduct == null)
            {
                 throw ExceptionBuilder.notFoundException("No product found with productName = " + productName + " in session = "+ sessionName+" in translator product cache", 0);
            }
        }
        return sessionProduct;
    }

    /**
     * Gets the session product class by product type and class name.  It looks into the cache first.  If not found
     * in cache, call CAS TradingSession to get it and store it in cache
     * @param sessionName     St
     * ring
     * @param productType     short
     * @param className       String
     * @return SessionProductClass
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     * @author Jing Chen
     * @version 11/22/00
     */
    public SessionProductClass getClassBySymbolForSession(String sessionName, short productType, String className)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        // attempts to find it in cache
        SessionProductClass sessionClass = SessionProductCacheFactory.find(sessionName).getClassBySymbol(productType, className);
        // if not found in cache, call CAS tradingSession to get the class and add the class into the cache
        if (sessionClass == null) {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                Object[] argObj = new Object[3];
                argObj[0] = sessionName;
                argObj[1] = productType;
                argObj[2] = className;

                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getClassBySymbolForSession calling tradingSession.getClassBySessionForSymbol()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
            }
            SessionClassStruct sessionClassStruct = tradingSession.getClassBySessionForSymbol(sessionName, productType, className);
            sessionClass = SessionProductClassFactory.create(sessionClassStruct);
            ProductClass productClass = ProductClassFactoryHome.find().create(sessionClassStruct.classStruct);
            ProductQueryCacheFactory.find().addClass(productClass, sessionClassStruct.classStruct.productType);
            productProcessor.addClass(sessionClassStruct);
        }
        return sessionClass;
    }


    /**
     * Gets a product by key.
     *
     * @param productKey the product
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     * @return the product class struct.
     */
    public Product getProductByKey(int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {

        Product product = null;

        if(productKey == DEFAULT_PRODUCT_KEY)
        {
            product = getDefaultProduct();
        }
        else if(productKey == ALL_SELECTED_PRODUCT_KEY)
        {
            product = getAllSelectedProduct();
        }
        else if(productKey == DEFAULT_STRATEGY_PRODUCT_KEY)
        {
            product = getDefaultStrategy();
        }
        else
        {
            product = ProductQueryCacheFactory.find().getProductByKey(productKey);
        }

        if (product == null)
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProductByKey calling productQuery.getProductByKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, productKey);
            }
            ProductStruct productStruct = productQuery.getProductByKey(productKey);
            if (productStruct.productKeys.productType == ProductTypes.STRATEGY)
            {
                if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
                {
                    GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProductByKey calling productQuery.getStrategyByKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, productKey);
                }
                StrategyStruct strategyStruct= productQuery.getStrategyByKey(productKey);
                ProductQueryCacheFactory.find().updateStrategy(ProductFactoryHome.find().create(strategyStruct));
                //loadStrategiesForClass(productStruct.productKeys.classKey);
                product = ProductQueryCacheFactory.find().getStrategyByKey(productKey);
            } else {
                ProductQueryCacheFactory.find().updateProduct(ProductFactoryHome.find().create(productStruct));
                //loadProductsForClass(productStruct.productKeys.classKey);
                product = ProductQueryCacheFactory.find().getProductByKey(productKey);
            }
            if (product == null)
            {
                throw ExceptionBuilder.notFoundException("No product found with key = " + productKey +" in translator product cache", 0);
            }
        }
        return product;
    }

    /**
     * Gets a product class by type and symbol.
     *
     * @param productType the product type
     * @param className the class name
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     * @return the product class struct.
     */
    public ProductClass getClassBySymbol(short productType, String className)
           throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
        ProductClass productClass = ProductQueryCacheFactory.find().getClassBySymbol(productType, className);
        if ( productClass == null ) {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                Object[] argObj = new Object[2];
                argObj[0] = productType;
                argObj[1] = className;

                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getClassBySymbol calling productQuery.getClassBySymbol()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
            }
            ClassStruct classStruct = productQuery.getClassBySymbol(productType, className);
            ProductClass rawProductClass = ProductClassFactoryHome.find().create(classStruct);
            ProductQueryCacheFactory.find().addClass(rawProductClass, classStruct.productType);
            productClass = ProductQueryCacheFactory.find().getClassBySymbol(productType, className);
        }
        return productClass;
     }
    /**
     * Gets strategies by product key that is used in the strategy legs.
     *
     * @param componentProductKey the product key to get strategies for
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @return the requested strategies.
     */
    public SessionStrategy[] getStrategiesByComponent(String sessionName, int componentProductKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(componentProductKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getStrategiesByComponent", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
        }
        SessionStrategyStruct[] sessionStrategies = tradingSession.getStrategiesByComponent(componentProductKey, sessionName);
        SessionStrategy[] result = new SessionStrategy[sessionStrategies.length];
        int sessionStrategiesLength = sessionStrategies.length;
        for (int i = 0; i < sessionStrategiesLength; i++)
        {
            result[i] = SessionProductFactory.create(sessionStrategies[i]);
        }
        return result;
    }

    /**
     * Gets SessionStrategy by sessionName and productKey.  It looks into the cache first.
     * If not found at cache, it calls CAS tradingSession to get the strategy and stores it at the cache
     * @param sessionName   String
     * @param productKey    int
     * @return SessionStrategyContainer
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidataionException
     */
    public SessionStrategy getStrategyByKeyForSession(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try {
            SessionStrategy sessionStrategy = SessionProductCacheFactory.find(sessionName).getStrategyByKey(productKey);
            if(sessionStrategy == null)
            {   // try to find in inactive session products cache
                sessionStrategy = SessionProductCacheFactory.find(SessionProduct.NOT_IN_TRADING_SESSION).getStrategyByKey(productKey);
            }
            if (sessionStrategy == null)
            {
                try
                {
                    if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
                    {
                        Object[] argObj = new Object[2];
                        argObj[0] = sessionName;
                        argObj[1] = productKey;

                        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getStrategyByKeyForSession calling tradingSession.getStrategyBySessionForKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
                    }
                    SessionStrategyStruct sessionStrategyStruct = tradingSession.getStrategyBySessionForKey(sessionName, productKey);
                    productProcessor.updateStrategy(sessionStrategyStruct);
                    //loadStrategiesForClassBySession(sessionName, sessionStrategyStruct.sessionProductStruct.productStruct.productKeys.classKey);
                    sessionStrategy = SessionProductCacheFactory.find(sessionName).getStrategyByKey(productKey);
                    if( sessionStrategy == null)
                    {
                        sessionStrategy = (SessionStrategy) findInactiveSessionProduct(sessionName, productKey);
                    }
                }
                catch(NotFoundException e)
                {   // the product is not part of the session, see if we can create an inactive product
                    sessionStrategy = (SessionStrategy) findInactiveSessionProduct(sessionName, productKey);
                }
                if (sessionStrategy == null)
                {
                     throw ExceptionBuilder.notFoundException("No strategy found with productKey = " + productKey + " in session = "+ sessionName+" in translator product cache", 0);
                }
            }
            return sessionStrategy;
        } catch (NotFoundException e) {
            DataValidationException de = new DataValidationException();
            de.details = e.details;
            throw de;
        }
    }

    /**
     * Gets product strategy for the given product.
     *
     * @param productKey the product key for the strategy to be retrieved.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     * @return StrategyStruct Strategy struct.
     */

    public Strategy getStrategyByKey(int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {

        Strategy strategy = ProductQueryCacheFactory.find().getStrategyByKey(productKey);
        if (strategy == null){
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getStrategyByKey calling productQuery.getStrategyByKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, productKey);
            }
            StrategyStruct strategyStruct = productQuery.getStrategyByKey(productKey);
            ProductQueryCacheFactory.find().updateStrategy(ProductFactoryHome.find().create(strategyStruct));
            //loadStrategiesForClass(strategyStruct.product.productKeys.classKey);
            strategy = ProductQueryCacheFactory.find().getStrategyByKey(productKey);
        }
        return strategy;
    }

    /**
     * Gets strategies by class.
     *
     * @param classKey the class to get strategies for
     * @param includeActiveOnly active strategies only
     * @param clientListener the subscribing listener
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @return the requested strategies.
     */
    private SessionStrategy[] getStrategiesByClassForSession(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getStrategiesByClassForSession", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
        }
        SessionStrategy[] strategies = new SessionStrategy[0];
        strategies = (SessionStrategy[])SessionProductCacheFactory.find(sessionName).getStrategiesForSession(classKey);
        ChannelKey key = new ChannelKey(ChannelType.CB_STRATEGY_UPDATE, new SessionKeyContainer(sessionName,classKey));
        SubscriptionManagerFactory.find().subscribe(key, clientListener, strategyStatusConsumer);
        strategies = (SessionStrategy[])loadStrategiesForClassBySession(sessionName, classKey);
        return strategies;
    }

    /**
     * getPersonalBestBook gets the traders personal best book for the given
     * product key and subscribes the client listener to receive continued
     * personal best book updates for that product.
     *
     * @return the personal best book struct for the given product key.
     * @param productKey the product key to get personal best book data for.
     * @param clientListener the client listener to subscribe for continued data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PersonalBestBook getPersonalBestBookByProduct(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException,NotFoundException
    {
        if (GUILoggerHome.find().isInformationOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_BOOK))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;

            // this is called continuously, so moving log message to information instead of debug, so it can be separated from other debug logging
            GUILoggerHome.find().information(TRANSLATOR_NAME+": getPersonalBestBookByProduct", GUILoggerBusinessProperty.ORDER_BOOK, argObj);
        }
        PersonalBestBook bestBook = null;

        if (clientListener != null) {
            ChannelKey key = new ChannelKey(ChannelType.CB_PERSONAL_BEST_BOOK, new SessionKeyContainer(sessionName, productKey));
            eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        }
        SessionProduct product = getProductByKeyForSession(sessionName, productKey);

        try
        {
            bestBook = orderBookManager.find(sessionName, product.getProductKey(), product.getProductKeysStruct().classKey).getPersonalBestBook();
        }
        catch(TradersOrderBookInitializationException e )
        {
            GUILoggerHome.find().exception(Category, "Unable to get Personal Best Book for session="+sessionName+
                                                     ", productKey ="+productKey, e);
        }
        return bestBook;
    }

    /**
     * getPersonalBestBook gets the traders personal best book for the given
     * product key and subscribes the client listener to receive continued
     * personal best book updates for that product.
     *
     * @return the personal best book struct for the given product key.
     * @param productKey the product key to get personal best book data for.
     * @param clientListener the client listener to subscribe for continued data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PersonalBestBook[] getPersonalBestBookByClass(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_BOOK))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getPersonalBestBookByClass", GUILoggerBusinessProperty.ORDER_BOOK, argObj);
        }


        if (clientListener != null) {
            ChannelKey key = new ChannelKey(ChannelType.CB_PERSONAL_BEST_BOOK, new SessionKeyContainer(sessionName, classKey));
            eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        }

        SessionProduct[] products = getProductsForSession(sessionName, classKey, null);
        PersonalBestBook[] books = new PersonalBestBook[products.length];

        for (int i=0; i < products.length; i++)
        {
            try
            {
                books[i] = orderBookManager.find(sessionName, products[i].getProductKey(),products[i].getProductKeysStruct().classKey).getPersonalBestBook();
            }
            catch (TradersOrderBookInitializationException e)
            {
                GUILoggerHome.find().exception(Category, "Unable to get Personal Best Book for session="+sessionName+
                                                         ", productKey ="+products[i].getProductKey(), e);
            }
        }
        return books;
    }

    /**
     * Unsubscribes the client listener from receiving personal best book info
     * for the given product key.
     *
     * @param productKey the product key to unsubscribe personal best book data from.
     * @param clientListener the client listener to unsubscribe from receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribePersonalBestBookByProduct(String sessionName, int productKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_PERSONAL_BEST_BOOK, new SessionKeyContainer(sessionName, productKey));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_BOOK))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribePersonalBestBookByProduct", GUILoggerBusinessProperty.ORDER_BOOK, argObj);
        }
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Unsubscribes the client listener from receiving personal best book info
     * for the given product key.
     *
     * @param productKey the product key to unsubscribe personal best book data from.
     * @param clientListener the client listener to unsubscribe from receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void unsubscribePersonalBestBookByClass(String sessionName, int classKey, EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_PERSONAL_BEST_BOOK, new SessionKeyContainer(sessionName, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_BOOK))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribePersonalBestBookByClass", GUILoggerBusinessProperty.ORDER_BOOK, argObj);
        }
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Unsubscribes the client listener from receiving all orders from the CAS.
     *
     * @param clientListener the unsubscribing listener.
     */
    public void unsubscribeAllOrders(EventChannelListener clientListener)
    {
        // This is only an unsubscription from the local IEC.  There is no way to
        // unsubscribe from order status info via the API.
        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_ORDERS, new Integer(0));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeAllOrders", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);
        }
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Subscribes the client listener to receive filledReport info
     *
     * @param clientListener the client listener to subscribe for receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public OrderFilledReportStruct[] getOrderFilledReports(EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getOrderFilledReports", GUILoggerBusinessProperty.ORDER_QUERY, listener);
        }

        subscribeOrderFilledReport(listener);
        return filledReportCache.getFilledReportsForOrders();
    }

    /**
     * Subscribes the client listener to receive OrderBustReport info
     *
     * @param clientListener the client listener to subscribe for receiving data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public OrderBustReportStruct[] getOrderBustReports(EventChannelListener listener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn()  && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getOrderBustReports", GUILoggerBusinessProperty.ORDER_QUERY, listener);
        }

        subscribeOrderBustReport(listener);
        return bustReportCache.getBustReportsForOrders();
    }

    /**
     * Register the event channel listener for the logoff events
     * @usage can be used to subscribe for log off events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    public void registerForLogoff(EventChannelListener clientListener)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": registerForLogoff", GUILoggerBusinessProperty.USER_SESSION, clientListener);
        }

        if ( clientListener != null )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_LOGOUT, new Integer(0));
            eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        }
    }

    /**
     * Register the event channel listener for the text message events
     * @usage can be used to subscribe for log off events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    public void registerForTextMessage(EventChannelListener clientListener)
    {
        if (GUILoggerHome.find().isDebugOn()  && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.TEXT_MESSAGE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": registerForTextMessage", GUILoggerBusinessProperty.TEXT_MESSAGE, clientListener);
        }

        if ( clientListener != null )
        {
            if(getUserRole() != Role.CLASS_DISPLAY)
            {
                ChannelKey key = new ChannelKey(ChannelType.CB_TEXT_MESSAGE, new Integer(0));
                eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
            }
        }
    }

    /**
     * Logs the current user out of the CAS.
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     */
    public void logout()
           throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": logout", GUILoggerBusinessProperty.USER_SESSION, "");
        }

        sessionManager.logout();
        cleanUp();
        UserAccessFactory.unregisterClientListener( userClientListener );

    }

    /**
     * @description Change the password for the current user
     * @param oldPassword - old password
     * @param confirmOldPassword - confirmation of the old password
     * @param newPassword - confirmation of the new password
     * @usage used to change the user's password
     * @returns void
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void changePassword(String oldPassword, String newPassword)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_MANAGEMENT) )
        {
            Object[] argObj = new Object[2];
            argObj[0] = oldPassword;
            argObj[1] = newPassword;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": changePassword", GUILoggerBusinessProperty.USER_MANAGEMENT,"");
        }

        sessionManager.changePassword(oldPassword, newPassword);
    }

    public UserStructModel getValidUser()
            throws SystemException, CommunicationException, AuthorizationException
    {
        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_SESSION) )
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getValidUser",
                                       GUILoggerBusinessProperty.USER_SESSION, "");
        }

        if( validSessionProfileUser == null )
        {
            SessionProfileUserStruct struct = sessionManager.getValidSessionProfileUser();
            if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_SESSION))
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getValidUser",
                                           GUILoggerBusinessProperty.USER_SESSION, struct);
            }
            validSessionProfileUser = new UserStructModelImpl(struct);
        }
        return validSessionProfileUser;
    }

    /**
     * @description get the current CMI version information
     * @usage can be used to verify or report API version
     * @returns string containing the version of the CMI interface
     */
    public String getVersion()
           throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getVersion",GUILoggerBusinessProperty.USER_SESSION, "");
        }
        return sessionManager.getVersion();
    }

    public void subscribeRecapForClass(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS, new SessionKeyContainer(sessionName, classKey));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeRecapForClass", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        CMIRecapConsumer recapConsumer = cmiConsumerCacheFactory.getRecapConsumerCache().getRecapConsumer(sessionName, classKey);

        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, recapConsumer) == 1) {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeRecapForClass", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for Recap for session='"+sessionName+"' classKey="+classKey);
            try
            {
                marketQuery.subscribeRecapForClass(sessionName, classKey, recapConsumer);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumer);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumer);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumer);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumer);
                throw e;
            }
        }
    }

    public void subscribeRecapForProduct(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey productChannelKey = new ChannelKey(ChannelType.CB_RECAP_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeRecapForProduct", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
            // for CurrentMarket by SessionProduct, use the CMICurrentMarketConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;
            ChannelKey classChannelKey = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
            CMIRecapConsumer recapConsumer = cmiConsumerCacheFactory.getRecapConsumerCache().getRecapConsumer(sessionName, classKey);

            if (clientListener != null)
            {
                eventChannel.addChannelListener(eventChannel,clientListener,productChannelKey);
            }


            if (SubscriptionManagerFactory.find().subscribe(classChannelKey, null, recapConsumer) == 1) {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeRecapForProduct", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for Recap for session='"+sessionName+"' productKey="+productKey);
                try
                {
                    marketQuery.subscribeRecapForClass(sessionName, classKey, recapConsumer);
                }
                catch (SystemException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, recapConsumer);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
                catch (CommunicationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, recapConsumer);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
                catch (AuthorizationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, recapConsumer);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
                catch (DataValidationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, recapConsumer);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }

            }

        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeRecapForProduct", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void subscribeCurrentMarketForProduct(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeCurrentMarketForProduct", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));

        try
        {
            // for CurrentMarket by SessionProduct, use the CMICurrentMarketConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            CMICurrentMarketConsumer currentMarketConsumer = cmiConsumerCacheFactory.getCurrentMarketConsumerCache().getCurrentMarketConsumer(sessionName, classKey);

            // if this was the first listener on the IEC, subscribe the translator to the CAS
            if (SubscriptionManagerFactory.find().subscribe(key, clientListener, currentMarketConsumer) == 1) {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeCurrentMarketForProduct", GUILoggerBusinessProperty.MARKET_QUERY, "subscribing translator to CAS for CurrentMarket for session='"+sessionName+"' productKey="+productKey);
                try
                {
                    marketQuery.subscribeCurrentMarketForProduct(sessionName, productKey, currentMarketConsumer);
                }
                catch (SystemException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumer);
                    throw e;
                }
                catch (CommunicationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumer);
                    throw e;
                }
                catch (AuthorizationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumer);
                    throw e;
                }
                catch (DataValidationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumer);
                    throw e;
                }
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeCurrentMarketForProduct", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void subscribeBookDepth(SessionProduct sessionProduct, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionProduct.getTradingSessionName();
            argObj[1] = new Integer(sessionProduct.getProductKey());
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepth", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT, new SessionKeyContainer(sessionProduct.getTradingSessionName(), sessionProduct.getProductKey()));

        // For BookDepth by SessionProduct, use the CMIOrderBookConsumer for the SessionProductCLASS.
        // Caching a separate consumer per product would create too many consumers.
        CMIOrderBookConsumer bookDepthConsumer = cmiConsumerCacheFactory.getBookDepthConsumerCache().getBookDepthConsumer(sessionProduct.getTradingSessionName(),
                    sessionProduct.getProductKeysStruct().classKey);

        // translator only subscribe book depth to CAS once, then it caches for multiple book depth subscription for the same product key
        int subscribeCount = SubscriptionManagerFactory.find().subscribe(key, clientListener, bookDepthConsumer);
        if (subscribeCount == 1)
        {
            try
            {
                marketQuery.subscribeBookDepth(sessionProduct.getTradingSessionName(), sessionProduct.getProductKey(), bookDepthConsumer);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, bookDepthConsumer);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, bookDepthConsumer);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, bookDepthConsumer);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, bookDepthConsumer);
                throw e;
            }

        }
        else // multiple book depth subscriptions for the same product
        if (subscribeCount > 1)
        {
            BookDepthStruct bookDepth = BookDepthCacheFactory.find(sessionProduct.getTradingSessionName()).getBookDepth(new Integer(sessionProduct.getProductKey()));

            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bookDepth);
            eventChannel.dispatch(event);
        }

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[1];
            argObj[0] = sessionProduct.getTradingSessionName();
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepth count = " + subscribeCount, GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }
    }

    public void subscribeBookDepthUpdate(SessionProduct sessionProduct, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        throw ExceptionBuilder.authorizationException("FUNCTION NOT IMPLEMENTED! ", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
    }

    public void unsubscribeBookDepthUpdate(SessionProduct sessionProduct, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        throw ExceptionBuilder.authorizationException("FUNCTION NOT IMPLEMENTED! ", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
    }

    public void subscribeCurrentMarketForClass(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeCurrentMarketForClass", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS, new SessionKeyContainer(sessionName, classKey));

        CMICurrentMarketConsumer currentMarketConsumer = cmiConsumerCacheFactory.getCurrentMarketConsumerCache().getCurrentMarketConsumer(sessionName, classKey);

        // if this was the first listener on the IEC, subscribe the translator to the CAS
        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, currentMarketConsumer) == 1) {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeCurrentMarketForClass", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for CurrentMarket for session='"+sessionName+"' classKey="+classKey);
            try
            {
                marketQuery.subscribeCurrentMarketForClass(sessionName, classKey, currentMarketConsumer);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumer);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumer);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumer);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumer);
                throw e;
            }
        }
    }

    public void subscribeNBBOForClass(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS, new SessionKeyContainer(sessionName, classKey));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeNBBOForClass", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        CMINBBOConsumer anNBBOConsumer = cmiConsumerCacheFactory.getNBBOConsumerCache().getNBBOConsumer(sessionName, classKey);

        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, anNBBOConsumer) == 1) {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeNBBOForClass", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for NBBO for session='"+sessionName+"' classKey="+classKey);
            try
            {
                marketQuery.subscribeNBBOForClass(sessionName, classKey, anNBBOConsumer);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, anNBBOConsumer);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, anNBBOConsumer);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, anNBBOConsumer);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, anNBBOConsumer);
                throw e;
            }
        }
    }

    public void subscribeNBBOForProduct(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeNBBOForProduct", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
            // for NBBO by SessionProduct, use the CMINBBOConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            CMINBBOConsumer anNBBOConsumer = cmiConsumerCacheFactory.getNBBOConsumerCache().getNBBOConsumer(sessionName, classKey);

            if (SubscriptionManagerFactory.find().subscribe(key, clientListener, anNBBOConsumer) == 1) {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeNBBOForProduct", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for NBBO for session='"+sessionName+"' productKey="+productKey);
                try
                {
                    marketQuery.subscribeNBBOForProduct(sessionName, productKey, anNBBOConsumer);
                }
                catch (SystemException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, anNBBOConsumer);
                    throw e;
                }
                catch (CommunicationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, anNBBOConsumer);
                    throw e;
                }
                catch (AuthorizationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, anNBBOConsumer);
                    throw e;
                }
                catch (DataValidationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, anNBBOConsumer);
                    throw e;
                }
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeNBBOForProduct", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    /**
     * Retrieves the book depth for the given product.
     * @param sessionProduct to retrieve Book Depth for.
     * @return BookDepth
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     */
    public BookDepth getCmiBookDepth(SessionProduct sessionProduct)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionProduct.getTradingSessionName();
            argObj[1] = new Integer(sessionProduct.getProductKey());

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getCmiBookDepth", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        BookDepthStruct struct = marketQuery.getBookDepth(sessionProduct.getTradingSessionName(), sessionProduct.getProductKey());

        GUILoggerHome.find().debug(TRANSLATOR_NAME + ".getCmiBookDepth", GUILoggerBusinessProperty.MARKET_QUERY, struct);

        BookDepth book = new BookDepthImpl(struct);

        return book;
    }


    public void subscribeTicker(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey productChannelKey = new ChannelKey(ChannelType.CB_TICKER, new SessionKeyContainer(sessionName, productKey));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeTicker", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try {
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            ChannelKey classChannelKey = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V2, new SessionKeyContainer(sessionName, classKey));

            if (clientListener != null)
            {
                eventChannel.addChannelListener(eventChannel,clientListener,productChannelKey);
            }



            if (SubscriptionManagerFactory.find().subscribe(classChannelKey, null, tickerConsumerV2) == 1) {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeTicker", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for Ticker for session='"+sessionName+"' productKey="+productKey);
                try
                {
                    marketQueryV2.subscribeTickerForClassV2(sessionName, classKey, tickerConsumerV2, DEFAULT_QUEUE_ACTION);

                }
                catch (SystemException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, clientListener, tickerConsumer);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
                catch (CommunicationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, clientListener, tickerConsumer);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
                catch (AuthorizationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, clientListener, tickerConsumer);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
                catch (DataValidationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, clientListener, tickerConsumer);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
            }

        }
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeTicker", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }

    }

    public void unsubscribeCurrentMarketForProduct(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeCurrentMarketForProduct", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
            // for CurrentMarket by SessionProduct, use the CMICurrentMarketConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            CMICurrentMarketConsumer currentMarketConsumer = cmiConsumerCacheFactory.getCurrentMarketConsumerCache().getCurrentMarketConsumer(sessionName, classKey);

            // if this was the last listener on the IEC, unsubscribe the translator from the CAS
            if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumer) == 0) {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeCurrentMarketForProduct", GUILoggerBusinessProperty.MARKET_QUERY, "unsubscribing translator to CAS for session='"+sessionName+"' productKey="+productKey);
                marketQuery.unsubscribeCurrentMarketForProduct(sessionName, productKey, currentMarketConsumer);
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": unsubscribeCurrentMarketForProduct", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void unsubscribeBookDepth(SessionProduct sessionProduct, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {

        ChannelKey key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT, new SessionKeyContainer(sessionProduct.getTradingSessionName(), sessionProduct.getProductKey()));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionProduct.getTradingSessionName();
            argObj[1] = new Integer(sessionProduct.getProductKey());
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeBookDepthForProduct", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        // For BookDepth by SessionProduct, use the CMIOrderBookConsumer for the SessionProductCLASS.
        // Caching a separate consumer per product would create too many consumers.
        CMIOrderBookConsumer bookDepthConsumer = cmiConsumerCacheFactory.getBookDepthConsumerCache().getBookDepthConsumer(sessionProduct.getTradingSessionName(),
                    sessionProduct.getProductKeysStruct().classKey);

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, bookDepthConsumer) == 0) {
            marketQuery.unsubscribeBookDepth(sessionProduct.getTradingSessionName(), sessionProduct.getProductKey(), bookDepthConsumer);

            // empty book depth cache
            BookDepthCacheFactory.find(sessionProduct.getTradingSessionName()).removeBookDepth(new Integer(sessionProduct.getProductKey()));
        }
    }

    public void unsubscribeCurrentMarketForClass(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeCurrentMarketForClass", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        CMICurrentMarketConsumer currentMarketConsumer = cmiConsumerCacheFactory.getCurrentMarketConsumerCache().getCurrentMarketConsumer(sessionName, classKey);

        // if this was the last listener on the IEC, unsubscribe the translator from the CAS
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumer) == 0) {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeCurrentMarketForClass", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for CurrentMarket for session='"+sessionName+"' classKey="+classKey);
            marketQuery.unsubscribeCurrentMarketForClass(sessionName, classKey, currentMarketConsumer);
        }
    }

    public void unsubscribeNBBOForClass(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeNBBOForClass", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        CMINBBOConsumer anNBBOConsumer = cmiConsumerCacheFactory.getNBBOConsumerCache().getNBBOConsumer(sessionName, classKey);

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, anNBBOConsumer) == 0) {
            marketQuery.unsubscribeNBBOForClass(sessionName, classKey, anNBBOConsumer);
        }
    }

    public void unsubscribeNBBOForProduct(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeNBBOForProduct", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
            // for NBBO by SessionProduct, use the CMINBBOConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            CMINBBOConsumer anNBBOConsumer = cmiConsumerCacheFactory.getNBBOConsumerCache().getNBBOConsumer(sessionName, classKey);

            if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, anNBBOConsumer) == 0) {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeNBBOForProduct", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for NBBO for session='"+sessionName+"' productKey="+productKey);
                marketQuery.unsubscribeNBBOForProduct(sessionName, productKey, anNBBOConsumer);
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": unsubscribeNBBOForProduct", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }

    }

    public void unsubscribeRecapForProduct(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey productChannelKey = new ChannelKey(ChannelType.CB_RECAP_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeRecapForProduct", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
            // for CurrentMarket by SessionProduct, use the CMICurrentMarketConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            CMIRecapConsumer recapConsumer = cmiConsumerCacheFactory.getRecapConsumerCache().getRecapConsumer(sessionName, classKey);

            ChannelKey classChannelKey = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
            if (clientListener != null)
            {
                eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
            }

            if (SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, recapConsumer) == 0) {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeRecapForProduct", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for Recap for session='"+sessionName+"' classKey="+classKey);
                marketQuery.unsubscribeRecapForClass(sessionName, classKey, recapConsumer);
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": unsubscribeRecapForProduct", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void unsubscribeRecapForClass(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeRecapForClass", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        CMIRecapConsumer recapConsumer = cmiConsumerCacheFactory.getRecapConsumerCache().getRecapConsumer(sessionName, classKey);

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumer) == 0) {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeRecapForClass", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for Recap for session='"+sessionName+"' classKey="+classKey);
            marketQuery.unsubscribeRecapForClass(sessionName, classKey, recapConsumer);
        }
    }

    public void unsubscribeTicker(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey productChannelKey = new ChannelKey(ChannelType.CB_TICKER, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeTicker", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try {
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            ChannelKey classChannelKey = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V2, new SessionKeyContainer(sessionName, classKey));

            if (clientListener != null)
            {
                eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
            }



            if (SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, tickerConsumer) == 0) {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeTicker", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for Ticker for session='"+sessionName+"' productKey="+productKey);
                marketQueryV2.unsubscribeTickerForClassV2(sessionName, classKey, tickerConsumerV2);
            }
        }
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeTicker", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }

    }

    /**
     * Retrieves the market data history by time for the given product.
     *
     * @param productKey the product key to retrieve for.
     * @param startTime starting time for the history to receive data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     *
     * @author Connie Feng
     */
    public MarketDataHistoryStruct getMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = startTime;
            argObj[3] = new Short(direction);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getMarketDataHistoryByTime", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        MarketDataHistoryStruct struct = marketQueryV3.getMarketDataHistoryByTime(sessionName, productKey, startTime, direction);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getMarketDataHistoryByTime", GUILoggerBusinessProperty.MARKET_QUERY, "Completed...");
        }

        return struct;
    }

    /**
     * Retrieves the detail market data history by time for the given product.
     *
     * @param productKey the product key to retrieve for.
     * @param startTime starting time for the history to receive data.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     *
     */
    public MarketDataHistoryDetailStruct getDetailMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = startTime;
            argObj[3] = new Short(direction);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getDetailMarketDataHistoryByTime", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        MarketDataHistoryDetailStruct struct = marketQueryV3.getDetailMarketDataHistoryByTime(sessionName, productKey, startTime, direction);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getDetailMarketDataHistoryByTime", GUILoggerBusinessProperty.MARKET_QUERY, "Completed...");
        }

        return struct;
    }


    /**
     * Retrieves the priority market data history by time for the given product.
     *
     * @param productKey the product key to retrieve for.
     * @param startTime  starting time for the history to receive data.
     *
     * @return none.
     *
     * @throws com.cboe.exceptions.NotFoundException
     *
     */
    public MarketDataHistoryDetailStruct getPriorityMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = startTime;
            argObj[3] = new Short(direction);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getPriorityMarketDataHistoryByTime", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        MarketDataHistoryDetailStruct struct = marketQueryV3.getPriorityMarketDataHistoryByTime(sessionName, productKey, startTime, direction);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getPriorityMarketDataHistoryByTime", GUILoggerBusinessProperty.MARKET_QUERY, "Completed...");
        }

        return struct;
    }

    public void subscribeExpectedOpeningPrice(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeExpectedOpeningPrice", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE, new SessionKeyContainer(sessionName, classKey));
        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, expectedOpeningPriceConsumer) == 1) {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeExpectedOpeningPrice", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for Expected Opening Price for session='"+sessionName+"' classKey="+classKey);
            try
            {
                marketQuery.subscribeExpectedOpeningPrice(sessionName, classKey, expectedOpeningPriceConsumer);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumer);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumer);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumer);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumer);
                throw e;
            }
        }
    }

    public void subscribeExpectedOpeningPriceByProduct(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeExpectedOpeningPriceByProduct", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        Product product = this.getProductByKey(productKey);
        ChannelKey key = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE, new SessionKeyContainer(sessionName, product.getProductKeysStruct().classKey));

        if (SubscriptionManagerFactory.find().subscribe(key, null, expectedOpeningPriceConsumer) == 1) {
            try
            {
                marketQuery.subscribeExpectedOpeningPrice(sessionName, product.getProductKeysStruct().classKey, expectedOpeningPriceConsumer);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, null, expectedOpeningPriceConsumer);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, null, expectedOpeningPriceConsumer);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, null, expectedOpeningPriceConsumer);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, null, expectedOpeningPriceConsumer);
                throw e;
            }
        }

        key = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
        EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    public void unsubscribeExpectedOpeningPrice(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeExpectedOpeningPrice", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE, new SessionKeyContainer(sessionName, classKey));

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumer) == 0) {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeExpectedOpeningPrice", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for Expected Opening Price for session='"+sessionName+"' classKey="+classKey);
            marketQuery.unsubscribeExpectedOpeningPrice(sessionName, classKey, expectedOpeningPriceConsumer);
        }
    }

    /**
     * Indicates whether the given product name is valid.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return true if the name is valid, false otherwise.
     * @param productName the name to check validity for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public boolean isValidProductName(ProductNameStruct productName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": isValidProductName", GUILoggerBusinessProperty.PRODUCT_QUERY, productName);
        }
        try
        {
            return getProductByName(productName) != null;
        }
        catch (NotFoundException e)
        {
            return false;
        }
    }

    /**
     * Gets pending price adjustments for all products.
     *
     * @return a sequence of pending adjustment info structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     */
    public ProductAdjustmentContainer[] getAllPendingAdjustments()
           throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAllPendingAdjustments", GUILoggerBusinessProperty.PRODUCT_QUERY, "");
        }

        PendingAdjustmentStruct[] adjustments = productQuery.getAllPendingAdjustments();
        ProductAdjustmentContainer[] containers = new ProductAdjustmentContainer[adjustments.length];
// This temporary debug code.
GUILoggerHome.find().information(TRANSLATOR_NAME+": getAllPendingAdjustments"+"PendingAdjustmentStruct[]:\n", GUILoggerBusinessProperty.PRODUCT_QUERY,adjustments);

        for (int i = 0; i < adjustments.length; i++) {
            containers[i] = ProductAdjustmentFactory.create(adjustments[i]);
        }

        return containers;
    }

    /**
     * Gets pending price adjustments for all products based on the
     * given sequence of class keys.
     *
     * @param classKeys the sequence of class keys to retrieve pending adjustments for.
     * @param includeProducts true to include products; false otherwise.
     * @return a sequence of pending adjustment info structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public ProductAdjustmentContainer[] getPendingAdjustments(int classKey, boolean includeProducts)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = new Boolean(includeProducts);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getPendingAdjustments", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
        }

        PendingAdjustmentStruct[] adjustments = productQuery.getPendingAdjustments(classKey, includeProducts);
        ProductAdjustmentContainer[] containers = new ProductAdjustmentContainer[adjustments.length];
        for (int i = 0; i < adjustments.length; i++) {
            containers[i] = ProductAdjustmentFactory.create(adjustments[i]);
        }

        return containers;
    }

    /**
     * Gets pending price adjusted products for the given sequence of classes.
     *
     * @param classKeys the sequence of class keys to retrieve pending adjustment products for.
     * @return a sequence of pending name structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PendingNameContainer[] getPendingAdjustmentProducts(int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getPendingAdjustmentProducts", GUILoggerBusinessProperty.PRODUCT_QUERY, new Integer(classKey));
        }

        PendingNameStruct[] names = productQuery.getPendingAdjustmentProducts(classKey);
        PendingNameContainer[] nameContainers = new PendingNameContainer[names.length];

        for (int i = 0; i < names.length; i++) {
            nameContainers[i] = PendingNameFactory.create(names[i]);
        }
        return nameContainers;
    }

    public void unsubscribeClassesByTypeForSession(String sessionName, short productType, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key;
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Short(productType);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeClassesByTypeForSession", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
        }

        key = new ChannelKey(ChannelType.CB_CLASS_UPDATE_BY_TYPE, new SessionKeyContainer(sessionName, productType));
        int subscriptionCount1 = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, productStatusConsumer);
        key = new ChannelKey(ChannelType.CB_CLASS_STATE_BY_TYPE, new SessionKeyContainer(sessionName, productType));
        int subscriptionCount2 = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, productStatusConsumer);
        if ( subscriptionCount1 == 0 && subscriptionCount2 == 0 ) {
            tradingSession.unsubscribeClassesByTypeForSession(sessionName, productType, classStatusConsumer);
        }
    }

    public void unsubscribeProductsByClassForSession(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key;
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeProductsByClassForSession", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
        }

        key = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
        //int subscriptionCount1 = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, productStatusConsumer);
        SubscriptionManagerFactory.find().unsubscribe(key, clientListener, productStatusConsumer);
        key = new ChannelKey(ChannelType.CB_PRODUCT_STATE_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
        //int subscriptionCount2 = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, productStatusConsumer);
        SubscriptionManagerFactory.find().unsubscribe(key, clientListener, productStatusConsumer);
        // we're never unsubscribing the translator from the CAS for ProductStatus and Product updates, because it could cause
        // problems with the caches getting out of sync.  After the caches are inspected and possibly refactored, this unsubscription
        // might be added again.
//        if ( subscriptionCount1 == 0 && subscriptionCount2 == 0 )
//        {
//            tradingSession.unsubscribeProductsByClassForSession(sessionName, classKey, productStatusConsumer);
//            CacheClassTrackFactory.find(new SessionKeyContainer(sessionName, classKey)).setSessionProductsLoaded(false);
//        }
        try {
            if ( isStrategy(classKey) ){
                unsubscribeStrategiesByClassForSession(sessionName, classKey, clientListener);
            }
        } catch (NotFoundException e) {
            DataValidationException de = new DataValidationException();
            de.details = e.details;
            throw de;
        }
    }

    public void unsubscribeStrategiesByClassForSession(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_STRATEGY_UPDATE, new SessionKeyContainer(sessionName, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeStrategiesByClassForSession", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
        }

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, strategyStatusConsumer) == 0) {
        // we're never unsubscribing the translator from the CAS for ProductStatus and Product updates, because it could cause
        // problems with the caches getting out of sync.  After the caches are inspected and possibly refactored, this unsubscription
        // might be added again.
//            tradingSession.unsubscribeStrategiesByClassForSession(sessionName, classKey, strategyStatusConsumer);
        }
    }

    /**
     * @description Submit an order
     *
     * @usage
     * Must provide a valid order entry struct
     *
     * @returns OrderIdStruct of new order.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     * @exception AlreadyExistsException
     * @see cmiOrder::OrderEntryStruct
     */
    public OrderIdStruct acceptOrder(OrderEntryStruct anOrder)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        String methodName = "acceptOrder";
        logTransaction(methodName, anOrder);
        OrderIdStruct results = null;
        try
        {
            results = orderEntry.acceptOrder(anOrder);
        }
        catch(Exception e)
        {
            handleTransactionException(methodName, e);
        }
        return results;

    }

    /**
     * @description Submit an order
     *
     * @usage
     * Must provide a valid order entry struct
     *
     * @returns OrderIdStruct of new order.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     * @exception AlreadyExistsException
     * @see cmiOrder::OrderEntryStruct
     */
    public OrderIdStruct acceptOrderByProductName(ProductNameStruct productName, OrderEntryStruct anOrder)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        String methodName = "acceptOrderByProductName";
        logTransaction(methodName, productName, anOrder);
        OrderIdStruct orderId = null;
        try
        {
            orderId = orderEntry.acceptOrderByProductName(productName, anOrder);

        }
        catch(Exception e)
        {
            handleTransactionException(methodName, e);
        }
        return orderId;
    }

    
    /**
     * @description
     * Request cancellation of part or all of an order
     *
     * @usage
     * Must be for a valid quantity and an existing order
     *
     * @param orderId identifier of an existing order
     * @param quantityToCancel the quantity to cancel from the order
     * @returns void
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public void acceptOrderCancelRequest( CancelRequestStruct cancelRequest )
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        String methodName = "acceptOrderCancelRequest";
        logTransaction(methodName, cancelRequest);
        try
        {
        orderEntry.acceptOrderCancelRequest( cancelRequest );
    }
        catch(Exception e)
        {
            try
            {
                handleTransactionException(methodName, e);
            }
            catch(AlreadyExistsException ae)
            {
                // Not valid for this method, must be handled
            }
        }
    }
    
    /**
     * @description
     * Request update of part or all of an order
     *
     * @usage
     * Must be for a valid quantity and an existing order
     *
     * @param currentRemainingQuantity The current remaining unfilled quantity of an order
     * @param updatedOrder Updated order
     * @returns void
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public void acceptOrderUpdateRequest(int currentRemainingQuantity, OrderEntryStruct updatedOrder)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        String methodName = "acceptOrderUpdateRequest";
        logTransaction(methodName, currentRemainingQuantity, updatedOrder);
        try
        {
        orderEntry.acceptOrderUpdateRequest(currentRemainingQuantity, updatedOrder);
    }
        catch(Exception e)
        {
            try
            {
                handleTransactionException(methodName, e);
            }
            catch(AlreadyExistsException ae)
            {
                // Not valid for this method, must be handled
            }
        }
    }

    /**
     * @description
     * Request cancellation and replacement of part or all of an order
     *
     * @usage
     * Must be for a valid quantity and an existing order
     *
     * @param orderId identifier of an existing order
     * @param originalOrderRemainingQuantity The original remaining unfilled quantity of an order
     * @param newOrder the new order
     * @returns OrderIdStruct for new order
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     */
    public OrderIdStruct acceptOrderCancelReplaceRequest(OrderIdStruct orderId, int originalOrderRemainingQuantity, OrderEntryStruct newOrder)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        String methodName = "acceptOrderCancelReplaceRequest";

        OrderDetailStruct order = null;
        try
        {
            order = getOrderById(orderId);
        }
        catch ( NotFoundException e )
        {
            DataValidationException dve = ExceptionBuilder.dataValidationException("OrderId not found", DataValidationCodes.INVALID_ORDER_ID );
            GUILoggerHome.find().audit(TRANSLATOR_NAME + ": " + methodName + " threw Data Validation Exception: " 
                + dve.details.message, orderId);
            throw dve;
        }

        CancelRequestStruct aCancelRequestStruct = new CancelRequestStruct(orderId, order.orderStruct.activeSession, "", OrderCancelTypes.DESIRED_CANCEL_QUANTITY , originalOrderRemainingQuantity);

        logTransaction(methodName, aCancelRequestStruct, newOrder);
        OrderIdStruct returnedOrderId = null;
        try
        {
            returnedOrderId = orderEntry.acceptOrderCancelReplaceRequest(aCancelRequestStruct, newOrder);
        }
        catch(Exception e)
        {
            try
            {
                handleTransactionException(methodName, e);
            }
            catch(AlreadyExistsException ae)
            {
                // Not valid for this method, must be handled
            }
        }
        return returnedOrderId;
    }
    

    /**
     * @description
     * Submit a crossing notification
     *
     * @usage
     * Crossing Notifications are intended to inform the market of an impending crossing order in hopes
     * of gaining price improvement.
     *
     * @param buyCrossingOrder the buy-side order.
     * @param sellCrossingOrder the sell-side order.
     * @returns void
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     * @exception AlreadyExistsException
     * @see cmiOrder::OrderEntryStruct
     */
    public void acceptCrossingOrder(OrderEntryStruct buyCrossingOrder, OrderEntryStruct sellCrossingOrder)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        String methodName = "acceptCrossingOrder";
        logTransaction(methodName, buyCrossingOrder, sellCrossingOrder);
        try
        {
        orderEntry.acceptCrossingOrder(buyCrossingOrder, sellCrossingOrder);
    }
        catch(Exception e)
        {
            handleTransactionException(methodName, e);
        }
    }

    /**
     * @description
     * Submit a request for quotation ("RFQ") for a product
     *
     * @usage
     * Must provide a valid request for quotation
     *
     * @param rfq Request for Quote Entry structure
     * @returns void
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     * @see cmiQuote::RFQEntryStruct
     */
    public void acceptRequestForQuote(RFQEntryStruct rfq)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.RFQ))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptRequestForQuote", GUILoggerBusinessProperty.RFQ, rfq);
        }
        orderEntry.acceptRequestForQuote(rfq);
    }


    public void subscribeOrders(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (isFirmUser())
        {
            subscribeOrdersByFirm(clientListener);
        }
        else if (isOrderSubscriptionAllowed())
        {
            subscribeOrdersByUser(clientListener);
        }
    }

    /**
     * Initializes callback listener to CAS
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void initializeOrderCallbackListener()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": initializeOrderCallbackListener", GUILoggerBusinessProperty.ORDER_QUERY);

        ChannelKey key;

        if(isFirmUser())
        {
            key = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, new Integer(0));
        }
        else
        {
            key = new ChannelKey(ChannelType.CB_ALL_ORDERS, new Integer(0));
        }

//todo: remove when we switch to total V2 subscribes
        if(SubscriptionManagerFactory.find().subscribe(key, null, orderStatusConsumer) == 1)
        {
            if(isFirmUser())
            {
                orderQuery.subscribeOrdersByFirm(orderStatusConsumer, gmdCallback);
            }
            if (isOrderSubscriptionAllowed())
            {
                orderQuery.subscribeOrders(orderStatusConsumer, gmdCallback);
            }
        }

//todo: uncomment when we switch to total V2 subscribes
//        if(SubscriptionManagerFactory.find().subscribe(key, null, orderStatusConsumerV2) == 1)
//        {
//            if(isFirmUser())
//            {
//                orderQueryV2.subscribeOrderStatusForFirmV2(orderStatusConsumerV2, DEFAULT_PUBLISH_ON_SUBSCRIBE_ACTION,
//                                                           gmdCallback);
//            }
//            if(isOrderSubscriptionAllowed())
//            {
//                orderQueryV2.subscribeOrderStatusV2(orderStatusConsumerV2, DEFAULT_PUBLISH_ON_SUBSCRIBE_ACTION,
//                                                    gmdCallback);
//            }
//        }
    }

    public void subscribeAndPublishOrders(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeAndPublishOrders", GUILoggerBusinessProperty.ORDER_QUERY, "");

        subscribeOrders( clientListener );

        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_ORDERS, new Integer(0));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orderQueryCache.getAllOrders());
        eventChannel.dispatch(event);
    }

    public void subscribeOrderCanceledReport(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (isFirmUser())
        {
            subscribeOrderCanceledReportForFirm(clientListener);
        }
        else
        {
            subscribeOrderCanceledReportByUser(clientListener);
        }
    }

    public void unsubscribeOrderCanceledReport(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if (isFirmUser())
        {
            unsubscribeOrderCanceledReportForFirm(clientListener);
        }
        else
        {
            unsubscribeOrderCanceledReportForUser(clientListener);
        }
    }

    public void subscribeOrderFilledReport(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if (isFirmUser())
        {
            subscribeOrderFilledReportForFirm(clientListener);
        }
        else
        {
            subscribeOrderFilledReportForUser(clientListener);
        }
    }

    public void unsubscribeOrderFilledReport(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        if (isFirmUser())
        {
            unsubscribeOrderFilledReportForFirm(clientListener);
        }
        else
        {
            unsubscribeOrderFilledReportForUser(clientListener);
        }

    }


    /**
     * Subscribes the client listener to receive order
     * bust and reinstate report information.
     *
     * @author Connie Feng
     *
     * @param clientListener the client listener to receive continued order bust
     *        reports.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void subscribeOrderBustReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (isFirmUser())
        {
            subscribeOrderBustReportForFirm(clientListener);
        }
        else
        {
            subscribeOrderBustReportForUser(clientListener);
        }
    }


        /**
         * Unsubscribes the client listener to receive order
         * bust and reinstate report information.
         *
         * @author Connie Feng
         *
         * @param clientListener the client listener to unsubscribe continued order bust
         *        reports.
         * @exception SystemException
         * @exception CommunicationException
         * @exception AuthorizationException
         * @exception DataValidationException
         */
    public void unsubscribeOrderBustReport(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (isFirmUser())
        {
            unsubscribeOrderBustReportForFirm(clientListener);
        }
        else
        {
            unsubscribeOrderBustReportForUser(clientListener);
        }
    }






        /**
         * Gets the order history for the given order id.
         *
         * @author Derek T. Chambers-Boucher
         *
         * @return the orders history.
         * @param orderId the order id to get historical information for.
         * @exception SystemException
         * @exception CommunicationException
         * @exception AuthorizationException
         * @exception DataValidationException
         */
    public ActivityHistoryStruct queryOrderHistory(OrderIdStruct orderId)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": queryOrderHistory", GUILoggerBusinessProperty.ORDER_QUERY, orderId);
        }
        return orderQuery.queryOrderHistory( orderId );
    }

    /**
     * getPendingAdjustmentOrdersByProduct gets all pending adjustment orders for
     * the given product key.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a sequence of pending order structs for product key.
     * @param productKey the product key to get pending adjustment data for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PendingOrderStruct[] getPendingAdjustmentOrdersByProduct(String sessionName, int productKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getPendingAdjustmentOrdersByProduct", GUILoggerBusinessProperty.ORDER_QUERY, argObj);
        }
        return orderQuery.getPendingAdjustmentOrdersByProduct(sessionName, productKey);
    }

    /**
     * getPendingAdjustmentOrdersByClasses gets all pending adjustment orders for
     * the given sequence of class keys.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a sequence of pending order structs for any order based on the given
     *         sequence of class keys.
     * @param classKeys the sequence of class keys to get pending order data for.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PendingOrderStruct[] getPendingAdjustmentOrdersByClass(String sessionName, int classKey)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getPendingAdjustmentOrdersByClass", GUILoggerBusinessProperty.ORDER_QUERY, argObj);
        }

        return orderQuery.getPendingAdjustmentOrdersByClass(sessionName, classKey);
    }


    public void unsubscribeOrderStatusForProduct(int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_FOR_PRODUCT, new Integer(productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(productKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderStatusForProduct", GUILoggerBusinessProperty.ORDER_QUERY, argObj);
        }

        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    public void unsubscribeOrderStatusBySession(String sessionName, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_FOR_SESSION, sessionName);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderStatusBySession", GUILoggerBusinessProperty.ORDER_QUERY, argObj);
        }
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    public void unsubscribeAllOrderStatusForType(short productType, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_ORDERS_FOR_TYPE, new Short(productType));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Short(productType);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeAllOrderStatusForType", GUILoggerBusinessProperty.ORDER_QUERY, argObj);
        }
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    public void unsubscribeOrderStatusByClass(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_BY_CLASS, new Integer(classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderStatusByClass", GUILoggerBusinessProperty.ORDER_QUERY, argObj);
        }
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Accepts a new strategy for addition to the system.
     *
     * @return the accepted strategy struct
     * @param strategyRequest the new strategy request struct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public SessionStrategy acceptSessionStrategy(String sessionName, StrategyRequestStruct strategyRequest)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_DEFINITION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = strategyRequest;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptSessionStrategy", GUILoggerBusinessProperty.PRODUCT_DEFINITION, argObj);
        }
        
        // We have to make sure Short Sale and Short Sale Exempt side indicator have been converted to Sell.
        // Product Definitions do not contain H and X. 
//        for(StrategyLegStruct requestStruct :strategyRequest.strategyLegs)
//        {
//        	if(requestStruct.side == Sides.SELL_SHORT || requestStruct.side == Sides.SELL_SHORT_EXEMPT){
//        		requestStruct.side = Sides.SELL;
//        	}
//        }
        
        SessionStrategyStruct strategy = productDefinition.acceptStrategy(sessionName, strategyRequest);
        StrategyStruct strategyStruct = new StrategyStruct();
        strategyStruct.product = strategy.sessionProductStruct.productStruct;
        strategyStruct.strategyType = strategy.strategyType;
        int strategyLength = strategy.sessionStrategyLegs.length;
        strategyStruct.strategyLegs = new StrategyLegStruct[strategyLength];
        for (int i=0; i<strategyLength; i++) {
            strategyStruct.strategyLegs[i] = new StrategyLegStruct();
            strategyStruct.strategyLegs[i].product = strategy.sessionStrategyLegs[i].product;
            strategyStruct.strategyLegs[i].ratioQuantity = strategy.sessionStrategyLegs[i].ratioQuantity;
            strategyStruct.strategyLegs[i].side = strategy.sessionStrategyLegs[i].side;
        }
        Strategy newStrategy = ProductFactoryHome.find().create(strategyStruct);
        ProductQueryCacheFactory.find().addStrategy(newStrategy, strategy.sessionProductStruct.productStruct.productKeys.classKey);
        productProcessor.updateStrategy(strategy);
        SessionStrategy sessionStrategy = SessionProductFactory.create(strategy);
        return sessionStrategy;
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

    /**
     * @description Retrieves current trading sessions and subscribes the user for subsequent trading session updates.
     * @param clientListener  EventChannelListener object to receive trading session
     * updates
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public TradingSessionStruct[] getCurrentTradingSessions(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        TradingSessionStruct[] sessions = tradingSessionCache.getCurrentTradingSessions();
        ChannelKey key = new ChannelKey(ChannelType.CB_TRADING_SESSION_STATE, new Integer(0));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getCurrentTradingSessions", GUILoggerBusinessProperty.TRADING_SESSION, clientListener);
        }

        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, tradingSessionStatusConsumer) == 1) {
            sessions = tradingSession.getCurrentTradingSessions(tradingSessionStatusConsumer);
            tradingSessionCache.addTradingSessions(sessions);
        }
        return sessions;
    }

    /**
     * @description subscirbe current trading sessions and subscribes the user for subsequent trading session updates.
     * @param clientListener  EventChannelListener object to receive trading session
     * updates
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void subscribeTradingSessions(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeTradingSessions", GUILoggerBusinessProperty.TRADING_SESSION, clientListener);
        }

        TradingSessionStruct[] sessions = tradingSessionCache.getCurrentTradingSessions();

        ChannelKey key = new ChannelKey(ChannelType.CB_TRADING_SESSION_STATE, new Integer(0));

        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, tradingSessionStatusConsumer) == 1)        {
            try
            {
                sessions = tradingSession.getCurrentTradingSessions(tradingSessionStatusConsumer);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tradingSessionStatusConsumer);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tradingSessionStatusConsumer);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tradingSessionStatusConsumer);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tradingSessionStatusConsumer);
                throw e;
            }

            tradingSessionCache.addTradingSessions(sessions);
        }
    }

    public void unsubscribeTradingSessionStatus(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeTradingSessionStatus", GUILoggerBusinessProperty.TRADING_SESSION, clientListener);
        }


        ChannelKey key = new ChannelKey(ChannelType.CB_TRADING_SESSION_STATE, new Integer(0));
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tradingSessionStatusConsumer) == 0) {
            tradingSession.unsubscribeTradingSessionStatus(tradingSessionStatusConsumer);
        }
    }

    /**
     * setUserPreferences sets the user preferences contained within the given
     * sequence of preference structs.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param preferenceSequence the user preferences to set.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void setUserPreferences(PreferenceStruct[] preferenceSequence)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_PREFERENCES))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": setUserPreferences", GUILoggerBusinessProperty.USER_PREFERENCES, preferenceSequence);
        }
        userPreferenceQuery.setUserPreferences(preferenceSequence);
    }

    /**
     * Removes the user preferences contained within the given sequence of
     * preference structs.
     *
     * @param preferenceSequence the user preferences to remove.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void removeUserPreference(PreferenceStruct[] preferenceSequence)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_PREFERENCES))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": removeUserPreference", GUILoggerBusinessProperty.USER_PREFERENCES, preferenceSequence);
        }
        userPreferenceQuery.removeUserPreference(preferenceSequence);
    }

    /**
     * Gets all user preferences for this user.
     *
     * @return a sequence of preference structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getAllUserPreferences()
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_PREFERENCES))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAllUserPreferences", GUILoggerBusinessProperty.USER_PREFERENCES, "");
        }
        return userPreferenceQuery.getAllUserPreferences();
    }

    /**
     * Gets all user preferences for this user that begin with the given prefix.
     *
     * @param prefix a string containing the requested preferences name prefix.
     * @return a sequence of preference structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getUserPreferencesByPrefix(String prefix)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_PREFERENCES))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getUserPreferencesByPrefix", GUILoggerBusinessProperty.USER_PREFERENCES, prefix);
        }
        return userPreferenceQuery.getUserPreferencesByPrefix(prefix);
    }

    /**
     * Removes all user preferences for this user that begin with the given prefix.
     *
     * @param prefix a string containing the requested preferences name prefix.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void removeUserPreferencesByPrefix(String prefix)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_PREFERENCES))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": removeUserPreferencesByPrefix", GUILoggerBusinessProperty.USER_PREFERENCES, prefix);
        }
        userPreferenceQuery.removeUserPreferencesByPrefix(prefix);
    }

    /**
     * Gets all system preferences for this user.
     *
     * @return a sequence of preference structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getAllSystemPreferences()
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_PREFERENCES))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAllSystemPreferences", GUILoggerBusinessProperty.USER_PREFERENCES, "");
        }
        return userPreferenceQuery.getAllSystemPreferences();
    }

    /**
     * Gets all system preferences for this user that begin with the given prefix.
     *
     * @param prefix a string containing the requested preferences name prefix.
     * @return a sequence of preference structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getSystemPreferencesByPrefix(String prefix)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_PREFERENCES))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getSystemPreferencesByPrefix", GUILoggerBusinessProperty.USER_PREFERENCES, prefix);
        }
        return userPreferenceQuery.getSystemPreferencesByPrefix(prefix);
    }

    ///////////////////////////// end of productQuery Impl //////////////////////


    ////////////////////////// begin TextMessagingService ////////////////////////
    /**
     * Send a message to a user and/or group
     * @usage Send a message to a user and/or group
     * @returns sent message's messageId
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public int sendMessage(MessageStruct message)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.TEXT_MESSAGE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": sendMessage", GUILoggerBusinessProperty.TEXT_MESSAGE, message);
        }
        return sessionManager.getAdministrator().sendMessage(message);
    }

    // public MessageResultStruct sendMessage( DestinationStruct [] receipients, MessageTransportStruct message)
    //   is part of the ManualReportingAPI

    ////////////////////////// end of TextMessagingService ////////////////////////


    ////////////////////////////// begin UserHistoryAPI impl ///////////////////////
    /**
     * Gets the activity history for the specified class, start time and direction.
     *
     * @author Dean Grippo
     *
     * @return com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct
     * @param  classKey  - the ID of the class
     * @param  startTime - get history starting from this time
     * @param  direction - and moving backward or forward from the start time
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public ActivityHistoryStruct getTraderClassActivityByTime(String sessionName, int classKey, DateTimeStruct startTime, short direction)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_HISTORY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = startTime;
            argObj[3] = new Short(direction);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getTraderClassActivityByTime", GUILoggerBusinessProperty.USER_HISTORY, argObj);
        }
        return userHistory.getTraderClassActivityByTime(sessionName, classKey, startTime, direction);
    }

    /**
     * Gets the activity history for the specified product, start time and direction.
     *
     * @author Dean Grippo
     *
     * @return com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct
     * @param  productKey  - the ID of the product
     * @param  startTime - get history starting from this time
     * @param  direction - and moving backward or forward from the start time
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotFoundException
     */
    public ActivityHistoryStruct getTraderProductActivityByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.USER_HISTORY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = startTime;
            argObj[3] = new Short(direction);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getTraderProductActivityByTime", GUILoggerBusinessProperty.USER_HISTORY, argObj);
        }
        return userHistory.getTraderProductActivityByTime(sessionName, productKey, startTime, direction);
    }

    /**
     * Get the most recent CurrentMarket for a session product
     */
    @SuppressWarnings({"DuplicateThrows"})
    public CurrentMarketProductContainer getCurrentMarketSnapshotForProduct(
            String sessionName, int productKey)
            throws AuthorizationException, DataValidationException, NotFoundException,
            SystemException, CommunicationException, UserException
    {
        return getCurrentMarketSnapshotForProduct(0, sessionName, productKey);
    }

    public StrategyImpliedMarketWrapper getStrategyImpliedMarket(final SessionStrategy sessionStrategy)
            throws UserException
    {
        StrategyImpliedMarketWrapper retVal = null;

        if(!StrategyUtility.containsUnderlyingLeg(sessionStrategy))
        {

            StrategyUtility.CurrentMarketGetter cmGetter = new StrategyUtility.CurrentMarketGetter()
            {
                public CurrentMarketStruct getCurrentMarket(final Product product) throws UserException
                {
                    return getCurrentMarketSnapshotForProduct(sessionStrategy.getTradingSessionName(),
                                                              product.getProductKey()).getBestMarket();
                }
                protected CurrentMarketStruct getUnderlyingCM(Product product) throws UserException
                {
                    return null; // TODO ??
                }
            };
            DsmBidAskStruct dsmBidAsk = StrategyUtility.getStrategyDSM(sessionStrategy, cmGetter);
            if (dsmBidAsk != null)
            {
                dsmBidAsk.setFlipped(StrategyBidAskFlipper.isDsmBidAskFlipped(sessionStrategy, dsmBidAsk));
            }
            retVal = StrategyUtility.convertToImpliedMarket(dsmBidAsk);
        }
        return retVal;
    }

    /**
     * Get the most recent CurrentMarket for a session product
     */
    @SuppressWarnings({"DuplicateThrows"})
    public CurrentMarketProductContainer getCurrentMarketSnapshotForProduct(
            int timeout, String sessionName, int productKey)
            throws AuthorizationException, DataValidationException, NotFoundException,
            SystemException, CommunicationException, UserException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = {timeout, sessionName, productKey};
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getCurrentMarketSnapshotForProduct",
                                       GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        SessionProduct product = getProductByKeyForSession(sessionName, productKey);
        int classKey = product.getProductKeysStruct().classKey;
        CurrentMarketV3Cache cmV3Cache = MarketDataCacheFactory.findCurrentMarketV3Cache(sessionName);
        CurrentMarketProductContainer retVal;

        // if there wasn't already a subscription for CurrentMarket, get a "snapshot".
        if(!cmV3Cache.isSubscribedForClass(classKey))
        {
            CurrentMarketEventChannelSnapshot listener =
                    new CurrentMarketEventChannelSnapshot(timeout, sessionName, productKey);
            try
            {
                retVal = (CurrentMarketProductContainer)listener.getEventChannelData();
            }
            catch(TimedOutException e)
            {
                // will throw a TimedOutException if the snapshot didn't get an event from
                // the CAS before the predetermined timeout period expired
                retVal = new CurrentMarketProductContainerImpl();
                // this isn't necessaruly a useful exception to show the user;
                // it only means that an event wasn't received within the
                //    pre-determined CurrentMarketEventChannelSnapshot.TIME_OUT period
                GUILoggerHome.find().exception(e.getMessage(), e);
            }
        }
        else
        {
            retVal = cmV3Cache.getMarketDataForProduct(classKey, productKey);
        }

        return retVal;
    }

    public RecapStruct getUnderlyingRecapSnapshotForReportingClass(SessionReportingClass reportingClass)
        throws UserException, SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getUnderlyingRecapSnapshotForReportingClass(0,reportingClass);
    }

    public RecapStruct getUnderlyingRecapSnapshotForReportingClass(int timeout, SessionReportingClass reportingClass)
            throws UserException, SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Long(timeout);
            argObj[1] = reportingClass;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getUnderlyingRecapSnapshotForReportingClass", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        return getUnderlyingRecapSnapshotForClass(timeout, reportingClass.getSessionProductClass());
    }

    public RecapStruct getUnderlyingRecapSnapshotForClass(SessionProductClass productClass)
        throws UserException, SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getUnderlyingRecapSnapshotForClass(0, productClass);
    }

    public RecapStruct getUnderlyingRecapSnapshotForClass(int timeout, SessionProductClass productClass)
            throws UserException, SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = timeout;
            argObj[1] = productClass.getSessionClassStruct(); // log the struct, because the SessionProductClass doesn't necessarily have any public member variables that would be logged
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getUnderlyingRecapSnapshotForClass", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        RecapStruct retVal = null;

        UnderlyingRecapEventChannelSnapshot listener = new UnderlyingRecapEventChannelSnapshot(timeout, productClass);
        try
        {
            retVal = (RecapStruct)listener.getEventChannelData();
        }
        catch(TimedOutException e)
        {
            // will throw a CommunicationException if the snapshot didn't get an event from the CAS before the predetermined timeout period expired
            retVal = new RecapStruct();
            // this isn't necessarily a useful exception to show the user; it only means that an event wasn't received within the
            //    pre-determined UnderlyingRecapEventChannelSnapshot.TIME_OUT period
            GUILoggerHome.find().exception(e.getMessage(), e);
        }

        return retVal;
    }

    ////////////////////////////// end of UserHistoryAPI impl ///////////////////////


///////////////////////////////////////////////////////////////////////////////
// PROTECTED METHODS

    /**
     * Subscribes to event channel for CB_ORDER_BY_FIRM
     * @param clientListener to subscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void subscribeOrdersByFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeOrdersByFirm", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Subscribes to event channel for CB_ALL_ORDERS
     * @param clientListener to subscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void subscribeOrdersByUser(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeOrdersByUser", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_ORDERS, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Unsubscribes to event channel for CB_ORDER_BY_FIRM
     * @param clientListener to unsubscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void unsubscribeOrdersByFirm (EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrdersByFirm", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Unsubscribes to event channel for CB_ORDER_BY_FIRM
     * @param clientListener to unsubscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void unsubscribeOrderStatusForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeOrderStatusForFirm", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Subscribes to event channel for CB_CANCELED_REPORT
     * @param clientListener to subscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void subscribeOrderCanceledReportByUser(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeOrderCanceledReportByUser", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key = new ChannelKey(ChannelType.CB_CANCELED_REPORT, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Subscribes to event channel for CB_CANCELED_REPORT_BY_FIRM, CB_FILLED_REPORT_BY_FIRM,
     * CB_ORDER_BUST_REPORT_BY_FIRM, CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM
     * @param clientListener to subscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void subscribeOrderCanceledReportForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeOrderCanceledReportForFirm", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key;

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Unsubscribes to event channel for CB_CANCELED_REPORT
     * @param clientListener to unsubscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void unsubscribeOrderCanceledReportForUser(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeOrderCanceledReportForUser", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key = new ChannelKey(ChannelType.CB_CANCELED_REPORT, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Unsubscribes to event channel for CB_CANCELED_REPORT_BY_FIRM, CB_FILLED_REPORT_BY_FIRM,
     * CB_ORDER_BUST_REPORT_BY_FIRM, CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM
     * @param clientListener to unsubscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void unsubscribeOrderCanceledReportForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeOrderCanceledReportForFirm", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key;

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    ///////////////////////////////////
    // Filled Report

    protected void subscribeOrderFilledReportForUser(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_FILLED_REPORT, new Integer(0));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_ENTRY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeOrderFilledReportForUser", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);
        }

        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }
    protected void unsubscribeOrderFilledReportForUser(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        ChannelKey key = new ChannelKey(ChannelType.CB_FILLED_REPORT, new Integer(0));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderFilledReportForUser", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);
        }
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Subscribes to event channel for CB_CANCELED_REPORT_BY_FIRM, CB_FILLED_REPORT_BY_FIRM,
     * CB_ORDER_BUST_REPORT_BY_FIRM, CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM
     * @param clientListener to subscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void subscribeOrderFilledReportForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeOrderFilledReportForFirm", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key;

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
     }

    /**
     * Unsubscribes to event channel for CB_CANCELED_REPORT_BY_FIRM, CB_FILLED_REPORT_BY_FIRM,
     * CB_ORDER_BUST_REPORT_BY_FIRM, CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM
     * @param clientListener to unsubscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void unsubscribeOrderFilledReportForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeOrderFilledReportForFirm", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key;

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    ///////////////////////////////////
    // Order Bust

    protected void subscribeOrderBustReportForUser(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeOrderBustReportForUser", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);
        }

        ChannelKey key;
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);

    }

    /**
     * Subscribes to event channel for CB_CANCELED_REPORT_BY_FIRM, CB_FILLED_REPORT_BY_FIRM,
     * CB_ORDER_BUST_REPORT_BY_FIRM, CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM
     * @param clientListener to subscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void subscribeOrderBustReportForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeOrderBustReportForFirm", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key;

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, new Integer(0));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    protected void unsubscribeOrderBustReportForUser(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key;

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderBustReportForUser", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);
        }


        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Unsubscribes to event channel for CB_CANCELED_REPORT_BY_FIRM, CB_FILLED_REPORT_BY_FIRM,
     * CB_ORDER_BUST_REPORT_BY_FIRM, CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM
     * @param clientListener to unsubscribe
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    protected void unsubscribeOrderBustReportForFirm(EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ": unsubscribeOrderBustReportForFirm", GUILoggerBusinessProperty.ORDER_QUERY, clientListener);

        ChannelKey key;

        key = new ChannelKey(ChannelType.CB_CANCELED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_FILLED_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        key = new ChannelKey(ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM, new Integer(0));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }


    ///////////////////////////////////
    // User role access

    protected Role getUserRole()
    {
        Role role = Role.UNKNOWN;
        try
        {
            role = getValidUser().getRole();
        }
        catch(UserException  e)
        {
            GUILoggerHome.find().exception(e);
        }
        return role;
    }

    protected boolean isFirmUser()
    {
        boolean retVal = false;
        Role userRole = getUserRole();

        if (userRole == Role.FIRM || userRole == Role.FIRM_DISPLAY)
        {
            retVal = true;
        }
        return retVal;
    }

    protected boolean isOrderSubscriptionAllowed()
    {
        boolean retVal;
        Role userRole = getUserRole();

        switch(userRole)
        {
            case CLASS_DISPLAY:
            case EOP:
            case DISPLAY_OMT:
            case REPORTING:
            case OPRA:
                retVal = false;
                break;
            case HELPDESK_OMT:
            case BOOTH_OMT:
            case CROWD_OMT:
            default:
                retVal = true;
                break;
        }

        return retVal;
    }

    /**
     * submit a strategy order. A pass-through method.
     *
     * @param anOrder OrderEntryStruct
     * @param legEntryDetails
     * @return OrderIdStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     * @exception AlreadyExistsException
     * @see com.cboe.idl.cmiOrder.LegOrderEntryStruct
     * @see com.cboe.idl.cmiOrder.OrderEntryStruct
     * @author Jing Chen
     */

    public OrderIdStruct acceptStrategyOrder(OrderEntryStruct anOrder, LegOrderEntryStruct[] legEntryDetails)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        String methodName = "acceptStrategyOrder";
        logTransaction(methodName, anOrder, legEntryDetails);
        OrderIdStruct orderId = null;
        try
        {
            orderId = orderEntry.acceptStrategyOrder(anOrder, legEntryDetails);
        }
        catch(Exception e)
        {
            handleTransactionException(methodName, e);
        }
        return orderId;
    }

    
    
    /**
     * submit a strategy order. A pass-through method.
     *
     * @param anOrder OrderEntryStruct
     * @param legEntryDetails
     * @return OrderIdStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     * @exception AlreadyExistsException
     * @see com.cboe.idl.cmiOrder.LegOrderEntryStruct
     * @see com.cboe.idl.cmiOrder.OrderEntryStruct
     * @author Jing Chen
     */

    public OrderIdStruct acceptStrategyOrderV7(OrderEntryStruct anOrder, LegOrderEntryStructV2 [] legEntryDetailsV2)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        String methodName = "acceptStrategyOrderV7";
        logTransaction(methodName, anOrder, legEntryDetailsV2, null);
        OrderIdStruct orderId = null;
        try
        {
            orderId = orderEntryV7.acceptStrategyOrderV7(anOrder, legEntryDetailsV2);
        }
        catch(Exception e)
        {
            handleTransactionException(methodName, e);
        }
        return orderId;
    }
    
    
    /**
     * submit a strategy order update request.  A pass-through call to cmi.
     *
     * @param currentRemainingQuantity int
     * @param updateOrder OrderEntryStruct
     * @param legEntryDetails LegOrderEntryStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     * @see com.cboe.idl.cmiOrder.OrderEntryStruct
     * @see com.cboe.idl.cmiOrder.LegOrderEntryStruct
     */

    public void acceptStrategyOrderUpdateRequest(int currentRemainingQuantity, OrderEntryStruct updateOrder, LegOrderEntryStruct[] legEntryDetails)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        String methodName = "acceptStrategyOrderUpdateRequest";
        logTransaction(methodName, currentRemainingQuantity, updateOrder, legEntryDetails);
        try
        {
        orderEntry.acceptStrategyOrderUpdateRequest(currentRemainingQuantity, updateOrder, legEntryDetails);
    }
        catch(Exception e)
        {
            try
            {
                handleTransactionException(methodName, e);
            }
            catch(AlreadyExistsException ae)
            {
                // Not valid for this method, must be handled
            }
        }
    }

    /**
     * submit a strategy order cancel replace request.  A pass-through call to cmi.
     *
     * @param orderId OrderIdStruct
     * @param cancelQuantity int
     * @param newOrder OrderEntryStruct
     * @param legEntryDetails LegOrderEntryStruct[]
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     * @see com.cboe.idl.cmiOrder.OrderIdStruct
     * @see com.cboe.idl.cmiOrder.OrderEntryStruct
     * @see com.cboe.idl.LegOrderEntryStruct
     * @return OrderIdStruct
     */

    public OrderIdStruct acceptStrategyOrderCancelReplaceRequest(OrderIdStruct orderId, int cancelQuantity, OrderEntryStruct newOrder, LegOrderEntryStruct[] legEntryDetails)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        String methodName = "acceptStrategyOrderCancelReplaceRequest";
        OrderDetailStruct order = null;
        try
        {
            order = getOrderById(orderId);
        }
        catch ( NotFoundException e )
        {
            DataValidationException dve = ExceptionBuilder.dataValidationException(
                    "OrderId not found", DataValidationCodes.INVALID_ORDER_ID);
            GUILoggerHome.find().audit(TRANSLATOR_NAME + ": " + methodName +
                    " threw Data Validation Exception: " + dve.details.message, orderId);
            throw ExceptionBuilder.dataValidationException("OrderId not found", DataValidationCodes.INVALID_ORDER_ID );
        }

        CancelRequestStruct aCancelRequestStruct = new CancelRequestStruct(orderId, order.orderStruct.activeSession, "", OrderCancelTypes.DESIRED_CANCEL_QUANTITY , cancelQuantity);

        logTransaction(methodName, aCancelRequestStruct, newOrder, legEntryDetails);

        OrderIdStruct returnedOrderId = null;
        try
        {
            returnedOrderId = orderEntry.acceptStrategyOrderCancelReplaceRequest(aCancelRequestStruct,  newOrder, legEntryDetails);
        }
        catch(Exception e)
        {
            try
            {
                handleTransactionException(methodName, e);
            }
            catch(AlreadyExistsException ae)
            {
                // Not valid for this method, must be handled
            }
        }
        return returnedOrderId;

    }

      
    /**
     * submit a strategy order cancel replace request.  A pass-through call to cmi.
     *
     * @param orderId OrderIdStruct
     * @param cancelQuantity int
     * @param newOrder OrderEntryStruct
     * @param legEntryDetails LegOrderEntryStruct[]
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     * @exception NotAcceptedException
     * @exception TransactionFailedException
     * @see com.cboe.idl.cmiOrder.OrderIdStruct
     * @see com.cboe.idl.cmiOrder.OrderEntryStruct
     * @see com.cboe.idl.LegOrderEntryStruct
     * @return OrderIdStruct
     */

    public OrderIdStruct acceptStrategyOrderCancelReplaceRequestV7(OrderIdStruct orderId, int cancelQuantity, OrderEntryStruct newOrder, LegOrderEntryStructV2[] legEntryDetails)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        
    	 String methodName = "acceptStrategyOrderCancelReplaceRequestV7";
         OrderDetailStruct order = null;
         try
         {
             order = getOrderById(orderId);
         }
         catch ( NotFoundException e )
         {
             DataValidationException dve = ExceptionBuilder.dataValidationException(
                     "OrderId not found", DataValidationCodes.INVALID_ORDER_ID);
             GUILoggerHome.find().audit(TRANSLATOR_NAME + ": " + methodName +
                     " threw Data Validation Exception: " + dve.details.message, orderId);
             throw ExceptionBuilder.dataValidationException("OrderId not found", DataValidationCodes.INVALID_ORDER_ID );
         }

         CancelRequestStruct aCancelRequestStruct = new CancelRequestStruct(orderId, order.orderStruct.activeSession, "", OrderCancelTypes.DESIRED_CANCEL_QUANTITY , cancelQuantity);

         logTransaction(methodName, aCancelRequestStruct, null, null);

         OrderIdStruct returnedOrderId = null;
         try
         {
             returnedOrderId = orderEntryV7.acceptStrategyOrderCancelReplaceRequestV7(aCancelRequestStruct,  newOrder, legEntryDetails);
         }
         catch(Exception e)
         {
             try
             {
                 handleTransactionException(methodName, e);
             }
             catch(AlreadyExistsException ae)
             {
                 // Not valid for this method, must be handled
             }
         }
         return returnedOrderId;
    }
    
    
    
    /**
     * build StrategyRequestStruct based on product name (added in strategy order scrum)
     * @param strategyType short
     * @param anchorProduct ProductNameStruct
     * @param priceIncrement PriceStruct
     * @param monthIncrement short
     * @return StrategyRequestStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidataion
     * @see com.cboe.idl.cmiStrategy.StrategyRequestStruct
     * @see com.cboe.idl.cmiProduct.ProductNameStruct
     */

     public StrategyRequestStruct buildStrategyRequestByName(short strategyType, ProductNameStruct anchorProduct, PriceStruct priceIncrement, short monthIncrement)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
     {
         if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_DEFINITION))
        {
            Object[] argObj = new Object[4];
            argObj[0] = new Short(strategyType);
            argObj[1] = anchorProduct;
            argObj[2] = priceIncrement;
            argObj[3] = new Short(monthIncrement);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": buildStrategyRequestByName", GUILoggerBusinessProperty.PRODUCT_DEFINITION, argObj);
        }
        return( productDefinition.buildStrategyRequestByName(strategyType, anchorProduct, priceIncrement, monthIncrement ));

    }

    /**
     * build StrategyRequestStruct based on product key (added in strategy order scrum)
     * @param strategyType short
     * @param anchorProductKey integer
     * @param priceIncrement PriceStruct
     * @param monthIncrement short
     * @return StrategyRequestStruct
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidataion
     * @see com.cboe.idl.cmiStrategy.StrategyRequestStruct
     * @see com.cboe.idl.cmiUtil.PriceStruct
     */
    public StrategyRequestStruct buildStrategyRequestByProductKey(short strategyType, int anchorProductKey, PriceStruct priceIncrement, short monthIncrement)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_DEFINITION))
        {
            Object[] argObj = new Object[4];
            argObj[0] = new Short(strategyType);
            argObj[1] = new Integer(anchorProductKey);
            argObj[2] = priceIncrement;
            argObj[3] = new Short(monthIncrement);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": buildStrategyRequestByProductKey", GUILoggerBusinessProperty.PRODUCT_DEFINITION, argObj);
        }
        return( productDefinition.buildStrategyRequestByProductKey(strategyType, anchorProductKey, priceIncrement, monthIncrement ));
    }

    /**
     * Judge the class is strategy or not based on classKey
     * @param classKey int
     * @return boolean
     */
    public boolean isStrategy(int classKey)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean isStrategy = false;
        ProductClass productClass = ProductQueryCacheFactory.find().getProductClassByKey(classKey);
        if(productClass == null)
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": isStrategy calling productQuery.getClassByKey()", GUILoggerBusinessProperty.PRODUCT_QUERY, classKey);
            }
            ClassStruct classStruct = productQuery.getClassByKey(classKey);

            if (classStruct != null) {
                ProductClass rawProductClass = ProductClassFactoryHome.find().create(classStruct);
                ProductQueryCacheFactory.find().addClass(rawProductClass, classStruct.productType);
                productClass = ProductQueryCacheFactory.find().getProductClassByKey(classKey);
            }
        }
        if (productClass != null) {
            if (productClass.getProductType() == ProductTypes.STRATEGY) {
                isStrategy = true;
            }
        }
        return isStrategy;
    }
    /**
     * load all the products in the class based on classKey
     * @param classKey int
     * @return Product[]
     */
    protected Product[] loadProductsForClass(int classKey)
          throws DataValidationException, AuthorizationException, CommunicationException, SystemException{

         if (!CacheClassTrackFactory.find(new SessionKeyContainer("", classKey)).wasProductsLoaded()){
             if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
             {
                 GUILoggerHome.find().debug(TRANSLATOR_NAME + ": loadProductsForClass calling productQuery.getProductsByClass()", GUILoggerBusinessProperty.PRODUCT_QUERY, classKey);
             }
             ProductStruct[] productStructs = productQuery.getProductsByClass(classKey);
             int productLength = productStructs.length;
             Product[] newProducts = new Product[productLength];
             for (int i=0; i<productLength; i++) {
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
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException{

        if (!CacheClassTrackFactory.find(new SessionKeyContainer("", classKey)).wasStrategiesLoaded())
        {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": loadStrategiesForClass calling productQuery.getStrategiesByClass()", GUILoggerBusinessProperty.PRODUCT_QUERY, classKey);
            }
            StrategyStruct[] strategyStructs = productQuery.getStrategiesByClass(classKey);
            int strategyLength = strategyStructs.length;
            Strategy[] newStrategies = new Strategy[strategyLength];
            for (int i=0; i<strategyLength; i++) {
                newStrategies[i] = ProductFactoryHome.find().create(strategyStructs[i]);
            }
            ProductQueryCacheFactory.find().addStrategies(newStrategies, classKey);
            CacheClassTrackFactory.find(new SessionKeyContainer("", classKey)).setSessionLessStrategiesLoaded(true);
        }
        return ProductQueryCacheFactory.find().getStrategies(classKey, false);
    }

    /**
     * load all the session products in the class based on session name and class key
     * @param sessionName String
     * @param classKey int
     * @return SessionProduct[]
     */
    private SessionProduct[] loadProductsForClassBySession(String sessionName, int classKey)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException{

        loadProductsForClass(classKey);
        SessionProductStruct[] sessionProductStructs = null;
        synchronized (SessionProductCacheFactory.find(sessionName)) {
            if(!CacheClassTrackFactory.find(new SessionKeyContainer(sessionName, classKey)).wasSessionProductsLoaded())
            {
                if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
                {
                    Object[] argObj = new Object[]{sessionName, classKey};
                    GUILoggerHome.find().debug(TRANSLATOR_NAME + ": loadProductsForClassBySession calling tradingSession.getProductsForSession()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
                }
                sessionProductStructs = tradingSession.getProductsForSession(sessionName, classKey, productStatusConsumer);
                productProcessor.addProducts(sessionName, sessionProductStructs);
                CacheClassTrackFactory.find(new SessionKeyContainer(sessionName, classKey)).setSessionProductsLoaded(true);
            }
            return SessionProductCacheFactory.find(sessionName).getProductsForSession(classKey);
        }
    }

    /**
     * load all the session strategies in the class based on sessionName and class key
     * @param sessionName String
     * @param classKey int
     * @return SessionStrategy[]
     */
    private SessionStrategy[] loadStrategiesForClassBySession(String sessionName, int classKey)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException{

        loadStrategiesForClass(classKey);
        SessionStrategyStruct[] sessionStrategyStructs = null;
        // subscribe for product status
        synchronized (SessionProductCacheFactory.find(sessionName)) {
            if(!CacheClassTrackFactory.find(new SessionKeyContainer(sessionName, classKey)).wasSessionStrategiesLoaded())
            {
                Object[] argObj = new Object[]{sessionName, classKey};
                if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
                {
                    GUILoggerHome.find().debug(TRANSLATOR_NAME + ": loadStrategiesForClassBySession calling tradingSession.getProductsForSession()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
                }
                tradingSession.getProductsForSession(sessionName, classKey, productStatusConsumer);
                if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
                {
                    GUILoggerHome.find().debug(TRANSLATOR_NAME + ": loadStrategiesForClassBySession calling tradingSession.getStrategiesByClassForSession()", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
                }
                sessionStrategyStructs = tradingSession.getStrategiesByClassForSession(sessionName, classKey, strategyStatusConsumer);
                productProcessor.addStrategies(sessionName, sessionStrategyStructs);
                CacheClassTrackFactory.find(new SessionKeyContainer(sessionName, classKey)).setSessionStrategiesLoaded(true);
            }
            return SessionProductCacheFactory.find(sessionName).getStrategiesForSession(classKey);
        }
    }

    /**
     * retrieve all the product typs in the system and put them into the product cache
     * @return ProductTypeStruct[]
     */
    private ProductTypeStruct[] loadProductTypes()
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException {

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": loadProductTypes calling productQuery.getProductTypes()",
                    GUILoggerBusinessProperty.PRODUCT_QUERY);
        }

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
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException {

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": loadProductTypesForSession calling tradingSession.getProductTypesForSession()",
                    GUILoggerBusinessProperty.PRODUCT_QUERY, sessionName);
        }
        ProductTypeStruct[] types = tradingSession.getProductTypesForSession(sessionName);
        productProcessor.addProductTypes(sessionName, types);
        return SessionProductCacheFactory.find(sessionName).getProductTypesForSession();
    }

    /**
     * retrieve all the product classes for a product type and cache them
     * @param productType short
     * @return ProductClass[]
     */
    private ProductClass[] loadProductClasses(short productType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException {
        ClassStruct[] newClasses = productQuery.getProductClasses(productType);
        if ( newClasses != null && newClasses.length > 0)
        {
            ProductClass[] productClasses = new ProductClass[newClasses.length];
            for (int i = 0; i < productClasses.length; i++)
            {
                productClasses[i] = ProductClassFactoryHome.find().create(newClasses[i]);
            }

            ProductQueryCacheFactory.find().addClasses(productClasses, productType);
        }
        return ProductQueryCacheFactory.find().getProductClasses(productType, false);
    }

    /**
     * retrieve all the product classes for a product type in a session and cache them in product cache
     * @param sessionName String
     * @param productType short
     * @return SessionProductClass[]
     */
    private SessionProductClass[] loadProductClassesForSession(String sessionName, short productType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException {
        synchronized (SessionProductCacheFactory.find(sessionName)) {
            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                Object[] argObj = new Object[] {sessionName, productType};
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ": loadProductClassesForSession calling tradingSession.getClassesForSession()",
                        GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
            }
            SessionClassStruct[] classStructs = tradingSession.getClassesForSession(sessionName, productType, classStatusConsumer);
            productProcessor.addClasses(sessionName, classStructs);
            return SessionProductCacheFactory.find(sessionName).getAllClassesForSession();
        }
    }

    /**
     * The ChannelKey can be for a ChannelType.CB_USER_MARKET_DATA or ChannelType.CB_USER_MARKET_DATA_BY_PRODUCT, but the
     * listeners will only be counted by session/classKey.  The count is maintained so that when all the listeners for a
     * class in a particular session have been removed, the UserMarketDataCache will be cleared and unsubscribe from CAS
     * event channels for NBBO, Recap, and CurrentMarket for that class.
     * added 6/2002
     */
    private int addUserMarketDataListener(EventChannelListener clientListener, ChannelKey key, String sessionName, int classKey)
    {
        String hashKey = sessionName + classKey;
        Integer currentCount;
        int count;
        synchronized(userMarketDataListenerCount)
        {
//            dump(userMarketDataListenerCount, "addUserMarketDataListener-top clientListener=" + clientListener);
            Map<EventChannelListener, Integer> perListenerCount = userMarketDataListenerCount.get(hashKey);
            if (perListenerCount == null)
            {
                perListenerCount = new HashMap<EventChannelListener, Integer>();
            }
            currentCount = perListenerCount.get(clientListener);
            count = currentCount == null ? 0 : currentCount;
            ++count;

            if (count == 1)
            {
                eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
            }

            perListenerCount.put(clientListener, count);

            userMarketDataListenerCount.put(hashKey, perListenerCount);
//            dump(userMarketDataListenerCount, "addUserMarketDataListener-bot");

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": addUserMarketDataListener()",GUILoggerBusinessProperty.MARKET_QUERY,
                                       "num InternalEventChannel listeners for session '"+sessionName+"', class "+classKey+" = "+count);
        }
        return count;
    }

    /**
     * The ChannelKey can be for ChannelType.CB_USER_MARKET_DATA or ChannelType.CB_USER_MARKET_DATA_BY_PRODUCT, but only
     * maintaining a count of listeners by session/classKey.  Once all the listeners for a class in a session have been
     * removed, the UserMarketDataCache will cleanUp it's caches and unsubscribe for event channels for NBBO, Recap, and
     * CurrentMarket updates from the CAS.
     * added 6/2002
     */
    private int removeUserMarketDataListener(EventChannelListener clientListener, ChannelKey key, String sessionName, int classKey)
    {
        boolean error = false;
        String hashKey = sessionName + classKey;
        Integer currentCount;
        int count = 0;
        synchronized(userMarketDataListenerCount)
        {
//            dump(userMarketDataListenerCount, "removeUserMarketDataListener-top clientListener=" + clientListener);
            Map<EventChannelListener, Integer> perListenerCount = userMarketDataListenerCount.get(hashKey);

            // do nothing if there are no registered listeners for this session/class
            if (perListenerCount != null)
            {
                currentCount = perListenerCount.get(clientListener);

                count = currentCount == null ? 0 : currentCount;

                // do nothing if there listener count is already zero. a bug can bring us here due to multiple unsubscriptions
                if (count > 0)
                {
                    --count;

                    // if this was the last USER_MARKET_DATA listener, unsubscribe the UserMarketDataCache
                    if(count == 0)
                    {
                        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);

                        perListenerCount.remove(clientListener);

                        if (perListenerCount.size() == 0)
                        {
                            userMarketDataListenerCount.remove(hashKey);

                            if(userMarketDataListenerCount.size() == 0)
                            {
                                GUILoggerHome.find().debug(TRANSLATOR_NAME+": removeUserMarketDataListener",GUILoggerBusinessProperty.MARKET_QUERY,
                                                          "No more InternalEventChannel listeners for session '"+sessionName+"', class "+classKey+" -- removing translator interest");
                                cleanupProductCache(sessionName, classKey);
                            }
                        }
                    }
                    else
                    {
                        perListenerCount.put(clientListener, count);
                    }
                } // end if count > 0
                else
                {
                    error = true;
                    count = 0;
                }
            } // end if (perListenerCount != null)
        } // end synchronized

        if (error)
        {
            GUILoggerHome.find().alarm(TRANSLATOR_NAME + ": removeUserMarketDataListener: ",
                                       "possible bug, listener count is already zero. session=" + sessionName
                                     + ", class=" + classKey + ", listener=" + clientListener);
        }
//        dump(userMarketDataListenerCount, "removeUserMarketDataListener-bot");
        return count;
    }

//    private void dump(Map<String, Map<EventChannelListener, Integer>> umdListenerCount, String where)
//    {
//        StringBuilder msg = new StringBuilder();
//        for (String hashKey : umdListenerCount.keySet())
//        {
//            msg.append("\tfor hashKey=").append(hashKey);
//            Map<EventChannelListener, Integer> perListenerCount = umdListenerCount.get(hashKey);
//            msg.append(" perListenerCount.size=").append(perListenerCount.size()).append("\n");
//            for (EventChannelListener listener : perListenerCount.keySet())
//            {
//                msg.append("\t\tlistener=").append(listener);
//                Integer count = perListenerCount.get(listener);
//                msg.append(" count=").append(count).append("\n");
//            }
//        }
//        msg.append("\n");
//
//        System.out.println(msg.toString());
//    }

    // UserAccessV2 APIs
    // MarketQueryV2API

    public DetailBookDepth getBookDepthDetails(String session, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = session;
            argObj[1] = new Integer(productKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getBookDepthDetailsV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        BookDepthStructV2 struct = marketQueryV2.getBookDepthDetails(session, productKey);
        GUILoggerHome.find().debug(TRANSLATOR_NAME + ".getBookDepthDetailsV2", GUILoggerBusinessProperty.MARKET_QUERY, struct);

        DetailBookDepth detailBook = OrderBookFactory.createDetailBookDepth(struct);

        return detailBook;
    }

    public void subscribeBookDepthForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            argObj[3] = new Short(DEFAULT_QUEUE_ACTION);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepthForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2, new SessionKeyContainer(sessionName, classKey));
        com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer orderBookConsumerV2 = cmiConsumerCacheFactoryV2.getBookDepthConsumerCache().getBookDepthConsumer( sessionName, classKey );

        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, orderBookConsumerV2) == 1)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepthForClassV2", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for BookDepth for session='"+sessionName+"' classKey="+classKey);
            try
            {
                marketQueryV2.subscribeBookDepthForClassV2(sessionName, classKey, orderBookConsumerV2, DEFAULT_QUEUE_ACTION);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookConsumerV2);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookConsumerV2);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookConsumerV2);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookConsumerV2);
                throw e;
            }
        }
    }

    public void subscribeBookDepthForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            argObj[3] = new Short(DEFAULT_QUEUE_ACTION);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepthForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2, new SessionKeyContainer(sessionName, productKey));

        try
        {
            // for BookDepth by SessionProduct, use the CMIOrderBookConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            // For BookDepth by SessionProduct, use the CMIOrderBookConsumer for the SessionProductCLASS.
            // Caching a separate consumer per product would create too many consumers.
            com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer orderBookConsumerV2 = cmiConsumerCacheFactoryV2.getBookDepthConsumerCache().getBookDepthConsumer( sessionName, classKey );

            // translator only subscribe book depth to CAS once, then it caches for multiple book depth subscription for the same product key
            int subscribeCount = SubscriptionManagerFactory.find().subscribe(key, clientListener, orderBookConsumerV2 );
            if (subscribeCount == 1)
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepthForProductV2", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for BookDepth for session='"+sessionName+"' productKey="+productKey);
                try
                {
                    marketQueryV2.subscribeBookDepthForProductV2(sessionName, productKey, orderBookConsumerV2, DEFAULT_QUEUE_ACTION);
                }
                catch (SystemException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookConsumerV2);
                    throw e;
                }
                catch (CommunicationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookConsumerV2);
                    throw e;
                }
                catch (AuthorizationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookConsumerV2);
                    throw e;
                }
                catch (DataValidationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookConsumerV2);
                    throw e;
                }
            }
            else // multiple book depth subscriptions for the same product
            if (subscribeCount > 1)
            {
                BookDepthStruct bookDepth = BookDepthV2CacheFactory.find(sessionName).getBookDepth(new Integer(productKey));

                ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, bookDepth);
                eventChannel.dispatch(event);
            }

            if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                Object[] argObj = new Object[2];
                argObj[0] = sessionName;
                argObj[1] = new Integer(productKey);
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepthForProductV2 count = " + subscribeCount, GUILoggerBusinessProperty.MARKET_QUERY, argObj);
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeBookDepthForProductV2", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void subscribeBookDepthUpdateForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw ExceptionBuilder.authorizationException("FUNCTION NOT IMPLEMENTED! V2", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
//        if (GUILoggerHome.find().isDebugOn())
//        {
//            Object[] argObj = new Object[4];
//            argObj[0] = sessionName;
//            argObj[1] = new Integer(classKey);
//            argObj[2] = clientListener;
//            argObj[3] = new Short(actionOnQueue);
//
//            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepthUpdateForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
//        }
//
//        ChannelKey key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_UPDATE_BY_PRODUCT, new SessionKeyContainer(sessionName, classKey));
//
//        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, orderBookUpdateConsumerV2) == 1)
//        {
//            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepthUpdateForClassV2", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for CurrentMarket for session='"+sessionName+"' classKey="+classKey);
//            marketQueryV2.subscribeBookDepthUpdateForProductV2(sessionName, classKey, orderBookUpdateConsumerV2, actionOnQueue);
//        }
    }

    public void subscribeBookDepthUpdateForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw ExceptionBuilder.authorizationException("FUNCTION NOT IMPLEMENTED! V2", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
//        if (GUILoggerHome.find().isDebugOn())
//        {
//            Object[] argObj = new Object[4];
//            argObj[0] = sessionName;
//            argObj[1] = new Integer(productKey);
//            argObj[2] = clientListener;
//            argObj[3] = new Short(actionOnQueue);
//
//            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepthUpdateForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
//        }
//
//        ChannelKey key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_UPDATE_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
//
//        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, orderBookUpdateConsumerV2) == 1)
//        {
//            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeBookDepthUpdateForProductV2", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for CurrentMarket for session='"+sessionName+"' productKey="+productKey);
//            marketQueryV2.subscribeBookDepthUpdateForProductV2(sessionName, productKey, orderBookUpdateConsumerV2, actionOnQueue);
//        }
    }

    public void subscribeCurrentMarketForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            argObj[3] = new Short(DEFAULT_QUEUE_ACTION);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeCurrentMarketForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
        com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer currentMarketConsumerV2 = cmiConsumerCacheFactoryV2.getCurrentMarketConsumerCache().getCurrentMarketConsumer(sessionName, classKey);

        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, currentMarketConsumerV2) == 1)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeCurrentMarketForClassV2", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for CurrentMarket for session='"+sessionName+"' classKey="+classKey);
            try
            {
                marketQueryV2.subscribeCurrentMarketForClassV2(sessionName, classKey, currentMarketConsumerV2, DEFAULT_QUEUE_ACTION);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV2);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV2);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV2);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV2);
                throw e;
            }
        }
    }

    public void subscribeCurrentMarketForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            argObj[3] = new Short(DEFAULT_QUEUE_ACTION);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeCurrentMarketForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));

        try
        {
            // for CurrentMarket by SessionProduct, use the CMICurrentMarketConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer currentMarketConsumerV2 = cmiConsumerCacheFactoryV2.getCurrentMarketConsumerCache().getCurrentMarketConsumer(sessionName, classKey);

            if (SubscriptionManagerFactory.find().subscribe(key, clientListener, currentMarketConsumerV2) == 1)
            {
                try
                {
                    marketQueryV2.subscribeCurrentMarketForProductV2(sessionName, productKey, currentMarketConsumerV2, DEFAULT_QUEUE_ACTION);
                }
                catch (SystemException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV2);
                    throw e;
                }
                catch (CommunicationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV2);
                    throw e;
                }
                catch (AuthorizationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV2);
                    throw e;
                }
                catch (DataValidationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV2);
                    throw e;
                }
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeCurrentMarketForProductV2", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void subscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            argObj[3] = new Short(DEFAULT_QUEUE_ACTION);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeExpectedOpeningPriceV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer expectedOpeningPriceConsumerV2 =
                cmiConsumerCacheFactoryV2.getEOPConsumerCache().getExpectedOpeningPriceConsumer(sessionName, classKey);
        ChannelKey key = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE, new SessionKeyContainer(sessionName, classKey));
        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, expectedOpeningPriceConsumerV2) == 1)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeExpectedOpeningPriceV2", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for Expected Opening Price for session='"+sessionName+"' classKey="+classKey);
            try
            {
                marketQueryV2.subscribeExpectedOpeningPriceForClassV2(sessionName, classKey, expectedOpeningPriceConsumerV2, DEFAULT_QUEUE_ACTION);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumerV2);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumerV2);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumerV2);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumerV2);
                throw e;
            }
        }
    }

    public void subscribeExpectedOpeningPriceForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException//, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            argObj[3] = new Short(DEFAULT_QUEUE_ACTION);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeExpectedOpeningPriceByProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        //Product product = this.getProductByKey(productKey);
        ChannelKey key = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
                                                                                         //   product.getProductKeysStruct().productKey));

        com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer expectedOpeningPriceConsumerV2 =
                cmiConsumerCacheFactoryV2.getEOPConsumerCache().getExpectedOpeningPriceConsumerForProduct(sessionName, productKey);
        if (SubscriptionManagerFactory.find().subscribe(key, null, expectedOpeningPriceConsumerV2) == 1)
        {
            try
            {
                marketQueryV2.subscribeExpectedOpeningPriceForProductV2(sessionName, productKey, expectedOpeningPriceConsumerV2, DEFAULT_QUEUE_ACTION);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, null, expectedOpeningPriceConsumerV2);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, null, expectedOpeningPriceConsumerV2);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, null, expectedOpeningPriceConsumerV2);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, null, expectedOpeningPriceConsumerV2);
                throw e;
            }
        }

        key = new ChannelKey(ChannelType.CB_OPENING_PRICE_BY_PRODUCT_V2, new SessionKeyContainer(sessionName, productKey));
        EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    public void subscribeNBBOForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
       ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS, new SessionKeyContainer(sessionName, classKey));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            argObj[3] = new Short(DEFAULT_QUEUE_ACTION);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeNBBOForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        com.cboe.idl.cmiCallbackV2.CMINBBOConsumer nbboConsumerV2 = cmiConsumerCacheFactoryV2.getNBBOConsumerCache().getNBBOConsumer(sessionName, classKey);
        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, nbboConsumerV2) == 1)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeNBBOForClassV2", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for NBBO for session='"+sessionName+"' classKey="+classKey);
            try
            {
                MarketDataCacheFactory.findNBBOV2Cache(sessionName).subscribeMarketData(classKey);
                marketQueryV2.subscribeNBBOForClassV2(sessionName, classKey, nbboConsumerV2, DEFAULT_QUEUE_ACTION);
            }
            catch (SystemException e)
            {
                MarketDataCacheFactory.findNBBOV2Cache(sessionName).unsubscribeMarketData(classKey);
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, nbboConsumerV2);
                throw e;
            }
            catch (CommunicationException e)
            {
                MarketDataCacheFactory.findNBBOV2Cache(sessionName).unsubscribeMarketData(classKey);
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, nbboConsumerV2);
                throw e;
            }
            catch (AuthorizationException e)
            {
                MarketDataCacheFactory.findNBBOV2Cache(sessionName).unsubscribeMarketData(classKey);
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, nbboConsumerV2);
                throw e;
            }
            catch (DataValidationException e)
            {
                MarketDataCacheFactory.findNBBOV2Cache(sessionName).unsubscribeMarketData(classKey);
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, nbboConsumerV2);
                throw e;
            }
        }
        else
        {
            MarketDataCacheFactory.findNBBOV2Cache(sessionName).publishMarketDataSnapshot(classKey);
        }
    }

    public void subscribeNBBOForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = productKey;
            argObj[2] = clientListener;
            argObj[3] = DEFAULT_QUEUE_ACTION;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeNBBOForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
            // for NBBO by SessionProduct, subscribe to the CAS by productClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            if(clientListener != null)
            {
                eventChannel.addChannelListener(eventChannel, clientListener, key);
            }

            try
            {
                subscribeNBBOForClassV2(sessionName, classKey, null);
            }
            // if the class-based subscription failed, remove the product-based listener from the IEC
            catch(SystemException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, key);
                }
                throw e;
            }
            catch(CommunicationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, key);
                }
                throw e;
            }
            catch(AuthorizationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, key);
                }
                throw e;
            }
            catch(DataValidationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, key);
                }
                throw e;
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeNBBOForProductV2", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void subscribeRecapForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS, new SessionKeyContainer(sessionName, classKey));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = classKey;
            argObj[2] = clientListener;
            argObj[3] = DEFAULT_QUEUE_ACTION;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeRecapForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapConsumerV2 = cmiConsumerCacheFactoryV2.getRecapConsumerCache().getRecapConsumer(sessionName, classKey);

        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, recapConsumerV2) == 1)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeRecapForClassV2", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for Recap for session='"+sessionName+"' classKey="+classKey);
            try
            {
                MarketDataCacheFactory.findRecapV2Cache(sessionName).subscribeMarketData(classKey);
                marketQueryV2.subscribeRecapForClassV2(sessionName, classKey, recapConsumerV2, DEFAULT_QUEUE_ACTION);
            }
            catch (SystemException e)
            {
                MarketDataCacheFactory.findRecapV2Cache(sessionName).unsubscribeMarketData(classKey);
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumerV2);
                throw e;
            }
            catch (CommunicationException e)
            {
                MarketDataCacheFactory.findRecapV2Cache(sessionName).unsubscribeMarketData(classKey);
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumerV2);
                throw e;
            }
            catch (AuthorizationException e)
            {
                MarketDataCacheFactory.findRecapV2Cache(sessionName).unsubscribeMarketData(classKey);
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumerV2);
                throw e;
            }
            catch (DataValidationException e)
            {
                MarketDataCacheFactory.findRecapV2Cache(sessionName).unsubscribeMarketData(classKey);
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumerV2);
                throw e;
            }
        }
        else
        {
            MarketDataCacheFactory.findRecapV2Cache(sessionName).publishMarketDataSnapshot(classKey);
        }
    }

    public void subscribeRecapForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey productChannelKey = new ChannelKey(ChannelType.CB_RECAP_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = productKey;
            argObj[2] = clientListener;
            argObj[3] = DEFAULT_QUEUE_ACTION;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeRecapForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
           // for Recap by SessionProduct, susbcribe to the CAS for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            if (clientListener != null)
            {
                eventChannel.addChannelListener(eventChannel,clientListener,productChannelKey);
            }

            try
            {
                subscribeRecapForClassV2(sessionName, classKey, null);
            }
            catch(SystemException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, productChannelKey);
                }
                throw e;
            }
            catch(CommunicationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, productChannelKey);
                }
                throw e;
            }
            catch(AuthorizationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, productChannelKey);
                }
                throw e;
            }
            catch(DataValidationException e)
            {
                if(clientListener != null)
                {
                    eventChannel.removeChannelListener(eventChannel, clientListener, productChannelKey);
                }
                throw e;
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeRecapForProduct", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void subscribeTickerForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V2, new SessionKeyContainer(sessionName, classKey));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            argObj[3] = new Short(DEFAULT_QUEUE_ACTION);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeTickerForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, tickerConsumerV2) == 1)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeTickerForClassV2", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for Ticker for session='"+sessionName+"' productKey="+classKey);
            try
            {
                marketQueryV2.subscribeTickerForClassV2(sessionName, classKey, tickerConsumerV2, DEFAULT_QUEUE_ACTION);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tickerConsumerV2);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tickerConsumerV2);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tickerConsumerV2);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tickerConsumerV2);
                throw e;
            }
        }
    }

    public void subscribeTickerForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey productChannelKey = new ChannelKey(ChannelType.CB_TICKER, new SessionKeyContainer(sessionName, productKey));

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            argObj[3] = new Short(DEFAULT_QUEUE_ACTION);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeTickerForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try {
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            ChannelKey classChannelKey = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V2, new SessionKeyContainer(sessionName, classKey));

            if (clientListener != null)
            {
                eventChannel.addChannelListener(eventChannel,clientListener,productChannelKey);
            }


            if (SubscriptionManagerFactory.find().subscribe(classChannelKey, null, tickerConsumerV2) == 1)
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeTickerForProductV2", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for Ticker for session='"+sessionName+"' productKey="+productKey);
                try
                {
                    marketQueryV2.subscribeTickerForClassV2(sessionName, classKey, tickerConsumerV2, DEFAULT_QUEUE_ACTION);
                }
                catch (SystemException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, tickerConsumerV2);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
                catch (CommunicationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, tickerConsumerV2);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
                catch (AuthorizationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, tickerConsumerV2);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
                catch (DataValidationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, tickerConsumerV2);
                    if (clientListener != null)
                    {
                        eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
                    }
                    throw e;
                }
            }

        }
        catch (NotFoundException e) {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeTickerForProductV2", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void unsubscribeBookDepthForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2, new SessionKeyContainer(sessionName, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(sessionName);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeBookDepthForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer orderBookConsumerV2 = cmiConsumerCacheFactoryV2.getBookDepthConsumerCache().getBookDepthConsumer( sessionName, classKey );
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookConsumerV2) == 0)
        {
            marketQueryV2.unsubscribeBookDepthForClassV2(sessionName, classKey, orderBookConsumerV2);

            // empty book depth cache
            //BookDepthCacheFactory.find(sessionProduct.getTradingSessionName()).removeBookDepth(new Integer(sessionProduct.getProductKey()));
        }
    }

    public void unsubscribeBookDepthForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeBookDepthForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
            // for BookDepth by SessionProduct, use the CMIOrderBookConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;
            com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer orderBookConsumerV2 = cmiConsumerCacheFactoryV2.getBookDepthConsumerCache().getBookDepthConsumer( sessionName, classKey );

            if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookConsumerV2) == 0)
            {
                marketQueryV2.unsubscribeBookDepthForProductV2(sessionName, productKey, orderBookConsumerV2);

                // empty book depth cache
                BookDepthV2CacheFactory.find(sessionName).removeBookDepth(new Integer(productKey));
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeBookDepthForProductV2", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void unsubscribeBookDepthUpdateForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw ExceptionBuilder.authorizationException("FUNCTION NOT IMPLEMENTED! V2", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
//        ChannelKey key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_CLASS_V2, new SessionKeyContainer(sessionName, classKey));
//        if (GUILoggerHome.find().isDebugOn())
//        {
//            Object[] argObj = new Object[3];
//            argObj[0] = sessionName;
//            argObj[1] = new Integer(sessionName);
//            argObj[2] = clientListener;
//            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeBookDepthUpdateForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
//        }
//
//        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookUpdateConsumerV2) == 0)
//        {
//            marketQueryV2.unsubscribeBookDepthUpdateForClassV2(sessionName, classKey, orderBookUpdateConsumerV2);
//            // empty book depth cache
//            //BookDepthCacheFactory.find(sessionProduct.getTradingSessionName()).removeBookDepth(new Integer(sessionProduct.getProductKey()));
//        }
    }

    public void unsubscribeBookDepthUpdateForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        throw ExceptionBuilder.authorizationException("FUNCTION NOT IMPLEMENTED! V2", AuthenticationCodes.FUNCTION_NOT_IMPLEMENTED);
//        ChannelKey key = new ChannelKey(ChannelType.CB_BOOK_DEPTH_BY_PRODUCT_V2, new SessionKeyContainer(sessionName, productKey));
//        if (GUILoggerHome.find().isDebugOn())
//        {
//            Object[] argObj = new Object[3];
//            argObj[0] = sessionName;
//            argObj[1] = new Integer(sessionName);
//            argObj[2] = clientListener;
//            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeBookDepthUpdateForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
//        }
//
//        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderBookUpdateConsumerV2) == 0)
//        {
//            marketQueryV2.unsubscribeBookDepthUpdateForProductV2(sessionName, productKey, orderBookUpdateConsumerV2);
//            // empty book depth cache
//            //BookDepthCacheFactory.find(sessionProduct.getTradingSessionName()).removeBookDepth(new Integer(sessionProduct.getProductKey()));
//        }
    }

    public void unsubscribeCurrentMarketForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeCurrentMarketForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer currentMarketConsumerV2 = cmiConsumerCacheFactoryV2.getCurrentMarketConsumerCache().getCurrentMarketConsumer(sessionName, classKey);
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV2) == 0)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeCurrentMarketForClassV2", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for CurrentMarket for session='"+sessionName+"' classKey="+classKey);
            marketQueryV2.unsubscribeCurrentMarketForClassV2(sessionName, classKey, currentMarketConsumerV2);
        }
    }

    public void unsubscribeCurrentMarketForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeCurrentMarketForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
            // for CurrentMarket by SessionProduct, use the CMICurrentMarketConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer currentMarketConsumerV2 = cmiConsumerCacheFactoryV2.getCurrentMarketConsumerCache().getCurrentMarketConsumer(sessionName, classKey);

            if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV2) == 0)
            {
                marketQueryV2.unsubscribeCurrentMarketForProductV2(sessionName, productKey, currentMarketConsumerV2);
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": unsubscribeCurrentMarketForProductV2", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void unsubscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeExpectedOpeningPriceForClassV2()", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE, new SessionKeyContainer(sessionName, classKey));

        com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer expectedOpeningPriceConsumerV2 =
                cmiConsumerCacheFactoryV2.getEOPConsumerCache().getExpectedOpeningPriceConsumer(sessionName, classKey);
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumerV2) == 0)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeExpectedOpeningPriceForClassV2()", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for Expected Opening Price for session='"+sessionName+"' classKey="+classKey);
            marketQueryV2.unsubscribeExpectedOpeningPriceForClassV2(sessionName, classKey, expectedOpeningPriceConsumerV2);
        }
    }

    public void unsubscribeExpectedOpeningPriceForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeExpectedOpeningPriceForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_EXPECTED_OPENING_PRICE_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));

        com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer expectedOpeningPriceConsumerV2 =
                cmiConsumerCacheFactoryV2.getEOPConsumerCache().getExpectedOpeningPriceConsumerForProduct(sessionName, productKey);
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, expectedOpeningPriceConsumerV2) == 0)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeExpectedOpeningPriceForProductV2", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for Expected Opening Price for session='"+sessionName+"' classKey="+productKey);
            marketQueryV2.unsubscribeExpectedOpeningPriceForProductV2(sessionName, productKey, expectedOpeningPriceConsumerV2);
        }
    }

    public void unsubscribeNBBOForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeNBBOForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        com.cboe.idl.cmiCallbackV2.CMINBBOConsumer nbboConsumerV2 = cmiConsumerCacheFactoryV2.getNBBOConsumerCache().getNBBOConsumer(sessionName, classKey);
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, nbboConsumerV2) == 0)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeNBBOForClassV2", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for NBBO for session='"+sessionName+"' classKey="+classKey);
            marketQueryV2.unsubscribeNBBOForClassV2(sessionName, classKey, nbboConsumerV2);
            MarketDataCacheFactory.findNBBOV2Cache(sessionName).unsubscribeMarketData(classKey);
        }
    }

    public void unsubscribeNBBOForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = productKey;
            argObj[2] = clientListener;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeNBBOForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
            // for NBBO by SessionProduct, the subscription to the CAS is really by SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            if(clientListener != null)
            {
                eventChannel.removeChannelListener(eventChannel, clientListener, key);
            }

            unsubscribeNBBOForClassV2(sessionName, classKey, null);
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+":unsubscribeNBBOForProductV2", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void unsubscribeRecapForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_RECAP_BY_CLASS, new SessionKeyContainer(sessionName, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = classKey;
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeRecapForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        com.cboe.idl.cmiCallbackV2.CMIRecapConsumer recapConsumerV2 = cmiConsumerCacheFactoryV2.getRecapConsumerCache().getRecapConsumer(sessionName, classKey);
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, recapConsumerV2) == 0)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeRecapForClassV2", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for Recap for session='"+sessionName+"' classKey="+classKey);
            marketQueryV2.unsubscribeRecapForClassV2(sessionName, classKey, recapConsumerV2);
            MarketDataCacheFactory.findRecapV2Cache(sessionName).unsubscribeMarketData(classKey);
        }
    }

    public void unsubscribeRecapForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey productChannelKey = new ChannelKey(ChannelType.CB_RECAP_BY_PRODUCT, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = productKey;
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeRecapForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
           // for Recap by SessionProduct, the subscription to the CAS is by the SessionProductClass
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            if (clientListener != null)
            {
                eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
            }

            unsubscribeRecapForClassV2(sessionName, classKey, null);
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": unsubscribeRecapForProductV2", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    public void unsubscribeTickerForClassV2(String sessionName, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V2, new SessionKeyContainer(sessionName, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeTickerForClassV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tickerConsumerV2) == 0)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeTickerForClassV2", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for Ticker for session='"+sessionName+"' productKey="+classKey);
            marketQueryV2.unsubscribeTickerForClassV2(sessionName, classKey, tickerConsumerV2);
        }
    }

    public void unsubscribeTickerForProductV2(String sessionName, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey productChannelKey = new ChannelKey(ChannelType.CB_TICKER, new SessionKeyContainer(sessionName, productKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeTickerForProductV2", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try {
            int classKey = getProductByKeyForSession(sessionName, productKey).getProductKeysStruct().classKey;

            ChannelKey classChannelKey = new ChannelKey(ChannelType.CB_TICKER_BY_CLASS_V2, new SessionKeyContainer(sessionName, classKey));

            if (clientListener != null)
            {
                eventChannel.removeChannelListener(eventChannel,clientListener,productChannelKey);
            }


            if (SubscriptionManagerFactory.find().unsubscribe(classChannelKey, null, tickerConsumer) == 0) {
                GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeTickerForProductV2", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for Ticker for session='"+sessionName+"' productKey="+productKey);
                marketQueryV2.unsubscribeTickerForClassV2(sessionName, classKey, tickerConsumerV2);
            }
        }
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": unsubscribeTickerForProductV2", "Unable to get SessionProduct. Session:'"+sessionName+"' Key="+productKey, e);
        }
    }

    // OrderQueryV2API
    public void subscribeOrderStatusForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeOrderStatusForClassV2",
                                       GUILoggerBusinessProperty.ORDER_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_BY_CLASS, new Integer(classKey));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    public void subscribeOrderStatusForFirmForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": subscribeOrderStatusForFirmForClassV2",
                                       GUILoggerBusinessProperty.ORDER_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, new Integer(classKey));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    public void subscribeOrderStatusV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(isFirmUser())
        {
            subscribeOrdersByFirm(clientListener);
        }
        else if(isOrderSubscriptionAllowed())
        {
            subscribeOrdersByUser(clientListener);
        }
    }

    public void unsubscribeOrderStatusForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            argObj[2] = new Boolean(gmdCallback);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderStatusForClassV2",GUILoggerBusinessProperty.ORDER_QUERY,argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_BY_CLASS, new Integer(classKey));

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderStatusConsumerV2) == 0)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderStatusForClassV2", GUILoggerBusinessProperty.ORDER_QUERY,"unsubscribing translator to CAS for OrderStatus for  classKey="+classKey);
            orderQueryV2.unsubscribeOrderStatusForClassV2(classKey, orderStatusConsumerV2);
        }
    }

    public void unsubscribeOrderStatusForFirmForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            argObj[2] = new Boolean(gmdCallback);
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderStatusForFirmForClassV2",GUILoggerBusinessProperty.ORDER_QUERY,argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_ORDERS_BY_FIRM, new Integer(classKey));

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderStatusConsumerV2) == 0)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderStatusForFirmForClassV2", GUILoggerBusinessProperty.ORDER_QUERY,"unsubscribing translator to CAS for OrderStatus for  classKey="+classKey);
            orderQueryV2.unsubscribeOrderStatusForFirmForClassV2(classKey, orderStatusConsumerV2);
        }
    }

    public void unsubscribeOrderStatusV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_QUERY))
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(0);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderStatusV2",GUILoggerBusinessProperty.ORDER_QUERY,argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_ALL_ORDERS, new Integer(0));

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, orderStatusConsumerV2) == 0)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeOrderStatusV2", GUILoggerBusinessProperty.ORDER_QUERY,"unsubscribing translator to CAS for OrderStatus for classKey=0");
            orderQueryV2.unsubscribeOrderStatusV2( orderStatusConsumerV2 );
        }
    }

    /**
     * @param product
     * @param quantity
     * @param price
     * @param side
     * @param millis millisecond time range
     * @return return true if another order matching this order (product, price, qty, side) has been entered within the specified time range
     */
    public List<OrderDetailStruct> getOrders(SessionProduct product, int quantity, Price price, char side, int millis)
    {
        return orderQueryCache.getOrders(product, quantity, price, side, millis);
    }

    public OrderFilledReportStruct[] getOrderFilledReportsByOrderId(OrderId orderId)
    {
        return filledReportCache.getFilledReportsForOrder(orderId);
    }

    public int getOrderFilledReportsCountByOrderId(OrderId orderId)
    {
        return filledReportCache.getFilledReportsCountForOrder(orderId);
    }

    public void subscribeCurrentMarketForClassV3(String session, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = session;
            argObj[1] = classKey;
            argObj[2] = clientListener;
            argObj[3] = DEFAULT_QUEUE_ACTION;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeCurrentMarketForClassV3", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3, new SessionKeyContainer(session, classKey));
        com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer currentMarketConsumerV3 = cmiConsumerCacheFactoryV3.getCurrentMarketConsumerCache().getCurrentMarketConsumer(session, classKey);

        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, currentMarketConsumerV3) == 1)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeCurrentMarketForClassV3", GUILoggerBusinessProperty.MARKET_QUERY,"subscribing translator to CAS for CurrentMarket for session='"+session+"' classKey="+classKey);
            try
            {
                MarketDataCacheFactory.findCurrentMarketV3Cache(session).subscribeMarketData(classKey);
                marketQueryV3.subscribeCurrentMarketForClassV3(session, classKey, currentMarketConsumerV3, DEFAULT_QUEUE_ACTION);
            }
            catch (SystemException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV3);
                MarketDataCacheFactory.findCurrentMarketV3Cache(session).unsubscribeMarketData(classKey);
                throw e;
            }
            catch (CommunicationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV3);
                MarketDataCacheFactory.findCurrentMarketV3Cache(session).unsubscribeMarketData(classKey);
                throw e;
            }
            catch (AuthorizationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV3);
                MarketDataCacheFactory.findCurrentMarketV3Cache(session).unsubscribeMarketData(classKey);
                throw e;
            }
            catch (DataValidationException e)
            {
                SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV3);
                MarketDataCacheFactory.findCurrentMarketV3Cache(session).unsubscribeMarketData(classKey);
                throw e;
            }
        }
        else
        {
            // if we were already subscribed, just republish a snapshot of the current market for the class
            MarketDataCacheFactory.findCurrentMarketV3Cache(session).publishMarketDataSnapshot(classKey);
        }
    }

    public void subscribeCurrentMarketForProductV3(String session, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[4];
            argObj[0] = session;
            argObj[1] = productKey;
            argObj[2] = clientListener;
            argObj[3] = DEFAULT_QUEUE_ACTION;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeCurrentMarketForProductV3", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        ChannelKey productChannelKey = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3, new SessionKeyContainer(session, productKey));

        try
        {
            // for CurrentMarket by SessionProduct, use the CMICurrentMarketConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(session, productKey).getProductKeysStruct().classKey;
            if(clientListener != null)
            {
                eventChannel.addChannelListener(eventChannel, clientListener, productChannelKey);
            }

            try
            {
                // subscribe to the CAS by class, not product
                subscribeCurrentMarketForClassV3(session, classKey, null);
            }
            catch (SystemException e)
            {
                if(clientListener != null)
                {
                    // unsubscribe the product EventChannelListener
                    eventChannel.removeChannelListener(eventChannel, clientListener, productChannelKey);
                }
                throw e;
            }
            catch (CommunicationException e)
            {
                if(clientListener != null)
                {
                    // unsubscribe the product EventChannelListener
                    eventChannel.removeChannelListener(eventChannel, clientListener, productChannelKey);
                }
                throw e;
            }
            catch (AuthorizationException e)
            {
                if(clientListener != null)
                {
                    // unsubscribe the product EventChannelListener
                    eventChannel.removeChannelListener(eventChannel, clientListener, productChannelKey);
                }
                throw e;
            }
            catch (DataValidationException e)
            {
                if(clientListener != null)
                {
                    // unsubscribe the product EventChannelListener
                    eventChannel.removeChannelListener(eventChannel, clientListener, productChannelKey);
                }
                throw e;
            }
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": subscribeCurrentMarketForProductV3", "Unable to get SessionProduct. Session:'"+session+"' Key="+productKey, e);
        }
    }

    public void unsubscribeCurrentMarketForClassV3(String session, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_CURRENT_MARKET_BY_CLASS_V3, new SessionKeyContainer(session, classKey));
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = session;
            argObj[1] = classKey;
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeCurrentMarketForClassV3", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer currentMarketConsumerV3 = cmiConsumerCacheFactoryV3.getCurrentMarketConsumerCache().getCurrentMarketConsumer(session, classKey);
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, currentMarketConsumerV3) == 0)
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeCurrentMarketForClassV3", GUILoggerBusinessProperty.MARKET_QUERY,"unsubscribing translator to CAS for CurrentMarket for session='"+session+"' classKey="+classKey);
            marketQueryV3.unsubscribeCurrentMarketForClassV3(session, classKey, currentMarketConsumerV3);
            MarketDataCacheFactory.findCurrentMarketV3Cache(session).unsubscribeMarketData(classKey);
        }
    }

    public void unsubscribeCurrentMarketForProductV3(String session, int productKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = session;
            argObj[1] = productKey;
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeCurrentMarketForProductV3", GUILoggerBusinessProperty.MARKET_QUERY, argObj);
        }

        try
        {
            // for CurrentMarket by SessionProduct, use the CMICurrentMarketConsumer for the SessionProductClass
            int classKey = getProductByKeyForSession(session, productKey).getProductKeysStruct().classKey;

            unsubscribeCurrentMarketForClassV3(session, classKey, null);
        }
        catch(NotFoundException e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME+": unsubscribeCurrentMarketForProductV2", "Unable to get SessionProduct. Session:'"+session+"' Key="+productKey, e);
        }
    }

	/* (non-Javadoc)
	 * @see com.cboe.interfaces.presentation.api.ProductQueryAPI#getAllProductsForClassForType(short, int, boolean, com.cboe.util.event.EventChannelListener)
	 */
	public Product[] getAllProductsByClass(int classKey, boolean activeOnly, EventChannelListener clientListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Product[] product = getAllProductsForClass(classKey,activeOnly);
        subscribeProductsByClass(classKey,clientListener);
		return product;
	}

	/* (non-Javadoc)
	 * @see com.cboe.interfaces.presentation.api.ProductQueryAPI#subscribeProductsForType(short, int, com.cboe.util.event.EventChannelListener)
	 */
	public void subscribeProductsByClass(int classKey, EventChannelListener clientListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
            try
            {
                ChannelKey key = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, new ObjectKeyContainer(new Integer(classKey),classKey));
                //int subscriptionCount = SubscriptionManagerFactory.find().subscribe(key, clientListener, productStatusConsumer);
                SubscriptionManagerFactory.find().subscribe(key, clientListener, productStatusConsumer);
                TradingSessionStruct[] sessions = tradingSessionCache.getCurrentTradingSessions();
                if (isStrategy(classKey))
                {
                    for (int i=0; i<sessions.length;i++)
                    {
                        try {
                            if (getClassByKeyForSession(sessions[i].sessionName, classKey) != null)
                            {
                                loadStrategiesForClassBySession(sessions[i].sessionName,classKey);
                            }
                        }
                        catch (Exception ex)
                        {
                           //Ignore exception here since class may not exist for the session
                        }
                    }
                }
                else
                {
                    for (int i=0; i<sessions.length;i++)
                    {
                        try
                        {
                            if (getClassByKeyForSession(sessions[i].sessionName, classKey) != null)
                            {
                                loadProductsForClassBySession(sessions[i].sessionName,classKey);
                            }
                        }
                        catch (Exception ex)
                        {
                            //Ignore exception here since class may not exist for the session
                        }
                    }
                }
           }
           catch (NotFoundException e)
           {
                DataValidationException de = new DataValidationException();
                de.details = e.details;
                throw de;
           }


	}

	/* (non-Javadoc)
	 * @see com.cboe.interfaces.presentation.api.ProductQueryAPI#unsubscribeProductsForType(short, int, com.cboe.util.event.EventChannelListener)
	 */
	public void unsubscribeProductsByClass(int classKey, EventChannelListener clientListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
        ChannelKey key;
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = new Integer(classKey);
            argObj[1] = new Integer(classKey);
            argObj[2] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeProductsByClass", GUILoggerBusinessProperty.PRODUCT_QUERY, argObj);
        }

        key = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, new ObjectKeyContainer(new Integer(classKey), classKey));
        //int subscriptionCount = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, productStatusConsumer);
        SubscriptionManagerFactory.find().unsubscribe(key, clientListener, productStatusConsumer);
	}
    /**
     * Gets the Auction's for the given class and session.
     *
     * @param sessionName
     * @param classKey
     *
     * @return Auction[]
     */
    public Auction[] getAuctionForClass(String sessionName, int classKey, short[] types, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = types;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAuctionForClass", GUILoggerBusinessProperty.AUCTION, argObj);
        }

        subscribeAuctionForClass(sessionName, classKey, types, listener);
        return AuctionCacheFactory.find(sessionName).getAuctionForClass(classKey);
    }

    /**
     * Gets the Auction's for the given product and session.
     *
     * @param sessionName
     * @param productKey
     *
     * @return Auction[]
     */
    public Auction[] getAuctionForProduct(String sessionName, int productKey, short[] types, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = types;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getAuctionForProduct", GUILoggerBusinessProperty.AUCTION, argObj);
        }

        Product product = getProductByKey(productKey);
        subscribeAuctionForClass(sessionName, product.getProductKeysStruct().classKey, types, listener);

        return AuctionCacheFactory.find(sessionName).getAuctionForProduct(productKey);
    }

    public Auction[] getCachedAuctionForSession(String sessionName, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": getCachedAuctionsForSession", GUILoggerBusinessProperty.AUCTION, sessionName);
        }

        registerAuctionListener(sessionName, listener);
        return AuctionCacheFactory.find(sessionName).getAllAuctions();
    }

    /**
     * Subscribes an EventChannelListener to receive events for the given session and class. This method returns all of
     * the current Auctions for the given criteria.
     *
     * @param sessionName
     * @param classKey
     */
    public void subscribeAuctionForClass(String sessionName, int classKey, short[] auctionTypes, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = auctionTypes;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeAuctionForClass", GUILoggerBusinessProperty.AUCTION, argObj);
        }

        // subscribe one by one
        int length = auctionTypes.length;
        ArrayList<AuctionSubscriptionResultStruct[]> results = new ArrayList<AuctionSubscriptionResultStruct[]>(length);
        for (int i=0; i<length; i++)
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_AUCTION, new AuctionTypeContainer(sessionName, classKey, auctionTypes[i]));
            if (SubscriptionManagerFactory.find().subscribe(key, listener, auctionConsumer) == 1)
            {
                short[] auctionType = new short[1];
                auctionType[0] = auctionTypes[i];
                try
                {
                    AuctionSubscriptionResultStruct theResult[] = orderQueryV3.subscribeAuctionForClass(sessionName, classKey, auctionType, auctionConsumer);
                    results.add(theResult);
                }
                catch (SystemException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, listener, auctionConsumer);
                    throw e;
                }
                catch (CommunicationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, listener, auctionConsumer);
                    throw e;
                }
                catch (AuthorizationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, listener, auctionConsumer);
                    throw e;
                }
                catch (DataValidationException e)
                {
                    SubscriptionManagerFactory.find().unsubscribe(key, listener, auctionConsumer);
                    throw e;
                }
            }
        }

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": subscribeAuctionForClass()-Completed...",
                                       GUILoggerBusinessProperty.AUCTION,
                                       results.toArray(new AuctionSubscriptionResultStruct[0][0]));
        }

        checkAuctionSubscriptionResults(results);
    }

    /**
     * Unsubscribes an EventChannelListener for the given session and class.
     *
     * @param sessionName
     * @param classKey
     */
    public void unsubscribeAuctionForClass(String sessionName, int classKey, short[] auctionTypes, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = auctionTypes;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeAuctionForClass", GUILoggerBusinessProperty.AUCTION, argObj);
        }

        int length = auctionTypes.length;
        ArrayList<AuctionSubscriptionResultStruct[]> results = new ArrayList<AuctionSubscriptionResultStruct[]>(length);
        for (int i=0; i<length; i++)
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_AUCTION, new AuctionTypeContainer(sessionName, classKey, auctionTypes[i]));
            if (SubscriptionManagerFactory.find().unsubscribe(key, listener, auctionConsumer) == 0)
            {
                short[] auctionType = new short[1];
                auctionType[0] = auctionTypes[i];

                AuctionSubscriptionResultStruct theResult[] = orderQueryV3.unsubscribeAuctionForClass(sessionName, classKey, auctionType, auctionConsumer);
                results.add(theResult);
            }
        }

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unsubscribeAuctionForClass()-Completed...", GUILoggerBusinessProperty.AUCTION, results);
        }

        checkAuctionSubscriptionResults(results);
    }

    /**
     * This method steps through the array of AuctionSubscriptionResultStruct checking the errocodes and
     * generates a DataValidationException if an error is found.  Any none zero errorcode is consider an error...
     *
     * @param results ArrayList<AuctionSubscriptionResultStruct[]>
     */
    protected void checkAuctionSubscriptionResults(ArrayList<AuctionSubscriptionResultStruct[]> results) throws DataValidationException
    {
        boolean error = false;
        StringBuffer buffer = new StringBuffer();
        for (AuctionSubscriptionResultStruct[] theResult: results)
        {
            if (theResult[0] != null && theResult[0].subscriptionResult.errorCode != 0)
            {
                error = true;
                buffer.append(FormatFactory.getAuctionSubscriptionResultFormatStrategy().format(theResult[0]));
                buffer.append("\n");
            }
        }

        if (error)
        {
            throw ExceptionBuilder.dataValidationException(buffer.toString(), DataValidationCodes.INVALID_AUCTION_TYPE);
        }
    }

    public InternalizationOrderResultStruct acceptInternalizationOrder(OrderEntryStruct primaryOrderEntry,
                                                                       OrderEntryStruct matchedOrderEntry,
                                                                       short matchOrderType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotAcceptedException, TransactionFailedException
    {
        String methodName = "acceptInternalizationOrder";
        logTransaction(methodName, primaryOrderEntry, matchedOrderEntry, MatchOrderTypes.toString(matchOrderType));
        InternalizationOrderResultStruct results = null;
        try
        {
            results = orderEntryV3.acceptInternalizationOrder(primaryOrderEntry, matchedOrderEntry, matchOrderType);
            checkInternalizationOrderResults(results);
        }
        catch(Exception e)
        {
            try
            {
                handleTransactionException(methodName, e);
            }
            catch (AlreadyExistsException ae)
            {
                // Not valid for this method, must be handled
            }
        }
        return results;
    }
    
    public InternalizationOrderResultStruct acceptInternalizationStrategyOrder( OrderEntryStruct primaryOrder,
    																	 LegOrderEntryStruct[] primaryOrderLegDetails,
    																	 OrderEntryStruct matchedOrder,
    																	 LegOrderEntryStruct[] matchOrderLegDetails,
                                                                		 short matchOrderType)
            							throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            								   NotAcceptedException, TransactionFailedException
    {

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_ENTRY))
        {
            Object[] argObj = new Object[5];
            argObj[0] = primaryOrder;
            argObj[1] = primaryOrderLegDetails;
            argObj[2] = matchedOrder;
            argObj[3] = matchOrderLegDetails;
            argObj[4] = MatchOrderTypes.toString(matchOrderType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptInternalizationStrategyOrder", GUILoggerBusinessProperty.ORDER_ENTRY, argObj);
        }

        InternalizationOrderResultStruct results = orderEntryV5.acceptInternalizationStrategyOrder(primaryOrder,primaryOrderLegDetails, matchedOrder, matchOrderLegDetails, matchOrderType);
        checkInternalizationOrderResults(results);

        return results;
    }
    
    public InternalizationOrderResultStruct acceptInternalizationStrategyOrderV7( OrderEntryStruct primaryOrder,
																					LegOrderEntryStructV2 [] primaryOrderLegDetailsV2,
																					OrderEntryStruct matchedOrder,
																					LegOrderEntryStructV2 [] matchOrderLegDetailsV2,
																					short matchOrderType)
    throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
    NotAcceptedException, TransactionFailedException
    {	

    	if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_ENTRY))
    	{
    		Object[] argObj = new Object[5];
    		argObj[0] = primaryOrder;
    		argObj[1] = primaryOrderLegDetailsV2;
    		argObj[2] = matchedOrder;
    		argObj[3] = matchOrderLegDetailsV2;
    		argObj[4] = MatchOrderTypes.toString(matchOrderType);

    		GUILoggerHome.find().debug(TRANSLATOR_NAME+": acceptInternalizationStrategyOrderV3", GUILoggerBusinessProperty.ORDER_ENTRY, argObj);
    	}

    	InternalizationOrderResultStruct results = orderEntryV7.acceptInternalizationStrategyOrderV7(primaryOrder,primaryOrderLegDetailsV2, matchedOrder, matchOrderLegDetailsV2, matchOrderType);
    	checkInternalizationOrderResults(results);

    	return results;
    }   

    private void checkInternalizationOrderResults(InternalizationOrderResultStruct results) throws DataValidationException
    {
        if (results != null && results.primaryOrderResult != null && results.matchOrderResult != null)
        {
            if (results.primaryOrderResult.result.errorCode != 0 || results.matchOrderResult.result.errorCode != 0)
            {
                OperationResultFormatStrategy formatter = FormatFactory.getOperationResultFormatStrategy();

                StringBuffer textBuffer = new StringBuffer(255);
                if (results.primaryOrderResult.result.errorCode != 0)
                {
                     textBuffer.append("Primary: ");
                     textBuffer.append(formatter.format(results.primaryOrderResult.result));
                     textBuffer.append('\n');
                }

                if (results.matchOrderResult.result.errorCode != 0)
                {
                    textBuffer.append("Match: ");
                    textBuffer.append(formatter.format(results.matchOrderResult.result));
                }

                throw ExceptionBuilder.dataValidationException(textBuffer.toString(), 0);
            }
        }
        else
        {
            GUILoggerHome.find().alarm("The InternalizationOrderResultStruct is null or internal structs are null...");
        }
    }

    public void registerAuctionListener(String sessionName, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": registerAuctionListener", GUILoggerBusinessProperty.AUCTION, sessionName);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_AUCTION, sessionName);
        EventChannelAdapterFactory.find().addChannelListener(eventChannel, listener, key);
    }

    public void unregisterAuctionListener(String sessionName, EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.AUCTION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+": unregisterAuctionListener", GUILoggerBusinessProperty.AUCTION, sessionName);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_AUCTION, sessionName);
        EventChannelAdapterFactory.find().removeChannelListener(eventChannel, listener, key);
    }

    /*
     * Logs any ORDER_ENTRY transaction to the audit and debug logs; transaction may have any number of parameters.
     */
    static void logTransaction(String transactionName, Object... parm)
    {
        String text = TRANSLATOR_NAME + ": " + transactionName;
        GUILoggerHome.find().audit(text, parm);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.ORDER_ENTRY))
        {
            GUILoggerHome.find().debug(text, GUILoggerBusinessProperty.ORDER_ENTRY, parm);
        }
    }

    static void handleTransactionException(String apiMethodName, Exception e) throws
            SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        if(e instanceof UserException)
        {
            StringBuilder buffer = new StringBuilder(apiMethodName).append(" threw CORBA Exception: ")
                    .append(e.getMessage()).append(" Details: ");
            if(e instanceof SystemException)
            {
                SystemException se = (SystemException) e;
                buffer.append(se.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw se;
            }
            else if(e instanceof CommunicationException)
            {
                CommunicationException ce = (CommunicationException) e;
                buffer.append(ce.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw ce;
            }
            else if(e instanceof DataValidationException)
            {
                DataValidationException dve = (DataValidationException) e;
                buffer.append(dve.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw dve;
            }
            else if(e instanceof TransactionFailedException)
            {
                TransactionFailedException tfe = (TransactionFailedException) e;
                buffer.append(tfe.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw tfe;
            }
            else if(e instanceof NotAcceptedException)
            {
                NotAcceptedException nae = (NotAcceptedException) e;
                buffer.append(nae.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw nae;
            }
            else if(e instanceof AuthorizationException)
            {
                AuthorizationException ae = (AuthorizationException) e;
                buffer.append(ae.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw ae;
            }
            else if(e instanceof AlreadyExistsException)
            {
                AlreadyExistsException aee = (AlreadyExistsException) e;
                buffer.append(aee.details.message);
                GUILoggerHome.find().audit(buffer.toString());
                throw aee;
            }
            else
            {
                GUILoggerHome.find().audit(buffer.toString());
                throw new SystemException(buffer.toString(), new ExceptionDetails(buffer.toString(), new Date().toString(), (short)0, 0));
            }
        }
        // this will handle org.omg.CORBA.UNKNOWN, etc.
        else
        {
            String text = apiMethodName + " threw unexpected exception: '"+e.toString()+"' Message: '" + e.getMessage()+"'";
            GUILoggerHome.find().audit(text);
            // log the original exception
            GUILoggerHome.find().exception(e);
            throw new SystemException(e.toString(), new ExceptionDetails(text, new Date().toString(), (short) 0, 0));
        }
    }

    private boolean isOmtRole()
    {
        UserPermissionMatrix matrix =
                UserSessionFactory.findUserSession().getUserPermissionMatrix();
        return (matrix.isAllowed(Permission.OMT_ORDER_QUERY) ||
                matrix.isAllowed(Permission.ORDER_MANAGEMENT_TERMINAL_ACCESS));
    }
    
    private boolean isHDRole()
    {
    	UserPermissionMatrix matrix =
            UserSessionFactory.findUserSession().getUserPermissionMatrix();
    return (matrix.isAllowed(Permission.USER_MANAGEMENT_ACCESS) ||
            matrix.isAllowed(Permission.USER_MANAGEMENT_ORDERS_ACCESS));
    }

    private OrderDetailStruct buildOrderDetailStruct(OrderStruct order)
    {
        ProductNameStruct productName = null;
        return new OrderDetailStruct(productName, StatusUpdateReasons.QUERY, order);
    }
    
    public CboeIdStruct acceptFloorTrade(FloorTradeEntryStruct aFloorTrade) 
    					throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
		String methodName = "acceptFloorTrade";
		logTransaction(methodName, aFloorTrade);
		CboeIdStruct  results = null;
		
		try
		{
			results = floorTradeMaintenanceService.acceptFloorTrade(aFloorTrade);
		}
		catch(Exception e)
		{
			handleTransactionException(methodName, e);
		}
		return results;
    }

    public void deleteFloorTrade( String sessionName, int productKey, CboeIdStruct tradeId, ExchangeAcronymStruct user, ExchangeFirmStruct firm, String reason)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        String methodName = "deleteFloorTrade";
        logTransaction(methodName, sessionName, productKey, tradeId, user, firm, reason);

        try
        {
            floorTradeMaintenanceService.deleteFloorTrade(sessionName, productKey, tradeId, user, firm, reason);
        }
        catch(Exception e)
        {
            handleTransactionException(methodName, e);
        }
    }

    public void subscribeForFloorTradeReportsByClass( int classKey, EventChannelListener clientListener)
					throws SystemException, CommunicationException, AuthorizationException, DataValidationException 
	{
		ChannelKey key = new ChannelKey(ChannelType.CB_MMHH_QUOTE_FILL_REPORT_V2, new Integer(classKey));
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        
        SubscriptionManagerFactory.find().subscribe(key, clientListener, quoteStatusConsumerV2);
        floorTradeMaintenanceService.subscribeForFloorTradeReportsByClass(quoteStatusConsumerV2, classKey, gmdCallback);
	}

	public void unsubscribeForFloorTradeReportsByClass( int classKey, EventChannelListener clientListener) 
						throws SystemException, CommunicationException, AuthorizationException, DataValidationException
	{
        ChannelKey key = new ChannelKey(ChannelType.CB_MMHH_QUOTE_FILL_REPORT_V2, new Integer(classKey));
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);

        SubscriptionManagerFactory.find().unsubscribe(key, clientListener, quoteStatusConsumerV2);
        floorTradeMaintenanceService.unsubscribeForFloorTradeReportsByClass(quoteStatusConsumerV2, classKey);
	}
	
	@Override
    public Product getProductByKeyFromCache(int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {
        return getProductByKey(productKey);
    }


	/**
     * @return the orderEntryAPI
     */
    public OrderEntryFacade getOrderEntryAPI()
    {
    	return orderEntryAPI;
    }

}
