package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiUtil.DateTimeStruct;

/**
 * This is container class for our data struct.
 * @author Connie Feng
 */

public class QuoteKeyCancelReportContainer
{
    private int[] quoteKeys;
    private int[] groups;
    private short cancelReason;
    private DateTimeStruct timeStamp;
    private String eventInitiator;

    /**
      * Sets the internal fields to the passed values
    */
    public QuoteKeyCancelReportContainer(int[] groups, int[] quoteKeys, short cancelReason)
    {
        this.groups = groups;
        this.quoteKeys = quoteKeys;
        this.cancelReason = cancelReason;
        this.timeStamp = new DateWrapper().toDateTimeStruct();
    }

    public QuoteKeyCancelReportContainer(int[] groups, int[] quoteKeys, short cancelReason, String eventInitiator)
    {
        this(groups, quoteKeys, cancelReason);
        this.eventInitiator = eventInitiator;
    }

    public int[] getGroups()
    {
        return groups;
    }

    public int[] getQuoteKeys()
    {
        return quoteKeys;
    }

    public short getCancelReason()
    {
        return cancelReason;
    }
    public DateTimeStruct getTimeStamp()
    {
        return timeStamp;
    }
    public String getEventInitiator()
    {
        return eventInitiator;
    }
}
