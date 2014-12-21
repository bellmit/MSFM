package com.cboe.consumers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.interfaces.events.IECRemoteCASSessionManagerConsumerHome;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.events.RemoteCASSessionManagerConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASSessionManagerConsumerHomeInProcessImpl extends ClientBOHome implements IECRemoteCASSessionManagerConsumerHome
{

    private RemoteCASSessionManagerConsumerIECImpl iecConsumer;

    public RemoteCASSessionManagerConsumer create()
    {
        return find();
    }

    public RemoteCASSessionManagerConsumer find()
    {
        return iecConsumer;
    }

    public void clientStart()
        throws Exception
    {
        iecConsumer.create(String.valueOf(iecConsumer.hashCode()));
        addToContainer(iecConsumer);
    }

    public void clientInitialize()
    {
        iecConsumer = new RemoteCASSessionManagerConsumerIECImpl();
    }

    public void addFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    public void removeFilter (ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

}