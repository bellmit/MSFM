//
// ------------------------------------------------------------------------
// FILE: AlarmNotificationInfoImpl.java
// 
// PACKAGE: com.cboe.presentation.alarms
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.alarms;

import java.util.*;

import com.cboe.idl.alarmConstants.ConditionTypes;

import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.instrumentation.alarms.AlarmCondition;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotification;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationCondition;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.formatters.ProcessWatcherState;

/**
 * @author torresl@cboe.com
 */
public class AlarmNotificationInfoHistoricalImpl
        extends AlarmNotificationInfoImpl
{
    /**
     * @param alarmNotification
     * @param cleared
     */
    public AlarmNotificationInfoHistoricalImpl(
            AlarmNotification alarmNotification, boolean cleared)
    {
        super(alarmNotification, cleared);
    }
    public String getDisplayCondition()
    {
        if(displayCondition == null)
        {
            StringBuffer buffer = new StringBuffer(200);
            try
            {
                AlarmNotificationImpl notification = (AlarmNotificationImpl)getAlarmNotification();
                AlarmNotificationConditionHistoricalImpl[] conditions = notification.getMatchedConditionsHistorical();

                if(conditions != null)
                {
                    Set<String> set = new HashSet<String>(conditions.length);

                    for(int i = 0; i < conditions.length; i++)
                    {
                        AlarmCondition alarmCond = conditions[i].getCondition();
                        String conditionName ="";
                        if (alarmCond != null)
                        {
                            conditionName= alarmCond.getName();
                        }
                        if(!set.contains(conditionName))
                        {
                            set.add(conditionName);

                            if(i > 0)
                            {
                                buffer.append(" / ");
                            }
                            buffer.append(conditionName);
                        }
                    }
                }
            }
            catch(Exception e)
            {
                DefaultExceptionHandlerHome.find().process(e,
                    "Could not obtain matching conditions for Alarm Notification.");
            }

            displayCondition = buffer.toString();
        }
        return displayCondition;
    }

    /* (non-Javadoc)
     * @see com.cboe.presentation.alarms.AlarmNotificationInfoImpl#calculateConditionTypes()
     */
    protected void calculateConditionTypes()
    {
        if(conditionTypes == null)
        {
            BitSet conditionsVisited = new BitSet(5);
            List<Short> conditionsList = new ArrayList<Short>(5);
            try
            {
                AlarmNotificationImpl notification = (AlarmNotificationImpl)getAlarmNotification();
                AlarmNotificationConditionHistoricalImpl[] notifyConditions = notification.getMatchedConditionsHistorical();

                if(notifyConditions != null)
                {
                    for(int i = 0; i < notifyConditions.length; i++)
                    {
                        AlarmNotificationCondition alarmNotificationCondition = notifyConditions[i];
                        AlarmCondition alarmCond = alarmNotificationCondition.getCondition();
                        if (alarmCond!=null)
                        {
                            short conditionType = alarmCond.getConditionType();
                            if(!conditionsVisited.get(conditionType))
                            {
                                conditionsList.add(new Short(conditionType));
                                conditionsVisited.set(conditionType);
                            }
                        }
                    }
                }
            }
            catch(NotFoundException e)
            {
                DefaultExceptionHandlerHome.find().process(e,
                    "Could not obtain matching conditions for Alarm Notification.");
            }
            conditionTypes = conditionsList.toArray(new Short[conditionsList.size()]);
        }
    }
    
    /* (non-Javadoc)
     * @see com.cboe.interfaces.instrumentation.alarms.AlarmNotificationInfo#getDisplayValue()
     */
    public String getDisplayValue()
    {
        if(displayValue == null)
        {
            StringBuffer buffer = new StringBuffer(200);
            try
            {
                AlarmNotificationImpl notification = (AlarmNotificationImpl)getAlarmNotification();
                AlarmNotificationConditionHistoricalImpl[] conditions = notification.getMatchedConditionsHistorical();

                if(conditions != null)
                {
                    for(int i = 0; i < conditions.length; i++)
                    {
                        if(i > 0)
                        {
                            buffer.append(" / ");
                        }
                        String value = conditions[i].getThresholdTripValue();
                        try
                        {
                            int integer = Integer.parseInt(value);
                            AlarmCondition alarmCond = conditions[i].getCondition();
                            if (alarmCond!=null && alarmCond.getConditionType() 
                                    == ConditionTypes.PROCESS_WATCHER)
                            {
                                value = ProcessWatcherState.toString(integer);    
                            }
                            else
                            {
                                value = numberFormatter.format(integer);
                            }
                        }
                        catch (NumberFormatException e)
                        {
                            //don't do anything, original value will be used
                        }
                        buffer.append(value);
                    }
                }
            }
            catch(NotFoundException e)
            {
                DefaultExceptionHandlerHome.find().process(e,
                                                           "Could not obtain matching conditions for Alarm Notification.");
            }

            displayValue = buffer.toString();
        }
        return displayValue;
    }
}
