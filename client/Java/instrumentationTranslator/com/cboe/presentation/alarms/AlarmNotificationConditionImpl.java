//
// -----------------------------------------------------------------------------------
// Source file: AlarmNotificationConditionImpl.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import org.omg.CORBA.UserException;

import com.cboe.idl.alarm.AlarmNotificationConditionStruct;

import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationCondition;
import com.cboe.interfaces.instrumentation.alarms.AlarmCondition;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotification;

import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;

public class AlarmNotificationConditionImpl extends AbstractBusinessModel implements AlarmNotificationCondition
{
    protected AlarmNotificationConditionStruct struct;
    protected AlarmCondition cachedCondition;

    public AlarmNotificationConditionImpl()
    {
        this(new AlarmNotificationConditionStruct());
    }

    public AlarmNotificationConditionImpl(AlarmNotificationConditionStruct struct)
    {
        super();
        checkParam(struct, "AlarmNotificationConditionStruct");
        this.struct = struct;
        cachedCondition = null;
    }

    public AlarmNotificationConditionImpl(AlarmNotificationConditionStruct conditionStruct,
                                          AlarmNotification notification)
    {
        this(conditionStruct);
        cachedCondition = notification.getAlarmDefinition().getConditionById(conditionStruct.conditionId);
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            isEqual = obj instanceof AlarmNotificationCondition;
            if(isEqual)
            {
                AlarmNotificationCondition castedObj = (AlarmNotificationCondition) obj;
                isEqual = (getCondition().equals(castedObj.getCondition()) &&
                           getThresholdTripValue().equals(castedObj.getThresholdTripValue()) &&
                           getSubjectNameTripValue().equals(castedObj.getSubjectNameTripValue()) &&
                           getContextNameTripValue().equals(castedObj.getContextNameTripValue()));
            }
        }

        return isEqual;
    }

    public int hashCode()
    {
        return getCondition().hashCode() + getThresholdTripValue().hashCode();
    }

    public Object clone()
    {
        AlarmNotificationConditionStruct newStruct = new AlarmNotificationConditionStruct();
        newStruct.conditionId = getCondition().getId().intValue();
        newStruct.tripValue = getThresholdTripValue();
        newStruct.tripContextName = getContextNameTripValue();
        newStruct.tripSubjectName = getSubjectNameTripValue();

        return new AlarmNotificationConditionImpl(newStruct);
    }
    
    public AlarmCondition getCondition()
    {
        if(cachedCondition == null)
        {
            try
            {
                cachedCondition = InstrumentationTranslatorFactory.find().getAlarmConditionById(struct.conditionId);
            }
            catch(UserException e)
            {
                //DefaultExceptionHandlerHome.find().process(e, "Could not load condition for notification.");
                GUILoggerHome.find().exception(e, "Could not load condition for notification.");
            }
        }
        return cachedCondition;
    }

    public String getThresholdTripValue()
    {
        return struct.tripValue;
    }

    public String getContextNameTripValue()
    {
        return struct.tripContextName;
    }

    public String getSubjectNameTripValue()
    {
        return struct.tripSubjectName;
    }
}
