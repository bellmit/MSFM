package com.cboe.instrumentationService.instrumentors;
/**
 * 
 * CalculatedJmxInstrumentor
 * 
 * @author neher
 * 
 * Created: Wed Oct 19 2006
 *
 * @version 1.0
 *
 */
public interface CalculatedJmxInstrumentor extends CalculatedInstrumentor {
    
    public long getIntervalPeakThreadCount();
    public long getIntervalCurrentThreadCount();
    public long getIntervalTotalThreadsStarted();
    public long getIntervalTotalCPUTime();
    
}
