package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.Instrumentor;

/**
 * InstrumentorFactoryObserver.java
 *
 *
 * Created: Fri May 28 13:57:02 2004
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface InstrumentorFactoryObserver {

	public void observeInstrumentorAdded( Instrumentor addedInstrumentor );
	public void observeInstrumentorRemoved( Instrumentor removedInstrumentor );

} // InstrumentorFactoryObserver
