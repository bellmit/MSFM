//
// -----------------------------------------------------------------------------------
// Source file: SettlementTypes.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

public class SettlementTypes
{
    public static final short AM_SETTLED = com.cboe.idl.constants.SettlementTypes.AM_SETTLED;
    public static final short PM_SETTLED = com.cboe.idl.constants.SettlementTypes.PM_SETTLED;

    public static final short[] ALL_TYPES = {AM_SETTLED, PM_SETTLED};

    public static final String NORMAL_FORMAT = "NORMAL_FORMAT";

    private static final String INVALID_FORMAT = "INVALID_FORMAT";
    private static final String INVALID_TYPE   = "INVALID_TYPE";

    private static final String AM_SETTLED_STRING = "AM";
    private static final String PM_SETTLED_STRING = "PM";

    private SettlementTypes ()
    {}

    public static boolean validateSettlementType(short settlementType)
    {
        switch(settlementType)
        {
            case AM_SETTLED:
            case PM_SETTLED:
                return true;
            default:
                return false;
        }
    }

    public static String toString(short settlementType)
    {
        return toString(settlementType, NORMAL_FORMAT);
    }

    public static String toString(short settlementType, String formatSpecifier)
    {
        if(formatSpecifier.equals(NORMAL_FORMAT))
        {
            switch (settlementType)
            {
                case AM_SETTLED:
                    return AM_SETTLED_STRING;
                case PM_SETTLED:
                    return PM_SETTLED_STRING;
                default:
                    return new StringBuffer(30).append(INVALID_TYPE).append(' ').append(settlementType).toString();
            }
        }
        return new StringBuffer(30).append(INVALID_FORMAT).append(' ').append(formatSpecifier).toString();
    }
}