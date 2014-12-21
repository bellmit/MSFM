//
// -----------------------------------------------------------------------------------
// Source file: InstrumentorCalculatedEventListenerImpl.java
//
// PACKAGE: com.cboe.presentation.collector;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.collector;

import java.util.*;

import com.cboe.interfaces.instrumentation.Instrumentor;
import com.cboe.interfaces.instrumentation.CalculatedHeapInstrumentorMutable;
import com.cboe.interfaces.instrumentation.CalculatedMethodInstrumentorMutable;
import com.cboe.interfaces.instrumentation.CalculatedNetworkConnectionInstrumentorMutable;
import com.cboe.interfaces.instrumentation.CalculatedQueueInstrumentorMutable;
import com.cboe.interfaces.instrumentation.CalculatedThreadPoolInstrumentorMutable;
import com.cboe.interfaces.instrumentation.CalculatedCountInstrumentorMutable;
import com.cboe.interfaces.instrumentation.CalculatedEventInstrumentorMutable;
import com.cboe.interfaces.instrumentation.CalculatedJstatInstrumentorMutable;
import com.cboe.interfaces.instrumentation.CalculatedJmxInstrumentorMutable;
import com.cboe.interfaces.instrumentation.collector.InstrumentorCollector;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.instrumentation.InstrumentorFactory;

import com.cboe.instrumentationService.calculator.InstrumentorCalculatedEventListener;
import com.cboe.instrumentationService.instrumentors.*;

public class InstrumentorCalculatedEventListenerImpl implements InstrumentorCalculatedEventListener
{
    private static final String CATEGORY = InstrumentorCalculatedEventListenerImpl.class.getName();

    public static final String SYSTEM_HEALTH_PROPERTY_SECTION = "SystemHealthMonitor";
    public static final String TYPE_HASH_INITIAL_SIZE_PROPERTY_KEY = "InstrumentorTypeCacheSize";
    public static final String ORBNAME_HASH_INITIAL_SIZE_PROPERTY_KEY = "ClusterOrbNameCacheSize";
    public static final String INSTRUMENTOR_HASH_INITIAL_SIZE_PROPERTY_KEY = "InstrumentorPerClusterOrbNameCacheSize";
    public static final int DEFAULT_ORBNAME_CACHE_INITIAL_SIZE = 500;
    public static final int DEFAULT_INSTRUMENTOR_CACHE_INITIAL_SIZE = 2000;
    public static final int DEFAULT_TYPE_CACHE_INITIAL_SIZE = 10;

    private int typeCacheSize;
    private int orbNameInitialCacheSize;
    private int instrumentorInitialCacheSize;

    protected EventChannelAdapter eventChannel;

    private final Map<String, Map<String, Map<String, Instrumentor>>> typeCache;

    private final Object heapLockObject = new Object();
    private final Object methodLockObject = new Object();
    private final Object networkLockObject = new Object();
    private final Object queueLockObject = new Object();
    private final Object threadLockObject = new Object();
    private final Object countLockObject = new Object();
    private final Object eventLockObject = new Object();
     private final Object jmxLockObject = new Object();
     private final Object jstatLockObject = new Object();
    
    private String subKey = InstrumentorCollectorImpl.INSTRUMENTATION_SUMMARY;
    
    public InstrumentorCalculatedEventListenerImpl(String subKey)
    {
    	this();
        this.subKey = subKey;
    }

    public InstrumentorCalculatedEventListenerImpl()
    {
        typeCacheSize = DEFAULT_TYPE_CACHE_INITIAL_SIZE;
        if( AppPropertiesFileFactory.isAppPropertiesAvailable() )
        {
            String value =
                    AppPropertiesFileFactory.find().getValue(SYSTEM_HEALTH_PROPERTY_SECTION,
                                                             TYPE_HASH_INITIAL_SIZE_PROPERTY_KEY);
            if( value != null && value.length() > 0 )
            {
                try
                {
                    typeCacheSize = Integer.parseInt(value);
                }
                catch( NumberFormatException e )
                {
                    GUILoggerHome.find().exception(CATEGORY,
                                                   "Could not parse initial hash size property. " +
                                                   TYPE_HASH_INITIAL_SIZE_PROPERTY_KEY + '=' + value +
                                                   ". Will use default value.", e);
                }
            }
        }
        orbNameInitialCacheSize = DEFAULT_ORBNAME_CACHE_INITIAL_SIZE;
        if( AppPropertiesFileFactory.isAppPropertiesAvailable() )
        {
            String value =
                    AppPropertiesFileFactory.find().getValue(SYSTEM_HEALTH_PROPERTY_SECTION,
                                                             ORBNAME_HASH_INITIAL_SIZE_PROPERTY_KEY);
            if( value != null && value.length() > 0 )
            {
                try
                {
                    orbNameInitialCacheSize = Integer.parseInt(value);
                }
                catch( NumberFormatException e )
                {
                    GUILoggerHome.find().exception(CATEGORY,
                                                   "Could not parse initial hash size property. " +
                                                   ORBNAME_HASH_INITIAL_SIZE_PROPERTY_KEY + '=' +
                                                   value + ". Will use default value.", e);
                }
            }
        }

        instrumentorInitialCacheSize = DEFAULT_INSTRUMENTOR_CACHE_INITIAL_SIZE;
        if( AppPropertiesFileFactory.isAppPropertiesAvailable() )
        {
            String value =
                    AppPropertiesFileFactory.find().getValue(SYSTEM_HEALTH_PROPERTY_SECTION,
                                                             INSTRUMENTOR_HASH_INITIAL_SIZE_PROPERTY_KEY);
            if( value != null && value.length() > 0 )
            {
                try
                {
                    instrumentorInitialCacheSize = Integer.parseInt(value);
                }
                catch( NumberFormatException e )
                {
                    GUILoggerHome.find().exception(CATEGORY,
                                                   "Could not parse initial hash size property. " +
                                                   INSTRUMENTOR_HASH_INITIAL_SIZE_PROPERTY_KEY +
                                                                                               '=' + value + ". Will use default value.", e);
                }
            }
        }


        typeCache = new HashMap<String, Map<String, Map<String, Instrumentor>>>(typeCacheSize);
        eventChannel = EventChannelAdapterFactory.find();
    }

	public void acceptCalculatedCountInstrumentorEvent( long eventTimeMillis,
                                                        CountInstrumentor total,
                                                        CalculatedCountInstrumentor calculated,
	                                                    String clusterName, String orbName )
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = total.getName();
            argObj[1] = total.getUserData();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedCountInstrumentorEvent",
                                       GUILoggerINBusinessProperty.COUNT_INSTRUMENTOR, argObj);
        }

        CalculatedCountInstrumentorMutable instrumentor = null;
        boolean cacheHadInstrumentor = true;
        synchronized(countLockObject)
        {
            try
            {
                instrumentor =
                        ( CalculatedCountInstrumentorMutable )
                        findCachedInstrumentor(clusterName, orbName, InstrumentorCollector.COUNT_INSTRUMENTOR_KEY,
                                               total);
            }
            catch(ClassCastException e)
            {
                GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedCountInstrumentorEvent",
                                               "Cached Instrumentor was not the expected type. Will create new one.", e);
            }

            if(instrumentor == null)
            {
                instrumentor =
                        ( CalculatedCountInstrumentorMutable ) InstrumentorFactory.
                        createCountInstrumentor(orbName, clusterName, total, calculated);
                addCachedInstrumentor(clusterName, orbName, InstrumentorCollector.COUNT_INSTRUMENTOR_KEY,
                                      instrumentor);
                cacheHadInstrumentor = false;
            }
        }

        synchronized( instrumentor )
        {
            if(cacheHadInstrumentor)
            {
                instrumentor.setData(orbName, clusterName, total);
                instrumentor.setCalculatedData(calculated);
            }
            dispatchEvent(instrumentor);
        }
    }

	public void acceptCalculatedEventChannelInstrumentorEvent( long eventTimeMillis,
                                                               EventChannelInstrumentor total,
                                                               CalculatedEventChannelInstrumentor calculated,
	                                                           String clusterName, String orbName )
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = total.getName();
            argObj[1] = total.getUserData();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedEventInstrumentorEvent",
                                       GUILoggerINBusinessProperty.EVENT_INSTRUMENTOR, argObj);
        }

        CalculatedEventInstrumentorMutable instrumentor = null;
        boolean cacheHadInstrumentor = true;
        synchronized(eventLockObject)
        {
            try
            {
                instrumentor =
                        ( CalculatedEventInstrumentorMutable )
                        findCachedInstrumentor(clusterName, orbName, InstrumentorCollector.EVENT_INSTRUMENTOR_KEY,
                                               total);
            }
            catch(ClassCastException e)
            {
                GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedEventInstrumentorEvent",
                                               "Cached Instrumentor was not the expected type. Will create new one.", e);
            }

            if(instrumentor == null)
            {
                instrumentor =
                        ( CalculatedEventInstrumentorMutable ) InstrumentorFactory.
                        createEventInstrumentor(orbName, clusterName, total, calculated);
                addCachedInstrumentor(clusterName, orbName, InstrumentorCollector.EVENT_INSTRUMENTOR_KEY,
                                      instrumentor);
                cacheHadInstrumentor = false;
            }
        }

        synchronized( instrumentor )
        {
            if(cacheHadInstrumentor)
            {
                instrumentor.setData(orbName, clusterName, total);
                instrumentor.setCalculatedData(calculated);
            }
            dispatchEvent(instrumentor);
        }
    }

	public void acceptCalculatedHeapInstrumentorEvent(long eventTimeMillis, HeapInstrumentor total,
                                                      CalculatedHeapInstrumentor calculated, String clusterName,
                                                      String orbName)
    {
        if( GUILoggerHome.find().isDebugOn() )
        {
            Object[] argObj = new Object[2];
            argObj[0] = total.getName();
            argObj[1] = total.getUserData();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedHeapInstrumentorEvent",
                                       GUILoggerINBusinessProperty.HEAP_INSTRUMENTOR, argObj);
        }

        CalculatedHeapInstrumentorMutable instrumentor = null;
        boolean cacheHadInstrumentor = true;
        synchronized(heapLockObject)
        {
            try
            {
                instrumentor =
                        ( CalculatedHeapInstrumentorMutable )
                        findCachedInstrumentor(clusterName, orbName, InstrumentorCollector.HEAP_INSTRUMENTOR_KEY,
                                               total);
            }
            catch(ClassCastException e)
            {
                GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedHeapInstrumentorEvent",
                                               "Cached Instrumentor was not the expected type. Will create new one.", e);
            }

            if(instrumentor == null)
            {
                instrumentor =
                        ( CalculatedHeapInstrumentorMutable ) InstrumentorFactory.
                        createHeapInstrumentor(orbName, clusterName, total, calculated);
                addCachedInstrumentor(clusterName, orbName, InstrumentorCollector.HEAP_INSTRUMENTOR_KEY,
                                      instrumentor);
                cacheHadInstrumentor = false;
            }
        }

        synchronized( instrumentor )
        {
            if(cacheHadInstrumentor)
            {
                instrumentor.setData(orbName, clusterName, total);
                instrumentor.setCalculatedData(calculated);
            }
            dispatchEvent(instrumentor);
        }
    }

    public void acceptCalculatedMethodInstrumentorEvent(long eventTimeMillis, MethodInstrumentor total,
                                                        CalculatedMethodInstrumentor calculated, String clusterName,
                                                        String orbName)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = total.getName();
            argObj[1] = total.getUserData();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedMethodInstrumentorEvent",
                                       GUILoggerINBusinessProperty.METHOD_INSTRUMENTOR, argObj);
        }

        CalculatedMethodInstrumentorMutable instrumentor = null;
        boolean cacheHadInstrumentor = true;
        synchronized( methodLockObject )
        {
            try
            {
                instrumentor =
                        ( CalculatedMethodInstrumentorMutable )
                        findCachedInstrumentor(clusterName, orbName, InstrumentorCollector.METHOD_INSTRUMENTOR_KEY,
                                               total);
            }
            catch( ClassCastException e )
            {
                GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedMethodInstrumentorEvent",
                                               "Cached Instrumentor was not the expected type. Will create new one.", e);
            }

            if( instrumentor == null )
            {
                instrumentor =
                        ( CalculatedMethodInstrumentorMutable ) InstrumentorFactory.
                        createMethodInstrumentor(orbName, clusterName, total, calculated);
                addCachedInstrumentor(clusterName, orbName, InstrumentorCollector.METHOD_INSTRUMENTOR_KEY,
                                      instrumentor);
                cacheHadInstrumentor = false;
            }
        }

        synchronized( instrumentor )
        {
            if( cacheHadInstrumentor )
            {
                instrumentor.setData(orbName, clusterName, total);
                instrumentor.setCalculatedData(calculated);
            }
            dispatchEvent(instrumentor);
        }
    }

	public void acceptCalculatedNetworkConnectionInstrumentorEvent(long eventTimeMillis,
                                                                   NetworkConnectionInstrumentor total,
                                                                   CalculatedNetworkConnectionInstrumentor calculated,
                                                                   String clusterName, String orbName)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = total.getName();
            argObj[1] = total.getUserData();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedNetworkConnectionInstrumentorEvent",
                                       GUILoggerINBusinessProperty.NETWORK_INSTRUMENTOR, argObj);
        }

        CalculatedNetworkConnectionInstrumentorMutable instrumentor = null;
        boolean cacheHadInstrumentor = true;
        synchronized( networkLockObject )
        {
            try
            {
                instrumentor =
                        ( CalculatedNetworkConnectionInstrumentorMutable )
                        findCachedInstrumentor(clusterName, orbName, InstrumentorCollector.NETWORK_INSTRUMENTOR_KEY,
                                               total);
            }
            catch( ClassCastException e )
            {
                GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedNetworkConnectionInstrumentorEvent",
                                               "Cached Instrumentor was not the expected type. Will create new one.", e);
            }

            if( instrumentor == null )
            {
                instrumentor =
                        ( CalculatedNetworkConnectionInstrumentorMutable ) InstrumentorFactory.
                        createNetworkConnectionInstrumentor(orbName, clusterName, total, calculated);
                addCachedInstrumentor(clusterName, orbName, InstrumentorCollector.NETWORK_INSTRUMENTOR_KEY,
                                      instrumentor);
                cacheHadInstrumentor = false;
            }
        }

        synchronized( instrumentor )
        {
            if( cacheHadInstrumentor )
            {
                instrumentor.setData(orbName, clusterName, total);
                instrumentor.setCalculatedData(calculated);
            }
            dispatchEvent(instrumentor);
        }
    }

	public void acceptCalculatedQueueInstrumentorEvent(long eventTimeMillis, QueueInstrumentor total,
                                                       CalculatedQueueInstrumentor calculated, String clusterName,
                                                       String orbName)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = total.getName();
            argObj[1] = total.getUserData();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedQueueInstrumentorEvent",
                                       GUILoggerINBusinessProperty.QUEUE_INSTRUMENTOR, argObj);
        }

        CalculatedQueueInstrumentorMutable instrumentor = null;
        boolean cacheHadInstrumentor = true;
        synchronized( queueLockObject )
        {
            try
            {
                instrumentor =
                        ( CalculatedQueueInstrumentorMutable )
                        findCachedInstrumentor(clusterName, orbName, InstrumentorCollector.QUEUE_INSTRUMENTOR_KEY,
                                               total);
            }
            catch( ClassCastException e )
            {
                GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedQueueInstrumentorEvent",
                                               "Cached Instrumentor was not the expected type. Will create new one.", e);
            }

            if( instrumentor == null )
            {
                instrumentor =
                        ( CalculatedQueueInstrumentorMutable ) InstrumentorFactory.
                        createQueueInstrumentor(orbName, clusterName, total, calculated);
                addCachedInstrumentor(clusterName, orbName, InstrumentorCollector.QUEUE_INSTRUMENTOR_KEY,
                                      instrumentor);
                cacheHadInstrumentor = false;
            }
        }

        synchronized( instrumentor )
        {
            if( cacheHadInstrumentor )
            {
                instrumentor.setData(orbName, clusterName, total);
                instrumentor.setCalculatedData(calculated);
            }
            dispatchEvent(instrumentor);
        }
    }

	public void acceptCalculatedThreadPoolInstrumentorEvent(long eventTimeMillis, ThreadPoolInstrumentor total,
                                                            CalculatedThreadPoolInstrumentor calculated,
                                                            String clusterName, String orbName)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = total.getName();
            argObj[1] = total.getUserData();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedThreadPoolInstrumentorEvent",
                                       GUILoggerINBusinessProperty.THREAD_INSTRUMENTOR, argObj);
        }

        CalculatedThreadPoolInstrumentorMutable instrumentor = null;
        boolean cacheHadInstrumentor = true;
        synchronized( threadLockObject )
        {
            try
            {
                instrumentor =
                        ( CalculatedThreadPoolInstrumentorMutable )
                        findCachedInstrumentor(clusterName, orbName, InstrumentorCollector.THREAD_INSTRUMENTOR_KEY,
                                               total);
            }
            catch( ClassCastException e )
            {
                GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedThreadPoolInstrumentorEvent",
                                               "Cached Instrumentor was not the expected type. Will create new one.", e);
            }

            if( instrumentor == null )
            {
                instrumentor =
                        ( CalculatedThreadPoolInstrumentorMutable ) InstrumentorFactory.
                        createThreadPoolInstrumentor(orbName, clusterName, total, calculated);
                addCachedInstrumentor(clusterName, orbName, InstrumentorCollector.THREAD_INSTRUMENTOR_KEY,
                                      instrumentor);
                cacheHadInstrumentor = false;
            }
        }

        synchronized( instrumentor )
        {
            if( cacheHadInstrumentor )
            {
                instrumentor.setData(orbName, clusterName, total);
                instrumentor.setCalculatedData(calculated);
            }
            dispatchEvent(instrumentor);
        }
    }

    public void acceptCalculatedJmxInstrumentorEvent(long eventTimeMillis, JmxInstrumentor total,
                                                     CalculatedJmxInstrumentor calculated, String clusterName, String orbName)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = total.getName();
            argObj[1] = total.getUserData();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedThreadPoolInstrumentorEvent",
                                       GUILoggerINBusinessProperty.JMX_INSTRUMENTOR, argObj);
        }

        CalculatedJmxInstrumentorMutable instrumentor = null;
        boolean cacheHadInstrumentor = true;
        synchronized( jmxLockObject )
        {
            try
            {
                instrumentor =
                        ( CalculatedJmxInstrumentorMutable )
                        findCachedInstrumentor(clusterName, orbName, InstrumentorCollector.JMX_INSTRUMENTOR_KEY,
                                               total);
            }
            catch( ClassCastException e )
            {
                GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedJmxInstrumentorEvent",
                                               "Cached Instrumentor was not the expected type. Will create new one.", e);
            }

            if( instrumentor == null )
            {
                instrumentor =
                        ( CalculatedJmxInstrumentorMutable ) InstrumentorFactory.
                        createJmxInstrumentor(orbName, clusterName, total, calculated);
                addCachedInstrumentor(clusterName, orbName, InstrumentorCollector.JMX_INSTRUMENTOR_KEY,
                                      instrumentor);
                cacheHadInstrumentor = false;
            }
        }

        synchronized( instrumentor )
        {
            if( cacheHadInstrumentor )
            {
                instrumentor.setData(orbName, clusterName, total);
                instrumentor.setCalculatedData(calculated);
            }
            dispatchEvent(instrumentor);
        }
    }

    public void acceptCalculatedJstatInstrumentorEvent(long eventTimeMillis, JstatInstrumentor total,
                                                       CalculatedJstatInstrumentor calculated, String clusterName, String orbName)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            Object[] argObj = new Object[2];
            argObj[0] = total.getName();
            argObj[1] = total.getUserData();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedThreadPoolInstrumentorEvent",
                                       GUILoggerINBusinessProperty.JSTAT_INSTRUMENTOR, argObj);
        }

        CalculatedJstatInstrumentorMutable instrumentor = null;
        boolean cacheHadInstrumentor = true;
        synchronized( jstatLockObject )
        {
            try
            {
                instrumentor =
                        (CalculatedJstatInstrumentorMutable)
                        findCachedInstrumentor(clusterName, orbName, InstrumentorCollector.JSTAT_INSTRUMENTOR_KEY,
                                               total);
            }
            catch( ClassCastException e )
            {
                GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedJstatInstrumentorEvent",
                                               "Cached Instrumentor was not the expected type. Will create new one.", e);
            }

            if( instrumentor == null )
            {
                instrumentor =
                        (CalculatedJstatInstrumentorMutable) InstrumentorFactory.
                        createJstatInstrumentor(orbName, clusterName, total, calculated);
                addCachedInstrumentor(clusterName, orbName, InstrumentorCollector.JSTAT_INSTRUMENTOR_KEY,
                                      instrumentor);
                cacheHadInstrumentor = false;
            }
        }

        synchronized( instrumentor )
        {
            if( cacheHadInstrumentor )
            {
                instrumentor.setData(orbName, clusterName, total);
                instrumentor.setCalculatedData(calculated);
            }
            dispatchEvent(instrumentor);
        }
    }

    public void acceptOutlierInstrumentorEvent(long eventTimeMillis, OutlierInstrumentor outlierInstrumentor, String clusterName, String orbName)
    {
        //todo: real implementation
    }

    private void dispatchEvent(Instrumentor instrumentor)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_UPDATE,
                                               instrumentor.getChannelKey() + subKey);
        ChannelEvent event =
                EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, instrumentor);
        eventChannel.dispatch(event);
    }

    @SuppressWarnings({"UNUSED_SYMBOL"})
    private Instrumentor findCachedInstrumentor(String clusterName, String orbName, String type,
                                                com.cboe.instrumentationService.instrumentors.Instrumentor instrumentor)
    {
        return getInstrumentor(type, orbName, instrumentor.getName());
    }

    @SuppressWarnings({"UNUSED_SYMBOL"})
    private void addCachedInstrumentor(String clusterName, String orbName, String type, Instrumentor instrumentor)
    {
        putInstrumentor(type, orbName, instrumentor);
    }

    private Instrumentor getInstrumentor(String type, String orbName, String name)
    {
        Instrumentor instrumentor = getOrbMap(type, orbName).get(name);
        return instrumentor;
    }

    private void putInstrumentor(String type, String orbName, Instrumentor instrumentor)
    {
        getOrbMap(type, orbName).put(instrumentor.getName(), instrumentor);
    }

    private Map<String, Instrumentor> getOrbMap(String type, String orbName)
    {
        Map<String, Instrumentor> orbMap = getTypeMap(type).get(orbName);
        if (orbMap == null)
        {
            orbMap = new HashMap<String, Instrumentor>(orbNameInitialCacheSize);
            getTypeMap(type).put(orbName, orbMap);
        }
        return orbMap;
    }

    private Map<String, Map<String, Instrumentor>> getTypeMap(String type)
    {
        Map<String, Map<String, Instrumentor>> typeMap = typeCache.get(type);
        if (typeMap == null)
        {
            typeMap = new HashMap<String, Map<String, Instrumentor>>(instrumentorInitialCacheSize);
            typeCache.put(type, typeMap);
        }
        return typeMap;
    }
}