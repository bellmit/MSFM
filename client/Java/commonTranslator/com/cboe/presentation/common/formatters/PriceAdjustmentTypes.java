// -----------------------------------------------------------------------------------
// Source file: PriceAdjustmentTypes.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

/**
 * Implements the PriceAdjustmentTypeFormatStrategy
 */
public class PriceAdjustmentTypes
{
    public static final short COMMON_DISTRIBUTION = com.cboe.idl.cmiConstants.PriceAdjustmentTypes.COMMON_DISTRIBUTION;
    public static final short DIVIDEND_CASH = com.cboe.idl.cmiConstants.PriceAdjustmentTypes.DIVIDEND_CASH;
    public static final short DIVIDEND_PERCENT = com.cboe.idl.cmiConstants.PriceAdjustmentTypes.DIVIDEND_PERCENT;
    public static final short DIVIDEND_STOCK = com.cboe.idl.cmiConstants.PriceAdjustmentTypes.DIVIDEND_STOCK;
    public static final short LEAP_ROLLOVER = com.cboe.idl.cmiConstants.PriceAdjustmentTypes.LEAP_ROLLOVER;
    public static final short MERGER = com.cboe.idl.cmiConstants.PriceAdjustmentTypes.MERGER;
    public static final short SPLIT = com.cboe.idl.cmiConstants.PriceAdjustmentTypes.SPLIT;
    public static final short SYMBOL_CHANGE = com.cboe.idl.cmiConstants.PriceAdjustmentTypes.SYMBOL_CHANGE;

    public static final String COMMON_DISTRIBUTION_STRING = "Common Distribution";
    public static final String DIVIDEND_CASH_STRING = "Dividend - Cash";
    public static final String DIVIDEND_PERCENT_STRING = "Dividend - Percent";
    public static final String DIVIDEND_STOCK_STRING = "Dividend - Stock";
    public static final String LEAP_ROLLOVER_STRING = "Leap Rollover";
    public static final String MERGER_STRING = "Merger";
    public static final String SPLIT_STRING = "Split";
    public static final String SYMBOL_CHANGE_STRING = "Symbol Change";
    public static final String UNKNOWN_ADJUSTMENT_STRING = "Unknown Adjustment Type";

    private PriceAdjustmentTypes()
    {
    }

    /**
     * Formats a ProductType
     * @param product to format
     */
    public static String toString(short priceAdjustmentType)
    {
        String retVal = "";
        switch(priceAdjustmentType)
        {
            case COMMON_DISTRIBUTION:
                retVal = COMMON_DISTRIBUTION_STRING;
                break;
            case DIVIDEND_CASH:
                retVal = DIVIDEND_CASH_STRING;
                break;
            case DIVIDEND_PERCENT:
                retVal = DIVIDEND_PERCENT_STRING;
                break;
            case DIVIDEND_STOCK:
                retVal = DIVIDEND_STOCK_STRING;
                break;
            case LEAP_ROLLOVER:
                retVal = LEAP_ROLLOVER_STRING;
                break;
            case MERGER:
                retVal = MERGER_STRING;
                break;
            case SPLIT:
                retVal = SPLIT_STRING;
                break;
            case SYMBOL_CHANGE:
                retVal = SYMBOL_CHANGE_STRING;
                break;
            default:
                retVal = new StringBuffer(20).append("[ ").append(priceAdjustmentType).append(" ]").toString();
                break;
        }
        return retVal;
    }
}
