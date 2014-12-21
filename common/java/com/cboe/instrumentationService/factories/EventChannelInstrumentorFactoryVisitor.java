package com.cboe.instrumentationService.factories;

import com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor;

/**
 * EventChannelInstrumentorFactoryVisitor.java
 *
 *
 * Created: Thu Aug 28 14:45:35 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface EventChannelInstrumentorFactoryVisitor extends InstrumentorFactoryVisitor {

	public boolean visit( EventChannelInstrumentor eci );

}
