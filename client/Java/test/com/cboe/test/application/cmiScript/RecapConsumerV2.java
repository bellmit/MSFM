package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMIRecapConsumerPOA;
import com.cboe.idl.cmiMarketData.RecapStruct;

public class RecapConsumerV2 extends CMIRecapConsumerPOA
{
    public void acceptRecap(RecapStruct recap[],
            int queueDepth, short queueAction)
    {
        Log.message("RecapConsumerV2.acceptRecap " + Struct.toString(recap)
            + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
