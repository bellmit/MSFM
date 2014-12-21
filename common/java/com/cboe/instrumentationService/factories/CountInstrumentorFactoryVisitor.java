package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.CountInstrumentor;

/**
 * CountInstrumentorFactoryVisitor.java
 *
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface CountInstrumentorFactoryVisitor extends InstrumentorFactoryVisitor {
	
	public boolean visit( CountInstrumentor mi );

} // CountInstrumentorFactoryVisitor
