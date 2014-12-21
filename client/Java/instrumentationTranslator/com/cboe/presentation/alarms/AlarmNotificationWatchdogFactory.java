//
// -----------------------------------------------------------------------------------
// Source file: AlarmNotificationWatchdogFactory.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import java.lang.reflect.Array;

import com.cboe.idl.alarm.NotificationWatchdogLimitStruct;
import com.cboe.idl.alarm.NotificationWatchdogPolicyStruct;
import com.cboe.idl.alarm.AlarmNotificationWatchdogStruct;

import com.cboe.interfaces.instrumentation.alarms.NotificationWatchdogLimit;
import com.cboe.interfaces.instrumentation.alarms.NotificationWatchdogLimitMutable;
import com.cboe.interfaces.instrumentation.alarms.NotificationWatchdogPolicy;
import com.cboe.interfaces.instrumentation.alarms.NotificationWatchdogPolicyMutable;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationWatchdog;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationWatchdogMutable;
import com.cboe.interfaces.instrumentation.alarms.AlarmActivation;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public final class AlarmNotificationWatchdogFactory
{
    private AlarmNotificationWatchdogFactory(){}

    public static NotificationWatchdogLimit createNotificationWatchdogLimit(NotificationWatchdogLimitStruct struct)
    {
        return new NotificationWatchdogLimitImpl(struct);
    }

    public static NotificationWatchdogLimitMutable createMutableNotificationWatchdogLimit(NotificationWatchdogLimitStruct struct)
    {
        return new NotificationWatchdogLimitImpl(struct);
    }

    public static NotificationWatchdogLimitMutable createMutableNotificationWatchdogLimit(NotificationWatchdogLimit limit)
    {
        try
        {
            return (NotificationWatchdogLimitMutable) limit.clone();
        }
        catch(CloneNotSupportedException e)
        {
            //should never happen, but just in case
            DefaultExceptionHandlerHome.find().process(e);
            return (NotificationWatchdogLimitMutable) limit;
        }
    }

    public static NotificationWatchdogLimitMutable createNewNotificationWatchdogLimit()
    {
        return new NotificationWatchdogLimitImpl();
    }

    public static NotificationWatchdogPolicy createNotificationWatchdogPolicy(NotificationWatchdogPolicyStruct struct)
    {
        return new NotificationWatchdogPolicyImpl(struct);
    }

    public static NotificationWatchdogPolicyMutable createMutableNotificationWatchdogPolicy(NotificationWatchdogPolicyStruct struct)
    {
        return new NotificationWatchdogPolicyImpl(struct);
    }

    public static NotificationWatchdogPolicyMutable createMutableNotificationWatchdogPolicy(NotificationWatchdogPolicy policy)
    {
        try
        {
            return (NotificationWatchdogPolicyMutable) policy.clone();
        }
        catch(CloneNotSupportedException e)
        {
            //should never happen, but just in case
            DefaultExceptionHandlerHome.find().process(e);
            return (NotificationWatchdogPolicyMutable) policy;
        }
    }

    public static NotificationWatchdogPolicyMutable createNewNotificationWatchdogPolicy()
    {
        return new NotificationWatchdogPolicyImpl();
    }

    public static AlarmNotificationWatchdog createAlarmNotificationWatchdog(AlarmNotificationWatchdogStruct struct)
    {
        return new AlarmNotificationWatchdogImpl(struct);
    }

    public static AlarmNotificationWatchdogMutable createMutableAlarmNotificationWatchdog(AlarmNotificationWatchdogStruct struct)
    {
        return new AlarmNotificationWatchdogImpl(struct);
    }

    public static AlarmNotificationWatchdogMutable createMutableAlarmNotificationWatchdog(AlarmNotificationWatchdog watchdog)
    {
        try
        {
            return (AlarmNotificationWatchdogMutable) watchdog.clone();
        }
        catch(CloneNotSupportedException e)
        {
            //should never happen, but just in case
            DefaultExceptionHandlerHome.find().process(e);
            return (AlarmNotificationWatchdogMutable) watchdog;
        }
    }

    public static AlarmNotificationWatchdogMutable createNewAlarmNotificationWatchdog(AlarmActivation activation)
    {
        return new AlarmNotificationWatchdogImpl(activation);
    }

    static Object[] addObjectToArray(int index, Object objectToAdd, Object[] array)
    {
        Object[] newArray;

        if(index > -1)
        {
            if(array.length == 0 && index == 0)
            {
                newArray = (Object[]) Array.newInstance(objectToAdd.getClass(), 1);
                newArray[0] = objectToAdd;
            }
            else if(index == array.length)
            {
                newArray = (Object[]) Array.newInstance(objectToAdd.getClass(), array.length + 1);
                System.arraycopy(array, 0, newArray, 0, array.length);
                newArray[index] = objectToAdd;
            }
            else if(index < array.length)
            {
                newArray = (Object[]) Array.newInstance(objectToAdd.getClass(), array.length + 1);
                System.arraycopy(array, 0, newArray, 0, index);
                newArray[index] = objectToAdd;
                System.arraycopy(array, index, newArray, index + 1,
                                 array.length - index);
            }
            else
            {
                throw new IllegalArgumentException("index is not valid: " + index +
                                                   "; Current size: " + array.length);
            }
        }
        else
        {
            throw new IllegalArgumentException("index is not valid: " + index +
                                               "; Current size: " + array.length);
        }

        return newArray;
    }

    static Object[] removeObjectFromArray(int index, Object[] array)
    {
        Object[] newArray;

        if(index > -1 && index < array.length)
        {
            newArray = (Object[]) Array.newInstance(array[0].getClass(), array.length - 1);
            System.arraycopy(array, 0, newArray, 0, index);
            if(index != (array.length - 1))
            {
                System.arraycopy(array, index + 1, newArray, index,
                                 newArray.length - index);
            }
        }
        else
        {
            throw new IllegalArgumentException("index is not valid: " + index +
                                               "; Current size: " + array.length);
        }

        return newArray;
    }
}
