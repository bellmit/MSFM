// -----------------------------------------------------------------------------------
// Source file: GUILoggerINBusinessProperty.java
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
public class GUILoggerINBusinessProperty
        extends GUILoggerBusinessProperty
{
    public static final GUILoggerINBusinessProperty INSTRUMENTATION =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_INSTRUMENTATION, "Instrumentation");
    public static final GUILoggerINBusinessProperty CONTEXT_DETAIL =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_CONTEXT_DETAIL, "Context Detail");
    public static final GUILoggerINBusinessProperty CAS_SUMMARY =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_CAS_SUMMARY, "CAS Summary");
    public static final GUILoggerINBusinessProperty CAS_CONFIGURATION =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_CAS_CONFIGURATION,
                                            "CAS Configuration");
    public static final GUILoggerINBusinessProperty QUEUE_INSTRUMENTOR =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_QUEUE_INSTRUMENTOR,
                                            "Queue Instrumentor");
    public static final GUILoggerINBusinessProperty THREAD_INSTRUMENTOR =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_THREAD_INSTRUMENTOR,
                                            "Thread Instrumentor");
    public static final GUILoggerINBusinessProperty METHOD_INSTRUMENTOR =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_METHOD_INSTRUMENTOR,
                                            "Method Instrumentor");
    public static final GUILoggerINBusinessProperty COUNT_INSTRUMENTOR =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_COUNT_INSTRUMENTOR,
                                            "Count Instrumentor");
    public static final GUILoggerINBusinessProperty EVENT_INSTRUMENTOR =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_EVENT_INSTRUMENTOR,
                                            "Event Instrumentor");
    public static final GUILoggerINBusinessProperty HEAP_INSTRUMENTOR =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_HEAP_INSTRUMENTOR,
                                            "Heap Instrumentor");
    public static final GUILoggerINBusinessProperty NETWORK_INSTRUMENTOR =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_NETWORK_INSTRUMENTOR,
                                            "Network Instrumentor");
    public static final GUILoggerINBusinessProperty JMX_INSTRUMENTOR =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_JMX_INSTRUMENTOR,
                                            "JMX Instrumentor");
    public static final GUILoggerINBusinessProperty JSTAT_INSTRUMENTOR =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_JSTAT_INSTRUMENTOR,
                                            "JSAT Instrumentor");
    public static final GUILoggerINBusinessProperty PROCESSES =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_PROCESSES, "Processes");
    public static final GUILoggerINBusinessProperty ALARM_CONDITION =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_ALARM_CONDITION,
                                            "Alarm Condition");
    public static final GUILoggerINBusinessProperty ALARM_CALCULATION =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_ALARM_CALCULATION,
                                            "Alarm Calculation");
    public static final GUILoggerINBusinessProperty ALARM_DEFINITION =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_ALARM_DEFINITION,
                                            "Alarm Definition");
    public static final GUILoggerINBusinessProperty ALARM_ACTIVATION =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_ALARM_ACTIVATION,
                                            "Alarm Activation");
    public static final GUILoggerINBusinessProperty ALARM_WATCHDOG =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_ALARM_WATCHDOG,
                                            "Alarm Watchdog");
    public static final GUILoggerINBusinessProperty ALARM_NOTIFICATION =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_ALARM_NOTIFICATION,
                                            "Alarm Notification");
    public static final GUILoggerINBusinessProperty ALARM_EXCEPTIONS =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_ALARM_EXCEPTIONS,
                                            "Alarm Exceptions");
    public static final GUILoggerINBusinessProperty XTP =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_XTP, "XTP");
    public static final GUILoggerINBusinessProperty ORB_NAME_ALIAS =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_ORB_NAME_ALIAS, "Orb Name Alias");
    public static final GUILoggerINBusinessProperty ALARM_ASSIGNMENT =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_ALARM_ASSIGNMENT,
                                            "Alarm Assignment");
    public static final GUILoggerINBusinessProperty LOGICAL_ORB_NAME =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_LOGICAL_ORB_NAME, "Logical Orb Name");

    public static final GUILoggerINBusinessProperty GROUPS =
            new GUILoggerINBusinessProperty(GUILoggerMsgTypes.LOG_GROUPS, "Groups");

    private static final IGUILoggerBusinessProperty[] businessProperties =
            {COMMON,
             INSTRUMENTATION,
             CONTEXT_DETAIL,
             CAS_SUMMARY,
             CAS_CONFIGURATION,
             QUEUE_INSTRUMENTOR,
             THREAD_INSTRUMENTOR,
             METHOD_INSTRUMENTOR,
             COUNT_INSTRUMENTOR,
             EVENT_INSTRUMENTOR,
             HEAP_INSTRUMENTOR,
             NETWORK_INSTRUMENTOR,
             JMX_INSTRUMENTOR,
             JSTAT_INSTRUMENTOR,
             PROCESSES,
             PRODUCT_QUERY,
             PRODUCT_SELECTOR,
             ALARM_CONDITION,
             ALARM_CALCULATION,
             ALARM_DEFINITION,
             ALARM_ACTIVATION,
             ALARM_WATCHDOG,
             ALARM_NOTIFICATION,
             ALARM_EXCEPTIONS,
             WINDOW_MANAGEMENT,
             XTP,
             ORB_NAME_ALIAS,
             DATABASE_QUERY_BUILDER,
             PERMISSION_MATRIX,
             REPORT_GENERATION,
             ALARM_ASSIGNMENT,
             LOGICAL_ORB_NAME,
             GROUPS
            };

    protected GUILoggerINBusinessProperty(int key, String name)
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
        return GUILoggerMsgTypes.LOG_INSTRUMENTATION_MAX;
    }
}
