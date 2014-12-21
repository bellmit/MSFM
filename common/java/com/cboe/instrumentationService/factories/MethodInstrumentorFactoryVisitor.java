package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

/**
 * MethodInstrumentorFactoryVisitor.java
 *
 *
 * Created: Tue Sep  9 13:57:38 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface MethodInstrumentorFactoryVisitor extends InstrumentorFactoryVisitor {
	
	public boolean visit( MethodInstrumentor mi );

} // MethodInstrumentorFactoryVisitor
