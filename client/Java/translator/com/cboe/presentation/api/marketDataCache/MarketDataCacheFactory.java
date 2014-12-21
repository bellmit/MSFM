//
// -----------------------------------------------------------------------------------
// Source file: MarketDataCacheFactory.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache;

import java.util.*;
import com.cboe.interfaces.presentation.api.marketDataCache.CurrentMarketV3Cache;
import com.cboe.interfaces.presentation.api.marketDataCache.CurrentMarketV4Cache;
import com.cboe.interfaces.presentation.api.marketDataCache.NBBOV2Cache;
import com.cboe.interfaces.presentation.api.marketDataCache.RecapV2Cache;
import com.cboe.interfaces.presentation.api.marketDataCache.LastSaleV4Cache;
import com.cboe.interfaces.presentation.api.marketDataCache.RecapV4Cache;
import com.cboe.interfaces.presentation.api.marketDataCache.TickerV4Cache;

public class MarketDataCacheFactory
{
    private static final Map<String, CurrentMarketV3Cache> cmV3CacheMap = new HashMap<String, CurrentMarketV3Cache>(10);
    private static final Map<String, NBBOV2Cache> nbboV2CacheMap = new HashMap<String, NBBOV2Cache>(10);
    private static final Map<String, RecapV2Cache> recapV2CacheMap = new HashMap<String, RecapV2Cache>(10);

    //V4 caches aren't session-based, so there's only one instance of each type
    private static final CurrentMarketV4Cache cmV4Cache = new CurrentMarketV4CacheImpl();
    private static final LastSaleV4Cache lastSaleV4Cache = new LastSaleV4CacheImpl();
    private static final RecapV4Cache recapV4Cache = new RecapV4CacheImpl();
    private static final TickerV4Cache tickerV4Cache = new TickerV4CacheImpl();

    private MarketDataCacheFactory()
    {
    }

    public static CurrentMarketV3Cache findCurrentMarketV3Cache(String sessionName)
    {
        CurrentMarketV3Cache cmCache;
        synchronized(cmV3CacheMap)
        {
            cmCache = cmV3CacheMap.get(sessionName);
            if(cmCache == null)
            {
                cmCache = new CurrentMarketV3CacheImpl(sessionName);
                cmV3CacheMap.put(sessionName, cmCache);
            }
        }
        return cmCache;
    }

    public static NBBOV2Cache findNBBOV2Cache(String sessionName)
    {
        NBBOV2Cache nbboCache;
        synchronized(nbboV2CacheMap)
        {
            nbboCache = nbboV2CacheMap.get(sessionName);
            if(nbboCache == null)
            {
                nbboCache = new NBBOV2CacheImpl(sessionName);
                nbboV2CacheMap.put(sessionName, nbboCache);
            }
        }
        return nbboCache;
    }

    public static RecapV2Cache findRecapV2Cache(String sessionName)
    {
        RecapV2Cache recapCache;
        synchronized(recapV2CacheMap)
        {
            recapCache = recapV2CacheMap.get(sessionName);
            if(recapCache == null)
            {
                recapCache = new RecapV2CacheImpl(sessionName);
                recapV2CacheMap.put(sessionName, recapCache);
            }
        }
        return recapCache;
    }

    public static CurrentMarketV4Cache findCurrentMarketV4Cache()
    {
        return cmV4Cache;
    }

    public static LastSaleV4Cache findLastSaleV4Cache()
    {
        return lastSaleV4Cache;
    }

    public static RecapV4Cache findRecapV4Cache()
    {
        return recapV4Cache;
    }

    public static TickerV4Cache findTickerV4Cache()
    {
        return tickerV4Cache;
    }
}
