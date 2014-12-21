//
// -----------------------------------------------------------------------------------
// Source file: ManualReportingFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.marketData.ManualPriceReportEntryStruct;
import com.cboe.idl.quote.ManualQuoteDetailStruct;
import com.cboe.idl.quote.ManualQuoteStruct;

/**
 * Defines a contract for a class that formats manualReporting structs and wrappers.
 */
public interface ManualReportingFormatStrategy extends FormatStrategy
{
    public static final String BRIEF = "Brief/One Line";

    public static final String BRIEF_DESCRIPTION = "Brief information designed to fit on one line.";

    /**
    * Defines a method for formatting ManualQuoteStruct
    * @param manualQuote to format.
    * @return formatted string
    */
    public String format(ManualQuoteStruct manualQuote, String styleName);

    /**
     * Defines a method for formatting ManualQuoteDetailStruct.
     * @param manualQuoteDetail to format.
     * @param styleName to use for formatting.
     * @return formatted string
     */
    public String format(ManualQuoteDetailStruct manualQuoteDetail, String styleName);

    /**
     * Defines a method for formatting ManualPriceStruct.
     * @param manualPrice to format.
     * @param styleName to use for formatting.
     * @return formatted string
     */
    public String format(ManualPriceReportEntryStruct manualPrice, String styleName);
}
