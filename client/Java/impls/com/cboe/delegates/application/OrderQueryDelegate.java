package com.cboe.delegates.application;

import com.cboe.interfaces.application.OrderQuery;

public class OrderQueryDelegate extends com.cboe.idl.cmi.POA_OrderQuery_tie {
    public OrderQueryDelegate(OrderQuery delegate) {
        super(delegate);
    }
}
