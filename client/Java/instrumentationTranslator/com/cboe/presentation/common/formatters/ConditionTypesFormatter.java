//
// ------------------------------------------------------------------------
// FILE: NotificationTypesFormatter.java
// 
// PACKAGE: com.cboe.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.common.formatters;

import com.cboe.idl.alarmConstants.ConditionTypes;

import com.cboe.interfaces.instrumentation.common.formatters.ConditionTypesFormatStrategy;

public class ConditionTypesFormatter
        extends Formatter
        implements ConditionTypesFormatStrategy
{
    public static final short[] SUPPORTED_CONDITION_TYPES = new short[]
    {
        ConditionTypes.INSTRUMENTOR,
        ConditionTypes.PROCESS_WATCHER,
        ConditionTypes.LOGGING
    };

    public ConditionTypesFormatter()
    {
        addStyle(FULL_INFO_NAME, FULL_INFO_DESCRIPTION);
        addStyle(BRIEF_INFO_NAME, BRIEF_INFO_DESCRIPTION);

        setDefaultStyle(FULL_INFO_NAME);
    }

    public String format(short conditionType, String style)
    {
        String returnValue = "";
        if (style.equals(FULL_INFO_NAME))
        {
            returnValue = getConditionTypeString(conditionType);
        }
        else if (style.equals(BRIEF_INFO_NAME))
        {
            returnValue = getConditionTypeString(conditionType);
            if (returnValue.length() > 0)
            {
                returnValue = returnValue.substring(0, 1);
            }
        }
        return returnValue;
    }

    public String format(short conditionType)
    {
        return format(conditionType, getDefaultStyle());
    }

    private String getConditionTypeString(short notificationtype)
    {
        String returnValue = "";
        switch (notificationtype)
        {
            case ConditionTypes.INSTRUMENTOR:
                returnValue = "Instrumentor";
                break;
            case ConditionTypes.PROCESS_WATCHER:
                returnValue = "Process Watcher";
                break;
                
            case ConditionTypes.LOGGING:
                returnValue = "Logging";
                break;
            default:
        }
        return returnValue;
    }

    public short[] getConditionTypes()
    {
        return SUPPORTED_CONDITION_TYPES;
    }
}
