package com.cboe.internalPresentation.productStateEventHistory.cache;
// -----------------------------------------------------------------------------------
// Source file: PSEventHistoryCache
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 13, 2006 1:14:29 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.internalPresentation.productStateEventHistory.ProductStateEventHistory;

public class PSEventHistoryCache
{
    private static PSEventHistoryCache cacheInstance;

    private List<ProductStateEventHistory> cache;
    private Set<PSEventHistoryCacheEventListener> listeners;
    private Lock cacheLock;

    protected Comparator<ProductStateEventHistory> comparator;

    private PSEventHistoryCache()
    {
        cacheLock = new ReentrantLock(true); // use fairness policy for threads
        cache     = new ArrayList<ProductStateEventHistory>(100);
        listeners = new CopyOnWriteArraySet<PSEventHistoryCacheEventListener>();
    }

    public static synchronized PSEventHistoryCache getInstance()
    {
        if (cacheInstance == null )
        {
            cacheInstance = new PSEventHistoryCache();
        }

        return cacheInstance;
    }

    public void add(ProductStateEventHistory psce)
    {
        cacheLock.lock();
        try
        {
            cache.add(psce);
        }
        finally
        {
            cacheLock.unlock();
        }

        fireEvent(new PSEventHistoryCacheEvent(this, PSEventHistoryCacheEvent.EventType.DATA_CHANGE));
    }

    public ProductStateEventHistory[] getAllEvents()
    {
        cacheLock.lock();
        try
        {
            Collections.sort(cache, getComparator());
            return cache.toArray(new ProductStateEventHistory[0]);
        }
        finally
        {
            cacheLock.unlock();
        }
    }

    public void clear()
    {
        cacheLock.lock();
        try
        {
            cache.clear();
        }
        finally
        {
            cacheLock.unlock();
        }
    }

    public void addPSEventHistoryCacheEventListener(PSEventHistoryCacheEventListener l)
    {
        listeners.add(l);
    }

    public void PSEventHistoryCacheEventListener(PSEventHistoryCacheEventListener l)
    {
        listeners.remove(l);
    }

    protected void fireEvent(PSEventHistoryCacheEvent e)
    {
        for (PSEventHistoryCacheEventListener l: listeners)
        {
            l.cacheUpdate(e);
        }
    }

    protected Comparator<ProductStateEventHistory> getComparator()
    {
        if (comparator == null)
        {
            comparator = new PSCEventComparator();
        }

        return comparator;
    }

    protected class PSCEventComparator implements Comparator<ProductStateEventHistory>
    {
        @SuppressWarnings("unchecked")
        public int compare(ProductStateEventHistory a, ProductStateEventHistory b)
        {
            DateTime d1 = a.getDateTime();
            DateTime d2 = b.getDateTime();

            // Suppress Warning Here(unchecked), DateTime from Domain Jar(can't change now) 
            int result = d1.compareTo(d2);

            if (result == 1)
            {
                result = -1;
            }
            else if (result == -1)
            {
                result = 1;
            }
            else if (result == 0) //equal times, now sort by Status
            {
                result = a.getStatus().compareTo(b.getStatus());
            }

            return result;
        }
    }
}
