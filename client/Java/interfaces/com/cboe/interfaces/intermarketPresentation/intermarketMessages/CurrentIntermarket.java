//
// -----------------------------------------------------------------------------------
// Source file: CurrentIntermarket.java
//
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketStruct;

import com.cboe.interfaces.presentation.product.ProductKeys;

public interface CurrentIntermarket
{
    /**
     * Gets the underlying struct
     * @return CurrentIntermarketStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public CurrentIntermarketStruct getStruct();

    public ProductKeys getProductKeys();
    public CurrentIntermarketBest[] getCurrentIntermarketBest();
}