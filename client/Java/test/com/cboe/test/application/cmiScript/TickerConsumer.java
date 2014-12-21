package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMITickerConsumerPOA;
import com.cboe.idl.cmiMarketData.TickerStruct;

public class TickerConsumer extends CMITickerConsumerPOA
{
    public void acceptTicker(TickerStruct ticker[])
    {
        Log.message("TickerConsumer.acceptTicker " + Struct.toString(ticker));
    }
}
