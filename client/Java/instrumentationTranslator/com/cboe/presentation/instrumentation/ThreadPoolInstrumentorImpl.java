// -----------------------------------------------------------------------------------
// Source file: ThreadPoolInstrumentorImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.CalculatedThreadPoolInstrumentor;
import com.cboe.interfaces.instrumentation.CalculatedThreadPoolInstrumentorMutable;
import com.cboe.interfaces.instrumentation.InstrumentorTypes;

import com.cboe.client.xml.bind.GIContextDetailType;
import com.cboe.client.xml.bind.GIThreadInstrumentorType;

public class ThreadPoolInstrumentorImpl extends AbstractInstrumentor implements CalculatedThreadPoolInstrumentorMutable
{
    public static final String[] INFRA_NAMES =
            {
                "POATP"
            };

    private int currentlyExecutingThreadsSize;
    private int startedThreadsSize;
    private int pendingThreadsSize;
    private int startedThreadsHighWaterMark;
    private int pendingTaskCount;
    private int pendingTaskCountHighWaterMark;
    private double percentThreadsUsed;
    //Calculated Instrumentor Data
    private long intervalExecutingThreads;
    private long intervalStartedThreads;
    private long intervalPendingThreads;
    private long intervalPendingTaskCount;

    private Integer currentlyExecutingThreadsSizeInteger;
    private Integer startedThreadsSizeInteger;
    private Integer pendingThreadsSizeInteger;
    private Integer startedThreadsHighWaterMarkInteger;
    private Integer pendingTaskCountInteger;
    private Integer pendingTaskCountHighWaterMarkInteger;
    private Double percentThreadsUsedDouble;
    private Long intervalExecutingThreadsLong;
    private Long intervalStartedThreadsLong;
    private Long intervalPendingThreadsLong;
    private Long intervalPendingTaskCountLong;

    /**
     * Default Constructor
     */
    protected ThreadPoolInstrumentorImpl()
    {
        super();
    }

    protected ThreadPoolInstrumentorImpl(String orbName, String clusterName, GIContextDetailType contextDetailType, GIThreadInstrumentorType threadInstrumentorType)
    {
        this();
        setData(orbName, clusterName, contextDetailType, threadInstrumentorType);
    }

    public ThreadPoolInstrumentorImpl(String orbName, String clusterName, com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor threadPoolInstrumentor)
    {
        this();
        setData(orbName, clusterName, threadPoolInstrumentor);
    }

    public ThreadPoolInstrumentorImpl(String orbName, String clusterName,
                                      com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor threadPoolInstrumentor,
                                      com.cboe.instrumentationService.instrumentors.CalculatedThreadPoolInstrumentor calculatedThreadPoolInstrumentor)
    {
        this(orbName, clusterName, threadPoolInstrumentor);
        setCalculatedData(calculatedThreadPoolInstrumentor);
    }

    /**
     * Sets type of the instrumentor.
     */
    protected void setType()
    {
        this.type = InstrumentorTypes.THREAD;
    }

    protected void setData(String orbName, String clusterName, GIContextDetailType contextDetailType, GIThreadInstrumentorType threadInstrumentorType)
    {
        super.setData(orbName, clusterName, contextDetailType.getName(), threadInstrumentorType.getUserData());
        this.currentlyExecutingThreadsSize = threadInstrumentorType.getCurrentlyExecutingThreads();
        this.startedThreadsSize = threadInstrumentorType.getStartedThreads();
        this.pendingThreadsSize = threadInstrumentorType.getPendingThreads();
        this.startedThreadsHighWaterMark = threadInstrumentorType.getStartedThreadsHighWaterMark();
        this.pendingTaskCount = threadInstrumentorType.getPendingTaskCount();
        this.pendingTaskCountHighWaterMark = threadInstrumentorType.getPendingTaskCountHighWaterMark();
    }

    public void setData(String orbName, String clusterName, com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor threadPoolInstrumentor)
    {
        super.setData(orbName, clusterName, threadPoolInstrumentor);
        this.currentlyExecutingThreadsSize = threadPoolInstrumentor.getCurrentlyExecutingThreads();
        this.startedThreadsSize = threadPoolInstrumentor.getStartedThreads();
        this.pendingThreadsSize = threadPoolInstrumentor.getPendingThreads();
        this.startedThreadsHighWaterMark = threadPoolInstrumentor.getStartedThreadsHighWaterMark();
        this.pendingTaskCount = threadPoolInstrumentor.getPendingTaskCount();
        this.pendingTaskCountHighWaterMark = threadPoolInstrumentor.getPendingTaskCountHighWaterMark();
//        this.percentThreadsUsed = threadPoolInstrumentor.get;
    }

    public void setCalculatedData(com.cboe.instrumentationService.instrumentors.CalculatedThreadPoolInstrumentor calculatedThreadPoolInstrumentor)
    {
        this.intervalExecutingThreads = calculatedThreadPoolInstrumentor.getIntervalExecutingThreads();
        this.intervalStartedThreads = calculatedThreadPoolInstrumentor.getIntervalStartedThreads();
        this.intervalPendingThreads = calculatedThreadPoolInstrumentor.getIntervalPendingThreads();
        this.intervalPendingTaskCount = calculatedThreadPoolInstrumentor.getIntervalPendingTaskCount();
    }

    /**
     * Returns CurrentlyExecutingThreadsSize value for the threadpool instrumentor object.
     * @return CurrentlyExecutingThreadsSize int
     */
    public int getCurrentlyExecutingThreadsSize()
    {
        return currentlyExecutingThreadsSize;
    }

    /**
     * Returns StartedThreadsSize value for the threadpool instrumentor object.
     * @return StartedThreadsSize int
     */
    public int getStartedThreadsSize()
    {
        return startedThreadsSize;
    }

    /**
     * Returns PendingThreadsSize value for the threadpool instrumentor object.
     * @return PendingThreadsSize int
     */
    public int getPendingThreadsSize()
    {
        return pendingThreadsSize;
    }

    /**
     * Returns PeakThreadsUsageCount value for the threadpool instrumentor object.
     * @return PeakThreadsUsageCount int
     */
    public int getStartedThreadsHighWaterMark()
    {
        return startedThreadsHighWaterMark;
    }

    /**
     * Returns PendingTaskCount value for the threadpool instrumentor object.
     * @return PendingTaskCount int
     */
    public int getPendingTaskCount()
    {
        return pendingTaskCount;
    }

    /**
     * Returns PeakPendingTaskCount value for the threadpool instrumentor object.
     * @return PeakPendingTaskCount int
     */
    public int getPendingTaskCountHighWaterMark()
    {
        return pendingTaskCountHighWaterMark;
    }

    /**
     * Returns PercentThreadsUsed value for the threadpool instrumentor object.
     * @return PercentThreadsUsed double
     */
    public double getPercentThreadsUsed()
    {
        return percentThreadsUsed;
    }

    public long getIntervalExecutingThreads()
    {
        return this.intervalExecutingThreads;
    }

    public long getIntervalStartedThreads()
    {
        return this.intervalStartedThreads;
    }

    public long getIntervalPendingThreads()
    {
        return this.intervalPendingThreads;
    }

    public long getIntervalPendingTaskCount()
    {
        return this.intervalPendingTaskCount;
    }

    /**
     *  Clears the data elements of the instrumentor.
     */
    public void clearData()
    {
        this.currentlyExecutingThreadsSize = 0;
        this.startedThreadsSize = 0;
        this.pendingThreadsSize = 0;
        this.startedThreadsHighWaterMark = 0;
        this.pendingTaskCount = 0;
        this.pendingTaskCountHighWaterMark = 0;
        this.percentThreadsUsed = 0;
        this.intervalExecutingThreads = 0;
        this.intervalStartedThreads = 0;
        this.intervalPendingThreads = 0;
        this.intervalPendingTaskCount = 0;
        clearDataObjects();
    }

    /**
     *  Increment the instrumentor with the values from
     *  the instrumentor passed in.
     */
    public void instrumentorPlusPlus(CalculatedThreadPoolInstrumentor threadPoolInstrumentor)
    {
        this.currentlyExecutingThreadsSize += threadPoolInstrumentor.getCurrentlyExecutingThreadsSize();
        this.startedThreadsSize += threadPoolInstrumentor.getStartedThreadsSize();
        this.pendingThreadsSize += threadPoolInstrumentor.getPendingThreadsSize();
        this.startedThreadsHighWaterMark += threadPoolInstrumentor.getStartedThreadsHighWaterMark();
        this.pendingTaskCount += threadPoolInstrumentor.getPendingTaskCount();
        this.pendingTaskCountHighWaterMark += threadPoolInstrumentor.getPendingTaskCountHighWaterMark();

        if (this.startedThreadsSize != 0)
        {
            this.percentThreadsUsed = this.currentlyExecutingThreadsSize / startedThreadsSize;
        }
        else
        {
            this.percentThreadsUsed = 0;
        }
        if (threadPoolInstrumentor.getLastUpdatedTimeMillis() > lastUpdatedTimeMillis)
        {
            lastUpdatedTimeMillis = threadPoolInstrumentor.getLastUpdatedTimeMillis();
            lastUpdatedTime = null;
        }
        this.intervalExecutingThreads += threadPoolInstrumentor.getIntervalExecutingThreads();
        this.intervalStartedThreads += threadPoolInstrumentor.getIntervalStartedThreads();
        this.intervalPendingThreads += threadPoolInstrumentor.getIntervalPendingThreads();
        this.intervalPendingTaskCount += threadPoolInstrumentor.getIntervalPendingTaskCount();
        clearDataObjects();
    }

    /**
     *  Clone all the parts of the object.  Clone is used in a very
     *  heavy use method, so not everything is cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        ThreadPoolInstrumentorImpl threadPoolInstrumentor = new ThreadPoolInstrumentorImpl();

        threadPoolInstrumentor.userData = this.getUserData();
        threadPoolInstrumentor.name = this.getName();
        threadPoolInstrumentor.instrumentorKey = this.getInstrumentorKey();
        threadPoolInstrumentor.orbName = this.getOrbName();
        threadPoolInstrumentor.clusterName = this.getClusterName();
        threadPoolInstrumentor.lastUpdatedTimeMillis = this.getLastUpdatedTimeMillis();

        threadPoolInstrumentor.setInstrumentedData(this);

        return threadPoolInstrumentor;
    }

    public void setInstrumentedData(CalculatedThreadPoolInstrumentor threadPoolInstrumentor)
    {
        this.lastUpdatedTimeMillis = threadPoolInstrumentor.getLastUpdatedTimeMillis();
        this.lastUpdatedTime = null;

        this.currentlyExecutingThreadsSize = threadPoolInstrumentor.getCurrentlyExecutingThreadsSize();
        this.startedThreadsSize = threadPoolInstrumentor.getStartedThreadsSize();
        this.pendingThreadsSize = threadPoolInstrumentor.getPendingThreadsSize();
        this.startedThreadsHighWaterMark = threadPoolInstrumentor.getStartedThreadsHighWaterMark();
        this.pendingTaskCount = threadPoolInstrumentor.getPendingTaskCount();
        this.pendingTaskCountHighWaterMark = threadPoolInstrumentor.getPendingTaskCountHighWaterMark();
        this.percentThreadsUsed = threadPoolInstrumentor.getPercentThreadsUsed();

        this.intervalExecutingThreads = threadPoolInstrumentor.getIntervalExecutingThreads();
        this.intervalStartedThreads = threadPoolInstrumentor.getIntervalStartedThreads();
        this.intervalPendingThreads = threadPoolInstrumentor.getIntervalPendingThreads();
        this.intervalPendingTaskCount = threadPoolInstrumentor.getIntervalPendingTaskCount();
        clearDataObjects();
    }


    public Integer getCurrentlyExecutingThreadsSizeInteger()
    {
        if (currentlyExecutingThreadsSizeInteger == null)
        {
            currentlyExecutingThreadsSizeInteger = new Integer(currentlyExecutingThreadsSize);
        }
        return currentlyExecutingThreadsSizeInteger;
    }


    public Integer getStartedThreadsSizeInteger()
    {
        if (startedThreadsSizeInteger == null)
        {
            startedThreadsSizeInteger = new Integer(startedThreadsSize);
        }
        return startedThreadsSizeInteger;
    }


    public Integer getPendingThreadsSizeInteger()
    {
        if (pendingThreadsSizeInteger == null)
        {
            pendingThreadsSizeInteger = new Integer(pendingThreadsSize);
        }
        return pendingThreadsSizeInteger;
    }


    public Integer getStartedThreadsHighWaterMarkInteger()
    {
        if (startedThreadsHighWaterMarkInteger == null)
        {
            startedThreadsHighWaterMarkInteger = new Integer(startedThreadsHighWaterMark);
        }
        return startedThreadsHighWaterMarkInteger;
    }


    public Integer getPendingTaskCountInteger()
    {
        if (pendingTaskCountInteger == null)
        {
            pendingTaskCountInteger = new Integer(pendingTaskCount);
        }
        return pendingTaskCountInteger;
    }


    public Integer getPendingTaskCountHighWaterMarkInteger()
    {
        if (pendingTaskCountHighWaterMarkInteger == null)
        {
            pendingTaskCountHighWaterMarkInteger = new Integer(pendingTaskCountHighWaterMark);
        }
        return pendingTaskCountHighWaterMarkInteger;
    }


    public Double getPercentThreadsUsedDouble()
    {
        if (percentThreadsUsedDouble == null)
        {
            percentThreadsUsedDouble = new Double(percentThreadsUsed);
        }
        return percentThreadsUsedDouble;
    }


    public Long getIntervalExecutingThreadsLong()
    {
        if (intervalExecutingThreadsLong == null)
        {
            intervalExecutingThreadsLong = new Long(intervalExecutingThreads);
        }
        return intervalExecutingThreadsLong;
    }


    public Long getIntervalStartedThreadsLong()
    {
        if (intervalStartedThreadsLong == null)
        {
            intervalStartedThreadsLong = new Long(intervalStartedThreads);
        }
        return intervalStartedThreadsLong;
    }


    public Long getIntervalPendingThreadsLong()
    {
        if (intervalPendingThreadsLong == null)
        {
            intervalPendingThreadsLong = new Long(intervalPendingThreads);
        }
        return intervalPendingThreadsLong;
    }


    public Long getIntervalPendingTaskCountLong()
    {
        if (intervalPendingTaskCountLong == null)
        {
            intervalPendingTaskCountLong = new Long(intervalPendingTaskCount);
        }
        return intervalPendingTaskCountLong;
    }

    private void clearDataObjects()
    {
        currentlyExecutingThreadsSizeInteger = null;
        startedThreadsSizeInteger = null;
        pendingThreadsSizeInteger = null;
        startedThreadsHighWaterMarkInteger = null;
        pendingTaskCountInteger = null;
        pendingTaskCountHighWaterMarkInteger = null;
        percentThreadsUsedDouble = null;
        intervalExecutingThreadsLong = null;
        intervalStartedThreadsLong = null;
        intervalPendingThreadsLong = null;
        intervalPendingTaskCountLong = null;

    }

    protected String[] getInfraNames()
    {
        return INFRA_NAMES;
    }


}
