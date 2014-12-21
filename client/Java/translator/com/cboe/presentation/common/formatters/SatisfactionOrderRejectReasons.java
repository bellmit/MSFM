/********************************************************************************
 * FILE:    SatisfactionOrderRejectReasons.java
 *
 * PACKAGE: com.cboe.presentation.common.formatters
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons;

/**
 * Represents Activity Events
 *
 * @see com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons
 */
public class SatisfactionOrderRejectReasons
{
    // Activity Events (mapping to com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons)
    public static final short   COMM_DELAYS             = com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons.COMM_DELAYS;
    public static final short   CROWD_TRADE             = com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons.CROWD_TRADE;
    public static final short   INVALID_PRODUCT_TYPE    = com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons.INVALID_PRODUCT_TYPE;
    public static final short   LATE_PRINT              = com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons.LATE_PRINT;
    public static final short   NON_BLOCK_TRADE         = com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons.NON_BLOCK_TRADE;
    public static final short   ORIGINAL_ORDER_REJECTED = com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons.ORIGINAL_ORDER_REJECTED;
    public static final short   PROCESSING_PROBLEMS     = com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons.PROCESSING_PROBLEMS;
    public static final short   TRADE_BUSTED            = com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons.TRADE_BUSTED;
    public static final short   TRADE_REJECTED          = com.cboe.idl.cmiConstants.SatisfactionOrderRejectReasons.TRADE_REJECTED;

    private static final short[] ALL_REASONS = new short[9];
    static
    {
        ALL_REASONS[0] = COMM_DELAYS;
        ALL_REASONS[1] = CROWD_TRADE;
        ALL_REASONS[2] = INVALID_PRODUCT_TYPE;
        ALL_REASONS[3] = LATE_PRINT;
        ALL_REASONS[4] = NON_BLOCK_TRADE;
        ALL_REASONS[5] = ORIGINAL_ORDER_REJECTED;
        ALL_REASONS[6] = PROCESSING_PROBLEMS;
        ALL_REASONS[7] = TRADE_BUSTED;
        ALL_REASONS[8] = TRADE_REJECTED;
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
    private SatisfactionOrderRejectReasons( )
    {
    }

    public static short[] getAllReasons()
    {
        return ALL_REASONS;
    }
}
