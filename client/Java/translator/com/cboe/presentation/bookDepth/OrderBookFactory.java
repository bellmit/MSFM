//
// -----------------------------------------------------------------------------------
// Source file: OrderBookFactory.java
//
// PACKAGE: com.cboe.presentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.bookDepth;

import com.cboe.idl.cmiMarketData.*;

import com.cboe.interfaces.presentation.marketData.MarketVolume;
import com.cboe.interfaces.presentation.bookDepth.*;
import com.cboe.presentation.marketData.MarketVolumeImpl;

public class OrderBookFactory
{
    public static OrderBookPrice createOrderBookPrice(OrderBookPriceStruct struct)
    {
        return new OrderBookPriceImpl(struct);
    }

    public static BookDepth createBookDepth(BookDepthStruct struct)
    {
        return new BookDepthImpl(struct);
    }

    public static BookDepthUpdate createBookDepthUpdate(BookDepthUpdateStruct struct)
    {
        return new BookDepthUpdateImpl(struct);
    }

    public static BookDepthUpdatePrice createBookDepthUpdatePrice(BookDepthUpdatePriceStruct struct)
    {
        return new BookDepthUpdatePriceImpl(struct);
    }

    public static OrderBookPriceView createOrderBookPriceView(OrderBookPriceViewStruct viewStruct)
    {
        return new OrderBookPriceViewImpl(viewStruct);
    }

    public static OrderBookPriceView[] createOrderBookPriceViews(OrderBookPriceViewStruct[] viewStructs)
    {
        OrderBookPriceView[] views = new OrderBookPriceView[viewStructs.length];
        for (int i = 0; i < views.length; i++)
        {
            views[i] = createOrderBookPriceView(viewStructs[i]);
        }

        return views;
    }

    public static DetailOrderBookPrice createDetailOrderBookPrice(OrderBookPriceStructV2 struct)
    {
        return new DetailOrderBookPriceImpl(struct);
    }

    public static DetailOrderBookPrice[] createDetailOrderBookPrices(OrderBookPriceStructV2[] structs)
    {
        DetailOrderBookPrice[] prices = new DetailOrderBookPrice[structs.length];
        for (int i = 0; i < prices.length; i++)
        {
            prices[i] = createDetailOrderBookPrice(structs[i]);
        }

        return prices;
    }

    public static DetailBookDepth createDetailBookDepth(BookDepthStructV2 struct)
    {
        return new DetailBookDepthImpl(struct);
    }

}
