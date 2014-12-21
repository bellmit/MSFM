package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.interfaces.events.IECSystemControlConsumerHome;
import com.cboe.interfaces.events.SystemControlConsumer;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

public class SystemControlConsumerHomeEventImpl extends ClientBOHome implements IECSystemControlConsumerHome {
    private SystemControlConsumerIECImpl systemControlConsumer;
    private SystemControlEventConsumerImpl systemControl;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private final String CHANNEL_NAME = "SystemControl";

    public SystemControlConsumer create()
    {
        return find();
    }

    public SystemControlConsumer find()
    {
        return systemControlConsumer;
    }

    public void clientInitialize()
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        systemControlConsumer = new SystemControlConsumerIECImpl();
        systemControl = new SystemControlEventConsumerImpl(systemControlConsumer);
    }

    public void clientStart() throws Exception
    {
        systemControlConsumer.create(String.valueOf(systemControlConsumer.hashCode()));
        addToContainer(systemControlConsumer);

        if (eventService == null)
        {
            eventService = eventChannelFilterHelper.connectEventService();
        }

        String interfaceRepId = com.cboe.idl.events.SystemControlEventConsumerHelper.id();
        eventChannelFilterHelper.connectConsumer(CHANNEL_NAME, interfaceRepId, systemControl);
    }

    public void addFilter(ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        addConstraint(channelKey);
    }

    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if (find() != null)
        {
            String constraintString = getConstraintString(channelKey);

            eventChannelFilterHelper.addEventFilter(systemControl, channelKey,
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
        if( find() != null )
        {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter(channelKey, constraintString);
        }
    }

    private String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if(parm.equals(EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT) ||
           parm.equals(EventChannelFilterHelper.NO_EVENTS_CONSTRAINT))
        {
            return parm;
        }

        StringBuilder buf = new StringBuilder(parm.length()+2);
        buf.append("$.").append(parm);
        return buf.toString();
    }

    private String getParmName(ChannelKey channelKey)
    {
        Integer key = (Integer)channelKey.key;

        switch (channelKey.channelType)
        {
            case ChannelType.SERVER_FAILURE:
                return new StringBuilder(50).append("acceptServerFailure.serverType==").append(key).toString();
            case ChannelType.USER_ACTIVITY_TIMEOUT:
                return EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT;
            case ChannelType.GROUP_CANCEL:
				return EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT;
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(SystemControlConsumer consumer, ChannelKey key) {}
    public void removeConsumer(SystemControlConsumer consumer, ChannelKey key) {}
    public void removeConsumer(SystemControlConsumer consumer) {}
}
