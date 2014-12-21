package com.cboe.instrumentationService.instrumentors;

/**
 * AggregatedQueueInstrumentor.java
 *
 *
 * Created: Tue Sep 30 12:25:28 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface AggregatedQueueInstrumentor extends AggregatedInstrumentor {

	public void addInstrumentor( QueueInstrumentor qi );
	public void removeInstrumentor( QueueInstrumentor qi );

} // AggregatedQueueInstrumentor
