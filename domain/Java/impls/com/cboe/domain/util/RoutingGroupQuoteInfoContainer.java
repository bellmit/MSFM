package com.cboe.domain.util;

import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.quote.QuoteInfoStruct;
/**
 * This is container class for our data struct.
 * @author Connie Liang
 */
public class RoutingGroupQuoteInfoContainer {
    private RoutingParameterStruct routing;
    private short statusChange;
    private QuoteInfoStruct quote;

    /**
      * Sets the internal fields to the passed values
      */
    public RoutingGroupQuoteInfoContainer(RoutingParameterStruct routing, QuoteInfoStruct quote, short statusChange)
    {
 		this.routing = routing;
        this.statusChange = statusChange;
        this.quote = quote;
    }
    public RoutingParameterStruct getRoutingParameters()
    {
        return routing;
    }

    public short getStatusChange()
    {
        return statusChange;
    }

    public QuoteInfoStruct getQuoteInfoStruct()
    {
        return quote;
    }
}
