// -----------------------------------------------------------------------------------
// Source file: QueueInstrumentorImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.CalculatedQueueInstrumentor;
import com.cboe.interfaces.instrumentation.CalculatedQueueInstrumentorMutable;
import com.cboe.interfaces.instrumentation.InstrumentorTypes;

import com.cboe.presentation.common.formatters.ProcessStatus;

import com.cboe.client.xml.bind.GIQueueInstrumentorType;
import com.cboe.client.xml.bind.GIContextDetailType;

public class QueueInstrumentorImpl extends AbstractProductClassInstrumentor implements CalculatedQueueInstrumentorMutable
{
    public static final String[] INFRA_NAMES =
            {
                "POAQ"
            };
    private long numEnqueued;
    private long numDequeued;
    private long numFlushed;
    private long numOverlaid;
    private long peakSize;
    private long currentSize;
    private short status;
    //Calculated Instrumentor Data
    private long peakEnqueued;
    private long peakDequeued;
    private long peakFlushed;
    private long peakOverlaid;
    private long intervalEnqueued;
    private long intervalDequeued;
    private long intervalFlushed;
    private long intervalOverlaid;
    private double peakEnqueuedRate;
    private double peakDequeuedRate;
    private double peakFlushedRate;
    private double peakOverlaidRate;
    private double avgEnqueuedRate;
    private double avgDequeuedRate;
    private double avgFlushedRate;
    private double avgOverlaidRate;

    private Long numEnqueuedLong;
    private Long numDequeuedLong;
    private Long numFlushedLong;
    private Long numOverlaidLong;
    private Long peakSizeLong;
    private Long currentSizeLong;
    private Short statusShort;
    private Long peakEnqueuedLong;
    private Long peakDequeuedLong;
    private Long peakFlushedLong;
    private Long peakOverlaidLong;
    private Long intervalEnqueuedLong;
    private Long intervalDequeuedLong;
    private Long intervalFlushedLong;
    private Long intervalOverlaidLong;
    private Double peakEnqueuedRateDouble;
    private Double peakDequeuedRateDouble;
    private Double peakFlushedRateDouble;
    private Double peakOverlaidRateDouble;
    private Double avgEnqueuedRateDouble;
    private Double avgDequeuedRateDouble;
    private Double avgFlushedRateDouble;
    private Double avgOverlaidRateDouble;



    protected QueueInstrumentorImpl()
    {
        super();
    }

    protected QueueInstrumentorImpl(String orbName, String clusterName,
                                    GIContextDetailType contextDetailType,
                                    GIQueueInstrumentorType queueInstrumentorType)
    {
        this();
        setData(orbName, clusterName, contextDetailType, queueInstrumentorType);
    }

    protected QueueInstrumentorImpl(String orbName, String clusterName,
                                    com.cboe.instrumentationService.instrumentors.QueueInstrumentor queueInstrumentor)
    {
        this();
        setData(orbName, clusterName, queueInstrumentor);
    }

    protected QueueInstrumentorImpl(String orbName, String clusterName,
                                    com.cboe.instrumentationService.instrumentors.QueueInstrumentor queueInstrumentor,
                                    com.cboe.instrumentationService.instrumentors.CalculatedQueueInstrumentor calculatedQueueInstrumentor)
    {
        this(orbName, clusterName, queueInstrumentor);
        setCalculatedData(calculatedQueueInstrumentor);
    }

    /**
     * Sets type of the instrumentor.
     */
    protected void setType()
    {
        this.type = InstrumentorTypes.QUEUE;
    }

    private void setData(String orbName, String clusterName, GIContextDetailType contextDetailType,
                         GIQueueInstrumentorType queueInstrumentorType)
    {
        super.setData(orbName, clusterName, contextDetailType.getName(), queueInstrumentorType.getUserData());
        numEnqueued = queueInstrumentorType.getEnqueued();
        numDequeued = queueInstrumentorType.getDequeued();
        numFlushed = queueInstrumentorType.getFlushed();
        numOverlaid = queueInstrumentorType.getOverlaid();
        peakSize = queueInstrumentorType.getHighWaterMark();
        currentSize = queueInstrumentorType.getCurrentSize();
        status = queueInstrumentorType.getStatus();
    }

    public void setData(String orbName, String clusterName,
                           com.cboe.instrumentationService.instrumentors.QueueInstrumentor queueInstrumentor)
    {
        super.setData(orbName, clusterName, queueInstrumentor);
        this.numEnqueued = queueInstrumentor.getEnqueued();
        this.numDequeued = queueInstrumentor.getDequeued();
        this.numFlushed = queueInstrumentor.getFlushed();
        this.numOverlaid = queueInstrumentor.getOverlaid();
        this.peakSize = queueInstrumentor.getHighWaterMark();
        this.currentSize = queueInstrumentor.getCurrentSize();
        this.status = queueInstrumentor.getStatus();
        // sessionKeys is lazily initialized
    }

    public void setCalculatedData(com.cboe.instrumentationService.instrumentors.CalculatedQueueInstrumentor calculatedQueueInstrumentor)
    {
        this.peakEnqueued = calculatedQueueInstrumentor.getPeakEnqueued();
        this.peakDequeued = calculatedQueueInstrumentor.getPeakDequeued();
        this.peakFlushed = calculatedQueueInstrumentor.getPeakFlushed();
        this.peakOverlaid = calculatedQueueInstrumentor.getPeakOverlaid();
        this.intervalEnqueued = calculatedQueueInstrumentor.getIntervalEnqueued();
        this.intervalDequeued = calculatedQueueInstrumentor.getIntervalDequeued();
        this.intervalFlushed = calculatedQueueInstrumentor.getIntervalFlushed();
        this.intervalOverlaid = calculatedQueueInstrumentor.getIntervalOverlaid();
        this.peakEnqueuedRate = calculatedQueueInstrumentor.getPeakEnqueuedRate();
        this.peakDequeuedRate = calculatedQueueInstrumentor.getPeakDequeuedRate();
        this.peakFlushedRate = calculatedQueueInstrumentor.getPeakFlushedRate();
        this.peakOverlaidRate = calculatedQueueInstrumentor.getPeakOverlaidRate();
        this.avgEnqueuedRate = calculatedQueueInstrumentor.getAvgEnqueuedRate();
        this.avgDequeuedRate = calculatedQueueInstrumentor.getAvgDequeuedRate();
        this.avgFlushedRate = calculatedQueueInstrumentor.getAvgFlushedRate();
        this.avgOverlaidRate = calculatedQueueInstrumentor.getAvgOverlaidRate();
    }

    /**
     * Returns NumEnqueued value for the queue instrumentor object.
     * @return NumEnqueued long
     */
    public long getNumEnqueued()
    {
        return numEnqueued;
    }

    /**
     * Returns NumDequeued value for the queue instrumentor object.
     * @return NumDequeued long
     */
    public long getNumDequeued()
    {
        return numDequeued;
    }

    /**
     * Returns NumFlushed value for the queue instrumentor object.
     * @return NumFlushed long
     */
    public long getNumFlushed()
    {
        return numFlushed;
    }

    /**
     * Returns NumOverlaid value for the queue instrumentor object.
     * @return NumOverlaid long
     */
    public long getNumOverlaid()
    {
        return numOverlaid;
    }

    /**
     * Returns PeakSize value for the queue instrumentor object.
     * @return PeakSize long
     */
    public long getPeakSize()
    {
        return peakSize;
    }

    /**
     * Returns CurrentSize value for the queue instrumentor object.
     * @return CurrentSize long
     */
    public long getCurrentSize()
    {
        return currentSize;
    }

    /**
     * Returns Status value for the queue instrumentor object.
     * @return Status short
     */
    public short getStatus()
    {
        return status;
    }

    public long getPeakEnqueued()
    {
        return this.peakEnqueued;
    }

    public long getPeakDequeued()
    {
        return this.peakDequeued;
    }

    public long getPeakFlushed()
    {
        return this.peakFlushed;
    }

    public long getPeakOverlaid()
    {
        return this.peakOverlaid;
    }

    public long getIntervalEnqueued()
    {
        return this.intervalEnqueued;
    }

    public long getIntervalDequeued()
    {
        return this.intervalDequeued;
    }

    public long getIntervalFlushed()
    {
        return this.intervalFlushed;
    }

    public long getIntervalOverlaid()
    {
        return this.intervalOverlaid;
    }

    public double getPeakEnqueuedRate()
    {
        return this.peakEnqueuedRate;
    }

    public double getPeakDequeuedRate()
    {
        return this.peakDequeuedRate;
    }

    public double getPeakFlushedRate()
    {
        return this.peakFlushedRate;
    }

    public double getPeakOverlaidRate()
    {
        return this.peakOverlaidRate;
    }

    public double getAvgEnqueuedRate()
    {
        return this.avgEnqueuedRate;
    }

    public double getAvgDequeuedRate()
    {
        return this.avgDequeuedRate;
    }

    public double getAvgFlushedRate()
    {
        return this.avgFlushedRate;
    }

    public double getAvgOverlaidRate()
    {
        return this.avgOverlaidRate;
    }

    /**
     *  Clears the data elements of the instrumentor.
     */
    public void clearData()
    {
        numEnqueued = 0;
        numDequeued = 0;
        numFlushed = 0;
        numOverlaid = 0;
        peakSize = 0;
        currentSize = 0;
        status = 0;
        this.peakEnqueued = 0;
        this.peakDequeued = 0;
        this.peakFlushed = 0;
        this.peakOverlaid = 0;
        this.intervalEnqueued = 0;
        this.intervalDequeued = 0;
        this.intervalFlushed = 0;
        this.intervalOverlaid = 0;
        this.peakEnqueuedRate = 0;
        this.peakDequeuedRate = 0;
        this.peakFlushedRate = 0;
        this.peakOverlaidRate = 0;
        this.avgEnqueuedRate = 0;
        this.avgDequeuedRate = 0;
        this.avgFlushedRate = 0;
        this.avgOverlaidRate = 0;
        this.lastUpdatedTime = null;
        clearDataObjects();
    }

    /**
     *  Increment the instrumentor with the values from
     *  the instrumentor passed in.
     */
    public void instrumentorPlusPlus(CalculatedQueueInstrumentor queueInstrumentor)
    {
        numEnqueued += queueInstrumentor.getNumEnqueued();
        numDequeued += queueInstrumentor.getNumDequeued();
        numFlushed += queueInstrumentor.getNumFlushed();
        numOverlaid += queueInstrumentor.getNumOverlaid();
        if (queueInstrumentor.getPeakSize() > peakSize)
        {
            peakSize = queueInstrumentor.getPeakSize();
        }
        currentSize += queueInstrumentor.getCurrentSize();

        // Set the status to the worst status
        short qiStatus = queueInstrumentor.getStatus();

        if (qiStatus != ProcessStatus.NOT_REPORTED)
        {
            if (status == ProcessStatus.NOT_REPORTED)
            {
                if (qiStatus == ProcessStatus.THREAD_NOT_STARTED || qiStatus == ProcessStatus.YELLOW)
                {
                    status = ProcessStatus.YELLOW;
                }
                else if (qiStatus == ProcessStatus.THREAD_EXITED || qiStatus == ProcessStatus.DOWN || qiStatus == ProcessStatus.RED)
                {
                    status = ProcessStatus.RED;
                }
                else
                {
                    status = ProcessStatus.GREEN;
                }

            }
            else if (status == ProcessStatus.GREEN)
            {
                if (qiStatus == ProcessStatus.THREAD_NOT_STARTED)
                {
                    status = ProcessStatus.YELLOW;
                }
                else if (qiStatus == ProcessStatus.THREAD_EXITED || qiStatus == ProcessStatus.DOWN)
                {
                    status = ProcessStatus.RED;
                }
            }
            else if (status == ProcessStatus.YELLOW)
            {
                if (qiStatus == ProcessStatus.THREAD_EXITED || qiStatus == ProcessStatus.DOWN)
                {
                    status = ProcessStatus.RED;
                }
            }
        }

        if (queueInstrumentor.getLastUpdatedTimeMillis() > lastUpdatedTimeMillis)
        {
            lastUpdatedTimeMillis = queueInstrumentor.getLastUpdatedTimeMillis();
            lastUpdatedTime = null;
        }
        if (queueInstrumentor.getPeakEnqueued() > peakEnqueued)
        {
            peakEnqueued = queueInstrumentor.getPeakEnqueued();
        }
        if (queueInstrumentor.getPeakDequeued() > peakDequeued)
        {
            peakDequeued = queueInstrumentor.getPeakDequeued();
        }
        if (queueInstrumentor.getPeakFlushed() > peakFlushed)
        {
            peakFlushed = queueInstrumentor.getPeakFlushed();
        }
        if (queueInstrumentor.getPeakOverlaid() > peakOverlaid)
        {
            peakOverlaid = queueInstrumentor.getPeakOverlaid();
        }
        intervalEnqueued += queueInstrumentor.getIntervalEnqueued();
        intervalDequeued += queueInstrumentor.getIntervalDequeued();
        intervalFlushed += queueInstrumentor.getIntervalFlushed();
        intervalOverlaid += queueInstrumentor.getIntervalOverlaid();
        if (queueInstrumentor.getPeakEnqueuedRate() > peakEnqueuedRate)
        {
            peakEnqueuedRate = queueInstrumentor.getPeakEnqueuedRate();
        }
        if (queueInstrumentor.getPeakDequeuedRate() > peakDequeuedRate)
        {
            peakDequeuedRate = queueInstrumentor.getPeakDequeuedRate();
        }
        if (queueInstrumentor.getPeakFlushedRate() > peakFlushedRate)
        {
            peakFlushedRate = queueInstrumentor.getPeakFlushedRate();
        }
        if (queueInstrumentor.getPeakOverlaidRate() > peakOverlaidRate)
        {
            peakOverlaidRate = queueInstrumentor.getPeakOverlaidRate();
        }
        avgEnqueuedRate += queueInstrumentor.getAvgEnqueuedRate();
        avgDequeuedRate += queueInstrumentor.getAvgDequeuedRate();
        avgFlushedRate += queueInstrumentor.getAvgFlushedRate();
        avgOverlaidRate += queueInstrumentor.getAvgOverlaidRate();
        clearDataObjects();
    }

    /**
     *  Clone all the parts of the object.  Clone is used in a very
     *  heavy use method, so not everything is cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        QueueInstrumentorImpl queueInstrumentor = new QueueInstrumentorImpl();

        queueInstrumentor.userData = getUserData();
        queueInstrumentor.name = getName();
        queueInstrumentor.instrumentorKey = getInstrumentorKey();
        queueInstrumentor.orbName = getOrbName();
        queueInstrumentor.clusterName = getClusterName();
        queueInstrumentor.lastUpdatedTimeMillis = getLastUpdatedTimeMillis();

        queueInstrumentor.setInstrumentedData(this);

        return queueInstrumentor;
    }

    public void setInstrumentedData(CalculatedQueueInstrumentor queueInstrumentor)
    {
        this.userData = queueInstrumentor.getUserData();
        this.lastUpdatedTimeMillis = queueInstrumentor.getLastUpdatedTimeMillis();
        this.lastUpdatedTime = null;

        this.numEnqueued = queueInstrumentor.getNumEnqueued();
        this.numDequeued = queueInstrumentor.getNumDequeued();
        this.numFlushed = queueInstrumentor.getNumFlushed();
        this.numOverlaid = queueInstrumentor.getNumOverlaid();
        this.peakSize = queueInstrumentor.getPeakSize();
        this.currentSize = queueInstrumentor.getCurrentSize();

        this.peakEnqueued = queueInstrumentor.getPeakEnqueued();
        this.peakDequeued = queueInstrumentor.getPeakDequeued();
        this.peakFlushed = queueInstrumentor.getPeakFlushed();
        this.peakOverlaid = queueInstrumentor.getPeakOverlaid();
        this.intervalEnqueued = queueInstrumentor.getIntervalEnqueued();
        this.intervalDequeued = queueInstrumentor.getIntervalDequeued();
        this.intervalFlushed = queueInstrumentor.getIntervalFlushed();
        this.intervalOverlaid = queueInstrumentor.getIntervalOverlaid();
        this.peakEnqueuedRate = queueInstrumentor.getPeakEnqueuedRate();
        this.peakDequeuedRate = queueInstrumentor.getPeakDequeuedRate();
        this.peakFlushedRate = queueInstrumentor.getPeakFlushedRate();
        this.peakOverlaidRate= queueInstrumentor.getPeakOverlaidRate();
        this.avgEnqueuedRate = queueInstrumentor.getAvgEnqueuedRate();
        this.avgDequeuedRate = queueInstrumentor.getAvgDequeuedRate();
        this.avgFlushedRate = queueInstrumentor.getAvgFlushedRate();
        this.avgOverlaidRate = queueInstrumentor.getAvgOverlaidRate();

        this.status = queueInstrumentor.getStatus();
        // sessionKeys, sessionProductClasses and productClasses are lazily initialized from
        // user data.
        this.sessionKeys = null;
        this.sessionProductClasses = null;
        this.productClasses = null;
        clearDataObjects();

    }


    public Long getNumEnqueuedLong()
    {
        if (numEnqueuedLong == null)
        {
            numEnqueuedLong = new Long(numEnqueued);
        }
        return numEnqueuedLong;
    }


    public Long getNumDequeuedLong()
    {
        if (numDequeuedLong == null)
        {
            numDequeuedLong = new Long(numDequeued);
        }
        return numDequeuedLong;
    }


    public Long getNumFlushedLong()
    {
        if (numFlushedLong == null)
        {
            numFlushedLong = new Long(numFlushed);
        }
        return numFlushedLong;
    }


    public Long getNumOverlaidLong()
    {
        if (numOverlaidLong == null)
        {
            numOverlaidLong = new Long(numOverlaid);
        }
        return numOverlaidLong;
    }


    public Long getPeakSizeLong()
    {
        if (peakSizeLong == null)
        {
            peakSizeLong = new Long(peakSize);
        }
        return peakSizeLong;
    }


    public Long getCurrentSizeLong()
    {
        if (currentSizeLong == null)
        {
            currentSizeLong = new Long(currentSize);
        }
        return currentSizeLong;
    }


    public Short getStatusShort()
    {
        if (statusShort == null)
        {
            statusShort = new Short(status);
        }
        return statusShort;
    }


    public Long getPeakEnqueuedLong()
    {
        if (peakEnqueuedLong == null)
        {
            peakEnqueuedLong = new Long(peakEnqueued);
        }
        return peakEnqueuedLong;
    }


    public Long getPeakDequeuedLong()
    {
        if (peakDequeuedLong == null)
        {
            peakDequeuedLong = new Long(peakDequeued);
        }
        return peakDequeuedLong;
    }


    public Long getPeakFlushedLong()
    {
        if (peakFlushedLong == null)
        {
            peakFlushedLong = new Long(peakFlushed);
        }
        return peakFlushedLong;
    }

    public Long getPeakOverlaidLong()
    {
        if (peakOverlaidLong == null)
        {
            peakOverlaidLong = new Long(peakOverlaid);
        }
        return peakOverlaidLong;
    }


    public Long getIntervalEnqueuedLong()
    {
        if (intervalEnqueuedLong == null)
        {
            intervalEnqueuedLong = new Long(intervalEnqueued);
        }
        return intervalEnqueuedLong;
    }

    public Long getIntervalDequeuedLong()
    {
        if (intervalDequeuedLong == null)
        {
            intervalDequeuedLong = new Long(intervalDequeued);
        }
        return intervalDequeuedLong;
    }


    public Long getIntervalFlushedLong()
    {
        if (intervalFlushedLong == null)
        {
            intervalFlushedLong = new Long(intervalFlushed);
        }
        return intervalFlushedLong;
    }


    public Long getIntervalOverlaidLong()
    {
        if (intervalOverlaidLong == null)
        {
            intervalOverlaidLong = new Long(intervalOverlaid);
        }
        return intervalOverlaidLong;
    }


    public Double getPeakEnqueuedRateDouble()
    {
        if (peakEnqueuedRateDouble == null)
        {
            peakEnqueuedRateDouble = new Double(peakEnqueuedRate);
        }
        return peakEnqueuedRateDouble;
    }


    public Double getPeakDequeuedRateDouble()
    {
        if (peakDequeuedRateDouble == null)
        {
            peakDequeuedRateDouble = new Double(peakDequeuedRate);
        }
        return peakDequeuedRateDouble;
    }


    public Double getPeakFlushedRateDouble()
    {
        if (peakFlushedRateDouble == null)
        {
            peakFlushedRateDouble = new Double(peakFlushedRate);
        }
        return peakFlushedRateDouble;
    }


    public Double getPeakOverlaidRateDouble()
    {
        if (peakOverlaidRateDouble == null)
        {
            peakOverlaidRateDouble = new Double(peakOverlaidRate);
        }
        return peakOverlaidRateDouble;
    }


    public Double getAvgEnqueuedRateDouble()
    {
        if (avgEnqueuedRateDouble == null)
        {
            avgEnqueuedRateDouble = new Double(avgEnqueuedRate);
        }
        return avgEnqueuedRateDouble;
    }


    public Double getAvgDequeuedRateDouble()
    {
        if (avgDequeuedRateDouble == null)
        {
            avgDequeuedRateDouble = new Double(avgDequeuedRate);
        }
        return avgDequeuedRateDouble;
    }


    public Double getAvgFlushedRateDouble()
    {
        if (avgFlushedRateDouble == null)
        {
            avgFlushedRateDouble = new Double(avgFlushedRate);
        }
        return avgFlushedRateDouble;
    }


    public Double getAvgOverlaidRateDouble()
    {
        if (avgOverlaidRateDouble == null)
        {
            avgOverlaidRateDouble = new Double(avgOverlaidRate);
        }
        return avgOverlaidRateDouble;
    }

    private void clearDataObjects()
    {
        numEnqueuedLong = null;
        numDequeuedLong = null;
        numFlushedLong = null;
        numOverlaidLong = null;
        peakSizeLong = null;
        currentSizeLong = null;
        statusShort = null;
        peakEnqueuedLong = null;
        peakDequeuedLong = null;
        peakFlushedLong = null;
        peakOverlaidLong = null;
        intervalEnqueuedLong = null;
        intervalDequeuedLong = null;
        intervalFlushedLong = null;
        intervalOverlaidLong = null;
        peakEnqueuedRateDouble = null;
        peakDequeuedRateDouble = null;
        peakFlushedRateDouble = null;
        peakOverlaidRateDouble = null;
        avgEnqueuedRateDouble = null;
        avgDequeuedRateDouble = null;
        avgFlushedRateDouble = null;
        avgOverlaidRateDouble = null;
    }

    protected String[] getInfraNames()
    {
        return INFRA_NAMES;
    }

}
