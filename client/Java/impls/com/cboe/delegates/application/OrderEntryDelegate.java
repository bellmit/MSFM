package com.cboe.delegates.application;

import com.cboe.interfaces.application.OrderEntry;

public class OrderEntryDelegate extends com.cboe.idl.cmi.POA_OrderEntry_tie {
    public OrderEntryDelegate(OrderEntry delegate) {
        super(delegate);
    }
}
