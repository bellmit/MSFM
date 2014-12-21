package com.cboe.presentation.common.formatters;

/**
 * Title:        PositionEffects
 * Description:  Describes the position effects
 * Company:      Chicago Board Options Exchange
 * Copyright     Copyright (c) 2001 Chicago Board Options Exchange
 *
 * @author Luis Torres
 * @version 1.0 
 */

public class PositionEffects 
{
    public static final char CLOSED        = com.cboe.idl.cmiConstants.PositionEffects.CLOSED;
    public static final char OPEN          = com.cboe.idl.cmiConstants.PositionEffects.OPEN;
    public static final char NOTAPPLICABLE = com.cboe.idl.cmiConstants.PositionEffects.NOTAPPLICABLE;

    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE   = "INVALID_TYPE";

    private static final String CLOSED_STRING        = "Closed";
    private static final String OPEN_STRING          = "Open";
    private static final String NOTAPPLICABLE_STRING = "N/A";
    private static final String EMPTY_STRING         = "";

    public static String toString(char position) 
    {
        return toString(position, TRADERS_FORMAT);
    }
    
    public static String toString(char position, String formatSpecifier) 
    {
        if (formatSpecifier.equals(TRADERS_FORMAT)) 
        {
            switch (position)
            {
                case CLOSED :
                    return CLOSED_STRING;
                case NOTAPPLICABLE :
                    return NOTAPPLICABLE_STRING;
                case OPEN :
                    return OPEN_STRING;
                default :
                    return EMPTY_STRING;
            }
        } 
        return INVALID_FORMAT;
    }
    private PositionEffects ()
    {
    }
}
