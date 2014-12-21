package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMITickerConsumerPOA;
import com.cboe.idl.cmiMarketData.TickerStruct;

public class TickerConsumerV2 extends CMITickerConsumerPOA
{
    public void acceptTicker(TickerStruct ticker[],
        int queueDepth, short queueAction)
    {
        Log.message("TickerConsumerV2.acceptTicker " + Struct.toString(ticker)
                + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
