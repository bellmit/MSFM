//
// -----------------------------------------------------------------------------------
// Source file: MarketDataCache.java
//
// PACKAGE: com.cboe.interfaces.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api.marketDataCache;

public interface MarketDataCache
{
    /**
     * @param classKey
     * @return true if the cache is subscribed for market data for the classKey
     */
    boolean isSubscribedForClass(int classKey);

    /**
     * Instructs the cache to publish its currently cached market data for the classKey.
     * @param classKey
     */
    void publishMarketDataSnapshot(int classKey);

    /**
     * Subscribes the cache for market data for the classKey.
     * @param classKey
     */
    void subscribeMarketData(int classKey);

    /**
     * Unsubscribes the cache for market data for the classKey.
     * @param classKey
     */
    void unsubscribeMarketData(int classKey);
}
