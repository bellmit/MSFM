package com.cboe.instrumentationService.instrumentors;

/**
 * AggregatedEventChannelInstrumentor.java
 *
 *
 * Created: Tue Sep 30 12:19:22 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface AggregatedEventChannelInstrumentor extends AggregatedInstrumentor {

	public void addInstrumentor( EventChannelInstrumentor eci );
	public void removeInstrumentor( EventChannelInstrumentor eci );

} // AggregatedEventChannelInstrumentor
