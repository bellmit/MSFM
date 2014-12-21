package com.cboe.instrumentationService.instrumentors;

/**
 * AggregatedMethodInstrumentor.java
 *
 *
 * Created: Tue Sep 30 12:23:25 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface AggregatedMethodInstrumentor extends AggregatedInstrumentor {

	public void addInstrumentor( MethodInstrumentor mi );
	public void removeInstrumentor( MethodInstrumentor mi );

} // AggregatedMethodInstrumentor
