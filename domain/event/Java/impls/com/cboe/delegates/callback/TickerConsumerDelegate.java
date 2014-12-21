package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class TickerConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMITickerConsumer_tie {

    public TickerConsumerDelegate(TickerConsumer delegate) {
        super(delegate);
    }
}
