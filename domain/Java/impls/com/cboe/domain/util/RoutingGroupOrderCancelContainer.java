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

import com.cboe.idl.order.CancelRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupOrderCancelContainer
        extends RoutingGroupV2Container
{
    private CancelRoutingStruct[] cancelRoutingStructs;

    public RoutingGroupOrderCancelContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                            CancelRoutingStruct[] cancelRoutingStructs)
    {
        super(routingParameterV2Struct);
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.cancelRoutingStructs = cancelRoutingStructs;
    }

    @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
    public CancelRoutingStruct[] getCancelRoutingStructs()
    {
        return cancelRoutingStructs;
    }
}
