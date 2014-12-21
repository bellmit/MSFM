package com.cboe.interfaces.events;

public interface IECExpectedOpeningPriceConsumerHome
    extends ExpectedOpeningPriceConsumerHome, EventChannelConsumerManager 
{
    /**
     * Indicate whether to accept messages on CurrentMarket channel.
     * @param on true to accept messages, false to disable messages.
     */
    void activateSubscription(boolean on);
}
