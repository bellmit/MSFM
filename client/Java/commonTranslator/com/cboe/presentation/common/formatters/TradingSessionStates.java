package com.cboe.presentation.common.formatters;

/**
 * Title:        TradingSessionStates
 * Description:  Describes the trading session states
 * Company:      The Chicago Board Options Exchange
 * Copyright:    Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
 * @author Luis Torres
 * @version 1.0
 */
public class TradingSessionStates
{
    public final static short CLOSED          = com.cboe.idl.cmiConstants.TradingSessionStates.CLOSED;
    public final static short OPEN            = com.cboe.idl.cmiConstants.TradingSessionStates.OPEN;

    private final static String CLOSED_STRING = "Closed";
    private final static String OPEN_STRING   = "Open";

    private final static String TRADERS_FORMAT = "TRADERS_FORMAT";
    private final static String INVALID_FORMAT = "Error: Invalid format specifier ";
    
    public static String toString(short state)
    {
        return toString(state, TRADERS_FORMAT);
    }
    
    public static String toString(short state, String formatSpecifier)
    {
        if ( formatSpecifier.equals(TRADERS_FORMAT) ) 
        {
            switch ( state )
            {
                case CLOSED:
                    return CLOSED_STRING;
                case OPEN:
                    return OPEN_STRING;
                default:
                    return Short.toString(state);
            }
        } 
        return INVALID_FORMAT;
    }
    
    private TradingSessionStates()
    {
    }
}
