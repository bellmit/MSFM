package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMICurrentMarketConsumerPOA;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;

public class CurrentMarketConsumer extends CMICurrentMarketConsumerPOA
{
    public void acceptCurrentMarket(CurrentMarketStruct currentMarket[])
    {
        Log.message("CurrentMarketConsumer.acceptCurrentMarket "
                + Struct.toString(currentMarket));
    }
}
