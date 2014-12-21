package com.cboe.infrastructureServices.loggingService;

import java.util.Calendar;

import com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType;
import com.cboe.loggingService.Message;
/**
 * The default implementation of the LoggingService.
 * @version 3.1 Support Increment3 infrastructure.
 */
public class LogServiceNullImpl extends LogServiceBaseImpl
{
	/**
	 * Does this class use the actual logging service code? 
	 */
	protected boolean fullService() { return false; }
	/** */
	public String getComponentName()
	{
		return "NullImpl";
	}

	public void log( Message msg ) {
	}

	public void clear( Message msg ) {
	}

    /**
       Log a non-standard message regarding an exception.
       @see MsgPriority
       @see MsgCategory
       @roseuid 3668038102E7
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String message, Throwable exception)
    {
    }
    /**
       Log a standard message regarding an exception.
       @roseuid 366810050186
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType, Throwable exception)
    {
	}
    /**
       Log a standard message regarding the occurrence of an event with a date / time stamp. To get a date/time stamp for a particular event, call the java.util.Calendar.getInstance() method when and where the event occurs.
       @see Calendar
       @roseuid 3668421D030D
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType, Calendar dateTimeStamp)
    {
    }
    /**
       Log a non-standard message regarding the occurrence of an event with a date / time stamp. To get a date/time stamp for a particular event, call the java.util.Calendar.getInstance() method when and where the event occurs.
       @see Calendar
       @roseuid 366842DE00F7
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String message, Calendar dateTimeStamp)
    {
    }

    public void log(MsgPriority priority, MsgCategory category, String messageString, MsgParameter[] parameters)
    {
    }
    /**
       Log a non-standard message with no parameters.
       @see MsgPriority
       @see MsgCategory
       @roseuid 365B2D7A0276
     */
    public void log(MsgPriority priority, MsgCategory category, String messageText)
    {
    }
    
    /**
       Log a standard message with no parameters.
       @see MsgPriority
       @see MsgCategory
       @see StdMsgType
       @roseuid 365B2E090311
     */
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType)
    {
    }
    /**
       Log a standard message with parameters.
       @see MsgPriority
       @see MsgCategory
       @see StdMsgType
       @see MsgParameter
       @roseuid 365B2F890255
     */
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, MsgParameter[] parameters)
    {
    }
	/**
       Log a non-standard message regarding an exception.
       @see MsgPriority
       @see MsgCategory
       @roseuid 3668038102E7
     */
    public void log(MsgPriority priority, MsgCategory category, String message, Throwable exception)
    {
    }
    /**
       Log a standard message regarding an exception.
       @roseuid 366810050186
     */
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, Throwable exception)
    {
    }
    /**
       Log a standard message regarding the occurrence of an event with a date / time stamp. To get a date/time stamp for a particular event, call the java.util.Calendar.getInstance() method when and where the event occurs.
       @see Calendar
       @roseuid 3668421D030D
     */
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, Calendar dateTimeStamp)
    {
    }
    /**
       Log a non-standard message regarding the occurrence of an event with a date / time stamp. To get a date/time stamp for a particular event, call the java.util.Calendar.getInstance() method when and where the event occurs.
       @see Calendar
       @roseuid 366842DE00F7
     */
    public void log(MsgPriority priority, MsgCategory category, String message, Calendar dateTimeStamp)
    {
    }
    /**
       Log a standard message with parameters from a finer granularity component. Generally, the origin of a log message is set to the logging component name specified when an instance of the logging service is retrieved. This log method allows a finer granularity component name to be appended to the logging component name, separated by a ".",  for this log message only.
       @see LoggingService#getInstance
       @roseuid 3675760F000C
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType, MsgParameter[] parameters)
    {
    }
    /**
       Log a non-standard message with parameters from a finer granularity component. Generally, the origin of a log message is set to the logging component name specified when an instance of the logging service is retrieved. This log method allows a finer granularity component name to be appended to the logging component name, separated by a ".",  for this log message only.
       @see LoggingService#getInstance
       @roseuid 367578250000
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String messageText, MsgParameter[] parameters)
    {
    }
    /**
     * Log a standard message from a finer granularity component. Generally, the origin of a log message is set 
     * to the logging component name specified when an instance of the logging service is retrieved. This log method
     * allows a finer granularity component name to be appended to the logging component name, separated by a ".",  
     * for this log message only.
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType)
    {
    }
    /**
     * Log a non-standard message from a finer granularity component. Generally, the origin of a log message is set 
     * to the logging component name specified when an instance of the logging service is retrieved. This log method
     * allows a finer granularity component name to be appended to the logging component name, separated by a ".", 
     * for this log message only.
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String messageText )
    {
    }
    public void log( String messageText )
    {
    }
}
