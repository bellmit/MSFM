//
// -----------------------------------------------------------------------------------
// Source file: ClassStatusConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.interfaces.callback.ClassStatusConsumer;

import com.cboe.idl.cmiSession.ClassStateStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.domain.util.SessionKeyContainer;

import com.cboe.interfaces.presentation.product.SessionProductClass;

import com.cboe.presentation.product.SessionProductClassFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 * This is the implementation of the ClassStatusConsumer callback object which
 * receives class status data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 */
public class ClassStatusConsumerImpl implements ClassStatusConsumer
{
    private EventChannelAdapter eventChannel = null;

    /**
     * @param eventChannel the event channel to publish to.
     */
    public ClassStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
    }

    /**
     * The callback method used by the CAS to publish class state information.
     * @param updatedClass the class struct containing updated product class
     *        state information to publish to all subscribed listeners.
     */
    public void updateProductClass(SessionClassStruct updatedClass)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            Object[] args = new Object[1];
            args[0] = updatedClass;

            GUILoggerHome.find().debug(this.getClass().getName() + ":updateProductClass",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY,
                                       args);
        }

        SessionProductClass sessionClass = SessionProductClassFactory.create(updatedClass);

        ChannelKey key = new ChannelKey(ChannelType.CB_PRODUCT_CLASS_UPDATE, new SessionKeyContainer(updatedClass.sessionName, updatedClass.classStruct.classKey));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, sessionClass);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_CLASS_UPDATE_BY_TYPE, new SessionKeyContainer(updatedClass.sessionName, updatedClass.classStruct.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, sessionClass);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.CB_CLASS_UPDATE_BY_TYPE, updatedClass.sessionName);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, sessionClass);
        eventChannel.dispatch(event);
    }

    /**
     * The callback method used by the CAS to publish class state information.
     * @param classState a sequence of product state structs containing updated
     *        state information to publish to all subscribed listeners.
     */
    public void acceptClassState(ClassStateStruct[] classState)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            Object[] args = new Object[1];
            args[0] = classState;

            GUILoggerHome.find().debug(this.getClass().getName() + ":acceptClassState",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY,
                                       args);
        }

        for ( int i = 0; i < classState.length; i++ )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_CLASS_STATE, new SessionKeyContainer(classState[i].sessionName, classState[i].classKey));
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, classState[i]);
            eventChannel.dispatch(event);

            key = new ChannelKey(ChannelType.CB_CLASS_STATE_BY_TYPE, new SessionKeyContainer(classState[i].sessionName, classState[i].classKey));
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, classState[i]);
            eventChannel.dispatch(event);

            key = new ChannelKey(ChannelType.CB_CLASS_STATE_BY_TYPE, classState[i].sessionName);
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, classState[i]);
            eventChannel.dispatch(event);
        }
    }
}
