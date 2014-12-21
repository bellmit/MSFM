package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor;

/**
 * NetworkConnectionInstrumentorFactory.java
 *
 *
 * Created: Thu Jul 24 11:15:36 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface NetworkConnectionInstrumentorFactory extends InstrumentorFactory {

	public NetworkConnectionInstrumentor getInstance( String name, Object userObject );
	public NetworkConnectionInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException;
	public NetworkConnectionInstrumentor find( String name );
	public void register( NetworkConnectionInstrumentor nci );
	public void unregister( NetworkConnectionInstrumentor nci );
	public void accept( NetworkConnectionInstrumentorFactoryVisitor nciVisitor, boolean exposeClonedMap );

} // NetworkConnectionInstrumentorFactory
