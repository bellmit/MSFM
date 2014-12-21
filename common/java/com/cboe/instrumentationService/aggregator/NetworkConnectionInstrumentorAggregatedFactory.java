package com.cboe.instrumentationService.aggregator;

import com.cboe.instrumentationService.impls.NetworkConnectionInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;

/**
 * NetworkConnectionInstrumentorAggregatedFactory.java
 *
 *
 * Created: Tue Sep 23 08:33:56 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class NetworkConnectionInstrumentorAggregatedFactory extends NetworkConnectionInstrumentorDefaultFactory {

	public synchronized NetworkConnectionInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		NetworkConnectionInstrumentor nci = find( name );
		if ( nci != null ) {
            throw new InstrumentorAlreadyCreatedException( "NetworkConnectionInstrumentor '" + name + "' already exists." );
        }
        nci = new NetworkConnectionInstrumentorAggregatedImpl( name, userObject );
		byte[] key = makeKey( name );
		nci.setKey( key );
		nci.setFactory( this );

		return nci;
	}

} // NetworkConnectionInstrumentorAggregatedFactory
