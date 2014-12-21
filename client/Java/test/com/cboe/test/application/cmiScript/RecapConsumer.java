package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIRecapConsumerPOA;
import com.cboe.idl.cmiMarketData.RecapStruct;

public class RecapConsumer extends CMIRecapConsumerPOA
{
    public void acceptRecap(RecapStruct recap[])
    {
        Log.message("RecapConsumer.acceptRecap " + Struct.toString(recap));
    }
}
