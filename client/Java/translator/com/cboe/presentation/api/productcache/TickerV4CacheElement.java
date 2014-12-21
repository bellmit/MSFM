//
// -----------------------------------------------------------------------------------
// Source file: TickerV4CacheElement.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;


import com.cboe.interfaces.presentation.marketData.express.TickerV4;

/**
 * 
 * CacheElement for a Ticker product V4.
 * This CacheElement is being stored inside the TickerProductCache.
 * This class is Thread safe.
 * 
 * @author Eric Maheo
 *
 */
class TickerV4CacheElement extends AbstractCacheElement<TickerV4>
{
    
    
    /**
     * Create a TickerV4CacheElement.
     * @param structProduct
     */
    public TickerV4CacheElement(final TickerV4 structProduct)
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
        if ((productCacheElement instanceof TickerV4CacheElement) == false){
            return false;
        }
        TickerV4CacheElement casted = (TickerV4CacheElement) productCacheElement;
        if (casted.getProductKey() == getProductKey()){
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    protected TickerV4[] initTemplateArray()
    {
        return new TickerV4[0];
    }
}
