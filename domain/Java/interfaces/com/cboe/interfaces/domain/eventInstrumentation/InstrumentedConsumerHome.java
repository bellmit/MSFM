package com.cboe.interfaces.domain.eventInstrumentation;

public interface InstrumentedConsumerHome {
public InstrumentedConsumer[] getInstrumentedConsumers();
public void registerInstrumentedConsumer(InstrumentedConsumer consumer);
public void unregisterInstrumentedConsumer(InstrumentedConsumer consumer);
}
