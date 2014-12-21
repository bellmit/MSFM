package com.cboe.interfaces.domain;

public interface DerivedQuoteSide extends QuoteSide
{
    public void setPrice(Price price);
    public void setBestLegMarkets(BestPriceStruct[] bestLegMkts);
    public BestPriceStruct[] getBestLegMarkets();
    public Price[] getLegPrices();
}
