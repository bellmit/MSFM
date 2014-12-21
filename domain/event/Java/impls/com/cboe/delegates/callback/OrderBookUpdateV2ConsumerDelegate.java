package com.cboe.delegates.callback;

import com.cboe.idl.cmiCallbackV2.POA_CMIOrderBookUpdateConsumer_tie;
import com.cboe.interfaces.callback.OrderBookUpdateV2Consumer;

public class OrderBookUpdateV2ConsumerDelegate extends POA_CMIOrderBookUpdateConsumer_tie {

    public OrderBookUpdateV2ConsumerDelegate(OrderBookUpdateV2Consumer delegate) {
        super(delegate);
    }
}
