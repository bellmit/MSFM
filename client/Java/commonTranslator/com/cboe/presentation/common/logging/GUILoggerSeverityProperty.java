// -----------------------------------------------------------------------------------
// Source file: GUILoggerSeverityProperty.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerSeverityProperty;
import com.cboe.interfaces.presentation.common.logging.GUILoggerMsgTypes;
import com.cboe.presentation.common.logging.GUILoggerProperty;

/**
 *  This class provides getters and setteers for GUILogger property attributes.
 *
 *  @author     Alex Brazhnichenko
 *  @created    July 17, 2001
 */
public class GUILoggerSeverityProperty extends GUILoggerProperty implements IGUILoggerSeverityProperty
{
    public static final GUILoggerSeverityProperty ALARM = new GUILoggerSeverityProperty( GUILoggerMsgTypes.LOG_ALARM, "Alarm");
    public static final GUILoggerSeverityProperty AUDIT = new GUILoggerSeverityProperty( GUILoggerMsgTypes.LOG_AUDIT, "Audit");
    public static final GUILoggerSeverityProperty DEBUG = new GUILoggerSeverityProperty( GUILoggerMsgTypes.LOG_DEBUG, "Debug");
    public static final GUILoggerSeverityProperty EXCEPTION = new GUILoggerSeverityProperty( GUILoggerMsgTypes.LOG_EXCEPTION, "Exception");
    public static final GUILoggerSeverityProperty INFORMATION = new GUILoggerSeverityProperty(GUILoggerMsgTypes.LOG_INFORMATION, "Information");

    private static final GUILoggerSeverityProperty[] severityProperties = { ALARM,
                                                                            AUDIT,
                                                                            DEBUG,
                                                                            EXCEPTION,
                                                                            INFORMATION };

    protected GUILoggerSeverityProperty(int key, String name)
    {
        super(key, name);
    }

    public static IGUILoggerProperty getProperty(int index)
    {
        return severityProperties[index];
    }

    public static IGUILoggerProperty[] getProperties()
    {
        return severityProperties;
    }
    public static int getMinIndex()
    {
        return GUILoggerMsgTypes.LOG_SEVERITY_MIN;
    }

    public static int getMaxIndex()
    {
        return GUILoggerMsgTypes.LOG_SEVERITY_MAX;
    }
}
