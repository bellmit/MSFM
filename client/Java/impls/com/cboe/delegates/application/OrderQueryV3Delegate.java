package com.cboe.delegates.application;

import com.cboe.interfaces.application.OrderQueryV3;

public class OrderQueryV3Delegate extends com.cboe.idl.cmiV3.POA_OrderQuery_tie {
    public OrderQueryV3Delegate(OrderQueryV3 delegate) {
        super(delegate);
    }
}
