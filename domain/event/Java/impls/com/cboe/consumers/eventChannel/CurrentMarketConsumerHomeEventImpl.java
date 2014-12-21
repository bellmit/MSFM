// $Workfile$ com.cboe.consumers.eventChannel.CurrentMarketConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Tom Lynch
*   Revision                                    Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.CurrentMarketConsumer;
import com.cboe.interfaces.events.IECCurrentMarketConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import java.util.HashMap;
import java.util.Map;

    /**
     * <b> Description </b>
     * <p>
     *      The Current Market Listener class.
     * </p>
     *
     * @author Tom Lynch
     * @author Jeff Illian
     * @author Keval Desai
     */

public class CurrentMarketConsumerHomeEventImpl extends ClientBOHome implements IECCurrentMarketConsumerHome {
    private CurrentMarketEventConsumerInterceptor currentMarketEventConsumerInterceptor;
    private CurrentMarketEventConsumerImpl currentMarketEvent;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private Map<ChannelKey, Integer> filters;
    private boolean useCurrentMarket;
    private final String CHANNEL_NAME = "CurrentMarket";

    /**
     * CurrentMarketConsumerHomeEventImpl constructor comment.
     */
    public CurrentMarketConsumerHomeEventImpl() {
        super();
    }

    public CurrentMarketConsumer create() {
        return find();
    }
    /**
     * Return the Current Market Listener (If first time, create it and bind it to the orb).
     * @author Tom Lynch
     * @author Jeff Illian
     * @return CurrentMarketConsumer
     */
    public CurrentMarketConsumer find() {
        return currentMarketEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {

        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.events.CurrentMarketEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, currentMarketEvent );
    }

    public void clientInitialize()
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        CurrentMarketConsumerIECImpl currentMarketConsumer = new CurrentMarketConsumerIECImpl();
        currentMarketConsumer.create(String.valueOf(currentMarketConsumer.hashCode()));
        // Every BObject must be added to the container.
        addToContainer(currentMarketConsumer);
        currentMarketEventConsumerInterceptor = new CurrentMarketEventConsumerInterceptor(currentMarketConsumer);
        if(getInstrumentationEnablementProperty())
        {
            currentMarketEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        currentMarketEvent = new CurrentMarketEventConsumerImpl(currentMarketEventConsumerInterceptor);
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
        Log.information(this, "Setting activateSubscription:" + turnOn);
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

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
     */
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

        if ( channelKey.channelType == ChannelType.CURRENT_MARKET_BY_PRODUCT
                || channelKey.channelType == ChannelType.CURRENT_MARKET_BY_CLASS
                || channelKey.channelType ==  ChannelType.CURRENT_MARKET_BY_TYPE
                || channelKey.channelType == ChannelType.NBBO_BY_PRODUCT
                || channelKey.channelType == ChannelType.NBBO_BY_CLASS)
        {
            ChannelKey marketAndNBBOChannelKey =
                    new ChannelKey(EventChannelFilterHelper.CURRENT_MARKET_AND_NBBO_CHANNEL_TYPE, channelKey.key);
            addConstraint(marketAndNBBOChannelKey);
        }
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
            eventChannelFilterHelper.addEventFilter( currentMarketEvent, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }// end of addConstraint

    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
     */
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

        ChannelKey marketAndNBBOChannelKey =
                new ChannelKey(EventChannelFilterHelper.CURRENT_MARKET_AND_NBBO_CHANNEL_TYPE, channelKey.key);
        removeConstraint(marketAndNBBOChannelKey);
    }

    /**
     * Removes constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null ) {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }// end of addConstraint

        /**
         * Returns the constraint string based on the channel key
         *
         * @param channelKey the event channel key
         *
         * @author Connie Feng
         */
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

    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getParmName(ChannelKey channelKey)
    {
        StringBuilder name = new StringBuilder(100);
        switch (channelKey.channelType)
        {
            case ChannelType.CURRENT_MARKET_BY_PRODUCT :
                name.append("acceptCurrentMarket.contingentMarket.productKeys.productKey == ").append(channelKey.key);
                return name.toString();
            case ChannelType.CURRENT_MARKET_BY_CLASS :
                name.append("acceptCurrentMarket.contingentMarket.productKeys.classKey == ").append(channelKey.key);
                return name.toString();
            case ChannelType.CURRENT_MARKET_BY_TYPE :
                name.append("acceptCurrentMarket.contingentMarket.productKeys.productType == ").append(channelKey.key);
                return name.toString();
            case ChannelType.OPENING_PRICE_BY_PRODUCT :
                name.append("acceptExpectedOpeningPrice.expectedOpeningPrice.productKeys.productKey == ").append(channelKey.key);
                return name.toString();
            case ChannelType.OPENING_PRICE_BY_CLASS :
                name.append("acceptExpectedOpeningPrice.expectedOpeningPrice.productKeys.classKey == ").append(channelKey.key);
                return name.toString();
            case ChannelType.OPENING_PRICE_BY_TYPE:
                name.append("acceptExpectedOpeningPrice.expectedOpeningPrice.productKeys.productType == ").append(channelKey.key);
                return name.toString();
            case ChannelType.NBBO_BY_PRODUCT :
                name.append("acceptNBBO.nbboStruct.productKeys.productKey == ").append(channelKey.key);
                return name.toString();
            case ChannelType.NBBO_BY_CLASS :
                name.append("acceptNBBO.nbboStruct.productKeys.classKey == ").append(channelKey.key);
                return name.toString();
            case EventChannelFilterHelper.CURRENT_MARKET_AND_NBBO_CHANNEL_TYPE :
                name.append("acceptCurrentMarketAndNBBO.contingentMarket.productKeys.classKey == ").append(channelKey.key);
                return name.toString();
            case ChannelType.CURRENT_MARKET_BY_CLASS_SEQ :
                name.append("acceptCurrentMarketsForClass.routingParameters.classKey == ").append(channelKey.key);
                return name.toString();
            case ChannelType.OPENING_PRICE_BY_CLASS_SEQ :
                name.append("acceptExpectedOpeningPricesForClass.routingParameters.classKey == ").append(channelKey.key);
                return name.toString();
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    }
    // Unused methods declared in home interface for server usage.
    public void addConsumer(CurrentMarketConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CurrentMarketConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CurrentMarketConsumer consumer) {}
}// EOF
