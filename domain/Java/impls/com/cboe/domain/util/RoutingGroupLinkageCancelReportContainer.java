//
// -----------------------------------------------------------------------------------
// Source file: RoutingGroupLinkageCancelReportContainer.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.order.LinkageCancelReportRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupLinkageCancelReportContainer
        extends RoutingGroupV2Container
{
    private LinkageCancelReportRoutingStruct[] linkageCancelReportRoutingStructs;

    @SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter"})
    public RoutingGroupLinkageCancelReportContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                                    LinkageCancelReportRoutingStruct[] linkageCancelReportRoutingStructs)
    {
        super(routingParameterV2Struct);
        this.linkageCancelReportRoutingStructs = linkageCancelReportRoutingStructs;
    }

    @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
    public LinkageCancelReportRoutingStruct[] getLinkageCancelReportStructs()
    {
        return linkageCancelReportRoutingStructs;
    }
}
