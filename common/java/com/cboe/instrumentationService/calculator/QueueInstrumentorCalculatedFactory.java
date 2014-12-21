package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.impls.QueueInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedQueueInstrumentor;

/**
 * QueueInstrumentorCalculatedFactory.java
 *
 *
 * Created: Tue Sep 23 08:35:10 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class QueueInstrumentorCalculatedFactory extends QueueInstrumentorDefaultFactory {
	public QueueInstrumentorCalculatedFactory() {
	} // QueueInstrumentorCalculatedFactory constructor

	public synchronized QueueInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		// Rely on default factory to throw exception.
		QueueInstrumentor rawInst = super.create( name, userObject );
		QueueInstrumentor calcInst = new QueueInstrumentorCalculatedImpl( rawInst );
		calcInst.setFactory( this );
		return calcInst;
	}

	public synchronized CalculatedQueueInstrumentor create( QueueInstrumentor qi ) {
		CalculatedQueueInstrumentor calcInst = new QueueInstrumentorCalculatedImpl( qi );
		((QueueInstrumentor)calcInst).setFactory( this );
		return calcInst;
	}

} // QueueInstrumentorCalculatedFactory
