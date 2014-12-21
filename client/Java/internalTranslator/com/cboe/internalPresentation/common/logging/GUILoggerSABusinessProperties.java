// -----------------------------------------------------------------------------------
// Source file: GUILoggerSABusinessProperties.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.logging;

//import com.cboe.interfaces.presentation.common.logging.GUILoggerMsgTypes;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperties;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperties;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

/**
 *  This class provides a proxy with different method signatures for convenience
 *  to the Logging Service. You must provide your own implementation for
 *  initializing.
 *
 *  @author     Alex Brazhnichenko
 *  @created    July 16, 2001
 */
public class GUILoggerSABusinessProperties extends GUILoggerBusinessProperties implements IGUILoggerBusinessProperties
{
    /**
     *  Constructor
     */
    public GUILoggerSABusinessProperties()
    {
        super();
    }

    protected int getMinIndex()
    {
        return GUILoggerSABusinessProperty.getMinIndex();
    }

    protected int getMaxIndex()
    {
        return GUILoggerSABusinessProperty.getMaxIndex();
    }

    public IGUILoggerProperty getProperty(int index)
    {
        return GUILoggerSABusinessProperty.getProperty(index);
    }

    public IGUILoggerProperty[] getProperties()
    {
        return GUILoggerSABusinessProperty.getProperties();
    }

//    public boolean getPropertyValue(IGUILoggerBusinessProperty property)
//    {
//        return super.getPropertyValue(property);
//    }
//    public boolean isPropertyOn(IGUILoggerBusinessProperty property)
//    {
//        return super.isPropertyOn(property);
//    }
//    public void setProperty(IGUILoggerBusinessProperty property, boolean value)
//    {
//        super.setProperty(property, value);
//    }
//    public boolean isValidProperty(IGUILoggerBusinessProperty property)
//    {
//        return super.isValidProperty(property);
//    }
//
}