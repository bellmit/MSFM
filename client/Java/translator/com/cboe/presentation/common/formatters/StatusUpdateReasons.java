package com.cboe.presentation.common.formatters;

/**
 * Title:        StatusUpdateReasons
 * Description:  Describes the status change/update reasons
 * Company:      The Chicago Board Options Exchange
 * Copyright:    Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
 * @author Luis Torres
 * @version 2.0
 */

public class StatusUpdateReasons
{
    // Status Update Reasons (mapping to com.cboe.idl.cmiConstants.StatusUpdateReasons)

    public static final short BOOKED = com.cboe.idl.cmiConstants.StatusUpdateReasons.BOOKED;
    public static final short BUST = com.cboe.idl.cmiConstants.StatusUpdateReasons.BUST;
    public static final short CANCEL = com.cboe.idl.cmiConstants.StatusUpdateReasons.CANCEL;
    public static final short FILL = com.cboe.idl.cmiConstants.StatusUpdateReasons.FILL;
    public static final short NEW = com.cboe.idl.cmiConstants.StatusUpdateReasons.NEW;
    public static final short OPEN_OUTCRY = com.cboe.idl.cmiConstants.StatusUpdateReasons.OPEN_OUTCRY;
    public static final short QUERY = com.cboe.idl.cmiConstants.StatusUpdateReasons.QUERY;
    public static final short REINSTATE = com.cboe.idl.cmiConstants.StatusUpdateReasons.REINSTATE;
    public static final short UPDATE = com.cboe.idl.cmiConstants.StatusUpdateReasons.UPDATE;
    public static final short QUOTE_TRIGGER_BUY = com.cboe.idl.cmiConstants.StatusUpdateReasons.QUOTE_TRIGGER_BUY;
    public static final short QUOTE_TRIGGER_SELL = com.cboe.idl.cmiConstants.StatusUpdateReasons.QUOTE_TRIGGER_SELL;


    private static final String BOOKED_STRING          = "BOOKED";
    private static final String BUST_STRING            = "BUSTED";
    private static final String CANCEL_STRING          = "CANCELLED";
    private static final String FILL_STRING            = "FILLED";
    private static final String NEW_STRING             = "NEW";
    private static final String OPEN_OUTCRY_STRING     = "OPEN OUTCRY";
    private static final String QUERY_STRING           = "QUERIED";
    private static final String REINSTATE_STRING       = "REINSTATED";
    private static final String UPDATE_STRING          = "UPDATED";
    private static final String QUOTE_TRIGGER_BUY_STRING   = "BUY TRIGGERED";
    private static final String QUOTE_TRIGGER_SELL_STRING   = "SELL TRIGGERED";

    private static final String EMPTY_STRING = new String( "" );

    // Format constants
    public static final String TRADERS_FORMAT = new String( "TRADERS_FORMAT" );
    public static final String INVALID_FORMAT = new String( "ERROR: Invalid Format" );

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
                case BOOKED:
                    return BOOKED_STRING;
                case BUST:
                    return BUST_STRING;
                case CANCEL:
                    return CANCEL_STRING;
                case FILL:
                    return FILL_STRING;
                case NEW:
                    return NEW_STRING;
                case OPEN_OUTCRY:
                    return OPEN_OUTCRY_STRING;
                case QUERY:
                    return QUERY_STRING;
                case REINSTATE:
                    return REINSTATE_STRING;
                case UPDATE:
                    return UPDATE_STRING;
                case QUOTE_TRIGGER_BUY:
                    return QUOTE_TRIGGER_BUY_STRING;
                case QUOTE_TRIGGER_SELL:
                    return QUOTE_TRIGGER_SELL_STRING;
                default:
                    return EMPTY_STRING;
            }
        }
        else
        {
            return INVALID_FORMAT;
        } // end of else
    }

    /**
     * Hide the default constructor from the public interface
     */
    private StatusUpdateReasons()
    {
    }
}
