//
// -----------------------------------------------------------------------------------
// Source file: RoutingGroupOrderCancelContainer.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.order.ManualCancelRequestStructV2;
import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupParCancelRequestStructSequenceContainer
        extends RoutingGroupV2Container
{
    private ManualCancelRequestStructV2[] cancelRequests;
    private RoutingParameterV2Struct routingParameterV2Struct;

    public RoutingGroupParCancelRequestStructSequenceContainer(RoutingParameterV2Struct routingParameterV2Struct,
            ManualCancelRequestStructV2[] cancelRequests)
    {
        super(routingParameterV2Struct);
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.routingParameterV2Struct = routingParameterV2Struct;
        this.cancelRequests= cancelRequests;
    }
    
    public RoutingParameterV2Struct getRoutingParameterV2Struct()
    {
        return routingParameterV2Struct;
    }

    @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
    public ManualCancelRequestStructV2[] getCancelRequests()
    {
        return cancelRequests;
    }
}
