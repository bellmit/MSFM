package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumerPOA;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

public class CurrentMarketConsumerV3 extends CMICurrentMarketConsumerPOA
{
    public void acceptCurrentMarket(CurrentMarketStruct bestMarkets[],
        CurrentMarketStruct bestPublicMarkets[], int queueDepth,
        short queueAction)
    {
        Log.message("CurrentMarketConsumerV3.acceptCurrentMarket "
                + "bestMarkets:" + Struct.toString(bestMarkets)
                + " bestPublicMarkets:" + Struct.toString(bestPublicMarkets)
                + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
