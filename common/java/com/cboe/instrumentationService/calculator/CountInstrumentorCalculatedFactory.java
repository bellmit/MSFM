package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.impls.CountInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;

/**
 * CountInstrumentorCalculatedFactory.java
 *
 *
 * Created: Mon Oct  6 13:37:35 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class CountInstrumentorCalculatedFactory extends CountInstrumentorDefaultFactory {

	public synchronized CountInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		// Rely on default factory to throw exception.
		CountInstrumentor rawInst = super.create( name, userObject );
		CountInstrumentor calcInst = new CountInstrumentorCalculatedImpl( rawInst );
		calcInst.setFactory( this );
		return calcInst;
	}

} // CountInstrumentorCalculatedFactory
