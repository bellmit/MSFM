//
// ------------------------------------------------------------------------
// FILE: AlarmActivationConsumerProxyImpl.java
// 
// PACKAGE: com.cboe.consumers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.AlarmActivationConsumer;
import com.cboe.idl.alarm.ActivationAssignmentStruct;
import com.cboe.idl.alarm.AlarmActivationStruct;
import com.cboe.idl.alarmEvents.AlarmActivationEventConsumerPOA;
import com.cboe.exceptions.ExceptionDetails;
import org.omg.CORBA.Any;
import org.omg.CosEventComm.Disconnected;

/**
 * Listen for Alarm Activation Events and pass them on to the delegate.
 *
 * @author torresl@cboe.com
 */
public class AlarmActivationConsumerProxyImpl
        extends AlarmActivationEventConsumerPOA
        implements AlarmActivationConsumer
{

    protected AlarmActivationConsumer delegate;

    public AlarmActivationConsumerProxyImpl(AlarmActivationConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void acceptActivations(long requestId, AlarmActivationStruct[] alarmActivationStructs)
    {
        delegate.acceptActivations(requestId, alarmActivationStructs);
    }

    public void acceptChangedActivation(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
        delegate.acceptChangedActivation(requestId, alarmActivationStruct);
    }

    public void acceptDeleteActivation(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
        delegate.acceptDeleteActivation(requestId, alarmActivationStruct);
    }

    public void acceptNewActivation(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
        delegate.acceptNewActivation(requestId, alarmActivationStruct);
    }

	public void acceptActivationAssignments(long requestId, ActivationAssignmentStruct[] structs)
    {
	    delegate.acceptActivationAssignments(requestId, structs);
    }

	public void acceptNewActivationAssignment(long requestId, ActivationAssignmentStruct struct)
	{
		delegate.acceptNewActivationAssignment(requestId, struct);
	}
	
	public void acceptChangedActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
	    delegate.acceptChangedActivationAssignment(requestId, struct);
    }

	public void acceptDeleteActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
	    delegate.acceptDeleteActivationAssignment(requestId, struct);
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


    public void disconnect_push_consumer()
    {

    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(Any any) throws Disconnected
    {

    }
}
