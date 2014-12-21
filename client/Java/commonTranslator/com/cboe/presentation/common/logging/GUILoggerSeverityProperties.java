// -----------------------------------------------------------------------------------
// Source file: GUILoggerSeverityProperties.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.common.logging.GUILoggerMsgTypes;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerSeverityProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerSeverityProperties;

import com.cboe.presentation.common.logging.GUILoggerProperties;

/**
 *  This class provides a proxy with different method signatures for convenience
 *  to the Logging Service. You must provide your own implementation for
 *  initializing.
 *
 *@author     Alex Brazhnichenko
 *@created    July 10, 2001
 */
public class GUILoggerSeverityProperties extends GUILoggerProperties implements IGUILoggerSeverityProperties
{
    private boolean defaultValue;

    /**
     *  Constructor
     */
    public GUILoggerSeverityProperties()
    {
        super();
        defaultValue = false;
    }

    protected int getMinIndex()
    {
        return GUILoggerMsgTypes.LOG_SEVERITY_MIN;
    }
    protected int getMaxIndex()
    {
        return GUILoggerMsgTypes.LOG_SEVERITY_MAX;
    }
    public IGUILoggerProperty getProperty(int index)
    {
        return GUILoggerSeverityProperty.getProperty(index);
    }

    public IGUILoggerProperty[] getProperties()
    {
        return GUILoggerSeverityProperty.getProperties();
    }

    /**
     *  Returns whether alarm messages are delivered or not.
     *
     *  @return    True is alarm messages are delivered, false otherwise.
     */
    public final boolean isAlarmOn()
    {
        return this.getPropertyValue(GUILoggerSeverityProperty.ALARM);
    }

    /**
     *  Returns whether audit messages are delivered or not.
     *
     *  @return    True is audit messages are delivered, false otherwise.
     */
    public final boolean isAuditOn()
    {
        return this.getPropertyValue(GUILoggerSeverityProperty.AUDIT);
    }

    /**
     *  Returns whether debug messages are delivered or not.
     *
     *  @return    True is debug messages are delivered, false otherwise.
     */
    public final boolean isDebugOn()
    {
        return this.getPropertyValue(GUILoggerSeverityProperty.DEBUG);
    }

    /**
     *  Returns whether exception messages are delivered or not.
     *
     *  @return    True is exception messages are delivered, false otherwise.
     */
    public final boolean isExceptionOn()
    {
        return this.getPropertyValue(GUILoggerSeverityProperty.EXCEPTION);
    }

    /**
     *  Returns whether information messages are delivered or not.
     *
     *  @return    True is information messages are delivered, false otherwise.
     */
    public final boolean isInformationOn()
    {
        return this.getPropertyValue(GUILoggerSeverityProperty.INFORMATION);
    }

    /**
     *  Turns the alarm messages delivery on or off.
     *
     *  @param  flag  True to delivery alarm, false for them to be suppressed.
     */
    public void setAlarmOn(boolean flag)
    {
        this.setProperty(GUILoggerSeverityProperty.ALARM, flag);
    }

    /**
     *  Turns the audit messages delivery on or off.
     *
     *  @param  flag  True to delivery audit, false for them to be suppressed.
     */
    public void setAuditOn(boolean flag)
    {
        this.setProperty(GUILoggerSeverityProperty.AUDIT, flag);
    }

    /**
     *  Turns the debug messages delivery on or off.
     *
     *  @param  flag  True to delivery debug, false for them to be suppressed.
     */
    public void setDebugOn(boolean flag)
    {
        this.setProperty(GUILoggerSeverityProperty.DEBUG, flag);
    }

    /**
     *  Turns the exception messages delivery on or off.
     *
     *  @param  flag  True to delivery exception, false for them to be suppressed.
     */
    public void setExceptionOn(boolean flag)
    {
        this.setProperty(GUILoggerSeverityProperty.EXCEPTION, flag);
    }

    /**
     *  Turns the information messages delivery on or off.
     *
     * @param  flag  True to delivery information, false for them to be suppressed.
     */
    public void setInformationOn(boolean flag)
    {
        this.setProperty(GUILoggerSeverityProperty.INFORMATION, flag);
    }

    public boolean getDefaultValue()
    {
        return defaultValue;
    }

    public boolean getPropertyValue(IGUILoggerSeverityProperty property)
    {
        return super.getPropertyValue(property);
    }
    public boolean isPropertyOn(IGUILoggerSeverityProperty property)
    {
        return super.isPropertyOn(property);
    }
    public void setProperty(IGUILoggerSeverityProperty property, boolean value)
    {
        super.setProperty(property, value);
    }
    public boolean isValidProperty(IGUILoggerSeverityProperty property)
    {
        return super.isValidProperty(property);
    }

}