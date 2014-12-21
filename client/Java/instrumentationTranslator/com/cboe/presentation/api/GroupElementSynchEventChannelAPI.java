//
// -----------------------------------------------------------------------------------
// Source file: AlarmsSynchEventChannelAPI.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import org.omg.CORBA.UserException;

import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.idl.groupElement.ElementErrorResultStruct;
import com.cboe.idl.constants.ElementGroupTypes;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.ExceptionDetails;

import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.events.ICSGroupElementServiceConsumer;

import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelListener;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.groups.events.ICSGroupElementPublisherHome;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.uuidService.IdService;
import com.cboe.domain.util.GroupElementEventStructContainer;

public class GroupElementSynchEventChannelAPI extends AbstractSnapshot implements EventChannelListener
{
    protected IdService idService;
    protected EventChannelAdapter eventChannel;

    private Long requestId;
    private int channelTypeToListen;

    private ICSGroupElementServiceConsumer groupElementProxy;

    private long nowTime;

    private static final String CATEGORY = GroupElementSynchEventChannelAPI.class.getName();

    private static final ElementStruct[] EMPTY_ELEMENT_STRUCT = new ElementStruct[0];

    public GroupElementSynchEventChannelAPI(ICSGroupElementPublisherHome publisherHome)
    {
        initialize();
        groupElementProxy = publisherHome.getICSGroupElementPublisher();
    }

    public GroupElementSynchEventChannelAPI(ICSGroupElementPublisherHome publisherHome, int timeout)
    {
        super(timeout);
        initialize();
        groupElementProxy = publisherHome.getICSGroupElementPublisher();
    }

    public ElementStruct[] publishAllGroups()
                                throws SystemException, CommunicationException, TimedOutException, NotFoundException
    {
        ElementStruct rootElement = publishRootElementForGroupType(ElementGroupTypes.GROUP_TYPE_PROCESS);
        return publishElementsForGroup(rootElement.elementKey);
    }

    public void createElementsForGroup(long groupKey, ElementEntryStruct[] elementEntryStructs)
            throws SystemException, TransactionFailedException, TimedOutException, DataValidationException
    {
        try
        {
            requestId = getNextRequestId();
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
            return;
        }
        channelTypeToListen = ChannelType.IC_ACCEPT_GROUP_ELEMENT_RESULTS;

        subscribeEventChannel();

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            nowTime = System.currentTimeMillis();
            Object[] argObj = new Object[1];
            argObj[0] = "RequestId" + requestId;
            GUILoggerHome.find().debug(CATEGORY + ": createElementsForGroup",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        groupElementProxy.createElementsForGroup(requestId, groupKey, elementEntryStructs);

        try
        {
            GroupElementEventStructContainer anAddedElement = (GroupElementEventStructContainer) getEventChannelData();
        }
        catch(AlreadyExistsException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected AlreadyExistsException while executing Group Service command.", e);
        }
        catch(NotAcceptedException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(CommunicationException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(NotFoundException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            Object[] argObj = new Object[2];
            argObj[0] = "RequestId:" + requestId;
            argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
            GUILoggerHome.find().debug(CATEGORY + ": createElementsForGroup" + ":returned",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }
    }

    public void addElementsToGroup(long groupKey, long[] elementKeys)
            throws SystemException, TimedOutException, DataValidationException, TransactionFailedException
    {
        ElementStruct[] returnValue = EMPTY_ELEMENT_STRUCT;

        try
        {
            requestId = getNextRequestId();
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
            return;
        }
        channelTypeToListen = ChannelType.IC_ACCEPT_GROUP_ELEMENT_RESULTS;

        subscribeEventChannel();

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            nowTime = System.currentTimeMillis();
            Object[] argObj = new Object[1];
            argObj[0] = "RequestId" + requestId;
            GUILoggerHome.find().debug(CATEGORY + ": addElementsToGroup" + ":publishedEvent",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        groupElementProxy.addElementsToGroup(requestId, groupKey, elementKeys);

        try
        {
            GroupElementEventStructContainer anAddedElement = (GroupElementEventStructContainer) getEventChannelData();
        }
        catch(AlreadyExistsException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected AlreadyExistsException while executing Group Service command.", e);
        }
        catch(CommunicationException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(NotAcceptedException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(NotFoundException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            Object[] argObj = new Object[2];
            argObj[0] = "RequestId:" + requestId;
            argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
            GUILoggerHome.find().debug(CATEGORY + ": addElementsToGroup" + ":returned",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }
    }

    public void updateElement(ElementStruct elementStruct)
            throws SystemException, TimedOutException, DataValidationException, TransactionFailedException
    {
        try
        {
            requestId = getNextRequestId();
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
            return;
        }
        channelTypeToListen = ChannelType.IC_ACCEPT_GROUP_UPDATE_ELEMENT;

        subscribeEventChannel();

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            nowTime = System.currentTimeMillis();
            Object[] argObj = new Object[1];
            argObj[0] = "RequestId" + requestId;
            GUILoggerHome.find().debug(CATEGORY + ": updateElement" + ":publishedEvent",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        groupElementProxy.updateElement(requestId, elementStruct);

        try
        {
            ElementStruct returnValue = (ElementStruct) getEventChannelData();
        }
        catch(NotAcceptedException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(NotFoundException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(AlreadyExistsException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(CommunicationException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            Object[] argObj = new Object[2];
            argObj[0] = "RequestId:" + requestId;
            argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
            GUILoggerHome.find().debug(CATEGORY + ": updateElement" + ":returned",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        return;
    }

    public void removeElementsFromGroup(long groupKey, long[] elementKeys)
            throws SystemException, TimedOutException, DataValidationException, TransactionFailedException
    {
        ElementStruct[] returnValue = EMPTY_ELEMENT_STRUCT;

        try
        {
            requestId = getNextRequestId();
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
            return;
        }
        channelTypeToListen = ChannelType.IC_ACCEPT_GROUP_ELEMENT_RESULTS;

        subscribeEventChannel();

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            nowTime = System.currentTimeMillis();
            Object[] argObj = new Object[1];
            argObj[0] = "RequestId" + requestId;
            GUILoggerHome.find().debug(CATEGORY + ": removeElementsFromGroup" + ":publishedEvent",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        groupElementProxy.removeElementsFromGroup(requestId, groupKey, elementKeys);

        try
        {
            GroupElementEventStructContainer removeResults = (GroupElementEventStructContainer) getEventChannelData();
            if(removeResults != null)
            {
                StringBuilder errorMsg = new StringBuilder();
                for(ElementErrorResultStruct errorResultStruct:removeResults.getElementErrorResultStructs())
                {
                    if(errorResultStruct.errorCode > 1)
                    {
                        errorMsg.append(errorResultStruct.errorMessage);
                        errorMsg.append("\n");
                    }
                }
                if(errorMsg.length()>0)
                {
                    DataValidationException exception = new DataValidationException(errorMsg.toString(),
                                                                                    new ExceptionDetails());
                    throw exception;
                }
            }
        }
        catch(AlreadyExistsException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected AlreadyExistsException while executing Group Service command.", e);
        }
        catch(NotAcceptedException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(NotFoundException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(CommunicationException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            Object[] argObj = new Object[2];
            argObj[0] = "RequestId:" + requestId;
            argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
            GUILoggerHome.find().debug(CATEGORY + ": removeElementsFromGroup" + ":returned",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        return;
    }

    public ElementStruct publishRootElementForGroupType(short groupType)
            throws SystemException, TimedOutException
    {
        ElementStruct returnValue = null;

        try
        {
            requestId = getNextRequestId();
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
            return returnValue;
        }
        channelTypeToListen = ChannelType.IC_ACCEPT_GROUP_ELEMENTS;

        subscribeEventChannel();

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            nowTime = System.currentTimeMillis();
            Object[] argObj = new Object[1];
            argObj[0] = "RequestId" + requestId;
            GUILoggerHome.find().debug(CATEGORY + ": publishRootElementForGroupType" + ":publishedEvent",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        groupElementProxy.publishRootElementForGroupType(requestId, groupType);

        try
        {
            ElementStruct[] returnStructs = (ElementStruct[]) getEventChannelData();
            returnValue = returnStructs.length == 1 ? returnStructs[0] : null; 
        }
        catch(AlreadyExistsException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }
        catch(TransactionFailedException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }
        catch(DataValidationException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }
        catch(NotAcceptedException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }
        catch(NotFoundException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }
        catch(CommunicationException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            Object[] argObj = new Object[2];
            argObj[0] = "RequestId:" + requestId;
            argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
            GUILoggerHome.find().debug(CATEGORY + ": publishRootElementForGroupType" + ":returned",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        return returnValue;
    }

    public ElementStruct[] publishElementsForGroup(long groupKey)
            throws SystemException, CommunicationException, TimedOutException
    {
        ElementStruct[] returnValue = EMPTY_ELEMENT_STRUCT;

        try
        {
            requestId = getNextRequestId();
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
            return returnValue;
        }
        channelTypeToListen = ChannelType.IC_ACCEPT_GROUP_ELEMENTS;

        subscribeEventChannel();

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            nowTime = System.currentTimeMillis();
            Object[] argObj = new Object[1];
            argObj[0] = "RequestId" + requestId;
            GUILoggerHome.find().debug(CATEGORY + ": publishElementsForGroup" + ":publishedEvent",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        groupElementProxy.publishElementsForGroup(requestId, groupKey);

        try
        {
            returnValue = (ElementStruct[]) getEventChannelData();
        }
        catch(AlreadyExistsException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }
        catch(TransactionFailedException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }
        catch(NotFoundException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }
        catch(DataValidationException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }
        catch(NotAcceptedException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                    "Unexpected Exception while executing GroupElement Event Channel command.", e);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            Object[] argObj = new Object[2];
            argObj[0] = "RequestId:" + requestId;
            argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
            GUILoggerHome.find().debug(CATEGORY + ": publishElementsForGroup" + ":returned",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        return returnValue;
    }

    public void cloneToGroup(long newGroupKey, ElementEntryStruct cloneElementEntryStruct, long[] elementKeys)
            throws SystemException, TimedOutException, DataValidationException, TransactionFailedException
    {
        ElementStruct[] returnValue = EMPTY_ELEMENT_STRUCT;

        try
        {
            requestId = getNextRequestId();
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
            return;
        }
        channelTypeToListen = ChannelType.IC_ACCEPT_GROUP_ELEMENT_RESULTS;

        subscribeEventChannel();

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            nowTime = System.currentTimeMillis();
            Object[] argObj = new Object[1];
            argObj[0] = "RequestId" + requestId;
            GUILoggerHome.find().debug(CATEGORY + ": cloneGroup" + ":publishedEvent",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        groupElementProxy.copyElementsToGroup(requestId, newGroupKey, cloneElementEntryStruct, elementKeys);

        try
        {
            GroupElementEventStructContainer copyResults = (GroupElementEventStructContainer) getEventChannelData();
        }
        catch(AlreadyExistsException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected AlreadyExistsException while executing Group Service command.", e);
        }
        catch(CommunicationException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(NotAcceptedException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(NotFoundException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            Object[] argObj = new Object[2];
            argObj[0] = "RequestId:" + requestId;
            argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
            GUILoggerHome.find().debug(CATEGORY + ": cloneGroup" + ":returned",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }
    }

    public void moveToGroup(long currentGroupKey, long newGroupKey, long[] elementKeys)
            throws SystemException, TimedOutException, DataValidationException, TransactionFailedException
    {
        ElementStruct[] returnValue = EMPTY_ELEMENT_STRUCT;

        try
        {
            requestId = getNextRequestId();
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
            return;
        }
        channelTypeToListen = ChannelType.IC_ACCEPT_GROUP_ELEMENT_RESULTS;

        subscribeEventChannel();

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            nowTime = System.currentTimeMillis();
            Object[] argObj = new Object[1];
            argObj[0] = "RequestId" + requestId;
            GUILoggerHome.find().debug(CATEGORY + ": moveToGroup" + ":publishedEvent",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }

        groupElementProxy.moveElementsToGroup(requestId, currentGroupKey, newGroupKey, elementKeys);

        try
        {
            GroupElementEventStructContainer moveResults = (GroupElementEventStructContainer) getEventChannelData();
        }
        catch(AlreadyExistsException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected AlreadyExistsException while executing Group Service command.", e);
        }
        catch(CommunicationException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(NotAcceptedException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }
        catch(NotFoundException e)
        {
            //not expected for this method
            GUILoggerHome.find().exception(getClass().getName(),
                                           "Unexpected Exception while executing Group Service command.", e);
        }

        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.GROUPS))
        {
            Object[] argObj = new Object[2];
            argObj[0] = "RequestId:" + requestId;
            argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
            GUILoggerHome.find().debug(CATEGORY + ": moveToGroup" + ":returned",
                                       GUILoggerINBusinessProperty.GROUPS, argObj);
        }
    }

    public Object getEventChannelData()
            throws CommunicationException, SystemException, DataValidationException, AlreadyExistsException,
                   NotAcceptedException, NotFoundException, TransactionFailedException, TimedOutException
    {
        Object eventChannelData = null;

        setStartTime(System.currentTimeMillis());

        synchronized(this)
        {
            while(!isChannelUpdated() && !isTimedOut() && !isExceptionThrown())
            {
                try
                {
                    wait(getTimeout());
                }
                catch(InterruptedException e)
                {
                    //Either timed out or interrupted for data input. Do nothing.
                }
                calculateIsTimedOut();
            }
        }

        unsubscribeEventChannel();

        if(isChannelUpdated())
        {
            eventChannelData = getEventData();
        }
        else if(isExceptionThrown())
        {
            processException();
        }
        else if(isTimedOut())
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
        switch(channelType)
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

    protected void processException()
            throws CommunicationException, SystemException, DataValidationException, AlreadyExistsException,
                   NotAcceptedException, NotFoundException, TransactionFailedException
    {
        if(exception != null)
        {
            if(exception instanceof SystemException)
            {
                SystemException newException =
                        new SystemException(exception.getMessage(), ((SystemException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if(exception instanceof CommunicationException)
            {
                CommunicationException newException =
                        new CommunicationException(exception.getMessage(), ((CommunicationException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if(exception instanceof DataValidationException)
            {
                DataValidationException newException =
                        new DataValidationException(exception.getMessage(),
                                                   ((DataValidationException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if(exception instanceof NotFoundException)
            {
                NotFoundException newException =
                        new NotFoundException(exception.getMessage(),
                                                   ((NotFoundException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if(exception instanceof AlreadyExistsException)
            {
                AlreadyExistsException newException =
                        new AlreadyExistsException(exception.getMessage(),
                                                   ((AlreadyExistsException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if(exception instanceof NotAcceptedException)
            {
                NotAcceptedException newException =
                        new NotAcceptedException(exception.getMessage(),
                                                   ((NotAcceptedException) exception).details);
                newException.initCause(exception);
                throw newException;
            }
            else if(exception instanceof TransactionFailedException)
            {
                TransactionFailedException newException =
                        new TransactionFailedException(exception.getMessage(),
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
        return "Exception while executing Group Service command.";
    }

    protected String getTimeoutMessage()
    {
        return "Timed out while waiting for Group Service response.";
    }

    protected void processTimeOut()
            throws TimedOutException
    {
        throw new TimedOutException(getTimeoutMessage(), getTimeout());
    }

    protected long getUniqueRequestId()
            throws SystemException, NotFoundException
    {
        return idService.getNextUUID();
    }

    private Long getNextRequestId()
            throws SystemException, NotFoundException
    {
        return new Long(getUniqueRequestId());
    }

    private void initialize()
    {
        idService = FoundationFramework.getInstance().getIdService();
        eventChannel = EventChannelAdapterFactory.find();
    }

}