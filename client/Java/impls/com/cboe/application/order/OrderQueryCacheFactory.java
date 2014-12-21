package com.cboe.application.order;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class OrderQueryCacheFactory
{
    private static Map<String, OrderQueryCache> userIdCacheMap = new HashMap<String, OrderQueryCache>();
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static final Lock rLock = rwLock.readLock();
    private static final Lock wLock = rwLock.writeLock();

    public OrderQueryCacheFactory()
    {
        super();
    }

    public static OrderQueryCache create(String userId)
    {
        return find(userId);
    }

    public static OrderQueryCache find(String userId)
    {
        boolean rLockReleased = false;
        rLock.lock();
        try
        {
            OrderQueryCache orderQueryCache = userIdCacheMap.get(userId);
            if (orderQueryCache == null)
            {
                rLock.unlock();
                rLockReleased = true;
                wLock.lock();
                try
                {
                    orderQueryCache = userIdCacheMap.get(userId);
                    if (orderQueryCache == null)
                    {
                        orderQueryCache = new OrderQueryCache(userId);
                        userIdCacheMap.put(userId, orderQueryCache);
                    }
                }
                finally
                {
                    wLock.unlock();
                }
            }
            return orderQueryCache;
        }
        finally
        {
            if (!rLockReleased)
            {
                rLock.unlock();
            }
        }
    }

    public static void remove ( String userId )
    {
        boolean rLockReleased = false;
        rLock.lock();
        try
        {
            OrderQueryCache orderQueryCache = userIdCacheMap.get(userId);
            if (orderQueryCache != null)
            {
                rLock.unlock();
                rLockReleased = true;
                wLock.lock();
                try
                {
                    orderQueryCache = userIdCacheMap.get(userId);
                    if (orderQueryCache != null)
                    {
                        orderQueryCache.cacheCleanUp();
                        userIdCacheMap.remove(userId);
                    }
                }
                finally
                {
                    wLock.unlock();
                }
            }
        }
        finally
        {
            if (!rLockReleased)
            {
                rLock.unlock();
            }

        }
    }
}
