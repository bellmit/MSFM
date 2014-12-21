package com.cboe.publishers.eventChannel;

import com.cboe.exceptions.*;
import com.cboe.util.ChannelKey;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.events.MarketDataCallbackEventConsumerHelper;
import com.cboe.idl.events.MarketDataCallbackEventConsumer;

import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.consumers.eventChannel.EventChannelFilterHelper;

import com.cboe.interfaces.events.MarketDataCallbackConsumer;
import com.cboe.interfaces.events.IECMarketDataCallbackConsumerHome;

public class MarketDataCallbackConsumerHomePublisherImpl extends BOHome implements IECMarketDataCallbackConsumerHome
{
    private MarketDataCallbackEventConsumer eventConsumer;
    private MarketDataCallbackConsumerPublisherImpl publisher;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "MarketDataCallback";

    public MarketDataCallbackConsumer create()
    {
        return find();
    }

    public MarketDataCallbackConsumer find()
    {
        return publisher;
    }

    public void start()
    {
        try
        {
            org.omg.CORBA.Object obj;
            String eventChannelName = eventChannelFilterHelper.getChannelName(CHANNEL_NAME);
            String repID = MarketDataCallbackEventConsumerHelper.id();
            obj = eventService.getEventChannelSupplierStub( eventChannelName, repID );
            eventConsumer = MarketDataCallbackEventConsumerHelper.narrow( obj );

            publisher = new MarketDataCallbackConsumerPublisherImpl(eventConsumer);
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

    public String getFullChannelName() throws Exception
    {
        return eventChannelFilterHelper.getChannelName(CHANNEL_NAME);
    }
}

