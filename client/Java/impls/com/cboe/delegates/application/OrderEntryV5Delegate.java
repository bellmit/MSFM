package com.cboe.delegates.application;

import com.cboe.interfaces.application.OrderEntryV5;

/**
 * Created by IntelliJ IDEA.
 * User: beniwalv
 * Date: Oct 26, 2007
 */
public class OrderEntryV5Delegate extends com.cboe.idl.cmiV5.POA_OrderEntry_tie {
    public OrderEntryV5Delegate(OrderEntryV5 delegate) {
        super(delegate);
    }
}