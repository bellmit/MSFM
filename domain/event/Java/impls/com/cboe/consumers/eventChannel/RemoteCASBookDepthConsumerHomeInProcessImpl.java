package com.cboe.consumers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.interfaces.events.IECRemoteCASBookDepthConsumerHome;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.events.RemoteCASBookDepthConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASBookDepthConsumerHomeInProcessImpl extends ClientBOHome implements IECRemoteCASBookDepthConsumerHome
{

    private RemoteCASBookDepthConsumerIECImpl iecConsumer;

    public RemoteCASBookDepthConsumer create()
    {
        return find();
    }

    public RemoteCASBookDepthConsumer find()
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
        iecConsumer = new RemoteCASBookDepthConsumerIECImpl();
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