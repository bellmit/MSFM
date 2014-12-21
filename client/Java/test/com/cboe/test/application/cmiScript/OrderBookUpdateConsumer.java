package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumerPOA;
import com.cboe.idl.cmiMarketData.BookDepthUpdateStruct;

public class OrderBookUpdateConsumer extends CMIOrderBookUpdateConsumerPOA
{
    public void acceptBookDepthUpdate(BookDepthUpdateStruct bdu)
    {
        Log.message("OrderBookUpdateConsumer.acceptBookDepth " + Struct.toString(bdu));
    }
}
