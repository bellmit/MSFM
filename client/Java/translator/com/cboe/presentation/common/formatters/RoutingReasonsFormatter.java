/********************************************************************************
 * FILE:    RoutingReasonsFormatter.java
 *
 * PACKAGE: com.cboe.presentation.common.formatters
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

// CBOE imports

import com.cboe.idl.constants.OHSRoutingReasons;

import com.cboe.interfaces.presentation.omt.MessageElement;


/********************************************************************************
 * Represents OHS Routing Reasons
 *
 * @see com.cboe.idl.constants.OHSRoutingReasons
 */
@SuppressWarnings({"ConstantNamingConvention"})
public class RoutingReasonsFormatter
{
//*** Public Attributes
    //1-5
    public static final short VOLUME_CHECK = OHSRoutingReasons.VOLUME_CHECK;
    public static final short AUTO_EXECUTION = OHSRoutingReasons.AUTO_EXECUTION;
    public static final short DIRECT_ROUTE = OHSRoutingReasons.DIRECT_ROUTE;
    public static final short ALTERNATE_ROUTE = OHSRoutingReasons.ALTERNATE_ROUTE;
    public static final short DISCRETIONARY_OR_NH_ORDER = OHSRoutingReasons.DISCRETIONARY_OR_NH_ORDER;
    //6-10
    public static final short ALL_ROUTING_ATTEMPT_FAILED = OHSRoutingReasons.ALL_ROUTING_ATTEMPT_FAILED;
    public static final short HAL_REROUTING = OHSRoutingReasons.HAL_REROUTING;
    public static final short REROUTING_TO_SENDER = OHSRoutingReasons.REROUTING_TO_SENDER;
    public static final short REROUTING_TO_DEFAULT_OMT = OHSRoutingReasons.REROUTING_TO_DEFAULT_OMT;
    public static final short LINKAGE_ROUTE = OHSRoutingReasons.LINKAGE_ROUTE;
    //11-15
    public static final short PAR_PRINT_ORDER_INTRA_DAY = OHSRoutingReasons.PAR_PRINT_ORDER_INTRA_DAY;
    public static final short PAR_PRINT_ORDER_END_OF_DAY = OHSRoutingReasons.PAR_PRINT_ORDER_END_OF_DAY;
    public static final short PAR_PRINT_CANCEL = OHSRoutingReasons.PAR_PRINT_CANCEL;
    public static final short PAR_PRINT_CANCEL_REPLACE = OHSRoutingReasons.PAR_PRINT_CANCEL_REPLACE;
    public static final short MANUAL_REROUTE_ORDER_TA = OHSRoutingReasons.MANUAL_REROUTE_ORDER_TA;
    //16-20
    public static final short MANUAL_REROUTE_ORDER_TB = OHSRoutingReasons.MANUAL_REROUTE_ORDER_TB;
    public static final short MANUAL_REROUTE_ORDER_BOOK = OHSRoutingReasons.MANUAL_REROUTE_ORDER_BOOK;
    public static final short MANUAL_REROUTE_ORDER_AUCTION = OHSRoutingReasons.MANUAL_REROUTE_ORDER_AUCTION;
    public static final short CANCEL_FOLLOW_ORDER = OHSRoutingReasons.CANCEL_FOLLOW_ORDER;
    public static final short MANUAL_ORDER_TIMEOUT = OHSRoutingReasons.MANUAL_ORDER_TIMEOUT;
    //21-25
    public static final short MANUAL_FILL_TIMEOUT = OHSRoutingReasons.MANUAL_FILL_TIMEOUT;
    public static final short CABINET_ORDER = OHSRoutingReasons.CABINET_ORDER;
    public static final short SIMPLE_FILL_REJECT = OHSRoutingReasons.SIMPLE_FILL_REJECT;
    public static final short COMPLEX_FILL_REJECT = OHSRoutingReasons.COMPLEX_FILL_REJECT;
    public static final short CANCEL_REQUEST_ON_RSS = OHSRoutingReasons.CANCEL_REQUEST_ON_RSS;
    //26-30
    public static final short NBBO_REJECT = OHSRoutingReasons.NBBO_REJECT;
    public static final short TRADE_NOTIFICATION_BUNDLE_TIMEOUT = OHSRoutingReasons.TRADE_NOTIFICATION_BUNDLE_TIMEOUT;
    public static final short TRADE_NOTIFICATION_ACK_TIMEOUT = OHSRoutingReasons.TRADE_NOTIFICATION_ACK_TIMEOUT;
    public static final short TRADE_NOTIFICATION_REJECT = OHSRoutingReasons.TRADE_NOTIFICATION_REJECT;
    public static final short FILL_REPORT_DROP_COPY = OHSRoutingReasons.FILL_REPORT_DROP_COPY;
    //31-35
    public static final short CANCEL_REPORT_DROP_COPY = OHSRoutingReasons.CANCEL_REPORT_DROP_COPY;
    public static final short PREMIUM_EXCEEDS_REASONABILITY = OHSRoutingReasons.PREMIUM_EXCEEDS_REASONABILITY;
    public static final short VOLUME_DEVIATION_CHECK_FAILED_ALL_LEVELS = OHSRoutingReasons.VOLUME_DEVIATION_CHECK_FAILED_ALL_LEVELS;
    public static final short VOLUME_DEVIATION_CHECK_PASSED_LEVEL_1 = OHSRoutingReasons.VOLUME_DEVIATION_CHECK_PASSED_LEVEL_1;
    public static final short VOLUME_DEVIATION_CHECK_PASSED_LEVEL_2 = OHSRoutingReasons.VOLUME_DEVIATION_CHECK_PASSED_LEVEL_2;
    //36-41
    public static final short VOLUME_DEVIATION_CHECK_PASSED_LEVEL_3 = OHSRoutingReasons.VOLUME_DEVIATION_CHECK_PASSED_LEVEL_3;
    public static final short CANCEL_REQUEST_ON_FALLBACK = OHSRoutingReasons.CANCEL_REQUEST_ON_FALLBACK;
    public static final short TOO_MANY_ROUTES = OHSRoutingReasons.TOO_MANY_ROUTES;
    public static final short PRODUCT_STATE_ROUTE = OHSRoutingReasons.PRODUCT_STATE_ROUTE;
    public static final short VOLUME_MAINTENANCE_MISMATCH = OHSRoutingReasons.VOLUME_MAINTENANCE_MISMATCH;
    public static final short FORCED_LOGOFF_PAR = OHSRoutingReasons.FORCED_LOGOFF_PAR;
    
    //42-44
    public static final short TRADE_NOTIFICATION_INVALID_CONTRA = OHSRoutingReasons.TRADE_NOTIFICATION_INVALID_CONTRA;
    public static final short TRADE_NOTIFICATION_TRADE_BUST = OHSRoutingReasons.TRADE_NOTIFICATION_TRADE_BUST;
    public static final short TRADE_NOTIFICATION_CMI_USER_UNDEFINED = OHSRoutingReasons.TRADE_NOTIFICATION_CMI_USER_UNDEFINED;

    // Format constants
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT =   "ERROR: Invalid Format Specifier";

    //*** Private Attributes
    @SuppressWarnings({"HardcodedFileSeparator"})
    private static final String NO_ROUTING_REASON_AVAILABLE_STRING = "N/A";
    private static final String ALL_ROUTING_ATTEMPT_FAILED_STRING = "All Routing Attempts Failed";
    private static final String ALTERNATE_ROUTE_STRING = "Alternate Route";
    private static final String AUTO_EXECUTION_STRING = "Auto Execution";
    private static final String CABINET_ORDER_STRING = "Cabinet Order";
    private static final String CANCEL_REPORT_DROP_COPY_STRING = "Cancel Report Drop Copy";
    private static final String CANCEL_REQUEST_ON_FALLBACK_STRING = "Cancel Request on Fallback";
    private static final String CANCEL_REQUEST_ON_RSS_STRING = "Cancel Request on RSS";
    private static final String CANCEL_FOLLOW_ORDER_STRING = "Cancel Follow Order";
    private static final String COMPLEX_FILL_REJECT_STRING = "Complex Fill Reject";
    private static final String DIRECT_ROUTE_STRING = "Direct Route";
    private static final String DISCRETIONARY_OR_NH_ORDER_STRING = "Discretionary or NH Order";
    private static final String FILL_REPORT_DROP_COPY_STRING = "Fill Report Drop Copy";
    private static final String FORCED_LOGOFF_PAR_STRING = "PAR Workstation Forced Logoff";
    private static final String HAL_REROUTING_STRING = "HAL Rerouting";
    private static final String LINKAGE_ROUTE_STRING = "Linkage Route";
    private static final String MANUAL_FILL_TIMEOUT_STRING = "Manual Fill Timeout";
    private static final String MANUAL_ORDER_TIMEOUT_STRING = "Manual Order Timeout";
    private static final String MANUAL_REROUTE_ORDER_AUCTION_STRING = "Manual Reroute Order Auction";
    private static final String MANUAL_REROUTE_ORDER_BOOK_STRING = "Manual Reroute Order Book";
    private static final String MANUAL_REROUTE_ORDER_TA_STRING = "Manual Reroute Order TA";
    private static final String MANUAL_REROUTE_ORDER_TB_STRING = "Manual Reroute Order TB";
    private static final String NBBO_REJECT_STRING = "NBBO Reject";
    private static final String PAR_PRINT_CANCEL_STRING = "PAR Print Cancel";
    private static final String PAR_PRINT_CANCEL_REPLACE_STRING = "PAR Print Cancel Replace";
    private static final String PAR_PRINT_ORDER_END_OF_DAY_STRING = "PAR Print Order EOD";
    private static final String PAR_PRINT_ORDER_INTRA_DAY_STRING = "PAR Print Order Intra Day";
    private static final String PRODUCT_STATE_ROUTE_STRING = "Product State Route";
    private static final String PREMIUM_EXCEEDS_REASONABILITY_STRING = "Premium Exceeds Reasonability";
    private static final String REROUTING_TO_DEFAULT_OMT_STRING = "Rerouting to Default OMT";
    private static final String REROUTING_TO_SENDER_STRING = "Rerouting to Sender";
    private static final String SIMPLE_FILL_REJECT_STRING = "Simple Fill Reject";
    private static final String TOO_MANY_ROUTES_STRING = "Too Many Routes";
    private static final String TRADE_NOTIFICATION_ACK_TIMEOUT_STRING = "Trade Notification Ack. Timeout";
    private static final String TRADE_NOTIFICATION_BUNDLE_TIMEOUT_STRING = "Trade Notification Bundle Timeout";
    private static final String TRADE_NOTIFICATION_REJECT_STRING = "Trade Notification Reject";
    private static final String VOLUME_CHECK_STRING = "Volume Check";
    private static final String VOLUME_DEVIATION_CHECK_PASSED_LEVEL_1_STRING = "Volume Deviation Check Passed Level 1";
    private static final String VOLUME_DEVIATION_CHECK_PASSED_LEVEL_2_STRING = "Volume Deviation Check Passed Level 2";
    private static final String VOLUME_DEVIATION_CHECK_PASSED_LEVEL_3_STRING = "Volume Deviation Check Passed Level 3";
    private static final String VOLUME_DEVIATION_CHECK_FAILED_ALL_LEVELS_STRING = "Volume Deviation Check Failed All Levels";
    private static final String VOLUME_MAINTENANCE_MISMATCH_STRING = "Volume Maintenance Mismatch";
    private static final String TRADE_NOTIFICATION_INVALID_CONTRA_STRING = "Trade Notification Invalid Contra";
    private static final String TRADE_NOTIFICATION_TRADE_BUST_STRING = "Trade Notification Trade Bust";
    private static final String TRADE_NOTIFICATION_CMI_USER_UNDEFINED_STRING = "Trade Notification Cmi Undefined User or Not Logged In";

    //*** Public Methods

    /**
     * **************************************************************************
     * Returns a string representation of the routing reason in TRADERS_FORMAT format
     * @param routingReason - the routing reason value to render (see defined constants)
     * @return a string representation of the routingReason
     * @see com.cboe.idl.constants.OHSRoutingReasons
     */
    public static String getString(short routingReason)
    {
        return getString(routingReason, TRADERS_FORMAT);
    }

    /*****************************************************************************
     * Returns a string representation of the routing reason in the given format
     *
     * @param routingReason - the routing reason value to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the routingReason
     * @see com.cboe.idl.constants.OHSRoutingReasons
     */
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod", "MethodWithMultipleReturnPoints"})
    public static String getString(short routingReason, String formatSpecifier)
    {
        if(formatSpecifier.equals(TRADERS_FORMAT))
        {
            switch(routingReason)
            {
                case ALL_ROUTING_ATTEMPT_FAILED:
                    return ALL_ROUTING_ATTEMPT_FAILED_STRING;
                case ALTERNATE_ROUTE:
                    return ALTERNATE_ROUTE_STRING;
                case AUTO_EXECUTION:
                    return AUTO_EXECUTION_STRING;
                case COMPLEX_FILL_REJECT:
                    return COMPLEX_FILL_REJECT_STRING;
                case CANCEL_REQUEST_ON_FALLBACK:
                    return CANCEL_REQUEST_ON_FALLBACK_STRING;
                case CANCEL_REQUEST_ON_RSS:
                    return CANCEL_REQUEST_ON_RSS_STRING;
                case CANCEL_FOLLOW_ORDER:
                    return CANCEL_FOLLOW_ORDER_STRING;
                case CANCEL_REPORT_DROP_COPY:
                    return CANCEL_REPORT_DROP_COPY_STRING;
                case DIRECT_ROUTE:
                    return DIRECT_ROUTE_STRING;
                case DISCRETIONARY_OR_NH_ORDER:
                    return DISCRETIONARY_OR_NH_ORDER_STRING;
                case FILL_REPORT_DROP_COPY:
                    return FILL_REPORT_DROP_COPY_STRING;
                case FORCED_LOGOFF_PAR:
                    return FORCED_LOGOFF_PAR_STRING;
                case HAL_REROUTING:
                    return HAL_REROUTING_STRING;
                case LINKAGE_ROUTE:
                    return LINKAGE_ROUTE_STRING;
                case MANUAL_REROUTE_ORDER_AUCTION:
                    return MANUAL_REROUTE_ORDER_AUCTION_STRING;
                case MANUAL_REROUTE_ORDER_BOOK:
                    return MANUAL_REROUTE_ORDER_BOOK_STRING;
                case MANUAL_REROUTE_ORDER_TA:
                    return MANUAL_REROUTE_ORDER_TA_STRING;
                case MANUAL_REROUTE_ORDER_TB:
                    return MANUAL_REROUTE_ORDER_TB_STRING;
                case NBBO_REJECT:
                    return NBBO_REJECT_STRING;
                case PAR_PRINT_CANCEL:
                    return PAR_PRINT_CANCEL_STRING;
                case PAR_PRINT_CANCEL_REPLACE:
                    return PAR_PRINT_CANCEL_REPLACE_STRING;
                case PAR_PRINT_ORDER_END_OF_DAY:
                    return PAR_PRINT_ORDER_END_OF_DAY_STRING;
                case PAR_PRINT_ORDER_INTRA_DAY:
                    return PAR_PRINT_ORDER_INTRA_DAY_STRING;
                case PREMIUM_EXCEEDS_REASONABILITY:
                    return PREMIUM_EXCEEDS_REASONABILITY_STRING;
                case PRODUCT_STATE_ROUTE:
                    return PRODUCT_STATE_ROUTE_STRING;
                case REROUTING_TO_DEFAULT_OMT:
                    return REROUTING_TO_DEFAULT_OMT_STRING;
                case REROUTING_TO_SENDER:
                    return REROUTING_TO_SENDER_STRING;
                case SIMPLE_FILL_REJECT:
                    return SIMPLE_FILL_REJECT_STRING;
                case TOO_MANY_ROUTES:
                    return TOO_MANY_ROUTES_STRING;
                case TRADE_NOTIFICATION_ACK_TIMEOUT:
                    return TRADE_NOTIFICATION_ACK_TIMEOUT_STRING;
                case TRADE_NOTIFICATION_BUNDLE_TIMEOUT:
                    return TRADE_NOTIFICATION_BUNDLE_TIMEOUT_STRING;
                case TRADE_NOTIFICATION_REJECT:
                    return TRADE_NOTIFICATION_REJECT_STRING;
                case VOLUME_CHECK:
                    return VOLUME_CHECK_STRING;
                case MessageElement.NO_ROUTING_REASON_AVAILABLE:
                    return NO_ROUTING_REASON_AVAILABLE_STRING;
                case MANUAL_ORDER_TIMEOUT:
                    return MANUAL_ORDER_TIMEOUT_STRING;
                case MANUAL_FILL_TIMEOUT:
                    return MANUAL_FILL_TIMEOUT_STRING;
                case CABINET_ORDER:
                    return CABINET_ORDER_STRING;
                case VOLUME_DEVIATION_CHECK_PASSED_LEVEL_1:
                    return VOLUME_DEVIATION_CHECK_PASSED_LEVEL_1_STRING;
                case VOLUME_DEVIATION_CHECK_PASSED_LEVEL_2:
                    return VOLUME_DEVIATION_CHECK_PASSED_LEVEL_2_STRING;
                case VOLUME_DEVIATION_CHECK_PASSED_LEVEL_3:
                    return VOLUME_DEVIATION_CHECK_PASSED_LEVEL_3_STRING;
                case VOLUME_DEVIATION_CHECK_FAILED_ALL_LEVELS:
                    return VOLUME_DEVIATION_CHECK_FAILED_ALL_LEVELS_STRING;
                case VOLUME_MAINTENANCE_MISMATCH:
                    return VOLUME_MAINTENANCE_MISMATCH_STRING;
                case TRADE_NOTIFICATION_INVALID_CONTRA:
                    return TRADE_NOTIFICATION_INVALID_CONTRA_STRING;
                case TRADE_NOTIFICATION_TRADE_BUST:
                    return TRADE_NOTIFICATION_TRADE_BUST_STRING;
                case TRADE_NOTIFICATION_CMI_USER_UNDEFINED:
                    return TRADE_NOTIFICATION_CMI_USER_UNDEFINED_STRING;

                default:
                    return new StringBuffer(10).append("[ ").append(routingReason).append(" ]").toString();
            }
        }
        return INVALID_FORMAT;
    }
//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private RoutingReasonsFormatter()
    {
    }

}
