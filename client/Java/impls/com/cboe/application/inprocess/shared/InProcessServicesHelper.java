package com.cboe.application.inprocess.shared;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.inprocess.*;
import com.cboe.interfaces.application.inprocess.OrderStatusConsumer;
import com.cboe.interfaces.application.inprocess.QuoteStatusConsumer;
import com.cboe.interfaces.application.inprocess.RFQConsumer;
import com.cboe.interfaces.application.inprocess.UserSessionAdminConsumer;
import com.cboe.interfaces.application.inprocess.AuctionConsumer;
import com.cboe.interfaces.application.inprocess.QuoteStatusV2Consumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;
import com.cboe.interfaces.callback.*;
import com.cboe.util.channel.ChannelListener;

import com.cboe.interfaces.application.inprocess.FloorTradeConsumer;
import com.cboe.interfaces.application.FloorTradeMaintenanceServiceHome;
import com.cboe.interfaces.application.UserQuoteService;
import com.cboe.interfaces.application.UserQuoteServiceHome;
import com.cboe.interfaces.callbackServices.HeartBeatCallbackService;
import com.cboe.interfaces.callbackServices.HeartBeatCallbackServiceHome;
import com.cboe.interfaces.businessServices.FirmServiceHome;
import com.cboe.interfaces.businessServices.FirmService;

/**
 * @author Jing Chen
 */
public class InProcessServicesHelper
{
    public static UserAccess getUserAccess()
    {
        try
        {
            UserAccessHome home = (UserAccessHome) HomeFactory.getInstance().findHome(UserAccessHome.HOME_NAME);
            return home.find();
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find InProcessUserAccess Home.");
        }
    }

    public static InProcessSessionManagerHome getSessionManagerHome()
    {
        try
        {
            InProcessSessionManagerHome home = (InProcessSessionManagerHome) HomeFactory.getInstance().findHome(InProcessSessionManagerHome.HOME_NAME);
            return home;
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find InProcess Session Manager Home.");
        }
    }

    public static InProcessTradingSessionHome getTradingSessionHome()
    {
        try
        {
            InProcessTradingSessionHome home = (InProcessTradingSessionHome) HomeFactory.getInstance().findHome(InProcessTradingSessionHome.HOME_NAME);
            return home;
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find InProcess Trading Session Home.");
        }
    }

    public static InProcessSessionManager createSessionManager(SessionProfileUserStructV2 userStruct, String sessionId, int sessionKey, UserSessionAdminConsumer clientListener)
            throws com.cboe.exceptions.DataValidationException, com.cboe.exceptions.SystemException
    {
        try
        {
            InProcessSessionManagerHome home = (InProcessSessionManagerHome) HomeFactory.getInstance().findHome(InProcessSessionManagerHome.HOME_NAME);
            return home.createInProcessSession(userStruct, sessionId, sessionKey, clientListener);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find InProcess Session Manager Home.");
        }
    }

    public static com.cboe.interfaces.application.inprocess.QuoteEntryHome getQuoteEntryHome()
    {
        try
        {
            return (com.cboe.interfaces.application.inprocess.QuoteEntryHome) HomeFactory.getInstance().findHome(QuoteEntryHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteEntryHome");
        }
    }

    public static com.cboe.interfaces.application.inprocess.QuoteQueryHome getQuoteQueryHome()
    {
        try
        {
            return (com.cboe.interfaces.application.inprocess.QuoteQueryHome) HomeFactory.getInstance().findHome(QuoteQueryHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteQueryHome");
        }
    }

    public static OrderEntryHome getOrderEntryHome()
    {
        try
        {
            return (OrderEntryHome) HomeFactory.getInstance().findHome(OrderEntryHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderEntryHome");
        }
    }

    public static UserOrderQueryHome getOrderQueryHome()
    {
        try
        {
            return (UserOrderQueryHome) HomeFactory.getInstance().findHome(UserOrderQueryHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserOrderQueryHome");
        }
    }

    public static MarketQueryHome getMarketQueryHome()
    {
        try
        {
            return (MarketQueryHome) HomeFactory.getInstance().findHome(MarketQueryHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find MarketQueryHome");
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

    // MWM - DAIM 
    public static FirmService getFirmService()
    {
        try
        {
            FirmServiceHome home = (FirmServiceHome) HomeFactory.getInstance().findHome(FirmServiceHome.HOME_NAME);
            return home.find();
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find FirmService");
        }
    }

    // MWM - new
    public static RemoteMarketQueryHome getRemoteMarketQueryHome()
    {
        try
        {
            return (RemoteMarketQueryHome) HomeFactory.getInstance().findHome(RemoteMarketQueryHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find RemoteMarketQueryHome");
        }
    }
    
    // MWM - MMHH support
    public static FloorTradeConsumerHome getFloorTradeMaintenanceServiceHome()
    {
        try
        {
             return (FloorTradeConsumerHome) HomeFactory.getInstance()
                    .findHome(FloorTradeConsumerHome.HOME_NAME);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find FloorTradeMaintenanceServiceHome (UOQ)");
        }
    }
    // MWM - MMHH support
    public static ChannelListener getQuoteStatusV2ConsumerProxy(
        QuoteStatusV2Consumer consumer,
        BaseSessionManager sessionManager)
            throws DataValidationException,
            SystemException,
            CommunicationException,
            AuthorizationException
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.QuoteStatusV2ConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.QuoteStatusV2ConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.QuoteStatusV2ConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusV2ConsumerProxyHome.");
        }
        return listener;
    }
    // MWM - MMHH support
    public static UserQuoteService getUserQuoteService(BaseSessionManager sessionManager)
    {
        try
        {
            UserQuoteServiceHome home = (UserQuoteServiceHome) HomeFactory.getInstance().findHome(UserQuoteServiceHome.HOME_NAME);
            return home.find(sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find UserQuoteService");
        }
    }

    public static ChannelListener getQuoteStatusConsumerProxy(
        QuoteStatusConsumer consumer,
        BaseSessionManager sessionManager)
            throws DataValidationException,
            SystemException,
            CommunicationException,
            AuthorizationException
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.QuoteStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.QuoteStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.QuoteStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getOrderStatusConsumerProxy(
        OrderStatusConsumer consumer,
        BaseSessionManager sessionManager)
            throws DataValidationException,
            SystemException,
            CommunicationException,
            AuthorizationException
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.OrderStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.OrderStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.OrderStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find OrderStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getQuoteNotificationConsumerProxy(LockedQuoteStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.QuoteNotificationConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.QuoteNotificationConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.QuoteNotificationConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteNotificationConsumerProxyHome.");
        }
        return listener;
    }
/* @todo SANTHAN
    public static ChannelListener getMarketAlertConsumerProxy(MarketAlertConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.MarketAlertConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.MarketAlertConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.MarketAlertConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteNotificationConsumerProxyHome.");
        }
        return listener;
    }
*/
    public static ChannelListener getRFQConsumerProxy(RFQConsumer consumer, BaseSessionManager sessionManager)
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.RFQConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.RFQConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.RFQConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find QuoteNotificationConsumerProxyHome.");
        }
        return listener;
    }

    public static InstrumentedChannelListener getAuctionConsumerProxy(AuctionConsumer consumer, BaseSessionManager sessionManager)
    {
        InstrumentedChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.AuctionConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.AuctionConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.AuctionConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find AuctionConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getClassStatusConsumerProxy(ClassStatusConsumer consumer, BaseSessionManager sessionManager)
        throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.ClassStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.ClassStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.ClassStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ClassStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getProductStatusConsumerProxy(ProductStatusConsumer consumer, BaseSessionManager sessionManager)
        throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.ProductStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.ProductStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.ProductStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find ProductStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getStrategyStatusConsumerProxy(StrategyStatusConsumer consumer, BaseSessionManager sessionManager)
        throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.StrategyStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.StrategyStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.StrategyStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find StrategyStatusConsumerProxyHome.");
        }
        return listener;
    }

    public static ChannelListener getTradingSessionStatusConsumerProxy(TradingSessionStatusConsumer consumer, BaseSessionManager sessionManager)
        throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        ChannelListener listener = null;
        try
        {
            com.cboe.interfaces.application.inprocess.TradingSessionStatusConsumerProxyHome home =
                    (com.cboe.interfaces.application.inprocess.TradingSessionStatusConsumerProxyHome) HomeFactory.getInstance()
                    .findHome(com.cboe.interfaces.application.inprocess.TradingSessionStatusConsumerProxyHome.HOME_NAME);
            listener = home.create(consumer, sessionManager);
        }
        catch (CBOELoggableException e)
        {
            Log.exception(e);
            // a really ugly way to get around the missing exception in the interface...
            throw new NullPointerException("Could not find TradingSessionStatusConsumerProxyHome.");
        }
        return listener;
    }
}
