//
// -----------------------------------------------------------------------------------
// Source file: ProcessWatcherStatusListener
//
// PACKAGE: com.cboe.presentation.processWatcher
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.processWatcher;

import com.cboe.interfaces.presentation.processWatcher.ProcessWatcherStatusEvent;
import java.util.EventListener;

/**
 * The event types are listed in ProcessWatcherStatusEvent
 */
public interface ProcessWatcherStatusListener extends EventListener
{
    public void processStatusChange(ProcessWatcherStatusEvent event);
    public void publishCurrentStatus();
    public void publishCurrentStatus(String orbName);

} // -- end of interface ProcessWatcherStatusListener
