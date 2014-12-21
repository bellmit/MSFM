//
// -----------------------------------------------------------------------------------
// Source file: RoutingGroupOrderIdStructSequenceContainer.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.order.OrderIdRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupOrderIdStructSequenceContainer
        extends RoutingGroupV2Container
{
    private OrderIdRoutingStruct[] orderIdStructs;

    public RoutingGroupOrderIdStructSequenceContainer(
            RoutingParameterV2Struct routingParameterV2Struct, OrderIdRoutingStruct[] orderIdStructs)
    {
        super(routingParameterV2Struct);
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.orderIdStructs = orderIdStructs;
    }

    public OrderIdRoutingStruct[] getOrderIdStructs()
    {
        //noinspection ReturnOfCollectionOrArrayField
        return orderIdStructs;
    }
}
