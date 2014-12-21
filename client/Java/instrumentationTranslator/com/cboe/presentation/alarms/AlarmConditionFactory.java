//
// -----------------------------------------------------------------------------------
// Source file: AlarmConditionFactory.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import com.cboe.idl.alarm.AlarmConditionStruct;

import com.cboe.interfaces.instrumentation.alarms.AlarmCondition;
import com.cboe.interfaces.instrumentation.alarms.AlarmConditionMutable;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public final class AlarmConditionFactory
{
    private AlarmConditionFactory(){};

    public static AlarmCondition createAlarmCondition(AlarmConditionStruct struct)
    {
        return new AlarmConditionImpl(struct);
    }

    public static AlarmConditionMutable createMutableAlarmCondition(AlarmConditionStruct struct)
    {
        return new AlarmConditionImpl(struct);
    }

    public static AlarmConditionMutable createMutableAlarmCondition(AlarmCondition condition)
    {
        try
        {
            return (AlarmConditionMutable) condition.clone();
        }
        catch(CloneNotSupportedException e)
        {
            //should never happen, but just in case
            DefaultExceptionHandlerHome.find().process(e);
            return (AlarmConditionMutable) condition;
        }
    }

    public static AlarmConditionMutable createNewMutableAlarmCondition()
    {
        return new AlarmConditionImpl();
    }
}
