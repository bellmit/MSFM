package com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Source file: ProcessStatus.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import java.awt.Color;
import com.cboe.interfaces.instrumentation.Status;
import com.cboe.interfaces.events.ProcessWatcherStatus;

public class ProcessWatcherState
{
    public static final int DOWN = ProcessWatcherStatus.PROCESS_DOWN;
    public static final int UP = ProcessWatcherStatus.PROCESS_UP;
    public static final int SLAVE = ProcessWatcherStatus.POA_DOWN;
    public static final int MASTER = ProcessWatcherStatus.POA_UP;

    public static final String UP_STRING = "UP";
    public static final String DOWN_STRING = "DOWN";
    public static final String MASTER_STRING = "MASTER";
    public static final String SLAVE_STRING = "SLAVE";
    public static final String UNKNOWN_STRING = "Unknown";
    public static final String UP_SHORT_STRING = "U";
    public static final String DOWN_SHORT_STRING = "D";
    public static final String MASTER_SHORT_STRING = "M";
    public static final String SLAVE_SHORT_STRING = "S";
    public static final String UNKNOWN_SHORT_STRING = "?";
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String SHORT_FORMAT = "SHORT_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE = "UNKNOWN";

    private ProcessWatcherState()
    {
    }
    public static String toString(int status)
    {
        return toString(status, TRADERS_FORMAT);
    }
    public static String toString(int status, String format)
    {
        if(format.equals(TRADERS_FORMAT))
        {
            switch(status)
            {
                case UP:
                    return UP_STRING;
                case DOWN:
                    return DOWN_STRING;
                case MASTER:
                    return MASTER_STRING;
                case SLAVE:
                    return SLAVE_STRING;
                default:
                    return UNKNOWN_STRING;
            }
        }
        else if(format.equals(SHORT_FORMAT))
        {
            switch(status)
            {
                case UP:
                    return UP_SHORT_STRING;
                case DOWN:
                    return DOWN_SHORT_STRING;
                case MASTER:
                    return MASTER_SHORT_STRING;
                case SLAVE:
                    return SLAVE_SHORT_STRING;
                default:
                    return UNKNOWN_SHORT_STRING;
            }
        }
        else
        {
            return UNKNOWN_SHORT_STRING;
        }
    }

}
