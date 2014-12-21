//
// ------------------------------------------------------------------------
// FILE: MemoryWatcher.java
// 
// PACKAGE: com.cboe.presentation.common.memory
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.memory;

import com.cboe.interfaces.presentation.common.memory.MemoryUsage;
import com.cboe.interfaces.presentation.threading.GUIWorker;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.threading.APIWorkerImpl;
import com.cboe.presentation.threading.NonGUIWorkerImpl;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import java.util.Timer;
import java.util.TimerTask;

public class MemoryWatcher
{
    public static final String MEMORY_WATCHER_SECTION = "MemoryWatcher";
    public static final String PERCENTAGE_THRESHOLD_PROPERTY = "PercentageUsedThreshold";
    public static final String TIMER_DELAY_PROPERTY = "TimerDelay";
    protected double usageThreshold;
    protected int timerDelay;

    public static final ChannelKey CK_MEMORY_USAGE = new ChannelKey(ChannelType.MEMORY_USAGE, new Integer(0));

    protected int maxMb;
    protected long maxMem;

    protected Timer taskTimer;

    private MemoryUsageImpl memoryUsageImpl;
    protected EventChannelAdapter eventChannelAdapter;

    private static MemoryWatcher instance;


    private MemoryWatcher()
    {
        initialize();
    }

    public synchronized static MemoryWatcher getInstance()
    {
        if(instance == null)
        {
            instance = new MemoryWatcher();
        }
        return instance;
    }

    private void initialize()
    {
        // setup default values
        timerDelay = 5000;
        usageThreshold = 75.0;  // 75% usage
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String value = AppPropertiesFileFactory.find().getValue(MEMORY_WATCHER_SECTION, TIMER_DELAY_PROPERTY);
            if (value != null)
            {
                try
                {
                    timerDelay = Integer.parseInt(value);
                }
                catch(Exception e)
                {
                }
            }
            value = AppPropertiesFileFactory.find().getValue(MEMORY_WATCHER_SECTION, PERCENTAGE_THRESHOLD_PROPERTY);
            if (value != null)
            {
                try
                {
                    usageThreshold = Double.parseDouble(value);
                }
                catch (Exception e)
                {
                }
            }
        }

        maxMem = Runtime.getRuntime().maxMemory();
        maxMb = (int) (maxMem / MemoryUsage.MB);
        memoryUsageImpl = new MemoryUsageImpl(0,0,0,0);

        taskTimer = new Timer();
        TimerTask timerTask = new TimerTask()
        {
            public void run()
            {
                fireEvent();
            }
        };
        taskTimer.schedule(timerTask, timerDelay, timerDelay);
    }

    protected EventChannelAdapter getEventChannelAdapter()
    {
        if (eventChannelAdapter == null)
        {
            eventChannelAdapter = EventChannelAdapterFactory.find();
        }
        return eventChannelAdapter;
    }


    private void fireEvent()
    {
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = getMaxMem() - freeMem;
        double percentUsage = (usedMem * 100.0) / maxMem ;
        memoryUsageImpl.resetValues(freeMem, usedMem, percentUsage, usageThreshold);
        getEventChannelAdapter().dispatch(
                getEventChannelAdapter().getChannelEvent(this, CK_MEMORY_USAGE, memoryUsageImpl)
        );
    }

    public long getMaxMem()
    {
        return maxMem;
    }

    public int getMaxMemMb()
    {
        return maxMb;
    }
}

