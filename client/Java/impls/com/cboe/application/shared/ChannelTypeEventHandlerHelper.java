package com.cboe.application.shared;

import com.cboe.util.*;
import com.cboe.application.supplier.*;
import com.cboe.interfaces.events.*;
import com.cboe.interfaces.application.*;
import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ChannelTypeEventHandlerHelper
{
    /**
    * Returns the proper instance of the Event Supplier.
    * @param memberKey member key
    * @param channelType channel type
    * @author Connie Feng
    */
    public static BaseSupplier getSupplier(SessionManager session, int channelType)
    {
        switch (channelType)
        {
            case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT:
            case ChannelType.CB_CURRENT_MARKET_BY_CLASS:
                return CurrentMarketSupplierFactory.find(session);

            case ChannelType.CB_NBBO_BY_PRODUCT:
            case ChannelType.CB_NBBO_BY_CLASS:
                return NBBOSupplierFactory.find(session);

            case ChannelType.CB_EXPECTED_OPENING_PRICE:
                return ExpectedOpeningPriceSupplierFactory.find(session);

            case ChannelType.CB_QUOTE_BY_CLASS:
            case ChannelType.CB_ALL_QUOTES:
            case ChannelType.CB_QUOTE_FILLED_REPORT:
            case ChannelType.CB_QUOTE_FILLED_REPORT_BY_CLASS:
            case ChannelType.CB_QUOTE_FILLED_REPORT_BY_FIRM:
            case ChannelType.CB_QUOTE_BUST_REPORT:
            case ChannelType.CB_QUOTE_BUST_REPORT_BY_CLASS:
            case ChannelType.CB_QUOTE_BUST_REPORT_BY_FIRM:
                return QuoteStatusSupplierFactory.find(session);

            case ChannelType.CB_RFQ:
                return RFQSupplierFactory.find();

            case ChannelType.CB_PRODUCT_CLASS_UPDATE:
            case ChannelType.CB_CLASS_STATE:
            case ChannelType.CB_CLASS_UPDATE_BY_TYPE:
            case ChannelType.CB_CLASS_STATE_BY_TYPE:
                return ClassStatusSupplierFactory.find();

            case ChannelType.CB_ORDERS_FOR_PRODUCT:
            case ChannelType.CB_ORDERS_BY_USER:
            case ChannelType.CB_ORDERS_BY_FIRM:
            case ChannelType.CB_ALL_ORDERS:
            case ChannelType.CB_ALL_ORDERS_FOR_TYPE:
            case ChannelType.CB_ORDERS_FOR_SESSION:
            case ChannelType.CB_ORDERS_BY_CLASS:
            case ChannelType.CB_FILLED_REPORT:
            case ChannelType.CB_FILLED_REPORT_BY_FIRM:
            case ChannelType.CB_FILLED_REPORT_FOR_PRODUCT:
            case ChannelType.CB_FILLED_REPORT_FOR_TYPE:
            case ChannelType.CB_FILLED_REPORT_FOR_SESSION:
            case ChannelType.CB_FILLED_REPORT_BY_CLASS:
            case ChannelType.CB_ORDER_BUST_REPORT:
            case ChannelType.CB_ORDER_BUST_REPORT_BY_FIRM:
            case ChannelType.CB_ORDER_BUST_REPORT_FOR_PRODUCT:
            case ChannelType.CB_ORDER_BUST_REPORT_FOR_TYPE:
            case ChannelType.CB_ORDER_BUST_REPORT_FOR_SESSION:
            case ChannelType.CB_ORDER_BUST_REPORT_BY_CLASS:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_FIRM:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_PRODUCT:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_SESSION:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_FOR_TYPE:
            case ChannelType.CB_ORDER_BUST_REINSTATE_REPORT_BY_CLASS:
            case ChannelType.CB_CANCELED_REPORT:
            case ChannelType.CB_CANCELED_REPORT_BY_FIRM:
            case ChannelType.CB_CANCELED_REPORT_FOR_PRODUCT:
            case ChannelType.CB_CANCELED_REPORT_FOR_SESSION:
            case ChannelType.CB_CANCELED_REPORT_FOR_TYPE:
            case ChannelType.CB_CANCELED_REPORT_BY_CLASS:
            case ChannelType.CB_NEW_ORDER_REPORT:
            case ChannelType.CB_NEW_ORDER_REPORT_BY_FIRM:
            case ChannelType.CB_NEW_ORDER_REPORT_FOR_PRODUCT:
            case ChannelType.CB_NEW_ORDER_REPORT_FOR_SESSION:
            case ChannelType.CB_NEW_ORDER_REPORT_FOR_TYPE:
            case ChannelType.CB_NEW_ORDER_REPORT_BY_CLASS:
                return OrderStatusSupplierFactory.find(session);

            case ChannelType.CB_PRODUCT_STATE:
            case ChannelType.CB_PRODUCT_UPDATE:
            case ChannelType.CB_PRODUCT_STATE_BY_CLASS:
            case ChannelType.CB_PRODUCT_UPDATE_BY_CLASS:
            case ChannelType.CB_UPDATE_LINKAGE_INDICATOR:    
                return ProductStatusSupplierFactory.find();

            case ChannelType.CB_TRADING_SESSION_STATE:
                return TradingSessionStatusSupplierFactory.find();

            case ChannelType.CB_RECAP_BY_CLASS:
            case ChannelType.CB_RECAP_BY_PRODUCT:
                return RecapSupplierFactory.find(session);

            case ChannelType.CB_TICKER:
                return TickerSupplierFactory.find(session);

            case ChannelType.CB_LOGOUT:
            case ChannelType.CB_HEARTBEAT:
            case ChannelType.CB_TEXT_MESSAGE:
            case ChannelType.CB_AUTHENTICATION_NOTICE:
            case ChannelType.CB_UNREGISTER_LISTENER:
                return UserSessionAdminSupplierFactory.find(session);

            case ChannelType.CB_STRATEGY_UPDATE:
                return StrategyStatusSupplierFactory.find();

            case ChannelType.CB_NEW_HELD_ORDER:
            case ChannelType.CB_HELD_ORDER_FILLED_REPORT:
            case ChannelType.CB_HELD_ORDER_CANCELED_REPORT:
            case ChannelType.CB_HELD_ORDERS:
                return HeldOrderSupplierFactory.find();

            default :
                if (Log.isDebugOn())
                {
                    Log.debug("ChannelTypeEventHandlerHelper::Unknown channel type: " + channelType);
                }
                return null;
        }
    }

    /**
    * Returns the proper instance of the EventChannelConsumerManager.
    * @param channelType channel type
    * @author Connie Feng
    */
    public static EventChannelConsumerManager getEventChannelConsumerManager(int channelType)
    {
        switch (channelType)
        {
            case ChannelType.CURRENT_MARKET_BY_TYPE:
            case ChannelType.CURRENT_MARKET_BY_PRODUCT:
            case ChannelType.CURRENT_MARKET_BY_CLASS:
            case ChannelType.OPENING_PRICE_BY_TYPE:
            case ChannelType.OPENING_PRICE_BY_CLASS:
            case ChannelType.OPENING_PRICE_BY_PRODUCT:
            case ChannelType.NBBO_BY_CLASS:
            case ChannelType.NBBO_BY_PRODUCT:
                return ServicesHelper.getCurrentMarketConsumerHome();

            case ChannelType.NEW_ORDER:
            case ChannelType.NEW_ORDER_BY_FIRM:
            case ChannelType.ORDER_UPDATE:
            case ChannelType.ORDER_UPDATE_BY_FIRM:
            case ChannelType.CANCEL_REPORT:
            case ChannelType.CANCEL_REPORT_BY_FIRM:
            case ChannelType.ORDER_FILL_REPORT:
            case ChannelType.ORDER_FILL_REPORT_BY_FIRM:
            case ChannelType.ORDER_BUST_REPORT:
            case ChannelType.ORDER_BUST_REPORT_BY_FIRM:
            case ChannelType.ORDER_BUST_REINSTATE_REPORT:
            case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM:
            case ChannelType.ORDER_ACCEPTED_BY_BOOK:
            case ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM:
            case ChannelType.ACCEPT_ORDERS:
            case ChannelType.ACCEPT_ORDERS_BY_FIRM:
               case ChannelType.ORDER_QUERY_EXCEPTION:
                return ServicesHelper.getOrderStatusConsumerV2Home();

            case ChannelType.QUOTE_FILL_REPORT:
            case ChannelType.QUOTE_FILL_REPORT_BY_FIRM:
            case ChannelType.QUOTE_BUST_REPORT:
            case ChannelType.QUOTE_BUST_REPORT_BY_FIRM:
            case ChannelType.QUOTE_DELETE_REPORT:
                return ServicesHelper.getQuoteStatusConsumerV2Home();

            case ChannelType.RFQ:
                return ServicesHelper.getRFQConsumerHome();

            case ChannelType.PRICE_ADJUSTMENT_UPDATED_NOTICE:
            case ChannelType.UPDATE_REPORTING_CLASS:
            case ChannelType.PRICE_ADJUSTMENT_APPLIED_NOTICE:
            case ChannelType.ALL_ADJUSTMENTS_APPLIED_NOTICE:
                return ServicesHelper.getProductStatusConsumerHome();

            case ChannelType.RECAP_BY_TYPE:
            case ChannelType.RECAP_BY_PRODUCT:
            case ChannelType.RECAP_BY_CLASS:
                return ServicesHelper.getRecapConsumerHome();

            case ChannelType.TICKER_BY_TYPE:
            case ChannelType.TICKER_BY_PRODUCT:
            case ChannelType.TICKER_BY_CLASS:
                return ServicesHelper.getTickerConsumerHome();

            case ChannelType.TRADING_SESSION:
            case ChannelType.SET_PRODUCT_STATE:
            case ChannelType.SET_CLASS_STATE:
            case ChannelType.UPDATE_PRODUCT:
            case ChannelType.UPDATE_PRODUCT_CLASS:
            case ChannelType.UPDATE_PRODUCT_BY_CLASS:
            case ChannelType.STRATEGY_UPDATE:
                return ServicesHelper.getTradingSessionConsumerHome();

            case ChannelType.USER_SECURITY_TIMEOUT:
                return ServicesHelper.getUserTimeoutWarningConsumerHome();

            case ChannelType.TEXT_MESSAGE_BY_USER:
                return ServicesHelper.getTextMessageConsumerHome();
            case ChannelType.TEXT_MESSAGE_BY_CLASS:
                return ServicesHelper.getTextMessageConsumerHome();
            case ChannelType.TEXT_MESSAGE_BY_TYPE:
                return ServicesHelper.getTextMessageConsumerHome();

            case ChannelType.CANCEL_HELD_ORDER:
            case ChannelType.FILL_REJECT_REPORT:
            case ChannelType.NEW_HELD_ORDER:
            case ChannelType.HELD_ORDER_CANCEL_REPORT:
            case ChannelType.HELD_ORDER_FILLED_REPORT:
            case ChannelType.HELD_ORDER_STATUS:
            case ChannelType.HELD_ORDERS:
                return ServicesHelper.getIntermarketOrderStatusConsumerHome();

            default :
                if (Log.isDebugOn())
                {
                    Log.debug("ChannelTypeEventHandlerHelper::Unknown channel type: " + channelType);
                }
                return null;
        }
    }

}

