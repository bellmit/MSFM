package com.cboe.delegates.application;

import com.cboe.interfaces.application.OrderQueryV6;

public class OrderQueryV6Delegate extends com.cboe.idl.cmiV6.POA_OrderQuery_tie {
    public OrderQueryV6Delegate(OrderQueryV6 delegate) {
        super(delegate);
    }
}