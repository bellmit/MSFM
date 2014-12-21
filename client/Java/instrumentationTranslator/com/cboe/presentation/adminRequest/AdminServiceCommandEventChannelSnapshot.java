//
// -----------------------------------------------------------------------------------
// Source file: AdminServiceCommandEventChannelSnapshot.java
//
// PACKAGE: com.cboe.presentation.adminRequest;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
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

import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.instrumentation.adminRequest.ExecuteCommandService;
import com.cboe.interfaces.instrumentation.adminRequest.ExecutionResult;

import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.systemsManagementService.asynchronousClient.ExecuteCommandCallback;
import com.cboe.infrastructureServices.systemsManagementService.asynchronousClient.AdminServiceClientAsync;

public class AdminServiceCommandEventChannelSnapshot
        extends AdminServiceEventChannelShapshot
        implements ExecuteCommandCallback, ExecuteCommandService
{
    private Command command;
    private Command resultCommand;
    private boolean returnValue;

    public AdminServiceCommandEventChannelSnapshot()
    {
    }

    public AdminServiceCommandEventChannelSnapshot(String destination, Command command)
    {
        this(destination, 0, command);
    }

    public AdminServiceCommandEventChannelSnapshot(String destination, int serviceTimeout,
                                                   Command command)
    {
        this(destination, serviceTimeout, 0, command);
    }

    public AdminServiceCommandEventChannelSnapshot(String destination, int serviceTimeout,
                                                   int snapshotTimeout, Command command)
    {
        super(destination, serviceTimeout, snapshotTimeout);
        this.command = command;
    }

    protected void subscribeEventChannel()
    {
        AdminServiceClientAsync adminService =
                InstrumentationTranslatorFactory.find().getAdminService();

        if(serviceTimeout > 0 || getTimeout() > 0)
        {
            int timeout = Math.max(serviceTimeout, getTimeout());
            adminService.executeCommand(destination, timeout, command, this);
        }
        else
        {
            adminService.executeCommand(destination, command, this);
        }
    }

    protected void unsubscribeEventChannel()
    {
        //Do nothing here
    }

    public void returned(Command result, boolean returnValue)
    {
        resultCommand = result;
        this.returnValue = returnValue;

        if(returnValue)
        {
            if(result.retValues != null && result.retValues.length > 0)
            {
                eventChannelData = result.retValues[0].value;
            }
            else
            {
                eventChannelData = null;
            }
            isChannelUpdated = true;
        }
        else
        {
            // If returnValue is false it means we have an exception on whatever
            //  service the AdminService had to call
            // to do the actual work. We should not be getting here, anyway.
            Object [] args = new Object[2];
            args[0] = result;
            args[1] = returnValue;
            GUILoggerHome.find().alarm("Command Callback returned False", args);
        }
    }

    public void catchException(UserException e)
    {
        exception = e;
        isExceptionThrown = true;
        Object[] argObj = new Object[2];
        argObj[0] = destination;
        argObj[1] = command;
        GUILoggerHome.find().exception(category,
                                       "Unable to execute AdminService command", e, argObj);
    }

    public void catchException(RuntimeException e)
    {
        exception = e;
        isExceptionThrown = true;
        Object[] argObj = new Object[2];
        argObj[0] = destination;
        argObj[1] = command;
        GUILoggerHome.find().exception(category,
                                       "Unable to execute AdminService command", e, argObj);
    }

    protected void processTimeOut() throws TimedOutException
    {
        if(adminServiceTimedOut)
        {
            StringBuilder buffer =
                    new StringBuilder("AdminService timed out while performing request.");
            buffer.append(" Command:").append(command.name);
            buffer.append(" Destination:").append(destination);
            throw new TimedOutException(buffer.toString(), serviceTimeout);
        }
        else // This is the case of the snapshot timeout
        {
            throw new TimedOutException(getTimeoutMessage(), snapshotTimeout);
        }
    }

    /**
     * Executes the command
     * @param command to execute
     * @return ExecutionResult that contains the response and state from the command
     * that was sent
     * @throws UserException represents an exception thrown from executing the command
     * @throws CommunicationException represents an exception thrown from executing the command
     * @throws SystemException represents an exception thrown from executing the command
     * @throws AuthorizationException represents an exception thrown from executing the command
     * @throws DataValidationException represents an exception thrown from executing the command
     * @throws AlreadyExistsException represents an exception thrown from executing the command
     * @throws AuthenticationException represents an exception thrown from executing the command
     * @throws NotAcceptedException represents an exception thrown from executing the command
     * @throws NotFoundException represents an exception thrown from executing the command
     * @throws NotSupportedException represents an exception thrown from executing the command
     * @throws TransactionFailedException represents an exception thrown from executing the command
     * @throws TimedOutException represents an exception thrown from executing the command
     */
    public ExecutionResult executeCommand(Command command)
            throws UserException, CommunicationException, SystemException, AuthorizationException,
            DataValidationException, AlreadyExistsException, AuthenticationException,
            NotAcceptedException, NotFoundException, NotSupportedException,
            TransactionFailedException, TimedOutException
    {
        this.command = command;
        getEventChannelData();
        return new ExecutionResultImpl(resultCommand, returnValue);
    }
}
