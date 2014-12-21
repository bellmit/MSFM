package com.cboe.consumers.eventChannel;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.CalendarUpdateConsumer;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infraUtil.DateTypeStruct;
import com.cboe.domain.util.CalendarUpdateEventHolder;

public class CalendarUpdateConsumerIECImpl extends BObject implements CalendarUpdateConsumer
{
    private EventChannelAdapter internalEventChannel;

    public CalendarUpdateConsumerIECImpl()
    {
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void updateCalendarEvent(DateTypeStruct[] dateTypeStructs, short updateType)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Event received: calendarUpdate");
        }
        
        ChannelKey channelKey = new ChannelKey(ChannelKey.CALENDAR_UPDATE, Integer.valueOf(0));
        CalendarUpdateEventHolder calendarEvent = new CalendarUpdateEventHolder(dateTypeStructs, updateType);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, calendarEvent);
        internalEventChannel.dispatch(event);
    }
}
