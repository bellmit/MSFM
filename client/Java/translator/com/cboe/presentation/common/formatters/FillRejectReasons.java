/********************************************************************************
 * FILE:    FillRejectReasons.java
 *
 * PACKAGE: com.cboe.presentation.common.formatters
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.FillRejectReasons;

/**
 * Represents Activity Events
 *
 * @see com.cboe.idl.cmiConstants.FillRejectReasons
 */
public class FillRejectReasons
{
    // Activity Events (mapping to com.cboe.idl.cmiConstants.FillRejectReasons)
    public static final short   INVALID_PRICE       = com.cboe.idl.cmiConstants.FillRejectReasons.INVALID_PRICE;
    public static final short   INVALID_PRODUCT     = com.cboe.idl.cmiConstants.FillRejectReasons.INVALID_PRODUCT;
    public static final short   INVALID_QUANTITY    = com.cboe.idl.cmiConstants.FillRejectReasons.INVALID_QUANTITY;
    public static final short   INVALID_SIDE        = com.cboe.idl.cmiConstants.FillRejectReasons.INVALID_SIDE;
    public static final short   NO_MATCHING_ORDER   = com.cboe.idl.cmiConstants.FillRejectReasons.NO_MATCHING_ORDER;
    public static final short   OTHER               = com.cboe.idl.cmiConstants.FillRejectReasons.OTHER;
    public static final short   STALE_EXECUTION     = com.cboe.idl.cmiConstants.FillRejectReasons.STALE_EXECUTION;

    private static final short[] ALL_REASONS = new short[7];
    static
    {
        ALL_REASONS[0] = INVALID_PRICE;
        ALL_REASONS[1] = INVALID_PRODUCT;
        ALL_REASONS[2] = INVALID_QUANTITY;
        ALL_REASONS[3] = INVALID_SIDE;
        ALL_REASONS[4] = NO_MATCHING_ORDER;
        ALL_REASONS[5] = OTHER;
        ALL_REASONS[6] = STALE_EXECUTION;
    }

    /**
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param activityEvent - the activity event code to render (see defined constants)
     * @return a string representation of the activityEvent
     * @see com.cboe.idl.cmiConstants.ActivityTypes
     */
    public static String toString( short activityEvent )
    {
        return ActivityTypes.toString(activityEvent);
    }


    /**
     * Returns a string representation of the object in the given format
     *
     * @param activityEvent - the activity event code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the activityEvent
     * @see com.cboe.idl.cmiConstants.ActivityTypes
     */
    public static String toString( short activityEvent, String formatSpecifier )
    {
        return ActivityTypes.toString(activityEvent, formatSpecifier);
    }

    /**
     * Hide the default constructor from the public interface
     */
    private FillRejectReasons( )
    {
    }

    public static short[] getAllReasons()
    {
        return ALL_REASONS;
    }
}
