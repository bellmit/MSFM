package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

/**
 * MethodInstrumentorFactory.java
 *
 *
 * Created: Tue Sep  9 13:55:34 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface MethodInstrumentorFactory extends InstrumentorFactory {

	public MethodInstrumentor getInstance( String name, Object userObject );
	public MethodInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException;
	public MethodInstrumentor find( String name );
	public void register( MethodInstrumentor mi );
	public void unregister( MethodInstrumentor mi );
	public void accept( MethodInstrumentorFactoryVisitor miVisitor, boolean exposeClonedMap );

} // MethodInstrumentorFactory
