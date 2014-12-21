// -----------------------------------------------------------------------------------
// Source file: GUILoggerMMBusinessProperty.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.GUILoggerMsgTypes;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;

/**
 *  This class provides getters and setters for GUILogger property attributes for instrumentor GUI.
 */
@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
public class GUILoggerMMBusinessProperty
        extends GUILoggerBusinessProperty
{
    //MM only properties
    public static final GUILoggerMMBusinessProperty MESSAGEMON =
            new GUILoggerMMBusinessProperty(GUILoggerMsgTypes.LOG_MESSAGEMON, "Message Monitor");
    public static final GUILoggerMMBusinessProperty SUBJECTS =
            new GUILoggerMMBusinessProperty(GUILoggerMsgTypes.LOG_SUBJECTS, "Subjects");
    public static final GUILoggerMMBusinessProperty CHANNELS =
            new GUILoggerMMBusinessProperty(GUILoggerMsgTypes.LOG_CHANNELS, "Channels");
    public static final GUILoggerMMBusinessProperty RECORDING =
            new GUILoggerMMBusinessProperty(GUILoggerMsgTypes.LOG_RECORDING, "Message Recording");
    public static final GUILoggerMMBusinessProperty PROCESS_WATCHER =
            new GUILoggerMMBusinessProperty(GUILoggerMsgTypes.LOG_PROCESS_WATCHER,
                                            "Process Watcher");
    public static final GUILoggerMMBusinessProperty EXTENT_MAP =
            new GUILoggerMMBusinessProperty(GUILoggerMsgTypes.LOG_EXTENT_MAP, "Extent Map");
    public static final GUILoggerMMBusinessProperty MONITOR =
            new GUILoggerMMBusinessProperty(GUILoggerMsgTypes.LOG_MONITOR, "Infra Monitor");
    public static final GUILoggerMMBusinessProperty SNIFFER =
            new GUILoggerMMBusinessProperty(GUILoggerMsgTypes.LOG_SNIFFER, "Sniffer");


    private static final IGUILoggerBusinessProperty[] businessProperties =
            {COMMON,
             USER_PREFERENCES,
             WINDOW_MANAGEMENT,
             MESSAGEMON,
             SUBJECTS,
             CHANNELS,
             RECORDING,
             PROCESS_WATCHER,
             EXTENT_MAP,
             MONITOR,
             SNIFFER,
             PERMISSION_MATRIX
            };

    protected GUILoggerMMBusinessProperty(int key, String name)
    {
        super(key, name);
    }

    public static IGUILoggerProperty getProperty(int index)
    {
        return businessProperties[index];
    }

    public static IGUILoggerProperty[] getProperties()
    {
        //noinspection ReturnOfCollectionOrArrayField
        return businessProperties;
    }

    public static int getMinIndex()
    {
        return GUILoggerMsgTypes.LOG_BUSINESS_MIN;
    }

    public static int getMaxIndex()
    {
        return GUILoggerMsgTypes.LOG_MESSAGEMON_MAX;
    }
}
