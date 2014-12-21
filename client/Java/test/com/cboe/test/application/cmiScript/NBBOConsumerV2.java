package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV2.CMINBBOConsumerPOA;
import com.cboe.idl.cmiMarketData.NBBOStruct;

public class NBBOConsumerV2 extends CMINBBOConsumerPOA
{
    public void acceptNBBO(NBBOStruct nbbo[], int queueDepth, short queueAction)
    {
        Log.message("NBBOConsumerV2.acceptNBBO " + Struct.toString(nbbo)
                + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
