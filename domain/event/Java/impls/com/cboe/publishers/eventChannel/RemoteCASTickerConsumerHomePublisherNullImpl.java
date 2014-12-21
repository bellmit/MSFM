package com.cboe.publishers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.interfaces.events.IECRemoteCASTickerConsumerHome;
import com.cboe.interfaces.events.RemoteCASTickerConsumer;
import com.cboe.util.ChannelKey;

public class RemoteCASTickerConsumerHomePublisherNullImpl extends BOHome implements IECRemoteCASTickerConsumerHome
{
    private RemoteCASTickerConsumerPublisherImpl publisher;

    /**
     * RemoteCASTickerConsumerHomePublisherImpl constructor comment.
     */
    public RemoteCASTickerConsumerHomePublisherNullImpl()
    {
        super();
    }

    public RemoteCASTickerConsumer create()
    {
        return find();
    }

    public RemoteCASTickerConsumer find()
    {
        return publisher;
    }

    public void start()
    {
        try
        {
            publisher = new RemoteCASTickerConsumerPublisherImpl(null);
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

//    public void addConsumer(RemoteCASTickerConsumer consumer, ChannelKey key) {}
//    public void removeConsumer(RemoteCASTickerConsumer consumer, ChannelKey key) {}
//    public void removeConsumer(RemoteCASTickerConsumer consumer) {}
}// EOF
