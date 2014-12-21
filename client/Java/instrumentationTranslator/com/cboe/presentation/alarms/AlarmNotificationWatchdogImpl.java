//
// -----------------------------------------------------------------------------------
// Source file: AlarmNotificationWatchdogImpl.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import com.cboe.idl.alarm.AlarmNotificationWatchdogStruct;
import com.cboe.idl.alarm.NotificationWatchdogPolicyStruct;
import com.cboe.idl.alarmConstants.NotificationWatchdogStates;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationWatchdogMutable;
import com.cboe.interfaces.instrumentation.alarms.NotificationWatchdogPolicy;
import com.cboe.interfaces.instrumentation.alarms.AlarmActivation;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationWatchdog;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;

import com.cboe.instrumentationCollector.watchdogs.validation.WatchdogFieldValidatorFactory;

public class AlarmNotificationWatchdogImpl
        extends AbstractMutableBusinessModel
        implements AlarmNotificationWatchdogMutable
{
    private AlarmNotificationWatchdogStruct struct;
    private NotificationWatchdogPolicy[] policies;

    AlarmNotificationWatchdogImpl(AlarmNotificationWatchdogStruct struct)
    {
        setStruct(struct);
    }

    AlarmNotificationWatchdogImpl(AlarmActivation activation)
    {
        checkParam(activation, "AlarmActivation");

        AlarmNotificationWatchdogStruct newStruct = new AlarmNotificationWatchdogStruct();
        newStruct.state = NotificationWatchdogStates.INACTIVE;
        newStruct.activationId = activation.getId();

        setStruct(newStruct);

        setModified(true);
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            isEqual = obj instanceof AlarmNotificationWatchdog;
            if(isEqual)
            {
                AlarmNotificationWatchdog castedObj = (AlarmNotificationWatchdog) obj;
                isEqual = getId() == castedObj.getId();
                if(isEqual)
                {
                    isEqual = getAlarmActivationId().equals(castedObj.getAlarmActivationId());
                }
            }
        }

        return isEqual;
    }

    public int hashCode()
    {
        return getId().intValue();
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        AlarmNotificationWatchdogImpl newImpl = (AlarmNotificationWatchdogImpl) super.clone();

        AlarmNotificationWatchdogStruct newStruct = getStruct();
        newImpl.setStruct(newStruct);

        return newImpl;
    }

    @Deprecated public AlarmNotificationWatchdogStruct getStruct()
    {
        AlarmNotificationWatchdogStruct newStruct = new AlarmNotificationWatchdogStruct();
        newStruct.notificationWatchdogId = getId().intValue();
        newStruct.state = getState();
        newStruct.activationId = struct.activationId;

        newStruct.policies = new NotificationWatchdogPolicyStruct[policies.length];
        for(int i = 0; i < policies.length; i++)
        {
            NotificationWatchdogPolicy policy = policies[i];
            newStruct.policies[i] = policy.getStruct();
        }

        return newStruct;
    }

    @Deprecated public void setStruct(AlarmNotificationWatchdogStruct struct)
    {
        checkParam(struct, "AlarmNotificationWatchdogStruct");
        this.struct = struct;

        this.struct = struct;

        policies = new NotificationWatchdogPolicy[struct.policies.length];
        for(int i = 0; i < struct.policies.length; i++)
        {
            NotificationWatchdogPolicyStruct policyStruct = struct.policies[i];
            policies[i] = AlarmNotificationWatchdogFactory.createNotificationWatchdogPolicy(policyStruct);
        }
    }

    public AlarmActivation getAlarmActivation()
            throws SystemException, NotFoundException, CommunicationException
    {
        AlarmActivation activation = InstrumentationTranslatorFactory.find().getAlarmActivationById(struct.activationId);
        return activation;
    }

    public Integer getAlarmActivationId()
    {
        return struct.activationId;
    }

    public Integer getId()
    {
        return struct.notificationWatchdogId;
    }

    public short getState()
    {
        return struct.state;
    }

    public void setState(short state)
    {
        if(getState() != state)
        {
            try
            {
                WatchdogFieldValidatorFactory.getInstance().validateState(getId(), state);
                struct.state = state;
                setModified(true);
            }
            catch(DataValidationException e)
            {
                throw new IllegalArgumentException("state is invalid.", e);
            }
        }
    }

    public NotificationWatchdogPolicy getPolicy(int index)
    {
        return policies[index];
    }

    public NotificationWatchdogPolicy[] getPolicies()
    {
        return policies.clone();
    }

    public int getPoliciesSize()
    {
        return policies.length;
    }

    public void setPolicies(NotificationWatchdogPolicy[] policies)
    {
        if(policies != null)
        {
            try
            {
                NotificationWatchdogPolicyStruct[] structs = new NotificationWatchdogPolicyStruct[policies.length];
                for(int i = 0; i < policies.length; i++)
                {
                    NotificationWatchdogPolicy policy = policies[i];
                    structs[i] = policy.getStruct();
                }
                WatchdogFieldValidatorFactory.getInstance().validatePolicies(getId(), structs);

                this.policies = new NotificationWatchdogPolicy[policies.length];
                System.arraycopy(policies, 0, this.policies, 0, policies.length);

                struct.policies = new NotificationWatchdogPolicyStruct[0];
                setModified(true);
            }
            catch(DataValidationException e)
            {
                throw new IllegalArgumentException("A policy is invalid.", e);
            }
        }
        else
        {
            this.policies = new NotificationWatchdogPolicy[0];
            struct.policies = new NotificationWatchdogPolicyStruct[0];
            setModified(true);
        }
    }

    public void setPolicy(int index, NotificationWatchdogPolicy policy)
    {
        try
        {
            WatchdogFieldValidatorFactory.getInstance().validatePolicy(getId(), policy.getStruct());
            policies[index] = policy;
            struct.policies = new NotificationWatchdogPolicyStruct[0];
            setModified(true);
        }
        catch(DataValidationException e)
        {
            throw new IllegalArgumentException("policy is invalid.", e);
        }
    }

    public void addPolicy(int index, NotificationWatchdogPolicy policy)
    {
        try
        {
            WatchdogFieldValidatorFactory.getInstance().validatePolicy(getId(), policy.getStruct());
            policies =
                (NotificationWatchdogPolicy[]) AlarmNotificationWatchdogFactory.addObjectToArray(index,
                                                                                                 policy, policies);
            struct.policies = new NotificationWatchdogPolicyStruct[0];
            setModified(true);
        }
        catch(DataValidationException e)
        {
            throw new IllegalArgumentException("policy is invalid.", e);
        }
    }

    public void removePolicy(int index)
    {
        policies = (NotificationWatchdogPolicy[]) AlarmNotificationWatchdogFactory.removeObjectFromArray(index,
                                                                                                         policies);
        struct.policies = new NotificationWatchdogPolicyStruct[0];
        setModified(true);
    }
}
