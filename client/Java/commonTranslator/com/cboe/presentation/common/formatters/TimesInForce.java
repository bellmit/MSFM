/********************************************************************************
 * FILE:    TimesInForce.java
 *
 * PACKAGE: com.cboe.presentation.common.types.constants
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.TimesInForce;


/********************************************************************************
 * Represents TimesInForce Types
 *
 * @see com.cboe.idl.cmiConstants.TimesInForce
 */
public class TimesInForce
{

//*** Public Attributes

    // TimeInForce Types (mapping to com.cboe.idl.cmiConstants.TimesInForce)
    public static final char ALL = com.cboe.idl.cmiConstants.TimesInForce.ALL;
    public static final char DAY = com.cboe.idl.cmiConstants.TimesInForce.DAY;
    public static final char GTC = com.cboe.idl.cmiConstants.TimesInForce.GTC;
    public static final char GTD = com.cboe.idl.cmiConstants.TimesInForce.GTD;

    // Format constants
    public static final String LITERAL_FORMAT = "LITERAL_FORMAT";
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String BRIEF_FORMAT   = "BRIEF_FORMAT";


//*** Private Attributes

    private static final String ALL_STRING     = "ALL";
    private static final String DAY_STRING     = "DAY";
    private static final String GTC_STRING     = "GTC";
    private static final String GTD_STRING     = "GTD";
    private static final String INVALID_FORMAT = "ERROR: Invalid Format Specifier";
    private static final String INVALID_TYPE   = "ERROR: Invalid Times in Force Type Code";

    private static final String EMPTY_STRING = "";


//*** Public Methods

    /*****************************************************************************
     * Returns a string representation of the object in LITERAL_FORMAT format
     *
     * @param timesInForceType - the timesInForceType code to render (see defined constants)
     * @return a string representation of the timesInForceType
     * @see com.cboe.idl.cmiConstants.TimesInForce
     */
    public static String toString( char timesInForceType )
    {
        return toString( timesInForceType, LITERAL_FORMAT );
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param timesInForceType - the timesInForceType code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the timesInForceType
     * @see com.cboe.idl.cmiConstants.TimesInForce
     */
    public static String toString( char timesInForceType, String formatSpecifier )
    {
        if( formatSpecifier.equals( LITERAL_FORMAT ))
        {
            switch( timesInForceType )
            {
                case ALL:
                    return String.valueOf( ALL );
                case DAY:
                    return String.valueOf( DAY );
                case GTC:
                    return String.valueOf( GTC );
                case GTD:
                    return String.valueOf( GTD );
                default:
                    return new StringBuffer(20).append(INVALID_TYPE).append("[ ").append(timesInForceType).append(" ]").toString();
            }
        }
        else if (formatSpecifier.equals(BRIEF_FORMAT) ||
                 formatSpecifier.equals(TRADERS_FORMAT))
        {
            switch( timesInForceType )
            {
                case ALL:
                    return ALL_STRING;
                case DAY:
                    return DAY_STRING;
                case GTC:
                    return GTC_STRING;
                case GTD:
                    return GTD_STRING;
                default:
                    if( formatSpecifier.equals( BRIEF_FORMAT ))
                    {
                        return EMPTY_STRING;
                    }
                    return new StringBuffer(20).append(INVALID_TYPE).append("[ ").append(timesInForceType).append(" ]").toString();
            }
        }
        else
        {
            return INVALID_FORMAT;
        }
    }


//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private TimesInForce( )
    {
    }

}
