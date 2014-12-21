package com.cboe.domain.util;

import com.cboe.idl.cmiQuote.QuoteStruct;
/**
 * Created by IntelliJ IDEA.
 * User: EbrahimR
 * Date: Mar 26, 2003
 * Time: 1:46:47 PM
 * To change this template use Options | File Templates.
 */
public class QuoteKeyCancelReportContainerV2 {



    private QuoteStruct[] quotes;
    private int[] groups;
    private short cancelReason;

    /**
      * Sets the internal fields to the passed values
      */
    public QuoteKeyCancelReportContainerV2(int[] groups, QuoteStruct[] quotes, short cancelReason)
    {
        this.groups = groups;
        quotes = new QuoteStruct[quotes.length];
        System.arraycopy(quotes,0,this.quotes,0,quotes.length);
        this.cancelReason = cancelReason;
    }

    public int[] getGroups()
    {
        return groups;
    }

    public QuoteStruct[] getQuotes()
    {
        return quotes;
    }

    public short getCancelReason()
    {
        return cancelReason;
    }
}

