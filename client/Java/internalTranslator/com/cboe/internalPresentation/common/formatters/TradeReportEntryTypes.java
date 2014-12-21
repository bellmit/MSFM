//
// -----------------------------------------------------------------------------------
// Source file: TradeReportEntryTypes.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

public class TradeReportEntryTypes
{
    public static final char ADD = com.cboe.idl.constants.TradeReportEntryTypes.ADD;
    public static final char DELETE = com.cboe.idl.constants.TradeReportEntryTypes.DELETE;

    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE   = "INVALID_TYPE";

    private static final String ADD_STRING = "Add";
    private static final String DELETE_STRING = "Delete";

    private TradeReportEntryTypes ()
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
                case ADD:
                    return ADD_STRING;
                case DELETE:
                    return DELETE_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(" ").append(tradeType).toString();
            }
        }
        return new StringBuffer(30).append(INVALID_FORMAT).append(" ").append(formatSpecifier).toString();
    }
}