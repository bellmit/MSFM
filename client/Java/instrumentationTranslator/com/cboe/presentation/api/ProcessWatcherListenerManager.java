//
// -----------------------------------------------------------------------------------
// Source file: ProcessWatcherListenerManager.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.processes.ProcessInfoModel;

interface ProcessWatcherListenerManager
{
    void dispatchProcessInfoEvent(ProcessInfoModel process);
    void addProcessInfoToCache(ProcessInfoModel processInfoModel);
}