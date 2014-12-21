package com.cboe.infrastructureServices.foundationFramework.utilities;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.loggingService.LogService;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.loggingService.StdMsgType;
/**
 * A set of static methods to simplify the creation of log messages
 *
 * @author John Wickberg
 * @author Brad Samuels
 * @version 1.6
 */
public class Log
{
	private static LogService defaultService;
    private static Boolean debugStatus;
	/**
	* Class only contains static methods, don't need constructor.
	*/
	private Log()
	{
		super();
	}
	/**
	* Creates a high priority alarm message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void alarm(BObject loggingObject, String text)
	{
		getService(loggingObject).log(MsgPriority.high, MsgCategory.systemAlarm, "", getBObjectName(loggingObject) + text);
	}
	/**
	* Creates a high priority alarm message using the component of the logging home.
	*
	* @param loggingHome the home creating the log message
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void alarm(BOHome loggingHome, String text)
	{
		getService(loggingHome).log(MsgPriority.high, MsgCategory.systemAlarm, "", getBOHomeName(loggingHome) + text);
	}
	/**
	* Creates a high priority alarm message using the component of the logging object.
	*
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void alarm(String text)
	{
		getDefaultService().log(MsgPriority.high, MsgCategory.systemAlarm, "", text);
	}
    /**
     * Creates a systemAlarm with the priority set by the calling method
     *
     * 
     * @param text the text of the message to be logged
     *
     * @author Mark Sabor
     */
    public static void medium( String text)
    {
        getDefaultService().log(MsgPriority.medium, MsgCategory.systemAlarm, "", text);
    }

    /**
     * Creates a systemAlarm with the priority set by the calling method
     *
     * @param loggingHome the object creating the log message
     * @param text the text of the message to be logged
     *
     * @author Mark Sabor
     */
    public static void medium(BOHome loggingHome, String text)
    {
        getService(loggingHome).log(MsgPriority.medium, MsgCategory.systemAlarm, "", getBOHomeName(loggingHome) + text);
    }

    /**
     * Creates a systemAlarm with the priority set by the calling method
     *
     * @param loggingObject the object creating the log message
     * @param text the text of the message to be logged
     *
     * @author Mark Sabor
     */
    public static void medium(BObject loggingObject, String text)
    {
        getService(loggingObject).log(MsgPriority.medium, MsgCategory.systemAlarm, "", getBObjectName(loggingObject) + text);
    }

	/**
	* Creates a low priority debug message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void debug(BObject loggingObject, String text)
	{
        if (isDebugOn()) {
		    getService(loggingObject).log(MsgPriority.low, MsgCategory.debug, "", getBObjectName(loggingObject) + text);
        }
	}
	/**
	* Creates a low priority debug message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param text the text of the message to be logged
	*
	* @author Brad Samuels
	*/
	public static void debug(BOHome loggingObject, String text)
	{
        if (isDebugOn()) {
		    getService(loggingObject).log(MsgPriority.low, MsgCategory.debug,"",  getBOHomeName(loggingObject) + text);
        }
	}
	/**
	* Creates a low priority debug message using the default logging service.
	*
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void debug(String text)
	{
        if (isDebugOn()) {
		    getDefaultService().log(MsgPriority.low, MsgCategory.debug, "", text);
        }
	}
	/**
	* Creates a high priority exception message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param loggedException the exception to log
	*
	* @author John Wickberg
	*/
	public static void exception(BObject loggingObject, Exception loggedException)
	{
		getService(loggingObject).log(MsgPriority.high, MsgCategory.systemAlarm, "", StdMsgType.NonStd, loggedException);
	}
	/**
	* Creates a high priority exception message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param message text of message
	* @param loggedException the exception to log
	*
	* @author John Wickberg
	*/
	public static void exception(BObject loggingObject, String message, Exception loggedException)
	{
		getService(loggingObject).log(MsgPriority.high, MsgCategory.systemAlarm,"",  message, loggedException);
	}
	/**
	* Creates a high priority exception message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param loggedException the exception to log
	*
	* @author Brad Samuels
	*/
	public static void exception(BOHome loggingObject, Exception loggedException)
	{
		getService(loggingObject).log(MsgPriority.high, MsgCategory.systemAlarm, "", StdMsgType.NonStd, loggedException);
	}
	/**
	* Creates a high priority exception message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param message text of message
	* @param loggedException the exception to log
	*
	* @author Brad Samuels
	*/
	public static void exception(BOHome loggingObject, String message, Exception loggedException)
	{
		getService(loggingObject).log(MsgPriority.high, MsgCategory.systemAlarm, "", message, loggedException);
	}
	/**
	* Creates a high priority exception message using the component of the logging object.
	*
	* @param loggedException the exception to log
	*
	* @author John Wickberg
	*/
	public static void exception(Exception loggedException)
	{
		getDefaultService().log(MsgPriority.high, MsgCategory.systemAlarm, "", StdMsgType.NonStd, loggedException);
	}
	/**
	* Creates a high priority exception message using the component of the logging object.
	*
	* @param message text of message
	* @param loggedException the exception to log
	*
	* @author John Wickberg
	*/
	public static void exception(String message, Exception loggedException)
	{
		getDefaultService().log(MsgPriority.high, MsgCategory.systemAlarm,"",  message, loggedException);
	}
	/**
	* This method creates a BObject prefix tag for the log.
	*
	* @return java.lang.String
	*/
	protected static String getBObjectName(BObject theBObject)
	{
		String objectName = (theBObject.getName() == null) ? "<anonymous>" : theBObject.getName();
		String homeName = (theBObject.getBOHome() == null) ? "<unknownHome>" : theBObject.getBOHome().getName();
		return homeName + ":" + objectName + ">>> ";
	}
	/**
	* This method creats a BOHome prefix tag for the log.
	*
	* @return java.lang.String
	* @param theBOHome The given home to create a prefix.
	*/
	protected static String getBOHomeName(BOHome theBOHome)
	{
		return theBOHome.getName() + ">>> ";
	}
	/**
	* Get the default logging service.
	*
	* @author John Wickberg
	*/
	public static LogService getDefaultService()
	{
		if (defaultService == null)
		{
			defaultService = FoundationFramework.getInstance().getDefaultLogService();
		}
		return defaultService;
	}
	/**
	* Get the logging service for an object.
	*
	* @return com.cboe.infrastructureServices.loggingService.LogService
	* @param loggingObject object creating log message
	*
	* @author John Wickberg
	*/
	public static LogService getService(BObject loggingObject)
	{
		return FoundationFramework.getInstance().getLogService(loggingObject.getComponentName());
	}
	/**
	* Get the logging service for an object.
	*
	* @return com.cboe.infrastructureServices.loggingService.LogService
	* @param loggingObject object creating log message
	*
	* @author Brad Samuels
	*/
	public static LogService getService(BOHome loggingObject)
	{
		return FoundationFramework.getInstance().getLogService(loggingObject.getComponentName());
	}
	/**
	* Creates a low priority informational message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void information(BObject loggingObject, String text)
	{
		getService(loggingObject).log(MsgPriority.low, MsgCategory.information,"",  getBObjectName(loggingObject) + text);
	}
	/**
	* Creates a low priority informational message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param text the text of the message to be logged
	*
	* @author Brad Samuels
	*/
	public static void information(BOHome loggingObject, String text)
	{
		getService(loggingObject).log(MsgPriority.low, MsgCategory.information,"",  getBOHomeName(loggingObject) + text);
	}
	/**
	* Creates a low priority informational message using the default logging service.
	*
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void information(String text)
	{
		getDefaultService().log(MsgPriority.low, MsgCategory.information,"",  text);
	}
    /**
     * Determines if debug logging has been enabled.
     */
    public static boolean isDebugOn() {
        if (debugStatus == null) {
            if (System.getProperty("ApplicationDebugOn") != null) {
                debugStatus = Boolean.TRUE;
            }
            else {
                debugStatus = Boolean.FALSE;
            }
            Log.information("Value of ApplicationDebugOn = " + debugStatus);
        }
        return debugStatus.booleanValue();
    }
	/**
	 * Determine if logging should even be attempted for the provided category and or priority.
	 * Either parameter may be null.
	 *
	 * @return boolean True means that an attempt to log the information should be made.
	 * @param category The category to use when determining applicability.
	 * @param priority The priority to use when determining applicability.
	 */
	public static boolean isEnabled(BOHome home, MsgPriority priority, MsgCategory category)
	{
        LogService service;
        if(home ==null)
        {
            service = getService(home);
        }
        else
        {
            service = getDefaultService();
        }
        return service.isEnabled(priority, category);
	}
	/**
	* Creates a high priority audit message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void audit(BObject loggingObject, String text)
	{
		getService(loggingObject).log(MsgPriority.high, MsgCategory.audit, "", getBObjectName(loggingObject) + text);
	}
	/**
	* Creates a high priority audit message using the component of the logging home.
	*
	* @param loggingHome the home creating the log message
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void audit(BOHome loggingHome, String text)
	{
		getService(loggingHome).log(MsgPriority.high, MsgCategory.audit, "", getBOHomeName(loggingHome) + text);
	}
	/**
	* Creates a high priority audit message using the component of the logging object.
	*
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void audit(String text)
	{
		getDefaultService().log(MsgPriority.high, MsgCategory.audit, "", text);
	}
	/**
	* Creates a high priority notification message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void notification(BObject loggingObject, String text)
	{
		getService(loggingObject).log(MsgPriority.high, MsgCategory.systemNotification, "", getBObjectName(loggingObject) + text);
	}
	/**
	* Creates a high priority notification message using the component of the logging home.
	*
	* @param loggingHome the home creating the log message
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void notification(BOHome loggingHome, String text)
	{
		getService(loggingHome).log(MsgPriority.high, MsgCategory.systemNotification, "", getBOHomeName(loggingHome) + text);
	}
	/**
	* Creates a high priority notification message using the component of the logging object.
	*
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void notification(String text)
	{
		getDefaultService().log(MsgPriority.high, MsgCategory.systemNotification, "", text);
	}
	/**
	* Creates a high priority nonRepudiation message using the component of the logging object.
	*
	* @param loggingObject the object creating the log message
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void nonRepudiation(BObject loggingObject, String text)
	{
		getService(loggingObject).log(MsgPriority.high, MsgCategory.nonRepudiation, "", getBObjectName(loggingObject) + text);
	}
	/**
	* Creates a high priority nonRepudiation message using the component of the logging home.
	*
	* @param loggingHome the home creating the log message
	* @param text the text of the message to be logged
	*
	* @author John Wickberg
	*/
	public static void nonRepudiation(BOHome loggingHome, String text)
	{
		getService(loggingHome).log(MsgPriority.high, MsgCategory.nonRepudiation, "", getBOHomeName(loggingHome) + text);
	}
	/**
	* Creates a high priority nonRepudiation message using the component of the logging object.
	*
	* @param text the text of the message to be logged
	* @author John Wickberg
	*/
	public static void nonRepudiation(String text)
	{
		getDefaultService().log(MsgPriority.high, MsgCategory.nonRepudiation, "", text);
	}
}
