package com.cboe.delegates.application;

import com.cboe.interfaces.application.OrderManagementService;

public class OrderManagementServiceDelegate extends com.cboe.idl.omt.POA_OrderManagementService_tie
{
    public OrderManagementServiceDelegate(OrderManagementService delegate)
    {
        super(delegate);
    }
}
