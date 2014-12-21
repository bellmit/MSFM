//
// -----------------------------------------------------------------------------------
// Source file: RoutingGroupOrderCancelReplaceContainer.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.order.CancelReplaceRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupOrderCancelReplaceContainer
        extends RoutingGroupV2Container
{
    private CancelReplaceRoutingStruct[] cancelReplaceRoutingStructs;

    public RoutingGroupOrderCancelReplaceContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                                   CancelReplaceRoutingStruct[] cancelReplaceRoutingStructs)
    {
        super(routingParameterV2Struct);
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.cancelReplaceRoutingStructs = cancelReplaceRoutingStructs;
    }

    public CancelReplaceRoutingStruct[] getCancelReplaceRoutingStructs()
    {
        //noinspection ReturnOfCollectionOrArrayField
        return cancelReplaceRoutingStructs;
    }
}
