//
// -----------------------------------------------------------------------------------
// Source file: TradeNotificationFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiTradeNotification.TradeNotificationStruct;

public interface TradeNotificationFormatStrategy extends FormatStrategy
{
    final public static String MESSAGE_SUMMARY = "Message Summary";
    final public static String PRODUCT_ONLY = "Product Only";
    final public static String PRODUCT_DESCRIPTION = "Product Description";
    final public static String CLASS_DESCRIPTION = "Class Description";
    final public static String REPORTING_CLASS_DESCRIPTION = "Reporting Class Description";
    final public static String MESSAGE_SUMMARY_DESC = "Message Summary for Info Panel Display";
    final public static String PRODUCT_ONLY_DESC = "Only the product information for report title";
    final public static String PRODUCT_DESCRIPTION_DESC = "The product description with key";
    final public static String CLASS_DESCRIPTION_DESC = "The Class Description with key";
    final public static String REPORTING_CLASS_DESCRIPTION_DESC = "The Reporting Class Description with key";

    public String format(TradeNotificationStruct tradeNotificationStruct);

    public String format(TradeNotificationStruct tradeNotificationStruct, String styleName);

}