package com.cboe.interfaces.domain.tradingProperty;

public interface MKTOrderDrillThroughPennies extends TradingProperty
{
    void setMinimumNBBORange(double minBid);
    void setMaximumNBBORange(double maxBid);
    void setNoOfPennies (double noOfPennies);
    double getMinimumNBBORange();
    double getMaximumNBBORange();
    double getNoOfPennies ();
}