// -----------------------------------------------------------------------------------
// Source file: MethodInstrumentorImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.CalculatedMethodInstrumentor;
import com.cboe.interfaces.instrumentation.CalculatedMethodInstrumentorMutable;
import com.cboe.interfaces.instrumentation.InstrumentorTypes;
import com.cboe.client.xml.bind.GIMethodInstrumentorType;
import com.cboe.client.xml.bind.GIContextDetailType;

public class MethodInstrumentorImpl extends AbstractProductClassInstrumentor implements CalculatedMethodInstrumentorMutable
{
    public static final String[] INFRA_NAMES =
            {
                "POAMI"
            };

    private long totalCalls;
    private long numExceptions;
    private long numSuccess;
    private double methodTime;
    private long maxMethodTime;
    private double sumOfSquareMethodTime;
    //Calculated Instrumentor Data
    private long peakCalls;
    private long peakExceptions;
    private double peakMethodTime;
    private long intervalCalls;
    private long intervalExceptions;
    private double intervalMethodTime;
    private double peakCallsRate;
    private double peakExceptionsRate;
    private double peakResponseTime;
    private double avgCallsRate;
    private double avgExceptionsRate;
    private double avgResponseTime;

    private Long totalCallsLong;
    private Long numExceptionsLong;
    private Long numSuccessLong;
    private Double methodTimeDouble;
    private Long maxMethodTimeLong;
    private Double sumOfSquareMethodTimeDouble;
    private Long peakCallsLong;
    private Long peakExceptionsLong;
    private Double peakMethodTimeDouble;
    private Long intervalCallsLong;
    private Long intervalExceptionsLong;
    private Double intervalMethodTimeDouble;
    private Double peakCallsRateDouble;
    private Double peakExceptionsRateDouble;
    private Double peakResponseTimeDouble;
    private Double avgCallsRateDouble;
    private Double avgExceptionsRateDouble;
    private Double avgResponseTimeDouble;

    /**
     * Default Constructor
     */
    protected MethodInstrumentorImpl()
    {
        super();
    }

    /**
     * Constructor for use with XML generated intrumentors
     */
    protected MethodInstrumentorImpl(String orbName, String clusterName,
                                     GIContextDetailType contextDetailType,
                                     GIMethodInstrumentorType methodInstrumentorType)
    {
        this();
        setData(orbName, clusterName, contextDetailType, methodInstrumentorType);
    }

    /**
     * Constructor for use with infra instrumentation.
     */
    public MethodInstrumentorImpl(String orbName, String clusterName,
                                  com.cboe.instrumentationService.instrumentors.MethodInstrumentor methodInstrumentor)
    {
        this();
        setData(orbName, clusterName, methodInstrumentor);
    }

    /**
     * Constructor for use with infra instrumentation.
     */
    public MethodInstrumentorImpl(String orbName, String clusterName,
                                  com.cboe.instrumentationService.instrumentors.MethodInstrumentor methodInstrumentor,
                                  com.cboe.instrumentationService.instrumentors.CalculatedMethodInstrumentor calculatedMethodInstrumentor)
    {
        this(orbName, clusterName, methodInstrumentor);
        setCalculatedData(calculatedMethodInstrumentor);
    }

    /**
     * Sets type of the instrumentor.
     */
    protected void setType()
    {
        this.type = InstrumentorTypes.METHOD;
    }

    protected void setData(String orbName, String clusterName, GIContextDetailType contextDetailType,
                           GIMethodInstrumentorType methodInstrumentorType)
    {
        super.setData(orbName, clusterName, contextDetailType.getName(), methodInstrumentorType.getUserData());
        this.totalCalls = methodInstrumentorType.getCalls();
        this.numExceptions = methodInstrumentorType.getExceptions();
        this.numSuccess = totalCalls - numExceptions;
        this.methodTime = methodInstrumentorType.getMethodTime();
        this.maxMethodTime = methodInstrumentorType.getMaxMethodTime();
        this.sumOfSquareMethodTime = methodInstrumentorType.getSumOfSquareMethodTime();
    }

    public void setCalculatedData(com.cboe.instrumentationService.instrumentors.CalculatedMethodInstrumentor calculatedMethodInstrumentor)
    {
        this.peakCalls = calculatedMethodInstrumentor.getPeakCalls();
        this.peakExceptions = calculatedMethodInstrumentor.getPeakExceptions();
        this.peakMethodTime = calculatedMethodInstrumentor.getPeakMethodTime();
        this.intervalCalls = calculatedMethodInstrumentor.getIntervalCalls();
        this.intervalExceptions = calculatedMethodInstrumentor.getIntervalExceptions();
        this.intervalMethodTime = calculatedMethodInstrumentor.getIntervalMethodTime();
        this.peakCallsRate = calculatedMethodInstrumentor.getPeakCallsRate();
        this.peakExceptionsRate = calculatedMethodInstrumentor.getPeakExceptionsRate();
        this.peakResponseTime = calculatedMethodInstrumentor.getPeakResponseTime();
        this.avgCallsRate = calculatedMethodInstrumentor.getAvgCallsRate();
        this.avgExceptionsRate = calculatedMethodInstrumentor.getAvgExceptionsRate();
        this.avgResponseTime = calculatedMethodInstrumentor.getAvgResponseTime();
    }

    public void setData(String orbName, String clusterName,
                           com.cboe.instrumentationService.instrumentors.MethodInstrumentor methodInstrumentor)
    {
        super.setData(orbName, clusterName, methodInstrumentor);
        this.totalCalls = methodInstrumentor.getCalls();
        this.numExceptions = methodInstrumentor.getExceptions();
        this.numSuccess = totalCalls - numExceptions;
        this.methodTime = methodInstrumentor.getMethodTime();
        this.maxMethodTime = methodInstrumentor.getMaxMethodTime();
        this.sumOfSquareMethodTime = methodInstrumentor.getSumOfSquareMethodTime();
        // session keys is lazily initialized
        this.sessionKeys = null;
    }

    /**
     * Returns Total Calls value for the method instrumentor object.
     * @return Total Calls long
     */
    public long getTotalCalls()
    {
        return totalCalls;
    }

    /**
     * Returns NumExceptions value for the method instrumentor object.
     * @return NumExceptions long
     */
    public long getNumExceptions()
    {
        return numExceptions;
    }

    /**
     * Returns NumSuccess value for the method instrumentor object.
     * @return NumSuccess long
     */
    public long getNumSuccess()
    {
        return numSuccess;
    }

    /**
     * Returns ResponseTime value for the method instrumentor object.
     * @return ResponseTime double
     */
    public double getMethodTime()
    {
        return methodTime;
    }

    /**
     * Returns SumOfSquareMethodTime value for the method instrumentor object.
     * @return SumOfSquareMethodTime double
     */
    public double getSumOfSquareMethodTime()
    {
        return sumOfSquareMethodTime;
    }

    /**
     * Returns MaxMethodTime value for the method instrumentor object.
     * @return MaxMethodTime long
     */
    public long getMaxMethodTime()
    {
        return maxMethodTime;
    }

    /**
     *  Clears the data elements of the instrumentor.
     */
    public void clearData()
    {
        this.totalCalls = 0;
        this.numExceptions = 0;
        this.numSuccess = 0;
        this.methodTime = 0.0;
        this.maxMethodTime = 0;
        this.sumOfSquareMethodTime = 0.0;
        //Calculated Instrumentor data
        this.peakCalls = 0;
        this.peakExceptions = 0;
        this.peakMethodTime = 0;
        this.intervalCalls = 0;
        this.intervalExceptions = 0;
        this.intervalMethodTime = 0.0;
        this.peakCallsRate = 0.0;
        this.peakExceptionsRate = 0.0;
        this.peakResponseTime = 0.0;
        this.avgCallsRate = 0.0;
        this.avgExceptionsRate = 0.0;
        this.avgResponseTime = 0.0;
        clearDataObjects();
    }

    public long getPeakCalls()
    {
        return this.peakCalls;
    }

    public long getPeakExceptions()
    {
        return this.peakExceptions;
    }

    public double getPeakMethodTime()
    {
        return this.peakMethodTime;
    }

    public long getIntervalCalls()
    {
        return this.intervalCalls;
    }

    public long getIntervalExceptions()
    {
        return this.intervalExceptions;
    }

    public double getIntervalMethodTime()
    {
        return this.intervalMethodTime;
    }

    public double getPeakCallsRate()
    {
        return this.peakCallsRate;
    }

    public double getPeakExceptionsRate()
    {
        return this.peakExceptionsRate;
    }

    public double getPeakResponseTime()
    {
        return this.peakResponseTime;
    }

    public double getAvgCallsRate()
    {
        return this.avgCallsRate;
    }

    public double getAvgExceptionsRate()
    {
        return this.avgExceptionsRate;
    }

    public double getAvgResponseTime()
    {
        return this.avgResponseTime;
    }

    /**
     *  Increment the instrumentor with the values from
     *  the instrumentor passed in.
     */
    public void instrumentorPlusPlus(CalculatedMethodInstrumentor methodInstrumentor)
    {

        this.totalCalls += methodInstrumentor.getTotalCalls();
        this.numExceptions += methodInstrumentor.getNumExceptions();
        this.numSuccess += methodInstrumentor.getNumSuccess();
        this.methodTime += methodInstrumentor.getMethodTime();
        if (methodInstrumentor.getMaxMethodTime() > this.getMaxMethodTime())
        {
            this.maxMethodTime = methodInstrumentor.getMaxMethodTime();
        }
        if (sumOfSquareMethodTime == 0 || totalCalls == 0)
        {
            this.sumOfSquareMethodTime = 0.0;
        }
        else
        {
            this.sumOfSquareMethodTime = (sumOfSquareMethodTime * totalCalls) / totalCalls ;
        }
        if (methodInstrumentor.getLastUpdatedTimeMillis() > lastUpdatedTimeMillis)
        {
            lastUpdatedTimeMillis = methodInstrumentor.getLastUpdatedTimeMillis();
            lastUpdatedTime = null;
        }

        // Calculated Fields
        if (methodInstrumentor.getPeakCalls() > this.peakCalls)
        {
            this.peakCalls = methodInstrumentor.getPeakCalls();
        }
        if (methodInstrumentor.getPeakExceptions() > this.peakExceptions)
        {
            this.peakExceptions = methodInstrumentor.getPeakExceptions();
        }
        if (methodInstrumentor.getPeakMethodTime() > this.peakMethodTime)
        {
            this.peakMethodTime = methodInstrumentor.getPeakMethodTime();
        }
        this.intervalCalls += methodInstrumentor.getIntervalCalls();
        this.intervalExceptions += methodInstrumentor.getIntervalExceptions();
        this.intervalMethodTime += methodInstrumentor.getIntervalMethodTime();
        if (methodInstrumentor.getPeakCallsRate() > this.peakCallsRate)
        {
            this.peakCallsRate = methodInstrumentor.getPeakCallsRate();
        }
        if (methodInstrumentor.getPeakExceptionsRate() > this.peakExceptionsRate)
        {
            this.peakExceptionsRate = methodInstrumentor.getPeakExceptionsRate();
        }
        if (methodInstrumentor.getPeakResponseTime() > this.peakResponseTime)
        {
            this.peakResponseTime = methodInstrumentor.getPeakResponseTime();
        }
        this.avgCallsRate += methodInstrumentor.getAvgCallsRate();
        this.avgExceptionsRate += methodInstrumentor.getAvgExceptionsRate();
        this.avgResponseTime += methodInstrumentor.getAvgResponseTime();
        clearDataObjects();
    }

    /**
     *  Clone all the parts of the object.  Clone is used in a very
     *  heavy use method, so not everything is cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        MethodInstrumentorImpl methodInstrumentor = new MethodInstrumentorImpl();

        methodInstrumentor.userData = getUserData();
        methodInstrumentor.name = getName();
        methodInstrumentor.instrumentorKey = getInstrumentorKey();
        methodInstrumentor.orbName = getOrbName();
        methodInstrumentor.clusterName = getClusterName();
        methodInstrumentor.lastUpdatedTimeMillis = getLastUpdatedTimeMillis();

        methodInstrumentor.setInstrumentedData(this);

        return methodInstrumentor;
    }

    public void setInstrumentedData(CalculatedMethodInstrumentor methodInstrumentor)
    {
        this.userData = methodInstrumentor.getUserData();

        this.lastUpdatedTimeMillis = methodInstrumentor.getLastUpdatedTimeMillis();
        this.lastUpdatedTime = null;

        this.totalCalls = methodInstrumentor.getTotalCalls();
        this.numExceptions = methodInstrumentor.getNumExceptions();
        this.numSuccess = methodInstrumentor.getNumSuccess();
        this.methodTime = methodInstrumentor.getMethodTime();
        this.maxMethodTime = methodInstrumentor.getMaxMethodTime();
        this.sumOfSquareMethodTime = methodInstrumentor.getSumOfSquareMethodTime();
        // sessionKeys, sessionProductClasses and productClasses are lazily initialized from
        // user data.
        this.sessionKeys = null;
        this.sessionProductClasses = null;
        this.productClasses = null;

        this.peakCalls = methodInstrumentor.getPeakCalls();
        this.peakExceptions  = methodInstrumentor.getPeakExceptions();
        this.peakMethodTime  = methodInstrumentor.getPeakMethodTime();
        this.intervalCalls  = methodInstrumentor.getIntervalCalls();
        this.intervalExceptions  = methodInstrumentor.getIntervalExceptions();
        this.intervalMethodTime  = methodInstrumentor.getIntervalMethodTime();
        this.peakCallsRate  = methodInstrumentor.getPeakCallsRate();
        this.peakExceptionsRate  = methodInstrumentor.getPeakExceptionsRate();
        this.peakResponseTime  = methodInstrumentor.getPeakResponseTime();
        this.avgCallsRate  = methodInstrumentor.getAvgCallsRate();
        this.avgExceptionsRate  = methodInstrumentor.getAvgExceptionsRate();
        this.avgResponseTime  = methodInstrumentor.getAvgResponseTime();
        clearDataObjects();
    }


    public Long getTotalCallsLong()
    {
        if (totalCallsLong == null)
        {
            totalCallsLong = new Long(totalCalls);
        }
        return totalCallsLong;
    }

    public Long getNumExceptionsLong()
    {
        if (numExceptionsLong == null)
        {
            numExceptionsLong = new Long(numExceptions);
        }
        return numExceptionsLong;
    }

    public Long getNumSuccessLong()
    {
        if (numSuccessLong == null)
        {
            numSuccessLong = new Long(numSuccess);
        }
        return numSuccessLong;
    }

    public Double getMethodTimeDouble()
    {
        if (methodTimeDouble == null)
        {
            methodTimeDouble = new Double(methodTime / 1000000.0);
        }
        return methodTimeDouble;
    }

    public Long getMaxMethodTimeLong()
    {
        if (maxMethodTimeLong == null)
        {
            maxMethodTimeLong = new Long(maxMethodTime / 1000000);
        }
        return maxMethodTimeLong;
    }

    public Double getSumOfSquareMethodTimeDouble()
    {
        if (sumOfSquareMethodTimeDouble == null)
        {
            sumOfSquareMethodTimeDouble = new Double(sumOfSquareMethodTime);
        }
        return sumOfSquareMethodTimeDouble;
    }

    public Long getPeakCallsLong()
    {
        if (peakCallsLong == null)
        {
            peakCallsLong = new Long(peakCalls);
        }
        return peakCallsLong;
    }

    public Long getPeakExceptionsLong()
    {
        if (peakExceptionsLong == null)
        {
            peakExceptionsLong = new Long(peakExceptions);
        }
        return peakExceptionsLong;
    }

    public Double getPeakMethodTimeDouble()
    {
        if (peakMethodTimeDouble == null)
        {
            peakMethodTimeDouble = new Double(peakMethodTime / 1000000.0);
        }
        return peakMethodTimeDouble;
    }

    public Long getIntervalCallsLong()
    {
        if (intervalCallsLong == null)
        {
            intervalCallsLong = new Long(intervalCalls);
        }
        return intervalCallsLong;
    }

    public Long getIntervalExceptionsLong()
    {
        if (intervalExceptionsLong == null)
        {
            intervalExceptionsLong = new Long(intervalExceptions);
        }
        return intervalExceptionsLong;
    }

    public Double getIntervalMethodTimeDouble()
    {
        if (intervalMethodTimeDouble == null)
        {
            intervalMethodTimeDouble = new Double(intervalMethodTime / 1000000.0);
        }
        return intervalMethodTimeDouble;
    }

    public Double getPeakCallsRateDouble()
    {
        if (peakCallsRateDouble == null)
        {
            peakCallsRateDouble = new Double(peakCallsRate);
        }
        return peakCallsRateDouble;
    }

    public Double getPeakExceptionsRateDouble()
    {
        if (peakExceptionsRateDouble == null)
        {
            peakExceptionsRateDouble = new Double(peakExceptionsRate);
        }
        return peakExceptionsRateDouble;
    }

    public Double getPeakResponseTimeDouble()
    {
        if (peakResponseTimeDouble == null)
        {
            peakResponseTimeDouble = new Double(peakResponseTime / 1000000.0);
        }
        return peakResponseTimeDouble;
    }

    public Double getAvgCallsRateDouble()
    {
        if (avgCallsRateDouble == null)
        {
            avgCallsRateDouble = new Double(avgCallsRate);
        }
        return avgCallsRateDouble;
    }

    public Double getAvgExceptionsRateDouble()
    {
        if (avgExceptionsRateDouble == null)
        {
            avgExceptionsRateDouble = new Double(avgExceptionsRate);
        }
        return avgExceptionsRateDouble;
    }

    public Double getAvgResponseTimeDouble()
    {
        if (avgResponseTimeDouble == null)
        {
            avgResponseTimeDouble = new Double(avgResponseTime / 1000000.0);
        }
        return avgResponseTimeDouble;
    }

    private void clearDataObjects()
    {
        totalCallsLong = null;
        numExceptionsLong = null;
        numSuccessLong = null;
        methodTimeDouble = null;
        maxMethodTimeLong = null;
        sumOfSquareMethodTimeDouble = null;
        peakCallsLong = null;
        peakExceptionsLong = null;
        peakMethodTimeDouble = null;
        intervalCallsLong = null;
        intervalExceptionsLong = null;
        intervalMethodTimeDouble = null;
        peakCallsRateDouble = null;
        peakExceptionsRateDouble = null;
        peakResponseTimeDouble = null;
        avgCallsRateDouble = null;
        avgExceptionsRateDouble = null;
        avgResponseTimeDouble = null;
    }

    protected String[] getInfraNames()
    {
        return INFRA_NAMES;
    }
}
