//
// -----------------------------------------------------------------------------------
// Source file: BookDepthUpdateImpl.java
//
// PACKAGE: com.cboe.presentation.bookDepth
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.bookDepth;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiMarketData.BookDepthUpdateStruct;

import com.cboe.interfaces.presentation.bookDepth.BookDepthUpdate;
import com.cboe.interfaces.presentation.bookDepth.BookDepthUpdatePrice;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

/*
 * Implements the contract that provides the entire set of book updates for a product
 */
public class BookDepthUpdateImpl implements BookDepthUpdate
{
    protected BookDepthUpdateStruct struct;
    protected SessionProduct product;
    protected BookDepthUpdatePrice[] buyPrices;
    protected BookDepthUpdatePrice[] sellPrices;

    /**
     * Default constructor
     * @param struct to represent
     */
    public BookDepthUpdateImpl(BookDepthUpdateStruct struct)
    {
        this.struct = struct;

        buyPrices = new BookDepthUpdatePriceImpl[struct.buySideChanges.length];
        for(int i = 0; i < struct.buySideChanges.length; i++)
        {
            buyPrices[i] = new BookDepthUpdatePriceImpl(struct.buySideChanges[i]);
        }

        sellPrices = new BookDepthUpdatePriceImpl[struct.sellSideChanges.length];
        for(int i = 0; i < struct.sellSideChanges.length; i++)
        {
            sellPrices[i] = new BookDepthUpdatePriceImpl(struct.sellSideChanges[i]);
        }

        try
        {
            product = APIHome.findProductQueryAPI().getProductByKeyForSession(struct.sessionName, struct.productKeys.productKey);
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Product could not be obtained for the BookDepthUpdate");
        }
    }

    /**
     * Gets the SessionProduct that these book updates are for
     * @return SessionProduct that these book updates are for
     */
    public SessionProduct getSessionProduct()
    {
        return product;
    }

    /**
     * Gets the buy side changes of the book for this product
     * @return OrderBookPrice sequence where each element represents an update for buys
     */
    public BookDepthUpdatePrice[] getBuySideUpdates()
    {
        return buyPrices;
    }

    /**
     * Gets the sell side changes of the book for this product
     * @return OrderBookPrice sequence where each element represents an update for buys
     */
    public BookDepthUpdatePrice[] getSellSideUpdates()
    {
        return sellPrices;
    }

    /**
     * Gets the sequence number
     */
    public int getSequenceNumber()
    {
        return struct.sequenceNumber;
    }
}
