//
// ------------------------------------------------------------------------
// FILE: OrderFlowDirectionType.java
// 
// PACKAGE: com.cboe.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.formatters;

/**
 * @author torresl@cboe.com
 */
public class OrderFlowDirectionType
{
    public static final short INBOUND = com.cboe.idl.cmiConstants.OrderFlowDirectionType.INBOUND;
    public static final short OUTBOUND = com.cboe.idl.cmiConstants.OrderFlowDirectionType.OUTBOUND;
    public static final short BOTH = com.cboe.idl.cmiConstants.OrderFlowDirectionType.BOTH;

    protected static final String INBOUND_STRING = "Inbound";
    protected static final String OUTBOUND_STRING = "Outbound";
    protected static final String BOTH_STRING = "Both";

    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_TYPE_CODE = "INVALID_TYPE_CODE";

    private OrderFlowDirectionType()
    {
    }
    public static String toString(short orderFlowDirection)
    {
        return toString(orderFlowDirection, TRADERS_FORMAT);
    }
    public static String toString(short orderFlowDirection, String format)
    {
        if(!format.equals(TRADERS_FORMAT))
        {
            return INVALID_FORMAT;
        }
        else
        {
            switch(orderFlowDirection)
            {
                case INBOUND:
                    return INBOUND_STRING;
                case OUTBOUND:
                    return OUTBOUND_STRING;
                case BOTH:
                    return BOTH_STRING;
                default:
                    return new StringBuffer("ERROR: Invalid Code - "+orderFlowDirection).toString();
            }
        }
    }
}
