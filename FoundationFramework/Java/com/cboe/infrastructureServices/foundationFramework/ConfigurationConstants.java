package com.cboe.infrastructureServices.foundationFramework;
/**
 * A place holder the property names used in the configuration of the foundation framework.
 * @author Dave Hoag
 * @version 4.1
 */
public interface ConfigurationConstants
{
	final static String AUTO_START = "autoStart";
	final static String AUTO_INIT = "autoInit";
	final static String IS_MASTER = "isMaster";
	final static String LOG_COMPONENT ="logServiceComponent";

	//Core service properties
	final static String LOG_SERVICE_IMPL ="logServiceImpl";
	final static String COMMAND_CALLBACK_SERVICE_IMPL = "commandCallbackServiceImpl";
	final static String ORB_SERVICE_IMPL = "orbServiceImpl";
	final static String EVENT_SERVICE_IMPL = "eventServiceImpl";
	final static String ADMIN_SERVICE_IMPL = "adminServiceImpl";
	final static String TRADER_SERVICE_IMPL = "traderServiceImpl";
	final static String SECURITY_SERVICE_IMPL = "securityServiceImpl";
    final static String ID_SERVICE_IMPL = "uuidServiceImpl";
    final static String JCACHE_SERVICE_IMPL = "jcacheServiceImpl";
	final static String LIFE_LINE_SERVICE_IMPL = "lifeLineServiceImpl";	
	final static String PERSISTENCE_SERVICE_IMPL = "persistenceServiceImpl";
	final static String TIMER_SERVICE_IMPL = "timeServiceImpl";
	final static String CALENDAR_SERVICE_IMPL = "calendarServiceImpl";
	final static String INSTRUMENTATION_SERVICE_IMPL = "instrumentationServiceImpl";
	final static String SESSION_MGMT_SERVICE_IMPL = "sessionManagementServiceImpl";
	final static String PROCESS_WATCHER_SERVICE_IMPL = "processWatcherServiceImpl";	
	final static String LIST_OF_SERVICES = "listOfServices";

	//BOHome and BOContainer properties
	final static String HOMELIST = "listOfHomes";
	final static String CONTAINERLIST = "listOfContainers";
	final static String INITCONTAINER = "initialContainer";
	final static String INITHOME = "initialHome";
	final static String CONTAINER_IMPL = "containerImpl";
	final static String HOME_IMPL ="homeImpl";
	final static String HOME_NAME = "homeName";
	final static String RESOURCE_NAME = "managedResourceName";
	final static String CONTAINER_DESC_NAME = "containerDescName";
	final static String INTERCEPTOR_IMPL = "interceptorImpl";
	final static String INTERCEPTOR_NAME = "interceptorName";
	final static String TRANSACTION_POLICY = "transactionPolicy";

	final static String NEXT ="next";
}
