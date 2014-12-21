// $Workfile$ com.cboe.consumers.eventChannel.TradingSessionConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Jeff Illian
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECTradingSessionConsumerHome;
import com.cboe.interfaces.events.TradingSessionConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

    /**
     * <b> Description </b>
     * <p>
     *      The Trading Session Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     */
public class TradingSessionConsumerHomeEventImpl extends ClientBOHome implements IECTradingSessionConsumerHome {
    private TradingSessionEventConsumerInterceptor tradingSessionEventConsumerInterceptor;
    private TradingSessionEventConsumerImpl tradingSessionEvent;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "TradingSession";
    /**
     * TradingSessionConsumerHomeEventImpl constructor comment.
     */
    public TradingSessionConsumerHomeEventImpl() {
        super();
    }

    public TradingSessionConsumer create() {
        return find();
    }

    /**
     * Return the TradingSessionConsumer  (If first time, create it and bind it to the orb).
     * @author Connie Feng
     * @return TradingSessionConsumer
     */
    public TradingSessionConsumer find() {
        return tradingSessionEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {
        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.events.TradingSessionEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, tradingSessionEvent );
    }

    public void clientInitialize()
        throws Exception
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        TradingSessionConsumerIECImpl tradingSessionConsumer = new TradingSessionConsumerIECImpl();
        tradingSessionConsumer.create(String.valueOf(tradingSessionConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(tradingSessionConsumer);

        tradingSessionEventConsumerInterceptor = new TradingSessionEventConsumerInterceptor(tradingSessionConsumer);
        if(getInstrumentationEnablementProperty())
        {
            tradingSessionEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        tradingSessionEvent = new TradingSessionEventConsumerImpl(tradingSessionEventConsumerInterceptor);
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
        SessionKeyContainer key = (SessionKeyContainer)channelKey.key;
        Integer keyInt = key.getKey();

        channelKey = new ChannelKey(channelKey.channelType, keyInt);

        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.addEventFilter( tradingSessionEvent, channelKey,
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
        SessionKeyContainer key = (SessionKeyContainer)channelKey.key;
        Integer keyInt = key.getKey();

        channelKey = new ChannelKey(channelKey.channelType, keyInt);

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
    }// end of getConstraintString

    /**
     * Returns the constraint parameter string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getParmName(ChannelKey channelKey)
    {
        switch (channelKey.channelType)
        {
            case ChannelType.TRADING_SESSION :
            case ChannelType.SET_CLASS_STATE :
            case ChannelType.UPDATE_PRODUCT_CLASS :
            case ChannelType.BUSINESS_DAY :
            case ChannelType.STRATEGY_UPDATE :
                  return EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT;
            case ChannelType.SET_PRODUCT_STATE :
                return new StringBuffer(40).append("setProductStates.classKey==")
                          .append(channelKey.key).toString();
            case ChannelType.UPDATE_PRODUCT :
            case ChannelType.UPDATE_PRODUCT_BY_CLASS :
                return new StringBuffer(80).append("updateProduct.updatedProduct.productStruct.productKeys.classKey==")
                          .append(channelKey.key).toString();
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    } // end of getParmName

    // Unused methods declared in home interface for server usage.
    public void addConsumer(TradingSessionConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TradingSessionConsumer consumer, ChannelKey key) {}
    public void removeConsumer(TradingSessionConsumer consumer) {}
}// EOF
