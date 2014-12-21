package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.*;


public class IntermarketAdminMessageConsumerHomeInProcessImpl extends ClientBOHome implements IECIntermarketAdminMessageConsumerHome
{

    private IntermarketAdminMessageConsumerIECImpl imAdminMessageConsumer;

   /**
     * IntermarketAdminMessageConsumerHomeEventImpl constructor comment.
     */
    public IntermarketAdminMessageConsumerHomeInProcessImpl()
    {
        super();
    }

    public IntermarketAdminMessageConsumer create()
    {
        return find();
    }

    /**
     * Return the AcceptIntermarketAdminMessageConsumer Listener (If first time, create it and bind it to the orb).
     * @return AcceptIntermarketAdminMessageConsumer
     */
    public IntermarketAdminMessageConsumer find()
    {
        return imAdminMessageConsumer;
    }

    public void clientStart()
    {
        imAdminMessageConsumer.create(String.valueOf(imAdminMessageConsumer.hashCode()));
        addToContainer(imAdminMessageConsumer);
    }

    public void clientInitialize()
    {
        imAdminMessageConsumer = new IntermarketAdminMessageConsumerIECImpl();
    }

    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(IntermarketAdminMessageConsumer consumer, ChannelKey key) {}
    public void removeConsumer(IntermarketAdminMessageConsumer consumer, ChannelKey key) {}
    public void removeConsumer(IntermarketAdminMessageConsumer consumer) {}

}// EOF