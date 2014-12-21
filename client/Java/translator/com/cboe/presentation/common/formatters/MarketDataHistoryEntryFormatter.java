// -----------------------------------------------------------------------------------
// Source file: MarketDataHistoryEntryTypes
//
// PACKAGE: com.cboe.presentation.common.formatters
// 
// Created: Jul 8, 2004 3:03:57 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.MarketDataHistoryEntryFormatStrategy;

public class MarketDataHistoryEntryFormatter extends Formatter implements MarketDataHistoryEntryFormatStrategy
{
    public static final short QUOTE_ENTRY            = com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes.QUOTE_ENTRY;
    public static final short PRICE_REPORT_ENTRY     = com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes.PRICE_REPORT_ENTRY;
    public static final short EXPECTED_OPEN_PRICE    = com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes.EXPECTED_OPEN_PRICE;
    public static final short MARKET_CONDITION_ENTRY = com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes.MARKET_CONDITION_ENTRY;
    public static final short UNSIZED_QUOTE_ENTRY    = com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes.UNSIZED_QUOTE_ENTRY;
    public static final short SHORT_SALE_TRIGGER_ON  = com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes.SHORT_SALE_TRIGGER_ON;
    public static final short SHORT_SALE_TRIGGER_OFF = com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes.SHORT_SALE_TRIGGER_OFF;

    public static final String QUOTE_ENTRY_STRING_FULL            = "Quote";
    public static final String UNSIZED_QUOTE_ENTRY_STRING_FULL    = "Unsized Quote";
    public static final String PRICE_REPORT_ENTRY_STRING_FULL     = "Trade";
    public static final String EXPECTED_OPEN_PRICE_STRING_FULL    = "Opening";
    public static final String MARKET_CONDITION_ENTRY_STRING_FULL = "Market Condition";
    public static final String SHORT_SALE_TRIGGER_ON_STRING_FULL  = "Short Sale Trigger On";
    public static final String SHORT_SALE_TRIGGER_OFF_STRING_FULL = "Short Sale Trigger Off";
    public static final String UNKNOWN_STRING_FULL                = "Unknown";                        

    public static final String QUOTE_ENTRY_STRING_BRIEF            = "Q";
    public static final String PRICE_REPORT_ENTRY_STRING_BRIEF     = "L";
    public static final String EXPECTED_OPEN_PRICE_STRING_BRIEF    = "E";
    public static final String MARKET_CONDITION_ENTRY_STRING_BRIEF = "M";
    public static final String UNSIZED_QUOTE_ENTRY_STRING_BRIEF    = "U";
    public static final String SHORT_SALE_TRIGGER_ON_STRING_BRIEF  = "SSO";
    public static final String SHORT_SALE_TRIGGER_OFF_STRING_BRIEF = "SSF";

    public MarketDataHistoryEntryFormatter()
    {
        super();

        addStyle(FULL, FULL_DESC);
        addStyle(BRIEF, BRIEF_DESC);

        setDefaultStyle(FULL);
    }

    public String format(short entryType)
    {
        return format(entryType, getDefaultStyle());
    }

    public String format(short entryType, String styleName)
    {
        validateStyle(styleName);

        String retValue = "";

        if (styleName.equalsIgnoreCase(BRIEF))
        {
            retValue = formatBrief(entryType);
        }
        else if (styleName.equalsIgnoreCase(FULL))
        {
            retValue = formatFull(entryType);
        }

        return retValue;
    }

    private String formatBrief(short entryType)
    {
        switch (entryType)
        {
            case QUOTE_ENTRY:
                return QUOTE_ENTRY_STRING_BRIEF;
            case UNSIZED_QUOTE_ENTRY:
                return UNSIZED_QUOTE_ENTRY_STRING_BRIEF;
            case PRICE_REPORT_ENTRY:
                return PRICE_REPORT_ENTRY_STRING_BRIEF;
            case EXPECTED_OPEN_PRICE:
                return EXPECTED_OPEN_PRICE_STRING_BRIEF;
            case MARKET_CONDITION_ENTRY:
                return MARKET_CONDITION_ENTRY_STRING_BRIEF;
            case SHORT_SALE_TRIGGER_ON:
                return SHORT_SALE_TRIGGER_ON_STRING_BRIEF;
            case SHORT_SALE_TRIGGER_OFF:
                return SHORT_SALE_TRIGGER_OFF_STRING_BRIEF;
            default:
                return new StringBuffer().append("[ ").append(entryType).append(" ]").toString();
        }
    }

    private String formatFull(short entryType)
    {
        switch (entryType)
        {
            case QUOTE_ENTRY:
                return QUOTE_ENTRY_STRING_FULL;
            case UNSIZED_QUOTE_ENTRY:
                return UNSIZED_QUOTE_ENTRY_STRING_FULL;
            case PRICE_REPORT_ENTRY:
                return PRICE_REPORT_ENTRY_STRING_FULL;
            case EXPECTED_OPEN_PRICE:
                return EXPECTED_OPEN_PRICE_STRING_FULL;
            case MARKET_CONDITION_ENTRY:
                return MARKET_CONDITION_ENTRY_STRING_FULL;
            case SHORT_SALE_TRIGGER_ON:
                return SHORT_SALE_TRIGGER_ON_STRING_FULL;
            case SHORT_SALE_TRIGGER_OFF:
                return SHORT_SALE_TRIGGER_OFF_STRING_FULL;
            default:
                return new StringBuffer().append(UNKNOWN_STRING_FULL).append("[ ").append(entryType).append(" ]").toString();
        }
    }

} // -- end of class MarketDataHistoryEntryTypes
