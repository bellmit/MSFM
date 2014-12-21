package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUtil.*;

public interface PersonalBestBook
{
    public String getSessionName();
    public int getProductKey();
    public MarketVolumeStruct[] getBidSizeSequence();
    public MarketVolumeStruct[] getAskSizeSequence();
    public PriceStruct getBidPrice();
    public PriceStruct getAskPrice();
}
