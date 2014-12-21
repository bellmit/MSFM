//
// -----------------------------------------------------------------------------------
// Source file: PolicyDispatcherManager.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import java.util.*;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

/**
 * The PolicyDispatcherManager class is a singleton.
 * The Policy Dispatcher Manager start/stop the policy dispatchers.
 * The policy dispatcher will be start if there are client for a product and 
 * the policy dispatcher will be stop if there is no more client for a product.
 * 
 * @author Eric Maheo
 *
 */
final public class PolicyDispatcherManager
{
    /** Eager instanciation of the PolicyDispatcherManager class. */
    private static final PolicyDispatcherManager instance = new PolicyDispatcherManager();
    
    private final Map<Integer, PolicyDispatcherCommand<?>> policyMap;
    
    /** Holds a reference to the IEC. */
    private final EventChannelAdapter eventChannel;
    
    /**
     * Create the PolicyDispatcherManager object.
     * Set to private to prevent to be called by an extern class.
     */
    private PolicyDispatcherManager(){
        policyMap = new HashMap<Integer, PolicyDispatcherCommand<?>>();
        eventChannel = EventChannelAdapterFactory.create();
        initPolicy();
    }
    
    /**
     * Gets the singleton instance of this class.
     * 
     * @return the instance of this class.
     */
    public static PolicyDispatcherManager getInstance(){
        return instance;
    }
    
    /**
     * Inform that a subscription occured.
     * @param channeltype
     * 
     * @return the number of subcription for the channelType.
     */
    public synchronized int informSubscription(int channeltype, int countSubscription){
        if(GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
              {
                 GUILoggerHome.find().debug(getLoggingPrefix()+".informSubscription()",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "inform Policy Dispatcher Manager of subscription for channelType=" 
                                           + channeltype 
                                           + " and count=" + countSubscription);
             }
        if (countSubscription == 1){
            startPolicy(channeltype);
        }
        return countSubscription;
    }

    /**
     * Inform of an unsubscription for a channelType.
     * @param channeltype
     * @return the count subscription for this channelType.
     */
    public synchronized int informUnsubscription(int channeltype, int countSubscription){
        if(GUILoggerHome.find().isDebugOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
              {
                 GUILoggerHome.find().debug(getLoggingPrefix()+".informUnsubscription()",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "inform Policy Dispatcher Manager of unsubscription for channelType=" 
                                           + channeltype 
                                           + " and count=" + countSubscription);
              }
        return countSubscription;
    }
    
    /**
     * Publish an event to the IEC into a channel.
     * 
     * @param channelType channel use to displatch.
     * @param key 
     * @param data data to dispatch.
     */
    public void dispatchEvent(int channelType, Object key, Object data)
    {
        ChannelKey channelKey = new ChannelKey(channelType, key);
        ChannelEvent event = eventChannel.getChannelEvent(this, channelKey, data);
        eventChannel.dispatch(event);
    }

    private void startPolicy(int channeltype){
        if(GUILoggerHome.find().isDebugOn() &&
          GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
        {
           GUILoggerHome.find().debug(getLoggingPrefix()+".startPolicy()",
                                     GUILoggerBusinessProperty.MARKET_QUERY,
                                     "start Policy to dispatch the IEC for channelType=" + channeltype);
       }
        policyMap.get(channeltype).startDispatching();
    }

    private void stopPolicy(int channeltype){
        policyMap.get(channeltype).stopDispatching();
    }

    private void initPolicy(){
        policyMap.put(ProductCacheAPI.PUBLISH_CURRENT_MARKET_BY_PRODUCT, new PolicyDispatcherCurrentMarketProduct());
        policyMap.put(ProductCacheAPI.PUBLISH_NBBO_BY_PRODUCT, new PolicyDispatcherNBBOProduct());
        policyMap.put(ProductCacheAPI.PUBLISH_TICKER_BY_PRODUCT , new PolicyDispatcherTickerProduct());
        policyMap.put(ProductCacheAPI.PUBLISH_RECAP_BY_PRODUCT , new PolicyDispatcherRecapProduct());
        policyMap.put(ProductCacheAPI.PUBLISH_LAST_SALE_PRODUCT , new PolicyDispatcherLastSaleProduct());
    }

    private String getLoggingPrefix(){
        return "PolicyDispatcherManager";
    }
}