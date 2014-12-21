package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.events.IECRemoteCASTickerConsumerHome;
import com.cboe.interfaces.events.RemoteCASTickerConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASTickerConsumerHomeInProcessImpl extends ClientBOHome implements IECRemoteCASTickerConsumerHome
{

    private RemoteCASTickerConsumerIECImpl iecConsumer;

    public RemoteCASTickerConsumer create()
    {
        return find();
    }

    public RemoteCASTickerConsumer find()
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
        iecConsumer = new RemoteCASTickerConsumerIECImpl();
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