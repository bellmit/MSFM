//
// -----------------------------------------------------------------------------------
// Source file: ProcessWatcherListener.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import java.util.*;

import com.cboe.idl.processWatcher.PWEventCodes;

import com.cboe.interfaces.instrumentation.Status;
import com.cboe.interfaces.presentation.processWatcher.ProcessWatcherStatusEvent;
import com.cboe.interfaces.presentation.processes.ProcessInfoModel;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.processWatcher.ProcessWatcherManagerHome;
import com.cboe.presentation.processWatcher.ProcessWatcherStatusListener;

class ProcessWatcherListener implements ProcessWatcherStatusListener
{
    private static final String CATEGORY = ProcessWatcherListener.class.getName();

    private Object lockObject;
    private Map<String, ProcessInfoModel> processCache;
    private ProcessWatcherListenerManager mgr;

    ProcessWatcherListener(Object lockObject, Map<String, ProcessInfoModel> processCache, ProcessWatcherListenerManager mgr)
    {
        this.lockObject = lockObject;
        this.processCache = processCache;
        this.mgr = mgr;
    }

    public void processStatusChange(ProcessWatcherStatusEvent event)
    {
        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.PROCESSES))
        {
            Object[] argObj = new Object[4];
            argObj[0] = new Integer(event.getType());
            argObj[1] = event.getOrbName();
            argObj[2] = new Integer(event.getState());
            argObj[3] = event.getTime();

            GUILoggerHome.find().debug(CATEGORY + ": processStatusChange",
                                       GUILoggerINBusinessProperty.PROCESSES, argObj);
        }

        String eventOrbName = event.getOrbName();
        if( eventOrbName != null && eventOrbName.length() > 0)
        {
            ProcessInfoModel foundProcess = null;
            boolean cacheHadProcess = true;

            //find cached process or add if not found
            synchronized( lockObject )
            {
                foundProcess = processCache.get(eventOrbName);

                if( foundProcess == null )
                {
                    ProcessInfoModel newModel = findUnknownProcess(eventOrbName);
                    if(newModel != null)
                    {
                        mgr.addProcessInfoToCache(newModel);
                        cacheHadProcess = false;
                        foundProcess = newModel;
                    }
                }
            }

            if(foundProcess != null)
            {
                boolean shouldSendEvent = false;
                synchronized( foundProcess )
                {
                    //update cached process
                    if( cacheHadProcess )
                    {
                        switch( event.getType() )
                        {
                            case ProcessWatcherStatusEvent.PROCESS_DOWN:
                                if(foundProcess.getOnlineStatus() != event.getState())
                                {
                                    clearFieldData(foundProcess);
                                    foundProcess.setPoaStatus(Status.NO_RESPONSE, "", PWEventCodes.Unknown);
                                    foundProcess.setOnlineStatus(event.getState(), event.getEventOriginator(),
                                                                 event.getReasonCode());
                                    shouldSendEvent = true;
                                }
                                updateTime(foundProcess);
                                break;
                            case ProcessWatcherStatusEvent.PROCESS_UP:
                                if( foundProcess.getOnlineStatus() != event.getState() )
                                {
                                    if(foundProcess.getOnlineStatus() == Status.DOWN)
                                    {
                                        foundProcess.setPoaStatus(Status.NO_RESPONSE, "", PWEventCodes.Unknown);
                                    }
                                    foundProcess.setOnlineStatus(event.getState(), event.getEventOriginator(),
                                                                 event.getReasonCode());
                                    shouldSendEvent = true;
                                }
                                updateTime(foundProcess);
                                break;
                            case ProcessWatcherStatusEvent.POA_DOWN:
                            case ProcessWatcherStatusEvent.POA_UP:
                                if( foundProcess.getPoaStatus() != event.getState() )
                                {
                                    foundProcess.setPoaStatus(event.getState(), event.getEventOriginator(),
                                                              event.getReasonCode());
                                    shouldSendEvent = true;
                                }
                                updateTime(foundProcess);
                                break;
                            default:
                                GUILoggerHome.find().alarm(CATEGORY +
                                                           ": processStatusChange", "Received unexpected " +
                                                           "event type: " + event.getType());
                        }
                    }

                    //send IEC events.
                    if( shouldSendEvent || !cacheHadProcess)
                    {
                        mgr.dispatchProcessInfoEvent(foundProcess);
                        if( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.PROCESSES))
                        {
                            GUILoggerHome.find().debug(CATEGORY + ": processStatusChange",
                                                       GUILoggerINBusinessProperty.PROCESSES, "Did not send event. No changes.");
                        }
                    }
                }
            }
            else
            {
                GUILoggerHome.find().alarm(CATEGORY +
                                           ": processStatusChange", "Received an event for an unknown ORBName:" +
                                           eventOrbName);
            }
        }
    }

    private ProcessInfoModel findUnknownProcess(String eventOrbName)
    {
        ProcessInfoModel foundModel = null;
        try
        {
            ProcessInfoModel[] allModels = ProcessWatcherManagerHome.find().getWatchedProcessList();
            for( int i = 0; i < allModels.length; i++ )
            {
                ProcessInfoModel processInfoModel = allModels[i];
                if(processInfoModel.getOrbName().equals(eventOrbName))
                {
                    foundModel = processInfoModel;
                    break;
                }
            }
        }
        catch( Exception e )
        {
            GUILoggerHome.find().exception(CATEGORY + ": findUnknownProcess",
                                           "Could not get watched process list to find unknown ORBName:" + eventOrbName,
                                           e);
        }
        return foundModel;
    }

    private void updateTime(ProcessInfoModel foundProcess)
    {
/*
        if( foundProcess instanceof CASModel )
        {
            //it is an CAS
            (( CASModel ) foundProcess).updateLastUpdateTime();
        }
*/
    }

    private void clearFieldData(ProcessInfoModel foundProcess)
    {
/*
        if( foundProcess instanceof CASModel )
        {
            //it is an CAS
            (( CASModel ) foundProcess).setCurrentMemoryUsage(0);
            (( CASModel ) foundProcess).setHeapMemorySize(0);
            (( CASModel ) foundProcess).setCurrentMaxQueueSize(0);
            (( CASModel ) foundProcess).setCurrentUsersSize(0);
        }
*/
    }

    public void publishCurrentStatus()
    {
        ProcessInfoModel[] processInfoModels = null;
        synchronized(lockObject)
        {
            Collection<ProcessInfoModel> processInfoModelCollection = processCache.values();
            processInfoModels = processInfoModelCollection.toArray(new ProcessInfoModel[processInfoModelCollection.size()]);
        }
        publishCurrentStatus(processInfoModels);
    }

    public void publishCurrentStatus(String orbName)
    {
        ProcessInfoModel[] processInfoModels = new ProcessInfoModel[1];
        synchronized( lockObject )
        {
            ProcessInfoModel foundProcess = processCache.get(orbName);
            if( foundProcess != null )
            {
                processInfoModels[0] = foundProcess;
            }
        }
        publishCurrentStatus(processInfoModels);
    }

    private void publishCurrentStatus(ProcessInfoModel[] processInfoModels)
    {
        if(processInfoModels != null)
        {
            for (int i=0; i<processInfoModels.length; i++)
            {
                mgr.dispatchProcessInfoEvent(processInfoModels[i]);
            }
        }
    }
}
