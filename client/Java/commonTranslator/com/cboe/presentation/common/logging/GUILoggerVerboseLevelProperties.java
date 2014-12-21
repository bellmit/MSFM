// -----------------------------------------------------------------------------------
// Source file: GUILoggerVerboseLevelProperties.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import java.util.Properties;
import java.util.BitSet;
import java.lang.Boolean;

import com.cboe.interfaces.presentation.common.logging.*;

/**
 *  This class provides a base implementation of IGUILoggerVerboseLevelProperties interface.
 *  You must provide your own implementation for initializing.
 *
 *  @author     Alex Brazhnichenko
 *  @created    July 29, 2003
 */
public class GUILoggerVerboseLevelProperties implements IGUILoggerVerboseLevelProperties
{
    private IGUILoggerVerboseLevel[] properties;
    private IGUILoggerVerboseLevel defaultLevel;

    /**
     *  Constructor
     */
    public GUILoggerVerboseLevelProperties()
    {
        super();
        defaultLevel = GUILoggerVerboseLevel.HIGH;
    }

    public IGUILoggerVerboseLevelProperty[] getProperties()
    {
        return GUILoggerVerboseLevelProperty.getProperties();
    }

    public IGUILoggerVerboseLevel getPropertyValue(IGUILoggerVerboseLevelProperty property)
    {
        int index = property.getKey();
        return getPropertyValue(index);
    }

    public IGUILoggerVerboseLevel getDefaultValue()
    {
        return defaultLevel;
    }

    public boolean isValidProperty(IGUILoggerVerboseLevelProperty property)
    {
        return isValidIndex(property.getKey());
    }

    public void setProperty(IGUILoggerProperty property, IGUILoggerVerboseLevel value)
    {
        setProperty(property.getKey(), value);
    }

    protected void setProperty(int index, IGUILoggerVerboseLevel value)
    {
        validateIndex(index);
        getPropertiesSet()[getNormalizedIndex(index)] = value;
    }

    protected IGUILoggerVerboseLevel[] getPropertiesSet()
    {
        if ( properties == null )
        {
            properties = new IGUILoggerVerboseLevel[getPropertiesSize()];
        }
        return properties;
    }

    protected IGUILoggerVerboseLevel getPropertyValue(int index)
    {
        validateIndex(index);
        return getPropertiesSet()[getNormalizedIndex(index)];
    }

    public IGUILoggerVerboseLevelProperty getProperty(int index)
    {
        return GUILoggerVerboseLevelProperty.getProperty(index);
    }

    private int getNormalizedIndex(int index)
    {
        return index - getMinIndex();
    }

    protected int getMinIndex()
    {
        return GUILoggerMsgTypes.LOG_VERBOSE_LEVEL_MIN;
    }

    protected int getMaxIndex()
    {
        return GUILoggerMsgTypes.LOG_VERBOSE_LEVEL_MAX;
    }

    protected int getPropertiesSize()
    {
        return getMaxIndex() - getMinIndex() + 1;
    }

    private boolean isValidIndex(int index)
    {
        boolean isValid = false;

        if ( index <= getMaxIndex() && index >= getMinIndex() )
        {
            isValid = true;
        }
        return isValid;
    }

    private void validateIndex(int index)
    {
        if ( ! isValidIndex(index) )
        {
            throw new IllegalArgumentException("Invalid property index.\nPassed in index(" +
                                                index + ") is not in the valid range[min=" +
                                                getMinIndex() + ", max=" + getMaxIndex() +"]." );
        }
    }

}