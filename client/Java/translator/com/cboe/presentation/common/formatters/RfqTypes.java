/********************************************************************************
 * FILE:    RfqTypes.java
 *
 * PACKAGE: com.cboe.presentation.common.types.constants
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.RFQTypes;


/********************************************************************************
 * Represents RFQ Types
 *
 * @see com.cboe.idl.cmiConstants.RFQTypes
 */
public class RfqTypes
{

//*** Public Attributes

    // RFQ Types (mapping to com.cboe.idl.cmiConstants.RFQTypes)
    public static final short MANUAL = com.cboe.idl.cmiConstants.RFQTypes.MANUAL;
    public static final short SYSTEM = com.cboe.idl.cmiConstants.RFQTypes.SYSTEM;

    // Format constants
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";


//*** Private Attributes

    private static final String INVALID_FORMAT = "ERROR: Invalid Format Specifier";
    private static final String INVALID_TYPE   = "ERROR: Invalid RFQ Type Code";
    private static final String MANUAL_STRING  = "Manual";
    private static final String SYSTEM_STRING  = "System";


//*** Public Methods

    /*****************************************************************************
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param rfqType - the rfqType code to render (see defined constants)
     * @return a string representation of the rfqType
     * @see com.cboe.idl.cmiConstants.RFQTypes
     */
    public static String toString( short rfqType )
    {
        return toString( rfqType, TRADERS_FORMAT );
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param rfqType - the rfqType code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the rfqType
     * @see com.cboe.idl.cmiConstants.RFQTypes
     */
    public static String toString( short rfqType, String formatSpecifier )
    {
        if( formatSpecifier.equals( TRADERS_FORMAT ))
        {
            switch( rfqType )
            {
                case MANUAL:
                    return MANUAL_STRING;
                case SYSTEM:
                    return SYSTEM_STRING;
                default:
                    return new StringBuffer(20).append(INVALID_TYPE).append("[ ").append(rfqType).append(" ]").toString();
            }
        }
        return INVALID_FORMAT;
    }


//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private RfqTypes( )
    {
    }

}
