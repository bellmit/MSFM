//
// -----------------------------------------------------------------------------------
// Source file: AbstractGUILogger.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.*;
import javax.swing.*;

import com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType;

import com.cboe.interfaces.presentation.common.formatters.ExceptionFormatStrategy;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperties;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerSeverityProperty;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerVerboseLevel;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerVerboseLevelProperties;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerVerboseLevelProperty;

import com.cboe.util.ReflectiveObjectWriter;

import com.cboe.presentation.common.formatters.CommonFormatFactory;
import com.cboe.presentation.common.formatters.DateFormatThreadLocal;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

import com.cboe.loggingService.LoggingComponentNotFoundException;
import com.cboe.loggingService.LoggingServiceImpl;
import com.cboe.loggingService.LoggingServiceInterface;
import com.cboe.loggingService.MsgCategory;
import com.cboe.loggingService.MsgParameter;
import com.cboe.loggingService.MsgPriority;

/**
 *  This class provides a proxy with different method signatures for convenience
 *  to the Logging Service. You must provide your own implementation for
 *  initializing.
 */
public abstract class AbstractGUILogger implements IGUILogger
{
    protected static final String PROPERTIES_SECTION_NAME = "Logging";
    protected static final String CONSOLE_CAPTURE_COMPONENT_KEY_NAME =
            "LogServiceConsoleCaptureComponent";

    //Severity properties keys
    protected static final String WINDOW_TITLE_KEY_NAME = "StandardWindowTitle";
    protected static final String ALARM_KEY_NAME = "LogAlarm";
    protected static final String AUDIT_KEY_NAME = "LogAudit";
    protected static final String DEBUG_KEY_NAME = "LogDebug";
    protected static final String INFORMATION_KEY_NAME = "LogInformation";
    protected static final String EXCEPTION_KEY_NAME = "LogException";

    //Verbose Level property keys
    protected static final String EXCEPTION_VERBOSE_LEVEL_KEY_NAME = "ExceptionVerboseLevel";
    protected static final String DEBUG_VERBOSE_LEVEL_KEY_NAME = "DebugVerboseLevel";

    //Business properties keys
    protected static final String TEXT_MESSAGE_KEY_NAME = "LogTextMessage";
    protected static final String USER_SESSION_KEY_NAME = "LogUserSession";
    protected static final String USER_MANAGEMENT_KEY_NAME = "LogUserManagement";
    protected static final String COMMON_KEY_NAME = "LogCommon";
    protected static final String WINDOW_MANAGEMENT_KEY_NAME = "LogWindowManagement";
    protected static final String MARKET_QUERY_KEY_NAME = "LogMarketQuery";
    protected static final String ORDER_ENTRY_KEY_NAME = "LogOrderEntry";
    protected static final String ORDER_QUERY_KEY_NAME = "LogOrderQuery";
    protected static final String PRODUCT_DEFINITION_KEY_NAME = "LogProductDefinition";
    protected static final String PRODUCT_QUERY_KEY_NAME = "LogProductQuery";
    protected static final String QUOTE_KEY_NAME = "LogQuote";
    protected static final String RFQ_KEY_NAME = "LogRFQ";
    protected static final String AUCTION_KEY_NAME = "LogAuction";
    protected static final String TRADING_SESSION_KEY_NAME = "LogTradingSession";
    protected static final String USER_HISTORY_KEY_NAME = "LogUserHistory";
    protected static final String USER_PREFERENCES_KEY_NAME = "LogUserPreferences";
    protected static final String QRM_KEY_NAME = "LogQRM";
    protected static final String ORDER_BOOK_KEY_NAME = "LogOrderBook";
    protected static final String PRODUCT_MAINTENANCE_KEY_NAME = "LogProductMaintenance";
    protected static final String TRADE_MAINTENANCE_KEY_NAME = "LogTradeMaintenance";
    protected static final String FIRM_MAINTENANCE_KEY_NAME = "LogFirmMaintenance";
    protected static final String SESSION_MANAGEMENT_KEY_NAME = "LogSessionManagement";
    protected static final String SECURITY_ADMIN_KEY_NAME = "LogSecurityAdmin";
    protected static final String TRADING_PROPERTY_KEY_NAME = "LogTradingProperty";
    protected static final String ORDER_HANDLING_KEY_NAME = "LogOrderHandling";
    protected static final String MM_QUOTE_KEY_NAME = "LogMMQuote";
    protected static final String PRODUCT_SELECTOR_KEY_NAME = "LogProductSelector";
    protected static final String TRADE_QUERY_KEY_NAME = "LogTradeQuery";
    protected static final String TICKER_KEY_NAME = "LogTicker";
    protected static final String INTERMARKET_HELD_ORDER_ENTRY_KEY_NAME = "LogHeldOrderEntry";
    protected static final String INTERMARKET_HELD_ORDER_QUERY_KEY_NAME = "LogHeldOrderQuery";
    protected static final String INTERMARKET_NBBO_AGENT_KEY_NAME = "LogNbboAgent";
    protected static final String REPORT_GENERATION_KEY_NAME = "LogReportGeneration";
    protected static final String PRODUCT_GROUPS_KEY_NAME = "LogProductGroups";
    protected static final String PROPERTY_SERVICE_KEY_NAME = "LogPropertyService";
    protected static final String AGENT_QUERY_KEY_NAME = "LogAgentQuery";
    protected static final String ALERTS_KEY_NAME = "LogAlerts";
    protected static final String CALENDAR_ADMIN_KEY_NAME = "CalendarAdmin";
    protected static final String ROUTING_PROPERTY_KEY_NAME = "LogRoutingProperty";
    protected static final String DATABASE_QUERY_BUILDER_KEY_NAME = "LogDatabaseQueryBuilder";
    protected static final String MANUAL_REPORTING_KEY_NAME = "LogManualReporting";
    protected static final String PREFERENCE_CONVERSION_KEY_NAME = "LogPreferenceConversion";
    protected static final String PERMISSION_MATRIX_KEY_NAME = "LogPermissionMatrix";
    protected static final String OMT_KEY_NAME = "LogOmt";
    protected static final String STRATEGY_DSM_KEY_NAME = "LogStrategyDSM";

    // Instrumentation Monitor
    protected static final String INSTRUMENTATION_KEY_NAME = "LogInstrumentation";
    protected static final String CONTEXT_DETAIL_KEY_NAME = "LogContextDetail";
    protected static final String CAS_CONFIGURATION_KEY_NAME = "LogCASConfig";
    protected static final String CAS_SUMMARY_KEY_NAME = "LogCASSummary";
    protected static final String QUEUE_INSTRUMENTOR_KEY_NAME = "LogQueueInstrumentor";
    protected static final String THREAD_INSTRUMENTOR_KEY_NAME = "LogThreadInstrumentor";
    protected static final String METHOD_INSTRUMENTOR_KEY_NAME = "LogMethodInstrumentor";
    protected static final String COUNT_INSTRUMENTOR_KEY_NAME = "LogCountInstrumentor";
    protected static final String EVENT_INSTRUMENTOR_KEY_NAME = "LogEventChnlInstrumentor";
    protected static final String HEAP_INSTRUMENTOR_KEY_NAME = "LogHeapInstrumentor";
    protected static final String JMX_INSTRUMENTOR_KEY_NAME = "LogJmxInstrumentor";
    protected static final String JSTAT_INSTRUMENTOR_KEY_NAME = "LogJStatInstrumentor";
    protected static final String NETWORK_INSTRUMENTOR_KEY_NAME = "LogNetworkInstrumentor";
    protected static final String PROCESSES_KEY_NAME = "LogProcesses";
    protected static final String ALARM_CONDITION_KEY_NAME = "LogAlarmCondition";
    protected static final String ALARM_CALCULATION_KEY_NAME = "LogAlarmCalculation";
    protected static final String ALARM_DEFINITION_KEY_NAME = "LogAlarmDefinition";
    protected static final String ALARM_ACTIVATION_KEY_NAME = "LogAlarmActivation";
    protected static final String ALARM_WATCHDOG_KEY_NAME = "LogAlarmWatchdog";
    protected static final String ALARM_NOTIFICATION_KEY_NAME = "LogAlarmNotification";
    protected static final String ALARM_EXCEPTIONS_KEY_NAME = "LogAlarmExceptions";
    protected static final String ORB_NAME_ALIAS_KEY_NAME = "LogOrbNameAlias";
    protected static final String ALARM_ASSIGNMENT_KEY_NAME = "LogAlarmAssignment";
    protected static final String LOGICAL_ORB_NAME_KEY_NAME = "LogLogicalOrbName";
    protected static final String GROUPS_KEY_NAME = "LogGroups";

    // XTP Gui
    protected static final String XTP_KEY_NAME = "LogXTP";

    // Message Monitor
    protected static final String MESSAGEMON_KEY_NAME = "LogMessageMon";
    protected static final String SUBJECTS_KEY_NAME = "LogSubjects";
    protected static final String CHANNELS_KEY_NAME = "LogChannels";
    protected static final String RECORDING_KEY_NAME = "LogRecording";
    protected static final String PROCESS_WATCHER_KEY_NAME = "LogProcessWatcher";
    protected static final String EXTENT_MAP_KEY_NAME = "LogExtentMap";
    protected static final String MONITOR_KEY_NAME = "LogMonitor";
    protected static final String SNIFFER_KEY_NAME = "LogSniffer";

    private boolean isAlarmOn;
    private boolean isAuditOn;
    private boolean isDebugOn;
    private boolean isExceptionOn;
    private boolean isInformationOn;

    private IGUILoggerVerboseLevel debugVerboseLevel;
    private IGUILoggerVerboseLevel exceptionVerboseLevel;

    /**
     *  This <code>logger</code> var needs to be initialized for logging to
     *  perform.
     */
    protected LoggingServiceInterface logger = null;
    protected LoggingServiceInterface consoleCaptureLogger = null;

    protected PrintStream errorOutputStream = System.err;

//    private IGUILoggerSeverityProperties severityProperties = null;
//    private IGUILoggerBusinessProperties businessProperties = null;

    protected String stdWindowTitle = "SBTGUI";
    private static final DateFormatThreadLocal dateFormatterThreadLocal = new DateFormatThreadLocal("yyyy/MM/dd HH:mm:ss:SSS");

    /**
     *
     */
    public AbstractGUILogger()
    {
        initLogger();
        initConsoleCaptureLogger();     // this must be after initLogger()
        initErrorStream();
        initSeverityProperties();
        initVerboseLevelProperties();
    }

    /**
     * Initialize the console capture logger, which copies the console into a file.
     * @since 20050124 SSK (Shawn Khosravani)
     */
    protected void initConsoleCaptureLogger()
    {
        String methodName = "AbstractGUILogger::initConsoleCaptureLogger: ";

        if (logger != null)
        {
            String loggingCaptureComponentName = null;
            if(AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
                loggingCaptureComponentName = AppPropertiesFileFactory.find().getValue(PROPERTIES_SECTION_NAME, CONSOLE_CAPTURE_COMPONENT_KEY_NAME);
            }

            if(loggingCaptureComponentName != null  &&  loggingCaptureComponentName.length() > 0)
            {
                try
                {
                    consoleCaptureLogger = LoggingServiceImpl.getInstance(loggingCaptureComponentName);
                }
                catch(LoggingComponentNotFoundException e)
                {
                    // any errors in LoggingServiceImpl.getInstance() above means no log files will be created
                    // so we can not log the error like this:
                    //
                    // logger.log (MsgPriority.high, MsgCategory.systemAlarm,
                    //             "The logging component name specified as " + loggingCaptureComponentName
                    //           + ", could not be found.", e);
                    //
                    // we will write to err instead:

                    System.err.println(methodName + "The logging component name specified as " + loggingCaptureComponentName
                                                  + ", could not be found.");
                    System.err.println(e);
                }
            }
            else
            {
                logger.log(MsgPriority.high, MsgCategory.systemAlarm,
                           "The logging component name specified as " + CONSOLE_CAPTURE_COMPONENT_KEY_NAME
                         + "= , is invalid. Will not capture console.");
            }
        }
        else
        {
            // I don't want to call
            //
            //   LoggingServiceImpl.setName(...);
            //   LoggingServiceImpl.initialize(...);
            //
            // which should have been called in initLogger. So if logger is null, I'll assume there was a problem
            // and will not set up the copy logger

            System.err.println(methodName + "logger has not been initialized, so will not capture console.");
        }
    } // end method initConsoleCaptureLogger

    /**
     * Captures, in a log file, any text written to the console.
     *
     * NOTE: logging service appends extra \r\n at the end of any logged message. We remove these so
     * that no extra blank lines are inserted into the log file. You can still log blank lines by simply
     * logging \n characters. After removing the extra \r\n, your \n will still remain in the logText.
     *
     * @param logText String that appears on console and will be captured in a file as well
     * @since 20050124 SSK
     */
    public synchronized void captureConsoleLog(String logText)
    {
        if (consoleCaptureLogger != null)
        {
            int len = logText.length();
            /*
             * commented out because it may slow us down. if the extra blank lines start to get
             * annoying and the string processing does not slow us down much, we will uncomment it.
             *
            if (len >= 2)
            {
                char lastChar       = logText.charAt(len - 1);
                char nextToLastChar = logText.charAt(len - 2);

                if (nextToLastChar == '\r'  &&  lastChar == '\n')
                {
                    len -= 2;
                    logText = len > 0 ? logText.substring(0, len) : "";
                }
            } // end outer if (len >= 2)
            */
            if (len > 0)
            {
                consoleCaptureLogger.log(logText);     // default priority=medium and category=information

                // to log with a specific priority and category do this:
                //
                //  consoleCaptureLogger.log(MsgPriority.low, MsgCategory.systemAlarm, logText);
            }
        } // if (consoleCaptureLogger != null)
    } // end method captureConsoleLog

    /**
     *  Initializes the error output stream. Should set the errorOutputStream to
     *  some PrintStream.
     */
    protected abstract void initErrorStream();

    /**
     *  Initializes the Logger engine. Should set the logger var to a valid
     *  value.
     */
    protected abstract void initLogger();

    /**
     *  Gets full name of the currently logged in user
     */
    protected abstract String getUserName();

    protected abstract void saveProperty(IGUILoggerProperty property, boolean value);
    protected abstract void saveProperty(IGUILoggerVerboseLevelProperty property, IGUILoggerVerboseLevel verboseLevel);

    public abstract IGUILoggerProperties getSeverityProperties();
    public abstract IGUILoggerProperties getBusinessProperties();
    public abstract IGUILoggerVerboseLevelProperties getVerboseLevelProperties();


    protected void initSeverityProperties()
    {
        isAlarmOn = getSeverityProperties().isPropertyOn(com.cboe.presentation.common.logging.GUILoggerSeverityProperty.ALARM);
        isAuditOn = getSeverityProperties().isPropertyOn(com.cboe.presentation.common.logging.GUILoggerSeverityProperty.AUDIT);
        isDebugOn = getSeverityProperties().isPropertyOn(com.cboe.presentation.common.logging.GUILoggerSeverityProperty.DEBUG);
        isExceptionOn = getSeverityProperties().isPropertyOn(com.cboe.presentation.common.logging.GUILoggerSeverityProperty.EXCEPTION);
        isInformationOn = getSeverityProperties().isPropertyOn(com.cboe.presentation.common.logging.GUILoggerSeverityProperty.INFORMATION);
    }

    protected void initVerboseLevelProperties()
    {
        debugVerboseLevel = getVerboseLevelProperties().getPropertyValue(GUILoggerVerboseLevelProperty.DEBUG_VERBOSE_LEVEL);
        exceptionVerboseLevel = getVerboseLevelProperties().getPropertyValue(GUILoggerVerboseLevelProperty.EXCEPTION_VERBOSE_LEVEL);
    }

    public IGUILoggerVerboseLevel getExceptionVerboseLevel()
    {
        return exceptionVerboseLevel;
    }

    public IGUILoggerVerboseLevel getPropertyValue(IGUILoggerVerboseLevelProperty property)
    {
        return getVerboseLevelProperties().getPropertyValue(property);
    }

    public IGUILoggerVerboseLevel getDebugVerboseLevel()
    {
        return debugVerboseLevel;
    }

    /**
     *  Turns the alarm messages delivery on or off.
     *
     *@param  flag  True to delivery alarm, false for them to be suppressed.
     */
    public void setAlarmOn(boolean flag)
    {
        isAlarmOn = flag;
        getSeverityProperties().setProperty(com.cboe.presentation.common.logging.GUILoggerSeverityProperty.ALARM, flag);
    }

    /**
     *  Turns the audit messages delivery on or off.
     *
     *@param  flag  True to delivery audit, false for them to be suppressed.
     */
    public void setAuditOn(boolean flag)
    {
        isAuditOn = flag;
        getSeverityProperties().setProperty(com.cboe.presentation.common.logging.GUILoggerSeverityProperty.AUDIT, flag);
    }

    /**
     *  Turns the debug messages delivery on or off.
     *
     *@param  flag  True to delivery debug, false for them to be suppressed.
     */
    public void setDebugOn(boolean flag)
    {
        isDebugOn = flag;
        getSeverityProperties().setProperty(com.cboe.presentation.common.logging.GUILoggerSeverityProperty.DEBUG, flag);
    }

    /**
     *  Turns the exception messages delivery on or off.
     *
     *@param  flag  True to delivery exception, false for them to be suppressed.
     */
    public void setExceptionOn(boolean flag)
    {
        isExceptionOn = flag;
        getSeverityProperties().setProperty(com.cboe.presentation.common.logging.GUILoggerSeverityProperty.EXCEPTION, flag);
    }

    /**
     *  Turns the information messages delivery on or off.
     *
     *@param  flag  True to delivery information, false for them to be
     *      suppressed.
     */
    public void setInformationOn(boolean flag)
    {
        isInformationOn = flag;
        getSeverityProperties().setProperty(com.cboe.presentation.common.logging.GUILoggerSeverityProperty.INFORMATION, flag);
    }

    /**
     *  Sets standard window title used if one is not specified for messages.
     *
     *@param  newWindowTitle
     */
    public void setStdWindowTitle(String newWindowTitle)
    {
        if(newWindowTitle != null && newWindowTitle.length() > 0)
        {
            stdWindowTitle = newWindowTitle;
        }
    }

    /**
     * Sets whether messages with category of a passed in property are delivered or not.
     * @param property IGUILoggerProperty
     * @param property flag, True - messages with category of a passed in property are delivered, false - they are blocked.
     */
    public final void setPropertyOn(IGUILoggerProperty property, boolean flag)
    {
        if ( property instanceof IGUILoggerBusinessProperty )
        {
            setPropertyOn((IGUILoggerBusinessProperty)property, flag);
        }
        else if ( property instanceof IGUILoggerSeverityProperty )
        {
            setPropertyOn((IGUILoggerSeverityProperty)property, flag);
        }
        else
        {
            throw new IllegalArgumentException("Unknown property type: " + property.getClass().getName());
        }
    }

    /**
     * Sets whether messages with category of a passed in property are delivered or not.
     * @param property GUILoggerSeverityProperty
     * @param property flag, True - messages with category of a passed in property are delivered, false - they are blocked.
     */
    public final void setPropertyOn(IGUILoggerSeverityProperty property, boolean flag)
    {
        getSeverityProperties().setProperty(property, flag);
        saveProperty(property, flag);
        initSeverityProperties();
    }

    /**
     * Sets whether messages with category of a passed in property are delivered or not.
     * @param property GUILoggerBusinessProperty
     * @param property flag, True - messages with category of a passed in property are delivered, false - they are blocked.
     */
    public final void setPropertyOn(IGUILoggerBusinessProperty property, boolean flag)
    {
        getBusinessProperties().setProperty(property, flag);
        saveProperty(property, flag);
    }

    public final void setVerboseLevel(IGUILoggerVerboseLevelProperty property, IGUILoggerVerboseLevel verboseLevel)
    {
        getVerboseLevelProperties().setProperty(property, verboseLevel);
        saveProperty(property, verboseLevel);
        initVerboseLevelProperties();
    }

    /**
     *  Gets standard window title used if one is not specified for messages.
     *
     *@return    String
     */
    public String getStdWindowTitle()
    {
        return stdWindowTitle;
    }

//    protected IGUILoggerSeverityProperties getSeverityProperties()
//    {
//        if ( severityProperties == null )
//        {
//            severityProperties = new GUILoggerSeverityProperties();
//        }
//        return severityProperties;
//    }
//
//    protected IGUILoggerBusinessProperties getBusinessProperties()
//    {
//        if ( businessProperties == null )
//        {
//            businessProperties = new GUILoggerBusinessProperties();
//        }
//        return businessProperties;
//    }

    /**
     *  Returns whether alarm messages are delivered or not.
     *
     *@return    True is alarm messages are delivered, false otherwise.
     */
    public final boolean isAlarmOn()
    {
        return isAlarmOn;
    }

    /**
     *  Returns whether audit messages are delivered or not.
     *
     *@return    True is audit messages are delivered, false otherwise.
     */
    public final boolean isAuditOn()
    {
        return isAuditOn;
    }

    /**
     *  Returns whether debug messages are delivered or not.
     *
     *@return    True is debug messages are delivered, false otherwise.
     */
    public final boolean isDebugOn()
    {
        return isDebugOn;
    }

    /**
     *  Returns whether exception messages are delivered or not.
     *
     *@return    True is exception messages are delivered, false otherwise.
     */
    public final boolean isExceptionOn()
    {
        return isExceptionOn;
    }

    /**
     *  Returns whether information messages are delivered or not.
     *
     *@return    True is information messages are delivered, false otherwise.
     */
    public final boolean isInformationOn()
    {
        return isInformationOn;
    }

    /**
     * Returns whether messages with category of a passed in property are delivered or not.
     * @param property IGUILoggerProperty
     * @return True - messages with category of a passed in property are delivered, false otherwise.
     */
    public final boolean isPropertyOn(IGUILoggerProperty property)
    {
        if ( property instanceof GUILoggerBusinessProperty )
        {
            return isPropertyOn((GUILoggerBusinessProperty)property);
        }
        else if ( property instanceof com.cboe.presentation.common.logging.GUILoggerSeverityProperty )
        {
            return isPropertyOn((com.cboe.presentation.common.logging.GUILoggerSeverityProperty)property);
        }
        else
        {
            throw new IllegalArgumentException("Unknown property type: " + property.getClass().getName());
        }
    }

    /**
     * Returns whether messages with category of a passed in property are delivered or not.
     * @param property IGUILoggerProperty
     * @return True - messages with category of a passed in property are delivered, false otherwise.
     */
    public final boolean isPropertyOn(com.cboe.presentation.common.logging.GUILoggerSeverityProperty property)
    {
        return getSeverityProperties().isPropertyOn(property);
    }

    /**
     * Returns whether messages with category of a passed in property are delivered or not.
     * @param property IGUILoggerProperty
     * @return True - messages with category of a passed in property are delivered, false otherwise.
     */
    public final boolean isPropertyOn(GUILoggerBusinessProperty property)
    {
        return getBusinessProperties().isPropertyOn(property);
    }

    protected void initProperty(IGUILoggerProperty property, String propertyString)
    {
        boolean propertyValue = false;
        if (propertyString != null)
        {
            propertyValue = new Boolean(propertyString).booleanValue();
        }
        else
        {
            if ( property instanceof IGUILoggerBusinessProperty )
            {
                propertyValue = getBusinessProperties().getDefaultValue();
            }
            else if (property instanceof IGUILoggerSeverityProperty)
            {
                propertyValue = getSeverityProperties().getDefaultValue();
            }
            else
            {
                throw new IllegalArgumentException("Unknown property type: " + property.getClass().getName());
            }

            //log error message here
            System.err.println("Logging property " + property.getName() + " was not found.");
            System.err.println("Will use default value of " + propertyValue);
        }
        setPropertyOn(property, propertyValue);
    }

    protected void initProperty(IGUILoggerVerboseLevelProperty property, String propertyString)
    {
        IGUILoggerVerboseLevel verboseLevel;
        if (propertyString != null)
        {
            try
            {
                int intProperty = Integer.parseInt(propertyString);
                verboseLevel = GUILoggerVerboseLevel.getProperty(intProperty);
            }
            catch (NumberFormatException e)
            {
                //log message here
                verboseLevel = getVerboseLevelProperties().getDefaultValue();
                System.err.println("Invalid value for logging property " + property.getName() +
                                   ". Value was " + propertyString);
                System.err.println("Will use default value of " + verboseLevel.getName());
            }
        }
        else
        {
            verboseLevel = getVerboseLevelProperties().getDefaultValue();

            //log error message here
            System.err.println("Logging property " + property.getName() + " was not found.");
            System.err.println("Will use default value of "+ verboseLevel.getName());
        }
        setVerboseLevel(property, verboseLevel);
    }


    /**
     *  Calls the alarm method with the standard window title and the message.
     *
     *@param  messageText  Message
     */
    public void alarm(String messageText)
    {
        alarm(stdWindowTitle, messageText);
    }

    /**
     *  Logs an alarm message to the logger with priority high in category
     *  system alarm.
     *
     *@param  windowTitle  From where message came from
     *@param  messageText  Message
     */
    public void alarm(String windowTitle, String messageText)
    {
        if ( isAlarmOn() )
        {
            log(MsgPriority.high, MsgCategory.systemAlarm, windowTitle, messageText);
        }
    }

    /**
     *  Logs an alarm message to the logger with priority high in category
     *  system alarm.
     *
     *@param  windowTitle   From where message came from
     *@param  structObject  Description of Parameter
     */
    public void alarm(String windowTitle, Object structObject)
    {
        if ( isAlarmOn()  )
        {
            alarm(windowTitle, writeObject(structObject));
        }
    }

    /**
     *  Logs an alarm message to the logger with priority high in category
     *  system alarm.
     *
     *@param  windowTitle        From where message came from
     *@param  structObjectArray  Description of Parameter
     */
    public void alarm(String windowTitle, Object[] structObjectArray)
    {
        alarm(windowTitle, (Object) structObjectArray);
    }

    /**
     *  Calls the audit method with the standard window title and the message.
     *
     *@param  messageText  Message
     */
    public void audit(String messageText)
    {
        audit(stdWindowTitle, messageText);
    }

    /**
     *  Logs an audit message to the logger with priority medium in category
     *  audit.
     *
     *@param  windowTitle  From where message came from
     *@param  messageText  Message
     */
    public void audit(String windowTitle, String messageText)
    {
        if ( isAuditOn() )
        {
            log(MsgPriority.medium, MsgCategory.audit, windowTitle, messageText);
        }
    }

    /**
     *  Logs an audit message to the logger with priority medium in category
     *  audit.
     *
     *@param  windowTitle   From where message came from
     *@param  structObject  Description of Parameter
     */
    public void audit(String windowTitle, Object structObject)
    {
        if ( isAuditOn() )
        {
            audit(windowTitle, writeObject(structObject));
        }
    }

    /**
     *  Logs an audit message to the logger with priority medium in category
     *  audit.
     *
     *@param  windowTitle        From where message came from
     *@param  structObjectArray  Description of Parameter
     */
    public void audit(String windowTitle, Object[] structObjectArray)
    {
        audit(windowTitle, (Object) structObjectArray);
    }

    /**
     * Logs an audit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param userAction string specifying action performed by user
     * @param Object[] Contains the content for the to be built Message
     */
    public void audit(String windowTitle, String userAction, Object[] structObjectArray)
    {
        audit(windowTitle, userAction, (Object)structObjectArray);
    }

    /**
     * Logs an audit message to the logger with priority medium in category audit.
     * @param windowTitle From where message came from
     * @param userAction string specifying action performed by user
     * @param Object Contains the content for the to be built Message
     */
    public void audit(String windowTitle, String userAction, Object structObject)
    {
        if(isAuditOn())
        {
            StringBuilder title = new StringBuilder(100);
            if(windowTitle == null || windowTitle.length() == 0)
            {
                title.append(stdWindowTitle);
            }
            else
            {
                title.append(windowTitle);
            }
            title.append(". User ").append(getUserName()).append(" performed ").append(userAction);

            audit(title.toString(), writeObject(structObject));
        }
    }

    /**
     *  Calls the audit method with the standard window title and the message.
     *
     *@param  messageText  Message
     */
    public void nonRepudiationAudit(String messageText)
    {
        nonRepudiationAudit(stdWindowTitle, messageText);
    }

    /**
     *  Logs an audit message to the logger with priority medium in category
     *  audit.
     *
     *@param  windowTitle  From where message came from
     *@param  messageText  Message
     */
    public void nonRepudiationAudit(String windowTitle, String messageText)
    {
        if ( isAuditOn() )
        {
            StringBuffer title = new StringBuffer(100);
            title.append("NonRepudiatonAudit: User ").append(getUserName()).append(". ");
            if ( windowTitle == null || windowTitle.length() == 0 )
            {
                title.append(stdWindowTitle);
            }
            else
            {
                title.append(windowTitle);
            }
            log(MsgPriority.medium, MsgCategory.audit, title.toString(), messageText);
        }
    }

    /**
     *  Logs an audit message to the logger with priority medium in category
     *  audit.
     *
     *@param  windowTitle   From where message came from
     *@param  structObject  Description of Parameter
     */
    public void nonRepudiationAudit(String windowTitle, Object structObject)
    {
        nonRepudiationAudit(windowTitle, writeObject(structObject));
    }

    /**
     *  Logs an audit message to the logger with priority medium in category
     *  audit.
     *
     *@param  windowTitle        From where message came from
     *@param  structObjectArray  Description of Parameter
     */
    public void nonRepudiationAudit(String windowTitle, Object[] structObjectArray)
    {
        nonRepudiationAudit(windowTitle, (Object) structObjectArray);
    }

    /**
     *  Logs an audit message to the logger with priority medium in category
     *  audit.
     *
     *@param  windowTitle   From where message came from
     *@param  structObject  Description of Parameter
     */
    public void nonRepudiationAudit(String windowTitle, String userAction, Object structObject)
    {
        if ( isAuditOn() )
        {
            StringBuffer title = new StringBuffer(100);
            if ( windowTitle == null || windowTitle.length() == 0 )
            {
                title.append(stdWindowTitle);
            }
            else
            {
                title.append(windowTitle);
            }
            title.append(". User ").append(getUserName()).append(" performed ").append(userAction);

            nonRepudiationAudit(title.toString(), writeObject(structObject));
        }
    }

    /**
     *  Logs an audit message to the logger with priority medium in category
     *  audit.
     *
     *@param  windowTitle        From where message came from
     *@param  structObjectArray  Description of Parameter
     */
    public void nonRepudiationAudit(String windowTitle, String userAction, Object[] structObjectArray)
    {
        nonRepudiationAudit(windowTitle, userAction, (Object) structObjectArray);
    }

    /**
     *  Calls the debug method with the standard window title and the message.
     *
     *@param  messageText  Message
     */
    public void debug(String messageText, IGUILoggerBusinessProperty businessProperty)
    {
        debug(stdWindowTitle, businessProperty, messageText);
    }

    /**
     *  Logs a debug message to the logger with priority low in category debug.
     *  Will only log it if debugging has been turned on for this GUILogger.
     *
     *@param  windowTitle  From where message came from
     *@param  messageText  Message
     */
    public void debug(String windowTitle, IGUILoggerBusinessProperty businessProperty, String messageText)
    {
        if ( isDebugOn() && isPropertyOn(businessProperty) )
        {
            log(MsgPriority.low, MsgCategory.debug, windowTitle, messageText);
        }
    }

    /**
     *  Logs an debug message to the logger with priority low in category audit.
     *
     *@param  windowTitle   From where message came from
     *@param  structObject  Description of Parameter
     */
    public void debug(String windowTitle, IGUILoggerBusinessProperty businessProperty, Object structObject)
    {
        if ( isDebugOn() && isPropertyOn(businessProperty) )
        {
            if ( debugVerboseLevel.equals(GUILoggerVerboseLevel.HIGH))
            {
                debug(windowTitle, businessProperty, writeObject(structObject));
            }
            else
            {
                debug(windowTitle, businessProperty);
            }
        }
    }

    /**
     *  Logs an debug message to the logger with priority low in category debug.
     *
     *@param  windowTitle        From where message came from
     *@param  structObjectArray  Description of Parameter
     */
    public void debug(String windowTitle, IGUILoggerBusinessProperty businessProperty, Object[] structObjectArray)
    {
        debug(windowTitle, businessProperty, (Object) structObjectArray);
    }

    /**
     *  Calls the exception method with the standard window title and the
     *  exception.
     *
     *@param  t  Exception to obtain message from.
     */
    public void exception(Throwable t)
    {
        exception(t, stdWindowTitle);
    }

    /**
     *  Logs an exception message to the logger with priority high in category
     *  system alarm. Uses the StdMsgType.NonStd to define the type.
     *
     *@param  t            Exception to obtain message from.
     *@param  windowTitle  From where message came from.
     */
    public void exception(Throwable t, String windowTitle)
    {
        exception(windowTitle, null, t);
//        if ( isExceptionOn() )
//        {
//            log(MsgPriority.high, MsgCategory.systemAlarm, windowTitle, StdMsgType.NonStd, t);
//        }
    }

    /**
     *  Calls the exception method with the standard window title, the message
     *  and the exception.
     *
     *@param  message  Message Text
     *@param  t        Exception to obtain message from.
     */
    public void exception(String message, Throwable t)
    {
        exception(stdWindowTitle, message, t);
    }

    /**
     *  Logs an exception message to the logger with priority high in category
     *  system alarm.
     *
     *@param  windowTitle  From where message came from.
     *@param  messageText  Custom message text.
     *@param  t            Exception to obtain message from.
     */
    public void exception(String windowTitle, String messageText, Throwable t)
    {
        exception(windowTitle, messageText, t, null);
//        if ( isExceptionOn() )
//        {
//            log(MsgPriority.high, MsgCategory.systemAlarm, windowTitle, messageText, t);
//        }
    }
    /**
     *  Logs an exception message to the logger with priority high in category
     *  system alarm.
     *
     *@param  windowTitle  From where message came from.
     *@param  userMessageText  Custom message text.
     *@param  t            Exception to obtain message from.
     */
    public void exception(String windowTitle, String userMessageText, Throwable t, Object[] object)
    {
        if (isExceptionOn())
        {
            StringBuffer messageText = new StringBuffer(500);
            messageText.append(userMessageText);
            messageText.append(" : ");

//            log(MsgPriority.high, MsgCategory.systemAlarm, source, messageText, t);
            ExceptionFormatStrategy formatStrategy = CommonFormatFactory.getExceptionFormatStrategy();
//            ExceptionFormatterBase anExceptionFormatterBase = new ExceptionFormatterBase();
            if (getExceptionVerboseLevel().equals(GUILoggerVerboseLevel.LOW))
            {
                messageText.append(formatStrategy.format(t, ExceptionFormatStrategy.SIMPLE_MESSAGE));
            }
            else if (getExceptionVerboseLevel().equals(GUILoggerVerboseLevel.NORMAL))
            {
                messageText.append(formatStrategy.format(t, ExceptionFormatStrategy.DETAIL_MESSAGE));
            }
            else if (getExceptionVerboseLevel().equals(GUILoggerVerboseLevel.HIGH))
            {
                messageText.append(formatStrategy.format(t, ExceptionFormatStrategy.DETAIL_MESSAGE));
                if (object != null)
                {
                    messageText.append("\nData:\n");
                    messageText.append(writeObject(object));
                }
            }
            else
            {
                messageText.append("Verbose mode : ");
                messageText.append(getExceptionVerboseLevel());
                messageText.append(" not defined ");

            }
            log(MsgPriority.high, MsgCategory.systemAlarm, windowTitle, messageText.toString());

        }
    }


    /**
     *  Calls the debug method with the standard window title and the message.
     *
     *@param  messageText  Message
     */
    public void information(String messageText, IGUILoggerBusinessProperty businessProperty)
    {
        information(stdWindowTitle, businessProperty, messageText);
    }

    /**
     *  Logs an information message to the logger with priority low in category
     *  information.
     *
     *@param  windowTitle  From where message came from
     *@param  messageText  Message
     */
    public void information(String windowTitle, IGUILoggerBusinessProperty businessProperty, String messageText)
    {
        if ( isInformationOn() && isPropertyOn(businessProperty) )
        {
            log(MsgPriority.low, MsgCategory.information, windowTitle, messageText);
        }
    }

    /**
     *  Logs an information message to the logger with priority low in category
     *  information.
     *
     *@param  windowTitle   From where message came from
     *@param  structObject  Description of Parameter
     */
    public void information(String windowTitle, IGUILoggerBusinessProperty businessProperty, Object structObject)
    {
        if ( isInformationOn() && isPropertyOn(businessProperty) )
        {
            information(windowTitle, businessProperty, writeObject(structObject));
        }
    }

    /**
     *  Logs an information message to the logger with priority low in category
     *  information.
     *
     *@param  windowTitle        From where message came from
     *@param  structObjectArray  Description of Parameter
     */
    public void information(String windowTitle, IGUILoggerBusinessProperty businessProperty, Object[] structObjectArray)
    {
        information(windowTitle, businessProperty, (Object) structObjectArray);
    }

    /**
     *  Logs the message to the error output stream.
     *
     *@param  message  to log.
     */
    public void logToErrorStream(String message)
    {
        errorOutputStream.println(message);
    }

    /**
     *  Calls the corresponding interface to the logging service method, passing
     *  the standard window title.
     *
     *@param  priority             Description of Parameter
     *@param  category             Description of Parameter
     *@param  standardMessageType  Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, StdMsgType standardMessageType)
    {
        log(priority, category, stdWindowTitle, standardMessageType);
    }

    /**
     *  Calls the corresponding interface to the logging service method, passing
     *  the standard window title.
     *
     *@param  priority    Description of Parameter
     *@param  category    Description of Parameter
     *@param  stdMsgType  Description of Parameter
     *@param  parameters  Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, MsgParameter[] parameters)
    {
        log(priority, category, stdWindowTitle, stdMsgType, parameters);
    }

    /**
     *  Calls the corresponding interface to the logging service method, passing
     *  the standard window title.
     *
     *@param  priority    Description of Parameter
     *@param  category    Description of Parameter
     *@param  stdMsgType  Description of Parameter
     *@param  exception   Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, Throwable exception)
    {
        log(priority, category, stdWindowTitle, stdMsgType, exception);
    }

    /**
     *  Calls the corresponding interface to the logging service method, passing
     *  the standard window title.
     *
     *@param  priority       Description of Parameter
     *@param  category       Description of Parameter
     *@param  messageType    Description of Parameter
     *@param  dateTimeStamp  Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, StdMsgType messageType, Calendar dateTimeStamp)
    {
        log(priority, category, stdWindowTitle, messageType, dateTimeStamp);
    }

    /**
     *  Calls the corresponding interface to the logging service method, passing
     *  the standard window title.
     *
     *@param  priority     Description of Parameter
     *@param  category     Description of Parameter
     *@param  messageText  Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String messageText)
    {
        log(priority, category, stdWindowTitle, messageText);
    }

    /**
     *  Calls the corresponding interface to the logging service method, passing
     *  the standard window title.
     *
     *@param  priority     Description of Parameter
     *@param  category     Description of Parameter
     *@param  messageText  Description of Parameter
     *@param  parameters   Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String messageText, MsgParameter[] parameters)
    {
        log(priority, category, stdWindowTitle, messageText, parameters);
    }

    /**
     *  Provides an interface to the logging service method.
     *
     *@param  priority             Description of Parameter
     *@param  category             Description of Parameter
     *@param  componentNameSuffix  Description of Parameter
     *@param  standardMessageType  Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType standardMessageType)
    {
        if (logger != null)
        {
            logger.log(priority, category, componentNameSuffix, standardMessageType);
        }
        else
        {
            StringBuffer message = new StringBuffer();
            message.append(priority.getUniqueName()).append("; ");
            message.append(category.getUniqueName()).append("; ");
            message.append(componentNameSuffix).append('\n');
            message.append("You have received this here because the Logging Service was unavailable.");

            logToErrorStream(message.toString());
            JOptionPane.showMessageDialog(null, message.toString(), "Log Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     *  Provides an interface to the logging service method.
     *
     *@param  priority             Description of Parameter
     *@param  category             Description of Parameter
     *@param  componentNameSuffix  Description of Parameter
     *@param  stdMsgType           Description of Parameter
     *@param  parameters           Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType, MsgParameter[] parameters)
    {
        if (logger != null)
        {
            logger.log(priority, category, componentNameSuffix, stdMsgType, parameters);
        }
        else
        {
            StringBuffer message = new StringBuffer();
            message.append(priority.getUniqueName()).append("; ");
            message.append(category.getUniqueName()).append("; ");
            message.append(componentNameSuffix).append("; ");

            for (int i = 0; i < parameters.length; i++)
            {
                message.append(parameters[i].toString());

                if ((i + 1) == parameters.length)
                {
                    message.append('\n');
                }
                else
                {
                    message.append("; ");
                }
            }

            message.append("You have received this here because the Logging Service was unavailable.");

            logToErrorStream(message.toString());
            JOptionPane.showMessageDialog(null, message.toString(), "Log Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     *  Provides an interface to the logging service method.
     *
     *@param  priority     Description of Parameter
     *@param  category     Description of Parameter
     *@param  windowTitle  Description of Parameter
     *@param  stdMsgType   Description of Parameter
     *@param  exception    Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String windowTitle, StdMsgType stdMsgType, Throwable exception)
    {
        if (logger != null)
        {
            logger.log(priority, category, windowTitle, stdMsgType, exception);
        }
        else
        {
            StringBuffer message = new StringBuffer();
            message.append(priority.getUniqueName()).append("; ");
            message.append(category.getUniqueName()).append("; ");
            message.append(windowTitle).append("; ");
            message.append(exception.getMessage()).append('\n');
            message.append("You have received this here because the Logging Service was unavailable.");

            logToErrorStream(message.toString());
            JOptionPane.showMessageDialog(null, message.toString(), "Log Message", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     *  Provides an interface to the logging service method.
     *
     *@param  priority             Description of Parameter
     *@param  category             Description of Parameter
     *@param  componentNameSuffix  Description of Parameter
     *@param  messageType          Description of Parameter
     *@param  dateTimeStamp        Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType messageType, Calendar dateTimeStamp)
    {
        if (logger != null)
        {
            logger.log(priority, category, componentNameSuffix, messageType, dateTimeStamp);
        }
        else
        {
            StringBuffer message = new StringBuffer();
            message.append(priority.getUniqueName()).append("; ");
            message.append(category.getUniqueName()).append("; ");
            message.append(componentNameSuffix).append("; ");
            message.append(dateFormatterThreadLocal.get().format(dateTimeStamp.getTime())).append('\n');
            message.append("You have received this here because the Logging Service was unavailable.");

            logToErrorStream(message.toString());
            JOptionPane.showMessageDialog(null, message.toString(), "Log Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     *  Provides an interface to the logging service method.
     *
     *@param  priority             Description of Parameter
     *@param  category             Description of Parameter
     *@param  componentNameSuffix  Description of Parameter
     *@param  messageText          Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String messageText)
    {
        if (logger != null)
        {
            logger.log(priority, category, componentNameSuffix, messageText);
        }
        else
        {
            StringBuffer message = new StringBuffer();
            message.append(priority.getUniqueName()).append("; ");
            message.append(category.getUniqueName()).append("; ");
            message.append(componentNameSuffix).append("; ");
            message.append(messageText).append('\n');
            message.append("You have received this here because the Logging Service was unavailable.");

            logToErrorStream(message.toString());
            JOptionPane.showMessageDialog(null, message.toString(), "Log Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     *  Provides an interface to the logging service method.
     *
     *@param  priority             Description of Parameter
     *@param  category             Description of Parameter
     *@param  componentNameSuffix  Description of Parameter
     *@param  messageText          Description of Parameter
     *@param  parameters           Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String messageText, MsgParameter[] parameters)
    {
        if (logger != null)
        {
            logger.log(priority, category, componentNameSuffix, messageText, parameters);
        }
        else
        {
            StringBuffer builtMessage = new StringBuffer();
            builtMessage.append(priority.getUniqueName()).append("; ");
            builtMessage.append(category.getUniqueName()).append("; ");
            builtMessage.append(componentNameSuffix).append("; ");
            builtMessage.append(messageText).append("; ");

            for (int i = 0; i < parameters.length; i++)
            {
                builtMessage.append(parameters[i].toString());

                if ((i + 1) == parameters.length)
                {
                    builtMessage.append('\n');
                }
                else
                {
                    builtMessage.append("; ");
                }
            }

            builtMessage.append("You have received this here because the Logging Service was unavailable.");

            logToErrorStream(builtMessage.toString());
            JOptionPane.showMessageDialog(null, builtMessage.toString(), "Log Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     *  Provides an interface to the logging service method.
     *
     *@param  priority             Description of Parameter
     *@param  category             Description of Parameter
     *@param  componentNameSuffix  Description of Parameter
     *@param  message              Description of Parameter
     *@param  exception            Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String message, Throwable exception)
    {
        if (logger != null)
        {
            logger.log(priority, category, componentNameSuffix, message, exception);
        }
        else
        {
            StringBuffer builtMessage = new StringBuffer();
            builtMessage.append(priority.getUniqueName()).append("; ");
            builtMessage.append(category.getUniqueName()).append("; ");
            builtMessage.append(componentNameSuffix).append("; ");
            builtMessage.append(message).append("; ");
            builtMessage.append(exception.getMessage()).append('\n');
            builtMessage.append("You have received this here because the Logging Service was unavailable.");

            logToErrorStream(builtMessage.toString());
            JOptionPane.showMessageDialog(null, builtMessage.toString(), "Log Message", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     *  Provides an interface to the logging service method.
     *
     *@param  priority             Description of Parameter
     *@param  category             Description of Parameter
     *@param  componentNameSuffix  Description of Parameter
     *@param  message              Description of Parameter
     *@param  dateTimeStamp        Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String message, Calendar dateTimeStamp)
    {
        if (logger != null)
        {
        	//The 'if case' doesn't behave like the else case. It will not display any UI popup error but simply log it into a file.
            logger.log(priority, category, componentNameSuffix, message, dateTimeStamp);
        }
        else
        {
            StringBuffer builtMessage = new StringBuffer();
            builtMessage.append(priority.getUniqueName()).append("; ");
            builtMessage.append(category.getUniqueName()).append("; ");
            builtMessage.append(componentNameSuffix).append("; ");
            builtMessage.append(message).append("; ");
            builtMessage.append(dateFormatterThreadLocal.get().format(dateTimeStamp.getTime())).append('\n');
            builtMessage.append("You have received this here because the Logging Service was unavailable.");

            logToErrorStream(builtMessage.toString());
            JOptionPane.showMessageDialog(null, builtMessage.toString(), "Log Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     *  Calls the corresponding interface to the logging service method, passing
     *  the standard window title.
     *
     *@param  priority   Description of Parameter
     *@param  category   Description of Parameter
     *@param  message    Description of Parameter
     *@param  exception  Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String message, Throwable exception)
    {
        log(priority, category, stdWindowTitle, message, exception);
    }

    /**
     *  Calls the corresponding interface to the logging service method, passing
     *  the standard window title.
     *
     *@param  priority       Description of Parameter
     *@param  category       Description of Parameter
     *@param  message        Description of Parameter
     *@param  dateTimeStamp  Description of Parameter
     */
    protected void log(MsgPriority priority, MsgCategory category, String message, Calendar dateTimeStamp)
    {
        log(priority, category, stdWindowTitle, message, dateTimeStamp);
    }


    /**
     *  Utility to call the reflective object write.
     *  Note that the Object object parameter can be an Object []
     *  since Object [] is an Object
     *
     *@param  object  Description of Parameter
     *@return         Description of the Returned Value
     */
    private String writeObject(Object object)
    {
        StringWriter structStringWriter = new StringWriter();
        String contentsStringWriter = null;

        try
        {
            //have the holdWindowTitle passed in a blank, the 2nd parameter
            ReflectiveObjectWriter.writeObject(object, " ", structStringWriter);
            contentsStringWriter = structStringWriter.toString();
        }
        catch (IOException e)
        {
            log(MsgPriority.high, MsgCategory.systemAlarm, stdWindowTitle, "Exception in Call to ReflectiveObjectWriter.writeObject(structObject, windowTitle, structStringWriter) from AbstractGUILoger", e);
        }

        return contentsStringWriter;
    }
}
