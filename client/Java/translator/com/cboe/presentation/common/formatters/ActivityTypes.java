/********************************************************************************
 * FILE:    ActivityTypes.java
 *
 * PACKAGE: com.cboe.presentation.common.types.constants
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.presentation.common.formatters;

import com.cboe.domain.util.InternalActivityTypes;

// CBOE imports
// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.ActivityTypes;


/********************************************************************************
 * Represents Activity Events
 *
 * @see com.cboe.idl.cmiConstants.ActivityTypes
 */
@SuppressWarnings({"ConstantNamingConvention"})
public class ActivityTypes
{

//*** Public Attributes

    // Activity Events (mapping to com.cboe.idl.cmiConstants.ActivityTypes)

    /* When adding a new activity type to the following list, the following files must also be updated
     * to account for the new activity types:
     *
     * ActivityLogTableRow.java - function getOrderIdQuoteIdString()
     * ActivityTypeComboBox.java - functions getSelectedTypes(), setSelectedType(), loadComboBox()
     *
     * Additionally, in this file, add a new string constant for each new activity type defined
     */
    
    public static final short BOOK_ORDER           = com.cboe.idl.cmiConstants.ActivityTypes.BOOK_ORDER;
    public static final short BUST_ORDER_FILL      = com.cboe.idl.cmiConstants.ActivityTypes.BUST_ORDER_FILL;
    public static final short BUST_QUOTE_FILL      = com.cboe.idl.cmiConstants.ActivityTypes.BUST_QUOTE_FILL;
    public static final short BUST_REINSTATE_ORDER = com.cboe.idl.cmiConstants.ActivityTypes.BUST_REINSTATE_ORDER;
    public static final short CANCEL_ALL_ORDERS    = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_ALL_ORDERS;
    public static final short CANCEL_ALL_QUOTES    = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_ALL_QUOTES;
    public static final short CANCEL_ORDER         = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_ORDER;
    public static final short CANCEL_QUOTE         = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_QUOTE;
    public static final short CANCEL_REPLACE_ORDER = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_REPLACE_ORDER;
    public static final short CROSSING_ORDER_ROUTED= com.cboe.idl.cmiConstants.ActivityTypes.CROSSING_ORDER_ROUTED;
    public static final short FILL_ORDER           = com.cboe.idl.cmiConstants.ActivityTypes.FILL_ORDER;
    public static final short FILL_QUOTE           = com.cboe.idl.cmiConstants.ActivityTypes.FILL_QUOTE;
    public static final short NEW_ORDER            = com.cboe.idl.cmiConstants.ActivityTypes.NEW_ORDER;
    public static final short NEW_QUOTE            = com.cboe.idl.cmiConstants.ActivityTypes.NEW_QUOTE;
    public static final short NEW_RFQ              = com.cboe.idl.cmiConstants.ActivityTypes.NEW_RFQ;
    public static final short ORDER_ROUTED         = com.cboe.idl.cmiConstants.ActivityTypes.ORDER_ROUTED;
    public static final short PRICE_ADJUST_ORDER   = com.cboe.idl.cmiConstants.ActivityTypes.PRICE_ADJUST_ORDER;
    public static final short STATE_CHANGE_ORDER   = com.cboe.idl.cmiConstants.ActivityTypes.STATE_CHANGE_ORDER;
    public static final short SYSTEM_CANCEL_QUOTE  = com.cboe.idl.cmiConstants.ActivityTypes.SYSTEM_CANCEL_QUOTE;
    public static final short UPDATE_ORDER         = com.cboe.idl.cmiConstants.ActivityTypes.UPDATE_ORDER;
    public static final short UPDATE_QUOTE         = com.cboe.idl.cmiConstants.ActivityTypes.UPDATE_QUOTE;
    public static final short FAILED_ROUTE         = com.cboe.idl.cmiConstants.ActivityTypes.FAILED_ROUTE;
    public static final short CANCEL_REQUEST_ROUTED = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_REQUEST_ROUTED;
    public static final short CANCEL_REQUEST_FAILED_ROUTE = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_REQUEST_FAILED_ROUTE;
    public static final short CANCEL_REPLACE_ORDER_REQUEST_FAILED_ROUTE = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_REPLACE_ORDER_REQUEST_FAILED_ROUTE;
    public static final short AUDIT_HISTORY_EVENT = com.cboe.idl.cmiConstants.ActivityTypes.AUDIT_HISTORY_EVENT;

    public static final short HYBRID_PROCESSING_REQUESTED = InternalActivityTypes.HYBRID_PROCESSING_REQUESTED;
    public static final short HYBRID_REQUEST_RETURNED = InternalActivityTypes.HYBRID_REQUEST_RETURNED;
    public static final short OMT_DISPLAY_ROUTED_AWAY = InternalActivityTypes.OMT_DISPLAY_ROUTED_AWAY;

    //Strategy support
    public static final short NEW_ORDER_STRATEGY_LEG        = com.cboe.idl.cmiConstants.ActivityTypes.NEW_ORDER_STRATEGY_LEG;
    public static final short FILL_STRATEGY_LEG         = com.cboe.idl.cmiConstants.ActivityTypes.FILL_STRATEGY_LEG;
    public static final short CANCEL_STRATEGY_LEG           = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_STRATEGY_LEG;
    public static final short BUST_STRATEGY_LEG_FILL        = com.cboe.idl.cmiConstants.ActivityTypes.BUST_STRATEGY_LEG_FILL;
    public static final short BUST_REINSTATE_STRATEGY_LEG   = com.cboe.idl.cmiConstants.ActivityTypes.BUST_REINSTATE_STRATEGY_LEG;
    public static final short BOOK_STRATEGY_LEG             = com.cboe.idl.cmiConstants.ActivityTypes.BOOK_STRATEGY_LEG;
    public static final short UPDATE_STRATEGY_LEG           = com.cboe.idl.cmiConstants.ActivityTypes.UPDATE_STRATEGY_LEG;
    public static final short PRICE_ADJUST_ORDER_LEG        = com.cboe.idl.cmiConstants.ActivityTypes.PRICE_ADJUST_ORDER_LEG;
    public static final short  QUOTE_LEG_FILL           = com.cboe.idl.cmiConstants.ActivityTypes.QUOTE_LEG_FILL;
    public static final short  BUST_QUOTE_LEG_FILL      = com.cboe.idl.cmiConstants.ActivityTypes.BUST_QUOTE_LEG_FILL;

    // IPP
    public static final short HELD_FOR_IPP_PROTECTION = com.cboe.idl.cmiConstants.ActivityTypes.HELD_FOR_IPP_PROTECTION;
    public static final short CANCEL_REPLACE_ORDER_REQUEST = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_REPLACE_ORDER_REQUEST;
    // Linkage
    public static final short INBOUND_S_ORDER_FILL = com.cboe.idl.cmiConstants.ActivityTypes.INBOUND_S_ORDER_FILL;
    public static final short NEW_ORDER_REJECT = com.cboe.idl.cmiConstants.ActivityTypes.NEW_ORDER_REJECT;
    public static final short FILL_REJECT = com.cboe.idl.cmiConstants.ActivityTypes.FILL_REJECT;
    public static final short CANCEL_ORDER_REQUEST = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_ORDER_REQUEST;
    public static final short CANCEL_ORDER_REQUEST_REJECT = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_ORDER_REQUEST_REJECT;
    public static final short CANCEL_REPORT_REJECT = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_REPORT_REJECT;
    public static final short NEW_ORDER_REJECT_REJECTED = com.cboe.idl.cmiConstants.ActivityTypes.NEW_ORDER_REJECT_REJECTED;
    public static final short FILL_REJECT_REJECTED = com.cboe.idl.cmiConstants.ActivityTypes.FILL_REJECT_REJECTED;
    public static final short CANCEL_ORDER_REQUEST_REJECT_REJECTED = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_ORDER_REQUEST_REJECT_REJECTED;
    public static final short CANCEL_REPORT_REJECT_REJECTED = com.cboe.idl.cmiConstants.ActivityTypes.CANCEL_REPORT_REJECT_REJECTED;
    public static final short ROUTE_TO_AWAY_EXCHANGE = com.cboe.idl.cmiConstants.ActivityTypes.ROUTE_TO_AWAY_EXCHANGE;
    public static final short LINKAGE_ORDER_RELATIONSHIP = com.cboe.idl.cmiConstants.ActivityTypes.LINKAGE_ORDER_RELATIONSHIP;
    public static final short EXECUTION_REPORT_ON_LINKED_ORDER = com.cboe.idl.cmiConstants.ActivityTypes.EXECUTION_REPORT_ON_LINKED_ORDER;
    public static final short EXECUTION_REPORT_ROUTED = com.cboe.idl.cmiConstants.ActivityTypes.EXECUTION_REPORT_ROUTED;
    public static final short EXECUTION_REPORT_FAILED_ROUTE = com.cboe.idl.cmiConstants.ActivityTypes.EXECUTION_REPORT_FAILED_ROUTE;
    public static final short AWAY_EXCHANGE_MARKET = com.cboe.idl.cmiConstants.ActivityTypes.AWAY_EXCHANGE_MARKET;
    public static final short LINKAGE_DISQUALIFIED_EXCHANGE = com.cboe.idl.cmiConstants.ActivityTypes.LINKAGE_DISQUALIFIED_EXCHANGE;
    public static final short MANUAL_ORDER_SR = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_SR;
    public static final short MANUAL_ORDER_SR_TIMEOUT= com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_SR_TIMEOUT;
    public static final short MANUAL_ORDER_SR_TIMEOUT_FAILURE = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_SR_TIMEOUT_FAILURE;
    public static final short MANUAL_ORDER_FR = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_FR;
    public static final short MANUAL_ORDER_FR_TIMEOUT= com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_FR_TIMEOUT;
    public static final short MANUAL_ORDER_FR_TIMEOUT_FAILURE = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_FR_TIMEOUT_FAILURE;

    // Misc
    public static final short AUCTION_START = com.cboe.idl.cmiConstants.ActivityTypes.AUCTION_START;
    public static final short AUCTION_TRIGGER_START = com.cboe.idl.cmiConstants.ActivityTypes.AUCTION_TRIGGER_START;
    public static final short AUCTION_END = com.cboe.idl.cmiConstants.ActivityTypes.AUCTION_END;
    public static final short AUCTION_TRIGGER_END = com.cboe.idl.cmiConstants.ActivityTypes.AUCTION_TRIGGER_END;
    public static final short TSB_REQUEST = com.cboe.idl.cmiConstants.ActivityTypes.TSB_REQUEST;
    public static final short VOL_MAINTENANCE = com.cboe.idl.cmiConstants.ActivityTypes.VOL_MAINTENANCE;
    public static final short MANUAL_ORDER_TA = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_TA;
    public static final short MANUAL_ORDER_TB = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_TB;
    public static final short MANUAL_ORDER_BOOK = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_BOOK;
    public static final short MANUAL_ORDER_AUCTION = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_AUCTION;
    public static final short PAR_BROKER_USED_MKT_DATA = com.cboe.idl.cmiConstants.ActivityTypes.PAR_BROKER_USED_MKT_DATA;
    public static final short PAR_BROKER_MKT_DATA = com.cboe.idl.cmiConstants.ActivityTypes.PAR_BROKER_MKT_DATA;
    public static final short PAR_PRINT_INTRA_DAY = com.cboe.idl.cmiConstants.ActivityTypes.PAR_PRINT_INTRA_DAY;
    public static final short PAR_PRINT_END_OF_DAY = com.cboe.idl.cmiConstants.ActivityTypes.PAR_PRINT_END_OF_DAY;
    public static final short MANUAL_FILL_REJECT = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_FILL_REJECT;

    public static final short MANUAL_ORDER_TA_TIMEOUT = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_TA_TIMEOUT;
    public static final short MANUAL_ORDER_TB_TIMEOUT = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_TB_TIMEOUT;
    public static final short MANUAL_ORDER_BOOK_TIMEOUT = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_BOOK_TIMEOUT;
    public static final short MANUAL_ORDER_AUCTION_TIMEOUT = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_AUCTION_TIMEOUT;
    public static final short MANUAL_ORDER_LINKAGE_TIMEOUT = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_LINKAGE_TIMEOUT;
    public static final short MANUAL_FILL_TIMEOUT = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_FILL_TIMEOUT;
    public static final short MANUAL_FILL_LINKAGE_TIMEOUT = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_FILL_LINKAGE_TIMEOUT;

    public static final short MANUAL_TA_TIMEOUT_STRATEGY_LEG = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_TA_TIMEOUT_STRATEGY_LEG;
    public static final short MANUAL_BOOK_TIMEOUT_STRATEGY_LEG = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_BOOK_TIMEOUT_STRATEGY_LEG;
    public static final short MANUAL_AUCTION_TIMEOUT_STRATEGY_LEG = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_AUCTION_TIMEOUT_STRATEGY_LEG;

    public static final short MANUAL_ORDER_REROUTE_REQUEST = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_REROUTE_REQUEST;
    public static final short MANUAL_ORDER_REROUTE_CROWD_REQUEST = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_REROUTE_CROWD_REQUEST;
    public static final short MANUAL_FILL_REJECT_FAILURE = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_FILL_REJECT_FAILURE;
    public static final short MANUAL_ORDER_TA_TIMEOUT_FAILURE = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_TA_TIMEOUT_FAILURE;
    public static final short MANUAL_ORDER_TB_TIMEOUT_FAILURE = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_TB_TIMEOUT_FAILURE;
    public static final short MANUAL_ORDER_BOOK_TIMEOUT_FAILURE = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_BOOK_TIMEOUT_FAILURE;
    public static final short MANUAL_ORDER_AUCTION_TIMEOUT_FAILURE = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_ORDER_AUCTION_TIMEOUT_FAILURE;
    public static final short MANUAL_FILL_TIMEOUT_FAILURE = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_FILL_TIMEOUT_FAILURE;
    public static final short MANUAL_FILL_LINKAGE_TIMEOUT_FAILURE = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_FILL_LINKAGE_TIMEOUT_FAILURE;
    public static final short MANUAL_FILL_TIMEOUT_STRATEGY_LEG = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_FILL_TIMEOUT_STRATEGY_LEG;
    public static final short MANUAL_FILL_REJECT_STRATEGY_LEG = com.cboe.idl.cmiConstants.ActivityTypes.MANUAL_FILL_REJECT_STRATEGY_LEG;
    public static final short FORCED_LOGOFF_PAR = com.cboe.idl.cmiConstants.ActivityTypes.FORCED_LOGOFF_PAR;
    public static final short FORCED_LOGOFF_PAR_FAILURE = com.cboe.idl.cmiConstants.ActivityTypes.FORCED_LOGOFF_PAR_FAILURE;

    public static final short NON_ORDER_MESSAGE_REROUTE = com.cboe.idl.cmiConstants.ActivityTypes.NON_ORDER_MESSAGE_REROUTE;
    public static final short PAR_BROKER_LEG_MKT = com.cboe.idl.cmiConstants.ActivityTypes.PAR_BROKER_LEG_MKT;

    //Directed AIM
    public static final short DIRECTED_AIM_NOTIFICATION_START = com.cboe.idl.cmiConstants.ActivityTypes.DIRECTED_AIM_NOTIFICATION_START;
    public static final short DIRECTED_AIM_NOTIFICATION_END = com.cboe.idl.cmiConstants.ActivityTypes.DIRECTED_AIM_NOTIFICATION_END;

    // added at request of CPS Phase 2 project
    public static final short CPS_SPLIT_ORDER_TIMEOUT = InternalActivityTypes.CPS_SPLIT_ORDER_TIMEOUT;
    public static final short CPS_SPLIT_ORDER_CANCEL_REQUEST_REJECT = InternalActivityTypes.CPS_SPLIT_ORDER_CANCEL_REQUEST_REJECT;
    public static final short CPS_SPLIT_ORDER_CANCEL_REPLACE_REJECT = InternalActivityTypes.CPS_SPLIT_ORDER_CANCEL_REPLACE_REJECT;
    public static final short CPS_SPLIT_DERIVED_ORDER_NEW = InternalActivityTypes.CPS_SPLIT_DERIVED_ORDER_NEW;
    public static final short CPS_SPLIT_DERIVED_ORDER_FILL = InternalActivityTypes.CPS_SPLIT_DERIVED_ORDER_FILL;
    public static final short CPS_SPLIT_DERIVED_ORDER_CANCEL = InternalActivityTypes.CPS_SPLIT_DERIVED_ORDER_CANCEL;

    // Format constants
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String FORMAT_UNDEFINED = "ERROR: Format Not Defined For Type";
    public static final String INVALID_FORMAT = "ERROR: Invalid Format Specifier";
    public static final String INVALID_TYPE = "ERROR: Invalid Type Code";

//*** Private Attributes

    private static final String NEW_ORDER_STRING           = "New Order";
    private static final String BOOK_STRATEGY_LEG_STRING   = "Book Strategy Leg";
    //private static final String BOOK_ORDER_STRING          = "Book Order" ;
    private static final String BUST_REINSTATE_STRING      = "Bust Reinstate";
    private static final String CANCEL_ALL_QUOTES_STRING   = "Cancel All Quotes";
    private static final String CANCEL_ALL_ORDERS_STRING   = "Cancel All Orders";
    private static final String CANCEL_QUOTE_STRING        = "Cancel Quote";
    private static final String CANCEL_REPLACE_STRING      = "Cancel Replace";
    private static final String CANCEL_REQUEST_ROUTED_STRING = "Cancel Request Routed";
    private static final String CANCEL_REQUEST_FAILED_ROUTE_STRING = "Cancel Request Failed Route";
    private static final String CANCEL_REPLACE_ORDER_REQUEST_FAILED_ROUTE_STRING = "Cancel Replace Order Request Failed Route";
    private static final String CANCEL_STRING              = "Cancel";
    private static final String CROSSING_ORDER_ROUTED_STRING ="Crossing Order Routed";
    private static final String ENTERED_INTO_BOOK_STRING   = "Entered Into Book";
    private static final String EXECUTION_STRING           = "Execution";
    private static final String FAILED_ROUTE_STRING        = "Failed Route";
    private static final String ORDER_MAINTENANCE_STRING   = "Order Maintenance";
    private static final String ORDER_ROUTED_STRING        = "Order Routed";
    private static final String PRICE_ADJUST_STRING        = "Price Adjust";
    private static final String RFQ_STRING                 = "RFQ";
    private static final String SENT_QUOTE_STRING          = "Sent Quote";
    private static final String STATE_CHANGE_STRING        = "State Change";
    private static final String SYSTEM_CANCEL_QUOTE_STRING = "System Cancel Quote";
    private static final String TRADE_BUST_STRING          = "Trade Bust";
    private static final String AUDIT_HISTORY_EVENT_STRING = "Audit";

    private static final String HYBRID_PROCESSING_REQUESTED_STRING = "Hybrid Processing Requested";
    private static final String HYBRID_REQUEST_RETURNED_STRING = "Hybrid Request Returned";
    private static final String OMT_DISPLAY_ROUTED_AWAY_STRING = "OMT Display Routed Away";

    //Strategy support
    private static final String NEW_ORDER_STRATEGY_LEG_STRING       = "New Strategy Leg Order";
    private static final String FILL_STRATEGY_LEG_STRING            = "Fill Strategy Leg Order";
    private static final String CANCEL_STRATEGY_LEG_STRING          = "Cancel Strategy Leg Order";
    private static final String BUST_STRATEGY_LEG_FILL_STRING       = "Bust Strategy Leg Order Fill";
    private static final String BUST_REINSTATE_STRATEGY_LEG_STRING  = "Bust Reinstate Strategy Leg Fill";
    private static final String UPDATE_STRATEGY_LEG_STRING          = "Update Strategy Leg";
    private static final String PRICE_ADJUST_ORDER_LEG_STRING       = "Price Adjust Order Leg";
    private static final String QUOTE_LEG_FILL_STRING               = "Fill Strategy Leg Quote";
    private static final String BUST_QUOTE_LEG_FILL_STRING          = "Bust Strategy Leg Quote Fill";

    //IPP
    private static final String HELD_FOR_IPP_PROTECTION_STRING = "Held for IPP Protection";
    private static final String CANCEL_REPLACE_ORDER_REQUEST_STRING = "Cancel Replace Order Request Routed";
    // Linkage
    private static final String INBOUND_S_ORDER_FILL_STRING = "Inbound S Order Fill";
    private static final String NEW_ORDER_REJECT_STRING = "New Order Reject";
    private static final String FILL_REJECT_STRING = "Fill Reject";
    private static final String CANCEL_ORDER_REQUEST_STRING = "Cancel Order Request";
    private static final String CANCEL_ORDER_REQUEST_REJECT_STRING = "Cancel Order Request Reject";
    private static final String CANCEL_REPORT_REJECT_STRING = "Cancel Report Reject";
    private static final String NEW_ORDER_REJECT_REJECTED_STRING = "New Order Reject Rejected";
    private static final String FILL_REJECT_REJECTED_STRING = "Fill Reject Rejected";
    private static final String CANCEL_ORDER_REQUEST_REJECT_REJECTED_STRING = "Cancel Order Request Reject Rejected";
    private static final String CANCEL_REPORT_REJECT_REJECTED_STRING = "Cancel Report Reject Rejected";
    private static final String ROUTE_TO_AWAY_EXCHANGE_STRING = "Route to Away Exchange";
    private static final String LINKAGE_ORDER_RELATIONSHIP_STRING = "Linkage Order Relationship";
    private static final String EXECUTION_REPORT_ON_LINKED_ORDER_STRING = "Execution Report on Linked Order";
    private static final String EXECUTION_REPORT_ROUTED_STRING = "Execution Report Routed";
    private static final String EXECUTION_REPORT_FAILED_ROUTE_STRING = "Execution Report Failed Routed";
    private static final String AWAY_EXCHANGE_MARKET_STRING = "Away Exchange Market";
    private static final String LINKAGE_DISQUALIFIED_EXCHANGE_STRING = "Linkage Disqualified Exchange";

    private static final String MANUAL_ORDER_SR_STRING = "Manual Order Sweep/Return";
    private static final String MANUAL_ORDER_SR_TIMEOUT_STRING = "Manual Order Sweep/Return Timeout";
    private static final String MANUAL_ORDER_SR_TIMEOUT_FAILURE_STRING = "Manual Order Sweep/Return Timeout Failure";
    private static final String MANUAL_ORDER_FR_STRING = "Manual Order New Sweep/Return";
    private static final String MANUAL_ORDER_FR_TIMEOUT_STRING = "Manual Order New Sweep/Return Timeout";
    private static final String MANUAL_ORDER_FR_TIMEOUT_FAILURE_STRING = "Manual Order New Sweep/Return Timeout Failure";

    
    
    // Misc
    private static final String AUCTION_START_STRING = "Auction Start";
    private static final String AUCTION_TRIGGER_START_STRING = "Auction Trigger Start";
    private static final String AUCTION_END_STRING = "Auction End";
    private static final String AUCTION_TRIGGER_END_STRING = "Auction Trigger End";
    private static final String TSB_REQUEST_STRING = "TSB Request";
    private static final String VOL_MAINTENANCE_STRING = "Vol Maintenance";
    private static final String MANUAL_ORDER_TA_STRING = "Manual Order TA";
    private static final String MANUAL_ORDER_TB_STRING = "Manual Order TB";
    private static final String MANUAL_ORDER_BOOK_STRING = "Manual Order Book";
    private static final String MANUAL_ORDER_AUCTION_STRING = "Manual Order Auction";
    private static final String PAR_BROKER_USED_MKT_DATA_STRING = "Par Broker Used Mkt Data";
    private static final String PAR_BROKER_MKT_DATA_STRING = "Par Broker Mkt Data";
    private static final String PAR_PRINT_INTRA_DAY_STRING = "Par Print Intra-day";
    private static final String PAR_PRINT_END_OF_DAY_STRING = "Par Print End of Day";
    private static final String PAR_BROKER_LEG_MKT_STRING = "Par Broker Leg Mkt";
    private static final String MANUAL_FILL_REJECT_STRING = "Manual Fill Reject";

    private static final String MANUAL_ORDER_TA_TIMEOUT_STRING = "Manual Order TA Timeout";
    private static final String MANUAL_ORDER_TB_TIMEOUT_STRING = "Manual Order TB Timeout";
    private static final String MANUAL_ORDER_BOOK_TIMEOUT_STRING = "Manual Order Book Timeout";
    private static final String MANUAL_ORDER_AUCTION_TIMEOUT_STRING = "Manual Order Auction Timeout";
    private static final String MANUAL_ORDER_LINKAGE_TIMEOUT_STRING = "Manual Order Linkage Timeout";
    private static final String MANUAL_FILL_TIMEOUT_STRING = "Manual Fill Timeout";
    private static final String MANUAL_FILL_LINKAGE_TIMEOUT_STRING = "Manual Fill Linkage Timeout";

    private static final String MANUAL_ORDER_REROUTE_REQUEST_STRING = "Manual Order Reroute Request";
    private static final String MANUAL_ORDER_REROUTE_CROWD_REQUEST_STRING = "Manual Order Reroute Crowd Request";
    private static final String MANUAL_FILL_REJECT_FAILURE_STRING = "Manual Fill Reject Failure";
    private static final String MANUAL_ORDER_TA_TIMEOUT_FAILURE_STRING = "Manual Order TA Timeout Failure";
    private static final String MANUAL_ORDER_TB_TIMEOUT_FAILURE_STRING = "Manual Order TB Timeout Failure";
    private static final String MANUAL_ORDER_BOOK_TIMEOUT_FAILURE_STRING = "Manual Order Book Timeout Failure";
    private static final String MANUAL_ORDER_AUCTION_TIMEOUT_FAILURE_STRING = "Manual Order Auction Timeout Failure";
    private static final String MANUAL_FILL_TIMEOUT_FAILURE_STRING = "Manual Fill Timeout Failure";
    private static final String MANUAL_FILL_LINKAGE_TIMEOUT_FAILURE_STRING = "Manual Fill Linkage Timeout Failure";
    private static final String MANUAL_FILL_TIMEOUT_STRATEGY_LEG_STRING = "Manual Fill Timeout Strategy Leg";
    private static final String MANUAL_FILL_REJECT_STRATEGY_LEG_STRING = "Manual Fill Reject Strategy Leg";

    private static final String MANUAL_TA_TIMEOUT_STRATEGY_LEG_STRING = "Manual TA Timeout Strategy Leg";
    private static final String MANUAL_BOOK_TIMEOUT_STRATEGY_LEG_STRING =  "Manual Book Timeout Strategy Leg";
    private static final String MANUAL_AUCTION_TIMEOUT_STRATEGY_LEG_STRING = "Manual Auction Timeout Strategy Leg";

    private static final String FORCED_LOGOFF_PAR_STRING = "Forced Logoff PAR";
    private static final String FORCED_LOGOFF_PAR_FAILURE_STRING = "Forced Logoff PAR Failure";

    private static final String NON_ORDER_MESSAGE_REROUTE_STRING = "Non-Order Message Reroute";

    //Directed AIM
    private static final String DIRECTED_AIM_NOTIFICATION_START_STRING = "Directed AIM Notification Start";
    private static final String DIRECTED_AIM_NOTIFICATION_END_STRING = "Directed AIM Notification End";

    // added at request of CPS Phase 2 project
    private static final String CPS_SPLIT_ORDER_TIMEOUT_STRING = "Split Order Timeout";
    private static final String CPS_SPLIT_ORDER_CANCEL_REQUEST_REJECT_STRING = "Split Order Cancel Request Reject";
    private static final String CPS_SPLIT_ORDER_CANCEL_REPLACE_REJECT_STRING = "Split Order Cancel Replace Reject";
    private static final String CPS_SPLIT_DERIVED_ORDER_NEW_STRING = "New Split Derived Order";
    private static final String CPS_SPLIT_DERIVED_ORDER_FILL_STRING = "Split Derived Order Filled";
    private static final String CPS_SPLIT_DERIVED_ORDER_CANCEL_STRING = "Split Derived Order Cancelled";

//*** Public Methods

    /*****************************************************************************
     * Returns a string representation of the object in TRADERS_FORMAT format
     *
     * @param activityEvent - the activity event code to render (see defined constants)
     * @return a string representation of the activityEvent
     * @see com.cboe.idl.cmiConstants.ActivityTypes
     */
    public static String toString( short activityEvent )
    {
        return toString( activityEvent, TRADERS_FORMAT );
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param activityEvent - the activity event code to render (see defined constants)
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the activityEvent
     * @see com.cboe.idl.cmiConstants.ActivityTypes
     */
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod", "MethodWithMultipleReturnPoints"})
    public static String toString( short activityEvent, String formatSpecifier )
    {
        if( formatSpecifier.equals( TRADERS_FORMAT ))
        {
            switch( activityEvent )
            {
                case BOOK_STRATEGY_LEG:
                    return BOOK_STRATEGY_LEG_STRING;
                case BOOK_ORDER:
                    return ENTERED_INTO_BOOK_STRING;
                case BUST_ORDER_FILL:
                case BUST_QUOTE_FILL:
                    return TRADE_BUST_STRING;
                case BUST_REINSTATE_ORDER:
                    return BUST_REINSTATE_STRING;
                case CANCEL_ALL_ORDERS:
                    return CANCEL_ALL_ORDERS_STRING;
                case CANCEL_ALL_QUOTES:
                    return CANCEL_ALL_QUOTES_STRING;
                case CANCEL_ORDER:
                    return CANCEL_STRING;
                case CANCEL_QUOTE:
                    return CANCEL_QUOTE_STRING;
                case CANCEL_REPLACE_ORDER:
                    return CANCEL_REPLACE_STRING;
                case CANCEL_REPLACE_ORDER_REQUEST_FAILED_ROUTE:
                    return CANCEL_REPLACE_ORDER_REQUEST_FAILED_ROUTE_STRING;
                case CANCEL_REQUEST_ROUTED:
                    return CANCEL_REQUEST_ROUTED_STRING;
                case CANCEL_REQUEST_FAILED_ROUTE:
                    return CANCEL_REQUEST_FAILED_ROUTE_STRING;
                case CROSSING_ORDER_ROUTED:
                    return CROSSING_ORDER_ROUTED_STRING;
                case FAILED_ROUTE:
                    return FAILED_ROUTE_STRING;
                case FILL_ORDER:
                case FILL_QUOTE:
                    return EXECUTION_STRING;
                case LINKAGE_ORDER_RELATIONSHIP:
                    return LINKAGE_ORDER_RELATIONSHIP_STRING;
                case EXECUTION_REPORT_ON_LINKED_ORDER:
                    return EXECUTION_REPORT_ON_LINKED_ORDER_STRING;
                case EXECUTION_REPORT_ROUTED:
                    return EXECUTION_REPORT_ROUTED_STRING;
                case EXECUTION_REPORT_FAILED_ROUTE:
                    return EXECUTION_REPORT_FAILED_ROUTE_STRING;
                case AWAY_EXCHANGE_MARKET:
                    return AWAY_EXCHANGE_MARKET_STRING;
                case LINKAGE_DISQUALIFIED_EXCHANGE:
                    return LINKAGE_DISQUALIFIED_EXCHANGE_STRING;
                case AUDIT_HISTORY_EVENT:
                    return AUDIT_HISTORY_EVENT_STRING;    
                case NEW_ORDER:
                    return NEW_ORDER_STRING;
                case NEW_QUOTE:
                case UPDATE_QUOTE:
                    return SENT_QUOTE_STRING;
                case NEW_RFQ:
                    return RFQ_STRING;
                case ORDER_ROUTED:
                    return ORDER_ROUTED_STRING;
                case PRICE_ADJUST_ORDER:
                    return PRICE_ADJUST_STRING;
                case STATE_CHANGE_ORDER:
                    return STATE_CHANGE_STRING;
                case SYSTEM_CANCEL_QUOTE:
                    return SYSTEM_CANCEL_QUOTE_STRING;
                case UPDATE_ORDER:
                    return ORDER_MAINTENANCE_STRING;
                case NEW_ORDER_STRATEGY_LEG:
                    return NEW_ORDER_STRATEGY_LEG_STRING;
                case FILL_STRATEGY_LEG:
                    return FILL_STRATEGY_LEG_STRING;
                case CANCEL_STRATEGY_LEG:
                    return CANCEL_STRATEGY_LEG_STRING;
                case BUST_STRATEGY_LEG_FILL:
                    return BUST_STRATEGY_LEG_FILL_STRING;
                case BUST_REINSTATE_STRATEGY_LEG:
                    return BUST_REINSTATE_STRATEGY_LEG_STRING;
                case UPDATE_STRATEGY_LEG:
                    return UPDATE_STRATEGY_LEG_STRING;
                case PRICE_ADJUST_ORDER_LEG:
                    return PRICE_ADJUST_ORDER_LEG_STRING;
                case QUOTE_LEG_FILL:
                    return QUOTE_LEG_FILL_STRING;
                case BUST_QUOTE_LEG_FILL:
                    return BUST_QUOTE_LEG_FILL_STRING;
                case HELD_FOR_IPP_PROTECTION:
                    return HELD_FOR_IPP_PROTECTION_STRING;
                case CANCEL_REPLACE_ORDER_REQUEST:
                    return CANCEL_REPLACE_ORDER_REQUEST_STRING;
                case NEW_ORDER_REJECT:
                    return NEW_ORDER_REJECT_STRING;
                case FILL_REJECT:
                    return FILL_REJECT_STRING;
                case CANCEL_ORDER_REQUEST:
                    return CANCEL_ORDER_REQUEST_STRING;
                case CANCEL_ORDER_REQUEST_REJECT:
                    return CANCEL_ORDER_REQUEST_REJECT_STRING;
                case CANCEL_REPORT_REJECT:
                    return CANCEL_REPORT_REJECT_STRING;
                case NEW_ORDER_REJECT_REJECTED:
                    return NEW_ORDER_REJECT_REJECTED_STRING;
                case FILL_REJECT_REJECTED:
                    return FILL_REJECT_REJECTED_STRING;
                case CANCEL_ORDER_REQUEST_REJECT_REJECTED:
                    return CANCEL_ORDER_REQUEST_REJECT_REJECTED_STRING;
                case CANCEL_REPORT_REJECT_REJECTED:
                    return CANCEL_REPORT_REJECT_REJECTED_STRING;
                case ROUTE_TO_AWAY_EXCHANGE:
                    return ROUTE_TO_AWAY_EXCHANGE_STRING;
                case AUCTION_START:
                    return AUCTION_START_STRING;
                case AUCTION_TRIGGER_START:
                    return AUCTION_TRIGGER_START_STRING;
                case AUCTION_END:
                    return AUCTION_END_STRING;
                case AUCTION_TRIGGER_END:
                    return AUCTION_TRIGGER_END_STRING;
                case TSB_REQUEST:
                    return TSB_REQUEST_STRING;
                case VOL_MAINTENANCE:
                    return VOL_MAINTENANCE_STRING;
                case MANUAL_FILL_REJECT:
                    return MANUAL_FILL_REJECT_STRING;
                case MANUAL_FILL_TIMEOUT:
                    return MANUAL_FILL_TIMEOUT_STRING;
                case MANUAL_FILL_LINKAGE_TIMEOUT:
                    return MANUAL_FILL_LINKAGE_TIMEOUT_STRING;
                case MANUAL_ORDER_TA:
                    return MANUAL_ORDER_TA_STRING;
                case MANUAL_ORDER_TB:
                    return MANUAL_ORDER_TB_STRING;
                case MANUAL_ORDER_TA_TIMEOUT:
                    return MANUAL_ORDER_TA_TIMEOUT_STRING;
                case MANUAL_ORDER_TB_TIMEOUT:
                    return MANUAL_ORDER_TB_TIMEOUT_STRING;
                case MANUAL_ORDER_BOOK:
                    return MANUAL_ORDER_BOOK_STRING;
                case MANUAL_ORDER_BOOK_TIMEOUT:
                    return MANUAL_ORDER_BOOK_TIMEOUT_STRING;
                case MANUAL_ORDER_AUCTION:
                    return MANUAL_ORDER_AUCTION_STRING;
                case MANUAL_ORDER_AUCTION_TIMEOUT:
                    return MANUAL_ORDER_AUCTION_TIMEOUT_STRING;
                case MANUAL_ORDER_LINKAGE_TIMEOUT:
                    return MANUAL_ORDER_LINKAGE_TIMEOUT_STRING;
                case MANUAL_ORDER_REROUTE_REQUEST:
                    return MANUAL_ORDER_REROUTE_REQUEST_STRING;
                case MANUAL_ORDER_REROUTE_CROWD_REQUEST:
                    return MANUAL_ORDER_REROUTE_CROWD_REQUEST_STRING;
                case MANUAL_FILL_REJECT_FAILURE:
                     return MANUAL_FILL_REJECT_FAILURE_STRING;
                case MANUAL_ORDER_TA_TIMEOUT_FAILURE:
                    return MANUAL_ORDER_TA_TIMEOUT_FAILURE_STRING;
                case MANUAL_ORDER_TB_TIMEOUT_FAILURE:
                    return MANUAL_ORDER_TB_TIMEOUT_FAILURE_STRING;
                case MANUAL_ORDER_BOOK_TIMEOUT_FAILURE:
                    return MANUAL_ORDER_BOOK_TIMEOUT_FAILURE_STRING;
                case MANUAL_ORDER_AUCTION_TIMEOUT_FAILURE:
                    return MANUAL_ORDER_AUCTION_TIMEOUT_FAILURE_STRING;
                case MANUAL_FILL_TIMEOUT_FAILURE:
                    return MANUAL_FILL_TIMEOUT_FAILURE_STRING;
                case MANUAL_FILL_LINKAGE_TIMEOUT_FAILURE:
                    return MANUAL_FILL_LINKAGE_TIMEOUT_FAILURE_STRING;
                case PAR_BROKER_USED_MKT_DATA:
                    return PAR_BROKER_USED_MKT_DATA_STRING;
                case MANUAL_FILL_TIMEOUT_STRATEGY_LEG:
                    return MANUAL_FILL_TIMEOUT_STRATEGY_LEG_STRING;
                case MANUAL_FILL_REJECT_STRATEGY_LEG:
                    return MANUAL_FILL_REJECT_STRATEGY_LEG_STRING;
                case PAR_BROKER_MKT_DATA:
                    return PAR_BROKER_MKT_DATA_STRING;
                case PAR_PRINT_INTRA_DAY:
                    return PAR_PRINT_INTRA_DAY_STRING;
                case PAR_PRINT_END_OF_DAY:
                    return PAR_PRINT_END_OF_DAY_STRING;
                case HYBRID_PROCESSING_REQUESTED:
                    return HYBRID_PROCESSING_REQUESTED_STRING;
                case HYBRID_REQUEST_RETURNED:
                    return HYBRID_REQUEST_RETURNED_STRING;
                case OMT_DISPLAY_ROUTED_AWAY:
                    return OMT_DISPLAY_ROUTED_AWAY_STRING;
                case FORCED_LOGOFF_PAR:
                    return FORCED_LOGOFF_PAR_STRING;
                case FORCED_LOGOFF_PAR_FAILURE:
                    return FORCED_LOGOFF_PAR_FAILURE_STRING;
                case INBOUND_S_ORDER_FILL:
                    return INBOUND_S_ORDER_FILL_STRING;
                case MANUAL_TA_TIMEOUT_STRATEGY_LEG:
                    return MANUAL_TA_TIMEOUT_STRATEGY_LEG_STRING;
                case MANUAL_BOOK_TIMEOUT_STRATEGY_LEG:
                    return MANUAL_BOOK_TIMEOUT_STRATEGY_LEG_STRING;
                case MANUAL_AUCTION_TIMEOUT_STRATEGY_LEG:
                    return MANUAL_AUCTION_TIMEOUT_STRATEGY_LEG_STRING;
                case NON_ORDER_MESSAGE_REROUTE:
                    return NON_ORDER_MESSAGE_REROUTE_STRING;
                case PAR_BROKER_LEG_MKT:
                    return PAR_BROKER_LEG_MKT_STRING;
                case MANUAL_ORDER_SR:
                    return MANUAL_ORDER_SR_STRING;
                case MANUAL_ORDER_SR_TIMEOUT:
                    return MANUAL_ORDER_SR_TIMEOUT_STRING;
                case MANUAL_ORDER_SR_TIMEOUT_FAILURE:
                    return MANUAL_ORDER_SR_TIMEOUT_FAILURE_STRING;
                case MANUAL_ORDER_FR:
                    return MANUAL_ORDER_FR_STRING;
                case MANUAL_ORDER_FR_TIMEOUT:
                    return MANUAL_ORDER_FR_TIMEOUT_STRING;
                case MANUAL_ORDER_FR_TIMEOUT_FAILURE:
                    return MANUAL_ORDER_FR_TIMEOUT_FAILURE_STRING;
                    
                case DIRECTED_AIM_NOTIFICATION_START:
                    return DIRECTED_AIM_NOTIFICATION_START_STRING;
                case DIRECTED_AIM_NOTIFICATION_END:
                    return DIRECTED_AIM_NOTIFICATION_END_STRING;

                case CPS_SPLIT_ORDER_TIMEOUT:
                    return CPS_SPLIT_ORDER_TIMEOUT_STRING;
                case CPS_SPLIT_ORDER_CANCEL_REQUEST_REJECT:
                    return CPS_SPLIT_ORDER_CANCEL_REQUEST_REJECT_STRING;
                case CPS_SPLIT_ORDER_CANCEL_REPLACE_REJECT:
                    return CPS_SPLIT_ORDER_CANCEL_REPLACE_REJECT_STRING;
                case CPS_SPLIT_DERIVED_ORDER_NEW:
                    return CPS_SPLIT_DERIVED_ORDER_NEW_STRING;
                case CPS_SPLIT_DERIVED_ORDER_FILL:
                    return CPS_SPLIT_DERIVED_ORDER_FILL_STRING;
                case CPS_SPLIT_DERIVED_ORDER_CANCEL:
                    return CPS_SPLIT_DERIVED_ORDER_CANCEL_STRING;

                default:
//                    return new StringBuffer().append(INVALID_TYPE).append(" ").append(activityEvent).toString();
                      return new StringBuffer(20).append("[ ").append(activityEvent).append(" ]").toString();
            }
        }
        return INVALID_FORMAT;
    }


//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private ActivityTypes( )
    {
    }

}
