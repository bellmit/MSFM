// -----------------------------------------------------------------------------------
// Source file: TradingSessionEventStateFormatter.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

import com.cboe.idl.constants.TradingSessionEventStates;

import com.cboe.interfaces.internalPresentation.common.formatters.TradingSessionEventStateFormatStrategy;

import com.cboe.presentation.common.formatters.Formatter;

/**
 * Formats TradingSessionEventState
 */
class TradingSessionEventStateFormatter extends Formatter implements TradingSessionEventStateFormatStrategy
{
    public static final short COMPLETED = TradingSessionEventStates.COMPLETED;
    public static final short FAILED = TradingSessionEventStates.FAILED;
    public static final short FIRED = TradingSessionEventStates.FIRED;
    public static final short UNFIRED = TradingSessionEventStates.UNFIRED;
    public static final short NOT_AVAILABLE = TradingSessionEventStates.NOT_AVAILABLE;
    public static final short TIMEOUT = TradingSessionEventStates.TIMEOUT;
    public static final short VETOED = TradingSessionEventStates.VETOED;
    public static final short TEMPORARILY_DISABLED = TradingSessionEventStates.TEMPORARILY_DISABLED;

    public final static String COMPLETED_STRING = "COMPLETED";
    public final static String FAILED_STRING = "FAILED";
    public final static String FIRED_STRING = "FIRED";
    public final static String UNFIRED_STRING = "UNFIRED";
    public final static String NOT_AVAILABLE_STRING = "NOT AVAILABLE";
    public final static String TIMEOUT_STRING = "TIMEOUT";
    public final static String VETOED_STRING = "VETOED";
    public final static String TEMPORARILY_DISABLED_STRING = "TEMPORARILY DISABLED";
    public final static String UNKNOWN_STRING = "UNKNOWN";

    /**
     * Constructor
     */
    public TradingSessionEventStateFormatter()
    {
        super();

        addStyle(FULL_EVENT_STATE_NAME, FULL_EVENT_STATE_NAME_DESCRIPTION);

        setDefaultStyle(FULL_EVENT_STATE_NAME);
    }

    /**
     * Defines a method for formatting TradingSessionEventState
     * @param eventState to format
     * @return formatted string
     */
    public String format(short eventState)
    {
        return format(eventState, getDefaultStyle());
    }

    /**
     * Defines a method for formatting TradingSessionEventState
     * @param eventState to format
     * @param style to use
     * @return formatted string
     */
    public String format(short eventState, String style)
    {
        String retVal = "";

        if(!containsStyle(style))
        {
            throw new IllegalArgumentException("TradingSessionEventStateFormatter - Unknown Style: '" + style + "'");
        }

        if(style.equals(FULL_EVENT_STATE_NAME))
        {
            switch(eventState)
            {
                case COMPLETED:
                    retVal = COMPLETED_STRING;
                    break;
                case FIRED:
                    retVal = FIRED_STRING;
                    break;
                case UNFIRED:
                    retVal = UNFIRED_STRING;
                    break;
                case FAILED:
                    retVal = FAILED_STRING;
                    break;
                case NOT_AVAILABLE:
                    retVal = NOT_AVAILABLE_STRING;
                    break;
                case TIMEOUT:
                    retVal = TIMEOUT_STRING;
                    break;
                case VETOED:
                    retVal = VETOED_STRING;
                    break;
                case TEMPORARILY_DISABLED:
                    retVal = TEMPORARILY_DISABLED_STRING;
                    break;
                default:
                    retVal = new StringBuffer(30).append(UNKNOWN_STRING).append('-').append(eventState).toString();
            }
        }

        return retVal;
    }
}