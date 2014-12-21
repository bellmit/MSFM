// -----------------------------------------------------------------------------------
// Source file: GUILoggerVerboseLevelProperty.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.IGUILoggerVerboseLevelProperty;
import com.cboe.interfaces.presentation.common.logging.GUILoggerMsgTypes;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;

public class GUILoggerVerboseLevelProperty extends GUILoggerProperty implements IGUILoggerVerboseLevelProperty
{
    public static final IGUILoggerVerboseLevelProperty EXCEPTION_VERBOSE_LEVEL = new GUILoggerVerboseLevelProperty(GUILoggerMsgTypes.LOG_EXCEPTION_VERBOSE_LEVEL, "Exception Verbose Level");
    public static final IGUILoggerVerboseLevelProperty DEBUG_VERBOSE_LEVEL = new GUILoggerVerboseLevelProperty(GUILoggerMsgTypes.LOG_DEBUG_VERBOSE_LEVEL, "Debug Verbose Level");

    private static final IGUILoggerVerboseLevelProperty[] verboseLevelProperties = {EXCEPTION_VERBOSE_LEVEL,
                                                                                    DEBUG_VERBOSE_LEVEL};

    public GUILoggerVerboseLevelProperty(int key, String name)
    {
        super(key, name);
    }

    public static IGUILoggerVerboseLevelProperty getProperty(int index)
    {
        return verboseLevelProperties[index];
    }

    public static IGUILoggerVerboseLevelProperty[] getProperties()
    {
        return verboseLevelProperties;
    }

    public static int getMinIndex()
    {
        return GUILoggerMsgTypes.LOG_VERBOSE_LEVEL_MIN;
    }

    public static int getMaxIndex()
    {
        return GUILoggerMsgTypes.LOG_VERBOSE_LEVEL_MAX;
    }


}
