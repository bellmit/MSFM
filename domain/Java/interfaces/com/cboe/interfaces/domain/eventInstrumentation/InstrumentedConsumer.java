package com.cboe.interfaces.domain.eventInstrumentation;

public interface InstrumentedConsumer {

	public EventInstrumentation[] getEventInstrumentation();
	public String                 getConsumerName();
}
