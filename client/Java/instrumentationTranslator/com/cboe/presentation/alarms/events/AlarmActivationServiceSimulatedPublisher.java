//
// ------------------------------------------------------------------------
// FILE: AlarmActivationServiceSimulatedPublisher.java
// 
// PACKAGE: com.cboe.presentation.alarms.events;
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.alarms.events;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cboe.domain.util.DateWrapper;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.alarm.ActivationAssignmentStruct;
import com.cboe.idl.alarm.AlarmActivationStruct;
import com.cboe.idl.alarm.AlarmDefinitionStruct;
import com.cboe.idl.alarmEvents.AlarmActivationEventService;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.uuidService.IdService;
import com.cboe.interfaces.events.AlarmActivationConsumer;
import com.cboe.interfaces.events.AlarmActivationEventDelegateServiceConsumer;
import com.cboe.interfaces.instrumentation.alarms.AlarmDefinition;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationWatchdog;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.api.InstrumentationTranslatorImpl;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

public class AlarmActivationServiceSimulatedPublisher
        implements AlarmActivationEventDelegateServiceConsumer, EventChannelListener
{
    private IdService idService;

    private Map<Integer, AlarmActivationStruct> activationsById = new HashMap<Integer, AlarmActivationStruct>(101);
    private Map<Integer, Map<Integer, AlarmActivationStruct>> activationsByDefinitionKey =
            new HashMap<Integer, Map<Integer, AlarmActivationStruct>>(101);
    private Map<Integer, ActivationAssignmentStruct> assignmentsById = new HashMap<Integer, ActivationAssignmentStruct>(101);

    private boolean isRegisteredForDefinitionEvents = false;

    public AlarmActivationServiceSimulatedPublisher()
    {
        idService = FoundationFramework.getInstance().getIdService();
    }

    public void setAlarmActivationEventServiceDelegate(AlarmActivationEventService eventChannelDelegate)
    {
        //not used, required for interface
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey key = (ChannelKey) event.getChannel();
        int channelType = key.channelType;

        if( channelType == ChannelType.IC_ACCEPT_CHANGED_DEFINITION)
        {
            AlarmDefinition definition = (AlarmDefinition) event.getEventData();
            Integer definitionKey = definition.getId();
            Map<Integer, AlarmActivationStruct> activationMap = activationsByDefinitionKey.get(definitionKey);
            if(activationMap != null)
            {
                for(Iterator<AlarmActivationStruct> iterator = activationMap.values().iterator(); iterator.hasNext();)
                {
                    AlarmActivationStruct alarmActivationStruct = iterator.next();
                    alarmActivationStruct.definition = definition.getStruct();
                }
            }
            activationsByDefinitionKey.put(definitionKey, activationMap);
        }
    }

    public void publishActivationById(long requestId, int activationId)
    {
        registerForDefinitionEvents();

        AlarmActivationStruct struct = activationsById.get(new Integer(activationId));
        if(struct != null)
        {
            AlarmActivationStruct[] structs = {struct};
            getAlarmActivationConsumer().acceptActivations(requestId, structs);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Activation for Id:" + activationId + ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmActivationConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    public void publishAllActivations(long requestId)
    {
        registerForDefinitionEvents();

        AlarmActivationStruct[] structs = new AlarmActivationStruct[0];
        structs = activationsById.values().toArray(structs);
        getAlarmActivationConsumer().acceptActivations(requestId, structs);
    }

    public void createActivation(long requestId, AlarmActivationStruct activation)
    {
        registerForDefinitionEvents();

        try
        {
            AlarmDefinition foundDefinition =
                    InstrumentationTranslatorFactory.find().getAlarmDefinitionById(activation.definition.definitionId);
            if(foundDefinition == null)
            {
                NotFoundException notFoundException =
                        ExceptionBuilder.notFoundException("Definition for Id:" + activation.definition.definitionId +
                                                           ", does not exist.",
                                                           NotFoundCodes.RESOURCE_DOESNT_EXIST);
                getAlarmActivationConsumer().acceptNotFoundException(requestId, notFoundException.details);
                return;
            }
        }
        catch(NotFoundException e)
        {
            getAlarmActivationConsumer().acceptNotFoundException(requestId, e.details);
            return;
        }
        catch(SystemException e)
        {
            getAlarmActivationConsumer().acceptSystemException(requestId, e.details);
            return;
        }
        catch(CommunicationException e)
        {
            getAlarmActivationConsumer().acceptSystemException(requestId, e.details);
            return;
        }

        AlarmActivationStruct otherEqualActivation = searchForEqualActivation(activation);
        if(otherEqualActivation != null)
        {
            AlreadyExistsException exception =
                    ExceptionBuilder.alreadyExistsException("Matching activation already exists. activationId:" +
                                                            otherEqualActivation.activationId, 0);
            getAlarmActivationConsumer().acceptAlreadyExistsException(requestId, exception.details);
        }
        else
        {
            try
            {
                int nextId = idService.getNextID();
                activation.activationId = nextId;
                activation.lastChanged = DateWrapper.convertToDateTime(System.currentTimeMillis());

                Integer activationKey = new Integer(activation.activationId);

                activationsById.put(activationKey, activation);


                Integer definitionKey = new Integer(activation.definition.definitionId);
                Map<Integer, AlarmActivationStruct> activationMap = activationsByDefinitionKey.get(definitionKey);
                if(activationMap == null)
                {
                    activationMap = new HashMap<Integer, AlarmActivationStruct>(101);
                }
                activationMap.put(activationKey, activation);
                activationsByDefinitionKey.put(definitionKey, activationMap);


                getAlarmActivationConsumer().acceptNewActivation(requestId, activation);
            }
            catch(NotFoundException e)
            {
                getAlarmActivationConsumer().acceptNotFoundException(requestId, e.details);
            }
            catch(SystemException e)
            {
                getAlarmActivationConsumer().acceptSystemException(requestId, e.details);
            }
        }
    }

    public void updateActivation(long requestId, AlarmActivationStruct activation)
    {
        registerForDefinitionEvents();

        Integer activationKey = new Integer(activation.activationId);

        AlarmActivationStruct struct = activationsById.get(activationKey);
        if(struct != null)
        {
            try
            {
                AlarmDefinition foundDefinition =
                        InstrumentationTranslatorFactory.find().getAlarmDefinitionById(activation.definition.definitionId);
                if(foundDefinition == null)
                {
                    NotFoundException notFoundException =
                            ExceptionBuilder.notFoundException("Definition for Id:" +
                                                               activation.definition.definitionId +
                                                               ", does not exist.",
                                                               NotFoundCodes.RESOURCE_DOESNT_EXIST);
                    getAlarmActivationConsumer().acceptNotFoundException(requestId, notFoundException.details);
                    return;
                }
            }
            catch(NotFoundException e)
            {
                getAlarmActivationConsumer().acceptNotFoundException(requestId, e.details);
                return;
            }
            catch(SystemException e)
            {
                getAlarmActivationConsumer().acceptSystemException(requestId, e.details);
                return;
            }
            catch(CommunicationException e)
            {
                getAlarmActivationConsumer().acceptSystemException(requestId, e.details);
                return;
            }

            AlarmActivationStruct otherEqualActivation = searchForEqualActivation(activation);
            if(otherEqualActivation != null && activation.activationId != otherEqualActivation.activationId)
            {
                AlreadyExistsException exception =
                        ExceptionBuilder.alreadyExistsException("Matching activation already exists. activationId:" +
                                                                otherEqualActivation.activationId, 0);
                getAlarmActivationConsumer().acceptAlreadyExistsException(requestId, exception.details);
            }
            else
            {
                activationsById.remove(activationKey);

                activation.lastChanged = DateWrapper.convertToDateTime(System.currentTimeMillis());

                activationsById.put(activationKey, activation);


                Integer definitionKey = new Integer(activation.definition.definitionId);
                Map<Integer, AlarmActivationStruct> activationMap = activationsByDefinitionKey.get(definitionKey);
                if(activationMap == null)
                {
                    activationMap = new HashMap<Integer, AlarmActivationStruct>(101);
                }
                activationMap.put(activationKey, activation);
                activationsByDefinitionKey.put(definitionKey, activationMap);


                getAlarmActivationConsumer().acceptChangedActivation(requestId, activation);
            }
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Activation for Id:" + activation.activationId +
                                                       ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmActivationConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    public void deleteActivation(long requestId, AlarmActivationStruct activation)
    {
        registerForDefinitionEvents();

        Integer activationKey = new Integer(activation.activationId);

        AlarmActivationStruct struct = activationsById.get(activationKey);
        if(struct != null)
        {
            try
            {
                AlarmNotificationWatchdog foundWatchdog =
                        InstrumentationTranslatorFactory.find().getWatchdogForActivationId(activationKey);
                if(foundWatchdog != null)
                {
                    DataValidationException exception =
                            ExceptionBuilder.dataValidationException("Activation for Id:" + activationKey +
                                                                     ", has watchdogs. Watchdogs must be deleted first.",
                                                                     0);
                    getAlarmActivationConsumer().acceptDataValidationException(requestId, exception.details);
                    return;
                }
            }
            catch(NotFoundException e)
            {
                //normal exception, actually expected.
            }

            Integer definitionKey = new Integer(struct.definition.definitionId);
            Map<Integer, AlarmActivationStruct> activationMap = activationsByDefinitionKey.get(definitionKey);
            if(activationMap != null)
            {
                activationMap.remove(activationKey);
                if(activationMap.isEmpty())
                {
                    activationsByDefinitionKey.remove(definitionKey);
                }
                else
                {
                    activationsByDefinitionKey.put(definitionKey, activationMap);
                }
            }

            activationsById.remove(activationKey);
            getAlarmActivationConsumer().acceptDeleteActivation(requestId, struct);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Activation for Id:" + activationKey +
                                                       ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmActivationConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    public void activate(long requestId, AlarmActivationStruct activation)
    {
        activateAlarmActivation(requestId, activation, true);
    }

    public void deactivate(long requestId, AlarmActivationStruct activation)
    {
        activateAlarmActivation(requestId, activation, false);
    }

    private void activateAlarmActivation(long requestId, AlarmActivationStruct activation, boolean activate)
    {
        registerForDefinitionEvents();

        Integer activationKey = new Integer(activation.activationId);

        AlarmActivationStruct struct = activationsById.get(activationKey);
        if(struct != null)
        {
            activation.activeStatus = activate;
            activation.lastChanged = DateWrapper.convertToDateTime(System.currentTimeMillis());

            activationsById.put(activationKey, activation);
            getAlarmActivationConsumer().acceptChangedActivation(requestId, activation);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Activation for Id:" + activation.activationId +
                                                       ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmActivationConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    private AlarmActivationConsumer getAlarmActivationConsumer()
    {
        return ((InstrumentationTranslatorImpl) InstrumentationTranslatorFactory.find()).
                getAlarmConsumersHome().getAlarmActivationConsumer();
    }

    private synchronized void registerForDefinitionEvents()
    {
        if(!isRegisteredForDefinitionEvents)
        {
            InstrumentationTranslatorFactory.find().subscribeAlarmDefinitionStatus(this);
            isRegisteredForDefinitionEvents = true;
        }
    }

    private AlarmActivationStruct searchForEqualActivation(AlarmActivationStruct activation)
    {
        AlarmActivationStruct foundActivation = null;

        for(Iterator<AlarmActivationStruct> iterator = activationsById.values().iterator(); iterator.hasNext();)
        {
            AlarmActivationStruct alarmActivationStruct = iterator.next();
            if(isActivationsEqual(activation, alarmActivationStruct))
            {
                foundActivation = alarmActivationStruct;
                break;
            }
        }

        return foundActivation;
    }

    private boolean isActivationsEqual(AlarmActivationStruct activation1, AlarmActivationStruct activation2)
    {
        boolean isEqual;

        AlarmDefinitionStruct act1Def = activation1.definition;
        AlarmDefinitionStruct act2Def = activation2.definition;

        isEqual = (activation1.notificationReceiver.equals(activation2.notificationReceiver) &&
                   activation1.notificationType == activation2.notificationType &&
                   act1Def.definitionId == act2Def.definitionId);

        return isEqual;
    }

	public void createActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
        try
        {
	        struct.assignmentId = idService.getNextID();
	        assignmentsById.put(struct.assignmentId, struct);
	        getAlarmActivationConsumer().acceptNewActivationAssignment(requestId, struct);
        }
        catch (SystemException e)
        {
        	getAlarmActivationConsumer().acceptSystemException(requestId, e.details);
        }
        catch (NotFoundException e)
        {
        	getAlarmActivationConsumer().acceptNotFoundException(requestId, e.details);
        }
    }
	
	public void updateActivationAssignment(long requestId, ActivationAssignmentStruct struct)
	{
		ActivationAssignmentStruct activationAssignmentStruct = assignmentsById.get(struct.assignmentId);
		if (activationAssignmentStruct != null)
		{
			assignmentsById.put(struct.assignmentId, struct);
		}
		else
		{
			NotFoundException exception = ExceptionBuilder.notFoundException("ActivationAssignment not found with id:" + struct.assignmentId, 0);
			getAlarmActivationConsumer().acceptNotFoundException(requestId, exception.details);
		}
	}

	public void deleteActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
		ActivationAssignmentStruct activationAssignmentStruct = assignmentsById.get(struct.assignmentId);
		if (activationAssignmentStruct != null)
		{
			assignmentsById.remove(struct.assignmentId);
		}
		else
		{
			NotFoundException exception = ExceptionBuilder.notFoundException("ActivationAssignment not found with id:" + struct.assignmentId, 0);
			getAlarmActivationConsumer().acceptNotFoundException(requestId, exception.details);
		}
    }

	public void publishActivationAssignmentById(long requestId, int activationAssignmentId)
    {
        ActivationAssignmentStruct struct = assignmentsById.get(activationAssignmentId);
        if(struct != null)
        {
        	ActivationAssignmentStruct[] structs = {struct};
            getAlarmActivationConsumer().acceptActivationAssignments(requestId, structs);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("ActivationAssignment for Id:" + activationAssignmentId + ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmActivationConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

	public void publishAllActivationAssignments(long requestId)
    {
		ActivationAssignmentStruct[] structs = assignmentsById.values().toArray(new ActivationAssignmentStruct[0]);
		getAlarmActivationConsumer().acceptActivationAssignments(requestId, structs);
    }
}
