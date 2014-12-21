package com.cboe.consumers.eventChannel;

import java.util.*;

import org.omg.PortableServer.*;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.idl.infrastructureServices.infrastructureEvents.*;
import com.cboe.idl.consumers.*;
import com.cboe.idl.internalConsumers.AlertConsumerHelper;
import com.cboe.idl.calendar.CalendarUpdateConsumerHelper;
import com.cboe.idl.ohsConsumers.OrderRoutingConsumerHelper;
import com.cboe.exceptions.*;
import com.cboe.util.*;

/**
 * Class to hide the event channel filter reference count implementation
 * and the communication to the CBOE event service
 * @author Jeff Illian
 * @author Connie Feng
 */
public class EventChannelFilterHelper
{
    protected EventService eventService = null;
    public final String EVENT_CHANNEL = "EventChannel";
    public static final String ALL_EVENTS_CONSTRAINT = "1";
    public static final String NO_EVENTS_CONSTRAINT = "0";
    public static final int CURRENT_MARKET_AND_NBBO_CHANNEL_TYPE = -99999;

    /**
    * Collection of the event channel filters contained in the home
    * The key will be the channel key and the value will be the EventChannelFilter
    */
    private Hashtable eventChannelFilters;
    private Hashtable channelNames;
    private ConsumerFilter inclusionFilter;

    /**
     * @author Jeff Illian
     */
    public EventChannelFilterHelper() {
        eventChannelFilters = new Hashtable();
        channelNames = new Hashtable();
        inclusionFilter = null;
    }

    /**
    * Finds the EventChannelFilter based on the ChannelKey
    * @param channelKey the event channel key information
    * @return EventChannelFilter the event channel filter object
    */
    private EventChannelFilter findEventChannelFilter(ChannelKey channelKey)
    {
        return (EventChannelFilter)eventChannelFilters.get(channelKey);
    }

    /**
    * Adds the EventChannelFilter to the collection
    * @param eventChannelFilter the event channel filter object
    * @param channelKey the event channel key information
    */
    private void addEventChannelFilter(EventChannelFilter eventChannelFilter, ChannelKey channelKey)
    {
        if ( eventChannelFilter != null)
        {
            eventChannelFilters.put(channelKey, eventChannelFilter);
        }
    }

    /**
    * Removes the EventChannelFilter from the collection based on the channelKey
    * @param channelKey the event channel key information
    */
    private void removeEventChannelFilter(ChannelKey channelKey)
    {
        eventChannelFilters.remove(channelKey);
    }

    /**
     * Clears all the constraints
     * @author Jeff Illian
     */
    protected void clearAllFilters()
    {
        // enumerate all elements in the eventChannelFilters to inform the
        // event service to remove all the filters, then clear the hashtable
    }

    /**
     * Makes the connection to the event service
     * @author Jeff Illian
     */
    public EventService connectEventService() {
        FoundationFramework ff = FoundationFramework.getInstance();

        eventService = ff.getEventService();

        return eventService;
    }

    public EventChannelHomeImpl getEventChannelHome()
    {
        EventChannelHomeImpl eventHome=null;
        try {
            eventHome = ((EventChannelHomeImpl)HomeFactory.getInstance().findHome(EventChannelHome.HOME_NAME));
        }
        catch (Exception e)
        {
             Log.exception("EventChannelFilterHelper -> Exception finding EventChannelHome", e);
        }
        return eventHome;
    }
    /**
     * Returns the event channel name from configuration
     * @author Jeff Illian
     */
    public String getChannelName(String channel)
        throws SystemException
    {
        String theChannelName = (String) channelNames.get(channel);
        if (theChannelName != null)
        {
            return theChannelName;
        }
        try
        {
            PropertyQuery pq = PropertyQuery.queryFor("channelName").from(EVENT_CHANNEL, channel);
            String channelName = getEventChannelHome().getProperty(pq.queryString());
            if (Log.isDebugOn())
            {
                Log.debug("EventChannelFilterHelper -> caching channel name: " + channel + ":" + channelName);
            }
            channelNames.put(channel, channelName);
            return channelName;
        } catch (Exception e) {
            Log.exception("EventChannelFilterHelper -> Exception getting property channelName:" + channel, e);
            throw ExceptionBuilder.systemException(e.toString(), 0);
        }
    }

    private String getDefaultMethod(String channel)
        throws SystemException
    {
        try
        {
            PropertyQuery pq = PropertyQuery.queryFor("defaultMethod").from( EVENT_CHANNEL, channel );
            return getEventChannelHome().getProperty( pq.queryString() );
        } catch (Exception e)
        {
            Log.exception("EventChannelFilterHelper -> Exception getting property", e);
            throw ExceptionBuilder.systemException(e.toString(), 0);
        }
    }

    private String getBaseInterface(String channel)
        throws SystemException
    {
        try
        {
            PropertyQuery pq = PropertyQuery.queryFor("baseInterface").from( EVENT_CHANNEL, channel );
            return getEventChannelHome().getProperty( pq.queryString() );
        } catch (Exception e)
        {
            Log.exception("EventChannelFilterHelper -> Exception getting property", e);
            throw ExceptionBuilder.systemException(e.toString(), 0);
        }
    }

    public void connectConsumer(String channel, String interfaceRepId, Servant servant)
        throws Exception
    {
        if (!getDefaultMethod(channel).equals("NONE")) {
          if (Log.isDebugOn())
          {
              Log.debug("EventChannelFilterHelper -> Turning off event channel : " + getChannelName(channel) + " : " + interfaceRepId);
          }
          inclusionFilter = eventService.createNewInclusionFilter(servant, getBaseInterface(channel), getDefaultMethod(channel), NO_EVENTS_CONSTRAINT, getChannelName(channel));
          eventService.applyFilter(inclusionFilter);
        }
        eventService.connectTypedNotifyChannelConsumer(getChannelName(channel), interfaceRepId, servant);
    }

    /////////////////////// new methods based on the new event service facade impl //////

    /**
     * Adds inclusion/exclusion event filter to the event channel.  Only the input constraint will
     * be added to the event channel.  When the constraintString is "" which
     * indicates no filter is set, the channelKey.key information will be set to
     * "" in order to have the same hashcode for the event filter
     *
     * @param servant event consumer
     * @param constraintString the constraint string formated
     * based on the filter constraint language.
     * @param eventChannelName the event channel name
     * @param createInclusionFilter : true = inclusion filter / false = exclusion filter
     *
     * @author Connie Feng
     */
    public int addEventFilter( Servant         servant
                                ,ChannelKey     channelKey
                                ,String         eventChannelName
                                ,String         constraintString
                                ,boolean        createInclusionFilter
                                )
        throws SystemException
    {
        int             count       = -1;
        ConsumerFilter  theFilter   = null;

        if ( eventService != null )
        {
            try
            {
                if(constraintString.equals(ALL_EVENTS_CONSTRAINT) || constraintString.equals(NO_EVENTS_CONSTRAINT))
                {
                    channelKey = new ChannelKey(channelKey.channelType, constraintString);
                }

                EventChannelFilter eventChannelFilter = findEventChannelFilter(channelKey);

                if ( eventChannelFilter != null )
                {
                    // increase the reference count
                    count = eventChannelFilter.increase();
                }
                else // create the filter
                {
                    String interfaceRepId = getInterfaceRepIdForFilter(channelKey);
                    String methodName = getMethodName(channelKey);

                    if (Log.isDebugOn())
                    {
                        Log.debug("EventChannelFilterHelper -> Inclusion for channel: " + channelKey + " : " + createInclusionFilter + " Applying filter : " + eventChannelName + " : " + interfaceRepId + " ::: " + methodName + " = " + constraintString);
                    }

                    if ( createInclusionFilter )
                    {
                        theFilter = eventService.createNewInclusionFilter(servant, interfaceRepId, methodName, constraintString, eventChannelName);
                    }
                    else
                    {
                        theFilter = eventService.createNewExclusionFilter(servant, interfaceRepId, methodName, constraintString, eventChannelName);
                    }
                    eventService.applyFilter(theFilter);

                    eventChannelFilter = new EventChannelFilter(theFilter);
//                    eventService.applyFilter(theFilter);

//                    Log.debug("****CAS REPID: " + theFilter.getRepositoryId());
//                    Log.debug("****CAS METHODNAME: " + theFilter.getMethodName());
//                    Log.debug("****CAS getConstraintStr: " + theFilter.getConstraintStr());
//                    Log.debug("****CAS eventChannelName: " + eventChannelName);

                    // added it to the collection of filters

/*
                    Enumeration enum = eventService.getAllFilters(servant);

                    ConsumerFilter cf;
                    while (enum.hasMoreElements()) {
                        cf = (ConsumerFilter)enum.nextElement();
                        Log.debug(cf.getConstraintStr());
                    }
*/
                    addEventChannelFilter(eventChannelFilter, channelKey);
                    count = 1;
                }
            }
            catch (Exception e)
            {
                Log.exception("EventChannelFilterHelper -> Error adding event filter", e);
                throw ExceptionBuilder.systemException(e.toString(), 0);
            }
        }
        return count;
    }

    /**
     * Removes event filter from the event channel.  Only the input constraint will
     * be removed to the event channel.  When the constraintString is "" which
     * indicates no filter is set, the channelKey.key information will be set to
     * "" in order to have the same hashcode for the event filter
     *
     * @param constraintString the constraint string formated
     * based on the filter constraint language.
     *
     * @author Connie Feng
     */
    public int removeEventFilter(ChannelKey channelKey, String constraintString)
        throws SystemException
    {
        int count = -1;

        if ( eventService != null )
        {
            try
            {
                if(constraintString.equals(ALL_EVENTS_CONSTRAINT) || constraintString.equals(NO_EVENTS_CONSTRAINT))
                {
                    channelKey = new ChannelKey(channelKey.channelType, constraintString);
                }

                EventChannelFilter eventChannelFilter = findEventChannelFilter(channelKey);

                if ( eventChannelFilter != null )
                {
                    // if the reference count is 0, inform the event service to remove the filter
                    // and then clean it up from the internal EventChannelFilter collection
                    count = eventChannelFilter.decrease();

                    if ( count == 0 )
                    {
                        removeEventChannelFilter(channelKey);
                        eventService.removeFilter(eventChannelFilter.getConsumerFilter());
                    }
                }
            }
            catch (Exception e)
            {
                Log.exception("EventChannelFilterHelper -> Error removing event filter", e);
                throw ExceptionBuilder.systemException(e.toString(), 0);
             }
        }
        return count;
    }// end of removeEventFilter


    /**
     * Adds event filter to the event channel.  Only the input constraint will
     * be added to the event channel.  When the constraintString is "" which
     * indicates no filter is set, the channelKey.key information will be set to
     * "" in order to have the same hashcode for the event filter
     *
     * @param servant event consumer
     * @param constraintString the constraint string formated
     * based on the filter constraint language.
     * @param eventChannelName the event channel name
     *
     * @author Connie Feng
     */
    public int addEventFilter(  Servant        servant,
                                ChannelKey     channelKey,
                                String         eventChannelName,
                                String         constraintString
                                )
        throws SystemException
    {
        return addEventFilter( servant, channelKey, eventChannelName, constraintString, true );
    }// end of addEventFilter

    /**
     * Returns the interface repository ID using the channel key.
     * The repository ID is the actual interface that has the method,
     * not the interface of the consumer.  For example, in the case of
     * OrderStatus, instead of passing the interface Id or OrderStatusEventConsumer,
     * it passes the NewOrderConsumer interface ID for acceptNewOrder and the
     * CancelReportConsumer id for acceptCancelReport.
     *
     * @param channelKey the event channel key
     * @author Connie Feng
     */
    protected String getInterfaceRepIdForFilter(ChannelKey channelKey)
    {
        // for now not handeling TRADEREPORT channnelType
        switch (channelKey.channelType)
        {
            case ChannelType.MDCAS_RECOVERY:
                return RemoteCASRecoveryConsumerHelper.id();
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_CLASS:
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_PRODUCT:
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_CLASS:
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_PRODUCT:
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_CLASS_V2:
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V2:
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_CLASS_V2:
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V2:
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_CLASS_V3:
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V3:
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_CLASS_V3:
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V3:
                return RemoteCASCurrentMarketConsumerHelper.id();
            case ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2:
            case ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2:
            case ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2:
            case ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2:
            case ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT:
            case ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT:
                return RemoteCASBookDepthConsumerHelper.id();
            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2:
            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2:
            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2:
            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2:
            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS:
            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS:
                return RemoteCASExpectedOpeningPriceConsumerHelper.id();
            case ChannelType.SUBSCRIBE_NBBO_BY_CLASS_V2:
            case ChannelType.SUBSCRIBE_NBBO_BY_PRODUCT_V2:
            case ChannelType.UNSUBSCRIBE_NBBO_BY_CLASS_V2:
            case ChannelType.UNSUBSCRIBE_NBBO_BY_PRODUCT_V2:
            case ChannelType.SUBSCRIBE_NBBO_BY_CLASS:
            case ChannelType.SUBSCRIBE_NBBO_BY_PRODUCT:
            case ChannelType.UNSUBSCRIBE_NBBO_BY_CLASS:
            case ChannelType.UNSUBSCRIBE_NBBO_BY_PRODUCT:
                return RemoteCASNBBOConsumerHelper.id();
            case ChannelType.SUBSCRIBE_ORDER_BOOK_UPDATE_BY_CLASS_V2:
            case ChannelType.SUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT_V2:
            case ChannelType.UNSUBSCRIBE_ORDER_BOOK_UPDATE_BY_CLASS_V2:
            case ChannelType.UNSUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT_V2:
            case ChannelType.SUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT:
            case ChannelType.UNSUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT:
                return RemoteCASBookDepthUpdateConsumerHelper.id();
            case ChannelType.SUBSCRIBE_RECAP_BY_CLASS_V2:
            case ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT_V2:
            case ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS_V2:
            case ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT_V2:
            case ChannelType.SUBSCRIBE_RECAP_BY_CLASS:
            case ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT:
            case ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS:
            case ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT:
                return RemoteCASRecapConsumerHelper.id();
            case ChannelType.SUBSCRIBE_TICKER_BY_CLASS_V2:
            case ChannelType.SUBSCRIBE_TICKER_BY_PRODUCT_V2:
            case ChannelType.UNSUBSCRIBE_TICKER_BY_CLASS_V2:
            case ChannelType.UNSUBSCRIBE_TICKER_BY_PRODUCT_V2:
            case ChannelType.SUBSCRIBE_TICKER_BY_PRODUCT:
            case ChannelType.UNSUBSCRIBE_TICKER_BY_PRODUCT:
            case ChannelType.SUBSCRIBE_LARGE_TRADE_LAST_SALE_BY_CLASS:
            case ChannelType.UNSUBSCRIBE_LARGE_TRADE_LAST_SALE_BY_CLASS:
                return RemoteCASTickerConsumerHelper.id();
            case ChannelType.MDCAS_LOGOUT:
                return RemoteCASSessionManagerConsumerHelper.id();
            case ChannelType.MDCAS_CALLBACK_REMOVAL:
                return RemoteCASCallbackRemovalConsumerHelper.id();
            case ChannelType.UPDATE_PROPERTY_ENABLEMENT:
            case ChannelType.REMOVE_PROPERTY_ENABLEMENT:
            case ChannelType.UPDATE_PROPERTY:
            case ChannelType.REMOVE_PROPERTY:
            case ChannelType.UPDATE_PROPERTY_RATELIMIT:
            case ChannelType.REMOVE_PROPERTY_RATELIMIT:
                return PropertyConsumerHelper.id();
            case ChannelType.CURRENT_MARKET_BY_PRODUCT :
            case ChannelType.CURRENT_MARKET_BY_CLASS :
            case ChannelType.CURRENT_MARKET_BY_CLASS_SEQ :
            case ChannelType.CURRENT_MARKET_BY_TYPE :
            case ChannelType.OPENING_PRICE_BY_PRODUCT :
            case ChannelType.OPENING_PRICE_BY_CLASS :
            case ChannelType.OPENING_PRICE_BY_CLASS_SEQ :
            case ChannelType.OPENING_PRICE_BY_TYPE  :
            case ChannelType.NBBO_BY_CLASS:
            case ChannelType.NBBO_BY_PRODUCT:
            case CURRENT_MARKET_AND_NBBO_CHANNEL_TYPE:
                return CurrentMarketConsumerHelper.id();
            case ChannelType.OPENING_PRICE_BY_CLASS_FOR_MDX:
            case ChannelType.OPENING_PRICE_BY_CLASS_SEQ_FOR_MDX:
                return ExpectedOpeningPriceConsumerHelper.id();
            case ChannelType.BOOK_DEPTH_BY_PRODUCT:
            case ChannelType.BOOK_DEPTH_BY_CLASS:
            case ChannelType.BOOK_DEPTH_BY_CLASS_SEQ:
                return BookDepthConsumerHelper.id();
            case ChannelType.NEW_ORDER :
                return NewOrderConsumerHelper.id();
           case ChannelType.NEW_ORDER_BY_FIRM :
                return NewOrderConsumerHelper.id();
            case ChannelType.ORDER_UPDATE :
                return OrderUpdateConsumerHelper.id();
            case ChannelType.ORDER_UPDATE_BY_FIRM :
                return OrderUpdateConsumerHelper.id();
            case ChannelType.ORDER_STATUS_UPDATE :
                return OrderUpdateConsumerHelper.id();
            case ChannelType.CANCEL_REPORT :
                return CancelReportConsumerHelper.id();
            case ChannelType.CANCEL_REPORT_BY_FIRM :
                return CancelReportConsumerHelper.id();
            case ChannelType.ORDER_FILL_REPORT :
                return OrderFillReportConsumerHelper.id();
            case ChannelType.ORDER_FILL_REPORT_BY_FIRM :
                return OrderFillReportConsumerHelper.id();
            case ChannelType.ORDER_BUST_REPORT :
                return OrderBustReportConsumerHelper.id();
            case ChannelType.ORDER_BUST_REPORT_BY_FIRM :
                return OrderBustReportConsumerHelper.id();
            case ChannelType.ORDER_BUST_REINSTATE_REPORT :
                return OrderBustReinstateReportConsumerHelper.id();
            case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM :
                return OrderBustReinstateReportConsumerHelper.id();
            case ChannelType.ORDER_ACCEPTED_BY_BOOK :
                return OrderAcceptedByBookConsumerHelper.id();
            case ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM :
                return OrderAcceptedByBookConsumerHelper.id();
            case ChannelType.ACCEPT_ORDERS :
                return OrderQueryConsumerHelper.id();
            case ChannelType.ACCEPT_ORDERS_BY_FIRM :
                return OrderQueryConsumerHelper.id();
            case ChannelType.ORDER_QUERY_EXCEPTION :
                return OrderQueryExceptionConsumerHelper.id();
            case ChannelType.QUOTE_FILL_REPORT :
                return QuoteFillReportConsumerHelper.id();
            case ChannelType.QUOTE_FILL_REPORT_BY_FIRM :
                return QuoteFillReportConsumerHelper.id();
            case ChannelType.QUOTE_STATUS_UPDATE :
                return QuoteUpdateConsumerHelper.id();
            case ChannelType.QUOTE_BUST_REPORT :
                return QuoteBustReportConsumerHelper.id();
            case ChannelType.QUOTE_BUST_REPORT_BY_FIRM :
                return QuoteBustReportConsumerHelper.id();
            case ChannelType.QUOTE_DELETE_REPORT :
            case ChannelType.QUOTES_DELETE_REPORTV2 :
                return QuoteDeleteReportConsumerHelper.id();
            case ChannelType.QUOTE_LOCKED_NOTIFICATION :
            case ChannelType.QUOTE_LOCKED_NOTIFICATION_BY_CLASS :
                return QuoteNotificationConsumerHelper.id();
            case ChannelType.RFQ :
                return RFQConsumerHelper.id();
            case ChannelType.SET_PRODUCT_STATE :
                return ProductStateConsumerHelper.id();
            case ChannelType.SET_CLASS_STATE :
                return ProductStateConsumerHelper.id();
            case ChannelType.PQS_PRICE_ADJUSTMENT_APPLIED_NOTICE :
            case ChannelType.PRICE_ADJUSTMENT_APPLIED_NOTICE :
                return ProductAdjustmentConsumerHelper.id();
            case ChannelType.PQS_PRICE_ADJUSTMENT_UPDATED_NOTICE :
            case ChannelType.PRICE_ADJUSTMENT_UPDATED_NOTICE :
                return ProductAdjustmentConsumerHelper.id();
            case ChannelType.PQS_ALL_ADJUSTMENTS_APPLIED_NOTICE :
            case ChannelType.ALL_ADJUSTMENTS_APPLIED_NOTICE :
                return ProductAdjustmentConsumerHelper.id();
            case ChannelType.UPDATE_PRODUCT :
                return TradingSessionConsumerHelper.id();
            case ChannelType.UPDATE_PRODUCT_CLASS :
                return TradingSessionConsumerHelper.id();
            case ChannelType.UPDATE_PRODUCT_BY_CLASS :
                return TradingSessionConsumerHelper.id();
            case ChannelType.UPDATE_REPORTING_CLASS :
                return TradingSessionConsumerHelper.id();
            case ChannelType.PQS_UPDATE_PRODUCT :
                return ProductUpdateConsumerHelper.id();
            case ChannelType.PQS_UPDATE_PRODUCT_CLASS :
                return ProductUpdateConsumerHelper.id();
            case ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS :
                return ProductUpdateConsumerHelper.id();
            case ChannelType.PQS_UPDATE_REPORTING_CLASS :
                return ProductUpdateConsumerHelper.id();
            case ChannelType.PQS_STRATEGY_UPDATE :
                return ProductUpdateConsumerHelper.id();
            case ChannelType.PS_UPDATE_LINKAGE_INDICATOR :
                return ProductUpdateConsumerHelper.id();
            case ChannelType.RECAP_BY_PRODUCT :
                return RecapConsumerHelper.id();
            case ChannelType.RECAP_BY_CLASS :
            case ChannelType.RECAP_BY_CLASS_SEQ :
                return RecapConsumerHelper.id();
            case ChannelType.RECAP_BY_TYPE :
                return RecapConsumerHelper.id();
            case ChannelType.TICKER_BY_PRODUCT :
                return TickerConsumerHelper.id();
            case ChannelType.TICKER_BY_CLASS :
            case ChannelType.TICKER_BY_CLASS_SEQ :
            case ChannelType.LARGE_TRADE_LAST_SALE_BY_CLASS :
                return TickerConsumerHelper.id();
            case ChannelType.TICKER_BY_TYPE :
                return TickerConsumerHelper.id();
            case ChannelType.TRADING_SESSION :
                return TradingSessionConsumerHelper.id();
            case ChannelType.STRATEGY_UPDATE :
                 return TradingSessionConsumerHelper.id();
            case ChannelType.BUSINESS_DAY :
                 return TradingSessionConsumerHelper.id();
            case ChannelType.USER_SECURITY_TIMEOUT:
                 return UserTimeoutWarningConsumerHelper.id();
            case ChannelType.TEXT_MESSAGE_BY_USER:
                 return TextMessageConsumerHelper.id();
            case ChannelType.TEXT_MESSAGE_BY_CLASS:
                 return TextMessageConsumerHelper.id();
            case ChannelType.TEXT_MESSAGE_BY_TYPE:
                 return TextMessageConsumerHelper.id();

            case ChannelType.USER_EVENT_ADD_USER:
            case ChannelType.USER_EVENT_DELETE_USER:
            case ChannelType.USER_EVENT_ADD_FIRM:
            case ChannelType.USER_EVENT_DELETE_FIRM:
            case ChannelType.USER_EVENT_USER_FIRM_AFFILIATION_UPDATE:
            case ChannelType.USER_EVENT_USER_FIRM_AFFILIATION_DELETE:
                     return CacheUpdateConsumerHelper.id();

            case ChannelType.HELD_ORDER_CANCEL_REPORT:
            case ChannelType.HELD_ORDER_FILLED_REPORT:
            case ChannelType.HELD_ORDER_STATUS:
            case ChannelType.HELD_ORDERS:
            case ChannelType.CANCEL_HELD_ORDER:
            case ChannelType.FILL_REJECT_REPORT:
            case ChannelType.NEW_HELD_ORDER:
                     return IntermarketOrderStatusConsumerHelper.id();
            case ChannelType.ALERT_SATISFACTION:
            case ChannelType.ALERT_SATISFACTION_ALL:
            case ChannelType.ALERT:
            case ChannelType.ALERT_ALL:
            case ChannelType.ALERT_UPDATE:
            case ChannelType.ALERT_UPDATE_ALL:
                    return AlertConsumerHelper.id();
            case ChannelType.INTERMARKET_ADMIN_MESSAGE:
            case ChannelType.INTERMARKET_ADMIN_MESSAGE_BROADCAST:
                    return IntermarketAdminMessageConsumerHelper.id();
            case ChannelType.NBBO_AGENT_FORCED_OUT:
            case ChannelType.NBBO_AGENT_REMINDER:
                     return NBBOAgentAdminConsumerHelper.id();

            case ChannelType.CASADMIN_ADD_USER:
            case ChannelType.CASADMIN_REMOVE_USER:
            case ChannelType.CASADMIN_ADD_FIRM:
            case ChannelType.CASADMIN_REMOVE_FIRM:
            case ChannelType.CASADMIN_ADD_RFQ_CLASS_FOR_USER:
            case ChannelType.CASADMIN_REMOVE_RFQ_CLASS_FOR_USER:
            case ChannelType.CASADMIN_ADD_CURRENTMARKET_CLASS_FOR_USER:
            case ChannelType.CASADMIN_REMOVE_CURRENTMARKET_CLASS_FOR_USER:
            case ChannelType.CASADMIN_ADD_OPENINGPRICE_CLASS_FOR_USER:
            case ChannelType.CASADMIN_REMOVE_OPENINGPRICE_CLASS_FOR_USER:
            case ChannelType.CASADMIN_ADD_TICKER_CLASS_FOR_USER:
            case ChannelType.CASADMIN_REMOVE_TICKER_CLASS_FOR_USER:
            case ChannelType.CASADMIN_ADD_RECAP_CLASS_FOR_USER:
            case ChannelType.CASADMIN_REMOVE_RECAP_CLASS_FOR_USER:
            case ChannelType.CASADMIN_ADD_BOOKDEPTH_PRODUCT_FOR_USER:
            case ChannelType.CASADMIN_REMOVE_BOOKDEPTH_PRODUCT_FOR_USER:
            case ChannelType.CASADMIN_ADD_QUOTE_LOCKED_NOTIFICATION:
            case ChannelType.CASADMIN_REMOVE_QUOTE_LOCKED_NOTIFICATION:
                {
                    return CASAdminConsumerHelper.id();
                }
            case ChannelType.CALENDAR_UPDATE:
                return CalendarUpdateConsumerHelper.id();
            case ChannelType.SERVER_FAILURE:
            case ChannelType.USER_ACTIVITY_TIMEOUT:
            case ChannelType.GROUP_CANCEL:
                return SystemControlConsumerHelper.id();
            case ChannelType.AUCTION_USER:
            case ChannelType.DAIM_USER:	
            case ChannelType.AUCTION:
                return AuctionConsumerHelper.id();
            case ChannelType.OMT_ORDER_ACCEPTED:
            case ChannelType.OMT_ORDER_CANCELED:
            case ChannelType.OMT_ORDER_CANCEL_REPLACED:
            case ChannelType.OMT_FILL_REPORT_REJECT:
            case ChannelType.OMT_ORDER_REMOVED:
            case ChannelType.OMT_LINKAGE_CANCEL_REPORT:
            case ChannelType.OMT_LINKAGE_FILL_REPORT:
            case ChannelType.OMT_ORDERS_FOR_LOCATION:
            case ChannelType.OMT_LOCATION_SUMMARY:
            case ChannelType.OMT_REMOVE_MESSAGE:
            case ChannelType.OMT_TRADE_NOTIFICATION:
            case ChannelType.OMT_FILL_REPORT_DROP_COPY:
            case ChannelType.OMT_CANCEL_REPORT_DROP_COPY:
            case ChannelType.OMT_MANUAL_ORDER_TIMEOUT:
            case ChannelType.OMT_MANUAL_FILL_TIMEOUT:
            case ChannelType.PAR_ORDER_ACCEPTED:
            case ChannelType.PAR_ORDER_CANCELED:
            case ChannelType.PAR_ORDER_CANCEL_REPLACED:
                return OrderRoutingConsumerHelper.id();
            case ChannelType.MARKET_BUFFER_CM_BY_SERVER:
            case ChannelType.MARKET_BUFFER_CM_BY_MDCASSET:
                return MarketBufferConsumerHelper.id();
            default :
                if  (Log.isDebugOn())
                {
                    Log.debug("EventChannelFilterHelper::Unknown channel type: " + channelKey.channelType);
                }
                return "";
        }
    }
    /**
     * Returns the method name based on the channel key
     * @author Connie Feng
     */
    protected String getMethodName(ChannelKey channelKey)
    {
        // for now not handeling TRADEREPORT channnelType
        switch (channelKey.channelType)
        {
            case ChannelType.MDCAS_RECOVERY:
                return "acceptMarketDataRecoveryForGroup";
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_CLASS:
                return "subscribeCurrentMarketForClass";
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_CLASS_V2:
                return "subscribeCurrentMarketForClassV2";
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_CLASS_V3:
                return "subscribeCurrentMarketForClassV3";
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_PRODUCT:
                return "subscribeCurrentMarketForProduct";
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V2:
                return "subscribeCurrentMarketForProductV2";
            case ChannelType.SUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V3:
                return "subscribeCurrentMarketForProductV3";
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_CLASS:
                return "unsubscribeCurrentMarketForClass";
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_CLASS_V2:
                return "unsubscribeCurrentMarketForClassV2";
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_CLASS_V3:
                return "unsubscribeCurrentMarketForClassV3";
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_PRODUCT:
                return "unsubscribeCurrentMarketForProduct";
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V2:
                return "unsubscribeCurrentMarketForProductV2";
            case ChannelType.UNSUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V3:
                return "unsubscribeCurrentMarketForProductV3";
            case ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2:
                return "subscribeBookDepthForClassV2";
            case ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2:
                return "subscribeBookDepthForProductV2";
            case ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT:
                return "subscribeBookDepthForProduct";
            case ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2:
                return "unsubscribeBookDepthForClassV2";
            case ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2:
                return "unsubscribeBookDepthForProductV2";
            case ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT:
                return "unsubscribeBookDepthForProduct";
            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2:
                return "subscribeExpectedOpeningPriceForClassV2";
            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS:
                return "subscribeExpectedOpeningPriceForClass";
            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2:
                return "subscribeExpectedOpeningPriceForProductV2";
            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2:
                return "unsubscribeExpectedOpeningPriceForClassV2";
            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS:
                return "unsubscribeExpectedOpeningPriceForClass";
            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2:
                return "unsubscribeExpectedOpeningPriceForProductV2";
            case ChannelType.SUBSCRIBE_NBBO_BY_CLASS:
                return "subscribeNBBOForClass";
            case ChannelType.SUBSCRIBE_NBBO_BY_CLASS_V2:
                return "subscribeNBBOForClassV2";
            case ChannelType.SUBSCRIBE_NBBO_BY_PRODUCT:
                return "subscribeNBBOForProduct";
            case ChannelType.SUBSCRIBE_NBBO_BY_PRODUCT_V2:
                return "subscribeNBBOForProductV2";
            case ChannelType.UNSUBSCRIBE_NBBO_BY_CLASS:
                return "unsubscribeNBBOForClass";
            case ChannelType.UNSUBSCRIBE_NBBO_BY_CLASS_V2:
                return "unsubscribeNBBOForClassV2";
            case ChannelType.UNSUBSCRIBE_NBBO_BY_PRODUCT:
                return "unsubscribeNBBOForProduct";
            case ChannelType.UNSUBSCRIBE_NBBO_BY_PRODUCT_V2:
                return "unsubscribeNBBOForProductV2";
            case ChannelType.SUBSCRIBE_ORDER_BOOK_UPDATE_BY_CLASS_V2:
                return "subscribeBookDepthUpdateForClassV2";
            case ChannelType.SUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT_V2:
                return "subscribeBookDepthUpdateForProductV2";
            case ChannelType.SUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT:
                return "subscribeBookDepthUpdateForProduct";
            case ChannelType.UNSUBSCRIBE_ORDER_BOOK_UPDATE_BY_CLASS_V2:
                return "unsubscribeBookDepthUpdateForClass";
            case ChannelType.UNSUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT_V2:
                return "unsubscribeBookDepthUpdateForProductV2";
            case ChannelType.UNSUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT:
                return "unsubscribeBookDepthUpdateForProduct";
            case ChannelType.SUBSCRIBE_RECAP_BY_CLASS_V2:
                return "subscribeRecapForClassV2";
            case ChannelType.SUBSCRIBE_RECAP_BY_CLASS:
                return "subscribeRecapForClass";
            case ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT_V2:
                return "subscribeRecapForProductV2";
            case ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT:
                return "subscribeRecapForProduct";
            case ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS_V2:
                return "unsubscribeRecapForClassV2";
            case ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS:
                return "unsubscribeRecapForClass";
            case ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT_V2:
                return "unsubscribeRecapForProductV2";
            case ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT:
                return "unsubscribeRecapForProduct";
            case ChannelType.SUBSCRIBE_TICKER_BY_CLASS_V2:
                return "subscribeTickerForClassV2";
            case ChannelType.SUBSCRIBE_LARGE_TRADE_LAST_SALE_BY_CLASS:
            	return "subscribeLargeTradeLastSaleForClass";
            case ChannelType.UNSUBSCRIBE_LARGE_TRADE_LAST_SALE_BY_CLASS:
            	return "unsubscribeLargeTradeLastSaleForClass";
            case ChannelType.SUBSCRIBE_TICKER_BY_PRODUCT_V2:
                return "subscribeTickerForProductV2";
            case ChannelType.SUBSCRIBE_TICKER_BY_PRODUCT:
                return "subscribeTickerForProduct";
            case ChannelType.UNSUBSCRIBE_TICKER_BY_CLASS_V2:
                return "unsubscribeTickerForClassV2";
            case ChannelType.UNSUBSCRIBE_TICKER_BY_PRODUCT_V2:
                return "unsubscribeTickerForProductV2";
            case ChannelType.UNSUBSCRIBE_TICKER_BY_PRODUCT:
                return "unsubscribeTickerForProduct";
            case ChannelType.MDCAS_LOGOUT:
                return "logout";
            case ChannelType.MDCAS_CALLBACK_REMOVAL:
                return "acceptCallbackRemoval";
            case ChannelType.CURRENT_MARKET_BY_TYPE :
                return "acceptCurrentMarket";
            case ChannelType.CURRENT_MARKET_BY_PRODUCT :
                return "acceptCurrentMarket";
            case ChannelType.CURRENT_MARKET_BY_CLASS :
                return "acceptCurrentMarket";
            case ChannelType.CURRENT_MARKET_BY_CLASS_SEQ :
                return "acceptCurrentMarketsForClass";
            case ChannelType.MARKET_BUFFER_CM_BY_SERVER:
            case ChannelType.MARKET_BUFFER_CM_BY_MDCASSET:
                return "acceptMarketBuffer";
            case ChannelType.BOOK_DEPTH_BY_PRODUCT :
            case ChannelType.BOOK_DEPTH_BY_CLASS :
                return "acceptBookDepth";
            case ChannelType.BOOK_DEPTH_BY_CLASS_SEQ :
                return "acceptBookDepthForClass";
            case ChannelType.QUOTE_LOCKED_NOTIFICATION :
                return "acceptQuoteLockedNotification";
            case ChannelType.QUOTE_LOCKED_NOTIFICATION_BY_CLASS :
                return "acceptQuoteLockedNotificationForClass";
            case ChannelType.OPENING_PRICE_BY_TYPE :
            case ChannelType.OPENING_PRICE_BY_PRODUCT :
            case ChannelType.OPENING_PRICE_BY_CLASS :
            case ChannelType.OPENING_PRICE_BY_CLASS_FOR_MDX:    
                return "acceptExpectedOpeningPrice";
            case ChannelType.OPENING_PRICE_BY_CLASS_SEQ :
            case ChannelType.OPENING_PRICE_BY_CLASS_SEQ_FOR_MDX :    
                return "acceptExpectedOpeningPricesForClass";
            case ChannelType.NEW_ORDER :
                return "acceptNewOrder";
            case ChannelType.NEW_ORDER_BY_FIRM :
                return "acceptNewOrder";
            case ChannelType.ORDER_UPDATE :
                return "acceptOrderUpdate";
            case ChannelType.ORDER_UPDATE_BY_FIRM :
                return "acceptOrderUpdate";
            case ChannelType.ORDER_STATUS_UPDATE :
                return "acceptOrderStatusUpdate";
            case ChannelType.CANCEL_REPORT :
                return "acceptCancelReport";
            case ChannelType.CANCEL_REPORT_BY_FIRM :
                return "acceptCancelReport";
            case ChannelType.ORDER_FILL_REPORT :
                return "acceptOrderFillReport";
            case ChannelType.ORDER_FILL_REPORT_BY_FIRM :
                return "acceptOrderFillReport";
            case ChannelType.ORDER_BUST_REPORT :
                return "acceptOrderBustReport";
            case ChannelType.ORDER_BUST_REPORT_BY_FIRM :
                return "acceptOrderBustReport";
            case ChannelType.ORDER_BUST_REINSTATE_REPORT :
                return "acceptOrderBustReinstateReport";
            case ChannelType.ORDER_BUST_REINSTATE_REPORT_BY_FIRM :
                return "acceptOrderBustReinstateReport";
            case ChannelType.ORDER_ACCEPTED_BY_BOOK :
                return "acceptOrderAcceptedByBook";
            case ChannelType.ORDER_ACCEPTED_BY_BOOK_BY_FIRM :
                return "acceptOrderAcceptedByBook";
            case ChannelType.ACCEPT_ORDERS :
                return "acceptOrders";
            case ChannelType.ACCEPT_ORDERS_BY_FIRM :
                return "acceptOrders";
            case ChannelType.ORDER_QUERY_EXCEPTION :
                return "acceptException";
            case ChannelType.QUOTE_FILL_REPORT :
                return "acceptQuoteFillReport";
            case ChannelType.QUOTE_FILL_REPORT_BY_FIRM :
                return "acceptQuoteFillReport";
            case ChannelType.QUOTE_BUST_REPORT :
                return "acceptQuoteBustReport";
            case ChannelType.QUOTE_BUST_REPORT_BY_FIRM :
                return "acceptQuoteBustReport";
            case ChannelType.QUOTE_DELETE_REPORT :
                return "acceptQuoteDeleteReport";
            case ChannelType.QUOTES_DELETE_REPORTV2 :
                return "acceptQuoteDeleteReportV2";
            case ChannelType.QUOTE_STATUS_UPDATE :
                return "acceptQuoteStatusUpdate";
            case ChannelType.UPDATE_PROPERTY :
            case ChannelType.UPDATE_PROPERTY_ENABLEMENT:
            case ChannelType.UPDATE_PROPERTY_RATELIMIT:
                return "acceptPropertyUpdate";
            case ChannelType.REMOVE_PROPERTY :
            case ChannelType.REMOVE_PROPERTY_ENABLEMENT:
            case ChannelType.REMOVE_PROPERTY_RATELIMIT:
                return "acceptPropertyRemove";
            case ChannelType.RFQ :
                return "acceptRFQ";
            case ChannelType.SET_PRODUCT_STATE :
                return "setProductStates";
            case ChannelType.SET_CLASS_STATE :
                return "setClassState";
            case ChannelType.PQS_PRICE_ADJUSTMENT_UPDATED_NOTICE :
            case ChannelType.PRICE_ADJUSTMENT_UPDATED_NOTICE :
                return "priceAdjustmentUpdatedNotice";
            case ChannelType.PQS_PRICE_ADJUSTMENT_APPLIED_NOTICE :
            case ChannelType.PRICE_ADJUSTMENT_APPLIED_NOTICE :
                return "priceAdjustmentAppliedNotice";
            case ChannelType.PQS_ALL_ADJUSTMENTS_APPLIED_NOTICE :
            case ChannelType.ALL_ADJUSTMENTS_APPLIED_NOTICE :
                return "allAdjustmentsAppliedNotice";
            case ChannelType.PQS_UPDATE_PRODUCT :
            case ChannelType.UPDATE_PRODUCT :
                return "updateProduct";
            case ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS :
            case ChannelType.UPDATE_PRODUCT_BY_CLASS :
                return "updateProduct";
            case ChannelType.PQS_UPDATE_PRODUCT_CLASS :
            case ChannelType.UPDATE_PRODUCT_CLASS :
                return "updateProductClass";
            case ChannelType.PQS_UPDATE_REPORTING_CLASS :
            case ChannelType.UPDATE_REPORTING_CLASS :
                return "updateReportingClass";
            case ChannelType.RECAP_BY_TYPE :
                return "acceptRecap";
            case ChannelType.RECAP_BY_PRODUCT :
                return "acceptRecap";
            case ChannelType.RECAP_BY_CLASS :
                return "acceptRecap";
            case ChannelType.RECAP_BY_CLASS_SEQ :
                return "acceptRecapForClass";
            case ChannelType.TICKER_BY_TYPE :
                return "acceptTicker";
            case ChannelType.TICKER_BY_PRODUCT :
                return "acceptTicker";
            case ChannelType.TICKER_BY_CLASS :
                return "acceptTicker";
            case ChannelType.LARGE_TRADE_LAST_SALE_BY_CLASS :
            	return "acceptLargeTradeTickerDetailForClass";
            case ChannelType.TICKER_BY_CLASS_SEQ :
                return "acceptTickerForClass";
            case ChannelType.TRADING_SESSION :
                return "acceptTradingSessionState";
            case ChannelType.PQS_STRATEGY_UPDATE :
            case ChannelType.STRATEGY_UPDATE :
                return "updateProductStrategy";
            case ChannelType.USER_SECURITY_TIMEOUT:
                return "acceptUserTimeoutWarning";
            case ChannelType.TEXT_MESSAGE_BY_USER:
                 return "acceptTextMessageForUser";
            case ChannelType.TEXT_MESSAGE_BY_CLASS:
                 return "acceptTextMessageForProductClass";
            case ChannelType.TEXT_MESSAGE_BY_TYPE:
                 return "acceptTextMessageForProductClass";
            case CURRENT_MARKET_AND_NBBO_CHANNEL_TYPE :
                 return "acceptCurrentMarketAndNBBO";
            case ChannelType.NBBO_BY_CLASS:
            case ChannelType.NBBO_BY_PRODUCT:
                 return "acceptNBBO";
            case ChannelType.BUSINESS_DAY:
                return "acceptBusinessDayEvent";

            case ChannelType.CASADMIN_ADD_USER:
                 return "addCASUser";
            case ChannelType.CASADMIN_REMOVE_USER:
                 return "removeCASUser";
            case ChannelType.CASADMIN_ADD_FIRM:
                 return "addCASFirm";
            case ChannelType.CASADMIN_REMOVE_FIRM:
                 return "removeCASFirm";
            case ChannelType.CASADMIN_ADD_RFQ_CLASS_FOR_USER:
                 return "addCASRFQClassForUser";
            case ChannelType.CASADMIN_REMOVE_RFQ_CLASS_FOR_USER:
                 return "removeCASRFQClassForUser";
            case ChannelType.CASADMIN_ADD_CURRENTMARKET_CLASS_FOR_USER:
                 return "addCASCurrentMarketClassForUser";
            case ChannelType.CASADMIN_REMOVE_CURRENTMARKET_CLASS_FOR_USER:
                 return "removeCASCurrentMarketClassForUser";
            case ChannelType.CASADMIN_ADD_OPENINGPRICE_CLASS_FOR_USER:
                 return "addCASOpeningPriceClassForUser";
            case ChannelType.CASADMIN_REMOVE_OPENINGPRICE_CLASS_FOR_USER:
                 return "removeCASOpeningPriceClassForUser";
            case ChannelType.CASADMIN_ADD_TICKER_CLASS_FOR_USER:
                 return "addCASTickerClassForUser";
            case ChannelType.CASADMIN_REMOVE_TICKER_CLASS_FOR_USER:
                 return "removeCASTickerClassForUser";
            case ChannelType.CASADMIN_ADD_RECAP_CLASS_FOR_USER:
                 return "addCASRecapClassForUser";
            case ChannelType.CASADMIN_REMOVE_RECAP_CLASS_FOR_USER:
                 return "removeCASRecapClassForUser";
            case ChannelType.CASADMIN_ADD_BOOKDEPTH_PRODUCT_FOR_USER:
                 return "addCASBookDepthProductForUser";
            case ChannelType.CASADMIN_REMOVE_BOOKDEPTH_PRODUCT_FOR_USER:
                 return "removeCASBookDepthProductForUser";
            case ChannelType.CASADMIN_ADD_QUOTE_LOCKED_NOTIFICATION:
                 return "addCASQuoteLockedNotification";
            case ChannelType.CASADMIN_REMOVE_QUOTE_LOCKED_NOTIFICATION:
                 return "removeCASQuoteLockedNotification";

            case ChannelType.USER_EVENT_ADD_USER:
                    return "acceptSessionProfileUserUpdate";

            case ChannelType.USER_EVENT_DELETE_USER:
                    return "acceptUserDeletion";

            case ChannelType.USER_EVENT_ADD_FIRM:
                    return "acceptFirmUpdate";

            case ChannelType.USER_EVENT_DELETE_FIRM:
                    return "acceptFirmDeletion";

            case ChannelType.USER_EVENT_USER_FIRM_AFFILIATION_UPDATE:
                    return "acceptUserFirmAffiliationUpdate";
            case ChannelType.USER_EVENT_USER_FIRM_AFFILIATION_DELETE:
                    return "acceptUserFirmAffiliationDelete";

            case ChannelType.ALERT_SATISFACTION:
            case ChannelType.ALERT_SATISFACTION_ALL:
                 return "acceptSatisfactionAlert";
            case ChannelType.ALERT:
            case ChannelType.ALERT_ALL:
                 return "acceptAlert";
            case ChannelType.ALERT_UPDATE:
            case ChannelType.ALERT_UPDATE_ALL:
                 return "acceptAlertUpdate";
            case ChannelType.INTERMARKET_ADMIN_MESSAGE_BROADCAST:
                 return "acceptBroadcastIntermarketAdminMessage";
            case ChannelType.INTERMARKET_ADMIN_MESSAGE:
                 return "acceptIntermarketAdminMessage";
            case ChannelType.HELD_ORDER_CANCEL_REPORT:
                 return "acceptHeldOrderCancelReport";
            case ChannelType.HELD_ORDER_FILLED_REPORT:
                 return "acceptHeldOrderFilledReport";
            case ChannelType.HELD_ORDER_STATUS:
                 return "acceptHeldOrderStatus";
            case ChannelType.HELD_ORDERS:
                 return "acceptHeldOrders";
            case ChannelType.NEW_HELD_ORDER:
                 return "acceptNewHeldOrder";
            case ChannelType.CANCEL_HELD_ORDER:
                 return "acceptCancelHeldOrder";
            case ChannelType.FILL_REJECT_REPORT:
                 return "acceptFillRejectReport";
            case ChannelType.NBBO_AGENT_FORCED_OUT:
                 return "acceptForcedTakeOver";
            case ChannelType.NBBO_AGENT_REMINDER:
                 return "acceptReminder";
            case ChannelType.CALENDAR_UPDATE:
                 return "updateCalendarEvent";
            case ChannelType.SERVER_FAILURE:
                 return "acceptServerFailure";
            case ChannelType.USER_ACTIVITY_TIMEOUT:
                 return "acceptUserActivityTimeout";
            case ChannelType.AUCTION:
            case ChannelType.AUCTION_USER:
                return "acceptAuction";
            case ChannelType.DAIM_USER:
                return "acceptDirectedAIMAuction";
            case ChannelType.GROUP_CANCEL:
                return "acceptGroupCancelSummary";
            case ChannelType.OMT_ORDER_ACCEPTED:
			    return "acceptOrders";
            case ChannelType.OMT_ORDER_CANCELED:
			    return "acceptCancels";
            case ChannelType.OMT_ORDER_CANCEL_REPLACED:
			    return "acceptCancelReplaces";
            case ChannelType.OMT_FILL_REPORT_REJECT:
			    return "acceptFillReportReject";
            case ChannelType.OMT_ORDER_REMOVED:
                return "acceptRemoveOrder";
            case ChannelType.OMT_LINKAGE_CANCEL_REPORT:
                return "acceptLinkageCancelReport";
            case ChannelType.OMT_LINKAGE_FILL_REPORT:
                return "acceptLinkageFillReport";
            case ChannelType.OMT_ORDERS_FOR_LOCATION:
                return "acceptOrderLocationServerResponse";
            case ChannelType.OMT_LOCATION_SUMMARY:
                return "acceptOrderLocationSummaryServerResponse";
            case ChannelType.OMT_TRADE_NOTIFICATION:
                return "acceptTradeNotifications";
            case ChannelType.PS_UPDATE_LINKAGE_INDICATOR:
                return "updateLinkageIndicator";
            case ChannelType.OMT_REMOVE_MESSAGE:
                return "acceptRemoveMessage";
            case ChannelType.OMT_FILL_REPORT_DROP_COPY:
                return "acceptFillReportDropCopy";
            case ChannelType.OMT_CANCEL_REPORT_DROP_COPY:
                return "acceptCancelReportDropCopy";
            case ChannelType.OMT_MANUAL_ORDER_TIMEOUT:
                return "acceptManualOrderTimeout";
            case ChannelType.OMT_MANUAL_FILL_TIMEOUT:
                return "acceptManualFillTimeout";
            case ChannelType.PAR_ORDER_ACCEPTED:
                return "acceptManualOrders";
            case ChannelType.PAR_ORDER_CANCELED:
                return "acceptManualCancels";
            case ChannelType.PAR_ORDER_CANCEL_REPLACED:
                return "acceptManualCancelReplaces";
            default :
                if (Log.isDebugOn())
                {
                    Log.debug("EventChannelFilterHelper::Unknown channel type: " + channelKey.channelType);
                }
                return "";
        }
    }// end of getMethodName
}// EOF


