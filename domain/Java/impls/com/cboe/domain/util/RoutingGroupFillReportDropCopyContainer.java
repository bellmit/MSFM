package com.cboe.domain.util;

import com.cboe.idl.util.RoutingParameterV2Struct;
import com.cboe.idl.order.FillReportDropCopyRoutingStruct;


/**
 * Created by IntelliJ IDEA.
 * User: krueyay
 * Date: Dec 19, 2007
 * Time: 3:37:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoutingGroupFillReportDropCopyContainer extends RoutingGroupV2Container
{
    private RoutingParameterV2Struct routingParameterV2Struct;
    private FillReportDropCopyRoutingStruct[] fillReportDropCopies;
    

    public RoutingGroupFillReportDropCopyContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                            FillReportDropCopyRoutingStruct[] fillReportDropCopies)
    {
        super(routingParameterV2Struct);
        this.routingParameterV2Struct = routingParameterV2Struct;
        this.fillReportDropCopies = fillReportDropCopies;
    }

    public RoutingParameterV2Struct getRoutingParameterV2Struct()
    {
        return routingParameterV2Struct;
    }

    public FillReportDropCopyRoutingStruct[] getFillReportDropCopies()
    {
        return fillReportDropCopies;
    }
}
