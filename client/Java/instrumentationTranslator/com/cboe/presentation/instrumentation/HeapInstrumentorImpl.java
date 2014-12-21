// -----------------------------------------------------------------------------------
// Source file: HeapInstrumentorImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.CalculatedHeapInstrumentor;
import com.cboe.interfaces.instrumentation.CalculatedHeapInstrumentorMutable;
import com.cboe.interfaces.instrumentation.InstrumentorTypes;

public class HeapInstrumentorImpl extends AbstractInstrumentor implements CalculatedHeapInstrumentorMutable
{
    private long maxMemory;
    private long totalMemory;
    private long freeMemory;
    //Calculated Instrumentor Data
    private long intervalMaxMemory;
    private long intervalTotalMemory;
    private long intervalFreeMemory;
    private Long maxMemoryLong;
    private Long totalMemoryLong;
    private Long freeMemoryLong;
    private Long intervalMaxMemoryLong;
    private Long intervalTotalMemoryLong;
    private Long intervalFreeMemoryLong;

    protected HeapInstrumentorImpl()
    {
        super();
    }

    public HeapInstrumentorImpl(String orbName, String clusterName, com.cboe.instrumentationService.instrumentors.HeapInstrumentor heapInstrumentor)
    {
        this();
        setData(orbName, clusterName, heapInstrumentor);
    }

    public HeapInstrumentorImpl(String orbName, String clusterName,
                                com.cboe.instrumentationService.instrumentors.HeapInstrumentor heapInstrumentor,
                                com.cboe.instrumentationService.instrumentors.CalculatedHeapInstrumentor calculatedHeapInstrumentor)
    {
        this(orbName, clusterName, heapInstrumentor);
        setCalculatedData(calculatedHeapInstrumentor);
    }

    /**
     * Sets type of the instrumentor.
     */
    protected void setType()
    {
        this.type = InstrumentorTypes.HEAP;
    }

    public void setData(String orbName, String clusterName, com.cboe.instrumentationService.instrumentors.HeapInstrumentor heapInstrumentor)
    {
        super.setData(orbName, clusterName, heapInstrumentor);
        this.freeMemory = heapInstrumentor.getFreeMemory();
        this.totalMemory = heapInstrumentor.getTotalMemory();
        this.maxMemory = heapInstrumentor.getMaxMemory();
    }

    public void setCalculatedData(com.cboe.instrumentationService.instrumentors.CalculatedHeapInstrumentor calculatedHeapInstrumentor)
    {
        this.intervalFreeMemory = calculatedHeapInstrumentor.getIntervalFreeMemory();
        this.intervalMaxMemory = calculatedHeapInstrumentor.getIntervalMaxMemory();
        this.intervalTotalMemory = calculatedHeapInstrumentor.getIntervalTotalMemory();
    }

    /**
     * Returns MaxMemory value for the instrumentor object.
     * @return MaxMemory long
     */
    public long getMaxMemory()
    {
        return maxMemory;
    }

    /**
     * Returns TotalMemory value for the instrumentor object.
     * @return TotalMemory long
     */
    public long getTotalMemory()
    {
        return totalMemory;
    }

    /**
     * Returns FreeMemory value for the instrumentor object.
     * @return FreeMemory long
     */
    public long getFreeMemory()
    {
        return freeMemory;
    }

    public long getIntervalMaxMemory()
    {
        return this.intervalMaxMemory;
    }

    public long getIntervalTotalMemory()
    {
        return this.intervalTotalMemory;
    }

    public long getIntervalFreeMemory()
    {
        return this.intervalFreeMemory;
    }

    /**
     *  Clears the data elements of the instrumentor.
     */
    public void clearData()
    {
        this.maxMemory = 0;
        this.totalMemory = 0;
        this.freeMemory = 0;
        this.intervalFreeMemory = 0;
        this.intervalMaxMemory = 0;
        this.intervalTotalMemory = 0;
        clearDataObjects();
    }

    /**
     *  Increment the instrumentor with the values from
     *  the instrumentor passed in.
     */
    public void instrumentorPlusPlus(CalculatedHeapInstrumentor heapInstrumentor)
    {
        maxMemory += heapInstrumentor.getMaxMemory();
        totalMemory += heapInstrumentor.getTotalMemory();
        freeMemory += heapInstrumentor.getFreeMemory();
        if (heapInstrumentor.getLastUpdatedTimeMillis() > lastUpdatedTimeMillis)
        {
            lastUpdatedTimeMillis = heapInstrumentor.getLastUpdatedTimeMillis();
            lastUpdatedTime = null;
        }
        intervalMaxMemory += heapInstrumentor.getIntervalMaxMemory();
        intervalTotalMemory += heapInstrumentor.getIntervalTotalMemory();
        intervalFreeMemory += heapInstrumentor.getIntervalFreeMemory();
        clearDataObjects();
    }

    /**
     *  Clone all the parts of the object.  Clone is used in a very
     *  heavy use method, so not everything is cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        HeapInstrumentorImpl heapInstrumentor = new HeapInstrumentorImpl();

        heapInstrumentor.userData = getUserData();
        heapInstrumentor.name = getName();
        heapInstrumentor.instrumentorKey = getInstrumentorKey();
        heapInstrumentor.orbName = getOrbName();
        heapInstrumentor.clusterName = getClusterName();
        heapInstrumentor.lastUpdatedTimeMillis = getLastUpdatedTimeMillis();

        heapInstrumentor.setInstrumentedData(this);

        return heapInstrumentor;
    }

    public void setInstrumentedData(CalculatedHeapInstrumentor heapInstrumentor)
    {
        this.lastUpdatedTimeMillis = heapInstrumentor.getLastUpdatedTimeMillis();
        this.lastUpdatedTime = null;

        this.maxMemory = heapInstrumentor.getMaxMemory();
        this.totalMemory = heapInstrumentor.getTotalMemory();
        this.freeMemory = heapInstrumentor.getFreeMemory();

        this.intervalMaxMemory = heapInstrumentor.getIntervalMaxMemory();
        this.intervalTotalMemory = heapInstrumentor.getIntervalTotalMemory();
        this.intervalFreeMemory = heapInstrumentor.getIntervalFreeMemory();
        clearDataObjects();
    }

    private void clearDataObjects()
    {
        maxMemoryLong = null;
        totalMemoryLong = null;
        freeMemoryLong = null;
        intervalFreeMemoryLong = null;
        intervalMaxMemoryLong = null;
        intervalTotalMemoryLong = null;
    }
    public Long getIntervalFreeMemoryLong()
    {
        if (intervalFreeMemoryLong == null)
        {
            intervalFreeMemoryLong = new Long(intervalFreeMemory);
        }
        return intervalFreeMemoryLong;
    }

    public Long getIntervalTotalMemoryLong()
    {
        if (intervalTotalMemoryLong == null)
        {
            intervalTotalMemoryLong = new Long(intervalTotalMemory);
        }
        return intervalTotalMemoryLong;
    }

    public Long getIntervalMaxMemoryLong()
    {
        if (intervalMaxMemoryLong == null)
        {
            intervalMaxMemoryLong = new Long(intervalMaxMemory);
        }
        return intervalMaxMemoryLong;
    }

    public Long getFreeMemoryLong()
    {
        if (freeMemoryLong == null)
        {
            freeMemoryLong = new Long(freeMemory);
        }
        return freeMemoryLong;
    }

    public Long getTotalMemoryLong()
    {
        if (totalMemoryLong == null)
        {
            totalMemoryLong = new Long(totalMemory);
        }
        return totalMemoryLong;
    }

    public Long getMaxMemoryLong()
    {
        if (maxMemoryLong == null)
        {
            maxMemoryLong = new Long(maxMemory);
        }
        return maxMemoryLong;
    }

    protected String[] getInfraNames()
    {
        return null;
    }

}
