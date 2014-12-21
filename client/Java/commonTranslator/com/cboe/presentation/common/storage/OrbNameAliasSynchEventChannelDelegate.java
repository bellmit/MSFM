/*
 * Created on Dec 27, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.presentation.common.storage;

import org.omg.CORBA.UserException;

import com.cboe.idl.clusterInfo.LogicalOrbNameStruct;
import com.cboe.idl.clusterInfo.OrbNameAliasStruct;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.events.OrbNameAliasServiceConsumer;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.processes.LogicalName;
import com.cboe.interfaces.presentation.processes.OrbNameAlias;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.api.AbstractSnapshot;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.uuidService.IdService;

public class OrbNameAliasSynchEventChannelDelegate extends AbstractSnapshot implements EventChannelListener
{

    protected IdService                       idService;

    protected EventChannelAdapter             eventChannel;

    private Long                              requestId;

    private int                               channelTypeToListen;

    private OrbNameAliasServiceConsumer       orbNameAliasProxy;
    
    private long                              simpleId;

    private static final OrbNameAliasStruct[] EMPTY_ORB_NAME_ALIAS_STRUCT = new OrbNameAliasStruct[0];

    private static final LogicalOrbNameStruct[] EMPTY_LOGICAL_ORB_NAME_STRUCT = new LogicalOrbNameStruct[0];

    public OrbNameAliasSynchEventChannelDelegate(OrbNameAliasServiceConsumer publisher)
    {
        this(publisher,500);
    }

    public OrbNameAliasSynchEventChannelDelegate(OrbNameAliasServiceConsumer publisher, int timeout)
    {
        super(timeout);
        initialize();
        orbNameAliasProxy = publisher;
    }

    public OrbNameAliasStruct[] publishAllOrbNameAlias() throws TimedOutException, UserException
    {
        OrbNameAliasStruct[] returnValue = EMPTY_ORB_NAME_ALIAS_STRUCT;
        requestId = getNextRequestId();
        channelTypeToListen = ChannelType.IC_ACCEPT_ORB_NAME_ALIASES;

        subscribeEventChannel();
        isChannelUpdated = false;
        isExceptionThrown = false;


        orbNameAliasProxy.publishAllOrbNameAliases(requestId.longValue());

        try
        {
            
            returnValue = (OrbNameAliasStruct[]) getEventChannelData();
            
        }
        catch (AlreadyExistsException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }
        catch (TransactionFailedException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }
        catch (NotFoundException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }
        catch (DataValidationException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }
        catch (NotAcceptedException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }

        return returnValue;
    }

    public LogicalOrbNameStruct[] publishAllLogicalOrbNames() throws TimedOutException, UserException
    {
        LogicalOrbNameStruct[] returnValue = EMPTY_LOGICAL_ORB_NAME_STRUCT;
        requestId = getNextRequestId();
        channelTypeToListen = ChannelType.IC_ACCEPT_LOGICAL_ORB_NAMES;

        subscribeEventChannel();
        isChannelUpdated = false;
        isExceptionThrown = false;


        orbNameAliasProxy.publishAllLogicalOrbNames(requestId.longValue());

        try
        {

            returnValue = (LogicalOrbNameStruct[]) getEventChannelData();

        }
        catch(AlreadyExistsException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing ClusterInfo Event Channel command.",
                                           e);
        }
        catch(TransactionFailedException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing ClusterInfo Event Channel command.",
                                           e);
        }
        catch(NotFoundException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing ClusterInfo Event Channel command.",
                                           e);
        }
        catch(DataValidationException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing ClusterInfo Event Channel command.",
                                           e);
        }
        catch(NotAcceptedException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing ClusterInfo Event Channel command.",
                                           e);
        }

        return returnValue;
    }

    public OrbNameAliasStruct publishOrbNameAliasByName(String orbName) throws TimedOutException, UserException
    {
        OrbNameAliasStruct returnValue = null;

        requestId = getNextRequestId();

        channelTypeToListen = ChannelType.IC_ACCEPT_ORB_NAME_ALIASES;

        subscribeEventChannel();
        isChannelUpdated = false;
        isExceptionThrown = false;
        orbNameAliasProxy.publishOrbNameAliasByName(requestId.longValue(), orbName);

        try
        {
            OrbNameAliasStruct[] structs = (OrbNameAliasStruct[]) getEventChannelData();
            if (structs.length > 0)
            {
                returnValue = structs[0];
            }
        }
        catch (AlreadyExistsException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }
        catch (TransactionFailedException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }
        catch (DataValidationException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }
        catch (NotAcceptedException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }

        return returnValue;
    }

    public OrbNameAlias createOrbNameAlias(OrbNameAliasStruct orbNameAliasStruct) throws TimedOutException, UserException
    {
        OrbNameAlias returnValue = null;
        requestId = getNextRequestId();
        channelTypeToListen = ChannelType.IC_ACCEPT_NEW_ORB_NAME_ALIAS;

        subscribeEventChannel();
        isChannelUpdated = false;
        isExceptionThrown = false;

        orbNameAliasProxy.createOrbNameAlias(requestId.longValue(), orbNameAliasStruct);

        try
        {
            returnValue = (OrbNameAlias) getEventChannelData();
        }
        catch (NotFoundException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }

        return returnValue;
    }

    public OrbNameAlias updateOrbNameAlias(OrbNameAliasStruct orbNameAliasStruct) throws TimedOutException, CommunicationException, SystemException, DataValidationException, AlreadyExistsException, NotAcceptedException, NotFoundException, TransactionFailedException
    {
        OrbNameAlias returnValue = null;
        requestId = getNextRequestId();
        channelTypeToListen = ChannelType.IC_ACCEPT_CHANGED_ORB_NAME_ALIAS;

        subscribeEventChannel();
        isChannelUpdated = false;
        isExceptionThrown = false;

        orbNameAliasProxy.updateOrbNameAlias(requestId.longValue(), orbNameAliasStruct);

        Object value = getEventChannelData();
        returnValue = (OrbNameAlias)value; 

        return returnValue;
    }

    public void deleteOrbNameAlias(OrbNameAliasStruct orbNameAliasStruct) throws TimedOutException, UserException
    {
        requestId = getNextRequestId();
        channelTypeToListen = ChannelType.IC_ACCEPT_DELETE_ORB_NAME_ALIAS;

        subscribeEventChannel();
        isChannelUpdated = false;
        isExceptionThrown = false;

        orbNameAliasProxy.deleteOrbNameAlias(requestId.longValue(), orbNameAliasStruct);

        try
        {
            getEventChannelData();
        }
        catch (AlreadyExistsException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing ClusterInfo Event Channel command.",
                    e);
        }

    }

    public LogicalName createLogicalOrbName(LogicalOrbNameStruct logicalOrbNameStruct) throws TimedOutException, UserException
    {
        LogicalName returnValue = null;
        requestId = getNextRequestId();
        channelTypeToListen = ChannelType.IC_ACCEPT_NEW_LOGICAL_ORB_NAME;

        subscribeEventChannel();
        isChannelUpdated = false;
        isExceptionThrown = false;

        orbNameAliasProxy.createLogicalOrbName(requestId.longValue(), logicalOrbNameStruct);

        try
        {
            returnValue = (LogicalName) getEventChannelData();
        }
        catch(NotFoundException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing ClusterInfo Event Channel command.",
                                           e);
        }

        return returnValue;
    }

    public void updateLogicalOrbName(LogicalOrbNameStruct logicalOrbNameStruct) throws
            TimedOutException, UserException
    {
        LogicalName returnValue = null;
        requestId = getNextRequestId();
        channelTypeToListen = ChannelType.IC_ACCEPT_CHANGED_LOGICAL_ORB_NAME;

        subscribeEventChannel();
        isChannelUpdated = false;
        isExceptionThrown = false;

        orbNameAliasProxy.updateLogicalOrbName(requestId.longValue(), logicalOrbNameStruct);

        try
        {
            getEventChannelData();
        }
        catch(NotFoundException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing ClusterInfo Event Channel command.",
                                           e);
        }
    }

    public void deleteLogicalOrbName(LogicalOrbNameStruct logicalOrbNameStruct) throws TimedOutException, UserException
    {
        requestId = getNextRequestId();
        channelTypeToListen = ChannelType.IC_ACCEPT_DELETE_LOGICAL_ORB_NAME;

        subscribeEventChannel();
        isChannelUpdated = false;
        isExceptionThrown = false;

        orbNameAliasProxy.deleteLogicalOrbName(requestId.longValue(), logicalOrbNameStruct);

        try
        {
            getEventChannelData();
        }
        catch(AlreadyExistsException e)
        {
            // not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing ClusterInfo Event Channel command.",
                                           e);
        }

    }

    public Object getEventChannelData() throws CommunicationException, SystemException, DataValidationException,
            AlreadyExistsException, NotAcceptedException, NotFoundException, TransactionFailedException, TimedOutException
    {
        Object eventChannelData = null;

        setStartTime(System.currentTimeMillis());

        synchronized (this)
        {
            while (!isChannelUpdated() && !isTimedOut() && !isExceptionThrown())
            {
                try
                {
                    wait(getTimeout());
                }
                catch (InterruptedException e)
                {
                    // Either timed out or interrupted for data input. Do
                    // nothing.
                }
                calculateIsTimedOut();
            }
        }

        unsubscribeEventChannel();

        if (isChannelUpdated())
        {
            eventChannelData = getEventData();
        }
        else if (isExceptionThrown())
        {
            processException();
        }
        else if (isTimedOut())
        {
            processTimeOut();
        }

        return eventChannelData;
    }

    public synchronized void channelUpdate(ChannelEvent event)
    {
        ChannelKey key = (ChannelKey) event.getChannel();
        int channelType = key.channelType;
        ExceptionDetails eDetails;
        switch (channelType)
        {
        case ChannelType.IC_ACCEPT_ALREADY_EXISTS_EXCEPTION:
            eDetails = (ExceptionDetails) event.getEventData();
            exception = new AlreadyExistsException(getExceptionMessage(), eDetails);
            isChannelUpdated = false;
            isExceptionThrown = true;
            break;
        case ChannelType.IC_ACCEPT_DATA_VALIDATION_EXCEPTION:
            eDetails = (ExceptionDetails) event.getEventData();
            exception = new DataValidationException(getExceptionMessage(), eDetails);
            isChannelUpdated = false;
            isExceptionThrown = true;
            break;
        case ChannelType.IC_ACCEPT_NOT_ACCEPTED_EXCEPTION:
            eDetails = (ExceptionDetails) event.getEventData();
            exception = new NotAcceptedException(getExceptionMessage(), eDetails);
            isChannelUpdated = false;
            isExceptionThrown = true;
            break;
        case ChannelType.IC_ACCEPT_NOT_FOUND_EXCEPTION:
            eDetails = (ExceptionDetails) event.getEventData();
            exception = new NotFoundException(getExceptionMessage(), eDetails);
            isChannelUpdated = false;
            isExceptionThrown = true;
            break;
        case ChannelType.IC_ACCEPT_SYSTEM_EXCEPTION:
            eDetails = (ExceptionDetails) event.getEventData();
            exception = new SystemException(getExceptionMessage(), eDetails);
            isChannelUpdated = false;
            isExceptionThrown = true;
            break;
        case ChannelType.IC_ACCEPT_TRANSACTION_FAILED_EXCEPTION:
            eDetails = (ExceptionDetails) event.getEventData();
            exception = new TransactionFailedException(getExceptionMessage(), eDetails);
            isChannelUpdated = false;
            isExceptionThrown = true;
            break;
        default:
            this.eventChannelData = event.getEventData();
            isChannelUpdated = true;
            isExceptionThrown = false;
            break;
        }

        notify();
    }

    protected void subscribeEventChannel()
    {
        ChannelKey channelKey = new ChannelKey(channelTypeToListen, requestId);
        eventChannel.addChannelListener(eventChannel, this, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_ALREADY_EXISTS_EXCEPTION, requestId);
        eventChannel.addChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DATA_VALIDATION_EXCEPTION, requestId);
        eventChannel.addChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NOT_ACCEPTED_EXCEPTION, requestId);
        eventChannel.addChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NOT_FOUND_EXCEPTION, requestId);
        eventChannel.addChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_SYSTEM_EXCEPTION, requestId);
        eventChannel.addChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_TRANSACTION_FAILED_EXCEPTION, requestId);
        eventChannel.addChannelListener(eventChannel, this, channelKey);
    }

    protected void unsubscribeEventChannel()
    {
        ChannelKey channelKey = new ChannelKey(channelTypeToListen, requestId);
        eventChannel.removeChannelListener(eventChannel, this, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_ALREADY_EXISTS_EXCEPTION, requestId);
        eventChannel.removeChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DATA_VALIDATION_EXCEPTION, requestId);
        eventChannel.removeChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NOT_ACCEPTED_EXCEPTION, requestId);
        eventChannel.removeChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NOT_FOUND_EXCEPTION, requestId);
        eventChannel.removeChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_SYSTEM_EXCEPTION, requestId);
        eventChannel.removeChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_TRANSACTION_FAILED_EXCEPTION, requestId);
        eventChannel.removeChannelListener(eventChannel, this, channelKey);
    }

    protected void processException() throws CommunicationException, SystemException, DataValidationException,
            AlreadyExistsException, NotAcceptedException, NotFoundException, TransactionFailedException
    {
        if (exception != null)
        {
            if (exception instanceof SystemException)
            {
                SystemException newException = new SystemException(exception.getMessage(), ((SystemException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if (exception instanceof CommunicationException)
            {
                CommunicationException newException = new CommunicationException(exception.getMessage(),
                        ((CommunicationException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if (exception instanceof DataValidationException)
            {
                DataValidationException newException = new DataValidationException(exception.getMessage(),
                        ((DataValidationException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if (exception instanceof NotFoundException)
            {
                NotFoundException newException = new NotFoundException(exception.getMessage(),
                        ((NotFoundException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if (exception instanceof AlreadyExistsException)
            {
                AlreadyExistsException newException = new AlreadyExistsException(exception.getMessage(),
                        ((AlreadyExistsException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if (exception instanceof NotAcceptedException)
            {
                NotAcceptedException newException = new NotAcceptedException(exception.getMessage(),
                        ((NotAcceptedException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if (exception instanceof TransactionFailedException)
            {
                TransactionFailedException newException = new TransactionFailedException(exception.getMessage(),
                        ((TransactionFailedException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else
            {
                throw (RuntimeException) exception;
            }
        }
    }

    protected String getExceptionMessage()
    {
        return "Exception while executing ClusterInfo Event Channel command.";
    }

    protected String getTimeoutMessage()
    {
        return "Timed out while waiting for ClusterInfo Event Channel response.";
    }

    protected void processTimeOut() throws TimedOutException
    {
        throw new TimedOutException(getTimeoutMessage(), getTimeout());
    }

    protected long getUniqueRequestId()
    {
        if (idService == null)
        {
            return simpleId++;
        }
        
        try
        {
            return idService.getNextUUID();
        }
        catch (Exception e)
        {
            return simpleId++;
        }
    }

    private Long getNextRequestId()
    {
        return new Long(getUniqueRequestId());
    }

    private void initialize()
    {
        idService = FoundationFramework.getInstance().getIdService();
        eventChannel = EventChannelAdapterFactory.find();
    }
}
