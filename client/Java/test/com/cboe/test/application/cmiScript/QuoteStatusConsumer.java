package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIQuoteStatusConsumerPOA;
import com.cboe.idl.cmiQuote.QuoteBustReportStruct;
import com.cboe.idl.cmiQuote.QuoteCancelReportStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;

public class QuoteStatusConsumer extends CMIQuoteStatusConsumerPOA
{
    public void acceptQuoteStatus(QuoteDetailStruct qdseq[])
    {
        Log.message("QuoteStatusConsumer.acceptQuoteStatus "
                + Struct.toString(qdseq));
    }

    public void acceptQuoteFilledReport(QuoteFilledReportStruct qfr)
    {
        Log.message("QuoteStatusConsumer.acceptQuoteFilledReport "
                + Struct.toString(qfr));
    }

    public void acceptQuoteBustReport(QuoteBustReportStruct qbr)
    {
        Log.message("QuoteStatusConsumer.acceptQuoteBusReport "
                + Struct.toString(qbr));
    }

    public void acceptQuoteCancelReport(QuoteCancelReportStruct qcr)
    {
        Log.message("QuoteStatusConsumer.acceptQuoteCancelReport "
                + Struct.toString(qcr));
    }
}
