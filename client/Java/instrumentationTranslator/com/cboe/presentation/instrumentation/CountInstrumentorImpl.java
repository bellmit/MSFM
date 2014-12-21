// -----------------------------------------------------------------------------------
// Source file: CountInstrumentorImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.CalculatedCountInstrumentor;
import com.cboe.interfaces.instrumentation.CalculatedCountInstrumentorMutable;
import com.cboe.interfaces.instrumentation.InstrumentorTypes;

public class CountInstrumentorImpl extends AbstractInstrumentor implements CalculatedCountInstrumentorMutable
{
    private long count;
    //Calculated Instrumentor Data
    private long intervalCount;
    private long peakCount;
    private double avgCountRate;
    private double peakCountRate;

    private Long countLong;
    private Long intervalCountLong;
    private Long peakCountLong;
    private Double avgCountRateDouble;
    private Double peakCountRateDouble;

    protected CountInstrumentorImpl()
    {
        super();
    }

    public CountInstrumentorImpl(String orbName, String clusterName, com.cboe.instrumentationService.instrumentors.CountInstrumentor countInstrumentor)
    {
        this();
        setData(orbName, clusterName, countInstrumentor);
    }

    public CountInstrumentorImpl(String orbName, String clusterName,
                                com.cboe.instrumentationService.instrumentors.CountInstrumentor countInstrumentor,
                                com.cboe.instrumentationService.instrumentors.CalculatedCountInstrumentor calculatedCountInstrumentor)
    {
        this(orbName, clusterName, countInstrumentor);
        setCalculatedData(calculatedCountInstrumentor);
    }

    /**
     * Sets type of the instrumentor.
     */
    protected void setType()
    {
        this.type = InstrumentorTypes.COUNT;
    }

    public void setData(String orbName, String clusterName, com.cboe.instrumentationService.instrumentors.CountInstrumentor countInstrumentor)
    {
        super.setData(orbName, clusterName, countInstrumentor);
        this.count = countInstrumentor.getCount();
    }

    public void setCalculatedData(com.cboe.instrumentationService.instrumentors.CalculatedCountInstrumentor calculatedCountInstrumentor)
    {
        this.intervalCount = calculatedCountInstrumentor.getIntervalCount();
        this.peakCount = calculatedCountInstrumentor.getPeakCount();
        this.avgCountRate = calculatedCountInstrumentor.getAvgCountRate();
        this.peakCountRate = calculatedCountInstrumentor.getPeakCountRate();
    }

    /**
     * Returns MaxMemory value for the instrumentor object.
     * @return MaxMemory long
     */
    public long getCount()
    {
        return count;
    }

    public long getPeakCount()
    {
        return this.peakCount;
    }

    public long getIntervalCount()
    {
        return this.intervalCount;
    }

    public double getAvgCountRate()
    {
        return this.avgCountRate;
    }

    public double getPeakCountRate()
    {
        return this.peakCountRate;
    }

    /**
     *  Clears the data elements of the instrumentor.
     */
    public void clearData()
    {
        this.count = 0;
        this.peakCount = 0;
        this.intervalCount = 0;
        this.avgCountRate = 0.0;
        this.peakCountRate = 0.0;
        clearDataObjects();
    }

    /**
     *  Increment the instrumentor with the values from
     *  the instrumentor passed in.
     */
    public void instrumentorPlusPlus(CalculatedCountInstrumentor countInstrumentor)
    {
        count += countInstrumentor.getCount();

        if (countInstrumentor.getLastUpdatedTimeMillis() > lastUpdatedTimeMillis)
        {
            lastUpdatedTimeMillis = countInstrumentor.getLastUpdatedTimeMillis();
            lastUpdatedTime = null;
        }

        peakCount += countInstrumentor.getPeakCount();
        intervalCount += countInstrumentor.getIntervalCount();
        avgCountRate += countInstrumentor.getAvgCountRate();
        peakCountRate += countInstrumentor.getPeakCountRate();
        clearDataObjects();
    }

    /**
     *  Clone all the parts of the object.  Clone is used in a very
     *  heavy use method, so not everything is cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        CountInstrumentorImpl countInstrumentor = new CountInstrumentorImpl();

        countInstrumentor.userData = getUserData();
        countInstrumentor.name = getName();
        countInstrumentor.instrumentorKey = getInstrumentorKey();
        countInstrumentor.orbName = getOrbName();
        countInstrumentor.clusterName = getClusterName();
        countInstrumentor.lastUpdatedTimeMillis = getLastUpdatedTimeMillis();

        return countInstrumentor;
    }

    public void setInstrumentedData(CalculatedCountInstrumentor countInstrumentor)
    {
        this.lastUpdatedTimeMillis = countInstrumentor.getLastUpdatedTimeMillis();
        this.lastUpdatedTime = null;

        this.count = countInstrumentor.getCount();

        this.peakCount = countInstrumentor.getPeakCount();
        this.intervalCount = countInstrumentor.getIntervalCount();
        this.avgCountRate = countInstrumentor.getAvgCountRate();
        this.peakCountRate = countInstrumentor.getPeakCountRate();
        clearDataObjects();
    }

    private void clearDataObjects()
    {
        countLong = null;
        peakCountLong = null;
        intervalCountLong = null;
        avgCountRateDouble = null;
        peakCountRateDouble = null;
    }

    public Long getCountLong()
    {
        if (countLong == null)
        {
            countLong = new Long(count);
        }
        return countLong;
    }
    public Long getIntervalCountLong()
    {
        if (intervalCountLong == null)
        {
            intervalCountLong = new Long(intervalCount);
        }
        return intervalCountLong;
    }
    public Long getPeakCountLong()
    {
        if (peakCountLong == null)
        {
            peakCountLong = new Long(peakCount);
        }
        return peakCountLong;
    }
    public Double getAvgCountRateDouble()
    {
        if (avgCountRateDouble == null)
        {
            avgCountRateDouble = new Double(avgCountRate);
        }
        return avgCountRateDouble;
    }
    public Double getPeakCountRateDouble()
    {
        if (peakCountRateDouble == null)
        {
            peakCountRateDouble = new Double(peakCountRate);
        }
        return peakCountRateDouble;
    }

    protected String[] getInfraNames()
    {
        return null;
    }

}
