package com.cboe.ffConsumers;

import com.cboe.ffidl.ffTrade.TradeReportStruct;
import com.cboe.ffidl.ffEvents.TradeReportEventConsumer;
import com.cboe.ffInterfaces.TradeReportConsumer;

public class TradeReportPublisher
    implements TradeReportConsumer
{
    TradeReportEventConsumer eventChannel;

    public TradeReportPublisher(TradeReportEventConsumer stub)
    {
        eventChannel = stub;
    }

    public void acceptTradeReport(String symbol, TradeReportStruct tradeReport)
    {
        eventChannel.acceptTradeReport(symbol, tradeReport);
    }
}
