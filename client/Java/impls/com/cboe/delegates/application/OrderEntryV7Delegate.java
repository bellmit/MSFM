package com.cboe.delegates.application;

public class OrderEntryV7Delegate extends com.cboe.idl.cmiV7.POA_OrderEntry_tie 
{
    public OrderEntryV7Delegate(com.cboe.interfaces.application.OrderEntryV7 delegate)
    {
        super(delegate);
    }
}
