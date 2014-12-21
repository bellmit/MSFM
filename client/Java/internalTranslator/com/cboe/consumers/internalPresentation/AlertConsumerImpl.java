package com.cboe.consumers.internalPresentation;


import com.cboe.interfaces.events.AlertConsumer;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.*;
import com.cboe.idl.cmiIntermarketMessages.AlertStruct;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;

public class AlertConsumerImpl implements AlertConsumer
{
    private EventChannelAdapter eventChannel = null;

    public AlertConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
    }
    public void acceptAlertUpdate(AlertStruct alertStruct)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        key = new ChannelKey(ChannelType.CB_ALERT_UPDATE, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, alertStruct);
        eventChannel.dispatch(event);
    }

    public void acceptSatisfactionAlert(SatisfactionAlertStruct satisfactionAlertStruct)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        key = new ChannelKey(ChannelType.CB_ALERT_SATISFACTION, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, satisfactionAlertStruct);
        eventChannel.dispatch(event);

    }


    public void acceptAlert(AlertStruct alertStruct)
    {
        ChannelKey key = null;
        ChannelEvent event = null;

        key = new ChannelKey(ChannelType.CB_ALERT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, alertStruct);
        eventChannel.dispatch(event);

    }

}
