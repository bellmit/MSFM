/*
 * Created by IntelliJ IDEA.
 * User: Brazhni
 * Date: Nov 13, 2002
 * Time: 4:20:53 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.presentation.permissionMatrix;

import com.cboe.interfaces.presentation.common.formatters.PermissionFormatStrategy;

@SuppressWarnings({"EnumeratedConstantNamingConvention"})
public enum Permission
{
	ACTIVITY_LOG,
    AGENT_QUERY_ACCESS,
    BLOCK_TRADES,
    BOOK_DEPTH_ACCESS,
    BOOK_DEPTH_FOR_BOOK,
    BOOK_DEPTH_FOR_DETAILS,
    CANCEL_USER_ORDER,
    CANCEL_ORDERS_BY_BC,
    CANCEL_ORDERS_BY_SELECTING_SPREADSHEET,
    CROSS_ORDER_ENTRY,
    DPM_QUERY,
    EOP_QUERY,
    EXECUTING_GIVE_UP_FIRM_UPDATE,
    EXPRESS_MKT_DISPLAY,
    FIRM_ORDER_SUBSCRIPTION,
    FIRM_QUOTE_SUBSCRIPTION,
    HELD_ORDER_QUERY,
    HELD_ORDER_STATUS_VIEW,
    IQOD,
    LIGHT_ORDER_ENTRY,
    LOGGING_DEFAULTS_DISPLAY,
    MARKET_HISTORY,
    MESSAGE_CENTER_ACCESS,
    MESSAGE_CENTER_CREATE_MESSAGE,
    MESSAGE_CENTER_DELETE,
    MESSAGE_CENTER_READ,
    MISC_DEFAULTS_DISPLAY,
    MISC_FUNCTION_DISPLAY,
    MONTAGE,
    NBBO_AGENT_QUERY,
    ORDER_ENTRY,
    ORDER_MODIFY,
    PRODUCT_CLASS_GROUPS_ACCESS,
    PRODUCT_CLASS_GROUPS_MODIFY,
    PRODUCT_CLASS_GROUPS_VIEW,
    PRODUCT_LOOKUP_DISPLAY,
    PRODUCT_MAINT_ACCESS,
    PRODUCT_MAINT_PRICE_ADJUSTMENTS_ACCESS,
    PRODUCT_MAINT_PRICE_ADJUSTMENTS_MODIFY,
    PRODUCT_MAINT_PRICE_ADJUSTMENTS_VIEW,
    PRODUCT_MAINT_PRODUCT_DEFINITION_ACCESS,
    PRODUCT_MAINT_PRODUCT_DEFINITION_MODIFY,
    PRODUCT_MAINT_PRODUCT_DEFINITION_VIEW,
    PRODUCT_MAINT_PRODUCT_SETTLEMENT_ACCESS,
    PRODUCT_MAINT_PRODUCT_SETTLEMENT_MODIFY,
    PRODUCT_MAINT_PRODUCT_SETTLEMENT_VIEW,
    PRODUCT_STATE_MAINT_ACCESS,
    PRODUCT_STATE_MAINT_BY_PRODUCT_CLASS_MODIFY,
    PRODUCT_STATE_MAINT_BY_PRODUCT_MODIFY,
    PRODUCT_STATE_MAINT_BY_TRADING_SESSION_ELEMENT_MODIFY,
    PRODUCT_STATE_MAINT_BY_TRADING_SESSION_MODIFY,
    PRODUCT_STATE_MAINT_VIEW,
    QUERY,
    QUOTE_ENTRY,
    QUOTE_LOCK_DISPLAY,
    QUOTE_MODIFY,
    QUOTES_DEFAULTS_DISPLAY,
    REGISTER_NBBO_AGENT,
    SESSION_BROWSER_ACCESS,
    SESSION_BROWSER_CAS,
    SESSION_BROWSER_FE,
    SESSION_BROWSER_USER,
    STATUS_DISPLAY,
    SYSTEM_ADMIN_APPLICATION_ACCESS,
    TEXT_MESSAGE_VIEW,
    TEXT_TO_OPRA_MESSAGE_VIEW,
    TFL_MANAGEMENT_ACCESS,
    TFL_MANAGEMENT_MODIFY,
    TFL_MANAGEMENT_VIEW,
    TRADE_BUST,
    TRADE_QUERY,
    TRADES_LOG,
    TRADER_APPLICATION_ACCESS,
    TRADING_SESSIONS_ACCESS,
    TRADING_SESSIONS_MODIFY,
    TRADING_SESSIONS_VIEW,
    USER_MANAGEMENT_ACCESS,
    USER_MANAGEMENT_BROKER_FIRMS_ACCESS,
    USER_MANAGEMENT_BROKER_FIRMS_MODIFY,
    USER_MANAGEMENT_BROKER_FIRMS_VIEW,
    USER_MANAGEMENT_BROKER_PROFILE_ACCESS,
    USER_MANAGEMENT_BROKER_PROFILE_MODIFY,
    USER_MANAGEMENT_BROKER_PROFILE_VIEW,
    USER_MANAGEMENT_CONTROL_ACCESS,
    USER_MANAGEMENT_CONTROL_MODIFY,
    USER_MANAGEMENT_CONTROL_VIEW,
    USER_MANAGEMENT_CREATE_DELETE_USER,
    USER_MANAGEMENT_DETAILS_ACCESS,
    USER_MANAGEMENT_DETAILS_MODIFY,
    USER_MANAGEMENT_DETAILS_VIEW,
    USER_MANAGEMENT_ENABLEMENTS_ACCESS,
    USER_MANAGEMENT_ENABLEMENTS_MODIFY,
    USER_MANAGEMENT_ENABLEMENTS_VIEW,
    USER_MANAGEMENT_MM_PROFILES_ACCESS,
    USER_MANAGEMENT_MM_PROFILES_MODIFY,
    USER_MANAGEMENT_MM_PROFILES_VIEW,
    USER_MANAGEMENT_ORDERS_ACCESS,
    USER_MANAGEMENT_ORDERS_MODIFY,
    USER_MANAGEMENT_ORDERS_VIEW,
    USER_MANAGEMENT_PREFERENCE_CONVERSION_ACCESS,
    USER_MANAGEMENT_PREFERENCE_CONVERSION_MODIFY,
    USER_MANAGEMENT_PREFERENCE_CONVERSION_VIEW,
    USER_MANAGEMENT_QRM_ACCESS,
    USER_MANAGEMENT_QRM_MODIFY,
    USER_MANAGEMENT_QRM_VIEW,
    USER_MANAGEMENT_RATE_LIMITS_ACCESS,
    USER_MANAGEMENT_RATE_LIMITS_MODIFY,
    USER_MANAGEMENT_RATE_LIMITS_VIEW,
    USER_MANAGEMENT_UPDATE_USER,
    USER_MANAGEMENT_USER_FIRM_AFFILIATION_ACCESS,
    USER_MANAGEMENT_USER_FIRM_AFFILIATION_MODIFY,
    USER_MANAGEMENT_USER_FIRM_AFFILIATION_VIEW,
    USER_MANAGEMENT_USER_GROUP_MANAGMENT_ACCESS,
    USER_MANAGEMENT_USER_GROUP_MANAGMENT_MODIFY,
    USER_MANAGEMENT_USER_GROUP_MANAGMENT_VIEW,
    USER_ORDER_SUBSCRIPTION,
    USER_QUOTE_SUBSCRIPTION,
    OMT_ORDER_QUERY,
    OMT_ORDER_QUERY_LOCATION,
    OMT_ORDER_QUERY_LOCATION_LIMITED,
    OMT_ORDER_QUERY_FIRM,
    OMT_MANUAL_FILL_CANCEL,
    TEXT_TO_OPRA,
    ORDER_MANAGEMENT_TERMINAL_ACCESS,
    OMT_ACTION_DIRECT_REROUTE_OWNED,
    OMT_ACTION_DIRECT_REROUTE_ALL,
    OMT_ACTION_ORDER_ENTRY,
    OMT_ACTION_ORDER_FILL,
    OMT_ACTION_ORDER_UPDATE,
    OMT_ACTION_CLEAR,
    OMT_ACTION_CANCEL,
    OMT_ACTION_CANCEL_OWNER_SOLICITED,
    OMT_ACTION_CANCEL_OWNER_UNSOLICITED,
    OMT_ACTION_CANCEL_NON_OWNER_UNSOLICITED,
    OMT_ACTION_CANCEL_REPLACE,
    OMT_ACTION_CANCEL_REPLACE_OWNER_SOLICITED,
    OMT_ACTION_CANCEL_REPLACE_OWNER_UNSOLICITED,
    OMT_ACTION_CANCEL_REPLACE_NON_OWNER_UNSOLICITED,
    MANUAL_PRICE_REPORT_ACCESS,
    MANUAL_QUOTE_REPORT_ACCESS,
    OMT_DISPLAY_AUDIT_HISTORY,
    OMT_AUTO_REMOVE_CANCELED_ORDER,
    FLOOR_TRADE_MAINTENANCE;

    @SuppressWarnings({"UnnecessaryDefault", "OverlyComplexMethod",
            "OverlyLongMethod", "MethodWithMultipleReturnPoints"})
    public String getDescription()
    {
        switch(this)
        {
	        case ACTIVITY_LOG:
	            return PermissionFormatStrategy.ACTIVITY_LOG_STRING;
            case AGENT_QUERY_ACCESS:
                return PermissionFormatStrategy.AGENT_QUERY_ACCESS_STRING;
            case BLOCK_TRADES:
                return PermissionFormatStrategy.BLOCK_TRADES_STRING;
            case BOOK_DEPTH_ACCESS:
                return PermissionFormatStrategy.BOOK_DEPTH_ACCESS_STRING;
            case BOOK_DEPTH_FOR_BOOK:
                return PermissionFormatStrategy.BOOK_DEPTH_FOR_BOOK_STRING;
            case BOOK_DEPTH_FOR_DETAILS:
                return PermissionFormatStrategy.BOOK_DEPTH_FOR_DETAILS_STRING;
            case CANCEL_USER_ORDER:
                return PermissionFormatStrategy.CANCEL_USER_ORDER_STRING;
            case CANCEL_ORDERS_BY_BC:
                return PermissionFormatStrategy.CANCEL_ORDERS_BY_BC_STRING;
            case CANCEL_ORDERS_BY_SELECTING_SPREADSHEET:
                return PermissionFormatStrategy.CANCEL_ORDERS_BY_SELECTING_SPREADSHEET_STRING;
            case CROSS_ORDER_ENTRY:
                return PermissionFormatStrategy.CROSS_ORDER_ENTRY_STRING;
            case DPM_QUERY:
                return PermissionFormatStrategy.DPM_QUERY_STRING;
            case EOP_QUERY:
                return PermissionFormatStrategy.EOP_QUERY_STRING;
            case EXECUTING_GIVE_UP_FIRM_UPDATE:
                return PermissionFormatStrategy.EXECUTING_GIVE_UP_FIRM_UPDATE_STRING;
	        case EXPRESS_MKT_DISPLAY:
	            return PermissionFormatStrategy.EXPRESS_MKT_DISPLAY_STRING;
            case FIRM_ORDER_SUBSCRIPTION:
                return PermissionFormatStrategy.FIRM_ORDER_SUBSCRIPTION_STRING;
            case FIRM_QUOTE_SUBSCRIPTION:
                return PermissionFormatStrategy.FIRM_QUOTE_SUBSCRIPTION_STRING;
            case HELD_ORDER_QUERY:
                return PermissionFormatStrategy.HELD_ORDER_QUERY_STRING;
            case HELD_ORDER_STATUS_VIEW:
                return PermissionFormatStrategy.HELD_ORDER_STATUS_VIEW_STRING;
            case IQOD:
                return PermissionFormatStrategy.IQOD_STRING;
            case LIGHT_ORDER_ENTRY:
                return PermissionFormatStrategy.LIGHT_ORDER_ENTRY_STRING;
            case LOGGING_DEFAULTS_DISPLAY:
                return PermissionFormatStrategy.LOGGING_DEFAULTS_DISPLAY_STRING;
	        case MARKET_HISTORY:
	            return PermissionFormatStrategy.MARKET_HISTORY_STRING;
            case MESSAGE_CENTER_ACCESS:
                return PermissionFormatStrategy.MESSAGE_CENTER_ACCESS_STRING;
            case MESSAGE_CENTER_CREATE_MESSAGE:
                return PermissionFormatStrategy.MESSAGE_CENTER_CREATE_MESSAGE_STRING;
            case MESSAGE_CENTER_DELETE:
                return PermissionFormatStrategy.MESSAGE_CENTER_DELETE_STRING;
            case MESSAGE_CENTER_READ:
                return PermissionFormatStrategy.MESSAGE_CENTER_READ_STRING;
            case MISC_DEFAULTS_DISPLAY:
                return PermissionFormatStrategy.MISC_DEFAULTS_DISPLAY_STRING;
            case MISC_FUNCTION_DISPLAY:
                return PermissionFormatStrategy.MISC_FUNCTION_DISPLAY_STRING;
	        case MONTAGE:
	            return PermissionFormatStrategy.MONTAGE_STRING;
            case NBBO_AGENT_QUERY:
                return PermissionFormatStrategy.NBBO_AGENT_QUERY_STRING;
            case ORDER_ENTRY:
                return PermissionFormatStrategy.ORDER_ENTRY_STRING;
            case ORDER_MODIFY:
                return PermissionFormatStrategy.ORDER_MODIFY_STRING;
            case PRODUCT_CLASS_GROUPS_ACCESS:
                return PermissionFormatStrategy.PRODUCT_CLASS_GROUPS_ACCESS_STRING;
            case PRODUCT_CLASS_GROUPS_MODIFY:
                return PermissionFormatStrategy.PRODUCT_CLASS_GROUPS_MODIFY_STRING;
            case PRODUCT_CLASS_GROUPS_VIEW:
                return PermissionFormatStrategy.PRODUCT_CLASS_GROUPS_VIEW_STRING;
            case PRODUCT_LOOKUP_DISPLAY:
                return PermissionFormatStrategy.PRODUCT_LOOKUP_DISPLAY_STRING;
            case PRODUCT_MAINT_ACCESS:
                return PermissionFormatStrategy.PRODUCT_MAINT_ACCESS_STRING;
            case PRODUCT_MAINT_PRICE_ADJUSTMENTS_ACCESS:
                return PermissionFormatStrategy.PRODUCT_MAINT_PRICE_ADJUSTMENTS_ACCESS_STRING;
            case PRODUCT_MAINT_PRICE_ADJUSTMENTS_MODIFY:
                return PermissionFormatStrategy.PRODUCT_MAINT_PRICE_ADJUSTMENTS_MODIFY_STRING;
            case PRODUCT_MAINT_PRICE_ADJUSTMENTS_VIEW:
                return PermissionFormatStrategy.PRODUCT_MAINT_PRICE_ADJUSTMENTS_VIEW_STRING;
            case PRODUCT_MAINT_PRODUCT_DEFINITION_ACCESS:
                return PermissionFormatStrategy.PRODUCT_MAINT_PRODUCT_DEFINITION_ACCESS_STRING;
            case PRODUCT_MAINT_PRODUCT_DEFINITION_MODIFY:
                return PermissionFormatStrategy.PRODUCT_MAINT_PRODUCT_DEFINITION_MODIFY_STRING;
            case PRODUCT_MAINT_PRODUCT_DEFINITION_VIEW:
                return PermissionFormatStrategy.PRODUCT_MAINT_PRODUCT_DEFINITION_VIEW_STRING;
            case PRODUCT_MAINT_PRODUCT_SETTLEMENT_ACCESS:
                return PermissionFormatStrategy.PRODUCT_MAINT_PRODUCT_SETTLEMENT_ACCESS_STRING;
            case PRODUCT_MAINT_PRODUCT_SETTLEMENT_MODIFY:
                return PermissionFormatStrategy.PRODUCT_MAINT_PRODUCT_SETTLEMENT_MODIFY_STRING;
            case PRODUCT_MAINT_PRODUCT_SETTLEMENT_VIEW:
                return PermissionFormatStrategy.PRODUCT_MAINT_PRODUCT_SETTLEMENT_VIEW_STRING;
            case PRODUCT_STATE_MAINT_ACCESS:
                return PermissionFormatStrategy.PRODUCT_STATE_MAINT_ACCESS_STRING;
            case PRODUCT_STATE_MAINT_BY_PRODUCT_CLASS_MODIFY:
                return PermissionFormatStrategy.PRODUCT_STATE_MAINT_BY_PRODUCT_CLASS_MODIFY_STRING;
            case PRODUCT_STATE_MAINT_BY_PRODUCT_MODIFY:
                return PermissionFormatStrategy.PRODUCT_STATE_MAINT_BY_PRODUCT_MODIFY_STRING;
            case PRODUCT_STATE_MAINT_BY_TRADING_SESSION_ELEMENT_MODIFY:
                return PermissionFormatStrategy.PRODUCT_STATE_MAINT_BY_TRADING_SESSION_ELEMENT_MODIFY_STRING;
            case PRODUCT_STATE_MAINT_BY_TRADING_SESSION_MODIFY:
                return PermissionFormatStrategy.PRODUCT_STATE_MAINT_BY_TRADING_SESSION_MODIFY_STRING;
            case PRODUCT_STATE_MAINT_VIEW:
                return PermissionFormatStrategy.PRODUCT_STATE_MAINT_VIEW_STRING;
            case QUERY:
                return PermissionFormatStrategy.QUERY_STRING;
            case QUOTE_ENTRY:
                return PermissionFormatStrategy.QUOTE_ENTRY_STRING;
            case QUOTE_LOCK_DISPLAY:
                return PermissionFormatStrategy.QUOTE_LOCK_DISPLAY_STRING;
            case QUOTE_MODIFY:
                return PermissionFormatStrategy.QUOTE_MODIFY_STRING;
            case QUOTES_DEFAULTS_DISPLAY:
                return PermissionFormatStrategy.QUOTES_DEFAULTS_DISPLAY_STRING;
            case REGISTER_NBBO_AGENT:
                return PermissionFormatStrategy.REGISTER_NBBO_AGENT_STRING;
            case SESSION_BROWSER_ACCESS:
                return PermissionFormatStrategy.SESSION_BROWSER_ACCESS_STRING;
            case SESSION_BROWSER_CAS:
                return PermissionFormatStrategy.SESSION_BROWSER_CAS_STRING;
            case SESSION_BROWSER_FE:
                return PermissionFormatStrategy.SESSION_BROWSER_FE_STRING;
            case SESSION_BROWSER_USER:
                return PermissionFormatStrategy.SESSION_BROWSER_USER_STRING;
            case STATUS_DISPLAY:
                return PermissionFormatStrategy.STATUS_DISPLAY_STRING;
            case SYSTEM_ADMIN_APPLICATION_ACCESS:
                return PermissionFormatStrategy.SYSTEM_ADMIN_APPLICATION_ACCESS_STRING;
            case TEXT_MESSAGE_VIEW:
                return PermissionFormatStrategy.TEXT_MESSAGE_VIEW_STRING;
            case TEXT_TO_OPRA_MESSAGE_VIEW:
                return PermissionFormatStrategy.TEXT_TO_OPRA_STRING;
            case TFL_MANAGEMENT_ACCESS:
                return PermissionFormatStrategy.TFL_MANAGEMENT_ACCESS_STRING;
            case TFL_MANAGEMENT_MODIFY:
                return PermissionFormatStrategy.TFL_MANAGEMENT_MODIFY_STRING;
            case TFL_MANAGEMENT_VIEW:
                return PermissionFormatStrategy.TFL_MANAGEMENT_VIEW_STRING;
            case TRADE_BUST:
                return PermissionFormatStrategy.TRADE_BUST_STRING;
            case TRADE_QUERY:
                return PermissionFormatStrategy.TRADE_QUERY_STRING;
	        case TRADES_LOG:
	            return PermissionFormatStrategy.TRADES_LOG_STRING;
            case TRADER_APPLICATION_ACCESS:
                return PermissionFormatStrategy.TRADER_APPLICATION_ACCESS_STRING;
            case TRADING_SESSIONS_ACCESS:
                return PermissionFormatStrategy.TRADING_SESSIONS_ACCESS_STRING;
            case TRADING_SESSIONS_MODIFY:
                return PermissionFormatStrategy.TRADING_SESSIONS_MODIFY_STRING;
            case TRADING_SESSIONS_VIEW:
                return PermissionFormatStrategy.TRADING_SESSIONS_VIEW_STRING;
            case USER_MANAGEMENT_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_ACCESS_STRING;
            case USER_MANAGEMENT_BROKER_FIRMS_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_BROKER_FIRMS_ACCESS_STRING;
            case USER_MANAGEMENT_BROKER_FIRMS_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_BROKER_FIRMS_MODIFY_STRING;
            case USER_MANAGEMENT_BROKER_FIRMS_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_BROKER_FIRMS_VIEW_STRING;
            case USER_MANAGEMENT_BROKER_PROFILE_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_BROKER_PROFILE_ACCESS_STRING;
            case USER_MANAGEMENT_BROKER_PROFILE_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_BROKER_PROFILE_MODIFY_STRING;
            case USER_MANAGEMENT_BROKER_PROFILE_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_BROKER_PROFILE_VIEW_STRING;
            case USER_MANAGEMENT_CONTROL_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_CONTROL_ACCESS_STRING;
            case USER_MANAGEMENT_CONTROL_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_CONTROL_MODIFY_STRING;
            case USER_MANAGEMENT_CONTROL_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_CONTROL_VIEW_STRING;
            case USER_MANAGEMENT_CREATE_DELETE_USER:
                return PermissionFormatStrategy.USER_MANAGEMENT_CREATE_DELETE_USER_STRING;
            case USER_MANAGEMENT_DETAILS_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_DETAILS_ACCESS_STRING;
            case USER_MANAGEMENT_DETAILS_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_DETAILS_MODIFY_STRING;
            case USER_MANAGEMENT_DETAILS_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_DETAILS_VIEW_STRING;
            case USER_MANAGEMENT_ENABLEMENTS_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_ENABLEMENTS_ACCESS_STRING;
            case USER_MANAGEMENT_ENABLEMENTS_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_ENABLEMENTS_MODIFY_STRING;
            case USER_MANAGEMENT_ENABLEMENTS_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_ENABLEMENTS_VIEW_STRING;
            case USER_MANAGEMENT_MM_PROFILES_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_MM_PROFILES_ACCESS_STRING;
            case USER_MANAGEMENT_MM_PROFILES_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_MM_PROFILES_MODIFY_STRING;
            case USER_MANAGEMENT_MM_PROFILES_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_MM_PROFILES_VIEW_STRING;
            case USER_MANAGEMENT_ORDERS_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_ORDERS_ACCESS_STRING;
            case USER_MANAGEMENT_ORDERS_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_ORDERS_MODIFY_STRING;
            case USER_MANAGEMENT_ORDERS_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_ORDERS_VIEW_STRING;
            case USER_MANAGEMENT_PREFERENCE_CONVERSION_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_PREFERENCE_CONVERSION_ACCESS_STRING;
            case USER_MANAGEMENT_PREFERENCE_CONVERSION_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_PREFERENCE_CONVERSION_MODIFY_STRING;
            case USER_MANAGEMENT_PREFERENCE_CONVERSION_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_PREFERENCE_CONVERSION_VIEW_STRING;
            case USER_MANAGEMENT_QRM_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_QRM_ACCESS_STRING;
            case USER_MANAGEMENT_QRM_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_QRM_MODIFY_STRING;
            case USER_MANAGEMENT_QRM_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_QRM_VIEW_STRING;
            case USER_MANAGEMENT_RATE_LIMITS_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_RATE_LIMITS_ACCESS_STRING;
            case USER_MANAGEMENT_RATE_LIMITS_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_RATE_LIMITS_MODIFY_STRING;
            case USER_MANAGEMENT_RATE_LIMITS_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_RATE_LIMITS_VIEW_STRING;
            case USER_MANAGEMENT_UPDATE_USER:
                return PermissionFormatStrategy.USER_MANAGEMENT_UPDATE_USER_STRING;
            case USER_MANAGEMENT_USER_FIRM_AFFILIATION_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_USER_FIRM_AFFILIATION_ACCESS_STRING;
            case USER_MANAGEMENT_USER_FIRM_AFFILIATION_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_USER_FIRM_AFFILIATION_MODIFY_STRING;
            case USER_MANAGEMENT_USER_FIRM_AFFILIATION_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_USER_FIRM_AFFILIATION_VIEW_STRING;
            case USER_MANAGEMENT_USER_GROUP_MANAGMENT_ACCESS:
                return PermissionFormatStrategy.USER_MANAGEMENT_USER_GROUP_MANAGMENT_ACCESS_STRING;
            case USER_MANAGEMENT_USER_GROUP_MANAGMENT_MODIFY:
                return PermissionFormatStrategy.USER_MANAGEMENT_USER_GROUP_MANAGMENT_MODIFY_STRING;
            case USER_MANAGEMENT_USER_GROUP_MANAGMENT_VIEW:
                return PermissionFormatStrategy.USER_MANAGEMENT_USER_GROUP_MANAGMENT_VIEW_STRING;
            case USER_ORDER_SUBSCRIPTION:
                return PermissionFormatStrategy.USER_ORDER_SUBSCRIPTION_STRING;
            case USER_QUOTE_SUBSCRIPTION:
                return PermissionFormatStrategy.USER_QUOTE_SUBSCRIPTION_STRING;
            case OMT_ORDER_QUERY:
                return PermissionFormatStrategy.OMT_ORDER_QUERY_STRING;
            case OMT_ORDER_QUERY_LOCATION:
                return PermissionFormatStrategy.OMT_ORDER_QUERY_LOCATION_STRING;
            case OMT_ORDER_QUERY_LOCATION_LIMITED:
                return PermissionFormatStrategy.OMT_ORDER_QUERY_LOCATION_LIMITED_STRING;
            case OMT_ORDER_QUERY_FIRM:
                return PermissionFormatStrategy.OMT_ORDER_QUERY_FIRM_STRING;
            case OMT_MANUAL_FILL_CANCEL:
                return PermissionFormatStrategy.OMT_MANUAL_FILL_CANCEL_STRING;
            case MANUAL_PRICE_REPORT_ACCESS:
                return PermissionFormatStrategy.MANUAL_PRICE_REPORT_ACCESS_STRING;
            case MANUAL_QUOTE_REPORT_ACCESS:
                return PermissionFormatStrategy.MANUAL_QUOTE_REPORT_ACCESS_STRING;

            case ORDER_MANAGEMENT_TERMINAL_ACCESS:
                return PermissionFormatStrategy.ORDER_MANAGEMENT_TERMINAL_ACCESS_STRING;
            case OMT_ACTION_DIRECT_REROUTE_OWNED:
                return PermissionFormatStrategy.OMT_ACTION_DIRECT_REROUTE_OWNED_STRING;
            case OMT_ACTION_DIRECT_REROUTE_ALL:
                return PermissionFormatStrategy.OMT_ACTION_DIRECT_REROUTE_ALL_STRING;
            case OMT_ACTION_ORDER_ENTRY:
                return PermissionFormatStrategy.OMT_ACTION_ORDER_ENTRY_STRING;
            case OMT_ACTION_ORDER_FILL:
                return PermissionFormatStrategy.OMT_ACTION_ORDER_FILL_STRING;
            case OMT_ACTION_ORDER_UPDATE:
                return PermissionFormatStrategy.OMT_ACTION_ORDER_UPDATE_STRING;
            case OMT_ACTION_CLEAR:
                return PermissionFormatStrategy.OMT_ACTION_CLEAR_STRING;
            case OMT_ACTION_CANCEL:
                return PermissionFormatStrategy.OMT_ACTION_CANCEL_STRING;
            case OMT_ACTION_CANCEL_OWNER_SOLICITED:
                return PermissionFormatStrategy.OMT_ACTION_CANCEL_OWNER_SOLICITED_STRING;
            case OMT_ACTION_CANCEL_OWNER_UNSOLICITED:
                return PermissionFormatStrategy.OMT_ACTION_CANCEL_OWNER_UNSOLICITED_STRING;
            case OMT_ACTION_CANCEL_NON_OWNER_UNSOLICITED:
                return PermissionFormatStrategy.OMT_ACTION_CANCEL_NON_OWNER_UNSOLICITED_STRING;
            case OMT_ACTION_CANCEL_REPLACE:
                return PermissionFormatStrategy.OMT_ACTION_CANCEL_REPLACE_STRING;
            case OMT_ACTION_CANCEL_REPLACE_OWNER_SOLICITED:
                return PermissionFormatStrategy.OMT_ACTION_CANCEL_REPLACE_OWNER_SOLICITED_STRING;
            case OMT_ACTION_CANCEL_REPLACE_OWNER_UNSOLICITED:
                return PermissionFormatStrategy.OMT_ACTION_CANCEL_REPLACE_OWNER_UNSOLICITED_STRING;
            case OMT_ACTION_CANCEL_REPLACE_NON_OWNER_UNSOLICITED:
                return PermissionFormatStrategy.OMT_ACTION_CANCEL_REPLACE_NON_OWNER_UNSOLICITED_STRING;
            case OMT_DISPLAY_AUDIT_HISTORY:
                return PermissionFormatStrategy.OMT_DISPLAY_AUDIT_HISTORY_STRING;
            case OMT_AUTO_REMOVE_CANCELED_ORDER:
                return PermissionFormatStrategy.OMT_AUTO_REMOVE_CANCELED_ORDER_STRING;
            case FLOOR_TRADE_MAINTENANCE:
                return PermissionFormatStrategy.FLOOR_TRADE_MAINTENANCE_STRING;

            default: return PermissionFormatStrategy.UNKNOWN_PERMISSION_STRING;
        }
    }
}
