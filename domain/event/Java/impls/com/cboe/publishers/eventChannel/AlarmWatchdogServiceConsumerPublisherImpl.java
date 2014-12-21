//
// -----------------------------------------------------------------------------------
// Source file: AlarmWatchdogServiceConsumerPublisherImpl.java
//
// PACKAGE: com.cboe.publishers.eventChannel
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.publishers.eventChannel;

import com.cboe.idl.alarmEvents.AlarmNotificationWatchdogEventService;
import com.cboe.idl.alarm.AlarmNotificationWatchdogStruct;

import com.cboe.interfaces.events.AlarmNotificationWatchdogEventDelegateServiceConsumer;

public class AlarmWatchdogServiceConsumerPublisherImpl implements AlarmNotificationWatchdogEventDelegateServiceConsumer
{
    protected AlarmNotificationWatchdogEventService eventChannelDelegate;

    public AlarmWatchdogServiceConsumerPublisherImpl()
    {}

    public AlarmWatchdogServiceConsumerPublisherImpl(AlarmNotificationWatchdogEventService eventChannelDelegate)
    {
        this();
        setAlarmNotificationWatchdogEventServiceDelegate(eventChannelDelegate);
    }

    public void setAlarmNotificationWatchdogEventServiceDelegate(AlarmNotificationWatchdogEventService eventChannelDelegate)
    {
        this.eventChannelDelegate = eventChannelDelegate;
    }

    public void createWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        eventChannelDelegate.createWatchdog(requestId, alarmNotificationWatchdogStruct);
    }

    public void deleteWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        eventChannelDelegate.deleteWatchdog(requestId, alarmNotificationWatchdogStruct);
    }

    public void publishAllWatchdogs(long requestId)
    {
        eventChannelDelegate.publishAllWatchdogs(requestId);
    }

    public void publishWatchdogById(long requestId, int id)
    {
        eventChannelDelegate.publishWatchdogById(requestId, id);
    }

    public void updateWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        eventChannelDelegate.updateWatchdog(requestId, alarmNotificationWatchdogStruct);
    }
}
