package com.cboe.common.log;

/**
 * Describe interface <code>LogHandler</code> here.
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
import java.util.ResourceBundle;
public interface LogHandler {

	public String getDefaultLoggerName();
	public boolean isLoggable( ResourceBundle rb, String logIdName, int logType );
	public String whatsLoggable( ResourceBundle rb, String logIdName );
	public void setLevel( ResourceBundle rb, String logIdName, int logType );
	public LogMessageId createLogMessageId( String logIdName, String msgText );
	public LogMessageId createLogMessageId( String logIdName, String msgText, String className, String methodName );

	public void traceEntry( ResourceBundle rb, String logIdName, Object[] params );
	public void traceExit( ResourceBundle rb, String logIdName, Object[] params );
	public void debug( ResourceBundle rb, String logIdName, Object[] params );
	public void stats( ResourceBundle rb, String logIdName, Object[] params );
	public void sysNotify( ResourceBundle rb, String logIdName, Object[] params );
	public void sysWarn( ResourceBundle rb, String logIdName, Object[] params );
	public void sysAlarm( ResourceBundle rb, String logIdName, Object[] params );

	public void debug( ResourceBundle rb, String logIdName, Object[] params, Throwable t );
	public void sysNotify( ResourceBundle rb, String logIdName, Object[] params, Throwable t );
	public void sysWarn( ResourceBundle rb, String logIdName, Object[] params, Throwable t );
	public void sysAlarm( ResourceBundle rb, String logIdName, Object[] params, Throwable t );


	public void traceEntry( LogMessageId id, Object[] params );
	public void traceExit( LogMessageId id, Object[] params );
	public void debug( LogMessageId id, Object[] params );
	public void stats( LogMessageId id, Object[] params );
	public void sysNotify( LogMessageId id, Object[] params );
	public void sysWarn( LogMessageId id, Object[] params );
	public void sysAlarm( LogMessageId id, Object[] params );

	public void debug( LogMessageId id, Object[] params, Throwable t );
	public void sysNotify( LogMessageId id, Object[] params, Throwable t );
	public void sysWarn( LogMessageId id, Object[] params, Throwable t );
	public void sysAlarm( LogMessageId id, Object[] params, Throwable t );



	// Older Methods
	public boolean isLoggable (int logType);
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void debug(String text);
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void sysNotify(String text);
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void sysWarn(String text);
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void sysAlarm(String text);
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void info(String text);
	/**
	 *
	 * @param text a <code>String</code> value
	 */
	public void stats(String text);

	/**
	 *
	 * @param text a <code>String</code> value
	 * @param t a <code>Throwable</code> value
	 */
	public void debug(String text, Throwable t);
	/**
	 *
	 * @param text a <code>String</code> value
	 * @param t a <code>Throwable</code> value
	 */
	public void sysNotify(String text, Throwable t);
	/**
	 *
	 * @param text a <code>String</code> value
	 * @param t a <code>Throwable</code> value
	 */
	public void sysWarn(String text, Throwable t);
	/**
	 *
	 * @param text a <code>String</code> value
	 * @param t a <code>Throwable</code> value
	 */
	public void sysAlarm(String text, Throwable t);
	/**
	 *
	 * @param text a <code>String</code> value
	 * @param t a <code>Throwable</code> value
	 */
	public void info(String text, Throwable t);

}
