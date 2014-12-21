//
// -----------------------------------------------------------------------------------
// Source file: PolicyDispatcherTickerProduct.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import java.util.concurrent.TimeUnit;

import com.cboe.interfaces.presentation.marketData.express.TickerV4;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;

/**
 * Publisher policy used to publish to the IEC (channel {@link ProductCacheAPI#PUBLISH_TICKER_BY_PRODUCT})
 * the ticker product from the product cache at a metronom pace 
 * defined by the SbtTraderGui.properties in the section cacheproduct.
 *  
 * @author Eric Maheo
 *
 */
public class PolicyDispatcherTickerProduct extends AbstractPolicyDispatcherCommand<TickerV4>
{

    /** Property name for the policy dispatcher Recap product located in the productcache section of the SbtTraderGui.properties. */
    public static final String TICKER_V4_TIMER_PROPERTY = "tickerV4TimerMillis";
    /** Runnable that will execute the task. */
    private final Runnable task;
    
    /**
     * Create the dispatcher policy for the ticker.
     */
    public PolicyDispatcherTickerProduct(){
        super(TICKER_V4_TIMER_PROPERTY);
        task = new TickerTask();
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public void dispatchEvent(TickerV4 event)
    {
        ChannelKey channelKey = new ChannelKey(ProductCacheAPI.PUBLISH_TICKER_BY_PRODUCT , event.getProductKey());
        ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKey, event);
        eventChannel.dispatch(channelEvent);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void startDispatching()
    {
        if(GUILoggerHome.find().isInformationOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
             {
                 GUILoggerHome.find().information(getLoggingPrefix()+".startDispatching()",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "starting the dispatcher");
             }
        TIMER.scheduleWithFixedDelay(task, START_DELAY, frequencyTask, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Task that represent the policy and that is being executed by the timer.
     * 
     */
    private class TickerTask implements Runnable {
        
        public void run(){
            try {
                TickerV4[] list = (TickerV4[]) ProductCacheAPI.getInstance().getTickerProductCache().getLatestUpdates();
                for (TickerV4 obj: list){
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
        return "PolicyDispatcherTickerProduct";
    }
}