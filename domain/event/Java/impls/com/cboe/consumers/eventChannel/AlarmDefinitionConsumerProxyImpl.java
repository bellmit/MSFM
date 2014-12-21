//
// ------------------------------------------------------------------------
// FILE: AlarmDefinitionConsumerProxyImpl.java
// 
// PACKAGE: com.cboe.consumers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.consumers.eventChannel;

import com.cboe.exceptions.ExceptionDetails;
import com.cboe.idl.alarm.AlarmConditionStruct;
import com.cboe.idl.alarm.AlarmDefinitionStruct;
import com.cboe.idl.alarm.AlarmCalculationStruct;
import com.cboe.idl.alarmEvents.AlarmDefinitionEventConsumerPOA;
import com.cboe.interfaces.events.AlarmDefinitionConsumer;

/**
 * Listen for Alarm Definition Events and pass them on to a delegate.
 *
 * @author torresl@cboe.com
 */
public class AlarmDefinitionConsumerProxyImpl
        extends AlarmDefinitionEventConsumerPOA
        implements AlarmDefinitionConsumer
{
    // extend the POA object

    protected AlarmDefinitionConsumer delegate;

    public AlarmDefinitionConsumerProxyImpl(AlarmDefinitionConsumer delegate)
    {
        this.delegate = delegate;
    }

    public void acceptConditions(long requestId, AlarmConditionStruct[] alarmConditionStructs)
    {
        delegate.acceptConditions(requestId, alarmConditionStructs);
    }

    public void acceptCalculations(long requestId, AlarmCalculationStruct[] alarmCalculationStructs)
    {
        delegate.acceptCalculations(requestId, alarmCalculationStructs);
    }

    public void acceptDefinitions(long requestId, AlarmDefinitionStruct[] alarmDefinitionStructs)
    {
        delegate.acceptDefinitions(requestId, alarmDefinitionStructs);
    }

    public void acceptChangedCondition(long requestId, AlarmConditionStruct alarmConditionStruct)
    {
        delegate.acceptChangedCondition(requestId, alarmConditionStruct);
    }

    public void acceptChangedCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        delegate.acceptChangedCalculation(requestId, alarmCalculationStruct);
    }

    public void acceptChangedDefinition(long requestId, AlarmDefinitionStruct alarmDefinitionStruct)
    {
        delegate.acceptChangedDefinition(requestId, alarmDefinitionStruct);
    }

    public void acceptDeleteCondition(long requestId, AlarmConditionStruct alarmConditionStruct)
    {
        delegate.acceptDeleteCondition(requestId, alarmConditionStruct);
    }

    public void acceptDeleteCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        delegate.acceptDeleteCalculation(requestId, alarmCalculationStruct);
    }

    public void acceptDeleteDefinition(long requestId, AlarmDefinitionStruct alarmDefinitionStruct)
    {
        delegate.acceptDeleteDefinition(requestId, alarmDefinitionStruct);
    }

    public void acceptNewCondition(long requestId, AlarmConditionStruct alarmConditionStruct)
    {
        delegate.acceptNewCondition(requestId, alarmConditionStruct);
    }

    public void acceptNewCalculation(long requestId, AlarmCalculationStruct alarmCalculationStruct)
    {
        delegate.acceptNewCalculation(requestId, alarmCalculationStruct);
    }

    public void acceptNewDefinition(long requestId, AlarmDefinitionStruct alarmDefinitionStruct)
    {
        delegate.acceptNewDefinition(requestId, alarmDefinitionStruct);
    }

    public void acceptNotAcceptedException(long requestId, ExceptionDetails exceptionDetails)
    {
        delegate.acceptNotAcceptedException(requestId, exceptionDetails);
    }

    public void acceptNotFoundException(long requestId, ExceptionDetails exceptionDetails)
    {
        delegate.acceptNotFoundException(requestId, exceptionDetails);
    }

    public void acceptSystemException(long requestId, ExceptionDetails exceptionDetails)
    {
        delegate.acceptSystemException(requestId, exceptionDetails);
    }

    public void acceptTransactionFailedException(long requestId, ExceptionDetails exceptionDetails)
    {
        delegate.acceptTransactionFailedException(requestId, exceptionDetails);
    }

    public void acceptAlreadyExistsException(long requestId, ExceptionDetails exceptionDetails)
    {
        delegate.acceptAlreadyExistsException(requestId, exceptionDetails);
    }

    public void acceptDataValidationException(long requestId, ExceptionDetails exceptionDetails)
    {
        delegate.acceptDataValidationException(requestId, exceptionDetails);
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
