// -----------------------------------------------------------------------------------
// Source file: OrderBookPriceView.java
//
// PACKAGE: com.cboe.interfaces.presentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.bookDepth;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.presentation.marketData.MarketVolume;

/**
 * Provides a contract for the OrderBookPriceView.
 */
public interface OrderBookPriceView extends BusinessModel
{
    /**
     * Get View Type
     * @return view type
     */
    public OrderBookPriceViewType getViewType();
    /**
     * Gets MarketVolume sequence for this view type
     * @return market volume sequence
     */
    public MarketVolume[] getMarketVolume();
}
