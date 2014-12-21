//
// ------------------------------------------------------------------------
// FILE: AlarmActivationServiceConsumerProxyImpl.java
// 
// PACKAGE: com.cboe.consumers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.AlarmActivationServiceConsumer;
import com.cboe.idl.alarm.ActivationAssignmentStruct;
import com.cboe.idl.alarm.AlarmActivationStruct;
import com.cboe.idl.alarmEvents.AlarmActivationEventServicePOA;

/**
 * @author torresl@cboe.com
 */
public class AlarmActivationServiceConsumerProxyImpl
        extends AlarmActivationEventServicePOA
        implements AlarmActivationServiceConsumer
{
    protected AlarmActivationServiceConsumer delegate;

    public AlarmActivationServiceConsumerProxyImpl(AlarmActivationServiceConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void activate(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
        delegate.activate(requestId, alarmActivationStruct);
    }

    public void deactivate(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
        delegate.deactivate(requestId, alarmActivationStruct);
    }

    public void createActivation(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
     delegate.createActivation(requestId, alarmActivationStruct);
    }

    public void deleteActivation(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
        delegate.deleteActivation(requestId, alarmActivationStruct);
    }

    public void publishActivationById(long requestId, int activationId)
    {
        delegate.publishActivationById(requestId, activationId);
    }

    public void publishAllActivations(long requestId)
    {
        delegate.publishAllActivations(requestId);
    }

    public void updateActivation(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
        delegate.updateActivation(requestId, alarmActivationStruct);
    }

	public void createActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
	    delegate.createActivationAssignment(requestId, struct);
    }

	public void publishActivationAssignmentById(long requestId, int assignmentId)
	{
		delegate.publishActivationAssignmentById(requestId, assignmentId);
	}
	
	public void publishAllActivationAssignments(long requestId)
	{
		delegate.publishAllActivationAssignments(requestId);
	}
	
	public void updateActivationAssignment(long requestId, ActivationAssignmentStruct struct)
	{
		delegate.updateActivationAssignment(requestId, struct);
	}
	
	public void deleteActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
		delegate.deleteActivationAssignment(requestId, struct);
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
