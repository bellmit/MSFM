package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV4.CMIRecapConsumerPOA;
import com.cboe.idl.cmiMarketData.LastSaleStructV4;
import com.cboe.idl.cmiMarketData.RecapStructV4;

public class RecapConsumerV4 extends CMIRecapConsumerPOA
{
    public void acceptRecap(RecapStructV4 recap[], int messageSequence,
            int queueDepth, short queueAction)
    {
        Log.message("RecapConsumerV4.acceptRecap " + Struct.toString(recap)
            + " messageSequence:" + messageSequence
            + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }

    public void acceptLastSale(LastSaleStructV4 lastSale[], int messageSequence,
            int queueDepth, short queueAction)
    {
        Log.message("RecapConsumerV4.acceptLastSale "
            + Struct.toString(lastSale)
            + " messageSequence:" + messageSequence
            + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
