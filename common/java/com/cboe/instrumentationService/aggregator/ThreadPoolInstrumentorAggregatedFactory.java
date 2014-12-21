package com.cboe.instrumentationService.aggregator;

import com.cboe.instrumentationService.impls.ThreadPoolInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;

/**
 * ThreadPoolInstrumentorAggregatedFactory.java
 *
 *
 * Created: Tue Sep 23 08:36:15 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class ThreadPoolInstrumentorAggregatedFactory extends ThreadPoolInstrumentorDefaultFactory {

	public synchronized ThreadPoolInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		ThreadPoolInstrumentor tpi = find( name );
		if ( tpi != null ) {
            throw new InstrumentorAlreadyCreatedException( "ThreadPoolInstrumentor '" + name + "' already exists." );
        }
        
		tpi = new ThreadPoolInstrumentorAggregatedImpl( name, userObject );
		byte[] key = makeKey( name );
		tpi.setKey( key );
		tpi.setFactory( this );

		return tpi;
	}

} // ThreadPoolInstrumentorAggregatedFactory
