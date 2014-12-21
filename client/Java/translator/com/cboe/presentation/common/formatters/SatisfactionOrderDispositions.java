//
// ------------------------------------------------------------------------
// FILE: SatisfactionOrderDispositions.java
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
public class SatisfactionOrderDispositions
{
    public static final short   SATISFIED_AS_SPECIFIED = com.cboe.idl.cmiConstants.SatisfactionOrderDispositions.SATISFIED_AS_SPECIFIED;
    public static final short   PRO_RATA_DISTRIBUTION = com.cboe.idl.cmiConstants.SatisfactionOrderDispositions.PRO_RATA_DISTRIBUTION;
    public static final short   ORDER_SIZE_MORE_THAN_TRADE_THROUGH = com.cboe.idl.cmiConstants.SatisfactionOrderDispositions.ORDER_SIZE_MORE_THAN_TRADE_THROUGH_SIZE;

    private static final String  SATISFIED_AS_SPECIFIED_STRING = "Satisfied as Specified";
    private static final String  PRO_RATA_DISTRIBUTION_STRING = "Pro-Rata Distribution";
    private static final String  ORDER_SIZE_MORE_THAN_TRADE_THROUGH_STRING = "Order Size More than Trade Through";

    public static final String  TRADERS_FORMAT = "TRADERS_FORMAT";

    public static final String  INVALID_FORMAT = "ERROR: Invalid Format Specifier";
    public static final String  INVALID_TYPE = "ERROR: Invalid Satisfaction Order Disposition Type Code";

    public static String toString(short disposition)
    {
        return toString(disposition, TRADERS_FORMAT);
    }
    public static String toString(short disposition, String format)
    {
        if(format.equals(TRADERS_FORMAT))
        {
            switch(disposition)
            {
                case SATISFIED_AS_SPECIFIED:
                    return SATISFIED_AS_SPECIFIED_STRING;
                case PRO_RATA_DISTRIBUTION:
                    return PRO_RATA_DISTRIBUTION_STRING;
                case ORDER_SIZE_MORE_THAN_TRADE_THROUGH:
                    return ORDER_SIZE_MORE_THAN_TRADE_THROUGH_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(' ').append(disposition).toString();
            }
        }
        return INVALID_FORMAT;
    }

    private SatisfactionOrderDispositions()
    {
    }

}
