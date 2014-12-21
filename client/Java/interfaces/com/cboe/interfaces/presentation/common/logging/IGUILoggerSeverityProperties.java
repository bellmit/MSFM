//
// -----------------------------------------------------------------------------------
// Source file: IGUILoggerSeverityProperties.java
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
public interface IGUILoggerSeverityProperties
{
    public boolean getPropertyValue(IGUILoggerSeverityProperty property);

//    public IGUILoggerSeverityProperty getProperty(int index);

    public boolean isPropertyOn(IGUILoggerSeverityProperty property);
    
    public void setProperty(IGUILoggerSeverityProperty property, boolean value);
    
    public boolean isValidProperty(IGUILoggerSeverityProperty property);
    
    public IGUILoggerProperty[] getProperties();
}