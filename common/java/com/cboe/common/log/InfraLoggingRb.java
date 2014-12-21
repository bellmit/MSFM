package com.cboe.common.log;

/**
 * InfraLoggingRb.java
 *
 * This class extends ResourceBundle and represents all messages logged
 * in the infrastructure.  It implements the ResourceBundle as a
 * HashMap.
 * For each new message ID added to the system, please follow
 * these conventions:
 * 1) The constant name should be meaningful.  Try to prefix the
 *    name with something meaningful and consistent.  Also, try to
 *    keep the constants grouped in the source together according
 *    to category.
 * 2) The value of the message ID needs to be in this format:
 *    <component>.<subcomponent>[.<subcomponent>].<nnnn>
 *    where <nnnn> is some number in sequence underneath the
 *    given component / subcomponent.
 * For each new message ID, a LogMessageId needs to be added
 * to the contentsMap.  This ID should have text that can be in a form usable
 * by java.text.MessageFormat.  The logging API allows
 * an argument array to be passed which will be used to
 * fill in values according to MessageFormat rules.  Also,
 * className (not full package name) and methodName can be filled in.
 *
 * Created: Tue Jun  3 10:49:31 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Iterator;

public class InfraLoggingRb extends ResourceBundle {
	private static HashMap contentsMap = new HashMap();

	// For each new message ID added to the system, please follow
	// these conventions:
	// 1) The constant name should be meaningful.  Try to prefix the
	//    name with something meaningful and consistent.  Also, try to
	//    keep the constants grouped in the source together according
	//    to category.
	// 2) The value of the message ID needs to be in this format:
	//    <component>.<subcomponent>[.<subcomponent>].<nnnn>
	//    where <nnnn> is some number in sequence underneath the
	//    given component / subcomponent.
	public static final String TRACE_ENTRY = "TRACE.0001";
	public static final String TRACE_EXIT = "TRACE.0002";

	public static final String METHOD_ENTRY_0 = "COMMON.ENTRY.0010";
	public static final String METHOD_ENTRY_1 = "COMMON.ENTRY.0011";
	public static final String METHOD_ENTRY_2 = "COMMON.ENTRY.0012";
	public static final String METHOD_ENTRY_3 = "COMMON.ENTRY.0013";
	public static final String METHOD_ENTRY_4 = "COMMON.ENTRY.0014";
	public static final String METHOD_ENTRY_5 = "COMMON.ENTRY.0015";
	public static final String METHOD_EXIT = "COMMON.EXIT.0001";

	// Generic SHUTDOWN.
	public static final String SHUTDOWN = "SHUTDOWN.0001";

	// Messaging, Locator / ImplementationRepository message ID's.
	public static final String MS_LOC_NO_POAOID_RESULTS = "MS.LOC.DB.0001";
	public static final String MS_LOC_FATAL_DB_EXCEPTION = "MS.LOC.DB.0002";
	public static final String MS_LOC_DB_ERROR = "MS.LOC.DB.0003";

	public static final String MS_LOC_SERVE_LOCATE_REQ = "MS.LOC.0001";
	public static final String MS_LOC_POA_NOT_FOUND = "MS.LOC.0002";
	public static final String MS_LOC_POA_FOUND = "MS.LOC.0003";
	public static final String MS_LOC_PING_ACTIVE_OBJECT = "MS.LOC.0004";
	public static final String MS_LOC_PING_ACTIVE_OBJECT_FAILED = "MS.LOC.0005";
	public static final String MS_LOC_REPLY_SUCCESS = "MS.LOC.0006";
	public static final String MS_LOC_PING_DISCARDING_OBJECT = "MS.LOC.0007";
	public static final String MS_LOC_PING_DISCARDING_OBJECT_FAILED = "MS.LOC.0008";
	public static final String MS_LOC_OBJECT_ACTIVE_DB_DISCARDING = "MS.LOC.0009";
	public static final String MS_LOC_OBJECT_NOT_ACTIVE_OR_DISCARDING = "MS.LOC.0010";
	public static final String MS_LOC_KNIFE_SWITCH = "MS.LOC.0011";
	public static final String MS_LOC_UNKNOWN_OBJECT = "MS.LOC.0012";
	public static final String MS_LOC_SKIP_SOFT = "MS.LOC.0013";
	public static final String MS_LOC_PING_EXCEPTION = "MS.LOC.0014";
	public static final String MS_LOC_OA_OBJECT = "MS.LOC.0015";
	public static final String MS_LOC_OA_PING_EXCEPTION = "MS.LOC.0016";
	public static final String MS_LOC_REGISTER_INTF = "MS.LOC.0017"; // Used to log registration intf stuff.
	public static final String MS_LOC_THREAD_POOL_START = "MS.LOC.0018";
	public static final String MS_LOC_SERVER_POA_INIT_ERROR = "MS.LOC.0019";
	public static final String MS_LOC_SERVER_INITREF_ERROR = "MS.LOC.0020";
	public static final String MS_LOC_MAIN_ORB_INIT_ERROR = "MS.LOC.0021";
	public static final String MS_LOC_MAIN_VM_SHUTDOWN_HOOK_ERROR = "MS.LOC.0022";
	public static final String MS_LOC_MAIN_NO_INITREFURL = "MS.LOC.0023";
	public static final String MS_LOC_MAIN_STARTUP_INFO = "MS.LOC.0024";
	public static final String MS_LOC_MAIN_STARTED = "MS.LOC.0025";

	public static final String MS_LOC_RESOLVE_VIA_LOCATOR = "MS.LOC.INITREF.0001";
	public static final String MS_LOC_USE_FILE_LOC_REF = "MS.LOC.INITREF.0002";
	public static final String MS_LOC_USE_DYN_LOC_REF = "MS.LOC.INITREF.0003";
	public static final String MS_LOC_NOREBIND_ON_LOCREF_ERROR = "MS.LOC.INITREF.0004";
	public static final String MS_LOC_NO_REF_FROM_LOCATOR = "MS.LOC.INITREF.0005";
	public static final String MS_LOC_SEND_INITREF_LOC_REQ = "MS.LOC.INITREF.0006";
	public static final String MS_LOC_INITREF_FOUND_VIA_LOCATOR = "MS.LOC.INITREF.0007";

	public static final String MS_DYNLOC_IOEXCEPTION = "MS.LOC.DYNLOC.0001";
	public static final String MS_DYNLOC_UNRECOGNIZED_DATAGRAM = "MS.LOC.DYNLOC.0002";
	public static final String MS_DYNLOC_PUB_DISABLED = "MS.LOC.DYNLOC.0003";

	// Trading Service

	public static final String DS_TS_TRADER_NO_PROPERTY_FILE = "DS.TS.TRADER.0001";
	public static final String DS_TS_TRADER_IO_EXCEPTION = "DS.TRDER.TS.0002";
	public static final String DS_TS_TRADER_TIES_INSTANTIATED = "DS.TS.TRADER.0003";
	public static final String DS_TS_TRADER_CORBA_OBJECT_ACTIVATED = "DS.TS.TRADER.0004";
	public static final String DS_TS_TRADER_POA_MGR_ACTIVATED = "DS.TS.TRADER.0005";
	public static final String DS_TS_TRADER_IOR_WRITTEN_TO_FILE = "DS.TS.TRADER.0006";
	public static final String DS_TS_TRADER_ACCEPTING_REQUESTS = "DS.TS.TRADER.0007";
	public static final String DS_TS_TRADER_DISCARDING_REQUESTS = "DS.TS.TRADER.0008";
	public static final String DS_TS_TRADER_CAUGHT_SIGNAL = "DS.TS.TRADER.0009";
	public static final String DS_TS_TRADER_STATE_CHANGE_EXCEPTION = "DS.TS.TRADER.0010";
	public static final String DS_TS_TRADER_SHUTDOWN = "DS.TS.TRADER.0011";
	
	public static final String DS_TS_TRADER_CORBA_SYSTEM_EXCEPTION = "DS.TS.TRADER.0013";
	public static final String DS_TS_TRADER_MISSING_PROPERTY = "DS.TS.TRADER.0014";
	public static final String DS_TS_TRADER_INS_NOT_FOUND_EXCEPTION = "DS.TS.TRADER.0015";
	public static final String DS_TS_TRADER_CLASS_NOT_FOUND_EXCEPTION = "DS.TS.TRADER.0016";
	public static final String DS_TS_TRADER_INS_EXCEPTION = "DS.TS.TRADER.0017";
	public static final String DS_TS_TRADER_ILLEGAL_ACCESS_EXCEPTION = "DS.TS.TRADER.0018";
	public static final String DS_TS_TRADER_POA_CREATED = "DS.TS.TRADER.0019";
	public static final String DS_TS_TRADER_UNINITIALIZED_ORB_ERROR = "DS.TS.TRADER.0020";
	public static final String DS_TS_TRADER_INVALID_STATE_TRANSITION_EXCEPTION = "DS.TS.TRADER.0021";
	public static final String DS_TS_TRADER_INVOCATION_TARGET_EXCEPTION = "DS.TS.TRADER.0022";
	public static final String DS_TS_TRADER_INFO = "DS.TS.TRADER.023";
	public static final String DS_TS_TRADER_IOR_NOTDEFINED = "DS.TS.TRADER.024";
	public static final String DS_TS_TRADER_URL_REMOTEREF = "DS.TS.TRADER.025";
	public static final String DS_TS_TRADER_BAD_URL = "DS.TS.TRADER.026";
	public static final String DS_TS_TRADER_USER_EXCEPTION = "DS.TS.TRADER.0027";
	public static final String DS_TS_TRADER_REMOTELOOKUP_NULL = "DS.TS.TRADER.0028";
	public static final String DS_TS_TRADER_ILLEGALLINKNAME_EXCEPTION = "DS.TS.TRADER.0029";
	public static final String DS_TS_TRADER_DUPLINKNAME_EXCEPTION = "DS.TS.TRADER.0030";

	public static final String DS_TS_LOOKUP_QUERY_RESULT = "DS.TS.LOOKUP.0001";
	public static final String DS_TS_LOOKUP_PROPERTYVALUE = "DS.TS.LOOKUP.0002";
	public static final String DS_TS_LOOKUP_FORWARD_QUERY = "DS.TS.LOOKUP.0003";
	public static final String DS_TS_LOOKUP_ILLEGALLINKNAME_EXCEPTION = "DS.TS.LOOKUP.0004";
	public static final String DS_TS_LOOKUP_UNKNOWNLINKNAME_EXCEPTION = "DS.TS.LOOKUP.0005";
	public static final String DS_TS_LOOKUP_TIMEOUT_EXCEPTION = "DS.TS.LOOKUP.0006";
	public static final String DS_TS_LOOKUP_OBJECTNOTEXISTS_EXCEPTION = "DS.TS.LOOKUP.0007";
	public static final String DS_TS_LOOKUP_INFO = "DS.TS.LOOKUP.008";
	
	
	public static final String INVALID_RESOLVE_INITREF_NAME = "DS.TS.0005";
	public static final String UNCAUGHT_EXCEPTION = "DS.TS.0006";
	public static final String FILE_NOT_FOUND_EXCEPTION = "DS.TS.0007";
	public static final String NULL_POINTER_EXCEPTION = "DS.TS.0008";
	public static final String DS_TS_POA_EXCEPTION = "DS.TS.0009";
	public static final String DS_TS_FATAL_SQL_EXCEPTION = "DS.TS.0010";
	
	public static final String DS_TS_PROPERTY_TYPE_MISMATCH = "DS.TS.0011";
	public static final String DS_TS_DB_QUERY_EXCEPTION = "DS.TS.0012";
	public static final String DS_TS_SERVICE_TYPE_QUERY_EXCEPTION = "DS.TS.0013";
	public static final String DS_TS_EXPORT_OFFER_QUERY_EXCEPTION = "DS.TS.0014";
	public static final String DS_TS_INVALID_OFFER_ID = "DS.TS.0015";
	public static final String DS_TS_WITHDRAW_QUERY_EXCEPTION = "DS.TS.0016";
	public static final String DS_TS_DESCRIBE_QUERY_EXCEPTION = "DS.TS.0017";
	public static final String DS_TS_MODIFY_QUERY_EXCEPTION = "DS.TS.0018";
	public static final String DS_TS_ADD_SERVICE_TYPE_EXCEPTION = "DS.TS.0019";
	public static final String DS_TS_REMOVE_SERVICE_TYPE_EXCEPTION = "DS.TS.0020";
	public static final String DS_TS_GET_ALL_SERVICE_TYPES_EXCEPTION = "DS.TS.0021";
	public static final String DS_TS_GET_SERVICE_TYPE_EXCEPTION = "DS.TS.0022";
	public static final String DS_TS_UNMASK_SERVICE_TYPE_EXCEPTION = "DS.TS.0023";
	public static final String DS_TS_MASK_SERVICE_TYPE_EXCEPTION = "DS.TS.0024";

	public static final String DS_TS_ADMIN_PROPERTYVALUE = "DS.TS.ADMIN.001";
	
	public static final String DS_TS_LINK_PROPERTYVALUE = "DS.TS.LINK.001";
	public static final String DS_TS_LINK_REMOVE_FAILED = "DS.TS.LINK.002";
	public static final String DS_TS_LINK_INFO = "DS.TS.LINK.003";

	
	public static final String DS_TS_REGISTER_DUPLICATE_PROPERTIES = "DS.TS.REGISTER.0001";
	public static final String DS_TS_OFFER_ID_DESCRIPTION_FULL = "DS.TS.REGISTER.0002";
	public static final String DS_TS_OFFER_ID_DESCRIPTION_DIGESTED = "DS.TS.REGISTER.0003";
	public static final String DS_TS_REGISTER_INVALID_OBJECT_REFERENCE = "DS.TS.REGISTER.0004";
	public static final String DS_TS_REGISTER_OFFER_KEY = "DS.TS.REGISTER.0005";
	public static final String DS_TS_REGISTER_SERVICE_OFFER_WITHDRAWN = "DS.TS.REGISTER.0006";
	public static final String DS_TS_WITHDRAW_ILLEGAL_OFFER_ID_EXCEPTION = "DS.TS.REGISTER.0007";
	public static final String DS_TS_WITHDRAW_UNKNOWN_OFFER_ID_EXCEPTION = "DS.TS.REGISTER.0008";
	public static final String DS_TS_REGISTER_NO_MODIFY_NEEDED = "DS.TS.REGISTER.0009";
	public static final String DS_TS_REGISTER_DESCRIPTION_MODIFIED = "DS.TS.REGISTER.0010";
	
	
	public static final String DS_TS_SERVICE_TYPE_EXPORTED = "DS.TS.REGISTER.0011";

	
	public static final String DS_TS_PERSIST_NO_MATCHING_TCKIND = "DS.TS.PERSIST.0001";
	public static final String DS_TS_PERSIST_NO_SUCH_FIELD_EXCEPTION = "DS.TS.PERSIST.0002"; 
	public static final String DS_TS_DBUTIL_SERVICE_TYPE_EXISTS_EXCEPTION = "DS.TS.PERSIST.0003";
	
	public static final String DS_TS_PROPBUILDER_NAMING_EXCEPTION = "DS.TS.TYPEREP.0001";
	public static final String DS_TS_PROPBUILDER_UNCAUGHT_EXCEPTION = "DS.TS.TYPEREP.0002";
	public static final String DS_TS_SERVICE_TYPE_REPOSITORY_OPERATION = "DS.TS.TYPEREP.0003";
	public static final String  DS_TS_DBUTIL_VALUE_TYPE_REDEFINITION_EXCEPTION = "TODO.1";
	public static final String DS_TS_EXPORT_OFFER_EXCEPTION = "TODO.2";
	public static final String DS_TS_PARSER_TYPE_NOT_SUPPORTED = "TS.DS.PARSER.0001";
	
	public static final String DS_TS_DBUTIL_ADD_SERVICE_TYPE_PROPERTY = "DS.TS.DBUTIL.0002";
	public static final String DS_TS_PARSER_NUMBER_FORMAT_EXCEPTION = "DS.TS.PARSER.0002";
	


	// Security
	public static final String SEC_SERVER_POA_CREATE_ERROR = "SEC.SERVER.0001";
	public static final String SEC_SERVER_POA_CREATED = "SEC.SERVER.0002";
	public static final String SEC_SERVER_SMA_RETRIEVED = "SEC.SERVER.0003";
	public static final String SEC_SERVER_MBEAN_RETRIEVED = "SEC.SERVER.0004";
	public static final String SEC_SERVER_CORBA_OBJECT_ACTIVATED = "SEC.SERVER.0005";
	public static final String SEC_SERVER_POA_MGR_ACTIVATED = "SEC.SERVER.0006";
	public static final String SEC_SERVER_INFE = "SEC.SERVER.0007";
	public static final String SEC_SERVER_TI_STATUS = "SEC.SERVER.0008";
	public static final String SEC_SERVER_IOR_WRITTEN = "SEC.SERVER.0009";
	public static final String SEC_SERVER_IORS_EXPORTED = "SEC.SERVER.0010";
	public static final String SEC_SERVER_ACCEPTING_REQUESTS = "SEC.SERVER.0011";
	public static final String SEC_SERVER_DISCARDING_REQUESTS = "SEC.SERVER.0012";
	public static final String SEC_SERVER_CAUGHT_SIGNAL = "SEC.SERVER.0013";
	public static final String SEC_SERVER_STATE_CHANGE_EXCEPTION = "SEC.SERVER.0014";
	public static final String SEC_IMPL_CTR_FAILURE = "SEC.SERVER.0015";
	public static final String SEC_IMPL_SIG_VERIFICATION = "SEC.SERVER.0016";
	public static final String SEC_IMPL_FIND_OBJREF_EXCEPTION = "SEC.SERVER.0017";
	public static final String SEC_IMPL_NO_ROLE = "SEC.SERVER.0018";
	public static final String SEC_IMPL_QUERY_EXCEPTION = "SEC.SERVER.0019";
	public static final String SEC_IMPL_FATAL_SQL_EXCEPTION = "SEC.SERVER.0020";
	public static final String SEC_IMPL_NO_CHANNEL = "SEC.SERVER.0021";
	public static final String SEC_SQL_NO_SUCH_FIELD = "SEC.SERVER.0022";
	public static final String SEC_SQL_ILLEGAL_ACCESS = "SEC.SERVER.0023";
	public static final String SEC_SQL_ILLEGAL_ARGUMENT = "SEC.SERVER.0024";
	public static final String SEC_SERVER_INVALID_STATE_TRANSITION = "SEC.SERVER.0025";
	public static final String SEC_SERVER_INVOCATION_EXCEPTION = "SEC.SERVER.0026";
	public static final String SEC_SERVER_INSTANCE_NOT_FOUND = "SEC.SERVER.0027";
	public static final String SEC_IMPL_EVENT_STARTED = "SEC.SERVER.0028";
	public static final String SEC_IMPL_SEVERE_ERROR = "SEC.SERVER.0029";
	public static final String SEC_JAVA_EXCEPTION = "SEC.SERVER.0030";
	public static final String SEC_INVALID_BIND_ORDER = "SEC.SERVER.0031";
	public static final String SEC_POA_ERROR = "SEC.SERVER.0032";
	public static final String SEC_LOGGER_EXCEPTION = "SEC.SERVER.0033";
	public static final String SEC_ACL_REQUEST_FOR_ACTION = "SEC.SERVER.0034";
	public static final String SEC_ACL_EC_SETUP = "SEC.SERVER.0035";
	public static final String SEC_LOGGER_INIT_EXCEPTION = "SEC.SERVER.0036";

	// JMS
	public static final String JMS_STATS_TIPC_CLIENT_SUBJ_POLL = "JMS.STATS.TIPC.CLIENT_SUBJECT_POLL_CALL";
	public static final String JMS_STATS_TIPC_CLIENT_BUFF_POLL = "JMS.STATS.TIPC.CLIENT_BUFFER_POLL_CALL";
	public static final String JMS_STATS_TIPC_CLIENT_GEN_POLL = "JMS.STATS.TIPC.CLIENT_GENERAL_POLL_CALL";
	public static final String JMS_STATS_TIPC_SUBJ_INFO = "JMS.STATS.TIPC.CLIENT_SUBJECT_INFO";
	public static final String JMS_STATS_TIPC_SERVER_BUFF_INFO = "JMS.STATS.TIPC.SERVER_BUFFER_INFO";
	public static final String JMS_STATS_TIPC_SERVER_BUFF_POLL = "JMS.STATS.TIPC.SERVER_BUFFER_POLL_CALL";
	public static final String JMS_STATS_TIPC_SERVER_NAMES_POLL = "JMS.STATS.TIPC.SERVER_NAMES_POLL_CALL";

	static {
		// See class comments above for information regarding the contentsMap
		// values.


		// TRACE_ENTRY and TRACE_EXIT are generic, so can't give class and
		// method they pertain to.  I made the msgText use two arguments, className
		// and methodName.
		contentsMap.put( TRACE_ENTRY, Logger.createLogMessageId( TRACE_ENTRY, "TRACE Entry {0}.{1}" ) );
		contentsMap.put( TRACE_EXIT, Logger.createLogMessageId( TRACE_EXIT, "TRACE Exit {0}.{1}" ) );

		// Generic Shutdown message.
		contentsMap.put( SHUTDOWN, Logger.createLogMessageId( SHUTDOWN, "VM Shutdown; Shutdown ORB.",
												    "", "" ) );

		// Messaging Locator / ImplementationRepository message ID's.
		String reqStr = "RequestingOrb({0}),ReqId({1,number,#}): Poa(/{2}),Host({3}),Port({4,number,#}),Orb({5}),Oid({6}),ProfileHost({7}),ProfilePort({8,number,#}),ProfileOrb({9})";
		String entryStr = "Poa(/{0}),Host({1}),Port({2,number,#}),Orb({3}),Oid({4})";
		String reqStrShift = "RequestingOrb({5}),ReqId({6,number,#}): Poa(/{7}),Host({8}),Port({9,number,#}),Orb({10}),Oid({11})";
		String registerStr = "{0}: Poa(/{1}),Host({2}),Port({3,number,#}),Orb({4}),Oid({5}),State({6}),LocPref({7}).";
		contentsMap.put( MS_LOC_NO_POAOID_RESULTS,
					  Logger.createLogMessageId( MS_LOC_NO_POAOID_RESULTS,
										    "query results empty for POA+OID({0}{1}{2}).  " +
										    "Doing query without OID.",
										    "LocatorDBUtil", "getEntries" ) );
		contentsMap.put( MS_LOC_FATAL_DB_EXCEPTION,
					  Logger.createLogMessageId( MS_LOC_FATAL_DB_EXCEPTION,
										    "PANIC: **** Connection to Oracle lost ****  Shutting down..." ) );
		contentsMap.put( MS_LOC_DB_ERROR,
					  Logger.createLogMessageId( MS_LOC_DB_ERROR,
										    "DB error.  {0}" ) );
		contentsMap.put( MS_LOC_SERVE_LOCATE_REQ,
					  Logger.createLogMessageId( MS_LOC_SERVE_LOCATE_REQ,
										    "Serve locate request: " + reqStr,
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_POA_NOT_FOUND,
					  Logger.createLogMessageId( MS_LOC_POA_NOT_FOUND,
										    "POA Not found, returning UNKNOWN_OBJECT, " +
										    "for original req(" + reqStr + ").",
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_POA_FOUND,
					  Logger.createLogMessageId( MS_LOC_POA_FOUND,
										    "POA found for original req(" + reqStr + ").",
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_PING_ACTIVE_OBJECT,
					  Logger.createLogMessageId( MS_LOC_PING_ACTIVE_OBJECT,
										    "Ping ACTIVE object(" + entryStr + "), " +
										    "for original req(" + reqStrShift + ").",
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_PING_ACTIVE_OBJECT_FAILED,
					  Logger.createLogMessageId( MS_LOC_PING_ACTIVE_OBJECT_FAILED,
										    "Ping FAILED for ACTIVE object(" + entryStr + "), " +
										    "for original req(" + reqStrShift + ").",
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_REPLY_SUCCESS,
					  Logger.createLogMessageId( MS_LOC_REPLY_SUCCESS,
										    "Returning object(" + entryStr + "), " +
										    "for original req(" + reqStrShift + ").",
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_PING_DISCARDING_OBJECT,
					  Logger.createLogMessageId( MS_LOC_PING_DISCARDING_OBJECT,
										    "Ping DISCARDING object(" + entryStr + ") " +
										    "to verify server is running, " +
										    "for original req(" + reqStrShift + ").",
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_PING_DISCARDING_OBJECT_FAILED,
					  Logger.createLogMessageId( MS_LOC_PING_DISCARDING_OBJECT_FAILED,
										    "Ping FAILED for DISCARDING object(" +
										    entryStr + "), for original req(" +
										    reqStrShift + ").", "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_OBJECT_ACTIVE_DB_DISCARDING,
					  Logger.createLogMessageId( MS_LOC_OBJECT_ACTIVE_DB_DISCARDING,
										    "Object(" + entryStr + ") is alive, but " +
										    "DB is DISCARDING, returning object " +
										    "for original req(" + reqStrShift + ").",
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_OBJECT_NOT_ACTIVE_OR_DISCARDING,
					  Logger.createLogMessageId( MS_LOC_OBJECT_NOT_ACTIVE_OR_DISCARDING,
										    "Object(" + entryStr + ") is not ACTIVE " +
										    " or DISCARDING, for original req(" + reqStrShift + ").",
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_KNIFE_SWITCH,
					  Logger.createLogMessageId( MS_LOC_KNIFE_SWITCH,
										    "Waiting on knife switch for original " +
										    "req(" + reqStr + "), time remaining({7,number,#}).",
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_UNKNOWN_OBJECT,
					  Logger.createLogMessageId( MS_LOC_UNKNOWN_OBJECT,
										    "Exhausted search, returning UNKNOWN_OBJECT, " +
										    "for original req(" + reqStr + ").",
										    "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_SKIP_SOFT,
					  Logger.createLogMessageId( MS_LOC_SKIP_SOFT,
										    "SOFT policy identified for Object(" +
										    entryStr + "), skipping.  Original req(" +
										    reqStrShift + ").", "LocateWorker", "" ) );
		contentsMap.put( MS_LOC_PING_EXCEPTION,
					  Logger.createLogMessageId( MS_LOC_PING_EXCEPTION,
										    "Exception during ping of Object(" +
										    entryStr + ").",
										    "LocateWorker", "pingServer" ) );
		contentsMap.put( MS_LOC_OA_OBJECT,
					  Logger.createLogMessageId( MS_LOC_OA_OBJECT,
										    "OA object found(" + entryStr +
										    "), send LocateReply to ORB for location " +
										    "for original req(" + reqStrShift + ").",
										    "LocateWorker", "talkToOA" ) );
		contentsMap.put( MS_LOC_OA_PING_EXCEPTION,
					  Logger.createLogMessageId( MS_LOC_OA_PING_EXCEPTION,
										    "Exception during OA ping of Object(" +
										    entryStr + ").",
										    "LocateWorker", "talkToOA" ) );
		contentsMap.put( MS_LOC_REGISTER_INTF,
					  Logger.createLogMessageId( MS_LOC_REGISTER_INTF,
										    registerStr, "LocatorServer", "" ) );
		contentsMap.put( MS_LOC_THREAD_POOL_START,
					  Logger.createLogMessageId( MS_LOC_THREAD_POOL_START,
										    "Starting {0,number,#} worker({1}) threads.",
										    "ThreadPool", "" ) );
		contentsMap.put( MS_LOC_SERVER_POA_INIT_ERROR,
					  Logger.createLogMessageId( MS_LOC_SERVER_POA_INIT_ERROR,
										    "Unable to create POA / register servant.",
										    "LocatorServer", "" ) );
		contentsMap.put( MS_LOC_SERVER_INITREF_ERROR,
					  Logger.createLogMessageId( MS_LOC_SERVER_INITREF_ERROR,
										    "Unable to write IOR to initrefUrl({0}).",
										    "LocatorServer", "" ) );
		contentsMap.put( MS_LOC_MAIN_ORB_INIT_ERROR,
					  Logger.createLogMessageId( MS_LOC_MAIN_ORB_INIT_ERROR,
										    "Unable to initialize ORB.  Exiting.",
										    "LocatorMain", "" ) );
		contentsMap.put( MS_LOC_MAIN_VM_SHUTDOWN_HOOK_ERROR,
					  Logger.createLogMessageId( MS_LOC_MAIN_VM_SHUTDOWN_HOOK_ERROR,
										    "Unable to register VM shutdown hook.",
										    "LocatorMain", "" ) );
		contentsMap.put( MS_LOC_MAIN_NO_INITREFURL,
					  Logger.createLogMessageId( MS_LOC_MAIN_NO_INITREFURL,
										    "No ORB.InitRefURL specified.",
										    "LocatorMain", "" ) );
		contentsMap.put( MS_LOC_MAIN_STARTUP_INFO,
					  Logger.createLogMessageId( MS_LOC_MAIN_STARTUP_INFO,
										    "Primary({0}), InitRefURL({1}), " +
										    "KnifeSwitchTimeout({2,number,#}), " +
										    "LocateThreadPoolSize({3,number,#}), " +
										    "Poa({4}), ObjectName({5}), " +
										    "PingTimeout({6,number,#}).",
										    "LocatorMain", "" ) );
		contentsMap.put( MS_LOC_MAIN_STARTED,
					  Logger.createLogMessageId( MS_LOC_MAIN_STARTED,
										    "Locator Started. Accepting Requests.",
										    "LocatorMain", "" ) );
		contentsMap.put( MS_LOC_RESOLVE_VIA_LOCATOR,
					  Logger.createLogMessageId( MS_LOC_RESOLVE_VIA_LOCATOR,
										    "InitRef({0}) does not Exist in Map. Resolve via the Locator.",
										    "InitialReferencesThroughLocator",
										    "getReference" ) );
		contentsMap.put( MS_LOC_USE_FILE_LOC_REF,
					  Logger.createLogMessageId( MS_LOC_USE_FILE_LOC_REF,
										    "Use locator ref from initrefs file.",
										    "InitialReferencesThroughLocator",
										    "getReference" ) );
		contentsMap.put( MS_LOC_USE_DYN_LOC_REF,
					  Logger.createLogMessageId( MS_LOC_USE_DYN_LOC_REF,
										    "Use dynamic locator ref.",
										    "InitialReferencesThroughLocator",
										    "getReference" ) );
		contentsMap.put( MS_LOC_NOREBIND_ON_LOCREF_ERROR,
					  Logger.createLogMessageId( MS_LOC_NOREBIND_ON_LOCREF_ERROR,
										    "Unable to add NO_REBIND policy to locator ref.",
										    "InitialReferencesThroughLocator",
										    "getReference" ) );
		contentsMap.put( MS_LOC_NO_REF_FROM_LOCATOR,
					  Logger.createLogMessageId( MS_LOC_NO_REF_FROM_LOCATOR,
										    "Failed to get reference({0}) from locator.",
										    "InitialReferencesThroughLocator",
										    "getReference" ) );
		contentsMap.put( MS_LOC_SEND_INITREF_LOC_REQ,
					  Logger.createLogMessageId( MS_LOC_SEND_INITREF_LOC_REQ,
										    "Sending initial reference locate request for ref({0}).",
										    "InitialReferencesThroughLocator",
										    "getReference" ) );
		contentsMap.put( MS_LOC_INITREF_FOUND_VIA_LOCATOR,
					  Logger.createLogMessageId( MS_LOC_INITREF_FOUND_VIA_LOCATOR,
										    "Initref({0} found via locator.  IOR({1}).",
										    "InitialReferencesThroughLocator",
										    "getReference" ) );
		contentsMap.put( MS_DYNLOC_IOEXCEPTION,
					  Logger.createLogMessageId( MS_DYNLOC_IOEXCEPTION,
										    "Exception receiving dynloc reply.",
										    "DynLoc", "" ) );
		contentsMap.put( MS_DYNLOC_UNRECOGNIZED_DATAGRAM,
					  Logger.createLogMessageId( MS_DYNLOC_UNRECOGNIZED_DATAGRAM,
										    "Unrecognized datagram received.",
										    "DynLoc", "" ) );
		contentsMap.put( MS_DYNLOC_PUB_DISABLED,
					  Logger.createLogMessageId( MS_DYNLOC_PUB_DISABLED,
										    "Locator publishing has been DISABLED. " +
										    "This locator can not be dynamically discovered.",
										    "DynLoc", "" ) );



		// TraderServer
		
		contentsMap.put( DS_TS_TRADER_MISSING_PROPERTY,
					  Logger.createLogMessageId( DS_TS_TRADER_MISSING_PROPERTY,
										    "Property {2} is missing in XML Repository",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( DS_TS_TRADER_NO_PROPERTY_FILE,
					  Logger.createLogMessageId( DS_TS_TRADER_NO_PROPERTY_FILE,		  
										    "File {3} not found.",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( DS_TS_TRADER_CORBA_OBJECT_ACTIVATED,			  
					  Logger.createLogMessageId( DS_TS_TRADER_CORBA_OBJECT_ACTIVATED,
										    "{2} CORBA Object Activated with POA: {3}",
										    "{0}",
										    "{1}" ) );
					  
		contentsMap.put( DS_TS_TRADER_IO_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_TRADER_IO_EXCEPTION,
										    "IO Exception from file {3}.",
										    "{0}",
										    "{1}" ) );
		
		contentsMap.put( DS_TS_TRADER_TIES_INSTANTIATED,
					  Logger.createLogMessageId( DS_TS_TRADER_TIES_INSTANTIATED,
										    "Instantiaged Tie Objects for Lookup, Admin, Register, and ServiceTypeRepository implementations.",
										    "{0}",
										    "{1}" ) );


		
		contentsMap.put( DS_TS_TRADER_POA_MGR_ACTIVATED,
					  Logger.createLogMessageId( DS_TS_TRADER_POA_MGR_ACTIVATED,
										    "POA manager for POA: {2} Activated.",
										    "{0}",
										    "{1}" ) );

					  
		 contentsMap.put( DS_TS_TRADER_IOR_WRITTEN_TO_FILE,	
					   Logger.createLogMessageId( DS_TS_TRADER_IOR_WRITTEN_TO_FILE,
											"TraderService IOR written to file: {2}",
											"{0}",
											"{1}" ) );


		 contentsMap.put( DS_TS_TRADER_ACCEPTING_REQUESTS,	   
					   Logger.createLogMessageId( DS_TS_TRADER_ACCEPTING_REQUESTS,
											"Trader Service Started. Accepting Requests",
											"{0}",
											"{1}" ) );


		 contentsMap.put( DS_TS_TRADER_DISCARDING_REQUESTS,	   
					   Logger.createLogMessageId( DS_TS_TRADER_DISCARDING_REQUESTS,
											"Trader Service Started. Discarding Requests",
											"{0}",
											"{1}" ) );


					   
		 contentsMap.put( DS_TS_TRADER_CAUGHT_SIGNAL,
					   Logger.createLogMessageId( DS_TS_TRADER_CAUGHT_SIGNAL,
											"Caught {2} Signal. Changed POA state to {3}",
											"{0}",
											"{1}" ) );

		 
		 contentsMap.put( DS_TS_TRADER_STATE_CHANGE_EXCEPTION,
					   Logger.createLogMessageId( DS_TS_TRADER_STATE_CHANGE_EXCEPTION,
											"Exception during POA state change update",
											"{0}",
											"{1}" ) );

		 contentsMap.put( DS_TS_TRADER_SHUTDOWN,
					   Logger.createLogMessageId( DS_TS_TRADER_SHUTDOWN,
											"Server shutting down",
											"{0}",
											"{1}" ) );


		 contentsMap.put( DS_TS_POA_EXCEPTION, 
					   Logger.createLogMessageId( DS_TS_POA_EXCEPTION, 		  
											"POA Exception",
											"{0}",
											"{1}" ) );
		 
		contentsMap.put( DS_TS_TRADER_CORBA_SYSTEM_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_TRADER_CORBA_SYSTEM_EXCEPTION,
										    "CORBA System Exception",
										    "{0}",
										    "{1}" ) );

		 
		contentsMap.put( DS_TS_TRADER_INS_NOT_FOUND_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_TRADER_INS_NOT_FOUND_EXCEPTION,
										    "Instance Not Found -- TODO",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( DS_TS_TRADER_CLASS_NOT_FOUND_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_TRADER_CLASS_NOT_FOUND_EXCEPTION,
										    "Class Not Found -- TODO",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( DS_TS_TRADER_INS_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_TRADER_INS_EXCEPTION,
										    "Instance Not Found -- TODO",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( DS_TS_TRADER_ILLEGAL_ACCESS_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_TRADER_ILLEGAL_ACCESS_EXCEPTION,
										    "Illegal Access -- TODO",
										    "{0}",
										    "{1}" ) );


		contentsMap.put( DS_TS_TRADER_POA_CREATED,
					  Logger.createLogMessageId( DS_TS_TRADER_POA_CREATED,
										    "Created {10} POA with policies {2}: {3}, {4}: {5}, {6}: {7}, {8}: {9}",
										    "{0}",
										    "{1}" ) );
		
		contentsMap.put( DS_TS_TRADER_UNINITIALIZED_ORB_ERROR,
					  Logger.createLogMessageId( DS_TS_TRADER_UNINITIALIZED_ORB_ERROR,
										    "ORB not initialized properly.",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( DS_TS_TRADER_INVALID_STATE_TRANSITION_EXCEPTION,
					  Logger.createLogMessageId(  DS_TS_TRADER_INVALID_STATE_TRANSITION_EXCEPTION,
											"TODO",
											"{0}",
											"{1}" ) );

		contentsMap.put( DS_TS_TRADER_INVOCATION_TARGET_EXCEPTION,
					  Logger.createLogMessageId(  DS_TS_TRADER_INVOCATION_TARGET_EXCEPTION,
											"TODO",
											"{0}",
											"{1}" ) );
		contentsMap.put( DS_TS_TRADER_INFO,
					Logger.createLogMessageId( DS_TS_TRADER_INFO,
                                                                        "{2}",
                                                                        "{0}",
                                                                        "{1}") );
		contentsMap.put( DS_TS_TRADER_REMOTELOOKUP_NULL,
					Logger.createLogMessageId( DS_TS_TRADER_REMOTELOOKUP_NULL,
                                                                        "{2}",
                                                                        "{0}",
                                                                        "{1}") );
		contentsMap.put( DS_TS_TRADER_IOR_NOTDEFINED,
					Logger.createLogMessageId( DS_TS_TRADER_IOR_NOTDEFINED,
                                                                        "{2}",
                                                                        "{0}",
                                                                        "{1}") );
		contentsMap.put( DS_TS_TRADER_URL_REMOTEREF,
					Logger.createLogMessageId( DS_TS_TRADER_URL_REMOTEREF,
                                                                        "{2}{3}{4}",
                                                                        "{0}",
                                                                        "{1}") );
		contentsMap.put( DS_TS_TRADER_BAD_URL,
					Logger.createLogMessageId( DS_TS_TRADER_BAD_URL,
                                                                        "{2}",
                                                                        "{0}",
                                                                        "{1}") );
		contentsMap.put( DS_TS_TRADER_USER_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_TRADER_USER_EXCEPTION,
								     "TODO",
								     "{0}",
								     "{1}" ) );
		contentsMap.put( DS_TS_TRADER_ILLEGALLINKNAME_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_TRADER_ILLEGALLINKNAME_EXCEPTION,
								     "IllegalLinkName Exception",
								     "{0}",
								     "{1}" ) );
		contentsMap.put( DS_TS_TRADER_DUPLINKNAME_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_TRADER_DUPLINKNAME_EXCEPTION,
								     "DuplicateLinkName Exception",
								     "{0}",
								     "{1}" ) );

		

		// common


		contentsMap.put( INVALID_RESOLVE_INITREF_NAME,
					  Logger.createLogMessageId( INVALID_RESOLVE_INITREF_NAME,
										    "No initial reference for {2}",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( FILE_NOT_FOUND_EXCEPTION,
					  Logger.createLogMessageId( FILE_NOT_FOUND_EXCEPTION,
										    "File {2} not found",
										    "{0}",
										    "{1}" ) );


		contentsMap.put( NULL_POINTER_EXCEPTION,
					   Logger.createLogMessageId( NULL_POINTER_EXCEPTION,
											"Cannot find {2}: {3}",
											"{0}",
											"{1}" ) );

		contentsMap.put( 	DS_TS_DB_QUERY_EXCEPTION,
						Logger.createLogMessageId( DS_TS_DB_QUERY_EXCEPTION,	
											  "QueryException for {2}: {3}, {4}: {5}. " +
											  "Throwing CORBA.UNKNOWN" ,
											  "{0}",
											  "{1}" ) );
		

		contentsMap.put( UNCAUGHT_EXCEPTION,
					   Logger.createLogMessageId( UNCAUGHT_EXCEPTION,	   
					   "Uncaught Exception",
					   "{0}",
					   "{1}") );

		
		

		contentsMap.put( DS_TS_REGISTER_INVALID_OBJECT_REFERENCE,
					  Logger.createLogMessageId( DS_TS_REGISTER_INVALID_OBJECT_REFERENCE,
					  "The Object Reference Passed is Invalid.",
					  "{0}",
					  "{1}" ) );
					
		contentsMap.put( METHOD_ENTRY_0,
					  Logger.createLogMessageId( METHOD_ENTRY_0,
										  "TRACE Entry",
										    "{0}", 
										    "{1}" ) );

		contentsMap.put( METHOD_ENTRY_1,
					  Logger.createLogMessageId( METHOD_ENTRY_1,
										    "TRACE Entry: Parameters Values ... {2}: {3} ",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( METHOD_ENTRY_2,
					  Logger.createLogMessageId( METHOD_ENTRY_2,
										    "TRACE Entry: Parameters Values ... {2}: {3}, {4}: {5} ",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( METHOD_ENTRY_3,
					  Logger.createLogMessageId( METHOD_ENTRY_3,
										    "TRACE Entry: Parameters Values ... {2}: {3}, {4}: {5}, {6}: {7} ",
										    "{0}",
										    "{1}" ) );
		
		contentsMap.put( METHOD_ENTRY_4,
					  Logger.createLogMessageId( METHOD_ENTRY_4,
										    "TRACE Entry: Parameters Values ... {2}: {3}, {4}: {5}, {6}: {7}, {8}: {9} ",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( METHOD_ENTRY_5,
					  Logger.createLogMessageId( METHOD_ENTRY_5,
										    "TRACE Entry: Parameters Values ... {2}: {3}, {4}: {5}, {6}: {7}, {8}: {9}, {10}: {11} ",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( METHOD_EXIT,
					  Logger.createLogMessageId( METHOD_EXIT,
										  "TRACE Exit",
										    "{0}", 
										    "{1}" ) );


		 contentsMap.put( DS_TS_REGISTER_OFFER_KEY,			   
					   Logger.createLogMessageId( DS_TS_REGISTER_OFFER_KEY,
											"Using {2} as OfferKey for the exported offer",
											"{0}",
											"{1}" ) );


		 contentsMap.put( DS_TS_SERVICE_TYPE_QUERY_EXCEPTION,
					   Logger.createLogMessageId( DS_TS_SERVICE_TYPE_QUERY_EXCEPTION,	
										  "Exception during retrieving ServiceType for  {5}." + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );

		 
		 contentsMap.put( DS_TS_EXPORT_OFFER_QUERY_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_EXPORT_OFFER_QUERY_EXCEPTION,	  
										    "Exception during exporting offer for ServiceType {5}." + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );
		 


		 		// LookupImpl
					  
		contentsMap.put( DS_TS_LOOKUP_QUERY_RESULT,
					  Logger.createLogMessageId( DS_TS_LOOKUP_QUERY_RESULT,
										    "For ServiceTypeName : {3} and  Constraint: {5}, {7, number, integer} " + 
										    "offers fetched from Data-Base",
										    "{0}",
										    "{1}" ) );
		contentsMap.put( DS_TS_LOOKUP_PROPERTYVALUE,
                                        Logger.createLogMessageId( DS_TS_LOOKUP_PROPERTYVALUE,
									"{2}{3}",
                                                                        "{0}",
                                                                        "{1}") );
		contentsMap.put( DS_TS_LOOKUP_FORWARD_QUERY,
					Logger.createLogMessageId( DS_TS_LOOKUP_FORWARD_QUERY,
									"{2}",
									"{0}",
                                                                        "{1}") );
		contentsMap.put( DS_TS_LOOKUP_ILLEGALLINKNAME_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_LOOKUP_ILLEGALLINKNAME_EXCEPTION,
										    "IllegalLinkName Exception",
										    "{0}",
										    "{1}" ) );
		contentsMap.put( DS_TS_LOOKUP_UNKNOWNLINKNAME_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_LOOKUP_UNKNOWNLINKNAME_EXCEPTION,
										    "UnknownLinkName Exception",
										    "{0}",
										    "{1}" ) );
		contentsMap.put( DS_TS_LOOKUP_TIMEOUT_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_LOOKUP_TIMEOUT_EXCEPTION,
										    "Corba Timeout Exception",
										    "{0}",
										    "{1}" ) );
		contentsMap.put( DS_TS_LOOKUP_OBJECTNOTEXISTS_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_LOOKUP_OBJECTNOTEXISTS_EXCEPTION,
										    "Corba Object Not Exist Exception",
										    "{0}",
										    "{1}" ) );
		contentsMap.put( DS_TS_LOOKUP_INFO,
					Logger.createLogMessageId( DS_TS_LOOKUP_INFO,
                                                                        "{2}{3}",
                                                                        "{0}",
                                                                        "{1}") );


		// AdminImpl
		contentsMap.put( DS_TS_ADMIN_PROPERTYVALUE,
					Logger.createLogMessageId( DS_TS_ADMIN_PROPERTYVALUE,
									"{2}{3}",
									"{0}",
									"{1}") );

	        // LinkImpl
		contentsMap.put( DS_TS_LINK_PROPERTYVALUE,
					Logger.createLogMessageId( DS_TS_LINK_PROPERTYVALUE,
									"{2}{3}",
                                                                        "{0}",
                                                                        "{1}") );
		contentsMap.put( DS_TS_LINK_REMOVE_FAILED,
                                        Logger.createLogMessageId( DS_TS_LINK_REMOVE_FAILED,
                                                                        "{2}{3}",
                                                                        "{0}",
                                                                        "{1}") );
		contentsMap.put( DS_TS_LINK_INFO,
					Logger.createLogMessageId( DS_TS_LINK_REMOVE_FAILED,
                                                                        "{2}{3}",
                                                                        "{0}",
                                                                        "{1}") );
		
	        // RegisterImpl
		contentsMap.put( DS_TS_REGISTER_DUPLICATE_PROPERTIES,
					  Logger.createLogMessageId( DS_TS_REGISTER_DUPLICATE_PROPERTIES,
										    "STRICT_SPEC property set to {2}",
										    "{0}",
										    "{1}" ) );

		
		// persist

		
		contentsMap.put( DS_TS_PERSIST_NO_MATCHING_TCKIND,
					  Logger.createLogMessageId( DS_TS_PERSIST_NO_MATCHING_TCKIND,
										    "Unknown TCKind {2}",
										    "{0}",
										    "{1}" ) );
		
		contentsMap.put( DS_TS_PERSIST_NO_SUCH_FIELD_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_PERSIST_NO_SUCH_FIELD_EXCEPTION,   
										    "No such filed in the DB table",
										    "{0}",
										    "{1}" ) );
					  



					   //TODO - better description for QueryException			   

		 contentsMap.put( DS_TS_WITHDRAW_QUERY_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_WITHDRAW_QUERY_EXCEPTION,	  
										    "Exception during retrieving entries for {4}: {3}." + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );
					  

		 contentsMap.put( DS_TS_DESCRIBE_QUERY_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_DESCRIBE_QUERY_EXCEPTION,	  
										    "Exception during retrieving entries for {2}: {3} " + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );			   


		 contentsMap.put( DS_TS_MODIFY_QUERY_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_MODIFY_QUERY_EXCEPTION,	  
										    "Exception during retrieving entries for {2}: {3}. " + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );		
		   

		  contentsMap.put( DS_TS_ADD_SERVICE_TYPE_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_ADD_SERVICE_TYPE_EXCEPTION,	  
										    "Exception during adding the service type {3}. " + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );

		  
		   contentsMap.put( DS_TS_REMOVE_SERVICE_TYPE_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_REMOVE_SERVICE_TYPE_EXCEPTION,	  
										    "Exception during removing the service type {3}. " + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );

		   contentsMap.put( DS_TS_GET_ALL_SERVICE_TYPES_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_GET_ALL_SERVICE_TYPES_EXCEPTION,	  
										    "Exception during retrieving all  service types known. " + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );

		   
		   contentsMap.put( DS_TS_GET_SERVICE_TYPE_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_GET_SERVICE_TYPE_EXCEPTION,	  
										    "Exception during retrieving the service type {3}. " + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );
		   
		   contentsMap.put( DS_TS_UNMASK_SERVICE_TYPE_EXCEPTION,
						Logger.createLogMessageId( DS_TS_UNMASK_SERVICE_TYPE_EXCEPTION,	  
										    "Exception during unmasking the service type {3}. " + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );

		    contentsMap.put( DS_TS_MASK_SERVICE_TYPE_EXCEPTION,
						Logger.createLogMessageId( DS_TS_MASK_SERVICE_TYPE_EXCEPTION,	  
										    "Exception during masking the service type {3}. " + 
										    " Throwing CORBA.UNKNOWN.",
										    "{0}",
										    "{1}" ) );

		    contentsMap.put( DS_TS_SERVICE_TYPE_EXPORTED,
					   Logger.createLogMessageId( DS_TS_SERVICE_TYPE_EXPORTED,
											"Operation completed succesfully for ServiceTypeName: {2} " +
											"and offer (ORBName_ORBHost_ORB_Port_InterfaceName): {3}", 
											"{0}",
											"{1}" ) );

					   			
		  
		contentsMap.put( DS_TS_INVALID_OFFER_ID,
					  Logger.createLogMessageId( DS_TS_INVALID_OFFER_ID,
										    "Service offer: ({3}) specified is invalid.",
										    "{0}",
										    "{1}" ) );  


		contentsMap.put( DS_TS_REGISTER_SERVICE_OFFER_WITHDRAWN,
					  Logger.createLogMessageId( DS_TS_REGISTER_SERVICE_OFFER_WITHDRAWN,
										 "Operation completed succesfully for {2}: {3}",  
					                                              "{0}", 
										   "{1}" ) );

		 contentsMap.put( DS_TS_PROPERTY_TYPE_MISMATCH,
					   Logger.createLogMessageId( DS_TS_PROPERTY_TYPE_MISMATCH,
											"Error: The property values for {2} " + 
											"does not match with the one specified: {3} . " +
											"Throwing CORBA.UNKNOWN", 
											"{0}",
											"{1}" ) );

	       contentsMap.put( DS_TS_WITHDRAW_ILLEGAL_OFFER_ID_EXCEPTION,
					    Logger.createLogMessageId( DS_TS_WITHDRAW_ILLEGAL_OFFER_ID_EXCEPTION,
					    "Exception during  withdrawal of  service offer: {0} using constraint: {1} " +
											  " Throwing CORBA.UNKNOWN ",   
											  "({2})",
											  "({3})" ) );



	        contentsMap.put( DS_TS_WITHDRAW_UNKNOWN_OFFER_ID_EXCEPTION,
					    Logger.createLogMessageId( DS_TS_WITHDRAW_UNKNOWN_OFFER_ID_EXCEPTION,
					    "Exception during  withdrawal of  service offer: {0} using constraint: {1} " +
											  " Throwing CORBA.UNKNOWN ",   
											  "({2})",
											  "({3})" ) );			 


		 contentsMap.put( DS_TS_REGISTER_NO_MODIFY_NEEDED,
					   Logger.createLogMessageId( DS_TS_REGISTER_NO_MODIFY_NEEDED, 
											"Delete and Modify lists are of zero length. " +
											"No modification done.",
											"{0}",
											"{1}" ) );


		 contentsMap.put( DS_TS_REGISTER_DESCRIPTION_MODIFIED,
					   Logger.createLogMessageId( DS_TS_REGISTER_DESCRIPTION_MODIFIED,
											"Description is changed for ServiceTypeName: ({3}). ",
											"{0}",
											"{1}" ) );

					   
		contentsMap.put( DS_TS_FATAL_SQL_EXCEPTION,
					  Logger.createLogMessageId( DS_TS_FATAL_SQL_EXCEPTION,
										    "Fatal SQL Error. Exiting",
										    "{0}",
										    "{1}" ) ); 


		contentsMap.put( DS_TS_OFFER_ID_DESCRIPTION_FULL,
					  Logger.createLogMessageId( DS_TS_OFFER_ID_DESCRIPTION_FULL,
										    "Using full IOR for Offer IDs",
										    "{0}",
										    "{1}" ) );

		contentsMap.put( DS_TS_OFFER_ID_DESCRIPTION_DIGESTED,
					  Logger.createLogMessageId( DS_TS_OFFER_ID_DESCRIPTION_DIGESTED,
										    "Using digested IOR for Offer IDs",
										    "{0}",
										    "{1}" ) );



					  // ServiceTypeRepositoryImpl
					  
						 //TODO  common one 
		contentsMap.put( 	DS_TS_DB_QUERY_EXCEPTION,
				Logger.createLogMessageId( DS_TS_DB_QUERY_EXCEPTION,	
									  "DB Exception during method {1}. ({2}) ({3}). " +
									  "Throwing CORBA.UNKNOWN" ,
									  "{0}",
									  "{1}" ) );

						
						
		 contentsMap.put( DS_TS_SERVICE_TYPE_REPOSITORY_OPERATION,
					   Logger.createLogMessageId( DS_TS_SERVICE_TYPE_REPOSITORY_OPERATION,
										   "Operation completed succesfully for {2}: {3}.",
										   "{0}",
										   "{1}" ) );


					   
					   //CorbaPropertyBuilder 
		 contentsMap.put( DS_TS_PROPBUILDER_NAMING_EXCEPTION,
					   Logger.createLogMessageId( DS_TS_PROPBUILDER_NAMING_EXCEPTION,
											"Naming Exception",
											"{0}",
											"{1}" ) );
					   
					   // TODO
		 contentsMap.put( DS_TS_PROPBUILDER_UNCAUGHT_EXCEPTION,
					   Logger.createLogMessageId( DS_TS_PROPBUILDER_UNCAUGHT_EXCEPTION,
											" uncaught exception TODO",
											"{0}",
											"{1}" ) );	

					   // TraderDBUtil
		 		  
		  contentsMap.put( DS_TS_DBUTIL_SERVICE_TYPE_EXISTS_EXCEPTION,
						 Logger.createLogMessageId( DS_TS_DBUTIL_SERVICE_TYPE_EXISTS_EXCEPTION,
											 "Operation failed. ServiceTypeName: {2} already exits.",
											"{0}",
											"{1}" ) );			

		 

		  contentsMap.put( DS_TS_DBUTIL_ADD_SERVICE_TYPE_PROPERTY,
						 Logger.createLogMessageId( DS_TS_DBUTIL_ADD_SERVICE_TYPE_PROPERTY,
											"Adding ServiceTypeName: {2}. Create a new entry for property: {3}",
											"{0}",
											"{1}" ) );


					    //TODO
		contentsMap.put( DS_TS_DBUTIL_VALUE_TYPE_REDEFINITION_EXCEPTION,
						 Logger.createLogMessageId( DS_TS_DBUTIL_VALUE_TYPE_REDEFINITION_EXCEPTION,
											"Operation failed. Throwing ValueTypeRedifnition",
											"{0}",
											"{1}" ) );			    



					  // parser/CompareObject
		 contentsMap.put( DS_TS_PARSER_TYPE_NOT_SUPPORTED,
					   Logger.createLogMessageId(  DS_TS_PARSER_TYPE_NOT_SUPPORTED,
											 "Operation failed. TypeCode {5} not supported",
											 "{0}",
											 "{1}" ) );
					   

		 contentsMap.put( DS_TS_PARSER_NUMBER_FORMAT_EXCEPTION,
					   Logger.createLogMessageId( DS_TS_PARSER_NUMBER_FORMAT_EXCEPTION,
											"TODO -- number format exception",
											"{0}",
											"{1}" ) );


		 // Security

		 contentsMap.put( SEC_SERVER_POA_CREATE_ERROR,
					    Logger.createLogMessageId( SEC_SERVER_POA_CREATE_ERROR,
											 "Could not create POAs, exiting",
											 "{0}",
											 "{1}" ) );

		 contentsMap.put( SEC_SERVER_POA_CREATED,
					    Logger.createLogMessageId( SEC_SERVER_POA_CREATED,
											 "Created {10} POAs with policies {2} : {3}, {4} : {5}, {6} : {7}, {8}, {9}", 
											 "{0}",
											 "{1}" ) );

		  contentsMap.put( SEC_SERVER_SMA_RETRIEVED,
					    Logger.createLogMessageId( SEC_SERVER_SMA_RETRIEVED,		
											 "SMA instance retrieved",
											 "{0}",
											 "{1}" ) );

		 contentsMap.put( SEC_SERVER_MBEAN_RETRIEVED,
					   Logger.createLogMessageId( SEC_SERVER_MBEAN_RETRIEVED,
											"{2} Bean retrieved", 
											"{0}",
											"{1}" ) );

		 contentsMap.put( SEC_SERVER_CORBA_OBJECT_ACTIVATED,		   
					 Logger.createLogMessageId(  SEC_SERVER_CORBA_OBJECT_ACTIVATED,
										    "{2} CORBA Object Activated with POA: {3}",
										    "{0}",
										    "{1}" ) );

		 contentsMap.put( SEC_SERVER_POA_MGR_ACTIVATED,
					   Logger.createLogMessageId( SEC_SERVER_POA_MGR_ACTIVATED,
											"POA Manager activated",
											"{0}",
											"{1}" ) );

		 contentsMap.put( SEC_SERVER_INFE,
					   Logger.createLogMessageId( SEC_SERVER_INFE,
											"Required properties for Security Interceptor are not found, exiting",
											"{0}",
											"{1}" ) );

		 contentsMap.put( SEC_SERVER_TI_STATUS,
					   Logger.createLogMessageId(  SEC_SERVER_TI_STATUS,
											 "Transition Interceptor is {2}",
											 "{0}",
											 "{1}" ) );

		 contentsMap.put( SEC_SERVER_IOR_WRITTEN,
					   Logger.createLogMessageId(  SEC_SERVER_IOR_WRITTEN,
											 "IORs written to file {2}",
											 "{0}",
											 "{1}" ) );

		 contentsMap.put( SEC_SERVER_IORS_EXPORTED,
					   Logger.createLogMessageId(  SEC_SERVER_IORS_EXPORTED,
											 "Security object references are exported to trader",
											 "{0}",
											 "{1}" ) );


		 contentsMap.put( SEC_SERVER_ACCEPTING_REQUESTS,
					   Logger.createLogMessageId(  SEC_SERVER_ACCEPTING_REQUESTS,
											 "Security Server accepting requests",
											 "{0}",
											 "{1}" ) );

		 contentsMap.put( SEC_SERVER_DISCARDING_REQUESTS,
					   Logger.createLogMessageId(  SEC_SERVER_DISCARDING_REQUESTS,
											 "Security Server discarding requests",
											 "{0}",
											 "{1}" ) );
		 
		 
		 contentsMap.put( SEC_SERVER_CAUGHT_SIGNAL,
					   Logger.createLogMessageId(  SEC_SERVER_CAUGHT_SIGNAL,
											 "Caught {2} Signal. Changing POA state to {3}",
											 "{0}",
											 "{1}" ) );
		 
		 contentsMap.put( SEC_SERVER_STATE_CHANGE_EXCEPTION,
					   Logger.createLogMessageId(  SEC_SERVER_STATE_CHANGE_EXCEPTION,
											 "Exception during POA state change",
											 "{0}",
											 "{1}" ) );

		 contentsMap.put( SEC_IMPL_CTR_FAILURE,
					   Logger.createLogMessageId(  SEC_IMPL_CTR_FAILURE,
											 "Exception in ctr. Exiting",
											 "{0}",
											 "{1}" ) );
		 
		  contentsMap.put( SEC_IMPL_SIG_VERIFICATION,
					   Logger.createLogMessageId(  SEC_IMPL_SIG_VERIFICATION,
											 "Signature verification returned {2}",
											 "{0}",
											 "{1}" ) );

		  contentsMap.put( SEC_IMPL_FIND_OBJREF_EXCEPTION,
					    Logger.createLogMessageId(  SEC_IMPL_FIND_OBJREF_EXCEPTION,
											  "Exception during retrieving objref for {2}  from trader",
											 "{0}",
											 "{1}" ) );

		  
		  contentsMap.put( SEC_IMPL_NO_ROLE,
					    Logger.createLogMessageId(  SEC_IMPL_NO_ROLE,
											  "No active roles exist for User ID: {2}",
											 "{0}",
											 "{1}" ) );
		  
		 
		  contentsMap.put( SEC_IMPL_QUERY_EXCEPTION,
					    Logger.createLogMessageId(  SEC_IMPL_QUERY_EXCEPTION,
											  "Exception while retrieving roles for User ID: {2}",
											  "{0}",
											  "{1}" ) );
		  
		  contentsMap.put( SEC_IMPL_FATAL_SQL_EXCEPTION,
					    Logger.createLogMessageId(  SEC_IMPL_FATAL_SQL_EXCEPTION,
											  "Fatal DB exception while retrieving roles for User ID: {2}",
											  "{0}",
											  "{1}" ) );
		  
		  contentsMap.put( SEC_IMPL_NO_CHANNEL,
					    Logger.createLogMessageId(  SEC_IMPL_NO_CHANNEL,
											  "Unable to obtain a reference for channel: {2}",
											  "{0}",
											  "{1}" ) );
		  
		  contentsMap.put( SEC_SQL_NO_SUCH_FIELD,
					    Logger.createLogMessageId(  SEC_SQL_NO_SUCH_FIELD,
											  "Field: name does not exist in DB",
											  "{0}",
											  "{1}" ) );

		  
		  contentsMap.put( SEC_SQL_ILLEGAL_ACCESS,
					    Logger.createLogMessageId(  SEC_SQL_ILLEGAL_ACCESS,
											  "DB exception during update",
											  "{0}",
											  "{1}" ) );

		   contentsMap.put( SEC_SQL_ILLEGAL_ARGUMENT,
					    Logger.createLogMessageId(  SEC_SQL_ILLEGAL_ARGUMENT,
											  "DB exception during update",
											  "{0}",
											  "{1}" ) );

		   contentsMap.put( SEC_SERVER_INVALID_STATE_TRANSITION,
						Logger.createLogMessageId( 	SEC_SERVER_INVALID_STATE_TRANSITION,
												"Invalid POA state transition",
												"{0}",
												"{1}" ) ); 

		   contentsMap.put( SEC_SERVER_INVOCATION_EXCEPTION,
					Logger.createLogMessageId( 	SEC_SERVER_INVOCATION_EXCEPTION,
											"Server Invocation Error ??",
											"{0}",
											"{1}" ) ); 	
		   
		   contentsMap.put( SEC_SERVER_INSTANCE_NOT_FOUND,
					Logger.createLogMessageId( 	SEC_SERVER_INSTANCE_NOT_FOUND,
											"Server Instance Not Found Error ??",
											"{0}",
											"{1}" ) ); 	

		   contentsMap.put( SEC_IMPL_EVENT_STARTED,
					Logger.createLogMessageId( 	SEC_IMPL_EVENT_STARTED,
											"Event Transport started",
											"{0}",
											"{1}" ) ); 	

		   
		     contentsMap.put( SEC_IMPL_SEVERE_ERROR,
					Logger.createLogMessageId( 	SEC_IMPL_SEVERE_ERROR,
											"Severe Error",
											"{0}",
											"{1}" ) ); 	
	
			
			contentsMap.put( SEC_JAVA_EXCEPTION,
						  Logger.createLogMessageId( SEC_JAVA_EXCEPTION,
											    "{2}",
											    "{0}",
											    "{1}" ) ); 		
			
			
			contentsMap.put( SEC_POA_ERROR,
						  Logger.createLogMessageId( SEC_POA_ERROR,
											    "{2}",
											    "{0}",
											    "{1}" ) ); 
			
			contentsMap.put( SEC_LOGGER_EXCEPTION,
						  Logger.createLogMessageId( SEC_LOGGER_EXCEPTION,
											    "Exception while initializing Security Logger",
											    "{0}",
											    "{1}" ) ); 
			
			
			contentsMap.put( SEC_ACL_REQUEST_FOR_ACTION,
						  Logger.createLogMessageId( SEC_ACL_REQUEST_FOR_ACTION,
											    "{2}",
											    "{0}",
											    "{1}" ) ); 

			contentsMap.put( SEC_ACL_EC_SETUP,
						  Logger.createLogMessageId( SEC_ACL_EC_SETUP,
											    "{2}",
											    "{0}",
											    "{1}" ) ); 

	
			contentsMap.put( JMS_STATS_TIPC_CLIENT_SUBJ_POLL,
							 Logger.createLogMessageId( JMS_STATS_TIPC_CLIENT_SUBJ_POLL,
														"Sending {0} responses for MON_CLIENT_SUBJECT_POLL_CALL request.",
														"TipcStatsMonitorCb", "process" ) );
			contentsMap.put( JMS_STATS_TIPC_SUBJ_INFO,
							 Logger.createLogMessageId( JMS_STATS_TIPC_SUBJ_INFO,
														"Sending subject({0}) for MON_CLIENT_SUBJECT_POLL_CALL request.",
														"TipcStatsMonitorCb", "process" ) );
			contentsMap.put( JMS_STATS_TIPC_CLIENT_BUFF_POLL,
							 Logger.createLogMessageId( JMS_STATS_TIPC_CLIENT_BUFF_POLL,
														"Response for MON_CLIENT_BUFFER_POLL_CALL currently unavailable.",
														"TipcStatsMonitorCb", "process" ) );
			contentsMap.put( JMS_STATS_TIPC_CLIENT_GEN_POLL,
							 Logger.createLogMessageId( JMS_STATS_TIPC_CLIENT_GEN_POLL,
														"Sending {0} responses for MON_CLIENT_GENERAL_POLL_CALL request.",
														"TipcStatsMonitorCb", "process" ) );
			contentsMap.put( JMS_STATS_TIPC_SERVER_NAMES_POLL,
							 Logger.createLogMessageId( JMS_STATS_TIPC_SERVER_NAMES_POLL,
														"Sending {0} responses for MON_SERVER_NAMES_POLL_CALL request.",
														"TipcStatsMonitorCb", "process" ) );
			contentsMap.put( JMS_STATS_TIPC_SERVER_BUFF_POLL,
							 Logger.createLogMessageId( JMS_STATS_TIPC_SERVER_BUFF_POLL,
														"Sending {0} responses for MON_SERVER_BUFFER_POLL_CALL request.",
														"TipcStatsMonitorCb", "process" ) );
			contentsMap.put( JMS_STATS_TIPC_SERVER_BUFF_INFO,
							 Logger.createLogMessageId( JMS_STATS_TIPC_SERVER_BUFF_INFO,
														"Sending client({0}) for MON_SERVER_BUFFER_POLL_CALL request.",
														"TipcStatsMonitorCb", "process" ) );
	}		   

	public InfraLoggingRb() {

	} // InfraLoggingRb constructor

	/**
	 * Implementation of abstract ResourceBundle.getKeys.
	 *
	 * @return an <code>Enumeration</code> value
	 */
	public Enumeration getKeys() {
		return new KeysEnumerator( contentsMap.keySet().iterator() );
	}

	/**
	 * Implementation of abstract ResourceBundle.handleGetObject.
	 *
	 * @param key a <code>String</code> value
	 * @return an <code>Object</code> value
	 */
	protected Object handleGetObject( String key ) {
		return contentsMap.get( key );
	}


	class KeysEnumerator implements Enumeration {
		private Iterator iter;
		public KeysEnumerator( Iterator iter ) {
			this.iter = iter;
		}

		public boolean hasMoreElements() {
			return iter.hasNext();
		}

		public Object nextElement() {
			return iter.next();
		}
	}

} // InfraLoggingRb
