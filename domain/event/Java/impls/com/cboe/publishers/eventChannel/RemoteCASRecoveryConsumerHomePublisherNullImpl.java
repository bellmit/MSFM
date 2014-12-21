package com.cboe.publishers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.interfaces.events.IECRemoteCASRecoveryConsumerHome;
import com.cboe.interfaces.events.RemoteCASRecoveryConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASRecoveryConsumerHomePublisherNullImpl extends BOHome implements IECRemoteCASRecoveryConsumerHome
{
    private RemoteCASRecoveryConsumerPublisherImpl publisher;

    /**
     * RemoteCASRecoveryConsumerHomePublisherImpl constructor comment.
     */
    public RemoteCASRecoveryConsumerHomePublisherNullImpl()
    {
        super();
    }

    public RemoteCASRecoveryConsumer create()
    {
        return find();
    }

    public RemoteCASRecoveryConsumer find()
    {
        return publisher;
    }

    public void start()
    {
        try
        {
            publisher = new RemoteCASRecoveryConsumerPublisherImpl(null);
            publisher.create(String.valueOf(publisher.hashCode()));

            //Every BObject must be added to the container.
            addToContainer(publisher);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }


    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // NO IMPL NEEDED FOR THE PUBLISHER
    }

    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // NO IMPL NEEDED FOR THE PUBLISHER
    }
}// EOF
