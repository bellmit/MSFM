package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallbackV5.CMITradingClassStatusQueryConsumerPOA;

public class TradingClassStatusQueryConsumerV5
        extends CMITradingClassStatusQueryConsumerPOA
{
    public void acceptTradingClassStatusUpdateforProductGroups(
            String listOfProductGroups[], short status)
    {
        Log.message("TradingClassStatusQueryConsumerV5"
                + ".acceptTradingClassStatusUpdateforProductGroups "
                + Struct.toString(listOfProductGroups)
                + " status:" + status);
    }

    public void acceptTradingClassStatusUpdateforClasses(int listOfClasses[],
            short status)
    {
        Log.message("TradingClassStatusQueryConsumerV5"
                + ".acceptTradingClassStatusUpdateforClasses "
                + Struct.toString(listOfClasses)
                + " status:" + status);
    }
}
