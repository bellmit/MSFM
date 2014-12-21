package com.cboe.delegates.callback;

import com.cboe.interfaces.callback.*;

public class OrderStatusV2ConsumerDelegate extends com.cboe.idl.cmiCallbackV2.POA_CMIOrderStatusConsumer_tie {

    public OrderStatusV2ConsumerDelegate(OrderStatusV2Consumer delegate) {
        super(delegate);
    }
}
