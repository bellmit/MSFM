//
// ------------------------------------------------------------------------
// FILE: AlarmNotificationConsumerImpl.java
// 
// PACKAGE: com.cboe.presentation.alarms.events.consumers
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.alarms.events.consumers;

import java.util.*;

import com.cboe.idl.alarm.AlarmNotificationStruct;

import com.cboe.interfaces.events.AlarmNotificationConsumer;

import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.properties.InstrumentationProperties;

public class AlarmNotificationConsumerImpl
        implements AlarmNotificationConsumer
{
    public static final int DEFAULT_NOTIFICATION_DISPATCH_DELAY = 800;
    public static final String NOTIFICATION_DISPATCH_DELAY_KEY_NAME = "NotificationDispatchDelayMillis";

    protected EventChannelAdapter eventChannel;
    protected Timer taskTimer;
    protected NotificationDispatcher notificationDispatchTask;

    public AlarmNotificationConsumerImpl()
    {
        super();
        taskTimer = new Timer(true);
        notificationDispatchTask = new NotificationDispatcher(getEventChannelAdapter());
        taskTimer.schedule(notificationDispatchTask, getNotificationDispatchDelay(), getNotificationDispatchDelay());
    }

    protected EventChannelAdapter getEventChannelAdapter()
    {
        if (eventChannel == null)
        {
            eventChannel = EventChannelAdapterFactory.find();
        }
        return eventChannel;
    }

    public void acceptAlarmNotification(AlarmNotificationStruct[] alarmNotificationStructs)
    {
        if (GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_NOTIFICATION))
        {
            GUILoggerHome.find().debug("acceptAlarmNotification",
                                       GUILoggerINBusinessProperty.ALARM_NOTIFICATION, alarmNotificationStructs);
        }

        notificationDispatchTask.addAlarmNotificationStructs(alarmNotificationStructs);
    }

    private int getNotificationDispatchDelay()
    {
        int returnValue = DEFAULT_NOTIFICATION_DISPATCH_DELAY;

        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String property =
                    AppPropertiesFileFactory.find().getValue(InstrumentationProperties.ALARM_NOTIFICATION_SECTION_NAME,
                                                             NOTIFICATION_DISPATCH_DELAY_KEY_NAME);
            if(property != null && property.length() > 0)
            {
                try
                {
                    returnValue = Integer.parseInt(property);
                }
                catch(NumberFormatException e)
                {
                    GUILoggerHome.find().exception("Property: [" +
                                                   InstrumentationProperties.ALARM_NOTIFICATION_SECTION_NAME +
                                                   "]" + NOTIFICATION_DISPATCH_DELAY_KEY_NAME + " contained an invalid value:" +
                                                   property, e);
                }
            }
        }
        else
        {
            GUILoggerHome.find().alarm("Property: [" + InstrumentationProperties.ALARM_NOTIFICATION_SECTION_NAME +
                                       "]" + NOTIFICATION_DISPATCH_DELAY_KEY_NAME + " could not be obtained." +
                                       "Properties were not available.");
        }

        return returnValue;
    }
}