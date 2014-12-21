package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.ExpectedOpeningPriceConsumer;
import com.cboe.interfaces.events.IECExpectedOpeningPriceConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import java.util.HashMap;
import java.util.Map;


public class ExpectedOpeningPriceConsumerHomeEventImpl
    extends ClientBOHome 
    implements IECExpectedOpeningPriceConsumerHome {
    private ExpectedOpeningPriceEventConsumerInterceptor eopEventConsumerInterceptor;
    private ExpectedOpeningPriceEventConsumerImpl eopEvent;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private Map<ChannelKey, Integer> filters;
    private boolean useCurrentMarket;
    private final String CHANNEL_NAME = "ExpectedOpeningPrice";

    /**
     * CurrentMarketConsumerHomeEventImpl constructor comment.
     */
    public ExpectedOpeningPriceConsumerHomeEventImpl() {
        super();
    }

    public ExpectedOpeningPriceConsumer create() {
        return find();
    }
    /**
     * Return the Current Market Listener (If first time, create it and bind it to the orb).
     * @author Tom Lynch
     * @author Jeff Illian
     * @return CurrentMarketConsumer
     */
    public ExpectedOpeningPriceConsumer find() {
        return eopEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {

        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.events.ExpectedOpeningPriceEventConsumerHelper.id();
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, eopEvent );
    }

    public void clientInitialize()
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        ExpectedOpeningPriceConsumerIECImpl eopConsumer = new ExpectedOpeningPriceConsumerIECImpl();
        eopConsumer.create(String.valueOf(eopConsumer.hashCode()));
        // Every BObject must be added to the container.
        addToContainer(eopConsumer);
        eopEventConsumerInterceptor = new ExpectedOpeningPriceEventConsumerInterceptor(eopConsumer);
        if(getInstrumentationEnablementProperty())
        {
            eopEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        eopEvent = new ExpectedOpeningPriceEventConsumerImpl(eopEventConsumerInterceptor);
        filters = new HashMap<ChannelKey, Integer>();
    }

    /** Use (or don't use) messages from CurrentMarket channel.
     * When we don't use messages, we remember the subscriptions anyway in case
     * we get another call telling us to startig using the messages. Called by
     * MarketBufferConsumerHomeEventImpl, which coordinates using CurrentMarket
     * vs. MarketBuffer channels.
     * @param turnOn true to use CurrentMarket messages, false to not use them.
     */
    public void activateSubscription(boolean turnOn)
    {
        StringBuilder setting = new StringBuilder(35);
        setting.append("Setting activateSubscription:").append(turnOn);
        Log.information(this, setting.toString());
        if (filters == null)
        {
            // During clientInitialize, MarketBuffer was initialized before we were.
            useCurrentMarket = turnOn;
            return;
        }

        if (turnOn == useCurrentMarket)
        {
            // Nothing to change, no work to do.
            return;
        }

        String action = turnOn ? "add" : "remove";
        synchronized (filters)
        {
            for (ChannelKey key : filters.keySet())
            {
                for (int subscribers = filters.get(key); subscribers > 0; --subscribers)
                {
                    try
                    {
                        if (turnOn)
                        {
                            applyFilter(key);
                        }
                        else
                        {
                            unapplyFilter(key);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.exception(this, "Could not " + action + " filter", e);
                    }
                }
            }
            // Now, while everyone else is locked out, update useCurrentMarket
            useCurrentMarket = turnOn;
        }
    }

    /** Add subscription to active list.
     * @param channelKey Filter to remember.
     */
    private void rememberFilter(ChannelKey channelKey)
    {
        synchronized (filters)
        {
            if (!filters.containsKey(channelKey))
            {
                filters.put(channelKey, 1);
            }
            else
            {
                filters.put(channelKey, 1 + filters.get(channelKey));
            }
        }
    }

    /** Remove subscription from active list.
     * @param channelKey Filter to forget.
     */
    private void forgetFilter(ChannelKey channelKey)
    {
        synchronized (filters)
        {
            if (filters.containsKey(channelKey))
            {
                int subscribers = filters.get(channelKey);
                if (subscribers <= 1)
                {
                    filters.remove(channelKey);
                }
                else
                {
                    filters.put(channelKey, subscribers-1);
                }
            }
        }
    }
    
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        rememberFilter(channelKey);
        if (useCurrentMarket)
        {
            applyFilter(channelKey);
        }
    }

    // Internal usage, for addFilter and activateSubscription
    private void applyFilter(ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        addConstraint( channelKey );
    }

    /**
     * Adds constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.addEventFilter( eopEvent, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }// end of addConstraint

    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        forgetFilter(channelKey);
        if (useCurrentMarket)
        {
            unapplyFilter(channelKey);
        }
    }

    // Internal usage, for removeFilter and activateSubscription
    private void unapplyFilter(ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        removeConstraint(channelKey);
    }
    
    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null ) {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }

     
    protected String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if(parm.equals(EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT) ||
           parm.equals(EventChannelFilterHelper.NO_EVENTS_CONSTRAINT))
        {
            return parm;
        }

        StringBuilder buf = new StringBuilder(parm.length()+2);
        buf.append("$.").append(parm);
        return buf.toString();
    }

    protected String getParmName(ChannelKey channelKey)
    {
        String key = channelKey.key.toString();
        StringBuilder name = new StringBuilder(key.length()+80);
        switch (channelKey.channelType)
        {
            case ChannelType.OPENING_PRICE_BY_PRODUCT :
                name.append("acceptExpectedOpeningPrice.expectedOpeningPrice.productKeys.productKey == ").append(key);
                return name.toString();
            case ChannelType.OPENING_PRICE_BY_CLASS :
            case ChannelType.OPENING_PRICE_BY_CLASS_FOR_MDX :
                name.append("acceptExpectedOpeningPrice.expectedOpeningPrice.productKeys.classKey == ").append(key);
                return name.toString();
            case ChannelType.OPENING_PRICE_BY_TYPE:
                name.append("acceptExpectedOpeningPrice.expectedOpeningPrice.productKeys.productType == ").append(key);
                return name.toString();
            case ChannelType.OPENING_PRICE_BY_CLASS_SEQ :
            case ChannelType.OPENING_PRICE_BY_CLASS_SEQ_FOR_MDX :
                name.append("acceptExpectedOpeningPricesForClass.routingParameters.classKey == ").append(key);
                return name.toString();
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    }
    
    // Unused methods declared in home interface for server usage.
    public void addConsumer(ExpectedOpeningPriceConsumer consumer, ChannelKey key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    public void removeConsumer(ExpectedOpeningPriceConsumer consumer, ChannelKey key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    public void removeConsumer(ExpectedOpeningPriceConsumer consumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

}
