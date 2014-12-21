//
// -----------------------------------------------------------------------------------
// Source file: WatchdogStateFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.idl.alarmConstants.NotificationWatchdogLimitTypes;

import com.cboe.interfaces.instrumentation.common.formatters.WatchdogLimitTypeFormatStrategy;

public class WatchdogLimitTypeFormatter extends Formatter implements WatchdogLimitTypeFormatStrategy
{
    private static final short[] ALL_LIMIT_TYPES = {NotificationWatchdogLimitTypes.WITHIN_TIME,
                                                    NotificationWatchdogLimitTypes.FIRE_UNTIL_N,
                                                    NotificationWatchdogLimitTypes.FIRE_AFTER_N,
                                                    NotificationWatchdogLimitTypes.SNOOZE,
                                                    NotificationWatchdogLimitTypes.PULSE};

    private static final String WITHIN_TIME_FULL_STRING = "Time Based";
    private static final String FIRE_UNTIL_N_FULL_STRING = "Fire Until, Within Time";
    private static final String FIRE_AFTER_N_FULL_STRING = "Fire After, Within Time";
    private static final String SNOOZE_FULL_STRING = "Snooze, Within Time";
    private static final String PULSE_FULL_STRING = "Pulse, Within Time on Interval";

    private static final String WITHIN_TIME_BRIEF_STRING = "Timed";
    private static final String FIRE_UNTIL_N_BRIEF_STRING = "Fire Until";
    private static final String FIRE_AFTER_N_BRIEF_STRING = "Fire After";
    private static final String SNOOZE_BRIEF_STRING = "Snooze";
    private static final String PULSE_BRIEF_STRING = "Pulse";

    WatchdogLimitTypeFormatter()
    {
        addStyle(FULL_INFO_NAME, FULL_INFO_DESCRIPTION);
        addStyle(BRIEF_INFO_NAME, BRIEF_INFO_DESCRIPTION);
        setDefaultStyle(FULL_INFO_NAME);
    }

    public short[] getLimitTypes()
    {
        return ALL_LIMIT_TYPES;
    }

    public String format(short limitType)
    {
        return format(limitType, getDefaultStyle());
    }

    public String format(short limitType, String style)
    {
        String formatted;
        if(FULL_INFO_NAME.equals(style))
        {
            formatted = getFullName(limitType);
        }
        else if(BRIEF_INFO_NAME.equals(style))
        {
            formatted = getBriefName(limitType);
        }
        else
        {
            formatted = "UNKNOWN:" + limitType;
        }
        return formatted;
    }

    private String getFullName(short limitType)
    {
        String name;
        switch(limitType)
        {
            case NotificationWatchdogLimitTypes.WITHIN_TIME:
                name = WITHIN_TIME_FULL_STRING;
                break;
            case NotificationWatchdogLimitTypes.FIRE_UNTIL_N:
                name = FIRE_UNTIL_N_FULL_STRING;
                break;
            case NotificationWatchdogLimitTypes.FIRE_AFTER_N:
                name = FIRE_AFTER_N_FULL_STRING;
                break;
            case NotificationWatchdogLimitTypes.SNOOZE:
                name = SNOOZE_FULL_STRING;
                break;
            case NotificationWatchdogLimitTypes.PULSE:
                name = PULSE_FULL_STRING;
                break;
            default:
                name = "UNKNOWN:" + limitType;
                break;
        }
        return name;
    }

    private String getBriefName(short limitType)
    {
        String name;
        switch(limitType)
        {
            case NotificationWatchdogLimitTypes.WITHIN_TIME:
                name = WITHIN_TIME_BRIEF_STRING;
                break;
            case NotificationWatchdogLimitTypes.FIRE_UNTIL_N:
                name = FIRE_UNTIL_N_BRIEF_STRING;
                break;
            case NotificationWatchdogLimitTypes.FIRE_AFTER_N:
                name = FIRE_AFTER_N_BRIEF_STRING;
                break;
            case NotificationWatchdogLimitTypes.SNOOZE:
                name = SNOOZE_BRIEF_STRING;
                break;
            case NotificationWatchdogLimitTypes.PULSE:
                name = PULSE_BRIEF_STRING;
                break;
            default:
                name = "UNKNOWN:" + limitType;
                break;
        }
        return name;
    }
}
