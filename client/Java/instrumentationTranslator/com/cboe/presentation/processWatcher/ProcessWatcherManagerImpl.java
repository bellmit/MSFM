//
// -----------------------------------------------------------------------------------
// Source file: ProcessWatcherManagerImpl
//
// PACKAGE: com.cboe.presentation.processWatcher
//
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.processWatcher;

import java.util.*;

import com.cboe.idl.processWatcher.PWEventCodes;

import com.cboe.interfaces.instrumentation.Status;
import com.cboe.interfaces.presentation.processWatcher.ProcessWatcherStatusEvent;
import com.cboe.interfaces.presentation.processes.ProcessInfoModel;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.instrumentation.ProcessInfoFactory;

import com.cboe.processWatcher.ProcessStatusPoaStateListener;
import com.cboe.processWatcher.ProcessWatcherPoaStateProxyECImpl;
import com.cboe.processWatcher.WatchedProcess;

public class ProcessWatcherManagerImpl implements ProcessWatcherManager
{
    private static final String CATEGORY = ProcessWatcherManagerImpl.class.getName();

    public static final String PROCESS_WATCHER_EVENT_CHANNEL_PROPERTY = "ProcessWatcher.ChannelName";

    private ProcessWatcherPoaStateProxyECImpl pw;     // infra ProcessWatcher Proxy Object
    private List<ProcessWatcherStatusListener> listeners;

    private String processWatcherEventChannelName;
    private ProcessWatcherConsumer consumer;

    public ProcessWatcherManagerImpl()
    {
        initialize();
    }

    private void initialize()
    {
        pw = null;
        listeners  = new ArrayList<ProcessWatcherStatusListener>();
        consumer = new ProcessWatcherConsumer();
        processWatcherEventChannelName = System.getProperty(PROCESS_WATCHER_EVENT_CHANNEL_PROPERTY, "ProdProcessWatcher");
    }

    private ProcessInfoModel createProcessInfoModel(WatchedProcess wp)
    {
        ProcessInfoModel updatedProcess = ProcessInfoFactory.createProcessInfoModel(wp.getProcessName(),
                                                                                    wp.getOrbName(),
                                                                                    wp.getHost(),
                                                                                    wp.getPort(),
                                                                                    Status.NO_RESPONSE,
                                                                                    "", PWEventCodes.Unknown,
                                                                                    Status.NO_RESPONSE,
                                                                                    "", PWEventCodes.Unknown,
                                                                                    "");

        return updatedProcess;
    }

    private ProcessWatcherPoaStateProxyECImpl getProcessWatcher() throws Exception
    {
        try
        {
            if (pw == null)
            {
                pw = new ProcessWatcherPoaStateProxyECImpl(processWatcherEventChannelName);
            }
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(this.getClass().getName(), "Failure initializing the ProcessWatcher", e);
            throw e;
        }

        return pw;
    }

    public synchronized void addProcessWatcherStatusListener(ProcessWatcherStatusListener l)
    {
        if (!listeners.contains(l))
        {
            listeners.add(l);
        }
    }

    public synchronized void removeProcessWatcherStatusListener(ProcessWatcherStatusListener l)
    {
        listeners.remove(l);
    }

    public synchronized void fireEvent(ProcessWatcherStatusEvent event)
    {
        for (int i=0; i<listeners.size(); i++)
        {
            listeners.get(i).processStatusChange(event);
        }
    }

    public ProcessInfoModel[] getWatchedProcessList() throws Exception
    {
        List pwList  = getProcessWatcher().getProcessWatchList();

        int size = pwList.size();
        ProcessInfoModel[] retList = new ProcessInfoModel[size];

        for (int i=0; i<size; i++)
        {
            retList[i] = createProcessInfoModel((WatchedProcess)pwList.get(i));
        }

        return retList;
    }

    public void registerWithProcessWatcher() throws Exception
    {
        getProcessWatcher().addProcessStatusListener(consumer);

        GUILoggerHome.find().information(CATEGORY + ": registerWithProcessWatcher", GUILoggerINBusinessProperty.PROCESSES,
                                         "Subscribe to Process Watcher Successful...");
    }

    public void unregisterWithProcessWatcher() throws Exception
    {
        getProcessWatcher().removeProcessStatusListener(consumer);
    }

// -----------------------------------------------------------------------------------
// Inner Classes
// -----------------------------------------------------------------------------------

    private class ProcessWatcherConsumer implements ProcessStatusPoaStateListener
    {
        public void processUp(WatchedProcess wp, String eventOriginator, short reasonCode)
        {
            if( GUILoggerHome.find().isDebugOn() )
            {
                GUILoggerHome.find().debug(CATEGORY + ": ProcessWatcherConsumer: processUp",
                                           GUILoggerINBusinessProperty.PROCESSES, wp.getOrbName() + " is Up; eventOriginator="+eventOriginator+", reasonCode="+reasonCode);
            }

            fireEvent(ProcessWatcherStatusEventFactory.createProcessWatcherStatusEvent(this, ProcessWatcherStatusEvent.PROCESS_UP, wp.getOrbName(),
                                                    Calendar.getInstance().getTime(),
                                                    Status.UP, eventOriginator, reasonCode));
        }

        public void processDown(WatchedProcess wp, String eventOriginator, short reasonCode)
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(CATEGORY + ": ProcessWatcherConsumer: processDown",
                                           GUILoggerINBusinessProperty.PROCESSES, wp.getOrbName() + " is Down; eventOriginator="+eventOriginator+", reasonCode="+reasonCode);
            }

            fireEvent(ProcessWatcherStatusEventFactory.createProcessWatcherStatusEvent(this, ProcessWatcherStatusEvent.PROCESS_DOWN, wp.getOrbName(),
                                                    Calendar.getInstance().getTime(),
                                                    Status.DOWN, eventOriginator, reasonCode));
        }

        public void processPoaStateUp(WatchedProcess wp, String eventOriginator, short reasonCode)
        {
            if( GUILoggerHome.find().isDebugOn() )
            {
                GUILoggerHome.find().debug(CATEGORY + ": ProcessWatcherConsumer: processPoaStateUp",
                                           GUILoggerINBusinessProperty.PROCESSES, wp.getOrbName() + " POA is Up; eventOriginator="+eventOriginator+", reasonCode="+reasonCode);
            }

            fireEvent(ProcessWatcherStatusEventFactory.createProcessWatcherStatusEvent(this, ProcessWatcherStatusEvent.POA_UP, wp.getOrbName(),
                                                    Calendar.getInstance().getTime(),
                                                    Status.UP, eventOriginator, reasonCode));
        }

        public void processPoaStateDown(WatchedProcess wp, String eventOriginator, short reasonCode)
        {
            if( GUILoggerHome.find().isDebugOn() )
            {
                GUILoggerHome.find().debug(CATEGORY + ": ProcessWatcherConsumer: processPoaStateDown",
                                           GUILoggerINBusinessProperty.PROCESSES, wp.getOrbName() + " POA is Down; eventOriginator="+eventOriginator+", reasonCode="+reasonCode);
            }

            fireEvent(ProcessWatcherStatusEventFactory.createProcessWatcherStatusEvent(this, ProcessWatcherStatusEvent.POA_DOWN, wp.getOrbName(),
                                                    Calendar.getInstance().getTime(),
                                                    Status.DOWN, eventOriginator, reasonCode));
        }
    }
}