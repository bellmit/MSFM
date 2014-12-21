package com.cboe.presentation.common.formatters;

/**
 * Title:        Sources
 * Description:  Describes the order sources
 * Company:      The Chicago Board Options Exchange
 * Copyright:    Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
 * @author Luis Torres
 * @version 1.0
 */

public class Sources
{
    public static final char SBT = com.cboe.idl.cmiConstants.Sources.SBT;
    public static final char TPF = com.cboe.idl.cmiConstants.Sources.TPF;
    public static final char COMPASS = com.cboe.idl.cmiConstants.Sources.COMPASS;
    public static final char LINKAGE = com.cboe.idl.cmiConstants.Sources.LINKAGE;
    public static final char LIGHT = com.cboe.idl.cmiConstants.Sources.LIGHT;

    private static final String SBT_STRING = "SBT";
    private static final String TPF_STRING = "TPF";
    private static final String COMPASS_STRING = "COMPASS";
    private static final String LINKAGE_STRING = "LINKAGE";
    private static final String LIGHT_STRING = "LIGHT";
    private static final String EMPTY_STRING = "";

    public static final String TRADERS_FORMAT = new String( "TRADERS_FORMAT" );

    /*****************************************************************************
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param source - the sources code to render (see defined constants)
     * @return a string representation of the source
     * @see com.cboe.idl.cmiConstants.Sources
     */
    public static String toString( char source )
    {
        return toString( source, TRADERS_FORMAT );
    }

    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param source - the source code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the source
     * @see com.cboe.idl.cmiConstants.Sources
     */
    public static String toString( char source, String formatSpecifier )
    {
        if( formatSpecifier.equals( TRADERS_FORMAT ))
        {
            switch (source)
            {
                case SBT:
                    return SBT_STRING;
                case TPF:
                    return TPF_STRING;
                case COMPASS:
                    return COMPASS_STRING;
                case LINKAGE:
                    return LINKAGE_STRING;
                case LIGHT:
                    return LIGHT_STRING;
                default :
                    return EMPTY_STRING;
            }
        }
        return EMPTY_STRING;
    }

    /**
     * Hide the default constructor from the public interface
     */
    public Sources ()
    {
    }

}// Sources
