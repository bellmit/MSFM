package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMITradingSessionStatusConsumerPOA;
import com.cboe.idl.cmiSession.TradingSessionStateStruct;

public class TradingSessionStatusConsumer
        extends CMITradingSessionStatusConsumerPOA
{
    public void acceptTradingSessionState(TradingSessionStateStruct tss)
    {
        Log.message("TradingSessionStatusConsumer.acceptTradingSessionState "
                + Struct.toString(tss));
    }
}
