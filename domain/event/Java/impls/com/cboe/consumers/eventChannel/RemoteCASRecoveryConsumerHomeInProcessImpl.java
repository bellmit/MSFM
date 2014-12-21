package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.events.IECRemoteCASRecoveryConsumerHome;
import com.cboe.interfaces.events.RemoteCASRecoveryConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASRecoveryConsumerHomeInProcessImpl extends ClientBOHome implements IECRemoteCASRecoveryConsumerHome
{

    private RemoteCASRecoveryConsumerIECImpl iecConsumer;

    public RemoteCASRecoveryConsumer create()
    {
        return find();
    }

    public RemoteCASRecoveryConsumer find()
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
        iecConsumer = new RemoteCASRecoveryConsumerIECImpl();
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