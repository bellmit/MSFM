//
// ------------------------------------------------------------------------
// FILE: AlarmDefinitionConsumerImpl.java
// 
// PACKAGE: com.cboe.presentation.alarms.events.consumers
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.alarms.events.consumers;

import com.cboe.idl.alarm.AlarmCalculationStruct;
import com.cboe.idl.alarm.AlarmConditionStruct;
import com.cboe.idl.alarm.AlarmDefinitionStruct;
import com.cboe.interfaces.events.AlarmDefinitionConsumer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.util.ChannelType;

/**
 * @author torresl@cboe.com
 */
public class AlarmDefinitionConsumerImpl
        extends AlarmConsumerImpl
        implements AlarmDefinitionConsumer
{

    public AlarmDefinitionConsumerImpl()
    {
    }

    public void acceptConditions(long requestId, AlarmConditionStruct[] alarmConditionStructs)
    {
        GUILoggerHome.find().debug("acceptConditions (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_CONDITION, alarmConditionStructs);
        dispatchEvent(ChannelType.IC_ACCEPT_CONDITIONS, requestId, alarmConditionStructs);
    }

    public void acceptNewCondition(long requestId, AlarmConditionStruct alarmConditionStruct)
    {
        GUILoggerHome.find().debug("acceptNewCondition (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_CONDITION, alarmConditionStruct);
        dispatchEvent(ChannelType.IC_ACCEPT_NEW_CONDITION, requestId, alarmConditionStruct);
    }

    public void acceptChangedCondition(long requestId, AlarmConditionStruct alarmConditionStruct)
    {
        GUILoggerHome.find().debug("acceptChangedCondition (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_CONDITION, alarmConditionStruct);
        dispatchEvent(ChannelType.IC_ACCEPT_CHANGED_CONDITION, requestId, alarmConditionStruct);
    }

    public void acceptDeleteCondition(long requestId, AlarmConditionStruct alarmConditionStruct)
    {
        GUILoggerHome.find().debug("acceptDeleteCondition (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_CONDITION, alarmConditionStruct);
        dispatchEvent(ChannelType.IC_ACCEPT_DELETED_CONDITION, requestId, alarmConditionStruct);
    }

    public void acceptCalculations(long requestId, AlarmCalculationStruct[] alarmCalculationStructs)
    {
        GUILoggerHome.find().debug("acceptCalculations (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_CALCULATION, alarmCalculationStructs);
        dispatchEvent(ChannelType.IC_ACCEPT_CALCULATION, requestId, alarmCalculationStructs);
    }

    public void acceptNewCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        GUILoggerHome.find().debug("acceptNewCalculation (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_CALCULATION, alarmCalculationStruct);
        dispatchEvent(ChannelType.IC_ACCEPT_NEW_CALCULATION, requestId, alarmCalculationStruct);
    }

    public void acceptChangedCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        GUILoggerHome.find().debug("acceptChangedCalculation (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_CALCULATION, alarmCalculationStruct);
        dispatchEvent(ChannelType.IC_ACCEPT_CHANGED_CALCULATION, requestId, alarmCalculationStruct);
    }

    public void acceptDeleteCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        GUILoggerHome.find().debug("acceptDeleteCalculation (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_CALCULATION, alarmCalculationStruct);
        dispatchEvent(ChannelType.IC_ACCEPT_DELETED_CALCULATION, requestId, alarmCalculationStruct);
    }

    public void acceptDefinitions(long requestId, AlarmDefinitionStruct[] alarmDefinitionStructs)
    {
        GUILoggerHome.find().debug("acceptDefinitions (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_DEFINITION, alarmDefinitionStructs);
        dispatchEvent(ChannelType.IC_ACCEPT_DEFINITIONS, requestId, alarmDefinitionStructs);
    }

    public void acceptNewDefinition(long requestId, AlarmDefinitionStruct alarmDefinitionStruct)
    {
        GUILoggerHome.find().debug("acceptNewDefinition (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_DEFINITION, alarmDefinitionStruct);
        dispatchEvent(ChannelType.IC_ACCEPT_NEW_DEFINITION, requestId, alarmDefinitionStruct);
    }

    public void acceptChangedDefinition(long requestId, AlarmDefinitionStruct alarmDefinitionStruct)
    {
        GUILoggerHome.find().debug("acceptChangedDefinition (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_DEFINITION, alarmDefinitionStruct);
        dispatchEvent(ChannelType.IC_ACCEPT_CHANGED_DEFINITION, requestId, alarmDefinitionStruct);
    }

    public void acceptDeleteDefinition(long requestId, AlarmDefinitionStruct alarmDefinitionStruct)
    {
        GUILoggerHome.find().debug("acceptDeleteCondition (requestId=" + requestId + ')', GUILoggerINBusinessProperty.ALARM_DEFINITION, alarmDefinitionStruct);
        dispatchEvent(ChannelType.IC_ACCEPT_DELETE_DEFINITION, requestId, alarmDefinitionStruct);
    }
}
