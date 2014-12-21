package com.cboe.interfaces.events;

public interface MarketDataCallbackConsumerHome
{
    public final static String HOME_NAME = "MarketDataCallbackConsumerHome";
    public final static String PUBLISHER_HOME_NAME = "MarketDataCallbackPublisherHome";

    public MarketDataCallbackConsumer find();
    public MarketDataCallbackConsumer create();
}
