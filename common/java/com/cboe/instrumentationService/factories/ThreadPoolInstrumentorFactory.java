package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor;

/**
 * ThreadPoolInstrumentorFactory.java
 *
 *
 * Created: Thu Jul 24 11:15:36 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface ThreadPoolInstrumentorFactory extends InstrumentorFactory {

	public ThreadPoolInstrumentor getInstance( String name, Object userObject );
	public ThreadPoolInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException;
	public ThreadPoolInstrumentor find( String name );
	public void register( ThreadPoolInstrumentor tpi );
	public void unregister( ThreadPoolInstrumentor tpi );
	public void accept( ThreadPoolInstrumentorFactoryVisitor tpiVisitor, boolean exposeClonedMap );

} // ThreadPoolInstrumentorFactory
