package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class StrategyStatusConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMIStrategyStatusConsumer_tie {

    public StrategyStatusConsumerDelegate(StrategyStatusConsumer delegate) {
        super(delegate);
    }
}
