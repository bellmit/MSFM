//
// -----------------------------------------------------------------------------------
// Source file: IGUILoggerBusinessProperties.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.logging;

/**
 * This interface defines interface for a GUILogger prperties manager.
 */
public interface IGUILoggerBusinessProperties
{
    public boolean getPropertyValue(IGUILoggerBusinessProperty property);

//    public IGUILoggerBusinessProperty getProperty(int index);

    public boolean isPropertyOn(IGUILoggerBusinessProperty property);
    
    public void setProperty(IGUILoggerBusinessProperty property, boolean value);
    
    public boolean isValidProperty(IGUILoggerBusinessProperty property);

    public IGUILoggerProperty[] getProperties();
}