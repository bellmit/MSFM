//
// -----------------------------------------------------------------------------------
// Source file: ARCommandResponseImpl.java
//
// PACKAGE: com.cboe.presentation.adminRequest
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.NotSupportedException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.instrumentation.adminRequest.ARCommandResponse;
import com.cboe.interfaces.instrumentation.adminRequest.ExecutionResult;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.processes.CBOEProcess;
import com.cboe.interfaces.domain.dateTime.DateTime;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.dateTime.DateTimeImpl;

import com.cboe.infrastructureServices.interfaces.adminService.Command;

/**
 * Implements the ARCommand response
 */
class ARCommandResponseImpl implements ARCommandResponse
{
    private String orbName;
    private DateTime dateTime;
    private ExecutionResult result;
    private Exception exception;

    private ARCommandResponseImpl(String orbName)
    {
        result = null;
        exception = null;

        if(orbName == null || orbName.length() == 0)
        {
            throw new IllegalArgumentException("orbName may not be null or empty.");
        }
        this.orbName = orbName;

        dateTime = new DateTimeImpl(System.currentTimeMillis());
    }

    ARCommandResponseImpl(ExecutionResult result, String orbName)
    {
        this(orbName);
        if(result == null)
        {
            throw new IllegalArgumentException("result may not be null.");
        }
        this.result = result;
    }

    ARCommandResponseImpl(Exception exception, String orbName)
    {
        this(orbName);
        if(exception == null)
        {
            throw new IllegalArgumentException("exception may not be null.");
        }
        this.exception = exception;
    }

    /**
     * The ORB name that this response is for.
     * @return ORB name
     */
    public String getOrbName()
    {
        return orbName;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append("ARCommandResponseImpl");
        sb.append("{orbName='").append(getOrbName()).append('\'');
        sb.append(", isSuccess=").append(isSuccess());
        sb.append(", dateTime=").append(getResponseTime());
        sb.append('}');
        return sb.toString();
    }

    /**
     * Convenience method to get the CBOEProcess for the ORB name that this response is for.
     * @return CBOEProcess for the ORB name
     * @throws com.cboe.exceptions.DataValidationException may be thrown if the CBOEProcess could
     * not be obtained
     */
    public CBOEProcess getOrbProcess() throws DataValidationException
    {
        return InstrumentationTranslatorFactory.find().getProcess(getOrbName(), null);
    }

    /**
     * Gets the time the response was received.
     * @return DateTime that represents when the response was received.
     */
    public DateTime getResponseTime()
    {
        return dateTime;
    }

    /**
     * Determines whether the ExecutionResult was a success.
     * @return If no exception occurred, then the value of isSuccess() from the ExecutionResult
     * is returned. If an exception occurred, false is returned.
     */
    public boolean isSuccess()
    {
        boolean success = false;
        if(result != null)
        {
            success = result.isSuccess();
        }
        return success;
    }

    /**
     * Convenience method to return the single String value from the ExecutionResult.
     * @return Single String value from the ExecutionResult's Command object.
     * If the ExecutionResult's Command object has at least one returned DataItem value,
     * then the first elements value is returned. Otherwise, an empty String is returned.
     * @throws org.omg.CORBA.UserException represents an exception thrown from executing the
     * command
     * @throws com.cboe.exceptions.CommunicationException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.SystemException represents an exception thrown from executing the
     * command
     * @throws com.cboe.exceptions.AuthorizationException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.DataValidationException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.AlreadyExistsException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.AuthenticationException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.NotAcceptedException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.NotFoundException represents an exception thrown from executing
     * the command
     * @throws com.cboe.exceptions.NotSupportedException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.TransactionFailedException represents an exception thrown from
     * executing the command
     * @throws com.cboe.interfaces.presentation.api.TimedOutException represents an exception thrown
     * from executing the command
     */
    public String getReturnValue() throws UserException, CommunicationException, SystemException,
            AuthorizationException, DataValidationException, AlreadyExistsException,
            AuthenticationException, NotAcceptedException, NotFoundException, NotSupportedException,
            TransactionFailedException, TimedOutException
    {
        String value;
        Command commandResult = getExecutionResult().getCommandResult();
        if(commandResult.retValues.length > 0)
        {
            value = commandResult.retValues[0].value;
        }
        else
        {
            value = "";
        }
        return value;
    }

    /**
     * Gets the full ExecutionResult from the command being executed.
     * @return ExecutionResult from the command being executed, if no exceptions occurred and the
     *         execution was success.
     * @throws org.omg.CORBA.UserException represents an exception thrown from executing the
     * command
     * @throws com.cboe.exceptions.CommunicationException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.SystemException represents an exception thrown from executing the
     * command
     * @throws com.cboe.exceptions.AuthorizationException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.DataValidationException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.AlreadyExistsException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.AuthenticationException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.NotAcceptedException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.NotFoundException represents an exception thrown from executing
     * the command
     * @throws com.cboe.exceptions.NotSupportedException represents an exception thrown from
     * executing the command
     * @throws com.cboe.exceptions.TransactionFailedException represents an exception thrown from
     * executing the command
     * @throws com.cboe.interfaces.presentation.api.TimedOutException represents an exception thrown
     * from executing the command
     */
    public ExecutionResult getExecutionResult() throws UserException, CommunicationException,
            SystemException, AuthorizationException, DataValidationException,
            AlreadyExistsException, AuthenticationException, NotAcceptedException,
            NotFoundException, NotSupportedException, TransactionFailedException, TimedOutException
    {
        processException();
        return result;
    }

    @SuppressWarnings({"InstanceofInterfaces", "OverlyComplexMethod"})
    protected void processException() throws UserException, CommunicationException, SystemException,
            AuthorizationException, DataValidationException, AlreadyExistsException,
            AuthenticationException, NotAcceptedException, NotFoundException, NotSupportedException,
            TransactionFailedException, TimedOutException
    {
        if(exception != null)
        {
            if(exception instanceof TimedOutException)
            {
                throw (TimedOutException) exception;
            }
            else if(exception instanceof SystemException)
            {
                throw (SystemException) exception;
            }
            else if(exception instanceof CommunicationException)
            {
                throw (CommunicationException) exception;
            }
            else if(exception instanceof AuthorizationException)
            {
                throw (AuthorizationException) exception;
            }
            else if(exception instanceof DataValidationException)
            {
                throw (DataValidationException) exception;
            }
            else if(exception instanceof NotFoundException)
            {
                throw (NotFoundException) exception;
            }
            else if(exception instanceof AlreadyExistsException)
            {
                throw (AlreadyExistsException) exception;
            }
            else if(exception instanceof AuthenticationException)
            {
                throw (AuthenticationException) exception;
            }
            else if(exception instanceof NotAcceptedException)
            {
                throw (NotAcceptedException) exception;
            }
            else if(exception instanceof NotSupportedException)
            {
                throw (NotSupportedException) exception;
            }
            else if(exception instanceof TransactionFailedException)
            {
                throw (TransactionFailedException) exception;
            }
            else if(exception instanceof UserException)
            {
                throw (UserException) exception;
            }
            else
            {
                SystemException newException =
                        ExceptionBuilder.systemException(exception.getMessage(), 0);
                newException.initCause(exception);
                throw newException;
            }
        }
    }
}
