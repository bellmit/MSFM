package com.cboe.internalPresentation.api;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omg.CORBA.IntHolder;
import org.omg.CORBA.UserException;

import com.cboe.consumers.callback.SubscriptionManagerFactory;
import com.cboe.consumers.callback.SubscriptionManagerImpl;
import com.cboe.consumers.internalPresentation.AdminCMICallbackConsumerCacheFactoryImpl;
import com.cboe.consumers.internalPresentation.AdminProductStatusConsumerFactory;
import com.cboe.consumers.internalPresentation.AlertConsumerFactory;
import com.cboe.consumers.internalPresentation.CacheUpdateCallbackConsumerFactory;
import com.cboe.consumers.internalPresentation.CalendarUpdateConsumerFactory;
import com.cboe.consumers.internalPresentation.ClassStatusConsumerFactory;
import com.cboe.consumers.internalPresentation.ExpectedOpeningPriceConsumerFactory;
import com.cboe.consumers.internalPresentation.GroupElementCallbackConsumerFactory;
import com.cboe.consumers.internalPresentation.IntermarketAdminMessageConsumerFactory;
import com.cboe.consumers.internalPresentation.LinkageStateConsumerFactory;
import com.cboe.consumers.internalPresentation.OrderStatusConsumerFactory;
import com.cboe.consumers.internalPresentation.ProductStatusConsumerFactory;
import com.cboe.consumers.internalPresentation.PropertyConsumerFactory;
import com.cboe.consumers.internalPresentation.QuoteStatusConsumerFactory;
import com.cboe.consumers.internalPresentation.RFQConsumerFactory;
import com.cboe.consumers.internalPresentation.StrategyStatusConsumerFactory;
import com.cboe.consumers.internalPresentation.SystemControlCallbackConsumerFactory;
import com.cboe.consumers.internalPresentation.TickerConsumerFactory;
import com.cboe.consumers.internalPresentation.TradingSessionEventStateCallbackConsumerFactory;
import com.cboe.consumers.internalPresentation.TradingSessionStatusConsumerFactory;
import com.cboe.domain.tradingProperty.TradingPropertyTypeImpl;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CacheInitializationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.alert.AlertHistoryStruct;
import com.cboe.idl.alert.AlertSearchCriteriaStruct;
import com.cboe.idl.businessServices.ExchangeService;
import com.cboe.idl.businessServices.FirmService;
import com.cboe.idl.businessServices.GroupCancelEventService;
import com.cboe.idl.businessServices.MarketMakerQuoteService;
import com.cboe.idl.businessServices.OrderBookService;
import com.cboe.idl.businessServices.OrderHandlingService;
import com.cboe.idl.businessServices.ProductQueryService;
import com.cboe.idl.businessServices.PropertyEventService;
import com.cboe.idl.businessServices.TextMessagingService;
import com.cboe.idl.businessServices.TradingSessionEventStateService;
import com.cboe.idl.businessServices.TradingSessionService;
import com.cboe.idl.businessServices.UserMaintenanceEventService;
import com.cboe.idl.businessServices.UserService;
import com.cboe.idl.businessServices.UserTradingParameterService;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiIntermarketMessages.AlertStruct;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.cmiQuote.ClassQuoteResultStruct;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiSession.SessionClassDetailStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyRequestStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.PreferenceStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.constants.FederatedOperationType;
import com.cboe.idl.constants.PropertyFederatedBulkOperation;
import com.cboe.idl.consumers.CacheUpdateConsumer;
import com.cboe.idl.consumers.GroupElementConsumer;
import com.cboe.idl.consumers.IntermarketAdminMessageConsumer;
import com.cboe.idl.consumers.ProductStatusConsumer;
import com.cboe.idl.consumers.PropertyConsumer;
import com.cboe.idl.consumers.SystemControlConsumer;
import com.cboe.idl.consumers.TradingSessionEventStateConsumer;
import com.cboe.idl.exchange.ExchangeStruct;
import com.cboe.idl.firm.FirmStruct;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.idl.groupElement.ElementErrorResultStruct;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.idl.infrastructureServices.CalendarAdminEventService;
import com.cboe.idl.infrastructureServices.securityService.securityAdmin.MemberAccountStruct;
import com.cboe.idl.infrastructureServices.securityService.securityAdmin.SecurityAdminService;
import com.cboe.idl.infrastructureServices.sessionManagementService.ComponentStruct;
import com.cboe.idl.infrastructureServices.sessionManagementService.SessionManagementAdminService;
import com.cboe.idl.infrastructureServices.sessionManagementService.UserLoginStruct;
import com.cboe.idl.internalApplication.SystemAdminSessionManager;
import com.cboe.idl.internalBusinessServices.AlertEventService;
import com.cboe.idl.internalBusinessServices.FirmMaintenanceService;
import com.cboe.idl.internalBusinessServices.GroupElementEventService;
import com.cboe.idl.internalBusinessServices.GroupService;
import com.cboe.idl.internalBusinessServices.OrderMaintenanceService;
import com.cboe.idl.internalBusinessServices.ProductConfigurationService;
import com.cboe.idl.internalBusinessServices.ProductMaintenanceEventService;
import com.cboe.idl.internalBusinessServices.ProductStateService;
import com.cboe.idl.internalBusinessServices.TradeMaintenanceService;
import com.cboe.idl.internalBusinessServices.TradingPropertyServiceExt;
import com.cboe.idl.internalConsumers.AlertConsumer;
import com.cboe.idl.internalConsumers.CalendarUpdateConsumer;
import com.cboe.idl.marketData.BookDepthDetailStruct;
import com.cboe.idl.orderBook.BestBookStruct;
import com.cboe.idl.orderBook.TradableStruct;
import com.cboe.idl.product.ClassDefinitionStruct;
import com.cboe.idl.product.ClassOpenInterestStruct;
import com.cboe.idl.product.ClassSettlementStruct;
import com.cboe.idl.product.ErrorCodeResultStruct;
import com.cboe.idl.product.GroupErrorCodeResultStruct;
import com.cboe.idl.product.GroupStruct;
import com.cboe.idl.product.GroupTypeStruct;
import com.cboe.idl.product.LinkageIndicatorResultStruct;
import com.cboe.idl.product.PriceAdjustmentStruct;
import com.cboe.idl.product.ProductClassExtStruct;
import com.cboe.idl.product.ProductClassStruct;
import com.cboe.idl.product.ProductOpenInterestStruct;
import com.cboe.idl.product.ProductSettlementStruct;
import com.cboe.idl.product.ProductStructV2;
import com.cboe.idl.product.ProductStructV4;
import com.cboe.idl.product.SessionCodeDescriptionStruct;
import com.cboe.idl.product.TransactionFeeCodeStruct;
import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.idl.quote.InternalQuoteStruct;
import com.cboe.idl.quote.UserAdminQuoteRiskManagementProfileStruct;
import com.cboe.idl.session.BusinessDayStruct;
import com.cboe.idl.session.ClassStateDetailStruct;
import com.cboe.idl.session.TemplateClassStruct;
import com.cboe.idl.session.TradingSessionElementStruct;
import com.cboe.idl.session.TradingSessionElementStructV2;
import com.cboe.idl.session.TradingSessionElementTemplateStruct;
import com.cboe.idl.session.TradingSessionElementTemplateStructV2;
import com.cboe.idl.session.TradingSessionEventHistoryStruct;
import com.cboe.idl.session.TradingSessionEventHistoryStructV2;
import com.cboe.idl.session.TradingSessionGroupStruct;
import com.cboe.idl.session.TradingSessionRegistrationStruct;
import com.cboe.idl.session.TradingSessionServerEventStateStruct;
import com.cboe.idl.session.TradingSessionStrategyDescriptionStruct;
import com.cboe.idl.session.TradingSessionStruct;
import com.cboe.idl.sysAdminIntermarketControlService.SysAdminIntermarketControlService;
import com.cboe.idl.textMessage.DestinationStruct;
import com.cboe.idl.textMessage.MessageResultStruct;
import com.cboe.idl.textMessage.MessageTransportStruct;
import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.trade.BustTradeStruct;
import com.cboe.idl.trade.MultipleTradeBustStruct;
import com.cboe.idl.trade.RelatedTradeReportSummaryStruct;
import com.cboe.idl.trade.TradeBustResponseStruct;
import com.cboe.idl.trade.TradeReportStruct;
import com.cboe.idl.trade.TradeReportStructV2;
import com.cboe.idl.trade.TradeReportStructV3;
import com.cboe.idl.trade.TradeReportSummaryStruct;
import com.cboe.idl.tradingProperty.AllocationStrategiesClassStruct;
import com.cboe.idl.tradingProperty.AllocationStrategyClassStruct;
import com.cboe.idl.tradingProperty.AllocationStrategyStruct;
import com.cboe.idl.tradingProperty.AllocationStrategyStructV2;
import com.cboe.idl.tradingProperty.AuctionBooleanClassStruct;
import com.cboe.idl.tradingProperty.AuctionBooleanStruct;
import com.cboe.idl.tradingProperty.AuctionLongClassStruct;
import com.cboe.idl.tradingProperty.AuctionLongStruct;
import com.cboe.idl.tradingProperty.AuctionOrderSizeTicksClassStruct;
import com.cboe.idl.tradingProperty.AuctionOrderSizeTicksStruct;
import com.cboe.idl.tradingProperty.AuctionRangeClassStruct;
import com.cboe.idl.tradingProperty.AuctionRangeStruct;
import com.cboe.idl.tradingProperty.BooleanStruct;
import com.cboe.idl.tradingProperty.DoubleStruct;
import com.cboe.idl.tradingProperty.DpmRightsScaleClassStruct;
import com.cboe.idl.tradingProperty.DpmRightsScaleStruct;
import com.cboe.idl.tradingProperty.InternalizationPercentageClassStruct;
import com.cboe.idl.tradingProperty.InternalizationPercentageStruct;
import com.cboe.idl.tradingProperty.LongClassStruct;
import com.cboe.idl.tradingProperty.LongStruct;
import com.cboe.idl.tradingProperty.SpreadClassStruct;
import com.cboe.idl.tradingProperty.TimeRangeClassStruct;
import com.cboe.idl.tradingProperty.TimeRangeStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.user.UserFirmAffiliationStruct;
import com.cboe.idl.user.UserSummaryStruct;
import com.cboe.idl.util.ErrorStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.util.ServerResponseStruct;
import com.cboe.infraUtil.DateTypeStruct;
import com.cboe.infrastructureServices.foundationFramework.POAHelper;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.domain.groupService.GroupElementCache;
import com.cboe.interfaces.domain.routingProperty.common.AffiliatedFirmAcronym;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.SatisfactionAlert;
import com.cboe.interfaces.internalPresentation.SystemAdminAPI;
import com.cboe.interfaces.internalPresentation.alert.AlertHistory;
import com.cboe.interfaces.internalPresentation.alert.AlertSearchCriteria;
import com.cboe.interfaces.internalPresentation.bookDepth.Tradable;
import com.cboe.interfaces.internalPresentation.firm.FirmModel;
import com.cboe.interfaces.internalPresentation.product.Exchange;
import com.cboe.interfaces.internalPresentation.product.SessionCodeDescription;
import com.cboe.interfaces.internalPresentation.trade.TradeReport;
import com.cboe.interfaces.internalPresentation.trade.TradeReportV3;
import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServer;
import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServerEvent;
import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionEvent;
import com.cboe.interfaces.internalPresentation.user.UserAccountModel;
import com.cboe.interfaces.internalPresentation.user.UserFirmAffiliation;
import com.cboe.interfaces.internalPresentation.userGroup.GroupElementModel;
import com.cboe.interfaces.presentation.api.OrderManagementTerminalAPI;
import com.cboe.interfaces.presentation.bookDepth.BookDepth;
import com.cboe.interfaces.presentation.common.formatters.GUILoggerPropertyFormatStrategy;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.ProductType;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.intermarketPresentation.intermarketMessages.SatisfactionAlertFactory;
import com.cboe.internalPresentation.alert.AlertHistoryFactory;
import com.cboe.internalPresentation.bookDepth.BookDepthImpl;
import com.cboe.internalPresentation.bookDepth.TradableImpl;
import com.cboe.internalPresentation.common.formatters.ProgramInterfaceTypes;
import com.cboe.internalPresentation.common.formatters.PropertyCategoryTypes;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;
import com.cboe.internalPresentation.common.logging.TradingPropertyCentralLogger;
import com.cboe.internalPresentation.firm.AffiliatedFirmCache;
import com.cboe.internalPresentation.firm.FirmCache;
import com.cboe.internalPresentation.product.ExchangeFactoryImpl;
import com.cboe.internalPresentation.product.SessionCodeDescriptionFactory;
import com.cboe.internalPresentation.trade.TradeReportFactory;
import com.cboe.internalPresentation.tradingSession.RegisteredServerEventFactory;
import com.cboe.internalPresentation.tradingSession.RegisteredServerFactory;
import com.cboe.internalPresentation.tradingSession.TradingSessionEventFactory;
import com.cboe.internalPresentation.user.UserCollectionFactory;
import com.cboe.internalPresentation.user.UserFirmAffiliationFactory;
import com.cboe.internalPresentation.userGroup.GroupElementCacheGuiImpl;
import com.cboe.internalPresentation.userGroup.GroupElementModelImpl;
import com.cboe.presentation.api.MarketMakerAPIImpl;
import com.cboe.presentation.api.ProductConfigurationQueryAPIFactory;
import com.cboe.presentation.api.SessionProductCacheFactory;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.ProductStates;
import com.cboe.presentation.common.formatters.ProductTypes;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.ProductFactoryHome;
import com.cboe.presentation.properties.CommonTranslatorProperties;
import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.user.UserStructModelImpl;
import com.cboe.presentation.userSession.UserSessionFactory;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

public class SystemAdminAPIImpl extends MarketMakerAPIImpl implements SystemAdminAPI
{

    private static final String                 SACAS_USER_NAME_DEFAULT = "Help Desk";
    private final String                        Category = this.getClass().getName();
    private static final String                 TRANSLATOR_NAME = "INTERNAL_TRANSLATOR";    //remove this and add to SysAdminAPI

    private GUILoggerPropertyFormatStrategy logPropertyFormatter = FormatFactory.getGUILoggerPropertyFormatStrategy();

    protected SystemAdminSessionManager         systemAdminSessionManager;
    protected ProductQueryService               productQueryService;
    protected ExchangeService                   exchangeService;
    protected ProductMaintenanceEventService    productMaintenance;
    protected ProductStateService               productStateService;
    protected TradingSessionService             tradingSessionService;
    protected TradingSessionEventStateService   tradingSessionEventStateService;
    protected ProductConfigurationService       productConfiguration;
    protected UserMaintenanceEventService       userMaintenanceEvent;
    protected TradingPropertyServiceExt         tradingProperty;
    protected PropertyEventService              propertyService;
    protected FirmService                       firmService;
    protected FirmMaintenanceService            firmMaintenance;

    protected MarketMakerQuoteService           marketMakerQuoteService;
    protected OrderHandlingService              orderHandlingService;
    protected OrderMaintenanceService           orderMaintenanceService;
    protected OrderBookService                  orderBookService;
    protected UserTradingParameterService       userTradingParameterService;
    protected TradeMaintenanceService           tradeMaintenanceService;

    // no "local/SACAS" home - strict pass through to server side service
    protected SessionManagementAdminService     sessionManagement;
    protected SecurityAdminService              securityAdmin           = null;
    protected TextMessagingService              textMessaging           = null;
    protected UserService                       userService             = null;
    protected CalendarAdminEventService         calendarAdminEventService;

    protected TradingSessionEventStateConsumer  tradingSessionEventStateConsumer;
    protected CacheUpdateConsumer               cacheUpdateConsumer;
    protected com.cboe.idl.consumers.ProductStatusConsumer adminProductStatusConsumer;
    protected SystemAdminProductProcessor systemAdminProductProcessor;
    protected PropertyConsumer                  propertyServiceConsumer;
    protected CalendarUpdateConsumer            calendarUpdateConsumer;

    protected AlertEventService                 alertEventService;
    protected AlertConsumer                     alertConsumer;

    protected SysAdminIntermarketControlService intermarketControlService;
    protected IntermarketAdminMessageConsumer   intermarketAdminMessageConsumer;

    protected ProductStatusConsumer             linkageStatusConsumer;

    /**
     * variable for group services like create group, add element to group, list all groups etc
     */
    protected GroupService groupService;


    protected FirmCache firmCache;
    protected AffiliatedFirmCache affiliatedFirmCache;
    /**
     * variable to hold the cache of user groups arranged in collection
     */
    protected GroupElementCache<GroupElementModel> groupElementCache;
    protected com.cboe.idl.exchange.ExchangeStruct[] exchangeCache;

    protected GroupElementConsumer groupElementConsumer;
    protected GroupElementEventService groupElementEvent;

    protected SystemControlConsumer systemControlConsumer;
    protected GroupCancelEventService groupCancelEvent;

    protected ExchangeProductMappingCache exchangeProductMappingCache;
    private boolean underlyingToClassCacheInitialized = false;
    private boolean exchangeToUnderlyingCacheInitialized = false;
    
    
    public SystemAdminAPIImpl(SystemAdminSessionManager sessionMgr, CMIUserSessionAdmin userListener, EventChannelListener clientListener)
    {
        super();
        systemAdminSessionManager = sessionMgr;
        userSessionAdminConsumer  = userListener;
        userClientListener        = clientListener;
        setSessionManager(systemAdminSessionManager);
    }

    public void initialize() throws Exception
    {
        try
        {
            initializeSessionManagerInterfaces();

            eventChannel = EventChannelAdapterFactory.find();
            eventChannel.setDynamicChannels(true);
            eventChannel.setListenerCleanup(CommonTranslatorProperties.isCleanChannelAdapterEnabled());

            initializeCallbackConsumers();

            initializeCaches();

            systemAdminProductProcessor = new SystemAdminProductProcessor();
            systemAdminProductProcessor.subscribeForProductEvents();

            ProductConfigurationQueryAPIFactory.create(systemAdminSessionManager.getProductConfigurationService());
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(TRANSLATOR_NAME + ": SystemAdminAPIImpl.initialize", e);
            throw e;
        }
   }
    public void startPropertyLoadingThread() throws com.cboe.exceptions.SystemException
   {
   }
   public void buildPropertyGroupCache() throws SystemException
    {
        
    }
   public PropertyGroupStruct[] getRoutingPropertyCache(String p_session) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return propertyService.getRoutingPropertyCache(p_session);
    }

    public PropertyGroupStruct[] getFirmPropertyCache(String p_session) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return propertyService.getFirmPropertyCache(p_session);
    }

    public PropertyGroupStruct[] getAffiliatedFirmPropertyCache(String p_session) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return propertyService.getAffiliatedFirmPropertyCache(p_session);
    }
    protected void initializeSessionManagerInterfaces() throws SystemException, CommunicationException, AuthorizationException
    {
        super.initializeSessionManagerInterfaces();

        exchangeService = systemAdminSessionManager.getExchangeService();
        productQueryService = systemAdminSessionManager.getProductQueryService();
        productMaintenance = systemAdminSessionManager.getProductMaintenanceEventService();
        productStateService = systemAdminSessionManager.getProductStateService();
        tradingSessionService = systemAdminSessionManager.getTradingSessionService();
        productConfiguration = systemAdminSessionManager.getProductConfigurationService();
        userMaintenanceEvent = systemAdminSessionManager.getUserMaintenanceEventService();
        tradingProperty = systemAdminSessionManager.getTradingPropertyService();
        propertyService = systemAdminSessionManager.getPropertyEventService();
        firmService = systemAdminSessionManager.getFirmService();
        firmMaintenance = systemAdminSessionManager.getFirmMaintenanceService();

        sessionManagement = systemAdminSessionManager.getSessionManagementAdminService();
        securityAdmin = systemAdminSessionManager.getSecurityAdminService();

        textMessaging = systemAdminSessionManager.getTextMessagingService();
        marketMakerQuoteService = systemAdminSessionManager.getMarketMakerQuoteService();
        orderHandlingService = systemAdminSessionManager.getOrderHandlingService();
        orderMaintenanceService = systemAdminSessionManager.getOrderMaintenanceService();

        try
        {
            OrderManagementTerminalAPI omtAPI = SAOrderManagementTerminalAPIFactory.find();
            omtAPI.initializeService(systemAdminSessionManager.getOrderManagementService());
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e,
                                                       "Exception when trying to get OrderManagementService from the SystemAdminSessionManager");
        }

        orderBookService = systemAdminSessionManager.getOrderBookService();
        tradeMaintenanceService = systemAdminSessionManager.getTradeMaintenanceService();

        tradingSessionEventStateService = systemAdminSessionManager.getTradingSessionEventStateService();

        userTradingParameterService = systemAdminSessionManager.getUserTradingParameterService();
        userService = systemAdminSessionManager.getUserService();
        alertEventService = systemAdminSessionManager.getAlertEventService();

        calendarAdminEventService = systemAdminSessionManager.getCalendarAdminEventService();

        groupService = systemAdminSessionManager.getGroupService();
        groupElementEvent = systemAdminSessionManager.getGroupElementEventService();
        groupCancelEvent =  systemAdminSessionManager.getGroupCancelEventService();
    }

    protected void initializeCallbackConsumers()
    {
        // create the cache of CMI callback Consumers
        //   -- it will lazily create and cache consumers by Session/ProductClass for: CurrentMarket, Recap, NBBO, and BookDepth
        cmiConsumerCacheFactory         = new AdminCMICallbackConsumerCacheFactoryImpl(eventChannel);

        productStatusConsumer           = ProductStatusConsumerFactory.create(eventChannel);
        adminProductStatusConsumer      = AdminProductStatusConsumerFactory.create(eventChannel);
        classStatusConsumer             = ClassStatusConsumerFactory.create(eventChannel);
        tradingSessionStatusConsumer    = TradingSessionStatusConsumerFactory.create(eventChannel);
        cacheUpdateConsumer             = CacheUpdateCallbackConsumerFactory.create(eventChannel);
        strategyStatusConsumer          = StrategyStatusConsumerFactory.create(eventChannel);
        rfqConsumer                     = RFQConsumerFactory.create(eventChannel);
        quoteStatusConsumer             = QuoteStatusConsumerFactory.create(eventChannel);

        expectedOpeningPriceConsumer    = ExpectedOpeningPriceConsumerFactory.create(eventChannel);
        orderStatusConsumer             = OrderStatusConsumerFactory.create(eventChannel);
        strategyStatusConsumer          = StrategyStatusConsumerFactory.create(eventChannel);
        tickerConsumer                  = TickerConsumerFactory.create(eventChannel);
        tradingSessionEventStateConsumer = TradingSessionEventStateCallbackConsumerFactory.create(eventChannel);
        propertyServiceConsumer         = PropertyConsumerFactory.create(eventChannel);
        alertConsumer                   = AlertConsumerFactory.create(eventChannel);
        calendarUpdateConsumer          = CalendarUpdateConsumerFactory.create(eventChannel);
        intermarketAdminMessageConsumer = IntermarketAdminMessageConsumerFactory.create(eventChannel);

        groupElementConsumer = GroupElementCallbackConsumerFactory.create(eventChannel);

        systemControlConsumer = SystemControlCallbackConsumerFactory.create(eventChannel);

        linkageStatusConsumer = LinkageStateConsumerFactory.create(eventChannel);
    }

    protected void initializeCaches()
            throws Exception
    {
        super.initializeCaches();

        firmCache = new FirmCache(firmMaintenance, this);
        affiliatedFirmCache = new AffiliatedFirmCache(this);
        exchangeCache = exchangeService.getAllExchanges();
    }

    /**
     * Initializes the exchange:product_set cache
     */
    public synchronized void initializeExchangeToUnderlyingProductsCache()
    {
        if(!exchangeToUnderlyingCacheInitialized)
        {
            boolean logInformation = false;
            try
            {
                exchangeProductMappingCache = ExchangeProductMappingCache.getInstance();
                if(GUILoggerHome.find().isInformationOn() &&
                   GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_QUERY))
                {
                    logInformation = true;
                    GUILoggerHome.find().information(TRANSLATOR_NAME +
                                                     ":initializeExchangeToUnderlyingProductsCache() - starting to load Exchange->UnderlyingProducts cache",
                                                     GUILoggerSABusinessProperty.PRODUCT_QUERY);
                }
                exchangeProductMappingCache.initializeCache();
                exchangeProductMappingCache.subscribeForEvents();
                exchangeToUnderlyingCacheInitialized = true;
            }
            catch(UserException e)
            {
                GUILoggerHome.find().exception(
                        TRANSLATOR_NAME + ": .initializeExchangeProductMappingCache()", "", e);
            }
            if(logInformation)
            {
                GUILoggerHome.find().information(TRANSLATOR_NAME +
                                                 ":initializeExchangeToUnderlyingProductsCache() - finished to load Exchange->UnderlyingProducts cache",
                                                 GUILoggerSABusinessProperty.PRODUCT_QUERY);
            }
        }
    }

    /**
     * Cleanup of exchange to product cache.
     */
    protected void cleanupExchangeToUnderlyingProductsCache()
    {
        exchangeProductMappingCache.unsubscribeForEvents();
        exchangeProductMappingCache.clear();
        exchangeProductMappingCache = null;
    }

    public synchronized void initializeUnderlyingProductToSessionClassesCache()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(!underlyingToClassCacheInitialized)
        {
            boolean logInformation = false;
            if(GUILoggerHome.find().isInformationOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_QUERY))
            {
                logInformation = true;
                GUILoggerHome.find().information(TRANSLATOR_NAME +
                                                 ":initializeUnderlyingProductToSessionClassesCache() - starting to load UnderlyingProduct->SessionProductClasses cache",
                                                 GUILoggerSABusinessProperty.PRODUCT_QUERY);
            }
            //load underlyingProduct->SessionProductClasses cache for each session
            com.cboe.idl.cmiSession.TradingSessionStruct[] tradingSessions = getCurrentTradingSessions(null);
            for(com.cboe.idl.cmiSession.TradingSessionStruct tradingSession : tradingSessions)
            {
                try
                {
                    ProductType[] productTypes = getProductTypesForSession(tradingSession.sessionName);
                    for(int i = 0; i < productTypes.length; i++)
                    {
                        loadUnderlyingProductToSessionClassesCache(tradingSession.sessionName, productTypes[i].getType());
                    }
                    underlyingToClassCacheInitialized = true;
                }
                catch(Exception e)
                {
                    GUILoggerHome.find().exception(TRANSLATOR_NAME + ": .initializeUnderlyingProductToSessionClassCache()", "", e);
                }
            }
            if(logInformation)
            {
                GUILoggerHome.find().information(TRANSLATOR_NAME +
                                                 ":initializeUnderlyingProductToSessionClassesCache() - finished loading UnderlyingProduct->SessionProductClasses cache",
                                                 GUILoggerSABusinessProperty.PRODUCT_QUERY);
            }
        }
    }

    /**
     * Loads the product:sessionClassSet cache.
     */
    private void loadUnderlyingProductToSessionClassesCache(String sessionName, short productType) throws
            DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        SessionProductClass[] productClasses = getAllProductClassesForSession(sessionName);
        if(productClasses != null && productClasses.length > 0)
        {
            if(GUILoggerHome.find().isInformationOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find()
                        .information(TRANSLATOR_NAME +
                                     ":loadUnderlyingProductToSessionClassesCache() - processing " +
                                     productClasses.length +
                                     " SessionProductClasses for session='" + sessionName +
                                     "' productType='" + ProductTypes.toString(productType) +
                                     "'", GUILoggerSABusinessProperty.PRODUCT_QUERY);
            }
            productProcessor.populateProductKeyToSessionClassesCache(sessionName, productClasses);
        }
        else
        {
            if(GUILoggerHome.find().isInformationOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find()
                        .information(TRANSLATOR_NAME +
                                     ":loadUnderlyingProductToSessionClassesCache() - no SessionProductClasses to process for session='" +
                                     sessionName + "' productType='" +
                                     ProductTypes.toString(productType) + "'",
                                     GUILoggerSABusinessProperty.PRODUCT_QUERY);
            }
        }
    }

    public RegisteredServer[] getTradingSessionRegistrationNames()
            throws DataValidationException, AuthorizationException, TransactionFailedException, CommunicationException,
            SystemException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessionRegistrationNames",
                                       GUILoggerSABusinessProperty.SESSION_MANAGEMENT);
        }

        TradingSessionRegistrationStruct[] structs = tradingSessionService.getTradingSessionRegistrationNames();

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessionRegistrationNames",
                                       GUILoggerSABusinessProperty.SESSION_MANAGEMENT, structs);
        }

        ArrayList<RegisteredServer> list = new ArrayList<RegisteredServer>(structs.length * 5);
        for (int i = 0; i < structs.length; i++)
        {
            TradingSessionRegistrationStruct tradingSessionRegistrationStruct = structs[i];
            RegisteredServer[] servers = RegisteredServerFactory.createRegisteredServers(tradingSessionRegistrationStruct);
            for (int j = 0; j < servers.length; j++)
            {
                RegisteredServer server = servers[j];
                list.add(server);
            }
        }
        RegisteredServer[] servers = new RegisteredServer[list.size()];
        if (list.size() > 0)
        {
            servers = list.toArray(servers);
        }

        return servers;
    }

    public RegisteredServerEvent[] getRegisteredServersEventState(String sessionName)
            throws DataValidationException, AuthorizationException, TransactionFailedException, CommunicationException,
            SystemException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getRegisteredServersEventState",
                                       GUILoggerSABusinessProperty.SESSION_MANAGEMENT, sessionName);
        }

        TradingSessionServerEventStateStruct[] eventStructs =
                tradingSessionService.getRegisteredServersEventState(sessionName);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getRegisteredServersEventState",
                                       GUILoggerSABusinessProperty.SESSION_MANAGEMENT, eventStructs);
        }

        RegisteredServerEvent[] events = new RegisteredServerEvent[eventStructs.length];
        for (int i = 0; i < eventStructs.length; i++)
        {
            TradingSessionServerEventStateStruct eventStruct = eventStructs[i];
            events[i] = RegisteredServerEventFactory.createRegisteredServer(eventStruct);
        }

        return events;
    }

    public TradingSessionEvent[] getEventHistoryV2(String sessionName)
            throws DataValidationException, AuthorizationException, TransactionFailedException, CommunicationException,
            SystemException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getEventHistoryV2",
                                       GUILoggerSABusinessProperty.SESSION_MANAGEMENT, sessionName);
        }

        TradingSessionEventHistoryStructV2[] eventHistoryStructs = tradingSessionService.getEventHistoryV2(sessionName);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getEventHistoryV2",
                                       GUILoggerSABusinessProperty.SESSION_MANAGEMENT, eventHistoryStructs);
        }

        TradingSessionEvent[] events = new TradingSessionEvent[eventHistoryStructs.length];
        for (int i = 0; i < eventHistoryStructs.length; i++)
        {
            TradingSessionEventHistoryStructV2 eventHistoryStructV2 = eventHistoryStructs[i];
            events[i] = TradingSessionEventFactory.createTradingSessionEvent(eventHistoryStructV2);
        }

        return events;
    }

    /**
     * @deprecated use the V2 version of this method
     * <code>TradingSessionEventHistoryStructV2[] getEventHistoryV2(String sessionName)</code>
     */
    public TradingSessionEventHistoryStruct[] getEventHistory(String sessionName)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getEventHistory",
                                       GUILoggerSABusinessProperty.SESSION_MANAGEMENT, sessionName);
        }

        TradingSessionEventHistoryStruct[] eventHistoryStructs = tradingSessionService.getEventHistory(sessionName);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getEventHistory",
                                       GUILoggerSABusinessProperty.SESSION_MANAGEMENT, eventHistoryStructs);
        }
        return eventHistoryStructs;
    }

    /**
     *  Return trading session elements for a given business day.
     *
     *  @param  sessionName     String
     *  @param  businessDay     DateStruct
     *  @return TradingSessionElementStruct[]
     */

    public TradingSessionElementStructV2[] getElementsForSessionV2(String sessionName, DateStruct businessDay)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = businessDay;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getElementsForSession", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        TradingSessionElementStructV2[] sessionElements = tradingSessionService.getElementsForSessionV2(sessionName, businessDay);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getElementsForSession", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionElements;
    }

    /**
     *  Return trading session element for a given key.
     *
     *  @param  elementKey      int
     *  @return TradingSessionElementStruct
     */
    public TradingSessionElementStructV2 getElementByKeyV2(int elementKey)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getElementByKey", GUILoggerSABusinessProperty.TRADING_SESSION, new Integer(elementKey));
        }

        TradingSessionElementStructV2 sessionElement = tradingSessionService.getElementByKeyV2(elementKey);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getElementByKey", GUILoggerSABusinessProperty.TRADING_SESSION, new Integer(elementKey));
        }
        return sessionElement;
    }

    /**
     *  Return current trading session elements.
     *
     *  @param  none
     *  @return TradingSessionElementStruct[]
     */
    public TradingSessionElementStruct[] getCurrentTradingSessionElements()
      throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getCurrentTradingSessionElements", GUILoggerSABusinessProperty.TRADING_SESSION);
        }

        TradingSessionElementStruct[] sessionElements = tradingSessionService.getCurrentTradingSessionElements();

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getCurrentTradingSessionElements", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionElements;
    }

    /**
     *  Return current business day state and session info.
     *
     *  @param  sessionName     String
     *  @return BusinessDayStruct
     */


    public BusinessDayStruct getCurrentBusinessDay()
      throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getCurrentBusinessDay", GUILoggerSABusinessProperty.TRADING_SESSION);
        }
        BusinessDayStruct businessDay = tradingSessionService.getCurrentBusinessDay();
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getCurrentBusinessDay", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return businessDay;
    }


    /**
     *  Return current trading session elements for a given session.
     *
     *  @param  sessionName     String
     *  @return TradingSessionElementStruct[]
     */


    public TradingSessionElementStruct[] getCurrentElementsForSession(String sessionName)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getCurrentElementsForSession", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }

        TradingSessionElementStruct[] sessionElements = tradingSessionService.getCurrentElementsForSession(sessionName);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getCurrentElementsForSession", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionElements;
    }


    /**
     *  Return trading session elements for a given template.
     *
     *  @param  templateName    String
     *  @return TradingSessionElementStruct[]
     */

    public TradingSessionElementStructV2[] getElementsForTemplateV2(String templateName)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getElementsForTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, templateName);
        }

        TradingSessionElementStructV2[] sessionElements = tradingSessionService.getElementsForTemplateV2(templateName);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getElementsForTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionElements;
    }


    /**
     * removeTradingSessionTemplate
     *
     *  @param    templateName - String
     */


   public void removeTradingSessionTemplate(String templateName)
      throws DataValidationException, CommunicationException, SystemException, TransactionFailedException, AuthorizationException
   {
       if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
       {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeTradingSessionTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, templateName);
       }

       tradingSessionEventStateService.removeTradingSessionTemplate(templateName);

       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION), "removeTradingSessionTemplate", templateName);
       }
   }

    /**
     * removeTradingSessionElement
     *
     *  @param    elementKey - int
     */


   public void removeTradingSessionElement(int elementKey)
      throws DataValidationException, CommunicationException, SystemException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeTradingSessionElement", GUILoggerSABusinessProperty.TRADING_SESSION, new Integer(elementKey));
        }
        tradingSessionEventStateService.removeTradingSessionElement(elementKey);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION), "removeTradingSessionElement", "Element Key = " + elementKey);
        }
   }

    /**
     * removeTradingSession
     *
     *  @param    sessionName   String
     */

   public void removeTradingSession(String sessionName)
      throws DataValidationException, CommunicationException, SystemException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeTradingSession", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }
        tradingSessionEventStateService.removeTradingSession(sessionName);
        if (GUILoggerHome.find().isAuditOn())
        {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                      "removeTradingSession", sessionName);
        }
   }

    /**
     * getTemplate
     *
     * @param templateName - String
     * @return TradingSessionElementTemplateStruct
     */
    public TradingSessionElementTemplateStruct getTemplate(String templateName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, templateName);
        }

        TradingSessionElementTemplateStruct sessionElementTemplate = tradingSessionService.getTemplate(templateName);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionElementTemplate;
    }

    public TradingSessionElementTemplateStructV2 getTemplateV2(String templateName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, templateName);
        }

        TradingSessionElementTemplateStructV2 sessionElementTemplate = tradingSessionService.getTemplateV2(templateName);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionElementTemplate;
    }

    /**
     * getTemplatesForSession
     *
     *  @param    sessionName - String
     *  @return  TradingSessionElementTemplateStruct[]
     */
   public TradingSessionElementTemplateStruct[] getTemplatesForSession(String sessionName)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
       if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
       {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTemplatesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
       }

       TradingSessionElementTemplateStruct[] templates = tradingSessionService.getTemplatesForSession(sessionName);

       if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
       {
           GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTemplatesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
       }
        return templates;
   }

    public TradingSessionElementTemplateStructV2[] getTemplatesForSessionV2(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTemplatesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }
        TradingSessionElementTemplateStructV2[] templates = tradingSessionService.getTemplatesForSessionV2(sessionName);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTemplatesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return templates;
    }

   /**
    * getCurrentTradingSessionsForClasses
     *
    *  @param     sessions - int[]
    *  @return   TradingSessionStruct[]
    */
    public TradingSessionStruct[] getCurrentTradingSessionsForClasses(int[]   sessions)
      throws DataValidationException, CommunicationException, SystemException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            Object[] argObj = new Object[sessions.length];
            for (int i = 0; i < sessions.length; i++)
            {
                argObj[i] = new Integer(sessions[i]);
            }
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getCurrentTradingSessionsForClasses", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

       TradingSessionStruct[] currentSessions = tradingSessionService.getCurrentTradingSessionsForClasses(sessions);

       if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
       {
           GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getCurrentTradingSessionsForClasses", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
       }
        return currentSessions;
   }

    /**
     * Retrieves the classes for the named template.
     *
     *  @param    templateName  String
     *  @return  TemplateClassStruct[]
     */

   public TemplateClassStruct[] getClassesForTemplate(String templateName)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesForTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, templateName);
        }

       TemplateClassStruct[] templateClasses = tradingSessionService.getClassesForTemplate(templateName);

       if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
       {
           GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesForTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
       }
        return templateClasses;
   }

    /**
     * Retrieves the classes for the Session.
     *
     *  @param  sessionName      String
     *  @return SessionClassDetailStruct[]
     */

   public SessionClassDetailStruct[] getClassesForSession(String sessionName)
     throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }

        SessionClassDetailStruct[] sessionClassDetails = tradingSessionService.getClassesForSession(sessionName);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionClassDetails;
    }

    /* Operation Definition */
    public SessionClassDetailStruct[] getClassesForSessionByGroup(String sessionName, String groupName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesForSessionByGroup", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }

        SessionClassDetailStruct[] sessionClassDetails = tradingSessionService.getClassesForSessionByGroup(sessionName, groupName);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesForSessionByGroup", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionClassDetails;
    }

    /**
     * Retrieves the classes for the given element.
     *
     *  @param  elementKey      int
     *  @return SessionClassDetailStruct[]
     */
   public SessionClassDetailStruct[] getClassesForElement(int elementKey)
     throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesForElement", GUILoggerSABusinessProperty.TRADING_SESSION, new Integer(elementKey));
        }

        SessionClassDetailStruct[] sessionClasses = tradingSessionService.getClassesForElement(elementKey);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesForElement", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionClasses;
    }

    /**
     * Retrieves the classe states for a named session.
     *
     *  @param  sessionName     String
     *  @return ClassStateDetailStruct[]
     */
   public ClassStateDetailStruct[] getClassStatesForSession(String sessionName)
     throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassStatesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }

        ClassStateDetailStruct[] classStates = tradingSessionService.getClassStatesForSession(sessionName);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassStatesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return classStates;
    }

    /**
     * Retrieves the classe states for an element.
     *
     *  @param  elementKey      int
     *  @return ClassStateDetailStruct[]
     */
   public ClassStateDetailStruct[] getClassStatesForElement(int elementKey)
     throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassStatesForElement", GUILoggerSABusinessProperty.TRADING_SESSION, new Integer(elementKey));
        }

        ClassStateDetailStruct[] classStates = tradingSessionService.getClassStatesForElement(elementKey);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassStatesForElement", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return classStates;
    }

    /**
     * getAllTemplates
     *
     *  @return TradingSessionElementTemplateStruct[]
     */
   public TradingSessionElementTemplateStruct[] getAllTemplates()
      throws SystemException, CommunicationException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllTemplates", GUILoggerSABusinessProperty.TRADING_SESSION);
        }

       TradingSessionElementTemplateStruct[] templates = tradingSessionService.getAllTemplates();

       if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
       {
           GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllTemplates", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
       }
        return templates;
   }

    @Override
    public TradingSessionElementTemplateStructV2[] getAllTemplatesV2() throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllTemplates", GUILoggerSABusinessProperty.TRADING_SESSION);
        }
        TradingSessionElementTemplateStructV2[] templates = tradingSessionService.getAllTemplatesV2();
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllTemplates", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return templates;
    }

   /**
     * addTradingSessionTemplate
     *
     *  @param sessionTemplate - TradingSessionElementTemplateStruct
     */

   public void addTradingSessionTemplateV2(TradingSessionElementTemplateStructV2 sessionTemplate)
      throws AlreadyExistsException, DataValidationException, CommunicationException, SystemException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addTradingSessionTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, sessionTemplate);
        }
        tradingSessionEventStateService.addTradingSessionTemplateV2(sessionTemplate);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "addTradingSessionTemplate", sessionTemplate);
        }
   }

    /**
     * addTradingSession
     *
     *  @param session - TradingSessionStruct
     */
   public void addTradingSession(TradingSessionStruct session)
      throws AlreadyExistsException, DataValidationException, CommunicationException, SystemException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addTradingSession", GUILoggerSABusinessProperty.TRADING_SESSION, session);
        }
        tradingSessionEventStateService.addTradingSession(session);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "addTradingSession", session);
        }
   }

    /**
     * modifyTradingSession
     *
     *  @param newSession - TradingSessionStruct
     */
   public void modifyTradingSession(TradingSessionStruct newSession)
      throws DataValidationException, CommunicationException, SystemException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":modifyTradingSession", GUILoggerSABusinessProperty.TRADING_SESSION, newSession);
        }
        tradingSessionEventStateService.modifyTradingSession(newSession);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "modifyTradingSession", newSession);
        }
   }

    /**
     * modifyTradingSessionTemplate
     *
     *  @param newSessionTemplate - TradingSessionElementTemplateStruct
     */
   public void modifyTradingSessionTemplateV2(TradingSessionElementTemplateStructV2 newSessionTemplate)
      throws DataValidationException, CommunicationException, SystemException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":modifyTradingSessionTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, newSessionTemplate);
        }
        tradingSessionEventStateService.modifyTradingSessionTemplateV2(newSessionTemplate);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "modifyTradingSessionTemplate", newSessionTemplate);
        }
   }

    /**
     * modifyTradingSessionElement
     *
     *  @param newSessionElement - TradingSessionElementStruct
     */
   public void modifyTradingSessionElementV2(TradingSessionElementStructV2 newSessionElement)
      throws DataValidationException, CommunicationException, SystemException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":modifyTradingSessionElement", GUILoggerSABusinessProperty.TRADING_SESSION, newSessionElement);
        }
        tradingSessionEventStateService.modifyTradingSessionElementV2(newSessionElement);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "modifyTradingSessionElement", newSessionElement);
        }
   }

    /**
     * Fires Trading Session Service Events for select servers
     *
     * @param eventType to fire
     * @param sessionName to fire for
     * @param contextString to fire for
     * @param waitForCompletion True to wait
     * @param vetoable True to allow veto
     * @param serverNames to fire events for
     * @return boolean status
     */
    public boolean fireEventForServers(int eventType, String sessionName, String contextString,
                                       boolean waitForCompletion, boolean vetoable, String[] serverNames)
            throws DataValidationException, AuthorizationException, TransactionFailedException,
            CommunicationException, SystemException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[6];
            argObj[0] = new Integer(eventType);
            argObj[1] = sessionName;
            argObj[2] = contextString;
            argObj[3] = new Boolean(waitForCompletion);
            argObj[4] = new Boolean(vetoable);
            argObj[5] = serverNames;
        }

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": fireEventForServers",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }
        boolean status = tradingSessionEventStateService.fireEventForServers(eventType, sessionName, contextString,
                                                                             waitForCompletion, vetoable, serverNames);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "fireEventForServers ", argObj);
        }

        return status;
    }

   /**
    * Fires Trading Session Service Events
     *
    * @param eventType to fire
    * @param sessionName to fire for
    * @param contextString to fire for
    * @param waitForCompletion True to wait
    * @param vetoable True to allow veto
    * @return boolean status
    */
   public boolean fireEvent(int eventType, String sessionName, String contextString, boolean waitForCompletion,
                            boolean vetoable)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
           TransactionFailedException
   {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[5];
            argObj[0] = new Integer(eventType);
            argObj[1] = sessionName;
            argObj[2] = contextString;
            argObj[3] = new Boolean(waitForCompletion);
            argObj[4] = new Boolean(vetoable);
        }

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": fireEvent",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }
        boolean status = tradingSessionEventStateService.fireEvent(eventType, sessionName, contextString,
                                                                   waitForCompletion, vetoable);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "fireEvent ", argObj);
        }

        return status;
   }


    /**
     * acceptStrategy
     *
     *  @param   sessionName     String
     *  @param   strategy        StrategyRequestStruct
     *  @return StrategyStruct
     */
   public SessionStrategyStruct acceptStrategy(String sessionName, StrategyRequestStruct strategy)
      throws CommunicationException, SystemException, AuthorizationException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = strategy;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptStrategy", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

       SessionStrategyStruct struct = tradingSessionService.acceptStrategy(sessionName, strategy);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                      "acceptStrategy for " + sessionName, strategy);
       }
       return struct;
   }


    /**
     * getStrategiesForSession
     *
     *  @return SessionStrategyStruct[]
     */
   public SessionStrategyStruct[] getStrategiesForSession(String sessionName)
      throws CommunicationException, SystemException, AuthorizationException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getStrategiesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }

       SessionStrategyStruct[] strategies = tradingSessionService.getStrategiesForSession(sessionName);

       if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
       {
           GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getStrategiesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
       }

      return strategies;
   }

    /**
     * getEndOfSessionStradegies
     *
     *  @return TradingSessionStrategyDescriptionStruct[]
     */
   public TradingSessionStrategyDescriptionStruct[] getEndOfSessionStrategies()
      throws CommunicationException, SystemException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getEndOfSessionStrategies", GUILoggerSABusinessProperty.TRADING_SESSION);
        }

       TradingSessionStrategyDescriptionStruct[] strategyDescriptions = tradingSessionService.getEndOfSessionStrategies();

       if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
       {
           GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getEndOfSessionStrategies", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
       }

      return strategyDescriptions;
   }

    /**
     * getEndOfDayStrategies
     *
     *  @return TradingSessionStrategyDescriptionStruct[]
     */
   public TradingSessionStrategyDescriptionStruct[] getEndOfDayStrategies()
      throws SystemException, CommunicationException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getEndOfDayStrategies", GUILoggerSABusinessProperty.TRADING_SESSION);
        }

       TradingSessionStrategyDescriptionStruct[] strategyDescriptions = tradingSessionService.getEndOfDayStrategies();

       if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
       {
           GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getEndOfDayStrategies", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
       }

       return strategyDescriptions;
   }

   /**
    * getProductBySessionForKey
     *
    * @param  sessionName   String
    * @param  productKey    int
    * @return SessionProductStruct
    */
    public SessionProductStruct getProductBySessionForKey(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductBySessionForKey", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

       return tradingSessionService.getProductBySessionForKey(sessionName, productKey);
   }

   /**
    * getStrategyBySessionForKey
     *
    * @param  sessionName String
    * @param  productKey  int
    * @return SessionStrategyStruct
    */
    public SessionStrategyStruct getStrategyBySessionForKey(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getStrategyBySessionForKey", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

       return tradingSessionService.getStrategyBySessionForKey(sessionName, productKey);
   }

   /**
    * getClassBySessionForKey
     *
    * @param  sessionName  String
    * @param  classKey     int
    * @return SessionClassStruct
    */
    public SessionClassStruct getClassBySessionForKey(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassBySessionForKey", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

       return tradingSessionService.getClassBySessionForKey(sessionName, classKey);
   }

   /**
    * getProductBySessionForName
     *
    * @param  sessionName   String
    * @param  productName   roductNameStruct
    * @return  SessionProductStruct
    */
    public SessionProductStruct getProductBySessionForName(String sessionName, ProductNameStruct productName)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = productName;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductBySessionForName", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

       return tradingSessionService.getProductBySessionForName(sessionName, productName);
   }

   /**
    * getClassBySessionForSymbol
     *
    * @param  sessionName   String
    * @param  productType   short
    * @param  className     String
    * @return  SessionClassStruct
    */
    public SessionClassStruct getClassBySessionForSymbol(String sessionName, short productType, String className)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Short(productType);
            argObj[2] = className;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassBySessionForSymbol", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

       return tradingSessionService.getClassBySessionForSymbol(sessionName, productType, className);
   }

    public void startSessionForServers(String sessionName, String[] serverNames)
            throws DataValidationException, AuthorizationException, TransactionFailedException, CommunicationException,
            SystemException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = serverNames;
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": startSessionForServers",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        tradingSessionEventStateService.startSessionForServers(sessionName, serverNames);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "startSessionForServers", argObj);
        }
    }

    public void startSession(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": startSession",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }
        tradingSessionEventStateService.startSession(sessionName);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "startSession", sessionName);
        }
    }

    public void startPriceAdjustmentsForServers(String[] serverNames)
            throws DataValidationException, AuthorizationException, TransactionFailedException,
            CommunicationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": startPriceAdjustmentsForServers",
                                       GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, serverNames);
        }

        tradingSessionEventStateService.startPriceAdjustmentsForServers(serverNames);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                       "startPriceAdjustmentsForServers");
        }
    }

   public void startPriceAdjustments()
     throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":startPriceAdjustments", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, "");
        }

        tradingSessionEventStateService.startPriceAdjustments();

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                       "startPriceAdjustments");
        }
    }

    public void priceAdjustmentsCompleteForServers(String[] serverNames)
            throws DataValidationException, AuthorizationException, TransactionFailedException,
            CommunicationException, SystemException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[1];
            argObj[0] = serverNames;
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": priceAdjustmentsCompleteForServers",
                                       GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }

        tradingSessionEventStateService.priceAdjustmentsCompleteForServers(serverNames);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                       "priceAdjustmentsCompleteForServers", argObj);
        }
    }

    /**
     * This method is invoked when price adjustments have been completed.
     */
    public void priceAdjustmentsComplete()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": priceAdjustmentsComplete",
                                       GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE);
        }

        tradingSessionEventStateService.priceAdjustmentsComplete();

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                       "priceAdjustmentsComplete");
        }
    }

    /**
     * Start the "End of session" processing for specific servers.
     *
     * @param sessionName the target session's identifier
     * @param forceStart True to force it
     * @param abortOnError True to stop on first error, false to continue on others servers
     * @param serverNames to start "EOS" for only
     */
    public void startEndOfSessionForServers(String sessionName, boolean forceStart, boolean abortOnError,
                                            String[] serverNames)
            throws DataValidationException, AuthorizationException, TransactionFailedException,
            CommunicationException, SystemException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Boolean(forceStart);
            argObj[2] = new Boolean(abortOnError);
            argObj[3] = serverNames;
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": startEndOfSessionForServers",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        tradingSessionEventStateService.startEndOfSessionForServers(sessionName, forceStart, abortOnError, serverNames);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "startEndOfSessionForServers", argObj);
        }
    }

    /**
     * Start the "End of session" processing.
     *
     * @param sessionName the target session's identifier
     * @param forceStart True to force it
     * @param abortOnError True to stop on first error, false to continue on others servers
     */
    public void startEndOfSession(String sessionName,  boolean forceStart, boolean abortOnError)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Boolean(forceStart);
            argObj[2] = new Boolean(abortOnError);
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": startEndOfSession",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        tradingSessionEventStateService.startEndOfSession(sessionName, forceStart, abortOnError);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "startEndOfSession", argObj);
        }
    }

    /**
     * Start the "End of day" processing for specific servers.
     *
     * @param forceStart True to force it
     * @param abortOnError True to stop on first error, false to continue on others servers
     * @param serverNames to start "EOD" for only
     */
    public void startEndOfDayForServers(boolean forceStart, boolean abortOnError, String[] serverNames)
            throws DataValidationException, AuthorizationException, TransactionFailedException, CommunicationException, SystemException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = new Boolean(forceStart);
            argObj[1] = new Boolean(abortOnError);
            argObj[2] = serverNames;
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": startEndOfDayForServers",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        tradingSessionEventStateService.startEndOfDayForServers(forceStart, abortOnError, serverNames);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "startEndOfDayForServers", argObj);
        }
    }

    /**
     * Start the "End of day " processing.
     *
     * @param forceStart True to force it
     * @param abortOnError True to stop on first error, false to continue on others servers
     */
    public void startEndOfDay(boolean forceStart, boolean abortOnError)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = new Boolean(forceStart);
            argObj[1] = new Boolean(abortOnError);
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": startEndOfDay",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        tradingSessionEventStateService.startEndOfDay(forceStart, abortOnError);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "startEndOfDay", argObj);
        }
    }

    /**
     * Set the state of all products in the given session to the given state.
     *
     *  @param sessionName the target session's identifier
     *  @param newProductState a numeric value indicating the new product state.  A value from ProductStates.
     */

   public void setProductStatesForSession(String sessionName, short newProductState)
      throws DataValidationException, CommunicationException, SystemException, TransactionFailedException, AuthorizationException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("setProductStatesForSession");
           buffer.append(" for ").append(sessionName).append(" New State: ").append(formatProductState(newProductState));
       }
       if (GUILoggerHome.find().isDebugOn())
       {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStatesForSession", GUILoggerSABusinessProperty.TRADING_SESSION, buffer.toString());
       }

       tradingSessionEventStateService.setProductStatesForSession(sessionName, newProductState);

       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                      buffer.toString(), "");
       }
   }

    /**
     * Set the state of all products in the given elementKey to the given state.
     *
     *  @param elementKey for product
     *  @param newProductState a numeric value indicating the new product state.  A value from ProductStates.
     */

   public void setProductStatesForElement(int elementKey, short newProductState)
      throws DataValidationException, CommunicationException, SystemException, TransactionFailedException, AuthorizationException
   {
       Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = new Integer(elementKey);
            argObj[1] = new Short(newProductState);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStatesForElement", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

       tradingSessionEventStateService.setProductStatesForElement(elementKey, newProductState);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                      "setProductStatesForElement", argObj);
       }
   }

    /**
     * Propogate the fact that a service has been lost.
     *
     * @param sessionName the target session's identifier
     * @param serviceName java.lang.String the name identifying the service which was lost.
     */

   public void serviceLost(String sessionName, String serviceName)
     throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = serviceName;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":serviceLost", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        tradingSessionEventStateService.serviceLost(sessionName, serviceName);
    }

    /**
     * Informs that the service identified by "serviceName" has been established
     * (perhaps re-established).
     *
     *  @param sessionName the target session's identifier
     *  @param serviceName java.lang.String the name of the session that has been established (or reestablished)
     */

   public void serviceEstablished(String sessionName, String serviceName)
     throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = serviceName;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":serviceEstablished", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        tradingSessionEventStateService.serviceEstablished(sessionName, serviceName);
    }

    /**
     * getUnassignedClasses
     *  gets all classes currently unassigned to a trading session
     *
     *  @param sessionName the target session's identifier
     */

    public ClassStruct[] getUnassignedClasses(String sessionName)
        throws CommunicationException, SystemException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUnassignedClasses", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }

        return tradingSessionEventStateService.getUnassignedClasses(sessionName);
    }

    /**
     * createElementsForSession
     *  gets all classes currently unassigned to a trading session
     *
     * @return TradingSessionElementStruct[]
     *  @parm   sessionName     String
     *  @parm   businessDay     DateStruct
     */

    public TradingSessionElementStruct[] createElementsForSession(String sessionName, DateStruct businessDay)
        throws CommunicationException, SystemException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = businessDay;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":createElementsForSession", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        TradingSessionElementStruct[] structs = tradingSessionEventStateService.createElementsForSession(sessionName, businessDay);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "createElementsForSession", argObj);
        }
        return structs;
    }

    /**
     * buildTemplateClassMapping
     *
     * @return String
     *  @parm   none
     */

    public String buildTemplateClassMapping()
        throws CommunicationException, SystemException, AuthorizationException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":buildTemplateClassMapping", GUILoggerSABusinessProperty.TRADING_SESSION, "");
        }

        String retString = tradingSessionEventStateService.buildTemplateClassMapping();
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "buildTemplateClassMapping", "");
        }
        return retString;
    }

    /**
     * createCurrentBusinessDay
     *
     * @return String
     *  @parm   none
     */

    public String createCurrentBusinessDay()
        throws CommunicationException, SystemException, AuthorizationException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":createCurrentBusinessDay", GUILoggerSABusinessProperty.TRADING_SESSION, "");
        }

        String retString = tradingSessionEventStateService.createCurrentBusinessDay();

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "createCurrentBusinessDay", "");
        }
        return retString;
    }

    /**
     * addClassToTemplate
     *
     * @param templateName  String
     * @param templateClass TemplateClassStruct
     *  @return void
     */

    public void addClassToTemplate(String templateName, TemplateClassStruct templateClass)
        throws CommunicationException, SystemException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = templateName;
            argObj[1] = templateClass;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addClassToTemplate", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        tradingSessionEventStateService.addClassToTemplate(templateName, templateClass);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       "addClassToTemplate", argObj);
        }
    }

    /**
     * Abort  the Session.
     *
     *  @param sessionName  String
     */

    public void abortEndOfSession(String sessionName)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            buffer = new StringBuffer("abortEndOfSession");
            buffer.append(" for Session ").append(sessionName).append('.');
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":abortEndOfSession", GUILoggerSABusinessProperty.TRADING_SESSION, buffer.toString());
        }

        tradingSessionEventStateService.abortEndOfSession(sessionName);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION),
                                       buffer.toString(), "");
        }
    }

// ProductStateAPI
//

   /**
    * Sets all products to the given state.
    *
    * @param productState the state to set all products to.
     * @throws NotFoundException
     * @throws com.cboe.exceptions.InvalidStateChangeException
     *
    */

   public ClassSettlementStruct getSettlementPricesForClass(String sessionName, ClassSettlementStruct previousSettlementPrices)
      throws SystemException, CommunicationException, DataValidationException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = previousSettlementPrices;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSettlementPricesForClass", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }

      return productStateService.getSettlementPricesForClass(sessionName, previousSettlementPrices);
   }

   public void setAllProductStates(String sessionName, short productState)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("setAllProductStates");
           buffer.append(" for Session ").append(sessionName).append('.');
           buffer.append(" New State: ").append(formatProductState(productState));
       }
       if (GUILoggerHome.find().isDebugOn())
       {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAllProductStates", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
       }

       productStateService.setAllProductStates(sessionName, productState);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }

   }

    /**
     * Sets the given class of products to the given state.
     *
     * @param classKey the class of products to set.
     * @param productState the state to set.
     * @throws NotFoundException
     * @throws com.cboe.exceptions.InvalidStateChangeException
     *
     */
    public void setProductStateByClass(String sessionName, int classKey, short productState)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
    {
        StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            buffer = new StringBuffer("setProductStateByClass");
            buffer.append(" for Session ").append(sessionName).append('.');
            buffer.append(" Class Key: ").append(classKey);
            buffer.append(" New State: ").append(formatProductState(productState));
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStateByClass", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productStateService.setProductStateByClass(sessionName, classKey, productState);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                       buffer.toString(), "");
        }
    }

   /**
    * Sets the given product to the given state.
    *
    * @param productKey the product to set.
    * @param productState the state to set.
     * @throws NotFoundException
     * @throws com.cboe.exceptions.InvalidStateChangeException
     *
    */
   public void setProductStateByProduct(String sessionName, int productKey, short productState)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
       StringBuffer buffer = null;
       if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("setProductStateByProduct");
           buffer.append(" for Session ").append(sessionName).append('.');
           buffer.append(" Product Key: ").append(productKey);
           buffer.append(" New State: ").append(formatProductState(productState));
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStateByProduct", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

      productStateService.setProductStateByProduct(sessionName, productKey, productState);

       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

// TraderAPIImpl overrides
//

    /**
    * @param oldPassword - old password
    * @param confirmOldPassword - confirmation of the old password
    * @param newPassword - confirmation of the new password
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @description Change the password for the current user
    * @usage used to change the user's password
    * @returns void
    */
    public void changePassword(String oldPassword, String newPassword)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            buffer = new StringBuffer("changePassword");
            buffer.append(" Old Password: ").append(oldPassword);
            buffer.append(" New Password: ").append(newPassword);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":changePassword", GUILoggerSABusinessProperty.USER_SESSION, buffer.toString());
        }

        systemAdminSessionManager.changePassword(oldPassword, newPassword);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_SESSION),
                                       buffer.toString(), "");
        }
    }

    public UserStructModel getValidUser()
            throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getValidUser()",
                                       GUILoggerSABusinessProperty.USER_SESSION, "");
        }

        if (validSessionProfileUser == null)
        {
            validSessionProfileUser = new UserStructModelImpl(systemAdminSessionManager.getValidSessionProfileUser());
        }
        return validSessionProfileUser;
    }

   /**
   * @description get the current CMI version information
   * @usage can be used to verify or report API version
   * @returns string containing the version of the CMI interface
   */
   public String getVersion()
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getVersion", GUILoggerSABusinessProperty.USER_SESSION, "");
        }

      String version = " ";

      try
      {
         version = systemAdminSessionManager.getVersion();
      }
      catch (Exception e)
      {
            GUILoggerHome.find().exception(Category + ".getVersion", e);
      }

      return version;
   }

    public void cleanUp()
    {
        //causes problem with being logged out before we cleanup. commented for now. should re-evaluate
        //when we can change cleanup to happen before logout
//        cleanupCaches();

        cleanupCallbackConsumers();

        cleanupSessionManagerInterfaces();

        systemAdminProductProcessor.unsubscribeForProductEvents();
        systemAdminProductProcessor = null;

        eventChannel = null;
    }

    protected void cleanupSessionManagerInterfaces()
    {
        exchangeService = null;
        productQueryService = null;
        productMaintenance = null;
        productStateService = null;
        tradingSessionService = null;
        productConfiguration = null;
        userMaintenanceEvent = null;
        tradingProperty = null;
        propertyService = null;
        firmService = null;
        firmMaintenance = null;

        sessionManagement = null;
        securityAdmin = null;

        textMessaging = null;
        marketMakerQuoteService = null;
        orderHandlingService = null;
        orderMaintenanceService = null;
        orderBookService = null;
        tradeMaintenanceService = null;

        tradingSessionEventStateService = null;

        userTradingParameterService = null;
        userService = null;
        alertEventService = null;

        super.cleanupSessionManagerInterfaces();
    }

    /**
     * cleanupCallbackConsumers()
     * This method cleans up all user related callback consumers
     */
    protected void cleanupCallbackConsumers()
    {
        // disconnect all consumers in the CurrentMarketConsumerCache and remove all consumers from the cache
        disconnectConsumers(cmiConsumerCacheFactory.getCurrentMarketConsumerCache().getAllCallbackConsumers());
        cmiConsumerCacheFactory.getCurrentMarketConsumerCache().cleanupCallbackConsumers();
        // disconnect all consumers in the RecapConsumerCache and remove all consumers from the cache
        disconnectConsumers(cmiConsumerCacheFactory.getRecapConsumerCache().getAllCallbackConsumers());
        cmiConsumerCacheFactory.getRecapConsumerCache().cleanupCallbackConsumers();
        // disconnect all consumers in the NBBOConsumerCache and remove all consumers from the cache
        disconnectConsumers(cmiConsumerCacheFactory.getNBBOConsumerCache().getAllCallbackConsumers());
        cmiConsumerCacheFactory.getNBBOConsumerCache().cleanupCallbackConsumers();
        // disconnect all consumers in the BookDepthConsumerCache and remove all consumers from the cache
        disconnectConsumers(cmiConsumerCacheFactory.getBookDepthConsumerCache().getAllCallbackConsumers());
        cmiConsumerCacheFactory.getBookDepthConsumerCache().cleanupCallbackConsumers();

        disconnectConsumer(expectedOpeningPriceConsumer);
        disconnectConsumer(orderStatusConsumer);
        disconnectConsumer(classStatusConsumer);
        disconnectConsumer(adminProductStatusConsumer);
        disconnectConsumer(strategyStatusConsumer);
        disconnectConsumer(tradingSessionStatusConsumer);
        disconnectConsumer(tickerConsumer);

        disconnectConsumer(quoteStatusConsumer);
        disconnectConsumer(rfqConsumer);

        disconnectConsumer(userSessionAdminConsumer);
        disconnectConsumer(alertConsumer);
        disconnectConsumer(linkageStatusConsumer);

        expectedOpeningPriceConsumer    = null;
        orderStatusConsumer             = null;
        classStatusConsumer             = null;
        adminProductStatusConsumer           = null;
        strategyStatusConsumer          = null;
        tradingSessionStatusConsumer    = null;
        tickerConsumer                  = null;

        quoteStatusConsumer             = null;
        rfqConsumer                     = null;

        userSessionAdminConsumer        = null;
        alertConsumer                   = null;

        linkageStatusConsumer           = null;
    }

    protected void cleanupCaches()
    {
        firmCache.cleanup();
        affiliatedFirmCache.cleanup();
        super.cleanupCaches();
        cleanupExchangeToUnderlyingProductsCache();
    }

    private void disconnectConsumers(org.omg.CORBA.Object[] consumers)
    {
        for (int i = 0; i < consumers.length; i++)
        {
            disconnectConsumer(consumers[i]);
        }
    }

    private void disconnectConsumer(org.omg.CORBA.Object consumer)
    {
        try
        {
            POAHelper.disconnect(POAHelper.reference_to_servant(consumer, null), null);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(Category + ".disconnectConsumer", "Error disconnecting user consumer::" + consumer, e);
        }
    }

    /**
     * Logs the current user out of the CAS.
     *
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     */
   public void logout()
      throws SystemException, CommunicationException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":logout", GUILoggerSABusinessProperty.USER_SESSION, "");
        }

      systemAdminSessionManager.logout();
      cleanUp();
        SystemAdminUserAccessFactory.unregisterClientListener(userClientListener);
   }

// ProductMaintenanceServices
//

   /**
    * This method allows the update or adding of an individual product.
    */
   public ProductStruct updateProduct(ProductStruct updatedProduct)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateProduct", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, updatedProduct);
        }

        ProductStruct struct = productMaintenance.updateProduct(updatedProduct);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "updateProduct", updatedProduct);
       }
       return struct;
   }

   /**
    * This method allows the update or adding of an individual product.
    */
   public ProductStructV2 updateProductV2(ProductStructV2 updatedProduct)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateProductV2", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, updatedProduct);
        }

        ProductStructV2 struct = productMaintenance.updateProductV2(updatedProduct);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "updateProductV2", updatedProduct);
       }
       return struct;
   }

    public ProductStructV4 updateProductV4(ProductStructV4 updatedProduct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateProductV4",
                                       GUILoggerSABusinessProperty.PRODUCT_DEFINITION,
                                       updatedProduct);
        }

        ProductStructV4 struct = productMaintenance.updateProductV4(updatedProduct);
        if(GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().nonRepudiationAudit(
                    formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                    "updateProductV4", struct);
        }
        return struct;
    }

    /**
    * This method allows the update or adding of an individual ProductClass.
    */
   public ProductClassStruct updateProductClass(ClassDefinitionStruct updatedProductClass)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateProductClass", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, updatedProductClass);
        }

       ProductClassStruct struct = productMaintenance.updateProductClass(updatedProductClass);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "updateProductClass", updatedProductClass);
       }
       return struct;
   }

    /**
     * This method allows the update or adding of an individual ProductClass associated with a session code.
     */
    public ProductClassStruct updateProductClassWithSessionCode(ClassDefinitionStruct updatedProductClass, String sessionCode)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = updatedProductClass;
            argObj[1] = sessionCode;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateProductClassWithSessionCode", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, argObj);
        }

       ProductClassStruct struct = productMaintenance.updateProductClassWithSessionCode(updatedProductClass, sessionCode);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "updateProductClassWithSessionCode", struct);
       }
       return struct;
    }

    /**
     * This method adds a new product class associated with a session code.
     */
    public ProductClassStruct addProductClassWithSessionCode(ClassDefinitionStruct newClass, String sessionCode)
          throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = newClass;
            argObj[1] = sessionCode;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addProductClassWithSessionCode", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, argObj);
        }

        ProductClassStruct struct = productMaintenance.addProductClassWithSessionCode(newClass, sessionCode);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                       "addProductClassWithSessionCode", struct);
        }
        return struct;
    }

    /**
     * This method returns an array of SessionCodeDescriptionStructs for a product type.
     */
    public SessionCodeDescription[] getSessionCodesForProductType(short productType)
          throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSessionCodesForProductType", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, new Short(productType));
        }
        SessionCodeDescriptionStruct[] structs = productMaintenance.getSessionCodesForProductType(productType);
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSessionCodesForProductType", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, structs);
        }
        SessionCodeDescription[] sessionCodeDescriptions = new SessionCodeDescription[structs.length];
        for (int i = 0; i < structs.length; i++)
        {
            sessionCodeDescriptions[i] = SessionCodeDescriptionFactory.create(structs[i]);
        }
        return sessionCodeDescriptions;
    }

   /**
    * This method allows the update or addition of a ProductDescription.
    */
    public ProductDescriptionStruct updateProductDescription(ProductDescriptionStruct updatedDescription)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateProductDescription", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, updatedDescription);
        }

        ProductDescriptionStruct struct = productMaintenance.updateProductDescription(updatedDescription);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                       "updateProductDescription", updatedDescription);
        }
        return struct;
    }

   /**
    * This method allows the updating or adding of a set of products.
    */
   public ProductStruct[] updateProducts(ProductStruct[] updatedProducts)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateProducts", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, updatedProducts);
        }

        ProductStruct[] structs = productMaintenance.updateProducts(updatedProducts);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "updateProducts", updatedProducts);
       }
       return structs;
   }

   /**
    * This method allows the updating or adding of an individual ReportingClass.
    */
   public ReportingClassStruct updateReportingClass(ReportingClassStruct updatedReportingClass)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateReportingClass", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, updatedReportingClass);
        }

        ReportingClassStruct struct = productMaintenance.updateReportingClass(updatedReportingClass);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "updateReportingClass", updatedReportingClass);
       }
       return struct;
   }

   /**
    * This method adds a new product.
    */
   public ProductStruct addProduct(ProductStruct newProduct)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addProduct", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, newProduct);
        }

      ProductStruct struct = productMaintenance.addProduct(newProduct);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "addProduct", newProduct);
       }
       return struct;
   }

   /**
    * This method adds a new product using the V2 struct.
    */
   public ProductStructV2 addProductV2(ProductStructV2 newProduct)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addProductV2", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, newProduct);
        }

      ProductStructV2 struct = productMaintenance.addProductV2(newProduct);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "addProductV2", newProduct);
       }
       return struct;
   }

    public ProductStructV4 addProductV4(ProductStructV4 newProduct)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addProductV4",
                                       GUILoggerSABusinessProperty.PRODUCT_DEFINITION, newProduct);
        }

        ProductStructV4 struct = productMaintenance.addProductV4(newProduct);
        if(GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().nonRepudiationAudit(
                    formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                    "addProductV4", struct);
        }
        return struct;
    }

    /**
    * This method adds a sequence of new products.
    */
   public ProductStruct[] addProducts(ProductStruct[] newProducts)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addProducts", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, newProducts);
        }

        ProductStruct[] structs = productMaintenance.addProducts(newProducts);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "addProducts", newProducts);
       }
       return structs;
   }

   /**
    * This method adds a new product class.
    */
   public ProductClassStruct addProductClass(ClassDefinitionStruct newClass)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":newClass", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, newClass);
        }

      ProductClassStruct struct = productMaintenance.addProductClass(newClass);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "addProductClass", newClass);
       }
       return struct;
   }

   /**
    * This method adds a new reporting class.
    */
   public ReportingClassStruct addReportingClass(ReportingClassStruct newClass)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addReportingClass", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, newClass);
        }

        ReportingClassStruct struct = productMaintenance.addReportingClass(newClass);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "addReportingClass", newClass);
       }
       return struct;
   }

   /**
    * This method updates a product type.
    */
   public ProductTypeStruct updateProductType(ProductTypeStruct updatedType)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateProductType", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, updatedType);
        }

        ProductTypeStruct struct = productMaintenance.updateProductType(updatedType);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                      "updateProductType", updatedType);
       }
       return struct;
   }

   /**
    * This method notifies the implementor that the final price adjustment
    * updates are starting.
    */
   public void priceAdjustmentUpdateBegin()
      throws SystemException, CommunicationException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":priceAdjustmentUpdateBegin", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, "");
        }

      productMaintenance.priceAdjustmentUpdateBegin();
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      "priceAdjustmentUpdateBegin", "");
       }
   }

   /**
    * This method notifies the implementor that the final price adjustment
    * updates are complete.
    */
   public void priceAdjustmentUpdateComplete()
      throws SystemException, CommunicationException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":priceAdjustmentUpdateComplete", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, "");
        }

      productMaintenance.priceAdjustmentUpdateComplete();
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      "priceAdjustmentUpdateComplete", "");
       }
   }

   /**
    * This method notifies the implementor that the a price adjustment update
    * has been received.
    */
   public PriceAdjustmentStruct updatePriceAdjustment(PriceAdjustmentStruct updatedAdjustment)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updatePriceAdjustment", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, updatedAdjustment);
        }

        PriceAdjustmentStruct struct = productMaintenance.updatePriceAdjustment(updatedAdjustment);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      "updatePriceAdjustment", updatedAdjustment);
       }
       return struct;
   }

    /**
     * This method provides a way to retrieve ALL PriceAdjustmentStructs, with or without adjustedClasses
     */
    public PriceAdjustmentStruct[] getAllPriceAdjustments(boolean includeDetail)
        throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllPriceAdjustments", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, new Boolean(includeDetail));
        }

        return productMaintenance.getAllPriceAdjustments(includeDetail);
    }

    /**
     * This method provides a way to remove a PriceAdjustmentStruct
     */
    public void removePriceAdjustment(int adjustmentNumber)
        throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            buffer = new StringBuffer("removePriceAdjustment");
            buffer.append(" for Adjustment Number: ").append(adjustmentNumber);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removePriceAdjustment", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productMaintenance.removePriceAdjustment(adjustmentNumber);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                       buffer.toString(), "");
        }
    }

   /**
    * This method provides a way to retrieve all information about a price
    * adjustment for a class.
    */
   public PriceAdjustmentStruct getPriceAdjustment(int classKey, boolean includeDetail)
      throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = new Boolean(includeDetail);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPriceAdjustment", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }

        return productMaintenance.getPriceAdjustment(classKey, includeDetail);
   }

   /**
    * This method provides a way to retrieve settlement price information
    * for products specified by passed in ProductKeysStruct sequence.
    */
   public ProductSettlementStruct[] getSettlementForProducts(ProductKeysStruct[] productKeys)
      throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSettlementForProducts", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, productKeys);
        }

        return productMaintenance.getSettlementForProducts(productKeys);
   }

   /**
    * This method provides a way to retrieve settlement price information
    * for classes specified by passed in ClassKeysStruct sequence.
    */
   public ClassSettlementStruct[] getSettlementForClasses(int[] classKeys)
      throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSettlementForClasses", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, classKeys);
        }

        return productMaintenance.getSettlementForClasses(classKeys);
   }

   /**
    * This method provides a way to update settlement price information
    * for products by passed in ProductSettlementStruct sequence.
    */
   public ProductSettlementStruct[] updateSettlementByProduct(ProductSettlementStruct[] productSettlements)
      throws SystemException, CommunicationException, DataValidationException, NotFoundException, TransactionFailedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateSettlementByProduct", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, productSettlements);
        }

        ProductSettlementStruct[] structs = productMaintenance.updateSettlementByProduct(productSettlements);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                       "updateSettlementByProduct", productSettlements);
        }
        return structs;
   }

   /**
    * This method provides a way to update open interest information
    * for products by passed in ProductOpenInterestStruct sequence.
    */
   public ProductOpenInterestStruct[] updateOpenInterestByProduct(ProductOpenInterestStruct[] openInterests)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateOpenInterestByProduct", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, openInterests);
        }

        ProductOpenInterestStruct[] structs = productMaintenance.updateOpenInterestByProduct(openInterests);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                       "updateOpenInterestByProduct", openInterests);
        }
        return structs;
   }

   /**
    * This method provides a way to retrieve open interest information
    * for products specified by passed in ProductKeysStruct sequence.
    */
   public ProductOpenInterestStruct[] getOpenInterestForProducts(ProductKeysStruct[] productKeys)
      throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOpenInterestForProducts", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, productKeys);
        }

        return productMaintenance.getOpenInterestForProducts(productKeys);
   }

   /**
    * This method provides a way to retrieve open interest information
    * for classes specified by passed in ClassKeysStruct sequence.
    */
   public ClassOpenInterestStruct[] getOpenInterestForClasses(int[] classKeys)
      throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOpenInterestForClasses", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, classKeys);
        }

        return productMaintenance.getOpenInterestForClasses(classKeys);
   }

   /**
    * This method notifies the service that the given class needs to be
    * refreshed from TPF.
    */
   public void refreshReportingClass(String classSymbol)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":refreshReportingClass", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, classSymbol);
        }

      productMaintenance.refreshReportingClass(classSymbol);
   }


    public void subscribeProductStatusByKey(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribeProductStatusByKey", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }
        ChannelKey key = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, new Integer(classKey));
        int subscriptionCount1 = SubscriptionManagerFactory.find().subscribe(key, clientListener, adminProductStatusConsumer);
        key = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, new Integer(classKey));
        int subscriptionCount2 = SubscriptionManagerFactory.find().subscribe(key, clientListener, adminProductStatusConsumer);
        if (subscriptionCount1 == 1 && subscriptionCount2 == 1)
        {
             productMaintenance.subscribeProductStatusByKey(classKey, adminProductStatusConsumer);
        }
    }

    public void subscribeClassStatus(short productType, EventChannelListener clientListener)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribeClassStatus", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, clientListener);
        }
        ChannelKey key = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, new Integer(productType));
        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, adminProductStatusConsumer) == 1)
        {
            productMaintenance.subscribeClassStatus(productType, adminProductStatusConsumer);
        }
    }

    public void unsubscribeClassStatus(short productType, EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":unsubscribeClassStatus", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, clientListener);
        }
        ChannelKey key = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, new Integer(productType));
        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, adminProductStatusConsumer) == 0)
        {
            productMaintenance.unsubscribeClassStatus(productType, adminProductStatusConsumer);
        }
    }

    public void unsubscribeProductStatusByKey(int classKey, EventChannelListener clientListener)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = clientListener;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":unsubscribeProductStatusByKey", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }
        ChannelKey key = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, new Integer(classKey));
        int subscriptionCount1 = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, adminProductStatusConsumer);
        key = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, new Integer(classKey));
        int subscriptionCount2 = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, adminProductStatusConsumer);
        if (subscriptionCount1 == 0 && subscriptionCount2 == 0)
        {
            productMaintenance.unsubscribeProductStatusByKey(classKey, adminProductStatusConsumer);
        }
    }

    /**
     * This method provides a way to retrieve an array of Transaction Fee Codes
     */
    public TransactionFeeCodeStruct[] getTransactionFeeCodes()
        throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTransactionFeeCodes", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE);
        }

        return productMaintenance.getTransactionFeeCodes();
    }

    public UserAccountModel addUser(UserAccountModel user)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            TransactionFailedException, AlreadyExistsException
    {
        SessionProfileUserDefinitionStruct struct = user.getUserDefinitionStruct();

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addUser",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, struct);
        }

        struct = userMaintenanceEvent.addSessionProfileUser(struct);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_MANAGEMENT),
                                       "addUser", struct);
        }

        return UserCollectionFactory.createUserAccountModel(struct, false);
    }

    /**
     * @param  user - UserAccountModel - user to be deleted
     * @since  Single Acronym scrum - May 18, 2005 - Shawn Khosravani
     */
    public void deleteUser(UserAccountModel user)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException,
                   NotAcceptedException, TransactionFailedException
    {
        SessionProfileUserDefinitionStruct struct = user.getUserDefinitionStruct();

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":deleteUser",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, struct);
        }

        userMaintenanceEvent.deleteUser(struct.userId);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_MANAGEMENT),
                                       "deleteUser", struct);
        }
    }

   public PreferenceStruct[] getSystemPreferences()
      throws SystemException, CommunicationException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSystemPreferences", GUILoggerSABusinessProperty.USER_MANAGEMENT, "");
        }

      return userMaintenanceEvent.getSystemPreferences();
   }

   public void removeSystemPreferencesByPrefix(String p0)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeSystemPreferencesByPrefix", GUILoggerSABusinessProperty.USER_MANAGEMENT, p0);
        }

      userMaintenanceEvent.removeSystemPreferencesByPrefix(p0);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_MANAGEMENT),
                                      "removeSystemPreferencesByPrefix", p0);
       }
   }

   public void removeSystemPreferencesForUser(String p0, String p1)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
       Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeSystemPreferencesForUser", GUILoggerSABusinessProperty.USER_MANAGEMENT, argObj);
        }

        userMaintenanceEvent.removeSystemPreferencesForUser(p0, p1);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_MANAGEMENT),
                                      "removeSystemPreferencesForUser", argObj);
       }
   }

   public void setSystemPreferences(PreferenceStruct[] p0)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setSystemPreferences", GUILoggerSABusinessProperty.USER_MANAGEMENT, p0);
        }

      userMaintenanceEvent.setSystemPreferences(p0);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_MANAGEMENT),
                                      "setSystemPreferences", p0);
       }
   }

   public void setSystemPreferencesForUser(String p0, PreferenceStruct[] p1)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
   {
       Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setSystemPreferencesForUser", GUILoggerSABusinessProperty.USER_MANAGEMENT, argObj);
        }

        userMaintenanceEvent.setSystemPreferencesForUser(p0, p1);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_MANAGEMENT),
                                      "setSystemPreferencesForUser", argObj);
       }
   }

// ProductConfigurationServices
//

    public boolean isPostGroup(GroupStruct struct)
    {
        return ProductConfigurationQueryAPIFactory.find().isPostGroup(struct);
    }

    public boolean isStationGroup(GroupStruct struct)
    {
        return ProductConfigurationQueryAPIFactory.find().isStationGroup(struct);
    }

    public GroupStruct getPostGroupForStation(GroupStruct stationStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return ProductConfigurationQueryAPIFactory.find().getPostGroupForStation(stationStruct);
    }

    public GroupStruct[] getSuperGroupsForGroup(int group)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
        return ProductConfigurationQueryAPIFactory.find().getSuperGroupsForGroup(group);
   }

    public GroupStruct[] getSubGroupsForGroup(int group)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException
   {
        return ProductConfigurationQueryAPIFactory.find().getSubGroupsForGroup(group);
   }

   public void addProductClassToGroup(int classKey, String group)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("addProductClassToGroup");
           buffer.append(" Group: ").append(group);
           buffer.append(" Class Key: ").append(classKey);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addProductClassToGroup", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.addProductClassToGroup(classKey, group);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void addProductClassToGroupByKey(int classKey, int group)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("addProductClassToGroupByKey");
           buffer.append(" Group Key: ").append(group);
           buffer.append(" Class Key: ").append(classKey);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addProductClassToGroupByKey", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.addProductClassToGroupByKey(classKey, group);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void addSubGroup(String subGroup, String superGroup)
      throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException, AlreadyExistsException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("addSubGroup");
           buffer.append(" SubGroup: ").append(subGroup);
           buffer.append(" SuperGroup: ").append(superGroup);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addSubGroup", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.addSubGroup(subGroup, superGroup);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void addSubGroupByKey(String subGroup, int superGroup)
      throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException, AlreadyExistsException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("addSubGroupByKey");
           buffer.append(" SubGroup: ").append(subGroup);
           buffer.append(" SuperGroup Key: ").append(superGroup);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addSubGroupByKey", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.addSubGroupByKey(subGroup, superGroup);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void createGroup(String name, int type)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, AlreadyExistsException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("createGroup");
           buffer.append(" Name: ").append(name);
           buffer.append(" Type: ").append(type);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":createGroup", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.createGroup(name, type);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void createGroupType(GroupTypeStruct groupType)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, AlreadyExistsException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":createGroupType", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, groupType);
        }

      productConfiguration.createGroupType(groupType);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      "createGroupType", groupType);
       }
   }

    public GroupStruct getGroup(int groupKey, int groupType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return ProductConfigurationQueryAPIFactory.find().getGroup(groupKey, groupType);
    }

    public int getPostStationGroupType()
    {
        return ProductConfigurationQueryAPIFactory.find().getPostStationGroupType();
    }

    public int getProcessGroupType()
    {
    	return ProductConfigurationQueryAPIFactory.find().getProcessGroupType();
    }

   public int getGroupKey(String groupName)
      throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
   {
      return ProductConfigurationQueryAPIFactory.find().getGroupKey(groupName);
   }

   public int[] getGroupKeysForProductClass(int classKey)
      throws SystemException, CommunicationException, DataValidationException, AuthorizationException
   {
      return ProductConfigurationQueryAPIFactory.find().getGroupKeysForProductClass(classKey);
   }

   public GroupTypeStruct[] getGroupTypes()
      throws SystemException, CommunicationException, DataValidationException, AuthorizationException
   {
      return ProductConfigurationQueryAPIFactory.find().getGroupTypes();
   }

   public GroupStruct[] getGroupsByType(int type)
      throws SystemException, CommunicationException, DataValidationException, AuthorizationException
   {
      return ProductConfigurationQueryAPIFactory.find().getGroupsByType(type);
   }

    public ProductClass[] getProductClassesByGroupKey(int groupKey) throws SystemException,
            CommunicationException, DataValidationException, AuthorizationException
    {
        return ProductConfigurationQueryAPIFactory.find().getProductClassesByGroupKey(groupKey);
    }

   public GroupStruct[] getGroupsForProductClass(int classKey)
      throws SystemException, CommunicationException, DataValidationException, AuthorizationException
   {
      return ProductConfigurationQueryAPIFactory.find().getGroupsForProductClass(classKey);
   }

   public int[] getProductClassesForGroup(String groupName)
      throws SystemException, CommunicationException, DataValidationException, AuthorizationException
   {
      return ProductConfigurationQueryAPIFactory.find().getProductClassesForGroup(groupName);
   }

   public int[] getProductClassesForGroupByKey(int groupKey)
      throws SystemException, CommunicationException, DataValidationException, AuthorizationException
   {
      return ProductConfigurationQueryAPIFactory.find().getProductClassesForGroupByKey(groupKey);
   }

   public void moveProductClassToGroup(int classKey, String origin, String destination)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("moveProductClassToGroup");
           buffer.append(" Class Key: ").append(classKey);
           buffer.append(" Origin: ").append(origin);
           buffer.append(" Destination: ").append(destination);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":moveProductClassToGroup", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.moveProductClassToGroup(classKey, origin, destination);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void moveProductClassToGroupByKey(int classKey, int origin, int destination)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException, AlreadyExistsException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("moveProductClassToGroupByKey");
           buffer.append(" Class Key: ").append(classKey);
           buffer.append(" Origin Key: ").append(origin);
           buffer.append(" Destination Key: ").append(destination);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":moveProductClassToGroupByKey", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.moveProductClassToGroupByKey(classKey, origin, destination);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void removeProductClassFromGroup(int classKey, String group)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("removeProductClassFromGroup");
           buffer.append(" Class Key: ").append(classKey);
           buffer.append(" Group: ").append(group);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeProductClassFromGroup", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.removeProductClassFromGroup(classKey, group);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void removeProductClassFromGroupByKey(int classKey, int group)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("removeProductClassFromGroupByKey");
           buffer.append(" Class Key: ").append(classKey);
           buffer.append(" Group Key: ").append(group);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeProductClassFromGroupByKey", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.removeProductClassFromGroupByKey(classKey, group);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void removeSubGroup(String group, String superGroup)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("removeSubGroup");
           buffer.append(" Group: ").append(group);
           buffer.append(" SuperGroup: ").append(superGroup);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeSubGroup", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.removeSubGroup(group, superGroup);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void removeSubGroupByKey(int group, int superGroup)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
       StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
       {
           buffer = new StringBuffer("removeSubGroupByKey");
           buffer.append(" Group Key: ").append(group);
           buffer.append(" SuperGroup Key: ").append(superGroup);
       }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeSubGroupByKey", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, buffer.toString());
        }

        productConfiguration.removeSubGroupByKey(group, superGroup);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      buffer.toString(), "");
       }
   }

   public void updateGroupType(GroupTypeStruct groupType)
      throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateGroupType", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, groupType);
        }

      productConfiguration.updateGroupType(groupType);
       if (GUILoggerHome.find().isAuditOn())
       {
           GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE),
                                      "updateGroupType", groupType);
       }
   }

// TradingPropertyService
//

   public AllocationStrategyStruct getAllocationStrategy(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllocationStrategy", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getAllocationStrategy(p0, p1, p2);
   }

    public int getBookDepthSize(String p0, int p1, IntHolder p2)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
         if (GUILoggerHome.find().isDebugOn())
         {
             Object[] argObj = new Object[3];
             argObj[0] = p0;
             argObj[1] = new Integer(p1);
             argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getBookDepthSize", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
         }

       return tradingProperty.getBookDepthSize(p0, p1, p2);
    }

   public int getContinuousQuotePeriodForCredit(String p0, IntHolder p1)
      throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getContinuousQuotePeriodForCredit", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getContinuousQuotePeriodForCredit(p0, p1);
   }

   public double getDPMParticipationPercentage(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDPMParticipationPercentage", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getDPMParticipationPercentage(p0, p1, p2);
   }

   public EPWStruct[] getExchangePrescribedWidth(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, DataValidationException, AuthorizationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getExchangePrescribedWidth", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getExchangePrescribedWidth(p0, p1, p2);
   }

   public double getFastMarketSpreadMultiplier(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFastMarketSpreadMultiplier", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getFastMarketSpreadMultiplier(p0, p1, p2);
   }

    public int getMinSizeForBlockTrade(String p0, int p1, IntHolder p2)
       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
         if (GUILoggerHome.find().isDebugOn())
         {
             Object[] argObj = new Object[3];
             argObj[0] = p0;
             argObj[1] = new Integer(p1);
             argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getMinSizeForBlockTrade", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
         }

       return tradingProperty.getMinSizeForBlockTrade(p0, p1, p2);
    }

    public int getContingencyTimeToLive(String s, int i, IntHolder intHolder) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getMinQuoteCreditDefaultSize(String p0, IntHolder p1)
      throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getMinQuoteCreditDefaultSize", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getMinQuoteCreditDefaultSize(p0, p1);
   }

   public TimeRangeStruct getOpeningPeriodTimeRange(String p0, IntHolder p1)
      throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOpeningPeriodTimeRange", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getOpeningPeriodTimeRange(p0, p1);
   }

   public int getOpeningPriceDelay(String p0, IntHolder p1)
      throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOpeningPriceDelay", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        if (tradingProperty != null)
      {
         return tradingProperty.getOpeningPriceDelay(p0, p1);
      }
      else
      {
            throw ExceptionBuilder.systemException("NULL TradingPropertyService !!!", 1);
      }
   }

   public int getOpeningPriceRate(String p0, IntHolder p1)
      throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOpeningPriceRate", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getOpeningPriceRate(p0, p1);
   }

   public TimeRangeStruct getPreClosingTimePeriod(String p0, IntHolder p1)
      throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPreClosingTimePeriod", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getPreClosingTimePeriod(p0, p1);
   }

   public boolean getETFFlag(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getETFFlag", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getETFFlag(p0, p1, p2);
   }

    public int getLotSize(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getLotSize", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getLotSize(p0, p1, p2);
    }

    public int getFCQS(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFCQS", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getFCQS(p0, p1, p2);

    }

    public int getFPQS(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFPQS", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getFPQS(p0, p1, p2);

    }

    public int getSOrderTimeToCreate(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSOrderTimeToCreate", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getSOrderTimeToCreate(p0, p1, p2);

    }

    public int getSOrderTimeToCreateBeforeClose(String p0, int p1, IntHolder p2)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSOrderTimeToCreateBeforeClose", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getSOrderTimeToCreateBeforeClose(p0, p1, p2);

    }

    public int getSOrderTimeToLive(String p0, int p1, IntHolder p2)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSOrderTimeToLive", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getSOrderTimeToLive(p0, p1, p2);

    }

    public int getSOrderTimeToRejectFill(String p0, int p1, IntHolder p2)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSOrderTimeToRejectFill", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getSOrderTimeToRejectFill(p0, p1, p2);

    }

    public int getPOrderTimeToLive(String p0, int p1, IntHolder p2)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPOrderTimeToLive", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getPOrderTimeToLive(p0, p1, p2);

    }

    public int getPAOrderTimeToLive(String p0, int p1, IntHolder p2)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPAOrderTimeToLive", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getPAOrderTimeToLive(p0, p1, p2);

    }


    public double getIPPToleranceAmount(String p0, int p1, IntHolder p2)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getIPPToleranceAmount", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getIPPToleranceAmount(p0, p1, p2);

    }

   public int getProductOpenProcedureType(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductOpenProcedureType", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getProductOpenProcedureType(p0, p1, p2);
   }

    public boolean getSatisfactionAlertFlag(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSatisfactionAlertFlag", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getSatisfactionAlertFlag(p0, p1, p2);

    }

    public int getTradeType(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradeType", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getTradeType(p0, p1, p2);

    }

    public boolean getNeedsDpmQuoteToOpen(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getNeedsDpmQuoteToOpen", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getNeedsDpmQuoteToOpen(p0, p1, p2);

    }

   public boolean getLinkageEnabledFlag(String p0, int p1, IntHolder p2)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getLinkageEnabledFlag", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getLinkageEnabledFlag(p0, p1, p2);

   }

   public double getPrescribedWidthRatio(String p0, IntHolder p1)
      throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPrescribedWidthRatio", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getPrescribedWidthRatio(p0, p1);
   }

   public double getRFQResponseRatio(String p0, IntHolder p1)
      throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getRFQResponseRatio", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getRFQResponseRatio(p0, p1);
   }

   public int getRFQTimeout(String p0, int p1, IntHolder p2)
      throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
   {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getRFQTimeout", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

      return tradingProperty.getRFQTimeout(p0, p1, p2);
   }

    /**
     * Returns IPP Minimum Size property for given Product Class.
     *
     * @param sessionName
     * @param classKey
     * @param seq
     * @return
     * @throws SystemException
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws NotFoundException
     */
    public int getIPPMinSize(String sessionName, int classKey, IntHolder seq)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getIPPMinSize", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getIPPMinSize(sessionName, classKey, seq);
    }

    /**
     * Returns IPP Trade Through Flag property for given Product Class.
     *
     * @param sessionName
     * @param classKey
     * @param seq
     * @return
     * @throws SystemException
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws NotFoundException
     */
    public boolean getIPPTradeThroughFlag(String sessionName, int classKey, IntHolder seq)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getIPPMinSize", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getIPPTradeThroughFlag(sessionName, classKey, seq);
    }

    /**
     * Returns Quote Lock Timer property for given Product Class.
     *
     * @param sessionName
     * @param classKey
     * @param seq
     * @return
     * @throws SystemException
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws NotFoundException
     */
    public int getQuoteLockTimer(String sessionName, int classKey, IntHolder seq)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getIPPMinSize", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getQuoteLockTimer(sessionName, classKey, seq);
    }

    /**
     * Returns Quote Lock Notification Timer property for given Product Class.
     *
     * @param sessionName
     * @param classKey
     * @param seq
     * @return
     * @throws SystemException
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws NotFoundException
     */
    public int getQuoteLockNotificationTimer(String sessionName, int classKey, IntHolder seq)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getIPPMinSize", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getQuoteLockNotificationTimer(sessionName, classKey, seq);
    }

    /**
     * Returns Quote Lock Notification Timer property for given Product Class.
     *
     * @param sessionName
     * @param classKey
     * @param seq
     * @return
     * @throws SystemException
     * @throws DataValidationException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws NotFoundException
     */
    public int getQuoteTriggerTimer(String sessionName, int classKey, IntHolder seq)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getIPPMinSize", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getQuoteTriggerTimer(sessionName, classKey, seq);
    }

    public AuctionRangeStruct[] getAuctionTimeToLive(String sessionName, int classKey, IntHolder seq)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionTimeToLive", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getAuctionTimeToLive(sessionName, classKey, seq);
    }

    public AuctionLongStruct[] getAuctionMinPriceIncrement(String sessionName, int classKey, IntHolder seq)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionMinPriceIncrement", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getAuctionMinPriceIncrement(sessionName, classKey, seq);
    }

    public AuctionLongStruct[] getAuctionMinQuoters(String sessionName, int classKey, IntHolder seq)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionMinQuoters", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getAuctionMinQuoters(sessionName, classKey, seq);
    }

    public AuctionLongStruct[] getAuctionReceiverTypes(String sessionName, int classKey, IntHolder seq)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionReceiverTypes", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getAuctionReceiverTypes(sessionName, classKey, seq);
    }

    public InternalizationPercentageStruct[] getInternalizationGuaranteedPercentage(String sessionName, int classKey, IntHolder seq)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getInternalizationGuaranteedPercentage", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getInternalizationGuaranteedPercentage(sessionName, classKey, seq);
    }

    public AuctionLongStruct[] getAuctionOrderTicksAwayFromNBBO(String sessionName, int classKey, IntHolder seq)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionOrderTicksAwayFromNBBO", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getAuctionOrderTicksAwayFromNBBO(sessionName, classKey, seq);
    }

    public int[] getAutoExEligibleStrategyTypes(String sessionName, int classKey, IntHolder seq)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAutoExEligibleStrategyTypes", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getAutoExEligibleStrategyTypes(sessionName, classKey, seq);
    }

    public AuctionBooleanStruct[] getAuctionEnabled(String sessionName, int classKey, IntHolder seq)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionEnabled", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getAuctionEnabled(sessionName, classKey, seq);
    }

    public AuctionOrderSizeTicksStruct[] getAuctionMinOrderSizeForTicksAboveNBBO(String sessionName, int classKey, IntHolder seq)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionMinOrderSizeForTicksAboveNBBO", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getAuctionMinOrderSizeForTicksAboveNBBO(sessionName, classKey, seq);
    }

    /**
     * Returns all Book Depth Sizes for a given trading session.
     *
     * @param sessionName
     * @param propertyType
     * @return
     * @throws DataValidationException
     * @throws AuthorizationException
     * @throws CommunicationException
     * @throws SystemException
     */
    public LongStruct[] getBookDepthSizeAllClasses(String sessionName, int propertyType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getBookDepthSizeAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getBookDepthSizeAllClasses(sessionName, propertyType);
    }

    /**
     * Returns all getContinuousQuotePeriodForCredit for a given trading session.
     *
     * @param sessionName
     * @param propertyType
     * @return
     * @throws DataValidationException
     * @throws AuthorizationException
     * @throws CommunicationException
     * @throws SystemException
     */
    public LongStruct[] getContinuousQuotePeriodForCreditAllClasses(String sessionName, int propertyType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getContinuousQuotePeriodForCreditAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getContinuousQuotePeriodForCreditAllClasses(sessionName, propertyType);
    }

    public DoubleStruct[] getDPMParticipationPercentageAllClasses(String sessionName, int propertyType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDPMParticipationPercentageAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getDPMParticipationPercentageAllClasses(sessionName, propertyType);
    }

    public SpreadClassStruct[] getExchangePrescribedWidthForAllClasses(String sessionName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[1];
            argObj[0] = sessionName;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getExchangePrescribedWidthForAllClasses",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getExchangePrescribedWidthForAllClasses(sessionName);
    }

    public DoubleStruct[] getFastMarketSpreadMultiplierAllClasses(String sessionName, int propertyType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFastMarketSpreadMultiplierAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getFastMarketSpreadMultiplierAllClasses(sessionName, propertyType);
    }

    public LongStruct[] getIPPMinSizeAllClasses(String sessionName, int propertyType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getIPPMinSizeAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getIPPMinSizeAllClasses(sessionName, propertyType);
    }

    public BooleanStruct[] getIPPTradeThroughFlagAllClasses(String sessionName, int propertyType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getIPPTradeThroughFlagAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getIPPTradeThroughFlagAllClasses(sessionName, propertyType);
    }

    public DpmRightsScaleClassStruct[] getDpmRightsScaleForAllClasses(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[1];
            argObj[0] = sessionName;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDpmRightsScaleForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
       return tradingProperty.getDpmRightsScaleForAllClasses(sessionName);
    }

    public DoubleStruct[] getDpmRightsSplitRateForAllClasses(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDpmRightsSplitRateForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
       return tradingProperty.getDpmRightsSplitRateForAllClasses(sessionName);
    }

    public DoubleStruct[] getUMASplitRateForAllClasses(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDpmRightsSplitRateForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
       return tradingProperty.getUMASplitRateForAllClasses(sessionName);
    }

    public LongStruct[] getUMAEqualDistributionWeightForDPMForAllClasses(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUMAEqualDistributionWeightForDPMForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
       return tradingProperty.getUMAEqualDistributionWeightForDPMForAllClasses(sessionName);
    }

    public AllocationStrategiesClassStruct[] getAllocationStrategiesForAllClasses(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":AllocationStrategiesClassStruct", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
       return tradingProperty.getAllocationStrategiesForAllClasses(sessionName);
    }

    public LongStruct[] getMinQuoteCreditDefaultSizeAllClasses(String sessionName, int propertyType)
        throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getMinQuoteCreditDefaultSizeAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getMinQuoteCreditDefaultSizeAllClasses(sessionName, propertyType);
    }

    public LongStruct[] getMinSizeForBlockTradeAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getMinSizeForBlockTradeAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getMinSizeForBlockTradeAllClasses(sessionName, propertyType);
    }

    public LongStruct[] getContingencyTimeToLiveAllClasses(String s, int i) throws SystemException, CommunicationException, DataValidationException, AuthorizationException {
        return null;
    }

    public TimeRangeClassStruct[] getOpeningPeriodTimeRangeAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOpeningPeriodTimeRangeAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getOpeningPeriodTimeRangeAllClasses(sessionName, propertyType);
    }

    public LongStruct[] getOpeningPriceDelayAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOpeningPriceDelayAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getOpeningPriceDelayAllClasses(sessionName, propertyType);
    }

    public LongStruct[] getOpeningPriceRateAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOpeningPriceRateAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getOpeningPriceRateAllClasses(sessionName, propertyType);
    }

    public TimeRangeClassStruct[] getPreClosingTimePeriodAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPreClosingTimePeriodAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getPreClosingTimePeriodAllClasses(sessionName, propertyType);
    }

    public DoubleStruct[] getPrescribedWidthRatioAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPrescribedWidthRatioAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getPrescribedWidthRatioAllClasses(sessionName, propertyType);
    }

    public LongStruct[] getQuoteLockNotificationTimerAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getQuoteLockNotificationTimerAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getQuoteLockNotificationTimerAllClasses(sessionName, propertyType);
    }

    public LongStruct[] getQuoteLockTimerAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getQuoteLockTimerAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getQuoteLockTimerAllClasses(sessionName, propertyType);
    }

    public LongStruct[] getQuoteTriggerTimerAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getQuoteTriggerTimerAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getQuoteTriggerTimerAllClasses(sessionName, propertyType);
    }

    public DoubleStruct[] getRFQResponseRatioAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getRFQResponseRatioAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getRFQResponseRatioAllClasses(sessionName, propertyType);
    }

    public LongStruct[] getRFQTimeoutAllClasses(String sessionName, int propertyType)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(propertyType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getRFQTimeoutAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getRFQTimeoutAllClasses(sessionName, propertyType);
    }

    public AllocationStrategyStructV2[] getAllocationStrategies(String sessionName, int classKey, IntHolder seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllocationStrategies", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getAllocationStrategies(sessionName, classKey, seq);
    }

    //typically not invoked directly
    public AllocationStrategyClassStruct[] getAllocationStrategyForAllClasses(String s)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
        return tradingProperty.getAllocationStrategyForAllClasses(s);
    }

    public AuctionRangeClassStruct[] getAuctionTimeToLiveForAllClasses(String sessionName)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionTimeToLiveForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
        return tradingProperty.getAuctionTimeToLiveForAllClasses(sessionName);
    }

    public AuctionLongClassStruct[] getAuctionMinPriceIncrementForAllClasses(String sessionName)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionMinPriceIncrementForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
        return tradingProperty.getAuctionMinPriceIncrementForAllClasses(sessionName);
    }

    public AuctionLongClassStruct[] getAuctionMinQuotersForAllClasses(String sessionName)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionMinQuotersForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
        return tradingProperty.getAuctionMinQuotersForAllClasses(sessionName);
    }

    public AuctionLongClassStruct[] getAuctionReceiverTypesForAllClasses(String sessionName)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionReceiverTypesForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
        return tradingProperty.getAuctionReceiverTypesForAllClasses(sessionName);
    }

    public InternalizationPercentageClassStruct[] getInternalizationGuaranteedPercentageForAllClasses(String sessionName)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getInternalizationGuaranteedPercentageForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
        return tradingProperty.getInternalizationGuaranteedPercentageForAllClasses(sessionName);
    }

    public AuctionLongClassStruct[] getAuctionOrderTicksAwayFromNBBOForAllClasses(String sessionName)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionOrderTicksAwayFromNBBOForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
        return tradingProperty.getAuctionOrderTicksAwayFromNBBOForAllClasses(sessionName);
    }

    public LongClassStruct[] getAutoExEligibleStrategyTypesForAllClasses(String sessionName)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAutoExEligibleStrategyTypesForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
        return tradingProperty.getAutoExEligibleStrategyTypesForAllClasses(sessionName);
    }

    public AuctionBooleanClassStruct[] getAuctionEnabledForAllClasses(String sessionName)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionEnabledForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
        return tradingProperty.getAuctionEnabledForAllClasses(sessionName);
    }

    public AuctionOrderSizeTicksClassStruct[] getAuctionMinOrderSizeForTicksAboveNBBOForAllClasses(String sessionName)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAuctionMinOrderSizeForTicksAboveNBBOForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }
        return tradingProperty.getAuctionMinOrderSizeForTicksAboveNBBOForAllClasses(sessionName);
    }

    public void removeTradingProperty(String sessionName, int classKey, int propertyType)
            throws DataValidationException, CommunicationException, AuthorizationException, SystemException,
                   NotFoundException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(propertyType);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeTradingProperty",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        String messageText = TradingPropertyCentralLogger
                .getMessageTextForRemoveTradingProperties(sessionName, classKey, propertyType);

        tradingProperty.removeTradingProperty(sessionName, classKey, propertyType);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "removeTradingProperty", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setAllocationStrategies(String sessionName, int classKey,
                                        AllocationStrategyStructV2[] allocationStrategyStructV2s, int seq)
            throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException,
                   DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = allocationStrategyStructV2s;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAllocationStrategies",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.ALLOCATION_STRATEGY.getName();

        String oldAllocationStrategies = TradingPropertyCentralLogger.getFormattedTradingProperties(
                        sessionName, classKey, propertyName);

        tradingProperty.setAllocationStrategies(sessionName, classKey, allocationStrategyStructV2s, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldAllocationStrategies, sessionName, classKey, propertyName);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setAllocationStrategies", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setAllocationStrategy(String sessionName, int classKey, AllocationStrategyStruct strategy, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = strategy;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAllocationStrategy",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.ALLOCATION_STRATEGY.getName();

        String oldAllocationStrategy = TradingPropertyCentralLogger.
                getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setAllocationStrategy(sessionName, classKey, strategy, seq);

        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldAllocationStrategy, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setAllocationStrategy", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setBookDepthSize(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setBookDepthSize",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.BOOK_DEPTH_SIZE.getName();
        String oldBookDepthSize = TradingPropertyCentralLogger.getFormattedTradingProperties(sessionName, classKey,
                                                                                  propertyName);
        tradingProperty.setBookDepthSize(sessionName, classKey, size, seq);

        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldBookDepthSize, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setBookDepthSize", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setContinuousQuotePeriodForCredit(String sessionName, int period, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(period);
            argObj[2] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setContinuousQuotePeriodForCredit",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.CONTINUOUS_QUOTE_PERIOD_FOR_CREDIT.getName();
        String oldContinuousQuotePeriodForCredit = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, propertyName);
        tradingProperty.setContinuousQuotePeriodForCredit(sessionName, period, seq);

        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldContinuousQuotePeriodForCredit, sessionName, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setContinuousQuotePeriodForCredit", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setDPMParticipationPercentage(String sessionName, int classKey, double participationPercentage, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Double(participationPercentage);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setDPMParticipationPercentage",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.DPM_PARTICIPATION_PERCENTAGE.getName();
        String oldDPMParticipationPercentage = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setDPMParticipationPercentage(sessionName, classKey, participationPercentage, seq);

        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldDPMParticipationPercentage, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setDPMParticipationPercentage", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setExchangePrescribedWidth(String sessionName, int classKey, EPWStruct[] quoteCreditSpreadTable, int seq)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = quoteCreditSpreadTable;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setExchangePrescribedWidth",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.EPW_STRUCT.getName();
        String oldExchangePrescribedWidth = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setExchangePrescribedWidth(sessionName, classKey, quoteCreditSpreadTable, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldExchangePrescribedWidth, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setExchangePrescribedWidth", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setFastMarketSpreadMultiplier(String sessionName, int classKey, double multiplier, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Double(multiplier);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setFastMarketSpreadMultiplier",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.FAST_MARKET_SPREAD_MULTIPLIER.getName();
        String oldFastMarketSpreadMultiplier = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setFastMarketSpreadMultiplier(sessionName, classKey, multiplier, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldFastMarketSpreadMultiplier, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setFastMarketSpreadMultiplier", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setIPPMinSize(String sessionName, int classKey, int value, int seq)
            throws DataValidationException, TransactionFailedException, AuthorizationException, CommunicationException,
                   SystemException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(value);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setIPPMinSize", GUILoggerSABusinessProperty.TRADING_PROPERTY,
                                       argObj);
        }
        String propertyName = TradingPropertyTypeImpl.IPP_MIN_SIZE.getName();
        String oldIPPMinSize = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);
        tradingProperty.setIPPMinSize(sessionName, classKey, value, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldIPPMinSize, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setIPPMinSize", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setIPPTradeThroughFlag(String sessionName, int classKey, boolean value, int seq)
            throws DataValidationException, TransactionFailedException, AuthorizationException, CommunicationException,
                   SystemException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = Boolean.valueOf(value);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setIPPTradeThroughFlag",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.IPP_TRADE_THROUGH_FLAG.getName();
        String oldIPPTradeThroughFlag = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setIPPTradeThroughFlag(sessionName, classKey, value, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldIPPTradeThroughFlag, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setIPPTradeThroughFlag", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setMinSizeForBlockTrade(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setMinSizeForBlockTrade",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.MIN_SIZE_FOR_BLOCK_TRADE.getName();
        String oldMinSizeForBlockTrade = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setMinSizeForBlockTrade(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldMinSizeForBlockTrade, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setMinSizeForBlockTrade", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setContingencyTimeToLive(String s, int i, int i1, int i2) throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setMinQuoteCreditDefaultSize(String sessionName, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(size);
            argObj[2] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setMinQuoteCreditDefaultSize",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        int propertyType = TradingPropertyTypeImpl.MIN_QUOTE_CREDIT_DEFAULT_SIZE.getType();
        String oldMinQuoteCreditDefaultSize = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, propertyType);

        tradingProperty.setMinQuoteCreditDefaultSize(sessionName, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldMinQuoteCreditDefaultSize, sessionName, propertyType);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setMinQuoteCreditDefaultSize", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setOpeningPeriodTimeRange(String sessionName, TimeRangeStruct timePeriod, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = timePeriod;
            argObj[2] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setOpeningPeriodTimeRange",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.OPENING_TIME_PERIOD_RANGE.getName();
        String oldOpeningPeriodTimeRange = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, propertyName);

        tradingProperty.setOpeningPeriodTimeRange(sessionName, timePeriod, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldOpeningPeriodTimeRange, sessionName, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setOpeningPeriodTimeRange", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setOpeningPriceDelay(String sessionName, int delay, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(delay);
            argObj[2] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setOpeningPriceDelay",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.OPENING_PRICE_DELAY.getName();
        String oldOpeningPriceDelay = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, propertyName);

        tradingProperty.setOpeningPriceDelay(sessionName, delay, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldOpeningPriceDelay, sessionName, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setOpeningPriceDelay", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setOpeningPriceRate(String sessionName, int rate, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(rate);
            argObj[2] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setOpeningPriceRate",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.OPENING_PRICE_RATE.getName();
        String oldOpeningPriceRate = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, propertyName);

        tradingProperty.setOpeningPriceRate(sessionName, rate, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldOpeningPriceRate, sessionName, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setOpeningPriceRate", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setPreClosingTimePeriod(String sessionName, TimeRangeStruct timePeriod, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = timePeriod;
            argObj[2] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setPreClosingTimePeriod",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.PRECLOSING_TIME_PERIOD.getName();
        String oldPreClosingTimePeriod = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, propertyName);

        tradingProperty.setPreClosingTimePeriod(sessionName, timePeriod, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldPreClosingTimePeriod, sessionName, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setPreClosingTimePeriod", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setPrescribedWidthRatio(String sessionName, double ratio, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Double(ratio);
            argObj[2] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setPrescribedWidthRatio",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.PRESCRIBED_WIDTH_RATIO.getName();
        String oldPrescribedWidthRatio = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, propertyName);

        tradingProperty.setPrescribedWidthRatio(sessionName, ratio, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldPrescribedWidthRatio, sessionName, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setPrescribedWidthRatio", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setQuoteLockNotificationTimer(String sessionName, int classKey, int value, int seq)
            throws DataValidationException, TransactionFailedException, AuthorizationException, CommunicationException,
                   SystemException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(value);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setQuoteLockNotificationTimer",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.QUOTE_LOCK_NOTIFICATION_TIMER.getName();
        String oldQuoteLockNotificationTimer = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setQuoteLockNotificationTimer(sessionName, classKey, value, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldQuoteLockNotificationTimer, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setQuoteLockNotificationTimer", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setQuoteLockTimer(String sessionName, int classKey, int value, int seq)
            throws DataValidationException, TransactionFailedException, AuthorizationException, CommunicationException,
                   SystemException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(value);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setQuoteLockTimer",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.QUOTE_LOCK_TIMER.getName();
        String oldQuoteLockTimer = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setQuoteLockTimer(sessionName, classKey, value, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldQuoteLockTimer, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setQuoteLockTimer", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setQuoteTriggerTimer(String sessionName, int classKey, int value, int seq)
            throws DataValidationException, TransactionFailedException, AuthorizationException, CommunicationException,
                   SystemException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(value);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setQuoteTriggerTimer",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.QUOTE_TRIGGER_TIMER.getName();
        String oldQuoteTriggerTimer = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setQuoteTriggerTimer(sessionName, classKey, value, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldQuoteTriggerTimer, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setQuoteTriggerTimer", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setRFQResponseRatio(String sessionName, double ratio, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Double(ratio);
            argObj[2] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setRFQResponseRatio",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.RFQ_RESPONSE_RATIO.getName();
        String oldRFQResponseRatio = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, propertyName);

        tradingProperty.setRFQResponseRatio(sessionName, ratio, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldRFQResponseRatio, sessionName, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setRFQResponseRatio", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setRFQTimeout(String sessionName, int classKey, int timeout, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(timeout);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setRFQTimeout", GUILoggerSABusinessProperty.TRADING_PROPERTY,
                                       argObj);
        }
        String propertyName = TradingPropertyTypeImpl.RFQ_TIMEOUT.getName();
        String oldRFQTimeout = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setRFQTimeout(sessionName, classKey, timeout, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldRFQTimeout, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setRFQTimeout", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setETFFlag(String sessionName, int classKey, boolean flag, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = Boolean.valueOf(flag);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setETFFlag", GUILoggerSABusinessProperty.TRADING_PROPERTY,
                                       argObj);
        }
        String propertyName = TradingPropertyTypeImpl.ETF_FLAG.getName();
        String oldETFFlag = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setETFFlag(sessionName, classKey, flag, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldETFFlag, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setETFFlag", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setLotSize(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setLotSize", GUILoggerSABusinessProperty.TRADING_PROPERTY,
                                       argObj);
        }
        String propertyName = TradingPropertyTypeImpl.LOT_SIZE.getName();
        String oldLotSize = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setLotSize(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldLotSize, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setLotSize", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setFCQS(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException

    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setFCQS", GUILoggerSABusinessProperty.TRADING_PROPERTY,
                                       argObj);
        }
        String propertyName = TradingPropertyTypeImpl.FIRM_CUSTOMER_QUOTE_SIZE.getName();
        String oldFCQS = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setFCQS(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE, oldFCQS,
                                sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setFCQS", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setFPQS(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setFPQS", GUILoggerSABusinessProperty.TRADING_PROPERTY,
                                       argObj);
        }
        String propertyName = TradingPropertyTypeImpl.FIRM_PRINCIPAL_QUOTE_SIZE.getName();
        String oldFPQS = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setFPQS(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE, oldFPQS,
                                sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setFPQS", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setSOrderTimeToCreate(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setSOrderTimeToCreate",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.S_ORDER_TIME_TO_CREATE.getName();
        String oldSOrderTimeToCreate = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setSOrderTimeToCreate(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldSOrderTimeToCreate, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setSOrderTimeToCreate", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setSOrderTimeToCreateBeforeClose(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setSOrderTimeToCreateBeforeClose",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.S_ORDER_TIME_TO_CREATE_BEFORE_CLOSE.getName();
        String oldSOrderTimeToCreateBeforeClose = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setSOrderTimeToCreateBeforeClose(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldSOrderTimeToCreateBeforeClose, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setSOrderTimeToCreateBeforeClose", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setSOrderTimeToLive(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setSOrderTimeToLive",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.S_ORDER_TIME_TO_LIVE.getName();
        String oldSOrderTimeToLive = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setSOrderTimeToLive(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldSOrderTimeToLive, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setSOrderTimeToLive", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setSOrderTimeToRejectFill(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setSOrderTimeToRejectFill",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.S_ORDER_TIME_TO_REJECT_FILL.getName();
        String oldSOrderTimeToRejectFill = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setSOrderTimeToRejectFill(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldSOrderTimeToRejectFill, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setSOrderTimeToRejectFill", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setPOrderTimeToLive(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setPOrderTimeToLive",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.P_ORDER_TIME_TO_LIVE.getName();
        String oldPOrderTimeToLive = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setPOrderTimeToLive(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldPOrderTimeToLive, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setPOrderTimeToLive", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setPAOrderTimeToLive(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setPAOrderTimeToLive",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.PA_ORDER_TIME_TO_LIVE.getName();
        String oldPAOrderTimeToLive = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setPAOrderTimeToLive(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldPAOrderTimeToLive, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setPAOrderTimeToLive", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setIPPToleranceAmount(String sessionName, int classKey, double amount, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Double(amount);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setIPPToleranceAmount",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.IPP_TOLERANCE_AMOUNT.getName();
        String oldIPPToleranceAmount = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setIPPToleranceAmount(sessionName, classKey, amount, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldIPPToleranceAmount, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setIPPToleranceAmount", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setProductOpenProcedureType(String sessionName, int classKey, int productOpenProcedureType, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(productOpenProcedureType);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductOpenProcedureType",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.PRODUCT_OPEN_PROCEDURE_TYPE.getName();
        String oldProductOpenProcedureType = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setProductOpenProcedureType(sessionName, classKey, productOpenProcedureType, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldProductOpenProcedureType, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setProductOpenProcedureType", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setSatisfactionAlertFlag(String sessionName, int classKey, boolean flag, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = Boolean.valueOf(flag);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setSatisfactionAlertFlag",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.SATISFACTION_ALERT_FLAG.getName();
        String oldSatisfactionAlertFlag = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setSatisfactionAlertFlag(sessionName, classKey, flag, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldSatisfactionAlertFlag, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setSatisfactionAlertFlag", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setTradeType(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setTradeType", GUILoggerSABusinessProperty.TRADING_PROPERTY,
                                       argObj);
        }
        String propertyName = TradingPropertyTypeImpl.TRADE_TYPE.getName();
        String oldTradeType = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setTradeType(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldTradeType, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setTradeType", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setNeedsDpmQuoteToOpen(String sessionName, int classKey, boolean flag, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = Boolean.valueOf(flag);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setNeedsDpmQuoteToOpen",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.NEEDS_DPM_QUOTE_TO_OPEN.getName();
        String oldNeedsDpmQuoteToOpen = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setNeedsDpmQuoteToOpen(sessionName, classKey, flag, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldNeedsDpmQuoteToOpen, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setNeedsDpmQuoteToOpen", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setLinkageEnabledFlag(String sessionName, int classKey, boolean flag, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = Boolean.valueOf(flag);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setLinkageEnabledFlag",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.LINKAGE_ENABLED_FLAG.getName();
        String oldLinkageEnabledFlag = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setLinkageEnabledFlag(sessionName, classKey, flag, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldLinkageEnabledFlag, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setLinkageEnabledFlag", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public DpmRightsScaleStruct[] getDpmRightsScales(String sessionName, int classKey, IntHolder seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDpmRightsScales",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getDpmRightsScales(sessionName, classKey, seq);
    }

    public void setDpmRightsScales(String sessionName, int classKey, DpmRightsScaleStruct[] dpmRightsScaleStructs,
                                   int seq)
            throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException,
                   DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = dpmRightsScaleStructs;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setDpmRightsScales",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.DPM_RIGHTS_SCALES.getName();
        String oldDpmRightsScales = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setDpmRightsScales(sessionName, classKey, dpmRightsScaleStructs, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldDpmRightsScales, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setDpmRightsScales", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public double getDpmRightsSplitRate(String sessionName, int classKey, IntHolder seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDpmRightsSplitRate",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getDpmRightsSplitRate(sessionName, classKey, seq);

    }

    public void setDpmRightsSplitRate(String sessionName, int classKey, double value, int seq)
            throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException,
                   DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Double(value);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setDpmRightsSplitRate",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.DPM_RIGHTS_SPLIT_RATE.getName();
        String oldDpmRightsSplitRate = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setDpmRightsSplitRate(sessionName, classKey, value, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldDpmRightsSplitRate, sessionName,
                                classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setDpmRightsSplitRate", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public int getUMAEqualDistributionWeightForDPM(String sessionName, int classKey, IntHolder seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUMAEqualDistributionWeightForDPM",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getUMAEqualDistributionWeightForDPM(sessionName, classKey, seq);
    }

    public void setUMAEqualDistributionWeightForDPM(String sessionName, int classKey, int value, int seq)
            throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException,
                   DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(value);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setUMAEqualDistributionWeightForDPM",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.UMA_EQUAL_DISTRIBUTION_WEIGHT_FOR_DPM.getName();
        String oldUMAEqualDistributionWeightForDPM = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setUMAEqualDistributionWeightForDPM(sessionName, classKey, value, seq);

        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldUMAEqualDistributionWeightForDPM, sessionName, classKey,
                                propertyName);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setUMAEqualDistributionWeightForDPM", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public double getUMASplitRate(String sessionName, int classKey, IntHolder seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUMASplitRate",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getUMASplitRate(sessionName, classKey, seq);
    }

    public void setUMASplitRate(String sessionName, int classKey, double value, int seq)
            throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException,
                   DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Double(value);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setUMASplitRate",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.UMA_SPLIT_RATE.getName();
        String oldUMASplitRate = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setUMASplitRate(sessionName, classKey, value, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldUMASplitRate, sessionName, classKey, propertyName);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setUMASplitRate", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setAuctionTimeToLive(String sessionName, int classKey, AuctionRangeStruct[] timeRange, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = timeRange;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAuctionTimeToLive",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.AUCTION_TIME_TO_LIVE.getName();
        String oldAuctionTimeToLive = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setAuctionTimeToLive(sessionName, classKey, timeRange, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldAuctionTimeToLive, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setAuctionTimeToLive", argObj);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setAuctionMinPriceIncrement(String sessionName, int classKey, AuctionLongStruct[] increment, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = increment;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAuctionMinPriceIncrement",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.AUCTION_MIN_PRICE_INCREMENT.getName();
        String oldAuctionMinPriceIncrement = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setAuctionMinPriceIncrement(sessionName, classKey, increment, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldAuctionMinPriceIncrement, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setAuctionMinPriceIncrement", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setAuctionMinQuoters(String sessionName, int classKey, AuctionLongStruct[] quoters, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = quoters;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAuctionMinQuoters",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.AUCTION_MIN_QUOTERS.getName();
        String oldAuctionMinQuoters = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setAuctionMinQuoters(sessionName, classKey, quoters, seq);

        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldAuctionMinQuoters, sessionName, classKey, propertyName);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setAuctionMinQuoters", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setAuctionReceiverTypes(String sessionName, int classKey, AuctionLongStruct[] types, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = types;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAuctionReceiverTypes",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.AUCTION_RECEIVER_TYPES.getName();
        String oldAuctionReceiverTypes = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setAuctionReceiverTypes(sessionName, classKey, types, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldAuctionReceiverTypes, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setAuctionReceiverTypes", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setInternalizationGuaranteedPercentage(String sessionName, int classKey,
                                                       InternalizationPercentageStruct[] percentage, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = percentage;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setInternalizationGuaranteedPercentage",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.INTERNALIZATION_GUARANTEED_PERCENTAGE.getName();
        String oldIGP = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setInternalizationGuaranteedPercentage(sessionName, classKey, percentage, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE, oldIGP,
                                sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setInternalizationGuaranteedPercentage", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setAuctionOrderTicksAwayFromNBBO(String sessionName, int classKey, AuctionLongStruct[] size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = size;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAuctionOrderTicksAwayFromNBBO",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.AUCTION_ORDER_TICKS_AWAY_FROM_NBBO.getName();
        String oldAuctionOrderTicksAwayFromNBBO = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setAuctionOrderTicksAwayFromNBBO(sessionName, classKey, size, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldAuctionOrderTicksAwayFromNBBO, sessionName, classKey,
                                propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setAuctionOrderTicksAwayFromNBBO", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

   public void setAutoExEligibleStrategyTypes(String sessionName, int classKey, int[] types, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = types;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAutoExEligibleStrategyTypes",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.AUTO_EX_ELIGIBLE_STRATEGY_TYPES.getName();
        String oldAutoExEligibleStrategyTypes = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setAutoExEligibleStrategyTypes(sessionName, classKey, types, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldAutoExEligibleStrategyTypes, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setAutoExEligibleStrategyTypes", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setAuctionEnabled(String sessionName, int classKey, AuctionBooleanStruct[] enabled, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = enabled;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAuctionEnabled",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.AUCTION_ENABLED.getName();
        String oldAuctionEnabled = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setAuctionEnabled(sessionName, classKey, enabled, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldAuctionEnabled, sessionName, classKey, propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setAuctionEnabled", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public void setAuctionMinOrderSizeForTicksAboveNBBO(String sessionName, int classKey,
                                                        AuctionOrderSizeTicksStruct[] sizeTicks, int seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = sizeTicks;
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAuctionMinOrderSizeForTicksAboveNBBO",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }
        String propertyName = TradingPropertyTypeImpl.AUCTION_MIN_ORDER_SIZE_FOR_TICKS_ABOVE_NBBO.getName();
        String oldAuctionMinOrderSizeForTicksAboveNBBO = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setAuctionMinOrderSizeForTicksAboveNBBO(sessionName, classKey, sizeTicks, seq);
        String messageText = TradingPropertyCentralLogger
                .getMessageText(TradingPropertyCentralLogger.OPERATION_ADD_UPDATE,
                                oldAuctionMinOrderSizeForTicksAboveNBBO, sessionName, classKey,
                                propertyName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setAuctionMinOrderSizeForTicksAboveNBBO", argObj);
            GUILoggerHome.find().nonRepudiationAudit(
                    TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public PropertyGroupStruct setProperties(PropertyGroupStruct properties)
            throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProperties INPUT",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, properties);
        }

        boolean doSAGUICentralLogging = false;
        if(null != properties && null != properties.category && PropertyCategoryTypes.TRADING_PROPERTIES.equals(properties.category))
        {
            doSAGUICentralLogging = true;
        }

        String oldProperties = null;
        if(doSAGUICentralLogging)
        {
            try
            {
                PropertyGroupStruct oldPropertiesStruct =
                        getProperties(properties.category, properties.propertyKey);
                oldProperties = TradingPropertyCentralLogger
                        .getFormattedTradingProperties(oldPropertiesStruct);
            }
            catch(NotFoundException nfe)
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProperties INPUT",
                                           GUILoggerSABusinessProperty.PROPERTY_SERVICE,
                                           "getProperties() for SAGUICentralLogging failed :" +
                                           nfe);
                oldProperties = TradingPropertyCentralLogger.NONE;
            }
        }

        PropertyGroupStruct returnStructs = propertyService.setProperties(properties);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PROPERTY_SERVICE),
                                       "setProperties", properties);
            if(doSAGUICentralLogging)
            {
                String messageText = TradingPropertyCentralLogger.getMessageText(
                        TradingPropertyCentralLogger.OPERATION_ADD_UPDATE, oldProperties,
                        returnStructs);
                GUILoggerHome.find().nonRepudiationAudit(
                        TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
            }
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProperties OUTPUT",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, returnStructs);
        }

        return returnStructs;
    }

    public PropertyGroupStruct[] getPropertiesForPartialKey(String category, String partialPropertyKey,
                                                            short partialPropertyKeyType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[3];
            argObj[0] = category;
            argObj[1] = partialPropertyKey;
            argObj[2] = new Short(partialPropertyKeyType);
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPropertiesForPartialKey",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        return propertyService.getPropertiesForPartialKey(category, partialPropertyKey, partialPropertyKeyType);
    }

    public PropertyGroupStruct getProperties(String category, String propertyKey)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[2];
            argObj[0] = category;
            argObj[1] = propertyKey;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProperties",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        return propertyService.getProperties(category, propertyKey);
    }

    public String[] getPropertyKeys(String category)
            throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            Object[] argObj = new Object[1];
            argObj[0] = category;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPropertyKeys",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        return propertyService.getPropertyKeys(category);
    }

    public void removeProperties(String category, String propertyKey)
            throws SystemException, CommunicationException, AuthorizationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = category;
            argObj[1] = propertyKey;
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeProperties",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, argObj);
        }

        boolean doSAGUICentralLogging = false;
        if(category != null && PropertyCategoryTypes.TRADING_PROPERTIES.equals(category))
        {
            doSAGUICentralLogging = true;
        }

        String messageText = null;
        if(doSAGUICentralLogging)
        {
            try
            {
                PropertyGroupStruct struct = getProperties(category, propertyKey);
                messageText =
                        TradingPropertyCentralLogger.getMessageTextForRemoveProperties(struct);
            }
            catch(NotFoundException nfe)
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeProperties",
                                           GUILoggerSABusinessProperty.PROPERTY_SERVICE,
                                           "getProperties() for SAGUICentralLogging failed :" + nfe);
                messageText = TradingPropertyCentralLogger.NONE;
            }
        }

        propertyService.removeProperties(category, propertyKey);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PROPERTY_SERVICE),
                                       "removeProperties", argObj);
            if(doSAGUICentralLogging)
            {
                GUILoggerHome.find().nonRepudiationAudit(
                        TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
            }
        }
    }

    public void subscribePropertyService(String category, EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
           GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribePropertyService(String, EventChannelListener)",
                                      GUILoggerSABusinessProperty.PROPERTY_SERVICE, clientListener);
        }

        ChannelKey updateKey = new ChannelKey(ChannelType.UPDATE_PROPERTY, category);
        int key1 = SubscriptionManagerFactory.find().subscribe(updateKey, clientListener, propertyServiceConsumer);
        ChannelKey removeKey = new ChannelKey(ChannelType.REMOVE_PROPERTY, category);
        int key2 = SubscriptionManagerFactory.find().subscribe(removeKey, clientListener, propertyServiceConsumer);
        if (key1 == 1 && key2 == 1)
        {
            propertyService.subscribePropertyEvent(propertyServiceConsumer);
        }
    }

    public void unsubscribePropertyService(String category, EventChannelListener clientListener)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":unsubscribePropertyService(String, EventChannelListener)",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, clientListener);
        }

        ChannelKey updateKey = new ChannelKey(ChannelType.UPDATE_PROPERTY, category);
        int key1 = SubscriptionManagerFactory.find().unsubscribe(updateKey, clientListener, propertyServiceConsumer);
        ChannelKey removeKey = new ChannelKey(ChannelType.REMOVE_PROPERTY, category);
        int key2 = SubscriptionManagerFactory.find().unsubscribe(removeKey, clientListener, propertyServiceConsumer);
        if (key1 == 0 && key2 == 0)
        {
            propertyService.unsubscribePropertyEvent(propertyServiceConsumer);
        }
    }

    public void subscribePropertyService(String category, String propertyKey, EventChannelListener clientListener)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
           GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribePropertyService(String, String, EventChannelListener)",
                                      GUILoggerSABusinessProperty.PROPERTY_SERVICE, clientListener);
        }

        ChannelKey updateKey = new ChannelKey(ChannelType.UPDATE_PROPERTY, propertyKey + category);
        int key1 = SubscriptionManagerFactory.find().subscribe(updateKey, clientListener, propertyServiceConsumer);
        ChannelKey removeKey = new ChannelKey(ChannelType.REMOVE_PROPERTY, propertyKey + category);
        int key2 = SubscriptionManagerFactory.find().subscribe(removeKey, clientListener, propertyServiceConsumer);
        if (key1 == 1 && key2 == 1)
        {
            propertyService.subscribePropertyEvent(propertyServiceConsumer);
        }
    }

    public void unsubscribePropertyService(String category, String propertyKey, EventChannelListener clientListener)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PROPERTY_SERVICE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":unsubscribePropertyService(String, String, EventChannelListener)",
                                       GUILoggerSABusinessProperty.PROPERTY_SERVICE, clientListener);
        }

        ChannelKey updateKey = new ChannelKey(ChannelType.UPDATE_PROPERTY, propertyKey + category);
        int key1 = SubscriptionManagerFactory.find().unsubscribe(updateKey, clientListener, propertyServiceConsumer);
        ChannelKey removeKey = new ChannelKey(ChannelType.REMOVE_PROPERTY, propertyKey + category);
        int key2 = SubscriptionManagerFactory.find().unsubscribe(removeKey, clientListener, propertyServiceConsumer);
        if (key1 == 0 && key2 == 0)
        {
            propertyService.unsubscribePropertyEvent(propertyServiceConsumer);
        }
    }

    public int createSession(String p0, String p1, String p2)
            throws CommunicationException, DataValidationException, SystemException, AuthorizationException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = p1;
            argObj[2] = p2;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":createSession", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, argObj);
        }

        int retVal = sessionManagement.createSession(p0, p1, p2);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SESSION_MANAGEMENT),
                                       "createSession", argObj);
        }
        return retVal;
    }

    public void joinSession(String p0, String p1, String p2, int p3)
            throws CommunicationException, DataValidationException, SystemException, AuthorizationException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = p0;
            argObj[1] = p1;
            argObj[2] = p2;
            argObj[3] = new Integer(p2);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":joinSession", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, argObj);
        }

        sessionManagement.joinSession(p0, p1, p2, p3);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SESSION_MANAGEMENT),
                                       "joinSession", argObj);
        }
    }

    public void leaveSession(String p0, int p1)
            throws CommunicationException, DataValidationException, SystemException, AuthorizationException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":leaveSession", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, argObj);
        }

        sessionManagement.leaveSession(p0, p1);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SESSION_MANAGEMENT),
                                       "leaveSession", argObj);
        }
    }

    public void closeSession(int p0)
            throws CommunicationException, DataValidationException, SystemException, AuthorizationException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":closeSession", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, new Integer(p0));
        }

        sessionManagement.closeSession(p0);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SESSION_MANAGEMENT),
                                       "closeSession", new Integer(p0));
        }
    }

    public int getSessionByUserId(String userId)
            throws NotFoundException, CommunicationException, DataValidationException, SystemException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSessionByUserId", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, userId);
        }

        long startTime = System.currentTimeMillis();
        int retVal = sessionManagement.getSessionByUserId(userId);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            long totTime = System.currentTimeMillis() - startTime;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSessionByUserId", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, "Milliseconds elapsed for SMS API call = " + totTime);
        }
        return retVal;
    }

    public UserLoginStruct[] getUsersForSourceComponent(String sourceComponentName, boolean onlyLoggedIn)
            throws CommunicationException, DataValidationException, SystemException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sourceComponentName;
            argObj[1] = new Boolean(onlyLoggedIn);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUsersForSourceComponent", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, argObj);
        }

        long startTime = System.currentTimeMillis();
        UserLoginStruct[] structs = sessionManagement.getUsersForSourceComponent(sourceComponentName, onlyLoggedIn);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            long totTime = System.currentTimeMillis() - startTime;
            StringBuffer sb = new StringBuffer();
            sb.append(TRANSLATOR_NAME).append(":getUsersForSourceComponent('").append(sourceComponentName).append("')");
            GUILoggerHome.find().debug(sb.toString(), GUILoggerSABusinessProperty.SESSION_MANAGEMENT, "Milliseconds elapsed for SMS API call = " + totTime);
        }
        return structs;
    }

    public UserLoginStruct[] getUsersForSession(int sessionKey)
            throws CommunicationException, DataValidationException, SystemException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUsersForSession", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, new Integer(sessionKey));
        }

        long startTime = System.currentTimeMillis();
        UserLoginStruct[] structs = sessionManagement.getUsersForSession(sessionKey);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            long totTime = System.currentTimeMillis() - startTime;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUsersForSession", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, "Milliseconds elapsed for SMS API call = " + totTime);
        }
        return structs;
    }

    public UserLoginStruct[] getUsers(boolean onlyLoggedIn)
            throws CommunicationException, SystemException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUsers", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, new Boolean(onlyLoggedIn));
        }

        long startTime = System.currentTimeMillis();
        UserLoginStruct[] structs = sessionManagement.getUsers(onlyLoggedIn);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            long totTime = System.currentTimeMillis() - startTime;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUsers", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, "Milliseconds elapsed for SMS API call = " + totTime);
        }
        return structs;
    }

//SessionManagementAdminService
//

    public void forceLeaveSession(int sessionKey, String sourceComponentName, String message)
            throws CommunicationException, DataValidationException, SystemException, AuthorizationException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = new Integer(sessionKey);
            argObj[1] = sourceComponentName;
            argObj[2] = message;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":forceLeaveSession", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, argObj);
        }

        sessionManagement.forceLeaveSession(sessionKey, sourceComponentName, message);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SESSION_MANAGEMENT),
                                       "forceLeaveSession", argObj);
        }
    }

    public void forceCloseSession(int sessionKey, String message)
            throws CommunicationException, DataValidationException, SystemException, AuthorizationException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = new Integer(sessionKey);
            argObj[1] = message;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":forceCloseSession", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, argObj);
        }

        if(sessionKey != 0)
        {
            sessionManagement.forceCloseSession(sessionKey, message);
        }
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SESSION_MANAGEMENT),
                                       "forceCloseSession", argObj);
        }
    }

    public void forceUserLogout(String userId, String message)
            throws CommunicationException, DataValidationException, SystemException, AuthorizationException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = userId;
            argObj[1] = message;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":forceUserLogout", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, argObj);
        }

        if(userId != null)
        {
            sessionManagement.forceUserLogout(userId, message);
        }
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SESSION_MANAGEMENT),
                                       "forceUserLogout", argObj);
        }
    }

    public ComponentStruct[] getComponentsForType(int p0)
        throws CommunicationException, DataValidationException, SystemException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getComponentsForType", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, new Integer(p0));
        }

        long startTime = System.currentTimeMillis();
        ComponentStruct[] structs = sessionManagement.getComponentsForType(p0);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            long totTime = System.currentTimeMillis() - startTime;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getComponentsForType", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, "Milliseconds elapsed for SMS API call = " + totTime);
        }
        return structs;
    }

    public ComponentStruct[] getConnectedComponents(String p0, int p1)
        throws CommunicationException, DataValidationException, SystemException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = new Integer(p1);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getConnectedComponents", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, argObj);
        }

        long startTime = System.currentTimeMillis();
        ComponentStruct[] structs = sessionManagement.getConnectedComponents(p0, p1);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            long totTime = System.currentTimeMillis() - startTime;
            StringBuffer sb = new StringBuffer();
            sb.append(TRANSLATOR_NAME).append(":getConnectedComponents('").append(p0).append("')");
            GUILoggerHome.find().debug(sb.toString(), GUILoggerSABusinessProperty.SESSION_MANAGEMENT, "Milliseconds elapsed for SMS API call = " + totTime);
        }
        return structs;
    }

    public ComponentStruct[] getSourceComponentsForUser(String userId)
        throws DataValidationException, CommunicationException, SystemException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSourceComponentsForUser", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, userId);
        }

        long startTime = System.currentTimeMillis();
        ComponentStruct[] structs = sessionManagement.getSourceComponentsForUser(userId);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            long totTime = System.currentTimeMillis() - startTime;
            StringBuffer sb = new StringBuffer();
            sb.append(TRANSLATOR_NAME).append(":getSourceComponentsForUser('").append(userId).append("')");
            GUILoggerHome.find().debug(sb.toString(), GUILoggerSABusinessProperty.SESSION_MANAGEMENT, "Milliseconds elapsed for SMS API call = " + totTime);
        }
        return structs;
    }

    public ComponentStruct getComponent(String component)
        throws NotFoundException, CommunicationException, DataValidationException, SystemException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getComponent", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, component);
        }

        long startTime = System.currentTimeMillis();
        ComponentStruct struct = sessionManagement.getComponent(component);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            long totTime = System.currentTimeMillis() - startTime;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getComponent", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, "Milliseconds elapsed for SMS API call = " + totTime);
        }
        return struct;
    }

    public ComponentStruct[] getReferencingComponents(String component)
        throws CommunicationException, DataValidationException, SystemException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getReferencingComponents", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, component);
        }

        long startTime = System.currentTimeMillis();
        ComponentStruct[] structs = sessionManagement.getReferencingComponents(component);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.SESSION_MANAGEMENT))
        {
            long totTime = System.currentTimeMillis() - startTime;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getReferencingComponents", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, "Milliseconds elapsed for SMS API call = " + totTime);
        }
        return structs;
    }

//SecurityAdminService
//

    public String getErrorMessage()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getErrorMessage", GUILoggerSABusinessProperty.SECURITY_ADMIN, "");
        }

        return securityAdmin.getErrorMessage();
    }

    public boolean addGroup(String p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addGroup", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        boolean retVal = securityAdmin.addGroup(p0);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SECURITY_ADMIN),
                                       "addGroup", p0);
        }

        return retVal;
    }

    public boolean addRole(String p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addRole", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        boolean retVal = securityAdmin.addRole(p0);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SECURITY_ADMIN),
                                       "addRole", p0);
        }
        return retVal;
    }

    public boolean addService(String p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addService", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        boolean retVal = securityAdmin.addService(p0);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SECURITY_ADMIN),
                                       "addService", p0);
        }
        return retVal;
    }

    public String[] getGroups()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getGroups", GUILoggerSABusinessProperty.SECURITY_ADMIN, "");
        }

        return securityAdmin.getGroups();
    }

    public String[] getRoles()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getRoles", GUILoggerSABusinessProperty.SECURITY_ADMIN, "");
        }

        return securityAdmin.getRoles();
    }

    public String[] getServices()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getServices", GUILoggerSABusinessProperty.SECURITY_ADMIN, "");
        }

        return securityAdmin.getServices();
    }

    public String[] getACLSet(String p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getACLSet", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        return securityAdmin.getACLSet(p0);
    }

    public String[] getRoleSet(String p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getRoleSet", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        return securityAdmin.getRoleSet(p0);
    }

    public String[] getUsers(String p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUsers", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        return securityAdmin.getUsers(p0);
    }

    public boolean updateACLSet(String[] p0, String p1)
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateACLSet", GUILoggerSABusinessProperty.SECURITY_ADMIN, argObj);
        }

        boolean retVal = securityAdmin.updateACLSet(p0, p1);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SECURITY_ADMIN),
                                       "updateACLSet", argObj);
        }
        return retVal;
    }

    public boolean updateRoleSet(String[] p0, String p1)
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateRoleSet", GUILoggerSABusinessProperty.SECURITY_ADMIN, argObj);
        }

        boolean retVal = securityAdmin.updateRoleSet(p0, p1);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SECURITY_ADMIN),
                                       "updateRoleSet", argObj);
        }
        return retVal;
    }

    public boolean addAccount(MemberAccountStruct p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addAccount", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        boolean retVal = securityAdmin.addAccount(p0);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SECURITY_ADMIN),
                                       "addAccount", p0);
        }
        return retVal;
    }

    public String getMemberName(String p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getMemberName", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        return securityAdmin.getMemberName(p0);
    }

    public MemberAccountStruct getAccount(String p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAccount", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        return securityAdmin.getAccount(p0);
    }

    public boolean updateAccount(MemberAccountStruct p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateAccount", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        boolean retVal = securityAdmin.updateAccount(p0);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SECURITY_ADMIN),
                                       "updateAccount", p0);
        }
        return retVal;
    }

    public boolean deleteAccount(MemberAccountStruct p0)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":deleteAccount", GUILoggerSABusinessProperty.SECURITY_ADMIN, p0);
        }

        boolean retVal = securityAdmin.deleteAccount(p0);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.SECURITY_ADMIN),
                                       "deleteAccount", p0);
        }
        return retVal;
    }

//TextMessagingService
//

    /**
     * Send a message to a user and/or group
     *
     * @usage Send a message to a user and/or group
     * @returns sent message's messageId
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public MessageResultStruct sendMessage(DestinationStruct[] receipients, MessageTransportStruct message)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = receipients;
            argObj[1] = message;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":sendMessage", GUILoggerSABusinessProperty.TEXT_MESSAGE, argObj);
        }

        message.message.sender = getSACASUserName();
        MessageResultStruct struct = textMessaging.sendMessage(receipients, message);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TEXT_MESSAGE),
                                       "sendMessage", argObj);
        }
        return struct;
    }

    public void updateMessageState(int messageId, int state)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = new Integer(messageId);
            argObj[1] = new Integer(state);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateMessageState", GUILoggerSABusinessProperty.TEXT_MESSAGE, argObj);
        }

        textMessaging.updateMessageState(messageId, state);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TEXT_MESSAGE),
                                       "updateMessageState", argObj);
        }
    }

    /**
     * Request any/all waiting messages be delivered via consumer
     *
     * @usage Request any/all waiting messages be delivered via consumer
     * @returns nothing
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public void publishMessagesForUser(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishMessagesForUser", GUILoggerSABusinessProperty.TEXT_MESSAGE, userId);
        }

        textMessaging.publishMessagesForUser(userId);
    }

    /**
     * Request any/all waiting messages be delivered via consumer
     *
     * @usage Request any/all waiting messages be delivered via consumer
     * @returns nothing
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public void publishMessagesForProductClass(int classKey, String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(classKey);
            argObj[1] = userId;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishMessagesForProductClass", GUILoggerSABusinessProperty.TEXT_MESSAGE, argObj);
        }

        textMessaging.publishMessagesForProductClass(classKey, userId);
    }

    /**
     * Request any/all waiting messages be delivered via consumer
     *
     * @usage Request any/all waiting messages be delivered via consumer
     * @returns nothing
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public void publishMessagesForProductType(short productType, String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Short(productType);
            argObj[1] = userId;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishMessagesForProductType", GUILoggerSABusinessProperty.TEXT_MESSAGE, argObj);
        }

        textMessaging.publishMessagesForProductType(productType, userId);
    }

    /**
     * Request all messages from the user's mailbox
     *
     * @usage Request all messages from the user's mailbox
     * @returns sequence of MesageTransportStructs ( messages )
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public MessageTransportStruct[] getMailboxForUser(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getMailboxForUser", GUILoggerSABusinessProperty.TEXT_MESSAGE, userId);
        }

        return textMessaging.getMailboxForUser(userId);
    }


    /**
     * create a new message text template
     *
     * @param templateName - name of template
     * @param templateText - text to save
     * @return nothing
     * @raise SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public void createTemplate(String templateName, String templateText)
        throws SystemException, CommunicationException, AuthorizationException, AlreadyExistsException, TransactionFailedException
    {
        StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            buffer = new StringBuffer("createTemplate");
            buffer.append(" Template Name: ").append(templateName);
            buffer.append(" Template Text: ").append(templateText);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":createTemplate", GUILoggerSABusinessProperty.TEXT_MESSAGE, buffer.toString());
        }

        textMessaging.createTemplate(templateName, templateText);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TEXT_MESSAGE),
                                       buffer.toString(), "");
        }
    }

    /**
     * delete a message text template
     *
     * @parm    templateName    - name of template
     * @returns nothing
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public void deleteTemplate(String templateName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":deleteTemplate", GUILoggerSABusinessProperty.TEXT_MESSAGE, templateName);
        }

        textMessaging.deleteTemplate(templateName);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TEXT_MESSAGE),
                                       "deleteTemplate", templateName);
        }
    }

    /**
     * get all available message text templates
     *
     * @returns sequence of template names
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public String[] getAvailableTemplates()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAvailableTemplates", GUILoggerSABusinessProperty.TEXT_MESSAGE, "");
        }

        return textMessaging.getAvailableTemplates();
    }

    /**
     * get the text for a named message template
     *
     * @parm    templateName    - name of template
     * @returns String          - template text
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public String getTemplateText(String templateName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTemplateText", GUILoggerSABusinessProperty.TEXT_MESSAGE, templateName);
        }

        return textMessaging.getTemplateText(templateName);
    }

    /**
     * update/edit a message text template
     *
     * @parm    templateName    - name of template
     * @parm    templateText    - text to update with
     * @returns nothing
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public void updateTemplate(String templateName, String templateText)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            buffer = new StringBuffer("updateTemplate");
            buffer.append(" Template Name: ").append(templateName);
            buffer.append(" Template Text: ").append(templateText);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateTemplate", GUILoggerSABusinessProperty.TEXT_MESSAGE, buffer.toString());
        }

        textMessaging.updateTemplate(templateName, templateText);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TEXT_MESSAGE),
                                       buffer.toString(), "");
        }
    }

    // UserTradinParameterService
    //
    public UserQuoteRiskManagementProfileStruct getAllQuoteRiskProfiles(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllQuoteRiskProfiles", GUILoggerSABusinessProperty.QRM, userId);
        }

        return userTradingParameterService.getAllQuoteRiskProfiles(userId);
    }

    public QuoteRiskManagementProfileStruct getQuoteRiskManagementProfileByClass(String userId, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = userId;
            argObj[1] = new Integer(classKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getQuoteRiskManagementProfileByClass", GUILoggerSABusinessProperty.QRM, argObj);
        }

        return userTradingParameterService.getQuoteRiskManagementProfileByClass(userId, classKey);
    }

    public QuoteRiskManagementProfileStruct getDefaultQuoteRiskProfile(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDefaultQuoteRiskProfile", GUILoggerSABusinessProperty.QRM, userId);
        }

        return userTradingParameterService.getDefaultQuoteRiskProfile(userId);
    }

    public void setQuoteRiskManagementEnabledStatus(String userId, boolean status)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException
    {
        StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            buffer = new StringBuffer("setQuoteRiskManagementEnabledStatus");
            buffer.append(" User ID: ").append(userId);
            buffer.append(" Status: ").append(status);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setQuoteRiskManagementEnabledStatus", GUILoggerSABusinessProperty.QRM, buffer.toString());
        }

        userTradingParameterService.setQuoteRiskManagementEnabledStatus(userId, status);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.QRM),
                                       buffer.toString(), "");
        }
    }

    public boolean getQuoteRiskManagementEnabledStatus(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getQuoteRiskManagementEnabledStatus", GUILoggerSABusinessProperty.QRM, userId);
        }

        return userTradingParameterService.getQuoteRiskManagementEnabledStatus(userId);
    }

    public UserAdminQuoteRiskManagementProfileStruct[] getUserProfiles()
        throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":UserAdminQuoteRiskManagementProfileStruct", GUILoggerSABusinessProperty.QRM, "");
        }

        return userTradingParameterService.getUserProfiles();
    }

    public void removeAllQuoteRiskProfiles(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            buffer = new StringBuffer("removeAllQuoteRiskProfiles");
            buffer.append(" for User ID: ").append(userId);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeAllQuoteRiskProfiles", GUILoggerSABusinessProperty.QRM, buffer.toString());
        }

        userTradingParameterService.removeAllQuoteRiskProfiles(userId);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.QRM),
                                       buffer.toString(), "");
        }
    }

    public void removeQuoteRiskProfile(String userId, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            buffer = new StringBuffer("removeQuoteRiskProfile");
            buffer.append(" for User ID: ").append(userId);
            buffer.append(" Class Key: ").append(classKey);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeQuoteRiskProfile", GUILoggerSABusinessProperty.QRM, buffer.toString());
        }

        userTradingParameterService.removeQuoteRiskProfile(userId, classKey);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.QRM),
                                       buffer.toString(), "");
        }
    }

    public void setQuoteRiskProfile(String userId, QuoteRiskManagementProfileStruct quoteRiskManagementProfile)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = userId;
            argObj[1] = quoteRiskManagementProfile;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setQuoteRiskProfile", GUILoggerSABusinessProperty.QRM, argObj);
        }

        userTradingParameterService.setQuoteRiskProfile(userId, quoteRiskManagementProfile);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.QRM),
                                       "setQuoteRiskProfile", argObj);
        }
    }

    private String getSACASUserName()
    {
        return SACAS_USER_NAME_DEFAULT;
    }

    public ExchangeFirm[] getFirms(boolean active, boolean clearingFirms)
            throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Boolean(active);
            argObj[1] = new Boolean(clearingFirms);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFirms", GUILoggerSABusinessProperty.FIRM_MAINTENANCE, argObj);
        }

        ExchangeFirmStruct[] firmStructs = firmService.getFirms(active, clearingFirms);

        // wrap structs in ExchangeFirm
        ExchangeFirm[] firms = new ExchangeFirm[firmStructs.length];
        for (int i = 0; i < firmStructs.length; i++)
        {
            firms[i] = ExchangeFirmFactory.createExchangeFirm(firmStructs[i]);
        }

        return firms;
    }

    public FirmModel getFirmByKey(int firmKey)
           throws NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.FIRM_MAINTENANCE))
        {
            Integer arg = new Integer(firmKey);
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFirmByKey", GUILoggerSABusinessProperty.FIRM_MAINTENANCE,
                                       arg);
        }
        return firmCache.getFirmByKey(firmKey);
    }

    public FirmModel getFirmByNumber(ExchangeFirm firmNumber)
           throws DataValidationException, NotFoundException
    {
        ExchangeFirmStruct struct = firmNumber.getExchangeFirmStruct();

        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.FIRM_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFirmByNumber",
                                       GUILoggerSABusinessProperty.FIRM_MAINTENANCE, struct);
        }
        return firmCache.getFirmByNumber(struct);
    }

    public FirmModel getFirmByAcronym(String firmAcronym, String exchangeAcr)
           throws DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.FIRM_MAINTENANCE))
        {
            Object[] argObj = {firmAcronym, exchangeAcr};
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFirmByAcronym",
                                       GUILoggerSABusinessProperty.FIRM_MAINTENANCE, argObj);
        }
        return firmCache.getFirmByAcronym(firmAcronym, exchangeAcr);
    }

    public FirmModel[] getFirms()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFirms", GUILoggerSABusinessProperty.FIRM_MAINTENANCE);
        }
        return firmCache.getFirms();
    }

    public FirmModel[] getActiveFirms()
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getActiveFirms", GUILoggerSABusinessProperty.FIRM_MAINTENANCE);
        }
        return firmCache.getActiveFirms();
    }

    public void addFirm(FirmModel newFirm)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                  TransactionFailedException, AlreadyExistsException
    {
        FirmStruct struct = newFirm.getFirmStruct();

        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.FIRM_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addFirm", GUILoggerSABusinessProperty.FIRM_MAINTENANCE,
                                       struct);
        }
        firmMaintenance.addFirm(struct);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.FIRM_MAINTENANCE),
                                       "addFirm", struct);
        }
    }

    public void updateFirm(FirmModel updatedFirm)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                  TransactionFailedException
    {
        FirmStruct struct = updatedFirm.getFirmStruct();

        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.FIRM_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateFirm", GUILoggerSABusinessProperty.FIRM_MAINTENANCE,
                                       struct);
        }
        firmMaintenance.updateFirm(struct);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.FIRM_MAINTENANCE),
                                       "updateFirm", struct);
        }
    }

// MarketMakerQuoteService

    public void acceptQuote(QuoteStruct p0)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptQuote", GUILoggerSABusinessProperty.MM_QUOTE, p0);
        }

        marketMakerQuoteService.acceptQuote(p0);
    }

    public ClassQuoteResultStruct[] acceptQuotesForClass(int p0, QuoteStruct[] p1)
        throws SystemException, CommunicationException, DataValidationException, NotAcceptedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(p0);
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptQuotesForClass", GUILoggerSABusinessProperty.MM_QUOTE, argObj);
        }

        return marketMakerQuoteService.acceptQuotesForClass(p0, p1);
    }

    public void cancelAllQuotes(String userId, String sessionName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = userId;
            argObj[1] = sessionName;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":cancelAllQuotes", GUILoggerSABusinessProperty.MM_QUOTE, argObj);
        }

        marketMakerQuoteService.cancelAllQuotes(userId, sessionName);
    }

    public void cancelQuote(String userId, String sessionName, int productKey)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = userId;
            argObj[1] = sessionName;
            argObj[2] = new Integer(productKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":cancelQuote", GUILoggerSABusinessProperty.MM_QUOTE, argObj);
        }

        marketMakerQuoteService.cancelQuote(userId, sessionName, productKey);
    }

    public void cancelQuotesByClass(String userId, String sessionName, int[] classKeys)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            Object[] arrayClassKeys = new Object[classKeys.length];

            argObj[0] = userId;
            argObj[1] = sessionName;

            for (int i = 0; i < classKeys.length; i++)
            {
                arrayClassKeys[i] = new Integer(classKeys[i]);
            }

            argObj[2] = arrayClassKeys;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":cancelQuotesByClas", GUILoggerSABusinessProperty.MM_QUOTE, argObj);
        }

        marketMakerQuoteService.cancelQuotesByClass(userId, sessionName, classKeys);
    }

    public InternalQuoteStruct getQuoteForProduct(String sessionName, int productKey, CboeIdStruct cboeId)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = cboeId;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getQuoteForProduct", GUILoggerSABusinessProperty.MM_QUOTE, argObj);
        }

        return marketMakerQuoteService.getQuoteForProduct(sessionName, productKey, cboeId);
    }

    public RFQStruct[] getRFQ(String sessionName, int[] classKeys)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            Object[] arrayClassKeys = new Object[classKeys.length];

            argObj[0] = sessionName;

            for (int i = 0; i < classKeys.length; i++)
            {
                arrayClassKeys[i] = new Integer(classKeys[i]);
            }

            argObj[1] = arrayClassKeys;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getRFQ", GUILoggerSABusinessProperty.MM_QUOTE, argObj);
        }

        return marketMakerQuoteService.getRFQ(sessionName, classKeys);
    }

    public void requestForQuote(RFQStruct p0, String p1)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":requestForQuote", GUILoggerSABusinessProperty.MM_QUOTE, argObj);
        }

        marketMakerQuoteService.requestForQuote(p0, p1);
    }

// OrderHandlingService

    public void acceptCrossingOrder(OrderStruct p0, OrderStruct p1)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptCrossingOrder", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        orderHandlingService.acceptCrossingOrder(p0, p1);
    }

    public void acceptUpdate(int p0, OrderStruct p1)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = new Integer(p0);
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptUpdate", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        orderHandlingService.acceptUpdate(p0, p1);
    }

    public OrderStruct getOrderById(String p0, OrderIdStruct p1)
        throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOrderById", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        return orderHandlingService.getOrderById(p0, p1);
    }

    public OrderStruct getOrderForProduct(String sessionName, int productKey, CboeIdStruct cboeId)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = cboeId;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOrderForProduct", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        return orderHandlingService.getOrderForProduct(sessionName, productKey, cboeId);
    }

    public OrderStruct[] getPendingAdjustmentOrdersByClass(String sessionName, int classKey, String userId)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = userId;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPendingAdjustmentOrdersByClass", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        return orderHandlingService.getPendingAdjustmentOrdersByClass(sessionName, classKey, userId);
    }

    public OrderStruct[] getPendingAdjustmentOrdersByProduct(String sessionName, ProductKeysStruct productKeys, String userId)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = productKeys;
            argObj[2] = userId;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPendingAdjustmentOrdersByProduct", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        return orderHandlingService.getPendingAdjustmentOrdersByProduct(sessionName, productKeys, userId);
    }

    public void publishOrdersForFirm(ExchangeFirm firm)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishOrdersForFirm", GUILoggerSABusinessProperty.ORDER_HANDLING, firm);
        }

        orderHandlingService.publishOrdersForFirm(firm.getExchangeFirmStruct());
    }

    public void publishOrdersForUser(String p0)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishOrdersForUser", GUILoggerSABusinessProperty.ORDER_HANDLING, p0);
        }

        orderHandlingService.publishOrdersForUser(p0);
    }

    public ActivityHistoryStruct queryOrderHistory(String userId, String sessionName, int productKey, OrderIdStruct orderId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[4];
            argObj[0] = userId;
            argObj[1] = sessionName;
            argObj[2] = new Integer(productKey);
            argObj[3] = orderId;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":queryOrderHistory", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        return orderHandlingService.queryOrderHistory(userId, sessionName, productKey, orderId);
    }

    public void deleteOrderFromBook(String userId, CancelRequestStruct cancelRequest, ProductKeysStruct productKeys)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException,
            NotAcceptedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[ 3 ];
            argObj[0] = userId;
            argObj[1] = cancelRequest;
            argObj[2] = productKeys;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":deleteOrderFromBook",
                                       GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        orderMaintenanceService.deleteOrderFromBook(userId, cancelRequest, productKeys);
    }

    public void acceptCancel(String p0, CancelRequestStruct p1, ProductKeysStruct p2)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = p0;
            argObj[1] = p1;
            argObj[2] = p2;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptCancel", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        orderHandlingService.acceptCancel(p0, p1, p2);
    }

    public OrderIdStruct acceptCancelReplace(com.cboe.idl.cmiOrder.CancelRequestStruct p0, OrderStruct p1)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = p0;
            argObj[1] = p1;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptCancelReplace", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        return orderHandlingService.acceptCancelReplace(p0, p1);
    }

    public OrderIdStruct acceptOrder(OrderStruct p0)
        throws SystemException, CommunicationException, DataValidationException, TransactionFailedException, NotAcceptedException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptOrder", GUILoggerSABusinessProperty.ORDER_HANDLING, p0);
        }

        return orderHandlingService.acceptOrder(p0);
    }


    /**
     * getOrdersForProduct
     * <p/>
     * This method calls the existing OHS method, getOrdersForProduct, translating the
     * UserDefinitionStruct to the userId ( string ) and repackaging the return OrderStruct[]
     * to an OrderDetailStruct[]
     */
    public OrderStruct[] getOrdersForProduct(String userId, int productKey)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = userId;
            argObj[1] = new Integer(productKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOrdersForProduct", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        return orderHandlingService.getOrdersForProduct(userId, productKey);
    }

     /**
     * getOrdersForProduct
     * <p/>
     * This method calls the existing OHS method, getOrdersForProduct, translating the
     * UserDefinitionStruct to the userId ( string ) and repackaging the return OrderStruct[]
     * to an OrderDetailStruct[]
     */
    public OrderDetailStruct[] getOrderDetailsForProduct(String userId, int productKey)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = userId;
            argObj[1] = new Integer(productKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOrdersForProduct",
                                       GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        OrderStruct[]           orderStructSequence         = null;
        OrderDetailStruct[]     orderDetailStructSequence   = null;
        OrderDetailStruct       orderDetailStruct           = null;
        int                     i;
        int                     orderLength;

        orderStructSequence = getOrdersForProduct(userId, productKey);

        if (orderStructSequence != null)
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOrdersForProduct.orderStructSequence",
                                           GUILoggerSABusinessProperty.ORDER_HANDLING, orderStructSequence);
            }

            orderLength = orderStructSequence.length;
            orderDetailStructSequence = new OrderDetailStruct[ orderLength ];
            for (i = 0; i < orderLength; i++)
            {
                orderDetailStruct = buildOrderDetailStruct(orderStructSequence[i]);
                if (orderDetailStruct == null)
                {
                    throw ExceptionBuilder.notFoundException("Unable to find product name information for productkey:" + productKey, 0);
                }
                orderDetailStructSequence[i] = orderDetailStruct;
            }
        }

        return orderDetailStructSequence;
    }


    /*
      * Helper method to build a CAS order detail struct from the CBOE order struct.
      * @param order OrderStruct  The CBOE Business order object.
      * @return OrderDetailStruct
      */
    private OrderDetailStruct buildOrderDetailStruct(OrderStruct order)
    {
        int[]               productKeys = {order.productKey};
        ProductNameStruct   productName     = null;
        try
        {
            productName = systemAdminSessionManager.getProductQuery().getProductNameStruct(order.productKey);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(Category + ".buildOrderDetailStruct", "Unable to retrieve productNameStruct for productKey:" + order.productKey, e);
            return null;
        }
        return new OrderDetailStruct(productName, StatusUpdateReasons.QUERY, order);
    }

    public BookDepth getBookDepth(SessionProduct sessionProduct, boolean topOfBook)
        throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionProduct.getTradingSessionName();
            argObj[1] = new Integer(sessionProduct.getProductKey());

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getBookDepth", GUILoggerSABusinessProperty.ORDER_BOOK, argObj);
        }

        return new com.cboe.presentation.bookDepth.BookDepthImpl(orderBookService.getBookDepth(sessionProduct.getTradingSessionName(), sessionProduct.getProductKey(), topOfBook));
    }

    public BestBookStruct [] getBestBookForClass(SessionProductClass sessionProductClass)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionProductClass.getTradingSessionName();
            argObj[1] = new Integer(sessionProductClass.getClassKey());

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getBestBookForClass", GUILoggerSABusinessProperty.ORDER_BOOK, argObj);
        }

        return orderBookService.getBestBookForClass(sessionProductClass.getTradingSessionName(), sessionProductClass.getClassKey());
    }

    public BookDepth[] getBookDepthByClass(SessionProductClass sessionProductClass, boolean topOfBook)
        throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionProductClass.getTradingSessionName();
            argObj[1] = new Integer(sessionProductClass.getClassKey());

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getBookDepthForClass", GUILoggerSABusinessProperty.ORDER_BOOK, argObj);
        }

        BookDepthStruct[] structSequence = orderBookService.getBookDepthByClass(sessionProductClass.getTradingSessionName(), sessionProductClass.getClassKey(), topOfBook);
        BookDepth[] bookDepthSequence = new BookDepth[structSequence.length];

        for (int i = 0; i < structSequence.length; i++)
        {
            bookDepthSequence[i] = new com.cboe.presentation.bookDepth.BookDepthImpl(structSequence[i]);
        }

        return bookDepthSequence;
    }

    public BookDepth getBookDepthDetails(SessionProduct sessionProduct)
        throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.ORDER_BOOK))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionProduct.getTradingSessionName();
            argObj[1] = new Integer(sessionProduct.getProductKey());

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getBookDepthDetails", GUILoggerSABusinessProperty.ORDER_BOOK, argObj);
        }

        BookDepthDetailStruct bookDepthDetailStruct = orderBookService.getBookDepthDetails(sessionProduct.getTradingSessionName(), sessionProduct.getProductKey());

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.ORDER_BOOK))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getBookDepthDetails returns", GUILoggerSABusinessProperty.ORDER_BOOK, bookDepthDetailStruct);
        }
        return new BookDepthImpl(bookDepthDetailStruct);
    }

    public Tradable[] getBookDetails(SessionProduct sessionProduct, Price[] priceSequence)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, TransactionFailedException
    {
        PriceStruct[] prices = new PriceStruct[priceSequence.length];

        for (int i = 0; i < priceSequence.length; i++)
        {
            prices[i] = priceSequence[i].toStruct();
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionProduct.getTradingSessionName();
            argObj[1] = new Integer(sessionProduct.getProductKey());
            argObj[2] = prices;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getBookDetails", GUILoggerSABusinessProperty.ORDER_BOOK, argObj);
        }

        TradableStruct[] structSequence = orderBookService.getBookDetails(sessionProduct.getTradingSessionName(), sessionProduct.getProductKey(), prices);
        Tradable[] tradableSequence = new Tradable[structSequence.length];

        for (int i = 0; i < structSequence.length; i++)
        {
            tradableSequence[i] = new TradableImpl(structSequence[i]);
        }

        return tradableSequence;
    }

    public short getOpeningRequirementCode(SessionProductClass sessionProductClass)
        throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionProductClass.getTradingSessionName();
            argObj[1] = new Integer(sessionProductClass.getClassKey());

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getOpeningRequirementCode", GUILoggerSABusinessProperty.ORDER_BOOK, argObj);
        }

        return orderBookService.getOpeningRequirementCode(sessionProductClass.getTradingSessionName(), sessionProductClass.getClassKey());
    }

    public void setOpeningRequirementCode(SessionProductClass sessionProductClass, short openingRequirementCode)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionProductClass.getTradingSessionName();
            argObj[1] = new Integer(sessionProductClass.getClassKey());
            argObj[2] = new Short(openingRequirementCode);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setOpeningRequirementCode", GUILoggerSABusinessProperty.ORDER_BOOK, argObj);
        }

        orderBookService.setOpeningRequirementCode(sessionProductClass.getTradingSessionName(), sessionProductClass.getClassKey(), openingRequirementCode);
    }


    public TradeReportStruct getTradeReportByTradeId(CboeIdStruct tradeId, boolean activeOnly)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradeReportByTradeId", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, tradeId);
        }

        return tradeMaintenanceService.getTradeReportByTradeId(tradeId, activeOnly);
    }

    public TradeReportStructV2 getTradeReportV2ByTradeId(CboeIdStruct tradeId, boolean activeOnly)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradeReportV2ByTradeId", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, tradeId);
        }

        return tradeMaintenanceService.getTradeReportV2ByTradeId(tradeId, activeOnly);
    }

    public DateStruct getSettlementDate(String sessionName, int productKey, char tradeType, boolean asOfFlag, short daysAhead)
            throws NotFoundException, DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSettlementDate", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, new Integer(productKey));
        }

        return tradeMaintenanceService.getSettlementDate(sessionName, productKey, tradeType, asOfFlag, daysAhead);
    }

    public TradeReport[] getTradeReports(ExchangeAcronym exchangeAcronym, ExchangeFirm exchangeFirm,
                                         SessionProduct product, DateTime beginDateTime, DateTime endDateTime,
                                         char buySellInd, boolean activeOnly)
            throws NotFoundException, AuthorizationException, SystemException, DataValidationException,
            CommunicationException
    {
        Object[] argObj = null;

        if (GUILoggerHome.find().isDebugOn())
        {
            argObj = new Object[8];
            argObj[0] = exchangeAcronym.getExchangeAcronymStruct();
            argObj[1] = exchangeFirm.getExchangeFirmStruct();
            argObj[2] = new Integer(product.getProductKey());
            argObj[3] = product.getTradingSessionName();
            argObj[4] = beginDateTime.getDateTimeStruct();
            argObj[5] = endDateTime.getDateTimeStruct();
            argObj[6] = new Character(buySellInd);
            argObj[7] = new Boolean(activeOnly);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradeReports", GUILoggerSABusinessProperty.TRADE_QUERY, argObj);
        }

        TradeReportStructV2[] structs = tradeMaintenanceService.getTradeReportsV2(exchangeAcronym.getExchangeAcronymStruct(),
                                                                              exchangeFirm.getExchangeFirmStruct(),
                                                                              product.getProductKey(),
                                                                              product.getTradingSessionName(),
                                                                              beginDateTime.getDateTimeStruct(),
                                                                              endDateTime.getDateTimeStruct(),
                                                                              buySellInd,
                                                                              activeOnly);

        GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradeReports", GUILoggerSABusinessProperty.TRADE_QUERY, structs);

        TradeReport[] reports = new TradeReport[structs.length];

        for (int i = 0; i < structs.length; i++)
        {
            reports[i] = TradeReportFactory.createTradeReport(structs[i]);
        }

        return reports;
    }

    public TradeReportStruct acceptTrade(TradeReportStruct efpOrBlockTradeStruct)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptTrade", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, efpOrBlockTradeStruct);
        }

        return tradeMaintenanceService.acceptTrade(efpOrBlockTradeStruct);
    }

    public TradeReportStructV2 acceptTrade(TradeReportStructV2 efpOrBlockTradeStruct)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptTrade", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, efpOrBlockTradeStruct);
        }

        return tradeMaintenanceService.acceptTradeV2(efpOrBlockTradeStruct);
    }


    public void acceptTradeBust(String sessionName, int productKey, CboeIdStruct tradeId, BustTradeStruct[] bustedTrades, String reason)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[5];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = tradeId;
            argObj[3] = bustedTrades;
            argObj[4] = reason;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptTradeBust", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        tradeMaintenanceService.acceptTradeBust(sessionName, productKey, tradeId, bustedTrades, reason);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE),
                                       "acceptTradeBust", argObj);
        }
    }

    public TradeReportStruct[] findTradeReportsBetween(DateTimeStruct beginDateTime, DateTimeStruct endDateTime, boolean activeOnly)
        throws NotFoundException, AuthorizationException, SystemException, CommunicationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = beginDateTime;
            argObj[1] = endDateTime;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":findTradeReportsBetween", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        return tradeMaintenanceService.findTradeReportsBetween(beginDateTime, endDateTime, activeOnly);
    }

    public void acceptTradeUpdate(String tradingSessionName, int productKey, CboeIdStruct idStruct, AtomicTradeStruct[] trades)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
        NotAcceptedException, TransactionFailedException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = tradingSessionName;
            argObj[1] = new Integer(productKey);
            argObj[2] = idStruct;
            argObj[3] = trades;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":acceptTradeUpdate", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        tradeMaintenanceService.acceptTradeUpdate(tradingSessionName, productKey, idStruct, trades);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE),
                                       "acceptTradeUpdate", argObj);
        }

    }

    public TradeReportV3[] getTradeReportsBySummary(TradeReportSummaryStruct[] tradeReportSummaries, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        tradeReportSummaries, activeOnly
                };
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsBySummary:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE), "getTradeReportsBySummary() params:", argObj);
        }

        TradeReportStructV3[] tradeReportStructs = tradeMaintenanceService.getTradeReportsBySummary(tradeReportSummaries, activeOnly);

        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsBySummary() results:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, tradeReportStructs);
        }

        List<TradeReportV3> retVal = new ArrayList<TradeReportV3>();
        for (TradeReportStructV3 v3Struct : tradeReportStructs)
        {
            retVal.add(TradeReportFactory.createTradeReport(v3Struct));
        }

        return retVal.toArray(new TradeReportV3[retVal.size()]);
    }

    public TradeBustResponseStruct[] acceptMultipleTradeBust(MultipleTradeBustStruct[] trades, String transactionID)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException
    {
        KeyValueStruct[] auditLogProperties = new KeyValueStruct[]
                {
                        new KeyValueStruct(PropertyFederatedBulkOperation.WORKSTATION_ID, getWorkStationID())
                };
        String requestingUserID = getCurrentLoggedInUser();
        DateTimeStruct timeStamp = getCurrentTime();
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        trades,
                        requestingUserID,
                        transactionID,
                        timeStamp,
                        auditLogProperties
                };
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".acceptMultipleTradeBust:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE), "acceptMultipleTradeBust() params:", argObj);
        }

        TradeBustResponseStruct[] retVal = tradeMaintenanceService.acceptMultipleTradeBust(
                trades,
                requestingUserID,
                transactionID,
                timeStamp,
                auditLogProperties);

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE), "acceptMultipleTradeBust() results:", retVal);
        }

        return retVal;
    }

    public RelatedTradeReportSummaryStruct[] getTradeReportsByTime(DateTimeStruct beginTime, DateTimeStruct endTime, String sessionName, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        beginTime, endTime, sessionName, activeOnly
                };
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsByTime:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE), "getTradeReportsByTime() params:", argObj);
        }

        RelatedTradeReportSummaryStruct[] retVal = tradeMaintenanceService.getTradeReportsByTime(beginTime, endTime, sessionName, activeOnly);

        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsByTime() results:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, retVal);
        }

        return retVal;
    }

    public RelatedTradeReportSummaryStruct[] getTradeReportsByTimeAndClass(DateTimeStruct beginTime, DateTimeStruct endTime,
                                                                           String sessionName, int[] classKeys, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        beginTime, endTime, sessionName, classKeys, activeOnly
                };
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsByTimeAndClass:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE), "getTradeReportsByTimeAndClass() params:", argObj);
        }

        RelatedTradeReportSummaryStruct[] retVal = tradeMaintenanceService.getTradeReportsByTimeAndClass(beginTime, endTime, sessionName, classKeys, activeOnly);

        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsByTimeAndClass() results:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, retVal);
        }

        return retVal;
    }

    public RelatedTradeReportSummaryStruct[] getTradeReportsByTimeAndExchange(DateTimeStruct beginTime, DateTimeStruct endTime,
                                                                          String sessionName, String[] primaryExchanges,
                                                                          boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        beginTime, endTime, sessionName, primaryExchanges, activeOnly
                };
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsByTimeAndExchange:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE), "getTradeReportsByTimeAndExchange() params:", argObj);
        }

        RelatedTradeReportSummaryStruct[] retVal = tradeMaintenanceService.getTradeReportsByTimeAndExchange(beginTime, endTime, sessionName, primaryExchanges, activeOnly);

        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsByTimeAndExchange() results:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, retVal);
        }

        return retVal;
    }

    public RelatedTradeReportSummaryStruct[] getTradeReportsByTimeAndProduct(DateTimeStruct beginTime, DateTimeStruct endTime,
                                                                            String sessionName, int[] productKeys, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        beginTime, endTime, sessionName, productKeys, activeOnly
                };
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsByTimeAndProduct:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE), "getTradeReportsByTimeAndProduct() params:", argObj);
        }

        RelatedTradeReportSummaryStruct[] retVal = tradeMaintenanceService.getTradeReportsByTimeAndProduct(beginTime, endTime, sessionName, productKeys, activeOnly);

        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsByTimeAndProduct() results:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, retVal);
        }

        return retVal;
    }

    public RelatedTradeReportSummaryStruct[] getTradeReportsByTimeAndUser(DateTimeStruct beginTime, DateTimeStruct endTime,
                                                                          String sessionName, String[] users, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        beginTime, endTime, sessionName, users, activeOnly
                };
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsByTimeAndUser:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE), "getTradeReportsByTimeAndUser() params:", argObj);
        }

        RelatedTradeReportSummaryStruct[] retVal = tradeMaintenanceService.getTradeReportsByTimeAndUser(beginTime, endTime, sessionName, users, activeOnly);

        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportsByTimeAndUser() results:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, retVal);
        }

        return retVal;
    }

    public RelatedTradeReportSummaryStruct getTradeReportSummaryByTradeId(CboeIdStruct tradeId, boolean activeOnly)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        tradeId, activeOnly
                };
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportSummaryByTradeId:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.TRADE_MAINTENANCE), "getTradeReportSummaryByTradeId() params:", argObj);
        }

        RelatedTradeReportSummaryStruct retVal = tradeMaintenanceService.getTradeReportSummaryByTradeId(tradeId, activeOnly);

        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.TRADE_MAINTENANCE))
        {
            guiLogger.debug(TRANSLATOR_NAME + ".getTradeReportSummaryByTradeId() results:", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, retVal);
        }

        return retVal;
    }

/*
    public TradeHistoryStruct[] findTradeBustHistoriesBetween(DateTimeStruct beginDateTime, DateTimeStruct endDateTime)
        throws NotFoundException, AuthorizationException, SystemException, CommunicationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = beginDateTime;
            argObj[1] = endDateTime;

            GUILoggerHome.find().debug(TRANSLATOR_NAME+":findTradeBustHistoriesBetween", GUILoggerSABusinessProperty.TRADE_MAINTENANCE, argObj);
        }

        return tradeMaintenanceService.findTradeBustHistoriesBetween(beginDateTime, endDateTime);
    }
*/

    /*
    public ClassStruct[] getUnassignedClasses()
        throws CommunicationException, SystemException, AuthorizationException
    {
        return tradingSessionService.getUnassignedClasses();
    }

    public String buildTemplateClassMapping()
        throws CommunicationException, SystemException, TransactionFailedException, AuthorizationException
    {
        return tradingSessionService.buildTemplateClassMapping();
    }
*/
    public TradingSessionStruct[] getTradingSessions()
        throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessions", GUILoggerSABusinessProperty.TRADING_SESSION, "");
        }

        TradingSessionStruct[] tradingSessions = tradingSessionService.getTradingSessions();

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessions", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }

        return tradingSessions;
    }

    public TradingSessionStruct[] getTradingSessionsByTime(TimeStruct startTime, TimeStruct endTime)
        throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = startTime;
            argObj[1] = endTime;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessionsByTime", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        TradingSessionStruct[] tradingSessions = tradingSessionService.getTradingSessionsByTime(startTime, endTime);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessionsByTime", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return tradingSessions;
    }

    public TradingSessionStruct[] getTradingSessionsForClass(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessionsForClass", GUILoggerSABusinessProperty.TRADING_SESSION, new Integer(classKey));
        }

        TradingSessionStruct[] tradingSessions = tradingSessionService.getTradingSessionsForClass(classKey);
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessionsForClass", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return tradingSessions;
    }

    public TradingSessionStruct getTradingSessionByName(String sessionName)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessionByName", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }

        TradingSessionStruct tradingSession = tradingSessionService.getTradingSessionByName(sessionName);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessionByName", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed");
        }
        return tradingSession;
    }

    public SessionClassStruct[] getClassesBySession(String sessionName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesBySession", GUILoggerSABusinessProperty.TRADING_SESSION, sessionName);
        }

        SessionClassStruct[] sessionClasses = tradingSessionService.getClassesBySession(sessionName);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesBySession", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionClasses;
    }

    public SessionClassStruct[] getClassesBySessionForType(String sessionName, short productType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(productType);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesBySessionForType", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        SessionClassStruct[] sessionClasses = tradingSessionService.getClassesBySessionForType(sessionName, productType);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getClassesBySessionForType", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionClasses;
    }

    public SessionProductStruct[] getProductsBySessionForClass(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductsBySessionForClass", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        SessionProductStruct[] sessionProducts = tradingSessionService.getProductsBySessionForClass(sessionName, classKey);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductsBySessionForClass", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionProducts;
    }

    public SessionStrategyStruct[] getStrategiesBySessionForClass(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getStrategiesBySessionForClass", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        SessionStrategyStruct[] sessionStrategies = tradingSessionService.getStrategiesBySessionForClass(sessionName, classKey);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getStrategiesBySessionForClass", GUILoggerSABusinessProperty.TRADING_SESSION, "Completed.");
        }
        return sessionStrategies;
    }

    public void setUserPreferences(String userName, PreferenceStruct[] preferenceSequence)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = userName;
            argObj[1] = preferenceSequence;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setUserPreferences", GUILoggerSABusinessProperty.USER_PREFERENCES, argObj);
        }

        userService.setUserPreferences(userName, preferenceSequence);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_PREFERENCES),
                                       "setUserPreferences", argObj);
        }
    }

    public void removeUserPreference(String userName, PreferenceStruct[] preferenceSequence)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = userName;
            argObj[1] = preferenceSequence;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeUserPreference", GUILoggerSABusinessProperty.USER_PREFERENCES, argObj);
        }

        userService.removeUserPreference(userName, preferenceSequence);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_PREFERENCES),
                                       "removeUserPreference", argObj);
        }
    }

    public PreferenceStruct[] getAllUserPreferences(String userName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllUserPreferences", GUILoggerSABusinessProperty.USER_PREFERENCES, userName);
        }

        return userService.getAllUserPreferences(userName);
    }

    public PreferenceStruct[] getUserPreferencesByPrefix(String userName, String prefix)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = userName;
            argObj[1] = prefix;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUserPreferencesByPrefix", GUILoggerSABusinessProperty.USER_PREFERENCES, argObj);
        }

        return userService.getUserPreferencesByPrefix(userName, prefix);
    }

    public void removeUserPreferencesByPrefix(String userName, String prefix)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = userName;
            argObj[1] = prefix;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeUserPreferencesByPrefix", GUILoggerSABusinessProperty.USER_PREFERENCES, argObj);
        }

        userService.removeUserPreferencesByPrefix(userName, prefix);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_PREFERENCES),
                                       "removeUserPreferencesByPrefix", argObj);
        }
    }

    public PreferenceStruct[] getAllSystemPreferences(String userName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllSystemPreferences", GUILoggerSABusinessProperty.USER_PREFERENCES, userName);
        }

        return userService.getAllSystemPreferences(userName);
    }

    public PreferenceStruct[] getSystemPreferencesByPrefix(String userName, String prefix)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = userName;
            argObj[1] = prefix;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSystemPreferencesByPrefix", GUILoggerSABusinessProperty.USER_PREFERENCES, argObj);
        }

        return userService.getSystemPreferencesByPrefix(userName, prefix);
    }

    /**
     * Subscribes the client listener to receive TradingSessionEventState
     */
    public void subscribeTradingSessionEventState(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.TRADING_SESSION_EVENT_STATE, new Integer(0));

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribeTradingSessionEventState",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, clientListener);
        }

        if (SubscriptionManagerFactory.find().subscribe(key, clientListener, tradingSessionEventStateConsumer) == 1)
        {
            tradingSessionEventStateService.subscribeTradingSessionEventState(tradingSessionEventStateConsumer);
        }

        key = new ChannelKey(ChannelType.CB_TRADING_SESSION_EVENT_STATE, new Integer(0));
        EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Unsubscribes the client listener to receive TradingSessionEventState
     */
    public void unsubscribeTradingSessionEventState(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey key = new ChannelKey(ChannelType.TRADING_SESSION_EVENT_STATE, new Integer(0));

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":unsubscribeTradingSessionEventState",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, clientListener);
        }

        if (SubscriptionManagerFactory.find().unsubscribe(key, clientListener, tradingSessionEventStateConsumer) == 0)
        {
            tradingSessionEventStateService.unsubscribeTradingSessionEventState(tradingSessionEventStateConsumer);
        }

        key = new ChannelKey(ChannelType.CB_TRADING_SESSION_EVENT_STATE, new Integer(0));
        EventChannelAdapterFactory.find().removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    /**
     * Gets strategies by product key that is used in the strategy legs.
     *
     * @param componentProductKey the product key to get strategies for
     * @return the requested strategies.
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public SessionStrategyStruct[] getStrategiesByComponent(int componentProductKey, String sessionName)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Integer(componentProductKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getStrategiesByComponent", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }

        SessionStrategyStruct[] strategies = tradingSessionService.getStrategiesByComponent(componentProductKey, sessionName);
        return strategies;
    }

    public void acceptStrategyUpdate(int remainingQuantity, OrderStruct anOrder, LegOrderEntryStruct[] legEntryDetails)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = new Integer(remainingQuantity);
            argObj[1] = anOrder;
            argObj[2] = legEntryDetails;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": acceptStrategyUpdate", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }

        orderHandlingService.acceptStrategyUpdate(remainingQuantity, anOrder, legEntryDetails);
    }

    public OrderIdStruct acceptStrategyOrder(OrderStruct anOrder, LegOrderEntryStruct[] legEntryDetails)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
         if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = anOrder;
            argObj[1] = legEntryDetails;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": acceptStrategyOrder", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        return orderHandlingService.acceptStrategyOrder(anOrder, legEntryDetails);
    }

    public OrderIdStruct acceptStrategyCancelReplace(com.cboe.idl.cmiOrder.CancelRequestStruct cancelRequest, OrderStruct anOrder, LegOrderEntryStruct[] legEntryDetails)
          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, NotAcceptedException
    {
         if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[3];
            argObj[0] = cancelRequest;
            argObj[1] = anOrder;
            argObj[2] = legEntryDetails;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": acceptStrategyCancelReplace", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        return orderHandlingService.acceptStrategyCancelReplace(cancelRequest, anOrder, legEntryDetails);
    }

    public StrategyRequestStruct buildStrategyRequestByName(short strategyType, ProductNameStruct anchorProduct, PriceStruct priceIncrement, short monthIncrement)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[4];
            argObj[0] = new Short(strategyType);
            argObj[1] = anchorProduct;
            argObj[2] = priceIncrement;
            argObj[3] = new Short(monthIncrement);
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": buildStrategyRequestByName", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }
        return (productDefinition.buildStrategyRequestByName(strategyType, anchorProduct, priceIncrement, monthIncrement));
    }

    public StrategyRequestStruct buildStrategyRequestByProductKey(short strategyType, int anchorProductKey, PriceStruct priceIncrement, short monthIncrement)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[4];
            argObj[0] = new Short(strategyType);
            argObj[1] = new Integer(anchorProductKey);
            argObj[2] = priceIncrement;
            argObj[3] = new Short(monthIncrement);
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": buildStrategyRequestByProductKey", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }
        return (productDefinition.buildStrategyRequestByProductKey(strategyType, anchorProductKey, priceIncrement, monthIncrement));
    }

// ProductQueryServices
//

    /**
     * Get Products for a classKey
     */
    public ProductStruct[] getProductsByClass(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductsByClass", GUILoggerSABusinessProperty.PRODUCT_QUERY, new Integer(classKey));
        }

        return productQueryService.getProductsByClass(classKey);
    }

    public ProductClassStruct[] getProductClassesByType(short[] types, boolean activeOnly, boolean includeReportingClasses, boolean includeProducts)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            int i;
            Object[] argObj = new Object[types.length + 3];
            for (i = 0; i < types.length; i++)
            {
                argObj[i] = new Short(types[i]);
            }
            argObj[i++] = new Boolean(activeOnly);
            argObj[i++] = new Boolean(includeReportingClasses);
            argObj[i] = new Boolean(includeProducts);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductClassesByType", GUILoggerSABusinessProperty.PRODUCT_QUERY, argObj);
        }

        return productQueryService.getProductClassesByType(types, activeOnly, includeReportingClasses, includeProducts);
    }

    public Product getProductByKey(int productKey)
        throws NotFoundException, SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductByKey", GUILoggerSABusinessProperty.PRODUCT_QUERY, productKey);
        }

        Product product = null;

        if (productKey == DEFAULT_PRODUCT_KEY)
        {
            product = getDefaultProduct();
        }
        else if (productKey == ALL_SELECTED_PRODUCT_KEY)
        {
            product = getAllSelectedProduct();
        }
        else if (productKey == DEFAULT_STRATEGY_PRODUCT_KEY)
        {
            product = getDefaultStrategy();
        }
        else
        {
            int[] keys = {productKey};
            product = ProductFactoryHome.find().create(this.getProductsByKey(keys)[0]);
        }
        return product;
    }

    public ProductStruct[] getProductsByKey(int[] productKeys)
        throws NotFoundException, SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[productKeys.length];
            for (int i = 0; i < productKeys.length; i++)
            {
                argObj[i] = new Integer(productKeys[i]);
            }

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductsByKey", GUILoggerSABusinessProperty.PRODUCT_QUERY, argObj);
        }

        return productQueryService.getProductsByKey(productKeys);
    }

    public ProductStructV2[] getProductsByKeyV2(int[] productKeys)
        throws NotFoundException, SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[productKeys.length];
            for (int i = 0; i < productKeys.length; i++)
            {
                argObj[i] = new Integer(productKeys[i]);
            }

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductsByKeyV2", GUILoggerSABusinessProperty.PRODUCT_QUERY, argObj);
        }

        return productQueryService.getProductsByKeyV2(productKeys);
    }

    public ProductStructV4[] getProductsByKeyV4(int[] productKeys)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[productKeys.length];
            for(int i = 0; i < productKeys.length; i++)
            {
                argObj[i] = new Integer(productKeys[i]);
            }

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductsByKeyV4",
                                       GUILoggerSABusinessProperty.PRODUCT_QUERY, argObj);
        }

        return productMaintenance.getProductsByKeyV4(productKeys);
    }

    public ProductClassStruct getProductClassByKey(int productClassKey, boolean includeReportingClasses, boolean includeProducts, boolean includeActiveOnly)
        throws NotFoundException, SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[4];
            argObj[0] = new Integer(productClassKey);
            argObj[1] = new Boolean(includeReportingClasses);
            argObj[2] = new Boolean(includeProducts);
            argObj[3] = new Boolean(includeActiveOnly);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductClassByKey", GUILoggerSABusinessProperty.PRODUCT_QUERY, argObj);
        }

        return productQueryService.getProductClassByKey(productClassKey, includeReportingClasses, includeProducts, includeActiveOnly);
    }

    //public ProductClassDetail[] getProductClassesByDescription(String productDescriptionName, boolean activeOnly, boolean includeReportingClasses, boolean includeProducts)
    public ProductClassStruct[] getProductClassesByDescription(String productDescriptionName)
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[1];
            argObj[0] = productDescriptionName;
            /*
            argObj[1] = new Boolean(activeOnly);
            argObj[2] = new Boolean(includeReportingClasses);
            argObj[3] = new Boolean(includeProducts);
            */

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductClassesByDescription", GUILoggerSABusinessProperty.PRODUCT_QUERY, argObj);
        }

//        ProductClassStruct[] productClassStructs = productQueryService.getProductClassesByDescription(productDescriptionName, activeOnly, includeReportingClasses, includeProducts);
        // FIXME - hardcoded choice until API is changed.
        return productQueryService.getProductClassesByDescription(productDescriptionName, true, true, true);
    }

    public ProductDescriptionStruct[] getProductDescriptions()
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        return productQueryService.getProductDescriptions();
    }

// ExchangeService implementations
//

    /**
     * returns the long ExchangeKey for the new exchange
     */
    public int addExchange(Exchange exchange)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException, AlreadyExistsException
    {
        // @todo: this method needs to update exchangeCache if it is ever called
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":addExchange", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, exchange);
        }

        int retVal = exchangeService.addExchange(exchange.getExchangeStruct());
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                       "addExchange", exchange);
        }
        return retVal;
    }

    /**
     * remove an Exchange
     */
    public void removeExchange(int exchangeKey)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, TransactionFailedException
    {
        // @todo: this method needs to update exchangeCache if it is ever used
        StringBuffer buffer = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            buffer = new StringBuffer("removeExchange");
            buffer.append(" Exchange Key: ").append(exchangeKey);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":removeExchange", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, buffer.toString());
        }
        exchangeService.removeExchange(exchangeKey);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                       buffer.toString(), "");
        }
    }

    /**
     * get an Exchange by its ExchangeKey
     */
    public Exchange getExchangeForKey(int exchangeKey)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        ExchangeStruct exchange = null;
        if (exchangeCache != null)
        {
            for (int i = 0; i < exchangeCache.length; ++i)
            {
                if (exchangeCache[i].exchangeKey == exchangeKey)
                {
                    exchange = exchangeCache[i];
                    break;
                }
            }
        }
        else
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getExchangeForKey", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, new Integer(exchangeKey));
            }
            exchange = exchangeService.getExchangeForKey(exchangeKey);
        }
        if (exchange == null)
        {
            throw new NotFoundException();
        }
        return ExchangeFactoryImpl.create(exchange);
    }

    /**
     * get an Exchange by its acronym
     */
    public Exchange getExchangeForAcronym(String acronym)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        ExchangeStruct exchange = null;
        if (exchangeCache != null)
        {
            for (int i = 0; i < exchangeCache.length; ++i)
            {
                if (exchangeCache[i].acronym.equals(acronym))
                {
                    exchange = exchangeCache[i];
                    break;
                }
            }
        }
        else
        {
            Object[] argObj = null;
            if (GUILoggerHome.find().isDebugOn())
            {
                argObj = new Object[1];
                argObj[0] = acronym;
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getExchangeForAcronym", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, argObj);
            }
            exchange = exchangeService.getExchangeForAcronym(acronym);
        }
        if (exchange == null)
        {
            throw new NotFoundException();
        }
        return ExchangeFactoryImpl.create(exchange);
    }

    /**
     * update an existing Exchange
     */
    public void updateExchange(Exchange exchange)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        // @todo: this method needs to update exchangeCache if it is ever used
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateExchange", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, exchange);
        }
        exchangeService.updateExchange(exchange.getExchangeStruct());
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_DEFINITION),
                                       "updateExchange", exchange);
        }
    }

    /**
     * get an array of all Exchanges
     */
    public Exchange[] getAllExchanges()
        throws SystemException, CommunicationException, AuthorizationException
    {
        Exchange[] exchanges = null;
        if (exchangeCache == null)
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllExchanges", GUILoggerSABusinessProperty.PRODUCT_DEFINITION, "");
            }
            exchangeCache = exchangeService.getAllExchanges();
        }
        if (exchangeCache != null)
        {
            exchanges = new Exchange[exchangeCache.length];

            for (int i = 0; i < exchanges.length; i++)
            {
                exchanges[i] = ExchangeFactoryImpl.create(exchangeCache[i]);
            }
        }
        return exchanges;
    }
// end ExchangeService implementations

    /**
     * Subscribes the client listener to receive UserMaintenanceEventService
     * UserMaintenanceEventServiceAPI operation definition.
     *
     * @param clientListener the client listener to subscribe.
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public void subscribeUserMaintenanceEvent(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        /* Server is not ready to publish user/firm delete or firm update as of Nov 10, 2001.
         */

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribeUserMaintenanceEventChannel", GUILoggerSABusinessProperty.USER_MANAGEMENT, clientListener);
        }

        ChannelKey myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_ADD_USER, new Integer(0));
        int key1 = SubscriptionManagerFactory.find().subscribe(myChannelKey, clientListener, cacheUpdateConsumer);
        myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_DELETE_USER, new Integer(0));
        int key2 = SubscriptionManagerFactory.find().subscribe(myChannelKey, clientListener, cacheUpdateConsumer);

        myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_ADD_FIRM, new Integer(0));
        int key3 = SubscriptionManagerFactory.find().subscribe(myChannelKey, clientListener, cacheUpdateConsumer);
        myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_DELETE_FIRM, new Integer(0));
        int key4 = SubscriptionManagerFactory.find().subscribe(myChannelKey, clientListener, cacheUpdateConsumer);

        myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_USER_FIRM_AFFILIATION_UPDATE, new Integer(0));
        int key5 = SubscriptionManagerFactory.find().subscribe(myChannelKey, clientListener, cacheUpdateConsumer);

        myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_USER_FIRM_AFFILIATION_DELETE, new Integer(0));
        int key6 = SubscriptionManagerFactory.find().subscribe(myChannelKey, clientListener, cacheUpdateConsumer);

        if (key1 == 1 && key2 == 1 && key3 == 1 && key4 == 1 && key5 == 1 && key6 == 1)
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribeUserMaintenanceEventChannel, listener count = 1", GUILoggerSABusinessProperty.USER_MANAGEMENT, clientListener);
            }
            userMaintenanceEvent.subscribeUserMaintenanceEvent(cacheUpdateConsumer);
        }
    }

    /**
     * Unsubscribes the client listener to receive UserMaintenanceEventService
     * UserMaintenanceEventServiceAPI operation definition.
     *
     * @param clientListener the client listener to be unsubscribed.
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
         */
    public void unsubscribeUserMaintenanceEvent(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":unsubscribeUserMaintenanceEvent", GUILoggerSABusinessProperty.USER_MANAGEMENT, clientListener);
        }

        ChannelKey myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_ADD_USER, new Integer(0));
        int key1 = SubscriptionManagerFactory.find().unsubscribe(myChannelKey, clientListener, cacheUpdateConsumer);
        myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_DELETE_USER, new Integer(0));
        int key2 = SubscriptionManagerFactory.find().unsubscribe(myChannelKey, clientListener, cacheUpdateConsumer);
        myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_ADD_FIRM, new Integer(0));
        int key3 = SubscriptionManagerFactory.find().unsubscribe(myChannelKey, clientListener, cacheUpdateConsumer);
        myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_DELETE_FIRM, new Integer(0));

        int key4 = SubscriptionManagerFactory.find().unsubscribe(myChannelKey, clientListener, cacheUpdateConsumer);
        myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_USER_FIRM_AFFILIATION_UPDATE, new Integer(0));
        int key5 = SubscriptionManagerFactory.find().unsubscribe(myChannelKey, clientListener, cacheUpdateConsumer);

        myChannelKey = new ChannelKey(ChannelType.CB_USER_EVENT_USER_FIRM_AFFILIATION_DELETE, new Integer(0));
        int key6 = SubscriptionManagerFactory.find().unsubscribe(myChannelKey, clientListener, cacheUpdateConsumer);

        if (key1 == 0 && key2 == 0 && key3 == 0 && key4 == 0 && key5 == 0 && key6 == 0)
        {
            userMaintenanceEvent.unsubscribeUserMaintenanceEvent(cacheUpdateConsumer);
        }
    }

    public void subscribeFirmEvents(EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.FIRM_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribeFirmEvents",
                                       GUILoggerSABusinessProperty.FIRM_MAINTENANCE, listener);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.CB_USER_EVENT_ADD_FIRM, new Integer(0));
        int key1 = SubscriptionManagerFactory.find().subscribe(channelKey, listener, cacheUpdateConsumer);
        channelKey = new ChannelKey(ChannelType.CB_USER_EVENT_DELETE_FIRM, new Integer(0));
        int key2 = SubscriptionManagerFactory.find().subscribe(channelKey, listener, cacheUpdateConsumer);

        if (key1 == 1 && key2 == 1)
        {
            if (GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.FIRM_MAINTENANCE))
            {
                GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribeFirmEvents, listener count = 1",
                                           GUILoggerSABusinessProperty.FIRM_MAINTENANCE, listener);
            }
            userMaintenanceEvent.subscribeUserMaintenanceEvent(cacheUpdateConsumer);
        }
    }

    public void unsubscribeFirmEvents(EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.FIRM_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":unsubscribeFirmEvents",
                                       GUILoggerSABusinessProperty.FIRM_MAINTENANCE, listener);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.CB_USER_EVENT_ADD_FIRM, new Integer(0));
        int key1 = SubscriptionManagerFactory.find().unsubscribe(channelKey, listener, cacheUpdateConsumer);
        channelKey = new ChannelKey(ChannelType.CB_USER_EVENT_DELETE_FIRM, new Integer(0));
        int key2 = SubscriptionManagerFactory.find().unsubscribe(channelKey, listener, cacheUpdateConsumer);

        if (key1 == 0 && key2 == 0)
        {
            userMaintenanceEvent.unsubscribeUserMaintenanceEvent(cacheUpdateConsumer);
        }
    }

    /**
     * Helper method. Formats IGUILoggerProperty using BRIEF_NAME format strategy
     *
     * @param IGUILoggerProperty - property to format
     * @return String representing property
     */
    private String formatLogProperty(IGUILoggerProperty property)
    {
        return logPropertyFormatter.format(property, GUILoggerPropertyFormatStrategy.BRIEF_NAME);
    }

    /**
     * Helper method. Formats ProductState using BRIEF_NAME format strategy
     *
     * @param state - product state to format
     * @return String representing Product State
     */
    private String formatProductState(short state)
    {
        return ProductStates.toString(state, ProductStates.BRIEF_FORMAT);
    }

    /**
     * Return the service helper class name
     */
    protected String getHelperClassName()
    {
        return "com.cboe.idl.internalBusinessServices.UserMaintenanceServiceHelper";
    }

    public UserAccountModel[] getAllUsers()
            throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllUsers()",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, "");
        }

        UserSummaryStruct[] structs = userMaintenanceEvent.getAllUsersSummaries();
        return convertStructArrayToModels(structs);
    }

    public UserAccountModel getDPMJointAccountForClass(String userId, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = userId;
            argObj[1] = new Integer(classKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDPMJointAccountForClass()",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, argObj);
        }

        SessionProfileUserDefinitionStruct struct =
                userMaintenanceEvent.getSessionProfileDpmJointAccountForClass(userId, classKey);

        return UserCollectionFactory.createUserAccountModel(struct, false);
    }

    public UserAccountModel[] getDPMParticipantForClass(int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDPMParticipantForClass()",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, new Integer(classKey));
        }

        SessionProfileUserDefinitionStruct[] structs =
                userMaintenanceEvent.getSessionProfileDpmParticipantsForClass(classKey);
        return convertStructArrayToModels(structs, false);
    }

    public UserAccountModel[] getDPMForClass(String userId, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = userId;
            argObj[1] = new Integer(classKey);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDPMForClass()",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, argObj);
        }

        SessionProfileUserDefinitionStruct[] structs =
                userMaintenanceEvent.getSessionProfileDpmsForClass(userId, classKey);
        return convertStructArrayToModels(structs, false);
    }

    public UserAccountModel[] getDPMForJointAccount(String userId)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getDPMForJointAccount()",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, userId);
        }

        SessionProfileUserDefinitionStruct[] structs =
                userMaintenanceEvent.getSessionProfileDpmsForJointAccount(userId);
        return convertStructArrayToModels(structs, false);
    }

    public UserSummaryStruct getUserSummaryStructByUserId(String userId)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUserSummaryStructByKey(String)",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, userId);
        }

        UserSummaryStruct struct = userMaintenanceEvent.getUserSummary(userId);

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUserSummaryStructByKey(String)",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, struct);
        }

        return struct;
    }

    public SessionProfileUserDefinitionStruct getUserStructByKey(int userKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUserStructByKey(int)",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, new Integer(userKey));
        }

        SessionProfileUserDefinitionStruct struct = userMaintenanceEvent.getSessionProfileUserByKey(userKey);

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUserStructByKey(int)",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, struct);
        }

        return struct;
    }

    public void updateUser(UserAccountModel user)
            throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException,
            DataValidationException
    {
        SessionProfileUserDefinitionStruct struct = user.getUserDefinitionStruct();

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateUser(UserAccountModel)",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, struct);
        }

        userMaintenanceEvent.updateSessionProfileUser(struct);
        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_MANAGEMENT),
                                       "updateUser(UserAccountModel)", struct);
        }
    }

    public char[] getProfileOriginTypesForSession(String sessionName, char role)
            throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = new Character(role);

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProfileOriginTypesForSession", GUILoggerSABusinessProperty.USER_SESSION,
                    argObj);
        }
        char[] originTypes = userMaintenanceEvent.getProfileOriginTypesForSession(sessionName, role);
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getProfileOriginTypesForSession", GUILoggerSABusinessProperty.USER_SESSION,
                    originTypes);
        }
        return originTypes;
    }

    /**
     * Gets the user given the user's system name.
     *
     * @param userId user's system name
     * @return user information
     * @throws NotFoundException if user is not found
     */
    public SessionProfileUserStructV2 getUserInformation(String userId)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUserInformation()",
                                       GUILoggerSABusinessProperty.USER_PREFERENCES, userId);
        }

        return userService.getSessionProfileUserInformationV2(userId);
    }

    private UserAccountModel[] convertStructArrayToModels(UserSummaryStruct[] structs)
    {
        UserAccountModel[] models = null;
        if (structs != null)
        {
            models = new UserAccountModel[structs.length];
            for (int i = 0; i < structs.length; i++)
            {
                models[i] = UserCollectionFactory.createUserAccountModel(structs[i]);
            }
        }
        return models;
    }

    private UserAccountModel[] convertStructArrayToModels(SessionProfileUserDefinitionStruct[] structs, boolean isNew)
    {
        UserAccountModel[] models = null;
        if (structs != null)
        {
            models = new UserAccountModel[structs.length];
            for (int i = 0; i < structs.length; i++)
            {
                models[i] = UserCollectionFactory.createUserAccountModel(structs[i], isNew);
            }
        }
        return models;
    }

    // AlertEventServiceAPI Impl
    public void alert(AlertStruct alertStruct)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        alertEventService.alert(alertStruct);
    }

    public void createSatisfactionAlert(SatisfactionAlertStruct satisfactionAlertStruct)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        alertEventService.createSatisfactionAlert(satisfactionAlertStruct);
    }

    public AlertHistoryStruct getAlerts(AlertSearchCriteriaStruct alertSearchCriteriaStruct)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.ALERTS))
        {
            GUILoggerHome.find().debug("getAlerts", GUILoggerSABusinessProperty.ALERTS, alertSearchCriteriaStruct);
        }
        return alertEventService.getAlerts(alertSearchCriteriaStruct);
    }

    public SatisfactionAlertStruct getSatisfactionAlertById(CboeIdStruct cboeIdStruct)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        return alertEventService.getSatisfactionAlertById(cboeIdStruct);
    }

    public void resolveAlert(CboeIdStruct cboeIdStruct, String tflUserId, String resolution, String comments)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        alertEventService.resolveAlert(cboeIdStruct, tflUserId, resolution, comments);
    }

    public void subscribeAlertEvent(AlertConsumer alertConsumer)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        alertEventService.subscribeAlertEvent(alertConsumer);
    }

    public void unsubscribeAlertEvent(AlertConsumer alertConsumer)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        alertEventService.unsubscribeAlertEvent(alertConsumer);
    }

    public void subscribeAlertEvent(EventChannelListener clientListener)
            throws DataValidationException, AuthorizationException, CommunicationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.ALERTS))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribeAlertEvent", GUILoggerSABusinessProperty.ALERTS);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_ALERT, new Integer(0));
        int subscriptionCount1 = SubscriptionManagerFactory.find().subscribe(key, clientListener, alertConsumer);
        key = new ChannelKey(ChannelType.CB_ALERT_SATISFACTION, new Integer(0));
        int subscriptionCount2 = SubscriptionManagerFactory.find().subscribe(key, clientListener, alertConsumer);
        key = new ChannelKey(ChannelType.CB_ALERT_UPDATE, new Integer(0));
        int subscriptionCount3 = SubscriptionManagerFactory.find().subscribe(key, clientListener, alertConsumer);
        if (subscriptionCount1 == 1 && subscriptionCount2 == 1 && subscriptionCount3 == 1)
        {
            // subscribe the consumer to the CAS
            subscribeAlertEvent(alertConsumer);
        }
    }

    public void unsubscribeAlertEvent(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.ALERTS))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":unsubscribeAlertEvent", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_ALERT, new Integer(0));
        int subscriptionCount1 = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, alertConsumer);
        key = new ChannelKey(ChannelType.CB_ALERT_SATISFACTION, new Integer(0));
        int subscriptionCount2 = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, alertConsumer);
        key = new ChannelKey(ChannelType.CB_ALERT_UPDATE, new Integer(0));
        int subscriptionCount3 = SubscriptionManagerFactory.find().unsubscribe(key, clientListener, alertConsumer);
        if (subscriptionCount1 == 0 && subscriptionCount2 == 0 && subscriptionCount3 == 0)
        {
            // unsubscribe the consumer to the CAS
            unsubscribeAlertEvent(alertConsumer);
        }
    }

    public AlertHistory getAlerts(AlertSearchCriteria alertSearchCriteria)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        return AlertHistoryFactory.createAlertHistory(getAlerts(alertSearchCriteria.toStruct()));
    }

    public SatisfactionAlert getSatisfactionAlertById(CBOEId cboeId)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        return SatisfactionAlertFactory.createSatisfactionAlert(getSatisfactionAlertById(cboeId.getStruct()));
    }

    public void resolveAlert(CBOEId cboeId, String resolution, String comments)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        resolveAlert(cboeId.getStruct(), UserSessionFactory.findUserSession().getUserModel().getUserId(), resolution, comments);
    }

    /**
     * Adds a list of events to the specified days.
     *
     * @param dateTypeStructs The list of dateTypeStructs containing the day and event type.
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public void addDay(DateTypeStruct[] dateTypeStructs)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": addDay", GUILoggerSABusinessProperty.CALENDAR_ADMIN, dateTypeStructs);
        }
        calendarAdminEventService.addDay(dateTypeStructs);
    }

    /**
     * Remove a list of events from the specified days.
     *
     * @param dateTypeStructs The list of dateTypeStructs containing the day and event type.
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @throws NotAcceptedException
     * @throws TransactionFailedException
     */
    public void removeDay(DateTypeStruct[] dateTypeStructs)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": removeDay", GUILoggerSABusinessProperty.CALENDAR_ADMIN, dateTypeStructs);
        }
        calendarAdminEventService.removeDay(dateTypeStructs);
    }

    /**
     * Gets a list of all calendar events and the days the occur on.
     *
     * @return DateTypeStruct  The list of calendar events and their days.
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public DateTypeStruct[] getDaysList()
           throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ": getDaysList", GUILoggerSABusinessProperty.CALENDAR_ADMIN);
        }
        return calendarAdminEventService.getDaysList();
    }


    /**
     * Subscribe for all changes to calendar events.
     *
     * @param clientListener the client listener to receive continued change events.
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public void subscribeCalendarUpdates(EventChannelListener clientListener)
           throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {


        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribeCalendarUpdates", GUILoggerSABusinessProperty.CALENDAR_ADMIN, clientListener);
        }

        ChannelKey updateKey = new ChannelKey(ChannelType.CB_CALENDAR_UPDATE, new Short(com.cboe.idl.calendar.UpdateTypes.ADD));
        ChannelKey removeKey = new ChannelKey(ChannelType.CB_CALENDAR_UPDATE, new Short(com.cboe.idl.calendar.UpdateTypes.REMOVE));

        int count1 = SubscriptionManagerFactory.find().subscribe(updateKey, clientListener, calendarUpdateConsumer);
        int count2 = SubscriptionManagerFactory.find().subscribe(removeKey, clientListener, calendarUpdateConsumer);

        if ((count1 == 1) && (count2 == 1))
        {
            calendarAdminEventService.subscribeCalendarUpdateEvent(calendarUpdateConsumer);
        }
    }

    /**
     * Unsubscribes for calendar update events.
     *
     * @param clientListener to remove from receiving calendar update events.
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public void unsubscribeCalendarUpdates(EventChannelListener clientListener)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":unsubscribeCalendarUpdates", GUILoggerSABusinessProperty.CALENDAR_ADMIN, clientListener);
        }
        ChannelKey updateKey = new ChannelKey(ChannelType.CB_CALENDAR_UPDATE, new Short(com.cboe.idl.calendar.UpdateTypes.ADD));
        ChannelKey removeKey = new ChannelKey(ChannelType.CB_CALENDAR_UPDATE, new Short(com.cboe.idl.calendar.UpdateTypes.REMOVE));

        int count1 = SubscriptionManagerFactory.find().unsubscribe(updateKey, clientListener, calendarUpdateConsumer);
        int count2 = SubscriptionManagerFactory.find().unsubscribe(removeKey, clientListener, calendarUpdateConsumer);

        if ((count1 == 1) && (count2 == 1))
        {
            calendarAdminEventService.unsubscribeCalendarUpdateEvent(calendarUpdateConsumer);
        }
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getETFFlagForAllClasses(java.lang.String)
     */
    public BooleanStruct[] getETFFlagForAllClasses(String sessionName) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getETFFlagForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getETFFlagForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getFCQSForAllClasses(java.lang.String)
     */
    public LongStruct[] getFCQSForAllClasses(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFCQSForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getFCQSForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getFPQSForAllClasses(java.lang.String)
     */
    public LongStruct[] getFPQSForAllClasses(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getFPQSForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getFPQSForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getIPPToleranceAmountForAllClasses(java.lang.String)
     */
    public DoubleStruct[] getIPPToleranceAmountForAllClasses(String sessionName) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getIPPToleranceAmountForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getIPPToleranceAmountForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getLinkageEnabledFlagForAllClasses(java.lang.String)
     */
    public BooleanStruct[] getLinkageEnabledFlagForAllClasses(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getLinkageEnabledFlagForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getLinkageEnabledFlagForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getLotSizeForAllClasses(java.lang.String)
     */
    public LongStruct[] getLotSizeForAllClasses(String sessionName) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getLotSizeForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getLotSizeForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getNeedsDpmQuoteToOpenForAllClasses(java.lang.String)
     */
    public BooleanStruct[] getNeedsDpmQuoteToOpenForAllClasses(String sessionName) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getNeedsDpmQuoteToOpenForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getNeedsDpmQuoteToOpenForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getPAOrderTimeToLiveForAllClasses(java.lang.String)
     */
    public LongStruct[] getPAOrderTimeToLiveForAllClasses(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPAOrderTimeToLiveForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getPAOrderTimeToLiveForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getPOrderTimeToLiveForAllClasses(java.lang.String)
     */
    public LongStruct[] getPOrderTimeToLiveForAllClasses(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPOrderTimeToLiveForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getPOrderTimeToLiveForAllClasses(sessionName);

   }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getProductOpenProcedureTypeForAllClasses(java.lang.String)
     */
    public LongStruct[] getProductOpenProcedureTypeForAllClasses(String sessionName) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductOpenProcedureTypeForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getProductOpenProcedureTypeForAllClasses(sessionName);
   }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getSOrderTimeToCreateBeforeCloseForAllClasses(java.lang.String)
     */
    public LongStruct[] getSOrderTimeToCreateBeforeCloseForAllClasses(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSOrderTimeToCreateBeforeCloseForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getSOrderTimeToCreateBeforeCloseForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getSOrderTimeToCreateForAllClasses(java.lang.String)
     */
    public LongStruct[] getSOrderTimeToCreateForAllClasses(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSOrderTimeToCreateForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getSOrderTimeToCreateForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getSOrderTimeToLiveForAllClasses(java.lang.String)
     */
    public LongStruct[] getSOrderTimeToLiveForAllClasses(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSOrderTimeToLiveForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getSOrderTimeToLiveForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getSOrderTimeToRejectFillForAllClasses(java.lang.String)
     */
    public LongStruct[] getSOrderTimeToRejectFillForAllClasses(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSOrderTimeToRejectFillForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getSOrderTimeToRejectFillForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getSatisfactionAlertFlagForAllClasses(java.lang.String)
     */
    public BooleanStruct[] getSatisfactionAlertFlagForAllClasses(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSatisfactionAlertFlagForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getSatisfactionAlertFlagForAllClasses(sessionName);
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.internalBusinessServices.TradingPropertyServiceExtOperations#getTradeTypeForAllClasses(java.lang.String)
     */
    public LongStruct[] getTradeTypeForAllClasses(String sessionName) throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradeTypeForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY);
           }
        return tradingProperty.getTradeTypeForAllClasses(sessionName);
    }

    public ErrorCodeResultStruct[] setProductStatesForSessionForGroup(String sessionName, String groupName, short newState)
        throws DataValidationException, TransactionFailedException, CommunicationException, AuthorizationException, SystemException
    {
        Object argObj[] = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = groupName;
            argObj[2] = formatProductState(newState);
        }

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStatesForSessionForGroup", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        ErrorCodeResultStruct[] errorStructs = tradingSessionEventStateService.setProductStatesForSessionForGroup(sessionName, groupName, newState);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStatesForSessionForGroup", GUILoggerSABusinessProperty.TRADING_SESSION, errorStructs);
        }

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION), "setProductStatesForSessionForGroup", argObj);
        }

        return errorStructs;
    }

    public GroupErrorCodeResultStruct[] setProductStatesForSessionV2(String sessionName, short newState)
        throws DataValidationException, TransactionFailedException, CommunicationException, AuthorizationException, SystemException
    {
        Object argObj[] = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = formatProductState(newState);
        }

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStatesForSessionV2", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        GroupErrorCodeResultStruct[] errorStructs = tradingSessionEventStateService.setProductStatesForSessionV2(sessionName, newState);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStatesForSessionV2", GUILoggerSABusinessProperty.TRADING_SESSION, errorStructs);
        }

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION), "setProductStatesForSessionV2", argObj);
        }

        return errorStructs;
    }

    public GroupErrorCodeResultStruct[] setProductStatesForElementV2(int elementKey, short newState)
        throws DataValidationException, TransactionFailedException, CommunicationException, AuthorizationException, SystemException
    {
        Object argObj[] = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = new Integer(elementKey);
            argObj[1] = formatProductState(newState);
        }

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStatesForElementV2", GUILoggerSABusinessProperty.TRADING_SESSION, argObj);
        }

        GroupErrorCodeResultStruct[] errorStructs = tradingSessionEventStateService.setProductStatesForElementV2(elementKey, newState);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStatesForElementV2", GUILoggerSABusinessProperty.TRADING_SESSION, errorStructs);
        }

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_SESSION), "setProductStatesForElementV2", argObj);
        }

        return errorStructs;
    }

    public TradingSessionGroupStruct[] getTradingSessionRoutingGroupNames()
            throws DataValidationException, AuthorizationException, TransactionFailedException, CommunicationException, SystemException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessionRoutingGroupNames",
                                       GUILoggerSABusinessProperty.TRADING_SESSION);
        }

        TradingSessionGroupStruct[] retStructs = tradingSessionService.getTradingSessionRoutingGroupNames();

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getTradingSessionRoutingGroupNames",
                                       GUILoggerSABusinessProperty.TRADING_SESSION, "Completed");
        }

        return retStructs;
    }

    public ErrorCodeResultStruct setProductStateByClassV2(String sessionName, int classKey, short productState)
        throws DataValidationException, TransactionFailedException, CommunicationException, AuthorizationException, SystemException
    {
        Object argObj[] = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = formatProductState(productState);
        }

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStateByClassV2", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }

        ErrorCodeResultStruct errorStruct = productStateService.setProductStateByClassV2(sessionName, classKey, productState);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setProductStateByClassV2", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, errorStruct);
        }

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE), "setProductStateByClassV2", argObj);
        }

        return errorStruct;
    }

    public void publishProductStatesForClasses(int[] classKeys, String sessionName, short publishTo)
        throws CommunicationException, AuthorizationException, SystemException, DataValidationException
    {
        Object argObj[] = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = classKeys;
            argObj[2] = ProgramInterfaceTypes.toString(publishTo);
        }

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishProductStatesForClasses", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }

        productStateService.publishProductStatesForClasses(classKeys, sessionName, publishTo);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishProductStatesForClasses", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, "Completed");
        }

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE), "publishProductStatesForClasses", argObj);
        }
    }

    public void publishProductStatesForSession(String sessionName, short publishTo)
        throws CommunicationException, AuthorizationException, SystemException, DataValidationException
    {
        Object argObj[] = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[2];
            argObj[0] = sessionName;
            argObj[1] = ProgramInterfaceTypes.toString(publishTo);
        }

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishProductStatesForSession", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }

        productStateService.publishProductStatesForSession(sessionName, publishTo);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishProductStatesForSession", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, "Completed");
        }

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE), "publishProductStatesForSession", argObj);
        }
    }

    public void publishProductStatesForGroupForSession(String groupName, String sessionName, short publishTo)
        throws CommunicationException, AuthorizationException, SystemException, DataValidationException
    {
        Object argObj[] = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = groupName;
            argObj[2] = ProgramInterfaceTypes.toString(publishTo);
        }

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishProductStatesForGroupForSession", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }

        productStateService.publishProductStatesForGroupForSession(groupName, sessionName, publishTo);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":publishProductStatesForGroupForSession", GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, "Completed");
        }

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE), "publishProductStatesForGroupForSession", argObj);
        }
    }


    public int getQuoteLockMinimumTradeQuantity(String sessionName, int classKey, IntHolder seq)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seq;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getQuoteLockMinimumTradeQuantity", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        return tradingProperty.getQuoteLockMinimumTradeQuantity(sessionName, classKey, seq);
    }

    public LongStruct[] getQuoteLockMinimumTradeQuantityAllClasses(String sessionName)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getQuoteLockMinimumTradeQuantityAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }

        return tradingProperty.getQuoteLockMinimumTradeQuantityAllClasses(sessionName);
    }

    public void setQuoteLockMinimumTradeQuantity(String sessionName, int classKey, int size, int seq)
            throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException,
                   DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Integer(size);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setQuoteLockMinimumTradeQuantity",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        String propertyName = TradingPropertyTypeImpl.QUOTE_LOCK_MIN_TRADE_QTY.getName();
        String oldQuoteLockQty = TradingPropertyCentralLogger.getFormattedTradingProperties(
                sessionName, classKey, propertyName);

        tradingProperty.setQuoteLockMinimumTradeQuantity(sessionName, classKey, size, seq);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setQuoteLockMinimumTradeQuantity", argObj);
            String messageText = TradingPropertyCentralLogger.getMessageText(
                    TradingPropertyCentralLogger.OPERATION_ADD_UPDATE, oldQuoteLockQty, sessionName,
                    classKey, propertyName);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public double getPreferredDpmRightsRate(String sessionName, int classKey, IntHolder seqHolder)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = seqHolder;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPreferredDpmRightsRate", GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        double retValue = tradingProperty.getPreferredDpmRightsRate(sessionName, classKey, seqHolder);

        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPreferredDpmRightsRate", GUILoggerSABusinessProperty.TRADING_PROPERTY, new Double(retValue));
        }

        return retValue;
    }

    public void setPreferredDpmRightsRate(String sessionName, int classKey, double splitRate, int seq)
            throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException,
                   DataValidationException
    {
        Object[] argObj = null;
        if (GUILoggerHome.find().isDebugOn() || GUILoggerHome.find().isAuditOn())
        {
            argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = new Integer(classKey);
            argObj[2] = new Double(splitRate);
            argObj[3] = new Integer(seq);
        }
        if (GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setPreferredDpmRightsRate",
                                       GUILoggerSABusinessProperty.TRADING_PROPERTY, argObj);
        }

        String propertyName = TradingPropertyTypeImpl.PDPM_RIGHTS_SCALES.getName();
        String oldQuoteLockQty = TradingPropertyCentralLogger
                .getFormattedTradingProperties(sessionName, classKey, propertyName);

        tradingProperty.setPreferredDpmRightsRate(sessionName, classKey, splitRate, seq);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.TRADING_PROPERTY),
                                       "setPreferredDpmRightsRate", argObj);
            String messageText = TradingPropertyCentralLogger.getMessageText(
                    TradingPropertyCentralLogger.OPERATION_ADD_UPDATE, oldQuoteLockQty, sessionName,
                    classKey, propertyName);
            GUILoggerHome.find().nonRepudiationAudit(TradingPropertyCentralLogger.TRADING_PROPERTY_AUDIT, messageText);
        }
    }

    public DoubleStruct[] getPreferredDpmRightsRateForAllClasses(String sessionName)
                throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_PROPERTY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPreferredDpmRightsRateForAllClasses", GUILoggerSABusinessProperty.TRADING_PROPERTY, sessionName);
        }

        return tradingProperty.getPreferredDpmRightsRateForAllClasses(sessionName);
    }

    public UserFirmAffiliation[] getUsersForAffiliatedFirm(String firm)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getUsersForAffiliatedFirm",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, firm);
        }

        UserFirmAffiliationStruct[] structs = userMaintenanceEvent.getUsersForAffiliatedFirm(firm);
        return UserFirmAffiliationFactory.create(structs);
    }

    public UserFirmAffiliation setAffiliatedFirmForUser(UserFirmAffiliation userFirmAffiliation)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        UserFirmAffiliationStruct userFirmAffiliationStruct = userFirmAffiliation.getUserFirmAffiliationStruct();

        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":setAffiliatedFirmForUser",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, userFirmAffiliationStruct);
        }

        UserFirmAffiliationStruct newStruct = userMaintenanceEvent.setAffiliatedFirmForUser(userFirmAffiliationStruct);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_MANAGEMENT),
                                       "setAffiliatedFirmForUser", newStruct);
        }

        return UserFirmAffiliationFactory.create(newStruct);
    }

    public UserFirmAffiliation[] getAllUserAffiliatedFirms()
            throws SystemException, CommunicationException, AuthorizationException
    {
        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAllUserAffiliatedFirms",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, "");
        }

        UserFirmAffiliationStruct[] structs = userMaintenanceEvent.getAllUserAffiliatedFirms();
        return UserFirmAffiliationFactory.create(structs);
    }

    public UserFirmAffiliation getAffiliatedFirmForUser(ExchangeAcronym exchangeAcronym)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   NotFoundException
    {
        ExchangeAcronymStruct exchangeAcronymStruct = exchangeAcronym.getExchangeAcronymStruct();

        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getAffiliatedFirmForUser",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, exchangeAcronymStruct);
        }

        UserFirmAffiliationStruct struct = userMaintenanceEvent.getAffiliatedFirmForUser(exchangeAcronymStruct);
        return UserFirmAffiliationFactory.create(struct);
    }

    public void deleteUserFirmAffiliation(UserFirmAffiliation userFirmAffiliation)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException
    {
        UserFirmAffiliationStruct userFirmAffiliationStruct = userFirmAffiliation.getUserFirmAffiliationStruct();

        if (GUILoggerHome.find().isDebugOn() &&
            GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":deleteUserFirmAffiliation",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, userFirmAffiliationStruct);
        }

        userMaintenanceEvent.deleteUserFirmAffiliation(userFirmAffiliationStruct);

        if (GUILoggerHome.find().isAuditOn())
        {
            GUILoggerHome.find().audit(formatLogProperty(GUILoggerSABusinessProperty.USER_MANAGEMENT),
                                       "deleteUserFirmAffiliation", "deleted...");
        }
    }

    public List<AffiliatedFirmAcronym> getAffiliatedFirms()
    {
        List<AffiliatedFirmAcronym> retval;
        if(affiliatedFirmCache != null)
        {
            retval = affiliatedFirmCache.getAffiliatedFirmAcronyms();
        }
        else
        {
            retval = new ArrayList<AffiliatedFirmAcronym>(0);
        }
        return retval;
    }

    //***********    Group Service Method   *************************************************************************/

    /**
     * Method used to initiate the group service cache initialization.
     *
     * @throws CacheInitializationException exception during cache initialization.
     */
    public void initializeGroupServiceCache() throws CacheInitializationException
    {
        if (groupElementCache == null)
        {
            groupElementCache = new GroupElementCacheGuiImpl(groupService, this, eventChannel);
        }
        groupElementCache.initializeCache();
    }


    /**
     * method to return root group element for a group type.
     */
    public GroupElementModel getRootGroupForGroupType(short groupType)
    {
        return groupElementCache.getRootGroupForGroupType(groupType);
    }

    /**
     * method to return list of all groups in the tree/cloud.
     */
    public Set<GroupElementModel> getAllGroupsForGroupType(short groupType)
    {
        return groupElementCache.getAllGroupsForGroupType(groupType);
    }

    /**
     * method to return immediate sub groups for a group.
     */
    public Set<GroupElementModel> getSubgroupsForGroup(long groupKey)
    {
        return groupElementCache.getSubGroupsForGroup(groupKey);
    }

    /**
     * method to return parent groups for a group element.
     */
    public Set<GroupElementModel> getParentsForGroupElement(long elementKey)
    {
        return groupElementCache.getParentGroupsForGroupElement(elementKey);
    }

    /**
     * Method to return all the immediate/direct leaf childers for a group.
     */
    public Set<GroupElementModel> getLeafElementsForGroup(long groupKey)
    {
        return groupElementCache.getLeafElementsForGroup(groupKey);
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
     * Methos used to convert the model object into structs and call the service.
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
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + "createElementsForGroup: groupKey:",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, groupKey);
            guiLogger.debug(TRANSLATOR_NAME + "createElementsForGroup: elements:",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, newElements);
        }

        ElementErrorResultStruct[] resultStructs = groupService.createElementsForGroup(groupKey, newElements);

        StringBuilder errorString = new StringBuilder(256);
        Set<GroupElementModel> alreadyExistElements = new HashSet<GroupElementModel>();
        for (int j = 0; j < resultStructs.length; j++)
        {
            ElementErrorResultStruct resultStruct = resultStructs[j];
            GroupElementModel elementModel = new GroupElementModelImpl(resultStruct.elementStruct);
            // if successful, convert returned struct to model object and add to cache
            if (resultStruct.errorCode != 1)
            {
                if (DataValidationCodes.GROUPELEMENT_ALREADY_EXISTS == resultStruct.errorCode)
                {
                    elementModel = groupElementCache.findElementKeyByUniqueSearchHashCode(elementModel);
                    if (elementModel != null)
                    {
                        alreadyExistElements.add(elementModel);
                    }
                    else
                    {
                        errorString.append(resultStruct.errorMessage).append("\n");
                    }
                }
                else
                {
                    errorString.append(resultStruct.errorMessage).append("\n");
                }
            }
        }

        // if there is any already existing element call addElementsToGroup
        if (alreadyExistElements.size() > 0)
        {
            try
            {
                addElementsToGroup(groupKey, alreadyExistElements);
            }
            catch (DataValidationException e)
            {
                errorString.append(e.getMessage()).append("\n");
            }
        }

        // if there is any error from the service, generate DataValidationException
        if (errorString.length() > 0)
        {
            throw ExceptionBuilder.dataValidationException(errorString.toString(), 0);
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
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + "addElementsToGroup: groupKey:",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, groupKey);
            guiLogger.debug(TRANSLATOR_NAME + "addElementsToGroup: elements:",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, elements);
        }

        ErrorStruct[] resultStructs = groupService.addElementsToGroup(groupKey, elements);
        StringBuilder errorString = new StringBuilder(256);
        List<GroupElementModel> notFoundElements = new ArrayList<GroupElementModel>();
        for (int j = 0; j < resultStructs.length; j++)
        {
            // if successful, add to cache
            GroupElementModel elementModel = groupElementCache.getGroupElementByElementKey(elements[j]);
            ErrorStruct resultStruct = resultStructs[j];
            if (resultStruct.errorCode != 1)
            {
                if (DataValidationCodes.INVALID_GROUPELEMENT == resultStruct.errorCode &&
                        groupElementCache.findElementKeyByUniqueSearchHashCode(elementModel) == null)
                {
                    notFoundElements.add(elementModel);
                }
                else
                {
                    errorString.append(resultStruct.errorMessage).append("\n");
                }
            }
        }

        // check if notFound elements are present than call createElementsForGroup
        if (notFoundElements.size() > 0)
        {
            try
            {
                createElementsForGroup(groupKey, notFoundElements);
            }
            catch (DataValidationException e)
            {
                errorString.append(e.getMessage()).append("\n");
            }
        }

        // if there is any error from the service, generate DataValidationException
        if (errorString.length() > 0)
        {
            throw ExceptionBuilder.dataValidationException(errorString.toString(), 0);
        }

    }

    /**
     * Methos used to convert the model object into structs and call the rename service.
     */
    public void  updateElement(GroupElementModel groupElement)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException
    {
        ElementStruct renameElement = groupElement.toElementStruct();
        StringBuilder errorString = new StringBuilder(100);

        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + "updateElement: groupKey:",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, renameElement);
        }
        try
        {
            // calling update element service.
            ElementStruct resultStruct = groupService.updateElement(renameElement);
        }
        catch (AlreadyExistsException e)
        {
            errorString.append(e.details.message);
        }
        catch (DataValidationException dve)
        {
            errorString.append(dve.details.message);
        }
        // if there is any error from the service, generate DataValidationException
        if (errorString.length() > 0)
        {
            throw ExceptionBuilder.dataValidationException(errorString.toString(), 0);
        }
    }


    /**
     * Methos used to convert the model object into structs and call the service.
     */
    public void cloneGroup(long parentGroupKey, GroupElementModel groupElement, long groupKey)
            throws SystemException, TransactionFailedException, CommunicationException, AuthorizationException, DataValidationException
    {
        ElementEntryStruct[] newElements = new ElementEntryStruct[1];
        newElements[0] = groupElement.toElementEntryStruct();

        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + "cloneGroup: parentGroupKey:",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, parentGroupKey);
            guiLogger.debug(TRANSLATOR_NAME + "cloneGroup: groupKey:",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, groupKey);
            guiLogger.debug(TRANSLATOR_NAME + "cloneGroup: groupElement:",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, newElements);
        }

        ElementErrorResultStruct[] resultStructs = groupService.createElementsForGroup(parentGroupKey, newElements);
        StringBuilder errorString = new StringBuilder(256);
        for (int j = 0; j < resultStructs.length; j++)
        {
            ElementErrorResultStruct resultStruct = resultStructs[j];
            if (resultStruct.errorCode != 1)
            {
                errorString.append(resultStruct.errorMessage).append("\n");
            }
            else
            {
                Set<GroupElementModel> elements = groupElementCache.getElementsForGroup(groupKey);
                if (!elements.isEmpty())
                {
                    addElementsToGroup(resultStruct.elementStruct.elementKey, elements);
                }
            }
        }
        // if there is any error from the service, generate DataValidationException
        if (errorString.length() > 0)
        {
            throw ExceptionBuilder.dataValidationException(errorString.toString(), 0);
        }
    }

    /**
     * Methos used to convert the model object into structs and call the service.
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
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + "removeElementsFromGroup: groupKey:",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, groupKey);
            guiLogger.debug(TRANSLATOR_NAME + "removeElementsFromGroup: elements:",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, elements);
        }
        ErrorStruct[] resultStructs = groupService.removeElementsFromGroup(groupKey, elements);

        for (int j = 0; j < resultStructs.length; j++)
        {
            if (resultStructs[j].errorCode != 1)
            {
                errorString.append(resultStructs[j].errorMessage).append("\n");
            }
        }

        // if there is any error from the service, generate DataValidationException
        if (errorString.length() > 0)
        {
            throw ExceptionBuilder.dataValidationException(errorString.toString(), 0);
        }

    }


    public void subscribeGroupElementEvent(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + "subscribeGroupElementEvent",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, clientListener.getClass().getName());

        }

        SubscriptionManagerImpl subscriptionManager = SubscriptionManagerFactory.find();
        ChannelKey myChannelKey = new ChannelKey(ChannelType.GROUP_ADD_ELEMENT, new Integer(0));
        int key1 = subscriptionManager.subscribe(myChannelKey, clientListener, groupElementConsumer);

        myChannelKey = new ChannelKey(ChannelType.GROUP_UPDATE_ELEMENT, new Integer(0));
        int key2 = subscriptionManager.subscribe(myChannelKey, clientListener, groupElementConsumer);

        myChannelKey = new ChannelKey(ChannelType.GROUP_REMOVE_ELEMENT, new Integer(0));
        int key3 = subscriptionManager.subscribe(myChannelKey, clientListener, groupElementConsumer);

        if (key1 == 1 && key2 == 1 && key3 == 1)
        {
            groupElementEvent.subscribeGroupElementEvent(groupElementConsumer);
        }
    }

    public void unsubscribeGroupElementEvent(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {

        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + "unsubscribeGroupElementEvent",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, clientListener.getClass().getName());

        }

        SubscriptionManagerImpl subscriptionManager = SubscriptionManagerFactory.find();
        ChannelKey myChannelKey = new ChannelKey(ChannelType.GROUP_ADD_ELEMENT, new Integer(0));
        int key1 = subscriptionManager.unsubscribe(myChannelKey, clientListener, groupElementConsumer);

        myChannelKey = new ChannelKey(ChannelType.GROUP_UPDATE_ELEMENT, new Integer(0));
        int key2 = subscriptionManager.unsubscribe(myChannelKey, clientListener, groupElementConsumer);

        myChannelKey = new ChannelKey(ChannelType.GROUP_REMOVE_ELEMENT, new Integer(0));
        int key3 = subscriptionManager.unsubscribe(myChannelKey, clientListener, groupElementConsumer);

        if (key1 == 0 && key2 == 0 && key3 == 0)
        {
            groupElementEvent.unsubscribeGroupElementEvent(groupElementConsumer);
        }
    }

    public void subscribeGroupElementCacheEvents(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + "subscribeGroupElementCacheEvents",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, clientListener.getClass().getName());

        }

        SubscriptionManagerImpl subscriptionManager = SubscriptionManagerFactory.find();
        ChannelKey myChannelKey = new ChannelKey(ChannelType.CB_GROUP_ADD_ELEMENT, new Integer(0));
        int key1 = subscriptionManager.subscribe(myChannelKey, clientListener, groupElementConsumer);

        myChannelKey = new ChannelKey(ChannelType.CB_GROUP_UPDATE_ELEMENT, new Integer(0));
        int key2 = subscriptionManager.subscribe(myChannelKey, clientListener, groupElementConsumer);

        myChannelKey = new ChannelKey(ChannelType.CB_GROUP_REMOVE_ELEMENT, new Integer(0));
        int key3 = subscriptionManager.subscribe(myChannelKey, clientListener, groupElementConsumer);

        if (key1 == 1 && key2 == 1 && key3 == 1)
        {
            groupElementEvent.subscribeGroupElementEvent(groupElementConsumer);
        }
    }

    public void unsubscribeGroupElementCacheEvents(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + "unsubscribeGroupElementCacheEvents",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, clientListener.getClass().getName());

        }

        ChannelKey myChannelKey = new ChannelKey(ChannelType.CB_GROUP_ADD_ELEMENT, new Integer(0));
        int key1 = SubscriptionManagerFactory.find().unsubscribe(myChannelKey, clientListener, groupElementConsumer);

        myChannelKey = new ChannelKey(ChannelType.CB_GROUP_UPDATE_ELEMENT, new Integer(0));
        int key2 = SubscriptionManagerFactory.find().unsubscribe(myChannelKey, clientListener, groupElementConsumer);

        myChannelKey = new ChannelKey(ChannelType.CB_GROUP_REMOVE_ELEMENT, new Integer(0));
        int key3 = SubscriptionManagerFactory.find().unsubscribe(myChannelKey, clientListener, groupElementConsumer);

        if (key1 == 0 && key2 == 0 && key3 == 0)
        {
            groupElementEvent.unsubscribeGroupElementEvent(groupElementConsumer);
        }


    }

    /**
     * GUI will invoke this method so that the order cancel request will be routed to FE
     */
    public ServerResponseStruct[] cancelOrderForUsers(RoutingParameterStruct routingParams, String userIdRequestingCancel,
                                                       String[] userIds, String transactionId,DateTimeStruct timestamp, KeyValueStruct[] properties)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException,
                   NotAcceptedException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn())
        {
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsers : routingParams", GUILoggerSABusinessProperty.ORDER_HANDLING, routingParams);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsers : userIdRequestingCancel", GUILoggerSABusinessProperty.ORDER_HANDLING, userIdRequestingCancel);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsers : userIds", GUILoggerSABusinessProperty.ORDER_HANDLING, userIds);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsers : transactionId", GUILoggerSABusinessProperty.ORDER_HANDLING, transactionId);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsers : timestamp", GUILoggerSABusinessProperty.ORDER_HANDLING, timestamp);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsers : properties", GUILoggerSABusinessProperty.ORDER_HANDLING, properties);
        }
        return orderHandlingService.cancelOrderForUsers(routingParams, userIdRequestingCancel, userIds, transactionId, timestamp, properties,
                FederatedOperationType.ALL_ORDERS);
    }

    public ServerResponseStruct[] cancelOrderForUsersV2(String userIdRequestingCancel, String[] userIds, String transactionId, DateTimeStruct timestamp, KeyValueStruct[] properties, short operationType, char[] orderTypes,
                                             String[] correspondentFirmValues, int[] classKeys, boolean currentDate)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException,
                   NotAcceptedException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn())
        {
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsersV2 : userIdRequestingCancel", GUILoggerSABusinessProperty.ORDER_HANDLING, userIdRequestingCancel);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsersV2 : userIds", GUILoggerSABusinessProperty.ORDER_HANDLING, userIds);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsersV2 : transactionId", GUILoggerSABusinessProperty.ORDER_HANDLING, transactionId);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsersV2 : timestamp", GUILoggerSABusinessProperty.ORDER_HANDLING, timestamp);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsersV2 : properties", GUILoggerSABusinessProperty.ORDER_HANDLING, properties);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsersV2 : operationType", GUILoggerSABusinessProperty.ORDER_HANDLING, operationType);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsersV2 : orderTypes", GUILoggerSABusinessProperty.ORDER_HANDLING, orderTypes);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsersV2 : correspondentFirmValues", GUILoggerSABusinessProperty.ORDER_HANDLING, correspondentFirmValues);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsersV2 : classKeys", GUILoggerSABusinessProperty.ORDER_HANDLING, classKeys);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrderForUsersV2 : currentDate", GUILoggerSABusinessProperty.ORDER_HANDLING, currentDate);
        }

        return orderHandlingService.cancelOrderForUsersV2(userIdRequestingCancel, userIds, transactionId, timestamp, properties, operationType,
                                                          orderTypes, correspondentFirmValues, classKeys, currentDate);
    }

    public ServerResponseStruct[] cancelOrdersForRoutingGroups( String userIdRequestingCancel, String transactionId, DateTimeStruct timestamp, KeyValueStruct[] properties, short operationType, char[] orderTypes,
                                             String[] serverRouteNames, boolean currentDate)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException,
                   NotAcceptedException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn())
        {
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForRoutingGroups : userIdRequestingCancel", GUILoggerSABusinessProperty.ORDER_HANDLING, userIdRequestingCancel);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForRoutingGroups : transactionId", GUILoggerSABusinessProperty.ORDER_HANDLING, transactionId);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForRoutingGroups : timestamp", GUILoggerSABusinessProperty.ORDER_HANDLING, timestamp);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForRoutingGroups : properties", GUILoggerSABusinessProperty.ORDER_HANDLING, properties);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForRoutingGroups : operationType", GUILoggerSABusinessProperty.ORDER_HANDLING, operationType);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForRoutingGroups : orderTypes", GUILoggerSABusinessProperty.ORDER_HANDLING, orderTypes);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForRoutingGroups : serverRouteNames", GUILoggerSABusinessProperty.ORDER_HANDLING, serverRouteNames);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForRoutingGroups : currentDate", GUILoggerSABusinessProperty.ORDER_HANDLING, currentDate);
        }
        return orderHandlingService.cancelOrdersForRoutingGroups(userIdRequestingCancel, transactionId, timestamp, properties, operationType, orderTypes, serverRouteNames, currentDate);
    }

    public ServerResponseStruct[] cancelOrdersForUser(String userIdRequestingCancel,String userId, String transactionId, DateTimeStruct timestamp,
                                             KeyValueStruct[] properties,short operationType, OrderIdStruct[] orderIdStructs, boolean currentDate)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException,
                   NotAcceptedException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.ORDER_HANDLING))
        {
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForUser : userIdRequestingCancel", GUILoggerSABusinessProperty.ORDER_HANDLING, userIdRequestingCancel);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForUser : userId", GUILoggerSABusinessProperty.ORDER_HANDLING, userId);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForUser : transactionId", GUILoggerSABusinessProperty.ORDER_HANDLING, transactionId);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForUser : timestamp", GUILoggerSABusinessProperty.ORDER_HANDLING, timestamp);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForUser : properties", GUILoggerSABusinessProperty.ORDER_HANDLING, properties);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForUser : operationType", GUILoggerSABusinessProperty.ORDER_HANDLING, operationType);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForUser : orderIdStructs", GUILoggerSABusinessProperty.ORDER_HANDLING, orderIdStructs);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForUser : currentDate", GUILoggerSABusinessProperty.ORDER_HANDLING, currentDate);
        }
        return orderHandlingService.cancelOrdersForUser(userIdRequestingCancel, userId, transactionId, timestamp, properties, operationType, orderIdStructs, currentDate);
    }

    /**
     * GUI will invoke this method so that the order count request will be routed to FE
     */
    public ServerResponseStruct[] getOrderCountForUsers(RoutingParameterStruct routingParams, String userIdRequestingGetCount,
                                                        String[] userIds, String transactionId,DateTimeStruct timestamp, KeyValueStruct[] properties)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException,
                   NotAcceptedException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn())
        {
            guiLogger.debug(TRANSLATOR_NAME + ":getOrderCountForUsers : routingParams", GUILoggerSABusinessProperty.ORDER_HANDLING, routingParams);
            guiLogger.debug(TRANSLATOR_NAME + ":getOrderCountForUsers : userIdRequestingCancel", GUILoggerSABusinessProperty.ORDER_HANDLING, userIdRequestingGetCount);
            guiLogger.debug(TRANSLATOR_NAME + ":getOrderCountForUsers : userIds", GUILoggerSABusinessProperty.ORDER_HANDLING, userIds);
            guiLogger.debug(TRANSLATOR_NAME + ":getOrderCountForUsers : transactionId", GUILoggerSABusinessProperty.ORDER_HANDLING, transactionId);
            guiLogger.debug(TRANSLATOR_NAME + ":getOrderCountForUsers : timestamp", GUILoggerSABusinessProperty.ORDER_HANDLING, timestamp);
            guiLogger.debug(TRANSLATOR_NAME + ":getOrderCountForUsers : properties", GUILoggerSABusinessProperty.ORDER_HANDLING, properties);
        }
        //return createServerResponseStructs();
        return orderHandlingService.getOrderCountForUsers(routingParams, userIdRequestingGetCount, userIds, transactionId, timestamp, properties,FederatedOperationType.ORDERS);
    }

    /**
     * GUI will invoke this method so that the I-order cancel request will be routed to FE
     */
    public ServerResponseStruct[] cancelIOrdersForUsers(RoutingParameterStruct routingParams, String userIdRequestingCancel,
                                                        String[] userIds, String transactionId,DateTimeStruct timestamp, KeyValueStruct[] properties)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException,
                   NotAcceptedException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn())
        {
            guiLogger.debug(TRANSLATOR_NAME + ":cancelIOrdersForUsers : routingParams", GUILoggerSABusinessProperty.ORDER_HANDLING, routingParams);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelIOrdersForUsers : userIdRequestingCancel", GUILoggerSABusinessProperty.ORDER_HANDLING, userIdRequestingCancel);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelIOrdersForUsers : userIds", GUILoggerSABusinessProperty.ORDER_HANDLING, userIds);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelIOrdersForUsers : transactionId", GUILoggerSABusinessProperty.ORDER_HANDLING, transactionId);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelIOrdersForUsers : timestamp", GUILoggerSABusinessProperty.ORDER_HANDLING, timestamp);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelIOrdersForUsers : properties", GUILoggerSABusinessProperty.ORDER_HANDLING, properties);
        }
        //return createServerResponseStructs();
        return orderHandlingService.cancelOrderForUsers(routingParams, userIdRequestingCancel, userIds, transactionId, timestamp, properties, FederatedOperationType.IORDERS);
    }

    /**
     * GUI will invoke this method so that the I-order count request will be routed to FE
     */
    public ServerResponseStruct[] getIOrderCountForUsers(RoutingParameterStruct routingParams, String userIdRequestingGetCount,
                                                         String[] userIds, String transactionId,DateTimeStruct timestamp, KeyValueStruct[] properties)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException,
                   NotAcceptedException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn())
        {
            guiLogger.debug(TRANSLATOR_NAME + ":getIOrderCountForUsers : routingParams", GUILoggerSABusinessProperty.ORDER_HANDLING, routingParams);
            guiLogger.debug(TRANSLATOR_NAME + ":getIOrderCountForUsers : userIdRequestingCancel", GUILoggerSABusinessProperty.ORDER_HANDLING, userIdRequestingGetCount);
            guiLogger.debug(TRANSLATOR_NAME + ":getIOrderCountForUsers : userIds", GUILoggerSABusinessProperty.ORDER_HANDLING, userIds);
            guiLogger.debug(TRANSLATOR_NAME + ":getIOrderCountForUsers : transactionId", GUILoggerSABusinessProperty.ORDER_HANDLING, transactionId);
            guiLogger.debug(TRANSLATOR_NAME + ":getIOrderCountForUsers : timestamp", GUILoggerSABusinessProperty.ORDER_HANDLING, timestamp);
            guiLogger.debug(TRANSLATOR_NAME + ":getIOrderCountForUsers : properties", GUILoggerSABusinessProperty.ORDER_HANDLING, properties);
        }
        //return createServerResponseStructs();
        return orderHandlingService.getOrderCountForUsers(routingParams, userIdRequestingGetCount, userIds, transactionId, timestamp, properties,FederatedOperationType.IORDERS);
    }

    public ServerResponseStruct[] cancelOrdersForUserByOrsId(String userIdRequestingCancel, String userId, String transactionId, DateTimeStruct timestamp,
                                                             KeyValueStruct[] auditLogProperties, short operationType, String[] orsIds)
            throws DataValidationException, CommunicationException, SystemException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        userIdRequestingCancel, userId, transactionId, timestamp,
                        auditLogProperties, operationType, orsIds
                };
        if(guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.ORDER_HANDLING))
        {
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForUserByOrsId", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.ORDER_HANDLING), "OrderHandlingService.cancelOrdersForUserByOrsId() params:", argObj);
        }

        ServerResponseStruct[] retVal = orderHandlingService.cancelOrdersForUserByOrsId(userIdRequestingCancel, userId, transactionId, timestamp,
                auditLogProperties, operationType, orsIds);

        //todo: audit log? -- why is there no audit logging in other OrderHandlingService cancel methods???
        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.ORDER_HANDLING), "OrderHandlingService.cancelOrdersForUserByOrsId() results for transactionID "+transactionId+":", retVal);
        }

        return retVal;
    }

    public ServerResponseStruct[] cancelOrdersForAllUsersByClass(String userIdRequestingCancel, String transactionId, DateTimeStruct timestamp, 
                                                                 KeyValueStruct[] auditLogProperties, short operationType,
                                                                 char[] orderTypes, String sessionName, int[] classKeys,
                                                                 boolean currentDate)
            throws DataValidationException, CommunicationException, SystemException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        userIdRequestingCancel, transactionId, timestamp, auditLogProperties,
                        operationType, orderTypes, sessionName, classKeys, currentDate
                };
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.ORDER_HANDLING))
        {
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForAllUsersByClass", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.ORDER_HANDLING), "OrderHandlingService.cancelOrdersForAllUsersByClass() params:", argObj);
        }

        ServerResponseStruct[] retVal = orderHandlingService.cancelOrdersForAllUsersByClass(userIdRequestingCancel,
                transactionId, timestamp, auditLogProperties, operationType, orderTypes, sessionName, classKeys, currentDate);

        //todo: audit log? -- why is there no audit logging in other OrderHandlingService cancel methods???
        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.ORDER_HANDLING), "OrderHandlingService.cancelOrdersForAllUsersByClass() results for transactionID " + transactionId + ":", retVal);
        }

        return retVal;
    }

    public ServerResponseStruct[] cancelOrdersForAllUsersByProduct(String userIdRequestingCancel, String transactionId, DateTimeStruct timestamp,
                                                                   KeyValueStruct[] auditLogProperties, short operationType, char[] orderTypes,
                                                                   String sessionName, int[] productKeys, boolean currentDate)
            throws DataValidationException, CommunicationException, SystemException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        Object[] argObj = new Object[]
                {
                        userIdRequestingCancel, transactionId, timestamp, auditLogProperties,
                        operationType, orderTypes, sessionName, productKeys, currentDate
                };
        if (guiLogger.isDebugOn() && guiLogger.isPropertyOn(GUILoggerSABusinessProperty.ORDER_HANDLING))
        {
            guiLogger.debug(TRANSLATOR_NAME + ":cancelOrdersForAllUsersByProduct", GUILoggerSABusinessProperty.ORDER_HANDLING, argObj);
        }

        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.ORDER_HANDLING), "OrderHandlingService.cancelOrdersForAllUsersByProduct() params:", argObj);
        }

        ServerResponseStruct[] retVal = orderHandlingService.cancelOrdersForAllUsersByProduct(userIdRequestingCancel,
                transactionId, timestamp, auditLogProperties, operationType, orderTypes, sessionName, productKeys, currentDate);

        //todo: audit log? -- why is there no audit logging in other OrderHandlingService cancel methods???
        if (guiLogger.isAuditOn())
        {
            guiLogger.audit(formatLogProperty(GUILoggerSABusinessProperty.ORDER_HANDLING), "OrderHandlingService.cancelOrdersForAllUsersByProduct() results for transactionID " + transactionId + ":", retVal);
        }

        return retVal;
    }

    /**
     * GUI will invoke this method so that the quote cancel request will be routed to FE
     */
    public ServerResponseStruct[] cancelQuotesForUsers(RoutingParameterStruct routingParams, String userIdRequestingCancel,
                                                       String[] userIds, String transactionId,DateTimeStruct timestamp, KeyValueStruct[] properties)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException,
                   NotAcceptedException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn())
        {
            guiLogger.debug(TRANSLATOR_NAME + ":cancelQuotesForUsers : routingParams", GUILoggerSABusinessProperty.ORDER_HANDLING, routingParams);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelQuotesForUsers : userIdRequestingCancel", GUILoggerSABusinessProperty.ORDER_HANDLING, userIdRequestingCancel);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelQuotesForUsers : userIds", GUILoggerSABusinessProperty.ORDER_HANDLING, userIds);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelQuotesForUsers : transactionId", GUILoggerSABusinessProperty.ORDER_HANDLING, transactionId);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelQuotesForUsers : timestamp", GUILoggerSABusinessProperty.ORDER_HANDLING, timestamp);
            guiLogger.debug(TRANSLATOR_NAME + ":cancelQuotesForUsers : properties", GUILoggerSABusinessProperty.ORDER_HANDLING, properties);
        }
        //return createServerResponseStructs();
        return marketMakerQuoteService.cancelQuotesForUsers(routingParams, userIdRequestingCancel, userIds, transactionId, timestamp, properties);
    }

    /**
     * GUI will invoke this method so that the quote count request will be routed to FE
     */
    public ServerResponseStruct[] getQuoteCountForUsers(RoutingParameterStruct routingParams, String userIdRequestingGetCount,
                                                        String[] userIds, String transactionId,DateTimeStruct timestamp, KeyValueStruct[] properties)
            throws SystemException, CommunicationException, DataValidationException, TransactionFailedException,
                   NotAcceptedException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn())
        {
            guiLogger.debug(TRANSLATOR_NAME + ":getQuoteCountForUsers : routingParams", GUILoggerSABusinessProperty.ORDER_HANDLING, routingParams);
            guiLogger.debug(TRANSLATOR_NAME + ":getQuoteCountForUsers : userIdRequestingCancel", GUILoggerSABusinessProperty.ORDER_HANDLING, userIdRequestingGetCount);
            guiLogger.debug(TRANSLATOR_NAME + ":getQuoteCountForUsers : userIds", GUILoggerSABusinessProperty.ORDER_HANDLING, userIds);
            guiLogger.debug(TRANSLATOR_NAME + ":getQuoteCountForUsers : transactionId", GUILoggerSABusinessProperty.ORDER_HANDLING, transactionId);
            guiLogger.debug(TRANSLATOR_NAME + ":getQuoteCountForUsers : timestamp", GUILoggerSABusinessProperty.ORDER_HANDLING, timestamp);
            guiLogger.debug(TRANSLATOR_NAME + ":getQuoteCountForUsers : properties", GUILoggerSABusinessProperty.ORDER_HANDLING, properties);
        }
        //return createServerResponseStructs();
        return marketMakerQuoteService.getQuoteCountForUsers(routingParams, userIdRequestingGetCount, userIds, transactionId, timestamp, properties);
    }

    /**
     * Implementation of subscription method defined in GroupServiceAPI
     */
    public void subscribeGroupCancelSummaryEvents(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + ":subscribeGroupCancelSummaryEvents",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, clientListener.getClass().getName());

        }

        ChannelKey myChannelKey = new ChannelKey(ChannelType.GROUP_CANCEL, new Integer(0));
        int key1 = SubscriptionManagerFactory.find().subscribe(myChannelKey, clientListener, systemControlConsumer);

        if (key1 == 1)
        {
            groupCancelEvent.subscribeGroupCancelSummaryEvents(systemControlConsumer);
        }
    }

    /**
     * Implementation of unsubscription method defined in GroupServiceAPI
     */
    public void unsubscribeGroupCancelSummaryEvents(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        IGUILogger guiLogger = GUILoggerHome.find();
        if (guiLogger.isDebugOn() &&
                guiLogger.isPropertyOn(GUILoggerSABusinessProperty.USER_MANAGEMENT))
        {
            guiLogger.debug(TRANSLATOR_NAME + ":unsubscribeGroupCancelSummaryEvents",
                                       GUILoggerSABusinessProperty.USER_MANAGEMENT, clientListener.getClass().getName());

        }

        ChannelKey myChannelKey = new ChannelKey(ChannelType.GROUP_CANCEL, new Integer(0));
        int key1 = SubscriptionManagerFactory.find().unsubscribe(myChannelKey, clientListener, systemControlConsumer);


        if (key1 == 0)
        {
            groupCancelEvent.unsubscribeGroupCancelSummaryEvents(systemControlConsumer);
        }
    }


    public void acceptIntermarketAdminMessage(String sessionName, int productKey, String destinationExchange, AdminStruct admin) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+":acceptIntermarketAdminMessage", GUILoggerSABusinessProperty.INTERMARKET_NBBO_AGENT, admin);
        }
        SysAdminIntermarketControlService intermarketControlService = getIntermarketControlService();
        systemAdminSessionManager.getSysAdminIntermarketControlService().acceptAdminMessage(sessionName,destinationExchange,productKey,admin);
    }

    public AdminStruct[] getIntermarketAdminMessage(String sessionName, int productKey, int adminMessageKey, String sourceExchange) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException {

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+":getIntermarketAdminMessage", GUILoggerSABusinessProperty.INTERMARKET_NBBO_AGENT, productKey+","+adminMessageKey+","+sourceExchange);
        }

        SysAdminIntermarketControlService intermarketControlService = getIntermarketControlService();
        return systemAdminSessionManager.getSysAdminIntermarketControlService().getAdminMessages(sessionName,productKey,adminMessageKey,sourceExchange);
    }

    public void subscribeAdminMessageByClass(String session, int classKey, EventChannelListener adminMessageListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+":subscribeAdminMessageByClass", GUILoggerSABusinessProperty.INTERMARKET_NBBO_AGENT, session+","+classKey);
        }


        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, new SessionKeyContainer(session, classKey));

        SysAdminIntermarketControlService intermarketControlService = getIntermarketControlService();
        if (SubscriptionManagerFactory.find().subscribe(key, adminMessageListener, intermarketAdminMessageConsumer) == 1) {
            systemAdminSessionManager.getSysAdminIntermarketControlService().subscribeITSAdminByClass(session,classKey, intermarketAdminMessageConsumer );
        }
    }

    public void subscribeAdminMessage(String session, EventChannelListener adminMessageListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+":subscribeAdminMessage", GUILoggerSABusinessProperty.INTERMARKET_NBBO_AGENT, session);
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, new SessionKeyContainer(session, 0));

        SysAdminIntermarketControlService intermarketControlService = getIntermarketControlService();
        if (SubscriptionManagerFactory.find().subscribe(key, adminMessageListener, intermarketAdminMessageConsumer) == 1) {
            systemAdminSessionManager.getSysAdminIntermarketControlService().subscribeITSAdmin(session,intermarketAdminMessageConsumer );
        }

    }

    public void unsubscribeAdminMessageByClass(String session, int classKey, EventChannelListener adminMessageListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+":unsubscribeAdminMessageByClass", GUILoggerSABusinessProperty.INTERMARKET_NBBO_AGENT, session+","+classKey);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, new SessionKeyContainer(session, classKey));

        SysAdminIntermarketControlService intermarketControlService = getIntermarketControlService();
        if (SubscriptionManagerFactory.find().unsubscribe(key, adminMessageListener, intermarketAdminMessageConsumer) == 0) {
            systemAdminSessionManager.getSysAdminIntermarketControlService().unsubscribeITSAdminByClass(session,classKey, intermarketAdminMessageConsumer );
        }

    }

    public void unsubscribeAdminMessage(String session, EventChannelListener adminMessageListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME+":unsubscribeAdminMessage", GUILoggerSABusinessProperty.INTERMARKET_NBBO_AGENT, session);
        }
        ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, new SessionKeyContainer(session, 0));

        SysAdminIntermarketControlService intermarketControlService = getIntermarketControlService();
        if (SubscriptionManagerFactory.find().unsubscribe(key, adminMessageListener, intermarketAdminMessageConsumer) == 0) {
            systemAdminSessionManager.getSysAdminIntermarketControlService().unsubscribeITSAdmin(session,intermarketAdminMessageConsumer );
        }

    }

    public int createSessionV2(java.lang.String userName, java.lang.String securitySessionId, java.lang.String sourceComponentName, boolean autoLogout)
         throws com.cboe.exceptions.CommunicationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.SystemException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.TransactionFailedException
    {
        return -1;
    }

    public void addComponent(java.lang.String componentName, int componentType, java.lang.String parentComponentName, int currentState)
         throws com.cboe.exceptions.CommunicationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.SystemException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.TransactionFailedException
    {

    }

     public void removeComponent(java.lang.String componentName, java.lang.String[] parentComponentNames)
         throws com.cboe.exceptions.CommunicationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.SystemException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.TransactionFailedException
     {

     }

    public com.cboe.idl.infrastructureServices.sessionManagementService.ComponentStruct[] getChildComponents(java.lang.String component, int depth)
         throws com.cboe.exceptions.CommunicationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.SystemException, com.cboe.exceptions.AuthorizationException
    {
        return null;
    }

    public void setComponentState(java.lang.String componentName, int state)
      throws com.cboe.exceptions.CommunicationException, com.cboe.exceptions.DataValidationException, com.cboe.exceptions.SystemException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.TransactionFailedException
    {
    }

    private SysAdminIntermarketControlService getIntermarketControlService() throws SystemException, CommunicationException, AuthorizationException
    {

        if (intermarketControlService == null)
        {
            intermarketControlService = systemAdminSessionManager.getSysAdminIntermarketControlService();
        }

        return intermarketControlService;
    }

    /**
     * Retrieves the underlying products corresponding to the exchange
     * @param exchange exchange for which  the underlying products to be obtained.
     * @return Product[]
     */
    public Product[] getUnderlyingProductsForExchange(Exchange exchange) throws SystemException,
            CommunicationException, AuthorizationException, DataValidationException,
            NotFoundException
    {
        if(exchange == null)
        {
            throw new IllegalArgumentException("Exchange parameter is null");
        }
        if(!exchangeToUnderlyingCacheInitialized)
        {
            initializeExchangeToUnderlyingProductsCache();
        }

        return exchangeProductMappingCache.getProductsForExchange(exchange.getExchange());
    }

    /**
     * Returns the set of Session Classes for the corresponding product key from productkey-SessionClass_set map
     * @param productKey product key for which the session class set is needed
     * @return Set<SessionProductClass>
     * @throws SystemException
     * @throws DataValidationException
     * @throws AuthorizationException
     * @throws CommunicationException
     */
    public Set<SessionProductClass> getSessionClassesForUnderlyingProductKey(int productKey) throws SystemException,
            DataValidationException,AuthorizationException, CommunicationException
    {
        if(!underlyingToClassCacheInitialized)
        {
            initializeUnderlyingProductToSessionClassesCache();
        }
        Set<SessionProductClass> sessionClasses = new HashSet<SessionProductClass>(SET_INITIAL_CAPACITY);
        Set<SessionProductClass> sessionClassSet;
        TradingSessionStruct[] sessions = getTradingSessions();
        for(TradingSessionStruct session : sessions)
        {
            sessionClassSet = SessionProductCacheFactory
                    .find(session.sessionName).getSessionClassesForUnderlyingProduct(productKey);
            if(sessionClassSet != null)
            {
                sessionClasses.addAll(sessionClassSet);
            }
        }

        return sessionClasses;
    }

    /**
     * Returns SessionProductClasses for the selected exchange
     * @param exchange - the exchange for which the session classes to be loaded.
     * @return SessionProductClass[] - session classes for the selected exchange.
     */
    public SessionProductClass[] getSessionClassesForSelectedExchange(Exchange exchange) throws SystemException,
            DataValidationException,AuthorizationException, CommunicationException,NotFoundException
    {
        Set<SessionProductClass> sessionProductClassSet = null;
        if(exchange != null)
        {
            Product[] products = getUnderlyingProductsForExchange(exchange);

            if(products != null && products.length > 0)
            {
                sessionProductClassSet = new HashSet<SessionProductClass>(SET_INITIAL_CAPACITY);
                for(Product product : products)
                {
                    sessionProductClassSet.addAll(getSessionClassesForUnderlyingProductKey(product.getProductKey()));
                }
            }
        }
        if(sessionProductClassSet == null)
        {
            sessionProductClassSet = Collections.emptySet();
        }

        return sessionProductClassSet.toArray(new SessionProductClass[sessionProductClassSet.size()]);
    }

    /**
     * Returns all session classes for the underlying product
     * @param product the underlying product
     * @return SessionProductClass[], all session classes corresponding to underlying product.
     */
    public SessionProductClass[] getSessionClassesForUnderlyingProduct(Product product) throws SystemException,
            DataValidationException,AuthorizationException, CommunicationException,NotFoundException
    {
        Set<SessionProductClass> sessionProductClassSet = null;
        if(product != null)
        {
            sessionProductClassSet = new HashSet<SessionProductClass>(SET_INITIAL_CAPACITY);
            sessionProductClassSet.addAll(getSessionClassesForUnderlyingProductKey(product.getProductKey()));
        }

        if(sessionProductClassSet == null)
        {
            sessionProductClassSet = Collections.emptySet();
        }

        return sessionProductClassSet.toArray(new SessionProductClass[sessionProductClassSet.size()]);
    }

    public ProductClassExtStruct[] getProductClassesExt(boolean activeOnly, boolean includeReportingClasses,
                                                        boolean includeProducts)
            throws SystemException, CommunicationException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            Object[] argObj = new Object[3];
            argObj[0] = activeOnly;
            argObj[1] = includeReportingClasses;
            argObj[2] = includeProducts;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductClassesExt",
                                       GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }

        ProductClassExtStruct[] structs = productMaintenance.getProductClassesExt(activeOnly,
                                                                                  includeReportingClasses,
                                                                                  includeProducts);

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductClassesExt",
                                       GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, structs.length);
        }

        return structs;
    }

    public LinkageIndicatorResultStruct[] getLinkageIndicators(String userId, String sessionName,
                                                               int[] classKeys)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            Object[] argObj = new Object[3];
            argObj[0] = sessionName;
            argObj[1] = userId;
            argObj[2] = classKeys;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getLinkageIndicators",
                                       GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }
        return productMaintenance.getLinkageIndicators(userId, sessionName, classKeys);
    }

    public LinkageIndicatorResultStruct[] updateLinkageIndicators(String userId, String sessionName,
                                                                  int[] classKeys, boolean isEnabled)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, TransactionFailedException
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            Object[] argObj = new Object[4];
            argObj[0] = sessionName;
            argObj[1] = userId;
            argObj[2] = classKeys;
            argObj[4] = isEnabled;

            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":updateLinkageIndicators",
                                       GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, argObj);
        }
        return productMaintenance.updateLinkageIndicators(userId, sessionName, classKeys, isEnabled);
    }

    public void subscribeLinkageStatus(EventChannelListener clientListener) throws SystemException,
            CommunicationException, DataValidationException, AuthorizationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_UPDATE_LINKAGE_INDICATOR, new Integer(0));

        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":subscribeLinkageStatus",
                                       GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, clientListener);
        }

        if(SubscriptionManagerFactory.find()
                .subscribe(key, clientListener, linkageStatusConsumer) == 1)
        {
            productMaintenance.subscribeLinkageStatus(linkageStatusConsumer);
        }

        key = new ChannelKey(ChannelType.CB_UPDATE_LINKAGE_INDICATOR, new Integer(0));
        EventChannelAdapterFactory.find()
                .addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    public void unsubscribeLinkageStatus(EventChannelListener clientListener) throws
            SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_UPDATE_LINKAGE_INDICATOR, new Integer(0));

        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":unsubscribeLinkageStatus",
                                       GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE,
                                       clientListener);
        }

        if(SubscriptionManagerFactory.find()
                .unsubscribe(key, clientListener, linkageStatusConsumer) == 0)
        {
            productMaintenance.unsubscribeLinkageStatus(linkageStatusConsumer);
        }

        key = new ChannelKey(ChannelType.CB_UPDATE_LINKAGE_INDICATOR, new Integer(0));
        EventChannelAdapterFactory.find()
                .removeChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
    }

    public String getWorkStationID()
    {
        String workStationInfo;
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            workStationInfo = addr.getHostAddress();
        }
        catch (UnknownHostException uhe)
        {
            workStationInfo = getCurrentLoggedInUser();
            GUILoggerHome.find().exception("Work station IP not found -- will user UserID '" + workStationInfo
                    + "' as unique identifier", uhe);
        }
        return workStationInfo;
    }

    public String createTransactionID()
    {
        return getWorkStationID() + ' ' + createTimeStamp();
    }

    public DateTimeStruct getCurrentTime()
    {
        return new DateWrapper().toDateTimeStruct();
    }

    public String createTimeStamp()
    {
        StringBuilder timeStamp = new StringBuilder();
        Calendar cal = new GregorianCalendar();
        timeStamp.append(cal.get(Calendar.DATE)).append(':')
                .append(cal.get(Calendar.MONTH)).append(':')
                .append(cal.get(Calendar.YEAR)).append(':')

                .append(cal.get(Calendar.HOUR)).append(':')
                .append(cal.get(Calendar.MINUTE)).append(':')
                .append(cal.get(Calendar.SECOND));
        return timeStamp.toString();
    }

    public String getCurrentLoggedInUser()
    {
        UserStructModel currentLoggedInUser = UserSessionFactory.findUserSession().getUserModel();
        return currentLoggedInUser.getUserId();
    }
    
    @Override
    public Product getProductByKeyFromCache(int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException {
        return super.getProductByKey(productKey);
    }

}
