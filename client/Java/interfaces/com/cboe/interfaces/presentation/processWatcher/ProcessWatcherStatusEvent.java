//
// ------------------------------------------------------------------------
// FILE: ProcessWatcherStatusEvent.java
// 
// PACKAGE: com.cboe.interfaces.presentation.processWatcher
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.processWatcher;

import com.cboe.interfaces.events.ProcessWatcherStatus;

import java.util.Date;

public interface ProcessWatcherStatusEvent extends ProcessWatcherStatus
{

    String getOrbName();

    int getType();

    Date getTime();

    short getState();

    String getEventOriginator();

    short getReasonCode();
}
