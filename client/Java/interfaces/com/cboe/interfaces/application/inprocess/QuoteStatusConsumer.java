package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiProduct.ProductStruct;

/**
 * @author Jing Chen
 */
public interface QuoteStatusConsumer
{
    public void acceptQuoteBustReport(BustReportStruct[] bustReports, ProductStruct product, int quoteKey, short statusChange, int queueDepth);
    public void acceptQuoteDeleteReport(QuoteDetailStruct quote, short reason, int queueDepth);
//This method is used when both sides are filled.
    public void acceptQuoteFilledReport(FilledReportStruct[] fill,  ProductStruct product, int quoteKey, short statusChange, int queueDepth);
    public void acceptQuoteFilledReport(FilledReportStruct[] fill, QuoteDetailStruct quote, int queueDepth);
    public void acceptQuoteStatus(QuoteDetailStruct quoteDetails, int queueDepth);
    public void acceptQuoteUpdate(QuoteDetailStruct quoteDetails, int queueDepth);
}
