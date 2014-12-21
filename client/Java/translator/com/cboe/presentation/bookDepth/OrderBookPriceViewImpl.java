// -----------------------------------------------------------------------------------
// Source file: OrderBookPriceViewImpl.java
//
// PACKAGE: com.cboe.presentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.bookDepth;

import com.cboe.interfaces.presentation.bookDepth.OrderBookPriceView;
import com.cboe.interfaces.presentation.bookDepth.OrderBookPriceViewType;
import com.cboe.interfaces.presentation.marketData.MarketVolume;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.marketData.MarketVolumeFactory;
import com.cboe.idl.cmiMarketData.OrderBookPriceViewStruct;
import com.cboe.domain.util.BookDepthStructBuilder;

public class OrderBookPriceViewImpl extends AbstractBusinessModel implements OrderBookPriceView
{
    private OrderBookPriceViewStruct struct;
    private OrderBookPriceViewType viewType;
    private MarketVolume[] marketVolume;

    OrderBookPriceViewImpl(OrderBookPriceViewStruct struct)
    {
        this();
        checkParam(struct, "OrderBookPriceViewStruct");
        this.struct = struct;
    }

    private OrderBookPriceViewImpl()
    {
        super();
    }

    /**
     * Get View Type
     * @return view type
     */
    public OrderBookPriceViewType getViewType()
    {
        if (viewType == null)
        {
            viewType = OrderBookPriceViewTypeImpl.getByKey(struct.orderBookPriceViewType);
        }
        return viewType;
    }

    /**
     * Gets MarketVolume sequence for this view type
     * @return market volume sequence
     */
    public MarketVolume[] getMarketVolume()
    {
        if (marketVolume == null)
        {
                marketVolume = MarketVolumeFactory.create(struct.viewSequence);
        }
        return marketVolume;
    }

    public Object clone() throws CloneNotSupportedException
    {
        OrderBookPriceViewStruct clonedStruct = BookDepthStructBuilder.cloneOrderBookPriceViewStruct(this.struct);
        return OrderBookFactory.createOrderBookPriceView(clonedStruct);
    }
}
