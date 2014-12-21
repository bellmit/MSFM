package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumerPOA;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;

public class ExpectedOpeningPriceConsumer
    extends CMIExpectedOpeningPriceConsumerPOA
{
    public void acceptExpectedOpeningPrice(ExpectedOpeningPriceStruct eop)
    {
        Log.message("ExpectedOpeningPriceConsumer.acceptExpectedOpeningPrice "
                + Struct.toString(eop));
    }
}
