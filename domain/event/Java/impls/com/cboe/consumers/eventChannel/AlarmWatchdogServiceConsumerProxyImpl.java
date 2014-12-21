//
// -----------------------------------------------------------------------------------
// Source file: AlarmWatchdogServiceConsumerProxyImpl.java
//
// PACKAGE: com.cboe.consumers.eventChannel
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.eventChannel;

import org.omg.CORBA.Any;
import org.omg.CosEventComm.Disconnected;

import com.cboe.idl.alarmEvents.AlarmNotificationWatchdogEventServicePOA;
import com.cboe.idl.alarm.AlarmNotificationWatchdogStruct;

import com.cboe.interfaces.events.AlarmNotificationWatchdogServiceConsumer;

public class AlarmWatchdogServiceConsumerProxyImpl
        extends AlarmNotificationWatchdogEventServicePOA
        implements AlarmNotificationWatchdogServiceConsumer
{
    protected AlarmNotificationWatchdogServiceConsumer delegate;

    public AlarmWatchdogServiceConsumerProxyImpl(AlarmNotificationWatchdogServiceConsumer delegate)
    {
        this.delegate = delegate;
    }

    public void publishAllWatchdogs(long requestId)
    {
        delegate.publishAllWatchdogs(requestId);
    }

    public void publishWatchdogById(long requestId, int id)
    {
        delegate.publishWatchdogById(requestId, id);
    }

    public void createWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        delegate.createWatchdog(requestId, alarmNotificationWatchdogStruct);
    }

    public void deleteWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        delegate.deleteWatchdog(requestId, alarmNotificationWatchdogStruct);
    }

    public void updateWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        delegate.updateWatchdog(requestId, alarmNotificationWatchdogStruct);
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(Any data)
            throws Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
