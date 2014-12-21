//
// -----------------------------------------------------------------------------------
// Source file: NBBOCacheElement.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import com.cboe.idl.cmiMarketData.NBBOStructV4;

/**
 * Cache element for a NBBO product V4.
 * This cache element is stored in the NBBOProductCache.
 * The NBBOCacheElement doesn't follow the same pattern as the the current market,
 * the ticker... which all have a struct per market exchange by product.
 * The NBBO has only 1 struct per product. So the generic interface doesn't fit well 
 * with the NBBO.
 * 
 * This class is Thread safe.
 * 
 * @author Eric Maheo
 *
 */

class NBBOCacheElement implements ProductCacheElement<NBBOStructV4>
{

    /** Holds the classKey value for this product. */
    private final int classKey;
    /** Holds the productKey value for this product. */
    private final int productKey;
    /** Holds the NBBOStructV4 structure for this product. */
    private final NBBOStructV4 struct;
    
    /**
     * Create a Cache element for a NBBO.
     * @param struct
     */
    public NBBOCacheElement(NBBOStructV4 struct){
        classKey = struct.classKey;
        productKey = struct.productKey;
        this.struct = struct;
    }

    /**
     * {@inheritDoc}.
     * 
     * In the case of the NBBO there is only 1 maket exchange for each element. 
     * So the NBBOCacheElement doesn't update/add any element.
     * 
     */
    @Override
    public NBBOStructV4 addProductForExchangeMarket(String exchange, NBBOStructV4 struct)
    {
        return this.struct; //just return the existing one. 
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getClassKey()
    {
        return classKey;
    }

    /**
     * {@inheritDoc}.
     * Return an Array of only 1 element.
     */
    @Override
    public NBBOStructV4[] getProductForAllExchangeMarket()
    {
        return new NBBOStructV4[]{struct};
    }
    
    /**
     * {@inheritDoc}.
     * The exchange market isn't used and therefor it will return 
     * the NBBO that it has for this product.
     */
    @Override
    public NBBOStructV4 getProductForExchangeMarket(String exchange)
    {
        return struct;
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public int getProductKey()
    {
        return productKey;
    }
}
