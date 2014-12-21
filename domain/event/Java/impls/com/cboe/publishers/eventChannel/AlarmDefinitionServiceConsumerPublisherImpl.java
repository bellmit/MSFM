//
// ------------------------------------------------------------------------
// FILE: AlarmDefinitionServiceConsumerPublisherImpl.java
// 
// PACKAGE: com.cboe.publishers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.publishers.eventChannel;

import com.cboe.idl.alarm.AlarmConditionStruct;
import com.cboe.idl.alarm.AlarmDefinitionStruct;
import com.cboe.idl.alarm.AlarmCalculationStruct;
import com.cboe.idl.alarmEvents.AlarmDefinitionEventService;
import com.cboe.interfaces.events.AlarmDefinitionEventDelegateServiceConsumer;

/**
 * @author torresl@cboe.com
 */
public class AlarmDefinitionServiceConsumerPublisherImpl
        implements AlarmDefinitionEventDelegateServiceConsumer
{
    protected AlarmDefinitionEventService eventChannelDelegate;

    public AlarmDefinitionServiceConsumerPublisherImpl()
    {
    }

    public AlarmDefinitionServiceConsumerPublisherImpl(AlarmDefinitionEventService eventChannelDelegate)
    {
        this();
        setAlarmDefinitionEventServiceDelegate(eventChannelDelegate);
    }

    public void setAlarmDefinitionEventServiceDelegate(AlarmDefinitionEventService eventChannelDelegate)
    {
        this.eventChannelDelegate = eventChannelDelegate;
    }

    public void publishConditionById(long requestId, int conditionID)
    {
        eventChannelDelegate.publishConditionById(requestId, conditionID);
    }

    public void publishAllConditions(long requestId)
    {
        eventChannelDelegate.publishAllConditions(requestId);
    }

    public void createCondition(long requestId, AlarmConditionStruct condition)
    {
        eventChannelDelegate.createCondition(requestId, condition);
    }

    public void updateCondition(long requestId, AlarmConditionStruct condition)
    {
        eventChannelDelegate.updateCondition(requestId, condition);
    }

    public void deleteCondition(long requestId, AlarmConditionStruct condition)
    {
        eventChannelDelegate.deleteCondition(requestId, condition);
    }

    public void createCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        eventChannelDelegate.createCalculation(requestId, alarmCalculationStruct);
    }

    public void publishAllCalculations(long requestId)
    {
        eventChannelDelegate.publishAllCalculations(requestId);
    }

    public void publishCalculationById(long requestId, int calculationId)
    {
        eventChannelDelegate.publishCalculationById(requestId, calculationId);
    }

    public void updateCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        eventChannelDelegate.updateCalculation(requestId, alarmCalculationStruct);
    }

    public void deleteCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        eventChannelDelegate.deleteCalculation(requestId, alarmCalculationStruct);
    }

    public void publishDefinitionById(long requestId, int definitionId)
    {
        eventChannelDelegate.publishDefinitionById(requestId, definitionId);
    }

    public void publishAllDefinitions(long requestId)
    {
        eventChannelDelegate.publishAllDefinitions(requestId);
    }

    public void createDefinition(long requestId, AlarmDefinitionStruct definition)
    {
        eventChannelDelegate.createDefinition(requestId, definition);
    }

    public void updateDefinition(long requestId, AlarmDefinitionStruct definition)
    {
        eventChannelDelegate.updateDefinition(requestId, definition);
    }

    public void deleteDefinition(long requestId, AlarmDefinitionStruct definition)
    {
        eventChannelDelegate.deleteDefinition(requestId, definition);
    }
}
