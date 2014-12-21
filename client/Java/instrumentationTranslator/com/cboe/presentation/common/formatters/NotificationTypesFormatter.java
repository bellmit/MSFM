//
// ------------------------------------------------------------------------
// FILE: NotificationTypesFormatter.java
// 
// PACKAGE: com.cboe.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.formatters;

import java.util.*;

import com.cboe.idl.alarmConstants.NotificationTypes;

import com.cboe.interfaces.instrumentation.common.formatters.NotificationTypesFormatStrategy;

/**
 * @author torresl@cboe.com
 */
public class NotificationTypesFormatter
        extends Formatter
        implements NotificationTypesFormatStrategy
{
    protected static final short[] SUPPORTED_NOTIFICATION_TYPES = new short[]
    {
        NotificationTypes.GLOBAL, NotificationTypes.LOCAL, NotificationTypes.EMAIL
    };
    protected Map<String, String> formatByStyle;

    public NotificationTypesFormatter()
    {
        formatByStyle = new HashMap<String, String>(10);
        addStyle(FULL_INFO_NAME, FULL_INFO_DESCRIPTION);
        addStyle(BRIEF_INFO_NAME, BRIEF_INFO_DESCRIPTION);

        setDefaultStyle(FULL_INFO_NAME);
    }

    protected String getCachedFormat(short notificationtype, String style)
    {
        synchronized (formatByStyle)
        {
            return formatByStyle.get(style + notificationtype);
        }
    }

    protected void setCachedFormat(short notificationtype, String style, String formatted)
    {
        synchronized (formatByStyle)
        {
            formatByStyle.put(style + notificationtype, formatted);
        }
    }
    public String format(short notificationtype, String style)
    {
        Object cached = getCachedFormat(notificationtype, style);
        String returnValue = "";
        if (cached != null)
        {
            returnValue = cached.toString();
        }
        else
        {
            if (style.equals(FULL_INFO_NAME))
            {
                returnValue = getNotificationTypeString(notificationtype);
            }
            else if (style.equals(BRIEF_INFO_NAME))
            {
                returnValue = getNotificationTypeString(notificationtype);
                if (returnValue.length() > 0)
                {
                    returnValue = returnValue.substring(0, 1);
                }
            }
            setCachedFormat(notificationtype, style, returnValue);
        }
        return returnValue;
    }

    public String format(short notificationtype)
    {
        return format(notificationtype, getDefaultStyle());
    }

    private String getNotificationTypeString(short notificationtype)
    {
        String returnValue = "";
        switch (notificationtype)
        {
            case NotificationTypes.GLOBAL:
                returnValue = "Global";
                break;
            case NotificationTypes.LOCAL:
                returnValue = "Local";
                break;
            case NotificationTypes.EMAIL:
                returnValue = "Email";
                break;
            default:
                returnValue = "UNKNOWN:" + notificationtype;
                break;
        }
        return returnValue;
    }

    public short[] getNotificationTypes()
    {
        return SUPPORTED_NOTIFICATION_TYPES;
    }
}
