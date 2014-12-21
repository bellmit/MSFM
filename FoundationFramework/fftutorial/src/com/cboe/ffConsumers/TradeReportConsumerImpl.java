package com.cboe.ffConsumers;

import com.cboe.ffidl.ffTrade.TradeReportStruct;
import com.cboe.ffidl.ffEvents.TradeReportEventConsumer;
import com.cboe.ffidl.ffEvents.POA_TradeReportEventConsumer;
import com.cboe.ffInterfaces.TradeReportConsumer;

public class TradeReportConsumerImpl
    extends POA_TradeReportEventConsumer
{
    TradeReportConsumer delegate;

    public void push(org.omg.CORBA.Any any)
    {
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void disconnect_push_consumer()
    {
    }

    public TradeReportConsumerImpl(TradeReportConsumer aDelegate)
    {
        delegate = aDelegate;
    }

    public void acceptTradeReport(String symbol, TradeReportStruct tradeReport)
    {
        delegate.acceptTradeReport(symbol, tradeReport);
    }
}
