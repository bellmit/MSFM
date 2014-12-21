//
// -----------------------------------------------------------------------------------
// Source file: SubscriptionRecoveryListener.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import java.util.*;

import com.cboe.interfaces.instrumentation.Status;
import com.cboe.interfaces.presentation.processes.CBOEProcess;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.common.formatters.ProcessTypes;

public class SubscriptionRecoveryListener
        implements EventChannelListener
{
    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private Map<String, CBOEProcess> collectorMap;

    private final Object lockObject = new Object();

    private boolean wasICSUpLastCheck;

    public SubscriptionRecoveryListener()
    {
        initialize();
    }

    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;

        if(channelType == ChannelType.INSTRUMENTATION_PROCESS_ICS_UPDATE)
        {
            CBOEProcess newProcess = (CBOEProcess) event.getEventData();
            if(isCollectorProcess(newProcess))
            {
                synchronized(lockObject)
                {
                    CBOEProcess existingProcess = collectorMap.get(newProcess.getOrbName());
                    if(existingProcess == null)
                    {
                        if(isCollectorProcessRunning(newProcess) && !wasICSUpLastCheck)
                        {
                            resubscribe();
                        }
                    }
                    else
                    {
                        if(!isCollectorProcessRunning(existingProcess) && isCollectorProcessRunning(newProcess))
                        {
                            resubscribe();
                        }
                    }

                    addProcess(newProcess);
                    wasICSUpLastCheck = isAnyCollectorProcessRunning();
                }
            }
        }
    }

    private void initialize()
    {
        collectorMap = new HashMap<String, CBOEProcess>(5);
        initListeners();
    }

    private void initListeners()
    {
        synchronized(lockObject)
        {
            CBOEProcess[] icsProcesses = InstrumentationTranslatorFactory.find().getAllICSes(this);
            for(CBOEProcess process : icsProcesses)
            {
                addProcess(process);
            }
            wasICSUpLastCheck = isAnyCollectorProcessRunning();
        }
    }

    private void addProcess(CBOEProcess process)
    {
        collectorMap.put(process.getOrbName(), process);
    }

    private boolean isCollectorProcess(CBOEProcess process)
    {
        boolean isCollector = process.getProcessType() == ProcessTypes.ICS_TYPE;
        return isCollector;
    }

    private boolean isAnyCollectorProcessRunning()
    {
        boolean isCollectorRunning = false;
        for(CBOEProcess process : collectorMap.values())
        {
            if(isCollectorProcessRunning(process))
            {
                isCollectorRunning = true;
                break;
            }
        }
        return isCollectorRunning;
    }

    private boolean isCollectorProcessRunning(CBOEProcess process)
    {
        boolean isCollectorRunning = (process.getMasterSlaveStatus() == Status.MASTER);
        return isCollectorRunning;
    }

    private void resubscribe()
    {
        InstrumentationTranslatorFactory.find().resubscribe();
    }
}
