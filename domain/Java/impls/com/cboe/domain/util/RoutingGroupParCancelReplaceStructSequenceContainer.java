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


import com.cboe.idl.order.ManualCancelReplaceStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupParCancelReplaceStructSequenceContainer
        extends RoutingGroupV2Container
{
    private ManualCancelReplaceStruct[] cancelReplaces;
    private RoutingParameterV2Struct routingParameterV2Struct;

    public RoutingGroupParCancelReplaceStructSequenceContainer(RoutingParameterV2Struct routingParameterV2Struct,
            ManualCancelReplaceStruct[] cancelReplaces)
    {
        super(routingParameterV2Struct);
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.cancelReplaces = cancelReplaces;
        this.routingParameterV2Struct =  routingParameterV2Struct;
    }
    public RoutingParameterV2Struct routingParameterV2Struct()
    {
        return routingParameterV2Struct;
    }
    public ManualCancelReplaceStruct[] getCancelReplaces()
    {
        //noinspection ReturnOfCollectionOrArrayField
        return cancelReplaces;
    }
}
