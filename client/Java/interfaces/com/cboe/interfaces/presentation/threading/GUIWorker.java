//
// -----------------------------------------------------------------------------------
// Source file: GUIWorker.java
//
// PACKAGE: com.cboe.interfaces.presentation.threading;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.threading;

public interface GUIWorker extends SharedLock
{
    public boolean isInitializeViewRequired();
    public boolean isCleanUpViewRequired();
    public boolean isProcessDataRequired();
    public void initializeView();
    public void cleanUpView();
    public void handleException(Exception e);
    public void processData();
    public void execute() throws Exception;
}