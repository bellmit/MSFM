//
// -----------------------------------------------------------------------------------
// Source file: ActionThreaderFactory.java
//
// PACKAGE: com.cboe.presentation.common.threading;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.threading;

import com.cboe.interfaces.presentation.common.threading.ActionThreader;
import com.cboe.interfaces.presentation.common.threading.ActionThreaderPool;

public class ActionThreaderFactory
{
    private static ActionThreaderPool actionThreaderPool;

    private static synchronized ActionThreaderPool getActionThreaderPool()
    {
        if(actionThreaderPool == null)
        {
            actionThreaderPool = new ActionThreaderPoolImpl();
        }
        return actionThreaderPool;
    }

    public static ActionThreaderPool find()
    {
        return getActionThreaderPool();
    }

    public static ActionThreader getNextAvailableActionThreader()
    {
        return find().getNextAvailableActionThreader();
    }

    public static ActionThreader getNextAvailableActionThreader(Object sharedReference)
    {
        return find().getNextAvailableActionThreader(sharedReference);
    }
}