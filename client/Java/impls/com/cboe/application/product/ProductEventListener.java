package com.cboe.application.product;

import com.cboe.application.cache.CacheFactory;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.product.*;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ProductEventListener extends ProductEventBaseListener
{
    public ProductEventListener()
    {
        super();
    }

    protected void dispatchClass(ProductClassStruct productClassStruct)
    {
        // Let the caches know about it
        if (Log.isDebugOn())
        {
            Log.debug("ProductEventListener.dispatchClass/updateCache = "+productClassStruct.toString());
        }
        CacheFactory.updateClassCache(productClassStruct.info);
        CacheFactory.loadProductCache(productClassStruct.products);
        super.dispatchClass(productClassStruct);
    }

    protected void dispatchProduct(ProductStruct productStruct)
    {
        // Let the cache know about it
        if (Log.isDebugOn())
        {
            Log.debug("ProductEventListener.dispatchProduct/updateCache = "+productStruct.toString());
        }
        CacheFactory.updateProductCache(productStruct);
        super.dispatchProduct(productStruct);
    }

    protected void dispatchProductStrategy(StrategyStruct strategyStruct)
    {
        // Let the caches know about it
        if (Log.isDebugOn())
        {
            Log.debug("ProductEventListener.dispatchProductStrategy/updateCache = "+strategyStruct.toString());
        }
        CacheFactory.updateStrategyCache(strategyStruct);
        super.dispatchProductStrategy(strategyStruct);
    }
}