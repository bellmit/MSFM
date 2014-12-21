package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumerPOA;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;

public class CurrentMarketConsumerV4 extends CMICurrentMarketConsumerPOA
{
    public void acceptCurrentMarket(CurrentMarketStructV4 bestMarkets[],
        CurrentMarketStructV4 bestPublicMarkets[], int messageSequence,
        int queueDepth, short queueAction)
    {
        Log.message("CurrentMarketConsumerV4.acceptCurrentMarket "
                + "bestMarkets:" + Struct.toString(bestMarkets)
                + " bestPublicMarkets:" + Struct.toString(bestPublicMarkets)
                + " messageSequence:" + messageSequence
                + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
