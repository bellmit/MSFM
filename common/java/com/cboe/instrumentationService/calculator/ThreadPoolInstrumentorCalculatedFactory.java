package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.impls.ThreadPoolInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;

/**
 * ThreadPoolInstrumentorCalculatedFactory.java
 *
 *
 * Created: Tue Sep 23 08:36:15 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class ThreadPoolInstrumentorCalculatedFactory extends ThreadPoolInstrumentorDefaultFactory {
	public ThreadPoolInstrumentorCalculatedFactory() {
	} // ThreadPoolInstrumentorCalculatedFactory constructor

	public synchronized ThreadPoolInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		// Rely on default factory to throw exception.
		ThreadPoolInstrumentor rawInst = super.create( name, userObject );
		ThreadPoolInstrumentor calcInst = new ThreadPoolInstrumentorCalculatedImpl( rawInst );
		calcInst.setFactory( this );
		return calcInst;
	}

} // ThreadPoolInstrumentorCalculatedFactory
