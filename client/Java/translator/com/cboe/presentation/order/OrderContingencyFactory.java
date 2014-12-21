//
// -----------------------------------------------------------------------------------
// Source file: OrderContingencyFactory.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.idl.cmiConstants.ContingencyTypes;
import com.cboe.idl.cmiConstants.PriceTypes;

import com.cboe.interfaces.presentation.order.OrderContingency;

import com.cboe.domain.util.StructBuilder;

public class OrderContingencyFactory
{
    public static OrderContingency createOrderContingency(OrderContingencyStruct orderContingencyStruct)
    {
        return new OrderContingencyImpl(orderContingencyStruct);
    }

    public static OrderContingency createOrderContingencyWithType(short type)
    {
        OrderContingencyImpl contingency = new OrderContingencyImpl(createDefaultStruct());
        contingency.type = type;
        contingency.orderContingencyStruct.type = type;
        return contingency;
    }

    static OrderContingencyStruct createDefaultStruct()
    {
        OrderContingencyStruct newStruct = new OrderContingencyStruct();
        newStruct.type = ContingencyTypes.NONE;
        newStruct.price = StructBuilder.buildPriceStruct();
        newStruct.price.type = PriceTypes.VALUED;
        newStruct.volume = 0;
        return newStruct;
    }
}