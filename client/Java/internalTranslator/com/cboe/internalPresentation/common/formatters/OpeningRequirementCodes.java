/**
 * Copyright 2003(C) Chicago Board Options Exchange
 * Package: com.cboe.internalPresentation.common.formatters
 */
package com.cboe.internalPresentation.common.formatters;

public class OpeningRequirementCodes
{
    public static final short   REQUIREMENTS_ON  = com.cboe.idl.constants.OpeningRequirementCodes.REQUIREMENTS_ON;
    public static final short   REQUIREMENTS_OFF  = com.cboe.idl.constants.OpeningRequirementCodes.REQUIREMENTS_OFF;
    public static final short   UNSUPPORTED       = com.cboe.idl.constants.OpeningRequirementCodes.UNSUPPORTED;
    public static final short   UNAVAILABLE       = REQUIREMENTS_ON + 1;

    public static final String  REQUIREMENTS_ON_STRING  = "Opening Requirements Enabled";
    public static final String  REQUIREMENTS_OFF_STRING  = "No Requirements to Open";
    public static final String  REQUIREMENTS_UNAVAILABLE_STRING  = "Opening Requirements Unavailable";

    public static final String  TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String  INVALID_FORMAT = "INVALID_FORMAT";
    public static final String  INVALID_TYPE   = "INVALID_TYPE";

    private OpeningRequirementCodes()
    {
    }
    public static String toString(short openingRequirementCode)
    {
        return toString(openingRequirementCode, TRADERS_FORMAT);
    }
    public static String toString(short openingRequirementCode, String format)
    {
        if(format.equals(TRADERS_FORMAT))
        {
            switch(openingRequirementCode)
            {
                case REQUIREMENTS_ON:
                    return REQUIREMENTS_ON_STRING;
                case REQUIREMENTS_OFF:
                    return REQUIREMENTS_OFF_STRING;
                case UNAVAILABLE:
                    return REQUIREMENTS_UNAVAILABLE_STRING;
                default:
                    return new StringBuffer(15).append(INVALID_TYPE).append(" ").append(openingRequirementCode).toString();
            }
        }
        else
        {
            return INVALID_FORMAT;
        }
    }
}
