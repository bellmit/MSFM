package com.cboe.instrumentationService.instrumentors;

/**
 * CalculatedThreadPoolInstrumentor.java
 *
 *
 * Created: Thu Sep 18 09:59:47 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface CalculatedThreadPoolInstrumentor extends CalculatedInstrumentor {

	public long getIntervalExecutingThreads();
	public long getIntervalStartedThreads();
	public long getIntervalPendingThreads();
	public long getIntervalPendingTaskCount();

} // CalculatedThreadPoolInstrumentor
