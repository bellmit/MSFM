
package com.cboe.infrastructureUtility;


public class LogSurrogate {

	public static void setDebugOn( boolean s )
	{
		LoggingHelper.setDebugOn(s);
	}
	public static void setTraceOn( boolean s )
	{
		LoggingHelper.setTraceOn(s);
	}
	public static void logDebugMsg(String source, String msg )
	{
		LoggingHelper.logDebugMessage(source,msg);
	}
	public static void logCriticalMsg(String source, String msg )
	{
		LoggingHelper.logCriticalError(source,msg);
	}
}
