//
// -----------------------------------------------------------------------------------
// Source file: IGUILoggerVerboseLevelProperties.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.logging;

/**
 * This interface defines interface for a GUILogger prperties manager.
 */
public interface IGUILoggerVerboseLevelProperties
{
    public IGUILoggerVerboseLevel getPropertyValue(IGUILoggerVerboseLevelProperty property);

    public IGUILoggerVerboseLevelProperty getProperty(int index);

//    public boolean isPropertyOn(IGUILoggerVerboseLevelProperty property);
    
    public void setProperty(IGUILoggerProperty property, IGUILoggerVerboseLevel value);
    
    public boolean isValidProperty(IGUILoggerVerboseLevelProperty property);
    
    public IGUILoggerVerboseLevel getDefaultValue();

    public IGUILoggerVerboseLevelProperty[] getProperties();
}