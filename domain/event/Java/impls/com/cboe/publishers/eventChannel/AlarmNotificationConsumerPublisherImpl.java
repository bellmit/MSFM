//
// ------------------------------------------------------------------------
// FILE: AlarmNotificationConsumerPublisherImpl.java
// 
// PACKAGE: com.cboe.publishers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.publishers.eventChannel;

import com.cboe.idl.alarm.AlarmNotificationStruct;
import com.cboe.idl.alarmEvents.AlarmNotificationEventConsumer;
import com.cboe.interfaces.events.AlarmNotificationConsumer;


/**
 * @author torresl@cboe.com
 */
public class AlarmNotificationConsumerPublisherImpl
        implements AlarmNotificationConsumer
{
    protected AlarmNotificationEventConsumer eventChannelDelegate;
    public AlarmNotificationConsumerPublisherImpl(AlarmNotificationEventConsumer eventChannelDelegate)
    {
        super();
        this.eventChannelDelegate = eventChannelDelegate;
    }

    public void acceptAlarmNotification(AlarmNotificationStruct[] alarmNotificationStructs)
    {
        eventChannelDelegate.acceptAlarmNotification(alarmNotificationStructs);
    }
}
