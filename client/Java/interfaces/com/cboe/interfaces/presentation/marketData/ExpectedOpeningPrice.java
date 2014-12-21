//
// -----------------------------------------------------------------------------------
// Source file: ExpectedOpeningPrice.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;

import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.domain.Price;

/**
 * Immutable wrapper for the ExpectedOpeningPriceStruct
 */
public interface ExpectedOpeningPrice
{
    /**
     * Updates the struct for this wrapper.  The new struct must be for the same
     * productKey as the original EOP struct for this wrapper.
     * @param struct
     */
    public void updateEOPStruct(ExpectedOpeningPriceStruct struct);
    public SessionProduct getProduct();
    public int getProductKey();
    public short getEOPType();
    public Price getExpectedOpeningPrice();
    public int getImbalanceQuantity();
    public boolean isLegalMarket();
    public ExpectedOpeningPriceStruct getExpectedOpeningPriceStruct();
}