package com.cboe.domain.util;

import com.cboe.idl.cmiQuote.QuoteBustReportStruct;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class QuoteInfoBustReportContainer {
    private QuoteBustReportStruct bustedQuoteReport;
    private QuoteInfoStruct quoteInfo;
    private DateTimeStruct  dateTimeAtCreation;

    /**
      * Sets the internal fields to the passed values
      */

    public QuoteInfoBustReportContainer(QuoteInfoStruct quoteInfo, QuoteBustReportStruct bustedQuoteReport) {
        this.quoteInfo      = quoteInfo;
    	this.bustedQuoteReport = bustedQuoteReport;
        this.dateTimeAtCreation = new DateWrapper().toDateTimeStruct();
    }

    public QuoteInfoStruct getQuoteInfoStruct()
    {
        return quoteInfo;
    }

    public QuoteBustReportStruct getQuoteBustReportStruct()
    {
        return bustedQuoteReport;
    }

    public DateTimeStruct getDateTimeAtCreation()
    {
        return dateTimeAtCreation;
    }
}
