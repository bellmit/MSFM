package com.cboe.instrumentationService.instrumentors;

/**
 * EventChannelInstrumentor.java
 *
 *
 * Created: Wed Jul 23 16:05:26 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
public interface EventChannelInstrumentor extends QueueInstrumentor {

	public static final String INSTRUMENTOR_TYPE_NAME = "EventChannelInstrumentor";

	public void get( EventChannelInstrumentor eci );

} // EventChannelInstrumentor
