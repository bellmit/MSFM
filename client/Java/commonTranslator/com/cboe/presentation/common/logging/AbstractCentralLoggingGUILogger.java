//
// -----------------------------------------------------------------------------------
// Source file: AbstractCentralLoggingGUILogger.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javax.swing.*;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

import com.cboe.loggingService.GUILoggerController;
import com.cboe.loggingService.InitializationFailedException;
import com.cboe.loggingService.LoggingComponentNotFoundException;
import com.cboe.loggingService.LoggingDispatcher;
import com.cboe.loggingService.LoggingServiceImpl;
import com.cboe.loggingService.MsgCategory;
import com.cboe.loggingService.MsgFilter;
import com.cboe.loggingService.MsgPriority;

/**
 *  This class provides a proxy with different method signatures for convenience
 *  to the Logging Service. You must provide your own implementation for
 *  initializing.
 *
 *@author     Martha Fourt
 *@created    March 7, 2007
 */
public abstract class AbstractCentralLoggingGUILogger extends AbstractGUILogger
{
    protected static final String CENTRAL_LOGGING_COMPONENT_KEY_NAME = "LogServiceCentralLoggingComponent";
    protected static final String CENTRAL_LOGGING_PROPERTIES_FILE_KEY_NAME = "LogServiceCentralLoggingFileName";

    protected LoggingDispatcher centralLoggingLogger = null;
    private   static Properties centralLoggingProperties = null;
    private   static boolean hasErrorPoppedUpYet = false;
    private GUILoggerController proxy = null;  	

    /**
     *
     */
    public AbstractCentralLoggingGUILogger()
    {
        super();
    }

    /**
     * Initialize the central logging agent and component.  
     *	(Captures log messages on a remote database.)
     * This method must be called AFTER the Foundation Framework has initialized.
     *	
     * @since 20070305 MMF (Martha Fourt)
     */
    public void initCentralLogging() throws InitializationFailedException
    {
        String methodName = "AbstractCentralLoggingGUILogger::initCentralLogging: ";
        String loggingPropertiesFileName = null;
        String loggingComponentName = null;
        MsgFilter filter = new MsgFilter();
        String errMsg = null;

	    if (logger != null) {
            if(AppPropertiesFileFactory.isAppPropertiesAvailable())
            {
		        loggingPropertiesFileName = AppPropertiesFileFactory.find().getValue(PROPERTIES_SECTION_NAME, CENTRAL_LOGGING_PROPERTIES_FILE_KEY_NAME);
		        if (loggingPropertiesFileName == null) 
		        {
		            errMsg = methodName + CENTRAL_LOGGING_PROPERTIES_FILE_KEY_NAME + " is missing from the " + PROPERTIES_SECTION_NAME +
			            " section of the application properties file.";
       		        throw new InitializationFailedException(errMsg);	
		        }

                // Load the initialization properties
                centralLoggingProperties = readInitProperties( loggingPropertiesFileName );
                loggingComponentName = AppPropertiesFileFactory.find().getValue(PROPERTIES_SECTION_NAME, CENTRAL_LOGGING_COMPONENT_KEY_NAME);
                if(loggingComponentName != null  &&  loggingComponentName.length() > 0)
                {
                    try
                    {
                        centralLoggingLogger = (LoggingDispatcher) LoggingServiceImpl.getInstance(loggingComponentName);
                        proxy = new GUILoggerController(logger);
		                proxy.initializeLoggingAgents(centralLoggingLogger, centralLoggingProperties, filter);	
                    }
                    catch(LoggingComponentNotFoundException e)
                    {
                        errMsg = methodName + "The logging component name specified as " + loggingComponentName
                                                  + ", could not be found.";
         		        throw new InitializationFailedException(errMsg);	
	                }
                }
            }

     	} 
     	else 
     	{
            System.err.println(methodName + "logger has not been initialized, so will not capture central logging.");
	    }


    } // end method initCentralLoggingLogger


    /**
	Read the initialization properties.
    */
    private Properties readInitProperties(String absFilePath) throws InitializationFailedException {
        logger.log(MsgPriority.low, MsgCategory.audit, "readInitProperties()" );

        // Get the input stream for the logging service initialization properties.
        FileInputStream initPropsInStream = null;
        try 
        {
            initPropsInStream = new FileInputStream( absFilePath );
        }
        catch( FileNotFoundException e ) 
        {
            throw new InitializationFailedException( "Could not find the initialization file \"" + absFilePath + "\"." );
        }

        Properties initProps = new Properties();
        try 
        {
            initProps.load( initPropsInStream );
        }
        catch( IOException e ) 
        {
            throw new InitializationFailedException( "Could not read the initialization properties file." );
        }

        logger.log(MsgPriority.low, MsgCategory.audit, "Successfully read initialization properties for the central logging service." );

        return initProps;
    }


    /**
     *  Logs an audit message to the central logging repository with priority medium in category
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
            logToCentralLogging(MsgPriority.medium, MsgCategory.audit, title.toString(), messageText);
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
    protected void logToCentralLogging(MsgPriority priority, MsgCategory category, String componentNameSuffix, String messageText)
    {
        if (centralLoggingLogger != null)
        {
            centralLoggingLogger.log(priority, category, componentNameSuffix, messageText);
        }
        else
        {
            StringBuffer message = new StringBuffer();
            message.append(priority.getUniqueName()).append("; ");
            message.append(category.getUniqueName()).append("; ");
            message.append(componentNameSuffix).append("; ");
            message.append(messageText).append('\n');
            message.append("You have received this here because the Central Logging Service was unavailable.");
            logToErrorStream(message.toString());

            if (!hasErrorPoppedUpYet)
            {
                hasErrorPoppedUpYet = true; 
                JOptionPane.showMessageDialog(null, message.toString(), "Log Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
