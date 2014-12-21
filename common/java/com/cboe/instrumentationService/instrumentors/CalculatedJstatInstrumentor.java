package com.cboe.instrumentationService.instrumentors;
/**
 * 
 * CalculatedJstatInstrumentor
 * 
 * @author neher
 * 
 * Created: November 2, 2006
 *
 * @version 1.0
 *
 */
public interface CalculatedJstatInstrumentor extends CalculatedInstrumentor {
    
    public double getIntervalS0Capacity();
    public double getIntervalS0Utilization();
    public double getIntervalS1Capacity();
    public double getIntervalS1Utilization();
    public double getIntervalECapacity();
    public double getIntervalEUtilization();
    public double getIntervalOCapacity();
    public double getIntervalOUtilization();
    public double getIntervalPCapacity();
    public double getIntervalPUtilization();
    public long getIntervalNbrYgGcs();
    public double getIntervalTimeYgGcs();
    public long getIntervalNbrFullGcs();
    public double getIntervalTimeFullGcs();
    public double getIntervalTimeYgFullGcs();
	public long getIntervalTickFreq();
    public long getIntervalSafepointSyncTime();
    public long getIntervalApplicationTime();
    public long getIntervalSafepointTime();
    public long getIntervalSafepoints();
}
