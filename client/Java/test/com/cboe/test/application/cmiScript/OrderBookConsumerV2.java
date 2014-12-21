package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerPOA;
import com.cboe.idl.cmiMarketData.BookDepthStruct;

public class OrderBookConsumerV2 extends CMIOrderBookConsumerPOA
{
    public void acceptBookDepth(BookDepthStruct bdseq[], int queueDepth, short queueAction)
    {
        Log.message("OrderBookConsumerV2.acceptBookDepth "
                + Struct.toString(bdseq)
                + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
