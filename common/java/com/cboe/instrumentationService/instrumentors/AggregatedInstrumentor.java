package com.cboe.instrumentationService.instrumentors;

import com.cboe.instrumentationService.factories.InstrumentorFactoryVisitor;

/**
 * AggregatedInstrumentor.java
 *
 *
 * Created: Thu Dec 11 09:12:11 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface AggregatedInstrumentor {

	public void setPrivateOnMembers( boolean newPrivateValue );
	public void visitMembers( InstrumentorFactoryVisitor visitor );
	public void removeMembersFromFactories();

} // AggregatedInstrumentor
