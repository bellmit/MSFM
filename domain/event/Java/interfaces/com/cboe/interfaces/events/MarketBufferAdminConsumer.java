package com.cboe.interfaces.events;

public interface MarketBufferAdminConsumer extends com.cboe.idl.consumers.MarketBufferAdminConsumerOperations
{
    // consumer state constants:
    short CONSUMER_ACTIVE = 1;
    short CONSUMER_INACTIVE = 2;
}
