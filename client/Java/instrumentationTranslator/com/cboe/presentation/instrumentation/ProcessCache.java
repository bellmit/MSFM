//
// -----------------------------------------------------------------------------------
// Source file: ProcessCache.java
//
// PACKAGE: com.cboe.interfaces.instrumentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import java.util.*;

import com.cboe.interfaces.casMonitor.CAS;
import com.cboe.interfaces.casMonitor.CASInformation;
import com.cboe.interfaces.instrumentation.CBOEProcessMutable;
import com.cboe.interfaces.instrumentation.HeapInstrumentor;
import com.cboe.interfaces.instrumentation.QueueInstrumentor;
import com.cboe.interfaces.instrumentation.Status;
import com.cboe.interfaces.instrumentation.api.InstrumentationMonitorAPI;
import com.cboe.interfaces.instrumentation.collector.InstrumentorCollectorEvent;
import com.cboe.interfaces.presentation.processes.CBOEProcess;
import com.cboe.interfaces.presentation.processes.OrbNameAlias;
import com.cboe.interfaces.presentation.processes.ProcessInfo;
import com.cboe.interfaces.presentation.processes.ProcessInfoTypes;
import com.cboe.interfaces.presentation.processes.LogicalName;
import com.cboe.interfaces.presentation.threading.GUIWorker;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.api.CASInformationCache;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.casMonitor.CASFactory;
import com.cboe.presentation.collector.InstrumentorCollectorImpl;
import com.cboe.presentation.common.formatters.InstrumentorTypes;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.orbNameAlias.OrbNameAliasCache;
import com.cboe.presentation.threading.APIWorkerImpl;
import com.cboe.presentation.threading.GUIWorkerImpl;

public class ProcessCache implements EventChannelListener
{
    private static String CATEGORY = "ProcessCache";
    private static final int MAX_SUBSCRIPTIONS_DEFAULT = 50;
    private static final String PROPERTY_SECTION = "SystemHealthMonitor";
    private static final String MAX_SUBSCRIPTIONS_PROPERTY_KEY = "MaxSubscriptions";

    protected Object mapLock;

    protected Map<String, CBOEProcessMutable> processMap;
    protected Map<String, CBOEProcessMutable> sacasCache;
    protected Map<String, CBOEProcessMutable> casCache;
    protected Map<String, CBOEProcessMutable> icsCache;
    protected Map<String, CBOEProcessMutable> subscribedMap;
    protected int maxSubscriptions;

    protected CASInformationCache casInformationCache;

    protected EventChannelAdapter eventChannel;

    private CBOEProcessMutable    icsManager;


    public ProcessCache(int initialHashSize)
    {
        initialize(initialHashSize);
    }

    public void channelUpdate(ChannelEvent event)
    {
        // Check the channel type to make sure it is an instrumentor update event.
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (channelKey.channelType == ChannelType.INSTRUMENTOR_UPDATE)
        {
            // Get the object from the event
            Object eventData = event.getEventData();
            if (eventData instanceof HeapInstrumentor)
            {
                HeapInstrumentor instrumentor = (HeapInstrumentor) eventData;
                // Put the instrumentor on the queue
                processHeapInstrumentorUpdate(instrumentor);
            }
        }
        else if(channelKey.channelType == ChannelType.INSTRUMENTOR_BLOCK_UPDATE)
        {
            String key = InstrumentorTypes.toString(InstrumentorTypes.HEAP) +
                    InstrumentorCollectorImpl.INSTRUMENTATION_SUMMARY;

            if(channelKey.key.equals(key))
            {
                InstrumentorCollectorEvent eventData =
                        (InstrumentorCollectorEvent) event.getEventData();

                List instrumentorBlock = eventData.getInstrumentors();
                if (instrumentorBlock.size() > 0)
                {
                    Object instrumentor = instrumentorBlock.get(0);
                    if ((instrumentor != null) && (instrumentor instanceof HeapInstrumentor))
                    {
                        HeapInstrumentor heapInstrumentor = (HeapInstrumentor) instrumentor;
                        // Put the instrumentor on the queue
                        processHeapInstrumentorUpdate(heapInstrumentor);
                    }
                }
            }
        }
        else if (channelKey.channelType == ChannelType.INSTRUMENTATION_PROCESS_INFO_UPDATE)
        {
            ProcessInfo processInfo = (ProcessInfo) event.getEventData();
            processProcessInfoUpdate(processInfo);
        }
        else if (channelKey.channelType == ChannelType.INSTRUMENTATION_ORBNAME_ALIAS_UPDATE)
        {
            OrbNameAlias alias = (OrbNameAlias) event.getEventData();
            processOrbNameAliasUpdate(alias);
        }
        else if ( channelKey.channelType == ChannelType.INSTRUMENTOR_LARGEST_QUEUE_UPDATE)
        {
            QueueInstrumentor queueInstrumentor = (QueueInstrumentor) event.getEventData();
            processLargestQueueUpdate(queueInstrumentor);
        }
        else if(channelKey.channelType == ChannelType.INSTRUMENTATION_LOGICAL_ORBNAME_UPDATE)
        {
            LogicalName name = (LogicalName) event.getEventData();
            processLogicalNameUpdate(name);
        }
        else if(channelKey.channelType == ChannelType.INSTRUMENTATION_LOGICAL_ORBNAME_DELETE)
        {
            LogicalName name = (LogicalName) event.getEventData();
            processLogicalNameDelete(name);
        }
        else
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(CATEGORY, GUILoggerINBusinessProperty.PROCESSES,"Received Channel Update for unkown channel " + channelKey.channelType);
            }
        }
    }

    protected void processLargestQueueUpdate(QueueInstrumentor queueInstrumentor)
    {
        CBOEProcessMutable process = (CBOEProcessMutable) getProcess(queueInstrumentor.getOrbName());
        if (process == null)
        {
            process = createProcess(queueInstrumentor.getOrbName());
            addProcess(process);
        }
        synchronized(process)
        {
            process.setLargestQueue(queueInstrumentor);
        }
        dispatchProcessEvent(process);
    }

    protected void processProcessInfoUpdate(ProcessInfo processInfo)
    {
        CBOEProcessMutable process = (CBOEProcessMutable) getProcess(processInfo.getOrbName());
        if (process == null)
        {
            process = createProcess(processInfo.getOrbName());
            process.setProcessInfo(processInfo);
            addProcess(process);
        }
        else
        {
            synchronized(process)
            {
                process.setProcessInfo(processInfo);

                // Check to see if ICS process has become the master; if so, get its mode
                if(process.isICS())
                {
                    if(process.getMasterSlaveStatus() == Status.MASTER &&
                       process.getProcessMode() == CBOEProcess.DEFAULT_PROCESS_MODE)
                    {
                        getProcessMode(process);
                    }
                }
            }
        }
        dispatchProcessEvent(process);
    }

    protected void processHeapInstrumentorUpdate(HeapInstrumentor heap)
    {
        CBOEProcessMutable process = (CBOEProcessMutable) getProcess(heap.getOrbName());
        if (process == null)
        {
            process = createProcess(heap.getOrbName());
            addProcess(process);
        }
        synchronized(process)
        {
            process.setHeapInstrumentor(heap);
        }
        dispatchProcessEvent(process);
    }

    protected void processOrbNameAliasUpdate(OrbNameAlias alias)
    {
        CBOEProcessMutable process = (CBOEProcessMutable) getProcess(alias.getOrbName());

        // We don't create a process node just for an orb name alias, if nothing else exists
        if (process != null)
        {
            synchronized(process)
            {
                process.setOrbNameAlias(alias);
            }
            dispatchProcessEvent(process);
        }
    }

    protected void processLogicalNameUpdate(LogicalName name)
    {
        CBOEProcessMutable process = (CBOEProcessMutable) getProcess(name.getOrbName());

        // We don't create a process node just for a logical orb name update, if nothing else exists
        if(process != null)
        {
            synchronized(process)
            {
                process.addLogicalName(name.getLogicalName());
            }
            dispatchProcessEvent(process);
        }
    }

    protected void processLogicalNameDelete(LogicalName name)
    {
        CBOEProcessMutable process = (CBOEProcessMutable) getProcess(name.getOrbName());

        // We don't create a process node just for a logical orb name update, if nothing else exists
        if(process != null)
        {
            synchronized(process)
            {
                process.removeLogicalName(name.getLogicalName());
            }
            dispatchProcessEvent(process);
        }
    }

    protected void initialize(int processMapInitialSize)
    {
        mapLock = new Object();
        processMap = new HashMap<String, CBOEProcessMutable>(processMapInitialSize);
        sacasCache = new TreeMap<String, CBOEProcessMutable>();
        casCache = new HashMap<String, CBOEProcessMutable>(processMapInitialSize);
        icsCache = new HashMap<String, CBOEProcessMutable>(processMapInitialSize);
        subscribedMap = new HashMap<String, CBOEProcessMutable>();

        eventChannel = EventChannelAdapterFactory.find();

        maxSubscriptions = getIntProperty(MAX_SUBSCRIPTIONS_DEFAULT, MAX_SUBSCRIPTIONS_PROPERTY_KEY);
    }

    public void initializeCache(InstrumentationMonitorAPI api)
    {
        initializeCASConfigurationInformation(api);
        initProcessCache(api);
        initListeners(api);
    }

    /*
     * Initialize process cache, make sure this is called after orb name alias
     * and CAS Configuration Information both are loaded
     */
    void initProcessCache(InstrumentationMonitorAPI api)
    {
        ProcessInfo[] processInfos = api.getAllProcessInfos(null);
        for (int i=0;i< processInfos.length;i++)
        {
            processProcessInfoUpdate(processInfos[i]);
        }

    }
    void initializeCASConfigurationInformation(InstrumentationMonitorAPI api)
    {
        casInformationCache = new CASInformationCache(api.getStorageManager().getRemoteStorage(),
                                                      api.getStorageManager().getLocalStorage());
        casInformationCache.initializeCache();
    }

    public void addProcess(CBOEProcessMutable process)
    {
        synchronized(mapLock)
        {
            processMap.put(process.getOrbName(), process);
        }

        if( process.getProcessType() == ProcessInfoTypes.SACAS_TYPE )
        {
            //it is an SACAS
            synchronized(mapLock)
            {
                if( !sacasCache.containsKey(process.getOrbName()) )
                {
                    sacasCache.put(process.getOrbName(), process);
                }
            }    
        }
        else if( process.isCAS() )
        {
            //it is an CAS
            CASInformation casInformation = null;
            if(casInformationCache != null)
            {
                casInformation = casInformationCache.getCASInformation(process);
            }
            if(casInformation != null)
            {
                CAS cas = CASFactory.createCASModel(process, casInformation);
                process.setCAS(cas);
            }
            else
            {
                CAS cas = CASFactory.createCASModel(process);
                process.setCAS(cas);
            }
            synchronized(mapLock)
            {
                if( !casCache.containsKey(process.getOrbName()) )
                {
                    casCache.put(process.getOrbName(), process);
                }
            }    
        }
        else if(process.isICS())
        {
            synchronized(mapLock)
            {
                icsCache.put(process.getOrbName(), process);
            }
            synchronized(process)
            {
                if (process.getMasterSlaveStatus() == Status.MASTER)
                {
                    getProcessMode(process);
                }
            }    
        }
    }

    public CBOEProcess getProcess(String orbName)
    {
        CBOEProcess process = null;
        synchronized(mapLock)
        {
            process = processMap.get(orbName);
        }
        return process;
    }

    public CBOEProcess[] getAllProcesses()
    {
        CBOEProcess[] processes = new CBOEProcess[0];
        synchronized(mapLock)
        {
            processes = processMap.values().toArray(processes);
        }
        return processes;
    }


    protected void dispatchProcessEvent(CBOEProcess process)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_UPDATE, process.getOrbName());
        ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKey, process);
        eventChannel.dispatch(channelEvent);

        channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_UPDATE, new Integer(0));
        channelEvent = eventChannel.getChannelEvent(this, channelKey, process);
        eventChannel.dispatch(channelEvent);

        // If it is a CAS, dispatch that event too
        if (process.isCAS())
        {
            channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_CAS_UPDATE, process.getOrbName());
            channelEvent = eventChannel.getChannelEvent(this, channelKey, process);
            eventChannel.dispatch(channelEvent);

            channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_CAS_UPDATE, new Integer(0));
            channelEvent = eventChannel.getChannelEvent(this, channelKey, process);
            eventChannel.dispatch(channelEvent);
        }
        else if(process.isICS())
        {
            channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_ICS_UPDATE, new Integer(0));
            channelEvent = eventChannel.getChannelEvent(this, channelKey, process);
            eventChannel.dispatch(channelEvent);
        }
    }

    protected void initListeners(InstrumentationMonitorAPI api)
    {
        api.subscribeOrbNameAlias(this);
        api.subscribeAllOrbsForSummary(this);
        api.subscribeAllProcessInfos(this);
        api.subscribeLargestQueue(this);
        api.subscribeLogicalOrbName(this);
    }

    protected void cleanUp()
    {
        InstrumentationMonitorAPI api = InstrumentationTranslatorFactory.find();

        api.unsubscribeOrbNameAlias(this);
        api.unsubscribeAllOrbsForSummary(this);
        api.unsubscribeAllProcessInfos(this);
        api.unsubscribeLargestQueue(this);
        api.unsubscribeLogicalOrbName(this);

        sacasCache = null;
        casCache = null;
        icsCache = null;
    }

    protected CBOEProcessMutable createProcess(String orbName)
    {
        CBOEProcessMutable process;

        process = CBOEProcessFactory.createCBOEProcessMutable(orbName);

        // Check for an orb name alias and attach it.
        OrbNameAlias alias = OrbNameAliasCache.getInstance().getOrbNameAlias(orbName);
        if (alias != null)
        {
            process.setOrbNameAlias(alias);
        }

        return process;
    }


    public void printCacheContents()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(CATEGORY, GUILoggerINBusinessProperty.PROCESSES,dumpCache().toString());
        }
    }

    public StringBuffer dumpCache()
    {
        StringBuffer sb = new StringBuffer(1000);

        CBOEProcess[] processes = getAllProcesses();

        sb.append("\nProcessCache::dumpCache");
        sb.append("\nProcess Cache contents:");
        dumpOrbNames(processes,sb);

        sb.append("\nCAS Cache contents:");
        CBOEProcess[] casProcesses = getAllCASes();
        dumpOrbNames(casProcesses,sb);

        sb.append("\nSACAS Cache contents:");
        CBOEProcess[] sacasProcesses = getAllSACASes();
        dumpOrbNames(sacasProcesses,sb);

        sb.append("\nICS Cache contents:");
        CBOEProcess[] icsProcesses = getAllICSes();
        dumpOrbNames(icsProcesses, sb);

        sb.append("\nSubscribed Cache contents:");
        CBOEProcess[] subscribedProcesses = getSubscribedProcesses();
        dumpOrbNames(subscribedProcesses,sb);

        return sb;
    }
    public void dumpOrbNames(CBOEProcess[] processes, StringBuffer sb)
    {
        for (int i=0;i<processes.length ;i++ )
        {
            sb.append("\nOrbName: ");
            sb.append(processes[i].getOrbName());
        }
    }

    public CBOEProcess[] getAllICSes()
    {
        CBOEProcess[] icses = new CBOEProcess[0];
        synchronized(mapLock)
        {
            icses = icsCache.values().toArray(icses);
        }
        return icses;
    }

    public CBOEProcess getICS(String icsOrbName)
    {
        CBOEProcess ics;

        synchronized(mapLock)
        {
            ics = icsCache.get(icsOrbName);
        }

        return ics;
    }

    public String[] getAllICSNames()
    {
        String[] icsNames = new String[0];
        synchronized(mapLock)
        {
            Set<String> keySet = icsCache.keySet();
            icsNames = keySet.toArray(icsNames);
        }
        return icsNames;
    }

    public CBOEProcess[] getAllSACASes()
    {
        CBOEProcess[] sacases = new CBOEProcess[0];
        synchronized(mapLock)
        {
            sacases = sacasCache.values().toArray(sacases);
        }
        return sacases;
    }

    public CBOEProcess[] getAllCASes()
    {
        CBOEProcess[] cases = new CBOEProcess[0];
        synchronized(mapLock)
        {
            cases = casCache.values().toArray(cases);
        }
        return cases;
    }

    public String[] getAllSACASNames()
    {
        String[] sacasNames = new String[0];
        synchronized(mapLock)
        {
            Set<String> keySet = sacasCache.keySet();
            sacasNames = keySet.toArray(sacasNames);
        }
        return sacasNames;

    }

    public String[] getAllCASNames()
    {
        CBOEProcess[] cases = getAllCASes();
        String[] casNames = new String[cases.length];

        for( int i = 0; i < cases.length; i++ )
        {
            casNames[i] = cases[i].getOrbName();
        }
        return casNames;
    }

    public CBOEProcess getCAS(String casOrbName)
    {
        CBOEProcess cas;

        synchronized(mapLock)
        {
            cas = casCache.get(casOrbName);
        }

        return cas;
    }

    public boolean isValidSACASForProductQuery(String processOrbName)
    {
        boolean valid = false;
        if (sacasCache.containsKey(processOrbName))
        {
            CBOEProcess process = sacasCache.get(processOrbName);
            valid = (process.getOnlineStatus() == Status.UP);
        }
        return valid;
    }


    public String findFirstAvailableSACAS()
    {
        String[] sacasNames = getAllSACASNames();
        Arrays.sort(sacasNames);
        if(sacasNames.length > 0)
        {
            return sacasNames[0];
        }
        else
        {
            return null;
        }
    }

    public String[] getFirmNames()
    {
        if(casInformationCache != null)
        {
            return casInformationCache.getFirmNames();
        }
        return new String[0];
    }

    public void markProcessAsSubscribed(String orbName)
    {
        CBOEProcessMutable process = processMap.get(orbName);
        process.markSubscribedForInstrumentors(true);
        subscribedMap.put(orbName,process);
        dispatchProcessEvent(process);
    }
    public void removeProcessAsSubscribed(String orbName)
    {
        CBOEProcessMutable process = processMap.get(orbName);
        process.markSubscribedForInstrumentors(false);
        subscribedMap.remove(orbName);
        dispatchProcessEvent(process);
    }

    public CBOEProcess[] getSubscribedProcesses()
    {
        ProcessImpl[] subscribedArray = new ProcessImpl[0];

        if (subscribedMap.size() > 0)
        {
            subscribedArray = subscribedMap.values().toArray(subscribedArray);
        }

        return subscribedArray;
    }

    public int getSubscribedProcessesCount()
    {
        return subscribedMap.size();
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
            GUILoggerHome.find().exception("ProcessCache::getIntProperty", nfe);
        }
        return initialMapSize;
    }

    public String getIcsManagerOrbName()
    {
        if (icsManager == null)
        {
            return "Not Found";
        }
        else
        {
            return icsManager.getOrbName();
        }
    }

    public String[] getIcsManagerLogicalNames()
    {
        if(icsManager == null)
        {
            return new String[0];
        }
        else
        {
            return icsManager.getLogicalNames();
        }
    }

    public CBOEProcessMutable getIcsManager()
    {
        return icsManager;
    }

    public void setIcsManager(CBOEProcessMutable icsManager)
    {
        this.icsManager = icsManager;
    }

    private void getProcessMode(CBOEProcessMutable process)
    {
        final CBOEProcessMutable ics = process;
        GUIWorker worker = new GUIWorkerImpl()
        {
            String response = CBOEProcess.DEFAULT_PROCESS_MODE;
            String result = null;

            public void execute() throws Exception
            {
                if(ics != null)
                {
                    result = InstrumentationTranslatorFactory.find().getIcsManagerState(ics);
                }
            }

            public void processData()
            {
                if(ics != null)
                {
                    if(result != null)
                    {
                        if(result.endsWith(ProcessImpl.ICS_MANAGER))
                        {
                            response = ProcessImpl.ICS_MANAGER;
                            setIcsManager(ics);                            
                        }
                        else if(result.endsWith(ProcessImpl.ICS_WORKER))
                        {
                            response = ProcessImpl.ICS_WORKER;
                        }
                    }
                    synchronized(ics)
                    {
                        ics.setProcessMode(response);
                    }   
                }
            }
        };
        APIWorkerImpl.run(worker);
    }

}
