//
// -----------------------------------------------------------------------------------
// Source file: StrategyStatusConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.idl.cmiSession.SessionStrategyStruct;

import com.cboe.interfaces.callback.StrategyStatusConsumer;
import com.cboe.interfaces.presentation.product.SessionStrategy;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.SessionProductFactory;

import com.cboe.domain.util.SessionKeyContainer;

/**
 * This is the implementation of the StrategyStatusConsumer callback object which
 * receives strategy status data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 */
public class StrategyStatusConsumerImpl implements StrategyStatusConsumer
{
    private EventChannelAdapter eventChannel = null;

    /**
     * @param eventChannel the event channel to publish to.
     */
    public StrategyStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
    }

    /**
     * @param updatedStrategies the product state data to publish to all subscribed listeners
     */
    public void updateProductStrategy(SessionStrategyStruct[] updatedStrategies)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            Object[] args = new Object[1];
            args[0] = updatedStrategies;

            GUILoggerHome.find().debug(this.getClass().getName() + ":updateProductStrategy",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY,
                                       args);
        }

        ChannelKey key;
        ChannelEvent event;

        for (int i = 0; i < updatedStrategies.length; i++)
        {
            SessionStrategy sessionStrategy = SessionProductFactory.create(updatedStrategies[i]);
            key = new ChannelKey(ChannelType.CB_STRATEGY_UPDATE, new SessionKeyContainer(updatedStrategies[i].sessionProductStruct.sessionName, updatedStrategies[i].sessionProductStruct.productStruct.productKeys.classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, sessionStrategy);
            eventChannel.dispatch(event);

            // dispatch to session cache listener
            key = new ChannelKey(ChannelType.CB_STRATEGY_UPDATE, updatedStrategies[i].sessionProductStruct.sessionName);
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, sessionStrategy);
            eventChannel.dispatch(event);
        }
    }
}
