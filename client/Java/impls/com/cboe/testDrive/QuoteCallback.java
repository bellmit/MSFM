package com.cboe.testDrive;

/**
 * This class is the Quote Callback for use by the Performance Driver Tool (PDT) test scripts.
 *
 * @author Dean Grippo
 */

import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.testDrive.*;
import com.cboe.infrastructureServices.foundationFramework.*;

public class QuoteCallback extends BObject implements com.cboe.interfaces.callback.QuoteStatusConsumer
{

    public void acceptQuoteCancelReport(QuoteCancelReportStruct struct)
    {
        System.out.println("Entering QuoteCallback::acceptQuoteCancelReport, id = "+ struct.quoteKey);
    }

    public QuoteCallback()
   {
     System.out.println("Entering QuoteCallback::constructor");
   }

   public void acceptQuoteStatus(com.cboe.idl.cmiQuote.QuoteDetailStruct[] quoteDetailStruct)
   {
//      CASMeter.setEndTime(Integer.valueOf(quoteDetailStruct[0].quote.userAssignedId).intValue());
      //System.out.println(quoteDetailStruct);
       System.out.println("Entering QuoteCallback::acceptQuoteStatus, id = "+ quoteDetailStruct[0].quote.userAssignedId);

   }

   public void acceptQuoteFilledReport(com.cboe.idl.cmiQuote.QuoteFilledReportStruct quoteFilledReportStruct)
   {
      System.out.println("Entering QuoteCallback::acceptQuoteFilledReport");
      System.out.println(quoteFilledReportStruct);
   }

   public void acceptQuoteBustReport(com.cboe.idl.cmiQuote.QuoteBustReportStruct quoteBustReportStruct)
   {
      System.out.println("Entering QuoteCallback::acceptQuoteBustReport");
   }



}

