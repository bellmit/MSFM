package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIOrderBookConsumerPOA;
import com.cboe.idl.cmiMarketData.BookDepthStruct;

public class OrderBookConsumer extends CMIOrderBookConsumerPOA
{
    public void acceptBookDepth(BookDepthStruct bd)
    {
        Log.message("OrderBookConsumer.acceptBookDepth " + Struct.toString(bd));
    }
}
