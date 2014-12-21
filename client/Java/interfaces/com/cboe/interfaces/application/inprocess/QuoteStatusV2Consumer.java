package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiProduct.ProductStruct;

/**
 * @author Magic Magee
 */
public interface QuoteStatusV2Consumer 
{
    public void acceptQuoteFilledReport(FilledReportStruct[] fill, QuoteDetailStruct quote, int queueDepth);
}
