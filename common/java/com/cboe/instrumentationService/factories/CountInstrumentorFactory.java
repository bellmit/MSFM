package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.CountInstrumentor;

/**
 * Describe interface <code>CountInstrumentorFactory</code> here.
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface CountInstrumentorFactory extends InstrumentorFactory {

	public CountInstrumentor getInstance( String name, Object userObject );
	public CountInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException;
	public CountInstrumentor find( String name );
	public void register( CountInstrumentor mi );
	public void unregister( CountInstrumentor mi );
	public void accept( CountInstrumentorFactoryVisitor miVisitor, boolean exposeClonedMap );

} // CountInstrumentorFactory
