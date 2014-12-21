package com.cboe.delegates.internalCallback;

import com.cboe.interfaces.callback.*;

public class TradingSessionEventStateConsumerDelegate
    extends com.cboe.idl.consumers.POA_TradingSessionEventStateConsumer_tie {
    public TradingSessionEventStateConsumerDelegate(TradingSessionEventStateCallbackConsumer delegate) {
        super(delegate);
    }
}
