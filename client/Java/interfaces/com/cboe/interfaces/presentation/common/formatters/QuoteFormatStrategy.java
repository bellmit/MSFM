//
// -----------------------------------------------------------------------------------
// Source file: QuoteFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.interfaces.presentation.quote.Quote;
import com.cboe.interfaces.presentation.quote.QuoteDetail;
import com.cboe.interfaces.presentation.quote.QuoteEntry;

/**
 * Defines a contract for a class that formats Quotes.
 * @author Troy Wehrle
 */
public interface QuoteFormatStrategy extends FormatStrategy
{
    public static final String BRIEF = "Brief/One Line";
    public static final String CLASSLESS_INFORMATION = "Classless Information";
    public static final String FULL_INFORMATION = "Full Information/Single Column";

    public static final String BRIEF_DESCRIPTION = "Brief information designed to fit on one line.";
    public static final String CLASSLESS_INFORMATION_DESCRIPTION = "All Struct information except product info ran down two columns, tab separated with fields left justified.";
    public static final String FULL_INFORMATION_DESCRIPTION ="Full Struct information ran down one column on the left";

/**
 * Defines a method for formatting QuoteFilledReportStruct's.
 * @param quoteFill to format.
 * @return formatted string
 */
//public String format(QuoteFilledReportStruct quoteFill);
/**
 * Defines a method for formatting QuoteFilledReportStruct's.
 * @param quoteFill to format.
 * @param styleName to use for formatting.
 * @param int reportIndex
 * @return formatted string
 */
public String format(QuoteFilledReportStruct quoteFill, String styleName, int reportIndex);
/**
 * Defines a method for formatting Quotes.
 * @param quote to format.
 * @return formatted string
 */
//public String format(QuoteStruct quote);
/**
 * Defines a method for formatting Quotes.
 * @param quote to format.
 * @param styleName to use for formatting.
 * @return formatted string
 */
public String format(QuoteStruct quote, String styleName);

    /**
     * Defines a method for formatting Quotes.
     * @param quote to format.
     * @param styleName to use for formatting.
     * @return formatted string
     */
    public String format(Quote quote, String styleName);
    /**
     * Defines a method for formatting QuoteDetails.
     * @param quoteDetail to format.
     * @param styleName to use for formatting.
     * @return formatted string
     */
    public String format(QuoteDetail quoteDetail, String styleName);

    /**
     * Defines a method for formatting QuoteEntry.
     * @param quoteEntry to format.
     * @param styleName to use for formatting.
     * @return formatted string
     */
    public String format(QuoteEntry quoteEntry, String styleName);
}
