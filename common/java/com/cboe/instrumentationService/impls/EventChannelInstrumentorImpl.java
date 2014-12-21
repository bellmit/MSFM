package com.cboe.instrumentationService.impls;

import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;

/**
 * EventChannelInstrumentorImpl.java
 *
 *
 * Created: Wed Sep  3 14:47:49 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class EventChannelInstrumentorImpl extends QueueInstrumentorImpl implements EventChannelInstrumentor {

	public EventChannelInstrumentorImpl( String name, Object userObject ) {
		super( name, userObject );
	} // EventChannelInstrumentorImpl constructor

	/**
	 * Copies this ECI into the given ECI.
	 *
	 * @param eci an <code>EventChannelInstrumentor</code> value
	 * @return an <code>EventChannelInstrumentor</code> value
	 */
	public void get( EventChannelInstrumentor eci ) {
		get( (QueueInstrumentor)eci );
	}

} // EventChannelInstrumentorImpl
