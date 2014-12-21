package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.CalendarUpdateConsumer;
import com.cboe.infraUtil.DateTypeStruct;

public class CalendarUpdateEventConsumerImpl extends com.cboe.idl.internalEvents.POA_CalendarUpdateEventConsumer
                                            implements CalendarUpdateConsumer
{
    private CalendarUpdateConsumer delegate;

    public CalendarUpdateEventConsumerImpl(CalendarUpdateConsumer calendarUpdateConsumer) 
    {
        delegate = calendarUpdateConsumer;
    }

    public void updateCalendarEvent(DateTypeStruct[] dateTypeStructs, short updateType)
    {
        delegate.updateCalendarEvent(dateTypeStructs, updateType);
    }
    
    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data) throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
