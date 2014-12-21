//
// -----------------------------------------------------------------------------------
// Source file: Subscriber.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.*;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

/**
 * Singleton class in charge of subscribing/Unsubscribing to the IEC for a channelType and 
 * maintaining state of the [un]subscribtion. It maintains the count of subscription 
 * for a productkey.
 * 
 * This class is Thread safe.
 * 
 * @author Eric Maheo
 *
 */
final public class Subscriber
{
    /** Constant for a count subscribtion returned for a not found product. */
    public static final int NOT_FOUND_SUBSCRIPTION = -1;
    /** Eager instantiation of this class. */
    private static final Subscriber instance = new Subscriber();
    /** Event Channel IEC. */
    private final EventChannelAdapter eventChannel;
    /** Guards the SubscriptionCounter inner object. */
    private final Lock lockSubscribedCounter = new ReentrantLock();
    /** Reference to the object that maintain the count for the subscription counter of subscribed product. */
    private final SubscriptionCounter subscriptionCounter;
    
    /**
     * Gets the instance of this class.
     * 
     * @return the instance of this class.
     */
    public static Subscriber getInstance(){
        return instance;
    }
    
    /**
     * Create this Object.
     */
    private Subscriber(){
        eventChannel = EventChannelAdapterFactory.find();
        subscriptionCounter = new SubscriptionCounter();
    }

    /**
     * Subscribe a product by its productKey.
     * @param productKey
     * 
     * @return the number of subscribtion for the productKey.
     */
    public int subscribeProduct(int channelType, int productKey, EventChannelListener client)
    {
           if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
            {
                GUILoggerHome.find().debug(getLoggingPrefix()+".subscribeMarketData()",
                                          GUILoggerBusinessProperty.MARKET_QUERY,
                                          "subscribing cache to the IEC for productKey=" + productKey);
            }
           lockSubscribedCounter.lock();
            try {
                ChannelKey key = new ChannelKey(channelType, productKey);
                internalSubscribeIEC(key, client);
                return subscriptionCounter.incrementProduct(key);
            }
            finally{
                lockSubscribedCounter.unlock();
            }
    }

    /**
     * Unsubscribe a Product by its productKey.
     * @param productKey
     * 
     * @return the number of subscribtion for the productKey.
     */
    public int unsubscribeProduct(int channelType, int productKey, EventChannelListener client)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
            GUILoggerHome.find().debug(getLoggingPrefix()+".unsubscribeMarketData()",
                                       GUILoggerBusinessProperty.MARKET_QUERY,
                                       "unsubscribing cache to the IEC for productKey=" + productKey);
        }
        lockSubscribedCounter.lock();
        try{
            ChannelKey key = new ChannelKey(channelType, productKey);
            internalUnsubscribeIEC(key, client);
            int count = subscriptionCounter.decrementProduct(key);
            return count;
        }
        finally{
            lockSubscribedCounter.unlock();
        }
    }
    
    /**
     * 
     * @param productKey 
     * @return true if the product is already subscribed.
     */
    public boolean isSubscribedForProduct(int channelType, int productKey)
    {
        lockSubscribedCounter.lock();
        try{
            ChannelKey key = new ChannelKey(channelType, productKey);
            return subscriptionCounter.isProductSubscribed(key);
        }
        finally{
            lockSubscribedCounter.unlock();
        }
    }
    
    private void internalSubscribeIEC(ChannelKey key, EventChannelListener client)
    {
        eventChannel.addChannelListener(eventChannel, client, key);
    }

    private void internalUnsubscribeIEC(ChannelKey key, EventChannelListener client)
    {
        eventChannel.removeChannelListener(eventChannel, client, key);
    }

    /**
     * @return the class name of this class.
     */
    private String getLoggingPrefix(){
        return "Subscriber";
    }
    
    /**
     * Class that maintain the count of subscribed product in cache.
     * The outter class must take care of the thread safety of this class.
     * 
     * @author Eric Maheo
     */
    private class SubscriptionCounter {
        
        private final Map<ChannelKey, Integer> subscribedCount;
        
        /**
         * Create the subscription counter.
         */
        public SubscriptionCounter(){
            subscribedCount = new HashMap<ChannelKey, Integer>();
        }
        /**
         * Increment the subscription count for a productkey. 
         * 
         * @param productKey to increment.
         * @return the count for this product key.
         */
        public int incrementProduct(ChannelKey key){
            Integer value = subscribedCount.get(key);
            int count = 0;
            if (value == null){
                count = 0;
            }
            else {
                count = value;
            }
            subscribedCount.put(key, ++count);
            
            if(GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
             {
                 GUILoggerHome.find().debug(getLoggingPrefix()+".incrementProduct()",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "increment count=" + count + " for productKey=" + key.toString());
             }
            return count;
        }
        
        /**
         * Decrement the subscription count for a productkey.
         * 
         * @param productKey to decrement.
         * @return the count for this product key.
         */
        public int decrementProduct(ChannelKey key){
            Integer value = subscribedCount.get(key);
            int count = 0;
            if (value == null){
                throw new IllegalStateException("Attempt to decrement the product " + 
                        key + " in cache that isn't registered.");
            }
            if (value == 0){
                return NOT_FOUND_SUBSCRIPTION; //nothing else to decrement.
            }
            count = value;
            subscribedCount.put(key, --count);
            if(GUILoggerHome.find().isDebugOn() &&
                    GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
                 {
                     GUILoggerHome.find().debug(getLoggingPrefix()+".decrementProduct()",
                                               GUILoggerBusinessProperty.MARKET_QUERY,
                                               "decrement count=" + count + " for productKey=" + key.toString());
                 }
            return count;
        }
        
        /**
         * Returns true if the channelKey has at least 1 subscription for it.
         * 
         * @param key channel key.
         * @return true if subscription > 0 and false in the other cases.
         */
        public boolean isProductSubscribed(ChannelKey key){
            Integer value = subscribedCount.get(key);
            if (value == null || value < 1){
                return false;
            }
            return true;
        }
        
    }
}