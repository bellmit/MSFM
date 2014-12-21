//
// ------------------------------------------------------------------------
// FILE: OrderBookDetailPriceImpl.java
// 
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.OrderBookDetailPriceStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderBook;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderBookDetailPrice;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;

/**
 * @author torresl@cboe.com
 */
class OrderBookDetailPriceImpl implements OrderBookDetailPrice
{
    protected OrderBook[] orderInfo;
    protected Price price;
    protected OrderBookDetailPriceStruct orderBookDetailPriceStruct;
    public OrderBookDetailPriceImpl(OrderBookDetailPriceStruct orderBookDetailPriceStruct)
    {
        this.orderBookDetailPriceStruct = orderBookDetailPriceStruct;
        initialize();
    }

    private void initialize()
    {
        this.price = DisplayPriceFactory.create(orderBookDetailPriceStruct.price);
        orderInfo = new OrderBook[orderBookDetailPriceStruct.orderInfo.length];
        for (int i = 0; i < orderBookDetailPriceStruct.orderInfo.length; i++)
        {
            orderInfo[i] = OrderBookFactory.createOrderBook(orderBookDetailPriceStruct.orderInfo[i]);
        }
    }

    public OrderBook[] getOrderInfo()
    {
        return orderInfo;
    }

    public Price getPrice()
    {
        return price;
    }

    public OrderBookDetailPriceStruct toStruct()
    {
        return orderBookDetailPriceStruct;
    }
}
