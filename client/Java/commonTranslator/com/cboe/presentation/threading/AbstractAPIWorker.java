//
// -----------------------------------------------------------------------------------
// Source file: AbstractAPIWorker.java
//
// PACKAGE: com.cboe.presentation.threading;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.threading;

import java.awt.event.*;
import com.cboe.interfaces.presentation.threading.APIWorker;
import com.cboe.interfaces.presentation.threading.GUIWorker;

public abstract class AbstractAPIWorker implements APIWorker
{
    protected GUIWorker guiWorker;

    protected AbstractAPIWorker()
    {
        super();
        guiWorker = null;
    }

    public abstract void process();

    public AbstractAPIWorker(GUIWorker guiWorker)
    {
        this();
        this.guiWorker = guiWorker;
    }

    public void actionPerformed(ActionEvent event)
    {
        process();
    }

    public GUIWorker getWorker()
    {
        return guiWorker;
    }
}