package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiCallback.*;
import com.cboe.infraUtil.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.interfaces.internalCallback.*;

/**
 * This is the implementation of the CalendarServiceConsumer callback object which
 * receives calendar event updates.
 *
 */

public class CalendarUpdateConsumerImpl implements CalendarUpdateConsumer
{
    private EventChannelAdapter eventChannel;

    /**
     * CalendarServiceConsumerImpl constructor.
     *
     * @param channelType the channel type to publish on.
     * @param eventChannel the event channel to publish to.
     */
    public CalendarUpdateConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();

        this.eventChannel = eventChannel;
    }
    
    /**
     *  Accept an event and pass it along
     *
     */
    public void updateCalendarEvent(DateTypeStruct[] struct, short type)
    {
        ChannelKey key = new ChannelKey(ChannelType.CB_CALENDAR_UPDATE, new Short(type));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, struct);
        eventChannel.dispatch(event);
    }
}
