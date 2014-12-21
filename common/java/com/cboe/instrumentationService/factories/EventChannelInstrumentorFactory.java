package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;

/**
 * EventChannelInstrumentorFactory.java
 *
 *
 * Created: Thu Jul 24 11:15:36 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface EventChannelInstrumentorFactory extends InstrumentorFactory {

	public EventChannelInstrumentor getInstance( String name, Object userObject );
	public EventChannelInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException;
	public EventChannelInstrumentor find( String name );
	public void register( EventChannelInstrumentor eci );
	public void unregister( EventChannelInstrumentor eci );
	public void accept( EventChannelInstrumentorFactoryVisitor eciVisitor, boolean exposeClonedMap );

} // EventChannelInstrumentorFactory
