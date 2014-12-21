//
// -----------------------------------------------------------------------------------
// Source file: UserMarketDataCacheFactory.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import java.util.Hashtable;

import com.cboe.interfaces.presentation.api.TraderAPI;
import com.cboe.interfaces.presentation.api.ProductQueryAPI;
import com.cboe.interfaces.presentation.api.MarketQueryV3API;

public class UserMarketDataCacheFactory
{
    private static Hashtable sessions;

    /**
     * MarketDataManagerFactory constructor comment.
     */
    public UserMarketDataCacheFactory()
    {
        super();
    }

    private static Hashtable getSessions()
    {
        if (sessions == null)
        {
            sessions = new Hashtable();
        }
        return sessions;
    }

    /**
      * This method was created in VisualAge.
      */
    synchronized public static UserMarketDataCacheProxy find(String sessionName, TraderAPI delegate)
    {
        return find(sessionName, delegate, delegate);
    }

    synchronized public static UserMarketDataCacheProxy find(String sessionName, ProductQueryAPI productQueryDelegate, MarketQueryV3API marketQueryDelegate)
    {
        UserMarketDataCacheProxy marketDataCache = (UserMarketDataCacheProxy) getSessions().get(sessionName);
        if(marketDataCache == null)
        {
            // Configuration service will eventually supply the initial hash table size
            marketDataCache = new UserMarketDataCacheProxy(sessionName, productQueryDelegate, marketQueryDelegate);
            getSessions().put(sessionName, marketDataCache);
        }
        return marketDataCache;
    }
}
