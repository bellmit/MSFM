package com.cboe.interfaces.domain;

public interface SystemMonitorCommandMethodNames
{
    public final static String CONTEXT_DETAIL_QUERY = "getContextDetail";
    public final static String PRODUCT_DATA_QUERY = "getProductData";
    public final static String CONFIGURATION_DATA_QUERY = "getConfigurationData";
    public static final String SUBSCRIBE_DETAILS = "subscribeDetails";
    public static final String UNSUBSCRIBE_DETAILS = "unsubscribeDetails";
    public static final String SESSION_CLEANUP = "sessionCleanup";
    public static final String REPUBLISH_DETAILS = "republishDetails";
    public static final String REPUBLISH_SUMMARY = "republishSummary";
    public static final String REPUBLISH = "republish";

    public static final String ADMIN_SHOW_PUBLISHERS = "adminShowPublishers";
    public static final String STOP_ALARM_NOTIFICATION_PUBLISH = "stopAlarmNotificationPublish";
    public static final String START_ALARM_NOTIFICATION_PUBLISH = "startAlarmNotificationPublish";
    
    public static final String EXECUTE_HSQL_QUERY = "executeHSQLQuery";
    public static final String EXECUTE_HSQL_UPDATE = "executeHSQLUpdate";
    public static final String SET_ACTIVATION_STATUS = "setActivationStatus";
    public static final String EVALUATE_PROCESS_WATCHER_STATE  = "evaluateProcessWatcherState";
    public static final String ADMIN_SHOW_ALARM_NOTIFICATION_PUBLISH_STATE = "adminShowNotificationPublishState";

    public static final String ADMIN_SET_DEBUG_LOG_LEVEL = "adminSetDebugLogLevel";
    
    public static final String ENABLE_CNTRL_LOG_CONSUMPTION = "enableLoggingConsumption";
    public static final String DISABLE_CNTRL_LOG_CONSUMPTION = "disableLoggingConsumption";

    public static final String ICS_MANAGER_STATE_QUERY = "getIcsManagerState";
    public static final String ACTIVATION_QUERY = "getActivations";
    public static final String RESEND_ACTIVATION_ASSIGNMENTS = "resendActivationAssignments";
    public static final String ORB_NAME_ALIAS_QUERY = "dumpOrbNameAliasCache";
    public static final String ACTIVE_CONDITION_KEYS_QUERY = "dumpActiveConditionKeys";
    public static final String APPLY_METHOD_FILTER = "applyMethodFilter";
    public static final String GET_METHOD_FILTER_LIST = "getMethodFilterList";

    public static final String ICS_LOG_ALARM = "logAlarm";

    public static final String ICS_TIME_QUERY = "getIcsTime";

}
