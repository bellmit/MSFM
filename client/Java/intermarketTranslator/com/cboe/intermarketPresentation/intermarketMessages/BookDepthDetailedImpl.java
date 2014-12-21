//
// ------------------------------------------------------------------------
// FILE: BookDepthDetailedImpl.java
// 
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.BookDepthDetailed;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderBookDetailPrice;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.presentation.product.ProductKeysFactory;

/**
 * @author torresl@cboe.com
 */
class BookDepthDetailedImpl implements BookDepthDetailed
{
    protected ProductKeys productKeys;
    protected String sessionName;
    protected int transactionSequenceNumber;
    protected OrderBookDetailPrice[] buyOrdersAtDifferentPrice;
    protected OrderBookDetailPrice[] sellOrdersAtDifferentPrice;
    protected BookDepthDetailedStruct bookDepthDetailedStruct;
    public BookDepthDetailedImpl(BookDepthDetailedStruct bookDepthDetailedStruct)
    {
        this.bookDepthDetailedStruct = bookDepthDetailedStruct;
        initialize();
    }

    private void initialize()
    {
        productKeys = ProductKeysFactory.createProductKeys(bookDepthDetailedStruct.productKeys);
        sessionName = bookDepthDetailedStruct.sessionName;
        transactionSequenceNumber = bookDepthDetailedStruct.transactionSequenceNumber;
        buyOrdersAtDifferentPrice = new OrderBookDetailPrice[bookDepthDetailedStruct.buyOrdersAtDifferentPrice.length];
        for (int i = 0; i < bookDepthDetailedStruct.buyOrdersAtDifferentPrice.length; i++)
        {
            buyOrdersAtDifferentPrice[i] = OrderBookDetailPriceFactory.createOrderBookDetailPrice(bookDepthDetailedStruct.buyOrdersAtDifferentPrice[i]);
        }
        sellOrdersAtDifferentPrice = new OrderBookDetailPrice[bookDepthDetailedStruct.sellOrdersAtDifferentPrice.length];
        for (int i = 0; i < bookDepthDetailedStruct.sellOrdersAtDifferentPrice.length; i++)
        {
            sellOrdersAtDifferentPrice[i] = OrderBookDetailPriceFactory.createOrderBookDetailPrice(bookDepthDetailedStruct.sellOrdersAtDifferentPrice[i]);
        }
    }

    public ProductKeys getProductKeys()
    {
        return productKeys;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public int getTransactionSequenceNumber()
    {
        return transactionSequenceNumber;
    }

    public OrderBookDetailPrice[] getBuyOrdersAtDifferentPrice()
    {
        return buyOrdersAtDifferentPrice;
    }

    public OrderBookDetailPrice[] getSellOrdersAtDifferentPrice()
    {
        return sellOrdersAtDifferentPrice;
    }

    public BookDepthDetailedStruct toStruct()
    {
        return bookDepthDetailedStruct;
    }
}
