package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.OrderBookV2Consumer;
import com.cboe.idl.cmiCallbackV2.POA_CMIOrderBookConsumer_tie;

public class OrderBookV2ConsumerDelegate extends POA_CMIOrderBookConsumer_tie {

    public OrderBookV2ConsumerDelegate(OrderBookV2Consumer delegate) {
        super(delegate);
    }
}
