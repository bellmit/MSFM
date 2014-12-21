//
// ------------------------------------------------------------------------
// FILE: OrderBookFactory.java
// 
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderBook;
import com.cboe.idl.cmiIntermarketMessages.OrderBookStruct;

public class OrderBookFactory
{
    public static OrderBook createOrderBook(OrderBookStruct orderBookStruct)
    {
        return new OrderBookImpl(orderBookStruct);
    }
    /**
     * This method is provided for use only when constructing table rows.  Nothing gets initialized
     * when calling the empty constructor, so methods in OrderBook will return null.
     * @return OrderBook
     */
    public static OrderBook createEmptyOrderBook()
    {
        return new OrderBookImpl();
    }

}