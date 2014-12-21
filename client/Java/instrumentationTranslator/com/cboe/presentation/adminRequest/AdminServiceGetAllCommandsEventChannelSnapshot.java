//
// -----------------------------------------------------------------------------------
// Source file: AdminServiceGetAllCommandsEventChannelSnapshot.java
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
import com.cboe.interfaces.instrumentation.adminRequest.GetAllCommandsService;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;

import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.systemsManagementService.asynchronousClient.GetAllCommandsCallback;
import com.cboe.infrastructureServices.systemsManagementService.asynchronousClient.AdminServiceClientAsync;

public class AdminServiceGetAllCommandsEventChannelSnapshot
        extends AdminServiceEventChannelShapshot
        implements GetAllCommandsCallback, GetAllCommandsService
{
    public AdminServiceGetAllCommandsEventChannelSnapshot()
    {
    }

    public AdminServiceGetAllCommandsEventChannelSnapshot(String destination)
    {
        this(destination, 0);
    }

    public AdminServiceGetAllCommandsEventChannelSnapshot(String destination, int serviceTimeout)
    {
        this(destination, serviceTimeout, 0);
    }

    public AdminServiceGetAllCommandsEventChannelSnapshot(String destination, int serviceTimeout,
                                                          int snapshotTimeout)
    {
        super(destination, serviceTimeout, snapshotTimeout);
    }

    protected void subscribeEventChannel()
    {
        AdminServiceClientAsync adminService =
                InstrumentationTranslatorFactory.find().getAdminService();

        if ( serviceTimeout > 0 )
        {
            adminService.getAllCommands(destination, serviceTimeout, this);
        }
        else
        {
            adminService.getAllCommands(destination, this);
        }
    }

    protected void unsubscribeEventChannel()
    {
        //Do nothing here
    }

    public void catchException(RuntimeException e)
    {
        exception = e;
        isExceptionThrown = true;
        GUILoggerHome.find().exception(category, "Unable to get AdminService commands for " +
                                       destination, e);
    }

    protected void processTimeOut() throws TimedOutException
    {
        if ( adminServiceTimedOut )
        {
            StringBuilder buffer =
                    new StringBuilder("AdminService timed out while performing request.");
            buffer.append(" GetAllCommands ");
            buffer.append(" Destination:").append(destination);
            throw new TimedOutException(buffer.toString(), serviceTimeout);
        }
        else // This is the case of the snapshot timeout
        {
            throw new TimedOutException(getTimeoutMessage(), snapshotTimeout);
        }
    }

    public void returned(Command[] commands)
    {
        if(commands != null)
        {
            eventChannelData = commands;
            isChannelUpdated = true;
        }
        else
        {
            // If commands is null it means we have an exception on whatever service the
            // AdminService had to call
            // to do the actual work. We should not be getting here, anyway.
            GUILoggerHome.find().alarm("GetAllCommands Callback returned NULL");
        }
    }

    /**
     * Gets all the commands that are available for the destination. The destination is set
     * from the ARCommandService interface, that this interface extends.
     * @return an array of all Command's that exist for the destination set
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
    public Command[] getAllCommands()
            throws UserException, CommunicationException, SystemException,
            AuthorizationException, DataValidationException, AlreadyExistsException,
            AuthenticationException, NotAcceptedException, NotFoundException, NotSupportedException,
            TransactionFailedException, TimedOutException
    {
        return (Command[]) getEventChannelData();
    }
}
