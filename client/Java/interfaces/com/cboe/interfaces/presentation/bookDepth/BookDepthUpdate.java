//
// -----------------------------------------------------------------------------------
// Source file: BookDepthUpdate.java
//
// PACKAGE: com.cboe.interfaces.presentation.bookDepth
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.bookDepth;

import com.cboe.interfaces.presentation.product.SessionProduct;

/*
 * Provides a contract that provides the entire set of book updates for a product
 */
public interface BookDepthUpdate
{
    /**
     * Gets the SessionProduct that these book updates are for
     * @return SessionProduct that these book updates are for
     */
    public SessionProduct getSessionProduct();

    /**
     * Gets the buy side changes of the book for this product
     * @return OrderBookPrice sequence where each element represents an update for buys
     */
    public BookDepthUpdatePrice[] getBuySideUpdates();

    /**
     * Gets the sell side changes of the book for this product
     * @return OrderBookPrice sequence where each element represents an update for buys
     */
    public BookDepthUpdatePrice[] getSellSideUpdates();

    /**
     * Gets the sequence number
     */
    public int getSequenceNumber();
}
