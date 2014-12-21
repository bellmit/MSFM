package com.cboe.common.log;

/**
 * LogMessageId.java
 *
 *
 * Created: Thu May 22 15:01:20 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */

public interface LogMessageId {

	public String getName();

	public String getMsgText();

	public void setLevel( int logType );

	public boolean isLoggable( int logType );

} // LogMessageId
