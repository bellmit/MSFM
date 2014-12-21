//
// -----------------------------------------------------------------------------------
// Source file: ProductCacheElement.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

/**
 * Class that is being used to store each product V4 into the 
 * ProductCache. 
 * 
 * @author Eric Maheo
 *
 */
interface ProductCacheElement<T>
{
    /**
     * Gets the productKey of the product stored in cache.
     * @return the productKey for this productCacheElement.
     */
    public int getProductKey();
    /**
     * Gets the classKey of the product stored in cache.
     * @return the classKey for this productCacheElement.
     */
    public int getClassKey();
    /**
     * Gets the struct of type generic T for a particular exchange market.
     * @return
     */
    public T getProductForExchangeMarket(String exchange);
    /**
     * Gets all struct of type generic T for all exchange markets.
     * @return
     */
    public T[] getProductForAllExchangeMarket();
    /**
     * Add a product for an exchange market. If the product doesn't exist for the 
     * specified exchange market then it will be added. 
     * If there exists a product for this market exchange then it will be updated with the 
     * new one and it will return the old one.
     * 
     * @param exchange
     * @param struct
     * @return the old struct if presents otherwize it will return null.
     */
    public T addProductForExchangeMarket(String exchange, T struct);
    /**
     * Hashcode for this ProductCacheElement.
     * 
     * @return the hashCode.
     */
    public int hashCode();
    /**
     * Test is 2 ProductCacheElement are equals.
     * The uniqueness of a ProductCacheElement is defined by its productKey value.
     * 
     * @param productCacheElement to test.
     * @return true if equals.
     */
    public boolean equals(Object productCacheElement);
}

