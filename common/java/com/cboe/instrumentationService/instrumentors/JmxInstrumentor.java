package com.cboe.instrumentationService.instrumentors;

/**
 * 
 * JmxInstrumentor
 * 
 * @author neher
 * 
 * Created: Wed Oct 18 2006
 *
 * @version 1.0
 *
 */
public interface JmxInstrumentor extends Instrumentor {

    public static final String INSTRUMENTOR_TYPE_NAME = "JmxInstrumentor";

    public void setPeakThreadCount( int threadCount );
    public int getPeakThreadCount();
    
    public void setCurrentThreadCount (int threadCount);
    public int getCurrentThreadCount();
    
    public void setTotalThreadsStarted (long threadCount);
    public long getTotalThreadsStarted();
    
    public void setTotalCPUTime (long cpuTime);
    public long getTotalCPUTime();
    
    public void get( JmxInstrumentor jmxi );

}
