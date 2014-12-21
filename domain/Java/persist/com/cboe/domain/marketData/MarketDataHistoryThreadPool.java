/*
 * Created on Apr 20, 2005
 *
 * light weight object pool for MDH.
 * 
 * Workings of MDH pool:
 * 1. Market data processing thread dequeues objects from its work queue.
 * 2. Market data history home acquires object from the MDH pool; fills it.
 * 3. jgrinder code uses the filled object to create entries in the database.
 * 4. The objects are released back to the pool.
 * We create one pool per market data processing thread as it's only this thread that acquires/releases
 * from/to the pool and therefore there isn't any contention on the pool.
 */
package com.cboe.domain.marketData;

/**
 * @author singh
 *
 */
public class MarketDataHistoryThreadPool
{
    private static ThreadLocal threadLocal = new ThreadLocal ();
    
    private static final int DEFAULT_POOL_SIZE = 50;
    
    public static void init (int size)
    {
        MarketDataHistoryObjectPool pool = (MarketDataHistoryObjectPool) threadLocal.get ();
        
        if (pool == null)
        {
            pool = new MarketDataHistoryObjectPool ();
            
            pool.init(size);
            
            threadLocal.set (pool);
        }
    }
    
    public static void releaseAll ()
    {
        MarketDataHistoryObjectPool pool = (MarketDataHistoryObjectPool) threadLocal.get ();
        
        pool.releaseAll();
    }
    
    public static MarketDataHistoryEntryImpl acquire ()
    {
        MarketDataHistoryObjectPool pool = (MarketDataHistoryObjectPool) threadLocal.get ();
                
        return pool != null ? pool.acquire() : MarketDataHistoryObjectPool.create ();
    }
    
}
