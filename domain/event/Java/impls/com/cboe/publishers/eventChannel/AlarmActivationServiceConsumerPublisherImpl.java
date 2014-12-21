//
// ------------------------------------------------------------------------
// FILE: AlarmActivationServiceConsumerPublisherImpl.java
// 
// PACKAGE: com.cboe.publishers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.publishers.eventChannel;

import com.cboe.idl.alarm.ActivationAssignmentStruct;
import com.cboe.idl.alarm.AlarmActivationStruct;
import com.cboe.idl.alarmEvents.AlarmActivationEventService;

import com.cboe.interfaces.events.AlarmActivationEventDelegateServiceConsumer;

/**
 * @author torresl@cboe.com
 */
public class AlarmActivationServiceConsumerPublisherImpl
        implements AlarmActivationEventDelegateServiceConsumer
{
    protected AlarmActivationEventService eventChannelDelegate;

    public AlarmActivationServiceConsumerPublisherImpl()
    {
        super();
    }

    public AlarmActivationServiceConsumerPublisherImpl(AlarmActivationEventService eventChannelDelegate)
    {
        this();
        setAlarmActivationEventServiceDelegate(eventChannelDelegate);
    }

    public void setAlarmActivationEventServiceDelegate(AlarmActivationEventService eventChannelDelegate)
    {
        this.eventChannelDelegate = eventChannelDelegate;
    }

    public void publishActivationById(long requestId, int activationId)
    {
        eventChannelDelegate.publishActivationById(requestId, activationId);
    }

    public void publishAllActivations(long requestId)
    {
        eventChannelDelegate.publishAllActivations(requestId);
    }

    public void createActivation(long requestId, AlarmActivationStruct activation)
    {
        eventChannelDelegate.createActivation(requestId, activation);
    }

    public void updateActivation(long requestId, AlarmActivationStruct activation)
    {
        eventChannelDelegate.updateActivation(requestId, activation);
    }

    public void deleteActivation(long requestId, AlarmActivationStruct activation)
    {
        eventChannelDelegate.deleteActivation(requestId, activation);
    }

    public void activate(long requestId, AlarmActivationStruct activation)
    {
        eventChannelDelegate.activate(requestId, activation);
    }

    public void deactivate(long requestId, AlarmActivationStruct activation)
    {
        eventChannelDelegate.deactivate(requestId, activation);
    }

	public void createActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
	    eventChannelDelegate.createActivationAssignment(requestId, struct);
    }

	public void deleteActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
		eventChannelDelegate.deleteActivationAssignment(requestId, struct);
    }

	public void publishActivationAssignmentById(long requestId, int assignmentId)
    {
	    eventChannelDelegate.publishActivationAssignmentById(requestId, assignmentId);
    }

	public void publishAllActivationAssignments(long requestId)
    {
	    eventChannelDelegate.publishAllActivationAssignments(requestId);
    }

	public void updateActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
		eventChannelDelegate.updateActivationAssignment(requestId, struct);
    }
}
