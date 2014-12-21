//
// ------------------------------------------------------------------------
// FILE: ExchangeMarketFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.ExchangeMarket;
import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;

public interface ExchangeMarketFormatStrategy extends FormatStrategy
{
    public static final String BRIEF_STYLE = "Brief Style";
    public static final String BRIEF_DESCRIPTION = "Single Line";
    public static final String FULL_STYLE = "Full Style";
    public static final String FULL_DESCRIPTION = "Multiple Lines";
    public static final String FULL_STYLE_TWO_COLUMN_NAME = "Two Column Style";
    public static final String FULL_STYLE_TWO_COLUMN_DESCRIPTION = "Two Columns, Bid then Ask";
    String format(ExchangeMarket exchangeMarket);
    String format(ExchangeMarket exchangeMarket, String style);
    String format(ExchangeMarketStruct exchangeMarketStruct);
    String format(ExchangeMarketStruct exchangeMarketStruct, String style);
}
