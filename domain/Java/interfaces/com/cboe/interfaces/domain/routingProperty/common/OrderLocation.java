//
// -----------------------------------------------------------------------------------
// Source file: OrderLocation.java
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.routingProperty.common;

public enum OrderLocation
{
    UNSPECIFIED(com.cboe.idl.constants.OrderLocations.UNSPECIFIED, "Unspecified"),
    CMI(com.cboe.idl.constants.OrderLocations.CMI, "CMI"),
    TPF(com.cboe.idl.constants.OrderLocations.TPF, "TPF"),
    TE(com.cboe.idl.constants.OrderLocations.TE, "TE"),
    PAR(com.cboe.idl.constants.OrderLocations.PAR, "PAR"),
    BOOTH(com.cboe.idl.constants.OrderLocations.BOOTH_OMT, "BOOTH"),
    CROWD(com.cboe.idl.constants.OrderLocations.CROWD_OMT, "Crowd"),
    HELP_DESK(com.cboe.idl.constants.OrderLocations.HELP_DESK_OMT, "Help Desk"),
    OHS(com.cboe.idl.constants.OrderLocations.OHS, "OHS"),
    DISPLAY(com.cboe.idl.constants.OrderLocations.DISPLAY, "Display");

    public short orderLocation;
    public String description;

    OrderLocation(short orderLocation, String description)
    {
        this.orderLocation = orderLocation;
        this.description = description;
    }

    public short getOrderLocation()
    {
        return orderLocation;
    }

    public String toString()
    {
        return description;
    }

    // convenience method to provide lookup from the IDL constant to the enum
    public static OrderLocation findOrderLocationEnum(short orderLocation)
    {
        OrderLocation retVal = OrderLocation.UNSPECIFIED;
        for(OrderLocation tmpLocation : OrderLocation.values())
        {
            if(orderLocation == tmpLocation.getOrderLocation())
            {
                retVal = tmpLocation;
                break;
            }
        }
        return retVal;
    }
    
    /**
     * Convenience method to provide lookup from name to the enum
     * @param orderLocation
     * @return
     */
    public static OrderLocation findOrderLocationEnum(String orderLocation)
    {
        OrderLocation retVal = OrderLocation.UNSPECIFIED;
        for(OrderLocation tmpLocation : OrderLocation.values())
        {
            if(tmpLocation.name().equalsIgnoreCase(orderLocation))
            {
                retVal = tmpLocation;
                break;
            }
        }
        return retVal;
    }
}
