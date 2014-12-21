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

import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_ACTIVATION;
import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_CALCULATION;
import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_CONDITION;
import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_DEFINITION;
import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_WATCHDOG;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.alarm.ActivationAssignmentStruct;
import com.cboe.idl.alarm.AlarmActivationStruct;
import com.cboe.idl.alarm.AlarmCalculationStruct;
import com.cboe.idl.alarm.AlarmConditionStruct;
import com.cboe.idl.alarm.AlarmDefinitionStruct;
import com.cboe.idl.alarm.AlarmNotificationWatchdogStruct;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.uuidService.IdService;
import com.cboe.interfaces.events.AlarmActivationServiceConsumer;
import com.cboe.interfaces.events.AlarmDefinitionServiceConsumer;
import com.cboe.interfaces.events.AlarmNotificationWatchdogServiceConsumer;
import com.cboe.interfaces.instrumentation.alarms.AlarmPublishersHome;
import com.cboe.interfaces.instrumentation.api.AlarmsAPI;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperty;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

public class AlarmsSynchEventChannelAPI extends AbstractSnapshot implements AlarmsAPI, EventChannelListener
{
	protected IdService idService;
	protected EventChannelAdapter eventChannel;

	private Long requestId;
	private int channelTypeToListen;

	private AlarmActivationServiceConsumer activationProxy;
	private AlarmDefinitionServiceConsumer definitionProxy;
	private AlarmNotificationWatchdogServiceConsumer watchdogProxy;

	private long nowTime;

	private static final String CATEGORY = AlarmsSynchEventChannelAPI.class.getName();

	private static final AlarmConditionStruct[] EMPTY_ALARM_CONDITION_STRUCT = new AlarmConditionStruct[0];
	private static final AlarmCalculationStruct[] EMPTY_ALARM_CALCULATION_STRUCT = new AlarmCalculationStruct[0];
	private static final AlarmDefinitionStruct[] EMPTY_ALARM_DEFINITION_STRUCT = new AlarmDefinitionStruct[0];
	private static final AlarmActivationStruct[] EMPTY_ALARM_ACTIVATION_STRUCT = new AlarmActivationStruct[0];
	private static final ActivationAssignmentStruct[] EMPTY_ACTIVATION_ASSIGNMENT_STRUCT = new ActivationAssignmentStruct[0];
	private static final AlarmNotificationWatchdogStruct[] EMPTY_ALARM_WATCHDOG_STRUCT = new AlarmNotificationWatchdogStruct[0];

	private static final boolean isDebugOn = GUILoggerHome.find().isDebugOn();

	public AlarmsSynchEventChannelAPI(AlarmPublishersHome publisherHome)
	{
		initialize();
		activationProxy = publisherHome.getAlarmActivationPublisher();
		definitionProxy = publisherHome.getAlarmDefinitionPublisher();
		watchdogProxy = publisherHome.getAlarmWatchdogPublisher();
	}

	public AlarmsSynchEventChannelAPI(AlarmPublishersHome publisherHome, int timeout)
	{
		super(timeout);
		initialize();
		activationProxy = publisherHome.getAlarmActivationPublisher();
		definitionProxy = publisherHome.getAlarmDefinitionPublisher();
		watchdogProxy = publisherHome.getAlarmWatchdogPublisher();
	}

	public AlarmConditionStruct[] publishAllConditions() throws SystemException, CommunicationException, TimedOutException
	{
		AlarmConditionStruct[] returnValue = EMPTY_ALARM_CONDITION_STRUCT;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_CONDITIONS;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_CONDITION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": publishAllConditions" + ":publishedEvent", ALARM_CONDITION, argObj);
		}

		definitionProxy.publishAllConditions(requestId);

		try
		{
			returnValue = (AlarmConditionStruct[]) getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishAllConditions" + ":returned", ALARM_CONDITION, argObj);
		}

		return returnValue;
	}

	public AlarmConditionStruct publishConditionById(int conditionId) throws NotFoundException, SystemException, CommunicationException,
	        TimedOutException
	{
		AlarmConditionStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_CONDITIONS;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_CONDITION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": publishConditionById" + ":publishedEvent", ALARM_CONDITION, argObj);
		}

		definitionProxy.publishConditionById(requestId, conditionId);

		try
		{
			AlarmConditionStruct[] structs = (AlarmConditionStruct[]) getEventChannelData();
			if (structs.length > 0)
			{
				returnValue = structs[0];
			}
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishConditionById" + ":returned", ALARM_CONDITION, argObj);
		}

		return returnValue;
	}

	public AlarmConditionStruct createCondition(AlarmConditionStruct alarmConditionStruct) throws SystemException, CommunicationException,
	        AlreadyExistsException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		AlarmConditionStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_NEW_CONDITION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_CONDITION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": createCondition" + ":publishedEvent", ALARM_CONDITION, argObj);
		}

		definitionProxy.createCondition(requestId, alarmConditionStruct);

		try
		{
			returnValue = (AlarmConditionStruct) getEventChannelData();
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": createCondition" + ":returned", ALARM_CONDITION, argObj);
		}

		return returnValue;
	}

	public void deleteCondition(AlarmConditionStruct alarmConditionStruct) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_DELETED_CONDITION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_CONDITION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": deleteCondition" + ":publishedEvent", ALARM_CONDITION, argObj);
		}

		definitionProxy.deleteCondition(requestId, alarmConditionStruct);

		try
		{
			getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": deleteCondition" + ":returned", ALARM_CONDITION, argObj);
		}
	}

	public AlarmConditionStruct updateCondition(AlarmConditionStruct alarmConditionStruct) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException, AlreadyExistsException
	{
		AlarmConditionStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_CHANGED_CONDITION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_CONDITION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": updateCondition" + ":publishedEvent", ALARM_CONDITION, argObj);
		}

		definitionProxy.updateCondition(requestId, alarmConditionStruct);

		returnValue = (AlarmConditionStruct) getEventChannelData();

		if (isDebugPropertyOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": updateCondition" + ":returned", ALARM_CONDITION, argObj);
		}

		return returnValue;
	}

	public AlarmCalculationStruct createCalculation(AlarmCalculationStruct alarmCalculationStruct) throws SystemException, NotAcceptedException,
	        CommunicationException, DataValidationException, AlreadyExistsException, TimedOutException, TransactionFailedException
	{
		AlarmCalculationStruct returnValue;

		try
		{
			requestId = getNextRequestId();
			channelTypeToListen = ChannelType.IC_ACCEPT_NEW_CALCULATION;

			subscribeEventChannel();

			if (isDebugPropertyOn(ALARM_CALCULATION))
			{
				nowTime = System.currentTimeMillis();
				Object[] argObj = new Object[1];
				argObj[0] = "RequestId" + requestId;
				debug(CATEGORY + ": createCalculation" + ":publishedEvent", ALARM_CALCULATION,
				        argObj);
			}

			definitionProxy.createCalculation(requestId, alarmCalculationStruct);

			returnValue = (AlarmCalculationStruct) getEventChannelData();

			if (isDebugPropertyOn(ALARM_CALCULATION))
			{
				Object[] argObj = new Object[2];
				argObj[0] = "RequestId:" + requestId;
				argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
				debug(CATEGORY + ": createCalculation" + ":returned", ALARM_CALCULATION, argObj);
			}
		}
		catch (NotFoundException e)
		{
			// noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
			throw new SystemException(e.details);
		}

		return returnValue;
	}

	public AlarmCalculationStruct publishCalculationById(int calculationId) throws NotFoundException, SystemException, CommunicationException,
	        TimedOutException
	{
		AlarmCalculationStruct returnValue = null;

		requestId = getNextRequestId();
		channelTypeToListen = ChannelType.IC_ACCEPT_CALCULATION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_CALCULATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": publishCalculationById" + ":publishedEvent", ALARM_CALCULATION,
			        argObj);
		}

		definitionProxy.publishCalculationById(requestId, calculationId);

		try
		{
			AlarmCalculationStruct[] structs = (AlarmCalculationStruct[]) getEventChannelData();
			if (structs.length > 0)
			{
				returnValue = structs[0];
			}
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishCalculationById" + ":returned", ALARM_CALCULATION, argObj);
		}

		return returnValue;
	}

	public AlarmCalculationStruct[] publishAllCalculations() throws SystemException, CommunicationException, TimedOutException
	{
		AlarmCalculationStruct[] returnValue = EMPTY_ALARM_CALCULATION_STRUCT;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_CALCULATION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_CALCULATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": publishAllCalculations" + ":publishedEvent", ALARM_CALCULATION,
			        argObj);
		}

		definitionProxy.publishAllCalculations(requestId);

		try
		{
			returnValue = (AlarmCalculationStruct[]) getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishAllCalculations" + ":returned", ALARM_CALCULATION, argObj);
		}

		return returnValue;
	}

	public AlarmCalculationStruct updateCalculation(AlarmCalculationStruct alarmCalculationStruct) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException, AlreadyExistsException
	{
		AlarmCalculationStruct returnValue;

		requestId = getNextRequestId();
		channelTypeToListen = ChannelType.IC_ACCEPT_CHANGED_CALCULATION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_CALCULATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": updateCalculation" + ":publishedEvent", ALARM_CALCULATION, argObj);
		}

		definitionProxy.updateCalculation(requestId, alarmCalculationStruct);

		returnValue = (AlarmCalculationStruct) getEventChannelData();

		if (isDebugPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": updateCalculation" + ":returned", ALARM_CALCULATION, argObj);
		}

		return returnValue;
	}

	public void deleteCalculation(AlarmCalculationStruct alarmCalculationStruct) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		requestId = getNextRequestId();
		channelTypeToListen = ChannelType.IC_ACCEPT_DELETED_CALCULATION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_CALCULATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": deleteCalculation" + ":publishedEvent", ALARM_CALCULATION, argObj);
		}

		definitionProxy.deleteCalculation(requestId, alarmCalculationStruct);

		try
		{
			getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": deleteCalculation" + ":returned", ALARM_CALCULATION, argObj);
		}
	}

	public AlarmDefinitionStruct[] publishAllDefinitions() throws SystemException, CommunicationException, TimedOutException
	{
		AlarmDefinitionStruct[] returnValue = EMPTY_ALARM_DEFINITION_STRUCT;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_DEFINITIONS;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_DEFINITION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			GUILoggerHome.find()
			        .debug(CATEGORY + ": publishAllDefinitions" + ":publishedEvent", ALARM_DEFINITION, argObj);
		}

		definitionProxy.publishAllDefinitions(requestId);

		try
		{
			returnValue = (AlarmDefinitionStruct[]) getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishAllDefinitions" + ":returned", ALARM_DEFINITION, argObj);
		}

		return returnValue;
	}

	public AlarmDefinitionStruct publishDefinitionById(int definitionId) throws NotFoundException, SystemException, CommunicationException,
	        TimedOutException
	{
		AlarmDefinitionStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_DEFINITIONS;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_DEFINITION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			GUILoggerHome.find()
			        .debug(CATEGORY + ": publishDefinitionById" + ":publishedEvent", ALARM_DEFINITION, argObj);
		}

		definitionProxy.publishDefinitionById(requestId, definitionId);

		try
		{
			AlarmDefinitionStruct[] structs = (AlarmDefinitionStruct[]) getEventChannelData();
			if (structs.length > 0)
			{
				returnValue = structs[0];
			}
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishDefinitionById" + ":returned", ALARM_DEFINITION, argObj);
		}

		return returnValue;
	}

	public AlarmDefinitionStruct createDefinition(AlarmDefinitionStruct alarmDefinitionStruct) throws SystemException, CommunicationException,
	        AlreadyExistsException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		AlarmDefinitionStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_NEW_DEFINITION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_DEFINITION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": createDefinition" + ":publishedEvent", ALARM_DEFINITION, argObj);
		}

		definitionProxy.createDefinition(requestId, alarmDefinitionStruct);

		try
		{
			returnValue = (AlarmDefinitionStruct) getEventChannelData();
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": createDefinition" + ":returned", ALARM_DEFINITION, argObj);
		}

		return returnValue;
	}

	public void deleteDefinition(AlarmDefinitionStruct alarmDefinitionStruct) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_DELETE_DEFINITION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_DEFINITION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": deleteDefinition" + ":publishedEvent", ALARM_DEFINITION, argObj);
		}

		definitionProxy.deleteDefinition(requestId, alarmDefinitionStruct);

		try
		{
			getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": deleteDefinition" + ":returned", ALARM_DEFINITION, argObj);
		}
	}

	public AlarmDefinitionStruct updateDefinition(AlarmDefinitionStruct alarmDefinitionStruct) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException, AlreadyExistsException
	{
		AlarmDefinitionStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_CHANGED_DEFINITION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_DEFINITION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": updateDefinition" + ":publishedEvent", ALARM_DEFINITION, argObj);
		}

		definitionProxy.updateDefinition(requestId, alarmDefinitionStruct);

		returnValue = (AlarmDefinitionStruct) getEventChannelData();

		if (isDebugPropertyOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": updateDefinition" + ":returned", ALARM_DEFINITION, argObj);
		}

		return returnValue;
	}

	public AlarmActivationStruct[] publishAllActivations() throws SystemException, CommunicationException, TimedOutException
	{
		AlarmActivationStruct[] returnValue = EMPTY_ALARM_ACTIVATION_STRUCT;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_ALARM_ACTIVATIONS;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			GUILoggerHome.find()
			        .debug(CATEGORY + ": publishAllActivations" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.publishAllActivations(requestId);

		try
		{
			returnValue = (AlarmActivationStruct[]) getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishAllActivations" + ":returned", ALARM_ACTIVATION, argObj);
		}

		return returnValue;
	}

	public AlarmActivationStruct publishActivationById(int activationId) throws NotFoundException, SystemException, CommunicationException,
	        TimedOutException
	{
		AlarmActivationStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_ALARM_ACTIVATIONS;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			GUILoggerHome.find()
			        .debug(CATEGORY + ": publishActivationById" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.publishActivationById(requestId, activationId);

		try
		{
			AlarmActivationStruct[] structs = (AlarmActivationStruct[]) getEventChannelData();
			if (structs.length > 0)
			{
				returnValue = structs[0];
			}
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishActivationById" + ":returned", ALARM_ACTIVATION, argObj);
		}

		return returnValue;
	}

	public AlarmActivationStruct createActivation(AlarmActivationStruct alarmActivationStruct) throws SystemException, CommunicationException,
	        AlreadyExistsException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		AlarmActivationStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_NEW_ACTIVATION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": createActivation" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.createActivation(requestId, alarmActivationStruct);

		try
		{
			returnValue = (AlarmActivationStruct) getEventChannelData();
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": createActivation" + ":returned", ALARM_ACTIVATION, argObj);
		}

		return returnValue;
	}

	public void deleteActivation(AlarmActivationStruct alarmActivationStruct) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_DELETE_ACTIVATION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": deleteActivation" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.deleteActivation(requestId, alarmActivationStruct);

		try
		{
			getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": deleteActivation" + ":returned", ALARM_ACTIVATION, argObj);
		}
	}

	public AlarmActivationStruct updateActivation(AlarmActivationStruct alarmActivationStruct) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException, AlreadyExistsException
	{
		AlarmActivationStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_CHANGED_ACTIVATION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": updateActivation" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.updateActivation(requestId, alarmActivationStruct);

		returnValue = (AlarmActivationStruct) getEventChannelData();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": updateActivation" + ":returned", ALARM_ACTIVATION, argObj);
		}

		return returnValue;
	}

	public AlarmActivationStruct activate(AlarmActivationStruct alarmActivationStruct) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		AlarmActivationStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_CHANGED_ACTIVATION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": activate" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.activate(requestId, alarmActivationStruct);

		try
		{
			returnValue = (AlarmActivationStruct) getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": activate" + ":returned", ALARM_ACTIVATION, argObj);
		}

		return returnValue;
	}

	public AlarmActivationStruct deactivate(AlarmActivationStruct alarmActivationStruct) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		AlarmActivationStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_CHANGED_ACTIVATION;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": deactivate" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.deactivate(requestId, alarmActivationStruct);

		try
		{
			returnValue = (AlarmActivationStruct) getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": deactivate" + ":returned", ALARM_ACTIVATION, argObj);
		}

		return returnValue;
	}

	public AlarmNotificationWatchdogStruct[] publishAllWatchdogs() throws SystemException, CommunicationException, TimedOutException
	{
		AlarmNotificationWatchdogStruct[] returnValue = EMPTY_ALARM_WATCHDOG_STRUCT;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_WATCHDOGS;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_WATCHDOG))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": publishAllWatchdogs" + ":publishedEvent", ALARM_WATCHDOG, argObj);
		}

		watchdogProxy.publishAllWatchdogs(requestId);

		try
		{
			returnValue = (AlarmNotificationWatchdogStruct[]) getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_WATCHDOG))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishAllWatchdogs" + ":returned", ALARM_WATCHDOG, argObj);
		}

		return returnValue;
	}

	public AlarmNotificationWatchdogStruct publishWatchdogById(int watchdogId) throws NotFoundException, SystemException, CommunicationException,
	        TimedOutException
	{
		AlarmNotificationWatchdogStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_WATCHDOGS;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_WATCHDOG))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": publishWatchdogById" + ":publishedEvent", ALARM_WATCHDOG, argObj);
		}

		watchdogProxy.publishWatchdogById(requestId, watchdogId);

		try
		{
			AlarmNotificationWatchdogStruct[] structs = (AlarmNotificationWatchdogStruct[]) getEventChannelData();
			if (structs.length > 0)
			{
				returnValue = structs[0];
			}
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_WATCHDOG))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishWatchdogById" + ":returned", ALARM_WATCHDOG, argObj);
		}

		return returnValue;
	}

	public AlarmNotificationWatchdogStruct createWatchdog(AlarmNotificationWatchdogStruct watchdog) throws SystemException, CommunicationException,
	        AlreadyExistsException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		AlarmNotificationWatchdogStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_NEW_WATCHDOG;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_WATCHDOG))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": createWatchdog" + ":publishedEvent", ALARM_WATCHDOG, argObj);
		}

		watchdogProxy.createWatchdog(requestId, watchdog);

		try
		{
			returnValue = (AlarmNotificationWatchdogStruct) getEventChannelData();
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_WATCHDOG))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": createWatchdog" + ":returned", ALARM_WATCHDOG, argObj);
		}

		return returnValue;
	}

	public AlarmNotificationWatchdogStruct updateWatchdog(AlarmNotificationWatchdogStruct watchdog) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException, AlreadyExistsException
	{
		AlarmNotificationWatchdogStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_CHANGED_WATCHDOG;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_WATCHDOG))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": updateWatchdog" + ":publishedEvent", ALARM_WATCHDOG, argObj);
		}

		watchdogProxy.updateWatchdog(requestId, watchdog);

		returnValue = (AlarmNotificationWatchdogStruct) getEventChannelData();

		if (isDebugPropertyOn(ALARM_WATCHDOG))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": updateWatchdog" + ":returned", ALARM_WATCHDOG, argObj);
		}

		return returnValue;
	}

	public void deleteWatchdog(AlarmNotificationWatchdogStruct watchdog) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
	{
		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_DELETE_WATCHDOG;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_WATCHDOG))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": deleteWatchdog" + ":publishedEvent", ALARM_WATCHDOG, argObj);
		}

		watchdogProxy.deleteWatchdog(requestId, watchdog);

		try
		{
			getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_WATCHDOG))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": deleteWatchdog" + ":returned", ALARM_WATCHDOG, argObj);
		}
	}

	/**
	 * @see com.cboe.interfaces.instrumentation.api.AlarmsAPI#createActivationAssignment(com.cboe.idl.alarm.ActivationAssignmentStruct)
     */
    @Override
    public ActivationAssignmentStruct createActivationAssignment(ActivationAssignmentStruct struct)
            throws CommunicationException, AlreadyExistsException, DataValidationException, NotAcceptedException, TransactionFailedException,
            TimedOutException, SystemException
    {
    	ActivationAssignmentStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}

		channelTypeToListen = ChannelType.IC_ACCEPT_NEW_ACTIVATION_ASSIGNMENT;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": createActivationAssignment" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.createActivationAssignment(requestId, struct);

		try
		{
			returnValue = (ActivationAssignmentStruct) getEventChannelData();
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": createActivationAssignment" + ":returned", ALARM_ACTIVATION, argObj);
		}

		return returnValue;
    }

	/**
     * @see com.cboe.interfaces.instrumentation.api.AlarmsAPI#updateActivationAssignment(com.cboe.idl.alarm.ActivationAssignmentStruct)
     */
    @Override
    public ActivationAssignmentStruct updateActivationAssignment(ActivationAssignmentStruct struct) throws SystemException,
            CommunicationException, NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
    {
		ActivationAssignmentStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": updateActivationAssignment" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.updateActivationAssignment(requestId, struct);

		try
        {
	        returnValue = (ActivationAssignmentStruct) getEventChannelData();
        }
        catch (AlreadyExistsException e)
        {
        	// this will never be reached
        }

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": updateActivationAssignment" + ":returned", ALARM_ACTIVATION, argObj);
		}

		return returnValue;
    }

	/**
     * @see com.cboe.interfaces.instrumentation.api.AlarmsAPI#deleteActivationAssignment(com.cboe.idl.alarm.ActivationAssignmentStruct)
     */
    @Override
    public void deleteActivationAssignment(ActivationAssignmentStruct struct) throws SystemException, CommunicationException,
            NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException, TimedOutException
    {
		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			debug(CATEGORY + ": deleteActivationAssignment" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.deleteActivationAssignment(requestId, struct);

		try
		{
			getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": deleteActivationAssignment" + ":returned", ALARM_ACTIVATION, argObj);
		}
    }

	/**
	 * @see com.cboe.interfaces.instrumentation.api.AlarmsAPI#publishActivationAssignmentById(int)
     */
    @Override
    public ActivationAssignmentStruct publishActivationAssignmentById(int id) throws NotFoundException, CommunicationException, TimedOutException, SystemException
    {
		ActivationAssignmentStruct returnValue = null;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_ALARM_ACTIVATION_ASSIGNMENTS;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			GUILoggerHome.find()
			        .debug(CATEGORY + ": publishActivationAssignmentById" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.publishActivationAssignmentById(requestId, id);

		try
		{
			ActivationAssignmentStruct[] structs = (ActivationAssignmentStruct[]) getEventChannelData();
			if (structs.length > 0)
			{
				returnValue = structs[0];
			}
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishActivationAssignmentById" + ":returned", ALARM_ACTIVATION, argObj);
		}

		return returnValue;
    }

	/**
	 * @see com.cboe.interfaces.instrumentation.api.AlarmsAPI#publishAllActivationAssignments()
     */
    @Override
    public ActivationAssignmentStruct[] publishAllActivationAssignments() throws CommunicationException, TimedOutException, SystemException
    {
		ActivationAssignmentStruct[] returnValue = EMPTY_ACTIVATION_ASSIGNMENT_STRUCT;

		try
		{
			requestId = getNextRequestId();
		}
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not obtain next Id from IdService.");
			return returnValue;
		}
		channelTypeToListen = ChannelType.IC_ACCEPT_ALARM_ACTIVATION_ASSIGNMENTS;

		subscribeEventChannel();

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			nowTime = System.currentTimeMillis();
			Object[] argObj = new Object[1];
			argObj[0] = "RequestId" + requestId;
			GUILoggerHome.find()
			        .debug(CATEGORY + ": publishAllActivationAssignments" + ":publishedEvent", ALARM_ACTIVATION, argObj);
		}

		activationProxy.publishAllActivationAssignments(requestId);

		try
		{
			returnValue = (ActivationAssignmentStruct[]) getEventChannelData();
		}
		catch (AlreadyExistsException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (TransactionFailedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotFoundException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (DataValidationException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}
		catch (NotAcceptedException e)
		{
			// not expected for this method
			GUILoggerHome.find().exception(getClass().getName(), "Unexpected Exception while executing Alarms Event Channel command.", e);
		}

		if (isDebugPropertyOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = "RequestId:" + requestId;
			argObj[1] = "Time:" + (System.currentTimeMillis() - nowTime);
			debug(CATEGORY + ": publishAllActivationAssignments" + ":returned", ALARM_ACTIVATION, argObj);
		}

		return returnValue;
    }

	public Object getEventChannelData() throws CommunicationException, SystemException, DataValidationException, AlreadyExistsException,
	        NotAcceptedException, NotFoundException, TransactionFailedException, TimedOutException
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
					// Either timed out or interrupted for data input. Do nothing.
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

	protected void processException() throws CommunicationException, SystemException, DataValidationException, AlreadyExistsException,
	        NotAcceptedException, NotFoundException, TransactionFailedException
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
				CommunicationException newException = new CommunicationException(exception.getMessage(), ((CommunicationException) exception).details);
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
				NotFoundException newException = new NotFoundException(exception.getMessage(), ((NotFoundException) exception).details);
				newException.initCause(exception);
				throw newException;
			}
			else if (exception instanceof AlreadyExistsException)
			{
				AlreadyExistsException newException = new AlreadyExistsException(exception.getMessage(), ((AlreadyExistsException) exception).details);
				newException.initCause(exception);
				throw newException;
			}
			else if (exception instanceof NotAcceptedException)
			{
				NotAcceptedException newException = new NotAcceptedException(exception.getMessage(), ((NotAcceptedException) exception).details);
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
		return "Exception while executing Alarms Event Channel command.";
	}

	protected String getTimeoutMessage()
	{
		return "Timed out while waiting for Alarms Event Channel response.";
	}

	protected void processTimeOut() throws TimedOutException
	{
		throw new TimedOutException(getTimeoutMessage(), getTimeout());
	}

	protected long getUniqueRequestId() throws SystemException, NotFoundException
	{
		return idService.getNextUUID();
	}

	private Long getNextRequestId() throws SystemException, NotFoundException
	{
		return new Long(getUniqueRequestId());
	}

	private void initialize()
	{
		idService = FoundationFramework.getInstance().getIdService();
		eventChannel = EventChannelAdapterFactory.find();
	}

	/**
	 * Convenience method for GUILogger.
	 * 
	 * @param windowTitle
	 * @param businessProperty
	 * @param structObjectArray
	 * @see IGUILogger#debug(String, IGUILoggerBusinessProperty, Object[])
	 */
	private static void debug(String windowTitle, IGUILoggerBusinessProperty businessProperty, Object[] structObjectArray)
	{
		GUILoggerHome.find().debug(windowTitle, businessProperty, structObjectArray);
	}

	/**
	 * Convenience method for GUILogger.
	 * 
	 * @param alarmWatchdog
	 * @return true if in debug mode
	 * @see IGUILogger#isDebugOn()
	 */
	private static boolean isDebugPropertyOn(IGUILoggerBusinessProperty businessProperty)
	{
		return isDebugOn && true && GUILoggerHome.find().isPropertyOn(businessProperty);
	}

}
