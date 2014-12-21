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

import com.cboe.idl.order.LinkageFillReportRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupLinkageFillReportContainer
        extends RoutingGroupV2Container
{
    private LinkageFillReportRoutingStruct[] fillReports;

    @SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter"})
    public RoutingGroupLinkageFillReportContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                                  LinkageFillReportRoutingStruct[] fillReports)
    {
        super(routingParameterV2Struct);
        this.fillReports = fillReports;
    }

    @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
    public LinkageFillReportRoutingStruct[] getFillReports()
    {
        return fillReports;
    }
}