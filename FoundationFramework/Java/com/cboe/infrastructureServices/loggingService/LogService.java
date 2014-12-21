package com.cboe.infrastructureServices.loggingService;
import java.util.Calendar;

import com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType;
import com.cboe.loggingService.Message;
/**
 * The facade interface that the actual implmentation must implement.
 * @version 3.1
 */
public interface LogService 
{
 
	/**
	 * Log method for a previously created Message object (direct from
	 * LoggingService).
	 *
	 * @param msg a <code>Message</code> value
	 */
	public void log( Message msg );

	/**
	 * Clear method for a previously created Message object (direct from
	 * LoggingService).
	 *
	 * @param msg a <code>Message</code> value
	 */
	public void clear( Message msg );

    /**
       Log a non-standard message regarding an exception.
       @see MsgPriority
       @see MsgCategory
       @roseuid 3668038102E7
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String message, Throwable exception);
    
    /**
       Log a standard message regarding an exception.
       @roseuid 366810050186
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType, Throwable exception);
    
    /**
       Log a standard message regarding the occurrence of an event with a date / time stamp. To get a date/time stamp for a particular event, call the java.util.Calendar.getInstance() method when and where the event occurs.
       @see Calendar
       @roseuid 3668421D030D
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType, Calendar dateTimeStamp);
    
    /**
       Log a non-standard message regarding the occurrence of an event with a date / time stamp. To get a date/time stamp for a particular event, call the java.util.Calendar.getInstance() method when and where the event occurs.
       @see Calendar
       @roseuid 366842DE00F7
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String message, Calendar dateTimeStamp);
    
    /**
       Log a standard message with parameters from a finer granularity component. Generally, the origin of a log message is set to the logging component name specified when an instance of the logging service is retrieved. This log method allows a finer granularity component name to be appended to the logging component name, separated by a ".",  for this log message only.
       @see LoggingServiceImpl#getInstance
       @roseuid 3675760F000C
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType, MsgParameter[] parameters);
    
    /**
       Log a non-standard message with parameters from a finer granularity component. Generally, the origin of a log message is set to the logging component name specified when an instance of the logging service is retrieved. This log method allows a finer granularity component name to be appended to the logging component name, separated by a ".",  for this log message only.
       @see LoggingServiceImpl#getInstance
       @roseuid 367578250000
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String messageText, MsgParameter[] parameters);
    
    /**
       @return The name of the component associated with this instance of the logging service.
       @roseuid 36967780037E
     */
    public String getComponentName();
    
    /**
       Log a non-standard message from a finer granularity component. Generally, the origin of a log message is set to the logging component name specified when an instance of the logging service is retrieved. This log method allows a finer granularity component name to be appended to the logging component name, separated by a ".",  for this log message only.
       @roseuid 3708D5FA0249
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String messageText);
    
    /**
       Log a standard message from a finer granularity component. Generally, the origin of a log message is set to the logging component name specified when an instance of the logging service is retrieved. This log method allows a finer granularity component name to be appended to the logging component name, separated by a ".",  for this log message only.
       @roseuid 3708D8110071
     */
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType standardMessageType);
    
    /**
       Log a non-standard message regarding an exception.
       @see MsgPriority
       @see MsgCategory
       @roseuid 37CAE48203CB
     */
    public void log(MsgPriority priority, MsgCategory category, String message, Throwable exception);
    
    /**
       Log a standard message regarding an exception.
       @roseuid 37CAE4D30314
     */
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, Throwable exception);
    
    /**
       Log a standard message regarding the occurrence of an event with a date / time stamp. To get a date/time stamp for a particular event, call the java.util.Calendar.getInstance() method when and where the event occurs.
       @see Calendar
       @roseuid 37CAE52301E2
     */
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, Calendar dateTimeStamp);
    
    /**
       Log a non-standard message regarding the occurrence of an event with a date / time stamp. To get a date/time stamp for a particular event, call the java.util.Calendar.getInstance() method when and where the event occurs.
       @see Calendar
       @roseuid 37CAE57E0099
     */
    public void log(MsgPriority priority, MsgCategory category, String message, Calendar dateTimeStamp);
    
    /**
       Log a standard message with parameters from a finer granularity component. Generally, the origin of a log message is set to the logging component name specified when an instance of the logging service is retrieved. This log method allows a finer granularity component name to be appended to the logging component name, separated by a ".",  for this log message only.
       @see LoggingServiceImpl#getInstance
       @roseuid 37CAE5E201E7
     */
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, MsgParameter[] parameters);
    
    /**
       Log a non-standard message with parameters from a finer granularity component. Generally, the origin of a log message is set to the logging component name specified when an instance of the logging service is retrieved. This log method allows a finer granularity component name to be appended to the logging component name, separated by a ".",  for this log message only.
       @see LoggingServiceImpl#getInstance
       @roseuid 37CAE630035C
     */
    public void log(MsgPriority priority, MsgCategory category, String messageText, MsgParameter[] parameters);
    
    /**
       Log a non-standard message from a finer granularity component. Generally, the origin of a log message is set to the logging component name specified when an instance of the logging service is retrieved. This log method allows a finer granularity component name to be appended to the logging component name, separated by a ".",  for this log message only.
       @roseuid 37CAE67F0297
     */
    public void log(MsgPriority priority, MsgCategory category, String messageText);
    
    /**
       Log a standard message from a finer granularity component. Generally, the origin of a log message is set to the logging component name specified when an instance of the logging service is retrieved. This log method allows a finer granularity component name to be appended to the logging component name, separated by a ".",  for this log message only.
       @roseuid 37CAE6BD014C
     */
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType);

    /**
       Log a text message with default properties, but message only formatting.
       This is meant for applications with simple logging requirements where only
       the message is of interest.
      */
    public void log( String messageTxt );

    public boolean isEnabled(MsgPriority priority, MsgCategory category);
	public boolean initialize(com.cboe.infrastructureServices.systemsManagementService.ConfigurationService config);
}
