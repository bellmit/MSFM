package com.cboe.common.log;

/**
 * LocalLogMessageId.java
 *
 *
 * Created: Wed May 28 15:04:20 2003
 *
 * @author <a href="mailto:yaussy@devinfra2">Kevin Yaussy</a>
 * @version 1.0
 */
class LocalLogMessageId implements LogMessageId {
	private String name;
	private String msgText;

	public LocalLogMessageId( String name, String msgText, String className, String methodName ) {
		this.name = name;
		this.msgText = msgText;
	} // LocalLogMessageId constructor

	public String getName() {
		return name;
	}

	public String getMsgText() {
		return msgText;
	}

	public void setLevel( int logType ) {
	}

	public boolean isLoggable( int logType ) {
		return true;
	}

} // LocalLogMessageId
