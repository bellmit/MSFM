//
// -----------------------------------------------------------------------------------
// Source file: RecapLastSaleV4ProductContainer.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData.express
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData.express;

import com.cboe.interfaces.presentation.product.ProductContainer;

public interface RecapLastSaleV4ProductContainer extends ProductContainer
{
    /**
     * Returns a String to uniquely identify this RecapLastSaleV4ProductContainer
     */
    public String getIdentifierString();
    public String getExchange();
    public int getProductKey();

    public LastSaleV4 getLastSaleV4();
    public RecapV4 getRecapV4();

    public void setLastSaleV4(LastSaleV4 lastSale);
    public void setRecapV4(RecapV4 recap);
}