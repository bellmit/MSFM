package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.*;

public class SystemControlConsumerHomeInProcessImpl extends ClientBOHome implements IECSystemControlConsumerHome {
    private SystemControlConsumerIECImpl systemControlConsumer;

    /**
     * SystemControlConsumerHomeInProcessImpl constructor comment.
     */
    public SystemControlConsumerHomeInProcessImpl() {
        super();
    }

    public SystemControlConsumer create() {
        return find();
    }

    /**
     * Return the SystemControlConsumer  (If first time, create it and bind it to the orb).
     *
     * @return SystemControlConsumer
     */
    public SystemControlConsumer find() {
        return systemControlConsumer;
    }

    public void clientStart() {
        systemControlConsumer.create(String.valueOf(systemControlConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(systemControlConsumer);
    }

    public void clientInitialize() {
        systemControlConsumer = new SystemControlConsumerIECImpl();
    }

    /**
     * Adds the event channel listener to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    public void addFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

    }// end of addEventChannelListener

    /**
     * Removes the event channel listener to the internal event channel and the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    public void removeFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

    }// end of removeEventChannelListener

    // Unused methods declared in home interface for server usage.
    public void addConsumer(SystemControlConsumer consumer, ChannelKey key) {}
    public void removeConsumer(SystemControlConsumer consumer, ChannelKey key) {}
    public void removeConsumer(SystemControlConsumer consumer) {}

}// EOF
