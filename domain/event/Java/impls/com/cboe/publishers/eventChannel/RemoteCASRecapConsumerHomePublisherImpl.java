package com.cboe.publishers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.util.ChannelKey;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.events.RemoteCASRecapEventConsumer;
import com.cboe.idl.events.RemoteCASRecapEventConsumerHelper;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.consumers.eventChannel.EventChannelFilterHelper;

import com.cboe.interfaces.events.RemoteCASRecapConsumer;
import com.cboe.interfaces.events.IECRemoteCASRecapConsumerHome;

public class RemoteCASRecapConsumerHomePublisherImpl extends BOHome implements IECRemoteCASRecapConsumerHome
{
    private RemoteCASRecapEventConsumer eventConsumer;
    private RemoteCASRecapConsumerPublisherImpl publisher;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "RemoteCASRecap";

    public RemoteCASRecapConsumer create()
    {
        return find();
    }

    public RemoteCASRecapConsumer find()
    {
        return publisher;
    }

    public void start()
    {
        try
        {
            org.omg.CORBA.Object obj;
            String eventChannelName = eventChannelFilterHelper.getChannelName(CHANNEL_NAME);
            String repID = RemoteCASRecapEventConsumerHelper.id();
            obj = eventService.getEventChannelSupplierStub( eventChannelName, repID );
            eventConsumer = RemoteCASRecapEventConsumerHelper.narrow( obj );

            publisher = new RemoteCASRecapConsumerPublisherImpl(eventConsumer);
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

//    public void addConsumer(RemoteCASRecapConsumer consumer, ChannelKey key) {}
//    public void removeConsumer(RemoteCASRecapConsumer consumer, ChannelKey key) {}
//    public void removeConsumer(RemoteCASRecapConsumer consumer) {}
}// EOF
