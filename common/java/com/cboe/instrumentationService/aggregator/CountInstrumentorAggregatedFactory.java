package com.cboe.instrumentationService.aggregator;

import com.cboe.instrumentationService.impls.CountInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;

/**
 * CountInstrumentorAggregatedFactory.java
 *
 *
 * Created: Mon Oct  6 10:23:57 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class CountInstrumentorAggregatedFactory extends CountInstrumentorDefaultFactory {

	public synchronized CountInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		CountInstrumentor ci = find( name );
		if ( ci != null ) {
            throw new InstrumentorAlreadyCreatedException( "CountInstrumentor '" + name + "' already exists." );
        }
		ci = new CountInstrumentorAggregatedImpl( name, userObject );
		byte[] key = makeKey( name );
		ci.setKey( key );
		ci.setFactory( this );

		return ci;
	}

} // CountInstrumentorAggregatedFactory
