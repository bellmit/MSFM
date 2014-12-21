package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerPOA;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

public class CurrentMarketConsumerV2 extends CMICurrentMarketConsumerPOA
{
    public void acceptCurrentMarket(CurrentMarketStruct currentMarket[],
            int queueDepth, short queueAction)
    {
        Log.message("CurrentMarketConsumerV2.acceptCurrentMarket "
                + Struct.toString(currentMarket)
                + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
