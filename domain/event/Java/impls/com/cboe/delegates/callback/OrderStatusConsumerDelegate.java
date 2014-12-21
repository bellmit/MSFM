package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class OrderStatusConsumerDelegate extends com.cboe.idl.cmiCallback.POA_CMIOrderStatusConsumer_tie {

    public OrderStatusConsumerDelegate(OrderStatusConsumer delegate) {
        super(delegate);
    }
}
