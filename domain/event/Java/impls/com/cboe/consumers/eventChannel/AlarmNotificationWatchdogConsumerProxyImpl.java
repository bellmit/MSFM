/*
 * Created on Oct 28, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.consumers.eventChannel;

import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CosEventComm.Disconnected;

import com.cboe.exceptions.ExceptionDetails;
import com.cboe.idl.alarm.AlarmNotificationWatchdogStruct;
import com.cboe.idl.alarmEvents.AlarmNotificationWatchdogEventConsumerPOA;
import com.cboe.interfaces.events.AlarmNotificationWatchdogConsumer;

public class AlarmNotificationWatchdogConsumerProxyImpl extends AlarmNotificationWatchdogEventConsumerPOA implements
        AlarmNotificationWatchdogConsumer
{
    private AlarmNotificationWatchdogConsumer delegate;
    
    public AlarmNotificationWatchdogConsumerProxyImpl(AlarmNotificationWatchdogConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }
    
    public void acceptWatchdogs(long id, AlarmNotificationWatchdogStruct[] watchdogs)
    {
        delegate.acceptWatchdogs(id,watchdogs);
    }

    public void acceptNewWatchdog(long id, AlarmNotificationWatchdogStruct watchdog)
    {
        delegate.acceptNewWatchdog(id,watchdog);
    }

    public void acceptChangedWatchdog(long id, AlarmNotificationWatchdogStruct watchdog)
    {
        delegate.acceptChangedWatchdog(id,watchdog);
    }

    public void acceptDeleteWatchdog(long id, AlarmNotificationWatchdogStruct watchdog)
    {
        delegate.acceptDeleteWatchdog(id,watchdog);
    }

    public void acceptAlreadyExistsException(long id, ExceptionDetails exception)
    {
        delegate.acceptAlreadyExistsException(id,exception);
    }

    public void acceptDataValidationException(long id, ExceptionDetails exception)
    {
        delegate.acceptDataValidationException(id,exception);
    }

    public void acceptNotFoundException(long id, ExceptionDetails exception)
    {
        delegate.acceptNotFoundException(id,exception);
    }

    public void acceptNotAcceptedException(long id, ExceptionDetails exception)
    {
        delegate.acceptNotAcceptedException(id,exception);
    }

    public void acceptSystemException(long id, ExceptionDetails exception)
    {
        delegate.acceptSystemException(id,exception);
    }

    public void acceptTransactionFailedException(long id, ExceptionDetails exception)
    {
        delegate.acceptTransactionFailedException(id,exception);
    }


    public Object get_typed_consumer()
    {
        return null;
    }

    public void push(Any arg0) throws Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }

}
