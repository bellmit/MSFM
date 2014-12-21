// -----------------------------------------------------------------------------------
// Source file: ExchangeIndicatorImpl
//
// PACKAGE: com.cboe.presentation.marketData
//
// Created: Jul 9, 2004 3:36:28 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.marketData.ExchangeIndicator;

public interface ExchangeIndicatorFormatStrategy extends FormatStrategy
{
    final public static String BRIEF_INDICATOR = "Brief Indicator";
    final public static String BRIEF_INDICATOR_SORTED_ARRAY = "Brief Sorted Indicators";

    final public static String FULL_INDICATOR  = "Full Indicator";

    final public static String BRIEF_INDICATOR_DESC = "Brief Indicator Character";
    final public static String BRIEF_INDICATOR_SORTED_ARRAY_DESC = "Brief Sorted Array Of Indicators";

    final public static String FULL_INDICATOR_DESC  = "Full Indicator String";

    public String format(ExchangeIndicator indicator);
    public String format(ExchangeIndicator indicator, String styleName);

    public String format(ExchangeIndicator[] indicator);
    public String format(ExchangeIndicator[] indicator, String styleName);
}

