package com.cboe.instrumentationService.instrumentors;

/**
 * CalculatedHeapInstrumentor.java
 *
 *
 * Created: Thu Sep 18 09:43:26 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface CalculatedHeapInstrumentor extends CalculatedInstrumentor {

	public long getIntervalMaxMemory();
	public long getIntervalTotalMemory();
	public long getIntervalFreeMemory();

} // CalculatedHeapInstrumentor
