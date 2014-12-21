//
// -----------------------------------------------------------------------------------
// Source file: AlarmDefinitionImpl.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.alarm.AlarmConditionStruct;
import com.cboe.idl.alarm.AlarmDefinitionStruct;

import com.cboe.interfaces.instrumentation.alarms.AlarmActivation;
import com.cboe.interfaces.instrumentation.alarms.AlarmActivationMutable;
import com.cboe.interfaces.instrumentation.alarms.AlarmCondition;
import com.cboe.interfaces.instrumentation.alarms.AlarmConditionMutable;
import com.cboe.interfaces.instrumentation.alarms.AlarmDefinition;
import com.cboe.interfaces.instrumentation.alarms.AlarmDefinitionMutable;

import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.logging.GUILoggerHome;

public class AlarmDefinitionImpl extends AbstractMutableBusinessModel<AlarmDefinition>
        implements AlarmDefinitionMutable
{
    private AlarmDefinitionStruct struct;
    private List<AlarmCondition> conditions;
    private List<AlarmActivation> activations;
    private Integer cachedId;

    private boolean isActivationsLoaded;

    public AlarmDefinitionImpl()
    {
        super();

        this.struct = new AlarmDefinitionStruct();

        conditions = new ArrayList<AlarmCondition>(5);
        activations = new ArrayList<AlarmActivation>(5);

        isActivationsLoaded = false;

        addActivation(AlarmActivationFactory.createNewMutableAlarmActivation(this));
    }

    public AlarmDefinitionImpl(AlarmDefinitionStruct struct)
    {
        super();
        checkParam(struct, "AlarmDefinitionStruct");
        conditions = new ArrayList<AlarmCondition>(5);
        activations = new ArrayList<AlarmActivation>(5);

        isActivationsLoaded = false;
        cachedId = null;

        this.struct = struct;

        loadConditionsFromTranslator();
    }

    public AlarmDefinitionImpl(AlarmDefinitionStruct struct,
                               AlarmConditionStruct[] conditionStructs)
    {
        super();
        checkParam(struct, "AlarmDefinitionStruct");
        checkParam(conditionStructs, "AlarmConditionStruct[]");
        conditions = new ArrayList<AlarmCondition>(5);
        activations = new ArrayList<AlarmActivation>(5);

        isActivationsLoaded = false;
        cachedId = null;

        this.struct = struct;

        loadConditions(conditionStructs);
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);
        if(!isEqual)
        {
            if(obj instanceof AlarmDefinition)
            {
                isEqual = (hashCode() == obj.hashCode());
            }
        }

        return isEqual;
    }

    public int hashCode()
    {
        if(isSaved())
        {
            return getId().intValue();
        }
        else
        {
            return getName().hashCode();
        }
    }

    public Object clone() throws CloneNotSupportedException
    {
        AlarmDefinitionStruct newStruct = new AlarmDefinitionStruct();
        newStruct.definitionId = getId().intValue();
        newStruct.name = getName();
        newStruct.severity = getSeverity();

        AlarmCondition[] conditions = getAllConditions();
        AlarmConditionStruct[] clonedConditionStructs = new AlarmConditionStruct[conditions.length];
        for(int i = 0; i < conditions.length; i++)
        {
            AlarmCondition condition = conditions[ i ];
            clonedConditionStructs[i] = ((AlarmCondition)condition.clone()).getStruct();
        }
        newStruct.conditions = clonedConditionStructs;

        AlarmDefinitionImpl newImpl = new AlarmDefinitionImpl(newStruct);
        newImpl.setModified(isModified());

        return newImpl;
    }

    /**
     * Performs a default comparison by <code>getName()</code>.
     */
    public int compareTo(AlarmDefinition obj)
    {
        return getName().compareTo(obj.getName());
    }

    public String getName()
    {
        return struct.name;
    }

    public void setName(String name)
    {
        checkParam(name, "Name");
        if(!name.equals(struct.name))
        {
            String oldValue = struct.name;
            struct.name = name;
            setModified(true);
            firePropertyChange(NAME_PROPERTY, oldValue, name);
        }
    }

    public short getSeverity()
    {
        return struct.severity;
    }

    public void setSeverity(short severity)
    {
        if(severity != struct.severity)
        {
            short oldValue = struct.severity;
            struct.severity = severity;
            setModified(true);
            firePropertyChange(SEVERITY_PROPERTY, oldValue, severity);
        }
    }

    /**
     * Gets a READ-ONLY version of the collection of Activations for convenience purposes.
     * @return READ-ONLY
     */
    public Collection<AlarmActivation> getActivationsCollection()
    {
        loadActivations();
        return Collections.unmodifiableList(activations);
    }

    public AlarmActivation[] getAllActivations()
    {
        loadActivations();
        AlarmActivation[] activationsArray = new AlarmActivation[0];
        activationsArray = activations.toArray(activationsArray);
        return activationsArray;
    }

    public AlarmActivationMutable[] getAllMutableActivations()
    {
        loadActivations();
        AlarmActivationMutable[] activationsArray = new AlarmActivationMutable[ 0 ];
        activationsArray = activations.toArray(activationsArray);
        return activationsArray;
    }

    public AlarmActivation getActivationById(int activationId)
    {
        loadActivations();

        AlarmActivation returnedValue = null;

        for(Iterator<AlarmActivation> iterator = activations.iterator(); iterator.hasNext();)
        {
            AlarmActivation alarmActivation = iterator.next();
            if(alarmActivation.getId().intValue() == activationId)
            {
                returnedValue = alarmActivation;
                break;
            }
        }
        return returnedValue;
    }

    public boolean containsActivation(AlarmActivation activation)
    {
        loadActivations();
        return activations.contains(activation);
    }

    public boolean containsActivation(int activationId)
    {
        loadActivations();

        AlarmActivation foundActivation = getActivationById(activationId);

        return foundActivation != null;
    }

    /**
     * Adds an activation to this definition
     * @param activation to add
     * @return If a previous activation with the same activationId already existed it will be updated.
     */
    public AlarmActivation addActivation(AlarmActivation activation)
    {
        loadActivations();

        AlarmActivation clonedActivation;

        AlarmActivation oldValue = getActivationById(activation.getId().intValue());

        if(oldValue == null)
        {
            try
            {
                clonedActivation = (AlarmActivation) activation.clone();
            }
            catch(CloneNotSupportedException e)
            {
                clonedActivation = activation;
            }
            activations.add(clonedActivation);
            firePropertyChange(ACTIVATION_PROPERTY, oldValue, clonedActivation);
        }
        else
        {
            return updateActivation(activation);
        }

        return oldValue;
    }

    /**
     * Removes an activation from this definition
     * @param activation to remove
     * @return If the passed activation did exist for this definition it will be returned.
     */
    public AlarmActivation removeActivation(AlarmActivation activation)
    {
        loadActivations();

        AlarmActivation oldValue = getActivationById(activation.getId().intValue());
        if(oldValue != null)
        {
            activations.remove(oldValue);
            firePropertyChange(ACTIVATION_PROPERTY, oldValue, null);
        }

        if(activations.isEmpty())
        {
            AlarmActivation defaultActivation = AlarmActivationFactory.createNewMutableAlarmActivation(this);
            activations.add(defaultActivation);
        }

        return oldValue;
    }

    /**
     * Updates an activation to this definition. If the passed activation was not already part of this
     * definition, then this method will do nothing.
     * @param activation to update
     * @return If a previous activation with the same activationId already existed it will be returned.
     */
    public AlarmActivation updateActivation(AlarmActivation activation)
    {
        loadActivations();

        AlarmActivation clonedActivation;

        AlarmActivation oldValue = getActivationById(activation.getId().intValue());

        if(oldValue != null && oldValue != activation)
        {
            if(!oldValue.equals(activation) ||
               oldValue.isActive() != activation.isActive())
            {
                try
                {
                    clonedActivation = (AlarmActivation) activation.clone();
                }
                catch(CloneNotSupportedException e)
                {
                    clonedActivation = activation;
                }
                activations.remove(oldValue);
                activations.add(clonedActivation);
                firePropertyChange(ACTIVATION_PROPERTY, oldValue, clonedActivation);
            }
        }

        return oldValue;
    }

    /**
     * Gets a READ-ONLY version of the collection of Conditions for convenience purposes.
     * @return READ-ONLY
     */
    public Collection<AlarmCondition> getConditionsCollection()
    {
        return Collections.unmodifiableList(conditions);
    }

    public AlarmCondition[] getAllConditions()
    {
        AlarmCondition[] conditionsArray = new AlarmCondition[ 0 ];
        conditionsArray = conditions.toArray(conditionsArray);
        return conditionsArray;
    }

    public AlarmConditionMutable[] getAllMutableConditions()
    {
        AlarmConditionMutable[] conditionsArray = new AlarmConditionMutable[ 0 ];
        conditionsArray = conditions.toArray(conditionsArray);
        return conditionsArray;
    }

    public AlarmCondition getConditionById(int conditionId)
    {
        AlarmCondition returnedValue = null;

        for(Iterator<AlarmCondition> iterator = conditions.iterator(); iterator.hasNext();)
        {
            AlarmCondition alarmCondition = iterator.next();
            if(alarmCondition.getId().intValue() == conditionId)
            {
                returnedValue = alarmCondition;
                break;
            }
        }
        return returnedValue;
    }

    public boolean containsCondition(AlarmCondition condition)
    {
        return conditions.contains(condition);
    }

    public boolean containsCondition(int conditionId)
    {
        AlarmCondition foundCondition = getConditionById(conditionId);

        return foundCondition != null;
    }

    /**
     * Adds an condition to this definition
     * @param condition to add
     * @return If a previous condition with the same conditionId already existed it will be updated.
     */
    public AlarmCondition addCondition(AlarmCondition condition)
    {
        AlarmCondition clonedCondition;

        AlarmCondition oldValue = getConditionById(condition.getId().intValue());

        if(oldValue == null)
        {
            try
            {
                clonedCondition = (AlarmCondition) condition.clone();
            }
            catch(CloneNotSupportedException e)
            {
                clonedCondition = condition;
            }
            conditions.add(clonedCondition);
            setModified(true);
            firePropertyChange(CONDITION_PROPERTY, oldValue, clonedCondition);
        }
        else
        {
            return updateCondition(condition);
        }

        return oldValue;
    }

    /**
     * Removes an condition from this definition
     * @param condition to remove
     * @return If the passed condition did exist for this definition it will be returned.
     */
    public AlarmCondition removeCondition(AlarmCondition condition)
    {
        AlarmCondition oldValue = getConditionById(condition.getId().intValue());
        if(oldValue != null)
        {
            conditions.remove(oldValue);
            setModified(true);
            firePropertyChange(CONDITION_PROPERTY, oldValue, null);
        }

        return oldValue;
    }

    /**
     * Updates an condition to this definition.
     * @param condition to update
     * @return If a previous condition with the same conditionId already existed it will be returned.
     */
    public AlarmCondition updateCondition(AlarmCondition condition)
    {
        AlarmCondition clonedCondition;

        AlarmCondition oldValue = getConditionById(condition.getId().intValue());

        if(oldValue != null && oldValue != condition)
        {
            if(!oldValue.equals(condition))
            {
                try
                {
                    clonedCondition = (AlarmCondition) condition.clone();
                }
                catch(CloneNotSupportedException e)
                {
                    clonedCondition = condition;
                }
                conditions.remove(oldValue);
                conditions.add(clonedCondition);
                setModified(true);
                firePropertyChange(CONDITION_PROPERTY, oldValue, clonedCondition);
            }
        }

        return oldValue;
    }

    public Integer getId()
    {
        if(cachedId == null)
        {
            cachedId = new Integer(struct.definitionId);
        }
        return cachedId;
    }

    /**
     * @deprecated used only for IDL API access
     */
    public AlarmDefinitionStruct getStruct()
    {
        AlarmCondition[] conditions = getAllConditions();
        struct.conditions = new AlarmConditionStruct[conditions.length];
        for(int i = 0; i < conditions.length; i++)
        {
            AlarmCondition condition = conditions[ i ];
            struct.conditions[i] = condition.getStruct();
        }
        return struct;
    }

    public boolean isSaved()
    {
        if(getId().intValue() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    synchronized void reinitializeActivations()
    {
        isActivationsLoaded = false;
        loadActivations();
    }

    private synchronized void loadActivations()
    {
        if(!isActivationsLoaded)
        {
            this.activations.clear();

            AlarmActivation[] activations =
                    InstrumentationTranslatorFactory.find().getAlarmActivationsForDefinitionId(getId());

            if(activations != null && activations.length > 0)
            {
                for(int i = 0; i < activations.length; i++)
                {
                    AlarmActivation activation = activations[ i ];
                    this.activations.add(activation);
                }
            }
            else
            {
                AlarmActivation defaultActivation = AlarmActivationFactory.createNewMutableAlarmActivation(this);
                this.activations.add(defaultActivation);
            }

            isActivationsLoaded = true;
        }
    }

    private void loadConditionsFromTranslator()
    {
        conditions.clear();

        AlarmConditionStruct[] conditionStructs = struct.conditions;
        for(int i = 0; i < conditionStructs.length; i++)
        {
            AlarmConditionStruct conditionStruct = conditionStructs[ i ];

            try
            {
                AlarmCondition condition =
                        InstrumentationTranslatorFactory.find().getAlarmConditionById(conditionStruct.conditionId);
                if(condition != null)
                {
                    conditions.add(condition);
                }
            }
            catch(UserException e)
            {
                GUILoggerHome.find().exception("Could not load condition for definition. ConditionId:" +
                                               conditionStruct.conditionId + "; DefinitionId:" +
                                               struct.definitionId, e);
            }
        }
    }

    private void loadConditions(AlarmConditionStruct[] conditionStructs)
    {
        conditions.clear();

        for(int i = 0; i < conditionStructs.length; i++)
        {
            AlarmConditionStruct conditionStruct = conditionStructs[i];
            AlarmCondition newCondition = AlarmConditionFactory.createAlarmCondition(conditionStruct);
            if(newCondition != null)
            {
                conditions.add(newCondition);
            }
        }
    }
}

class AlarmConditionSortingComparator implements Comparator
{
    public AlarmConditionSortingComparator()
    {
        super();
    }

    public int compare(Object o1, Object o2)
    {
        int result;

        AlarmCondition cond1 = (AlarmCondition) o1;
        AlarmCondition cond2 = (AlarmCondition) o2;

        result = cond1.getId().compareTo(cond2.getId());

        return result;
    }
}

class AlarmActivationSortingComparator implements Comparator
{
    public AlarmActivationSortingComparator()
    {
        super();
    }

    public int compare(Object o1, Object o2)
    {
        int result;

        AlarmActivation act1 = (AlarmActivation) o1;
        AlarmActivation act2 = (AlarmActivation) o2;

        result = act1.getId().compareTo(act2.getId());

        return result;
    }
}