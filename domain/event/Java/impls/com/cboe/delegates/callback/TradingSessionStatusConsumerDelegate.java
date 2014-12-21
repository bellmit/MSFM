package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class TradingSessionStatusConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMITradingSessionStatusConsumer_tie {

    public TradingSessionStatusConsumerDelegate(TradingSessionStatusConsumer delegate) {
        super(delegate);
    }
}
