//
// -----------------------------------------------------------------------------------
// Source file: RoutingGroupV2Container.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupV2Container
{
    protected RoutingParameterV2Struct routingParameterV2Struct;

    protected RoutingGroupV2Container(RoutingParameterV2Struct routingParameterV2Struct)
    {
        this.routingParameterV2Struct = routingParameterV2Struct;
    }

    public RoutingParameterV2Struct getRoutingParameterV2Struct()
    {
        return routingParameterV2Struct;
    }
}
