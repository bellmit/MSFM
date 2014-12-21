//
// -----------------------------------------------------------------------------------
// Source file: NotificationWatchdogPolicyImpl.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import java.util.*;

import com.cboe.idl.alarm.NotificationWatchdogLimitStruct;
import com.cboe.idl.alarm.NotificationWatchdogPolicyStruct;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.instrumentation.alarms.NotificationWatchdogLimit;
import com.cboe.interfaces.instrumentation.alarms.NotificationWatchdogPolicy;
import com.cboe.interfaces.instrumentation.alarms.NotificationWatchdogPolicyMutable;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

import com.cboe.instrumentationCollector.watchdogs.validation.WatchdogFieldValidatorFactory;

public class NotificationWatchdogPolicyImpl
        extends AbstractMutableBusinessModel
        implements NotificationWatchdogPolicyMutable
{
    private NotificationWatchdogPolicyStruct struct;
    private NotificationWatchdogLimit[] limits;

    NotificationWatchdogPolicyImpl()
    {
        this(new NotificationWatchdogPolicyStruct());
        setModified(true);
    }

    NotificationWatchdogPolicyImpl(NotificationWatchdogPolicyStruct struct)
    {
        setStruct(struct);
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            isEqual = obj instanceof NotificationWatchdogPolicy;
            if(isEqual)
            {
                NotificationWatchdogPolicy castedObj = (NotificationWatchdogPolicy) obj;
                isEqual = Arrays.equals(getLimits(), castedObj.getLimits());
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
        NotificationWatchdogPolicyImpl newImpl = (NotificationWatchdogPolicyImpl) super.clone();

        NotificationWatchdogPolicyStruct newStruct = getStruct();
        newImpl.setStruct(newStruct);

        return newImpl;
    }

    @Deprecated public NotificationWatchdogPolicyStruct getStruct()
    {
        NotificationWatchdogPolicyStruct newStruct = new NotificationWatchdogPolicyStruct();
        newStruct.notificationWatchdogPolicyId = getId().intValue();

        newStruct.limits = new NotificationWatchdogLimitStruct[limits.length];
        for(int i = 0; i < limits.length; i++)
        {
            NotificationWatchdogLimit limit = limits[i];
            newStruct.limits[i] = limit.getStruct();
        }

        return newStruct;
    }

    @Deprecated public void setStruct(NotificationWatchdogPolicyStruct struct)
    {
        checkParam(struct, "NotificationWatchdogPolicyStruct");

        this.struct = struct;

        limits = new NotificationWatchdogLimit[struct.limits.length];
        for(int i = 0; i < struct.limits.length; i++)
        {
            NotificationWatchdogLimitStruct limitStruct = struct.limits[i];
            limits[i] = AlarmNotificationWatchdogFactory.createNotificationWatchdogLimit(limitStruct);
        }
    }

    public Integer getId()
    {
        return struct.notificationWatchdogPolicyId;
    }

    public NotificationWatchdogLimit getLimit(int index)
    {
        return limits[index];
    }

    public NotificationWatchdogLimit[] getLimits()
    {
        return limits.clone();
    }

    public int getLimitsSize()
    {
        return limits.length;
    }

    public void setLimits(NotificationWatchdogLimit[] limits)
    {
        if(limits != null)
        {
            try
            {
                NotificationWatchdogLimitStruct[] structs = new NotificationWatchdogLimitStruct[limits.length];
                for(int i = 0; i < limits.length; i++)
                {
                    NotificationWatchdogLimit limit = limits[i];
                    structs[i] = limit.getStruct();
                }
                WatchdogFieldValidatorFactory.getInstance().validateLimits(getId(), structs);

                this.limits = new NotificationWatchdogLimit[limits.length];
                System.arraycopy(limits, 0, this.limits, 0, limits.length);

                struct.limits = new NotificationWatchdogLimitStruct[0];
                setModified(true);
            }
            catch(DataValidationException e)
            {
                throw new IllegalArgumentException("A limit is invalid.", e);
            }
        }
        else
        {
            this.limits = new NotificationWatchdogLimit[0];
            struct.limits = new NotificationWatchdogLimitStruct[0];
            setModified(true);
        }
    }

    public void setLimit(int index, NotificationWatchdogLimit limit)
    {
        try
        {
            WatchdogFieldValidatorFactory.getInstance().validateLimit(getId(), limit.getStruct());
            limits[index] = limit;
            struct.limits = new NotificationWatchdogLimitStruct[0];
            setModified(true);
        }
        catch(DataValidationException e)
        {
            throw new IllegalArgumentException("limit is invalid.", e);
        }
    }

    public void addLimit(int index, NotificationWatchdogLimit limit)
    {
        try
        {
            WatchdogFieldValidatorFactory.getInstance().validateLimit(getId(), limit.getStruct());
            limits =
                (NotificationWatchdogLimit[]) AlarmNotificationWatchdogFactory.addObjectToArray(index, limit, limits);
            struct.limits = new NotificationWatchdogLimitStruct[0];
            setModified(true);
        }
        catch(DataValidationException e)
        {
            throw new IllegalArgumentException("limit is invalid.", e);
        }
    }

    public void removeLimit(int index)
    {
        limits = (NotificationWatchdogLimit[]) AlarmNotificationWatchdogFactory.removeObjectFromArray(index, limits);
        struct.limits = new NotificationWatchdogLimitStruct[0];
        setModified(true);
    }
}
