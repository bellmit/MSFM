//
// ------------------------------------------------------------------------
// FILE: SeverityFormatter.java
// 
// PACKAGE: com.cboe.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.common.formatters;

import java.awt.*;
import java.util.*;
import java.util.prefs.*;

import com.cboe.idl.alarmConstants.Severities;

import com.cboe.interfaces.instrumentation.common.formatters.SeverityFormatStrategy;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.preferences.InstrumentationBusinessPreferenceHelper;

/**
 * @author torresl@cboe.com
 */
public class SeverityFormatter
        extends Formatter
        implements SeverityFormatStrategy
{
    protected static final short[] SUPPORTED_SEVERITIES = new short[]
    {
        Severities.HIGH, Severities.MEDIUM, Severities.LOW
    };

    protected Map<String, String> formatByStyle;

    private static boolean isColorsInitialized = false;
    private static Color lowColor = Color.YELLOW;
    private static Color mediumColor = Color.ORANGE;
    private static Color highColor = Color.RED;

    public SeverityFormatter()
    {
        formatByStyle = new HashMap<String, String>(10);
        addStyle(FULL_INFO_NAME, FULL_INFO_DESCRIPTION);
        addStyle(BRIEF_INFO_NAME, BRIEF_INFO_DESCRIPTION);
        addStyle(COLOR_INFO_NAME, COLOR_INFO_DESCRIPTION);
        setDefaultStyle(FULL_INFO_NAME);
    }

    private void initializeColors()
    {
        synchronized(SeverityFormatter.class)
        {
            if(!isColorsInitialized)
            {
                try
                {
                    setSeverityColor(Severities.LOW, InstrumentationBusinessPreferenceHelper.getLowNotificationColor());
                }
                catch(InvalidPreferencesFormatException e)
                {
                    GUILoggerHome.find().exception("SeverityFormatter",
                                                   "Could not load Low Color Preference. Using default.", e);
                }
                try
                {
                    setSeverityColor(Severities.MEDIUM, InstrumentationBusinessPreferenceHelper.getMediumNotificationColor());
                }
                catch(InvalidPreferencesFormatException e)
                {
                    GUILoggerHome.find().exception("SeverityFormatter",
                                                   "Could not load Medium Color Preference. Using default.", e);
                }
                try
                {
                    setSeverityColor(Severities.HIGH, InstrumentationBusinessPreferenceHelper.getHighNotificationColor());
                }
                catch(InvalidPreferencesFormatException e)
                {
                    GUILoggerHome.find().exception("SeverityFormatter",
                                                   "Could not load High Color Preference. Using default.", e);
                }
                isColorsInitialized = true;
            }
        }
    }

    public void setSeverityColor(short severity, Color color)
    {
        switch(severity)
        {
            case Severities.HIGH:
                highColor = color;
                break;
            case Severities.MEDIUM:
                mediumColor = color;
                break;
            case Severities.LOW:
                lowColor = color;
                break;
            default:
                GUILoggerHome.find().alarm("SeverityFormatter",
                                           "Attempt to set a color for an unknown severity:" + severity);
        }
    }

    protected String getCachedFormat(short severity, String style)
    {
        synchronized(formatByStyle)
        {
            return formatByStyle.get(style+severity);
        }
    }

    protected void setCachedFormat(short severity, String style, String formatted)
    {
        synchronized(formatByStyle)
        {
            formatByStyle.put(style+severity, formatted);
        }
    }

    public String format(short severity, String style)
    {
        Object cached = getCachedFormat(severity, style);
        String returnValue;
        if(cached != null)
        {
            returnValue = cached.toString();
        }
        else
        {
            if (style.equals(FULL_INFO_NAME))
            {
                returnValue = getSeverityString(severity);
            }
            else if (style.equals(BRIEF_INFO_NAME))
            {
                returnValue = getSeverityString(severity);
                if(returnValue.length()>0)
                {
                    returnValue = returnValue.substring(0, 1);
                }
            }
            else if(style.equals(COLOR_INFO_NAME))
            {
                returnValue = getColorForSeverity(severity).toString();
            }
            else
            {
                returnValue = "Unknown Style";
            }
            setCachedFormat(severity, style, returnValue);
        }
        return returnValue;
    }

    public String format(short severity)
    {
        return format(severity, getDefaultStyle());
    }

    public Color getColorForSeverity(short severity)
    {
        initializeColors();

        Color severityColor;
        switch(severity)
        {
            case Severities.HIGH:
                severityColor = highColor;
                break;
            case Severities.MEDIUM:
                severityColor = mediumColor;
                break;
            case Severities.LOW:
                severityColor = lowColor;
                break;
            default:
                GUILoggerHome.find().alarm("SeverityFormatter",
                                           "Attempt to get a color for an unknown severity:" + severity);
                severityColor = Color.WHITE;
        }
        return severityColor;
    }

    private String getSeverityString(short severity)
    {
        String returnValue;
        switch (severity)
        {
            case Severities.HIGH:
                returnValue = "High";
                break;
            case Severities.MEDIUM:
                returnValue = "Medium";
                break;
            case Severities.LOW:
                returnValue = "Low";
                break;
            default:
                GUILoggerHome.find().alarm("SeverityFormatter",
                                           "Attempt to get a name for an unknown severity:" + severity);
                returnValue = "Unknown";
        }
        return returnValue;
    }

    public short[] getSeverities()
    {
        return SUPPORTED_SEVERITIES;
    }
}
