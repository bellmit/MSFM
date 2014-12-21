//
// -----------------------------------------------------------------------------------
// Source file: RoutingGroupFillReportRejectContainer.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.util.RoutingParameterV2Struct;
import com.cboe.idl.order.FillReportRejectRoutingStruct;

public class RoutingGroupFillReportRejectContainer
        extends RoutingGroupV2Container
{

    private FillReportRejectRoutingStruct[]  fillReportRejects;

    public RoutingGroupFillReportRejectContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                                 FillReportRejectRoutingStruct[]  fillReportRejects )
    {
        super(routingParameterV2Struct);
        this.fillReportRejects = fillReportRejects;
    }

    public FillReportRejectRoutingStruct[] getFillReportRejects()
    {
        return fillReportRejects;
    }
}
