//
// -----------------------------------------------------------------------------------
// Source file: AbstractProductCache.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.*;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.event.EventChannelListener;
import com.cboe.interfaces.presentation.api.productcache.ProductCache;
import com.cboe.interfaces.presentation.marketData.express.V4MarketData;
import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;

/**
 * Abstract ProductCache that is being implemented all ProductCache concrete classes.
 * This class handles the [un]subscribtion and divers methods needed to store/retreive/invalidate 
 * product in cache.
 * 
 * T represents a generic structure used for the product.
 * E represents a generic cacheElement used to store in the cache. 
 * 
 * @author Eric Maheo
 *
 */
public abstract class AbstractProductCache<T, E extends ProductCacheElement<T>> 
        implements ProductCache<T>, EventChannelListener
{

    private final PolicyDispatcherManager pdm;
    private final Subscriber subscriber;
    
    /** Lock for atomic operation while subscribing. */
    private static final Lock lockSubscribe = new ReentrantLock();
    /** Value to initialize the cache size to {@value}. */
    private static final int INITIAL_CACHE_VALUE = 128;
    /** Cache that holds the CurrentMarketCacheElement indexed by the productKey of the product. */
    protected final ConcurrentHashMap<Integer,E> table;
    /** Set guarded by lockUpdates. */
    protected final Set<T> updates;
    /** Lock that guard the updates set. */
    protected final Lock lockUpdates = new ReentrantLock();

    
    /**
     * Returns the ChannelType constant for the events that will be published on the IEC
     * @return -1 if no events will be published by productKey, else returns a ChannelType constant
     */
    protected abstract int getChannelTypeForPublishByProductKey();

    
    public AbstractProductCache(){
        subscriber = Subscriber.getInstance();
        pdm = PolicyDispatcherManager.getInstance();
        table = new ConcurrentHashMap<Integer, E>(INITIAL_CACHE_VALUE);
        updates = new HashSet<T>();
    }
    
    @Override
    public boolean isSubscribedForProduct(int channelType, int productKey)
    {
        return subscriber.isSubscribedForProduct(channelType, productKey);
    }

    @Override
    public int subscribeProduct(int channelType, int productKey, EventChannelListener client)
    {
        lockSubscribe.lock();
        try {
            int count = subscriber.subscribeProduct(channelType, productKey, client);
            pdm.informSubscription(channelType, count);
            return count;
        }
        finally{
            lockSubscribe.unlock();
        }
    }

    @Override
    public int unsubscribeProduct(int channelType, int productKey, EventChannelListener client)
    {
        lockSubscribe.lock();
        try {
            int count = subscriber.unsubscribeProduct(channelType, productKey, client);
            pdm.informUnsubscription(channelType, count);
            if (count == 0){
                removeProductCache(productKey);
            }
            return count;
        }
        finally{
            lockSubscribe.unlock();
        }
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public T[] removeProductCache(int productKey){
        E element = table.remove(Integer.valueOf(productKey));
        return (element == null)? initTemplateArray():element.getProductForAllExchangeMarket();
    }
   
    /**
     * {@inheritDoc}. 
     */
    @Override
    public T[] getProductCache(int productKey){
        final E cache = table.get(productKey);
        if (cache == null){
            if(GUILoggerHome.find().isDebugOn() &&
                    GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
                  {
                     GUILoggerHome.find().debug(getLoggingPrefix()+".getProductCache()",
                                               GUILoggerBusinessProperty.MARKET_QUERY,
                                               "get product from cache for productKey=" + productKey);
                 }
            return initTemplateArray();
        }
        return cache.getProductForAllExchangeMarket();
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public T[] getLatestUpdates()
    {
        List<T> list = new ArrayList<T>(30);
        lockUpdates.lock();
        try { //operation needs to be done atomicly.
            for (T marketData : updates)
            {
                list.add(marketData);
            }
            updates.clear();
        }
        finally {
            lockUpdates.unlock();
        }
        return list.toArray(initTemplateArray());
    }

    /**
     * Convenience method to publish an event on the IEC.
     * @param source
     * @param channelType
     * @param key
     * @param data
     */
    protected void dispatchEvent(int channelType, Object key, T data)
    {
        pdm.dispatchEvent(channelType, key, data);
    }
    
    /**
     * The purpose of this method is to provide a type that will be 
     * used in the toArray(T[]) call.
     * The concreate implementation should return an array of type with 0 element.
     * 
     * @return an array of 0 element like new String[0].
     * 
     */
    protected abstract T[] initTemplateArray();
    
    /**
     * Gets the concrete class name.
     * @return the class name.
     */
    protected abstract String getLoggingPrefix();
    
}