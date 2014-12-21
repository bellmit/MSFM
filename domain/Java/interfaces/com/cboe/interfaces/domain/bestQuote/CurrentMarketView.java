package com.cboe.interfaces.domain.bestQuote;

import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiMarketData.CurrentMarketViewStruct;

/**
 *
 */
public interface CurrentMarketView {
    
    public short getType();
    public Price getBidPrice();
    public int getBidSize();
    public int getNonVolumeContingentBidSize();
    public Price getAskPrice();
    public int getAskSize();
    public int getNonVolumeContingentAskSize();
    public void update(CurrentMarketViewStruct newView);
    public CurrentMarketViewStruct toStruct();
}
