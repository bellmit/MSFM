//
// -----------------------------------------------------------------------------------
// Source file: AlarmActivationFactory.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import com.cboe.idl.alarm.AlarmActivationStruct;

import com.cboe.interfaces.instrumentation.alarms.AlarmActivationMutable;
import com.cboe.interfaces.instrumentation.alarms.AlarmActivation;
import com.cboe.interfaces.instrumentation.alarms.AlarmDefinition;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public final class AlarmActivationFactory
{
    private AlarmActivationFactory(){}

    public static AlarmActivation createAlarmActivation(AlarmActivationStruct struct)
    {
        return new AlarmActivationImpl(struct);
    }

    public static AlarmActivation createAlarmActivation(AlarmActivationStruct struct, AlarmDefinition definition)
    {
        return new AlarmActivationImpl(struct, definition);
    }

    public static AlarmActivationMutable createMutableAlarmActivation(AlarmActivationStruct struct)
    {
        return new AlarmActivationImpl(struct);
    }

    public static AlarmActivationMutable createMutableAlarmActivation(AlarmActivation condition)
    {
        try
        {
            return (AlarmActivationMutable) condition.clone();
        }
        catch(CloneNotSupportedException e)
        {
            //should never happen, but just in case
            DefaultExceptionHandlerHome.find().process(e);
            return (AlarmActivationMutable) condition;
        }
    }

    public static AlarmActivationMutable createNewMutableAlarmActivation(AlarmDefinition definition)
    {
        return new AlarmActivationImpl(definition);
    }
}
