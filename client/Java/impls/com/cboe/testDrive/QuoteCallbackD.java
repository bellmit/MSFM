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

public class QuoteCallbackD extends com.cboe.idl.cmiCallback.CMIQuoteStatusConsumerPOA
{
    private CASMeter casMeter = null;
    private int totalfills = 0;
   public QuoteCallbackD(CASMeter cm)
   {
    casMeter = cm;
     System.out.println("Entering QuoteCallback::constructor");
   }

    public void acceptQuoteCancelReport(QuoteCancelReportStruct struct)
    {
        System.out.println("Entering QuoteCallback::acceptQuoteCancelReport");
    }

    public void acceptQuoteStatus(com.cboe.idl.cmiQuote.QuoteDetailStruct[] quoteDetailStruct)
   {
   try {
      casMeter.setEndTime(Integer.valueOf(quoteDetailStruct[0].quote.userAssignedId).intValue());
        }
        catch (Exception e) {
            e.printStackTrace() ;
        }
      //System.out.println(quoteDetailStruct);
//      System.out.println("Entering QuoteCallback::acceptQuoteStatus, id = "+ quoteDetailStruct[0].quote.userAssignedId);

   }

    public void acceptQuoteFilledReport(com.cboe.idl.cmiQuote.QuoteFilledReportStruct quoteFilledReportStruct)
    {
        try {
            casMeter.incrementFillCount(Integer.valueOf(quoteFilledReportStruct.filledReport[0].userAssignedId).intValue());
        }
        catch (Exception e) {
            e.printStackTrace() ;
        }
    }

   public void acceptQuoteBustReport(com.cboe.idl.cmiQuote.QuoteBustReportStruct quoteBustReportStruct)
   {
      System.out.println("Entering QuoteCallback::acceptQuoteBustReport");
   }



}

