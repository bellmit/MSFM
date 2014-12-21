/********************************************************************************
 * FILE:    ActivitySubTypes.java
 *
 * PACKAGE: com.cboe.presentation.common.formatters
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.constants.ActivitySubTypes;


/********************************************************************************
 * Represents ActivitySub Events
 *
 * @author Eric Maheo
 * @since Dec 16, 2008
 * @see com.cboe.idl.constants.ActivitySubTypes
 */
@SuppressWarnings({"ConstantNamingConvention"})
public class ActivitySubTypes
{

//*** Public Attributes

    // ActivitySub Events (mapping to com.cboe.idl.constants.ActivitySubTypes)

    /* When adding a new sub activity type to the following list, the following files must also be updated
     * to account for the new sub activity types:
     *
     * Additionally, in this file, add a new string constant for each new sub activity type defined
     */

    //PAR
    public static final short RECEIVED              = com.cboe.idl.constants.ActivitySubTypes.RECEIVED;
    public static final short SELECTED              = com.cboe.idl.constants.ActivitySubTypes.SELECTED;
    public static final short TRADED                = com.cboe.idl.constants.ActivitySubTypes.TRADED;
    public static final short HILO                  = com.cboe.idl.constants.ActivitySubTypes.HILO;

    //
    public static final short SPLIT                 = com.cboe.idl.constants.ActivitySubTypes.SPLIT;
    public static final short DIVIDEND_CASH         = com.cboe.idl.constants.ActivitySubTypes.DIVIDEND_CASH;
    public static final short DIVIDEND_PERCENT      = com.cboe.idl.constants.ActivitySubTypes.DIVIDEND_PERCENT;
    public static final short DIVIDEND_STOCK        = com.cboe.idl.constants.ActivitySubTypes.DIVIDEND_STOCK;
    public static final short LEAP_ROLLOVER         = com.cboe.idl.constants.ActivitySubTypes.LEAP_ROLLOVER;
    public static final short MERGER                = com.cboe.idl.constants.ActivitySubTypes.MERGER;
    public static final short SYMBOL_CHANGE         = com.cboe.idl.constants.ActivitySubTypes.SYMBOL_CHANGE;
    public static final short COMMON_DISTRIBUTION   = com.cboe.idl.constants.ActivitySubTypes.COMMON_DISTRIBUTION;
    public static final short MANUAL_FILL_REJECT_SYSTEM_ERROR = com.cboe.idl.constants.ActivitySubTypes.MANUAL_FILL_REJECT_SYSTEM_ERROR;
    public static final short MANUAL_FILL_REJECT_INVALID_VOLUME = com.cboe.idl.constants.ActivitySubTypes.MANUAL_FILL_REJECT_INVALID_VOLUME;
    public static final short MANUAL_FILL_REJECT_REROUTE = com.cboe.idl.constants.ActivitySubTypes.MANUAL_FILL_REJECT_REROUTE;


    // Format constants
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String FORMAT_UNDEFINED = "ERROR: Format Not Defined For Type";
    public static final String INVALID_FORMAT = "ERROR: Invalid Format Specifier";
    public static final String INVALID_TYPE = "ERROR: Invalid Type Code";

//*** Private Attributes

    private static final String RECEIVED_STRING         = "Received";
    private static final String SELECTED_STRING         = "Selected";
    private static final String TRADED_STRING           = "Traded";
    private static final String HILO_STRING             = "Hilo";
    
    private static final String SPLIT_STRING            = "Split";
    private static final String DIVIDEND_CASH_STRING    = "Dividend Cash";
    private static final String DIVIDEND_PERCENT_STRING = "Dividend Percent";
    private static final String DIVIDEND_STOCK_STRING   = "Dividend Stock";
    private static final String LEAP_ROLLOVER_STRING    = "Leap Rollover";
    private static final String MERGER_STRING           = "Merger";
    private static final String SYMBOL_CHANGE_STRING    = "Symbol Change";
    private static final String COMMON_DISTRIBUTION_STRING  = "Common Distribution";
    private static final String MANUAL_FILL_REJECT_SYSTEM_ERROR_STRING = "Manual Fill Reject System Error";
    private static final String MANUAL_FILL_REJECT_INVALID_VOLUME_STRING = "Manual Fill Reject Invalid Volume";
    private static final String MANUAL_FILL_REJECT_REROUTE_STRING = "Manual Fill Reject Reroute";

//*** Public Methods

    /*****************************************************************************
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param activitySubEvent - the activity event code to render (see defined constants)
     * @return a string representation of the activityEvent
     * @see com.cboe.idl.constants.ActivitySubTypes
     */
    public static String toString( short activitySubEvent )
    {
        return toString( activitySubEvent, TRADERS_FORMAT );
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param activitySubEvent - the activity sub event code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the activitySubEvent
     * @see com.cboe.idl.constants.ActivitySubTypes
     */
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod", "MethodWithMultipleReturnPoints"})
    public static String toString( short activitySubEvent, String formatSpecifier )
    {
        if( formatSpecifier.equals( TRADERS_FORMAT ))
        {
            switch(activitySubEvent)
            {

                case RECEIVED:
                    return RECEIVED_STRING;
                case SELECTED:
                    return SELECTED_STRING;
                case TRADED:
                    return TRADED_STRING;
                case HILO:
                    return HILO_STRING;
                case SPLIT:
                    return SPLIT_STRING;
                case DIVIDEND_CASH:
                    return DIVIDEND_CASH_STRING;
                case DIVIDEND_PERCENT:
                    return DIVIDEND_PERCENT_STRING;
                case DIVIDEND_STOCK:
                    return DIVIDEND_STOCK_STRING;
                case LEAP_ROLLOVER:
                    return LEAP_ROLLOVER_STRING;
                case MERGER:
                    return MERGER_STRING;
                case SYMBOL_CHANGE:
                    return SYMBOL_CHANGE_STRING;
                case COMMON_DISTRIBUTION:
                    return COMMON_DISTRIBUTION_STRING;
                case MANUAL_FILL_REJECT_SYSTEM_ERROR:
                    return MANUAL_FILL_REJECT_SYSTEM_ERROR_STRING;
                case MANUAL_FILL_REJECT_INVALID_VOLUME:
                    return MANUAL_FILL_REJECT_INVALID_VOLUME_STRING;
                case MANUAL_FILL_REJECT_REROUTE:
                    return MANUAL_FILL_REJECT_REROUTE_STRING;
                default:
                      return new StringBuffer(20).append("[ ").append(activitySubEvent).append(" ]").toString();
            }
        }
        return INVALID_FORMAT;
    }


//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private ActivitySubTypes( )
    {
    }

}
