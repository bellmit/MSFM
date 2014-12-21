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
import com.cboe.idl.order.ManualOrderTimeoutRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

/**
 * Created by IntelliJ IDEA.
 * User: krueyay
 * Date: Dec 19, 2007
 * Time: 3:37:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoutingGroupManualOrderTimeoutContainer extends RoutingGroupV2Container
{
    private RoutingParameterV2Struct routingParameterV2Struct;
    private ManualOrderTimeoutRoutingStruct[] manualOrderTimeouts;


    public RoutingGroupManualOrderTimeoutContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                                  ManualOrderTimeoutRoutingStruct[] manualOrderTimeouts)
    {
        super(routingParameterV2Struct);
        this.routingParameterV2Struct = routingParameterV2Struct;
        this.manualOrderTimeouts = manualOrderTimeouts;
    }

    public RoutingParameterV2Struct getRoutingParameterV2Struct()
    {
        return routingParameterV2Struct;
    }

    public ManualOrderTimeoutRoutingStruct[] getManualOrderTimeouts()
    {
        return manualOrderTimeouts;
    }
}