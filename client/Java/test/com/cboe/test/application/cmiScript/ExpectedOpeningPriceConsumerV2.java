package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerPOA;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;

public class ExpectedOpeningPriceConsumerV2
    extends CMIExpectedOpeningPriceConsumerPOA
{
    public void acceptExpectedOpeningPrice(ExpectedOpeningPriceStruct eopseq[], int queueDepth, short queueAction)
    {
        Log.message("ExpectedOpeningPriceConsumerV2.acceptExpectedOpeningPrice "
                + Struct.toString(eopseq) 
                + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
