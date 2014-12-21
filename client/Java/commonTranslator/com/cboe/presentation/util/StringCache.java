//
// -----------------------------------------------------------------------------------
// Source file: StringCache.java
//
// PACKAGE: com.cboe.presentation.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.util;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.memory.MemoryWatcher;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.interfaces.presentation.common.memory.MemoryUsage;

import java.util.*;

public class StringCache implements EventChannelListener
{
    private static final String PROPERTY_SECTION = "Defaults";
    private static final String MAX_SIZE_PROPERTY_NAME = "StringCacheMaxSize";
    private static final String MEM_USAGE_PROPERTY_NAME = "StringCacheMemUsageThreshold";
    private static final int DEFAULT_CACHE_MAX_SIZE = 200000;
    private static final int DEFAULT_MEM_USAGE_THRESHOLD = 95;
    private static final StringCache instance = new StringCache();
    private Map<String, String> stringCache;
    private Integer cacheMaxSize;
    private Integer memUsageThreshold;
    private MemoryUsage memoryUsage;
    private String memUsageString;

    private StringCache()
    {
        initialize();
    }

    private void initialize()
    {
        cacheMaxSize = null;
        memUsageThreshold = null;
        stringCache = Collections.synchronizedMap(new HashMap<String, String>());

        EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), this,
                new ChannelKey(ChannelType.MEMORY_USAGE, 0));
        MemoryWatcher.getInstance(); // initialize if not already initialized
    }

    public static String get(String str)
    {
        return instance.internalGet(str);
    }

    public void channelUpdate(ChannelEvent event)
    {
        try
        {
            memoryUsage = (MemoryUsage) ((MemoryUsage) event.getEventData()).clone();
        }
        catch(CloneNotSupportedException e)
        {
            // should never happen
            memoryUsage = (MemoryUsage) event.getEventData();
        }
        finally
        {
            memUsageString = memoryUsage.toString();
            if (stringCache.size() > 0 && memoryUsage.getPercentUsage() >= getMemUsageThreshold())
            {
                StringBuilder sb = new StringBuilder(150);
                sb.append(getClass().getName()).append(" being cleared (cache size: ").append(stringCache.size()).append("). Memory usage reached threshold ");
                sb.append(getMemUsageThreshold()).append(" percent. Current memory usage details: ").append(memUsageString);
                GUILoggerHome.find().audit(sb.toString());
                stringCache.clear();
            }
        }
    }

    private String internalGet(String str)
    {
        String retVal = stringCache.get(str);
        if (retVal == null)
        {
            retVal = new String(str);
            stringCache.put(retVal, retVal);
        }
        if(stringCache.size() >= getCacheMaxSize())
        {
            StringBuilder sb = new StringBuilder(150);
            sb.append(getClass().getName()).append(" being cleared (cache size: ").append(stringCache.size()).append("). Cache reached max size ");
            sb.append(getCacheMaxSize()).append(". Current memory usage details: ").append(memUsageString);
            GUILoggerHome.find().audit(sb.toString());
            stringCache.clear();
        }
        return retVal;
    }

    private int getMemUsageThreshold()
    {
        if (memUsageThreshold == null)
        {
            memUsageThreshold = getIntPropValue(MEM_USAGE_PROPERTY_NAME, DEFAULT_MEM_USAGE_THRESHOLD);
        }
        return memUsageThreshold;
    }

    private int getCacheMaxSize()
    {
        if (cacheMaxSize == null)
        {
            cacheMaxSize = getIntPropValue(MAX_SIZE_PROPERTY_NAME, DEFAULT_CACHE_MAX_SIZE);
        }
        return cacheMaxSize;
    }

    private int getIntPropValue(String propName, int defaultValue)
    {
        int retVal = defaultValue;
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String propVal = AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION, propName);
            if (propVal != null && propVal.length() > 0)
            {
                try
                {
                    retVal = Integer.parseInt(propVal);
                }
                catch (NumberFormatException e)
                {
                    GUILoggerHome.find().exception(e, propName + " setting in properties file is invalid '" +
                            propVal + "'; will use default : " + defaultValue);
                }
            }
        }
        return retVal;
    }
}
