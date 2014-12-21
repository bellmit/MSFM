package com.cboe.interfaces.domain.routingProperty.common;

public enum OrderOriginator {

    UNSPECIFIED(com.cboe.idl.constants.OrderSource.UNSPECIFIED, "Unspecified"),
    TE(com.cboe.idl.constants.OrderSource.TE, "Trade Engine"),
    PAR(com.cboe.idl.constants.OrderSource.PAR, "PAR");

    public char orderOriginator;
    public String description;

    OrderOriginator(char p_orderLocation, String p_description)
    {
        this.orderOriginator = p_orderLocation;
        this.description = description;
    }

    public char getOrderOriginator()
    {
        return orderOriginator;
    }

    public String toString()
    {
        return description;
    }

    // convenience method to provide lookup from the IDL constant to the enum
    public static OrderOriginator findOrderOriginatorEnum(char orderOriginator)
    {
        OrderOriginator retVal = OrderOriginator.UNSPECIFIED;
        for(OrderOriginator tmpLocation : OrderOriginator.values())
        {
            if(orderOriginator == tmpLocation.getOrderOriginator())
            {
                retVal = tmpLocation;
                break;
            }
        }
        return retVal;
    }
    
    /**
     * Convenience method to provide lookup from name to the enum
     * @param OrderOriginator
     * @return
     */
    public static OrderOriginator findOrderOriginatorEnum(String orderSource)
    {
        OrderOriginator retVal = OrderOriginator.UNSPECIFIED;
        for(OrderOriginator tmpLocation : OrderOriginator.values())
        {
            if(tmpLocation.name().equalsIgnoreCase(orderSource))
            {
                retVal = tmpLocation;
                break;
            }
        }
        return retVal;
    }


}
