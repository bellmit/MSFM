//
// -----------------------------------------------------------------------------------
// Source file: AuctionMinMaxOrderSize.java
//
// PACKAGE: com.cboe.interfaces.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.tradingProperty;

/**
 * Defines a contract for a TradingProperty for the Minimum and Maximum Order Size for an Auction.
 */
public interface AuctionMinMaxOrderSize extends TradingProperty
{
    /**
     * Gets the auction type
     */
    short getAuctionType();

    /**
     * Sets the auction type
     */
    void setAuctionType(short auctionType);

    /**
     * Gets the maximum order size for this auction type
     */
    int getMaxOrderSize();

    /**
     * Sets the maximum order size for this auction type
     */
    void setMaxOrderSize(int maxOrderSize);

    /**
     * Gets the minimum order size for this auction type
     */
    int getMinOrderSize();

    /**
     * Sets the minimum order size for this auction type
     */
    void setMinOrderSize(int minOrderSize);
}