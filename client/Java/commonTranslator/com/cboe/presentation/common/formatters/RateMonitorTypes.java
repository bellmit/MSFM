//
// -----------------------------------------------------------------------------------
// Source file: RateMonitorTypes.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.domain.RateMonitorTypeConstants;

public class RateMonitorTypes
{
    public static final short GET_BOOK_DEPTH = RateMonitorTypeConstants.GET_BOOK_DEPTH;
    public static final short QUOTES = RateMonitorTypeConstants.QUOTES;
    public static final short ACCEPT_QUOTE = RateMonitorTypeConstants.ACCEPT_QUOTE;
    public static final short ACCEPT_ORDER = RateMonitorTypeConstants.ACCEPT_ORDER;
    public static final short QUERY_INTERMARKET = RateMonitorTypeConstants.QUERY_INTERMARKET;
    public static final short BOOK_DEPTH_INTERMARKET = RateMonitorTypeConstants.BOOK_DEPTH_INTERMARKET;
    public static final short QUOTE_BLOCK_SIZE = RateMonitorTypeConstants.QUOTE_BLOCK_SIZE;
    public static final short ACCEPT_LIGHT_ORDER = RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER;

    private static final String GET_BOOK_DEPTH_STRING = "Book Depth Calls";
    private static final String QUOTES_STRING = "Quote Rate";
    private static final String ACCEPT_QUOTE_STRING = "Quote Calls";
    private static final String ACCEPT_ORDER_STRING = "Order Calls";
    private static final String QUERY_INTERMARKET_STRING = "Intermarket Query Calls";
    private static final String BOOK_DEPTH_INTERMARKET_STRING = "Intermarket Book Depth Calls";
    private static final String QUOTE_BLOCK_SIZE_STRING = "Quote Block Size";
    private static final String ACCEPT_LIGHT_ORDER_STRING = "Light Order Calls";

    private static final String UNKNOWN_STRING = "Unknown Rate Type:";

    public static final short[] ALL_TYPES = {
                                                GET_BOOK_DEPTH,
                                                QUOTES,
                                                ACCEPT_QUOTE,
                                                ACCEPT_ORDER,
                                                QUERY_INTERMARKET,
                                                BOOK_DEPTH_INTERMARKET,
                                                QUOTE_BLOCK_SIZE,
                                                ACCEPT_LIGHT_ORDER
                                            };

    public static String toString(short type)
    {
        switch(type)
        {
            case GET_BOOK_DEPTH:
                return GET_BOOK_DEPTH_STRING;
            case QUOTES:
                return QUOTES_STRING;
            case ACCEPT_QUOTE:
                return ACCEPT_QUOTE_STRING;
            case ACCEPT_ORDER:
                return ACCEPT_ORDER_STRING;
            case QUERY_INTERMARKET:
                return QUERY_INTERMARKET_STRING;
            case BOOK_DEPTH_INTERMARKET:
                return BOOK_DEPTH_INTERMARKET_STRING;
            case QUOTE_BLOCK_SIZE:
                return QUOTE_BLOCK_SIZE_STRING;
            case ACCEPT_LIGHT_ORDER:
                return ACCEPT_LIGHT_ORDER_STRING;
            default:
                return UNKNOWN_STRING + Short.toString(type);
        }
    }

    private RateMonitorTypes()
    {
    }
}
