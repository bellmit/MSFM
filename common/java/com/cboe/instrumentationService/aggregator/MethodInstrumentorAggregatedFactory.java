package com.cboe.instrumentationService.aggregator;

import com.cboe.instrumentationService.impls.MethodInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

/**
 * MethodInstrumentorAggregatedFactory.java
 *
 *
 * Created: Tue Sep 23 08:30:14 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class MethodInstrumentorAggregatedFactory extends MethodInstrumentorDefaultFactory {

	public synchronized MethodInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		MethodInstrumentor mi = find( name );
		if ( mi != null ) {
            throw new InstrumentorAlreadyCreatedException( "MethodInstrumentor '" + name + "' already exists." );
        }
        
		mi = new MethodInstrumentorAggregatedImpl( name, userObject );
		byte[] key = makeKey( name );
		mi.setKey( key );
		mi.setFactory( this );

		return mi;
	}

} // MethodInstrumentorAggregatedFactory
