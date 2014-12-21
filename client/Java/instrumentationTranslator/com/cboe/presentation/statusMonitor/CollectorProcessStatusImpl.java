package com.cboe.presentation.statusMonitor;

import com.cboe.interfaces.instrumentation.CollectorProcessStatus;
import com.cboe.interfaces.presentation.processes.ProcessInfo;

public class CollectorProcessStatusImpl implements CollectorProcessStatus
{
    protected ProcessInfo[] processInfos;

    public CollectorProcessStatusImpl(ProcessInfo[] processInfos)
    {
        this.processInfos = processInfos;
    }

    public ProcessInfo[] getCollectorProcessInfos()
    {
        return processInfos;
    }
}
