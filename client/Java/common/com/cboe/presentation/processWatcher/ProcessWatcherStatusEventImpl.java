//
// -----------------------------------------------------------------------------------
// Source file: ProcessWatcherStatusEventImpl
//
// PACKAGE: com.cboe.presentation.processWatcher
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.processWatcher;

import java.util.*;

import com.cboe.interfaces.presentation.processWatcher.ProcessWatcherStatusEvent;

public class ProcessWatcherStatusEventImpl extends EventObject
        implements ProcessWatcherStatusEvent
{
    private int eventType;
    private String orbName;
    private Date time;            // time the event was received from the processWatcher
    private short state;
    private String eventOriginator;
    private short reasonCode;

    public ProcessWatcherStatusEventImpl(Object source, int eventType, String orbName, Date time, short state,
                                         String eventOriginator, short reasonCode)
    {
        super(source);
        this.eventType = eventType;
        this.orbName = orbName;
        this.time = time;
        this.state = state;
        this.eventOriginator = eventOriginator;
        this.reasonCode = reasonCode;
    }

    public String getOrbName()
    {
        return orbName;
    }

    public int getType()
    {
        return eventType;
    }

    public Date getTime()
    {
        return time;
    }

    public short getState()
    {
        return state;
    }

    public String getEventOriginator()
    {
        return eventOriginator;
    }

    public short getReasonCode()
    {
        return reasonCode;
    }
}