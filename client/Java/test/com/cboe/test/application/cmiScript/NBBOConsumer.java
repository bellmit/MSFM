package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMINBBOConsumerPOA;
import com.cboe.idl.cmiMarketData.NBBOStruct;

public class NBBOConsumer extends CMINBBOConsumerPOA
{
    public void acceptNBBO(NBBOStruct nbbo[])
    {
        Log.message("NBBOConsumer.acceptNBBO " + Struct.toString(nbbo));
    }
}
