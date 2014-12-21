//
// -----------------------------------------------------------------------------------
// Source file: BustFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiOrder.OrderBustReinstateReportStruct;
import com.cboe.idl.cmiOrder.OrderBustReportStruct;
import com.cboe.idl.cmiQuote.QuoteBustReportStruct;

/**
 * Defines a contract for a class that formats order/quote bust structs.
 * @author Troy Wehrle
 */
public interface BustFormatStrategy extends FormatStrategy
{
    public static final String BRIEF_NAME = "Brief/One Line";
    public static final String FULL_INFORMATION_NAME =  "Full Information/Two Column Page";

    public static final String BRIEF_DESCRIPTION = "Brief information designed to fit on one line.";
    public static final String FULL_INFORMATION_DESCRIPTION =  "Full Struct information ran down two columns, tab separated with fields left justified.";

    /**
     * Defines a method for formatting order OrderBustReinstateReportStruct's.
     * @param orderBustReinstate to format.
     * @return formatted string
     */
    public String format(OrderBustReinstateReportStruct orderBustReinstate);
    /**
     * Defines a method for formatting order OrderBustReinstateReportStruct's.
     * @param orderBustReinstate to format.
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(OrderBustReinstateReportStruct orderBustReinstate, String styleName);
    /**
     * Defines a method for formatting order OrderBustReportStruct's.
     * @param orderBust to format.
     * @return formatted string
     */
    public String format(OrderBustReportStruct orderBust);
    /**
     * Defines a method for formatting order OrderBustReportStruct's.
     * @param orderBust to format.
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(OrderBustReportStruct orderBust, String styleName);
    /**
     * Defines a method for formatting order QuoteBustReportStruct's.
     * @param quoteBust to format.
     * @return formatted string
     */
    public String format(QuoteBustReportStruct quoteBust);
    /**
     * Defines a method for formatting order QuoteBustReportStruct's.
     * @param quoteBust to format.
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(QuoteBustReportStruct quoteBust, String styleName);
}
