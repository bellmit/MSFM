package com.cboe.domain.util;

import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class QuoteInfoFilledReportContainer {
    private QuoteFilledReportStruct filledQuoteReport;
    private QuoteInfoStruct quoteInfo;
    private DateTimeStruct dateTimeAtCreation;

    /**
      * Sets the internal fields to the passed values
      */

    public QuoteInfoFilledReportContainer(QuoteInfoStruct quoteInfo, QuoteFilledReportStruct filledQuoteReport) {
        this.quoteInfo      = quoteInfo;
    	this.filledQuoteReport = filledQuoteReport;
    	this.dateTimeAtCreation = new DateWrapper().toDateTimeStruct();
    }

    public QuoteInfoStruct getQuoteInfoStruct()
    {
        return quoteInfo;
    }

    public QuoteFilledReportStruct getQuoteFilledReportStruct()
    {
        return filledQuoteReport;
    }

    public DateTimeStruct getDateTimeAtCreation()
    {
        return dateTimeAtCreation;
    }
}
