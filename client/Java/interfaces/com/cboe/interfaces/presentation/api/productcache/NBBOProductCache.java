//
// -----------------------------------------------------------------------------------
// Source file: NBBOProductCache.java
//
// PACKAGE: com.cboe.interfaces.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api.productcache;

import com.cboe.util.event.EventChannelListener;


/**
 * Defines the API for the NBBOProductCache.
 * 
 * @author Eric Maheo
 * 
 * @param is the class dispatch by the consumer.
 */
public interface NBBOProductCache<T> extends ProductCache<T>, EventChannelListener
{

    /**
     * Instructs the cache to publish its current cached value for the product with the productKey.
     * @param productKey
     */
    void publishMarketDataSnapshot(int productKey);
    
}
