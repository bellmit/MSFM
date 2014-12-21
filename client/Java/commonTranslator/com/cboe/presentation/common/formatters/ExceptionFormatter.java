//
// -----------------------------------------------------------------------------------
// Source file: ExceptionFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.io.*;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.NotSupportedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.presentation.common.formatters.ExceptionFormatStrategy;

import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Implements the ExceptionFormatStrategy
 */
class ExceptionFormatter extends Formatter implements ExceptionFormatStrategy
{
    private static final String ALREADY_EXISTS         = "The system has returned an AlreadyExistsException.";
    private static final String AUTHENTICATION_FAILED  = "The system has returned an AuthenticationException.";
    private static final String AUTHORIZATION_FAILED   = "The system has returned an AuthorizationException.";
    private static final String COMMUNICATION_FAILURE  = "The system has returned a CommunicationException.";
    private static final String DATA_INVALID           = "The system has returned a DataValidationException.";
    private static final String NOT_ACCEPTED           = "The system has returned a NotAcceptedException.";
    private static final String NOT_FOUND              = "The system has returned a NotFoundException.";
    private static final String NOT_SUPPORTED          = "The system has returned a NotSupportedException.";
    private static final String SYSTEM_FAILURE         = "The system has returned a SystemException.";
    private static final String TRANSACTION_FAILURE    = "The system has returned a TransactionFailedException.";
    private static final String UNCAUGHT_EXCEPTION     = "This application encountered an uncaught exception.";
    private static final String UNKNOWN_USER_EXCEPTION = "The system has returned an unknown exception of type org.omg.CORBA.UserException.";
    private static final String NULL_EXCEPTION         = "WARNING: The exception to format was NULL.";

    /**
     * Constructor, defines styles and sets initial default style
     */
    public ExceptionFormatter()
    {
        super();

        addStyle(SIMPLE_MESSAGE, SIMPLE_MESSAGE_DESCRIPTION);
        addStyle(DETAIL_MESSAGE, DETAIL_MESSAGE_DESCRIPTION);
        addStyle(FULL_MESSAGE,FULL_MESSAGE_DESCRIPTION);
        addStyle(STACK_TRACE_MESSAGE, STACK_TRACE_MESSAGE_DESCRIPTION);

        setDefaultStyle(FULL_MESSAGE);
    }

    /**
     * Formats a Throwable
     * @param throwable to format
     */
    public String format(Throwable throwable)
    {
        return format(throwable, getDefaultStyle());
    }

    /**
     * Formats a Throwable
     * @param throwable to format
     * @param styleName to use for formatting
     */
    public String format(Throwable throwable, String styleName)
    {
        validateStyle(styleName);
        String exceptionText = null;

        if(styleName.equals(SIMPLE_MESSAGE))
        {
            exceptionText = formatSimpleMessage(throwable);
        }
        else if(styleName.equals(DETAIL_MESSAGE))
        {
            exceptionText = formatDetailMessage(throwable);
        }
        else if(styleName.equals(FULL_MESSAGE) )
        {
            exceptionText = formatFullMessage(throwable);
        }
        else if(styleName.equals(STACK_TRACE_MESSAGE))
        {
            exceptionText = getStackTrace(throwable);
        }
        return exceptionText;
    }

    /**
     * Formats a Throwable as FULL_MESSAGE
     * @param throwable to format
     * @return formatted string
     */
    private String formatFullMessage(Throwable throwable)
    {
        return formatSimpleMessage(throwable) + '\n' + formatDetailMessage(throwable);
    }

    /**
     * Formats a Throwable as SIMPLE_MESSAGE
     * @param throwable to format
     * @return formatted string
     */
    private String formatSimpleMessage(Throwable throwable)
    {
        StringBuffer simpleMessage = new StringBuffer(100);

        if(throwable == null)
        {
            simpleMessage.append(NULL_EXCEPTION);
        }
        else if(throwable instanceof AlreadyExistsException)
        {
            simpleMessage.append(ALREADY_EXISTS);
        }
        else if(throwable instanceof AuthenticationException)
        {
            simpleMessage.append(AUTHENTICATION_FAILED);
        }
        else if(throwable instanceof AuthorizationException)
        {
            simpleMessage.append(AUTHORIZATION_FAILED);
        }
        else if(throwable instanceof CommunicationException)
        {
            simpleMessage.append(COMMUNICATION_FAILURE);
        }
        else if(throwable instanceof DataValidationException)
        {
            simpleMessage.append(DATA_INVALID);
        }
        else if(throwable instanceof NotAcceptedException)
        {
            simpleMessage.append(NOT_ACCEPTED);
        }
        else if(throwable instanceof NotFoundException)
        {
            simpleMessage.append(NOT_FOUND);
        }
        else if(throwable instanceof NotSupportedException)
        {
            simpleMessage.append(NOT_SUPPORTED);
        }
        else if(throwable instanceof SystemException)
        {
            simpleMessage.append(SYSTEM_FAILURE);
        }
        else if(throwable instanceof TransactionFailedException)
        {
            simpleMessage.append(TRANSACTION_FAILURE);
        }
        else if(throwable instanceof UserException)
        {
            simpleMessage.append(UNKNOWN_USER_EXCEPTION);
        }
        else if(throwable instanceof Exception)
        {
//            simpleMessage.append(UNCAUGHT_EXCEPTION).append(" ").append(((Exception)throwable).toString());
            simpleMessage.append(UNCAUGHT_EXCEPTION);
        }
        else // It is a Throwable
        {
//            simpleMessage.append(UNCAUGHT_EXCEPTION).append(" ").append(throwable.toString());
            simpleMessage.append(UNCAUGHT_EXCEPTION);
        }

        return simpleMessage.toString();
    }

    /**
     * Formats a Throwable as DETAIL_MESSAGE
     * @param throwable to format
     * @return formatted string
     */
    private String formatDetailMessage(Throwable throwable)
    {
        StringBuffer detailMessage = new StringBuffer(1000);
        ExceptionDetails details = null;

        if(throwable == null)
        {
            detailMessage.append( NULL_EXCEPTION );
        }
        else if(throwable instanceof AlreadyExistsException)
        {
            details = ((AlreadyExistsException)throwable).details;
        }
        else if(throwable instanceof AuthenticationException)
        {
            details = ((AuthenticationException)throwable).details;
        }
        else if(throwable instanceof AuthorizationException)
        {
            details = ((AuthorizationException)throwable).details;
        }
        else if(throwable instanceof CommunicationException)
        {
            details = ((CommunicationException)throwable).details;
        }
        else if(throwable instanceof DataValidationException)
        {
            details = ((DataValidationException)throwable).details;
        }
        else if(throwable instanceof NotAcceptedException)
        {
            details = ((NotAcceptedException)throwable).details;
        }
        else if(throwable instanceof NotFoundException)
        {
            details = ((NotFoundException)throwable).details;
        }
        else if(throwable instanceof NotSupportedException)
        {
            details = ((NotSupportedException)throwable).details;
        }
        else if(throwable instanceof SystemException)
        {
            details = ((SystemException)throwable).details;
        }
        else if(throwable instanceof TransactionFailedException)
        {
            details = ((TransactionFailedException)throwable).details;
        }
        else if(throwable instanceof UserException)
        {
            detailMessage.append("Message: ");
            detailMessage.append(throwable.getMessage());
            detailMessage.append('\n').append(getStackTrace(throwable));
        }
        else if(throwable instanceof Exception)
        {
            detailMessage.append(getStackTrace(throwable));
        }
        else //It is a Throwable
        {
            detailMessage.append(getStackTrace(throwable));
        }

        // Condition detailes != null will be true only for specific instances of
        // the UserException, i.e. TransactionFailedException, NotFoundException, etc.
        if ( details != null )
        {
            detailMessage.append(formatExceptionDetails(details));
            detailMessage.append('\n').append(getStackTrace(throwable));
        }

        return detailMessage.toString();
    }

    /**
     * Formats the contents of an ExceptionDetails into a String
     * @param details to format
     * @return formatted details
     */
    private static String formatExceptionDetails(ExceptionDetails details)
    {
        StringBuffer buffer = new StringBuffer();

        // Format message
        buffer.append("Message: ");
        if(details.message != null )
        {
            buffer.append(details.message);
        }
        else
        {
            buffer.append("ERROR: String is NULL");
        }

        // Format time
        buffer.append("\nTime: ");
        if(details.dateTime != null)
        {
            buffer.append(details.dateTime);
        }
        else
        {
            buffer.append("ERROR: ExceptionDetails.dateTime is NULL");
        }

        // Format severity
        buffer.append("\nSeverity level: ");
        buffer.append(String.valueOf(details.severity));

        // Format error code
        buffer.append("\nError code: ");
        buffer.append(String.valueOf(details.error));

        return buffer.toString();
    }

    /**
     * Obtains the stack trace as a string
     * @param throwable to obtain stack trace from
     * @return stack trace
     */
    private String getStackTrace(Throwable throwable)
    {
        String message;

        StringWriter stringWriter = new StringWriter(200);
        PrintWriter printWriter = new PrintWriter(stringWriter, true);

        throwable.printStackTrace(printWriter);

        printWriter.flush();
        stringWriter.flush();

        message = stringWriter.toString();

        try
        {
            printWriter.close();
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(this.getClass().getName() + ":getStackTrace()",
                                           "Could not close PrintWriter", e);
        }

        try
        {
            stringWriter.close();
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(this.getClass().getName() + ":getStackTrace()",
                                           "Could not close StringWriter", e);
        }

        return message;
    }
}
