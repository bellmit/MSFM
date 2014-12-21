package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.exceptions.*;
import com.cboe.domain.startup.ClientBOHome;

public class AlertConsumerHomeInProcessImpl extends ClientBOHome implements IECAlertConsumerHome {
    private AlertConsumerIECImpl alertConsumer;

   /**
     * AlertConsumerHomeEventImpl constructor comment.
     */
    public AlertConsumerHomeInProcessImpl() {
        super();
    }

    public AlertConsumer create() {
        return find();
    }

    public AlertConsumer find() {
        return alertConsumer;
    }

    public void clientStart()
        throws Exception
    {
        alertConsumer.create(String.valueOf(alertConsumer.hashCode()));
        addToContainer(alertConsumer);
    }

    public void clientInitialize() {
        alertConsumer = new AlertConsumerIECImpl();
    }

    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }
    // Unused methods declared in home interface for server usage.
    public void addConsumer(AlertConsumer consumer, ChannelKey key) {}
    public void removeConsumer(AlertConsumer consumer, ChannelKey key) {}
    public void removeConsumer(AlertConsumer consumer) {}

}
