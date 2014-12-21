package com.cboe.instrumentationService.factories;

/**
 * InstrumentorFactory.java
 *
 *
 * Created: Tue Sep 16 10:59:55 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface InstrumentorFactory {

	public void addObserver( InstrumentorFactoryObserver observer );
	public void removeObserver( InstrumentorFactoryObserver observer );

} // InstrumentorFactory
