package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.events.IECRemoteCASNBBOConsumerHome;
import com.cboe.interfaces.events.RemoteCASNBBOConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASNBBOConsumerHomeInProcessImpl extends ClientBOHome implements IECRemoteCASNBBOConsumerHome
{

    private RemoteCASNBBOConsumerIECImpl iecConsumer;

    public RemoteCASNBBOConsumer create()
    {
        return find();
    }

    public RemoteCASNBBOConsumer find()
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
        iecConsumer = new RemoteCASNBBOConsumerIECImpl();
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