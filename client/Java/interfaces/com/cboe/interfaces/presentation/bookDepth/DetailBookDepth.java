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
public interface DetailBookDepth extends BookDepth
{
    /**
     * Gets the detail buy side of the book for this product
     * @return DetailOrderBookPrice sequence where each element represents a different price for buys
     */
    public DetailOrderBookPrice[] getDetailBuySide();

    /**
     * Gets the detail sell side of the book for this product
     * @return DetailOrderBookPrice sequence where each element represents a different price for sells
     */
    public DetailOrderBookPrice[] getDetailSellSide();
}
