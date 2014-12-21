//
// ------------------------------------------------------------------------
// FILE: OrderBookDetailPriceFactory.java
// 
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderBookDetailPrice;
import com.cboe.idl.cmiIntermarketMessages.OrderBookDetailPriceStruct;

public class OrderBookDetailPriceFactory
{
    public static OrderBookDetailPrice createOrderBookDetailPrice(OrderBookDetailPriceStruct orderBookDetailPriceStruct)
    {
        return new OrderBookDetailPriceImpl(orderBookDetailPriceStruct);
    }
}