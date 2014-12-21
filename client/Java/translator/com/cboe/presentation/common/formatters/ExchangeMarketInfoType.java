//
// ------------------------------------------------------------------------
// Source file: {FILE_NAME}
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2002 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

/**
 * @author torresl@cboe.com
 */
public class ExchangeMarketInfoType
{
    public static final short   NBBO_ORDER_RECEIVED = com.cboe.idl.cmiConstants.ExchangeMarketInfoType.NBBO_ORDER_RECEIVED;
    public static final short   NBBO_ORDER_EXECUTED = com.cboe.idl.cmiConstants.ExchangeMarketInfoType.NBBO_ORDER_EXECUTED;
    public static final short   BBO_ORDER_RECEIVED = com.cboe.idl.cmiConstants.ExchangeMarketInfoType.BBO_ORDER_RECEIVED;
    public static final short   BBO_ORDER_EXECUTED = com.cboe.idl.cmiConstants.ExchangeMarketInfoType.BBO_ORDER_EXECUTED;
    public static final short   WORST_NBBO_IN_TIME_WINDOW = com.cboe.idl.cmiConstants.ExchangeMarketInfoType.WORST_NBBO_IN_TIME_WINDOW;
    public static final short   WORST_BBO_IN_TIME_WINDOW = com.cboe.idl.cmiConstants.ExchangeMarketInfoType.WORST_BBO_IN_TIME_WINDOW;

    public static final String  NBBO_ORDER_RECEIVED_STRING = "NBBO Order Received";
    public static final String  NBBO_ORDER_EXECUTED_STRING = "NBBO Order Executed";
    public static final String  BBO_ORDER_RECEIVED_STRING = "BBO Order Received";
    public static final String  BBO_ORDER_EXECUTED_STRING  = "BBO Order Executed";
    public static final String  WORST_NBBO_IN_TIME_WINDOW_STRING = "Worst NBBO in Time Window";
    public static final String  WORST_BBO_IN_TIME_WINDOW_STRING = "Worst BBO in Time Window";

    public static final String  TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String  INVALID_FORMAT = "INVALID_FORMAT";
    public static final String  INVALID_TYPE = "INVALID_TYPE";

    private ExchangeMarketInfoType()
    {
    }
    public static String toString(short type)
    {
        return toString(type, TRADERS_FORMAT);
    }
    public static String toString(short type, String format)
    {
        if(format.equals(TRADERS_FORMAT))
        {
            switch(type)
            {
                case NBBO_ORDER_RECEIVED:
                    return NBBO_ORDER_RECEIVED_STRING;
                case NBBO_ORDER_EXECUTED:
                    return NBBO_ORDER_EXECUTED_STRING;
                case BBO_ORDER_EXECUTED:
                    return BBO_ORDER_EXECUTED_STRING;
                case BBO_ORDER_RECEIVED:
                    return BBO_ORDER_RECEIVED_STRING;
                case WORST_NBBO_IN_TIME_WINDOW:
                    return WORST_NBBO_IN_TIME_WINDOW_STRING;
                case WORST_BBO_IN_TIME_WINDOW:
                    return WORST_BBO_IN_TIME_WINDOW_STRING;
                default:
//                    return new StringBuffer(20).append(INVALID_TYPE).append(" ").append(type).toString();
                    return new StringBuffer(20).append("[ ").append(type).append(" ]").toString();
            }
        }
        return INVALID_FORMAT;
    }

}
