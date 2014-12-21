// -----------------------------------------------------------------------------------
// Source file: GUILoggerBusinessProperties.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.GUILoggerMsgTypes;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperties;

/**
 *  This class provides a proxy with different method signatures for convenience
 *  to the Logging Service. You must provide your own implementation for
 *  initializing.
 *
 *  @author     Alex Brazhnichenko
 *  @created    July 16, 2001
 */
public class GUILoggerBusinessProperties extends GUILoggerProperties implements IGUILoggerBusinessProperties
{
    private boolean defaultValue;
    /**
     *  Constructor
     */
    public GUILoggerBusinessProperties()
    {
        super();
        defaultValue = false;
    }

    protected int getMinIndex()
    {
        return GUILoggerBusinessProperty.getMinIndex();
    }

    protected int getMaxIndex()
    {
        return GUILoggerBusinessProperty.getMaxIndex();
    }

    public IGUILoggerProperty getProperty(int index)
    {
        return GUILoggerBusinessProperty.getProperty(index);
    }

    public IGUILoggerProperty[] getProperties()
    {
        return GUILoggerBusinessProperty.getProperties();
    }

    public boolean getDefaultValue()
    {
        return defaultValue;
    }

    public boolean getPropertyValue(IGUILoggerBusinessProperty property)
    {
        return super.getPropertyValue(property);
    }
    public boolean isPropertyOn(IGUILoggerBusinessProperty property)
    {
        return super.isPropertyOn(property);
    }
    public void setProperty(IGUILoggerBusinessProperty property, boolean value)
    {
        super.setProperty(property, value);
    }
    public boolean isValidProperty(IGUILoggerBusinessProperty property)
    {
        return super.isValidProperty(property);
    }

}