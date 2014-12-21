package com.cboe.instrumentationService.factories;

import java.util.Map;

/**
 * InstrumentorFactoryVisitor.java
 *
 *
 * Created: Tue Sep 16 11:03:02 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface InstrumentorFactoryVisitor {
	public boolean start( Map unmodifiableCloneOfInstrumentorMap );
	public void end();
} // InstrumentorFactoryVisitor
