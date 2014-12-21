package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.events.IECRemoteCASRecapConsumerHome;
import com.cboe.interfaces.events.RemoteCASRecapConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASRecapConsumerHomeInProcessImpl extends ClientBOHome implements IECRemoteCASRecapConsumerHome
{

    private RemoteCASRecapConsumerIECImpl iecConsumer;

    public RemoteCASRecapConsumer create()
    {
        return find();
    }

    public RemoteCASRecapConsumer find()
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
        iecConsumer = new RemoteCASRecapConsumerIECImpl();
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