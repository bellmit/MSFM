//
// ------------------------------------------------------------------------
// FILE: AlarmNotificationConsumerProxyImpl.java
// 
// PACKAGE: com.cboe.consumers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.AlarmNotificationConsumer;
import com.cboe.idl.alarm.AlarmNotificationStruct;
import com.cboe.idl.alarmEvents.AlarmNotificationEventConsumerPOA;

/**
 * Listen for Alarm Notification Events and pass them on the delegate.
 * @author torresl@cboe.com
 */
public class AlarmNotificationConsumerProxyImpl
        extends AlarmNotificationEventConsumerPOA
        implements AlarmNotificationConsumer
{
    protected AlarmNotificationConsumer delegate;

    public AlarmNotificationConsumerProxyImpl(AlarmNotificationConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void acceptAlarmNotification(AlarmNotificationStruct[] alarmNotificationStructs)
    {
        delegate.acceptAlarmNotification(alarmNotificationStructs);
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
            throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }

}
