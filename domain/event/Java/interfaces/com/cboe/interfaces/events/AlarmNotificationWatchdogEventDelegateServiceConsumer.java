//
// -----------------------------------------------------------------------------------
// Source file: AlarmNotificationWatchdogEventDelegateServiceConsumer.java
//
// PACKAGE: com.cboe.interfaces.events
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.events;

import com.cboe.idl.alarmEvents.AlarmNotificationWatchdogEventService;

public interface AlarmNotificationWatchdogEventDelegateServiceConsumer extends AlarmNotificationWatchdogServiceConsumer
{
    void setAlarmNotificationWatchdogEventServiceDelegate(AlarmNotificationWatchdogEventService service);
}