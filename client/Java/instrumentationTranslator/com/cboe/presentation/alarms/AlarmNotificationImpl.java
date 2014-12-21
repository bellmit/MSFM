//
// -----------------------------------------------------------------------------------
// Source file: AlarmNotificationImpl.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import java.util.Arrays;
import java.util.Comparator;

import org.omg.CORBA.UserException;

import com.cboe.idl.alarm.AlarmNotificationStruct;
import com.cboe.idl.alarm.AlarmNotificationConditionStruct;

import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.instrumentation.alarms.AlarmNotification;
import com.cboe.interfaces.instrumentation.alarms.AlarmActivation;
import com.cboe.interfaces.instrumentation.alarms.AlarmDefinition;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationCondition;
import com.cboe.interfaces.domain.dateTime.DateTime;

import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;

public class AlarmNotificationImpl extends AbstractBusinessModel implements AlarmNotification
{
    private AlarmNotificationStruct struct;
    private AlarmActivation cachedAlarmActivation;
    private AlarmDefinition cachedAlarmDefinition;
    private AlarmNotificationCondition[] cachedAlarmNotificationConditions;
    private AlarmNotificationConditionHistoricalImpl[] cachedAlarmNotificationConditionsHistorical;
    private DateTime cachedDateTime;
    private Integer cachedId;
    private boolean isAlarmDefinitionFromTranslator;
    private boolean isAlarmActivationFromTranslator;
    private final long receivedTime;

    public AlarmNotificationImpl()
    {
        this(new AlarmNotificationStruct(), 0);
    }

    public AlarmNotificationImpl(AlarmNotificationStruct struct, long receivedTime)
    {
        super();
        checkParam(struct, "AlarmNotificationStruct");
        this.receivedTime = receivedTime;
        this.struct = struct;
        cachedAlarmActivation = null;
        cachedAlarmDefinition = null;
        cachedAlarmNotificationConditions = null;
        cachedDateTime = null;
        isAlarmActivationFromTranslator = false;
        isAlarmDefinitionFromTranslator = false;
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            isEqual = obj instanceof AlarmNotification;
            if(isEqual)
            {
                AlarmNotification castedObj = (AlarmNotification) obj;
                isEqual = getId().equals(castedObj.getId());
            }
        }

        return isEqual;
    }

    public int hashCode()
    {
        return getId().intValue();
    }

    public Object clone()
    {
        AlarmNotificationStruct newStruct = new AlarmNotificationStruct();
        newStruct.activation = struct.activation;
        newStruct.notificationId = struct.notificationId;
        newStruct.timeStamp = struct.timeStamp;

        AlarmNotificationConditionStruct[] matchedConditions = struct.matchedConditions;
        AlarmNotificationConditionStruct[] clonedMatchedConditions =
                new AlarmNotificationConditionStruct[ matchedConditions.length ];
        for(int i = 0; i < matchedConditions.length; i++)
        {
            AlarmNotificationConditionStruct matchedCondition = matchedConditions[ i ];
            clonedMatchedConditions[i] = matchedCondition;
        }
        newStruct.matchedConditions = clonedMatchedConditions;

        return new AlarmNotificationImpl(newStruct, receivedTime);
    }

    /**
     * Determines whether the Alarm Activation returned from <code>getAlarmActivation</code> has been changed from what
     * the Translator knows the current Alarm Activation as.
     * @return True if the Alarm Activation is not the same Alarm Activation that the Translator now knows about. False
     *         if they are still the same.
     */
    public boolean isAlarmActivationChanged()
    {
        return !isAlarmActivationFromTranslator;
    }

    /**
     * Determines whether the Alarm Definition returned from <code>getAlarmDefinition</code> has been changed from what
     * the Translator knows the current Alarm Definition as.
     * @return True if the Alarm Definition is not the same Alarm Definition that the Translator now knows about. False
     *         if they are still the same.
     */
    public boolean isAlarmDefinitionChanged()
    {
        return !isAlarmDefinitionFromTranslator;
    }

    public AlarmActivation getAlarmActivation()
    {
        if(cachedAlarmActivation == null)
        {
            AlarmDefinition definition = getAlarmDefinition();
            AlarmActivation notifyActivation = AlarmActivationFactory.createAlarmActivation(struct.activation, definition);
            AlarmActivation definitionAlarmActivation = definition.getActivationById(struct.activation.activationId);

            if(definitionAlarmActivation != null &&
               definitionAlarmActivation.equals(notifyActivation) &&
               definitionAlarmActivation.isActive() == notifyActivation.isActive())
            {
                cachedAlarmActivation = definitionAlarmActivation;
                isAlarmActivationFromTranslator = true;
            }
            else
            {
                cachedAlarmActivation = notifyActivation;
                isAlarmActivationFromTranslator = false;
            }
        }
        return cachedAlarmActivation;
    }

    public AlarmDefinition getAlarmDefinition()
    {
        if(cachedAlarmDefinition == null)
        {
            AlarmDefinition notifyDefinition =
                    AlarmDefinitionFactory.createAlarmDefinition(struct.activation.definition,
                                                                 struct.activation.definition.conditions);
            try
            {
                AlarmDefinition translatorAlarmDefinition =
                    InstrumentationTranslatorFactory.find().getAlarmDefinitionById(struct.activation.definition.definitionId);
                if(translatorAlarmDefinition != null &&
                   translatorAlarmDefinition.equals(notifyDefinition))
                {
                    cachedAlarmDefinition = translatorAlarmDefinition;
                    isAlarmDefinitionFromTranslator = true;
                }
                else
                {
                    cachedAlarmDefinition = notifyDefinition;
                    isAlarmDefinitionFromTranslator = false;
                }
            }
            catch(NotFoundException e)
            {
                cachedAlarmDefinition = notifyDefinition;
                isAlarmDefinitionFromTranslator = false;
            }
            catch(UserException e)
            {
                GUILoggerHome.find().exception("Could not load definition for notification. Using version from Notification.", e);
                cachedAlarmDefinition = notifyDefinition;
                isAlarmDefinitionFromTranslator = false;
            }
        }
        return cachedAlarmDefinition;
    }

    public Integer getId()
    {
        if(cachedId == null)
        {
            cachedId = new Integer(struct.notificationId);
        }
        return cachedId;
    }

    @Override
    public long getReceivedTime()
    {
    	return receivedTime;
    }

	public AlarmNotificationCondition[] getMatchedConditions()
            throws NotFoundException
    {
        if(cachedAlarmNotificationConditions == null)
        {
            AlarmNotificationConditionStruct[] matchedConditions = struct.matchedConditions;
            cachedAlarmNotificationConditions = new AlarmNotificationCondition[matchedConditions.length];
            for(int i = 0; i < matchedConditions.length; i++)
            {
                AlarmNotificationConditionStruct matchedCondition = matchedConditions[ i ];
                cachedAlarmNotificationConditions[i] =
                    AlarmNotificationFactory.createAlarmNotificationCondition(matchedCondition, this);
            }
        }
        return cachedAlarmNotificationConditions;
    }

    public AlarmNotificationConditionHistoricalImpl[] getMatchedConditionsHistorical()
    throws NotFoundException
    {
        if(cachedAlarmNotificationConditionsHistorical == null)
        {
            AlarmNotificationConditionStruct[] matchedConditions = struct.matchedConditions;
            cachedAlarmNotificationConditionsHistorical = new AlarmNotificationConditionHistoricalImpl[matchedConditions.length];
            for(int i = 0; i < matchedConditions.length; i++)
            {
                AlarmNotificationConditionStruct matchedCondition = matchedConditions[ i ];
                cachedAlarmNotificationConditionsHistorical[i] =
                    AlarmNotificationFactory.createAlarmNotificationConditionHistorical(matchedCondition);
            }
        }
        return cachedAlarmNotificationConditionsHistorical;
    }

    
    public DateTime getTimeStamp()
    {
        if(cachedDateTime == null)
        {
            cachedDateTime = new DateTimeImpl(struct.timeStamp);
        }
        return cachedDateTime;
    }
    
    public void sortMathcCondition(boolean ascending)
    {
        Comparator<AlarmNotificationCondition> comparator =
                new AlarmConditionComparator<AlarmNotificationCondition>(ascending);
        
        try {
            if (getMatchedConditions().length >1)
            {
                Arrays.sort(cachedAlarmNotificationConditions,comparator);
            }
       
            if (getMatchedConditionsHistorical().length > 1)
            {
                Arrays.sort(cachedAlarmNotificationConditionsHistorical,comparator);
            }
        }
        catch (Exception e)
        {
            
        }
    }
}


class AlarmConditionComparator<T> implements Comparator<T>
{
    private boolean ascending;
    AlarmConditionComparator(boolean ascending)
    {
        this.ascending = ascending;   
    }
    
    public int compare(T arg0, T arg1)
    {
        int result=0;
        AlarmNotificationCondition cond0 = (AlarmNotificationCondition)arg0;
        AlarmNotificationCondition cond1 = (AlarmNotificationCondition)arg1;
        Double val0 = null;
        Double val1 = null;
        try {
            val0 =  Double.valueOf(cond0.getThresholdTripValue());
            val1 =  Double.valueOf(cond1.getThresholdTripValue());
        }
        catch (Exception e) {}
        if (val0 != null && val1 != null)
        {
            result = val0.compareTo(val1);
        }
        else
        {
            result = arg0.toString().compareTo(arg1.toString());
        }
    
        if (!ascending)
        {
            result = -1 * result;
        }
        return result;
    }
    
}