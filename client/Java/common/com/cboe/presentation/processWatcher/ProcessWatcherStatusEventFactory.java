//
// ------------------------------------------------------------------------
// FILE: ProcessWatcherStatusEventFactory.java
// 
// PACKAGE: com.cboe.presentation.processWatcher
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.processWatcher;

import java.util.*;

import com.cboe.interfaces.presentation.processWatcher.ProcessWatcherStatusEvent;

public class ProcessWatcherStatusEventFactory
{
    private ProcessWatcherStatusEventFactory() {}

    public static ProcessWatcherStatusEvent createProcessWatcherStatusEvent(Object source, int eventType,
                                                                            String orbName, Date time,
                                                                            short state, String eventOriginator,
                                                                            short reasonCode)
    {
        return new ProcessWatcherStatusEventImpl(source, eventType, orbName, time, state, eventOriginator, reasonCode);
    }
}