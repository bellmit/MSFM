package com.cboe.interfaces.events;

public interface IECLargeTradeLastSaleConsumerHome
    extends TickerConsumerHome, EventChannelConsumerManager 
{
    // overwrites TickerConsumerHome
    public final static String HOME_NAME = "LargeTradeLastSaleConsumerHome";
}
