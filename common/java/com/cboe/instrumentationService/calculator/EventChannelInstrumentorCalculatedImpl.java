package com.cboe.instrumentationService.calculator;

import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;
import com.cboe.instrumentationService.instrumentors.CalculatedEventChannelInstrumentor;

/**
 * EventChannelInstrumentorCalculatedImpl.java
 *
 *
 * Created: Thu Sep 18 08:15:16 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class EventChannelInstrumentorCalculatedImpl extends QueueInstrumentorCalculatedImpl implements EventChannelInstrumentor, CalculatedEventChannelInstrumentor {

	public EventChannelInstrumentorCalculatedImpl( EventChannelInstrumentor rawInst ) {
		super( rawInst );
	} // EventChannelInstrumentorCalculatedImpl constructor

	/**
	 * Copies this ECI into the given ECI.
	 *
	 * @param eci an <code>EventChannelInstrumentor</code> value
	 * @return an <code>EventChannelInstrumentor</code> value
	 */
	public void get( EventChannelInstrumentor eci ) {
		get( (QueueInstrumentor)eci );
	}
    
	public String toString() {
		return super.toString();
	}
} // EventChannelInstrumentorCalculatedImpl
