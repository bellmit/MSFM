package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.HeapInstrumentor;

/**
 * HeapInstrumentorFactory.java
 *
 *
 * Created: Thu Jul 24 11:15:36 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface HeapInstrumentorFactory extends InstrumentorFactory {

	public HeapInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException;
	public HeapInstrumentor getInstance( String name, Object userObject );
	public HeapInstrumentor find( String name );
	public void register( HeapInstrumentor hi );
	public void unregister( HeapInstrumentor hi );
	public void accept( HeapInstrumentorFactoryVisitor hiVisitor, boolean exposeClonedMap );

} // HeapInstrumentorFactory
