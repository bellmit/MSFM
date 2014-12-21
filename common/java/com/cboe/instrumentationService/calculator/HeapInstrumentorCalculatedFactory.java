package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.impls.HeapInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;

/**
 * HeapInstrumentorCalculatedFactory.java
 *
 *
 * Created: Tue Sep 23 08:28:33 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class HeapInstrumentorCalculatedFactory extends HeapInstrumentorDefaultFactory {
	public HeapInstrumentorCalculatedFactory() {
	} // HeapInstrumentorCalculatedFactory constructor

	public synchronized HeapInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		// Rely on default factory to throw exception.
		HeapInstrumentor rawInst = super.create( name, userObject );
		HeapInstrumentor calcInst = new HeapInstrumentorCalculatedImpl( rawInst );
		calcInst.setFactory( this );
		return calcInst;
	}

} // HeapInstrumentorCalculatedFactory
