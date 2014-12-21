// $Workfile$ com.cboe.consumers.eventChannel.ComponentCallbackConsumerHomeImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Keval Desai
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 - 2002 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.events.ComponentConsumer;
import com.cboe.interfaces.events.IECComponentConsumerHome;
import com.cboe.util.ChannelKey;

    /**
     * <b> Description </b>
     * <p>
     *      The Component Listener class.
     * </p>
     *
     * @author Keval Desai
     */
public class  ComponentCallbackConsumerHomeImpl extends ClientBOHome implements IECComponentConsumerHome
{
    private ComponentEventConsumerInterceptor componentEventConsumerInterceptor;
    private ComponentEventConsumerInterceptor casStatusEventConsumerInterceptor;
   /**
     * ComponentCallbackConsumerHomeImpl constructor comment.
     */
    public  ComponentCallbackConsumerHomeImpl()
    {
        super();
    }

    public ComponentConsumer create() {
        return find();
    }

    public ComponentConsumer createCASStatusListener() {
        return findCASStatusListener();
    }

    /**
     * Return the ComponentConsumer Listener .
     * @return ComponentConsumer
     */
    public ComponentConsumer find() {
        return componentEventConsumerInterceptor;
    }

    public ComponentConsumer findCASStatusListener()
    {
        CASStatusConsumerIECImpl componentConsumerIEC = new CASStatusConsumerIECImpl();
        componentConsumerIEC.create(String.valueOf(componentConsumerIEC.hashCode()));
        addToContainer(componentConsumerIEC);
        ComponentEventConsumerInterceptor componentConsumerInterceptor = new ComponentEventConsumerInterceptor (componentConsumerIEC);
        if(getInstrumentationEnablementProperty())
        {
            componentConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        casStatusEventConsumerInterceptor = componentConsumerInterceptor;
        return casStatusEventConsumerInterceptor;
    }

    public void clientStart() {

    }

    public void clientInitialize() {
        ComponentConsumerIECImpl componentConsumer = new ComponentConsumerIECImpl();
        componentConsumer.create(String.valueOf(componentConsumer.hashCode()));
        addToContainer(componentConsumer);
        componentEventConsumerInterceptor = new ComponentEventConsumerInterceptor(componentConsumer);
        if(getInstrumentationEnablementProperty())
        {
            componentEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {}

    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {}

    // Unused methods declared in home interface for server usage.
    public void addConsumer(ComponentConsumer consumer, ChannelKey key) {}
    public void removeConsumer(ComponentConsumer consumer, ChannelKey key) {}
    public void removeConsumer(ComponentConsumer consumer) {}

}// EOF
