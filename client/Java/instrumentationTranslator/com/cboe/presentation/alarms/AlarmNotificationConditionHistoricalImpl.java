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
import com.cboe.interfaces.instrumentation.alarms.AlarmCondition;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

public class AlarmNotificationConditionHistoricalImpl extends AlarmNotificationConditionImpl
{
    public AlarmNotificationConditionHistoricalImpl(AlarmNotificationConditionStruct conditionStruct)
    {
        super(conditionStruct);
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
                GUILoggerHome.find().alarm(
                        "Could not load condition for notification. conditionId: ["
                        +struct.conditionId+"]");
            }
        }
        return cachedCondition;
    }

}
