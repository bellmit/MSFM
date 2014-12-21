//
// -----------------------------------------------------------------------------------
// Source file: AbstractPolicyDispatcherCommand.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

/**
 * Abstract class that implements some methods of the PolicyDispatcherCommand method.
 * It also has the timer that will dispatch the latest updates from the cache into the IEC.
 * 
 * @author Eric Maheo
 *
 */
public abstract class AbstractPolicyDispatcherCommand<T> implements PolicyDispatcherCommand<T>
{

    /** Default Frequency set by default at {@value}. */
    public static final int DEFAULT_FREQUENCY = 100;
    /** Start delay before the first timer task's call. */
    public static final int START_DELAY = 0;
    
    /** Event Channel IEC. */
    protected final EventChannelAdapter eventChannel;
    /** Timer that will execute this policy at the frequency of DEFAULT_FREQUENCY if no property found. */
    protected static final ScheduledThreadPoolExecutor TIMER = new TimerScheduledExecutor();
    /** Constant for not found property in the SbtTraderGui.properties. Its value is {@value}. */
    protected static final int NOT_FOUND_PROPERTY = -1;
    /** Constant for the section where the properties for the policy dispatcher timer is set. */
    protected static final String PRODUCT_CACHE_SECTION = "ProductCache";
    /** frequency in millis used by the timer task. */
    protected final int frequencyTask;
    
    /**
     * Prevent to call this constructor.
     */
    private AbstractPolicyDispatcherCommand(){
        eventChannel = EventChannelAdapterFactory.create();
        frequencyTask = DEFAULT_FREQUENCY; 
    }
    
    /**
     * Create this object and initialize the frequencyTask based on property or default value.
     * 
     * @param property in the SbtTraderGUI.properties in the section ProductCache.
     */
    protected AbstractPolicyDispatcherCommand(String property){
        eventChannel = EventChannelAdapterFactory.create();
        int frequency = initializeValues(property);
        if (frequency == NOT_FOUND_PROPERTY){
            frequencyTask = DEFAULT_FREQUENCY;
        }
        else {
            frequencyTask = frequency;
        }
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public void stopDispatching()
    {
        if(GUILoggerHome.find().isInformationOn() &&
                GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.MARKET_QUERY))
             {
                 GUILoggerHome.find().information(getLoggingPrefix()+".stopDispatching()",
                                           GUILoggerBusinessProperty.MARKET_QUERY,
                                           "Shutting down the dispatcher");
             }
        List<Runnable> list = TIMER.shutdownNow();
    }
    

    /**
     * Read property file SbtTraderGui.properties and initialize the  and {@code blockSize}
     * value. If not present it will call setDefault() method.
     * 
     * @param property for the timer value.
     * 
     * @return the value found for the property argument. If the property isn't 
     * found or isn't an integer in the property file it will return a #NOT_FOUND_PROPERTY.
     */
    private int initializeValues(String property)
    {
        int frequency = -1;
        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String frequencyString = AppPropertiesFileFactory.find()
                    .getValue(PRODUCT_CACHE_SECTION, property);
            
            GUILoggerHome.find().information(getLoggingPrefix() + ".initializeValues()",
                                       GUILoggerBusinessProperty.MARKET_QUERY, "section" +
                                                                      '=' + PRODUCT_CACHE_SECTION + " : " +
                                                                      property +
                                                                      '=' + frequencyString);
            
            if(frequencyString != null && frequencyString.trim().length() != 0)
            {
                try
                {
                    frequency = Integer.parseInt(frequencyString);
                }
                catch(NumberFormatException e)
                {
                    return NOT_FOUND_PROPERTY;
                }
            }
            else
            {
                return NOT_FOUND_PROPERTY;
            }
        }
        else
        {
            return NOT_FOUND_PROPERTY;
        }
        return frequency;
    }

    /**
     * Timer pool for executing the policies.
     */
    private static class TimerScheduledExecutor extends ScheduledThreadPoolExecutor {
        /** Number of timers in the pool. */
        private static final int TIMER_COUNT_MAX = 5;
        
        public TimerScheduledExecutor()
        {
            super(TIMER_COUNT_MAX);
        }
    }
    
    /**
     * 
     * @return the className.
     */
    private String getLoggingPrefix(){
        return "PolicyDispatcherCommand";
    }
}
