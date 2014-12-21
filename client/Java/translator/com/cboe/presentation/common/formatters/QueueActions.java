//
// -----------------------------------------------------------------------------------
// Source file: PositionEffects.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

public class QueueActions
{
    public static final short OVERLAY    = com.cboe.idl.cmiConstants.QueueActions.OVERLAY_LAST;
    public static final short NO_ACTION  = com.cboe.idl.cmiConstants.QueueActions.NO_ACTION;
    public static final short FLUSH      = com.cboe.idl.cmiConstants.QueueActions.FLUSH_QUEUE;
    public static final short DISCONNECT = com.cboe.idl.cmiConstants.QueueActions.DISCONNECT_CONSUMER;

    private static final String OVERLAY_STRING     = "Overlayed";
    private static final String NO_ACTION_STRING   = "No Action";
    private static final String FLUSH_STRING       = "Flushed";
    private static final String DISCONNECT_STRING  = "Disconnected";
    private static final String UNKNOWN_STRING     = "Unknown";

    public static String toString(short queueAction)
    {
        switch(queueAction)
        {
            case OVERLAY:
                return OVERLAY_STRING;
            case NO_ACTION:
                return NO_ACTION_STRING;
            case FLUSH:
                return FLUSH_STRING;
            case DISCONNECT:
                return DISCONNECT_STRING;
            default:
                return UNKNOWN_STRING;
        }
    }

    private QueueActions ()
    {}
}