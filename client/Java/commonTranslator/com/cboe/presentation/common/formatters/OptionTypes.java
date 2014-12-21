package com.cboe.presentation.common.formatters;

/**
 * Title:        OptionTypes
 * Description:  Describes the option types
 * Company:      The Chicago Board Options Exchange
 * Copyright:    Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
 * @author Luis Torres
 * @version 1.0
 */

public class OptionTypes
{
    public static final char CALL        = com.cboe.idl.cmiConstants.OptionTypes.CALL;
    public static final char PUT         = com.cboe.idl.cmiConstants.OptionTypes.PUT;

    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE   = "INVALID_TYPE";

    private static final String CALL_STRING        = "Call";
    private static final String PUT_STRING         = "Put";

    public static String toString(char optionType)
    {
        return toString(optionType, TRADERS_FORMAT);
    }

    public static String toString(char optionType, String formatSpecifier)
    {
        if (formatSpecifier.equals(TRADERS_FORMAT))
        {
            switch (optionType)
            {
                case CALL :
                    return CALL_STRING;
                case PUT :
                    return PUT_STRING;
                default :
                    return new StringBuffer().append("[ ").append(optionType).append(" ]").toString();
            }
        }
        return INVALID_FORMAT;
    }
    private OptionTypes ()
    {
    }
}
