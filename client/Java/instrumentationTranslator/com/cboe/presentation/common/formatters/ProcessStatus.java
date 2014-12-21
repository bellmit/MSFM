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

public class ProcessStatus
{
    public static final short NOT_REPORTED       = 0;
    public static final short INIT               = Status.INIT;
    public static final short UP                 = Status.UP;
    public static final short DOWN               = Status.DOWN;
    public static final short THREAD_RUNNING     = Status.THREAD_RUNNING;
    public static final short THREAD_EXITED      = Status.THREAD_EXITED;
    public static final short THREAD_NOT_STARTED = Status.THREAD_NOT_STARTED;
    public static final short THREAD_WAITING     = Status.THREAD_WAITING;
    public static final short UNKNOWN            = Status.UNKNOWN;
    public static final short NO_RESPONSE        = Status.NO_RESPONSE;
    public static final short MASTER             = Status.MASTER;
    public static final short SLAVE              = Status.SLAVE;
    public static final short GREEN              = 100;
    public static final short YELLOW             = GREEN + 1;
    public static final short RED                = YELLOW + 1;

    public static final String  NOT_REPORTED_STRING       = "";
    public static final String  INIT_STRING               = "Init";
    public static final String  UP_STRING                 = "Up";
    public static final String  DOWN_STRING               = "Down";
    public static final String  THREAD_RUNNING_STRING     = "Thread Running";
    public static final String  THREAD_EXITED_STRING      = "Thread Exited";
    public static final String  THREAD_NOT_STARTED_STRING = "Thread Not Started";
    public static final String  THREAD_WAITING_STRING     = "Thread Waiting";
    public static final String  UNKNOWN_STRING            = "Unknown";
    public static final String  NO_RESPONSE_STRING        = "No Response";
    public static final String  MASTER_STRING             = "MASTER";
    public static final String  SLAVE_STRING              = "SLAVE";
    public static final String  GREEN_STRING              = "";
    public static final String  YELLOW_STRING             = "";
    public static final String  RED_STRING                = "";
    public static final String  INIT_SHORT_STRING               = "I";
    public static final String  UP_SHORT_STRING                 = "U";
    public static final String  DOWN_SHORT_STRING               = "D";
    public static final String  THREAD_RUNNING_SHORT_STRING     = "TR";
    public static final String  THREAD_EXITED_SHORT_STRING      = "TE";
    public static final String  THREAD_NOT_STARTED_SHORT_STRING = "TN";
    public static final String  THREAD_WAITING_SHORT_STRING     = "TW";
    public static final String  UNKNOWN_SHORT_STRING            = "?";
    public static final String  NO_RESPONSE_SHORT_STRING        = "NR";
    public static final String  MASTER_SHORT_STRING             = "M";
    public static final String  SLAVE_SHORT_STRING              = "S";

    public static final String  TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String  SHORT_FORMAT   = "SHORT_FORMAT";
    public static final String  INVALID_FORMAT = "INVALID_FORMAT";
    public static final String  INVALID_TYPE   = "UNKNOWN";

    private static final Color SLAVE_COLOR = new Color(102, 204, 255);

    private ProcessStatus()
    {
    }
    public static String toString(short status)
    {
        return toString(status, TRADERS_FORMAT);
    }
    public static String toString(short status, String format)
    {
        if(format.equals(TRADERS_FORMAT))
        {
            switch(status)
            {
                case NOT_REPORTED:
                    return NOT_REPORTED_STRING;
                case INIT:
                    return INIT_STRING;
                case UP:
                    return UP_STRING;
                case DOWN:
                    return DOWN_STRING;
                case THREAD_RUNNING:
                    return THREAD_RUNNING_STRING;
                case THREAD_EXITED:
                    return THREAD_EXITED_STRING;
                case THREAD_NOT_STARTED:
                    return THREAD_NOT_STARTED_STRING;
                case THREAD_WAITING:
                    return THREAD_WAITING_STRING;
                case UNKNOWN:
                    return UNKNOWN_STRING;
                case NO_RESPONSE:
                    return NO_RESPONSE_STRING;
                case MASTER:
                    return MASTER_STRING;
                case SLAVE:
                    return SLAVE_STRING;
                case GREEN:
                    return GREEN_STRING;
                case YELLOW:
                    return YELLOW_STRING;
                case RED:
                    return RED_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(" ").append(status).toString();
            }
        }
        else if(format.equals(SHORT_FORMAT))
        {
            switch(status)
            {
                case NOT_REPORTED:
                    return NOT_REPORTED_STRING;
                case INIT:
                    return INIT_SHORT_STRING;
                case UP:
                    return UP_SHORT_STRING;
                case DOWN:
                    return DOWN_SHORT_STRING;
                case THREAD_RUNNING:
                    return THREAD_RUNNING_SHORT_STRING;
                case THREAD_EXITED:
                    return THREAD_EXITED_SHORT_STRING;
                case THREAD_NOT_STARTED:
                    return THREAD_NOT_STARTED_SHORT_STRING;
                case THREAD_WAITING:
                    return THREAD_WAITING_SHORT_STRING;
                case UNKNOWN:
                    return UNKNOWN_SHORT_STRING;
                case NO_RESPONSE:
                    return NO_RESPONSE_SHORT_STRING;
                case MASTER:
                    return MASTER_SHORT_STRING;
                case SLAVE:
                    return SLAVE_SHORT_STRING;
                case GREEN:
                    return GREEN_STRING;
                case YELLOW:
                    return YELLOW_STRING;
                case RED:
                    return RED_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(" ").append(status).toString();
            }
        }
        else
        {
            return new StringBuffer(30).append(INVALID_FORMAT).append(" ").append(format).toString();
        }
    }

    public static Color getColorForStatus(short status)
    {
        Color statusColor = null;
        switch (status)
        {
            case ProcessStatus.NOT_REPORTED:
                break;
            case ProcessStatus.UP:
            case ProcessStatus.INIT:
            case ProcessStatus.THREAD_RUNNING:
            case ProcessStatus.THREAD_WAITING:
            case ProcessStatus.MASTER:
            case ProcessStatus.GREEN:
                statusColor = Color.green;
                break;
            case ProcessStatus.SLAVE:
                statusColor = SLAVE_COLOR;
                break;
            case ProcessStatus.DOWN:
            case ProcessStatus.THREAD_EXITED:
            case ProcessStatus.UNKNOWN:
            case ProcessStatus.NO_RESPONSE:
            case ProcessStatus.RED:
                statusColor = Color.red;
                break;
            case ProcessStatus.THREAD_NOT_STARTED:
            case ProcessStatus.YELLOW:
                statusColor = Color.yellow;
                break;
            default:
                statusColor = Color.orange;
                break;
        }
        return statusColor;

    }


}
