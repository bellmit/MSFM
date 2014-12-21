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

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.cboe.idl.alarmConstants.ConditionTypes;

import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.instrumentation.alarms.AlarmCondition;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotification;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationCondition;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationInfo;
import com.cboe.interfaces.instrumentation.common.formatters.ConditionTypesFormatStrategy;
import com.cboe.interfaces.instrumentationCollector.processWatcher.ProcessWatcherEvent;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.formatters.InstrumentationFormatFactory;
import com.cboe.presentation.common.formatters.ProcessWatcherState;
import com.cboe.presentation.common.formatters.PWEventCodesFormatter;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;

/**
 * @author torresl@cboe.com
 */
public class AlarmNotificationInfoImpl
        extends AbstractMutableBusinessModel
        implements AlarmNotificationInfo
{
    protected static final NumberFormat numberFormatter = NumberFormat.getIntegerInstance();

    private AlarmNotification alarmNotification;
    private Boolean cleared;
    protected String displayCondition;
    protected String displayValue;
    private String displaySubjectName;
    private String displayContextName;
    private Short severity;
    private Short notificationType;
    private String displayConditionType;
    private String[] subjectNames;
    protected Short[] conditionTypes;

    private Boolean snoozed;
    private boolean visualExpirationNoticeSent;


    public AlarmNotificationInfoImpl(AlarmNotification alarmNotification, boolean cleared)
    {
        super();
        this.alarmNotification = alarmNotification;
        this.cleared = new Boolean(cleared);
        initialize();
    }
    protected AlarmNotificationInfoImpl()
    {
        super();
        initialize();
    }
    public Object clone() throws CloneNotSupportedException
    {
        AlarmNotificationInfoImpl impl = new AlarmNotificationInfoImpl();
        impl.alarmNotification = alarmNotification;
        impl.cleared = cleared;
        impl.displayCondition = displayCondition;
        impl.displayValue = displayValue;
        impl.displaySubjectName = displaySubjectName;
        impl.displayContextName = displayContextName;
        impl.severity = severity;
        impl.notificationType = notificationType;
        impl.displayConditionType = displayConditionType;
        impl.subjectNames = subjectNames;
        impl.conditionTypes = conditionTypes;
        impl.visualExpirationNoticeSent = visualExpirationNoticeSent;
        impl.snoozed = snoozed;
        return impl;
    }
    private void initialize()
    {
        this.displayCondition = null;
        this.displayValue = null;
        this.displaySubjectName = null;
        this.displayContextName = null;
        this.severity = null;
        this.notificationType = null;
        this.displayConditionType = null;
        this.subjectNames = null;
        this.conditionTypes = null;
        visualExpirationNoticeSent = false;
        this.snoozed= null;
    }
    public AlarmNotification getAlarmNotification()
    {
        return alarmNotification;
    }

    public void setAlarmNotification(AlarmNotification alarmNotification)
    {
        AlarmNotification oldNotification = this.alarmNotification;
        this.alarmNotification = alarmNotification;
        initialize();
        setModified(true);
        firePropertyChange(NOTIFICATION_PROPERTY, oldNotification, alarmNotification);
    }

    public boolean isVisualExpirationNoticeSent()
    {
        return visualExpirationNoticeSent;
    }

    public void setVisualExpirationNoticeSent(boolean sent)
    {
        visualExpirationNoticeSent = sent;
    }

    public boolean isCleared()
    {
        return cleared.booleanValue();
    }

    public void setCleared(boolean cleared)
    {
        Boolean oldValue = this.cleared;
        this.cleared = new Boolean(cleared);
        setModified(true);
        firePropertyChange(CLEARED_PROPERTY, oldValue, this.cleared);
    }

    public Boolean getCleared()
    {
        return cleared;
    }

    public String[] getSubjectNames()
    {
        if (subjectNames == null)
        {
            calculateSubjectNames();
        }
        return subjectNames;
    }

    synchronized private void calculateSubjectNames()
    {
        if (subjectNames == null)
        {
            ArrayList<String> list = new ArrayList<String>();
            try
            {
                AlarmNotificationCondition[] notifyConditions = alarmNotification.getMatchedConditions();

                if (notifyConditions != null)
                {
                    list.ensureCapacity(notifyConditions.length);
                	
                    for (int i = 0; i < notifyConditions.length; i++)
                    {
                        AlarmNotificationCondition alarmNotificationCondition = notifyConditions[i];
                        if (alarmNotificationCondition != null) {
                                if (!list.contains(alarmNotificationCondition.getSubjectNameTripValue()))
                                {
                                    list.add(alarmNotificationCondition.getSubjectNameTripValue());
	                        }
                        }
                    }
                }
            }
            catch (NotFoundException e)
            {
                DefaultExceptionHandlerHome.find().process(e,
                                                           "Could not obtain matching conditions for Alarm Notification.");
            }
            subjectNames = list.toArray(new String[list.size()]);
        }
    }

    protected void calculateConditionTypes()
    {
        if(conditionTypes == null)
        {
            BitSet conditionsVisited = new BitSet(5);
            List<Short> conditionsList = new ArrayList<Short>(5);
            try
            {
                AlarmNotificationCondition[] notifyConditions = alarmNotification.getMatchedConditions();

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

    public String getDisplaySubjectName()
    {
        if(displaySubjectName == null)
        {
            calculateSubjectNames();
            if(subjectNames != null && subjectNames.length > 0)
            {
                StringBuffer buffer = new StringBuffer(100);
                for(int i = 0; i < subjectNames.length; i++)
                {
                    if(i > 0)
                    {
                        buffer.append(" / ");
                    }

                    String subjectName = subjectNames[i];
                    buffer.append(subjectName);
                }
                displaySubjectName = buffer.toString();
            }
            else
            {
                displaySubjectName = "";
            }
        }
        return displaySubjectName;
    }

    public String getDisplayContextName()
    {
        if(displayContextName == null)
        {
            StringBuffer buffer = new StringBuffer(200);

            try
            {
                AlarmNotificationCondition[] notifyConditions = alarmNotification.getMatchedConditions();

                if(notifyConditions != null)
                {
                    Set<String> set = new HashSet<String>(notifyConditions.length);

                    for(int i = 0; i < notifyConditions.length; i++)
                    {
                        AlarmNotificationCondition alarmNotificationCondition = notifyConditions[i];
                        String contextNameTripValue = alarmNotificationCondition.getContextNameTripValue();
                        if(!set.contains(contextNameTripValue))
                        {
                            set.add(contextNameTripValue);

                            if(i > 0)
                            {
                                buffer.append("; ");
                            }

                            buffer.append(contextNameTripValue);
                        }
                    }
                }
            }
            catch(NotFoundException e)
            {
                DefaultExceptionHandlerHome.find().process(e,
                                                           "Could not obtain matching conditions for Alarm Notification.");
            }

            displayContextName = buffer.toString();
        }
        return displayContextName;
    }

    public Short getSeverity()
    {
        if (severity == null)
        {
            severity = new Short(alarmNotification.getAlarmDefinition().getSeverity());
        }
        return severity;
    }

    public Short getNotificationType()
    {
        if (notificationType == null)
        {
            notificationType = new Short(alarmNotification.getAlarmActivation().getNotificationType());
        }
        return notificationType;
    }

    public String getDisplayCondition()
    {
        if(displayCondition == null)
        {
            StringBuffer buffer = new StringBuffer(200);
            try
            {
                AlarmNotificationCondition[] conditions = alarmNotification.getMatchedConditions();

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
            catch(NotFoundException e)
            {
                DefaultExceptionHandlerHome.find().process(e,
                                                           "Could not obtain matching conditions for Alarm Notification.");
            }

            displayCondition = buffer.toString();
        }
        return displayCondition;
    }

    public String getDisplayValue()
    {
        if(displayValue == null)
        {
            StringBuffer buffer = new StringBuffer(200);
            try
            {
                AlarmNotificationCondition[] conditions = alarmNotification.getMatchedConditions();

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
                            AlarmCondition alarmCond = conditions[i].getCondition();
                            if (alarmCond!=null && alarmCond.getConditionType() 
                                    == ConditionTypes.PROCESS_WATCHER)
                            {
                                if(alarmCond.getFieldName().equals(ProcessWatcherEvent.REASON_CODE_FIELD_NAME))
                                {
                                    short shortVal = Short.parseShort(value);
                                    value = PWEventCodesFormatter.toString(shortVal);
                                }
                                else
                                {
                                    int integer = Integer.parseInt(value);
                                    value = ProcessWatcherState.toString(integer);
                                }
                            }
                            else
                            {
                                int integer = Integer.parseInt(value);
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

    public String getDisplayConditionType()
    {
        if(displayConditionType == null)
        {
            calculateConditionTypes();
            if(conditionTypes != null && conditionTypes.length > 0)
            {
                ConditionTypesFormatStrategy conditionFormatter =
                        InstrumentationFormatFactory.getConditionTypesFormatStrategy();
                StringBuffer buffer = new StringBuffer(200);

                for(int i = 0; i < conditionTypes.length; i++)
                {
                    if(i > 0)
                    {
                        buffer.append(" / ");
                    }

                    Short condition = conditionTypes[i];
                    String formattedConditionType = conditionFormatter.format(condition.shortValue(),
                                                                              ConditionTypesFormatStrategy.FULL_INFO_NAME);
                    buffer.append(formattedConditionType);
                }
                displayConditionType = buffer.toString();
            }
            else
            {
                displayConditionType = "";
            }
        }
        return displayConditionType;
    }

    public Short[] getConditionTypes()
    {
        if (conditionTypes == null)
        {
            calculateConditionTypes();
        }
        return conditionTypes;
    }
    public void resetDisplayValue()
    {
        displayValue=null;
    }

    public void setSnoozed(boolean snoozed)
    {
        Boolean oldValue = this.snoozed;
        this.snoozed= new Boolean(snoozed);

        if(snoozed)
        {
            InstrumentationTranslatorFactory.find().snoozeAlarm(alarmNotification.getAlarmActivation(), TimeUnit.MINUTES.toMillis(10));
        }
        else
        {
            InstrumentationTranslatorFactory.find().snoozeAlarm(alarmNotification.getAlarmActivation(), 0);
        }

        setModified(true);
        firePropertyChange(SNOOZED_PROPERTY, oldValue, this.snoozed);
    }

    public Boolean getSnoozed()
    {
        return snoozed;
    }

    public boolean isSnoozed()
    {
       return snoozed.booleanValue();
    }

}
