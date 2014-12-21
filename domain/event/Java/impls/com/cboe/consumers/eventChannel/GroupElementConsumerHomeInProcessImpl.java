package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.*;

public class GroupElementConsumerHomeInProcessImpl extends ClientBOHome implements IECGroupElementConsumerHome {
    private GroupElementConsumerIECImpl groupElementConsumer;

    /**
     * GroupElementConsumerHomeEventImpl constructor comment.
     */
    public GroupElementConsumerHomeInProcessImpl() {
        super();
    }

    public GroupElementConsumer create() {
        return find();
    }

    /**
     * Return the GroupElementConsumer  (If first time, create it and bind it to the orb).
     * 
     * @return GroupElementConsumer
     */
    public GroupElementConsumer find() {
        return groupElementConsumer;
    }

    public void clientStart() {
        groupElementConsumer.create(String.valueOf(groupElementConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(groupElementConsumer);
    }

    public void clientInitialize() {
        groupElementConsumer = new GroupElementConsumerIECImpl();
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
    public void addConsumer(GroupElementConsumer consumer, ChannelKey key) {}
    public void removeConsumer(GroupElementConsumer consumer, ChannelKey key) {}
    public void removeConsumer(GroupElementConsumer consumer) {}

}// EOF
