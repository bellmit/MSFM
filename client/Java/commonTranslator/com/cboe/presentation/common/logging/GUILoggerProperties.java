// -----------------------------------------------------------------------------------
// Source file: GUILoggerBusinessProperties.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import java.util.Properties;
import java.util.BitSet;
import java.lang.Boolean;

import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperties;

/**
 *  This class provides a base implementation of IGUILoggerProperties interface.
 *  You must provide your own implementation for initializing.
 *
 *  @author     Alex Brazhnichenko
 *  @created    July 16, 2001
 */
public abstract class GUILoggerProperties implements IGUILoggerProperties
{
    private BitSet propertiesBitSet = null;

    /**
     *  Constructor
     */
    public GUILoggerProperties()
    {
        super();
    }

    protected BitSet getPropertiesBitSet()
    {
        if ( propertiesBitSet == null )
        {
            propertiesBitSet = new BitSet(getPropertiesSize());
        }
        return propertiesBitSet;
    }

    public boolean getPropertyValue(IGUILoggerProperty property)
    {
        int index = property.getKey();
        return getPropertyValue(index);
    }

    protected boolean getPropertyValue(int index)
    {
        validateIndex(index);
        return getPropertiesBitSet().get(getNormalizedIndex(index));
    }

    public boolean isPropertyOn(IGUILoggerProperty property)
    {
        return getPropertyValue(property.getKey());
    }

    public boolean isValidProperty(IGUILoggerProperty property)
    {
        return isValidIndex(property.getKey());
    }

    public void setProperty(IGUILoggerProperty property, boolean value)
    {
        setProperty(property.getKey(), value);
    }

    protected void setProperty(int index, boolean value)
    {
        validateIndex(index);
        if ( value )
        {
            getPropertiesBitSet().set(getNormalizedIndex(index));
        }
        else
        {
            getPropertiesBitSet().clear(getNormalizedIndex(index));
        }
    }

    private int getNormalizedIndex(int index)
    {
        return index - getMinIndex();
    }

    abstract protected int getMinIndex();
    abstract protected int getMaxIndex();
    abstract public IGUILoggerProperty getProperty(int index);

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