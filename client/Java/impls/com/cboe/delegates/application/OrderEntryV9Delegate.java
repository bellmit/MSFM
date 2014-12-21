package com.cboe.delegates.application;

public class OrderEntryV9Delegate extends com.cboe.idl.cmiV9.POA_OrderEntry_tie
{
    public OrderEntryV9Delegate(com.cboe.interfaces.application.OrderEntryV9 delegate)
    {
        super(delegate);
    }
}