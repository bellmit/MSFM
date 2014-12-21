//
// -----------------------------------------------------------------------------------
// Source file: InstrumentorCollectorImpl.java
//
// PACKAGE: com.cboe.presentation.collector;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.collector;

import com.cboe.interfaces.instrumentation.InstrumentorTypes;
import com.cboe.interfaces.instrumentation.collector.InstrumentorCollector;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.environment.EnvironmentManagerFactory;

import com.cboe.common.utils.InvalidMatchList;
import com.cboe.instrumentationService.calculator.InstrumentorCalculatedEventBlockListener;
import com.cboe.instrumentationService.calculator.InstrumentorCalculatedEventListener;
//import com.cboe.instrumentationService.collection.InstrumentorECCollector;
import com.cboe.instrumentationService.distribution.InstrumentorMonitorECInitException;
import com.cboe.instrumentationService.monitor.InstrumentorMonitorDefaults;

/**
 *  Instrument Collector implementation
 */
public class InstrumentorCollectorImpl implements InstrumentorCollector
{
    private static final String CATEGORY = InstrumentorCollectorImpl.class.getName();

    public static final String SYSTEM_HEALTH_PROPERTY_SECTION = "SystemHealthMonitor";
    public static final String BLOCK_UPDATES_PROPERTY_KEY = "InstrumentorBlockUpdates";
    public static final String COLLECTOR_PROCESS_SUMMARY_CHANNEL = "InstrumentationSummary";
    public static final String COLLECTOR_PROCESS_DETAILS_CHANNEL = "InstrumentationDetails";
    public static final String RAW_INSTRUMENTOR_CHANNEL = "InstrumentationChannel";

    public static final String INSTRUMENTATION_SUMMARY = "Summary";
    public static final String INSTRUMENTATION_DETAIL = "Detail";

    public static final boolean DEFAULT_DO_BLOCK_UPDATES = false;

    public static final short DEFAULT_SAMPLE_FACTOR = 1;

    private boolean doBlockUpdates;
    protected String prefix;

    private InstrumentorECCollector ecCollectorSummary;
    private InstrumentorECCollector ecCollectorDetail;
    private InstrumentorDiscoveryExplorer magellan;

    protected boolean collectorProcessAttached;

    public InstrumentorCollectorImpl() throws InstrumentorMonitorECInitException
    {
        doBlockUpdates = DEFAULT_DO_BLOCK_UPDATES;
        if( AppPropertiesFileFactory.isAppPropertiesAvailable() )
        {
            String value =
                    AppPropertiesFileFactory.find().getValue(SYSTEM_HEALTH_PROPERTY_SECTION,
                                                             BLOCK_UPDATES_PROPERTY_KEY);
            if( value != null && value.length() > 0 )
            {
                doBlockUpdates = Boolean.valueOf(value);
            }
        }
        initializeCollector();
    }

    public void subscribeAllOrbsForSummary()
    {
        if (collectorProcessAttached)
        {
            try
            {
                ecCollectorSummary.registerCountInstrumentorMonitor( null );
                ecCollectorSummary.registerEventChannelInstrumentorMonitor( null );
                ecCollectorSummary.registerHeapInstrumentorMonitor( null );
                ecCollectorSummary.registerMethodInstrumentorMonitor( null );
                ecCollectorSummary.registerNetworkConnectionInstrumentorMonitor( null );
                ecCollectorSummary.registerQueueInstrumentorMonitor( null );
                ecCollectorSummary.registerThreadPoolInstrumentorMonitor( null );
//                ecCollectorSummary.registerJmxInstrumentorMonitor( null );
//                ecCollectorSummary.registerJstatInstrumentorMonitor( null );
                ecCollectorSummary.registerKeyValueInstrumentorMonitor(null);
            }
            catch (InvalidMatchList iml)
            {
                DefaultExceptionHandlerHome.find().process(iml, "Failed to initialize Instrumentor Collector");
            }
        }
        else
        {
            try
            {
                ecCollectorSummary.registerHeapInstrumentorMonitor( null );
//                ecCollectorSummary.registerJmxInstrumentorMonitor(null);
//                ecCollectorSummary.registerJstatInstrumentorMonitor(null);
                ecCollectorSummary.registerKeyValueInstrumentorMonitor(null);
            }
            catch (InvalidMatchList iml)
            {
                DefaultExceptionHandlerHome.find().process(iml, "Failed to initialize Instrumentor Collector");
            }

        }
    }

    public void unsubscribeAllOrbsForSummary()
    {
        if (collectorProcessAttached)
        {
            ecCollectorSummary.unregisterCountInstrumentorMonitor();
            ecCollectorSummary.unregisterEventChannelInstrumentorMonitor();
            ecCollectorSummary.unregisterHeapInstrumentorMonitor();
            ecCollectorSummary.unregisterMethodInstrumentorMonitor();
            ecCollectorSummary.unregisterNetworkConnectionInstrumentorMonitor();
            ecCollectorSummary.unregisterQueueInstrumentorMonitor();
            ecCollectorSummary.unregisterThreadPoolInstrumentorMonitor();
//            ecCollectorSummary.unregisterJmxInstrumentorMonitor();
//            ecCollectorSummary.unregisterJstatInstrumentorMonitor();
            ecCollectorSummary.unregisterKeyValueInstrumentorMonitor();
        }
        else
        {
            ecCollectorSummary.unregisterHeapInstrumentorMonitor();
//            ecCollectorSummary.unregisterJmxInstrumentorMonitor();
//            ecCollectorSummary.unregisterJstatInstrumentorMonitor();
            ecCollectorSummary.unregisterKeyValueInstrumentorMonitor();
        }
    }

    /**
     *  This method unsubscribes from everything on the collectors and removes the collectors
     *  from the list of collectors.
     */
    public void unsubscribeAllInstrumentors()
    {
        if (collectorProcessAttached)
        {
            ecCollectorSummary = null;
            ecCollectorDetail = null;
        }
        else
        {
            unsubscribeAllInstrumentors(ecCollectorSummary);
            ecCollectorSummary = null;
            ecCollectorDetail = null;
        }
    }
    protected void unsubscribeAllInstrumentors(InstrumentorECCollector ecCollector)
    {
        if (ecCollector != null)
        {
            ecCollector.unregisterCountInstrumentorMonitor( );
            ecCollector.unregisterEventChannelInstrumentorMonitor( );
            ecCollector.unregisterHeapInstrumentorMonitor( );
            ecCollector.unregisterMethodInstrumentorMonitor( );
            ecCollector.unregisterNetworkConnectionInstrumentorMonitor( );
            ecCollector.unregisterQueueInstrumentorMonitor( );
            ecCollector.unregisterThreadPoolInstrumentorMonitor( );
//            ecCollector.unregisterJmxInstrumentorMonitor();
//            ecCollector.unregisterJstatInstrumentorMonitor();
            ecCollector.unregisterKeyValueInstrumentorMonitor();
        }
    }

    /**
     * Initialize the instrumentation collector.  Try and discover if any collectors are up in a master
     * mode.  If so, initialize the collector channels and collectors.  If not, initialize the raw instrumentation
     * channel collector.
     *
     * @throws InstrumentorMonitorECInitException
     */
    private void initializeCollector() throws InstrumentorMonitorECInitException
    {
        boolean discovered = isAnyCollectorsPresent();

        if (discovered)
        {
            initializeCollectorCollector();
        }
        else
        {
            initializeRawCollector();
        }
    }

    /**
     * Attempt a discovery process for the collector processes.  Get the list of processes
     * from PW, looking for collector orb names.  Monitor the PW states to see if any collector
     * processes go to Master.  Timeout after a certain amount of time, if none of the end conditions
     * have been met.
     *
     * @return true if any collector is Up and Master as defined by PW
     */
    private boolean isAnyCollectorsPresent()
    {
        magellan = new InstrumentorDiscoveryExplorer();

        boolean found = magellan.initialDiscovery();

        return found;
    }

    /**
     * Initialize the collectors that will use the collector processes.
     */
    private void initializeCollectorCollector() throws InstrumentorMonitorECInitException
    {
        // set the flag for collector process processing
        collectorProcessAttached = true;
        // Setup two collectors, one for the detail, one for the summary/
        ecCollectorDetail = createCollector(COLLECTOR_PROCESS_DETAILS_CHANNEL, false);
        ecCollectorSummary = createCollector(COLLECTOR_PROCESS_SUMMARY_CHANNEL, true);
    }

    /**
     * Initialize the collector that will take the instrumentation data directly from the
     * raw event channel, skipping the collector processes.
     *
     * @throws InstrumentorMonitorECInitException
     */
    private void initializeRawCollector() throws InstrumentorMonitorECInitException
    {
        collectorProcessAttached = false;

        ecCollectorSummary = createRawCollector(INSTRUMENTATION_SUMMARY);
        ecCollectorSummary.setCalcToSampleFactor(DEFAULT_SAMPLE_FACTOR);

        ecCollectorDetail = createRawCollector(INSTRUMENTATION_DETAIL);
        ecCollectorDetail.setCalcToSampleFactor(DEFAULT_SAMPLE_FACTOR);
    }

    /**
     * Create a collector for the specified channel.  The environment prefix is prepended to the name.
     *
     * @param channelName  Channel name without the prefix prepended.
     * @throws InstrumentorMonitorECInitException
     */
    private InstrumentorECCollector createCollector(String channelName, boolean isSummary) throws InstrumentorMonitorECInitException
    {
        // Prepend the prefix
        String fullChannelName = prependPrefix(channelName);

        InstrumentorECCollector ecCollector;

        if(doBlockUpdates)
        {
            GUILoggerHome.find().information(CATEGORY, GUILoggerINBusinessProperty.INSTRUMENTATION,
                                             "Block Updates in effect.");
            
            InstrumentorCalculatedEventBlockListener listener;

            if (isSummary)
            {
            	listener = new InstrumentorCalculatedEventBlockListenerImpl(INSTRUMENTATION_SUMMARY);
            }
            else
            {
            	listener = new InstrumentorCalculatedEventBlockListenerImpl(INSTRUMENTATION_DETAIL);
            }

            ecCollector = new InstrumentorECCollector(fullChannelName,InstrumentorMonitorDefaults.MONITOR_NAME, false,
                    InstrumentorMonitorDefaults.SAMPLE_INTERVAL, DEFAULT_SAMPLE_FACTOR, listener);
        }
        else
        {
            GUILoggerHome.find().information(CATEGORY, GUILoggerINBusinessProperty.INSTRUMENTATION,
                                             "Individual Updates in effect.");
            InstrumentorCalculatedEventListener listener;
            
            if (isSummary)
            {
            	listener = new InstrumentorCalculatedEventListenerImpl(INSTRUMENTATION_SUMMARY);	
            }
            else
            {
            	listener = new InstrumentorCalculatedEventListenerImpl(INSTRUMENTATION_DETAIL);	
            }

            ecCollector = new InstrumentorECCollector(fullChannelName,InstrumentorMonitorDefaults.MONITOR_NAME, false,
                    InstrumentorMonitorDefaults.SAMPLE_INTERVAL, DEFAULT_SAMPLE_FACTOR, listener);
        }

        return ecCollector;
    }

    /**
     * Create a collector for the raw channel.
     */
    private InstrumentorECCollector createRawCollector(String summaryOrDetail)
            throws InstrumentorMonitorECInitException
    {
        InstrumentorECCollector ecCollector;

        if(doBlockUpdates)
        {
            GUILoggerHome.find().information(CATEGORY, GUILoggerINBusinessProperty.INSTRUMENTATION,
                                             "Block Updates in effect.");

            InstrumentorCalculatedEventBlockListener listener =
                    new InstrumentorCalculatedEventBlockListenerImpl(summaryOrDetail);

            ecCollector = new InstrumentorECCollector(listener);
        }
        else
        {
            GUILoggerHome.find().information(CATEGORY, GUILoggerINBusinessProperty.INSTRUMENTATION,
                                             "Individual Updates in effect.");

            InstrumentorCalculatedEventListener listener =
                    new InstrumentorCalculatedEventListenerImpl(summaryOrDetail);

            ecCollector = new InstrumentorECCollector(listener);
        }

        return ecCollector;
    }

    private String prependPrefix(String name)
    {
        String fullName = getPrefix() + name;
        return fullName;
    }

    @SuppressWarnings({"OverlyComplexMethod"})
    public void subscribeInstrumentorsForOrb(String orbName, short instrumentorType)
    {
        if (ecCollectorDetail == null)
        {
            throw new IllegalStateException("Instrumentor Collector is not initialized.");
        }
        if(orbName == null || orbName.length() == 0)
        {
            throw new IllegalArgumentException("Orb Name can not be NULL or zero length string");
        }
        switch(instrumentorType)
        {
            case InstrumentorTypes.HEAP:
                ecCollectorDetail.filterOrbHeapInstrumentor(orbName);
                break;
            case InstrumentorTypes.METHOD:
                ecCollectorDetail.filterOrbMethodInstrumentor(orbName);
                break;
            case InstrumentorTypes.NETWORK_CONNECTION:
                ecCollectorDetail.filterOrbNetworkConnectionInstrumentor(orbName);
                break;
            case InstrumentorTypes.QUEUE:
                ecCollectorDetail.filterOrbQueueInstrumentor(orbName);
                break;
            case InstrumentorTypes.THREAD:
                ecCollectorDetail.filterOrbThreadPoolInstrumentor(orbName);
                break;
            case InstrumentorTypes.COUNT:
                ecCollectorDetail.filterOrbCountInstrumentor(orbName);
                break;
            case InstrumentorTypes.EVENT:
                ecCollectorDetail.filterOrbEventChannelInstrumentor(orbName);
                break;
            case InstrumentorTypes.KEY_VALUE:
                ecCollectorDetail.filterOrbKeyValueInstrumentor(orbName);
                break;
//            case InstrumentorTypes.JMX:
//                ecCollectorDetail.filterOrbJmxInstrumentor(orbName);
//                break;
//            case InstrumentorTypes.JSTAT:
//                ecCollectorDetail.filterOrbJstatInstrumentor(orbName);
//                break;
            default:
                throw new IllegalArgumentException("Invalid InstrumentorType - "+instrumentorType);
        }
    }

    @SuppressWarnings({"OverlyComplexMethod"})
    public void unsubscribeInstrumentorsForOrb(String orbName, short instrumentorType)
    {
        if (ecCollectorDetail == null)
        {
            throw new IllegalStateException("Instrumentor Collector is not initialized.");
        }
        if (orbName == null || orbName.length() == 0)
        {
            throw new IllegalArgumentException("Orb Name can not be NULL or zero length string");
        }

        switch (instrumentorType)
        {
            case InstrumentorTypes.HEAP:
                ecCollectorDetail.unfilterOrbHeapInstrumentor(orbName);
                break;
            case InstrumentorTypes.METHOD:
                ecCollectorDetail.unfilterOrbMethodInstrumentor(orbName);
                break;
            case InstrumentorTypes.NETWORK_CONNECTION:
                ecCollectorDetail.unfilterOrbNetworkConnectionInstrumentor(orbName);
                break;
            case InstrumentorTypes.QUEUE:
                ecCollectorDetail.unfilterOrbQueueInstrumentor(orbName);
                break;
            case InstrumentorTypes.THREAD:
                ecCollectorDetail.unfilterOrbThreadPoolInstrumentor(orbName);
                break;
            case InstrumentorTypes.COUNT:
                ecCollectorDetail.unfilterOrbCountInstrumentor(orbName);
                break;
            case InstrumentorTypes.EVENT:
                ecCollectorDetail.unfilterOrbEventChannelInstrumentor(orbName);
                break;
            case InstrumentorTypes.KEY_VALUE:
                ecCollectorDetail.unfilterOrbKeyValueInstrumentor(orbName);
                break;
//            case InstrumentorTypes.JMX:
//                ecCollectorDetail.unfilterOrbJmxInstrumentor(orbName);
//                break;
//            case InstrumentorTypes.JSTAT:
//                ecCollectorDetail.unfilterOrbJstatInstrumentor(orbName);
//                break;
            default:
                throw new IllegalArgumentException("Invalid InstrumentorType - " + instrumentorType);
        }
    }

    public void subscribeAllOrbsByType(short instrumentorType)
    {
        if (ecCollectorDetail == null)
        {
            throw new IllegalStateException("Instrumentor Collector is not initialized.");
        }
        try
        {
            switch (instrumentorType)
            {
                case InstrumentorTypes.HEAP:
                    ecCollectorDetail.registerHeapInstrumentorMonitor(null);
                    break;
                case InstrumentorTypes.METHOD:
                    ecCollectorDetail.registerMethodInstrumentorMonitor(null);
                    break;
                case InstrumentorTypes.NETWORK_CONNECTION:
                    ecCollectorDetail.registerNetworkConnectionInstrumentorMonitor(null);
                    break;
                case InstrumentorTypes.QUEUE:
                    ecCollectorDetail.registerQueueInstrumentorMonitor(null);
                    break;
                case InstrumentorTypes.THREAD:
                    ecCollectorDetail.registerThreadPoolInstrumentorMonitor(null);
                    break;
                case InstrumentorTypes.COUNT:
                    ecCollectorDetail.registerCountInstrumentorMonitor(null);
                    break;
                case InstrumentorTypes.EVENT:
                    ecCollectorDetail.registerEventChannelInstrumentorMonitor(null);
                    break;
                case InstrumentorTypes.KEY_VALUE:
                    ecCollectorDetail.registerKeyValueInstrumentorMonitor(null);
                    break;
//                case InstrumentorTypes.JMX:
//                    ecCollectorDetail.registerJmxInstrumentorMonitor(null);
//                    break;
//                case InstrumentorTypes.JSTAT:
//                    ecCollectorDetail.registerJstatInstrumentorMonitor(null);
//                    break;
                default:
                    throw new IllegalArgumentException("Invalid InstrumentorType - " + instrumentorType);
            }
        }
        catch (InvalidMatchList iml)
        {
            DefaultExceptionHandlerHome.find().process(iml, "Failed to initialize Instrumentor Collector");
        }
    }

    public void unsubscribeAllOrbsByType(short instrumentorType)
    {
        if (ecCollectorDetail == null)
        {
            throw new IllegalStateException("Instrumentor Collector is not initialized.");
        }
        switch (instrumentorType)
        {
            case InstrumentorTypes.HEAP:
                ecCollectorDetail.unregisterHeapInstrumentorMonitor();
                break;
            case InstrumentorTypes.METHOD:
                ecCollectorDetail.unregisterMethodInstrumentorMonitor();
                break;
            case InstrumentorTypes.NETWORK_CONNECTION:
                ecCollectorDetail.unregisterNetworkConnectionInstrumentorMonitor();
                break;
            case InstrumentorTypes.QUEUE:
                ecCollectorDetail.unregisterQueueInstrumentorMonitor();
                break;
            case InstrumentorTypes.THREAD:
                ecCollectorDetail.unregisterThreadPoolInstrumentorMonitor();
                break;
            case InstrumentorTypes.COUNT:
                ecCollectorDetail.unregisterCountInstrumentorMonitor();
                break;
            case InstrumentorTypes.EVENT:
                ecCollectorDetail.unregisterEventChannelInstrumentorMonitor();
                break;
            case InstrumentorTypes.KEY_VALUE:
                ecCollectorDetail.unregisterKeyValueInstrumentorMonitor();
                break;
//            case InstrumentorTypes.JMX:
//                ecCollectorDetail.unregisterJmxInstrumentorMonitor();
//                break;
//            case InstrumentorTypes.JSTAT:
//                ecCollectorDetail.unregisterJstatInstrumentorMonitor();
//                break;
            default:
                throw new IllegalArgumentException("Invalid InstrumentorType - " + instrumentorType);
        }
    }

    /**
     * Get the system prefix for this environment.
     * @return environment prefix
     */
    private String getPrefix()
    {
        if (prefix == null)
        {
            prefix = EnvironmentManagerFactory.find().getCurrentEnvironment().getSBTPrefix();
        }
        return prefix;
    }

    public boolean isCollectorProcessAttached()
    {
        return collectorProcessAttached;
    }

    public String[] getCollectorOrbNames()
    {
        String[] orbNames;

        orbNames = magellan.getCollectorProcessNames();
        
        return orbNames;
    }
}

