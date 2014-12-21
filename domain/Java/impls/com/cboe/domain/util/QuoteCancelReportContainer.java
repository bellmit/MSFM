package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiQuote.QuoteStruct;

/**
 * This is container class for our data struct.
 * @author Emily Huang
 */

public class QuoteCancelReportContainer
{
    private QuoteStruct[] quotes;
    private int[] groups;
    private short cancelReason;
    private String eventInitiator;

    /**
      * Sets the internal fields to the passed values
    */
    public QuoteCancelReportContainer(int[] groups, QuoteStruct[] quoteKeys, short cancelReason)
    {
        this.groups = groups;
        this.quotes = quoteKeys;
        this.cancelReason = cancelReason;
    }

    public QuoteCancelReportContainer(int[] groups, QuoteStruct[] quoteKeys, short cancelReason, String eventInitiator)
    {
        this(groups, quoteKeys, cancelReason);
        this.eventInitiator = eventInitiator;
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
    public String getEventInitiator()
    {
        return eventInitiator;
    }
}
