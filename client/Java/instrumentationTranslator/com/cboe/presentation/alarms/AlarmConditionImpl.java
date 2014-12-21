//
// -----------------------------------------------------------------------------------
// Source file: AlarmConditionImpl.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.cboe.idl.alarm.AlarmConditionStruct;

import com.cboe.interfaces.instrumentation.alarms.AlarmConditionMutable;
import com.cboe.interfaces.instrumentation.alarms.IllegalTypeException;
import com.cboe.interfaces.instrumentation.alarms.AlarmCondition;
import com.cboe.interfaces.instrumentationCollector.AlarmConstants;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

public class AlarmConditionImpl extends AbstractMutableBusinessModel implements AlarmConditionMutable
{
    private AlarmConditionStruct struct;
    private Integer cachedId;

    public AlarmConditionImpl()
    {
        this(new AlarmConditionStruct());
    }

    public AlarmConditionImpl(AlarmConditionStruct struct)
    {
        checkParam(struct, "AlarmConditionStruct");
        this.struct = struct;

        cachedId = null;
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            isEqual = obj instanceof AlarmCondition;
            if(isEqual)
            {
                AlarmCondition castedObj = (AlarmCondition) obj;
                isEqual = (getConditionType() == castedObj.getConditionType() &&
                           getContextName().equals(castedObj.getContextName()) &&
                           getContextType().equals(castedObj.getContextType()) &&
                           getFieldName().equals(castedObj.getFieldName()) &&
                           getFieldTypeText().equals(castedObj.getFieldTypeText()) &&
                           getName().equals(castedObj.getName()) &&
                           getOperator().equals(castedObj.getOperator()) &&
                           getSubjectName().equals(castedObj.getSubjectName()) &&
                           getThresholdText().equals(castedObj.getThresholdText()));
            }
        }

        return isEqual;
    }

    public int hashCode()
    {
        if(isSaved())
        {
            return getId();
        }
        else
        {
            return getName().hashCode();
        }
    }

    @Override
    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException", "CloneDoesntCallSuperClone"})
    public Object clone()
    {
        AlarmConditionStruct newStruct = new AlarmConditionStruct();
        newStruct.conditionId = getId();
        newStruct.conditionType = getConditionType();
        newStruct.contextName = getContextName();
        newStruct.contextType = getContextType();
        newStruct.fieldName = getFieldName();
        newStruct.fieldType = getFieldTypeText();
        newStruct.name = getName();
        newStruct.operator = getOperator();
        newStruct.subjectName = getSubjectName();
        newStruct.threshold = getThresholdText();

        AlarmConditionImpl newImpl = new AlarmConditionImpl(newStruct);
        newImpl.setModified(isModified());

        return newImpl;
    }

    /**
     * Performs a default comparison by <code>getName()</code>.
     */
    public int compareTo(Object obj)
    {
        return getName().compareTo(((AlarmCondition) obj).getName());
    }

    public short getConditionType()
    {
        return struct.conditionType;
    }

    public void setConditionType(short conditionType)
    {
        if(conditionType != struct.conditionType)
        {
            short oldValue = struct.conditionType;
            struct.conditionType = conditionType;
            setModified(true);
            firePropertyChange(CONDITION_TYPE_PROPERTY, oldValue, conditionType);
        }
    }

    public String getContextName()
    {
        return struct.contextName;
    }

    public void setContextName(String contextName)
    {
        checkParam(contextName, "contextName");
        if(!contextName.equals(struct.contextName))
        {
            String oldValue = struct.contextName;
            struct.contextName = contextName;
            setModified(true);
            firePropertyChange(CONTEXT_NAME_PROPERTY, oldValue, contextName);
        }
    }

    public String getContextType()
    {
        return struct.contextType;
    }

    public void setContextType(String contextType)
    {
        checkParam(contextType, "contextType");
        if(!contextType.equals(struct.contextType))
        {
            String oldValue = struct.contextType;
            struct.contextType = contextType;
            setModified(true);
            firePropertyChange(CONTEXT_TYPE_PROPERTY, oldValue, contextType);
        }
    }

    public String getFieldName()
    {
        return struct.fieldName;
    }

    public void setFieldName(String fieldName)
    {
        checkParam(fieldName, "fieldName");
        if(!fieldName.equals(struct.fieldName))
        {
            String oldValue = struct.fieldName;
            struct.fieldName = fieldName;
            setModified(true);
            firePropertyChange(FIELD_NAME_PROPERTY, oldValue, fieldName);
        }
    }

    public String getFieldTypeText()
    {
        return struct.fieldType;
    }

    /**
     * Set the field type text.
     * @param fieldType to set with.
     * @throws com.cboe.interfaces.instrumentation.alarms.IllegalTypeException
     *          Is thrown if fieldType does not specify a valid fully qualified class name.
     */
    public void setFieldTypeText(String fieldType) throws IllegalTypeException
    {
        checkParam(fieldType, "fieldType");
        if(!fieldType.equals(struct.fieldType))
        {
            try
            {
                Class.forName(fieldType);
            }
            catch(ClassNotFoundException e)
            {
                throw new IllegalTypeException(e.getMessage(), e);
            }

            String oldValue = struct.fieldType;
            struct.fieldType = fieldType;
            setModified(true);
            firePropertyChange(FIELD_TYPE_PROPERTY, oldValue, fieldType);
        }
    }

    /**
     * Converts the field type text into a Class.
     * @return Class represented by the field type text
     * @throws com.cboe.interfaces.instrumentation.alarms.IllegalTypeException
     *          If the field type text was not defined or does not name a fully qualified class name, then this
     *          exception will be returned.
     */
    public Class<?> getFieldTypeValue() throws IllegalTypeException
    {
        String fieldTypeText = getFieldTypeText();
        if(fieldTypeText != null)
        {
            try
            {
                return Class.forName(fieldTypeText);
            }
            catch(ClassNotFoundException e)
            {
                throw new IllegalTypeException(e.getMessage(), e);
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Set the field type text, using the fully qualified class name of the passed fieldType.
     * @param fieldType to get fully qualified class name from for setting the field type text
     */
    public void setFieldTypeValue(Class<?> fieldType)
    {
        checkParam(fieldType, "fieldType");
        if(!fieldType.getName().equals(struct.fieldType))
        {
            String oldValue = struct.fieldType;
            struct.fieldType = fieldType.getName();
            setModified(true);
            firePropertyChange(FIELD_TYPE_PROPERTY, oldValue, fieldType);
        }
    }

    public String getName()
    {
        return struct.name;
    }

    public void setName(String name)
    {
        checkParam(name, "name");
        if(!name.equals(struct.name))
        {
            String oldValue = struct.name;
            struct.name = name;
            setModified(true);
            firePropertyChange(NAME_PROPERTY, oldValue, name);
        }
    }

    public String getOperator()
    {
        return struct.operator;
    }

    public void setOperator(String operator)
    {
        checkParam(operator, "operator");
        if(!operator.equals(struct.operator))
        {
            String oldValue = struct.operator;
            struct.operator = operator;
            setModified(true);
            firePropertyChange(OPERATOR_PROPERTY, oldValue, operator);
        }
    }

    public String getSubjectName()
    {
        return struct.subjectName;
    }

    public void setSubjectName(String subjectName)
    {
        checkParam(subjectName, "subjectName");
        if(!subjectName.equals(struct.subjectName))
        {
            String oldValue = struct.subjectName;
            struct.subjectName = subjectName;
            setModified(true);
            firePropertyChange(SUBJECT_NAME_PROPERTY, oldValue, subjectName);
        }
    }

    public String getThresholdText()
    {
        return struct.threshold;
    }

    /**
     * Set the threshold value.
     * @param thresholdText to set with.
     * @throws com.cboe.interfaces.instrumentation.alarms.IllegalTypeException
     *          Is thrown if a field type is set and that field type cannot represent this thresholdText value.
     */
    public void setThresholdText(String thresholdText) throws IllegalTypeException
    {
        checkParam(thresholdText, "thresholdText");
        if(!thresholdText.equals(struct.threshold))
        {
            Class<?> typeClass = getFieldTypeValue();

            if(typeClass != null)
            {
                Class<?>[] parmClass = {thresholdText.getClass()};
                try
                {
                    Constructor<?> constructor = typeClass.getConstructor(parmClass);
                    if(constructor != null)
                    {
                        Object[] parms = {thresholdText};
                        constructor.newInstance(parms);
                    }
                    else
                    {
                        throw new IllegalTypeException("Invalid thresholdText for fieldType. Constuctor was not returned.");
                    }
                }
                catch(NoSuchMethodException e)
                {
                    throw new IllegalTypeException("Invalid thresholdText for fieldType.", e);
                }
                catch(IllegalAccessException e)
                {
                    throw new IllegalTypeException("Invalid thresholdText for fieldType.", e);
                }
                catch(InvocationTargetException e)
                {
                    throw new IllegalTypeException("Invalid thresholdText for fieldType.", e);
                }
                catch(InstantiationException e)
                {
                    throw new IllegalTypeException("Invalid thresholdText for fieldType.", e);
                }
            }

            String oldValue = struct.threshold;
            struct.threshold = thresholdText;
            setModified(true);
            firePropertyChange(THRESHOLD_VALUE_PROPERTY, oldValue, thresholdText);
        }
    }

    /**
     * Gets the threshold text returned as the field type defined by <code>getFieldType()</code>. For instance, if the
     * threshold text is "12" and the fieldType is "java.lang.Integer", a java.lang.Integer will be returned as the
     * Object type, representing the value 12.
     * @return Threshold text as actual field type.
     * @throws com.cboe.interfaces.instrumentation.alarms.IllegalTypeException
     *                               If the field type was not defined, does not name a fully qualified class name or
     *                               the class name cannot be initialized with a String, then this exception will be
     *                               returned.
     * @throws NumberFormatException If the threshold text is not a valid value that can be represented by field type,
     *                               then this exception is thrown.
     */
    public Object getThresholdValue() throws NumberFormatException, IllegalTypeException
    {
        String thresholdText = getThresholdText();
        Class<?> typeClass = getFieldTypeValue();
        if(typeClass != null)
        {
            Class<?>[] parmClass = {thresholdText.getClass()};
            try
            {
                Constructor<?> constructor = typeClass.getConstructor(parmClass);
                if(constructor != null)
                {
                    Object[] parms = {thresholdText};
                    return constructor.newInstance(parms);
                }
                else
                {
                    throw new IllegalTypeException("Invalid thresholdText for fieldType. Constructor was not returned.");
                }
            }
            catch(NoSuchMethodException e)
            {
                throw new IllegalTypeException("Invalid thresholdText for fieldType.", e);
            }
            catch(IllegalAccessException e)
            {
                throw new IllegalTypeException("Invalid thresholdText for fieldType.", e);
            }
            catch(InvocationTargetException e)
            {
                throw new IllegalTypeException("Invalid thresholdText for fieldType.", e);
            }
            catch(InstantiationException e)
            {
                throw new IllegalTypeException("Invalid thresholdText for fieldType.", e);
            }
        }
        else
        {
            throw new IllegalTypeException("FieldType was not set.");
        }
    }

    /**
     * Sets the threshold text and field type.
     * @param thresholdValue to get toString() from for threshold text and Class for setFieldTypeValue(Class).
     */
    public void setThresholdValue(Object thresholdValue)
    {
        checkParam(thresholdValue, "thresholdValue");
        if(!thresholdValue.toString().equals(struct.threshold))
        {
            setFieldTypeValue(thresholdValue.getClass());

            String oldValue = struct.threshold;
            struct.threshold = thresholdValue.toString();
            setModified(true);
            firePropertyChange(THRESHOLD_VALUE_PROPERTY, oldValue, struct.threshold);
        }
    }

    public Integer getId()
    {
        if(cachedId == null)
        {
            cachedId = struct.conditionId;
        }
        return cachedId;
    }

    /**
     * @deprecated used only for IDL API access
     */
    public AlarmConditionStruct getStruct()
    {
        return struct;
    }

    /**
     * @deprecated used only for IDL API access
     */
    public void setStruct(AlarmConditionStruct struct)
    {
        this.struct = struct;

        cachedId = null;
    }

    public boolean isSaved()
    {
        return getId() > 0;
    }

    public boolean isUsingCalculation()
    {
        return getFieldName() != null && getFieldName().startsWith(AlarmConstants.ALARM_CALCULATION_PREFIX);
    }
}
