//
// -----------------------------------------------------------------------------------
// Source file: RoutingGroupManualFillTimeoutContainer.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.order.ManualFillTimeoutRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

/**
 * Created by IntelliJ IDEA.
 * User: krueyay
 * Date: Dec 19, 2007
 * Time: 3:37:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoutingGroupManualFillTimeoutContainer extends RoutingGroupV2Container
{
    private RoutingParameterV2Struct routingParameterV2Struct;
    private ManualFillTimeoutRoutingStruct[] manualFillTimeouts;


    public RoutingGroupManualFillTimeoutContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                                  ManualFillTimeoutRoutingStruct[] manualFillTimeouts)
    {
        super(routingParameterV2Struct);
        this.routingParameterV2Struct = routingParameterV2Struct;
        this.manualFillTimeouts = manualFillTimeouts;
    }

    public RoutingParameterV2Struct getRoutingParameterV2Struct()
    {
        return routingParameterV2Struct;
    }

    public ManualFillTimeoutRoutingStruct[] getManualFillTimeouts()
    {
        return manualFillTimeouts;
    }
}
