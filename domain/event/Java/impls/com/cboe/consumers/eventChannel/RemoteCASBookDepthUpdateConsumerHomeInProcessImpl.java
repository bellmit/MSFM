package com.cboe.consumers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.interfaces.events.IECRemoteCASBookDepthUpdateConsumerHome;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.events.RemoteCASBookDepthUpdateConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASBookDepthUpdateConsumerHomeInProcessImpl extends ClientBOHome implements IECRemoteCASBookDepthUpdateConsumerHome
{

    private RemoteCASBookDepthUpdateConsumerIECImpl iecConsumer;

    public RemoteCASBookDepthUpdateConsumer create()
    {
        return find();
    }

    public RemoteCASBookDepthUpdateConsumer find()
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
        iecConsumer = new RemoteCASBookDepthUpdateConsumerIECImpl();
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