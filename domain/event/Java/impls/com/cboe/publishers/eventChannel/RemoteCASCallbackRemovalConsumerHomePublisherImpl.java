package com.cboe.publishers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.util.ChannelKey;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.events.RemoteCASCallbackRemovalEventConsumer;
import com.cboe.idl.events.RemoteCASCallbackRemovalEventConsumerHelper;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.consumers.eventChannel.EventChannelFilterHelper;

import com.cboe.interfaces.events.RemoteCASCallbackRemovalConsumer;
import com.cboe.interfaces.events.IECRemoteCASCallbackRemovalConsumerHome;

public class RemoteCASCallbackRemovalConsumerHomePublisherImpl extends BOHome implements IECRemoteCASCallbackRemovalConsumerHome
{
    private RemoteCASCallbackRemovalEventConsumer eventConsumer;
    private RemoteCASCallbackRemovalConsumerPublisherImpl publisher;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "RemoteCASCallbackRemoval";

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
            org.omg.CORBA.Object obj;
            String eventChannelName = eventChannelFilterHelper.getChannelName(CHANNEL_NAME);
            String repID = RemoteCASCallbackRemovalEventConsumerHelper.id();
            obj = eventService.getEventChannelSupplierStub( eventChannelName, repID );
            eventConsumer = RemoteCASCallbackRemovalEventConsumerHelper.narrow( obj );

            publisher = new RemoteCASCallbackRemovalConsumerPublisherImpl(eventConsumer);
            publisher.create(String.valueOf(publisher.hashCode()));

            //Every BObject must be added to the container.
            addToContainer(publisher);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    public void initialize()
    {
        FoundationFramework ff = FoundationFramework.getInstance();
    	eventService = ff.getEventService();
        eventChannelFilterHelper = new EventChannelFilterHelper();
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
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // NO IMPL NEEDED FOR THE PUBLISHER
    }

//    public void addConsumer(RemoteCASCallbackRemovalConsumer consumer, ChannelKey key) {}
//    public void removeConsumer(RemoteCASCallbackRemovalConsumer consumer, ChannelKey key) {}
//    public void removeConsumer(RemoteCASCallbackRemovalConsumer consumer) {}
}// EOF
