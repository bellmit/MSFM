package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;

import com.cboe.interfaces.events.IntermarketAdminMessageConsumer;

import com.cboe.domain.util.IntermarketAdminMessageContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

public class IntermarketAdminMessageConsumerImpl implements
        IntermarketAdminMessageConsumer
{
    private EventChannelAdapter eventProcessor;

    public IntermarketAdminMessageConsumerImpl(EventChannelAdapter eventProcessor)
    {
        super();
        this.eventProcessor = eventProcessor;
    }

    public void acceptIntermarketAdminMessage(String session, String sourceExchange,
            ProductKeysStruct productKey, AdminStruct adminMessage)
    {
        ChannelKey key = null;
        ChannelEvent event = null;
        SessionKeyContainer sessionKeyContainer = null;

        sessionKeyContainer = new SessionKeyContainer(session, productKey.classKey);
        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, sessionKeyContainer);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, adminMessage);
        eventProcessor.dispatch(event);
        if (productKey.classKey != 0)
        {
            sessionKeyContainer = new SessionKeyContainer(session, 0);
            key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, sessionKeyContainer);
            event = EventChannelAdapterFactory.find().getChannelEvent(this, key, adminMessage);
            eventProcessor.dispatch(event);
        }

    }

    public void acceptBroadcastIntermarketAdminMessage(String sessionName,
            String sourceExchange, AdminStruct adminMessage)
    {
        ChannelKey key = null;
        ChannelEvent event = null;
        SessionKeyContainer sessionKeyContainer = null;

        sessionKeyContainer = new SessionKeyContainer(sessionName, 0);
        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, sessionKeyContainer);
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, adminMessage);
        eventProcessor.dispatch(event);
    }

}
