//
// -----------------------------------------------------------------------------------
// Source file: LastSaleCacheElement.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;


import com.cboe.interfaces.presentation.marketData.express.LastSaleV4;

/**
 * CacheElement for a LastSale product V4.
 * This CacheElement is being stored inside the LastSaleProductCache.
 * This class is Thread safe.
 * 
 * @author Eric Maheo
 *
 */
class LastSaleCacheElement extends AbstractCacheElement<LastSaleV4>
{

    /**
     * Create a LastSaleCacheElement object.
     * @param structProduct
     */
    public LastSaleCacheElement(final LastSaleV4 structProduct)
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
        if ((productCacheElement instanceof LastSaleV4) == false){
            return false;
        }
        LastSaleV4 casted = (LastSaleV4) productCacheElement;
        if (casted.getProductKey() == getProductKey()){
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected LastSaleV4[] initTemplateArray()
    {
        return new LastSaleV4[0];
    }

}
