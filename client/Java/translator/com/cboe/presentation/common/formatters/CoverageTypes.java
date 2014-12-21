package com.cboe.presentation.common.formatters;

/**
 * Title:        CoverageTypes
 * Description:  Describes the coverage types
 * Company:      The Chicago Board Options Exchange
 * Copyright:    Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
 * @author Luis Torres
 * @version 1.0
 */

public class CoverageTypes 
{
    public static final char COVERED     = com.cboe.idl.cmiConstants.CoverageTypes.COVERED;
    public static final char UNCOVERED   = com.cboe.idl.cmiConstants.CoverageTypes.UNCOVERED;
    public static final char UNSPECIFIED = com.cboe.idl.cmiConstants.CoverageTypes.UNSPECIFIED;

    private static final String COVERED_STRING     = "Covered";
    private static final String UNCOVERED_STRING   = "Uncovered";
    private static final String UNSPECIFIED_STRING = "Unspecified";
    private static final String EMPTY_STRING       = "";

    public static final String TRADERS_FORMAT = new String( "TRADERS_FORMAT" );

    /*****************************************************************************
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param type - the coverage type code to render (see defined constants)
     * @return a string representation of the coverage types
     * @see com.cboe.idl.cmiConstants.CoverageTypes
     */
    public static String toString( char type )
    {
        return toString( type, TRADERS_FORMAT );
    }

    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param role - the coverage type code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the product type
     * @see com.cboe.idl.cmiConstants.CoverageTypes
     */
    public static String toString( char role, String formatSpecifier )
    {
        if( formatSpecifier.equals( TRADERS_FORMAT ))
        {
            switch (role)
            {
                case COVERED: 
                    return COVERED_STRING ;
                case UNCOVERED: 
                    return UNCOVERED_STRING ;
                case UNSPECIFIED: 
                    return UNSPECIFIED_STRING ;
                default:
                    return EMPTY_STRING;
            }
        }
        return EMPTY_STRING;
    }

    /**
     * Hide the default constructor from the public interface
     */
    private CoverageTypes ()
    {
    }
    
}// CoverageTypes
