package com.cboe.domain.util;

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



/**
 * Implements the ExceptionFormatStrategy
 */
public class ExceptionFormatterBase
{
    private static final String ALREADY_EXISTS         = "The system has returned a AlreadyExistsExcpetion.";
    private static final String AUTHENTICATION_FAILED  = "The system has returned a AuthenticationException.";
    private static final String AUTHORIZATION_FAILED   = "The system has returned a AuthorizationException.";
    private static final String COMMUNICATION_FAILURE  = "The system has returned a CommunicationException.";
    private static final String DATA_INVALID           = "The system has returned a DataValidationException.";
    private static final String NOT_ACCEPTED           = "The system has returned a NotAcceptedException.";
    private static final String NOT_FOUND              = "The system has returned a NotFoundException.";
    private static final String NOT_SUPPORTED          = "The system has returned a NotSupportedException.";
    private static final String SYSTEM_FAILURE         = "The system has returned a SystemException.";
    private static final String TRANSACTION_FAILURE    = "The system has returned a TransactionFailedException.";
    private static final String UNCAUGHT_EXCEPTION     = "This application encountered an uncaught exception.";
    private static final String UNKNOWN_USER_EXCEPTION = "The system has returned an unknown exception of type org.omg.CORBA.UserException.";


    /**
     * Constructor, defines styles and sets initial default style
     */
    public ExceptionFormatterBase()
    {
        super();
    }

    /**
     * Formats a Throwable as FULL_MESSAGE
     * @param Throwable t to format
     * @return formatted string
     */
    public String formatFullMessage(Throwable t)
    {
        return formatSimpleMessage(t) + '\n' + formatDetailMessage(t);
    }
    /**
     * Formats a Throwable as SIMPLE_MESSAGE
     * @param Throwable t to format
     * @return formatted string
     */
    public String formatSimpleMessage(Throwable t)
    {
        StringBuilder simpleMessage = new StringBuilder(80);

        if(t instanceof AlreadyExistsException)
        {
            simpleMessage.append(ALREADY_EXISTS);
        }
        else if(t instanceof AuthenticationException)
        {
            simpleMessage.append(AUTHENTICATION_FAILED);
        }
        else if(t instanceof AuthorizationException)
        {
            simpleMessage.append(AUTHORIZATION_FAILED);
        }
        else if(t instanceof CommunicationException)
        {
            simpleMessage.append(COMMUNICATION_FAILURE);
        }
        else if(t instanceof DataValidationException)
        {
            simpleMessage.append(DATA_INVALID);
        }
        else if(t instanceof NotAcceptedException)
        {
            simpleMessage.append(NOT_ACCEPTED);
        }
        else if(t instanceof NotFoundException)
        {
            simpleMessage.append(NOT_FOUND);
        }
        else if(t instanceof NotSupportedException)
        {
            simpleMessage.append(NOT_SUPPORTED);
        }
        else if(t instanceof SystemException)
        {
            simpleMessage.append(SYSTEM_FAILURE);
        }
        else if(t instanceof TransactionFailedException)
        {
            simpleMessage.append(TRANSACTION_FAILURE);
        }
        else if(t instanceof UserException)
        {
            simpleMessage.append(UNKNOWN_USER_EXCEPTION);
        }
        else if(t instanceof Exception)
        {
//            simpleMessage.append(UNCAUGHT_EXCEPTION).append(" ").append(((Exception)t).toString());
            simpleMessage.append(UNCAUGHT_EXCEPTION);
        }
        else // It is a Throwable
        {
//            simpleMessage.append(UNCAUGHT_EXCEPTION).append(" ").append(t.toString());
            simpleMessage.append(UNCAUGHT_EXCEPTION);
        }

        return simpleMessage.toString();
    }
    /**
     * Formats a Throwable as DETAIL_MESSAGE
     * @param Throwable t to format
     * @return formatted string
     */
    public String formatDetailMessage(Throwable t)
    {
        StringBuilder detailMessage = new StringBuilder(80);
        ExceptionDetails details = null;

        if(t instanceof AlreadyExistsException)
        {
            details = ((AlreadyExistsException)t).details;
        }
        else if(t instanceof AuthenticationException)
        {
            details = ((AuthenticationException)t).details;
        }
        else if(t instanceof AuthorizationException)
        {
            details = ((AuthorizationException)t).details;
        }
        else if(t instanceof CommunicationException)
        {
            details = ((CommunicationException)t).details;
        }
        else if(t instanceof DataValidationException)
        {
            details = ((DataValidationException)t).details;
        }
        else if(t instanceof NotAcceptedException)
        {
            details = ((NotAcceptedException)t).details;
        }
        else if(t instanceof NotFoundException)
        {
            details = ((NotFoundException)t).details;
        }
        else if(t instanceof NotSupportedException)
        {
            details = ((NotSupportedException)t).details;
        }
        else if(t instanceof SystemException)
        {
            details = ((SystemException)t).details;
        }
        else if(t instanceof TransactionFailedException)
        {
            details = ((TransactionFailedException)t).details;
        }
        else if(t instanceof UserException)
        {
            detailMessage.append("Message: ");
            if(((UserException)t).getMessage() != null)
            {
                detailMessage.append(((UserException)t).getMessage());
            }
            else
            {
                detailMessage.append("Message is NULL");
            }
            detailMessage.append('\n').append(getStackTrace((UserException)t));
        }
        else if(t instanceof Exception)
        {
            detailMessage.append(getStackTrace((Exception)t));
        }
        else //It is a Throwable
        {
            detailMessage.append(getStackTrace(t));
        }

        // Condition detailes != null will be true only for specific instances of
        // the UserException, i.e. TransactionFailedException, NotFoundException, etc.
        if ( details != null )
        {
            detailMessage.append(formatExceptionDetails(details));
            detailMessage.append('\n').append(getStackTrace(t));
        }

        return detailMessage.toString();
    }

    /**
     * Formats the contents of an ExceptionDetails into a String
     * @param details to format
     * @return formatted details
     */
    public static String formatExceptionDetails(ExceptionDetails details)
    {
        StringBuilder buffer = new StringBuilder(200);

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
     * @param t Throwable to obtain stack trace from
     * @return stack trace
     */
    public static String getStackTrace(Throwable t)
    {
        String message = null;

        StringWriter stringWriter = new StringWriter(200);
        PrintWriter printWriter = new PrintWriter(stringWriter, true);

        t.printStackTrace(printWriter);

        printWriter.flush();
        stringWriter.flush();

        message = stringWriter.toString();

        try
        {
            printWriter.close();
        }
        catch(Exception e)
        {
//            GUILoggerHome.find().exception("com.cboe.presentation.utility.getStackTrace()","",e);
            System.err.println("Exception "+e.getMessage()+"\n while calling printWriter.close()");
        }
        try
        {
            stringWriter.close();
        }
        catch(Exception e)
        {
//            GUILoggerHome.find().exception("com.cboe.presentation.utility.getStackTrace()","",e);
            System.err.println("Exception "+e.getMessage()+"\n while calling stringWriter.close()");
        }

        return message;
    }

}
