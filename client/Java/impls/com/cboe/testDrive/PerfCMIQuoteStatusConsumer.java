package com.cboe.testDrive;


import com.cboe.idl.cmiCallback.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiUser.*;
import com.cboe.idl.cmiQuote.QuoteCancelReportStruct;
/**
 *
 */
public class PerfCMIQuoteStatusConsumer extends com.cboe.idl.cmiCallback.CMIQuoteStatusConsumerPOA
{
    com.cboe.idl.cmi.UserSessionManager userSessionManager=null;
    com.cboe.idl.cmiUser.UserLogonStruct userLogonStruct=null;

        public void acceptQuoteStatus(com.cboe.idl.cmiQuote.QuoteDetailStruct[] quotes)

        {
            System.out.println("Just received acceptQuoteStatus from CAS...");
        }

    public void acceptQuoteCancelReport(QuoteCancelReportStruct struct)
    {
        System.out.println("Just received acceptQuoteCancelReport from CAS...");
    }

    public void acceptQuoteFilledReport(com.cboe.idl.cmiQuote.QuoteFilledReportStruct filledReport)

             {
             System.out.println("Just received acceptQuoteFilledReport from CAS...");
             }

       public void acceptQuoteBustReport(com.cboe.idl.cmiQuote.QuoteBustReportStruct bustReport)

             {
             System.out.println("Just received acceptQuoteBustReport from CAS...");
             }
    /**
     */
}
