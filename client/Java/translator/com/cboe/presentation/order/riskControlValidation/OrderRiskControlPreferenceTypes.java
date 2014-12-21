//
// -----------------------------------------------------------------------------------
// Source file: OrderRiskControlPreferenceTypes.java
//
// PACKAGE: com.cboe.presentation.properties.orderEntryConfirmation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order.riskControlValidation;

/**
 * This enum defines the supported types of pre-trade risk control business preferences.
 */
enum OrderRiskControlPreferenceTypes
{
    SINGLE_ORDER_QTY("SingleOrderQty"),
    SINGLE_ORDER_DOLLAR_VALUE("SingleOrderDollarValue"),
    DUPLICATE_ORDER_TIME_RANGE("DupOrderTimeRange"),
    DUPLICATE_ORDER_ALLOWED_COUNT("DupOrderAllowedCount"),
    DAILY_GROSS_TRADED_QTY("DailyGrossTradedQty"),
    DAILY_GROSS_DOLLAR_VALUE("DailyGrossDollarValue"),
    DAILY_NET_TRADED_QTY("DailyNetTradedQty"),
    DAILY_NET_DOLLAR_VALUE("DailyNetDollarValue");

    public String prefPrefix;
    private OrderRiskControlPreferenceTypes(String prefPrefix)
    {
        this.prefPrefix = prefPrefix;
    }

    public String getBusinessPrefPrefix()
    {
        return prefPrefix;
    }
}
