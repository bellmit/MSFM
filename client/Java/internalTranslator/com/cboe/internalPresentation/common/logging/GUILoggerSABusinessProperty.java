// -----------------------------------------------------------------------------------
// Source file: GUILoggerSABusinessProperty.java
//
// PACKAGE: com.cboe.internalPresentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperty;
import com.cboe.interfaces.presentation.common.logging.GUILoggerMsgTypes;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

/**
 *  This class provides getters and setters for GUILogger property attributes.
 */
@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "FieldNameHidesFieldInSuperclass"})
public class GUILoggerSABusinessProperty extends GUILoggerBusinessProperty
{
    //SA only properties
    public static final GUILoggerSABusinessProperty PRODUCT_MAINTENANCE =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_PRODUCT_MAINTENANCE,
                                            "Product Maintenance");
    public static final GUILoggerSABusinessProperty TRADE_MAINTENANCE =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_TRADE_MAINTENANCE,
                                            "Trade Maintenance");
    public static final GUILoggerSABusinessProperty FIRM_MAINTENANCE =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_FIRM_MAINTENANCE,
                                            "Firm Maintenance");
    public static final GUILoggerSABusinessProperty SESSION_MANAGEMENT =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_SESSION_MANAGEMENT,
                                            "Session Management");
    public static final GUILoggerSABusinessProperty SECURITY_ADMIN =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_SECURITY_ADMIN, "Security Admin");
    public static final GUILoggerSABusinessProperty TRADING_PROPERTY =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_TRADING_PROPERTY,
                                            "Trading Property");
    public static final GUILoggerSABusinessProperty ORDER_HANDLING =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_ORDER_HANDLING, "Order Handling");
    public static final GUILoggerSABusinessProperty MM_QUOTE =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_MM_QUOTE, "Market Maker Quote");
    public static final GUILoggerSABusinessProperty TRADE_QUERY =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_TRADE_QUERY, "Trade Query");
    public static final GUILoggerSABusinessProperty PRODUCT_GROUPS =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_PRODUCT_GROUPS, "Product Groups");
    public static final GUILoggerSABusinessProperty CALENDAR_ADMIN =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_CALENDAR_ADMIN, "Calendar Admin");
    public static final GUILoggerSABusinessProperty PROPERTY_SERVICE =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_PROPERTY_SERVICE,
                                            "Property Service");
    public static final GUILoggerSABusinessProperty AGENT_QUERY =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_AGENT_QUERY, "Agent Query");
    public static final GUILoggerSABusinessProperty ALERTS =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_ALERTS, "Alerts");
    public static final GUILoggerSABusinessProperty ROUTING_PROPERTY =
            new GUILoggerSABusinessProperty(GUILoggerMsgTypes.LOG_ROUTING_PROPERTY,
                                            "Routing Property");

    private static final IGUILoggerBusinessProperty[] businessProperties =
            {COMMON,
                                                                            MARKET_QUERY,
                                                                            ORDER_ENTRY,
                                                                            ORDER_QUERY,
                                                                            PRODUCT_DEFINITION,
                                                                            PRODUCT_QUERY,
                                                                            QRM,
                                                                            QUOTE,
                                                                            RFQ,
                                                                            TEXT_MESSAGE,
                                                                            TRADING_SESSION,
                                                                            USER_HISTORY,
                                                                            USER_MANAGEMENT,
                                                                            USER_PREFERENCES,
                                                                            USER_SESSION,
                                                                            ORDER_BOOK,
                                                                            PRODUCT_MAINTENANCE,
                                                                            TRADE_MAINTENANCE,
                                                                            FIRM_MAINTENANCE,
                                                                            SESSION_MANAGEMENT,
                                                                            SECURITY_ADMIN,
                                                                            TRADING_PROPERTY,
                                                                            ORDER_HANDLING,
                                                                            MM_QUOTE,
                                                                            TRADE_QUERY,
                                                                            PREFERENCE_CONVERSION,
                                                                            PRODUCT_GROUPS,
                                                                            CALENDAR_ADMIN,
                                                                            AGENT_QUERY,
                                                                            ALERTS,
             PROPERTY_SERVICE,
             ROUTING_PROPERTY,
             PERMISSION_MATRIX,
             OMT
            };

    protected GUILoggerSABusinessProperty(int key, String name)
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
        return GUILoggerMsgTypes.LOG_BUSINESS_MIN;
    }

    public static int getMaxIndex()
    {
        return GUILoggerMsgTypes.LOG_SA_BUSINESS_MAX;
    }
}
