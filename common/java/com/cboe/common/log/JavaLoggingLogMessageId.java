package com.cboe.common.log;

/**
 * JavaLoggingLogMessageId.java
 *
 *
 * Created: Fri May 30 09:35:22 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
import java.util.logging.*;
import java.text.MessageFormat;

class JavaLoggingLogMessageId implements LogMessageId {
	private String name;
	private String msgText;
	private String className;
	private String methodName;
	private java.util.logging.Logger jLogger;

	public JavaLoggingLogMessageId( String name, String msgText, String className, String methodName ) {
		this.name = name;
		this.msgText = msgText;
		this.className = className;
		this.methodName = methodName;

		jLogger = java.util.logging.Logger.getLogger( name );

	} // JavaLoggingLogMessageId constructor

	public String getName() {
		return name;
	}

	public String getMsgText() {
		return msgText;
	}

	public void setLevel( int newLogType ) {
		jLogger.setLevel( mapToLogLevel( newLogType ) );
	}

	protected void overrideLowestLevel( int lowLevelOverrideType ) {
		// Take the lowest Level setting.
		if ( jLogger.getLevel() == null ||
			mapToLogLevel( lowLevelOverrideType ).intValue() < jLogger.getLevel().intValue() ) {
			jLogger.setLevel( mapToLogLevel( lowLevelOverrideType ) );
		}
	}

	public boolean isLoggable( int logType ) {
		return jLogger.isLoggable( mapToLogLevel( logType ) );
	}

	public String whatsLoggable() {
		String levels = "";
		if ( isLoggable( Logger.TRACE ) ) levels += "TRACE,";
		if ( isLoggable( Logger.DEBUG ) ) levels += "DEBUG,";
		if ( isLoggable( Logger.SYSNOTIFY ) ) levels += "SYSNOTIFY,";
		if ( isLoggable( Logger.SYSWARN ) ) levels += "SYSWARN,";
		if ( isLoggable( Logger.SYSALARM ) ) levels += "SYSALARM,";
		if ( isLoggable( Logger.STATS ) ) levels += "STATS,";

		if ( levels.endsWith( "," ) ) {
			levels = levels.substring( 0, levels.length()-1 );
		}

		return levels;
	}

	public void log( int logType, Object[] params ) {
		jLogger.logp( mapToLogLevel( logType ), className, methodName, msgText, params );
	}

	public void log( int logType, Object[] params, Throwable t ) {
		// For some reason, Java Logging API does not give a log method
		// which takes parameters for logging Throwables.  So, run
		// msgText through a formatter.
		try {
			jLogger.logp( mapToLogLevel( logType ), className, methodName,
					MessageFormat.format( msgText, params ), t );
		} catch( Exception e ) {
			// Log without MessageFormat, as this is the likely reason for an exception here.
			jLogger.logp( mapToLogLevel( logType ), className, methodName,
					msgText, t );
		}
	}

	public void log( int logType, String msgTextOverride, Object[] params ) {
		jLogger.logp( mapToLogLevel( logType ), className, methodName, msgTextOverride, params );
	}

	public void log( int logType, String msgTextOverride, Object[] params, Throwable t ) {
		// For some reason, Java Logging API does not give a log method
		// which takes parameters for logging Throwables.  So, run
		// msgTextOverride through a formatter.
		try {
			jLogger.logp( mapToLogLevel( logType ), className, methodName,
					MessageFormat.format( msgTextOverride, params ), t );
		} catch( Exception e ) {
			// Log without MessageFormat, as this is the likely reason for an exception here.
			jLogger.logp( mapToLogLevel( logType ), className, methodName,
					msgTextOverride, t );
		}
	}

	private Level mapToLogLevel( int logType ) {
		switch( logType ) {

		case Logger.TRACE: return Level.FINEST;
		case Logger.DEBUG: return Level.FINER;
		case Logger.SYSNOTIFY: return Level.INFO;
		case Logger.SYSWARN: return Level.WARNING;
		case Logger.SYSALARM: return Level.SEVERE;
		case Logger.STATS: return LWLLevel.STATS;
		case Logger.ALL: return Level.ALL;

		default: return Level.OFF;

		}
	}

} // JavaLoggingLogMessageId
