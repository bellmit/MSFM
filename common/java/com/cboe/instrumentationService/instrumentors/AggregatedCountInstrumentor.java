package com.cboe.instrumentationService.instrumentors;

/**
 * AggregatedCountInstrumentor.java
 *
 *
 * Created: Mon Oct  6 10:07:48 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface AggregatedCountInstrumentor extends AggregatedInstrumentor {

	public void addInstrumentor( CountInstrumentor ci );
	public void removeInstrumentor( CountInstrumentor ci );

} // AggregatedCountInstrumentor
