/********************************************************************************
 * FILE:    QueryDirections.java
 *
 * PACKAGE: com.cboe.presentation.common.types.constants
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.QueryDirections;


/********************************************************************************
 * Represents Query Directions
 *
 * @see com.cboe.idl.cmiConstants.QueryDirections
 */  
public class QueryDirections
{

//*** Public Attributes

    // Query Directions (mapping to com.cboe.idl.cmiConstants.QueryDirections)
    public static final short QUERY_BACKWARD = com.cboe.idl.cmiConstants.QueryDirections.QUERY_BACKWARD;
    public static final short QUERY_FORWARD  = com.cboe.idl.cmiConstants.QueryDirections.QUERY_FORWARD;

    // Format constants
    public static final String LITERAL_FORMAT = "LITERAL_FORMAT";


//*** Private Attributes

    private static final String INVALID_FORMAT        = "ERROR: Invalid Format Specifier";
    private static final String INVALID_TYPE          = "ERROR: Invalid Query Direction Type Code";
    private static final String QUERY_BACKWARD_STRING = "Query Forward";
    private static final String QUERY_FORWARD_STRING  = "Query Backward";


//*** Public Methods

    /*****************************************************************************
     * Returns a string representation of the object in LITERAL_FORMAT format
     *
     * @param queryDirection - the QueryDirections code to render (see defined constants)
     * @return a string representation of the queryDirection
     * @see com.cboe.idl.cmiConstants.QueryDirections
     */  
    public static String toString( short queryDirection )
    {
        return toString( queryDirection, LITERAL_FORMAT );
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param queryDirection  - the QueryDirections code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the queryDirection
     * @see com.cboe.idl.cmiConstants.QueryDirections
     */  
    public static String toString( short queryDirection, String formatSpecifier )
    {
        if( formatSpecifier.equals( LITERAL_FORMAT ))
        {
            switch( queryDirection )
            {
                case QUERY_BACKWARD:
                    return QUERY_BACKWARD_STRING;
                case QUERY_FORWARD:
                    return QUERY_FORWARD_STRING;
                default:
                    return new StringBuffer(20).append(INVALID_TYPE).append(' ').append(queryDirection).toString();
            }
        }
        return INVALID_FORMAT;
    }


//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */  
    private QueryDirections( )
    {
    }

}

