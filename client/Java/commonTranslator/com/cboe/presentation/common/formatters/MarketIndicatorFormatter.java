//
// -----------------------------------------------------------------------------------
// Source file: MarketIndicatorFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiConstants.MarketIndicators;

public class MarketIndicatorFormatter
{
    public final static String REGULAR_DESC = "Regular";
    public final static String AUTO_EXECUTION_DESC = "Auto Execution";
    public final static String BID_IS_BOOK_DESC = "Bid Is Book";
    public final static String ASK_IS_BOOK_DESC = "Ask Is Book";
    public final static String BID_ASK_IS_BOOK_DESC = "Bid Ask Is Book";
    public final static String INACTIVE_DESC = "Inactive";
    public final static String ROTATION_DESC = "Rotation";
    public final static String FAST_MARKET_DESC = "Fast Market";
    public final static String TRADING_HALT_DESC = "Trading Halt";
    public final static String DISQUALIFIED_DESC = "Disqualified";
    public final static String UNKNOWN_DESC = "Unknown";

    /**
     * Returns a String representation of the char constants in com.cboe.idl.cmiConstants.MarketIndicators.
     *
     * @param marketIndicator
     * @return String
     */
    public static String toString(byte marketIndicator)
    {
        String retVal = UNKNOWN_DESC;
        switch(marketIndicator)
        {
            case MarketIndicators.REGULAR_QUOTE:
                retVal = REGULAR_DESC;
                break;
            case MarketIndicators.AUTO_EXECUTION:
                retVal = AUTO_EXECUTION_DESC;
                break;
            case MarketIndicators.BID_IS_BOOK:
                retVal = BID_IS_BOOK_DESC;
                break;
            case MarketIndicators.ASK_IS_BOOK:
                retVal = ASK_IS_BOOK_DESC;
                break;
            case MarketIndicators.BID_ASK_IS_BOOK:
                retVal = BID_ASK_IS_BOOK_DESC;
                break;
            case MarketIndicators.INACTIVE:
                retVal = INACTIVE_DESC;
                break;
            case MarketIndicators.ROTATION:
                retVal = ROTATION_DESC;
                break;
            case MarketIndicators.FAST_MARKET:
                retVal = FAST_MARKET_DESC;
                break;
            case MarketIndicators.TRADING_HALT:
                retVal = TRADING_HALT_DESC;
                break;
            case MarketIndicators.DISQUALIFIED:
                retVal = DISQUALIFIED_DESC;
                break;
            case MarketIndicators.UNKNOWN:
                retVal = UNKNOWN_DESC;
                break;
        }
        return retVal;
    }
}
