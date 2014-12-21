//
// -----------------------------------------------------------------------------------
// Source file: AbstractGUIWorker.java
//
// PACKAGE: com.cboe.presentation.threading;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.threading;

import com.cboe.interfaces.presentation.threading.GUIWorker;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public abstract class AbstractGUIWorker implements GUIWorker
{
    protected volatile boolean exceptionThrown;
    protected Object sharedLockReference;

    public AbstractGUIWorker()
    {
        exceptionThrown = false;
        sharedLockReference = null;
    }

    public AbstractGUIWorker(Object sharedLockReference)
    {
        this();
        this.sharedLockReference = sharedLockReference;
    }

    public abstract void processData();
    public abstract void execute() throws Exception;

    public Object getSharedLockReference()
    {
        return sharedLockReference;
    }

    public void initializeView()
    {}

    public void cleanUpView()
    {}

    public void handleException(Exception e)
    {
        exceptionThrown = true;
        DefaultExceptionHandlerHome.find().process(e);
    }

    public boolean isCleanUpViewRequired()
    {
        return isCleanUpEnabled();
    }

    public boolean isProcessDataRequired()
    {
        return isProcessDataEnabled();
    }

    public boolean isInitializeViewRequired()
    {
        return isInitializeEnabled();
    }

    public boolean isCleanUpEnabled()
    {
        return true;
    }

    public boolean isInitializeEnabled()
    {
        return true;
    }

    public boolean isProcessDataEnabled()
    {
        return true;
    }

    public boolean wasExceptionThrown()
    {
        return exceptionThrown;
    }
}