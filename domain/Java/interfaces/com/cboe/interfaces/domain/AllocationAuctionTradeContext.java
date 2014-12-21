package com.cboe.interfaces.domain;

/**
 * This interface is introduced to hold auction related trade context
 * to support auction related trades
 */
public interface AllocationAuctionTradeContext  extends AllocationTradeContext
{

    // to indicate whether includes or exclude the special group of the firm match group in UMA/CUMA,
    // it should be false by default.
    public void setIncludeFirmMatchGroup (boolean includeFirmMatchGroup);
    public boolean getIncludeFirmMatchGroup();

    // to indicate if allocates to orignal tradables only or not based on the timeStamp,  by default it is false
    public void setOriginalOnlyFlag (boolean originalOnly);
    public boolean isOriginalOnlyFlag ();

    // the timestamp used to determine if a tradable is original or not, default value is 0
    public void setTimeToCompare(long timeStamp);
    public long getTimeToCompare ();

    // to indicate if ignores match order or not, by default, we should include match order if any
    public void setIgnoreMatchOrder(boolean ignoreMatchOrder);
    public boolean getIgnoreMatchOrder();

    // to indicate if allocates to auction reponses only or not, by default should be false
    public void setAuctionResponsesOnlyFlag(boolean auctionResponsesOnly);
    public boolean getAuctionResponsesOnlyFlag();

}
