//
// -----------------------------------------------------------------------------------
// Source file: GUIWorkerImpl.java
//
// PACKAGE: com.cboe.presentation.threading;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.threading;

public class GUIWorkerImpl extends AbstractGUIWorker
{
    public GUIWorkerImpl()
    {
        super();
    }

    public GUIWorkerImpl(Object sharedLockReference)
    {
        super(sharedLockReference);
    }

    public void processData()
    {}

    public void execute() throws Exception
    {}
}