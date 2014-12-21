//
// -----------------------------------------------------------------------------------
// Source file: SessionMarketDataCache.java
//
// PACKAGE: com.cboe.interfaces.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api.marketDataCache;

import java.util.*;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.SessionKeyWrapper;

/**
 * Extends the more generic MarketDataCache, making the cache session-based.
 *
 * The generic 'T' is the type of market data object received on the IEC (e.g.,
 * CurrentMarketProductContainer or NBBOStruct).
 */
public interface SessionMarketDataCache<T> extends MarketDataCache
{
    String getSessionName();

    T getMarketDataForProduct(int classKey, int productKey) throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException;

    /**
     * If there is no market data cached for the product and lazyInitCache is true, then the cache
     * will initialize for the product, and subscribe for the product's class, if it's not already
     * subscribed.
     */
    T getMarketDataForProduct(int classKey, int productKey, boolean lazyInitCache) throws SystemException, CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException;

    /**
     * Instruct the cache to maintain a list of products that have received updates since the last
     * time the MarketDataCacheClient called getUpdatedProductsList()
     */
    void maintainUpdatedProductsList(MarketDataCacheClient cacheUser);

    /**
     * Return a Set of Products that the MarketDataCache has received updates for.  A Set will be
     * maintained by the cache for each MarketDataCacheClient that has been been registered via
     * maintainUpdatedProductsList().  Each time a MarketDataCacheClient calls this method, the
     * cache will clear out the Set of updated Products for that client.
     * @param cacheUser that has registered with maintainUpdatedProductsList()
     * @return Set<SessionKeyWrapper> that this cache has received market data updates for since the
     *         last time this MarketDataCacheClient has called this method
     * @throws IllegalArgumentException if maintainUpdatedProductsList() hasn't been called for this
     * MarketDataCacheClient
     */
    Set<SessionKeyWrapper> getUpdatedProductsForClass(MarketDataCacheClient cacheUser,
                                                      SessionKeyWrapper classKeyContainer)
            throws IllegalArgumentException;
}
