//
// -----------------------------------------------------------------------------------
// Source file: IGUILoggerProperties.java
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
public interface IGUILoggerProperties
{
    public boolean getPropertyValue(IGUILoggerProperty property);

    public IGUILoggerProperty getProperty(int index);

    public boolean isPropertyOn(IGUILoggerProperty property);

    public void setProperty(IGUILoggerProperty property, boolean value);

    public boolean isValidProperty(IGUILoggerProperty property);

    public boolean getDefaultValue();

    public IGUILoggerProperty[] getProperties();
}