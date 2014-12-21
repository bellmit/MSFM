package com.cboe.delegates.application;

import com.cboe.interfaces.application.OrderQueryV2;

public class OrderQueryV2Delegate extends com.cboe.idl.cmiV2.POA_OrderQuery_tie {
    public OrderQueryV2Delegate(OrderQueryV2 delegate) {
        super(delegate);
    }
}
