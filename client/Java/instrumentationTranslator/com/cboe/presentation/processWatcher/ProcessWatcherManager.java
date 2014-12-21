//
// -----------------------------------------------------------------------------------
// Source file: ProcessWatcherManager
//
// PACKAGE: com.cboe.presentation.processWatcher
//
// The ProcessWatcherManager requires 3 vm parameters:
//     1. ProcessWatcher.EventChannel    = ProdProcessWatcher
//     2. ProcessWatcher.lifeLineTimeout = 60000
//     3. CHECK_REP_IDL                  = false
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.processWatcher;

import com.cboe.interfaces.presentation.processes.ProcessInfoModel;

public interface ProcessWatcherManager
{
    /**
     * This method adds a listener to the internal list of callbacks to be notified
     * when the process state changes.
     * @param l a ProcessWatcherStatusListener Object
     */
    public void addProcessWatcherStatusListener(ProcessWatcherStatusListener l);

    /**
     * This method removes a listener from the internal list of callbacks for process state
     * @param l a ProcessWatcherStatusListener Object
     */
    public void removeProcessWatcherStatusListener(ProcessWatcherStatusListener l);

    /**
     * This method return a list of the processes being watched by the ProcessWatcher
     * @return an Array of ProcessInfoModel Objects which identify each individual process
     */
    public ProcessInfoModel[] getWatchedProcessList() throws Exception;

    /**
     * This method registers the ProcessWatcherManager to receive process events
     * @throws Exception
     */
    public void registerWithProcessWatcher() throws Exception;

    /**
     * This method unregisters the ProcessWatcherManager to receive process events
     * @throws Exception
     */
    public void unregisterWithProcessWatcher() throws Exception;

} // -- end of interface ProcessWatcherManager
