package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;

/**
 * QueueInstrumentorFactory.java
 *
 *
 * Created: Thu Jul 24 11:15:36 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface QueueInstrumentorFactory extends InstrumentorFactory {

	public QueueInstrumentor getInstance( String name, Object userObject );
	public QueueInstrumentor create( String name, Object userObject ) throws InstrumentorAlreadyCreatedException;
	public QueueInstrumentor find( String name );
	public void register( QueueInstrumentor qi );
	public void unregister( QueueInstrumentor qi );
	public void accept( QueueInstrumentorFactoryVisitor qiVisitor, boolean exposeClonedMap );

} // QueueInstrumentorFactory
