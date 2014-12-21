package com.cboe.domain.util;

import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
/**
 * This is container class for our data struct.
 * @author Connie Liang
 */
public class RoutingGroupQuoteContainer {
    private RoutingParameterStruct routing;
    private short statusChange;
    private QuoteStruct quote;

    /**
      * Sets the internal fields to the passed values
      */
    public RoutingGroupQuoteContainer(RoutingParameterStruct routing, QuoteStruct quote, short statusChange)
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

    public QuoteStruct getQuoteStruct()
    {
        return quote;
    }
}
