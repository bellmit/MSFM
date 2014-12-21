package com.cboe.consumers.eventChannel;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.IntermarketAdminMessageContainer;
import com.cboe.domain.util.IntermarketBroadcastMessageContainer;


public class IntermarketAdminMessageConsumerIECImpl extends BObject implements IntermarketAdminMessageConsumer
{
    private InstrumentedEventChannelAdapter internalEventChannel;
    private static final Integer INT_0 = 0;

    public IntermarketAdminMessageConsumerIECImpl()
    {
        super();
        internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
    }

    public void acceptIntermarketAdminMessage(String sessionName, String sourceExchange, ProductKeysStruct productKeys, AdminStruct adminMessage)
    {
        if (Log.isDebugOn())
        {
            Log.debug( this, "event received -> acceptIntermarketAdminMessageFrom : " + sourceExchange );
        }
        ChannelKey channelKey = new ChannelKey(ChannelKey.INTERMARKET_ADMIN_MESSAGE, new SessionKeyContainer(sessionName, productKeys.classKey));
        IntermarketAdminMessageContainer imAdminMessage = new IntermarketAdminMessageContainer(productKeys, adminMessage, sourceExchange, sessionName);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, imAdminMessage);
        if (productKeys.classKey != 0)
        {
            internalEventChannel.dispatch(event);
            channelKey = new ChannelKey(ChannelKey.INTERMARKET_ADMIN_MESSAGE, new SessionKeyContainer(sessionName, INT_0));
            imAdminMessage = new IntermarketAdminMessageContainer(productKeys, adminMessage, sourceExchange, sessionName);
            event = internalEventChannel.getChannelEvent(this, channelKey, imAdminMessage);
        }
        internalEventChannel.dispatch(event);
    }

    public void acceptBroadcastIntermarketAdminMessage(
                      String sessionName,
                      String sourceExchange,
                      AdminStruct adminMessage)
    {
        if (Log.isDebugOn())
        {
            Log.debug( this, "event received -> acceptBroadcastIntermarketAdminMessage : " + sourceExchange );
        }

        ChannelKey channelKey = new ChannelKey(ChannelKey.INTERMARKET_ADMIN_MESSAGE_BROADCAST, INT_0);
        IntermarketBroadcastMessageContainer imAdminMessage = new IntermarketBroadcastMessageContainer(adminMessage, sourceExchange, sessionName);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, imAdminMessage);
        internalEventChannel.dispatch(event);
    }

 }
