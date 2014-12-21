package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumerPOA;
import com.cboe.idl.cmiMarketData.BookDepthUpdateStruct;

public class OrderBookUpdateConsumerV2 extends CMIOrderBookUpdateConsumerPOA
{
    public void acceptBookDepthUpdate(BookDepthUpdateStruct bduseq[],
        int queueDepth, short queueAction)
    {
        Log.message("OrderBookUpdateConsumerV2.acceptBookDepth "
                + Struct.toString(bduseq)
                + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
