// -----------------------------------------------------------------------------------
// Source file: GUILoggerINBusinessProperties.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

//import com.cboe.interfaces.presentation.common.logging.GUILoggerMsgTypes;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperties;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperties;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;

/**
 *  This class provides a proxy with different method signatures for convenience
 *  to the Logging Service. You must provide your own implementation for
 *  initializing.
 *
 *  @author     Alex Brazhnichenko
 *  @created    July 16, 2001
 */
public class GUILoggerMMBusinessProperties extends GUILoggerBusinessProperties implements IGUILoggerBusinessProperties
{
    /**
     *  Constructor
     */
    public GUILoggerMMBusinessProperties()
    {
        super();
    }

    protected int getMinIndex()
    {
        return GUILoggerMMBusinessProperty.getMinIndex();
    }

    protected int getMaxIndex()
    {
        return GUILoggerMMBusinessProperty.getMaxIndex();
    }

    public IGUILoggerProperty getProperty(int index)
    {
        return GUILoggerMMBusinessProperty.getProperty(index);
    }

    public IGUILoggerProperty[] getProperties()
    {
        return GUILoggerMMBusinessProperty.getProperties();
    }
}