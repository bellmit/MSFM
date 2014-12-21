//
// -----------------------------------------------------------------------------------
// Source file: ProductCache.java
//
// PACKAGE: com.cboe.interfaces.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.interfaces.presentation.api.productcache;

import com.cboe.util.event.EventChannelListener;

/**
 * ProductCache is a generic interface for all objects that are being 
 * cache in the productCache. 
 * 
 * @author Eric Maheo
 *
 * @param is the class dispatch by the consumer.
 */
public interface ProductCache<T>
{

    /**
     * Is the product with a productKey is currently subcribing to the IEC?
     * 
     * @param channelType
     * @param productKey
     * 
     * @return true if the cache is subscribed for product with the productKey and channelType.
     */
    boolean isSubscribedForProduct(int channelType, int productKey);

    /**
     * Subscribes the cache for product with the productKey to a channelType for a client.
     * 
     * @param channelType
     * @param productKey
     * @param client
     * 
     * @return the count of subscribtion for a product.
     */
    int subscribeProduct(int channelType, int productKey, EventChannelListener client);

    /**
     * Unsubscribes the cache for product with the productKey from a channelType for a client.
     * 
     * @param channelType
     * @param productKey
     * @param client
     * 
     * @return the count of subscribtion for a product.
     */
    int unsubscribeProduct(int channelType, int productKey, EventChannelListener client);
    
    /**
     * Add or update a product to the cache for a particular market exchange.
     * The market exchange information is in the struct istself.
     * 
     * @param productKey to add to the cache.
     * @param struct to add or update to the cache for a particular market exchange.
     */
    void addProductCache(int productKey, T struct);
    
    /**
     * Remove product from the cache based on its productKey.
     * 
     * @return all exchanges for this product.
     */
    T[] removeProductCache(int productKey);
    
    /**
     * Retreive product from the cache.
     * 
     * @return all exchanges for this product.
     */
    T[] getProductCache(int productKey);
    
    /**
     * Get the latest updates for a product key in the cache. 
     * @param productKey for latest updates.
     * @return the lastest updates.
     */
    T[] getLatestUpdates();
    
}
