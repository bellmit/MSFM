package com.cboe.interfaces.domain;

public interface TradingProductSyncListener
{
    public void acceptCurrentStateCodeUpdateFromSync(short p_currentStateCode);
    public void acceptClosingBidPriceUpdateFromSync(Price p_closingBidPrice);
    public void acceptClosingBidSizeUpdateFromSync(int p_closingBidSize);
    public void acceptClosingAskPriceUpdateFromSync(Price p_closingAskPrice);
    public void acceptClosingAskSizeUpdateFromSync(int p_closingAskSize);
    public void acceptLastSalePriceUpdateFromSync(Price p_lastSalePrice);
}
