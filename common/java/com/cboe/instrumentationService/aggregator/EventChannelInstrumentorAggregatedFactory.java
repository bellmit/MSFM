package com.cboe.instrumentationService.aggregator;

import com.cboe.instrumentationService.impls.EventChannelInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;

/**
 * EventChannelInstrumentorAggregatedFactory.java
 *
 *
 * Created: Mon Sep 22 13:13:06 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class EventChannelInstrumentorAggregatedFactory extends EventChannelInstrumentorDefaultFactory {

	public synchronized EventChannelInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		EventChannelInstrumentor eci = find( name );
		if ( eci != null ) {
            throw new InstrumentorAlreadyCreatedException( "EventChannelInstrumentor '" + name + "' already exists." );
        }
		eci = new EventChannelInstrumentorAggregatedImpl( name, userObject );
		byte[] key = makeKey( name );
		eci.setKey( key );

		return eci;
	}

} // EventChannelInstrumentorAggregatedFactory
