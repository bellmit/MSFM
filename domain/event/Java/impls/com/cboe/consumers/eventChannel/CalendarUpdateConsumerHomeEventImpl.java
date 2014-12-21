package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.events.IECCalendarUpdateConsumerHome;
import com.cboe.interfaces.events.CalendarUpdateConsumer;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.loggingService.Log;

public class CalendarUpdateConsumerHomeEventImpl extends ClientBOHome implements IECCalendarUpdateConsumerHome {
    private CalendarUpdateConsumerIECImpl calendarUpdateConsumer;
    private CalendarUpdateEventConsumerImpl calendarUpdate;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "CalendarAdminChannel";

    public CalendarUpdateConsumer create()
    {
        return find();
    }

    public CalendarUpdateConsumer find()
    {
        return calendarUpdateConsumer;
    }

    public void clientStart() throws Exception
    {
        calendarUpdateConsumer.create(String.valueOf(calendarUpdateConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(calendarUpdateConsumer);

        //The addToContainer call MUST occur prior to creation of the interceptor.
        //      manageObject(orderStatusConsumer);

        if (eventService == null)
        {
            eventService = eventChannelFilterHelper.connectEventService();
        }

        String interfaceRepId = com.cboe.idl.internalEvents.CalendarUpdateEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer(CHANNEL_NAME, interfaceRepId, calendarUpdate );
    }

    public void clientInitialize()
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        calendarUpdateConsumer = new CalendarUpdateConsumerIECImpl();
        calendarUpdate = new CalendarUpdateEventConsumerImpl(calendarUpdateConsumer);
    }

    public void addFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        addConstraint(channelKey);
    }

    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if(find() != null)
        {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.addEventFilter(calendarUpdate, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }


    public void removeFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        removeConstraint(channelKey);
    }

    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if(find() != null)
        {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }

    protected String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if (parm.equals(""))
        {
            return "";
        }
        else
        {
        	StringBuilder buf = new StringBuilder(50);
            buf.append("$.").append(parm);
            return buf.toString();
        }

    }

    protected String getParmName(ChannelKey channelKey)
    {
        switch (channelKey.channelType)
        {
            case ChannelType.CALENDAR_UPDATE:
                return "";
            default :
                if(Log.isDebugOn())
                {
                    Log.debug(this, "Unknown channel type: " + channelKey.channelType);
                }
                return "";
        }
    }

//    // Unused methods declared in home interface for server usage.
    public void addConsumer(CalendarUpdateConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CalendarUpdateConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CalendarUpdateConsumer consumer) {}
}
