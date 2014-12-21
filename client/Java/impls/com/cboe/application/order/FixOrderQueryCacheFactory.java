package com.cboe.application.order;

import java.util.HashMap;
import java.util.Map;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class FixOrderQueryCacheFactory
{
    private static Map<String, FixOrderQueryCache> userIdCacheMap = new HashMap<String, FixOrderQueryCache>();
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static final Lock rLock = rwLock.readLock();
    private static final Lock wLock = rwLock.writeLock();

    public FixOrderQueryCacheFactory()
    {
        super();
    }

    public static FixOrderQueryCache create(String userId)
    {
        return find(userId);
    }

    public static FixOrderQueryCache find(String userId)
    {
        boolean rLockReleased = false;
        rLock.lock();
        try
        {
            FixOrderQueryCache orderQueryCache = userIdCacheMap.get(userId);
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
                        orderQueryCache = new FixOrderQueryCache(userId);
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

    public static FixOrderQueryCache find(String userId, Hashtable cleanupTable) {
        boolean rLockReleased = false;
        rLock.lock();
        try {
            FixOrderQueryCache orderQueryCache = userIdCacheMap.get(userId);
            if (orderQueryCache == null) {
                rLock.unlock();
                rLockReleased = true;
                wLock.lock();
                try {
                    orderQueryCache = userIdCacheMap.get(userId);
                    if (orderQueryCache == null) {
                        orderQueryCache = new FixOrderQueryCache(userId, cleanupTable);
                        userIdCacheMap.put(userId, orderQueryCache);
                    }
                }
                finally {
                    wLock.unlock();
                }
            } else {
                orderQueryCache.setCleanupTable(cleanupTable);
            }
            return orderQueryCache;
        }
        finally {
            if (!rLockReleased) {
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
            FixOrderQueryCache orderQueryCache = userIdCacheMap.get(userId);
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
