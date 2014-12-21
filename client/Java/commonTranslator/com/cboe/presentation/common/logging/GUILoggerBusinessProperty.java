// -----------------------------------------------------------------------------------
// Source file: GUILoggerBusinessProperty.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperty;
import com.cboe.interfaces.presentation.common.logging.GUILoggerMsgTypes;

/**
 *  This class provides getters and setteers for GUILogger property attributes.
 */
public class GUILoggerBusinessProperty
        extends GUILoggerProperty
        implements IGUILoggerBusinessProperty
{
    public static final GUILoggerBusinessProperty COMMON =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_COMMON, "Common");
    public static final GUILoggerBusinessProperty WINDOW_MANAGEMENT =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_WINDOW_MANAGEMENT,
                                          "Window Management");
    public static final GUILoggerBusinessProperty MARKET_QUERY =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_MARKET_QUERY, "Market Query");
    public static final GUILoggerBusinessProperty ORDER_ENTRY =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_ORDER_ENTRY, "Order Entry");
    public static final GUILoggerBusinessProperty ORDER_QUERY =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_ORDER_QUERY, "Order Query");
    public static final GUILoggerBusinessProperty PRODUCT_DEFINITION =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_PRODUCT_DEFINITION,
                                          "Product Definition");
    public static final GUILoggerBusinessProperty PRODUCT_QUERY =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_PRODUCT_QUERY, "Product Query");
    public static final GUILoggerBusinessProperty QRM =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_QRM, "QRM");
    public static final GUILoggerBusinessProperty QUOTE =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_QUOTE, "Quote");
    public static final GUILoggerBusinessProperty RFQ =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_RFQ, "RFQ");
    public static final GUILoggerBusinessProperty AUCTION =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_AUCTION, "Auction");
    public static final GUILoggerBusinessProperty TEXT_MESSAGE =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_TEXT_MESSAGE, "Message Notification");
    public static final GUILoggerBusinessProperty TRADING_SESSION =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_TRADING_SESSION, "Trading Session");
    public static final GUILoggerBusinessProperty USER_HISTORY =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_USER_HISTORY, "User History");
    public static final GUILoggerBusinessProperty USER_MANAGEMENT =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_USER_MANAGEMENT, "User Management");
    public static final GUILoggerBusinessProperty USER_PREFERENCES =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_USER_PREFERENCES, "User Preferences");
    public static final GUILoggerBusinessProperty USER_SESSION =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_USER_SESSION, "User Session");
    public static final GUILoggerBusinessProperty ORDER_BOOK =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_ORDER_BOOK, "Order Book");
    public static final GUILoggerBusinessProperty PRODUCT_SELECTOR =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_PRODUCT_SELECTOR, "Product Selector");
    public static final GUILoggerBusinessProperty TICKER =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_TICKER, "Ticker");
    public static final GUILoggerBusinessProperty PREFERENCE_CONVERSION =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_PREFERENCE_CONVERSION,
                                          "Preference Conversion");
    public static final GUILoggerBusinessProperty INTERMARKET_HELD_ORDER_ENTRY =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_INTERMARKET_HELD_ORDER_ENTRY,
                                          "Held Order Entry");
    public static final GUILoggerBusinessProperty INTERMARKET_HELD_ORDER_QUERY =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_INTERMARKET_HELD_ORDER_QUERY,
                                          "Held Order Query");
    public static final GUILoggerBusinessProperty INTERMARKET_NBBO_AGENT =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_INTERMARKET_NBBO_AGENT,
                                          "NBBO Agent");
    public static final GUILoggerBusinessProperty REPORT_GENERATION =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_REPORT_GENERATION,
                                          "Report Generation");
    public static final GUILoggerBusinessProperty DATABASE_QUERY_BUILDER =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_DATABASE_QUERY_BUILDER,
                                          "Database Query Builder");
    public static final GUILoggerBusinessProperty PERMISSION_MATRIX =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_PERMISSION_MATRIX,
                                          "Permission Matrix");
    public static final GUILoggerBusinessProperty MANUAL_REPORTING = 
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_MANUAL_REPORTING, 
                                          "Manual Reporting");
    public static final GUILoggerBusinessProperty OMT =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_OMT, "OMT");
    public static final GUILoggerBusinessProperty STRATEGY_DSM =
            new GUILoggerBusinessProperty(GUILoggerMsgTypes.LOG_STRATEGY_DSM, "Strategy DSM");

    private static final GUILoggerBusinessProperty[] businessProperties =
                                                                            {COMMON,
                                                                            WINDOW_MANAGEMENT,
                                                                            MARKET_QUERY,
                                                                            ORDER_ENTRY,
                                                                            ORDER_QUERY,
                                                                            PRODUCT_DEFINITION,
                                                                            PRODUCT_QUERY,
                                                                            QRM,
                                                                            QUOTE,
                                                                            RFQ,
                                                                            AUCTION,
                                                                            TEXT_MESSAGE,
                                                                            TRADING_SESSION,
                                                                            USER_HISTORY,
                                                                            USER_MANAGEMENT,
                                                                            USER_PREFERENCES,
                                                                            USER_SESSION,
                                                                            ORDER_BOOK,
                                                                            PRODUCT_SELECTOR,
                                                                            TICKER,
                                                                            PREFERENCE_CONVERSION,
                                                                            INTERMARKET_HELD_ORDER_ENTRY,
                                                                            INTERMARKET_HELD_ORDER_QUERY,
                                                                            INTERMARKET_NBBO_AGENT,
                                                                            REPORT_GENERATION,
                                                                            MANUAL_REPORTING,
                                                                            PERMISSION_MATRIX,
                                                                            OMT,
                                                                            STRATEGY_DSM
                                                                            };

    protected GUILoggerBusinessProperty(int key, String name)
    {
        super(key, name);
    }

    public static IGUILoggerProperty getProperty(int index)
    {
        return businessProperties[index];
    }

    public static IGUILoggerProperty[] getProperties()
    {
        //noinspection ReturnOfCollectionOrArrayField
        return businessProperties;
    }

    public static int getMinIndex()
    {
        return GUILoggerMsgTypes.LOG_TRADER_BUSINESS_MIN;
    }

    public static int getMaxIndex()
    {
        return GUILoggerMsgTypes.LOG_TRADER_BUSINESS_MAX;
    }
}
