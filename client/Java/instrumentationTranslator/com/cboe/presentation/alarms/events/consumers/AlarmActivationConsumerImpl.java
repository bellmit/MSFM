//
// ------------------------------------------------------------------------
// FILE: AlarmActivationConsumerImpl.java
// 
// PACKAGE: com.cboe.presentation.alarms.events.consumers
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.alarms.events.consumers;

import com.cboe.idl.alarm.ActivationAssignmentStruct;
import com.cboe.idl.alarm.AlarmActivationStruct;
import com.cboe.interfaces.events.AlarmActivationConsumer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;
import com.cboe.util.ChannelType;

/**
 * @author torresl@cboe.com
 */
public class AlarmActivationConsumerImpl
        extends AlarmConsumerImpl
        implements AlarmActivationConsumer
{

    public AlarmActivationConsumerImpl()
    {
        super();
    }

    public void acceptActivations(long requestId, AlarmActivationStruct[] alarmActivationStructs)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            GUILoggerHome.find().debug("acceptActivations (requestId=" + requestId + ")", GUILoggerINBusinessProperty.ALARM_ACTIVATION, alarmActivationStructs);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_ALARM_ACTIVATIONS, requestId, alarmActivationStructs);
    }

    public void acceptChangedActivation(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            GUILoggerHome.find().debug("acceptChangedActivation (requestId=" + requestId + ")", GUILoggerINBusinessProperty.ALARM_ACTIVATION, alarmActivationStruct);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION, requestId, alarmActivationStruct);
    }

    public void acceptDeleteActivation(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            GUILoggerHome.find().debug("acceptDeleteActivation (requestId=" + requestId + ")", GUILoggerINBusinessProperty.ALARM_ACTIVATION, alarmActivationStruct);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_DELETE_ACTIVATION, requestId, alarmActivationStruct);
    }

    public void acceptNewActivation(long requestId, AlarmActivationStruct alarmActivationStruct)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            GUILoggerHome.find().debug("acceptNewActivation (requestId=" + requestId + ")", GUILoggerINBusinessProperty.ALARM_ACTIVATION, alarmActivationStruct);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_NEW_ACTIVATION, requestId, alarmActivationStruct);
    }

	public void acceptActivationAssignments(long requestId, ActivationAssignmentStruct[] structs)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            GUILoggerHome.find().debug("acceptActivationAssignments (requestId=" + requestId + ")", GUILoggerINBusinessProperty.ALARM_ACTIVATION, structs);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_ALARM_ACTIVATION_ASSIGNMENTS, requestId, structs);
    }

	public void acceptChangedActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            GUILoggerHome.find().debug("acceptChangedActivationAssignment (requestId=" + requestId + ")", GUILoggerINBusinessProperty.ALARM_ACTIVATION, struct);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_CHANGED_ACTIVATION_ASSIGNMENT, requestId, struct);
    }

	public void acceptDeleteActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            GUILoggerHome.find().debug("acceptDeleteActivationAssignment (requestId=" + requestId + ")", GUILoggerINBusinessProperty.ALARM_ACTIVATION, struct);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_DELETE_ACTIVATION_ASSIGNMENT, requestId, struct);
    }

	public void acceptNewActivationAssignment(long requestId, ActivationAssignmentStruct struct)
    {
        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_ACTIVATION))
        {
            GUILoggerHome.find().debug("acceptNewActivationAssignment (requestId=" + requestId + ")", GUILoggerINBusinessProperty.ALARM_ACTIVATION, struct);
        }
        dispatchEvent(ChannelType.IC_ACCEPT_NEW_ACTIVATION_ASSIGNMENT, requestId, struct);
    }
}
