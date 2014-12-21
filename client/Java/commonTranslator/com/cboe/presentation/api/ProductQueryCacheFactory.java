package com.cboe.presentation.api;

public class ProductQueryCacheFactory {
    private static ProductQueryOrderedCacheProxy productCache = null;

    public ProductQueryCacheFactory () {
    }

    public static ProductQueryOrderedCacheProxy find() {
        if (productCache == null) {
            productCache = new ProductQueryOrderedCacheProxy();
        }
        return productCache;
    }
}