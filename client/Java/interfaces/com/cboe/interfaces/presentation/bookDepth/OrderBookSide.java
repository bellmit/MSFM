//
// -----------------------------------------------------------------------------------
// Source file: OrderBookSide.java
//
// PACKAGE: com.cboe.interfaces.presentation.bookDepth
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.bookDepth;

/*
 * Provides a contract that provides a side of the book
 */
public interface OrderBookSide
{
    /**
     * Gets the side of the book this is for
     * @return String as designated as com.cboe.idl.cmiConstants.Sides
     */
    public String getSide();

    /**
     * Gets the order book for this side
     * @param OrderBookPrice sequence where each element represents a different price
     */
    public OrderBookPrice[] getOrderBook();
}
