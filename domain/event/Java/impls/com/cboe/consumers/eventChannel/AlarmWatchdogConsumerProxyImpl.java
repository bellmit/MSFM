//
// -----------------------------------------------------------------------------------
// Source file: AlarmWatchdogConsumerProxyImpl.java
//
// PACKAGE: com.cboe.consumers.eventChannel
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.eventChannel;

import org.omg.CORBA.*;
import org.omg.CosEventComm.Disconnected;

import com.cboe.idl.alarmEvents.AlarmNotificationWatchdogEventConsumerPOA;
import com.cboe.idl.alarm.AlarmNotificationWatchdogStruct;

import com.cboe.exceptions.ExceptionDetails;

import com.cboe.interfaces.events.AlarmNotificationWatchdogConsumer;

public class AlarmWatchdogConsumerProxyImpl
        extends AlarmNotificationWatchdogEventConsumerPOA
        implements AlarmNotificationWatchdogConsumer
{
    protected AlarmNotificationWatchdogConsumer delegate;

    public AlarmWatchdogConsumerProxyImpl(AlarmNotificationWatchdogConsumer delegate)
    {
        this.delegate = delegate;
    }

    public void disconnect_push_consumer()
    {}

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(Any any)
            throws Disconnected
    {}

    public void acceptWatchdogs(long requestId, AlarmNotificationWatchdogStruct[] alarmNotificationWatchdogStructs)
    {
        delegate.acceptWatchdogs(requestId, alarmNotificationWatchdogStructs);
    }

    public void acceptChangedWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        delegate.acceptChangedWatchdog(requestId, alarmNotificationWatchdogStruct);
    }

    public void acceptDeleteWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        delegate.acceptDeleteWatchdog(requestId, alarmNotificationWatchdogStruct);
    }

    public void acceptNewWatchdog(long requestId, AlarmNotificationWatchdogStruct alarmNotificationWatchdogStruct)
    {
        delegate.acceptNewWatchdog(requestId, alarmNotificationWatchdogStruct);
    }

    public void acceptAlreadyExistsException(long requestId, ExceptionDetails exceptionDetails)
    {
        delegate.acceptAlreadyExistsException(requestId, exceptionDetails);
    }

    public void acceptDataValidationException(long requestId, ExceptionDetails exceptionDetails)
    {
        delegate.acceptDataValidationException(requestId, exceptionDetails);
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
}
