//
// -----------------------------------------------------------------------------------
// Source file: OrderContingencyImpl.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.order.OrderContingency;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;

class OrderContingencyImpl implements OrderContingency
{
    protected short type;
    protected Price price;
    protected int   volume;
    protected OrderContingencyStruct orderContingencyStruct;

    public OrderContingencyImpl(OrderContingencyStruct orderContingencyStruct)
    {
        this.orderContingencyStruct = orderContingencyStruct;
        initialize();
    }

    private void initialize()
    {
        type = orderContingencyStruct.type;
        price = DisplayPriceFactory.create(orderContingencyStruct.price);
        volume = orderContingencyStruct.volume;
    }

    public short getType()
    {
        return type;
    }

    public Price getPrice()
    {
        return price;
    }

    public int getVolume()
    {
        return volume;
    }

    /**
     * Gets the underlying struct
     * @return OrderContingencyStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderContingencyStruct getStruct()
    {
        return orderContingencyStruct;
    }

    public Object getKey()
    {
        return new Integer(hashCode());
    }

    public Object clone() throws CloneNotSupportedException
    {
        OrderContingencyStruct orderContingencyStruct = new OrderContingencyStruct(
                getType(),
                getPrice().toStruct(),
                getVolume());

        return new OrderContingencyImpl(orderContingencyStruct);
    }
}
