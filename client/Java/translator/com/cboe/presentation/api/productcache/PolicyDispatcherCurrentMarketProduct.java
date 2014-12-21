//
// -----------------------------------------------------------------------------------
// Source file: PolicyDispatcherCurrentMarketProduct.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import java.util.concurrent.TimeUnit;

import com.cboe.interfaces.presentation.marketData.express.CurrentMarketV4ProductContainer;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;

/**
 * Publisher policy used to publish to the IEC (channel {@link ProductCacheAPI#PUBLISH_CURRENT_MARKET_BY_PRODUCT})
 * the current market product from the product cache at a metronom pace 
 * defined by the SbtTraderGui.properties in the section cacheproduct.
 * 
 * @author Eric Maheo
 * 
 */
class PolicyDispatcherCurrentMarketProduct extends
        AbstractPolicyDispatcherCommand<CurrentMarketV4ProductContainer>
        
{
    /** Property name for the policy dispatcher current market product located in the productcache section of the SbtTraderGui.properties. */
    public static final String CURRENT_MARKET_V4_TIMER_PROPERTY = "currentMarketV4TimerMillis";
    /** Runnable that will execute the task. */
    private final Runnable runTask;
    
    /**
     * Create this object.
     */
    public PolicyDispatcherCurrentMarketProduct(){
        super(CURRENT_MARKET_V4_TIMER_PROPERTY);
        runTask = new Task();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public synchronized void startDispatching()
    {
        if(GUILoggerHome.find().isInformationOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
             {
                 GUILoggerHome.find().information(getLoggingPrefix()+".startDispatching()",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "starting the dispatcher");
             }
        TIMER.scheduleWithFixedDelay(runTask, START_DELAY, frequencyTask, TimeUnit.MILLISECONDS);
    }
    
    /**
     * {@inheritDoc}.
     */
    public synchronized void dispatchEvent(CurrentMarketV4ProductContainer event)
    {
        ChannelKey channelKey = new ChannelKey(ProductCacheAPI.PUBLISH_CURRENT_MARKET_BY_PRODUCT , event.getProductKey());
        ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKey, event);
        eventChannel.dispatch(channelEvent);
    }


    /**
     * Task that represent the policy and that is being executed by the timer.
     */
    private class Task implements Runnable {
        
        @Override
        public void run(){
            //query the cache and get the latest updates to dispatch them into IEC.
            try {
            CurrentMarketV4ProductContainer[] list = (CurrentMarketV4ProductContainer[]) ProductCacheAPI.getInstance().getCurrentMarketProductCache().getLatestUpdates();
                for (CurrentMarketV4ProductContainer obj: list){
                    dispatchEvent(obj);
                }
            }catch (Throwable t){
                GUILoggerHome.find().exception(t);
            }
        }
    }
    
    /**
     * 
     * @return the class name.
     */
    private String getLoggingPrefix(){
        return "PolicyDispatcherCurrentMarketProduct";
    }    
}
