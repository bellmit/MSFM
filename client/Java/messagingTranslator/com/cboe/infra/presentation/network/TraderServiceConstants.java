//
//-----------------------------------------------------------------------------------
//Source file: TraderServiceConstants.java
//
//PACKAGE: package com.cboe.infra.presentation.traderService;
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import com.cboe.EventService.Transport.EventJMSProfileImpl;
import com.cboe.EventService.Transport.EventJMSProfileImplFactory;

public class TraderServiceConstants
{
    // IsAlive string values
    public final static String FAILED_ALIVE_STR = "FAILED";
    public final static String UNKNOWN_ALIVE_STR = "UNKNOWN";
    public final static String HERE_ALIVE_STR = "HERE";
    public final static String FORWARD_ALIVE_STR = "FORWARD";
    public final static String FORPERM_ALIVE_STR = "FORPERM";
    public final static String LOCEX_ALIVE_STR = "LOCEX";
    public final static String LOCADDR_ALIVE_STR = "LOCADDR";
    public final static String INVALID_ALIVE_STR = "INVALID";
    public final static String NOT_AVAILABLE_ALIVE_STR = "N/A";

    // POA State string values
    public final static String FAILED_STATE_STR = "FAILED";
    public final static String HOLDING_STATE_STR = "HOLDING";
    public final static String DISCARDING_STATE_STR = "DISCARDING";
    public final static String ACTIVE_STATE_STR = "ACTIVE";
    public final static String INACTIVE_STATE_STR = "INACTIVE";
    public final static String UNKNOWN_STATE_STR = "UNKNOWN";
    public final static String NOT_AVAILABLE_STATE_STR = "N/A";

    // bind order string values
    public final static String LOFT_BIND_ORDER_STR = "LOFT";
    public final static String TOFT_BIND_ORDER_STR = "TOFT";
    public final static String SOFT_BIND_ORDER_STR = "SOFT";
    public final static String SWUL_BIND_ORDER_STR = "SWUL";
    public final static String UNKNOWN_BIND_ORDER_STR = "UNKNOWN";
    public final static String NONE_BIND_ORDER_STR = "NONE";

    public final static String EVENT_CHANNEL_SERVICE_TYPE = "EventChannel";

    // query results type indicator (non-eventChannel vs. eventChannel)
    public final static int QUERY_RESULTS_TYPE_UNKNOWN = -1;
    public final static int QUERY_RESULTS_TYPE_NON_EC = 0;
    public final static int QUERY_RESULTS_TYPE_EC = 1;

    // constraint builder keywords
    public final static String[] constraintKeywords = new String[] 
    {
        "and" ,"or" ,"not"                            // boolean connectives
        ,"exist"                                      // property existence       
        ,"==" ,"!=" ,">" ,">=" ,"<" ,"<=" ,"~" ,"in"  // comparative functions
        ,"+" ,"-" ,"*" ,"/"                           // mathematic operators
        ,","                                          // grouping operators
    };
    
    public static final String CONSTRAINT_GROUP_OPERATOR = ",";
    
    // args passed to createEventChannel method
    public static final String INTERFACE_WILDCARD = "*";
    public static final String ARG_NOTIFIED = "-notify";
    public static final String ARG_INTERFACE = "-interface";
    public static final String ARG_QOS_BEST_EFFORT = "-best_effort";
    public static final String ARG_QOS_GMD = "-reliable";
    public static final String ARG_QOS_FIFO = "-fifo";
    public static final String ARG_QOS_PRIORITY = "-priority";
    public static final String ARG_QOS_PRIORITY_LOW = "low";
    public static final String ARG_QOS_PRIORITY_MEDIUM = "medium";
    public static final String ARG_QOS_PRIORITY_HIGH = "high";
    public static final String ARG_QOS_PRIORITY_CRITICAL = "critical";
    public static final String ARG_TIME_TO_LIVE = "-time_to_live";
    public static final String ARG_EXPORT_TO_INITREFS = "-exportToInitrefs";

    // jms args passed to createEventChannel method
    public static final String ARG_JMS_PROVIDER_NAME = "-jmsProviderName";
    public static final String ARG_JMS_ADMIN_CLASS_NAME = "-jmsAdminClassName";
    public static final String ARG_JMS_PROVIDER_UTILS_CLASS_NAME = "-jmsProviderUtilsClassName";
    public static final String ARG_USE_JMS_MESSAGE_ID = "-useJmsMessageID";
    public static final String ARG_JMS_SESSION_POLICY = "-jmsSessionPolicy";
    public static final String ARG_JMS_CONNECTION_POLICY = "-jmsConnectionPolicy";
    
    // quality of service types
    public static final String QOS_BEST_EFFORT = "best_effort";
    public static final String QOS_GMD = "reliable";
    public static final String QOS_FIFO = "fifo";
    public static final String QOS_PRIORITY = "priority";

    public final static String[] qualityOfServiceTypes = new String[] 
    {
        QOS_BEST_EFFORT 
        ,QOS_GMD 
        ,QOS_FIFO
        ,QOS_PRIORITY
    };
    
    // quality of service args
    public final static String[] qualityOfServiceArgs = new String[] 
    {
        ARG_QOS_BEST_EFFORT 
        ,ARG_QOS_GMD 
        ,ARG_QOS_FIFO
        ,ARG_QOS_PRIORITY
    };
                                                                    
    // priority qos types
    public final static String[] priorityQosTypes = new String[] 
    {
        ARG_QOS_PRIORITY_LOW 
        ,ARG_QOS_PRIORITY_MEDIUM 
        ,ARG_QOS_PRIORITY_HIGH
        ,ARG_QOS_PRIORITY_CRITICAL
    };

    // validate interface 
    public static final boolean defaultValidateInterface = true;

    // time to live 
    public static final int defaultTimeToLive = 0;
    
    // notify 
    public static final boolean defaultNotify = true;

    // export to initRefs
    public static final boolean defaultExportToInitRefs = false;
    
    public final static String[] jmsSessionPolicyTypes = new String[]
    {
        "SESSION_PER_SUBJECT" 
        ,"SESSION_PER_CHANNEL" 
    };

    // provider our own index into the jmsConnectionPolicyTypes array
    // the actual value is EventJMSProfileImpl.DEFAULT_JMS_CONNECTION_POLICY = -1
    public final static int defaultJmsConnectionPolicy = 2;
    
    public final static String[] jmsConnectionPolicyTypes = new String[]
    {
        "CONNECTION_PER_SUBJECT" 
        ,"CONNECTION_PER_CHANNEL" 
        ,"JMS_GLOBAL_CONNECTION" 
    };

}
