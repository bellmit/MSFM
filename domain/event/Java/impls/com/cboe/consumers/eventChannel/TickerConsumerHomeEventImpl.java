// $Workfile$ com.cboe.consumers.eventChannel.TickerConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import java.util.ArrayList;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECTickerConsumerHome;
import com.cboe.interfaces.events.TickerConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

    /**
     * <b> Description </b>
     * <p>
     *      The Ticker Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     */
public class TickerConsumerHomeEventImpl extends ClientBOHome implements IECTickerConsumerHome {
    private TickerEventConsumerInterceptor tickerEventConsumerInterceptor;
    private TickerEventConsumerImpl tickerEvent;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "Ticker";
    /**
     * TickerListenerFactory constructor comment.
     */
    public TickerConsumerHomeEventImpl() {
        super();
    }

    public TickerConsumer create() {
        return find();
    }

    /**
     * Return the UnderlyngTicker Listener (If first time, create it and bind it to the orb).
     * @author Jeff Illian
     * @return TickerListener
     */
    public TickerConsumer find() {
        return tickerEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {
        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.events.TickerEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, tickerEvent );
    }

    public void clientInitialize()
        throws Exception
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        TickerConsumerIECImpl tickerConsumer = new TickerConsumerIECImpl();
        tickerConsumer.create(String.valueOf(tickerConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(tickerConsumer);

        //The addToContainer call MUST occur prior to creation of the interceptor.
        //      manageObject(orderStatusConsumer);
        tickerEventConsumerInterceptor = new TickerEventConsumerInterceptor(tickerConsumer);
        if( getInstrumentationEnablementProperty())
        {
            tickerEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        tickerEvent = new TickerEventConsumerImpl(tickerEventConsumerInterceptor);
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
        addConstraint(channelKey);
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
            eventChannelFilterHelper.addEventFilter( tickerEvent, channelKey,
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
        removeConstraint(channelKey);
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
        StringBuilder name = new StringBuilder(80);
        switch (channelKey.channelType)
        {
            // doubled ticker is correct - internal ticker contains CMI ticker
            case ChannelType.TICKER_BY_PRODUCT :
                name.append("acceptTicker.ticker.ticker.productKeys.productKey == ").append(channelKey.key);
                return name.toString();
            case ChannelType.TICKER_BY_CLASS :
                name.append("acceptTicker.ticker.ticker.productKeys.classKey == ").append(channelKey.key);
                return name.toString();
            case ChannelType.TICKER_BY_TYPE :
                name.append("acceptTicker.ticker.ticker.productKeys.productType == ").append(channelKey.key);
                return name.toString();
            case ChannelType.TICKER_BY_CLASS_SEQ :
                name.append("acceptTickerForClass.routingParameters.classKey == ").append(channelKey.key);
                return name.toString();
            case ChannelType.LARGE_TRADE_LAST_SALE_BY_CLASS :
                name.append("acceptLargeTradeTickerDetailForClass.routingParameters.classKey == ").append(channelKey.key);
                return name.toString();
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    } // end of getParmName

    // Unused methods declared in home interface for server usage.
    public void addConsumer(TickerConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TickerConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TickerConsumer consumer) {}
    public ArrayList getInvalidSalePrefixesForLargeTrade() {return new ArrayList() ;}
    public ArrayList getValidSessionsForLargeTrade() {return new ArrayList();}

}// EOF
