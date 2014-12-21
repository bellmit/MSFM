/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.presentation.common.formatters
 * User: torresl
 * Date: Dec 12, 2002 4:15:33 PM
 */
package com.cboe.presentation.common.formatters;

public class OrderCancelTypes
{
    public static final short DESIRED_CANCEL_QUANTITY = com.cboe.idl.cmiConstants.OrderCancelTypes.DESIRED_CANCEL_QUANTITY;
    public static final short DESIRED_REMAINING_QUANTITY = com.cboe.idl.cmiConstants.OrderCancelTypes.DESIRED_REMAINING_QUANTITY;
    public static final short CANCEL_ALL_QUANTITY = com.cboe.idl.cmiConstants.OrderCancelTypes.CANCEL_ALL_QUANTITY;

    private static final String DESIRED_CANCEL_QUANTITY_STRING = "CXL";
    private static final String DESIRED_REMAINING_QUANTITY_STRING = "CXL/RE";
    private static final String CANCEL_ALL_QUANTITY_STRING = "CXL ALL";

    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";

    private static final String INVALID_FORMAT_STRING = "INVALID FORMAT";
    private static final String INVALID_TYPE_STRING = "INVALID TYPE";

    private OrderCancelTypes()
    {
    }
    public static String toString(short orderCancelType)
    {
        return toString(orderCancelType, TRADERS_FORMAT);
    }
    public static String toString(short orderCancelType, String format)
    {
        if(format.equals(TRADERS_FORMAT))
        {
            switch(orderCancelType)
            {
                case DESIRED_CANCEL_QUANTITY:
                    return DESIRED_CANCEL_QUANTITY_STRING;
                case DESIRED_REMAINING_QUANTITY:
                    return DESIRED_REMAINING_QUANTITY_STRING;
                case CANCEL_ALL_QUANTITY:
                    return CANCEL_ALL_QUANTITY_STRING;
            }
            return new StringBuffer(20).append("[ ").append(orderCancelType).append(" ]").toString();
        }
        return INVALID_FORMAT_STRING;
    }
}
