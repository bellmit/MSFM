package com.cboe.instrumentationService.instrumentors;

/**
 * AggregatedThreadPoolInstrumentor.java
 *
 *
 * Created: Tue Sep 30 12:26:31 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface AggregatedThreadPoolInstrumentor extends AggregatedInstrumentor {

	public void addInstrumentor( ThreadPoolInstrumentor tpi );
	public void removeInstrumentor( ThreadPoolInstrumentor tpi );

} // AggregatedThreadPoolInstrumentor
