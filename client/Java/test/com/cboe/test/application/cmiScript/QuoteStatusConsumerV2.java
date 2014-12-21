package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerPOA;
import com.cboe.idl.cmiQuote.QuoteBustReportStruct;
import com.cboe.idl.cmiQuote.QuoteDeleteReportStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;

public class QuoteStatusConsumerV2 extends CMIQuoteStatusConsumerPOA
{
    public void acceptQuoteStatus(QuoteDetailStruct qdseq[], int queueDepth)
    {
        Log.message("QuoteStatusConsumer.acceptQuoteStatus "
                + Struct.toString(qdseq) + " queueDepth:" + queueDepth);
    }

    public void acceptQuoteFilledReport(
            QuoteFilledReportStruct qfr, int queueDepth)
    {
        Log.message("QuoteStatusConsumer.acceptQuoteFilledReport "
                + Struct.toString(qfr) + " queueDepth:" + queueDepth);
    }

    public void acceptQuoteBustReport(QuoteBustReportStruct qbr, int queueDepth)
    {
        Log.message("QuoteStatusConsumer.acceptQuoteBusReport "
                + Struct.toString(qbr) + " queueDepth:" + queueDepth);
    }

    public void acceptQuoteDeleteReport(
            QuoteDeleteReportStruct[] qdrseq, int queueDepth)
    {
        Log.message("QuoteStatusConsumer.acceptQuoteCancelReport "
                + Struct.toString(qdrseq) + " queueDepth:" + queueDepth);
    }
}
