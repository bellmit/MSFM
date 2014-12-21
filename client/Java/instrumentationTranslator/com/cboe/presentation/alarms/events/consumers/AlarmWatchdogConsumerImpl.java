//
// ------------------------------------------------------------------------
// FILE: AlarmWatchdogConsumerImpl.java
// 
// PACKAGE: com.cboe.presentation.alarms.events.consumers
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.alarms.events.consumers;

import com.cboe.idl.alarm.AlarmNotificationWatchdogStruct;

import com.cboe.interfaces.events.AlarmNotificationWatchdogConsumer;

import com.cboe.util.ChannelType;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;

public class AlarmWatchdogConsumerImpl
        extends AlarmConsumerImpl
        implements AlarmNotificationWatchdogConsumer
{
    public void acceptWatchdogs(long requestId, AlarmNotificationWatchdogStruct[] alarmNotificationWatchdogStructs)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            GUILoggerHome.find().debug("acceptWatchdogs (requestId=" + requestId + ')',
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, alarmNotificationWatchdogStructs);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_WATCHDOGS, requestId, alarmNotificationWatchdogStructs);
    }

    public void acceptChangedWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            GUILoggerHome.find().debug("acceptChangedWatchdog (requestId=" + requestId + ')',
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, alarmNotificationWatchdogStruct);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG, requestId, alarmNotificationWatchdogStruct);
    }

    public void acceptDeleteWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            GUILoggerHome.find().debug("acceptDeleteWatchdog (requestId=" + requestId + ')',
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, alarmNotificationWatchdogStruct);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, requestId, alarmNotificationWatchdogStruct);
    }

    public void acceptNewWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_WATCHDOG))
        {
            GUILoggerHome.find().debug("acceptNewWatchdog (requestId=" + requestId + ')',
                                       GUILoggerINBusinessProperty.ALARM_WATCHDOG, alarmNotificationWatchdogStruct);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_NEW_WATCHDOG, requestId, alarmNotificationWatchdogStruct);
    }
}
