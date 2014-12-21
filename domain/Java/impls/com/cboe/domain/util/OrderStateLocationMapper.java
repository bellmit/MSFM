package com.cboe.domain.util;

import java.util.HashMap;
import java.util.Map;

public class OrderStateLocationMapper
{
    private static Map<Short, OrderStatesExtEnum> locationLookup;
    static {
        locationLookup = new HashMap<Short, OrderStatesExtEnum>();
        locationLookup.put(new Short(com.cboe.idl.constants.OrderLocations.PAR), OrderStatesExtEnum.PAR);
        locationLookup.put(new Short(com.cboe.idl.constants.OrderLocations.BOOTH_OMT), OrderStatesExtEnum.BOOTH);
        locationLookup.put(new Short(com.cboe.idl.constants.OrderLocations.CROWD_OMT), OrderStatesExtEnum.PAR);
        locationLookup.put(new Short(com.cboe.idl.constants.OrderLocations.HELP_DESK_OMT), OrderStatesExtEnum.HELP_DESK);
        locationLookup.put(new Short(com.cboe.idl.constants.OrderLocations.OHS), OrderStatesExtEnum.OHS);
    }
    
    public OrderStateLocationMapper(){}
 
    public OrderStatesExtEnum getOrderStatesExt(short locationType)
    {
        return locationLookup.get(locationType);
    }
    
    public static OrderStatesExtEnum getOrderStates(short locationType)
    {
        return locationLookup.get(locationType);
    }
}
