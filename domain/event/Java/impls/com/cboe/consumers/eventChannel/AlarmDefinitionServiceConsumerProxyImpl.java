//
// ------------------------------------------------------------------------
// FILE: AlarmDefinitionServiceConsumerProxyImpl.java
// 
// PACKAGE: com.cboe.consumers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.consumers.eventChannel;

import com.cboe.idl.alarm.AlarmConditionStruct;
import com.cboe.idl.alarm.AlarmDefinitionStruct;
import com.cboe.idl.alarm.AlarmCalculationStruct;
import com.cboe.idl.alarmEvents.AlarmDefinitionEventServicePOA;
import com.cboe.interfaces.events.AlarmDefinitionServiceConsumer;

/**
 * @author torresl@cboe.com
 */
public class AlarmDefinitionServiceConsumerProxyImpl
        extends AlarmDefinitionEventServicePOA
        implements AlarmDefinitionServiceConsumer
{
    // extend the POA object
    protected AlarmDefinitionServiceConsumer delegate;

    public AlarmDefinitionServiceConsumerProxyImpl(AlarmDefinitionServiceConsumer delegate)
    {
        this.delegate = delegate;
    }

    public void publishConditionById(long requestId, int conditionId)
    {
        delegate.publishConditionById(requestId, conditionId);
    }

    public void publishAllConditions(long requestId)
    {
        delegate.publishAllConditions(requestId);
    }

    public void createCondition(long requestId, AlarmConditionStruct condition)
    {
        delegate.createCondition(requestId, condition);
    }

    public void updateCondition(long requestId, AlarmConditionStruct condition)
    {
        delegate.updateCondition(requestId, condition);
    }

    public void deleteCondition(long requestId, AlarmConditionStruct condition)
    {
        delegate.deleteCondition(requestId, condition);
    }

    public void createCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        delegate.createCalculation(requestId, alarmCalculationStruct);
    }

    public void publishAllCalculations(long requestId)
    {
        delegate.publishAllCalculations(requestId);
    }

    public void publishCalculationById(long requestId, int calculationId)
    {
        delegate.publishCalculationById(requestId, calculationId);
    }

    public void updateCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        delegate.updateCalculation(requestId, alarmCalculationStruct);
    }

    public void deleteCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        delegate.deleteCalculation(requestId, alarmCalculationStruct);
    }

    public void publishDefinitionById(long requestId, int definitionId)
    {
        delegate.publishDefinitionById(requestId, definitionId);
    }

    public void publishAllDefinitions(long requestId)
    {
        delegate.publishAllDefinitions(requestId);
    }

    public void createDefinition(long requestId, AlarmDefinitionStruct definition)
    {
        delegate.createDefinition(requestId, definition);
    }

    public void updateDefinition(long requestId, AlarmDefinitionStruct definition)
    {
        delegate.updateDefinition(requestId, definition);
    }

    public void deleteDefinition(long requestId, AlarmDefinitionStruct definition)
    {
        delegate.deleteDefinition(requestId, definition);
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
            throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
