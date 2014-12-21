//
// -----------------------------------------------------------------------------------
// Source file: RoutingGroupOrderStructSequenceContainer.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.order.OrderRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupOrderStructSequenceContainer
        extends RoutingGroupV2Container
{
    private OrderRoutingStruct[] orderStructs;

    public RoutingGroupOrderStructSequenceContainer(
            RoutingParameterV2Struct routingParameterV2Struct, OrderRoutingStruct[] orderStructs)
    {
        super(routingParameterV2Struct);
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.orderStructs = orderStructs;
    }

    public OrderRoutingStruct[] getOrderStructs()
    {
        //noinspection ReturnOfCollectionOrArrayField
        return orderStructs;
    }
}
