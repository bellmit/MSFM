//
// -----------------------------------------------------------------------------------
// Source file: AlarmDefinitionFactory.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import com.cboe.idl.alarm.AlarmDefinitionStruct;
import com.cboe.idl.alarm.AlarmConditionStruct;

import com.cboe.interfaces.instrumentation.alarms.AlarmDefinition;
import com.cboe.interfaces.instrumentation.alarms.AlarmDefinitionMutable;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public final class AlarmDefinitionFactory
{
    private AlarmDefinitionFactory(){};

    public static AlarmDefinition createAlarmDefinition(AlarmDefinitionStruct struct)
    {
        return new AlarmDefinitionImpl(struct);
    }

    public static AlarmDefinition createAlarmDefinition(AlarmDefinitionStruct definitionStruct,
                                                        AlarmConditionStruct[] conditionStructs)
    {
        return new AlarmDefinitionImpl(definitionStruct, conditionStructs);
    }

    public static AlarmDefinitionMutable createMutableAlarmDefinition(AlarmDefinitionStruct struct)
    {
        return new AlarmDefinitionImpl(struct);
    }

    public static AlarmDefinitionMutable createMutableAlarmDefinition(AlarmDefinition definition)
    {
        try
        {
            return (AlarmDefinitionMutable) definition.clone();
        }
        catch(CloneNotSupportedException e)
        {
            //should never happen, but just in case
            DefaultExceptionHandlerHome.find().process(e);
            return (AlarmDefinitionMutable) definition;
        }
    }

    public static AlarmDefinitionMutable createNewMutableAlarmDefinition()
    {
        return new AlarmDefinitionImpl();
    }

    public static void initializeActivations(AlarmDefinition definition)
    {
        ((AlarmDefinitionImpl)definition).reinitializeActivations();
    }
}
