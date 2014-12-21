package com.cboe.instrumentationService.aggregator;

import com.cboe.instrumentationService.impls.QueueInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;

/**
 * QueueInstrumentorAggregatedFactory.java
 *
 *
 * Created: Tue Sep 23 08:35:10 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class QueueInstrumentorAggregatedFactory extends QueueInstrumentorDefaultFactory {

	public synchronized QueueInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		QueueInstrumentor qi = find( name );
		if ( qi != null ) {
            throw new InstrumentorAlreadyCreatedException( "QueueInstrumentor '" + name + "' already exists." );
        }
		qi = new QueueInstrumentorAggregatedImpl( name, userObject );
		byte[] key = makeKey( name );
		qi.setKey( key );
		qi.setFactory( this );

		return qi;
	}

} // QueueInstrumentorAggregatedFactory
