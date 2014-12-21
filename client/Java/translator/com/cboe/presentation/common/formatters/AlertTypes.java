/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.presentation.common.formatters
 * User: torresl
 * Date: Jan 9, 2003 11:20:48 AM
 */
package com.cboe.presentation.common.formatters;

public class AlertTypes
{
    public static final short   CBOE_TRADE_THROUGH = com.cboe.idl.cmiConstants.AlertTypes.CBOE_TRADE_THROUGH;
    public static final short   NBBO_TRADE_THROUGH = com.cboe.idl.cmiConstants.AlertTypes.NBBO_TRADE_THROUGH;
    public static final short   NON_EXECUTION = com.cboe.idl.cmiConstants.AlertTypes.NON_EXECUTION;
    public static final short   NO_NBBO_AGENT = com.cboe.idl.cmiConstants.AlertTypes.NO_NBBO_AGENT;
    public static final short   SATISFACTION_ALERT = com.cboe.idl.cmiConstants.AlertTypes.SATISFACTION_ALERT;

    public static final short   MAX_ALERT_TYPE = SATISFACTION_ALERT;

    public static final String  CBOE_TRADE_THROUGH_STRING = "CBOE Trade Through";
    public static final String  NBBO_TRADE_THROUGH_STRING = "NBBO Trade Through";
    public static final String  NON_EXECUTION_STRING = "Non Execution";
    public static final String  NO_NBBO_AGENT_STRING = "No NBBO Agent";
    public static final String  SATISFACTION_ALERT_STRING = "Satisfaction Alert";

    public static final String  TRADERS_FORMAT = "TRADERS_FORMAT";
    public static final String  INVALID_FORMAT = "INVALID_FORMAT";
    public static final String  INVALID_TYPE = "INVALID_TYPE";

    private AlertTypes()
    {
    }
    public static String toString(short alertType)
    {
        return toString(alertType, TRADERS_FORMAT);
    }
    public static String toString(short alertType, String format)
    {
        if(format.equals(TRADERS_FORMAT))
        {
            switch(alertType)
            {
                case CBOE_TRADE_THROUGH:
                    return CBOE_TRADE_THROUGH_STRING;
                case NBBO_TRADE_THROUGH:
                    return NBBO_TRADE_THROUGH_STRING;
                case NON_EXECUTION:
                    return NON_EXECUTION_STRING;
                case NO_NBBO_AGENT:
                    return NO_NBBO_AGENT_STRING;
                case SATISFACTION_ALERT:
                    return SATISFACTION_ALERT_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(" ").append(alertType).toString();
            }
        }
        else
        {
            return new StringBuffer(30).append(INVALID_FORMAT).append(" ").append(format).toString();
        }
    }
}
