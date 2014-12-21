package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV4.CMITickerConsumerPOA;
import com.cboe.idl.cmiMarketData.TickerStructV4;

public class TickerConsumerV4 extends CMITickerConsumerPOA
{
    public void acceptTicker(TickerStructV4 ticker[], int messageSequence,
        int queueDepth, short queueAction)
    {
        Log.message("TickerConsumerV4.acceptTicker " + Struct.toString(ticker)
                + " messageSequence:" + messageSequence
                + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
