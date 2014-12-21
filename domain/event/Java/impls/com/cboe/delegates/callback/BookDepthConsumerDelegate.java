package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class BookDepthConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMIOrderBookConsumer_tie {

    public BookDepthConsumerDelegate(OrderBookConsumer delegate) {
        super(delegate);
    }
}
