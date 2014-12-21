/********************************************************************************
 * FILE:    OrderNBBOProtectionTypes.java
 *
 * PACKAGE: com.cboe.presentation.common.formatters
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.OrderStates;

/**
 * Represents Order NBBO Protection Types
 *
 * @see com.cboe.idl.cmiConstants.OrderNBBOProtectionTypes
 */
public class OrderNBBOProtectionTypes
{
    // mapping to com.cboe.idl.cmiConstants.OrderNBBOProtectionTypes
    public static final short NONE = com.cboe.idl.cmiConstants.OrderNBBOProtectionTypes.NONE;
    public static final short FULL = com.cboe.idl.cmiConstants.OrderNBBOProtectionTypes.FULL;

    // Format constants
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";

    private static final String NONE_STRING = "None";
    private static final String FULL_STRING = "Full";

    /**
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param nbboOrderProtectionType - the order nbbo protection type code to render (see defined constants)
     * @return a string representation of the order nbbo protection type
     * @see com.cboe.idl.cmiConstants.OrderNBBOProtectionTypes
     */
    public static String toString(short orderNbboProtectionType)
    {
        return toString(orderNbboProtectionType, TRADERS_FORMAT);
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param orderNbboProtectionType - the order state code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the orderNbboProtectionType
     * @see com.cboe.idl.cmiConstants.OrderStates
     */
    public static String toString(short orderNbboProtectionType, String formatSpecifier)
    {
        if (formatSpecifier.equals(TRADERS_FORMAT))
        {
            switch (orderNbboProtectionType)
            {
                case FULL:
                    return FULL_STRING;
                case NONE:
                    return NONE_STRING;
                default:
                    return new StringBuffer(6).append("[ ").append(orderNbboProtectionType).append(" ]").toString();
            }
        }
        return INVALID_FORMAT;
    }


    /**
     * Hide the default constructor from the public interface
     */
    private OrderNBBOProtectionTypes()
    {
    }

}