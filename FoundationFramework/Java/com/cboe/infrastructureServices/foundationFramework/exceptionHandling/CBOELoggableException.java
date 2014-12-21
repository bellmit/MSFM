package com.cboe.infrastructureServices.foundationFramework.exceptionHandling;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.loggingService.*;
/**
 * This will assume there exists a Log Service component of the name "Exception".
 * Alternately, the CBOELoggableException can be created with the LogService as a parameter.
 * This will cause the exception to use the same LogService component name.
 * 
 * @author Dave Hoag
 * @version 2.0
 */
public class CBOELoggableException extends Exception 
{
	// The name of the component the logging service will use
	final static String loggableExceptionComponent = "Exception";
	Exception exception;
    /**
       Exceptions with priorities greater than or equal to this threshold will be logged as debug messages.
     */
    private static MsgPriority loggingPriorityThreshold = MsgPriority.low;
    /**
       Exceptions with priorities greater than or equal to this will be logged as system alarms.
     */
    private static MsgPriority alarmPriorityThreshold = MsgPriority.high;
	/**
	 */
	public Exception getException()
	{
		return exception;
	}
	/**
	 */
	public CBOELoggableException(Exception error, MsgPriority priority)
	{
		super(error.toString());
		LogService defaultService = FoundationFramework.getInstance().getLogService(loggableExceptionComponent);
        MsgCategory cat = getCategory(priority);
        if(cat != null)
        {
			defaultService.log(priority, cat, error.toString(), error);
        }
		exception = error;
	}
	/**
	 */
	public CBOELoggableException(String message, MsgPriority priority)
	{
		super(message);
		logMessage(message, priority, FoundationFramework.getInstance().getLogService(loggableExceptionComponent));
	}
	/**
	 */
	public CBOELoggableException(String message, MsgPriority priority, LogService service)
	{
		super(message);
		logMessage(message, priority, service);
	}
	/**
	 */
	protected MsgCategory getCategory(MsgPriority priority)
	{
        // Log the exception for debugging and / or system alarm depending on the
        // priority thresholds.
        if( priority.value <=  loggingPriorityThreshold.value  ) 
        {
            return MsgCategory.debug;
        }
        if( priority.value <=  alarmPriorityThreshold.value  )
        {
            return MsgCategory.systemAlarm;
        }
        return null;
	}
	/**
	 * Check the priority and log it as a debug, systemAlarm, or not at all.
	 * @param message A text message to log. Hopefully something that will aid in resolving any problems.
	 * @param priority The percieved priority of the exception.
	 * @param service The target LogService facade to where the message should go.
	 */
	protected void logMessage(String message, MsgPriority priority, LogService service)
	{
        // Log the exception parameters to accompany the log message.
        MsgParameter[] parameters = MsgParameter.fromException( this );

        // Log the exception for debugging and / or system alarm depending on the
        // priority thresholds.
        MsgCategory cat = getCategory(priority);
        if(cat != null)
        {
            service.log( priority, cat, "", message, parameters );
        }
	}
    /**
       @roseuid 366822E90053
     */
    public static void setLoggingPriorityThreshold(MsgPriority priority) 
    {
        if( priority == null )
        {
            throw new IllegalArgumentException( "Message priority is null." );
        }

        loggingPriorityThreshold = priority;
    }
    /**
       @roseuid 366823010256
     */
    public static MsgPriority getLoggingPriorityThreshold() 
    {
        return loggingPriorityThreshold;
    }
    /**
       @roseuid 3668231200CA
     */
    public static void setAlarmPriorityThreshold(MsgPriority priority) 
    {
        if( priority == null )
        {
            throw new IllegalArgumentException( "Message priority is null." );
        }

        alarmPriorityThreshold = priority;
    }
    /**
       @roseuid 36682329009B
     */
    public static MsgPriority getAlarmPriorityThreshold()
    {
        return alarmPriorityThreshold;
    }
}
