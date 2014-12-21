//
// -----------------------------------------------------------------------------------
// Source file: CurrentMarketCacheElement.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;

/**
 * CacheElement for a currentMarket product V4.
 * This CacheElement is being stored inside the CurrentMarketProductCache.
 * This class is Thread safe.
 * 
 * @author Eric Maheo
 *
 */
class CurrentMarketCacheElement extends AbstractCacheElement<CurrentMarketV4ProductContainer>
{

    /**
     * Create a CurrentMarketCacheElement.
     */
    public CurrentMarketCacheElement(final CurrentMarketV4ProductContainer structProduct)
    {
        super(structProduct.getProductClassKey(), structProduct.getProductKey());
    }

    /**
     * {@inheritDoc}.
     * 
     */
    @Override
    public boolean equals(Object productCacheElement)
    {
        if (productCacheElement == null){
            return false;
        }
        if ((productCacheElement instanceof CurrentMarketCacheElement) == false){
            return false;
        }
        CurrentMarketCacheElement casted = (CurrentMarketCacheElement) productCacheElement;
        if (casted.getProductKey() == getProductKey()){
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected CurrentMarketV4ProductContainer[] initTemplateArray()
    {
        return new CurrentMarketV4ProductContainer[0];
    }

}
