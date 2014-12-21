package com.cboe.interfaces.domain.optionsLinkage;

import java.util.List;

import com.cboe.idl.order.MarketDetailStruct;

public interface AllExchangesBBO
{
    public List <SweepElement> getQualifiedExchangesBBO();
    public List <SweepElement> getDisqualifiedExchangesBBO();
    public void setQualifiedExchangesBBO(List <SweepElement> se);
    public void setDisqualifiedExchangesBBO(List <SweepElement> se);
    public MarketDetailStruct[] getMarketDetailStructs();
    public void setMarketDetailStructs(MarketDetailStruct[] detailStructs);
}
