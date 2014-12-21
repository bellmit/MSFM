package com.cboe.application.quote;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 
 * @author Gijo Joseph
 */
public class QuoteCacheFactory
{
    private static Map<String, QuoteCache> userIds = new HashMap<String, QuoteCache>();
    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static final Lock rLock = rwLock.readLock();
    private static final Lock wLock = rwLock.writeLock();

    public static QuoteCache create(String userId)
    {
        return find(userId);
    }

    /** Default implementation of find().  It will create the cache if none exists for the user.
     * 
     * @param userId
     * @return
     */ 
    public static QuoteCache find(String userId)
    {
        return find(userId, true);
    }
    
    /**
     * Implementation of find() that uses a boolean to determine if a new QuoteCache
     * should be created if there is none existing for the user.
     * 
     * @param userId
     * @param createIfNull
     * @return
     */ 
    public static QuoteCache find(String userId, boolean createIfNull)
    {
        boolean rLockReleased = false;
        QuoteCache quoteCache = null;
        rLock.lock();
        try
        {
	        quoteCache = userIds.get(userId);
	        if (createIfNull && quoteCache == null)
	        {
	            rLock.unlock();
	            rLockReleased = true;
	            wLock.lock();
	            try
	            {
	            	quoteCache = userIds.get(userId);
			        if (quoteCache == null)
			        {
			            quoteCache = new QuoteCache(userId);
			            userIds.put(userId, quoteCache);
			        }
	            }
	            finally
	            {
                    wLock.unlock();	            	
	            }
	        }
	        return quoteCache;        
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
        QuoteCache quoteCache = null;
        rLock.lock();
        try
        {
	        quoteCache = userIds.get(userId);
	        if (quoteCache != null)
	        {
	            rLock.unlock();
	            rLockReleased = true;
	            wLock.lock();
	            try
	            {
	            	quoteCache = userIds.get(userId);
			        if (quoteCache != null)
			        {
			            quoteCache.cacheCleanUp();
			            userIds.remove(userId);
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
