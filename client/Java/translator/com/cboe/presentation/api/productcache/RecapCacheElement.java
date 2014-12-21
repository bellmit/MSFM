//
// -----------------------------------------------------------------------------------
// Source file: RecapCacheElement.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import com.cboe.interfaces.presentation.marketData.express.RecapV4;

/**
 * CacheElement for a Recap product V4.
 * This CacheElement is being stored inside the RecapCacheElement.
 * This class is Thread safe.
 * 
 * @author Eric Maheo
 *
 */
class RecapCacheElement extends AbstractCacheElement<RecapV4>
{

    /**
     * Create a TickerV4CacheElement.
     * @param structProduct
     */
    public RecapCacheElement(final RecapV4 structProduct)
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
        if ((productCacheElement instanceof RecapCacheElement) == false){
            return false;
        }
        RecapCacheElement casted = (RecapCacheElement) productCacheElement;
        if (casted.getProductKey() == getProductKey()){
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    protected RecapV4[] initTemplateArray()
    {
        return new RecapV4[0];
    }
}
