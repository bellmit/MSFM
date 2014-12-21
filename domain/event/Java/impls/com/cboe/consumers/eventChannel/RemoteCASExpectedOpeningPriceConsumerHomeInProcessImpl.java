package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.events.IECRemoteCASExpectedOpeningPriceConsumerHome;
import com.cboe.interfaces.events.RemoteCASExpectedOpeningPriceConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASExpectedOpeningPriceConsumerHomeInProcessImpl extends ClientBOHome implements IECRemoteCASExpectedOpeningPriceConsumerHome
{

    private RemoteCASExpectedOpeningPriceConsumerIECImpl iecConsumer;

    public RemoteCASExpectedOpeningPriceConsumer create()
    {
        return find();
    }

    public RemoteCASExpectedOpeningPriceConsumer find()
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
        iecConsumer = new RemoteCASExpectedOpeningPriceConsumerIECImpl();
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