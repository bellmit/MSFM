//
// -----------------------------------------------------------------------------------
// Source file: CurrentIntermarketImpl.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.CurrentIntermarket;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.CurrentIntermarketBest;
import com.cboe.interfaces.presentation.product.ProductKeys;

import com.cboe.presentation.product.ProductKeysImpl;

class CurrentIntermarketImpl implements CurrentIntermarket
{
    private CurrentIntermarketStruct    currentIntermarketStruct;
    private CurrentIntermarketBest[]    currentIntermarketBests;
    private ProductKeys                 productKeys;
    public CurrentIntermarketImpl(CurrentIntermarketStruct currentIntermarketStruct)
    {
        this.currentIntermarketStruct = currentIntermarketStruct;
        initialize();
    }

    private void initialize()
    {
        productKeys = new ProductKeysImpl(currentIntermarketStruct.productKeys);
        currentIntermarketBests = new CurrentIntermarketBest[currentIntermarketStruct.otherMarketsBest.length];
        for( int i = 0; i < currentIntermarketStruct.otherMarketsBest.length; i++ )
        {
            currentIntermarketBests[i] = new CurrentIntermarketBestImpl(currentIntermarketStruct.otherMarketsBest[i]);
        }
    }

    public ProductKeys getProductKeys()
    {
        return productKeys;
    }

    public CurrentIntermarketBest[] getCurrentIntermarketBest()
    {
        return currentIntermarketBests;
    }

    /**
     * Gets the underlying struct
     * @return CurrentIntermarketStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public CurrentIntermarketStruct getStruct()
    {
        return currentIntermarketStruct;
    }
}
