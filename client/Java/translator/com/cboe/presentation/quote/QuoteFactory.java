// -----------------------------------------------------------------------------------
// Source file: QuoteFactory.java
//
// PACKAGE: com.cboe.presentation.quote;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.quote;

import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteCancelReportStruct;
import com.cboe.interfaces.presentation.quote.Quote;
import com.cboe.interfaces.presentation.quote.QuoteDetail;
import com.cboe.interfaces.presentation.quote.QuoteCancelReport;

/**
 *  Factory for creating instances of Quote
 */
public class QuoteFactory
{

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private QuoteFactory()
    {}

    /**
     * Creates an instance of a Quote from a QuoteStruct.
     * @param quoteStruct to wrap in instance of Quote
     * @return Quote to represent the QuoteStruct
     */
    public static Quote create(QuoteStruct quoteStruct)
    {
        if (quoteStruct == null)
        {
            throw new IllegalArgumentException("QuoteStruct can not be NULL");
        }

        Quote quote = new QuoteImpl(quoteStruct);

        return quote;
    }
    /**
     * Creates an instance of a QuoteDetail from a QuoteDetailStruct.
     * @param quoteDetailStruct to wrap in instance of QuoteDetail
     * @return QuoteDetail to represent the QuoteDetailStruct
     */
    public static QuoteDetail create(QuoteDetailStruct quoteDetailStruct)
    {
        if (quoteDetailStruct == null)
        {
            throw new IllegalArgumentException("quoteDetailStruct can not be NULL");
        }

        QuoteDetail quoteDetail = new QuoteDetailImpl(quoteDetailStruct);

        return quoteDetail;
    }
    /**
     * Creates an instance of a QuoteCancelReport from a QuoteCancelReportStruct.
     * @param quoteCancelReportStruct to wrap in instance of QuoteCancelReport
     * @return QuoteCancelReport to represent the QuoteCancelReportStruct
     */
    public static QuoteCancelReport create(QuoteCancelReportStruct quoteCancelReportStruct)
    {
        if (quoteCancelReportStruct == null)
        {
            throw new IllegalArgumentException("quoteCancelReportStruct can not be NULL");
        }

        QuoteCancelReport quoteCancelReport = new QuoteCancelReportImpl(quoteCancelReportStruct);

        return quoteCancelReport;
    }

}
