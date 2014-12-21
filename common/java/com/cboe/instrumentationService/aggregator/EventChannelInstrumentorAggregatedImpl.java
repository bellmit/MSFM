package com.cboe.instrumentationService.aggregator;

import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;
import com.cboe.instrumentationService.instrumentors.AggregatedEventChannelInstrumentor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;

/**
 * EventChannelInstrumentorAggregatedImpl.java
 *
 *
 * Created: Wed Sep  3 14:47:49 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class EventChannelInstrumentorAggregatedImpl extends QueueInstrumentorAggregatedImpl implements EventChannelInstrumentor, AggregatedEventChannelInstrumentor {

	public EventChannelInstrumentorAggregatedImpl( String name, Object userObject ) {
		super( name, userObject );
	} // EventChannelInstrumentorAggregatedImpl constructor

	public synchronized void addInstrumentor( EventChannelInstrumentor eci ) {
		super.addInstrumentor( eci );
	}

	public synchronized void removeInstrumentor( EventChannelInstrumentor eci ) {
		super.removeInstrumentor( eci );
	}

	/**
	 * Copies this ECI into the given ECI.
	 *
	 * @param eci an <code>EventChannelInstrumentor</code> value
	 * @return an <code>EventChannelInstrumentor</code> value
	 */
	public void get( EventChannelInstrumentor eci ) {
		get( (QueueInstrumentor)eci );
	}

} // EventChannelInstrumentorAggregatedImpl
