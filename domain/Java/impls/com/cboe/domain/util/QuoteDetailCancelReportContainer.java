package com.cboe.domain.util;

import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteCancelReportStruct;

/**
 * This is container class for our data struct.
 * @author Connie Feng
 */

public class QuoteDetailCancelReportContainer
{
    private QuoteDetailStruct quoteDetail;
    private QuoteCancelReportStruct quoteCancel;

    /**
      * Sets the internal fields to the passed values
      */
    public QuoteDetailCancelReportContainer(QuoteDetailStruct quoteDetail, QuoteCancelReportStruct quoteCancel)
    {
        this.quoteDetail = quoteDetail;
        this.quoteCancel = quoteCancel;
    }

    public QuoteDetailStruct getQuoteDetailStruct()
    {
        return quoteDetail;
    }

    public QuoteCancelReportStruct getQuoteCancelReportStruct()
    {
        return quoteCancel;
    }
}
