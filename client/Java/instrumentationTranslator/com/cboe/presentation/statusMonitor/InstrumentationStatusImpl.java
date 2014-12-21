package com.cboe.presentation.statusMonitor;

import com.cboe.interfaces.instrumentation.InstrumentationStatus;

public class InstrumentationStatusImpl implements InstrumentationStatus
{
    protected int status;
    protected int intervalCount;

    public InstrumentationStatusImpl(int status, int intervalCount)
    {
        this.status = status;
        this.intervalCount = intervalCount;
    }

    public int getInstrumentorStatus()
    {
        return status;
    }

    public int getIntervalInstrumentorCount()
    {
        return intervalCount;
    }
}
