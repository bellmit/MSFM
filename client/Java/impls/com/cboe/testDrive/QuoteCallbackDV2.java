package com.cboe.testDrive;

/**
 * This class is the Quote Callback for use by the Performance Driver Tool (PDT) test scripts.
 *
 * @author Dean Grippo
 */

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.application.test.*;
import com.cboe.idl.consumers.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;

public class QuoteCallbackDV2 extends com.cboe.idl.cmiCallbackV2.CMIQuoteStatusConsumerPOA
{
    private CASMeter casMeter = null;
    private int totalfills = 0;
    public QuoteCallbackDV2(CASMeter cm)
    {
        casMeter = cm;
        System.out.println("Entering QuoteCallbackV2::constructor");
    }

    public void acceptQuoteCancelReport(QuoteCancelReportStruct[] structs, int queueAction)
    {
//        System.out.println("Entering QuoteCallback::acceptQuoteCancelReport");
    }

    public void acceptQuoteDeleteReport(QuoteDeleteReportStruct[] structs, int queueAction)
    {
//        System.out.println("Entering QuoteCallback::acceptQuoteDeleteReport");
    }

    public void acceptQuoteStatus(com.cboe.idl.cmiQuote.QuoteDetailStruct[] quoteDetailStructs, int queueAction)
    {
        int numOfStatus = quoteDetailStructs.length;
        try {
            for (int i = 0; i < numOfStatus; i++)
            {
                casMeter.setEndTime(Integer.valueOf(quoteDetailStructs[i].quote.userAssignedId).intValue());
            }
        }
        catch (Exception e) {
            e.printStackTrace() ;
        }
        //System.out.println(quoteDetailStruct);
        //      System.out.println("Entering QuoteCallback::acceptQuoteStatus, id = "+ quoteDetailStruct[0].quote.userAssignedId);
    }

    public void acceptQuoteFilledReport(com.cboe.idl.cmiQuote.QuoteFilledReportStruct quoteFilledReportStruct, int queueAction)
    {
        int numOfFills = quoteFilledReportStruct.filledReport.length;
        try {
            for (int i = 0; i < numOfFills; i++)
            {
                casMeter.incrementFillCount(Integer.valueOf(quoteFilledReportStruct.filledReport[i].userAssignedId).intValue());
            }
        }
        catch (Exception e) {
            e.printStackTrace() ;
        }
    }

    public void acceptQuoteBustReport(com.cboe.idl.cmiQuote.QuoteBustReportStruct quoteBustReportStruct, int queueAction)
    {
        System.out.println("Entering QuoteCallback::acceptQuoteBustReport");
    }
}

