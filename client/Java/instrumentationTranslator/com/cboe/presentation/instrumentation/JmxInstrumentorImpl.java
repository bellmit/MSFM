package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.CalculatedJmxInstrumentorMutable;
import com.cboe.interfaces.instrumentation.InstrumentorTypes;

import com.cboe.instrumentationService.instrumentors.CalculatedJmxInstrumentor;
import com.cboe.instrumentationService.instrumentors.JmxInstrumentor;

/**
 * Created by IntelliJ IDEA.
 * User: gupta
 * Date: Aug 6, 2008
 * Time: 11:37:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class JmxInstrumentorImpl extends AbstractInstrumentor
        implements CalculatedJmxInstrumentorMutable
{
    private long peakThreadCount;
    private long currentThreadCount;
    private long totalThreadsStarted;
    private long totalCpuTime;

    private long intervalPeakThreadCount;
    private long intervalCurrentThreadCount;
    private long intervalTotalThreadsStarted;
    private long intervalTotalCpuTime;

    public JmxInstrumentorImpl()
    {
        super();
    }

    public JmxInstrumentorImpl(String orbName, String clusterName,
                               com.cboe.instrumentationService.instrumentors.JmxInstrumentor jmxInstrumentor)
    {
        this();
        setData(orbName, clusterName, jmxInstrumentor);
    }

    public JmxInstrumentorImpl(String orbName, String clusterName,
                               com.cboe.instrumentationService.instrumentors.JmxInstrumentor jmxInstrumentor,
                               com.cboe.instrumentationService.instrumentors.CalculatedJmxInstrumentor calculatedJmxInstrumentor)
    {
        this(orbName, clusterName, jmxInstrumentor);
        setCalculatedData(calculatedJmxInstrumentor);
    }


    protected void setType()
    {
        this.type = InstrumentorTypes.JMX;
    }

    protected String[] getInfraNames()
    {
        return new String[0];
    }

    public void setData(String orbName, String clusterName, JmxInstrumentor jmxInstrumentor)
    {
        super.setData(orbName, clusterName, jmxInstrumentor);
        peakThreadCount = jmxInstrumentor.getPeakThreadCount();
        totalCpuTime = jmxInstrumentor.getTotalCPUTime();
        totalThreadsStarted = jmxInstrumentor.getTotalThreadsStarted();
        currentThreadCount = jmxInstrumentor.getCurrentThreadCount();
    }

    public void setCalculatedData(CalculatedJmxInstrumentor calculatedJmxInstrumentor)
    {
        intervalPeakThreadCount = calculatedJmxInstrumentor.getIntervalPeakThreadCount();
        intervalTotalCpuTime = calculatedJmxInstrumentor.getIntervalTotalCPUTime();
        intervalTotalThreadsStarted = calculatedJmxInstrumentor.getIntervalTotalThreadsStarted();
        intervalCurrentThreadCount = calculatedJmxInstrumentor.getIntervalCurrentThreadCount();
    }

    public long getIntervalPeakThreadCount()
    {
        return intervalPeakThreadCount;
    }

    public long getIntervalCurrentThreadCount()
    {
        return intervalCurrentThreadCount;
    }

    public long getIntervalTotalThreadsStarted()
    {
        return intervalTotalThreadsStarted;
    }

    public long getIntervalTotalCPUTime()
    {
        return intervalTotalCpuTime;
    }

    public void clearData()
    {
        this.peakThreadCount = 0;
        this.totalCpuTime = 0;
        this.totalThreadsStarted = 0;
        this.currentThreadCount = 0;

        intervalPeakThreadCount = 0;
        intervalTotalCpuTime = 0;
        intervalTotalThreadsStarted = 0;
        intervalCurrentThreadCount = 0;
    }

    public void instrumentorPlusPlus(
            com.cboe.interfaces.instrumentation.CalculatedJmxInstrumentor jmxInstrumentor)
    {
        peakThreadCount += jmxInstrumentor.getPeakThreadCount();
        totalCpuTime += jmxInstrumentor.getTotalCPUTime();
        totalThreadsStarted += jmxInstrumentor.getTotalThreadsStarted();
        currentThreadCount += jmxInstrumentor.getCurrentThreadCount();

        intervalPeakThreadCount += jmxInstrumentor.getIntervalPeakThreadCount();
        intervalTotalCpuTime += jmxInstrumentor.getIntervalTotalCPUTime();
        intervalTotalThreadsStarted += jmxInstrumentor.getIntervalTotalThreadsStarted();
        intervalCurrentThreadCount += jmxInstrumentor.getIntervalCurrentThreadCount();
        if(jmxInstrumentor.getLastUpdatedTimeMillis() > lastUpdatedTimeMillis)
        {
            lastUpdatedTimeMillis = jmxInstrumentor.getLastUpdatedTimeMillis();
            lastUpdatedTime = null;
        }
    }

    /**
     * Clone all the parts of the object.  Clone is used in a very heavy use method, so not
     * everything is cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        JmxInstrumentorImpl jmxInstrumentor = new JmxInstrumentorImpl();

        jmxInstrumentor.userData = getUserData();
        jmxInstrumentor.name = getName();
        jmxInstrumentor.instrumentorKey = getInstrumentorKey();
        jmxInstrumentor.orbName = getOrbName();
        jmxInstrumentor.clusterName = getClusterName();
        jmxInstrumentor.lastUpdatedTimeMillis = getLastUpdatedTimeMillis();

        return jmxInstrumentor;
    }


    public void setInstrumentedData(
            com.cboe.interfaces.instrumentation.CalculatedJmxInstrumentor jmxInstrumentor)
    {
        lastUpdatedTimeMillis = jmxInstrumentor.getLastUpdatedTimeMillis();
        lastUpdatedTime = null;

        peakThreadCount = jmxInstrumentor.getPeakThreadCount();
        totalCpuTime = jmxInstrumentor.getTotalCPUTime();
        totalThreadsStarted = jmxInstrumentor.getTotalThreadsStarted();
        currentThreadCount = jmxInstrumentor.getCurrentThreadCount();

        intervalPeakThreadCount = jmxInstrumentor.getIntervalPeakThreadCount();
        intervalTotalCpuTime = jmxInstrumentor.getIntervalTotalCPUTime();
        intervalTotalThreadsStarted = jmxInstrumentor.getIntervalTotalThreadsStarted();
        intervalCurrentThreadCount = jmxInstrumentor.getIntervalCurrentThreadCount();
    }

    public long getPeakThreadCount()
    {
        return peakThreadCount;
    }

    public long getCurrentThreadCount()
    {
        return currentThreadCount;
    }

    public long getTotalThreadsStarted()
    {
        return totalThreadsStarted;
    }

    public long getTotalCPUTime()
    {
        return totalCpuTime;
    }
}
