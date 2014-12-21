// -----------------------------------------------------------------------------------
// Source file: GUILoggerPropertyFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.GUILoggerPropertyFormatStrategy;
import com.cboe.interfaces.presentation.common.logging.GUILoggerMsgTypes;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerVerboseLevelProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerVerboseLevel;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.formatters.Formatter;

/**
 * Implements the GUILoggerPropertyFormatStrategy
 */
public class GUILoggerPropertyFormatter extends Formatter implements GUILoggerPropertyFormatStrategy
{
    /**
     * Constructor, defines styles and sets initial default style
     */
    public GUILoggerPropertyFormatter()
    {
        super();

        addStyle(BRIEF_NAME, BRIEF_NAME_DESCRIPTION);
        addStyle(BRIEF_NAME_AND_VALUE, BRIEF_NAME_AND_VALUE_DESCRIPTION);
        addStyle(FULL_INFORMATION, FULL_INFORMATION_DESCRIPTION);

        setDefaultStyle(BRIEF_NAME);
    }

    /**
     * Formats a IGUILoggerProperty
     * @param property to format
     */
    public String format(IGUILoggerProperty property)
    {
        return format(property, getDefaultStyle());
    }
    /**
     * Formats a IGUILoggerProperty
     * @param property to format
     * @param styleName to use for formatting
     */
    public String format(IGUILoggerProperty property, String styleName)
    {
        validateStyle(styleName);
        StringBuffer propertyText = new StringBuffer();

        if(styleName.equals(BRIEF_NAME))
        {
            propertyText.append(property.getName());
        }
        else if(styleName.equals(BRIEF_NAME_AND_VALUE))
        {
            propertyText.append(property.getName());
            propertyText.append(" = ");
            propertyText.append(getPropertyValue(property));
        }
        else if(styleName.equals(FULL_INFORMATION))
        {
            propertyText.append(getPropertyType(property));
            propertyText.append(' ');
            propertyText.append(property.getName());
            propertyText.append(" = ");
            propertyText.append(getPropertyValue(property));
        }

        return propertyText.toString();
    }

    private boolean getPropertyValue(IGUILoggerProperty property)
    {
        return GUILoggerHome.find().isPropertyOn(property);
    }

    private IGUILoggerVerboseLevel getPropertyValue(IGUILoggerVerboseLevelProperty property)
    {
        return GUILoggerHome.find().getPropertyValue(property);
    }

    private String getPropertyType(IGUILoggerProperty property)
    {
        int propertyKey = property.getKey();
        String type = null;

        if ( propertyKey >= GUILoggerMsgTypes.LOG_SEVERITY_MIN && propertyKey <= GUILoggerMsgTypes.LOG_SEVERITY_MAX )
        {
            type = "Severity Property";
        }
        else if ( propertyKey >= GUILoggerMsgTypes.LOG_TRADER_BUSINESS_MIN && propertyKey <= GUILoggerMsgTypes.LOG_TRADER_BUSINESS_MAX )
        {
            type = "Business Property";
        }
        else if ( propertyKey >= GUILoggerMsgTypes.LOG_SA_BUSINESS_MIN && propertyKey <= GUILoggerMsgTypes.LOG_SA_BUSINESS_MAX )
        {
            type = "SysAdmin Business Property";
        }
        else if (propertyKey >= GUILoggerMsgTypes.LOG_INSTRUMENTATION_MIN && propertyKey <= GUILoggerMsgTypes.LOG_INSTRUMENTATION_MAX)
        {
            type = "InstMonitor Business Property";
        }
        else if (propertyKey >= GUILoggerMsgTypes.LOG_MESSAGEMON_MIN && propertyKey <= GUILoggerMsgTypes.LOG_MESSAGEMON_MAX)
        {
            type = "MsgMonitor Business Property";
        }
        else if (propertyKey >= GUILoggerMsgTypes.LOG_VERBOSE_LEVEL_MIN && propertyKey <= GUILoggerMsgTypes.LOG_VERBOSE_LEVEL_MAX)
        {
            type = "Verbose Level Property";
        }
        else
        {
            type = "Unknown Property";
        }

        return type;
    }
}
