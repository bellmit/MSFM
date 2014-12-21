package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.events.IECRemoteCASCallbackRemovalConsumerHome;
import com.cboe.interfaces.events.RemoteCASCallbackRemovalConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASCallbackRemovalConsumerHomeInProcessImpl extends ClientBOHome implements IECRemoteCASCallbackRemovalConsumerHome
{

    private RemoteCASCallbackRemovalConsumerIECImpl iecConsumer;

    public RemoteCASCallbackRemovalConsumer create()
    {
        return find();
    }

    public RemoteCASCallbackRemovalConsumer find()
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
        iecConsumer = new RemoteCASCallbackRemovalConsumerIECImpl();
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