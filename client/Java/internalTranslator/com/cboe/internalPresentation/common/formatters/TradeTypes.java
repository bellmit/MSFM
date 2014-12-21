//
// -----------------------------------------------------------------------------------
// Source file: TradeTypes.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

public class TradeTypes
{
    public static final char BLOCK_TRADE = com.cboe.idl.constants.TradeTypes.BLOCK_TRADE;
    public static final char EXCHANGE_FOR_PHYSICAL = com.cboe.idl.constants.TradeTypes.EXCHANGE_FOR_PHYSICAL;
    public static final char REGULAR_TRADE = com.cboe.idl.constants.TradeTypes.REGULAR_TRADE;
    public static final char CASH_TRADE = com.cboe.idl.constants.TradeTypes.CASH_TRADE;
    public static final char NEXT_DAY_TRADE = com.cboe.idl.constants.TradeTypes.NEXT_DAY_TRADE;
    public static final char SOLD_TRADE = com.cboe.idl.constants.TradeTypes.SOLD;

    public static final char INTERMARKET_SWEEP_TRADE=com.cboe.idl.constants.TradeTypes.INTERMARKET_SWEEP;
    public static final char LINKAGE_TRADE=com.cboe.idl.constants.TradeTypes.NO_PRINT_LINKAGE_TRADE;
    public static final char TRADE_REVERSAL=com.cboe.idl.constants.TradeTypes.REGULAR_TRADE_REVERSAL;
    public static final char MANUAL_LINKAGE_TRADE=com.cboe.idl.constants.TradeTypes.NO_PRINT_LINKAGE_TRADE_MANUAL;
    public static final char LINKAGE_TRADE_REVERSAL=com.cboe.idl.constants.TradeTypes.NO_PRINT_LINKAGE_TRADE_REVERSAL;

    public static final char MANUAL_TRADE = com.cboe.idl.constants.TradeTypes.MANUAL_TRADE;
    public static final char PAR_TRADE = com.cboe.idl.constants.TradeTypes.PAR_TRADE;
    public static final char CROSS_PRODUCT_LEG_TRADE = com.cboe.idl.constants.TradeTypes.CROSS_PRODUCT_LEG_TRADE;
    public static final char CROSS_PRODUCT_CROSS_TRADE = com.cboe.idl.constants.TradeTypes.CROSS_PRODUCT_CROSS_TRADE;
    public static final char CROSS_PRODUCT_AIM_CROSS_TRADE = com.cboe.idl.constants.TradeTypes.CROSS_PRODUCT_AIM_CROSS_TRADE;
    public static final char GWAP_TRADE = com.cboe.idl.constants.TradeTypes.GWAP_TRADE;
    public static final char HANDHELD_TRADE = com.cboe.idl.constants.TradeTypes.HANDHELD_TRADE;
    public static final char PAR_TO_MARKET_MAKER_TRADE = com.cboe.idl.constants.TradeTypes.PAR_TO_MARKET_MAKER_TRADE;

    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE   = "INVALID_TYPE";

    private static final String BLOCK_TRADE_STRING = "Block";
    private static final String EXCHANGE_FOR_PHYSICAL_STRING = "Exch. for Phys.";
    private static final String REGULAR_TRADE_STRING = "Regular";
    private static final String CASH_TRADE_STRING = "Cash";
    private static final String NEXT_DAY_TRADE_STRING = "Next Day";
    private static final String SOLD_TRADE_STRING = "Sold";
    private static final String INTERMARKET_SWEEP_TRADE_STRING = "Sweep";
    private static final String NO_PRINT_LINKAGE_TRADE_STRING = "Linkage";
    private static final String TRADE_REVERSAL_STRING = "Trade Reversal";
    private static final String MANUAL_LINKAGE_TRADE_STRING = "Manual Linkage";
    private static final String LINKAGE_TRADE_REVERSAL_STRING = "Linkage Reversal";

    private static final String MANUAL_TRADE_STRING = "Manual";
    private static final String PAR_TRADE_STRING = "PAR";
    private static final String CROSS_PRODUCT_LEG_TRADE_STRING = "Cross Product Leg";
    private static final String CROSS_PRODUCT_CROSS_TRADE_STRING = "Cross Product Cross";
    private static final String CROSS_PRODUCT_AIM_CROSS_TRADE_STRING = "Cross Product AIM Cross";
    private static final String GWAP_TRADE_STRING = "GWAP";
    private static final String HANDHELD_TRADE_STRING = "Handheld";
    private static final String PAR_TO_MARKET_MAKER_TRADE_STRING = "PAR to Market Maker";

    private TradeTypes ()
    {}

    public static String toString(char tradeType)
    {
        return toString(tradeType, TRADERS_FORMAT);
    }

    public static String toString(char tradeType, String formatSpecifier)
    {
        if(formatSpecifier.equals(TRADERS_FORMAT))
        {
            switch (tradeType)
            {
                case BLOCK_TRADE:
                    return BLOCK_TRADE_STRING;
                case EXCHANGE_FOR_PHYSICAL:
                    return EXCHANGE_FOR_PHYSICAL_STRING;
                case REGULAR_TRADE:
                    return REGULAR_TRADE_STRING;
                case CASH_TRADE:
                    return CASH_TRADE_STRING;
                case NEXT_DAY_TRADE:
                    return NEXT_DAY_TRADE_STRING;
                case SOLD_TRADE:
                    return SOLD_TRADE_STRING;
                case INTERMARKET_SWEEP_TRADE:
                    return INTERMARKET_SWEEP_TRADE_STRING;
                case LINKAGE_TRADE:
                    return NO_PRINT_LINKAGE_TRADE_STRING;
                case MANUAL_LINKAGE_TRADE:
                	return MANUAL_LINKAGE_TRADE_STRING;
                case LINKAGE_TRADE_REVERSAL:
                	return LINKAGE_TRADE_REVERSAL_STRING;
                case TRADE_REVERSAL:
                	return TRADE_REVERSAL_STRING;
                case MANUAL_TRADE:
                    return MANUAL_TRADE_STRING;
                case PAR_TRADE:
                    return PAR_TRADE_STRING;
                case CROSS_PRODUCT_LEG_TRADE:
                    return CROSS_PRODUCT_LEG_TRADE_STRING;
                case CROSS_PRODUCT_CROSS_TRADE:
                    return CROSS_PRODUCT_CROSS_TRADE_STRING;
                case CROSS_PRODUCT_AIM_CROSS_TRADE:
                    return CROSS_PRODUCT_AIM_CROSS_TRADE_STRING;
                case GWAP_TRADE:
                    return GWAP_TRADE_STRING;
                case HANDHELD_TRADE:
                    return HANDHELD_TRADE_STRING;
                case PAR_TO_MARKET_MAKER_TRADE:
                    return PAR_TO_MARKET_MAKER_TRADE_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(" ").append(tradeType).toString();
            }
        }
        return new StringBuffer(30).append(INVALID_FORMAT).append(" ").append(formatSpecifier).toString();
    }
}