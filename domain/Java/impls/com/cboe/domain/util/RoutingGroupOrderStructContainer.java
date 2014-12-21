package com.cboe.domain.util;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.util.RoutingParameterStruct;
/**
 * This is container class for our data struct.
 * @author Connie Liang
 */
public class RoutingGroupOrderStructContainer {
    private RoutingParameterStruct routing;
    private short statusChange;
    private OrderStruct order;

    /**
      * Sets the internal fields to the passed values
      */
    public RoutingGroupOrderStructContainer(RoutingParameterStruct routing, OrderStruct order, short statusChange)
    {
 		this.routing = routing;
        this.statusChange = statusChange;
        this.order = order;
    }
    public RoutingParameterStruct getRoutingParameters()
    {
        return routing;
    }

    public short getStatusChange()
    {
        return statusChange;
    }

    public OrderStruct getOrderStruct()
    {
        return order;
    }
}
