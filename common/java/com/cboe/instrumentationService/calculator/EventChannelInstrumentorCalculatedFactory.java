package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.impls.EventChannelInstrumentorDefaultFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;

/**
 * EventChannelInstrumentorCalculatedFactory.java
 *
 *
 * Created: Mon Sep 22 13:13:06 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class EventChannelInstrumentorCalculatedFactory extends EventChannelInstrumentorDefaultFactory {

	public EventChannelInstrumentorCalculatedFactory() {
	} // EventChannelInstrumentorCalculatedFactory constructor

	public synchronized EventChannelInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException {
		// Rely on default factory to throw exception.
		EventChannelInstrumentor rawInst = super.create( name, userObject );
		EventChannelInstrumentor calcInst = new EventChannelInstrumentorCalculatedImpl( rawInst );
		calcInst.setFactory( this );
		return calcInst;
	}

} // EventChannelInstrumentorCalculatedFactory
