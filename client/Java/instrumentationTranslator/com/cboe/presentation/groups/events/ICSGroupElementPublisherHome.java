//
// ------------------------------------------------------------------------
// FILE: ICSGroupElementPublisherHome.java
// 
// PACKAGE: com.cboe.interfaces.instrumentation.alarms
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.groups.events;

import com.cboe.interfaces.events.ICSGroupElementServiceConsumer;

public interface ICSGroupElementPublisherHome
{
    /**
     * Initialize the home, create the publishers and attach them to EC
     * @param channelName
     * @throws Exception
     */
    void initializePublisher(String channelName)
            throws Exception;

    /**
     * @return alarm definition and activation event channel name
     */
    String getChannelName();

    /**
     * The Alarm Definition Publisher is provides access to the definition and condition interfaces.
     * The publisher is responsible for getting definition and condition events into the AlarmDefinition Event Channel.
     * @return Alarm Definition Publisher Proxy
     */
    ICSGroupElementServiceConsumer getICSGroupElementPublisher();

}