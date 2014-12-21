package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV4.CMINBBOConsumerPOA;
import com.cboe.idl.cmiMarketData.NBBOStructV4;

public class NBBOConsumerV4 extends CMINBBOConsumerPOA
{
    public void acceptNBBO(NBBOStructV4 nbbo[], int messageSequence,
        int queueDepth, short queueAction)
    {
        Log.message("NBBOConsumerV4.acceptNBBO " + Struct.toString(nbbo)
                + " messageSequence:" + messageSequence
                + " queueDepth:" + queueDepth + " queueAction:" + queueAction);
    }
}
