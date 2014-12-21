package com.cboe.interfaces.domain.marketData;

import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

/**
 * This interface is designed to provide hold various market data structs. 
 */
public interface MarketDataStructsHolder {
    public int getProductKey();
    public CurrentMarketStruct getBestMarket();
    public CurrentMarketStruct getBestLimitMarket();
    public CurrentMarketStruct getBestPublicMarket();
    public CurrentMarketStruct getBestPublicMarketAtTop();
    //public void bestLimitBidInNBBO(boolean isInNBBO);
    //public void bestLimitAskInNBBO(boolean isInNBBO);
}
