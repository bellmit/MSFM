
package com.cboe.presentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.*;

public class MockGUILoggerImpl implements  IGUILogger
{
    /**
     * Calls the alarm method with the standard window title and the message.
     * @param messageText Message
     */
    public void alarm ( String messageText )
    {
    }

    /**
     * Logs an alarm message to the logger with priority high in category system alarm.
     * @param windowTitle From where message came from
     * @param messageText Message
     */
    public void alarm ( String windowTitle , String messageText )
    {
    }

    /**
     * Logs an alarm message to the logger with priority high in category system alarm.
     * @param windowTitle From where message came from
     * @param Object Contains the content for the to be built Message
     */
    public void alarm ( String windowTitle , Object structObject )
    {
    }

    /**
     * Logs an alarm message to the logger with priority high in category system alarm.
     * @param windowTitle From where message came from
     * @param Object[] Contains the content for the to be built Message
     */
    public void alarm ( String windowTitle , Object[] structObjectArray )
    {
    }

    /**
     * Calls the audit method with the standard window title and the message.
     * @param messageText Message
     */
    public void audit ( String messageText )
    {
    }

    /**
     * Logs an audit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param messageText Message
     */
    public void audit ( String windowTitle , String messageText )
    {
    }

    /**
     * Logs an audit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param Object Contains the content for the to be built Message
     */
    public void audit ( String windowTitle , Object structObject )
    {
    }

    /**
     * Logs an audit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param Object[] Contains the content for the to be built Message
     */
    public void audit ( String windowTitle , Object[] structObjectArray )
    {
    }

    /**
     * Logs an audit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param userAction string specifying action performed by user
     * @param Object Contains the content for the to be built Message
     */
    public void audit(String windowTitle, String userAction, Object structObject)
    {
    }

    /**
     * Logs an audit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param userAction string specifying action performed by user
     * @param Object[] Contains the content for the to be built Message
     */
    public void audit(String windowTitle, String userAction, Object[] structObjectArray)
    {
    }

    /**
     * Logs a nonRepudiationAudit message to the logger with priority medium in category audit.
     * @param messageText Message
     */
    public void nonRepudiationAudit ( String messageText )
    {
    }

    /**
     * Logs a nonRepudiationAudit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param messageText Message
     */
    public void nonRepudiationAudit ( String windowTitle , String messageText )
    {
    }

    /**
     * Logs a nonRepudiationAudit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param Object Contains the content for the to be built Message
     */
    public void nonRepudiationAudit ( String windowTitle , Object structObject )
    {
    }

    /**
     * Logs a nonRepudiationAudit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param Object[] Contains the content for the to be built Message
     */
    public void nonRepudiationAudit ( String windowTitle , Object[] structObjectArray )
    {
    }

    /**
     * Logs a nonRepudiationAudit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param userAction string specifying action performed by user
     * @param Object Contains the content for the to be built Message
     */
    public void nonRepudiationAudit ( String windowTitle , String userAction , Object structObject )
    {
    }

    /**
     * Logs a nonRepudiationAudit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param userAction string specifying action performed by user
     * @param Object[] Contains the content for the to be built Message
     */
    public void nonRepudiationAudit ( String windowTitle , String userAction , Object[] structObjectArray )
    {
    }

    /**
     * Calls the debug method with the standard window title and the message.
     * @param messageText Message
     */
    public void debug ( String messageText , IGUILoggerBusinessProperty businessProperty )
    {
    }

    /**
     * Logs a debug message to the logger with priority low in category debug. Will only
     * log it if debugging has been turned on for this GUILogger.
     * @param windowTitle From where message came from
     * @param messageText Message
     */
    public void debug ( String windowTitle , IGUILoggerBusinessProperty businessProperty , String messageText )
    {
    }

    /**
     * Logs an debug message to the logger with priority low in category debug.
     * @param windowTitle From where message came from
     * @param Object[] Contains the content for the to be built Message
     */
    public void debug ( String windowTitle , IGUILoggerBusinessProperty businessProperty , Object[] structObjectArray )
    {
    }

    /**
     * Logs an debug message to the logger with priority low in category debug.
     * @param windowTitle From where message came from
     * @param Object Contains the content for the to be built Message
     */
    public void debug ( String windowTitle , IGUILoggerBusinessProperty businessProperty , Object structObject )
    {
    }

    /**
     * Calls the exception method with the standard window title and the exception.
     * @param t Exception to obtain message from.
     */
    public void exception ( Throwable t )
    {
    }

    /**
     * Logs an exception message to the logger with priority high in category system alarm. Uses
     * the StdMsgType.NonStd to define the type.
     * @param t Exception to obtain message from.
     * @param windowTitle From where message came from.
     */
    public void exception ( Throwable t , String windowTitle )
    {
    }

    /**
     * Calls the exception method with the standard window title, the message and the exception.
     * @param message Message Text
     * @param t Exception to obtain message from.
     */
    public void exception ( String message , Throwable t )
    {
    }

    /**
     *  Logs an exception message to the logger with priority high in category system alarm.
     *@param  windowTitle  From where message came from.
     *@param  userMessageText  Custom message text.
     *@param  t            Exception to obtain message from.
     *@param  object   additional objects to be dumped.
     */
    public void exception(String windowTitle, String userMessageText, Throwable t, Object[] object)
    {
    }

    /**
     * Logs an exception message to the logger with priority high in category system alarm.
     * @param windowTitle From where message came from.
     * @param messageText Custom message text.
     * @param t Exception to obtain message from.
     */
    public void exception ( String windowTitle , String messageText , Throwable t )
    {
    }

    /**
     * Gets standard window title used if one is not specified for messages.
     * @return String
     */
    public String getStdWindowTitle ()
    {
        return null;
    }

    /**
     * Calls the debug method with the standard window title and the message.
     * @param messageText Message
     */
    public void information ( String messageText , IGUILoggerBusinessProperty businessProperty )
    {
    }

    /**
     * Logs an information message to the logger with priority low in category information.
     * @param windowTitle From where message came from
     * @param messageText Message
     */
    public void information ( String windowTitle , IGUILoggerBusinessProperty businessProperty , String messageText )
    {
    }

    /**
     * Logs an information message to the logger with priority low in category information.
     * @param windowTitle From where message came from
     * @param Object Contains the content for the to be built Message
     */
    public void information ( String windowTitle , IGUILoggerBusinessProperty businessProperty , Object structObject )
    {
    }

    /**
     * Logs an information message to the logger with priority low in category information.
     * @param windowTitle From where message came from
     * @param Object[] Contains the content for the to be built Message
     */
    public void information ( String windowTitle , IGUILoggerBusinessProperty businessProperty , Object[] structObjectArray )
    {
    }

    /**
     * Returns whether alarm messages are delivered or not.
     * @return True is alarm messages are delivered, false otherwise.
     */
    public boolean isAlarmOn ()
    {
        return false;
    }

    /**
     * Returns whether audit messages are delivered or not.
     * @return True is audit messages are delivered, false otherwise.
     */
    public boolean isAuditOn ()
    {
        return false;
    }

    /**
     * Returns whether debug messages are delivered or not.
     * @return True is debug messages are delivered, false otherwise.
     */
    public boolean isDebugOn ()
    {
        return false;
    }

    /**
     * Returns whether exception messages are delivered or not.
     * @return True is exception messages are delivered, false otherwise.
     */
    public boolean isExceptionOn ()
    {
        return false;
    }

    /**
     * Returns whether information messages are delivered or not.
     * @return True is information messages are delivered, false otherwise.
     */
    public boolean isInformationOn ()
    {
        return false;
    }

    /**
     * Returns whether messages with category of a passed in property are delivered or not.
     * @param property IGUILoggerProperty
     * @return True - messages with category of a passed in property are delivered, false otherwise.
     */
    public boolean isPropertyOn ( IGUILoggerProperty property )
    {
        return false;
    }

    /**
     * Turns the alarm messages delivery on or off.
     * @param flag True to delivery alarm, false for them to be suppressed.
     */
    public void setAlarmOn ( boolean flag )
    {
    }

    /**
     * Turns the audit messages delivery on or off.
     * @param flag True to delivery audit, false for them to be suppressed.
     */
    public void setAuditOn ( boolean flag )
    {
    }

    /**
     * Turns the debug messages delivery on or off.
     * @param flag True to delivery debug, false for them to be suppressed.
     */
    public void setDebugOn ( boolean flag )
    {
    }

    /**
     * Turns the exception messages delivery on or off.
     * @param flag True to delivery exception, false for them to be suppressed.
     */
    public void setExceptionOn ( boolean flag )
    {
    }

    /**
     * Turns the information messages delivery on or off.
     * @param flag True to delivery information, false for them to be suppressed.
     */
    public void setInformationOn ( boolean flag )
    {
    }

    /**
     * Turns the message delivery for specified property on or off.
     * @param flag True to turn ON messages, false for them to be suppressed.
     */
    public void setPropertyOn ( IGUILoggerProperty property , boolean flag )
    {
    }

    /**
     * Returns SeverityProperties object
     * @return IGUILoggerProperties - severity properties
     */
    public IGUILoggerProperties getSeverityProperties ()
    {
        return null;
    }

    /**
     * Returns BusinessProperties object
     * @return IGUILoggerProperties - business properties
     */
    public IGUILoggerProperties getBusinessProperties ()
    {
        return null;
    }

    /**
     * Sets standard window title used if one is not specified for messages.
     * @param newWindowTitle
     */
    public void setStdWindowTitle ( String newWindowTitle )
    {
    }

    public IGUILoggerVerboseLevel getDebugVerboseLevel()
    {
        return null;
    }

    public IGUILoggerVerboseLevel getExceptionVerboseLevel()
    {
        return null;
    }

    public IGUILoggerVerboseLevelProperties getVerboseLevelProperties()
    {
        return null;
    }

    public void setVerboseLevel(IGUILoggerVerboseLevelProperty property, IGUILoggerVerboseLevel verboseLevel)
    {
    }

    /**
     * Returns verbose level for a passed in verbose level property.
     * @return IGUILoggerVerboseLevel - verbose level.
     */
    public IGUILoggerVerboseLevel getPropertyValue(IGUILoggerVerboseLevelProperty property)
    {
        return null;
    }

    /**
     * Used to captures, in a log file, any text written to the console.
     * @since 20050126 SSK
     */
    public void captureConsoleLog(String logText)
    {
    }

}
