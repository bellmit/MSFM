// -----------------------------------------------------------------------------------
// Source file: CancelReasonFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiConstants.ActivityReasons;
import com.cboe.interfaces.presentation.common.formatters.CancelReasonFormatStrategy;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Implements the ProductTypeFormatStrategy
 */
class CancelReasonFormatter extends Formatter implements CancelReasonFormatStrategy
{
    public static final String INSUFFICIENT_QUANTITY_STRING = "Insufficient Quantity";
    public static final String LOST_CONNECTION_STRING = "Lost Connection";
    public static final String NOTHING_DONE_STRING = "Nothing Done";
    public static final String QRM_REMOVED_STRING = "QRM Removed";
    public static final String SPECIAL_ADJUSTMENT_STRING = "Special Adjustment";
    public static final String SYSTEM_STRING = "System";
    public static final String USER_STRING = "User";
    public static final String NO_USER_ACTIVITY = "No User Activity";
    public static final String UNKNOWN_STRING = "Unknown";
    public static final String FAILED_OVER_STRING = "Failed Over";

    public static final String CROSS_IN_PROGRESS_STRING = "Cross In Progress";
    public static final String INVALID_NBBO_STRING = "Invalid NBBO";
    public static final String NOT_WITHIN_NBBO_STRING = "Not Within NBBO";
    public static final String TRADE_THROUGH_CBOE_STRING = "Trade Through CBOE";
    public static final String INSUFFICIENT_CUSTOMER_ORDER_QUANTITY_STRING = "Insufficient Customer Order Qty";
    public static final String INSUFFICIENT_CROSS_ORDER_SIZE_STRING = "Insufficient Cross Order Size";
    public static final String INSUFFICIENT_CROSS_ORDER_DOLLAR_AMOUNT_STRING = "Insufficient Cross Order Dollar Amount";
    public static final String SELL_SHORT_RULE_VIOLATION_STRING = "Sell Short Rule Violation";
    public static final String CANCEL_ON_RSS_STRING = "Cancel On RSS";
    public static final String CANCEL_ON_FALLBACK_STRING = "Cancel On Fallback";

    public static final String INSUFFICIENT_QUANTITY_BUY_SIDE_STRING = "Insufficient Quantity Buy Side";
    public static final String INSUFFICIENT_QUANTITY_SELL_SIDE_STRING = "Insufficient Quantity Sell Side";

    // The following are used for Linkage
    public static final String BROKER_OPTION_STRING = "Broker Option";
    public static final String CANCEL_PENDING_STRING = "Cancel Pending";
    public static final String CROWD_TRADE_STRING = "Crowd Trade";
    public static final String DUPLICATE_ORDER_STRING = "Duplicate Order";
    public static final String EXCHANGE_CLOSED_STRING = "Exchange Closed";
    public static final String GATE_VIOLATION_STRING = "Gate Violation";
    public static final String INVALID_ACCOUNT_STRING = "Invalid Account";
    public static final String INVALID_AUTOEX_VALUE_STRING = "Invalid Autoex Value";
    public static final String INVALID_CMTA_STRING = "Invalid CMTA";
    public static final String INVALID_FIRM_STRING = "Invalid Firm";
    public static final String INVALID_ORIGIN_TYPE_STRING = "Invalid Origin Type";
    public static final String INVALID_POSITION_EFFECT_STRING = "Invalid Position Effect";
    public static final String INVALID_PRICE_STRING = "Invalid Price";
    public static final String INVALID_PRODUCT_STRING = "Invalid Product";
    public static final String INVALID_PRODUCT_TYPE_STRING = "Invalid Product Type";
    public static final String INVALID_QUANTITY_STRING = "Invalid Quantity";
    public static final String INVALID_SIDE_STRING = "Invalid Side";
    public static final String INVALID_SUBACCOUNT_STRING = "Invalid Subaccount";
    public static final String INVALID_TIME_IN_FORCE_STRING = "Invalid Time In Force";
    public static final String INVALID_USER_STRING = "Invalid User";
    public static final String LATE_PRINT_STRING = "Late Print";
    public static final String NOT_FIRM_STRING = "Not Firm";
    public static final String MISSING_EXEC_INFO_STRING = "Missing Exec Info";
    public static final String NO_MATCHING_ORDER_STRING = "No Matching Order";
    public static final String NON_BLOCK_TRADE_STRING = "Non Block Trade";
    public static final String NOT_NBBO_STRING = "Not NBBO";
    public static final String COMM_DELAYS_STRING = "Comm Delays";
    public static final String ORIGINAL_ORDER_REJECTED_STRING = "Original Order Rejected";
    public static final String OTHER_STRING = "Other";
    public static final String PROCESSING_PROBLEMS_STRING = "Processing Problems";
    public static final String PRODUCT_HALTED_STRING = "Product Halted";
    public static final String PRODUCT_IN_ROTATION_STRING = "Product In Rotation";
    public static final String STALE_EXECUTION_STRING = "Stale Execution";
    public static final String STALE_ORDER_STRING = "Stale Order";
    public static final String ORDER_TOO_LATE_STRING = "Order Too Late";
    public static final String TRADE_BUSTED_STRING = "Trade Busted";
    public static final String TRADE_REJECTED_STRING = "Trade Rejected";
    public static final String PRODUCT_SUSPENDED_STRING = "Product Suspended";
    public static final String ORDER_TIMEOUT_STRING  = "Order Timeout";
    public static final String REJECTED_LINKAGE_TRADE_STRING = "Rejected Linkage Trade";
    public static final String SATISFACTION_ORD_REJ_OTHER_STRING = "Satisfaction Order Rejected Other";

    // Currently used for TPF linkage; in future may be used for CBOEdirect
    public static final String UNKNOWN_ORDER_STRING = "Unknown Order";
    public static final String INVALD_EXCHANGE_STRING = "Invalid Exchange";
    public static final String TRANSACTION_FAILED_STRING = "Transaction Failed";
    public static final String NOT_ACCEPTED_STRING = "Not Accepted";

    // Used for linkage when cancel reason is not provided (could be user cancel or cancel remaining)
    public static final String AWAY_EXCHANGE_CANCEL_STRING = "Away Exchange Cancel";

    // Linkage Business Message Reject codes
    public static final String LINKAGE_CONDITIONAL_FIELD_MISSING_STRING = "Linkage Conditional Field Missing";
    public static final String LINKAGE_EXCHANGE_UNAVAILABLE_STRING = "Linkage Exchange Unavailable";
    public static final String LINKAGE_INVALID_MESSAGE_STRING = "Linkage Invalid Message";
    public static final String LINKAGE_INVALID_DESTINATION_STRING = "Linkage Invalid Destination";
    public static final String LINKAGE_INVALID_PRODUCT_STRING = "Linkage Invalid Product";
    public static final String LINKAGE_SESSION_REJECT_STRING = "Linkage Session Reject";

    public static final String QUOTE_UPDATE_CONTROL_STRING = "Quote Update Control";
    public static final String INVALID_SESSION_ID_STRING = "Invalid Session ID";

    // cmi V3 additions
    public static final String QUOTE_IN_TRIGGER_STRING = "Quote In Trigger";

	// SAL addition
	public static final String SAL_IN_PROGRESS_STRING = "SAL In Progress";

    public static final String WASH_TRADE_PREVENTION_STRING = "Wash Trade Prevention";

    /**
     * Constructor, defines styles and sets initial default style
     */
    public CancelReasonFormatter()
    {
        addStyle(FULL_CANCEL_REASON, FULL_CANCEL_REASON_DESCRIPTION);
        setDefaultStyle(FULL_CANCEL_REASON);
    }

    /**
     * Formats a Cancel Reason using default style
     * @param cancelReason short to format
     */
    public String format(short cancelReason)
    {
        return format(cancelReason, getDefaultStyle());
    }

    /**
     * Formats a Cancel Reason
     * @param style String - formatting style to use
     * @param cancelReason short to format
     */
    public String format(short cancelReason, String style) throws IllegalArgumentException
    {
        String cancelReasonText = null;

        if ( ! containsStyle(style) )
        {
            throw new IllegalArgumentException("CancelReasonFormatter - Unknown Style");
        }
        if(style.equals(FULL_CANCEL_REASON))
        {
            cancelReasonText = formatFullCancelReason(cancelReason);
        }
        return cancelReasonText;
    }

    /**
     * Formats a Cancel Reason using FULL_CANCEL_REASON style
     * @param cancelReason short to format
     */
    private String formatFullCancelReason(short cancelReason)
    {
        String text;
        switch (cancelReason)
        {
            case ActivityReasons.INSUFFICIENT_QUANTITY:
                text = INSUFFICIENT_QUANTITY_STRING;
                break;
            case ActivityReasons.LOST_CONNECTION:
                text = LOST_CONNECTION_STRING;
                break;
            case ActivityReasons.NOTHING_DONE:
                text = NOTHING_DONE_STRING;
                break;
            case ActivityReasons.QRM_REMOVED:
                text = QRM_REMOVED_STRING;
                break;
            case ActivityReasons.SPECIAL_ADJUSTMENT:
                text = SPECIAL_ADJUSTMENT_STRING;
                break;
            case ActivityReasons.SYSTEM:
                text = SYSTEM_STRING;
                break;
            case ActivityReasons.USER:
                text = USER_STRING;
                break;
            case ActivityReasons.NO_USER_ACTIVITY:
                text = NO_USER_ACTIVITY;
                break;
            case ActivityReasons.FAILOVER:
                text = FAILED_OVER_STRING;
                break;
            case ActivityReasons.INSUFFICIENT_QUANTITY_BUY_SIDE:
                text = INSUFFICIENT_QUANTITY_BUY_SIDE_STRING;
                break;
            case ActivityReasons.INSUFFICIENT_QUANTITY_SELL_SIDE:
                text = INSUFFICIENT_QUANTITY_SELL_SIDE_STRING;
                break;
            case ActivityReasons.BROKER_OPTION:
                text = BROKER_OPTION_STRING;
                break;
            case ActivityReasons.CANCEL_PENDING:
                text = CANCEL_PENDING_STRING;
                break;
            case ActivityReasons.CROWD_TRADE:
                text = CROWD_TRADE_STRING;
                break;
            case ActivityReasons.DUPLICATE_ORDER:
                text = DUPLICATE_ORDER_STRING;
                break;
            case ActivityReasons.EXCHANGE_CLOSED:
                text = EXCHANGE_CLOSED_STRING;
                break;
            case ActivityReasons.GATE_VIOLATION:
                text = GATE_VIOLATION_STRING;
                break;
            case ActivityReasons.INVALID_ACCOUNT:
                text = INVALID_ACCOUNT_STRING;
                break;
            case ActivityReasons.INVALID_AUTOEX_VALUE:
                text = INVALID_AUTOEX_VALUE_STRING;
                break;
            case ActivityReasons.INVALID_CMTA:
                text = INVALID_CMTA_STRING;
                break;
            case ActivityReasons.INVALID_FIRM:
                text = INVALID_FIRM_STRING;
                break;
            case ActivityReasons.INVALID_ORIGIN_TYPE:
                text = INVALID_ORIGIN_TYPE_STRING;
                break;
            case ActivityReasons.INVALID_POSITION_EFFECT:
                text = INVALID_POSITION_EFFECT_STRING;
                break;
            case ActivityReasons.INVALID_PRICE:
                text = INVALID_PRICE_STRING;
                break;
            case ActivityReasons.INVALID_PRODUCT:
                text = INVALID_PRODUCT_STRING;
                break;
            case ActivityReasons.INVALID_PRODUCT_TYPE:
                text = INVALID_PRODUCT_TYPE_STRING;
                break;
            case ActivityReasons.INVALID_QUANTITY:
                text = INVALID_QUANTITY_STRING;
                break;
            case ActivityReasons.INVALID_SIDE:
                text = INVALID_SIDE_STRING;
                break;
            case ActivityReasons.INVALID_SUBACCOUNT:
                text = INVALID_SUBACCOUNT_STRING;
                break;
            case ActivityReasons.INVALID_TIME_IN_FORCE:
                text = INVALID_TIME_IN_FORCE_STRING;
                break;
            case ActivityReasons.INVALID_USER:
                text = INVALID_USER_STRING;
                break;
            case ActivityReasons.LATE_PRINT:
                text = LATE_PRINT_STRING;
                break;
            case ActivityReasons.NOT_FIRM:
                text = NOT_FIRM_STRING;
                break;
            case ActivityReasons.MISSING_EXEC_INFO:
                text = MISSING_EXEC_INFO_STRING;
                break;
            case ActivityReasons.NO_MATCHING_ORDER:
                text = NO_MATCHING_ORDER_STRING;
                break;
            case ActivityReasons.NON_BLOCK_TRADE:
                text = NON_BLOCK_TRADE_STRING;
                break;
            case ActivityReasons.NOT_NBBO:
                text = NOT_NBBO_STRING;
                break;
            case ActivityReasons.COMM_DELAYS:
                text = COMM_DELAYS_STRING;
                break;
            case ActivityReasons.ORIGINAL_ORDER_REJECTED:
                text = ORIGINAL_ORDER_REJECTED_STRING;
                break;
            case ActivityReasons.SAL_IN_PROGRESS:
                text = SAL_IN_PROGRESS_STRING;
                break;
            case ActivityReasons.OTHER:
                text = OTHER_STRING;
                break;
            case ActivityReasons.PROCESSING_PROBLEMS:
                text = PROCESSING_PROBLEMS_STRING;
                break;
            case ActivityReasons.PRODUCT_HALTED:
                text = PRODUCT_HALTED_STRING;
                break;
            case ActivityReasons.PRODUCT_IN_ROTATION:
                text = PRODUCT_IN_ROTATION_STRING;
                break;
            case ActivityReasons.STALE_EXECUTION:
                text = STALE_EXECUTION_STRING;
                break;
            case ActivityReasons.STALE_ORDER:
                text = STALE_ORDER_STRING;
                break;
            case ActivityReasons.ORDER_TOO_LATE:
                text = ORDER_TOO_LATE_STRING;
                break;
            case ActivityReasons.TRADE_BUSTED:
                text = TRADE_BUSTED_STRING;
                break;
            case ActivityReasons.TRADE_REJECTED:
                text = TRADE_REJECTED_STRING;
                break;
            case ActivityReasons.PRODUCT_SUSPENDED:
                text = PRODUCT_SUSPENDED_STRING;
                break;
            case ActivityReasons.UNKNOWN_ORDER:
                text = UNKNOWN_ORDER_STRING;
                break;
            case ActivityReasons.INVALD_EXCHANGE:
                text = INVALD_EXCHANGE_STRING;
                break;
            case ActivityReasons.TRANSACTION_FAILED:
                text = TRANSACTION_FAILED_STRING;
                break;
            case ActivityReasons.NOT_ACCEPTED:
                text = NOT_ACCEPTED_STRING;
                break;
            case ActivityReasons.AWAY_EXCHANGE_CANCEL:
                text = AWAY_EXCHANGE_CANCEL_STRING;
                break;
            case ActivityReasons.LINKAGE_CONDITIONAL_FIELD_MISSING:
                text = LINKAGE_CONDITIONAL_FIELD_MISSING_STRING;
                break;
            case ActivityReasons.LINKAGE_EXCHANGE_UNAVAILABLE:
                text = LINKAGE_EXCHANGE_UNAVAILABLE_STRING;
                break;
            case ActivityReasons.LINKAGE_INVALID_MESSAGE:
                text = LINKAGE_INVALID_MESSAGE_STRING;
                break;
            case ActivityReasons.LINKAGE_INVALID_DESTINATION:
                text = LINKAGE_INVALID_DESTINATION_STRING;
                break;
            case ActivityReasons.LINKAGE_INVALID_PRODUCT:
                text = LINKAGE_INVALID_PRODUCT_STRING;
                break;
            case ActivityReasons.LINKAGE_SESSION_REJECT:
                text = LINKAGE_SESSION_REJECT_STRING;
                break;
            case ActivityReasons.ORDER_TIMEOUT:
                text = ORDER_TIMEOUT_STRING ;
                break;
            case ActivityReasons.REJECTED_LINKAGE_TRADE:
                text = REJECTED_LINKAGE_TRADE_STRING;
                break;
            case ActivityReasons.SATISFACTION_ORD_REJ_OTHER:
                text = SATISFACTION_ORD_REJ_OTHER_STRING;
                break;
            case ActivityReasons.QUOTE_IN_TRIGGER:
                text = QUOTE_IN_TRIGGER_STRING;
                break;
            case ActivityReasons.QUOTE_UPDATE_CONTROL:
                text = QUOTE_UPDATE_CONTROL_STRING;
                break;
            case ActivityReasons.INVALID_SESSION_ID:
                text = INVALID_SESSION_ID_STRING;
                break;
            case ActivityReasons.CROSS_IN_PROGRESS:
                text = CROSS_IN_PROGRESS_STRING;
                break;
            case ActivityReasons.INVALID_NBBO:
                text = INVALID_NBBO_STRING;
                break;
            case ActivityReasons.NOT_WITHIN_NBBO:
                text = NOT_WITHIN_NBBO_STRING;
                break;
            case ActivityReasons.TRADE_THROUGH_CBOE:
                text = TRADE_THROUGH_CBOE_STRING;
                break;
            case ActivityReasons.INSUFFICIENT_CUSTOMER_ORDER_QUANTITY:
                text = INSUFFICIENT_CUSTOMER_ORDER_QUANTITY_STRING;
                break;
            case ActivityReasons.INSUFFICIENT_CROSS_ORDER_SIZE:
                text = INSUFFICIENT_CROSS_ORDER_SIZE_STRING;
                break;
            case ActivityReasons.INSUFFICIENT_CROSS_ORDER_DOLLAR_AMOUNT:
                text = INSUFFICIENT_CROSS_ORDER_DOLLAR_AMOUNT_STRING;
                break;
            case ActivityReasons.SELL_SHORT_RULE_VIOLATION:
                text = SELL_SHORT_RULE_VIOLATION_STRING;
                break;
            case ActivityReasons.CANCEL_ON_RSS:
                text = CANCEL_ON_RSS_STRING;
                break;
            case ActivityReasons.CANCEL_ON_FALLBACK:
                text = CANCEL_ON_FALLBACK_STRING;
                break;
            case ActivityReasons.WASH_TRADE_PREVENTION:
                text = WASH_TRADE_PREVENTION_STRING;
                break;
            default:
                text = new StringBuffer(20).append(UNKNOWN_STRING).append("[ ").append(cancelReason).append(" ]").toString();
                if(cancelReason != 0)
                {
                    GUILoggerHome.find().alarm("CancelReasonFormatter - Unknown Cancel Reason - " + cancelReason);
                }
                break;
        }
        return text;
    }
}
