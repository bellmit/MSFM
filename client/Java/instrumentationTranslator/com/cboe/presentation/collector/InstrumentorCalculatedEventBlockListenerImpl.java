//
// -----------------------------------------------------------------------------------
// Source file: InstrumentorCalculatedEventBlockListenerImpl.java
//
// PACKAGE: com.cboe.presentation.collector;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.collector;

import java.util.*;

import com.cboe.interfaces.instrumentation.CalculatedJstatInstrumentorMutable;
import com.cboe.interfaces.instrumentation.*;
import com.cboe.interfaces.instrumentation.collector.InstrumentorCollector;
import com.cboe.interfaces.instrumentation.collector.InstrumentorCollectorEvent;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.common.formatters.InstrumentorTypes;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.instrumentation.InstrumentorFactory;

import com.cboe.instrumentationService.calculator.InstrumentorCalculatedEventBlockListener;
import com.cboe.instrumentationService.instrumentors.CalculatedCountInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedEventChannelInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedHeapInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedMethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedNetworkConnectionInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedQueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedThreadPoolInstrumentor;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;
import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;
import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedJmxInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedJstatInstrumentor;


public class InstrumentorCalculatedEventBlockListenerImpl
        implements InstrumentorCalculatedEventBlockListener
{
    private static final String CATEGORY =
            InstrumentorCalculatedEventBlockListenerImpl.class.getName();

    public static final String SYSTEM_HEALTH_PROPERTY_SECTION =
            "SystemHealthMonitor";
    public static final String CLUSTER_ORBNAME_HASH_INITIAL_SIZE_PROPERTY_KEY =
            "ClusterOrbNameCacheSize";
    public static final String INSTRUMENTOR_HASH_INITIAL_SIZE_PROPERTY_KEY =
            "InstrumentorPerClusterOrbNameCacheSize";
    public static final String TYPE_HASH_INITIAL_SIZE_PROPERTY_KEY = "InstrumentorTypeCacheSize";

    public static final String DEFAULT_CLUSTER_NAME = "DefClst";

    public static final int DEFAULT_CLUSTER_ORBNAME_CACHE_INITIAL_SIZE = 500;
    public static final int DEFAULT_INSTRUMENTOR_CACHE_INITIAL_SIZE = 2000;
    public static final int DEFAULT_TYPE_CACHE_INITIAL_SIZE = 10;

    protected EventChannelAdapter eventChannel;

    private final Map<String, Map<String, CacheHolder>> typeCache;

    private final Object heapLockObject = new Object();
    private final Object methodLockObject = new Object();
    private final Object networkLockObject = new Object();
    private final Object queueLockObject = new Object();
    private final Object threadLockObject = new Object();
    private final Object countLockObject = new Object();
    private final Object eventLockObject = new Object();
     private final Object jmxLockObject = new Object();
     private final Object jstatLockObject = new Object();

    private int clusterOrbNameInitialCacheSize;
    private int instrumentorInitialCacheSize;
    private int typeCacheSize;

    private String subKey = InstrumentorCollectorImpl.INSTRUMENTATION_SUMMARY;

    public InstrumentorCalculatedEventBlockListenerImpl(String subKey)
    {
    	this();
        this.subKey = subKey;
    }
    
    public InstrumentorCalculatedEventBlockListenerImpl()
    {
        clusterOrbNameInitialCacheSize = DEFAULT_CLUSTER_ORBNAME_CACHE_INITIAL_SIZE;
        if( AppPropertiesFileFactory.isAppPropertiesAvailable() )
        {
            String value =
                    AppPropertiesFileFactory.find().getValue(SYSTEM_HEALTH_PROPERTY_SECTION,
                                                             CLUSTER_ORBNAME_HASH_INITIAL_SIZE_PROPERTY_KEY);
            if( value != null && value.length() > 0 )
            {
                try
                {
                    clusterOrbNameInitialCacheSize = Integer.parseInt(value);
                }
                catch( NumberFormatException e )
                {
                    GUILoggerHome.find().exception(CATEGORY,
                                                   "Could not parse initial hash size property. " +
                                                   CLUSTER_ORBNAME_HASH_INITIAL_SIZE_PROPERTY_KEY +
                                                   '=' + value + ". Will use default value.", e);
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
        typeCache = new HashMap<String, Map<String, CacheHolder>>(typeCacheSize);

        eventChannel = EventChannelAdapterFactory.find();
    }

    @SuppressWarnings({"OverlyLongMethod", "RawUseOfParameterizedType",
            "CollectionDeclaredAsConcreteClass"})
    public void acceptCalculatedCountInstrumentorsEvent(long eventTimeMillis, ArrayList totalList,
                                                        ArrayList calculatedList, String clusterName,
                                                        String orbName)
    {
        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.COUNT_INSTRUMENTOR) )
        {
            Object[] argObj = new Object[3];
            argObj[0] = clusterName;
            argObj[1] = orbName;
            argObj[2] = "instrumentors.size=" + totalList.size();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedCountInstrumentorsEvent",
                                       GUILoggerINBusinessProperty.COUNT_INSTRUMENTOR, argObj);
        }

        CacheHolder holder;
        synchronized( countLockObject )
        {
            holder = findTypeOrbNameHolder(orbName, InstrumentorCollector.COUNT_INSTRUMENTOR_KEY);
        }

        synchronized( holder )
        {
            Map<String, Instrumentor> instrumentorCache = holder.getInstrumentorCache();

            List<Instrumentor> eventList = new ArrayList<Instrumentor>(totalList.size());

            if( instrumentorCache.isEmpty() )
            {
                Instrumentor instrumentor;
                CountInstrumentor totalInstrumentor;
                CalculatedCountInstrumentor calcInstrumentor;
                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (CountInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedCountInstrumentor) calculatedList.get(i);

                    instrumentor =
                            InstrumentorFactory.
                            createCountInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.COUNT_INSTRUMENTOR) )
                    {
                        Object[] argObj = new Object[3];
                        argObj[0] = clusterName;
                        argObj[1] = orbName;
                        argObj[2] = instrumentor.getName();

                        GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedCountInstrumentorsEvent - createCountInstrumentor",
                                                   GUILoggerINBusinessProperty.COUNT_INSTRUMENTOR, argObj);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, instrumentorCache);
                }
            }
            else
            {
                Map<String, Instrumentor> newCache = holder.createNewInstrumentorCache();

                CalculatedCountInstrumentorMutable instrumentor = null;
                CountInstrumentor totalInstrumentor;
                CalculatedCountInstrumentor calcInstrumentor;

                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (CountInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedCountInstrumentor) calculatedList.get(i);

                    try
                    {
                        instrumentor = ( CalculatedCountInstrumentorMutable ) findCachedInstrumentor(
                                totalInstrumentor, instrumentorCache);
                    }
                    catch( ClassCastException e )
                    {
                        GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedCountInstrumentorsEvent",
                                                       "Cached Instrumentor was not the expected type. Will create new one.", e);
                    }

                    if( instrumentor == null )
                    {
                        instrumentor =
                                ( CalculatedCountInstrumentorMutable ) InstrumentorFactory.
                                createCountInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    }
                    else
                    {
                        instrumentor.setData(orbName, clusterName, totalInstrumentor);
                        instrumentor.setCalculatedData(calcInstrumentor);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, newCache);
                }

                holder.getInstrumentorCache().clear();
                holder.setInstrumentorCache(newCache);
            }
            //String orbSubscriptionKey = orbName + InstrumentorTypes.QUEUE;
            String orbSubscriptionKey = InstrumentorTypes.QUEUE_STRING;
            InstrumentorCollectorEvent instrumentorCollectorEvent = createEvent(clusterName, orbName, eventList);
            dispatchEvent(instrumentorCollectorEvent, orbSubscriptionKey);
        }
    }

    @SuppressWarnings({"OverlyLongMethod", "CollectionDeclaredAsConcreteClass",
            "RawUseOfParameterizedType"})
    public void acceptCalculatedEventChannelInstrumentorsEvent(long eventTimeMillis,
                                                               ArrayList totalList,
                                                               ArrayList calculatedList,
                                                               String clusterName,
                                                               String orbName)
    {
        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.EVENT_INSTRUMENTOR) )
        {
            Object[] argObj = new Object[3];
            argObj[0] = clusterName;
            argObj[1] = orbName;
            argObj[2] = "instrumentors.size=" + totalList.size();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedEventChannelInstrumentorsEvent",
                                       GUILoggerINBusinessProperty.EVENT_INSTRUMENTOR, argObj);
        }

        CacheHolder holder;
        synchronized( eventLockObject )
        {
            holder = findTypeOrbNameHolder(orbName, InstrumentorCollector.EVENT_INSTRUMENTOR_KEY);
        }

        synchronized( holder )
        {
            Map<String, Instrumentor> instrumentorCache = holder.getInstrumentorCache();

            List<Instrumentor> eventList = new ArrayList<Instrumentor>(totalList.size());

            if( instrumentorCache.isEmpty() )
            {
                Instrumentor instrumentor;
                EventChannelInstrumentor totalInstrumentor;
                CalculatedEventChannelInstrumentor calcInstrumentor;
                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (EventChannelInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedEventChannelInstrumentor) calculatedList.get(i);

                    instrumentor =
                            InstrumentorFactory.
                            createEventInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);

                    if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.EVENT_INSTRUMENTOR) )
                    {
                        Object[] argObj = new Object[3];
                        argObj[0] = clusterName;
                        argObj[1] = orbName;
                        argObj[2] = instrumentor.getName();

                        GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedEventChannelInstrumentorsEvent - createCountInstrumentor",
                                                   GUILoggerINBusinessProperty.EVENT_INSTRUMENTOR, argObj);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, instrumentorCache);
                }
            }
            else
            {
                Map<String, Instrumentor> newCache = holder.createNewInstrumentorCache();

                CalculatedEventInstrumentorMutable instrumentor = null;
                EventChannelInstrumentor totalInstrumentor;
                CalculatedEventChannelInstrumentor calcInstrumentor;

                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (EventChannelInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedEventChannelInstrumentor) calculatedList.get(i);

                    try
                    {
                        instrumentor = ( CalculatedEventInstrumentorMutable ) findCachedInstrumentor(
                                totalInstrumentor,
                                                                                                     instrumentorCache);
                    }
                    catch( ClassCastException e )
                    {
                        GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedEventChannelInstrumentorsEvent",
                                                       "Cached Instrumentor was not the expected type. Will create new one.", e);
                    }

                    if( instrumentor == null )
                    {
                        instrumentor =
                                ( CalculatedEventInstrumentorMutable ) InstrumentorFactory.
                                createEventInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    }
                    else
                    {
                        instrumentor.setData(orbName, clusterName, totalInstrumentor);
                        instrumentor.setCalculatedData(calcInstrumentor);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, newCache);
                }

                holder.getInstrumentorCache().clear();
                holder.setInstrumentorCache(newCache);
            }
            //String orbSubscriptionKey = orbName + InstrumentorTypes.QUEUE;
            String orbSubscriptionKey = InstrumentorTypes.QUEUE_STRING;
            InstrumentorCollectorEvent instrumentorCollectorEvent = createEvent(clusterName, orbName, eventList);
            dispatchEvent(instrumentorCollectorEvent, orbSubscriptionKey);
        }
    }

    @SuppressWarnings({"OverlyLongMethod", "CollectionDeclaredAsConcreteClass",
            "RawUseOfParameterizedType"})
    public void acceptCalculatedHeapInstrumentorsEvent(long eventTimeMillis, ArrayList totalList,
                                                       ArrayList calculatedList, String clusterName,
                                                       String orbName)
    {
        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.HEAP_INSTRUMENTOR) )
        {
            Object[] argObj = new Object[3];
            argObj[0] = clusterName;
            argObj[1] = orbName;
            argObj[2] = "instrumentors.size=" + totalList.size();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedHeapInstrumentorsEvent",
                                       GUILoggerINBusinessProperty.HEAP_INSTRUMENTOR, argObj);
        }

        CacheHolder holder;
        synchronized( heapLockObject )
        {
            holder = findTypeOrbNameHolder(orbName, InstrumentorCollector.HEAP_INSTRUMENTOR_KEY);
        }

        synchronized( holder )
        {
            Map<String, Instrumentor> instrumentorCache = holder.getInstrumentorCache();

            List<Instrumentor> eventList = new ArrayList<Instrumentor>(totalList.size());


            if(instrumentorCache.isEmpty())
            {
                Instrumentor instrumentor;
                HeapInstrumentor totalInstrumentor;
                CalculatedHeapInstrumentor calcInstrumentor;

                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (HeapInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedHeapInstrumentor) calculatedList.get(i);

                    instrumentor = InstrumentorFactory.createHeapInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.HEAP_INSTRUMENTOR) )
                    {
                        Object[] argObj = new Object[3];
                        argObj[0] = clusterName;
                        argObj[1] = orbName;
                        argObj[2] = instrumentor.getName();

                        GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedHeapInstrumentorsEvent - createHeapInstrumentor",
                                                   GUILoggerINBusinessProperty.HEAP_INSTRUMENTOR, argObj);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, instrumentorCache);
                }
            }
            else
            {
                Map<String, Instrumentor> newCache = holder.createNewInstrumentorCache();

                CalculatedHeapInstrumentorMutable instrumentor = null;
                HeapInstrumentor totalInstrumentor;
                CalculatedHeapInstrumentor calcInstrumentor;

                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (HeapInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedHeapInstrumentor) calculatedList.get(i);

                    try
                    {
                        instrumentor =
                                ( CalculatedHeapInstrumentorMutable )
                                findCachedInstrumentor(totalInstrumentor,instrumentorCache);
                    }
                    catch( ClassCastException e )
                    {
                        GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedHeapInstrumentorsEvent",
                                                       "Cached Instrumentor was not the expected type. Will create new one.", e);
                    }

                    if( instrumentor == null )
                    {
                        instrumentor =
                                ( CalculatedHeapInstrumentorMutable ) InstrumentorFactory.
                                createHeapInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    }
                    else
                    {
                        instrumentor.setData(orbName, clusterName, totalInstrumentor);
                        instrumentor.setCalculatedData(calcInstrumentor);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, newCache);
                }

                holder.getInstrumentorCache().clear();
                holder.setInstrumentorCache(newCache);
            }
            InstrumentorCollectorEvent instrumentorCollectorEvent = createEvent(clusterName, orbName, eventList);
            dispatchEvent(instrumentorCollectorEvent, InstrumentorCollector.HEAP_INSTRUMENTOR_KEY);
        }
    }

    @SuppressWarnings({"OverlyLongMethod", "CollectionDeclaredAsConcreteClass",
            "RawUseOfParameterizedType"})
    public void acceptCalculatedMethodInstrumentorsEvent(long eventTimeMillis, ArrayList totalList,
                                                         ArrayList calculatedList,
                                                         String clusterName, String orbName)
    {
        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.METHOD_INSTRUMENTOR) )
        {
            Object[] argObj = new Object[3];
            argObj[0] = clusterName;
            argObj[1] = orbName;
            argObj[2] = "instrumentors.size=" + totalList.size();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedMethodInstrumentorsEvent",
                                       GUILoggerINBusinessProperty.METHOD_INSTRUMENTOR, argObj);
        }

        CacheHolder holder;
        synchronized( methodLockObject )
        {
            holder = findTypeOrbNameHolder(orbName, InstrumentorCollector.METHOD_INSTRUMENTOR_KEY);
        }

        synchronized( holder )
        {
            Map<String, Instrumentor> instrumentorCache = holder.getInstrumentorCache();

            List<Instrumentor> eventList = new ArrayList<Instrumentor>(totalList.size());

            if( instrumentorCache.isEmpty() )
            {
                Instrumentor instrumentor;
                MethodInstrumentor totalInstrumentor;
                CalculatedMethodInstrumentor calcInstrumentor;

                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (MethodInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedMethodInstrumentor) calculatedList.get(i);

                    instrumentor = InstrumentorFactory.createMethodInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.METHOD_INSTRUMENTOR) )
                    {
                        Object[] argObj = new Object[3];
                        argObj[0] = clusterName;
                        argObj[1] = orbName;
                        argObj[2] = instrumentor.getName();

                        GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedMethodInstrumentorsEvent - createMethodInstrumentor",
                                                   GUILoggerINBusinessProperty.METHOD_INSTRUMENTOR, argObj);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, instrumentorCache);
                }
            }
            else
            {
                Map<String, Instrumentor> newCache = holder.createNewInstrumentorCache();

                CalculatedMethodInstrumentorMutable instrumentor = null;
                MethodInstrumentor totalInstrumentor;
                CalculatedMethodInstrumentor calcInstrumentor;

                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (MethodInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedMethodInstrumentor) calculatedList.get(i);

                    try
                    {
                        instrumentor = ( CalculatedMethodInstrumentorMutable ) findCachedInstrumentor(
                                totalInstrumentor,
                                                                                                      instrumentorCache);
                    }
                    catch( ClassCastException e )
                    {
                        GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedMethodInstrumentorsEvent",
                                                       "Cached Instrumentor was not the expected type. Will create new one.", e);
                    }

                    if( instrumentor == null )
                    {
                        instrumentor =
                                ( CalculatedMethodInstrumentorMutable ) InstrumentorFactory.
                                createMethodInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);

                    }
                    else
                    {
                        instrumentor.setData(orbName, clusterName, totalInstrumentor);
                        instrumentor.setCalculatedData(calcInstrumentor);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, newCache);
                }

                holder.getInstrumentorCache().clear();
                holder.setInstrumentorCache(newCache);
            }
            //String orbSubscriptionKey = orbName + InstrumentorTypes.METHOD;
            String orbSubscriptionKey = InstrumentorTypes.METHOD_STRING;
            InstrumentorCollectorEvent instrumentorCollectorEvent = createEvent(clusterName, orbName, eventList);
            dispatchEvent(instrumentorCollectorEvent, orbSubscriptionKey);
        }
    }

    @SuppressWarnings({"OverlyLongMethod", "CollectionDeclaredAsConcreteClass",
            "RawUseOfParameterizedType"})
    public void acceptCalculatedNetworkConnectionInstrumentorsEvent(long eventTimeMillis,
                                                                    ArrayList totalList,
                                                                    ArrayList calculatedList,
                                                                    String clusterName,
                                                                    String orbName)
    {
        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.NETWORK_INSTRUMENTOR) )
        {
            Object[] argObj = new Object[3];
            argObj[0] = clusterName;
            argObj[1] = orbName;
            argObj[2] = "instrumentors.size=" + totalList.size();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedNetworkConnectionInstrumentorsEvent",
                                       GUILoggerINBusinessProperty.NETWORK_INSTRUMENTOR, argObj);
        }

        CacheHolder holder;
        synchronized( networkLockObject )
        {
            holder = findTypeOrbNameHolder(orbName, InstrumentorCollector.NETWORK_INSTRUMENTOR_KEY);
        }

        synchronized( holder )
        {
            Map<String, Instrumentor> instrumentorCache = holder.getInstrumentorCache();

            List<Instrumentor> eventList = new ArrayList<Instrumentor>(totalList.size());


            if( instrumentorCache.isEmpty() )
            {
                Instrumentor instrumentor;
                NetworkConnectionInstrumentor totalInstrumentor;
                CalculatedNetworkConnectionInstrumentor calcInstrumentor;

                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (NetworkConnectionInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedNetworkConnectionInstrumentor) calculatedList.get(i);

                    instrumentor =
                            InstrumentorFactory.
                            createNetworkConnectionInstrumentor(orbName, clusterName, totalInstrumentor,
                                                                calcInstrumentor);
                    if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.NETWORK_INSTRUMENTOR) )
                    {
                        Object[] argObj = new Object[3];
                        argObj[0] = clusterName;
                        argObj[1] = orbName;
                        argObj[2] = instrumentor.getName();

                        GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedNetworkConnectionInstrumentorsEvent - createNetworkConnectionInstrumentor",
                                                   GUILoggerINBusinessProperty.NETWORK_INSTRUMENTOR, argObj);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, instrumentorCache);
                }
            }
            else
            {
                Map<String, Instrumentor> newCache = holder.createNewInstrumentorCache();

                CalculatedNetworkConnectionInstrumentorMutable instrumentor = null;
                NetworkConnectionInstrumentor totalInstrumentor;
                CalculatedNetworkConnectionInstrumentor calcInstrumentor;

                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (NetworkConnectionInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedNetworkConnectionInstrumentor) calculatedList.get(i);

                    try
                    {
                        instrumentor = ( CalculatedNetworkConnectionInstrumentorMutable )
                                findCachedInstrumentor(totalInstrumentor, instrumentorCache);
                    }
                    catch( ClassCastException e )
                    {
                        GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedNetworkConnectionInstrumentorsEvent",
                                                       "Cached Instrumentor was not the expected type. Will create new one.", e);
                    }

                    if( instrumentor == null )
                    {
                        instrumentor =
                                ( CalculatedNetworkConnectionInstrumentorMutable ) InstrumentorFactory.
                                createNetworkConnectionInstrumentor(orbName, clusterName, totalInstrumentor,
                                                                    calcInstrumentor);

                    }
                    else
                    {
                        instrumentor.setData(orbName, clusterName, totalInstrumentor);
                        instrumentor.setCalculatedData(calcInstrumentor);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, newCache);
                }

                holder.getInstrumentorCache().clear();
                holder.setInstrumentorCache(newCache);
            }
            //String orbSubscriptionKey = orbName + InstrumentorTypes.NETWORK_CONNECTION;
            String orbSubscriptionKey = InstrumentorTypes.NETWORK_CONNECTION_STRING;
            InstrumentorCollectorEvent instrumentorCollectorEvent = createEvent(clusterName, orbName, eventList);
            dispatchEvent(instrumentorCollectorEvent, orbSubscriptionKey);
        }
    }

    @SuppressWarnings({"OverlyLongMethod", "CollectionDeclaredAsConcreteClass",
            "RawUseOfParameterizedType"})
    public void acceptCalculatedQueueInstrumentorsEvent(long eventTimeMillis, ArrayList totalList,
                                                        ArrayList calculatedList, String clusterName,
                                                        String orbName)
    {
        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.QUEUE_INSTRUMENTOR) )
        {
            Object[] argObj = new Object[3];
            argObj[0] = clusterName;
            argObj[1] = orbName;
            argObj[2] = "instrumentors.size=" + totalList.size();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedQueueInstrumentorsEvent",
                                       GUILoggerINBusinessProperty.QUEUE_INSTRUMENTOR, argObj);
        }

        CacheHolder holder;
        synchronized( queueLockObject )
        {
            holder = findTypeOrbNameHolder(orbName, InstrumentorCollector.QUEUE_INSTRUMENTOR_KEY);
        }

        synchronized( holder )
        {
            Map<String, Instrumentor> instrumentorCache = holder.getInstrumentorCache();

            List<Instrumentor> eventList = new ArrayList<Instrumentor>(totalList.size());
            Instrumentor largestCurrentSizeInstrumentor = null;
            long largestCurrentSize = -1;

            if( instrumentorCache.isEmpty() )
            {
                Instrumentor instrumentor;
                QueueInstrumentor totalInstrumentor;
                CalculatedQueueInstrumentor calcInstrumentor;
                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (QueueInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedQueueInstrumentor) calculatedList.get(i);
                    instrumentor =
                            InstrumentorFactory.
                            createQueueInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);

                    if(totalInstrumentor.getCurrentSize() > largestCurrentSize)
                    {
                        largestCurrentSizeInstrumentor = instrumentor;
                        largestCurrentSize = totalInstrumentor.getCurrentSize();
                    }

                    if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.QUEUE_INSTRUMENTOR) )
                    {
                        Object[] argObj = new Object[3];
                        argObj[0] = clusterName;
                        argObj[1] = orbName;
                        argObj[2] = instrumentor.getName();

                        GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedQueueInstrumentorsEvent - createQueueInstrumentor",
                                                   GUILoggerINBusinessProperty.QUEUE_INSTRUMENTOR, argObj);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, instrumentorCache);
                }
            }
            else
            {
                Map<String, Instrumentor> newCache = holder.createNewInstrumentorCache();

                CalculatedQueueInstrumentorMutable instrumentor = null;
                QueueInstrumentor totalInstrumentor;
                CalculatedQueueInstrumentor calcInstrumentor;

                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (QueueInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedQueueInstrumentor) calculatedList.get(i);

                    try
                    {
                        instrumentor = ( CalculatedQueueInstrumentorMutable ) findCachedInstrumentor(
                                totalInstrumentor,
                                                                                                     instrumentorCache);
                    }
                    catch( ClassCastException e )
                    {
                        GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedQueueInstrumentorsEvent",
                                                       "Cached Instrumentor was not the expected type. Will create new one.", e);
                    }

                    if( instrumentor == null )
                    {
                        instrumentor =
                                ( CalculatedQueueInstrumentorMutable ) InstrumentorFactory.
                                createQueueInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    }
                    else
                    {
                        instrumentor.setData(orbName, clusterName, totalInstrumentor);
                        instrumentor.setCalculatedData(calcInstrumentor);
                    }

                    if(totalInstrumentor.getCurrentSize()>largestCurrentSize)
                    {
                        largestCurrentSizeInstrumentor = instrumentor;
                        largestCurrentSize = totalInstrumentor.getCurrentSize();
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, newCache);
                }

                holder.getInstrumentorCache().clear();
                holder.setInstrumentorCache(newCache);
            }
            //String orbSubscriptionKey = orbName + InstrumentorTypes.QUEUE;
            String orbSubscriptionKey = InstrumentorTypes.QUEUE_STRING;
            InstrumentorCollectorEvent instrumentorCollectorEvent = createEvent(clusterName, orbName, eventList);
            dispatchEvent(instrumentorCollectorEvent, orbSubscriptionKey);

            if(largestCurrentSizeInstrumentor != null)
            {
                ChannelKey channelKey =
                        new ChannelKey(ChannelType.INSTRUMENTOR_LARGEST_QUEUE_UPDATE, 0);
                ChannelEvent event =
                        EventChannelAdapterFactory.find().getChannelEvent(this, channelKey,
                                                                          largestCurrentSizeInstrumentor);
                //noinspection NonPrivateFieldAccessedInSynchronizedContext
                eventChannel.dispatch(event);
            }
        }
    }

    @SuppressWarnings({"OverlyLongMethod", "CollectionDeclaredAsConcreteClass",
            "RawUseOfParameterizedType"})
    public void acceptCalculatedThreadPoolInstrumentorsEvent(long eventTimeMillis, ArrayList totalList,
                                                             ArrayList calculatedList,
                                                             String clusterName,
                                                             String orbName)
    {
        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.THREAD_INSTRUMENTOR) )
        {
            Object[] argObj = new Object[3];
            argObj[0] = clusterName;
            argObj[1] = orbName;
            argObj[2] = "instrumentors.size=" + totalList.size();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedThreadPoolInstrumentorsEvent",
                                       GUILoggerINBusinessProperty.THREAD_INSTRUMENTOR, argObj);
        }

        CacheHolder holder;
        synchronized( threadLockObject )
        {
            holder = findTypeOrbNameHolder(orbName, InstrumentorCollector.THREAD_INSTRUMENTOR_KEY);
        }

        synchronized( holder )
        {
            Map<String, Instrumentor> instrumentorCache = holder.getInstrumentorCache();

            List<Instrumentor> eventList = new ArrayList<Instrumentor>(totalList.size());

            if( instrumentorCache.isEmpty() )
            {
                Instrumentor instrumentor;
                ThreadPoolInstrumentor totalInstrumentor;
                CalculatedThreadPoolInstrumentor calcInstrumentor;
                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (ThreadPoolInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedThreadPoolInstrumentor) calculatedList.get(i);
                    instrumentor =
                            InstrumentorFactory.createThreadPoolInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.THREAD_INSTRUMENTOR) )
                    {
                        Object[] argObj = new Object[3];
                        argObj[0] = clusterName;
                        argObj[1] = orbName;
                        argObj[2] = instrumentor.getName();

                        GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedThreadPoolInstrumentorsEvent - createThreadPoolInstrumentor",
                                                   GUILoggerINBusinessProperty.THREAD_INSTRUMENTOR, argObj);
                    }


                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, instrumentorCache);
                }
            }
            else
            {
                Map<String, Instrumentor> newCache = holder.createNewInstrumentorCache();

                CalculatedThreadPoolInstrumentorMutable instrumentor = null;
                ThreadPoolInstrumentor totalInstrumentor;
                CalculatedThreadPoolInstrumentor calcInstrumentor;
                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (ThreadPoolInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedThreadPoolInstrumentor) calculatedList.get(i);

                    try
                    {
                        instrumentor = (CalculatedThreadPoolInstrumentorMutable) findCachedInstrumentor(totalInstrumentor, instrumentorCache);
                    }
                    catch( ClassCastException e )
                    {
                        GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedThreadPoolInstrumentorsEvent",
                                                       "Cached Instrumentor was not the expected type. Will create new one.", e);
                    }

                    if( instrumentor == null )
                    {
                        instrumentor =
                                ( CalculatedThreadPoolInstrumentorMutable ) InstrumentorFactory.
                                createThreadPoolInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    }
                    else
                    {
                        instrumentor.setData(orbName, clusterName, totalInstrumentor);
                        instrumentor.setCalculatedData(calcInstrumentor);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, newCache);
                }

                holder.getInstrumentorCache().clear();
                holder.setInstrumentorCache(newCache);
            }
            //String orbSubscriptionKey = orbName + InstrumentorTypes.THREAD;
            String orbSubscriptionKey = InstrumentorTypes.THREAD_STRING;
            InstrumentorCollectorEvent instrumentorCollectorEvent = createEvent(clusterName, orbName, eventList);
            dispatchEvent(instrumentorCollectorEvent, orbSubscriptionKey);
        }
    }

    public void acceptCalculatedJmxInstrumentorsEvent(long eventTimeMillis, ArrayList totalList, ArrayList calculatedList,
                                                      String clusterName, String orbName)
    {
        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.JMX_INSTRUMENTOR) )
        {
            Object[] argObj = new Object[3];
            argObj[0] = clusterName;
            argObj[1] = orbName;
            argObj[2] = "instrumentors.size=" + totalList.size();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedJmxInstrumentorsEvent",
                                       GUILoggerINBusinessProperty.JMX_INSTRUMENTOR, argObj);
        }

        CacheHolder holder;
        synchronized( jmxLockObject )
        {
            holder = findTypeOrbNameHolder(orbName, InstrumentorCollector.JMX_INSTRUMENTOR_KEY);
        }

        synchronized( holder )
        {
            Map<String, Instrumentor> instrumentorCache = holder.getInstrumentorCache();

            List<Instrumentor> eventList = new ArrayList<Instrumentor>(totalList.size());

            if( instrumentorCache.isEmpty() )
            {
                Instrumentor instrumentor;
                JmxInstrumentor totalInstrumentor;
                CalculatedJmxInstrumentor calcInstrumentor;
                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (JmxInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedJmxInstrumentor) calculatedList.get(i);
                    instrumentor =
                            InstrumentorFactory.createJmxInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.JMX_INSTRUMENTOR) )
                    {
                        Object[] argObj = new Object[3];
                        argObj[0] = clusterName;
                        argObj[1] = orbName;
                        argObj[2] = instrumentor.getName();

                        GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedJmxInstrumentorsEvent - createJmxInstrumentor",
                                                   GUILoggerINBusinessProperty.JMX_INSTRUMENTOR, argObj);
                    }


                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, instrumentorCache);
                }
            }
            else
            {
                Map<String, Instrumentor> newCache = holder.createNewInstrumentorCache();

                CalculatedJmxInstrumentorMutable instrumentor = null;
                JmxInstrumentor totalInstrumentor;
                CalculatedJmxInstrumentor calcInstrumentor;
                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (JmxInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedJmxInstrumentor) calculatedList.get(i);

                    try
                    {
                        instrumentor = (CalculatedJmxInstrumentorMutable) findCachedInstrumentor(totalInstrumentor, instrumentorCache);
                    }
                    catch( ClassCastException e )
                    {
                        GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedJmxInstrumentorsEvent",
                                                       "Cached Instrumentor was not the expected type. Will create new one.", e);
                    }

                    if( instrumentor == null )
                    {
                        instrumentor =
                                ( CalculatedJmxInstrumentorMutable ) InstrumentorFactory.
                                createJmxInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    }
                    else
                    {
                        instrumentor.setData(orbName, clusterName, totalInstrumentor);
                        instrumentor.setCalculatedData(calcInstrumentor);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, newCache);
                }

                holder.getInstrumentorCache().clear();
                holder.setInstrumentorCache(newCache);
            }
            //String orbSubscriptionKey = orbName + InstrumentorTypes.THREAD;
            String orbSubscriptionKey = InstrumentorTypes.JMX_STRING;
            InstrumentorCollectorEvent instrumentorCollectorEvent = createEvent(clusterName, orbName, eventList);
            dispatchEvent(instrumentorCollectorEvent, orbSubscriptionKey);
        }
    }

    public void acceptCalculatedJstatInstrumentorsEvent(long eventTimeMillis, ArrayList totalList, ArrayList calculatedList,
                                                        String clusterName, String orbName) {
       if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.JSTAT_INSTRUMENTOR) )
        {
            Object[] argObj = new Object[3];
            argObj[0] = clusterName;
            argObj[1] = orbName;
            argObj[2] = "instrumentors.size=" + totalList.size();

            GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedJstatInstrumentorsEvent",
                                       GUILoggerINBusinessProperty.JSTAT_INSTRUMENTOR, argObj);
        }

        CacheHolder holder;
        synchronized( jstatLockObject )
        {
            holder = findTypeOrbNameHolder(orbName, InstrumentorCollector.JSTAT_INSTRUMENTOR_KEY);
        }

        synchronized( holder )
        {
            Map<String, Instrumentor> instrumentorCache = holder.getInstrumentorCache();

            List<Instrumentor> eventList = new ArrayList<Instrumentor>(totalList.size());

            if( instrumentorCache.isEmpty() )
            {
                Instrumentor instrumentor;
                JstatInstrumentor totalInstrumentor;
                CalculatedJstatInstrumentor calcInstrumentor;
                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (JstatInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedJstatInstrumentor) calculatedList.get(i);
                    instrumentor =   
                            InstrumentorFactory.createJstatInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.JSTAT_INSTRUMENTOR) )
                    {
                        Object[] argObj = new Object[3];
                        argObj[0] = clusterName;
                        argObj[1] = orbName;
                        argObj[2] = instrumentor.getName();

                        GUILoggerHome.find().debug(CATEGORY + ": acceptCalculatedJstatInstrumentorsEvent - createJstatInstrumentor",
                                                   GUILoggerINBusinessProperty.JSTAT_INSTRUMENTOR, argObj);
                    }


                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, instrumentorCache);
                }
            }
            else
            {
                Map<String, Instrumentor> newCache = holder.createNewInstrumentorCache();

                CalculatedJstatInstrumentorMutable instrumentor = null;
                JstatInstrumentor totalInstrumentor;
                CalculatedJstatInstrumentor calcInstrumentor;
                int size = totalList.size();
                for( int i = 0; i < size; i++ )
                {
                    totalInstrumentor = (JstatInstrumentor) totalList.get(i);
                    calcInstrumentor = (CalculatedJstatInstrumentor) calculatedList.get(i);

                    try
                    {
                        instrumentor = (CalculatedJstatInstrumentorMutable) findCachedInstrumentor(totalInstrumentor, instrumentorCache);
                    }
                    catch( ClassCastException e )
                    {
                        GUILoggerHome.find().exception(CATEGORY + ": acceptCalculatedJstatInstrumentorsEvent",
                                                       "Cached Instrumentor was not the expected type. Will create new one.", e);
                    }

                    if( instrumentor == null )
                    {
                        instrumentor =
                                (CalculatedJstatInstrumentorMutable) InstrumentorFactory.
                                createJstatInstrumentor(orbName, clusterName, totalInstrumentor, calcInstrumentor);
                    }
                    else
                    {
                        instrumentor.setData(orbName, clusterName, totalInstrumentor);
                        instrumentor.setCalculatedData(calcInstrumentor);
                    }

                    eventList.add(instrumentor);
                    addCachedInstrumentor(instrumentor, newCache);
                }

                holder.getInstrumentorCache().clear();
                holder.setInstrumentorCache(newCache);
            }
            //String orbSubscriptionKey = orbName + InstrumentorTypes.THREAD;
            String orbSubscriptionKey = InstrumentorTypes.JSTAT_STRING;
            InstrumentorCollectorEvent instrumentorCollectorEvent = createEvent(clusterName, orbName, eventList);
            dispatchEvent(instrumentorCollectorEvent, orbSubscriptionKey);
        }
    }

    private void dispatchEvent(InstrumentorCollectorEvent eventObject, String key)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_BLOCK_UPDATE, key + subKey);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey,
                                                                               eventObject);
        eventChannel.dispatch(event);
    }

    private InstrumentorCollectorEvent createEvent(String clusterName, String orbName,
                                                   List<Instrumentor> eventList)
    {
        return new InstrumentorCollectorEventImpl(clusterName, orbName, eventList);
    }

    private Instrumentor findCachedInstrumentor(
            com.cboe.instrumentationService.instrumentors.Instrumentor instrumentor,
                                                Map<String, Instrumentor> cache)
    {
        return cache.get(instrumentor.getName());
    }

    private void addCachedInstrumentor(Instrumentor instrumentor, Map<String, Instrumentor> cache)
    {
        cache.put(instrumentor.getName(), instrumentor);
    }

    private CacheHolder findTypeOrbNameHolder(String orbName, String type)
    {
        CacheHolder holder;
        // the returned map includes all the orbs for the requested type
        Map<String, CacheHolder> orbNameMap = findOrbNameMap(type);
        holder = orbNameMap.get(orbName);
        if( holder == null)
        {
            holder = new CacheHolder(instrumentorInitialCacheSize);
            orbNameMap.put(orbName, holder);
        }
        return holder;
    }

    private Map<String, CacheHolder> findOrbNameMap(String type)
    {
        Map<String, CacheHolder> typeMap = typeCache.get(type);
        if(typeMap == null)
        {
            typeMap = new HashMap<String, CacheHolder>(clusterOrbNameInitialCacheSize);
            typeCache.put(type, typeMap);
        }
        return typeMap;
    }

    private class CacheHolder
    {
        private Map<String, Instrumentor> instrumentorCache;
        private int cacheSize;

        CacheHolder()
        {
            this(instrumentorInitialCacheSize);
        }

        CacheHolder(int cacheSize)
        {
            this.cacheSize = cacheSize;
            initialize();
        }

        public int getCacheSize()
        {
            return cacheSize;
        }

        @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
        public Map<String, Instrumentor> getInstrumentorCache()
        {
            return instrumentorCache;
        }

        @SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter"})
        public void setInstrumentorCache(Map<String, Instrumentor> instrumentorCache)
        {
            this.instrumentorCache = instrumentorCache;
        }

        public Map<String, Instrumentor> createNewInstrumentorCache()
        {
            return new SoftHashMap(cacheSize);
        }

        private void initialize()
        {
            instrumentorCache = createNewInstrumentorCache();
        }
    }
}