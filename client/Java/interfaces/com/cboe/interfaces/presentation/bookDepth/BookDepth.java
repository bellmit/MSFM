//
// -----------------------------------------------------------------------------------
// Source file: BookDepth.java
//
// PACKAGE: com.cboe.interfaces.presentation.bookDepth
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.bookDepth;

import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/*
 * Provides a contract that provides the entire book for a product
 */
public interface BookDepth extends BusinessModel
{
    /**
     * Gets the SessionProduct that this book is for
     * @return SessionProduct this book represents
     */
    public SessionProduct getSessionProduct();

    /**
     * Gets the buy side of the book for this product
     * @return OrderBookPrice sequence where each element represents a different price for buys
     */
    public OrderBookPrice[] getBuySide();

    /**
     * Gets the sell side of the book for this product
     * @return OrderBookPrice sequence where each element represents a different price for sells
     */
    public OrderBookPrice[] getSellSide();

    /**
     * Determines if all prices are included
     * @return True if all prices are included, false if this BookDepth only represents a partial view.
     */
    public boolean isAllPricesIncluded();

    /**
     * Gets the transaction sequence number
     */
    public int getTransactionSequenceNumber();
}
