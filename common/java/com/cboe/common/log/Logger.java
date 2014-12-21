package com.cboe.common.log;

import java.util.ResourceBundle;

public class Logger {
	static LogHandler handler;

	public static final int OFF = -2;
	public static final int ALL = -1;
	public static final int TRACE = 0;
	public static final int DEBUG = 1;
	public static final int SYSNOTIFY = 2;
	public static final int SYSWARN = 3;
	public static final int SYSALARM = 4;
	public static final int STATS = 5;

	static {
		String handlerName = "NOT SET";
		try {
			handlerName = System.getProperty("ORB.LogHandler", "com.cboe.common.log.JavaLoggingHandler");
			Class c = Class.forName(handlerName);
			java.lang.Object o = c.newInstance();
			handler = (LogHandler)o;
		}
		catch (Throwable t) {
			handler = new LocalHandler();
			handler.sysAlarm("ERROR: Cannot load LogHandler class: '"+handlerName+"'.  Using default handler", t);
		}
	}
      

	private Logger() {
	}

	public static boolean isLoggable( ResourceBundle rb, String logIdName, int logType ) {
		return handler.isLoggable( rb, logIdName, logType );
	}

	public static String whatsLoggable( ResourceBundle rb, String logIdName ) {
		return handler.whatsLoggable( rb, logIdName );
	}

	public static void setLevel( ResourceBundle rb, String logIdName, int logType ) {
		handler.setLevel( rb, logIdName, logType );
	}

	public static String getDefaultLoggerName() {
		return handler.getDefaultLoggerName();
	}

	public static LogMessageId createLogMessageId( String logIdName, String msgText ) {
		return handler.createLogMessageId( logIdName, msgText );
	}

	public static LogMessageId createLogMessageId( String logIdName, String msgText, String className, String methodName ) {
		return handler.createLogMessageId( logIdName, msgText, className, methodName );
	}

	public static void traceEntry( ResourceBundle rb, String logIdName, Object[] params ) {
		handler.traceEntry( rb, logIdName, params );
	}

	public static void traceExit( ResourceBundle rb, String logIdName, Object[] params ) {
		handler.traceExit( rb, logIdName, params );
	}

	public static void debug( ResourceBundle rb, String logIdName, Object[] params ) {
		handler.debug( rb, logIdName, params );
	}

	public static void stats( ResourceBundle rb, String logIdName, Object[] params ) {
		handler.stats( rb, logIdName, params );
	}

	public static void sysNotify( ResourceBundle rb, String logIdName, Object[] params ) {
		handler.sysNotify( rb, logIdName, params );
	}

	public static void sysWarn( ResourceBundle rb, String logIdName, Object[] params ) {
		handler.sysWarn( rb, logIdName, params );
	}

	public static void sysAlarm( ResourceBundle rb, String logIdName, Object[] params ) {
		handler.sysAlarm( rb, logIdName, params );
	}

	public static void debug( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
		handler.debug( rb, logIdName, params, t );
	}

	public static void sysNotify( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
		handler.sysNotify( rb, logIdName, params, t );
	}

	public static void sysWarn( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
		handler.sysWarn( rb, logIdName, params, t );
	}

	public static void sysAlarm( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
		handler.sysAlarm( rb, logIdName, params, t );
	}



	public static void traceEntry( LogMessageId id, Object[] params ) {
		handler.traceEntry( id, params );
	}

	public static void traceExit( LogMessageId id, Object[] params ) {
		handler.traceExit( id, params );
	}

	public static void debug( LogMessageId id, Object[] params ) {
		handler.debug( id, params );
	}

	public static void stats( LogMessageId id, Object[] params ) {
		handler.stats( id, params );
	}

	public static void sysNotify( LogMessageId id, Object[] params ) {
		handler.sysNotify( id, params );
	}

	public static void sysWarn( LogMessageId id, Object[] params ) {
		handler.sysWarn( id, params );
	}

	public static void sysAlarm( LogMessageId id, Object[] params ) {
		handler.sysAlarm( id, params );
	}

	public static void debug( LogMessageId id, Object[] params, Throwable t ) {
		handler.debug( id, params, t );
	}

	public static void sysNotify( LogMessageId id, Object[] params, Throwable t ) {
		handler.sysNotify( id, params, t );
	}

	public static void sysWarn( LogMessageId id, Object[] params, Throwable t ) {
		handler.sysWarn( id, params, t );
	}

	public static void sysAlarm( LogMessageId id, Object[] params, Throwable t ) {
		handler.sysAlarm( id, params, t );
	}


	// Older methods.

	public static boolean isLoggable ( int logType ) {
	    return handler.isLoggable( logType );
	}
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public static void debug(String text) {
		handler.debug(text);
	}
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public static void sysNotify(String text) {
		handler.sysNotify(text);
	}
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public static void sysWarn(String text) {
		handler.sysWarn(text);
	}
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public static void sysAlarm(String text) {
		handler.sysAlarm(text);
	}
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public static void info(String text) {
		handler.info(text);
	}

	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public static void stats(String text) {
		handler.stats(text);
	}


	/**
	 *
	 * @param text a <code>String</code> value
	 * @param t a <code>Throwable</code> value
	 */
	public static void debug(String text, Throwable t) {
		handler.debug(text,t);
	}
	/**
	 *
	 * @param text a <code>String</code> value
	 * @param t a <code>Throwable</code> value
	 */
	public static void sysNotify(String text, Throwable t) {
		handler.sysNotify(text,t);
	}
	/**
	 *
	 * @param text a <code>String</code> value
	 * @param t a <code>Throwable</code> value
	 */
	public static void sysWarn(String text, Throwable t) {
		handler.sysWarn(text,t);
	}
	/**
	 *
	 * @param text a <code>String</code> value
	 * @param t a <code>Throwable</code> value
	 */
	public static void sysAlarm(String text, Throwable t) {
		handler.sysAlarm(text,t);
	}
	/**
	 *
	 * @param text a <code>String</code> value
	 * @param t a <code>Throwable</code> value
	 */
	public static void info(String text, Throwable t) {
		handler.info(text,t);
	}

}
