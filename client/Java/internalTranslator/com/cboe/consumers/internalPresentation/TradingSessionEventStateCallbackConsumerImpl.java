//
// -----------------------------------------------------------------------------------
// Source file: TradingSessionEventStateCallbackConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.internalPresentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.internalPresentation;

import com.cboe.idl.session.TradingSessionEventHistoryStruct;
import com.cboe.idl.session.TradingSessionEventHistoryStructV2;

import com.cboe.interfaces.callback.TradingSessionEventStateCallbackConsumer;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

/**
 * This is the implementation of the TradingSessionEventStateCallbackConsumer callback object which
 * receives event state data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 */
public class TradingSessionEventStateCallbackConsumerImpl implements TradingSessionEventStateCallbackConsumer
{
    private EventChannelAdapter eventChannel;

    /**
     * @param eventChannel the event channel to publish to.
     */
    public TradingSessionEventStateCallbackConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
    }

    public void acceptTradingSessionEventState(TradingSessionEventHistoryStruct eventHistory)
    {
        /* Not used anymore

        ChannelKey key = new ChannelKey(ChannelType.CB_TRADING_SESSION_EVENT_STATE, new Integer(0));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, eventHistory);
        eventChannel.dispatch(event);
        */
    }

    public void acceptTradingSessionEventStateV2(TradingSessionEventHistoryStructV2 eventHistory, boolean allServersIncluded)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.TRADING_SESSION))
        {
            Object[] args = new Object[2];
            args[0] = eventHistory;
            args[1] = new Boolean(allServersIncluded);

            GUILoggerHome.find().debug(this.getClass().getName() + ":acceptTradingSessionEventStateV2",
                                       GUILoggerSABusinessProperty.TRADING_SESSION,
                                       args);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_TRADING_SESSION_EVENT_STATE, new Integer(0));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, eventHistory);
        eventChannel.dispatch(event);
    }
}