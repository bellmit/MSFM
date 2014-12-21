//
// -----------------------------------------------------------------------------------
// Source file: NonGUIWorkerImpl.java
//
// PACKAGE: com.cboe.presentation.threading;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.threading;

public class NonGUIWorkerImpl extends GUIWorkerImpl
{
    public NonGUIWorkerImpl()
    {
        super();
    }

    public NonGUIWorkerImpl(Object sharedLockReference)
    {
        super(sharedLockReference);
    }

    final public boolean isCleanUpViewRequired()
    {
        return false;
    }

    final public boolean isInitializeViewRequired()
    {
        return false;
    }

    final public boolean isProcessDataRequired()
    {
        return false;
    }
}