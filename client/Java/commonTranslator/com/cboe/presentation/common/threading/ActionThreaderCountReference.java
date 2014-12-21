//
// -----------------------------------------------------------------------------------
// Source file: ActionThreadCountReference.java
//
// PACKAGE: com.cboe.presentation.common.threading
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.threading;

import com.cboe.interfaces.presentation.common.threading.ActionThreader;

public class ActionThreaderCountReference
{
    private ActionThreader actionThreader;
    private int counter;

    public ActionThreaderCountReference(ActionThreader actionThreader)
    {
        super();
        if(actionThreader == null)
        {
            throw new IllegalArgumentException("ActionThreader may not be null.");
        }
        this.actionThreader = actionThreader;
        counter = 0;
    }

    public ActionThreader getActionThreader()
    {
        return actionThreader;
    }

    public int increaseCounter()
    {
        return ++counter;
    }

    public int decreaseCounter()
    {
        return --counter;
    }

    public int getCounter()
    {
        return counter;
    }
}