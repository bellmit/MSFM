package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiCallback.CMIStrategyStatusConsumerPOA;
import com.cboe.idl.cmiSession.SessionStrategyStruct;

public class StrategyStatusConsumer extends CMIStrategyStatusConsumerPOA
{
    public void updateProductStrategy(SessionStrategyStruct updatedStrategies[])
    {
        Log.message("StrategyStatusConsumer.updateStrategy "
                + Struct.toString(updatedStrategies));
    }
}
