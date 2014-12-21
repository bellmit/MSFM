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

import com.cboe.idl.order.OrderManualHandlingStructV2;
import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupParOrderStructSequenceContainer
        extends RoutingGroupV2Container
{
    private OrderManualHandlingStructV2[] orders;
    private RoutingParameterV2Struct routingParameterV2Struct;

    public RoutingGroupParOrderStructSequenceContainer(
            RoutingParameterV2Struct routingParameterV2Struct, OrderManualHandlingStructV2[] orders)
    {
        super(routingParameterV2Struct);
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.routingParameterV2Struct =routingParameterV2Struct;
        this.orders = orders;
    }
    public RoutingParameterV2Struct getRoutingParameterV2Struct()
    {
        return routingParameterV2Struct;
    }
    public OrderManualHandlingStructV2[] getOrders()
    {
        //noinspection ReturnOfCollectionOrArrayField
        return orders;
    }
}
