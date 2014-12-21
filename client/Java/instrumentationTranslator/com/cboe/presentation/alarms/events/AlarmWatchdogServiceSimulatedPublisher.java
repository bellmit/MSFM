//
// ------------------------------------------------------------------------
// FILE: AlarmWatchdogServiceSimulatedPublisher.java
// 
// PACKAGE: com.cboe.presentation.alarms.events;
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.alarms.events;

import java.util.*;

import com.cboe.idl.alarm.AlarmNotificationWatchdogStruct;
import com.cboe.idl.alarm.NotificationWatchdogPolicyStruct;
import com.cboe.idl.alarm.NotificationWatchdogLimitStruct;
import com.cboe.idl.alarmEvents.AlarmNotificationWatchdogEventService;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.events.AlarmNotificationWatchdogConsumer;
import com.cboe.interfaces.events.AlarmNotificationWatchdogEventDelegateServiceConsumer;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.api.InstrumentationTranslatorImpl;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.uuidService.IdService;
import com.cboe.instrumentationCollector.watchdogs.validation.WatchdogFieldValidatorFactory;

public class AlarmWatchdogServiceSimulatedPublisher
        implements AlarmNotificationWatchdogEventDelegateServiceConsumer
{
    private IdService idService;

    private Map<Integer, AlarmNotificationWatchdogStruct> watchdogsById =
            new HashMap<Integer, AlarmNotificationWatchdogStruct>(101);

    public AlarmWatchdogServiceSimulatedPublisher()
    {
        idService = FoundationFramework.getInstance().getIdService();
    }

    public void setAlarmNotificationWatchdogEventServiceDelegate(AlarmNotificationWatchdogEventService service)
    {
        //not used, required for interface
    }

    public void publishAllWatchdogs(long requestId)
    {
        AlarmNotificationWatchdogStruct[] structs = new AlarmNotificationWatchdogStruct[0];
        structs = watchdogsById.values().toArray(structs);
        getAlarmWatchdogConsumer().acceptWatchdogs(requestId, structs);
    }

    public void publishWatchdogById(long requestId, int watchdogId)
    {
        AlarmNotificationWatchdogStruct struct = watchdogsById.get(new Integer(watchdogId));
        if(struct != null)
        {
            AlarmNotificationWatchdogStruct[] structs = {struct};
            getAlarmWatchdogConsumer().acceptWatchdogs(requestId, structs);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Watchdog for Id:" + watchdogId + ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmWatchdogConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    public void createWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        AlarmNotificationWatchdogStruct otherEqualWatchdog = searchForEqualWatchdog(alarmNotificationWatchdogStruct);
        if(otherEqualWatchdog != null)
        {
            AlreadyExistsException exception =
                    ExceptionBuilder.alreadyExistsException("Matching Watchdog already exists. notificationWatchdogId:" +
                                                            otherEqualWatchdog.notificationWatchdogId, 0);
            getAlarmWatchdogConsumer().acceptAlreadyExistsException(requestId, exception.details);
        }
        else
        {
            try
            {
                int nextId = idService.getNextID();
                alarmNotificationWatchdogStruct.notificationWatchdogId = nextId;

                NotificationWatchdogPolicyStruct[] policies = alarmNotificationWatchdogStruct.policies;
                for(int i = 0; i < policies.length; i++)
                {
                    NotificationWatchdogPolicyStruct policy = policies[i];
                    nextId = idService.getNextID();
                    policy.notificationWatchdogPolicyId = nextId;

                    NotificationWatchdogLimitStruct[] limits = policy.limits;
                    for(int j = 0; j < limits.length; j++)
                    {
                        NotificationWatchdogLimitStruct limit = limits[j];
                        nextId = idService.getNextID();
                        limit.notificationWatchdogLimitId = nextId;
                    }
                }

                WatchdogFieldValidatorFactory.validateAlarmNotificationWatchdog(alarmNotificationWatchdogStruct);

                Integer watchdogKey = new Integer(alarmNotificationWatchdogStruct.notificationWatchdogId);

                watchdogsById.put(watchdogKey, alarmNotificationWatchdogStruct);

                getAlarmWatchdogConsumer().acceptNewWatchdog(requestId, alarmNotificationWatchdogStruct);
            }
            catch(NotFoundException e)
            {
                getAlarmWatchdogConsumer().acceptNotFoundException(requestId, e.details);
            }
            catch(SystemException e)
            {
                getAlarmWatchdogConsumer().acceptSystemException(requestId, e.details);
            }
            catch(DataValidationException e)
            {
                getAlarmWatchdogConsumer().acceptDataValidationException(requestId, e.details);
            }
        }
    }

    public void updateWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        Integer watchdogKey = new Integer(alarmNotificationWatchdogStruct.notificationWatchdogId);

        AlarmNotificationWatchdogStruct struct = watchdogsById.get(watchdogKey);
        if(struct != null)
        {
            AlarmNotificationWatchdogStruct otherEqualCondition =
                    searchForEqualWatchdog(alarmNotificationWatchdogStruct);
            if(otherEqualCondition != null &&
               alarmNotificationWatchdogStruct.notificationWatchdogId != otherEqualCondition.notificationWatchdogId)
            {
                AlreadyExistsException exception =
                        ExceptionBuilder.alreadyExistsException("Matching Watchdog already exists. notificationWatchdogId:" +
                                                                otherEqualCondition.notificationWatchdogId, 0);
                getAlarmWatchdogConsumer().acceptAlreadyExistsException(requestId, exception.details);
            }
            else
            {
                try
                {
                    WatchdogFieldValidatorFactory.validateAlarmNotificationWatchdog(alarmNotificationWatchdogStruct);

                    watchdogsById.remove(watchdogKey);
                    watchdogsById.put(watchdogKey, alarmNotificationWatchdogStruct);
                    getAlarmWatchdogConsumer().acceptChangedWatchdog(requestId, alarmNotificationWatchdogStruct);
                }
                catch(DataValidationException e)
                {
                    getAlarmWatchdogConsumer().acceptDataValidationException(requestId, e.details);
                }
            }
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Watchdog for Id:" +
                                                       alarmNotificationWatchdogStruct.notificationWatchdogId +
                                                       ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmWatchdogConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    public void deleteWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        Integer watchdogKey = new Integer(alarmNotificationWatchdogStruct.notificationWatchdogId);

        AlarmNotificationWatchdogStruct struct = watchdogsById.remove(watchdogKey);
        if(struct != null)
        {
            getAlarmWatchdogConsumer().acceptDeleteWatchdog(requestId, struct);
        }
        else
        {
            NotFoundException notFoundException =
                    ExceptionBuilder.notFoundException("Watchdog for Id:" +
                                                       alarmNotificationWatchdogStruct.notificationWatchdogId +
                                                       ", does not exist.",
                                                       NotFoundCodes.RESOURCE_DOESNT_EXIST);
            getAlarmWatchdogConsumer().acceptNotFoundException(requestId, notFoundException.details);
        }
    }

    private AlarmNotificationWatchdogConsumer getAlarmWatchdogConsumer()
    {
        return ((InstrumentationTranslatorImpl) InstrumentationTranslatorFactory.find()).
                getAlarmConsumersHome().getAlarmNotificationWatchdogConsumer();
    }

    private AlarmNotificationWatchdogStruct searchForEqualWatchdog(AlarmNotificationWatchdogStruct watchdog)
    {
        AlarmNotificationWatchdogStruct foundWatchdog = null;

        for(Iterator<AlarmNotificationWatchdogStruct> iterator = watchdogsById.values().iterator(); iterator.hasNext();)
        {
            AlarmNotificationWatchdogStruct alarmWatchdogStruct = iterator.next();
            if(isWatchdogsEqual(watchdog, alarmWatchdogStruct))
            {
                foundWatchdog = alarmWatchdogStruct;
                break;
            }
        }

        return foundWatchdog;
    }

    private boolean isWatchdogsEqual(AlarmNotificationWatchdogStruct watchdog1, AlarmNotificationWatchdogStruct watchdog2)
    {
        boolean isEqual = watchdog1.activationId == watchdog2.activationId;

        return isEqual;
    }
}
