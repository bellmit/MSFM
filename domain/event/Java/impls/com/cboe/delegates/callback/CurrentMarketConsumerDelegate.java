package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class CurrentMarketConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMICurrentMarketConsumer_tie {

    public CurrentMarketConsumerDelegate(CurrentMarketConsumer delegate) {
        super(delegate);
    }
}
