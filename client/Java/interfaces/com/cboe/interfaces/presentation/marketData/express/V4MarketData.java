//
// -----------------------------------------------------------------------------------
// Source file: V4MarketData.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData.express;

import com.cboe.interfaces.presentation.product.ProductContainer;

public interface V4MarketData extends ProductContainer
{
    /**
     * Returns a String to uniquely identify this V4MarketData
     */
    public String getIdentifierString();

    public void setMessageSequenceNumber(int msgSeqNum);

    /**
     * Returns the message sequence number received in the original message from the server
     */
    public int getMessageSequenceNumber();

    public int getProductKey();

    public int getProductClassKey();

    public String getExchange();
}