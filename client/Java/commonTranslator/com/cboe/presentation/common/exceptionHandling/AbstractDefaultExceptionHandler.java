//
// -----------------------------------------------------------------------------------
// Source file: AbstractDefaultExceptionHandler.java
//
// PACKAGE: com.cboe.presentation.common.exceptionHandling;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.exceptionHandling;

import org.omg.CORBA.UserException;

import com.cboe.interfaces.presentation.common.exceptionHandling.DefaultExceptionHandler;

import com.cboe.presentation.common.formatters.CommonFormatFactory;
import com.cboe.interfaces.presentation.common.formatters.ExceptionFormatStrategy;
import com.cboe.presentation.common.logging.GUILoggerHome;

/*
 * Provides an abstract implementation of DefaultExceptionHandler
 */
public abstract class AbstractDefaultExceptionHandler implements DefaultExceptionHandler
{
    /**
     * Notifies the user of the exception.
     * @param simpleMessage
     * @param detailMessage
     */
    protected abstract void notifyUser(String simpleMessage, String detailMessage);

    /**
     * Notifies the user of the exception.
     * @param throwable to notify user about. Simple and detailed message can be
     * obtained from the Throwable.
     */
    protected abstract void notifyUser(Throwable throwable);

    /**
     * Notifies the user of the exception.
     * @param throwable to notify user about. Simple and detailed message can be
     * obtained from the Throwable.
     * @param userMessage optional message
     */
    protected abstract void notifyUser(Throwable throwable, String userMessage);

    /**
     * Processes a standard throwable
     * @param throwable Throwable object to handle
     */
    public synchronized void process(Throwable throwable)
    {
        process(throwable, null);
    }

    /**
     * Processes a standard throwable
     * @param throwable Throwable object to handle
     * @param userMessage optional message
     */
    public synchronized void process(Throwable throwable, String userMessage)
    {
        try
        {
            StringBuffer detailMessage = new StringBuffer();
            StringBuffer simpleMessage = new StringBuffer();

            ExceptionFormatStrategy strategy = CommonFormatFactory.getExceptionFormatStrategy();

            if(userMessage != null && userMessage.length() > 0)
            {
                simpleMessage.append(userMessage).append(' ');
            }

            String simpleException = strategy.format(throwable, ExceptionFormatStrategy.SIMPLE_MESSAGE);
            simpleMessage.append(simpleException);

            detailMessage.append(strategy.format(throwable, ExceptionFormatStrategy.DETAIL_MESSAGE));

            logMessage(throwable, userMessage);
            notifyUser(simpleMessage.toString(), detailMessage.toString());
        }
        catch(Exception totalFailureException)
        {
            // This is really bad.  Caught an exception while logging or displaying
            // the message box.  Just dump what we can to stdout.
            System.err.println(TOTAL_FAILURE);
            System.err.println(throwable);
        }
    }

    /**
     * Processes a standard UserException
     * @param userException to handle
     */
    public synchronized void process(UserException userException)
    {
        process(userException, null);
    }

    /**
     * Processes a standard UserException
     * @param userException to handle
     * @param userMessage optional message
     */
    public synchronized void process(UserException userException, String userMessage)
    {
        process((Throwable)userException, userMessage);
    }

    /**
     * Logs the message to the GUI logger.
     * @param message to log
     */
    protected void logMessage(String message)
    {
        GUILoggerHome.find().alarm(message);
    }

    /**
     * Logs the throwable to the GUI logger.
     * @param throwable to log
     * @param userMessage optional user message
     */
    protected void logMessage(Throwable throwable, String userMessage)
    {
        String message;

        ExceptionFormatStrategy strategy = CommonFormatFactory.getExceptionFormatStrategy();
        String exceptionMessage = strategy.format(throwable, ExceptionFormatStrategy.SIMPLE_MESSAGE);

        if(userMessage != null && userMessage.length() > 0)
        {
            message = userMessage + "\n" + exceptionMessage;
        }
        else
        {
            message = exceptionMessage;
        }

        GUILoggerHome.find().exception(message, throwable);
    }

    /**
     * Logs the throwable to the GUI logger.
     * @param throwable to log
     */
    protected void logMessage(Throwable throwable)
    {
        logMessage(throwable, null);
    }
}