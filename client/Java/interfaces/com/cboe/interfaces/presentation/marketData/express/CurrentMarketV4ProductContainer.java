//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketV4ProductContainer.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData.express;

import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;

/**
 * Contains Best and Best-Public CurrentMarketV4s for a Product at an Exchange.
 */
public interface CurrentMarketV4ProductContainer extends V4MarketData
{
    public CurrentMarketV4 getBestMarket();
    public CurrentMarketV4 getBestPublicMarketAtTop();

    public void setBestMarket(CurrentMarketStructV4 bestMarket);
    public void setBestPublicMarketAtTop(CurrentMarketStructV4 bestPublicMarket);
    public void setBestMarket(CurrentMarketV4 bestMarket);
    public void setBestPublicMarketAtTop(CurrentMarketV4 bestPublicMarket);
}
