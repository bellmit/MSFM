//
// -----------------------------------------------------------------------------------
// Source file: BookDepthImpl.java
//
// PACKAGE: com.cboe.internalPresentation.bookDepth
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.bookDepth;

import org.omg.CORBA.UserException;

import com.cboe.idl.marketData.BookDepthDetailStruct;

import com.cboe.interfaces.presentation.bookDepth.BookDepth;
import com.cboe.interfaces.presentation.bookDepth.OrderBookPrice;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

/*
 * Implements the contract that provides the entire book for a product
 */
public class BookDepthImpl extends AbstractBusinessModel implements BookDepth
{
    protected BookDepthDetailStruct struct;
    protected SessionProduct product;
    protected OrderBookPrice[] buyPrices;
    protected OrderBookPrice[] sellPrices;

    /**
     * Default constructor
     * @param struct to represent
     */
    public BookDepthImpl(BookDepthDetailStruct struct)
    {
        this.struct = struct;

        buyPrices = new OrderBookPriceImpl[struct.buySidePrices.length];
        for(int i = 0; i < struct.buySidePrices.length; i++)
        {
            buyPrices[i] = new OrderBookPriceImpl(struct.buySidePrices[i]);
        }

        sellPrices = new OrderBookPriceImpl[struct.sellSidePrices.length];
        for(int i = 0; i < struct.sellSidePrices.length; i++)
        {
            sellPrices[i] = new OrderBookPriceImpl(struct.sellSidePrices[i]);
        }

        try
        {
            product = APIHome.findProductQueryAPI().getProductByKeyForSession(struct.sessionName, struct.productKeys.productKey);
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Product could not be obtained for the BookDepth");
        }
    }

    /**
     * Gets the buy side of the book for this product
     * @return OrderBookPrice sequence where each element represents a different price for buys
     */
    public OrderBookPrice[] getBuySide()
    {
        return buyPrices;
    }

    /**
     * Gets the sell side of the book for this product
     * @return OrderBookPrice sequence where each element represents a different price for sells
     */
    public OrderBookPrice[] getSellSide()
    {
        return sellPrices;
    }

    /**
     * Gets the SessionProduct that this book is for
     * @return SessionProduct this book represents
     */
    public SessionProduct getSessionProduct()
    {
        return product;
    }

    /**
     * Gets the transaction sequence number
     */
    public int getTransactionSequenceNumber()
    {
        return 0;
    }

    /**
     * Determines if all prices are included
     * @return True if all prices are included, false if this BookDepth only represents a partial view.
     */
    public boolean isAllPricesIncluded()
    {
        return true;
    }
}
