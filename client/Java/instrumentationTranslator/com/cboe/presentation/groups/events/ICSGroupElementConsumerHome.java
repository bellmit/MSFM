//
// ------------------------------------------------------------------------
// FILE: AlarmConsumersHome.java
// 
// PACKAGE: com.cboe.interfaces.instrumentation.alarms
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.groups.events;

import com.cboe.interfaces.events.ICSGroupElementConsumer;

public interface ICSGroupElementConsumerHome
{
    /**
     * Initialize the home, create the consumer and attach them to the EC
     */
    void initializeConsumer(String channelName)
            throws Exception;

    /**
     *
     * @return icsGroupElement event channel name
     */
    String getICSGroupElementEventChannelName();

    /**
     * The AlarmNotificationConsumer takes notification events and sends them to the IEC.
     * @return Alarm Notification Consumer
     */
    ICSGroupElementConsumer getICSGroupElementConsumer();
}