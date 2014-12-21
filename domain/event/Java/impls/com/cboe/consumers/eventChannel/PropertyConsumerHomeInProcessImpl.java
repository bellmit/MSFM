package com.cboe.consumers.eventChannel;

import com.cboe.util.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.*;
import com.cboe.interfaces.events.IECPropertyConsumerHome;
import com.cboe.interfaces.events.PropertyConsumer;

public class PropertyConsumerHomeInProcessImpl extends ClientBOHome implements IECPropertyConsumerHome {
    private PropertyConsumerIECImpl propertyConsumer;

    /**
     * PropertyConsumerHomeEventImpl constructor comment.
     */
    public PropertyConsumerHomeInProcessImpl() {
        super();
    }

    public PropertyConsumer create() {
        return find();
    }

    /**
     * Return the PropertyConsumer  (If first time, create it and bind it to the orb).
     * @return PropertyConsumer
     */
    public PropertyConsumer find() {
        return propertyConsumer;
    }

    public void clientStart() {
        propertyConsumer.create(String.valueOf(propertyConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(propertyConsumer);
    }

    public void clientInitialize() {
        propertyConsumer = new PropertyConsumerIECImpl();
    }

    /**
     * Adds the event channel listener to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    public void addFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

    }// end of addEventChannelListener

    /**
     * Removes the event channel listener to the internal event channel and the CBOE event channel.
     * @param channelKey the event channel key

     */
    public void removeFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

    }// end of removeEventChannelListener

    // Unused methods declared in home interface for server usage.
    public void addConsumer(PropertyConsumer consumer, ChannelKey key) {}
    public void removeConsumer(PropertyConsumer consumer, ChannelKey key) {}
    public void removeConsumer(PropertyConsumer consumer) {}

}// EOF
