package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.events.AuctionConsumer;
import com.cboe.interfaces.events.IECAuctionConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

public class AuctionConsumerHomeInProcessImpl  extends ClientBOHome implements IECAuctionConsumerHome
{
    private AuctionConsumerIECImpl auctionConsumer;
    
    public void clientStart()
        throws Exception
    {
        auctionConsumer.create(String.valueOf(auctionConsumer.hashCode()));
        addToContainer(auctionConsumer);
    }

    public void clientInitialize()
    {
        auctionConsumer = new AuctionConsumerIECImpl();
    }

    public AuctionConsumer find()
    {
        return create();
    }

    public AuctionConsumer create()
    {
        return auctionConsumer;
    }

    public void addConsumer(AuctionConsumer consumer, ChannelKey key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // intentionally blank
    }

    public void removeConsumer(AuctionConsumer consumer, ChannelKey key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // intentionally blank
    }

    public void removeConsumer(AuctionConsumer consumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // intentionally blank
    }

    public void addFilter( ChannelKey filterKey ) throws
        SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // intentionally blank
    }

    public void removeFilter( ChannelKey filterKey ) throws
        SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // intentionally blank
    }
    
}
