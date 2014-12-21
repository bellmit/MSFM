/********************************************************************************
 * FILE:    OrderStates.java
 *
 * PACKAGE: com.cboe.presentation.common.types.constants
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

import com.cboe.domain.util.OrderStatesExtEnum;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.OrderStates;


/********************************************************************************
 * Represents Order States
 *
 * @see com.cboe.idl.cmiConstants.OrderStates
 */
public class OrderStates
{

//*** Public Attributes

    // Order States (mapping to com.cboe.idl.cmiConstants.OrderStates)
    public static final short ACTIVE      = com.cboe.idl.cmiConstants.OrderStates.ACTIVE;
    public static final short BOOKED      = com.cboe.idl.cmiConstants.OrderStates.BOOKED;
    public static final short CANCEL      = com.cboe.idl.cmiConstants.OrderStates.CANCEL;
    public static final short EXPIRED     = com.cboe.idl.cmiConstants.OrderStates.EXPIRED;
    public static final short FILL        = com.cboe.idl.cmiConstants.OrderStates.FILL;
    public static final short INACTIVE    = com.cboe.idl.cmiConstants.OrderStates.INACTIVE;
    public static final short OPEN_OUTCRY = com.cboe.idl.cmiConstants.OrderStates.OPEN_OUTCRY;
    public static final short PURGED      = com.cboe.idl.cmiConstants.OrderStates.PURGED;
    public static final short REMOVED     = com.cboe.idl.cmiConstants.OrderStates.REMOVED;
    public static final short WAITING     = com.cboe.idl.cmiConstants.OrderStates.WAITING;

    // Internal Order States (mapping to com.cboe.idl.constants.OrderStatesExtOperations)
    public static final short ROUTED_TO_CROWD = com.cboe.idl.constants.OrderStatesExtOperations.ROUTED_TO_CROWD;
    public static final short ROUTED_TO_BOOTH = com.cboe.idl.constants.OrderStatesExtOperations.ROUTED_TO_BOOTH;
    public static final short ROUTED_TO_HELP_DESK = com.cboe.idl.constants.OrderStatesExtOperations.ROUTED_TO_HELP_DESK;
    public static final short NOT_ROUTED = com.cboe.idl.constants.OrderStatesExtOperations.NOT_ROUTED;

    // Format constants
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";


//*** Private Attributes

    private static final String ACTIVE_STRING      = "Active";
    private static final String BOOKED_STRING      = "Booked";
    private static final String CANCEL_STRING      = "Cancel";
    private static final String EXPIRED_STRING     = "Expired";
    private static final String FILL_STRING        = "Fill";
    private static final String INACTIVE_STRING    = "Inactive";
    private static final String INVALID_FORMAT     = "ERROR: Invalid Format Specifier";
    private static final String INVALID_ORDER_STATE_TYPE = "ERROR: Invalid Order State Type Code";
    private static final String OPEN_OUTCRY_STRING = "Open Outcry";
    private static final String PURGED_STRING      = "Purged";
    private static final String REMOVED_STRING     = "Removed";
    private static final String WAITING_STRING     = "Waiting";
    private static final String ROUTED_TO_CROWD_STRING = OrderStatesExtEnum.PAR.getDescription();
    private static final String ROUTED_TO_BOOTH_STRING = OrderStatesExtEnum.BOOTH.getDescription();
    private static final String ROUTED_TO_HELP_DESK_STRING = OrderStatesExtEnum.HELP_DESK.getDescription();
    private static final String NOT_ROUTED_STRING = OrderStatesExtEnum.OHS.getDescription();

//*** Public Methods

    /*****************************************************************************
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param orderState - the order state code to render (see defined constants)
     * @return a string representation of the orderState
     * @see com.cboe.idl.cmiConstants.OrderStates
     */
    public static String toString( short orderState )
    {
        return toString( orderState, TRADERS_FORMAT );
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param orderState - the order state code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the orderState
     * @see com.cboe.idl.cmiConstants.OrderStates
     */
    public static String toString( short orderState, String formatSpecifier )
    {
        if( formatSpecifier.equals( TRADERS_FORMAT ))
        {
            switch( orderState )
            {
                case BOOKED:
                    return BOOKED_STRING;
                case CANCEL:
                    return CANCEL_STRING;
                case FILL:
                    return FILL_STRING;
                case OPEN_OUTCRY:
                    return OPEN_OUTCRY_STRING;
                case INACTIVE:
                    return INACTIVE_STRING;
                case ACTIVE:
                    return ACTIVE_STRING;
                case EXPIRED:
                    return EXPIRED_STRING;
                case PURGED:
                    return PURGED_STRING;
                case REMOVED:
                    return REMOVED_STRING;
                case WAITING:
                    return WAITING_STRING;
                case ROUTED_TO_CROWD:
                    return ROUTED_TO_CROWD_STRING;
                case ROUTED_TO_BOOTH:
                    return ROUTED_TO_BOOTH_STRING;
                case ROUTED_TO_HELP_DESK:
                    return ROUTED_TO_HELP_DESK_STRING;
                case NOT_ROUTED:
                    return NOT_ROUTED_STRING;
                default:
                    return new StringBuffer(20).append(INVALID_ORDER_STATE_TYPE).append ("[ ").append(orderState).append(" ]").toString();
            }
        }
        return INVALID_FORMAT;
    }


//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private OrderStates( )
    {
    }

}
