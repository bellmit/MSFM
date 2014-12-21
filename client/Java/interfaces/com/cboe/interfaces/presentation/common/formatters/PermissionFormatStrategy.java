//
// -----------------------------------------------------------------------------------
// Source file: PermissionFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.permissionMatrix.Permission;

@SuppressWarnings({"ConstantNamingConvention"})
public interface PermissionFormatStrategy extends FormatStrategy
{
    String FULL_PERMISSION_NAME = "Full Permission Name";
    String FULL_PERMISSION_NAME_DESCRIPTION = "Full Permission Name";

	String ACTIVITY_LOG_STRING = "Activity Log";
    String AGENT_QUERY_ACCESS_STRING = "Access Agent Query Screen";
    String BLOCK_TRADES_STRING = "Block Trades";
    String BOOK_DEPTH_ACCESS_STRING = "Access Book Depth Screen";
    String BOOK_DEPTH_FOR_BOOK_STRING = "Book Depth";
    String BOOK_DEPTH_FOR_DETAILS_STRING = "Book Depth Details";
    String CANCEL_USER_ORDER_STRING = "Cancel User Orders";
    String CANCEL_ORDERS_BY_BC_STRING = "Cancel Orders By BC";
    String CANCEL_ORDERS_BY_SELECTING_SPREADSHEET_STRING = "Cancel Orders By Selecting Spreadsheet";
    String CROSS_ORDER_ENTRY_STRING = "Cross Order Entry";
    String DPM_QUERY_STRING = "Query DPM Information";
    String EOP_QUERY_STRING = "Expected Opening Price Query";
    String EXECUTING_GIVE_UP_FIRM_UPDATE_STRING = "Update Executing Give Up Firm";
	String EXPRESS_MKT_DISPLAY_STRING = "Express Market Display";
    String FIRM_ORDER_SUBSCRIPTION_STRING = "Subscribe for Firm Order";
    String FIRM_QUOTE_SUBSCRIPTION_STRING = "Subscribe for Firm Quote";
    String HELD_ORDER_QUERY_STRING = "Query Held Orders";
    String HELD_ORDER_STATUS_VIEW_STRING = "View Held Order Status";
    String IQOD_STRING = "Order Query";
    String LIGHT_ORDER_ENTRY_STRING = "Light Order Entry";
    String LOGGING_DEFAULTS_DISPLAY_STRING = "Display Logging Defaults";
	String MARKET_HISTORY_STRING = "Market History";
    String MESSAGE_CENTER_ACCESS_STRING = "Access Message Center Screen";
    String MESSAGE_CENTER_CREATE_MESSAGE_STRING = "Create Message";
    String MESSAGE_CENTER_DELETE_STRING = "Delete Message";
    String MESSAGE_CENTER_READ_STRING = "Read Message";
    String MISC_DEFAULTS_DISPLAY_STRING = "Display Miscellaneous Defaults";
    String MISC_FUNCTION_DISPLAY_STRING = "Display Misc Functions";
	String MONTAGE_STRING = "Montage";
    String NBBO_AGENT_QUERY_STRING = "Query NBBO Agent Information";
    String ORDER_ENTRY_STRING = "Order Entry";
    String ORDER_MODIFY_STRING = "Modify Order";
    String ORDER_REROUTE_STRING = "Reroute Order";
    String PRODUCT_CLASS_GROUPS_ACCESS_STRING = "Access Product Class Groups Maintenance Screen";
    String PRODUCT_CLASS_GROUPS_MODIFY_STRING = "Modify Product Class Groups";
    String PRODUCT_CLASS_GROUPS_VIEW_STRING = "View Product Class Groups";
    String PRODUCT_LOOKUP_DISPLAY_STRING = "Display Product Lookup";
    String PRODUCT_MAINT_ACCESS_STRING = "Access Product Maintenance Screens";
    String PRODUCT_MAINT_PRICE_ADJUSTMENTS_ACCESS_STRING = "Access Price Adjustments Screen";
    String PRODUCT_MAINT_PRICE_ADJUSTMENTS_MODIFY_STRING = "Price Adjustments Modify";
    String PRODUCT_MAINT_PRICE_ADJUSTMENTS_VIEW_STRING = "Price Adjustments View";
    String PRODUCT_MAINT_PRODUCT_DEFINITION_ACCESS_STRING = "Access Product Definition Screen";
    String PRODUCT_MAINT_PRODUCT_DEFINITION_MODIFY_STRING = "Product Definition Modify";
    String PRODUCT_MAINT_PRODUCT_DEFINITION_VIEW_STRING = "Product Definition View";
    String PRODUCT_MAINT_PRODUCT_SETTLEMENT_ACCESS_STRING = "Access Product Settlement Screen";
    String PRODUCT_MAINT_PRODUCT_SETTLEMENT_MODIFY_STRING = "Product Settlement Modify";
    String PRODUCT_MAINT_PRODUCT_SETTLEMENT_VIEW_STRING = "Product Settlement View";
    String PRODUCT_STATE_MAINT_ACCESS_STRING = "Access Product State Maintenance Screen";
    String PRODUCT_STATE_MAINT_BY_PRODUCT_CLASS_MODIFY_STRING =
            "Product State Maintenance By Class Modify";
    String PRODUCT_STATE_MAINT_BY_PRODUCT_MODIFY_STRING =
            "Product State Maintenance By Product Modify";
    String PRODUCT_STATE_MAINT_BY_TRADING_SESSION_ELEMENT_MODIFY_STRING =
            "Product State Maintenance By Trading Session Element Modify";
    String PRODUCT_STATE_MAINT_BY_TRADING_SESSION_MODIFY_STRING =
            "Product State Maintenance By Trading Session Modify";
    String PRODUCT_STATE_MAINT_VIEW_STRING = "Product State Maintenance View";
    String QUERY_STRING = "Query";
    String QUOTE_ENTRY_STRING = "Quote Entry";
    String QUOTE_LOCK_DISPLAY_STRING = "Display Quote Lock";
    String QUOTE_MODIFY_STRING = "Modify Quote";
    String QUOTES_DEFAULTS_DISPLAY_STRING = "Quotes Defaults Display";
    String REGISTER_NBBO_AGENT_STRING = "Register as NBBO Agent";
    String SESSION_BROWSER_ACCESS_STRING = "Access Session Browser Screen";
    String SESSION_BROWSER_CAS_STRING = "CAS Session Browser";
    String SESSION_BROWSER_FE_STRING = "FrontEnd Session Browser";
    String SESSION_BROWSER_USER_STRING = "User Session Browser";
    String STATUS_DISPLAY_STRING = "Display Status";
    String SYSTEM_ADMIN_APPLICATION_ACCESS_STRING = "Access System Admin Application";
    String TEXT_MESSAGE_VIEW_STRING = "Display Text Message";
    String TFL_MANAGEMENT_ACCESS_STRING = "Access TFL Maintenance Screen";
    String TFL_MANAGEMENT_MODIFY_STRING = "Modify Tradethrough Alert";
    String TFL_MANAGEMENT_VIEW_STRING = "View Tradethrough Alert";
    String TRADE_BUST_STRING = "Trade Bust";
    String TRADE_QUERY_STRING = "Trade Query";
	String TRADES_LOG_STRING = "Trades Log";
    String TRADER_APPLICATION_ACCESS_STRING = "Access Trader Application";
    String TRADING_SESSIONS_ACCESS_STRING = "Access Trading Sessions Screen";
    String TRADING_SESSIONS_MODIFY_STRING = "Trading Session Control";
    String TRADING_SESSIONS_VIEW_STRING = "Trading Session Template Maintenance";
    String USER_MANAGEMENT_ACCESS_STRING = "Access User Maintenance Screen";
    String USER_MANAGEMENT_BROKER_FIRMS_ACCESS_STRING = "Access User Maintenance Broker Firms Tab";
    String USER_MANAGEMENT_BROKER_FIRMS_MODIFY_STRING = "User Broker Firms Modify";
    String USER_MANAGEMENT_BROKER_FIRMS_VIEW_STRING = "User Broker Firms View";
    String USER_MANAGEMENT_BROKER_PROFILE_ACCESS_STRING = "Access User Maintenance Broker Profile Tab";
    String USER_MANAGEMENT_BROKER_PROFILE_MODIFY_STRING = "User Broker Profile Modify";
    String USER_MANAGEMENT_BROKER_PROFILE_VIEW_STRING = "User Broker Profile View";
    String USER_MANAGEMENT_CONTROL_ACCESS_STRING = "Access User Maintenance Control Tab";
    String USER_MANAGEMENT_CONTROL_MODIFY_STRING = "User Control Modify";
    String USER_MANAGEMENT_CONTROL_VIEW_STRING = "User Control View";
    String USER_MANAGEMENT_CREATE_DELETE_USER_STRING = "Create New Or Delete Existing User";
    String USER_MANAGEMENT_DETAILS_ACCESS_STRING = "Access User Maintenance Details Tab";
    String USER_MANAGEMENT_DETAILS_MODIFY_STRING = "User Details Modify";
    String USER_MANAGEMENT_DETAILS_VIEW_STRING = "User Details View";
    String USER_MANAGEMENT_ENABLEMENTS_ACCESS_STRING = "Access User Maintenance Enablements Tab";
    String USER_MANAGEMENT_ENABLEMENTS_MODIFY_STRING = "User Enablements Modify";
    String USER_MANAGEMENT_ENABLEMENTS_VIEW_STRING = "User Enablements View";
    String USER_MANAGEMENT_MM_PROFILES_ACCESS_STRING = "Access User Maintenance MM Profiles Tab";
    String USER_MANAGEMENT_MM_PROFILES_MODIFY_STRING = "User Profile Modify";
    String USER_MANAGEMENT_MM_PROFILES_VIEW_STRING = "User Profile View";
    String USER_MANAGEMENT_ORDERS_ACCESS_STRING = "Access User Maintenance Orders Tab";
    String USER_MANAGEMENT_ORDERS_MODIFY_STRING = "User Orders Modify";
    String USER_MANAGEMENT_ORDERS_VIEW_STRING = "User Orders View";
    String USER_MANAGEMENT_PREFERENCE_CONVERSION_ACCESS_STRING =
            "Access User Maintenance Preference Conversion Tab";
    String USER_MANAGEMENT_PREFERENCE_CONVERSION_MODIFY_STRING = "User Preference Conversion Modify";
    String USER_MANAGEMENT_PREFERENCE_CONVERSION_VIEW_STRING = "User Preference Conversion View";
    String USER_MANAGEMENT_QRM_ACCESS_STRING = "Access User Maintenance QRM Tab";
    String USER_MANAGEMENT_QRM_MODIFY_STRING = "User QRM Modify";
    String USER_MANAGEMENT_QRM_VIEW_STRING = "User QRM View";
    String USER_MANAGEMENT_RATE_LIMITS_ACCESS_STRING = "Access Rate Limits Maintenance Screen";
    String USER_MANAGEMENT_RATE_LIMITS_MODIFY_STRING = "User Rate Limits Modify";
    String USER_MANAGEMENT_RATE_LIMITS_VIEW_STRING = "User Rate Limits View";
    String USER_MANAGEMENT_UPDATE_USER_STRING = "Update User";
    String USER_MANAGEMENT_USER_FIRM_AFFILIATION_ACCESS_STRING =
            "Access User Maintenance User Firm Affiliation Tab";
    String USER_MANAGEMENT_USER_FIRM_AFFILIATION_MODIFY_STRING = "User Firm Affiliation Modify";
    String USER_MANAGEMENT_USER_FIRM_AFFILIATION_VIEW_STRING = "User Firm Affiliation View";
    String USER_MANAGEMENT_USER_GROUP_MANAGMENT_ACCESS_STRING =
            "Access User Maintenance User Group Maintenance Tab";
    String USER_MANAGEMENT_USER_GROUP_MANAGMENT_MODIFY_STRING = "User Group Modify";
    String USER_MANAGEMENT_USER_GROUP_MANAGMENT_VIEW_STRING = "User Group View";
    String USER_ORDER_SUBSCRIPTION_STRING = "Subscribe for User Order";
    String USER_QUOTE_SUBSCRIPTION_STRING = "Subscribe for User Quote";
    String OMT_ORDER_QUERY_STRING = "OMT Order Query";
    String OMT_ORDER_QUERY_LOCATION_STRING = "OMT Order Query by Location";
    String OMT_ORDER_QUERY_LOCATION_LIMITED_STRING = "OMT Order Query by Location Limited";
    String OMT_ORDER_QUERY_FIRM_STRING = "OMT Order Query by Firm";
    String OMT_MANUAL_FILL_CANCEL_STRING = "Manual Fill, Cancel, Cancel Report";
    String TEXT_TO_OPRA_STRING = "Send Text Message to OPRA";
    String ORDER_MANAGEMENT_TERMINAL_ACCESS_STRING = "OMT Message List View";
    String OMT_ACTION_DIRECT_REROUTE_OWNED_STRING = "Reroute Own Items to another OMT";
    String OMT_ACTION_DIRECT_REROUTE_ALL_STRING = "Reroute Items to another OMT";
    String OMT_ACTION_ORDER_ENTRY_STRING = "Enter New Order";
    String OMT_ACTION_ORDER_FILL_STRING = "Fill Order";
    String OMT_ACTION_ORDER_UPDATE_STRING = "Update Order";
    String OMT_ACTION_CLEAR_STRING = "Clear Items";
    String OMT_ACTION_CANCEL_STRING = "Cancel Order";
    String OMT_ACTION_CANCEL_OWNER_SOLICITED_STRING = "Cancel owned order that has a cancel request";
    String OMT_ACTION_CANCEL_OWNER_UNSOLICITED_STRING = "Cancel owned order that does not have a cancel request";
    String OMT_ACTION_CANCEL_NON_OWNER_UNSOLICITED_STRING = "Cancel unowned order that does not have a cancel request";
    String OMT_ACTION_CANCEL_REPLACE_STRING = "Cancel and replace owned order that has a cancel request";
    String OMT_ACTION_CANCEL_REPLACE_OWNER_SOLICITED_STRING = "Cancel and replace owned order that has a cancel request";
    String OMT_ACTION_CANCEL_REPLACE_OWNER_UNSOLICITED_STRING = "Cancel and replace owned order that does not have a cancel request";
    String OMT_ACTION_CANCEL_REPLACE_NON_OWNER_UNSOLICITED_STRING = "Cancel and replace unowned order that does not have a cancel request";
    String MANUAL_PRICE_REPORT_ACCESS_STRING = "Access to Manual Price Report";
    String MANUAL_QUOTE_REPORT_ACCESS_STRING = "Access to Manual Quote Report";
    String OMT_DISPLAY_AUDIT_HISTORY_STRING = "Display Audit Order History";
    String OMT_AUTO_REMOVE_CANCELED_ORDER_STRING = "OMT Automatically Remove Canceled Orders";
    String FLOOR_TRADE_MAINTENANCE_STRING = "Floor Trade Maintenance";

    String UNKNOWN_PERMISSION_STRING = "UNKNOWN";

    String format(Permission permission);
}
