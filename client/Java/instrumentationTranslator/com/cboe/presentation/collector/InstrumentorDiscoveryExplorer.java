package com.cboe.presentation.collector;

import java.util.*;

import com.cboe.interfaces.instrumentation.Status;
import com.cboe.interfaces.instrumentation.api.InstrumentationMonitorAPI;
import com.cboe.interfaces.presentation.processes.ProcessInfo;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.common.processes.ProcessPattern;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.environment.EnvironmentManagerFactory;
import com.cboe.presentation.statusMonitor.CollectorProcessStatusImpl;

/**
 * Discover the Instrumentor processes and keep track of their status.
 */
public class InstrumentorDiscoveryExplorer implements EventChannelListener
{
    private static final int DEFAULT_DISCOVERY_TIME = 20;
    private final static String PROPERTY_SECTION = "SystemHealthMonitor";
    private static final String DISCOVERY_TIME_PROPERTY = "DiscoveryTimeout";
    private static final String FORCE_RAW_CHANNEL_PROPERTY = "ForceRawChannelInstrumentors";

    protected EventChannelAdapter eventChannel;
    protected Map<String, ProcessInfo> collectorMap;
    protected Integer updateKey;

    public InstrumentorDiscoveryExplorer()
    {
        initialize();
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();

        if (channelKey.channelType == ChannelType.INSTRUMENTATION_PROCESS_INFO_UPDATE)
        {
            ProcessInfo processInfo = (ProcessInfo) event.getEventData();
            processProcessInfoUpdate(processInfo);
        }
    }

    public void initialize()
    {
        collectorMap = new HashMap<String, ProcessInfo>();
        eventChannel = EventChannelAdapterFactory.find();
        updateKey = new Integer(0);
        initListeners();
    }

    public void initListeners()
    {
        InstrumentationMonitorAPI api = InstrumentationTranslatorFactory.find();
        // Subscribe for PW events
        api.subscribeAllProcessInfos(this);
    }

    /**
     * Tries to discover a collector process.
     *
     * @return true if a collector is discovered in the first 20 seconds
     */
    public boolean initialDiscovery()
    {
        boolean discovered = false;

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("InstrumentorDiscoveryExplorer::initialDiscovery",
                    GUILoggerINBusinessProperty.INSTRUMENTATION," discovering collector");
        }
        // Check for override
        String override = AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION, FORCE_RAW_CHANNEL_PROPERTY);
        if ((override != null) && (override.equalsIgnoreCase("true")))
        {
            GUILoggerHome.find().alarm("Forced to use InstrumentationChannel Instrumentors");
            return false;
        }

        // Get the timeout
        int timeout = getIntProperty(DEFAULT_DISCOVERY_TIME,DISCOVERY_TIME_PROPERTY);
        // Double the timeout to get half seconds
        timeout *= 2;


        // Check to see if a collector is MASTER
        if (isCollectorProcessRunning())
        {
            discovered = true;
        }
        else
        {
            for (int i=0;i < timeout;i++)
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException tie)
                {
                    DefaultExceptionHandlerHome.find().process(tie, "Trouble discovering collector");
                }
                discovered = isCollectorProcessRunning();
                if (discovered)
                {
                    break;
                }
            }
        }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("InstrumentorDiscoveryExplorer::initialDiscovery",
                    GUILoggerINBusinessProperty.INSTRUMENTATION," discovered = " + discovered);
        }
        return discovered;
    }

    /**
     * Check the list of collectors to see if any are in a state of MASTER.
     * @return true if any collector is running
     */
    public boolean isCollectorProcessRunning()
    {
        synchronized(collectorMap)
        {
            Iterator<ProcessInfo> iterator = collectorMap.values().iterator();
            while (iterator.hasNext())
            {
                ProcessInfo processInfo = iterator.next();
                if (processInfo.getMasterSlaveStatus() == Status.MASTER)
                {
                    return true;
                }
            }

        }
        return false;
    }

    protected void processProcessInfoUpdate(ProcessInfo processInfo)
    {
        if (isCollectorProcess(processInfo.getOrbName()))
        {
            boolean fireEvent = false;
            try
            {
                ProcessInfo ourProcessInfo = (ProcessInfo) processInfo.clone();
                ProcessInfo oldProcessInfo;
                synchronized(collectorMap)
                {
                    oldProcessInfo = collectorMap.put(ourProcessInfo.getOrbName(),ourProcessInfo);
                }
                if (oldProcessInfo != null)
                {
                    // Check if the state changed
                    if (oldProcessInfo.getOnlinePoaStatusCombo() != ourProcessInfo.getOnlinePoaStatusCombo())
                    {
                        if (GUILoggerHome.find().isDebugOn())
                        {
                            GUILoggerHome.find().debug("InstrumentorDiscoveryExplorer::processProcessInfoUpdate",
                                    GUILoggerINBusinessProperty.INSTRUMENTATION,
                                    "State change for " + ourProcessInfo.getOrbName());
                        }
                        // Fire an event, if someone cares
                        fireEvent = true;
                    }
                }
                else
                {
                    fireEvent = true;
                }
            }
            catch (CloneNotSupportedException cnse)
            {
                // Can't happen, well, better not
            }
            if (fireEvent)
            {
                fireUpdateEvent();
            }

        }
    }

    /**
     * Fire an event to the IEC that the collector process state has changed
     */
    protected void fireUpdateEvent()
    {
        ProcessInfo[] processInfos = getCollectorProcesses();
        CollectorProcessStatusImpl status = new CollectorProcessStatusImpl(processInfos);
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_COLLECTOR_STATUS_UPDATE, updateKey);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, status);
        eventChannel.dispatch(event);
    }

    protected ProcessInfo[] getCollectorProcesses()
    {
        ProcessInfo[] processInfos;
        int i = 0;

        synchronized (collectorMap)
        {
            processInfos = new ProcessInfo[collectorMap.size()];
            Iterator<ProcessInfo> processIterator = collectorMap.values().iterator();
            while (processIterator.hasNext())
            {
                processInfos[i++] = processIterator.next();
            }
        }
        return processInfos;
    }

    public String[] getCollectorProcessNames()
    {
        String[] orbNames;
        int i = 0;
        synchronized (collectorMap)
        {
            orbNames = new String[collectorMap.size()];
            Iterator<ProcessInfo> processIterator = collectorMap.values().iterator();
            while (processIterator.hasNext())
            {
                orbNames[i++] = processIterator.next().getOrbName();
            }
        }
        return orbNames;
    }

    protected boolean isCollectorProcess(String orbName)
    {
        boolean isCollector = false;
        String prefix = EnvironmentManagerFactory.find().getCurrentEnvironment().getSBTPrefix();
        String icsPattern = ProcessPattern.getInstance().getICSPattern(prefix,"");

        if( orbName.matches( icsPattern))
        {
            // this is a FrontEnd
            isCollector = true;
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("InstrumentorDiscoveryExplorer::isCollectorProcess",
                        GUILoggerINBusinessProperty.INSTRUMENTATION," found Collector Process = " + orbName);
            }

        }

        return isCollector;
    }

    private int getIntProperty(int defaultSize, String propertyKey)
    {
        int initialMapSize = defaultSize;

        String initialPropertyValue = AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION, propertyKey);
        try
        {
            if (initialPropertyValue != null && !initialPropertyValue.equals(""))
            {
                initialMapSize = Integer.parseInt(initialPropertyValue);
            }
        }
        catch (NumberFormatException nfe)
        {
            GUILoggerHome.find().exception("InstrumentorDiscoveryExplorer::getInitialMapSize", nfe);
        }
        return initialMapSize;
    }
}
