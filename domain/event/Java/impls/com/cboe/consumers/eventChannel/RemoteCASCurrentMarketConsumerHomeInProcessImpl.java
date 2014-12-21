package com.cboe.consumers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.interfaces.events.IECRemoteCASCurrentMarketConsumerHome;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.events.RemoteCASCurrentMarketConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASCurrentMarketConsumerHomeInProcessImpl extends ClientBOHome implements IECRemoteCASCurrentMarketConsumerHome
{

    private RemoteCASCurrentMarketConsumerIECImpl iecConsumer;

    public RemoteCASCurrentMarketConsumer create()
    {
        return find();
    }

    public RemoteCASCurrentMarketConsumer find()
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
        iecConsumer = new RemoteCASCurrentMarketConsumerIECImpl();
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