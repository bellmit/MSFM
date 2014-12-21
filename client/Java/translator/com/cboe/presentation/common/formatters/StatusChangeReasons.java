package com.cboe.presentation.common.formatters;

/**
 * Title:        StatusChangeReasons
 * Description:  Describes the status change/update reasons
 * Copyright:    Copyright (c) 2000,2001
 * Company:      Chicago Board Options Exchange
 * @author Luis Torres
 * @version 1.0
 */

public class StatusChangeReasons
{
    // Status Update Reasons (mapping to com.cboe.idl.cmiConstants.StatusUpdateReasons)
    public static final short POSSIBLE_RESEND = com.cboe.idl.cmiConstants.StatusUpdateReasons.POSSIBLE_RESEND;

    private static final String POSSIBLE_RESEND_STRING = "POSS RESEND";

    private static final String EMPTY_STRING = "";

    // Format constants
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";


    /*****************************************************************************
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param orderState - the order state code to render (see defined constants)
     * @return a string representation of the orderState
     * @see com.cboe.idl.cmiConstants.OrderStates
     */
    public static String toString( short status )
    {
        return toString( status, TRADERS_FORMAT );
    }

    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param status - the status change code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the status change
     * @see com.cboe.idl.cmiConstants.OrderStates
     */
    public static String toString( short status, String formatSpecifier )
    {
        if( formatSpecifier.equals( TRADERS_FORMAT ))
        {
            switch( status )
            {
                case POSSIBLE_RESEND:
                    return POSSIBLE_RESEND_STRING;
                default:
                    return EMPTY_STRING;
            }
        }
        return EMPTY_STRING;
    }

    /**
     * Hide the default constructor from the public interface
     */
    private StatusChangeReasons()
    {
    }
}