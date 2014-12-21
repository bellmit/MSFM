package com.cboe.application.shared;

import org.omg.PortableServer.Servant;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallback.CMIClassStatusConsumer;
import com.cboe.idl.cmiCallback.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer;
import com.cboe.idl.cmiCallback.CMINBBOConsumer;
import com.cboe.idl.cmiCallback.CMIOrderBookConsumer;
import com.cboe.idl.cmiCallback.CMIProductStatusConsumer;
import com.cboe.idl.cmiCallback.CMIRFQConsumer;
import com.cboe.idl.cmiCallback.CMIRecapConsumer;
import com.cboe.idl.cmiCallback.CMIStrategyStatusConsumer;
import com.cboe.idl.cmiCallback.CMITickerConsumer;
import com.cboe.idl.cmiCallback.CMITradingSessionStatusConsumer;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiCallbackV2.CMILockedQuoteStatusConsumer;
import com.cboe.idl.cmiIntermarketCallback.CMIIntermarketOrderStatusConsumer;
import com.cboe.idl.cmiIntermarketCallback.CMINBBOAgentSessionAdmin;
import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.POAHelper;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.orbService.NoSuchPOAException;
import com.cboe.interfaces.businessServices.EnhancedOrderHandlingService;
import com.cboe.interfaces.application.AdministratorHome;
import com.cboe.interfaces.application.AppServerStatusManager;
import com.cboe.interfaces.application.AppServerStatusManagerHome;
import com.cboe.interfaces.application.BookDepthCollector;
import com.cboe.interfaces.application.CurrentMarketCollector;
import com.cboe.interfaces.application.ExternalTradeMaintenanceServiceHome;
import com.cboe.interfaces.application.FloorTradeMaintenanceServiceHome;
import com.cboe.interfaces.application.IORMaker;
import com.cboe.interfaces.application.IORMakerHome;
import com.cboe.interfaces.application.IntermarketQueryHome;
import com.cboe.interfaces.application.IntermarketSessionManagerHome;
import com.cboe.interfaces.application.MarketDataRequestPublisher;
import com.cboe.interfaces.application.MarketDataRequestPublisherExt;
import com.cboe.interfaces.application.MarketDataRequestPublisherExtHome;
import com.cboe.interfaces.application.MarketDataRequestPublisherHome;
import com.cboe.interfaces.application.MarketQueryHome;
import com.cboe.interfaces.application.NBBOAgentHome;
import com.cboe.interfaces.application.NBBOAgentSessionManagerHome;
import com.cboe.interfaces.application.OMTSessionManager;
import com.cboe.interfaces.application.OMTSessionManagerHome;
import com.cboe.interfaces.application.OrderManagementServiceHome;
import com.cboe.interfaces.application.OrderRoutingConsumerProxyHome;
import com.cboe.interfaces.application.OrderStatusCollector;
import com.cboe.interfaces.application.PCQSSessionManager;
import com.cboe.interfaces.application.PCQSSessionManagerHome;
import com.cboe.interfaces.application.ProductConfigurationQueryServiceHome;
import com.cboe.interfaces.application.ProductDefinitionHome;
import com.cboe.interfaces.application.ProductQueryManagerHome;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.ProductQueryServiceAdapterHome;
import com.cboe.interfaces.application.ProductStatusCollector;
import com.cboe.interfaces.application.QuoteNotificationCollector;
import com.cboe.interfaces.application.QuoteStatusCollector;
import com.cboe.interfaces.application.RecapCollector;
import com.cboe.interfaces.application.RemoteSessionManagerHome;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.SessionManagerCleanupHome;
import com.cboe.interfaces.application.SessionManagerHome;
import com.cboe.interfaces.application.SessionManagerTMS;
import com.cboe.interfaces.application.SessionManagerV5;
import com.cboe.interfaces.application.SessionManagerV6;
import com.cboe.interfaces.application.SessionManagerV7;
import com.cboe.interfaces.application.SessionManagerV8;
import com.cboe.interfaces.application.TCSProcessWatcherManagerHome;
import com.cboe.interfaces.application.TickerCollector;
import com.cboe.interfaces.application.TradingClassStatusQueryServiceHome;
import com.cboe.interfaces.application.TradingSessionHome;
import com.cboe.interfaces.application.TradingSessionServiceAdapter;
import com.cboe.interfaces.application.TradingSessionServiceAdapterHome;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.application.UserEnablementHome;
import com.cboe.interfaces.application.UserHistoryHome;
import com.cboe.interfaces.domain.userLoadManager.UserLoadManagerHome;
import com.cboe.interfaces.application.UserMarketDataService;
import com.cboe.interfaces.application.UserMarketDataServiceHome;
import com.cboe.interfaces.application.UserOrderEntryHome;
import com.cboe.interfaces.application.UserOrderQueryHome;
import com.cboe.interfaces.application.UserOrderService;
import com.cboe.interfaces.application.UserOrderServiceHome;
import com.cboe.interfaces.application.UserPreferenceQueryHome;
import com.cboe.interfaces.application.UserQuoteHome;
import com.cboe.interfaces.application.UserQuoteService;
import com.cboe.interfaces.application.UserQuoteServiceHome;
import com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome;
import com.cboe.interfaces.application.UserSessionQueryHome;
import com.cboe.interfaces.application.UserSessionThreadPoolHome;
import com.cboe.interfaces.application.UserTradingParametersHome;
import com.cboe.interfaces.application.UserTradingSessionService;
import com.cboe.interfaces.application.UserTradingSessionServiceHome;
import com.cboe.interfaces.application.SessionManagerV9;
import com.cboe.interfaces.application.SessionManagerV9Home;
import com.cboe.interfaces.application.inprocess.InProcessSessionManagerHome;
import com.cboe.interfaces.application.subscription.SubscriptionCollectionService;
import com.cboe.interfaces.application.subscription.SubscriptionCollectionServiceHome;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.interfaces.application.subscription.SubscriptionServiceHome;
import com.cboe.interfaces.businessServices.FirmService;
import com.cboe.interfaces.businessServices.FirmServiceHome;
import com.cboe.interfaces.businessServices.IntermarketDataQueryService;
import com.cboe.interfaces.businessServices.IntermarketDataQueryServiceHome;
import com.cboe.interfaces.businessServices.MarketDataReportService;
import com.cboe.interfaces.businessServices.MarketDataReportServiceHome;
import com.cboe.interfaces.businessServices.MarketDataService;
import com.cboe.interfaces.businessServices.MarketDataServiceHome;
import com.cboe.interfaces.businessServices.MarketMakerQuoteService;
import com.cboe.interfaces.businessServices.MarketMakerQuoteServiceHome;
import com.cboe.interfaces.businessServices.NBBOAgentService;
import com.cboe.interfaces.businessServices.NBBOAgentServiceHome;
import com.cboe.interfaces.businessServices.OrderBookService;
import com.cboe.interfaces.businessServices.OrderBookServiceHome;
import com.cboe.interfaces.businessServices.OrderHandlingService;
import com.cboe.interfaces.businessServices.OrderHandlingServiceHome;
import com.cboe.interfaces.businessServices.LightOrderHandlingServiceHome;
import com.cboe.interfaces.businessServices.OrderStatusService;
import com.cboe.interfaces.businessServices.OrderStatusServiceHome;
import com.cboe.interfaces.businessServices.ProductQueryService;
import com.cboe.interfaces.businessServices.ProductQueryServiceHome;
import com.cboe.interfaces.businessServices.PropertyService;
import com.cboe.interfaces.businessServices.PropertyServiceHome;
import com.cboe.interfaces.businessServices.QuoteStatusService;
import com.cboe.interfaces.businessServices.QuoteStatusServiceHome;
import com.cboe.interfaces.businessServices.TextMessagingService;
import com.cboe.interfaces.businessServices.TextMessagingServiceHome;
import com.cboe.interfaces.businessServices.TradingSessionService;
import com.cboe.interfaces.businessServices.TradingSessionServiceHome;
import com.cboe.interfaces.businessServices.UserActivityService;
import com.cboe.interfaces.businessServices.UserActivityServiceHome;
import com.cboe.interfaces.businessServices.UserService;
import com.cboe.interfaces.businessServices.UserServiceHome;
import com.cboe.interfaces.businessServices.UserTradingParameterService;
import com.cboe.interfaces.businessServices.UserTradingParameterServiceHome;
import com.cboe.interfaces.callbackServices.HeartBeatCallbackService;
import com.cboe.interfaces.callbackServices.HeartBeatCallbackServiceHome;
import com.cboe.interfaces.internalBusinessServices.ActivityHistoryService;
import com.cboe.interfaces.internalBusinessServices.ActivityHistoryServiceHome;
import com.cboe.interfaces.domain.AuditLog;
import com.cboe.interfaces.domain.AuditLogHome;
import com.cboe.interfaces.domain.RateMonitorHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.events.AuctionConsumerHome;
import com.cboe.interfaces.events.CASAdminConsumer;
import com.cboe.interfaces.events.CASAdminConsumerHome;
import com.cboe.interfaces.events.ComponentConsumerHome;
import com.cboe.interfaces.events.IECAlertConsumerHome;
import com.cboe.interfaces.events.IECAuctionConsumerHome;
import com.cboe.interfaces.events.IECBookDepthConsumerHome;
import com.cboe.interfaces.events.IECCASAdminConsumerHome;
import com.cboe.interfaces.events.IECCacheUpdateConsumerHome;
import com.cboe.interfaces.events.IECCancelReportConsumerHome;
import com.cboe.interfaces.events.IECCurrentMarketConsumerHome;
import com.cboe.interfaces.events.IECGroupElementConsumerHome;
import com.cboe.interfaces.events.IECIntermarketAdminMessageConsumerHome;
import com.cboe.interfaces.events.IECIntermarketOrderStatusConsumerHome;
import com.cboe.interfaces.events.IECMarketBufferConsumerHome;
import com.cboe.interfaces.events.IECMarketDataCallbackConsumerHome;
import com.cboe.interfaces.events.IECNBBOAgentAdminConsumerHome;
import com.cboe.interfaces.events.IECNewOrderConsumerHome;
import com.cboe.interfaces.events.IECOrderAcceptedByBookConsumerHome;
import com.cboe.interfaces.events.IECOrderFillReportConsumerHome;
import com.cboe.interfaces.events.IECOrderStatusConsumerHome;
import com.cboe.interfaces.events.IECOrderStatusConsumerV2Home;
import com.cboe.interfaces.events.IECOrderUpdateConsumerHome;
import com.cboe.interfaces.events.IECProductStatusConsumerHome;
import com.cboe.interfaces.events.IECPropertyConsumerHome;
import com.cboe.interfaces.events.IECQuoteDeleteReportConsumerHome;
import com.cboe.interfaces.events.IECQuoteFillReportConsumerHome;
import com.cboe.interfaces.events.IECQuoteNotificationConsumerHome;
import com.cboe.interfaces.events.IECQuoteStatusConsumerHome;
import com.cboe.interfaces.events.IECQuoteStatusConsumerV2Home;
import com.cboe.interfaces.events.IECRFQConsumerHome;
import com.cboe.interfaces.events.IECRecapConsumerHome;
import com.cboe.interfaces.events.IECRemoteCASBookDepthConsumerHome;
import com.cboe.interfaces.events.IECRemoteCASCallbackRemovalConsumerHome;
import com.cboe.interfaces.events.IECRemoteCASCurrentMarketConsumerHome;
import com.cboe.interfaces.events.IECRemoteCASExpectedOpeningPriceConsumerHome;
import com.cboe.interfaces.events.IECRemoteCASNBBOConsumerHome;
import com.cboe.interfaces.events.IECRemoteCASRecapConsumerHome;
import com.cboe.interfaces.events.IECRemoteCASRecoveryConsumerHome;
import com.cboe.interfaces.events.IECRemoteCASSessionManagerConsumerHome;
import com.cboe.interfaces.events.IECRemoteCASTickerConsumerHome;
import com.cboe.interfaces.events.IECSystemControlConsumerHome;
import com.cboe.interfaces.events.IECTextMessageConsumerHome;
import com.cboe.interfaces.events.IECTickerConsumerHome;
import com.cboe.interfaces.events.IECTradingSessionConsumerHome;
import com.cboe.interfaces.events.IECUserSessionConsumerHome;
import com.cboe.interfaces.events.IECUserTimeoutWarningConsumerHome;
import com.cboe.interfaces.events.MarketDataCallbackConsumer;
import com.cboe.interfaces.events.MarketDataCallbackConsumerHome;
import com.cboe.interfaces.events.OrderStatusAdminConsumer;
import com.cboe.interfaces.events.OrderStatusAdminConsumerHome;
import com.cboe.interfaces.events.QuoteStatusAdminConsumer;
import com.cboe.interfaces.events.QuoteStatusAdminConsumerHome;
import com.cboe.interfaces.events.RemoteCASBookDepthConsumer;
import com.cboe.interfaces.events.RemoteCASBookDepthConsumerHome;
import com.cboe.interfaces.events.RemoteCASCurrentMarketConsumer;
import com.cboe.interfaces.events.RemoteCASCurrentMarketConsumerHome;
import com.cboe.interfaces.events.RemoteCASExpectedOpeningPriceConsumer;
import com.cboe.interfaces.events.RemoteCASExpectedOpeningPriceConsumerHome;
import com.cboe.interfaces.events.RemoteCASNBBOConsumer;
import com.cboe.interfaces.events.RemoteCASNBBOConsumerHome;
import com.cboe.interfaces.events.RemoteCASRecapConsumer;
import com.cboe.interfaces.events.RemoteCASRecapConsumerHome;
import com.cboe.interfaces.events.RemoteCASSessionManagerConsumer;
import com.cboe.interfaces.events.RemoteCASSessionManagerConsumerHome;
import com.cboe.interfaces.events.RemoteCASTickerConsumer;
import com.cboe.interfaces.events.RemoteCASTickerConsumerHome;
import com.cboe.interfaces.expressApplication.MarketDataCallbackRequestPublisher;
import com.cboe.interfaces.expressApplication.MarketDataCallbackRequestPublisherHome;
import com.cboe.interfaces.expressApplication.MarketQueryV4Home;
import com.cboe.interfaces.expressApplication.SessionManagerV4;
import com.cboe.interfaces.externalIntegrationServices.IntermarketControlService;
import com.cboe.interfaces.externalIntegrationServices.IntermarketControlServiceHome;
import com.cboe.interfaces.floorApplication.FloorSessionManager;
import com.cboe.interfaces.floorApplication.FloorSessionManagerHome;
import com.cboe.interfaces.floorApplication.ManualReportingServiceHome;
import com.cboe.interfaces.internalBusinessServices.ClientProductConfigurationService;
import com.cboe.interfaces.internalBusinessServices.ClientProductConfigurationServiceHome;
import com.cboe.interfaces.internalBusinessServices.GroupServiceHome;
import com.cboe.interfaces.internalBusinessServices.ProductRoutingService;
import com.cboe.interfaces.internalBusinessServices.ProductRoutingServiceHome;
import com.cboe.interfaces.application.ActivityService;
import com.cboe.interfaces.application.ActivityServiceHome;
import com.cboe.interfaces.ohsEvents.IECOrderRoutingConsumerHome;

import com.cboe.interfaces.application.ParOrderManagementServiceHome;
import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelListener;

/**
 * This class is a utility class to centralize the Foundation Framework accessor methods for the
 * BusinessServices, and CAS services.  It maintains no state.
 * @author Thomas Lynch
 */
public class ServicesHelper extends Object
{
    public ServicesHelper()
    {
        super();
    }

    public static AppServerStatusManager getAppServerStatusManager()
    {
        try
        {
            AppServerStatusManagerHome home = (AppServerStatusManagerHome) HomeFactory.getInstance().findHome(AppServerStatusManagerHome.HOME_NAME);
            return home.find();
        }
        catch (Exception e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find Subscription");
        }
    }
    public static RateMonitorHome getRateMonitorHome()
    {
        RateMonitorHome rateMonitorHome = null;
        try
        {
            rateMonitorHome = (RateMonitorHome) HomeFactory.getInstance().findHome(RateMonitorHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.exception("Could not find RateMonitor Home", e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RateMonitor Home");
        }
        return rateMonitorHome;
    }
    
    public static UserLoadManagerHome getUserLoadManagerHome()
    {
    	UserLoadManagerHome userLoadManagerHome = null;
        try
        {
        	userLoadManagerHome = (UserLoadManagerHome) HomeFactory.getInstance().findHome(UserLoadManagerHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.exception("Could not find UserLoadManager Home", e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserLoadManager Home");
        }
        return userLoadManagerHome;
    }
    
    public static SubscriptionCollectionService getSubscriptionCollectionService()
    {
        try
        {
            SubscriptionCollectionServiceHome home = (SubscriptionCollectionServiceHome) HomeFactory.getInstance().findHome(SubscriptionCollectionServiceHome.HOME_NAME);
            return home.find();
        }
        catch (Exception e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find SubscriptionCollectionService");
        }
    }

    public static SubscriptionService getSubscriptionService(BaseSessionManager sessionManager)
    {
        try
        {
            SubscriptionServiceHome home = (SubscriptionServiceHome) HomeFactory.getInstance().findHome(SubscriptionServiceHome.HOME_NAME);
            return home.find(sessionManager);
        }
        catch (Exception e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find Subscription");
        }
    }

    public static SubscriptionServiceHome getSubscriptionServiceHome()
    {
        try
        {
            SubscriptionServiceHome home = (SubscriptionServiceHome) HomeFactory.getInstance().findHome(SubscriptionServiceHome.HOME_NAME);
            return home;
        }
        catch (Exception e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find Subscription");
        }
    }

    public static IECCacheUpdateConsumerHome getCacheUpdateConsumerHome()
    {
        try {
                return (IECCacheUpdateConsumerHome)HomeFactory.getInstance().findHome(IECCacheUpdateConsumerHome.HOME_NAME);
        }
        catch (CBOELoggableException e) {
                Log.exception(e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find IECCacheUpdateConsumerHome (UOQ)");
        }

    }

    public static MarketDataRequestPublisher getMarketDataRequestPublisher()
    {
        try
        {
            MarketDataRequestPublisherHome home = (MarketDataRequestPublisherHome) HomeFactory.getInstance().findHome(MarketDataRequestPublisherHome.HOME_NAME);
            return home.find();
        } catch (Exception e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketDataRequestPublisher");
        }
    }

    // MDX
    public static MarketDataCallbackRequestPublisher getV4MarketDataRequestPublisher()
    {
        try
        {
            MarketDataCallbackRequestPublisherHome home = (MarketDataCallbackRequestPublisherHome) HomeFactory.getInstance().findHome(MarketDataCallbackRequestPublisherHome.HOME_NAME);
            return home.find();
        }
        catch(Exception e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketDataCallbackRequestPublisher");
        }
    }

    public static MarketDataRequestPublisherExt getMarketDataRequestPublisherExt()
    {
        try
        {
            MarketDataRequestPublisherExtHome home = (MarketDataRequestPublisherExtHome) HomeFactory.getInstance().findHome(MarketDataRequestPublisherExtHome.HOME_NAME);
            return home.find();
        } catch (Exception e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketDataRequestPublisherExt");
        }
    }

    /**
     * Returns a reference to TradingSessionService handling service.
     */
    public static TradingSessionServiceHome getTradingSessionServiceHome()
    {
        try
        {
            return (TradingSessionServiceHome) HomeFactory.getInstance().findHome(TradingSessionServiceHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TradingSessionServiceHome");
        }
    }// end of getTradingSessionService

    /**
     * Returns a reference to TradingSessionService handling service.
     */
    public static TradingSessionService getTradingSessionService()
    {
        return getTradingSessionServiceHome().find();
    }// end of getTradingSessionService

    /**
     * Get the TradingSessionService that can handle the getCurrentBusinessDay
     * call.
     * @return object (or null if XML doesn't specify the home!)
     */
/*    public static TradingSessionService getBusinessDayTradingSessionService()
    {
        try
        {
            TradingSessionServiceHome home = (TradingSessionServiceHome)HomeFactory.getInstance().findHome(TradingSessionServiceHome.BUSINESS_DAY_HOME_NAME);
            return home.find();
        }
        catch (CBOELoggableException e)
        {
            throw new NullPointerException("Could not find businessDay TradingSessionServiceHome");
        }
    }
 */

    /**
     * Returns a reference to ProductConfigurationService
     */

   public static ClientProductConfigurationServiceHome getProductConfigurationServiceHome()
   {
      try
      {
         return (ClientProductConfigurationServiceHome)HomeFactory.getInstance().findHome(ClientProductConfigurationServiceHome.HOME_NAME);
      }
      catch (CBOELoggableException e)
      {
         Log.alarm("Could not find ProductConfigurationService (ServicesHelper).getProductConfigurationService");
         // a really ugly way to get around the missing exception in the interface...
         throw new NullPointerException("Could not find ClientProductConfigurationServiceHome (UOQ)");
      }
   }

    public static ClientProductConfigurationService getProductConfigurationService()
    {
        return getProductConfigurationServiceHome().find();
    }

    /**
     * Returns a reference to productQueryService handling service.
     */
    public static ProductQueryService getProductQueryService()
    {
        try
        {
            ProductQueryServiceHome home = (ProductQueryServiceHome) HomeFactory.getInstance().findHome(ProductQueryServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ProductQueryServiceHome (UOQ)");
        }
    }// end of getProductQueryService

    /**
     * Returns a reference to productQueryStartupService handling service.
     */
    public static ProductQueryService getProductQueryStartupService()
    {
        try
        {
            ProductQueryServiceHome home = (ProductQueryServiceHome) HomeFactory.getInstance().findHome(ProductQueryServiceHome.STARTUP_PROXY_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ProductQueryServiceHome (UOQ)");
        }
    }

    public static ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        try
        {
            ProductQueryServiceAdapterHome home = (ProductQueryServiceAdapterHome) HomeFactory.getInstance().findHome(ProductQueryServiceAdapterHome.HOME_NAME);
            return home.find();
        }
        catch(CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ProductQueryServiceAdapterHome");
        }
    }

    public static TradingSessionServiceAdapter getTradingSessionServiceAdapter()
    {
        try
        {
            TradingSessionServiceAdapterHome home = (TradingSessionServiceAdapterHome) HomeFactory.getInstance().findHome(TradingSessionServiceAdapterHome.HOME_NAME);
            return home.find();
        }
        catch(CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TradingSessionServiceAdapterHome");
        }
    }

    /**
     * Returns a reference to FirmService.
     */
    public static FirmServiceHome getFirmServiceHome()
    {
        try
        {
            return (FirmServiceHome) HomeFactory.getInstance().findHome(FirmServiceHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find FirmServiceHome (UOQ)");
        }
    }// end of getFirmService
    
    public static FirmService getFirmService()
    {
        try
        {
        	FirmServiceHome home = (FirmServiceHome) HomeFactory.getInstance().findHome(FirmServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find FirmService (UOQ)");
        }
    }// end of getFirmService

    /**
     * Returns a reference to an AuditLog
     * */
    public static AuditLog getAuditLog()
    {
        try
        {
            AuditLogHome home = (AuditLogHome) HomeFactory.getInstance().findHome(AuditLogHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find AuditLogHome (UOQ)");
        }
    }// end of getAuditLog

    /**
     * Returns a reference to order handling service.
     */
    public static OrderHandlingService getOrderHandlingService()
    {
        try
        {
            OrderHandlingServiceHome home = (OrderHandlingServiceHome) HomeFactory.getInstance().findHome(OrderHandlingServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderHandlingServiceClientHome (UOQ)");
        }
    }

    public static ActivityHistoryService getActivityHistoryService()
    {
        try
        {
            ActivityHistoryServiceHome home = (ActivityHistoryServiceHome) HomeFactory.getInstance().findHome(ActivityHistoryServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ActivityHistoryServiceServerHome");
        }
    }
    /**
     * Returns a reference to Light order handling service. This call must be to local proxy otherwise cast will fail!
     */
    public static EnhancedOrderHandlingService getEnhancedOrderHandlingService()
    {
        try
        {
            LightOrderHandlingServiceHome home = (LightOrderHandlingServiceHome) HomeFactory.getInstance().findHome(LightOrderHandlingServiceHome.HOME_NAME);
            return (EnhancedOrderHandlingService)home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find EnhancedOrderHandlingServiceClientHome (UOQ)");
        }
    }

    public static IntermarketControlService getIntermarketControlService()
    {
        try
        {
            IntermarketControlServiceHome home = (IntermarketControlServiceHome) HomeFactory.getInstance().findHome(IntermarketControlServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IntermarketControlServiceHome (UOQ)");
        }
    }

    /**
     * Returns the instance of the Market Maker Quote Service.
     */
    public static MarketMakerQuoteService getMarketMakerQuoteService()
    {
        try
        {
            MarketMakerQuoteServiceHome home = (MarketMakerQuoteServiceHome) HomeFactory.getInstance().findHome(MarketMakerQuoteServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketMakerQuoteServiceClientHome");
        }
    }

    /**
     * Returns a reference to order handling service.
     */
    public static UserOrderService getUserOrderService(BaseSessionManager sessionManager)
    {
        try
        {
            UserOrderServiceHome home = (UserOrderServiceHome) HomeFactory.getInstance().findHome(UserOrderServiceHome.HOME_NAME);
            return home.find(sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserOrderServiceHome");
        }
    }

    /**
     * Returns a reference to user quote service.
     */
    public static UserQuoteService getUserQuoteService(BaseSessionManager sessionManager)
    {
        try
        {
            UserQuoteServiceHome home = (UserQuoteServiceHome) HomeFactory.getInstance().findHome(UserQuoteServiceHome.HOME_NAME);
            return home.find(sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserQuoteServiceHome");
        }
    }

    /**
     * Returns a reference to user tradingSession service.
     */
    public static UserTradingSessionService getUserTradingSessionService(BaseSessionManager sessionManager)
    {
        try
        {
            UserTradingSessionServiceHome home = (UserTradingSessionServiceHome) HomeFactory.getInstance().findHome(UserTradingSessionServiceHome.HOME_NAME);
            return home.find(sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserTradingSessionServiceHome");
        }
    }

    /**
     * Returns a reference to user marketQuery.
     */
    public static UserMarketDataService getUserMarketDataService(BaseSessionManager sessionManager)
    {
        try
        {
            UserMarketDataServiceHome home = (UserMarketDataServiceHome) HomeFactory.getInstance().findHome(UserMarketDataServiceHome.HOME_NAME);
            return home.find(sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserMarketQueryServiceHome");
        }
    }

    /**
     * Returns a reference to user marketData service home.
     */
    public static UserMarketDataServiceHome getUserMarketDataServiceHome()
    {
        try
        {
            return (UserMarketDataServiceHome) HomeFactory.getInstance().findHome(UserMarketDataServiceHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserMarketDataServiceHome)");
        }
    }

    /**
     * Returns a reference to user order service home.
     */
    public static UserOrderServiceHome getUserOrderServiceHome()
    {
        try
        {
            return (UserOrderServiceHome) HomeFactory.getInstance().findHome(UserOrderServiceHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserOrderServiceHome)");
        }
    }

    /**
     * Returns a reference to user quote service home.
     */
    public static UserQuoteServiceHome getUserQuoteServiceHome()
    {
        try
        {
            return (UserQuoteServiceHome) HomeFactory.getInstance().findHome(UserQuoteServiceHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserQuoteServiceHome)");
        }
    }

    /**
     * Returns a reference to user tradingSession service home.
     */
    public static UserTradingSessionServiceHome getUserTradingSessionServiceHome()
    {
        try
        {
            return (UserTradingSessionServiceHome) HomeFactory.getInstance().findHome(UserTradingSessionServiceHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserTradingSessionServiceHome)");
        }
    }
    /**
     * Returns the instance of the Market Data Service.
     */
    public static MarketDataService getMarketDataService()
    {
        try
        {
            MarketDataServiceHome home = (MarketDataServiceHome) HomeFactory.getInstance().findHome(MarketDataServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketDataServiceClientHome");
        }
    }

    /**
     * Returns the instance of the MarketDataReportService.
     */
    public static MarketDataReportService getMarketDataReportService()
    {
        try
        {
            MarketDataReportServiceHome home = (MarketDataReportServiceHome)
                    HomeFactory.getInstance().findHome(MarketDataReportServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketDataReportServiceHome");
        }
    }
    /**
     * Returns the instance of the NBBO Agent Service.
     */
    public static NBBOAgentService getNBBOAgentService()
    {
        try
        {
            NBBOAgentServiceHome home = (NBBOAgentServiceHome) HomeFactory.getInstance().findHome(NBBOAgentServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find NBBOAgentService Home");
        }
    }

    /**
     * Returns the instance of the intermarket Data Service.
     */
    public static IntermarketDataQueryService getIntermarketDataService()
    {
        try
        {
            IntermarketDataQueryServiceHome home = (IntermarketDataQueryServiceHome) HomeFactory.getInstance().findHome(IntermarketDataQueryServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IntermarketDataQueryServiceHome");
        }
    }

    /**
     * Returns the instance of the Order Book Service.
     */
    public static OrderBookService getOrderBookService()
    {
        try
        {
            OrderBookServiceHome home = (OrderBookServiceHome) HomeFactory.getInstance().findHome(OrderBookServiceHome.HOME_NAME);
            return home.find();
            
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderBookServiceClientHome");
        }
    }

    public static PropertyService getPropertyService()
    {
        try
        {
            PropertyServiceHome home = (PropertyServiceHome) HomeFactory.getInstance().findHome(PropertyServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderBookServiceHome");
        }
    }

    /**
     * Returns reference to user trading parameter service.
     *
     * @return UserTradingParameterService reference to object of type UserTradingParameterService
     */
    public static UserTradingParameterService getUserTradingParameterService()
    {
        return getUserTradingParameterServiceHome().find();
    }

    /**
     * Returns reference to user trading parameter service.
     *
     * @return UserTradingParameterService reference to object of type UserTradingParameterService
     */
    public static UserTradingParameterServiceHome getUserTradingParameterServiceHome()
    {
        try
        {
            return (UserTradingParameterServiceHome) HomeFactory.getInstance().findHome(UserTradingParameterServiceHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            throw new NullPointerException("Could not find UserTradingParameterServiceHome");
        }
    }

    /**
     * Returns reference to user service.
     */
    public static UserServiceHome getUserServiceHome()
    {
        try
        {
            return (UserServiceHome) HomeFactory.getInstance().findHome(UserServiceHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            throw new NullPointerException("Could not find UserServiceHome");
        }

    }// end of getUserService

    /**
     * Returns reference to user service.
     */
    public static UserService getUserService()
    {
        return getUserServiceHome().find();

    }// end of getUserService

    /**
     * Returns reference to user trading parameter CAS service.
     *
     * @return UserTradingParameter reference to object of type UserTradingParameters
     */
    public static UserTradingParametersHome getUserTradingParametersHome()
    {
        try
        {
            return (UserTradingParametersHome) HomeFactory.getInstance().findHome(UserTradingParametersHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            throw new NullPointerException("Could not find UserTradinParametersHome");
        }

    }// end of getUserTradingParameterCASService


    /**
     * Returns a reference to session manager home .
     */
    public static InProcessSessionManagerHome getInProcessSessionManagerHome()
    {
        try
        {
            InProcessSessionManagerHome home = (InProcessSessionManagerHome) HomeFactory.getInstance().findHome(InProcessSessionManagerHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find InProcess Session Manager Home.");
        }
    }
    /**
     * Returns a reference to session manager home .
     * Always returns Home for the latest session Manager
     */
    public static SessionManagerHome getSessionManagerHome()
    {
        try
        {
            return (SessionManagerV9Home) HomeFactory.getInstance().findHome(SessionManagerV9Home.HOME_NAME);
        }
        catch(CBOELoggableException e)
        {
            log(e);
            throw new NullPointerException("Could not find V9 Session Manager Home.");
        }


    }


     /**
     * Returns a reference to IM session manager home .
     */
    public static IntermarketSessionManagerHome getIntermarketSessionManagerHome()
    {
        try
        {
            IntermarketSessionManagerHome home = (IntermarketSessionManagerHome) HomeFactory.getInstance().findHome(IntermarketSessionManagerHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find Intermarket Session Manager Home.");
        }
    }
    /**
     * Returns a reference to NNBO Agent home .
     */
    public static NBBOAgentHome getNBBOAgentHome()
    {
        try
        {
            NBBOAgentHome home = (NBBOAgentHome) HomeFactory.getInstance().findHome(NBBOAgentHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find NBBOAgent Home.");
        }
    }
    /**
     * Returns a reference to Intermarket Query home .
     */
    public static IntermarketQueryHome getIntermarketQueryHome()
    {
        try
        {
            IntermarketQueryHome home = (IntermarketQueryHome) HomeFactory.getInstance().findHome(IntermarketQueryHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IntermarketQueryHome Home.");
        }
    }

    /**
     * Returns a reference to session manager cleanup home .
     */
    public static SessionManagerCleanupHome getSessionManagerCleanupHome()
    {
        try
        {
            SessionManagerCleanupHome home = (SessionManagerCleanupHome) HomeFactory.getInstance().findHome(SessionManagerCleanupHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find Session Manager Cleanup Home.");
        }
    }

    /**
     * Returns a reference to remote session manager cleanup home .
     */
    public static RemoteSessionManagerHome getRemoteSessionManagerHome()
    {
        try
        {
            RemoteSessionManagerHome home = (RemoteSessionManagerHome) HomeFactory.getInstance().findHome(RemoteSessionManagerHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RemoteSessionManagerHome.");
        }
    }

    /**
     * Returns a reference to remote session manager cleanup home .
     */
    public static UserSessionQueryHome getUserSessionQueryHome()
    {
        try
        {
            UserSessionQueryHome home = (UserSessionQueryHome) HomeFactory.getInstance().findHome(UserSessionQueryHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserSessionQueryHome.");
        }
    }

    /**
     * Creates a reference to the V5 session manager
     * Always returns the latest version of Session Manager
     */
    public static SessionManagerV5 createSessionManagerV5(SessionProfileUserStructV2 userStruct, String sessionId,
                                                          int sessionKey, CMIUserSessionAdmin clientListener,
                                                          short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException
    {
        return (SessionManagerV5)createSessionManager(userStruct, sessionId, sessionKey, clientListener, sessionType, gmdTextMessaging);
    }



    /**
     * Creates a reference to TMS session manager
     * Always returns the latest version of Session Manager
     */
    public static SessionManagerTMS createSessionManagerTMS(SessionProfileUserStructV2 userStruct, String sessionId,
                                                                        int sessionKey,
                                                                        CMIUserSessionAdmin clientListener, short sessionType,
                                                                        boolean gmdTextMessaging)
            throws DataValidationException, SystemException
    {
        return (SessionManagerTMS)createSessionManager(userStruct, sessionId, sessionKey, clientListener, sessionType, gmdTextMessaging);
    }
    
    /**
     * Creates a reference to MDX V4 session manager (which will creater a quote & order handling service).
     * Always returns the latest version of Session Manager
     */
    public static SessionManagerV4 createSessionManagerV4(SessionProfileUserStructV2 userStruct, String sessionId,
                                                                        int sessionKey,
                                                                        CMIUserSessionAdmin clientListener, short sessionType,
                                                                        boolean gmdTextMessaging)
            throws DataValidationException, SystemException
    {                                                                  
       return (SessionManagerV4)createSessionManager(userStruct, sessionId, sessionKey, clientListener, sessionType, gmdTextMessaging);
    }

    /**
     * Creates a reference to session manager (which will creater a quote & order handling service).
     * Always returns the latest version of Session Manager
     */
    public static SessionManager createSessionManager(SessionProfileUserStructV2 userStruct, String sessionId, int sessionKey,
                                                      CMIUserSessionAdmin clientListener, short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException
    {


         return ((SessionManagerV9Home)getSessionManagerHome()).createSessionManagerV9(userStruct, sessionId, sessionKey,
                                                                clientListener, sessionType, gmdTextMessaging);
    }

    /**
     * Creates a reference to the V6 session manager
     * Always returns the latest version of Session Manager
     */
    public static SessionManagerV6 createSessionManagerV6(SessionProfileUserStructV2 userStruct, String sessionId,
                                                          int sessionKey, CMIUserSessionAdmin clientListener,
                                                          short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException
    {
        return (SessionManagerV6)createSessionManager(userStruct, sessionId, sessionKey,
                                                                clientListener, sessionType, gmdTextMessaging);
    }

    /**
     * Creates a reference to the V7 session manager
     * Always returns the latest version of Session Manager
     */
    public static SessionManagerV7 createSessionManagerV7(SessionProfileUserStructV2 userStruct, String sessionId,
                                                          int sessionKey, CMIUserSessionAdmin clientListener,
                                                          short sessionType, boolean gmdTextMessaging)
            throws DataValidationException, SystemException
    {
        return (SessionManagerV7)createSessionManager(userStruct, sessionId, sessionKey,
                                                                clientListener, sessionType, gmdTextMessaging);
    }

    /**
     * Creates a reference to NBBO Agent session manager (which will creater a intermarketheldorderquery and intermarketheldorderentry).
     */
    public static NBBOAgentSessionManagerHome getNBBOAgentSessionManagerHome()
    {
        try
        {
            NBBOAgentSessionManagerHome home = (NBBOAgentSessionManagerHome) HomeFactory.getInstance().findHome(NBBOAgentSessionManagerHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find NBBOAgentSessionManagerHome Home.");
        }
    }


    /**
     * Gets User Preference Query.
     *
     * @return reference to test service
     */
    public static UserPreferenceQueryHome getUserPreferenceQueryHome()
    {
        try
        {
            return (UserPreferenceQueryHome) HomeFactory.getInstance().findHome(UserPreferenceQueryHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find User Preference Query Home.");
        }
    }

    /**
     * gets the quote query service.
     *
     * @return Quote the quote query service
     */
    public static UserQuoteHome getUserQuoteHome()
    {
        try
        {
            return (UserQuoteHome) HomeFactory.getInstance().findHome(UserQuoteHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserQuoteHome");
        }
    }// end of getQuoteService

    /**
     * gets the market query service.
     *
     * @return MarketQuery the market query service
     */
    public static MarketQueryHome getMarketQueryHome()
    {
        try
        {
            return (MarketQueryHome) HomeFactory.getInstance().findHome(MarketQueryHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketQueryHome");
        }
    }// end of getMarketQueryService

//    public static IntermarketHeldOrderQueryHome getIntermarketHeldOrderQueryHome()
//    {
//        try
//        {
//            return (IntermarketHeldOrderQueryHome) HomeFactory.getInstance().findHome(IntermarketHeldOrderQueryHome.HOME_NAME);
//        } catch (CBOELoggableException e)
//        {
//            log(e);
//            // a really ugly way to get around the missing exception in the interface...
//            throw new NullPointerException("Could not find IntermarketHeldOrderQueryHome");
//        }
//    }// end of getMarketQueryService

//    public static IntermarketHeldOrderEntryHome getIntermarketHeldOrderEntryHome()
//    {
//        try
//        {
//            return (IntermarketHeldOrderEntryHome) HomeFactory.getInstance().findHome(IntermarketHeldOrderEntryHome.HOME_NAME);
//        } catch (CBOELoggableException e)
//        {
//            log(e);
//            // a really ugly way to get around the missing exception in the interface...
//            throw new NullPointerException("Could not find IntermarketHeldOrderEntryHome");
//        }
//    }// end of getMarketQueryService

    /**
     * gets the (CAS) Product Definition service.
     *
     * @return ProductDefinition the market query service
     */
    public static ProductDefinitionHome getProductDefinitionHome()
    {
        try
        {
            return (ProductDefinitionHome) HomeFactory.getInstance().findHome(ProductDefinitionHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ProductDefinitionHome");
        }
    }// end of getProductQueryManagerService

    /**
     * gets the (CAS) Product Manager service.
     *
     * @return MarketQuery the market query service
     */
    public static ProductQueryManagerHome getProductQueryManagerHome()
    {
        try
        {
            return (ProductQueryManagerHome) HomeFactory.getInstance().findHome(ProductQueryManagerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ProductQueryManagerHome");
        }
    }// end of getProductQueryManagerService

    /**
     * gets the (CAS) Product Manager service.
     *
     * @return MarketQuery the market query service
     */
    public static AdministratorHome getAdministratorHome()
    {
        try
        {
            return (AdministratorHome) HomeFactory.getInstance().findHome(AdministratorHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ProductQueryManagerHome");
        }
    }// end of getAdministratorService

    /**
     * gets the UserOrderQuery service.
     *
     * @return UserOrderQuery theUserOrderQuery service
     */
    public static UserOrderQueryHome getUserOrderQueryHome()
    {
        try
        {
            return (UserOrderQueryHome) HomeFactory.getInstance().findHome(UserOrderQueryHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserOrderQueryHome");
        }
    }// end of getUserOrderQueryService

    /**
     * gets the (CAS) OrderEntry service.
     *
     * @return OrderEntry the OrderEntry service. Always return the latest.
     */
    public static UserOrderEntryHome getUserOrderEntryHome()
    {
        try
        {
            return (UserOrderEntryHome) HomeFactory.getInstance().findHome(UserOrderEntryHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserOrderEntryHome");
        }
    }


    /**
     * gets the (CAS) OrderEntryV5 service.
     *
     * @return OrderEntry the OrderEntry service
     */
    //todo - Vivek: get rid of this method
    /*
    public static UserOrderEntryV5Home getUserOrderEntryV5Home()
    {
        try
        {
            return (UserOrderEntryV5Home) HomeFactory.getInstance().findHome(UserOrderEntryV5Home.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserOrderEntryV5Home");
        }
    }
    */

    /**
     * gets the (CAS) UserEnablement service.
     *
     * @return UserEnablement the UserEnablement service
     */
    public static UserEnablement getUserEnablementService(String userId, String exchange, String acronym)
    {
        try
        {
            UserEnablementHome home = (UserEnablementHome) HomeFactory.getInstance().findHome(UserEnablementHome.HOME_NAME);
            return home.create(userId, exchange, acronym);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserEnablementHome");
        }
    }// end of getUserEnablementService

    public static UserEnablementHome getUserEnablementHome()
    {
        try
        {
            UserEnablementHome home = (UserEnablementHome) HomeFactory.getInstance().findHome(UserEnablementHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserSessionThreadPoolHome.");
        }
    }

    /**
     * gets the (CAS) TradingSession service.
     *
     * @return TradingSession the TradingSession service
     */
    public static TradingSessionHome getTradingSessionHome()
    {
        try
        {
            return (TradingSessionHome) HomeFactory.getInstance().findHome(TradingSessionHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TradingSessionHome");
        }
    }// end of getTradingSession

    /**
     * log the message
     */
    public static void log(Exception e)
    {
        Log.exception(e);
    }

    public static OrderStatusService getOrderStatusService()
    {
        try
        {
            OrderStatusServiceHome home = (OrderStatusServiceHome) HomeFactory.getInstance().findHome(OrderStatusServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderStatusServiceHome (UOQ)");
        }
    }// end of getOrderStatusService

    public static UserActivityService getUserActivityService()
    {
        try
        {
            UserActivityServiceHome home = (UserActivityServiceHome) HomeFactory.getInstance().findHome(UserActivityServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderStatusServiceHome (UOQ)");
        }
    }// end of getOrderStatusService

    public static QuoteStatusService getQuoteStatusService()
    {
        try
        {
            QuoteStatusServiceHome home = (QuoteStatusServiceHome) HomeFactory.getInstance().findHome(QuoteStatusServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusServiceHome (UOQ)");
        }
    }

    public static TextMessagingService getTextMessagingService()
    {
        try
        {
            TextMessagingServiceHome home = (TextMessagingServiceHome) HomeFactory.getInstance().findHome(TextMessagingServiceHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TextMessagingServiceHome (UOQ)");
        }
    }

    /**
     * gets the (CAS) User History service.
     *
     * @return UserHistory the User History service
     */
    public static UserHistoryHome getUserHistoryHome()
    {
        try
        {
            return (UserHistoryHome) HomeFactory.getInstance().findHome(UserHistoryHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserHistoryHome");
        }
    }// end of getUserHistoryService

    /////////////////////////////////////////////////////////////////
    // All the event service homes
    /////////////////////////////////////////////////////////////////
    public static IECCurrentMarketConsumerHome getCurrentMarketConsumerHome()
    {
        try
        {
            return (IECCurrentMarketConsumerHome) HomeFactory.getInstance().findHome(IECCurrentMarketConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CurrentMarketConsumerHome (UOQ)");
        }
    }

    public static IECMarketBufferConsumerHome getMarketBufferConsumerHome()
    {
        try
        {
            return (IECMarketBufferConsumerHome) HomeFactory.getInstance().findHome(IECMarketBufferConsumerHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketBufferConsumerHome (UOQ)");
        }
    }

    public static IECBookDepthConsumerHome getBookDepthConsumerHome()
    {
        try
        {
            return (IECBookDepthConsumerHome) HomeFactory.getInstance().findHome(IECBookDepthConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find BookDepthConsumerHome (UOQ)");
        }
    }

    public static IECQuoteNotificationConsumerHome getQuoteNotificationConsumerHome()
    {
        try
        {
            return (IECQuoteNotificationConsumerHome ) HomeFactory.getInstance().findHome(IECQuoteNotificationConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IECQuoteNotificationConsumerHome (UOQ)");
        }
    }

    public static IECOrderStatusConsumerHome getOrderStatusConsumerClientChannelHome()
    {
        try
        {
            return (IECOrderStatusConsumerHome) HomeFactory.getInstance().findHome(IECOrderStatusConsumerHome.CLIENT_CHANNEL_HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderStatusConsumerClientChannelHome (UOQ)");
        }
    }

    public static IECOrderStatusConsumerHome getOrderStatusConsumerHome()
    {
        try
        {
            return (IECOrderStatusConsumerHome) HomeFactory.getInstance().findHome(IECOrderStatusConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderStatusConsumerHome (UOQ)");
        }
    }

     public static IECOrderStatusConsumerV2Home getOrderStatusConsumerV2Home()
    {
        try
        {
            return (IECOrderStatusConsumerV2Home) HomeFactory.getInstance().findHome(IECOrderStatusConsumerV2Home.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderStatusConsumerHomeV2 (UOQ)");
        }
    }

    public static IECNewOrderConsumerHome getNewOrderConsumerHome()
    {
        try
        {
            return (IECNewOrderConsumerHome) HomeFactory.getInstance().findHome(IECNewOrderConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find NewOrderConsumerHome (UOQ)");
        }
    }

    public static IECOrderUpdateConsumerHome getOrderUpdateConsumerHome()
    {
        try
        {
            return (IECOrderUpdateConsumerHome) HomeFactory.getInstance().findHome(IECOrderUpdateConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderUpdateConsumerHome (UOQ)");
        }
    }

    public static IECCancelReportConsumerHome getCancelReportConsumerHome()
    {
        try
        {
            return (IECCancelReportConsumerHome) HomeFactory.getInstance().findHome(IECCancelReportConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CancelReportConsumerHome (UOQ)");
        }
    }

    public static IECOrderFillReportConsumerHome getOrderFillReportConsumerHome()
    {
        try
        {
            return (IECOrderFillReportConsumerHome) HomeFactory.getInstance().findHome(IECOrderFillReportConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderFillReportConsumerHome (UOQ)");
        }
    }

    public static IECOrderAcceptedByBookConsumerHome getOrderAcceptedByBookConsumerHome()
    {
        try
        {
            return (IECOrderAcceptedByBookConsumerHome) HomeFactory.getInstance().findHome(IECOrderAcceptedByBookConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderAcceptedByBookConsumerHome (UOQ)");
        }
    }


    public static IECProductStatusConsumerHome getProductStatusConsumerHome()
    {
        try
        {
            return (IECProductStatusConsumerHome) HomeFactory.getInstance().findHome(IECProductStatusConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ProductStatusConsumerHome (UOQ)");
        }
    }

    public static IECQuoteStatusConsumerHome getQuoteStatusConsumerClientChannelHome()
    {
        try
        {
            return (IECQuoteStatusConsumerHome) HomeFactory.getInstance().findHome(IECQuoteStatusConsumerHome.CLIENT_CHANNEL_HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusConsumerClientChannelHome (UOQ)");
        }
    }

    public static IECQuoteStatusConsumerHome getQuoteStatusConsumerHome()
    {
        try
        {
            return (IECQuoteStatusConsumerHome) HomeFactory.getInstance().findHome(IECQuoteStatusConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusConsumerHome (UOQ)");
        }
    }

    public static IECQuoteStatusConsumerV2Home getQuoteStatusConsumerV2Home()
    {
        try
        {
            return (IECQuoteStatusConsumerV2Home) HomeFactory.getInstance().findHome(IECQuoteStatusConsumerV2Home.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusConsumerHomeV2 (UOQ)");
        }
    }


    public static IECQuoteFillReportConsumerHome getQuoteFillReportConsumerHome()
    {
        try
        {
            return (IECQuoteFillReportConsumerHome) HomeFactory.getInstance().findHome(IECQuoteFillReportConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteFillReportConsumerHome (UOQ)");
        }
    }

    public static IECQuoteDeleteReportConsumerHome getQuoteDeleteReportConsumerHome()
    {
        try
        {
            return (IECQuoteDeleteReportConsumerHome) HomeFactory.getInstance().findHome(IECQuoteDeleteReportConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteDeleteReportConsumerHome (UOQ)");
        }
    }


    public static IECRFQConsumerHome getRFQConsumerHome()
    {
        try
        {
            return (IECRFQConsumerHome) HomeFactory.getInstance().findHome(IECRFQConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RFQConsumerHome (UOQ)");
        }
    }

    public static IECIntermarketOrderStatusConsumerHome getIntermarketOrderStatusConsumerHome()
    {
        try
        {
            return (IECIntermarketOrderStatusConsumerHome) HomeFactory.getInstance().findHome(IECIntermarketOrderStatusConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IECIntermarketOrderStatusConsumerHome (UOQ)");
        }
    }
    public static IECNBBOAgentAdminConsumerHome getNBBOAgentAdminConsumerHome()
    {
        try
        {
            return (IECNBBOAgentAdminConsumerHome) HomeFactory.getInstance().findHome(IECNBBOAgentAdminConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IECIntermarketOrderStatusConsumerHome (UOQ)");
        }
    }

    public static IECIntermarketAdminMessageConsumerHome getIntermarketAdminMessageConsumerHome()
    {
        try
        {
            return (IECIntermarketAdminMessageConsumerHome) HomeFactory.getInstance().findHome(IECIntermarketAdminMessageConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IECIntermarketAdminMessageConsumerHome (UOQ)");
        }
    }

    public static IECAlertConsumerHome getAlertConsumerHome()
    {
        try
        {
            return (IECAlertConsumerHome) HomeFactory.getInstance().findHome(IECAlertConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IECAlertConsumerHome");
        }
    }

    public static IECRecapConsumerHome getRecapConsumerHome()
    {
        try
        {
            return (IECRecapConsumerHome) HomeFactory.getInstance().findHome(IECRecapConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RecapConsumerHome (UOQ)");
        }
    }

    public static IECTickerConsumerHome getTickerConsumerHome()
    {
        try
        {
            return (IECTickerConsumerHome) HomeFactory.getInstance().findHome(IECTickerConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TickerConsumerHome (UOQ)");
        }
    }

    public static IECTradingSessionConsumerHome getTradingSessionConsumerHome()
    {
        try
        {
            return (IECTradingSessionConsumerHome) HomeFactory.getInstance().findHome(IECTradingSessionConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TradingSessionConsumerHome (UOQ)");
        }

    }

    public static IECUserTimeoutWarningConsumerHome getUserTimeoutWarningConsumerHome()
    {
        try
        {
            return (IECUserTimeoutWarningConsumerHome) HomeFactory.getInstance().findHome(IECUserTimeoutWarningConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserTimeoutWarningConsumerHome (UOQ)");
        }

    }

    public static IECTextMessageConsumerHome getTextMessageConsumerHome()
    {
        try
        {
            return (IECTextMessageConsumerHome) HomeFactory.getInstance().findHome(IECTextMessageConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IECTextMessageConsumerHome (UOQ)");
        }

    }

    public static IECPropertyConsumerHome getPropertyConsumerHome()
    {
        try
        {
            return (IECPropertyConsumerHome) HomeFactory.getInstance().findHome(IECPropertyConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IECPropertyConsumerHome");
        }

    }

    public static IECUserSessionConsumerHome getForcedLogoutConsumerHome()
    {
        try
        {
            return (IECUserSessionConsumerHome) HomeFactory.getInstance().findHome(IECUserSessionConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ForcedLogoutConsumerHome (UOQ)");
        }

    }

    public static ComponentConsumerHome getComponentConsumerHome()
    {
        try
        {
            return (ComponentConsumerHome) HomeFactory.getInstance().findHome(ComponentConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ForcedLogoutConsumerHome (UOQ)");
        }

    }

    public static IECAuctionConsumerHome getAuctionConsumerHome()
    {
        try
        {
            return (IECAuctionConsumerHome) HomeFactory.getInstance().findHome(AuctionConsumerHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find AuctionConsumerHome.");
        }
    }

    public static org.omg.CORBA.Object connectToOrb(Servant servant) throws NoSuchPOAException
    {
        return POAHelper.connect(servant, null);
    }

    public static org.omg.CORBA.Object connectToOrb(Servant servant, BOHome boHome) throws NoSuchPOAException
    {
        return POAHelper.connect(servant, boHome);
    }

    public static IORMaker getIORMaker()
    {
        IORMaker iorMaker;
        try
        {
            IORMakerHome home = (IORMakerHome) HomeFactory.getInstance().findHome(IORMakerHome.HOME_NAME);
            iorMaker = home.create();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IORMakerHome.");
        }
        return iorMaker;
    }

    public static ChannelListener getClassStatusConsumerProxy(CMIClassStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.ClassStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.ClassStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.ClassStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ClassStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getQuoteNotificationConsumerProxy(CMILockedQuoteStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.QuoteNotificationConsumerProxyHome home =
                    (com.cboe.interfaces.application.QuoteNotificationConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.QuoteNotificationConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteNotificationConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getCurrentMarketConsumerProxy(CMICurrentMarketConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.CurrentMarketConsumerProxyHome home =
                    (com.cboe.interfaces.application.CurrentMarketConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.CurrentMarketConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CurrentMarketConsumerProxyHome.");
        }
        return listener;
    }

/****
    public static ChannelListener getMarketAlertConsumerProxy(CMIMarketAlertConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.MarketAlertConsumerProxyHome home =
                    (com.cboe.interfaces.application.MarketAlertConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.MarketAlertConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketAlertConsumerProxyHome.");
        }
        return listener;
    }
****/

    public static ChannelListener getCurrentMarketV2ConsumerProxy(com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer consumer,
                                                                  BaseSessionManager sessionManager,
                                                                  short queuePolicy)
            throws DataValidationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.CurrentMarketV2ConsumerProxyHome home =
                    (com.cboe.interfaces.application.CurrentMarketV2ConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.CurrentMarketV2ConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, queuePolicy);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CurrentMarketV2ConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getCurrentMarketV3ConsumerProxy(com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer consumer,
                                                                  BaseSessionManager sessionManager,
                                                                  short queuePolicy)
            throws DataValidationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.CurrentMarketV3ConsumerProxyHome home =
                    (com.cboe.interfaces.application.CurrentMarketV3ConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.CurrentMarketV3ConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, queuePolicy);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CurrentMarketV3ConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getBookDepthConsumerProxy(CMIOrderBookConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.BookDepthConsumerProxyHome home =
                    (com.cboe.interfaces.application.BookDepthConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.BookDepthConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find BookDepthConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getBookDepthV2ConsumerProxy(com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer consumer,
                                                              BaseSessionManager sessionManager,
                                                              short queuePolicy)
            throws DataValidationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.BookDepthV2ConsumerProxyHome home =
                    (com.cboe.interfaces.application.BookDepthV2ConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.BookDepthV2ConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, queuePolicy);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find BookDepthConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getNBBOConsumerProxy(CMINBBOConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.NBBOConsumerProxyHome home =
                    (com.cboe.interfaces.application.NBBOConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.NBBOConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find NBBOConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getNBBOV2ConsumerProxy(com.cboe.idl.cmiCallbackV2.CMINBBOConsumer consumer,
                                                         BaseSessionManager sessionManager,
                                                         short queuePolicy)
            throws DataValidationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.NBBOV2ConsumerProxyHome home =
                    (com.cboe.interfaces.application.NBBOV2ConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.NBBOV2ConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, queuePolicy);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find NBBOV2ConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getExpectedOpeningPriceConsumerProxy(CMIExpectedOpeningPriceConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.ExpectedOpeningPriceConsumerProxyHome home =
                    (com.cboe.interfaces.application.ExpectedOpeningPriceConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.ExpectedOpeningPriceConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ExpectedOpeningPriceConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getExpectedOpeningPriceV2ConsumerProxy(com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer consumer,
                                                                         BaseSessionManager sessionManager,
                                                                         short queuePolicy)
            throws DataValidationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.ExpectedOpeningPriceV2ConsumerProxyHome home =
                    (com.cboe.interfaces.application.ExpectedOpeningPriceV2ConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.ExpectedOpeningPriceV2ConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, queuePolicy);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ExpectedOpeningPriceV2ConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getOrderStatusConsumerProxy(
            com.cboe.idl.cmiCallback.CMIOrderStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        ChannelListener proxy;

        try
        {
            com.cboe.interfaces.application.OrderStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.OrderStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.OrderStatusConsumerProxyHome.HOME_NAME);
            proxy = home.create(consumer, sessionManager, gmd);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderStatusConsumerProxyHome.");
        }

        return proxy;
    }

    /**
     * V2 - Takes a version 2 CMIOrderStatusConsumer
     */
    public static ChannelListener getOrderStatusConsumerProxy(
            com.cboe.idl.cmiCallbackV2.CMIOrderStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        ChannelListener proxy;

        try
        {
            com.cboe.interfaces.application.OrderStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.OrderStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.OrderStatusConsumerProxyHome.HOME_NAME);
            proxy = home.create(consumer, sessionManager, gmd);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderStatusConsumerProxyHome.");
        }

        return proxy;
    }

    public static ChannelListener getProductStatusConsumerProxy(CMIProductStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.ProductStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.ProductStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.ProductStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ProductStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getQuoteStatusConsumerProxy(
            com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.QuoteStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.QuoteStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.QuoteStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, gmd);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getQuoteStatusConsumerProxy(
            com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumer consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.QuoteStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.QuoteStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.QuoteStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, gmd);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getProductStatusCollectorProxy(ProductStatusCollector consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.ProductStatusCollectorProxyHome home =
                    (com.cboe.interfaces.application.ProductStatusCollectorProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.ProductStatusCollectorProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getOrderStatusCollectorProxy(OrderStatusCollector consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.OrderStatusCollectorProxyHome home =
                    (com.cboe.interfaces.application.OrderStatusCollectorProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.OrderStatusCollectorProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderStatusCollectorProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getQuoteStatusCollectorProxy(QuoteStatusCollector consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.QuoteStatusCollectorProxyHome home =
                    (com.cboe.interfaces.application.QuoteStatusCollectorProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.QuoteStatusCollectorProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusCollectorProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getQuoteNotificationCollectorProxy(QuoteNotificationCollector consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.QuoteNotificationCollectorProxyHome home =
                    (com.cboe.interfaces.application.QuoteNotificationCollectorProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.QuoteNotificationCollectorProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteNotificationCollectorProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getRecapCollectorProxy(RecapCollector consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.RecapCollectorProxyHome home =
                    (com.cboe.interfaces.application.RecapCollectorProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.RecapCollectorProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RecapCollectorProxyHome.");
        }
        return listener;
    }



    public static ChannelListener getTickerCollectorProxy(TickerCollector consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.TickerCollectorProxyHome home =
                    (com.cboe.interfaces.application.TickerCollectorProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.TickerCollectorProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TickerCollectorProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getCurrentMarketCollectorProxy(CurrentMarketCollector consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.CurrentMarketCollectorProxyHome home =
                    (com.cboe.interfaces.application.CurrentMarketCollectorProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.CurrentMarketCollectorProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CurrentMarketCollectorProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getBookDepthCollectorProxy(BookDepthCollector consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.BookDepthCollectorProxyHome home =
                    (com.cboe.interfaces.application.BookDepthCollectorProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.BookDepthCollectorProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find BookDepthCollectorProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getRecapConsumerProxy(CMIRecapConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.RecapConsumerProxyHome home =
                    (com.cboe.interfaces.application.RecapConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.RecapConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RecapConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getRecapV2ConsumerProxy(com.cboe.idl.cmiCallbackV2.CMIRecapConsumer consumer,
                                                          BaseSessionManager sessionManager,
                                                          short queuePolicy)
            throws DataValidationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.RecapV2ConsumerProxyHome home =
                    (com.cboe.interfaces.application.RecapV2ConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.RecapV2ConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, queuePolicy);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RecapConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getRFQConsumerProxy(CMIRFQConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.RFQConsumerProxyHome home =
                    (com.cboe.interfaces.application.RFQConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.RFQConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IORMakerHome.");
        }
        return listener;
    }

    public static ChannelListener getRFQV2ConsumerProxy(com.cboe.idl.cmiCallbackV2.CMIRFQConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.RFQV2ConsumerProxyHome home =
                    (com.cboe.interfaces.application.RFQV2ConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.RFQV2ConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RFQConsumerHome.");
        }
        return listener;
    }

    public static ChannelListener getHeldOrderConsumerProxy(CMIIntermarketOrderStatusConsumer consumer, BaseSessionManager sessionManager)
            throws DataValidationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.HeldOrderConsumerProxyHome home =
                    (com.cboe.interfaces.application.HeldOrderConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.HeldOrderConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find HeldOrderConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getNBBOAgentSessionAdminProxy(CMINBBOAgentSessionAdmin consumer, BaseSessionManager sessionManager)
            throws DataValidationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.NBBOAgentSessionAdminProxyHome home =
                    (com.cboe.interfaces.application.NBBOAgentSessionAdminProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.NBBOAgentSessionAdminProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find NBBOAgentSessionAdminProxyHome.");
        }
        return listener;
    }


    public static ChannelListener getSessionAdminConsumerProxy(
            CMIUserSessionAdmin consumer,
            SessionManager sessionManager,
            boolean gmd)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.SessionAdminConsumerProxyHome home =
                    (com.cboe.interfaces.application.SessionAdminConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.SessionAdminConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, gmd);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find SessionAdminConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getStrategyStatusConsumerProxy(CMIStrategyStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.StrategyStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.StrategyStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.StrategyStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find StrategyStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getTickerConsumerProxy(CMITickerConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.TickerConsumerProxyHome home =
                    (com.cboe.interfaces.application.TickerConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.TickerConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TickerConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getTickerV2ConsumerProxy(com.cboe.idl.cmiCallbackV2.CMITickerConsumer consumer,
                                                           BaseSessionManager sessionManager,
                                                           short queuePolicy)
            throws DataValidationException
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.TickerV2ConsumerProxyHome home =
                    (com.cboe.interfaces.application.TickerV2ConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.TickerV2ConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager, queuePolicy);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TickerConsumerProxyHome.");
        }
        return listener;
    }
    
    public static ChannelListener getLargeTradetLastSaleConsumerProxy(com.cboe.idl.consumers.TickerConsumer consumer,
            												BaseSessionManager sessionManager,
            												short queuePolicy)
    	throws DataValidationException
	{
		ChannelListener listener;
		try
		{
			com.cboe.interfaces.application.LargeTradeLastSaleConsumerProxyHome home =
			(com.cboe.interfaces.application.LargeTradeLastSaleConsumerProxyHome) HomeFactory.getInstance()
			.findHome(com.cboe.interfaces.application.LargeTradeLastSaleConsumerProxyHome.HOME_NAME);
			listener = home.create(consumer, sessionManager, queuePolicy);
		} catch (CBOELoggableException e) {
			log(e);
			// a really ugly way to get around the missing exception in the interface...
			throw new NullPointerException("Could not find LargeTradeLastSaleConsumerProxyHome.");
		}
		return listener;
	}

    public static ChannelListener getTradingSessionStatusConsumerProxy(CMITradingSessionStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.TradingSessionStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.TradingSessionStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.TradingSessionStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TradingSessionStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getAuctionConsumerProxy(com.cboe.idl.cmiCallbackV3.CMIAuctionConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener;
        try
        {
            com.cboe.interfaces.application.AuctionConsumerProxyHome home =
                    (com.cboe.interfaces.application.AuctionConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.AuctionConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find AuctionConsumerProxyHome.");
        }
        return listener;
    }

    public static ThreadPool getUserSessionThreadPool(BaseSessionManager session)
    {
        ThreadPool threadPool;
        try
        {
            com.cboe.interfaces.application.UserSessionThreadPoolHome home =
                    (com.cboe.interfaces.application.UserSessionThreadPoolHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.UserSessionThreadPoolHome.HOME_NAME);
            threadPool = home.find(session);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserSessionThreadPoolHome.");
        }
        return threadPool;
    }

    public static ThreadPool getUserSessionMarketDataThreadPool(BaseSessionManager session)
    {
        ThreadPool threadPool;
        try
        {
            com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome home =
                    (com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome.HOME_NAME);
            threadPool = home.find(session);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserSessionThreadPoolHome.");
        }
        return threadPool;
    }
    public static ThreadPool getUserSessionMarketDataOverlayThreadPool(BaseSessionManager session)
    {
        ThreadPool threadPool;
        try
        {
            com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome home =
                    (com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome.HOME_NAME);
            threadPool = home.findOverlayThreadPool(session);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserSessionThreadPoolHome.");
        }
        return threadPool;
    }

    public static UserSessionThreadPoolHome getUserSessionThreadPoolHome()
    {
        try
        {
            com.cboe.interfaces.application.UserSessionThreadPoolHome home =
                    (com.cboe.interfaces.application.UserSessionThreadPoolHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.UserSessionThreadPoolHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserSessionThreadPoolHome.");
        }
    }
    public static UserSessionMarketDataThreadPoolHome getUserSessionMarketDataThreadPoolHome()
    {
        try
        {
            com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome home =
                    (com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.UserSessionMarketDataThreadPoolHome.HOME_NAME);
            return home;
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserSessionMarketDataThreadPoolHome.");
        }
    }
    public static IECCASAdminConsumerHome getCASAdminConsumerHome()
    {
        try
        {
            return (IECCASAdminConsumerHome) HomeFactory.getInstance().findHome(IECCASAdminConsumerHome.HOME_NAME);
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CASAdminConsumerHome (UOQ)");
        }
    }

    public static CASAdminConsumer getCASAdminPublisher()
    {
        try
        {
            CASAdminConsumerHome home = (CASAdminConsumerHome) HomeFactory.getInstance().findHome(IECCASAdminConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CASAdminPublisher ");
        }
    }

    public static OrderStatusAdminConsumer getOrderStatusAdminPublisher()
    {
        try
        {
            OrderStatusAdminConsumerHome home = (OrderStatusAdminConsumerHome) HomeFactory.getInstance().findHome(OrderStatusAdminConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderStatusAdminPublisher ");
        }
    }

    public static QuoteStatusAdminConsumer getQuoteStatusAdminPublisher()
    {
        try
        {
            QuoteStatusAdminConsumerHome home = (QuoteStatusAdminConsumerHome) HomeFactory.getInstance().findHome(QuoteStatusAdminConsumerHome.PUBLISEHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatussAdminPublisher ");
        }
    }

    // MWM - SPOW
    public static HeartBeatCallbackService getHeartBeatCallbackService()
    {
        try
        {
            HeartBeatCallbackServiceHome home = (HeartBeatCallbackServiceHome) HomeFactory.getInstance().findHome(HeartBeatCallbackServiceHome.HOME_NAME);
            return home.find();
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find HeartBeatCallbackService");
        }
    }

    public static RemoteCASCurrentMarketConsumer getRemoteCASCurrentMarketPublisher()
    {
        try
        {
            RemoteCASCurrentMarketConsumerHome home = (RemoteCASCurrentMarketConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASCurrentMarketConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CASAdminPublisher ");
        }
    }

    public static MarketDataCallbackConsumer getMarketDataCallbackPublisher()
    {
        try
        {
            MarketDataCallbackConsumerHome home = (MarketDataCallbackConsumerHome) HomeFactory.getInstance().findHome(IECMarketDataCallbackConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        }
        catch(CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketDataCallbackPublisher");
        }
    }

    public static RemoteCASNBBOConsumer getRemoteCASNBBOPublisher()
    {
        try
        {
            RemoteCASNBBOConsumerHome home = (RemoteCASNBBOConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASNBBOConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CASAdminPublisher ");
        }
    }

    public static RemoteCASRecapConsumer getRemoteCASRecapPublisher()
    {
        try
        {
            RemoteCASRecapConsumerHome home = (RemoteCASRecapConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASRecapConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CASAdminPublisher ");
        }
    }

    public static RemoteCASTickerConsumer getRemoteCASTickerPublisher()
    {
        try
        {
            RemoteCASTickerConsumerHome home = (RemoteCASTickerConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASTickerConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CASAdminPublisher ");
        }
    }

    public static RemoteCASBookDepthConsumer getRemoteCASBookDepthPublisher()
    {
        try
        {
            RemoteCASBookDepthConsumerHome home = (RemoteCASBookDepthConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASBookDepthConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CASAdminPublisher ");
        }
    }

    public static RemoteCASExpectedOpeningPriceConsumer getRemoteCASExpectedOpeningPricePublisher()
    {
        try
        {
            RemoteCASExpectedOpeningPriceConsumerHome home = (RemoteCASExpectedOpeningPriceConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASExpectedOpeningPriceConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CASAdminPublisher ");
        }
    }

    public static RemoteCASSessionManagerConsumer getRemoteCASSessionManagerPublisher()
    {
        try
        {
            RemoteCASSessionManagerConsumerHome home = (RemoteCASSessionManagerConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASSessionManagerConsumerHome.PUBLISHER_HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find CASAdminPublisher ");
        }
    }

    public static IECRemoteCASCallbackRemovalConsumerHome getRemoteCASCallbackRemovalConsumerHome()
    {
        try
        {
            return (IECRemoteCASCallbackRemovalConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASCallbackRemovalConsumerHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IECRemoteCASCallbackRemovalConsumerHome");
        }
    }

    public static IECRemoteCASRecoveryConsumerHome getRemoteCASRecoveryConsumerHome()
    {
        try
        {
            return (IECRemoteCASRecoveryConsumerHome) HomeFactory.getInstance().findHome(IECRemoteCASRecoveryConsumerHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IECRemoteCASRecoveryConsumerHome");
        }
    }

    public static IECSystemControlConsumerHome getSystemControlConsumerHome()
    {
        try
        {
            return (IECSystemControlConsumerHome) HomeFactory.getInstance().findHome(IECSystemControlConsumerHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find IECSystemControlConsumerHome");
        }
    }

    /**
     * gets the V4 market query service.
     * @return MarketQueryV4 the V4 market query service
     */
    public static MarketQueryV4Home getMarketQueryV4Home()
    {
        try
        {
            return (MarketQueryV4Home) HomeFactory.getInstance().findHome(MarketQueryV4Home.HOME_NAME);
        }
        catch(CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketQueryV4Home");
        }
    }
    public static ProductConfigurationQueryServiceHome getProductConfigurationQueryServiceHome()
    {
        try
        {
            return (ProductConfigurationQueryServiceHome) HomeFactory.getInstance().findHome(ProductConfigurationQueryServiceHome.HOME_NAME);
        }
        catch(CBOELoggableException e)
        {
            Log.alarm("Could not find ProductConfigurationQueryServiceHome");
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ProductConfigurationQueryServiceHome");
        }
    }

    public static PCQSSessionManagerHome getPCQSSessionManagerHome()
    {
        try
        {
            PCQSSessionManagerHome home = (PCQSSessionManagerHome) HomeFactory.getInstance().findHome(PCQSSessionManagerHome.HOME_NAME);
            return home;
        }
        catch(CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find PCQS Session Manager Home.");
        }
    }

    /**
     * Creates a reference to PCQSSessionManager (which will create a PCQS service).
     */
    public static PCQSSessionManager createPCQSSessionManager(SessionManager userSession)
    {
        PCQSSessionManagerHome home = getPCQSSessionManagerHome();
        return home.create(userSession);
    }

    public static GroupServiceHome getGroupServiceHome()
    {
        try
        {
            return (GroupServiceHome) HomeFactory.getInstance().findHome(GroupServiceHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find GroupServiceHome");

        }
    }

    public static IECGroupElementConsumerHome getGroupElementConsumerHome()
    {
        try {
                return (IECGroupElementConsumerHome)HomeFactory.getInstance().findHome(IECGroupElementConsumerHome.HOME_NAME);
        }
        catch (CBOELoggableException e) {
                Log.exception(e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find IECGroupElementConsumerHome (UOQ)");
        }

    } 	

    public static ExternalTradeMaintenanceServiceHome getExternalTradeMaintenanceServiceHome()
    {
		try
        {
            return (ExternalTradeMaintenanceServiceHome) HomeFactory.getInstance().findHome(ExternalTradeMaintenanceServiceHome.HOME_NAME);
        }
        catch(CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find Trade Maintenance Service Home");
        }
    }

    public static ManualReportingServiceHome getManualReportingServiceHome()
    {
        try
        {
            return (ManualReportingServiceHome) HomeFactory.getInstance().findHome(ManualReportingServiceHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.alarm("Could not find ManualReportingServiceHome");
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ManualReportingServiceHome");
        }
    }

    public static FloorSessionManagerHome getFloorSessionManagerHome()
    {

        try
        {
            FloorSessionManagerHome home = (FloorSessionManagerHome) HomeFactory.getInstance().findHome(FloorSessionManagerHome.HOME_NAME);
            return home;
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find FloorSessionManagerHome.");
        }
        
    }

    /**
     * Creates a reference to FloorSessionManager
     */
    public static FloorSessionManager createFloorSessionManager(SessionManager userSession)
    {
        FloorSessionManagerHome home = getFloorSessionManagerHome();
        return home.create(userSession);
    }

    public static OrderManagementServiceHome getOrderManagementServiceHome()
    {
        try
        {
            return (OrderManagementServiceHome) HomeFactory.getInstance().findHome(OrderManagementServiceHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.alarm("Could not find OrderManagementServiceHome");
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderManagementServiceHome");
        }
    }

    public static OMTSessionManagerHome getOMTSessionManagerHome()
    {
        try
        {
            OMTSessionManagerHome home = (OMTSessionManagerHome) HomeFactory.getInstance().findHome(OMTSessionManagerHome.HOME_NAME);
            return home;
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OMT Session Manager Home.");
        }
    }

    /**
     * Creates a reference to OMTSessionManager (which will create a OM service).
     */
    public static OMTSessionManager createOMTSessionManager(SessionManager userSession)
    {
        OMTSessionManagerHome home = getOMTSessionManagerHome();
        return home.create(userSession);
    }

    public static ParOrderManagementServiceHome getParOrderManagementServiceHome()
    {
        try
        {
            return (ParOrderManagementServiceHome) HomeFactory.getInstance().findHome(ParOrderManagementServiceHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.alarm("Could not find ParOrderManagementServiceHome");
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ParOrderManagementServiceHome");
        }
    }
    
    public static ActivityServiceHome getActivityServiceHome()
    {
        try
        {
            return (ActivityServiceHome) HomeFactory.getInstance().findHome(ActivityServiceHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.alarm("Could not find ActivityServiceHome");
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ActivityServiceHome");
        }
    }
    


    public static ChannelListener getOrderRoutingConsumerProxy(OrderRoutingConsumer consumer,
                                                               SessionManager sessionManager,
                                                               boolean gmd)
            throws DataValidationException, SystemException, CommunicationException,
            AuthorizationException
    {
        ChannelListener proxy;

        try
        {
            OrderRoutingConsumerProxyHome home = (OrderRoutingConsumerProxyHome) HomeFactory
                    .getInstance()
                    .findHome(OrderRoutingConsumerProxyHome.HOME_NAME);
            proxy = home.create(consumer, sessionManager, gmd);
        }
        catch(CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderRoutingConsumerProxyHome.");
        }

        return proxy;
    }

    public static IECOrderRoutingConsumerHome getOrderRoutingConsumerHome()
    {
        try
        {
            return (IECOrderRoutingConsumerHome) HomeFactory.getInstance()
                    .findHome(IECOrderRoutingConsumerHome.HOME_NAME);
        }
        catch(CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderRoutingConsumerHome (UOQ)");
        }
    }
    
    /**
     * Returns a reference to orderBookClient handling service.
     */
   /* public static OrderBookServiceClient getOrderBookServiceClient()
    {
        try
        {
            OrderBookServiceClientHome home = (OrderBookServiceClientHome) HomeFactory.getInstance().findHome(OrderBookServiceClientHome.HOME_NAME);
            return home.find();
        } catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderBookServiceClientHome (UOQ)");
        }
    }*/


    public static FloorTradeMaintenanceServiceHome getFloorTradeMaintenanceServiceHome()
    {
        try
        {
             return (FloorTradeMaintenanceServiceHome) HomeFactory.getInstance()
                    .findHome(FloorTradeMaintenanceServiceHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find FloorTradeMaintenanceServiceHome (UOQ)");
        }
    }
    
    public static TradingClassStatusQueryServiceHome getTradingClassStatusQueryServiceHome(){
    	try
        {
             return (TradingClassStatusQueryServiceHome) HomeFactory.getInstance()
                    .findHome(TradingClassStatusQueryServiceHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TradingClassStatusQueryServiceHome (UOQ)");
        }
    }
    
    
    public static TCSProcessWatcherManagerHome getTCSProcessWatcherManagerHome(){
    	try
        {
             return (TCSProcessWatcherManagerHome) HomeFactory.getInstance()
                    .findHome(TCSProcessWatcherManagerHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            log(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TCSProcessWatcherManagerHome (UOQ)");
        }
    }
    
    /**
     * Creates a reference to the V8 session manager
     * Always returns the latest version of Session Manager
     */

	public static SessionManagerV8 createSessionManagerV8(
			SessionProfileUserStructV2 sessionProfileUser, String sessionId,
			int sessionKey, CMIUserSessionAdmin clientListener,
			short sessionType, boolean gmdTextMessaging) throws DataValidationException, SystemException{
		// TODO Auto-generated method stub
		return (SessionManagerV8)createSessionManager(sessionProfileUser, sessionId, sessionKey,
                clientListener, sessionType, gmdTextMessaging);
	}

    /**
     * Creates a reference to the latest session manager
     * Always returns the latest version of Session Manager
     */

    public static SessionManagerV9 createSessionManagerV9(
            SessionProfileUserStructV2 sessionProfileUser, String sessionId,
            int sessionKey, CMIUserSessionAdmin clientListener,
            short sessionType, boolean gmdTextMessaging) throws DataValidationException, SystemException{
        return (SessionManagerV9)createSessionManager(sessionProfileUser, sessionId, sessionKey,
                                                                clientListener, sessionType, gmdTextMessaging);
    }


    public static ProductRoutingService getProductRoutingService(){
    	try
        {
            ProductRoutingServiceHome routingServiceHome = (ProductRoutingServiceHome)(HomeFactory.getInstance().findHome( ProductRoutingServiceHome.HOME_NAME ));
            return routingServiceHome.find();
        }
        catch (CBOELoggableException e)
        {
            log(e);
            throw new NullPointerException("Could not find ProductRoutingService.");
        }
    }
}//EOC
