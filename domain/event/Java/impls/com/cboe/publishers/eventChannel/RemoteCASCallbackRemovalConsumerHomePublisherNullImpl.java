package com.cboe.publishers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.interfaces.events.IECRemoteCASCallbackRemovalConsumerHome;
import com.cboe.interfaces.events.RemoteCASCallbackRemovalConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASCallbackRemovalConsumerHomePublisherNullImpl extends BOHome implements IECRemoteCASCallbackRemovalConsumerHome
{
    private RemoteCASCallbackRemovalConsumerPublisherImpl publisher;

    /**
     * RemoteCASCallbackRemovalConsumerHomePublisherImpl constructor comment.
     */
    public RemoteCASCallbackRemovalConsumerHomePublisherNullImpl()
    {
        super();
    }

    public RemoteCASCallbackRemovalConsumer create()
    {
        return find();
    }

    public RemoteCASCallbackRemovalConsumer find()
    {
        return publisher;
    }

    public void start()
    {
        try
        {
            publisher = new RemoteCASCallbackRemovalConsumerPublisherImpl(null);
            publisher.create(String.valueOf(publisher.hashCode()));

            //Every BObject must be added to the container.
            addToContainer(publisher);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Jeff Illian
     * @author Eric J. Fredericks
     * @version 3/20/03
     */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // NO IMPL NEEDED FOR THE PUBLISHER
    }

    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Jeff Illian
     * @author Eric J. Fredericks
     * @version 3/20/03
     * */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // NO IMPL NEEDED FOR THE PUBLISHER
    }

//    public void addConsumer(RemoteCASCallbackRemovalConsumer consumer, ChannelKey key) {}
//    public void removeConsumer(RemoteCASCallbackRemovalConsumer consumer, ChannelKey key) {}
//    public void removeConsumer(RemoteCASCallbackRemovalConsumer consumer) {}
}// EOF
