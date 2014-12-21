package com.cboe.instrumentationService.instrumentors;

/**
 * AggregatedNetworkConnectionInstrumentor.java
 *
 *
 * Created: Tue Sep 30 12:24:37 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface AggregatedNetworkConnectionInstrumentor extends AggregatedInstrumentor {

	public void addInstrumentor( NetworkConnectionInstrumentor nci );
	public void removeInstrumentor( NetworkConnectionInstrumentor nci );

} // AggregatedNetworkConnectionInstrumentor
