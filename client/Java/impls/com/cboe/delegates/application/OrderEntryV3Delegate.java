package com.cboe.delegates.application;

import com.cboe.interfaces.application.OrderEntryV3;

public class OrderEntryV3Delegate extends com.cboe.idl.cmiV3.POA_OrderEntry_tie {
    public OrderEntryV3Delegate(OrderEntryV3 delegate) {
        super(delegate);
    }
}
