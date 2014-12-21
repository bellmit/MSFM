package com.cboe.instrumentationService.factories;

/**
 * InstrumentorAlreadyCreatedException.java
 *
 *
 * Created: Wed Sep  3 10:03:14 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public class InstrumentorAlreadyCreatedException extends Exception {

	public InstrumentorAlreadyCreatedException() {
		super();
	} // InstrumentorAlreadyCreatedException constructor

	public InstrumentorAlreadyCreatedException( String message ) {
		super( message );
	}

} // InstrumentorAlreadyCreatedException
