package com.cboe.domain.util;

import com.cboe.interfaces.domain.routingProperty.common.ExceptionTypeEnum;

public enum OrderStatesExtEnum 
{
    PAR(com.cboe.idl.constants.OrderStatesExtOperations.ROUTED_TO_CROWD, "Routed To PAR or Crowd OMT"),
    BOOTH(com.cboe.idl.constants.OrderStatesExtOperations.ROUTED_TO_BOOTH, "Routed To Booth"),
    HELP_DESK(com.cboe.idl.constants.OrderStatesExtOperations.ROUTED_TO_HELP_DESK, "Routed To Help Desk OMT"),
    OHS(com.cboe.idl.constants.OrderStatesExtOperations.NOT_ROUTED, "Not Routed");

    private short orderState;
    private String description;
    
    OrderStatesExtEnum(short orderState, String description)
    {
        this.orderState = orderState;
        this.description = description;
    }
    
    public short getOrderStateExt()
    {
        return orderState;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public static OrderStatesExtEnum findOrderStatesEnum(short propertyValue)
    {
        OrderStatesExtEnum retVal = null;
        for(OrderStatesExtEnum oseType : OrderStatesExtEnum.values())
        {
            if(propertyValue == oseType.ordinal())
            {
                retVal = oseType;
                break;
            }
        }
        return retVal;
    }
}
