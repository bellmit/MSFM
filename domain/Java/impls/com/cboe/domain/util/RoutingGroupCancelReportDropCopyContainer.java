package com.cboe.domain.util;

import com.cboe.idl.util.RoutingParameterV2Struct;
import com.cboe.idl.order.CancelReportDropCopyRoutingStruct;



/**
 * Created by IntelliJ IDEA.
 * User: krueyay
 * Date: Dec 19, 2007
 * Time: 3:38:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoutingGroupCancelReportDropCopyContainer
{
    private RoutingParameterV2Struct routingParameterV2Struct;
    private CancelReportDropCopyRoutingStruct[]  cancelRoprtDropCopies ;


    public RoutingGroupCancelReportDropCopyContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                            CancelReportDropCopyRoutingStruct[]  cancelRoprtDropCopies )
    {
        
        this.routingParameterV2Struct = routingParameterV2Struct;
        this.cancelRoprtDropCopies = cancelRoprtDropCopies;

    }

    public RoutingParameterV2Struct getRoutingParameterV2Struct()
    {
        return routingParameterV2Struct;
    }

    public CancelReportDropCopyRoutingStruct[]  getCancelRoprtDropCopies()
    {
        return cancelRoprtDropCopies;
    }
}
