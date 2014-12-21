package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.impls.NetworkConnectionInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;

/**
 * NetworkConnectionInstrumentorCalculatedFactory.java
 *
 *
 * Created: Tue Sep 23 08:33:56 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class NetworkConnectionInstrumentorCalculatedFactory extends NetworkConnectionInstrumentorDefaultFactory {
	public NetworkConnectionInstrumentorCalculatedFactory() {
	} // NetworkConnectionInstrumentorCalculatedFactory constructor

	public synchronized NetworkConnectionInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		// Rely on default factory to throw exception.
		NetworkConnectionInstrumentor rawInst = super.create( name, userObject );
		NetworkConnectionInstrumentor calcInst = new NetworkConnectionInstrumentorCalculatedImpl( rawInst );
		calcInst.setFactory( this );
		return calcInst;
	}

} // NetworkConnectionInstrumentorCalculatedFactory
