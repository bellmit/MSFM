package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.impls.MethodInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

/**
 * MethodInstrumentorCalculatedFactory.java
 *
 *
 * Created: Tue Sep 23 08:30:14 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class MethodInstrumentorCalculatedFactory extends MethodInstrumentorDefaultFactory {
	public MethodInstrumentorCalculatedFactory() {
	} // MethodInstrumentorCalculatedFactory constructor

	public synchronized MethodInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		// Rely on default factory to throw exception.
		MethodInstrumentor rawInst = super.create( name, userObject );
		MethodInstrumentor calcInst = new MethodInstrumentorCalculatedImpl( rawInst );
		calcInst.setFactory( this );
		return calcInst;
	}

} // MethodInstrumentorCalculatedFactory
