package com.cboe.instrumentationService.instrumentors;

/**
 * HeapInstrumentor.java
 *
 *
 * Created: Tue Aug 26 07:19:57 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface HeapInstrumentor extends Instrumentor {
	
	public static final String INSTRUMENTOR_TYPE_NAME = "HeapInstrumentor";

	public void setMaxMemory( long newMaxValue );
	public long getMaxMemory();

	public void setTotalMemory( long newTotalValue );
	public long getTotalMemory();

	public void setFreeMemory( long newFreeValue );
	public long getFreeMemory();

	public void get( HeapInstrumentor hi );

} // HeapInstrumentor
