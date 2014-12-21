package com.cboe.common.log;

/**
 * JavaLoggingHandler.java
 *
 * This class implements the LogHandler interface.  It is implemented as a layer
 * over JavaLogging.  As some code will be moved to use the new interface methods,
 * other code will be left alone, much of it using this logging facility.  A default
 * LogMessageId (logger) is created to handle the deprecated methods on the interface.
 * Here are the mappings between this LightWeightLogger (LWL) and JavaLogging severity
 * levels:
 *
 * debug=FINEST
 * sysNotify=INFO
 * sysAlarm=SEVERE
 * info=INFO
 *
 * Additionally, I will sub-class Level to add an additional level, STATS.  This will be
 * one greater than INFO so that the messages will be output if INFO is the output level
 * (which is the default).
 * stats=STATS
 *
 * Created: Mon May 12 07:34:55 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
import java.util.ResourceBundle;

public class JavaLoggingHandler implements LogHandler {

	private JavaLoggingLogMessageId defaultLogMsgId;

	public JavaLoggingHandler() {
		// Get a LogMessageId for use with older methods.
		defaultLogMsgId = new JavaLoggingLogMessageId( "LWL", "", "", "" );

		// Let the logging config file handle default configuration, including
		// Level settings.  However, to get some backward-compatibility, play
		// some games with old command-line properties to turn on additional
		// levels in the jLogger.
		int legacyLevelOverrideType = Logger.SYSNOTIFY;
		if ( System.getProperty("LocalLog.OFF") != null ) {
			legacyLevelOverrideType = Logger.OFF;
		} else if ( System.getProperty("LocalLog.ALL") != null ) {
			legacyLevelOverrideType = Logger.ALL;
		} else if ( System.getProperty("LocalLog.Debug") != null ) {
			legacyLevelOverrideType = Logger.DEBUG;
		} else if ( System.getProperty("LocalLog.SysNotify") != null ||
				  System.getProperty("LocalLog.Info") != null ) {
			legacyLevelOverrideType = Logger.SYSNOTIFY;
		} else if ( System.getProperty("LocalLog.SysAlarm") != null ) {
			legacyLevelOverrideType = Logger.SYSALARM;
		}
		defaultLogMsgId.overrideLowestLevel( legacyLevelOverrideType );
	} // JavaLoggingHandler constructor

	public String getDefaultLoggerName() {
		return "LWL";
	}

	public boolean isLoggable( ResourceBundle rb, String logIdName, int logType ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			return id.isLoggable( logType );
		} 
        return false;
	}

	public String whatsLoggable( ResourceBundle rb, String logIdName ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			return id.whatsLoggable();
		} 
        return "UNKNOWN";
	}

	public void setLevel( ResourceBundle rb, String logIdName, int logType ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.setLevel( logType );
		} else {
			return;
		}
	}

	public LogMessageId createLogMessageId( String logIdName, String msgText ) {
		return new JavaLoggingLogMessageId( logIdName, msgText, null, null );
	}

	public LogMessageId createLogMessageId( String logIdName, String msgText, String className, String methodName ) {
		return new JavaLoggingLogMessageId( logIdName, msgText, className, methodName );
	}

	public void traceEntry( ResourceBundle rb, String logIdName, Object[] params ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.TRACE, params );
		}
	}

	public void traceExit( ResourceBundle rb, String logIdName, Object[] params ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.TRACE, params );
		}
	}

	public void debug( ResourceBundle rb, String logIdName, Object[] params ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.DEBUG, params );
		}
	}

	public void stats( ResourceBundle rb, String logIdName, Object[] params ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.STATS, params );
		}
	}

	public void sysNotify( ResourceBundle rb, String logIdName, Object[] params ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.SYSNOTIFY, params );
		}
	}

	public void sysWarn( ResourceBundle rb, String logIdName, Object[] params ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.SYSWARN, params );
		}
	}

	public void sysAlarm( ResourceBundle rb, String logIdName, Object[] params ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.SYSALARM, params );
		}
	}

	public void debug( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.DEBUG, params, t );
		}
	}

	public void sysNotify( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.SYSNOTIFY, params, t );
		}
	}

	public void sysWarn( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.SYSWARN, params, t );
		}
	}

	public void sysAlarm( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
		JavaLoggingLogMessageId id = getLogger( rb, logIdName );
		if ( id != null ) {
			id.log( Logger.SYSALARM, params, t );
		}
	}



	public void traceEntry( LogMessageId id, Object[] params ) {
		((JavaLoggingLogMessageId)id).log( Logger.TRACE, params );
	}

	public void traceExit( LogMessageId id, Object[] params ) {
		((JavaLoggingLogMessageId)id).log( Logger.TRACE, params );
	}

	public void debug( LogMessageId id, Object[] params ) {
		((JavaLoggingLogMessageId)id).log( Logger.DEBUG, params );
	}

	public void stats( LogMessageId id, Object[] params ) {
		((JavaLoggingLogMessageId)id).log( Logger.STATS, params );
	}

	public void sysNotify( LogMessageId id, Object[] params ) {
		((JavaLoggingLogMessageId)id).log( Logger.SYSNOTIFY, params );
	}

	public void sysWarn( LogMessageId id, Object[] params ) {
		((JavaLoggingLogMessageId)id).log( Logger.SYSWARN, params );
	}

	public void sysAlarm( LogMessageId id, Object[] params ) {
		((JavaLoggingLogMessageId)id).log( Logger.SYSALARM, params );
	}

	public void debug( LogMessageId id, Object[] params, Throwable t ) {
		((JavaLoggingLogMessageId)id).log( Logger.DEBUG, params, t );
	}

	public void sysNotify( LogMessageId id, Object[] params, Throwable t ) {
		((JavaLoggingLogMessageId)id).log( Logger.SYSNOTIFY, params, t );
	}

	public void sysWarn( LogMessageId id, Object[] params, Throwable t ) {
		((JavaLoggingLogMessageId)id).log( Logger.SYSWARN, params, t );
	}

	public void sysAlarm( LogMessageId id, Object[] params, Throwable t ) {
		((JavaLoggingLogMessageId)id).log( Logger.SYSALARM, params, t );
	}



	private JavaLoggingLogMessageId getLogger( ResourceBundle rb, String logIdName ) {
		try {
			return (JavaLoggingLogMessageId)rb.getObject( logIdName );
		} catch( Exception e ) {
			defaultLogMsgId.log( Logger.SYSWARN,
							 "JavaLoggingHandler.getLogger: Unable to get LogMessageId({0}).",
							 new Object[] {logIdName}, e );
			return null;
		}
	}

	// Older Methods.

	public boolean isLoggable(int logType ) {
	    return defaultLogMsgId.isLoggable(logType);
	}
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void debug(String text) {
		defaultLogMsgId.log( Logger.DEBUG, text, null );
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void sysNotify(String text) {
		defaultLogMsgId.log( Logger.SYSNOTIFY, text, null );
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void sysWarn(String text) {
		defaultLogMsgId.log( Logger.SYSWARN, text, null );
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void sysAlarm(String text) {
		defaultLogMsgId.log( Logger.SYSALARM, text, null );
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void info(String text) {
		defaultLogMsgId.log( Logger.SYSNOTIFY, text, null );
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void stats(String text) {
		defaultLogMsgId.log( Logger.STATS, text, null );
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void debug(String text, Throwable t) {
		defaultLogMsgId.log( Logger.DEBUG, text, null, t );
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void sysNotify(String text, Throwable t) {
		defaultLogMsgId.log( Logger.SYSNOTIFY, text, null, t );
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void sysWarn(String text, Throwable t) {
		defaultLogMsgId.log( Logger.SYSWARN, text, null, t );
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void sysAlarm(String text, Throwable t) {
		defaultLogMsgId.log( Logger.SYSALARM, text, null, t );
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void info(String text, Throwable t) {
		defaultLogMsgId.log( Logger.SYSNOTIFY, text, null, t );
	}

} // JavaLoggingHandler
