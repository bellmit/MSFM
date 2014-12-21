//
// -----------------------------------------------------------------------------------
// Source file: TradingSessionStatusConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiSession.TradingSessionStateStruct;

import com.cboe.interfaces.callback.TradingSessionStatusConsumer;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

public class TradingSessionStatusConsumerImpl implements TradingSessionStatusConsumer
{
    private EventChannelAdapter eventChannel = null;

    /**
     * @param eventChannel the event channel to publish on.
     */
    public TradingSessionStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
    }

    /**
     * The callback method
     * @param sessionState containing published data
     */

    public void acceptTradingSessionState(TradingSessionStateStruct sessionState)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.TRADING_SESSION))
        {
            Object[] args = new Object[1];
            args[0] = sessionState;

            GUILoggerHome.find().debug(this.getClass().getName() + ":acceptTradingSessionState",
                                       GUILoggerBusinessProperty.TRADING_SESSION,
                                       args);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_TRADING_SESSION_STATE, new Integer(0));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, sessionState);
        eventChannel.dispatch(event);
    }
}
