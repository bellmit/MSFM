//
// -----------------------------------------------------------------------------------
// Source file: AlarmsCache.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_ACTIVATION;
import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_ASSIGNMENT;
import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_CALCULATION;
import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_CONDITION;
import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_DEFINITION;
import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_EXCEPTIONS;
import static com.cboe.presentation.common.logging.GUILoggerINBusinessProperty.ALARM_WATCHDOG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
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
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.interfaces.instrumentation.alarms.ActivationAssignment;
import com.cboe.interfaces.instrumentation.alarms.AlarmActivation;
import com.cboe.interfaces.instrumentation.alarms.AlarmCalculation;
import com.cboe.interfaces.instrumentation.alarms.AlarmCondition;
import com.cboe.interfaces.instrumentation.alarms.AlarmConditionMutable;
import com.cboe.interfaces.instrumentation.alarms.AlarmDefinition;
import com.cboe.interfaces.instrumentation.alarms.AlarmDefinitionMutable;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationWatchdog;
import com.cboe.interfaces.instrumentation.alarms.AlarmPublishersHome;
import com.cboe.interfaces.instrumentation.api.AlarmsAPI;
import com.cboe.interfaces.instrumentationCollector.AlarmConstants;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperty;
import com.cboe.presentation.alarms.ActivationAssignmentFactory;
import com.cboe.presentation.alarms.AlarmActivationFactory;
import com.cboe.presentation.alarms.AlarmActivationImpl;
import com.cboe.presentation.alarms.AlarmCalculationFactory;
import com.cboe.presentation.alarms.AlarmConditionFactory;
import com.cboe.presentation.alarms.AlarmDefinitionFactory;
import com.cboe.presentation.alarms.AlarmNotificationWatchdogFactory;
import com.cboe.presentation.alarms.ActivationAssignmentImpl;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelListener;

/**
 * Provides caching and cross-referencing functionality for AlarmCalculations, AlarmConditions,
 * AlarmDefinitions, AlarmActivations and AlarmNotificiationWatchdog's.
 */
@SuppressWarnings({"deprecation"})
public class AlarmsCache implements EventChannelListener
{
	private static final Integer GUI_KEY = -1;
	private static final Integer CONSUMER_KEY = 0;

	private static final AlarmCondition[] EMPTY_ALARM_CONDITION_ARRAY = new AlarmCondition[0];
	private static final AlarmCalculation[] EMPTY_ALARM_CALCULATION_ARRAY = new AlarmCalculation[0];
	private static final AlarmDefinition[] EMPTY_ALARM_DEFINITION_ARRAY = new AlarmDefinition[0];
	private static final AlarmActivation[] EMPTY_ALARM_ACTIVATION_ARRAY = new AlarmActivation[0];
	private static final AlarmNotificationWatchdog[] EMPTY_WATCHDOG_ARRAY = new AlarmNotificationWatchdog[0];
    private static final AlarmActivation[] EMPTY_ALARM_ASSIGNMENT_ARRAY = new AlarmActivation[0];

    private Map<Integer, AlarmCondition> conditionsById;
	private Map<String, List<AlarmCondition>> conditionsByName;
	private Map<Integer, AlarmCalculation> calculationsById;
	private Map<String, AlarmCalculation> calculationsByName;
	private Map<String, List<AlarmCondition>> conditionsByCalculation;
	private Map<Integer, AlarmDefinition> definitionsById;
	private Map<String, List<AlarmDefinition>> definitionsByName;
	private Map<AlarmCondition, List<AlarmDefinition>> definitionsByCondition;
	private Map<Integer, List<AlarmActivation>> activationsByDefinitionKey;
	private Map<Integer, AlarmActivation> activationsById;
	private Map<Integer, AlarmNotificationWatchdog> watchdogsById;
	private Map<Integer, AlarmNotificationWatchdog> watchdogsByActivationId;
	private Map<Integer, ActivationAssignment> assignmentsById;
	private Map<Integer, ActivationAssignment> assignmentsByActivationId;

	private EventChannelAdapter eventChannel;
	private int apiTimeoutMillis = 10000;

	private AlarmPublishersHome publisherHome;

	private static final String CATEGORY = AlarmsCache.class.getName();

	private static final Object CONDITION_LOCK = new Object();
	private static final Object CALCULATION_LOCK = new Object();
	private static final Object DEFINITION_LOCK = new Object();
	private static final Object ACTIVATION_LOCK = new Object();
	private static final Object ASSIGNMENT_LOCK = new Object();
	private static final Object WATCHDOG_LOCK = new Object();

	public AlarmsCache(EventChannelAdapter eventChannel, int apiTimeoutMillis, AlarmPublishersHome publisherHome)
	{
		conditionsById = new HashMap<Integer, AlarmCondition>(201);
		conditionsByName = new HashMap<String, List<AlarmCondition>>(201);
		calculationsById = new HashMap<Integer, AlarmCalculation>(201);
		calculationsByName = new HashMap<String, AlarmCalculation>(201);
		conditionsByCalculation = new HashMap<String, List<AlarmCondition>>(201);
		definitionsById = new HashMap<Integer, AlarmDefinition>(201);
		definitionsByName = new HashMap<String, List<AlarmDefinition>>(201);
		definitionsByCondition = new HashMap<AlarmCondition, List<AlarmDefinition>>(201);
		activationsByDefinitionKey = new HashMap<Integer, List<AlarmActivation>>(201);
		activationsById = new HashMap<Integer, AlarmActivation>(201);
		watchdogsById = new HashMap<Integer, AlarmNotificationWatchdog>(201);
		watchdogsByActivationId = new HashMap<Integer, AlarmNotificationWatchdog>(201);
		assignmentsById = new HashMap<Integer, ActivationAssignment>(201);
		assignmentsByActivationId = new HashMap<Integer, ActivationAssignment>(201);

		this.eventChannel = eventChannel;
		this.apiTimeoutMillis = apiTimeoutMillis;
		this.publisherHome = publisherHome;
	}

	public synchronized void channelUpdate(ChannelEvent event)
	{
        ChannelKey key = (ChannelKey) event.getChannel();
        // noinspection NonPrivateFieldAccessedInSynchronizedContext
        int channelType = key.channelType;

        try
        {
            Object eventData = event.getEventData();

            switch (channelType)
            {
                case ChannelType.IC_ACCEPT_NEW_CONDITION:
                case ChannelType.IC_ACCEPT_CHANGED_CONDITION:
                case ChannelType.IC_ACCEPT_DELETED_CONDITION:
                    if (isDebugOn(ALARM_CONDITION))
                    {
                        Object[] argObj = new Object[2];
                        argObj[0] = eventData;
                        argObj[1] = channelType;

                        debug(CATEGORY + ": channelUpdate", ALARM_CONDITION, argObj);
                    }

                    processConditionEvent((AlarmConditionStruct) eventData, channelType);
                    break;

                case ChannelType.IC_ACCEPT_NEW_CALCULATION:
                case ChannelType.IC_ACCEPT_CHANGED_CALCULATION:
                case ChannelType.IC_ACCEPT_DELETED_CALCULATION:
                    if (isDebugOn(ALARM_CALCULATION))
                    {
                        Object[] argObj = new Object[2];
                        argObj[0] = eventData;
                        argObj[1] = channelType;
                        debug(CATEGORY + ": channelUpdate", ALARM_CALCULATION, argObj);
                    }

                    processCalculationEvent((AlarmCalculationStruct) eventData, channelType);
                    break;

                case ChannelType.IC_ACCEPT_NEW_DEFINITION:
                case ChannelType.IC_ACCEPT_CHANGED_DEFINITION:
                case ChannelType.IC_ACCEPT_DELETE_DEFINITION:
                    if (isDebugOn(ALARM_DEFINITION))
                    {
                        Object[] argObj = new Object[2];
                        argObj[0] = eventData;
                        argObj[1] = channelType;

                        debug(CATEGORY + ": channelUpdate", ALARM_DEFINITION, argObj);
                    }

                    processDefinitionEvent((AlarmDefinitionStruct) eventData, channelType);
                    break;

                case ChannelType.IC_ACCEPT_NEW_ACTIVATION:
                case ChannelType.IC_ACCEPT_CHANGED_ACTIVATION:
                case ChannelType.IC_ACCEPT_DELETE_ACTIVATION:
                    if (isDebugOn(ALARM_ACTIVATION))
                    {
                        Object[] argObj = new Object[2];
                        argObj[0] = eventData;
                        argObj[1] = channelType;

                        debug(CATEGORY + ": channelUpdate", ALARM_ACTIVATION, argObj);
                    }

                    processActivationEvent((AlarmActivationStruct) eventData, channelType);
                    break;

                case ChannelType.IC_ACCEPT_NEW_WATCHDOG:
                case ChannelType.IC_ACCEPT_CHANGED_WATCHDOG:
                case ChannelType.IC_ACCEPT_DELETE_WATCHDOG:
                    if (isDebugOn(ALARM_WATCHDOG))
                    {
                        Object[] argObj = new Object[2];
                        argObj[0] = eventData;
                        argObj[1] = channelType;

                        debug(CATEGORY + ": channelUpdate", ALARM_WATCHDOG, argObj);
                    }

                    processWatchdogEvent((AlarmNotificationWatchdogStruct) eventData, channelType);
                    break;

                case ChannelType.IC_ACCEPT_NEW_ACTIVATION_ASSIGNMENT:
                case ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT:
                case ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT:
                    if(isDebugOn(ALARM_ASSIGNMENT))
                    {
                        Object[] argObj = new Object[2];
                        argObj[0] = eventData;
                        argObj[1] = channelType;

                        debug(CATEGORY + ": channelUpdate", ALARM_ASSIGNMENT, argObj);
                    }

                    processAssignmentEvent((ActivationAssignmentStruct) eventData, channelType);
                    break;

                default:
                    if (isDebugOn(ALARM_EXCEPTIONS))
                    {
                        Object[] argObj = new Object[2];
                        argObj[0] = eventData;
                        argObj[1] = channelType;

                        debug(CATEGORY + ": channelUpdate : unexpected event", ALARM_EXCEPTIONS, argObj);
                    }

                    GUILoggerHome.find().alarm("Unexpected event received by AlarmsCache: channelType:" + channelType, event.getEventData());
                    break;
            }
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(
                    "Exception while processing event received by AlarmsCache: channelType:" +
                    getEventTypeName(channelType), e);
        }
	}

	public synchronized void initialize()
	{
		subscribeChannels();

		// this order is important since definitions need conditions loaded first for reference,
		// and activations need definitions loaded first for reference.
		loadAllCalculations();
		loadAllConditions();
		loadAllDefinitions();
		loadAllActivations();
		loadAllWatchdogs();
		loadAllActivationAssignments();
	}

	public void cleanup()
	{
		unsubscribeChannels();

		conditionsById.clear();
		conditionsByName.clear();
		calculationsById.clear();
		calculationsByName.clear();
		conditionsByCalculation.clear();
		definitionsById.clear();
		definitionsByName.clear();
		definitionsByCondition.clear();
		activationsByDefinitionKey.clear();
		activationsById.clear();
		watchdogsById.clear();
		watchdogsByActivationId.clear();
        assignmentsByActivationId.clear();
        assignmentsById.clear();
    }

	public AlarmCondition[] getAllAlarmConditions()
	{
        if (isDebugOn(ALARM_CONDITION))
		{
			debug(CATEGORY + ": getAllAlarmConditions" + ":entry", ALARM_CONDITION);
		}
        
        AlarmCondition[] conditions;
		synchronized (CONDITION_LOCK)
		{
			conditions = conditionsById.values().toArray(EMPTY_ALARM_CONDITION_ARRAY);
		}

		if (isDebugOn(ALARM_CONDITION))
		{
			if (conditions != null && conditions.length > 0)
			{
				Object[] argObj = new Object[1];
				for (AlarmCondition condition : conditions)
				{

					argObj[0] = condition.getStruct();
					debug(CATEGORY + ": getAllAlarmConditions" + ":exit", ALARM_CONDITION, argObj);
				}
			}
			else
			{
				debug(CATEGORY + ": getAllAlarmConditions" + ":exit", ALARM_CONDITION, "conditions were null or zero length");
			}
		}

		return conditions;
	}

	public AlarmCondition findAlarmConditionById(Integer conditionId)
	{
		if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = conditionId;
			debug(CATEGORY + ": findAlarmConditionById" + ":entry", ALARM_CONDITION, argObj);
		}

		AlarmCondition condition;
		synchronized (CONDITION_LOCK)
		{
			condition = conditionsById.get(conditionId);
		}

		if (isDebugOn(ALARM_CONDITION))
		{
			if (condition != null)
			{
				Object[] argObj = new Object[1];

				argObj[0] = condition.getStruct();
				debug(CATEGORY + ": findAlarmConditionById" + ":exit", ALARM_CONDITION, argObj);
			}
			else
			{
				debug(CATEGORY + ": findAlarmConditionById" + ":exit", ALARM_CONDITION, "condition was null");
			}
		}

		return condition;
	}

	public AlarmCondition[] findAlarmConditionsByName(String name)
	{
		if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = name;
			debug(CATEGORY + ": findAlarmConditionsByName" + ":entry", ALARM_CONDITION, argObj);
		}

		AlarmCondition[] conditions = EMPTY_ALARM_CONDITION_ARRAY;
		synchronized (CONDITION_LOCK)
		{
			List<AlarmCondition> list = conditionsByName.get(name);
			if (list != null)
			{
				conditions = list.toArray(conditions);
			}
		}

		if (isDebugOn(ALARM_CONDITION))
		{
			if (conditions != null && conditions.length > 0)
			{
				Object[] argObj = new Object[1];
				for (AlarmCondition condition : conditions)
				{

					argObj[0] = condition.getStruct();
					debug(CATEGORY + ": findAlarmConditionsByName" + ":exit", ALARM_CONDITION, argObj);
				}
			}
			else
			{
				GUILoggerHome.find()
				        .debug(CATEGORY + ": findAlarmConditionsByName" + ":exit", ALARM_CONDITION, "conditions were null or zero length");
			}
		}

		return conditions;
	}

	public AlarmCondition addAlarmCondition(AlarmCondition condition) throws SystemException, CommunicationException, AlreadyExistsException,
	        DataValidationException, NotAcceptedException, TransactionFailedException
	{
        audit(CATEGORY + ": addAlarmCondition with id: " + condition.getId() + ", name: " + condition.getName());
        if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = condition.getStruct();
			debug(CATEGORY + ": addAlarmCondition" + ":entry", ALARM_CONDITION, argObj);
		}

		AlarmCondition addedCondition;

		if (condition.isSaved())
		{
			AlarmCondition existingCondition = findAlarmConditionById(condition.getId());
			if (existingCondition != null)
			{
				throw ExceptionBuilder.alreadyExistsException("Alarm condition with condition Id:" + condition.getId() + ", already exists.", 0);
			}
		}

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
			synchronized (CONDITION_LOCK)
			{

				AlarmConditionStruct addedStruct = api.createCondition(condition.getStruct());
				if (addedStruct != null)
				{
					addedCondition = addConditionToCache(addedStruct);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_CONDITION, GUI_KEY),
					        addedCondition);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_CONDITION, addedCondition.getId()),
					        addedCondition);
					eventChannel.dispatch(guiEvent);
				}
				else
				{
					throw ExceptionBuilder.systemException("API to create Alarm Condition returned NULL.", 0);
				}
			}
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}

		if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = addedCondition.getStruct();
			debug(CATEGORY + ": addAlarmCondition" + ":exit", ALARM_CONDITION, argObj);
		}

		return addedCondition;
	}

	public AlarmCondition updateAlarmCondition(AlarmCondition condition) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
	{
        audit(CATEGORY + ": updateAlarmCondition with id: " + condition.getId() + ", name: " +
              condition.getName());
        if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = condition.getStruct();
			debug(CATEGORY + ": updateAlarmCondition" + ":entry", ALARM_CONDITION, argObj);
		}

		AlarmCondition updatedCondition;

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
			synchronized (CONDITION_LOCK)
			{
				AlarmCondition matchingCondition = findAlarmConditionById(condition.getId());
				if (matchingCondition != null)
				{

					AlarmConditionStruct updatedStruct = api.updateCondition(condition.getStruct());
					if (updatedStruct != null)
					{
						updatedCondition = updateConditionInCache(updatedStruct, matchingCondition);
						ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION,
						        GUI_KEY), updatedCondition);
						eventChannel.dispatch(guiEvent);
						guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, updatedCondition
						        .getId()), updatedCondition);
						eventChannel.dispatch(guiEvent);
					}
					else
					{
						throw ExceptionBuilder.systemException("API to update Alarm Condition returned NULL.", 0);
					}
				}
				else
				{
					throw ExceptionBuilder.notFoundException("Alarm condition with condition Id:" + condition.getId() + ", could not be found.",
					        NotFoundCodes.RESOURCE_DOESNT_EXIST);
				}
			}
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}

		if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = updatedCondition.getStruct();
			debug(CATEGORY + ": updateAlarmCondition" + ":exit", ALARM_CONDITION, argObj);
		}

		return updatedCondition;
	}

	public AlarmCondition removeAlarmCondition(AlarmCondition condition) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException
	{
        audit(CATEGORY + ": removeAlarmCondition with id: " + condition.getId() + ", name: " +
              condition.getName());
        if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = condition.getStruct();
			debug(CATEGORY + ": removeAlarmCondition" + ":entry", ALARM_CONDITION, argObj);
		}

		AlarmCondition removedCondition = findAlarmConditionById(condition.getId());

		if (removedCondition != null)
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			try
			{
				synchronized (CONDITION_LOCK)
				{

					api.deleteCondition(removedCondition.getStruct());
					deleteConditionFromCache(removedCondition);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CONDITION, GUI_KEY),
					        removedCondition);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CONDITION, removedCondition.getId()),
					        removedCondition);
					eventChannel.dispatch(guiEvent);
				}
			}
			catch (TimedOutException e)
			{
				SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
				systemException.initCause(e);
				throw systemException;
			}
		}
		else
		{
			throw ExceptionBuilder.notFoundException("Alarm condition with condition Id:" + condition.getId() + ", could not be found.",
			        NotFoundCodes.RESOURCE_DOESNT_EXIST);
		}

		if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = removedCondition.getStruct();
			debug(CATEGORY + ": removeAlarmCondition" + ":exit", ALARM_CONDITION, argObj);
		}

		return removedCondition;
	}

	public AlarmCalculation[] getAllAlarmCalculations()
	{
		if (isDebugOn(ALARM_CALCULATION))
		{
			debug(CATEGORY + ": getAllAlarmCalculations" + ":entry", ALARM_CALCULATION);
		}

		AlarmCalculation[] calculations;
		synchronized (CALCULATION_LOCK)
		{
			calculations = calculationsById.values().toArray(EMPTY_ALARM_CALCULATION_ARRAY);
		}

		if (isDebugOn(ALARM_CALCULATION))
		{
			if (calculations != null && calculations.length > 0)
			{
				Object[] argObj = new Object[1];
				for (AlarmCalculation calculation : calculations)
				{

					argObj[0] = calculation.getStruct();
					debug(CATEGORY + ": getAllAlarmCalculations" + ":exit", ALARM_CALCULATION, argObj);
				}
			}
			else
			{
				debug(CATEGORY + ": getAllAlarmCalculations" + ":exit", ALARM_CALCULATION, "calculations were null or zero length");
			}
		}

		return calculations;
	}

	public AlarmCalculation findAlarmCalculationById(Integer calculationId)
	{
		if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = calculationId;
			debug(CATEGORY + ": findAlarmCalculationById" + ":entry", ALARM_CALCULATION, argObj);
		}

		AlarmCalculation calculation;
		synchronized (CALCULATION_LOCK)
		{
			calculation = calculationsById.get(calculationId);
		}

		if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(ALARM_CALCULATION))
		{
			if (calculation != null)
			{
				Object[] argObj = new Object[1];

				argObj[0] = calculation.getStruct();
				debug(CATEGORY + ": findAlarmCalculationById" + ":exit", ALARM_CALCULATION, argObj);
			}
			else
			{
				debug(CATEGORY + ": findAlarmCalculationById" + ":exit", ALARM_CALCULATION, "calculation was null");
			}
		}

		return calculation;
	}

	public AlarmCalculation findAlarmCalculationsByName(String name)
	{
		if (isDebugOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = name;
			debug(CATEGORY + ": findAlarmCalculationsByName" + ":entry", ALARM_CALCULATION, argObj);
		}

		AlarmCalculation calculation;
		synchronized (CALCULATION_LOCK)
		{
			calculation = calculationsByName.get(name);
		}

		if (isDebugOn(ALARM_CALCULATION))
		{
			if (calculation != null)
			{
				Object[] argObj = new Object[1];
				argObj[0] = calculation.getStruct();
				debug(CATEGORY + ": findAlarmCalculationsByName" + ":exit", ALARM_CALCULATION, argObj);
			}
			else
			{
				debug(CATEGORY + ": findAlarmCalculationsByName" + ":exit", ALARM_CALCULATION, "calculation was null");
			}
		}

		return calculation;
	}

	public AlarmCalculation addAlarmCalculation(AlarmCalculation calculation) throws SystemException, CommunicationException, AlreadyExistsException,
	        DataValidationException, NotAcceptedException, TransactionFailedException
	{
        audit(CATEGORY + ": addAlarmCalculation with id: " + calculation.getId() + ", name: " +
              calculation.getName());
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = calculation.getStruct();
			debug(CATEGORY + ": addAlarmCalculation" + ":entry", ALARM_CALCULATION, argObj);
		}

		AlarmCalculation addedCalculation;

		if (calculation.isSaved())
		{
			AlarmCalculation existingCalculation = findAlarmCalculationById(calculation.getId());
			if (existingCalculation != null)
			{
				throw ExceptionBuilder
				        .alreadyExistsException("Alarm calculation with calculation Id:" + calculation.getId() + ", already exists.", 0);
			}
		}

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
			synchronized (CALCULATION_LOCK)
			{

				AlarmCalculationStruct addedStruct = api.createCalculation(calculation.getStruct());
				if (addedStruct != null)
				{
					addedCalculation = addCalculationToCache(addedStruct);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_CALCULATION, GUI_KEY),
					        addedCalculation);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_CALCULATION, addedCalculation.getId()),
					        addedCalculation);
					eventChannel.dispatch(guiEvent);
				}
				else
				{
					throw ExceptionBuilder.systemException("API to create Alarm Calculation returned NULL.", 0);
				}
			}
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}

		if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = addedCalculation.getStruct();
			debug(CATEGORY + ": addAlarmCalculation" + ":exit", ALARM_CALCULATION, argObj);
		}

		return addedCalculation;
	}

	public AlarmCalculation updateAlarmCalculation(AlarmCalculation calculation) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
	{
        audit(CATEGORY + ": updateAlarmCalculation with id: " + calculation.getId() + ", name: " +
              calculation.getName());
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = calculation.getStruct();
			debug(CATEGORY + ": updateAlarmCalculation" + ":entry", ALARM_CALCULATION, argObj);
		}

		AlarmCalculation updatedCalculation;

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
			synchronized (CALCULATION_LOCK)
			{
				AlarmCalculation matchingCalculation = findAlarmCalculationById(calculation.getId());
				if (matchingCalculation != null)
				{

					AlarmCalculationStruct updatedStruct = api.updateCalculation(calculation.getStruct());
					if (updatedStruct != null)
					{
						updatedCalculation = updateCalculationInCache(updatedStruct, matchingCalculation);
						ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CALCULATION,
						        GUI_KEY), updatedCalculation);
						eventChannel.dispatch(guiEvent);
						guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CALCULATION, updatedCalculation
						        .getId()), updatedCalculation);
						eventChannel.dispatch(guiEvent);
					}
					else
					{
						throw ExceptionBuilder.systemException("API to update Alarm Calculation returned NULL.", 0);
					}
				}
				else
				{
					throw ExceptionBuilder.notFoundException(
					        "Alarm calculation with calculation Id:" + calculation.getId() + ", could not be found.",
					        NotFoundCodes.RESOURCE_DOESNT_EXIST);
				}
			}
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}

		if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = updatedCalculation.getStruct();
			debug(CATEGORY + ": updateAlarmCalculation" + ":exit", ALARM_CALCULATION, argObj);
		}

		return updatedCalculation;
	}

	/*
	 * Removes the alarm calculation after first removing any conditions that use the specified
	 * alarm calculation.
	 */
	public AlarmCalculation removeAlarmCalculation(AlarmCalculation calculation) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException
	{
        audit(CATEGORY + ": removeAlarmCalculation with id: " + calculation.getId() + ", name: " +
              calculation.getName());
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = calculation.getStruct();
			debug(CATEGORY + ": removeAlarmCalculation" + ":entry", ALARM_CALCULATION, argObj);
		}

		AlarmCalculation removedCalculation = findAlarmCalculationById(calculation.getId());

		if (removedCalculation != null)
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			try
			{
				synchronized (CALCULATION_LOCK)
				{
					// Must remove conditions that use the removedCalculation first
					removeConditionsForCalculation(removedCalculation);

					api.deleteCalculation(removedCalculation.getStruct());
					deleteCalculationFromCache(removedCalculation);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this,
					        new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CALCULATION, GUI_KEY), removedCalculation);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CALCULATION, removedCalculation
					        .getId()), removedCalculation);
					eventChannel.dispatch(guiEvent);
				}
			}
			catch (TimedOutException e)
			{
				SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
				systemException.initCause(e);
				throw systemException;
			}
		}
		else
		{
			throw ExceptionBuilder.notFoundException("Alarm calculation with calculation Id:" + calculation.getId() + ", could not be found.",
			        NotFoundCodes.RESOURCE_DOESNT_EXIST);
		}

		if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = removedCalculation.getStruct();
			debug(CATEGORY + ": removeAlarmCalculation" + ":exit", ALARM_CALCULATION, argObj);
		}

		return removedCalculation;
	}

	public AlarmDefinition[] getAllAlarmDefinitions()
	{
		if (isDebugOn(ALARM_DEFINITION))
		{
			debug(CATEGORY + ": getAllAlarmDefinitions" + ":entry", ALARM_DEFINITION);
		}

		AlarmDefinition[] definitions;
		synchronized (DEFINITION_LOCK)
		{
			definitions = definitionsById.values().toArray(EMPTY_ALARM_DEFINITION_ARRAY);
		}

		if (isDebugOn(ALARM_DEFINITION))
		{
			if (definitions != null && definitions.length > 0)
			{
				Object[] argObj = new Object[1];
				for (AlarmDefinition definition : definitions)
				{

					argObj[0] = definition.getStruct();
					debug(CATEGORY + ": getAllAlarmDefinitions" + ":exit", ALARM_DEFINITION, argObj);
				}
			}
			else
			{
				debug(CATEGORY + ": getAllAlarmDefinitions" + ":exit", ALARM_DEFINITION, "definitions were null or zero length");
			}
		}

		return definitions;
	}

	public AlarmDefinition findAlarmDefinitionById(Integer definitionId)
	{
		if (isDebugOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = definitionId;
			debug(CATEGORY + ": findAlarmDefinitionById" + ":entry", ALARM_DEFINITION, argObj);
		}

		AlarmDefinition definition;
		synchronized (DEFINITION_LOCK)
		{
			definition = definitionsById.get(definitionId);
		}

		if (isDebugOn(ALARM_DEFINITION))
		{
			if (definition != null)
			{
				Object[] argObj = new Object[1];

				argObj[0] = definition.getStruct();
				debug(CATEGORY + ": findAlarmDefinitionById" + ":exit", ALARM_DEFINITION, argObj);
			}
			else
			{
				debug(CATEGORY + ": findAlarmDefinitionById" + ":exit", ALARM_DEFINITION, "definition was null");
			}
		}

		return definition;
	}

	public AlarmDefinition[] findAlarmDefinitionsByName(String name)
	{
		if (isDebugOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = name;
			debug(CATEGORY + ": findAlarmDefinitionsByName" + ":entry", ALARM_DEFINITION, argObj);
		}

		AlarmDefinition[] definitions = EMPTY_ALARM_DEFINITION_ARRAY;
		synchronized (DEFINITION_LOCK)
		{
			List<AlarmDefinition> list = definitionsByName.get(name);
			if (list != null)
			{
				definitions = list.toArray(definitions);
			}
		}

		if (isDebugOn(ALARM_DEFINITION))
		{
			if (definitions != null && definitions.length > 0)
			{
				Object[] argObj = new Object[1];
				for (AlarmDefinition definition : definitions)
				{

					argObj[0] = definition.getStruct();
					debug(CATEGORY + ": findAlarmDefinitionsByName" + ":exit", ALARM_DEFINITION, argObj);
				}
			}
			else
			{
				debug(CATEGORY + ": findAlarmDefinitionsByName" + ":exit", ALARM_DEFINITION, "definitions were null or zero length");
			}
		}

		return definitions;
	}

	public AlarmDefinition[] findAlarmDefinitionsForCondition(AlarmCondition condition)
	{
		if (isDebugOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = condition.getStruct();
			debug(CATEGORY + ": findAlarmDefinitionsForCondition" + ":entry", ALARM_DEFINITION, argObj);
		}

		AlarmDefinition[] definitions = EMPTY_ALARM_DEFINITION_ARRAY;

		synchronized (CONDITION_LOCK)
		{
			synchronized (DEFINITION_LOCK)
			{
				Collection<AlarmDefinition> collection = definitionsByCondition.get(condition);

				if (collection != null)
				{
					definitions = collection.toArray(definitions);
				}
			}
		}

		if (isDebugOn(ALARM_DEFINITION))
		{
			if (definitions != null && definitions.length > 0)
			{
				Object[] argObj = new Object[1];
				for (AlarmDefinition definition : definitions)
				{

					argObj[0] = definition.getStruct();
					debug(CATEGORY + ": findAlarmDefinitionsForCondition" + ":exit", ALARM_DEFINITION, argObj);
				}
			}
			else
			{
				debug(CATEGORY + ": findAlarmDefinitionsForCondition" + ":exit", ALARM_DEFINITION, "definitions were null or zero length");
			}
		}

		return definitions;
	}

	public AlarmDefinition addAlarmDefinition(AlarmDefinition definition) throws SystemException, CommunicationException, AlreadyExistsException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, NotFoundException
	{
        audit(CATEGORY + ": addAlarmDefinition with id: " + definition.getId() + ", name: " +
              definition.getName());
        if (isDebugOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = definition.getStruct();
			debug(CATEGORY + ": addAlarmDefinition" + ":entry", ALARM_DEFINITION, argObj);
		}

		AlarmDefinition addedDefinition;

		if (definition.isSaved())
		{
			AlarmDefinition existingDefinition = findAlarmDefinitionById(definition.getId());
			if (existingDefinition != null)
			{
				throw ExceptionBuilder.alreadyExistsException("Alarm definition with definition Id:" + definition.getId() + ", already exists.", 0);
			}
		}

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
			synchronized (CONDITION_LOCK)
			{
				synchronized (DEFINITION_LOCK)
				{
					AlarmDefinitionStruct addedStruct = api.createDefinition(definition.getStruct());

					if (addedStruct != null)
					{
						addedDefinition = addDefinitionToCache(addedStruct);
						ChannelEvent guiEvent = eventChannel.getChannelEvent(this,
						        new ChannelKey(ChannelType.IC_ACCEPT_NEW_DEFINITION, GUI_KEY), addedDefinition);
						eventChannel.dispatch(guiEvent);
						guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_DEFINITION, addedDefinition.getId()),
						        addedDefinition);
						eventChannel.dispatch(guiEvent);
					}
					else
					{
						throw ExceptionBuilder.systemException("API to create Alarm Definition returned NULL.", 0);
					}
				}
			}
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}

		if (isDebugOn(ALARM_DEFINITION))
		{
			// noinspection ConstantConditions
			if (addedDefinition != null)
			{
				Object[] argObj = new Object[1];

				argObj[0] = addedDefinition.getStruct();
				debug(CATEGORY + ": addAlarmDefinition" + ":exit", ALARM_DEFINITION, argObj);
			}
			else
			{
				debug(CATEGORY + ": addAlarmDefinition" + ":exit", ALARM_DEFINITION, "addedDefinition was null");
			}
		}

		return addedDefinition;
	}

	public AlarmDefinition updateAlarmDefinition(AlarmDefinition definition) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
	{
        audit(CATEGORY + ": updateAlarmDefinition with id: " + definition.getId() + ", name: " +
              definition.getName());
        if (isDebugOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = definition.getStruct();
			debug(CATEGORY + ": updateAlarmDefinition" + ":entry", ALARM_DEFINITION, argObj);
		}

		AlarmDefinition updatedDefinition;

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
			synchronized (CONDITION_LOCK)
			{
				synchronized (DEFINITION_LOCK)
				{
					AlarmDefinition matchingDefinition = findAlarmDefinitionById(definition.getId());
					if (matchingDefinition != null)
					{
						AlarmDefinitionStruct updatedStruct = api.updateDefinition(definition.getStruct());

						if (updatedStruct != null)
						{
							updatedDefinition = updateDefinitionInCache(updatedStruct, matchingDefinition);
							ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION,
							        GUI_KEY), updatedDefinition);
							eventChannel.dispatch(guiEvent);
							guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, updatedDefinition
							        .getId()), updatedDefinition);
							eventChannel.dispatch(guiEvent);
						}
						else
						{
							throw ExceptionBuilder.systemException("API to update Alarm Definition returned NULL.", 0);
						}
					}
					else
					{
						throw ExceptionBuilder.notFoundException("Alarm definition with definition Id:" + definition.getId()
						        + ", could not be found.", NotFoundCodes.RESOURCE_DOESNT_EXIST);
					}
				}
			}
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}

		if (isDebugOn(ALARM_DEFINITION))
		{
			// noinspection ConstantConditions
			if (updatedDefinition == null)
			{
				debug(CATEGORY + ": updateAlarmDefinition" + ":exit", ALARM_DEFINITION, "updatedDefinition was null");
			}
			else
			{
				Object[] argObj = new Object[1];

				argObj[0] = updatedDefinition.getStruct();
				debug(CATEGORY + ": updateAlarmDefinition" + ":exit", ALARM_DEFINITION, argObj);
			}
		}

		return updatedDefinition;
	}

	public AlarmDefinition removeAlarmDefinition(AlarmDefinition definition) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException
	{
        audit(CATEGORY + ": removeAlarmDefinition with id: " + definition.getId() + ", name: " +
              definition.getName());
        if (isDebugOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[1];

			argObj[0] = definition.getStruct();
			debug(CATEGORY + ": removeAlarmDefinition" + ":entry", ALARM_DEFINITION, argObj);
		}

		AlarmDefinition removedDefinition = findAlarmDefinitionById(definition.getId());

		if (removedDefinition != null)
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			try
			{
				synchronized (CONDITION_LOCK)
				{
					synchronized (DEFINITION_LOCK)
					{
						api.deleteDefinition(removedDefinition.getStruct());
						deleteDefinitionFromCache(removedDefinition);
						ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_DEFINITION,
						        GUI_KEY), removedDefinition);
						eventChannel.dispatch(guiEvent);
						guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_DEFINITION, removedDefinition
						        .getId()), removedDefinition);
						eventChannel.dispatch(guiEvent);
					}
				}
			}
			catch (TimedOutException e)
			{
				SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
				systemException.initCause(e);
				throw systemException;
			}
		}
		else
		{
			throw ExceptionBuilder.notFoundException("Alarm definition with definition Id:" + definition.getId() + ", could not be found.",
			        NotFoundCodes.RESOURCE_DOESNT_EXIST);
		}

		if (isDebugOn(ALARM_DEFINITION))
		{
			// noinspection ConstantConditions
			if (removedDefinition != null)
			{
				Object[] argObj = new Object[1];

				argObj[0] = removedDefinition.getStruct();
				debug(CATEGORY + ": removeAlarmDefinition" + ":exit", ALARM_DEFINITION, argObj);
			}
			else
			{
				debug(CATEGORY + ": removeAlarmDefinition" + ":exit", ALARM_DEFINITION, "removedDefinition was null");
			}
		}

		return removedDefinition;
	}

	public AlarmActivation[] findAlarmActivationsByDefinitionId(Integer definitionId)
	{
		if (isDebugOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = definitionId;
			debug(CATEGORY + ": findAlarmActivationsByDefinitionId" + ":entry", ALARM_ACTIVATION, argObj);
		}

		AlarmActivation[] activations = EMPTY_ALARM_ACTIVATION_ARRAY;

		synchronized (CONDITION_LOCK)
		{
			synchronized (DEFINITION_LOCK)
			{
				synchronized (ACTIVATION_LOCK)
				{
					Collection<AlarmActivation> collection = activationsByDefinitionKey.get(definitionId);

					if (collection != null)
					{
						activations = collection.toArray(activations);
					}
				}
			}
		}

		if (isDebugOn(ALARM_ACTIVATION))
		{
			if (activations != null && activations.length > 0)
			{
				Object[] argObj = new Object[1];
				for (AlarmActivation activation : activations)
				{
					argObj[0] = activation.getStruct();
					debug(CATEGORY + ": findAlarmActivationsByDefinitionId" + ":exit", ALARM_ACTIVATION, argObj);
				}
			}
			else
			{
				debug(CATEGORY + ": findAlarmActivationsByDefinitionId" + ":exit", ALARM_ACTIVATION, "activations were null or zero length");
			}
		}

		return activations;
	}

	public AlarmActivation findAlarmActivationById(Integer activationId)
	{
		if (isDebugOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = activationId;
			debug(CATEGORY + ": findAlarmActivationById" + ":entry", ALARM_ACTIVATION, argObj);
		}

		AlarmActivation activation;
		synchronized (ACTIVATION_LOCK)
		{
			activation = activationsById.get(activationId);
		}

		if (isDebugOn(ALARM_ACTIVATION))
		{
			if (activation != null)
			{
				Object[] argObj = new Object[1];
				argObj[0] = activation.getStruct();
				debug(CATEGORY + ": findAlarmActivationById" + ":exit", ALARM_ACTIVATION, argObj);
			}
			else
			{
				debug(CATEGORY + ": findAlarmActivationById" + ":exit", ALARM_ACTIVATION, "activation was null");
			}
		}

		return activation;
	}

	public AlarmActivation addAlarmActivation(AlarmActivation activation) throws SystemException, CommunicationException, AlreadyExistsException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, NotFoundException
	{
        audit(CATEGORY + ": addAlarmActivation with activation id: " + activation.getId() +
              ", definition id: " + activation.getAlarmDefinition().getId() +
              ", definition name: " + activation.getAlarmDefinition().getName());
        if (isDebugOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = activation.getStruct();
			debug(CATEGORY + ": addAlarmActivation" + ":entry", ALARM_ACTIVATION, argObj);
		}

		AlarmActivation addedActivation;

		if (activation.isSaved())
		{
			AlarmActivation existingActivation = findAlarmActivationById(activation.getId());
			if (existingActivation != null)
			{
				throw ExceptionBuilder.alreadyExistsException("Alarm activation with activation Id:" + activation.getId() + ", already exists.", 0);
			}
		}

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
			synchronized (CONDITION_LOCK)
			{
				synchronized (DEFINITION_LOCK)
				{
					synchronized (ACTIVATION_LOCK)
					{
						AlarmActivationStruct addedStruct = api.createActivation(activation.getStruct());

						if (addedStruct != null)
						{
							addedActivation = addActivationToCache(addedStruct, true);
							ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION,
							        GUI_KEY), addedActivation);
							eventChannel.dispatch(guiEvent);
							guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION, addedActivation
							        .getAlarmDefinition().getId()), addedActivation);
							eventChannel.dispatch(guiEvent);
						}
						else
						{
							throw ExceptionBuilder.systemException("API to create Alarm Activation returned NULL.", 0);
						}
					}
				}
			}
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}

		if (isDebugOn(ALARM_ACTIVATION))
		{
			if (addedActivation != null)
			{
				Object[] argObj = new Object[1];
				argObj[0] = addedActivation.getStruct();
				debug(CATEGORY + ": addAlarmActivation" + ":exit", ALARM_ACTIVATION, argObj);
			}
			else
			{
				debug(CATEGORY + ": addAlarmActivation" + ":exit", ALARM_ACTIVATION, "addedActivation was null");
			}
		}

		return addedActivation;
	}

	public AlarmActivation updateAlarmActivation(AlarmActivation activation) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
	{
        audit(CATEGORY + ": updateAlarmActivation with activation id: " + activation.getId() +
              ", definition id: " + activation.getAlarmDefinition().getId() +
              ", definition name: " + activation.getAlarmDefinition().getName());
        if (isDebugOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = activation.getStruct();
			debug(CATEGORY + ": updateAlarmActivation" + ":entry", ALARM_ACTIVATION, argObj);
		}

		AlarmActivation updatedActivation;

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
			synchronized (CONDITION_LOCK)
			{
				synchronized (DEFINITION_LOCK)
				{
					synchronized (ACTIVATION_LOCK)
					{
						AlarmActivation matchingActivation = findAlarmActivationById(activation.getId());
						if (matchingActivation != null)
						{
							AlarmActivationStruct updatedStruct = api.updateActivation(activation.getStruct());

							if (updatedStruct != null)
							{
								updatedActivation = updateActivationInCache(updatedStruct, matchingActivation);
								ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION,
								        GUI_KEY), updatedActivation);
								eventChannel.dispatch(guiEvent);
								guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION,
								        updatedActivation.getAlarmDefinition().getId()), updatedActivation);
								eventChannel.dispatch(guiEvent);
							}
							else
							{
								throw ExceptionBuilder.systemException("API to update Alarm Activation returned NULL.", 0);
							}
						}
						else
						{
							throw ExceptionBuilder.notFoundException("Alarm activation with activation Id:" + activation.getId()
							        + ", could not be found.", NotFoundCodes.RESOURCE_DOESNT_EXIST);
						}
					}
				}
			}
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}

		if (isDebugOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = updatedActivation.getStruct();
			debug(CATEGORY + ": updateAlarmActivation" + ":exit", ALARM_ACTIVATION, argObj);
		}

		return updatedActivation;
	}

	public AlarmActivation removeAlarmActivation(AlarmActivation activation) throws SystemException, CommunicationException, NotFoundException,
	        DataValidationException, NotAcceptedException, TransactionFailedException
	{
        audit(CATEGORY + ": removeAlarmActivation with activation id: " + activation.getId() +
              ", definition id: " + activation.getAlarmDefinition().getId() +
              ", definition name: " + activation.getAlarmDefinition().getName());
        if (isDebugOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = activation.getStruct();
			debug(CATEGORY + ": removeAlarmActivation" + ":entry", ALARM_ACTIVATION, argObj);
		}

		AlarmActivation removedActivation = findAlarmActivationById(activation.getId());

		if (removedActivation != null)
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			try
			{
				synchronized (CONDITION_LOCK)
				{
					synchronized (DEFINITION_LOCK)
					{
						synchronized (ACTIVATION_LOCK)
						{
							api.deleteActivation(removedActivation.getStruct());
							deleteActivationFromCache(removedActivation);
							ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION,
							        GUI_KEY), removedActivation);
							eventChannel.dispatch(guiEvent);
							guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION, removedActivation
							        .getAlarmDefinition().getId()), removedActivation);
							eventChannel.dispatch(guiEvent);
						}
					}
				}
			}
			catch (TimedOutException e)
			{
				SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
				systemException.initCause(e);
				throw systemException;
			}
		}
		else
		{
			throw ExceptionBuilder.notFoundException("Alarm activation with activation Id:" + activation.getId() + ", could not be found.",
			        NotFoundCodes.RESOURCE_DOESNT_EXIST);
		}

		if (isDebugOn(ALARM_ACTIVATION))
		{
			if (removedActivation != null)
			{
				Object[] argObj = new Object[1];
				argObj[0] = removedActivation.getStruct();
				debug(CATEGORY + ": removeAlarmActivation" + ":exit", ALARM_ACTIVATION, argObj);
			}
			else
			{
				debug(CATEGORY + ": removeAlarmActivation" + ":exit", ALARM_ACTIVATION, "removedActivation was null");
			}
		}

		return removedActivation;
	}

	public AlarmNotificationWatchdog[] getAllAlarmWatchdogs()
	{
		if (isDebugOn(ALARM_WATCHDOG))
		{
			debug(CATEGORY + ": getAllAlarmWatchdogs" + ":entry", ALARM_WATCHDOG);
		}

		AlarmNotificationWatchdog[] watchdogs;
		synchronized (WATCHDOG_LOCK)
		{
			watchdogs = watchdogsById.values().toArray(EMPTY_WATCHDOG_ARRAY);
		}

		if (isDebugOn(ALARM_WATCHDOG))
		{
			if (watchdogs != null && watchdogs.length > 0)
			{
				Object[] argObj = new Object[1];
				for (AlarmNotificationWatchdog watchdog : watchdogs)
				{
					argObj[0] = watchdog.getStruct();
					debug(CATEGORY + ": getAllAlarmWatchdogs" + ":exit", ALARM_WATCHDOG, argObj);
				}
			}
			else
			{
				debug(CATEGORY + ": getAllAlarmWatchdogs" + ":exit", ALARM_WATCHDOG, "watchdogs were null or zero length");
			}
		}

		return watchdogs;
	}

	public AlarmNotificationWatchdog findAlarmWatchdogById(Integer watchdogId)
	{
		if (isDebugOn(ALARM_WATCHDOG))
		{
			Object[] argObj = {watchdogId};
			debug(CATEGORY + ": findAlarmWatchdogById" + ":entry", ALARM_WATCHDOG, argObj);
		}

		AlarmNotificationWatchdog watchdog;
		synchronized (WATCHDOG_LOCK)
		{
			watchdog = watchdogsById.get(watchdogId);
		}

		if (isDebugOn(ALARM_WATCHDOG))
		{
			if (watchdog != null)
			{
				Object[] argObj = {watchdog.getStruct()};
				debug(CATEGORY + ": findAlarmWatchdogById" + ":exit", ALARM_WATCHDOG, argObj);
			}
			else
			{
				debug(CATEGORY + ": findAlarmWatchdogById" + ":exit", ALARM_WATCHDOG, "watchdog was null");
			}
		}

		return watchdog;
	}

	public AlarmNotificationWatchdog getWatchdogForActivationId(Integer activationId)
	{
		if (isDebugOn(ALARM_WATCHDOG))
		{
			Object[] argObj = {activationId};
			debug(CATEGORY + ": getWatchdogForActivationId" + ":entry", ALARM_WATCHDOG, argObj);
		}

		AlarmNotificationWatchdog watchdog;
		synchronized (WATCHDOG_LOCK)
		{
			watchdog = watchdogsByActivationId.get(activationId);
		}

		if (isDebugOn(ALARM_WATCHDOG))
		{
			if (watchdog != null)
			{
				Object[] argObj = {watchdog.getStruct()};
				debug(CATEGORY + ": getWatchdogForActivationId" + ":exit", ALARM_WATCHDOG, argObj);
			}
			else
			{
				debug(CATEGORY + ": getWatchdogForActivationId" + ":exit", ALARM_WATCHDOG, "watchdog was null");
			}
		}

		return watchdog;
	}

	public AlarmNotificationWatchdog addWatchdog(AlarmNotificationWatchdog watchdog) throws SystemException, CommunicationException,
	        AlreadyExistsException, DataValidationException, NotAcceptedException, TransactionFailedException
	{
		if (isDebugOn(ALARM_WATCHDOG))
		{
			Object[] argObj = {watchdog.getStruct()};
			debug(CATEGORY + ": addWatchdog" + ":entry", ALARM_WATCHDOG, argObj);
		}

		AlarmNotificationWatchdog addedWatchdog;

		AlarmNotificationWatchdog existingWatchdog = findAlarmWatchdogById(watchdog.getId());
		if (existingWatchdog != null)
		{
			throw ExceptionBuilder
			        .alreadyExistsException("Alarm Notification Watchdog with watchdog Id:" + watchdog.getId() + ", already exists.", 0);
		}

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
			synchronized (WATCHDOG_LOCK)
			{
				AlarmNotificationWatchdogStruct addedStruct = api.createWatchdog(watchdog.getStruct());
				if (addedStruct != null)
				{
					addedWatchdog = addWatchdogToCache(addedStruct);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_WATCHDOG, GUI_KEY),
					        addedWatchdog);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_WATCHDOG, addedWatchdog.getId()),
					        addedWatchdog);
					eventChannel.dispatch(guiEvent);
				}
				else
				{
					throw ExceptionBuilder.systemException("API to create Alarm Notification Watchdog returned NULL.", 0);
				}
			}
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}

		if (isDebugOn(ALARM_WATCHDOG))
		{
			if (addedWatchdog != null)
			{
				Object[] argObj = {addedWatchdog};
				debug(CATEGORY + ": addWatchdog" + ":exit", ALARM_WATCHDOG, argObj);
			}
			else
			{
				debug(CATEGORY + ": addWatchdog" + ":exit", ALARM_WATCHDOG, "addedWatchdog was null");
			}
		}

		return addedWatchdog;
	}

	public AlarmNotificationWatchdog updateWatchdog(AlarmNotificationWatchdog watchdog) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException, AlreadyExistsException
	{
		if (isDebugOn(ALARM_WATCHDOG))
		{
			Object[] argObj = {watchdog.getStruct()};
			debug(CATEGORY + ": updateWatchdog" + ":entry", ALARM_WATCHDOG, argObj);
		}

		AlarmNotificationWatchdog updatedWatchdog;

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
			synchronized (WATCHDOG_LOCK)
			{
				AlarmNotificationWatchdog matchingWatchdog = findAlarmWatchdogById(watchdog.getId());
				if (matchingWatchdog != null)
				{
					AlarmNotificationWatchdogStruct updatedStruct = api.updateWatchdog(watchdog.getStruct());
					if (updatedStruct != null)
					{
						updatedWatchdog = updateWatchdogInCache(updatedStruct, matchingWatchdog);
						ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG,
						        GUI_KEY), updatedWatchdog);
						eventChannel.dispatch(guiEvent);
						guiEvent = eventChannel.getChannelEvent(this,
						        new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG, updatedWatchdog.getId()), updatedWatchdog);
						eventChannel.dispatch(guiEvent);
					}
					else
					{
						throw ExceptionBuilder.systemException("API to update Alarm Notification Watchdog returned NULL.", 0);
					}
				}
				else
				{
					throw ExceptionBuilder.notFoundException("Alarm Notification Watchdog with watchdog Id:" + watchdog.getId()
					        + ", could not be found.", NotFoundCodes.RESOURCE_DOESNT_EXIST);
				}
			}
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}

		if (isDebugOn(ALARM_WATCHDOG))
		{
			if (updatedWatchdog != null)
			{
				Object[] argObj = {updatedWatchdog.getStruct()};
				debug(CATEGORY + ": updateWatchdog" + ":exit", ALARM_WATCHDOG, argObj);
			}
			else
			{
				debug(CATEGORY + ": updateWatchdog" + ":exit", ALARM_WATCHDOG, "updatedWatchdog was null");
			}
		}

		return updatedWatchdog;
	}

	public AlarmNotificationWatchdog removeWatchdog(AlarmNotificationWatchdog watchdog) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException
	{
		if (isDebugOn(ALARM_WATCHDOG))
		{
			Object[] argObj = {watchdog.getStruct()};
			debug(CATEGORY + ": removeWatchdog" + ":entry", ALARM_WATCHDOG, argObj);
		}

		AlarmNotificationWatchdog removedWatchdog = findAlarmWatchdogById(watchdog.getId());

		if (removedWatchdog != null)
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			try
			{
				synchronized (WATCHDOG_LOCK)
				{
					api.deleteWatchdog(removedWatchdog.getStruct());
					deleteWatchdogFromCache(removedWatchdog);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, GUI_KEY),
					        removedWatchdog);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, removedWatchdog.getId()),
					        removedWatchdog);
					eventChannel.dispatch(guiEvent);
				}
			}
			catch (TimedOutException e)
			{
				SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
				systemException.initCause(e);
				throw systemException;
			}
		}
		else
		{
			throw ExceptionBuilder.notFoundException("Alarm Notification Watchdog with watchdog Id:" + watchdog.getId() + ", could not be found.",
			        NotFoundCodes.RESOURCE_DOESNT_EXIST);
		}

		if (isDebugOn(ALARM_WATCHDOG))
		{
			if (removedWatchdog != null)
			{
				Object[] argObj = {removedWatchdog.getStruct()};
				debug(CATEGORY + ": removeWatchdog" + ":exit", ALARM_WATCHDOG, argObj);
			}
			else
			{
				debug(CATEGORY + ": removeWatchdog" + ":exit", ALARM_WATCHDOG, "removedWatchdog was null");
			}
		}

		return removedWatchdog;
	}

	protected void loadAllConditions()
	{
		try
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			AlarmConditionStruct[] structs = api.publishAllConditions();

			if (isDebugOn(ALARM_CONDITION))
			{
				debug(CATEGORY + ": loadAllConditions", ALARM_CONDITION, structs);
			}

            synchronized (CONDITION_LOCK)
            {
                for (AlarmConditionStruct struct : structs)
                {
                    addConditionToCache(struct);
                }
            }    
        }
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Alarms Cache with Alarm Conditions.");
		}
		catch (TimedOutException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Alarms Cache with Alarm Conditions.");
		}
	}

	protected void loadAllCalculations()
	{
		try
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			AlarmCalculationStruct[] structs = api.publishAllCalculations();

			if (isDebugOn(ALARM_CALCULATION))
			{
				debug(CATEGORY + ": loadAllCalculations", ALARM_CALCULATION, structs);
			}

            synchronized(CALCULATION_LOCK)
            {
                for (AlarmCalculationStruct struct : structs)
                {
                    addCalculationToCache(struct);
                }
            }
        }
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Alarms Cache with Alarm Calculations.");
		}
		catch (TimedOutException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Alarms Cache with Alarm Calculations.");
		}
	}

	protected void loadAllDefinitions()
	{
		try
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			AlarmDefinitionStruct[] structs = api.publishAllDefinitions();

			if (isDebugOn(ALARM_DEFINITION))
			{
				debug(CATEGORY + ": loadAllDefinitions", ALARM_DEFINITION, structs);
			}

            synchronized(DEFINITION_LOCK)
            {
                for (AlarmDefinitionStruct struct : structs)
                {
                    addDefinitionToCache(struct);
                }
            }
        }
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Alarms Cache with Alarm Definitions.");
		}
		catch (TimedOutException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Alarms Cache with Alarm Definitions.");
		}
	}

	protected void loadAllActivations()
	{
		try
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			AlarmActivationStruct[] structs = api.publishAllActivations();

			if (isDebugOn(ALARM_ACTIVATION))
			{
				debug(CATEGORY + ": loadAllActivations", ALARM_ACTIVATION, structs);
			}

            synchronized(ACTIVATION_LOCK)
            {
                for (AlarmActivationStruct struct : structs)
                {
                    addActivationToCache(struct, false);
                }
            }
        }
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Alarms Cache with Alarm Activations.");
		}
		catch (TimedOutException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Alarms Cache with Alarm Activations.");
		}

		updateDefinitionsWithLoadedActivations();
	}

	protected void loadAllWatchdogs()
	{
		try
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			AlarmNotificationWatchdogStruct[] structs = api.publishAllWatchdogs();

			if (isDebugOn(ALARM_WATCHDOG))
			{
				debug(CATEGORY + ": loadAllWatchdogs", ALARM_WATCHDOG, structs);
			}

            synchronized(WATCHDOG_LOCK)
            {
                for (AlarmNotificationWatchdogStruct struct : structs)
                {
                    addWatchdogToCache(struct);
                }
            }
        }
		catch (UserException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Alarms Cache with Alarm Notification Watchdogs.");
		}
		catch (TimedOutException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Alarms Cache with Alarm Notification Watchdogs.");
		}
	}

	private void updateDefinitionsWithLoadedActivations()
	{
		for (Integer definitionKey : activationsByDefinitionKey.keySet())
		{
			AlarmDefinition definition = findAlarmDefinitionById(definitionKey);
			AlarmDefinitionFactory.initializeActivations(definition);
		}
	}

	protected void subscribeChannels()
	{
		ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_CONDITION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CONDITION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);

		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_CALCULATION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CALCULATION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CALCULATION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);

		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_DEFINITION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_DEFINITION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);

		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);

		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_WATCHDOG, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, CONSUMER_KEY);
		eventChannel.addChannelListener(eventChannel, this, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION_ASSIGNMENT, CONSUMER_KEY);
        eventChannel.addChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT, CONSUMER_KEY);
        eventChannel.addChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT, CONSUMER_KEY);
        eventChannel.addChannelListener(eventChannel, this, channelKey);
    }

	protected void unsubscribeChannels()
	{
		ChannelKey channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_CONDITION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CONDITION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);

		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_CALCULATION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CALCULATION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CALCULATION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);

		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_DEFINITION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_DEFINITION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);

		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);

		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_WATCHDOG, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);
		channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, CONSUMER_KEY);
		eventChannel.removeChannelListener(eventChannel, this, channelKey);

        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION_ASSIGNMENT, CONSUMER_KEY);
        eventChannel.removeChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT, CONSUMER_KEY);
        eventChannel.removeChannelListener(eventChannel, this, channelKey);
        channelKey = new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT, CONSUMER_KEY);
        eventChannel.removeChannelListener(eventChannel, this, channelKey);
    }

	private void processConditionEvent(AlarmConditionStruct struct, int eventType)
	{
        synchronized (CONDITION_LOCK)
		{
			if (eventType == ChannelType.IC_ACCEPT_NEW_CONDITION || eventType == ChannelType.IC_ACCEPT_CHANGED_CONDITION)
			{
				AlarmCondition matchingCondition = findAlarmConditionById(struct.conditionId);
				AlarmCondition eventData;
				if (matchingCondition == null)
				{
					eventData = addConditionToCache(struct);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_CONDITION, GUI_KEY),
					        eventData);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_CONDITION, eventData.getId()), eventData);
					eventChannel.dispatch(guiEvent);
				}
				else
				{
					eventData = updateConditionInCache(struct, matchingCondition);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, GUI_KEY),
					        eventData);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, eventData.getId()),
					        eventData);
					eventChannel.dispatch(guiEvent);
				}
			}
			else if (eventType == ChannelType.IC_ACCEPT_DELETED_CONDITION)
			{
				AlarmCondition matchingCondition = findAlarmConditionById(struct.conditionId);
				if (matchingCondition != null)
				{
					AlarmCondition deletedCondition = deleteConditionFromCache(matchingCondition);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CONDITION, GUI_KEY),
					        deletedCondition);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CONDITION, deletedCondition.getId()),
					        deletedCondition);
					eventChannel.dispatch(guiEvent);
				}
			}
		}
	}

	private AlarmCondition addConditionToCache(AlarmConditionStruct struct)
	{
		if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = struct;

			debug(CATEGORY + ": addConditionToCache", ALARM_CONDITION, argObj);
		}

		AlarmCondition condition = AlarmConditionFactory.createAlarmCondition(struct);
		conditionsById.put(condition.getId(), condition);

		List<AlarmCondition> conditionsList = conditionsByName.get(condition.getName());
		if (conditionsList == null)
		{
			conditionsList = new ArrayList<AlarmCondition>(1);
		}
		if (!conditionsList.contains(condition))
		{
			conditionsList.add(condition);
		}
		conditionsByName.put(condition.getName(), conditionsList);

		if (condition.isUsingCalculation())
		{
			addToConditionsByCalculationCache(condition);
		}

		return condition;
	}

	private void addToConditionsByCalculationCache(AlarmCondition condition)
	{
		synchronized (CONDITION_LOCK)
		{
			String calculationName = getCalculationName(condition);
			List<AlarmCondition> conditionsList = conditionsByCalculation.get(calculationName);
			if (conditionsList == null)
			{
				conditionsList = new ArrayList<AlarmCondition>(1);
				conditionsByCalculation.put(calculationName, conditionsList);
			}
			if (!conditionsList.contains(condition))
			{
				conditionsList.add(condition);
			}
		}
	}

	private void deleteFromConditionsByCalculationCache(AlarmCondition condition)
	{
		synchronized (CONDITION_LOCK)
		{
			if (condition.isUsingCalculation())
			{
				String calculationName = getCalculationName(condition);
				List<AlarmCondition> conditionList = conditionsByCalculation.get(calculationName);
				if (conditionList != null)
				{
					conditionList.remove(condition);
					if (conditionList.isEmpty())
					{
						conditionsByCalculation.remove(calculationName);
					}
				}
			}
		}
	}

	private void removeConditionsForCalculation(AlarmCalculation removedCalculation) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException, NotAcceptedException, TransactionFailedException
	{
		synchronized (CONDITION_LOCK)
		{
			List<AlarmCondition> conditionList = conditionsByCalculation.remove(removedCalculation.getName());
			if (conditionList != null)
			{
				for (AlarmCondition condition : conditionList)
				{
					removeAlarmCondition(condition);
				}
			}
		}
	}

	public AlarmCondition[] findAlarmConditionsForCalculation(AlarmCalculation calculation)
	{
		AlarmCondition[] conditions = EMPTY_ALARM_CONDITION_ARRAY;
		synchronized (CONDITION_LOCK)
		{
			List<AlarmCondition> list = conditionsByCalculation.get(calculation.getName());
			if (list != null)
			{
				conditions = list.toArray(conditions);
			}
		}
		if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[conditions.length];
			for (int i = 0; i < conditions.length; i++)
			{
				AlarmCondition condition = conditions[i];
				argObj[i] = condition.getStruct();
			}
			debug(CATEGORY + ": findAlarmConditionsForCalculation:", ALARM_CONDITION, argObj);
		}
		return conditions;
	}

	private String getCalculationName(AlarmCondition condition)
	{
		return condition.getFieldName().substring(AlarmConstants.AC_PREFIX_LENGTH);
	}

	private AlarmCondition updateConditionInCache(AlarmConditionStruct struct, AlarmCondition matchingCondition)
	{
		if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = struct;
			argObj[1] = matchingCondition.getStruct();

			debug(CATEGORY + ": updateConditionInCache", ALARM_CONDITION, argObj);
		}

		conditionsById.remove(matchingCondition.getId());

		List<AlarmCondition> conditionsList = conditionsByName.get(matchingCondition.getName());
		if (conditionsList != null)
		{
			conditionsList.remove(matchingCondition);
			if (conditionsList.isEmpty())
			{
				conditionsByName.remove(matchingCondition.getName());
			}
			else
			{
				conditionsByName.put(matchingCondition.getName(), conditionsList);
			}
		}

		deleteFromConditionsByCalculationCache(matchingCondition);

		AlarmCondition newCondition = addConditionToCache(struct);

		synchronized (DEFINITION_LOCK)
		{
			List<AlarmDefinition> definitionsList = definitionsByCondition.remove(matchingCondition);
			if (definitionsList != null)
			{
				for (AlarmDefinition aDefinitionsList : definitionsList)
				{
					AlarmDefinitionMutable alarmDefinition = (AlarmDefinitionMutable) aDefinitionsList;
					alarmDefinition.removeCondition(matchingCondition);
					alarmDefinition.addCondition(newCondition);

					ChannelEvent guiEvent = eventChannel.getChannelEvent(this,
					        new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, GUI_KEY), alarmDefinition);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, alarmDefinition.getId()),
					        alarmDefinition);
					eventChannel.dispatch(guiEvent);

					synchronized (ACTIVATION_LOCK)
					{
						List<AlarmActivation> activationsForDefinition = activationsByDefinitionKey.get(alarmDefinition.getId());
						if (activationsForDefinition != null && !activationsForDefinition.isEmpty())
						{
							for (AlarmActivation alarmActivation : activationsForDefinition)
							{
                                AlarmActivationImpl activation = (AlarmActivationImpl) alarmActivation;
                                activation.setAlarmDefinition(alarmDefinition);
                                guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, GUI_KEY),
								        activation);
								eventChannel.dispatch(guiEvent);

								guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION,
								        activation.getId()), activation);
								eventChannel.dispatch(guiEvent);
                                synchronized(ASSIGNMENT_LOCK)
                                {
                                    ActivationAssignmentImpl assignment = (ActivationAssignmentImpl) assignmentsByActivationId
                                            .get(activation.getId());
                                    assignment.setActivation(activation);
                                    guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(
                                            ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT,
                                            GUI_KEY), assignment);
                                    eventChannel.dispatch(guiEvent);

                                    guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(
                                            ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT,
                                            assignment.getId()), assignment);
                                    eventChannel.dispatch(guiEvent);
                                }
                            }
						}
					}
				}
				definitionsByCondition.put(newCondition, definitionsList);
			}
		}

		return newCondition;
	}

	private AlarmCondition deleteConditionFromCache(AlarmCondition condition)
	{
		if (isDebugOn(ALARM_CONDITION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = condition.getStruct();

			debug(CATEGORY + ": deleteConditionFromCache", ALARM_CONDITION, argObj);
		}

		AlarmCondition removedCondition = conditionsById.remove(condition.getId());

		List<AlarmCondition> conditionsList = conditionsByName.get(removedCondition.getName());
		if (conditionsList != null)
		{
			conditionsList.remove(removedCondition);
			if (conditionsList.isEmpty())
			{
				conditionsByName.remove(removedCondition.getName());
			}
			else
			{
				conditionsByName.put(removedCondition.getName(), conditionsList);
			}
		}

		deleteFromConditionsByCalculationCache(condition);

		synchronized (DEFINITION_LOCK)
		{
			List<AlarmDefinition> definitionsList = definitionsByCondition.remove(removedCondition);
			if (definitionsList != null)
			{
				for (AlarmDefinition aDefinitionsList : definitionsList)
				{
					AlarmDefinitionMutable alarmDefinition = (AlarmDefinitionMutable) aDefinitionsList;
					alarmDefinition.removeCondition(removedCondition);

					ChannelEvent guiEvent = eventChannel.getChannelEvent(this,
					        new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, GUI_KEY), alarmDefinition);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, alarmDefinition.getId()),
					        alarmDefinition);
					eventChannel.dispatch(guiEvent);
				}
			}
		}

		return removedCondition;
	}

	private void processCalculationEvent(AlarmCalculationStruct struct, int eventType)
	{
		synchronized (CALCULATION_LOCK)
		{
			if (eventType == ChannelType.IC_ACCEPT_NEW_CALCULATION || eventType == ChannelType.IC_ACCEPT_CHANGED_CALCULATION)
			{
				AlarmCalculation matchingCalculation = findAlarmCalculationById(struct.calculationId);
				AlarmCalculation eventData;
				if (matchingCalculation == null)
				{
					eventData = addCalculationToCache(struct);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_CALCULATION, GUI_KEY),
					        eventData);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel
					        .getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_CALCULATION, eventData.getId()), eventData);
					eventChannel.dispatch(guiEvent);
				}
				else
				{
					eventData = updateCalculationInCache(struct, matchingCalculation);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this,
					        new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CALCULATION, GUI_KEY), eventData);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CALCULATION, eventData.getId()),
					        eventData);
					eventChannel.dispatch(guiEvent);
				}
			}
			else if (eventType == ChannelType.IC_ACCEPT_DELETED_CALCULATION)
			{
				AlarmCalculation matchingCalculation = findAlarmCalculationById(struct.calculationId);
				if (matchingCalculation != null)
				{
					AlarmCalculation deletedCalculation = deleteCalculationFromCache(matchingCalculation);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this,
					        new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CALCULATION, GUI_KEY), deletedCalculation);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETED_CALCULATION, deletedCalculation
					        .getId()), deletedCalculation);
					eventChannel.dispatch(guiEvent);
				}
			}
		}
	}

	private AlarmCalculation addCalculationToCache(AlarmCalculationStruct struct)
	{
		if (isDebugOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = struct;
			debug(CATEGORY + ": addCalculationToCache", ALARM_CALCULATION, argObj);
		}

		AlarmCalculation calculation = AlarmCalculationFactory.createAlarmCalculation(struct);
		calculationsById.put(calculation.getId(), calculation);
		calculationsByName.put(calculation.getName(), calculation);

		return calculation;
	}

	private AlarmCalculation updateCalculationInCache(AlarmCalculationStruct struct, AlarmCalculation matchingCalculation)
	{
		if (isDebugOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = struct;
			argObj[1] = matchingCalculation.getStruct();
			debug(CATEGORY + ": updateCalculationInCache", ALARM_CALCULATION, argObj);
		}

		calculationsById.remove(matchingCalculation.getId());
		calculationsByName.remove(matchingCalculation.getName());

		AlarmCalculation newCalculation = addCalculationToCache(struct);
		synchronized (CONDITION_LOCK)
		{
			List<AlarmCondition> conditionList = conditionsByCalculation.remove(matchingCalculation.getName());
			if (conditionList != null)
			{
				for (AlarmCondition condition : conditionList)
				{
					((AlarmConditionMutable) condition).setFieldName(AlarmConstants.ALARM_CALCULATION_PREFIX + newCalculation.getName());

					fireConditionChangedEvent(condition);
				}
				conditionsByCalculation.put(newCalculation.getName(), conditionList);
			}
		}

		return newCalculation;
	}

	private AlarmCalculation deleteCalculationFromCache(AlarmCalculation calculation)
	{
		if (isDebugOn(ALARM_CALCULATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = calculation.getStruct();
			debug(CATEGORY + ": deleteCalculationFromCache", ALARM_CALCULATION, argObj);
		}

		AlarmCalculation removedCalculation = calculationsById.remove(calculation.getId());
		calculationsByName.remove(calculation.getName());

		return removedCalculation;
	}

	private void fireConditionChangedEvent(AlarmCondition condition)
	{
		ChannelEvent event = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, GUI_KEY), condition);
		eventChannel.dispatch(event);

		event = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_CONDITION, condition.getId()), condition);
		eventChannel.dispatch(event);

		fireDefinitionChangedEvent(condition);
	}

	private void fireDefinitionChangedEvent(AlarmCondition condition)
	{
		synchronized (DEFINITION_LOCK)
		{
			List<AlarmDefinition> definitionsList = definitionsByCondition.get(condition);
			if (definitionsList != null)
			{
				for (AlarmDefinition definition : definitionsList)
				{
					ChannelEvent event = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, GUI_KEY),
					        definition);
					eventChannel.dispatch(event);

					event = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, definition.getId()),
					        definition);
					eventChannel.dispatch(event);

					fireActivationChangedEvent(definition);
				}
			}
		}
	}

	private void fireActivationChangedEvent(AlarmDefinition alarmDefinition)
	{
		synchronized (ACTIVATION_LOCK)
		{
			List<AlarmActivation> activationsForDefinition = activationsByDefinitionKey.get(alarmDefinition.getId());
			if (activationsForDefinition != null)
			{
				for (AlarmActivation activation : activationsForDefinition)
				{
					ChannelEvent event = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, GUI_KEY),
					        activation);
					eventChannel.dispatch(event);

					event = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, activation.getId()),
					        activation);
					eventChannel.dispatch(event);
				}
			}
		}
	}

	private void processDefinitionEvent(AlarmDefinitionStruct struct, int eventType)
	{
		synchronized (CONDITION_LOCK)
		{
			synchronized (DEFINITION_LOCK)
			{
				if (eventType == ChannelType.IC_ACCEPT_NEW_DEFINITION || eventType == ChannelType.IC_ACCEPT_CHANGED_DEFINITION)
				{
					AlarmDefinition matchingDefinition = findAlarmDefinitionById(struct.definitionId);
					AlarmDefinition eventData;
					if (matchingDefinition == null)
					{
						eventData = addDefinitionToCache(struct);
						ChannelEvent guiEvent = eventChannel.getChannelEvent(this,
						        new ChannelKey(ChannelType.IC_ACCEPT_NEW_DEFINITION, GUI_KEY), eventData);
						eventChannel.dispatch(guiEvent);
						guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_DEFINITION, eventData.getId()),
						        eventData);
						eventChannel.dispatch(guiEvent);
					}
					else
					{
						eventData = updateDefinitionInCache(struct, matchingDefinition);
						ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION,
						        GUI_KEY), eventData);
						eventChannel.dispatch(guiEvent);
						guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, eventData.getId()),
						        eventData);
						eventChannel.dispatch(guiEvent);
					}
				}
				else if (eventType == ChannelType.IC_ACCEPT_DELETE_DEFINITION)
				{
					AlarmDefinition matchingDefinition = findAlarmDefinitionById(struct.definitionId);
					if (matchingDefinition != null)
					{
						AlarmDefinition deletedDefinition = deleteDefinitionFromCache(matchingDefinition);
						ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_DEFINITION,
						        GUI_KEY), deletedDefinition);
						eventChannel.dispatch(guiEvent);
						guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_DEFINITION, deletedDefinition
						        .getId()), deletedDefinition);
						eventChannel.dispatch(guiEvent);
					}
				}
			}
		}
	}

	private AlarmDefinition addDefinitionToCache(AlarmDefinitionStruct struct)
	{
		if (isDebugOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = struct;

			debug(CATEGORY + ": addDefinitionToCache", ALARM_DEFINITION, argObj);
		}

		AlarmDefinition definition = AlarmDefinitionFactory.createAlarmDefinition(struct);
		return addDefinitionToCache(definition);
	}

	private AlarmDefinition addDefinitionToCache(AlarmDefinition definition)
	{
		if (isDebugOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = definition.getStruct();

			debug(CATEGORY + ": addDefinitionToCache", ALARM_DEFINITION, argObj);
		}

		Integer definitionKey = definition.getId();
		String definitionName = definition.getName();

		definitionsById.put(definitionKey, definition);

		List<AlarmDefinition> definitionsListByName = definitionsByName.get(definitionName);
		if (definitionsListByName == null)
		{
			definitionsListByName = new ArrayList<AlarmDefinition>(1);
		}
		if (!definitionsListByName.contains(definition))
		{
			definitionsListByName.add(definition);
		}
		definitionsByName.put(definitionName, definitionsListByName);

		AlarmCondition[] conditions = definition.getAllConditions();
		for (AlarmCondition condition : conditions)
		{
			List<AlarmDefinition> definitionsListByCondition = definitionsByCondition.get(condition);
			if (definitionsListByCondition == null)
			{
				definitionsListByCondition = new ArrayList<AlarmDefinition>(2);
			}
			if (!definitionsListByCondition.contains(definition))
			{
				definitionsListByCondition.add(definition);
			}
			definitionsByCondition.put(condition, definitionsListByCondition);
		}
		return definition;
	}

	private AlarmDefinition updateDefinitionInCache(AlarmDefinitionStruct struct, AlarmDefinition matchingDefinition)
	{
		if (isDebugOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = struct;
			argObj[1] = matchingDefinition.getStruct();

			debug(CATEGORY + ": updateDefinitionInCache", ALARM_DEFINITION, argObj);
		}

		AlarmDefinition newDefinition = AlarmDefinitionFactory.createAlarmDefinition(struct);
		definitionsById.put(newDefinition.getId(), newDefinition);

		List<AlarmDefinition> definitionsList = definitionsByName.get(matchingDefinition.getName());
		if (definitionsList != null)
		{
			definitionsList.remove(matchingDefinition);
			if (definitionsList.isEmpty())
			{
				definitionsByName.remove(matchingDefinition.getName());
			}
			else
			{
				definitionsByName.put(matchingDefinition.getName(), definitionsList);
			}
		}

		definitionsList = definitionsByName.get(newDefinition.getName());
		if (definitionsList == null)
		{
			definitionsList = new ArrayList<AlarmDefinition>(1);
		}
		if (!definitionsList.contains(newDefinition))
		{
			definitionsList.add(newDefinition);
		}
		definitionsByName.put(newDefinition.getName(), definitionsList);

		List<AlarmActivation> activationList = activationsByDefinitionKey.get(newDefinition.getId());
		if (activationList != null)
		{
			for (AlarmActivation anActivationList : activationList)
			{
				AlarmActivationImpl alarmActivation = (AlarmActivationImpl) anActivationList;
				alarmActivation.setAlarmDefinition(newDefinition);
			}
		}

		AlarmCondition[] conditions = matchingDefinition.getAllConditions();
		for (AlarmCondition condition : conditions)
		{
			List<AlarmDefinition> definitionList = definitionsByCondition.get(condition);
			if (definitionList != null)
			{
				definitionList.remove(matchingDefinition);
				if (definitionsList.isEmpty())
				{
					definitionsByCondition.remove(condition);
				}
				else
				{
					definitionsByCondition.put(condition, definitionList);
				}
			}
		}

		conditions = newDefinition.getAllConditions();
		for (AlarmCondition condition : conditions)
		{
			List<AlarmDefinition> definitionList = definitionsByCondition.get(condition);
			if (definitionList == null)
			{
				definitionList = new ArrayList<AlarmDefinition>(2);
			}
			definitionList.remove(matchingDefinition);
			definitionList.add(newDefinition);
			definitionsByCondition.put(condition, definitionList);
		}

		return newDefinition;
	}

	private AlarmDefinition deleteDefinitionFromCache(AlarmDefinition definition)
	{
		if (isDebugOn(ALARM_DEFINITION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = definition.getStruct();

			debug(CATEGORY + ": deleteDefinitionFromCache", ALARM_DEFINITION, argObj);
		}

		AlarmDefinition removedDefinition = definitionsById.remove(definition.getId());

		List<AlarmDefinition> definitionsList = definitionsByName.get(removedDefinition.getName());
		if (definitionsList != null)
		{
			definitionsList.remove(removedDefinition);
			if (definitionsList.isEmpty())
			{
				definitionsByName.remove(removedDefinition.getName());
			}
			else
			{
				definitionsByName.put(removedDefinition.getName(), definitionsList);
			}
		}

		activationsByDefinitionKey.remove(definition.getId());

		AlarmCondition[] conditions = removedDefinition.getAllConditions();
		for (AlarmCondition condition : conditions)
		{
			definitionsList = definitionsByCondition.get(condition);
			if (definitionsList != null)
			{
				definitionsList.remove(removedDefinition);
				if (definitionsList.isEmpty())
				{
					definitionsByCondition.remove(condition);
				}
				else
				{
					definitionsByCondition.put(condition, definitionsList);
				}
			}
		}

		return removedDefinition;
	}

	private void processActivationEvent(AlarmActivationStruct struct, int eventType)
	{
		synchronized (CONDITION_LOCK)
		{
			synchronized (DEFINITION_LOCK)
			{
				synchronized (ACTIVATION_LOCK)
				{
					if (eventType == ChannelType.IC_ACCEPT_NEW_ACTIVATION || eventType == ChannelType.IC_ACCEPT_CHANGED_ACTIVATION)
					{
						AlarmActivation matchingActivation = findAlarmActivationById(struct.activationId);
						AlarmActivation eventData;
						if (matchingActivation == null)
						{
							eventData = addActivationToCache(struct, true);
							ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION,
							        GUI_KEY), eventData);
							eventChannel.dispatch(guiEvent);
							guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION, eventData
							        .getAlarmDefinition().getId()), eventData);
							eventChannel.dispatch(guiEvent);
						}
						else
						{
							eventData = updateActivationInCache(struct, matchingActivation);
							ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION,
							        GUI_KEY), eventData);
							eventChannel.dispatch(guiEvent);
							guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, eventData
							        .getAlarmDefinition().getId()), eventData);
							eventChannel.dispatch(guiEvent);
						}
					}
					else if (eventType == ChannelType.IC_ACCEPT_DELETE_ACTIVATION)
					{
						AlarmActivation matchingActivation = findAlarmActivationById(struct.activationId);
						if (matchingActivation != null)
						{
							AlarmActivation deletedActivation = deleteActivationFromCache(matchingActivation);
							ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION,
							        GUI_KEY), deletedActivation);
							eventChannel.dispatch(guiEvent);
							guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION, deletedActivation
							        .getAlarmDefinition().getId()), deletedActivation);
							eventChannel.dispatch(guiEvent);
						}
					}
				}
			}
		}
	}

	private AlarmActivation addActivationToCache(AlarmActivationStruct struct, boolean updateDefinition)
	{
		if (isDebugOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = struct;

			debug(CATEGORY + ": addActivationToCache", ALARM_ACTIVATION, argObj);
		}

		AlarmActivation newActivation = null;

		Integer definitionKey = struct.definition.definitionId;
		AlarmDefinition referencedDefinition = findAlarmDefinitionById(definitionKey);

		if (referencedDefinition != null)
		{
			newActivation = AlarmActivationFactory.createAlarmActivation(struct, referencedDefinition);
			activationsById.put(newActivation.getId(), newActivation);

			List<AlarmActivation> activationList = activationsByDefinitionKey.get(definitionKey);
			if (activationList == null)
			{
				activationList = new ArrayList<AlarmActivation>(2);
			}
			if (!activationList.contains(newActivation))
			{
				activationList.add(newActivation);
			}

			if (updateDefinition)
			{
				AlarmDefinitionMutable mutableDefinition = AlarmDefinitionFactory.createMutableAlarmDefinition(referencedDefinition);
				mutableDefinition.addActivation(newActivation);
				deleteDefinitionFromCache(referencedDefinition);
				addDefinitionToCache(mutableDefinition);
			}
			activationsByDefinitionKey.put(definitionKey, activationList);
		}
		else
		{
			GUILoggerHome.find().alarm(
			        CATEGORY + ": addActivation : Definition could not be found. " + "Activation could not be created or added to cache.", struct);
		}

		return newActivation;
	}

	private AlarmActivation updateActivationInCache(AlarmActivationStruct struct, AlarmActivation matchingActivation)
	{
		if (isDebugOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[2];
			argObj[0] = struct;
			argObj[1] = matchingActivation.getStruct();

			debug(CATEGORY + ": updateActivationInCache", ALARM_ACTIVATION, argObj);
		}

		AlarmActivation updatedActivation = null;

		Integer definitionKey = struct.definition.definitionId;
		AlarmDefinitionMutable referencedDefinition = (AlarmDefinitionMutable) findAlarmDefinitionById(definitionKey);

		if (referencedDefinition != null)
		{
			activationsById.remove(matchingActivation.getId());

			updatedActivation = AlarmActivationFactory.createAlarmActivation(struct, referencedDefinition);
			activationsById.put(updatedActivation.getId(), updatedActivation);

			List<AlarmActivation> activationList = activationsByDefinitionKey.get(definitionKey);
			if (activationList == null)
			{
				activationList = new ArrayList<AlarmActivation>(2);
			}

			activationList.remove(matchingActivation);
			activationList.add(updatedActivation);

			AlarmDefinitionMutable mutableDefinition = AlarmDefinitionFactory.createMutableAlarmDefinition(referencedDefinition);
			mutableDefinition.updateActivation(updatedActivation);
			deleteDefinitionFromCache(referencedDefinition);
			addDefinitionToCache(mutableDefinition);

			activationsByDefinitionKey.put(definitionKey, activationList);
		}

		return updatedActivation;
	}

	private AlarmActivation deleteActivationFromCache(AlarmActivation activation)
	{
		if (isDebugOn(ALARM_ACTIVATION))
		{
			Object[] argObj = new Object[1];
			argObj[0] = activation.getStruct();

			debug(CATEGORY + ": deleteActivationFromCache", ALARM_ACTIVATION, argObj);
		}

		AlarmActivation removedActivation = activationsById.remove(activation.getId());

		AlarmDefinitionMutable definition = (AlarmDefinitionMutable) removedActivation.getAlarmDefinition();

		AlarmDefinitionMutable mutableDefinition = AlarmDefinitionFactory.createMutableAlarmDefinition(definition);
		mutableDefinition.removeActivation(removedActivation);
		deleteDefinitionFromCache(definition);
		addDefinitionToCache(mutableDefinition);

		List<AlarmActivation> activationList = activationsByDefinitionKey.get(mutableDefinition.getId());
		if (activationList != null)
		{
			activationList.remove(removedActivation);
			if (activationList.isEmpty())
			{
				activationsByDefinitionKey.remove(mutableDefinition.getId());
			}
			else
			{
				activationsByDefinitionKey.put(mutableDefinition.getId(), activationList);
			}
		}

		return removedActivation;
	}

	private void processWatchdogEvent(AlarmNotificationWatchdogStruct struct, int eventType)
	{
		synchronized (WATCHDOG_LOCK)
		{
			if (eventType == ChannelType.IC_ACCEPT_NEW_WATCHDOG || eventType == ChannelType.IC_ACCEPT_CHANGED_WATCHDOG)
			{
				AlarmNotificationWatchdog matchingWatchdog = findAlarmWatchdogById(struct.notificationWatchdogId);
				AlarmNotificationWatchdog eventData;
				if (matchingWatchdog == null)
				{
					eventData = addWatchdogToCache(struct);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_WATCHDOG, GUI_KEY),
					        eventData);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_WATCHDOG, eventData.getId()), eventData);
					eventChannel.dispatch(guiEvent);
				}
				else
				{
					eventData = updateWatchdogInCache(struct, matchingWatchdog);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG, GUI_KEY),
					        eventData);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_WATCHDOG, eventData.getId()),
					        eventData);
					eventChannel.dispatch(guiEvent);
				}
			}
			else if (eventType == ChannelType.IC_ACCEPT_DELETE_WATCHDOG)
			{
				AlarmNotificationWatchdog matchingWatchdog = findAlarmWatchdogById(struct.notificationWatchdogId);
				if (matchingWatchdog != null)
				{
					AlarmNotificationWatchdog deletedWatchdog = deleteWatchdogFromCache(matchingWatchdog);
					ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, GUI_KEY),
					        deletedWatchdog);
					eventChannel.dispatch(guiEvent);
					guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_WATCHDOG, deletedWatchdog.getId()),
					        deletedWatchdog);
					eventChannel.dispatch(guiEvent);
				}
			}
		}
	}

	private AlarmNotificationWatchdog addWatchdogToCache(AlarmNotificationWatchdogStruct struct)
	{
		if (isDebugOn(ALARM_WATCHDOG))
		{
			Object[] argObj = {struct};

			debug(CATEGORY + ": addWatchdogToCache", ALARM_WATCHDOG, argObj);
		}

		AlarmNotificationWatchdog watchdog = AlarmNotificationWatchdogFactory.createAlarmNotificationWatchdog(struct);
		watchdogsById.put(watchdog.getId(), watchdog);

		watchdogsByActivationId.put(watchdog.getAlarmActivationId(), watchdog);

		return watchdog;
	}

	private AlarmNotificationWatchdog updateWatchdogInCache(AlarmNotificationWatchdogStruct struct, AlarmNotificationWatchdog matchingWatchdog)
	{
		if (isDebugOn(ALARM_WATCHDOG))
		{
			Object[] argObj = {struct, matchingWatchdog.getStruct()};

			debug(CATEGORY + ": updateWatchdogInCache", ALARM_WATCHDOG, argObj);
		}

		watchdogsById.remove(matchingWatchdog.getId());

		watchdogsByActivationId.remove(matchingWatchdog.getAlarmActivationId());

		return addWatchdogToCache(struct);
	}

	private AlarmNotificationWatchdog deleteWatchdogFromCache(AlarmNotificationWatchdog watchdog)
	{
		if (isDebugOn(ALARM_WATCHDOG))
		{
			Object[] argObj = {watchdog.getStruct()};

			debug(CATEGORY + ": deleteWatchdogFromCache", ALARM_WATCHDOG, argObj);
		}

		AlarmNotificationWatchdog removedWatchdog = watchdogsById.remove(watchdog.getId());
		watchdogsByActivationId.remove(watchdog.getAlarmActivationId());

		return removedWatchdog;
	}

	// ********************************************************************************************
	// ********************************** Activation Assignments **********************************
	// ********************************************************************************************

    private void processAssignmentEvent(ActivationAssignmentStruct struct, int eventType)
    {
        synchronized(ACTIVATION_LOCK)
        {
                synchronized(ASSIGNMENT_LOCK)
            {
                if(eventType == ChannelType.IC_ACCEPT_NEW_ACTIVATION_ASSIGNMENT ||
                   eventType == ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT)
                {
                    ActivationAssignment matchingAssignment = findAssignmentById(struct.assignmentId);
                    ActivationAssignment eventData;
                    if(matchingAssignment == null)
                    {
                        eventData = addAssignmentToCache(struct);
                        ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(
                                ChannelType.IC_ACCEPT_NEW_ACTIVATION_ASSIGNMENT, GUI_KEY), eventData);
                        eventChannel.dispatch(guiEvent);
                        guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION_ASSIGNMENT,
                                                                                     eventData.getAlarmActivation().getId()), eventData);
                        eventChannel.dispatch(guiEvent);
                    }
                    else
                    {
                        eventData = updateAssignmentInCache(struct, matchingAssignment);
                        ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(
                                ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT, GUI_KEY), eventData);
                        eventChannel.dispatch(guiEvent);
                        guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT,
                                                                                     eventData.getAlarmActivation().getId()), eventData);
                        eventChannel.dispatch(guiEvent);
                    }
                }
                else if(eventType == ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT)
                {
                    ActivationAssignment matchingAssignment = findAssignmentById(struct.assignmentId);
                    if(matchingAssignment != null)
                    {
                        ActivationAssignment deletedAssignment = deleteAssignmentFromCache(matchingAssignment);
                        ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(
                                ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT, GUI_KEY),
                                                                             deletedAssignment);
                        eventChannel.dispatch(guiEvent);
                        guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT,
                                                                                     deletedAssignment.getAlarmActivation().getId()), deletedAssignment);
                        eventChannel.dispatch(guiEvent);
                    }
                }
            }
        }
    }

    /**
	 * Retrieves an activation assignment for an activation ID.
	 * 
	 * @param activationId
	 * @return {@link ActivationAssignment}
	 */
	public ActivationAssignment findAssignmentByActivationId(Integer activationId)
	{
		ActivationAssignment assignment = null;

		synchronized (ACTIVATION_LOCK)
		{
			synchronized (ASSIGNMENT_LOCK)
			{
				assignment = assignmentsByActivationId.get(activationId);
			}
		}

		if (isDebugOn(ALARM_ASSIGNMENT))
		{
			debug(CATEGORY + ": findAssignmentByActivationId" + ":exit", ALARM_ASSIGNMENT, String.valueOf(assignment));
		}

		return assignment;
	}

	/**
	 * Retrieves an activation assignment for an ID.
	 * 
	 * @param assignmentId
	 * @return {@link ActivationAssignment}
	 */
	public ActivationAssignment findAssignmentById(Integer assignmentId)
	{
		ActivationAssignment assignment;

		synchronized (ASSIGNMENT_LOCK)
		{
			assignment = assignmentsById.get(assignmentId);
		}

		if (isDebugOn(ALARM_ASSIGNMENT))
		{
			debug(CATEGORY + ": findActivationAssignmentById" + ":exit", ALARM_ASSIGNMENT, String.valueOf(assignment));
		}

		return assignment;
	}

	/**
	 * Retrieves all activation assignments.
	 * 
	 * @return {@link ActivationAssignment}[]
	 */
	public ActivationAssignment[] getAllAssignments()
	{
		ActivationAssignment[] assignments = null;

		synchronized (ASSIGNMENT_LOCK)
		{
				Collection<ActivationAssignment> c = assignmentsById.values();
				assignments = c.toArray(new ActivationAssignment[c.size()]);
		}

		if (isDebugOn(ALARM_ASSIGNMENT))
		{
			debug(CATEGORY + ": getAllAssignments" + ":exit", ALARM_ASSIGNMENT, "size:" + String.valueOf(assignments.length));
		}

		return assignments;
	}

	/**
	 * Sends a request to the server to persist a new activation assignment and adds it to the cache
	 * if the transaction was successful.
	 * 
	 * @param assignment
	 * @return {@link ActivationAssignment}
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws AlreadyExistsException
	 * @throws DataValidationException
	 * @throws NotAcceptedException
	 * @throws TransactionFailedException
	 * @throws NotFoundException
	 */
	public ActivationAssignment addActivationAssignment(ActivationAssignment assignment) throws SystemException, CommunicationException,
	        AlreadyExistsException, DataValidationException
	{
        audit(CATEGORY + ": addActivationAssignment with assignment id: " + assignment.getId() +
              ", Logical Name: " + assignment.getLogicalName() + ", activation id: " + assignment.getAlarmActivation().getId()
              + ", defiinition id: "  + assignment.getAlarmActivation().getAlarmDefinition().getId() +
              ", definition name: "  + assignment.getAlarmActivation().getAlarmDefinition().getName());

        if(isDebugOn(ALARM_ASSIGNMENT))
        {
            Object[] argObj = new Object[1];
            argObj[0] = assignment;
            debug(CATEGORY + ": addActivationAssignment" + ":entry", ALARM_ASSIGNMENT, argObj);
        }

        ActivationAssignment addedAssignment = null;

		if (assignment.isSaved())
		{
			ActivationAssignment existingAssignment = findAssignmentById(assignment.getId());
			if (existingAssignment != null)
			{
				throw ExceptionBuilder.alreadyExistsException("Activation assignment with Id:" + assignment.getId() + ", already exists.", 0);
			}
		}

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
            synchronized(ACTIVATION_LOCK)
            {
                synchronized (ASSIGNMENT_LOCK)
                {
                    ActivationAssignmentStruct addedStruct = api.createActivationAssignment(ActivationAssignmentFactory.getStruct(assignment));

                    if (addedStruct != null)
                    {
                        addedAssignment = addAssignmentToCache(addedStruct);
                        ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_NEW_ACTIVATION_ASSIGNMENT,
                                GUI_KEY), addedAssignment);
                        eventChannel.dispatch(guiEvent);
                    }
                    else
                    {
                        throw ExceptionBuilder.systemException("API to create Activation Assignment returned NULL.", 0);
                    }
                }
            }
		}
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}
        catch (UserException e)
        {
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
        }

		if (isDebugOn(ALARM_ASSIGNMENT))
		{
			debug(CATEGORY + ": addActivationAssignment" + ":exit", ALARM_ASSIGNMENT, String.valueOf(addedAssignment));
		}

		return addedAssignment;
	}

	/**
	 * Sends a request to the server to update an existing activation assignment and updates it in
	 * the cache if the transaction was successful.
	 * 
	 * @param assignment
	 * @return {@link ActivationAssignment}
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws NotFoundException
	 * @throws DataValidationException
	 */
	public ActivationAssignment updateActivationAssignment(ActivationAssignment assignment) throws SystemException, CommunicationException,
	        NotFoundException, DataValidationException
	{
        audit(CATEGORY + ": updateActivationAssignment with assignment id: " + assignment.getId() +
              ", Logical Name: " + assignment.getLogicalName() + ", activation id: " +
              assignment.getAlarmActivation().getId() + ", defiinition id: " +
              assignment.getAlarmActivation().getAlarmDefinition().getId() + ", definition name: " +
              assignment.getAlarmActivation().getAlarmDefinition().getName());

        if(isDebugOn(ALARM_ASSIGNMENT))
        {
            Object[] argObj = new Object[1];
            argObj[0] = assignment;
            debug(CATEGORY + ": updateActivationAssignment" + ":entry", ALARM_ASSIGNMENT, argObj);
        }

        ActivationAssignment updatedAssignment;
		ActivationAssignmentStruct updatedStruct;

		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

		try
		{
            synchronized(ACTIVATION_LOCK)
            {
                synchronized (ASSIGNMENT_LOCK)
                {
                    ActivationAssignment matchingAssignment = findAssignmentById(assignment.getId());
                    if (matchingAssignment != null)
                    {
                        updatedStruct = api.updateActivationAssignment(ActivationAssignmentFactory.getStruct(assignment));

                        if (updatedStruct != null)
                        {
                            updatedAssignment = addAssignmentToCache(updatedStruct);
                            ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(
                                    ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT, GUI_KEY), updatedAssignment);
                            eventChannel.dispatch(guiEvent);
                        }
                        else
                        {
                            throw ExceptionBuilder.systemException("API to update Activation Assignment returned NULL.", 0);
                        }
                    }
                    else
                    {
                        throw ExceptionBuilder.notFoundException("Activation Assignment with activation Id:" + assignment.getId()
                                + ", could not be found.", NotFoundCodes.RESOURCE_DOESNT_EXIST);
                    }
                }
            }        
        }
		catch (TimedOutException e)
		{
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
		}
        catch (UserException e)
        {
			SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
			systemException.initCause(e);
			throw systemException;
        }

		if (isDebugOn(ALARM_ASSIGNMENT))
		{
			debug(CATEGORY + ": updateActivationAssignment : ", ALARM_ASSIGNMENT, String.valueOf(updatedAssignment));
		}

		return updatedAssignment;
	}

	/**
	 * Sends a request to the server to delete an existing activation assignment and removes it from
	 * the cache if the transaction was successful.
	 * 
	 * @param assignment
	 * @return {@link ActivationAssignment}
	 * @throws SystemException
	 * @throws CommunicationException
	 * @throws NotFoundException
	 */
	public ActivationAssignment removeActivationAssignment(ActivationAssignment assignment) throws SystemException, CommunicationException, NotFoundException
	{
        audit(CATEGORY + ": removeActivationAssignment with assignment id: " + assignment.getId() +
              ", Logical Name: " + assignment.getLogicalName() + ", activation id: " +
              assignment.getAlarmActivation().getId() + ", defiinition id: " +
              assignment.getAlarmActivation().getAlarmDefinition().getId() + ", definition name: " +
              assignment.getAlarmActivation().getAlarmDefinition().getName());

        if(isDebugOn(ALARM_ASSIGNMENT))
        {
            Object[] argObj = new Object[1];
            argObj[0] = assignment;
            debug(CATEGORY + ": removeActivationAssignment" + ":entry", ALARM_ASSIGNMENT, argObj);
        }
        ActivationAssignment removedAssignment = findAssignmentById(assignment.getId());

		if (removedAssignment != null)
		{
			AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);

			try
			{
                synchronized(ACTIVATION_LOCK)
                {
                    synchronized (ASSIGNMENT_LOCK)
                    {
                        api.deleteActivationAssignment(ActivationAssignmentFactory.getStruct(removedAssignment));
                        deleteAssignmentFromCache(removedAssignment);
                        ChannelEvent guiEvent = eventChannel.getChannelEvent(this, new ChannelKey(ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT,
                                GUI_KEY), removedAssignment);
                        eventChannel.dispatch(guiEvent);
                    }
                }
            }
			catch (TimedOutException e)
			{
				SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
				systemException.initCause(e);
				throw systemException;
			}
            catch (UserException e)
            {
				SystemException systemException = ExceptionBuilder.systemException(e.getMessage(), 0);
				systemException.initCause(e);
				throw systemException;
            }
		}
		else
		{
			throw ExceptionBuilder.notFoundException("Activation Assignment with Id:" + assignment.getId() + ", could not be found.",
			        NotFoundCodes.RESOURCE_DOESNT_EXIST);
		}

		if (isDebugOn(ALARM_ASSIGNMENT))
		{
			debug(CATEGORY + ": removeActivationAssignment" + ":exit", ALARM_ASSIGNMENT, String.valueOf(removedAssignment));
		}

		return removedAssignment;
	}

	/**
	 * Loads all activation assignments into the cache
	 */
	private void loadAllActivationAssignments()
	{
		AlarmsAPI api = new AlarmsSynchEventChannelAPI(publisherHome, apiTimeoutMillis);
		try
		{
			ActivationAssignmentStruct[] structs = api.publishAllActivationAssignments();
            if(isDebugOn(ALARM_ASSIGNMENT))
            {
                debug(CATEGORY + ": loadAllActivationAssignments", ALARM_ASSIGNMENT, structs);
            }
            synchronized(ASSIGNMENT_LOCK)
            {
                for (ActivationAssignmentStruct struct : structs)
                {
                    addAssignmentToCache(struct);
                }
            }    
        }
		catch (CommunicationException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Activation Assignments.");
		}
		catch (SystemException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Activation Assignments.");
		}
		catch (TimedOutException e)
		{
			DefaultExceptionHandlerHome.find().process(e, "Could not load Activation Assignments.");
		}
	}

	/**
	 * Creates a new activation assignment business object and adds it to the cache.
	 * 
	 * @param struct
	 */
	private ActivationAssignment addAssignmentToCache(ActivationAssignmentStruct struct)
	{
		ActivationAssignment activationAssignment = ActivationAssignmentFactory.newActivationAssignment(struct);
		synchronized (ASSIGNMENT_LOCK)
		{
			assignmentsById.put(struct.assignmentId, activationAssignment);
			assignmentsByActivationId.put(struct.activation.activationId, activationAssignment);
		}
		return activationAssignment;
	}

    /**
     * Removes an activation assignment from the cache.
     */
    private ActivationAssignment deleteAssignmentFromCache(ActivationAssignment removedAssignment)
    {
        synchronized(ASSIGNMENT_LOCK)
        {
            ActivationAssignment deletedAssignment = assignmentsById.remove(removedAssignment.getId());
            assignmentsByActivationId.remove(removedAssignment.getAlarmActivation().getId());
            return deletedAssignment;
        }
    }

    private ActivationAssignment updateAssignmentInCache(ActivationAssignmentStruct struct,
                                                         ActivationAssignment matchingAssignment)
    {
        synchronized(ASSIGNMENT_LOCK)
        {
            assignmentsById.remove(matchingAssignment.getId());
            assignmentsByActivationId.remove(matchingAssignment.getAlarmActivation().getId());
            return addAssignmentToCache(struct);
        }
    }

    // ********************************************************************************************
	// ******************************* Logging Convenience methods ********************************
	// ********************************************************************************************

	private static boolean isDebugOn(GUILoggerINBusinessProperty businessProperty)
	{
		return GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(businessProperty);
	}

	private static void debug(String windowTitle, IGUILoggerBusinessProperty businessProperty)
	{
		GUILoggerHome.find().debug(windowTitle, businessProperty);
	}

	private static void debug(String windowTitle, IGUILoggerBusinessProperty businessProperty, String messageText)
	{
		GUILoggerHome.find().debug(windowTitle, businessProperty, messageText);
	}

	private static void debug(String windowTitle, IGUILoggerBusinessProperty businessProperty, Object[] structObjectArray)
	{
		GUILoggerHome.find().debug(windowTitle, businessProperty, structObjectArray);
	}

    private static void audit(String messageText)
    {
        GUILoggerHome.find().audit(messageText);
    }

    private static String getEventTypeName(int eventType)
    {
        String eventName = "Unknown";

        switch(eventType)
        {
            case ChannelType.IC_ACCEPT_NEW_CONDITION:
                eventName = "NewCondition";
                break;
            case ChannelType.IC_ACCEPT_CHANGED_CONDITION:
                eventName = "ChangedCondition";
                break;
            case ChannelType.IC_ACCEPT_DELETED_CONDITION:
                eventName = "DeletedCondition";
                break;
            case ChannelType.IC_ACCEPT_NEW_CALCULATION:
                eventName = "NewCalculation";
                break;
            case ChannelType.IC_ACCEPT_CHANGED_CALCULATION:
                eventName = "ChangedCalculation";
                break;
            case ChannelType.IC_ACCEPT_DELETED_CALCULATION:
                eventName = "DeletedCalculation";
                break;
            case ChannelType.IC_ACCEPT_NEW_DEFINITION:
                eventName = "NewDefinition";
                break;
            case ChannelType.IC_ACCEPT_CHANGED_DEFINITION:
                eventName = "ChangedDefinition";
                break;
            case ChannelType.IC_ACCEPT_DELETE_DEFINITION:
                eventName = "DeleteDefinition";
                break;
            case ChannelType.IC_ACCEPT_NEW_ACTIVATION:
                eventName = "NewActivation";
                break;
            case ChannelType.IC_ACCEPT_CHANGED_ACTIVATION:
                eventName = "ChangedActivation";
                break;
            case ChannelType.IC_ACCEPT_DELETE_ACTIVATION:
                eventName = "DeleteActivation";
                break;
            case ChannelType.IC_ACCEPT_NEW_ACTIVATION_ASSIGNMENT:
                eventName = "NewAssignment";
                break;
            case ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT:
                eventName = "ChangedAssignment";
                break;
            case ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT:
                eventName = "DeletedAssignment";
                break;
            case ChannelType.IC_ACCEPT_NEW_WATCHDOG:
                eventName = "NewWatchdog";
                break;
            case ChannelType.IC_ACCEPT_CHANGED_WATCHDOG:
                eventName = "ChangedWatchdog";
                break;
            case ChannelType.IC_ACCEPT_DELETE_WATCHDOG:
                eventName = "DeletedWatchdog";
                break;
        }
        return eventName;
    }
}
