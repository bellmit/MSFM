//
// -----------------------------------------------------------------------------------
// Source file: WatchdogStateFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.alarmConstants.NotificationWatchdogStates;

import com.cboe.interfaces.instrumentation.common.formatters.WatchdogStateFormatStrategy;

public class WatchdogStateFormatter extends Formatter implements WatchdogStateFormatStrategy
{
    private static final short[] ALL_STATES = {NotificationWatchdogStates.ACTIVE, NotificationWatchdogStates.INACTIVE};

    WatchdogStateFormatter()
    {
        addStyle(FULL_INFO_NAME, FULL_INFO_DESCRIPTION);
        addStyle(BRIEF_INFO_NAME, BRIEF_INFO_DESCRIPTION);
        setDefaultStyle(FULL_INFO_NAME);
    }

    public short[] getWatchdogStates()
    {
        return ALL_STATES;
    }

    public String format(short watchdogState)
    {
        return format(watchdogState, getDefaultStyle());
    }

    public String format(short watchdogState, String style)
    {
        String formatted;
        String name = getStateName(watchdogState);
        if(FULL_INFO_NAME.equals(style))
        {
            formatted = name;
        }
        else if(BRIEF_INFO_NAME.equals(style))
        {
            formatted = name.substring(0, 1);
        }
        else
        {
            formatted = "None";
        }
        return formatted;
    }

    private String getStateName(short watchdogState)
    {
        String name;
        switch(watchdogState)
        {
            case NotificationWatchdogStates.ACTIVE:
                name = "Active";
                break;
            case NotificationWatchdogStates.INACTIVE:
                name = "Inactive";
                break;
            case NotificationWatchdogStates.SUSPENDED:
                name = "Suspended";
                break;
            default:
                name = "None";
                break;
        }
        return name;
    }
}
