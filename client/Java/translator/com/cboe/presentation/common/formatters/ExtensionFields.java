//
// ------------------------------------------------------------------------
// FILE: ExtensionFields.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.formatters;

import java.util.HashMap;

/**
 * @author torresl@cboe.com
 */
public final class ExtensionFields
{
    public static final String GUICFI = com.cboe.idl.cmiConstants.ExtensionFields.GUICFI;
    public static final String BARTID = com.cboe.idl.cmiConstants.ExtensionFields.BARTID;
    public static final String STOCK_FIRM = com.cboe.idl.cmiConstants.ExtensionFields.STOCK_FIRM;
    public static final String STOCK_FIRM_NAME = com.cboe.idl.cmiConstants.ExtensionFields.STOCK_FIRM_NAME;
    public static final String CBOE_EXEC_ID = com.cboe.idl.cmiConstants.ExtensionFields.CBOE_EXEC_ID;
    public static final String ORIGINAL_QUANTITY = com.cboe.idl.cmiConstants.ExtensionFields.ORIGINAL_QUANTITY;
    public static final String SIDE = com.cboe.idl.cmiConstants.ExtensionFields.SIDE;
    public static final String EXEC_BROKER = com.cboe.idl.cmiConstants.ExtensionFields.EXEC_BROKER;
    public static final String ORS_ID = com.cboe.idl.cmiConstants.ExtensionFields.ORS_ID;
    public static final String SATISFACTION_ALERT_ID = com.cboe.idl.cmiConstants.ExtensionFields.SATISFACTION_ALERT_ID;
    public static final String ASSOCIATED_ORDER_ID = com.cboe.idl.cmiConstants.ExtensionFields.ASSOCIATED_ORDER_ID;
    public static final String AWAY_CANCEL_REPORT_EXEC_ID  = com.cboe.idl.cmiConstants.ExtensionFields.AWAY_CANCEL_REPORT_EXEC_ID ;
    public static final String AWAY_EXCHANGE_USER_ACRONYM  = com.cboe.idl.cmiConstants.ExtensionFields.AWAY_EXCHANGE_USER_ACRONYM ;
    public static final String USER_ASSIGNED_CANCEL_ID  = com.cboe.idl.cmiConstants.ExtensionFields.USER_ASSIGNED_CANCEL_ID ;
    public static final String AWAY_EXCHANGE_EXEC_ID  = com.cboe.idl.cmiConstants.ExtensionFields.AWAY_EXCHANGE_EXEC_ID ;
    public static final String HANDLING_INSTRUCTION = com.cboe.idl.cmiConstants.ExtensionFields.HANDLING_INSTRUCTION;
    public static final String AWAY_EXCHANGE_ORDER_ID = com.cboe.idl.cmiConstants.ExtensionFields.AWAY_EXCHANGE_ORDER_ID;
    public static final String TEXT = com.cboe.idl.cmiConstants.ExtensionFields.TEXT;
    public static final String AWAY_EXCHANGE_TRANSACT_TIME = com.cboe.idl.cmiConstants.ExtensionFields.AWAY_EXCHANGE_TRANSACT_TIME;
    public static final String EXCHANGE_DESTINATION = com.cboe.idl.cmiConstants.ExtensionFields.EXCHANGE_DESTINATION;
    public static final String AUTO_EXECUTION_SIZE = com.cboe.idl.cmiConstants.ExtensionFields.AUTO_EXECUTION_SIZE;
    public static final String TRADE_THRU_TIME = com.cboe.idl.cmiConstants.ExtensionFields.TRADE_THRU_TIME;
    public static final String TRADE_THRU_SIZE = com.cboe.idl.cmiConstants.ExtensionFields.TRADE_THRU_SIZE;
    public static final String TRADE_THRU_PRICE = com.cboe.idl.cmiConstants.ExtensionFields.TRADE_THRU_PRICE;
    public static final String ADJUSTED_PRICE_INDICATOR = com.cboe.idl.cmiConstants.ExtensionFields.ADJUSTED_PRICE_INDICATOR;
    public static final String SATISFACTION_ORDER_DISPOSITION = com.cboe.idl.cmiConstants.ExtensionFields.SATISFACTION_ORDER_DISPOSITION;
    public static final String EXECUTION_RECEIPT_TIME = com.cboe.idl.cmiConstants.ExtensionFields.EXECUTION_RECEIPT_TIME;
    public static final String ORIGINAL_ORDER_TIME = com.cboe.idl.cmiConstants.ExtensionFields.ORIGINAL_ORDER_TIME;
    public static final String OLA_REJECT_REASON = com.cboe.idl.cmiConstants.ExtensionFields.OLA_REJECT_REASON;
    public static final String ORDER_CAPACITY = com.cboe.idl.cmiConstants.ExtensionFields.ORDER_CAPACITY;
    public static final String ORDER_RESTRICTIONS = com.cboe.idl.cmiConstants.ExtensionFields.ORDER_RESTRICTIONS;
    public static final String EXPIRATION_TIME = com.cboe.idl.cmiConstants.ExtensionFields.EXPIRATION_TIME;
    public static final String BILLING_TYPE = com.cboe.idl.cmiConstants.ExtensionFields.BILLING_TYPE; // CBSX Billing Enhancements
    public static final String DIRECTED_FIRM = com.cboe.idl.cmiConstants.ExtensionFields.DIRECTED_FIRM;

    private static final String GUICFI_STRING = "GUI Complex Flip Indicator";
    private static final String BARTID_STRING = "Bart ID";
    private static final String STOCK_FIRM_STRING = "Stock Firm";
    private static final String STOCK_FIRM_NAME_STRING = "Stock Firm Name";
    private static final String CBOE_EXEC_ID_STRING = "CBOE Exec ID";
    private static final String AWAY_EXCHANGE_USER_ACRONYM_STRING = "Away Exchange User Acronym";
    private static final String USER_ASSIGNED_CANCEL_ID_STRING = "User Assigned Cancel ID";
    private static final String AWAY_EXCHANGE_EXEC_ID_STRING = "Away Exchange Exec ID";
    private static final String HANDLING_INSTRUCTION_STRING = "Handling Instruction";
    private static final String AWAY_EXCHANGE_ORDER_ID_STRING = "Away Exchange Order ID";
    private static final String TEXT_STRING = "Text";
    private static final String AWAY_EXCHANGE_TRANSACT_TIME_STRING = "Away Exchange Transaction Time";
    private static final String EXCHANGE_DESTINATION_STRING = "Away Exchange";
    private static final String AUTO_EXECUTION_SIZE_STRING = "Auto Execution Size";
    private static final String TRADE_THRU_TIME_STRING = "Trade Thru Time";
    private static final String TRADE_THRU_SIZE_STRING = "Trade Thru Size";
    private static final String TRADE_THRU_PRICE_STRING = "Trader Thru Price";
    private static final String ADJUSTED_PRICE_INDICATOR_STRING = "Adjusted Price Indicator";
    private static final String SATISFACTION_ORDER_DISPOSITION_STRING = "Satisfaction Order Disposition";
    private static final String EXECUTION_RECEIPT_TIME_STRING = "Execution Receipt Time";
    private static final String ORIGINAL_ORDER_TIME_STRING = "Original Order Time";
    private static final String OLA_REJECT_REASON_STRING = "OLA Reject Reason";
    private static final String ORDER_CAPACITY_STRING = "Order Capacity";
    private static final String ORDER_RESTRICTIONS_STRING = "Order Restrictions";
    private static final String ORIGINAL_QUANTITY_STRING = "Original Quantity";
    private static final String SIDE_STRING = "Side";
    private static final String EXEC_BROKER_STRING = "Exec Broker";
    private static final String ORS_ID_STRING = "ORS ID";
    private static final String SATISFACTION_ALERT_ID_STRING = "Satisfaction Alert ID";
    private static final String ASSOCIATED_ORDER_ID_STRING = "Associated Order ID";
    private static final String AWAY_CANCEL_REPORT_EXEC_ID_STRING = "Away Cancel Report Exec ID";
    private static final String EXPIRATION_TIME_STRING = "Expiration Time";
    private static final String BILLING_TYPE_STRING = "Billing Type"; // CBSX Billing Enhancements
    private static final String DIRECTED_FIRM_STRING = "Directed FIRM";

    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE = "INVALID_TYPE";

    private static HashMap map = new HashMap(30);
    static
    {
        map.put(GUICFI, GUICFI_STRING);
        map.put(BARTID, BARTID_STRING);
        map.put(STOCK_FIRM, STOCK_FIRM_STRING);
        map.put(STOCK_FIRM_NAME, STOCK_FIRM_NAME_STRING);
        map.put(CBOE_EXEC_ID, CBOE_EXEC_ID_STRING);
        map.put(ORIGINAL_QUANTITY, ORIGINAL_QUANTITY_STRING);
        map.put(SIDE, SIDE_STRING);
        map.put(EXEC_BROKER, EXEC_BROKER_STRING);
        map.put(ORS_ID, ORS_ID_STRING);
        map.put(SATISFACTION_ALERT_ID, SATISFACTION_ALERT_ID_STRING);
        map.put(ASSOCIATED_ORDER_ID, ASSOCIATED_ORDER_ID_STRING);
        map.put(AWAY_CANCEL_REPORT_EXEC_ID, AWAY_CANCEL_REPORT_EXEC_ID_STRING);
        map.put(AWAY_EXCHANGE_USER_ACRONYM, AWAY_EXCHANGE_USER_ACRONYM_STRING);
        map.put(USER_ASSIGNED_CANCEL_ID, USER_ASSIGNED_CANCEL_ID_STRING);
        map.put(AWAY_EXCHANGE_EXEC_ID, AWAY_EXCHANGE_EXEC_ID_STRING);
        map.put(HANDLING_INSTRUCTION, HANDLING_INSTRUCTION_STRING);
        map.put(AWAY_EXCHANGE_ORDER_ID, AWAY_EXCHANGE_ORDER_ID_STRING);
        map.put(TEXT, TEXT_STRING);
        map.put(AWAY_EXCHANGE_TRANSACT_TIME, AWAY_EXCHANGE_TRANSACT_TIME_STRING);
        map.put(EXCHANGE_DESTINATION, EXCHANGE_DESTINATION_STRING);
        map.put(AUTO_EXECUTION_SIZE, AUTO_EXECUTION_SIZE_STRING);
        map.put(TRADE_THRU_TIME, TRADE_THRU_TIME_STRING);
        map.put(TRADE_THRU_SIZE, TRADE_THRU_SIZE_STRING);
        map.put(TRADE_THRU_PRICE, TRADE_THRU_PRICE_STRING);
        map.put(ADJUSTED_PRICE_INDICATOR, ADJUSTED_PRICE_INDICATOR_STRING);
        map.put(SATISFACTION_ORDER_DISPOSITION, SATISFACTION_ORDER_DISPOSITION_STRING);
        map.put(EXECUTION_RECEIPT_TIME, EXECUTION_RECEIPT_TIME_STRING);
        map.put(ORIGINAL_ORDER_TIME, ORIGINAL_ORDER_TIME_STRING);
        map.put(OLA_REJECT_REASON, OLA_REJECT_REASON_STRING);
        map.put(ORDER_CAPACITY, ORDER_CAPACITY_STRING);
        map.put(ORDER_RESTRICTIONS, ORDER_RESTRICTIONS_STRING);
        map.put(EXPIRATION_TIME, EXPIRATION_TIME_STRING);
        map.put(BILLING_TYPE, BILLING_TYPE_STRING);   // CBSX Billing Enhancements
        map.put(DIRECTED_FIRM, DIRECTED_FIRM_STRING);
    }
    private ExtensionFields()
    {
        super();
    }

    public static String toString(String extensionFieldKey)
    {
        return toString(extensionFieldKey, TRADERS_FORMAT);
    }

    public static String toString(String extensionFieldKey, String format)
    {
        if(TRADERS_FORMAT.equals(format))
        {
            String value = (String) map.get(extensionFieldKey);
            if(value != null)
            {
                return value;
            }
            return INVALID_TYPE;
        }
        return INVALID_FORMAT;
    }
}
