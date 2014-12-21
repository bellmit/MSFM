package com.cboe.domain.util;

import com.cboe.idl.order.TradeNotificationRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

/**
 * Created by IntelliJ IDEA.
 * User: krueyay
 * Date: Nov 16, 2007
 * Time: 1:53:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoutingGroupTradeNotificationContainer extends RoutingGroupV2Container
{

    private RoutingParameterV2Struct routingParameterV2Struct;
    private TradeNotificationRoutingStruct[] tradeNotifications;


    public RoutingGroupTradeNotificationContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                                  TradeNotificationRoutingStruct[] tradeNotifications)
    {
        super(routingParameterV2Struct);
        this.routingParameterV2Struct = routingParameterV2Struct;
        this.tradeNotifications = tradeNotifications;
    }

    public RoutingParameterV2Struct getRoutingParameterV2Struct()
    {
        return routingParameterV2Struct;
    }

    public TradeNotificationRoutingStruct[] getTradeNotifications()
    {
        return tradeNotifications;
    }
}

