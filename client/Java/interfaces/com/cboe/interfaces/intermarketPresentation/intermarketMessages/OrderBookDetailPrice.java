//
// ------------------------------------------------------------------------
// FILE: OrderBookDetailPrice.java
// 
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.OrderBookDetailPriceStruct;
import com.cboe.interfaces.domain.Price;

public interface OrderBookDetailPrice
{
    OrderBook[] getOrderInfo();
    Price getPrice();

    OrderBookDetailPriceStruct toStruct();

}
