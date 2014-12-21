package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.CalculatedJstatInstrumentorMutable;
import com.cboe.interfaces.instrumentation.InstrumentorTypes;

import com.cboe.instrumentationService.instrumentors.CalculatedJstatInstrumentor;
import com.cboe.instrumentationService.instrumentors.JstatInstrumentor;

/**
 * Created by IntelliJ IDEA. User: gupta Date: Aug 6, 2008 Time: 11:38:41 AM To change this template
 * use File | Settings | File Templates.
 */
public class JstatInstrumentorImpl extends AbstractInstrumentor
        implements CalculatedJstatInstrumentorMutable
{

    private double s0Capacity;
    private double s0Utilization;
    private double s1Capacity;
    private double s1Utilization;
    private double eCapacity;
    private double eUtilization;
    private double oCapacity;
    private double oUtilization;
    private double pCapacity;
    private double pUtilization;
    private long nbrYgGcs;
    private double timeYgGcs;
    private long nbrFullGcs;
    private double timeFullGcs;
    private double timeYgFullGcs;

    private double intervalS0Capacity;
    private double intervalS0Utilization;
    private double intervalS1Capacity;
    private double intervalS1Utilization;
    private double intervalECapacity;
    private double intervalEUtilization;
    private double intervalOCapacity;
    private double intervalOUtilization;
    private double intervalPCapacity;
    private double intervalPUtilization;
    private long intervalNbrYgGcs;
    private double intervalTimeYgGcs;
    private long intervalNbrFullGcs;
    private double intervalTimeFullGcs;
    private double intervalTimeYgFullGcs;

    public JstatInstrumentorImpl()
    {
        super();
    }

    public JstatInstrumentorImpl(String orbName, String clusterName,
                                 com.cboe.instrumentationService.instrumentors.JstatInstrumentor jstatInstrumentor)
    {
        this();
        setData(orbName, clusterName, jstatInstrumentor);
    }

    public JstatInstrumentorImpl(String orbName, String clusterName,
                                 com.cboe.instrumentationService.instrumentors.JstatInstrumentor jstatInstrumentor,
                                 com.cboe.instrumentationService.instrumentors.CalculatedJstatInstrumentor calculatedJstatInstrumentor)
    {
        this(orbName, clusterName, jstatInstrumentor);
        setCalculatedData(calculatedJstatInstrumentor);
    }

    protected void setType()
    {
        this.type = InstrumentorTypes.JSTAT;
    }

    protected String[] getInfraNames()
    {
        return null;
    }

    public void setData(String orbName, String clusterName, JstatInstrumentor jstatInstrumentor)
    {
        super.setData(orbName, clusterName, jstatInstrumentor);
        eCapacity = jstatInstrumentor.getECapacity();
        eUtilization = jstatInstrumentor.getEUtilization();
        nbrFullGcs = jstatInstrumentor.getNbrFullGcs();
        nbrYgGcs = jstatInstrumentor.getNbrYgGcs();
        oCapacity = jstatInstrumentor.getOCapacity();
        oUtilization = jstatInstrumentor.getOUtilization();
        pCapacity = jstatInstrumentor.getPCapacity();
        pUtilization = jstatInstrumentor.getPUtilization();
        s0Capacity = jstatInstrumentor.getS0Capacity();
        s0Utilization = jstatInstrumentor.getS0Utilization();
        timeFullGcs = jstatInstrumentor.getTimeFullGcs();
        timeYgFullGcs = jstatInstrumentor.getTimeYgFullGcs();
        timeYgGcs = jstatInstrumentor.getTimeYgGcs();
        s1Capacity = jstatInstrumentor.getS1Capacity();
        s1Utilization = jstatInstrumentor.getS1Utilization();
    }

    public void setCalculatedData(CalculatedJstatInstrumentor calculatedJstatInstrumentor)
    {
        this.intervalECapacity = calculatedJstatInstrumentor.getIntervalECapacity();
        this.intervalEUtilization = calculatedJstatInstrumentor.getIntervalEUtilization();
        this.intervalNbrFullGcs = calculatedJstatInstrumentor.getIntervalNbrFullGcs();
        this.intervalNbrYgGcs = calculatedJstatInstrumentor.getIntervalNbrYgGcs();
        this.intervalOCapacity = calculatedJstatInstrumentor.getIntervalOCapacity();
        this.intervalOUtilization = calculatedJstatInstrumentor.getIntervalOUtilization();
        this.intervalPCapacity = calculatedJstatInstrumentor.getIntervalPCapacity();
        this.intervalPUtilization = calculatedJstatInstrumentor.getIntervalPUtilization();
        this.intervalS0Capacity = calculatedJstatInstrumentor.getIntervalS0Capacity();
        this.intervalS0Utilization = calculatedJstatInstrumentor.getIntervalS0Utilization();
        this.intervalTimeFullGcs = calculatedJstatInstrumentor.getIntervalTimeFullGcs();
        this.intervalTimeYgFullGcs = calculatedJstatInstrumentor.getIntervalTimeYgFullGcs();
        this.intervalTimeYgGcs = calculatedJstatInstrumentor.getIntervalTimeYgGcs();
        this.intervalS1Capacity = calculatedJstatInstrumentor.getIntervalS1Capacity();
        this.intervalS1Utilization = calculatedJstatInstrumentor.getIntervalS1Utilization();
    }

    public double getIntervalS0Capacity()
    {
        return intervalS0Capacity;
    }

    public double getIntervalS0Utilization()
    {
        return intervalS0Utilization;
    }

    public double getIntervalS1Capacity()
    {
        return intervalS1Capacity;
    }

    public double getIntervalS1Utilization()
    {
        return intervalS1Utilization;
    }

    public double getIntervalECapacity()
    {
        return intervalECapacity;
    }

    public double getIntervalEUtilization()
    {
        return intervalEUtilization;
    }

    public double getIntervalOCapacity()
    {
        return intervalOCapacity;
    }

    public double getIntervalOUtilization()
    {
        return intervalOUtilization;
    }

    public double getIntervalPCapacity()
    {
        return intervalPCapacity;
    }

    public double getIntervalPUtilization()
    {
        return intervalPUtilization;
    }

    public long getIntervalNbrYgGcs()
    {
        return intervalNbrYgGcs;
    }

    public double getIntervalTimeYgGcs()
    {
        return intervalTimeYgGcs;
    }

    public long getIntervalNbrFullGcs()
    {
        return intervalNbrFullGcs;
    }

    public double getIntervalTimeFullGcs()
    {
        return intervalTimeFullGcs;
    }

    public double getIntervalTimeYgFullGcs()
    {
        return intervalTimeYgFullGcs;
    }

    public void clearData()
    {
        s0Capacity = 0;
        s0Utilization = 0;
        s1Capacity = 0;
        s1Utilization = 0;
        eCapacity = 0;
        eUtilization = 0;
        oCapacity = 0;
        oUtilization = 0;
        pCapacity = 0;
        pUtilization = 0;
        nbrYgGcs = 0;
        timeYgGcs = 0;
        nbrFullGcs = 0;
        timeFullGcs = 0;
        timeYgFullGcs = 0;

        intervalECapacity = 0;
        intervalEUtilization = 0;
        intervalNbrFullGcs = 0;
        intervalNbrYgGcs = 0;
        intervalOCapacity = 0;
        intervalOUtilization = 0;
        intervalPCapacity = 0;
        intervalPUtilization = 0;
        intervalS0Capacity = 0;
        intervalS0Utilization = 0;
        intervalTimeFullGcs = 0;
        intervalTimeYgFullGcs = 0;
        intervalTimeYgGcs = 0;
        intervalS1Capacity = 0;
        intervalS1Utilization = 0;
    }

    public void instrumentorPlusPlus(com.cboe.interfaces.instrumentation.CalculatedJStatInstrumentor jstatInstrumentor)
    {
        if(jstatInstrumentor.getLastUpdatedTimeMillis() > lastUpdatedTimeMillis)
        {
            lastUpdatedTimeMillis = jstatInstrumentor.getLastUpdatedTimeMillis();
            lastUpdatedTime = null;
        }

        s0Capacity += jstatInstrumentor.getS0Capacity();
        s0Utilization += jstatInstrumentor.getS0Utilization();
        s1Capacity += jstatInstrumentor.getS1Capacity();
        s1Utilization += jstatInstrumentor.getS1Utilization();
        eCapacity += jstatInstrumentor.getECapacity();
        eUtilization += jstatInstrumentor.getEUtilization();
        oCapacity += jstatInstrumentor.getOCapacity();
        oUtilization += jstatInstrumentor.getOUtilization();
        pCapacity += jstatInstrumentor.getPCapacity();
        pUtilization += jstatInstrumentor.getPUtilization();
        nbrYgGcs += jstatInstrumentor.getNumberYGGCs();
        timeYgGcs += jstatInstrumentor.getTimeYGGCs();
        nbrFullGcs += jstatInstrumentor.getNumberFullGCs();
        timeFullGcs += jstatInstrumentor.getTimeFullGCs();
        timeYgFullGcs += jstatInstrumentor.getTimeYGFullGCs();

        intervalECapacity += jstatInstrumentor.getIntervalECapacity();
        intervalEUtilization += jstatInstrumentor.getIntervalEUtilization();
        intervalNbrFullGcs += jstatInstrumentor.getIntervalNbrFullGcs();
        intervalNbrYgGcs += jstatInstrumentor.getIntervalNbrYgGcs();
        intervalOCapacity += jstatInstrumentor.getIntervalOCapacity();
        intervalOUtilization += jstatInstrumentor.getIntervalOUtilization();
        intervalPCapacity += jstatInstrumentor.getIntervalPCapacity();
        intervalPUtilization += jstatInstrumentor.getIntervalPUtilization();
        intervalS0Capacity += jstatInstrumentor.getIntervalS0Capacity();
        intervalS0Utilization += jstatInstrumentor.getIntervalS0Utilization();
        intervalTimeFullGcs += jstatInstrumentor.getIntervalTimeFullGcs();
        intervalTimeYgFullGcs += jstatInstrumentor.getIntervalTimeYgFullGcs();
        intervalTimeYgGcs += jstatInstrumentor.getIntervalTimeYgGcs();
        intervalS1Capacity += jstatInstrumentor.getIntervalS1Capacity();
        intervalS1Utilization += jstatInstrumentor.getIntervalS1Utilization();
    }


    /**
     * Clone all the parts of the object.  Clone is used in a very heavy use method, so not
     * everything is cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        JstatInstrumentorImpl jstatInstrumentor = new JstatInstrumentorImpl();

        jstatInstrumentor.userData = getUserData();
        jstatInstrumentor.name = getName();
        jstatInstrumentor.instrumentorKey = getInstrumentorKey();
        jstatInstrumentor.orbName = getOrbName();
        jstatInstrumentor.clusterName = getClusterName();
        jstatInstrumentor.lastUpdatedTimeMillis = getLastUpdatedTimeMillis();

        return jstatInstrumentor;
    }

    public void setInstrumentedData(com.cboe.interfaces.instrumentation.CalculatedJStatInstrumentor jstatInstrumentor)
    {
        lastUpdatedTimeMillis = jstatInstrumentor.getLastUpdatedTimeMillis();
        lastUpdatedTime = null;
        eCapacity = jstatInstrumentor.getECapacity();
        eUtilization = jstatInstrumentor.getEUtilization();
        nbrFullGcs = jstatInstrumentor.getNumberFullGCs();
        nbrYgGcs = jstatInstrumentor.getNumberYGGCs();
        oCapacity = jstatInstrumentor.getOCapacity();
        oUtilization = jstatInstrumentor.getOUtilization();
        pCapacity = jstatInstrumentor.getPCapacity();
        pUtilization = jstatInstrumentor.getPUtilization();
        s0Capacity = jstatInstrumentor.getS0Capacity();
        s0Utilization = jstatInstrumentor.getS0Utilization();
        timeFullGcs = jstatInstrumentor.getTimeFullGCs();
        timeYgFullGcs = jstatInstrumentor.getTimeYGFullGCs();
        timeYgGcs = jstatInstrumentor.getTimeYGGCs();
        s1Capacity = jstatInstrumentor.getS1Capacity();
        s1Utilization = jstatInstrumentor.getS1Utilization();

        intervalECapacity = jstatInstrumentor.getIntervalECapacity();
        intervalEUtilization = jstatInstrumentor.getIntervalEUtilization();
        intervalNbrFullGcs = jstatInstrumentor.getIntervalNbrFullGcs();
        intervalNbrYgGcs = jstatInstrumentor.getIntervalNbrYgGcs();
        intervalOCapacity = jstatInstrumentor.getIntervalOCapacity();
        intervalOUtilization = jstatInstrumentor.getIntervalOUtilization();
        intervalPCapacity = jstatInstrumentor.getIntervalPCapacity();
        intervalPUtilization = jstatInstrumentor.getIntervalPUtilization();
        intervalS0Capacity = jstatInstrumentor.getIntervalS0Capacity();
        intervalS0Utilization = jstatInstrumentor.getIntervalS0Utilization();
        intervalTimeFullGcs = jstatInstrumentor.getIntervalTimeFullGcs();
        intervalTimeYgFullGcs = jstatInstrumentor.getIntervalTimeYgFullGcs();
        intervalTimeYgGcs = jstatInstrumentor.getIntervalTimeYgGcs();
        intervalS1Capacity = jstatInstrumentor.getIntervalS1Capacity();
        intervalS1Utilization = jstatInstrumentor.getIntervalS1Utilization();
    }

    public double getS0Capacity()
    {
        return s0Capacity;
    }

    public double getS0Utilization()
    {
        return s0Utilization;
    }

    public double getS1Capacity()
    {
        return s1Capacity;
    }

    public double getS1Utilization()
    {
        return s1Utilization;
    }

    public double getECapacity()
    {
        return eCapacity;
    }

    public double getEUtilization()
    {
        return eUtilization;
    }

    public double getOCapacity()
    {
        return oCapacity;
    }

    public double getOUtilization()
    {
        return oUtilization;
    }

    public double getPCapacity()
    {
        return pCapacity;
    }

    public double getPUtilization()
    {
        return pUtilization;
    }

    public long getNumberYGGCs()
    {
        return nbrYgGcs;
    }

    public double getTimeYGGCs()
    {
        return timeYgGcs;
    }

    public long getNumberFullGCs()
    {
        return nbrFullGcs;
    }

    public double getTimeFullGCs()
    {
        return timeFullGcs;
    }

    public double getTimeYGFullGCs()
    {
        return timeYgFullGcs;
    }
}
