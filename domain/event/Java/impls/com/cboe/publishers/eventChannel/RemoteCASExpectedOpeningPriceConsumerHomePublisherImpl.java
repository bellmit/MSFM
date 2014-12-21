package com.cboe.publishers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.util.ChannelKey;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.events.RemoteCASExpectedOpeningPriceEventConsumer;
import com.cboe.idl.events.RemoteCASExpectedOpeningPriceEventConsumerHelper;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.consumers.eventChannel.EventChannelFilterHelper;

import com.cboe.interfaces.events.RemoteCASExpectedOpeningPriceConsumer;
import com.cboe.interfaces.events.IECRemoteCASExpectedOpeningPriceConsumerHome;

public class RemoteCASExpectedOpeningPriceConsumerHomePublisherImpl extends BOHome implements IECRemoteCASExpectedOpeningPriceConsumerHome
{
    private RemoteCASExpectedOpeningPriceEventConsumer eventConsumer;
    private RemoteCASExpectedOpeningPriceConsumerPublisherImpl publisher;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "RemoteCASExpectedOpeningPrice";

    public RemoteCASExpectedOpeningPriceConsumer create()
    {
        return find();
    }

    public RemoteCASExpectedOpeningPriceConsumer find()
    {
        return publisher;
    }

    public void start()
    {
        try
        {
            org.omg.CORBA.Object obj;
            String eventChannelName = eventChannelFilterHelper.getChannelName(CHANNEL_NAME);
            String repID = RemoteCASExpectedOpeningPriceEventConsumerHelper.id();
            obj = eventService.getEventChannelSupplierStub( eventChannelName, repID );
            eventConsumer = RemoteCASExpectedOpeningPriceEventConsumerHelper.narrow( obj );

            publisher = new RemoteCASExpectedOpeningPriceConsumerPublisherImpl(eventConsumer);
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

//    public void addConsumer(RemoteCASExpectedOpeningPriceConsumer consumer, ChannelKey key) {}
//    public void removeConsumer(RemoteCASExpectedOpeningPriceConsumer consumer, ChannelKey key) {}
//    public void removeConsumer(RemoteCASExpectedOpeningPriceConsumer consumer) {}
}// EOF
