package com.cboe.presentation.alarms;

import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.alarm.ActivationAssignmentStruct;
import com.cboe.interfaces.instrumentation.alarms.ActivationAssignment;
import com.cboe.interfaces.instrumentation.alarms.AlarmActivation;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

public class ActivationAssignmentFactory
{

	private ActivationAssignmentFactory()
	{
	}

	/**
	 * Creates a new activation assignment business object from a struct.
	 * 
	 * @param struct
	 * @return {@link ActivationAssignment}
	 */
	public static ActivationAssignment newActivationAssignment(ActivationAssignmentStruct struct)
	{
		AlarmActivation activation = null;
		try
		{
			activation = InstrumentationTranslatorFactory.find().getAlarmActivationById(struct.activation.activationId);
		}
		catch (NotFoundException e)
		{
			GUILoggerHome.find().exception(
			        "Could not find Activation with id:" + struct.activation.activationId + " for Assignment with id:" + struct.assignmentId, e);
		}
		catch (SystemException e)
		{
			GUILoggerHome.find().exception(
			        "Could not find Activation with id:" + struct.activation.activationId + " for Assignment with id:" + struct.assignmentId, e);
		}
		catch (CommunicationException e)
		{
			GUILoggerHome.find().exception(
			        "Could not find Activation with id:" + struct.activation.activationId + " for Assignment with id:" + struct.assignmentId, e);
		}
		return new ActivationAssignmentImpl(struct.assignmentId, activation, struct.processId);
	}

	public static ActivationAssignmentStruct getStruct(ActivationAssignment assignment)
    {
	    return new ActivationAssignmentStruct(assignment.getId(), assignment.getAlarmActivation().getStruct(),assignment.getLogicalName());
    }
}
