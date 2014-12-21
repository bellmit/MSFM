//
// -----------------------------------------------------------------------------------
// Source file: AlarmCalculationImpl.java
//
// PACKAGE: com.cboe.presentation.alarms
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms;

import com.cboe.idl.alarm.AlarmCalculationStruct;
import com.cboe.interfaces.instrumentation.alarms.AlarmCalculation;
import com.cboe.interfaces.instrumentation.alarms.AlarmCalculationMutable;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

/**
 * @author Thomas Morrow
 * @since Sep 15, 2008
 */
public class AlarmCalculationImpl extends AbstractMutableBusinessModel<AlarmCalculationImpl> implements AlarmCalculationMutable
{
    private AlarmCalculationStruct struct;
    private Integer cacheId;

    public AlarmCalculationImpl()
    {
        struct = new AlarmCalculationStruct();
    }

    public AlarmCalculationImpl(AlarmCalculationStruct struct)
    {
        checkParam(struct, "AlarmCalculationStruct");
        this.struct = struct;
    }

    public Integer getId()
    {
        if (cacheId == null)
        {
            cacheId = struct.calculationId;
        }
        return cacheId;
    }

    public String getName()
    {
        return struct.name;
    }

    public void setName(String name)
    {
        checkParam(name, "name");
        if (!name.equals(struct.name))
        {
            String oldValue = struct.name;
            struct.name = name;
            setModified(true);
            firePropertyChange(NAME_PROPERTY, oldValue, name);
        }
    }

    public String getContextType()
    {
        return struct.contextType;
    }

    public void setContextType(String contextType)
    {
        checkParam(contextType, "contextType");
        if (!contextType.equals(struct.contextType))
        {
            String oldValue = struct.contextType;
            struct.contextType = contextType;
            setModified(true);
            firePropertyChange(CONTEXT_TYPE_PROPERTY, oldValue, contextType);
        }
    }

    public String getExpression()
    {
        return struct.expression;
    }

    public void setExpression(String expression)
    {
        checkParam(expression, "expression");
        if (!expression.equals(struct.expression))
        {
            String oldValue = struct.expression;
            struct.expression = expression;
            setModified(true);
            firePropertyChange(EXPRESSION_PROPERTY, oldValue, expression);
        }
    }

    public AlarmCalculationStruct getStruct()
    {
        return struct;
    }

    private void setStruct(AlarmCalculationStruct struct)
    {
        this.struct = struct;
        cacheId = null;
    }

    public boolean isSaved()
    {
        return struct.calculationId > 0;
    }

    @Override
    public AlarmCalculationImpl clone() throws CloneNotSupportedException
    {
        AlarmCalculationImpl newCalculation = (AlarmCalculationImpl)super.clone();
        AlarmCalculationStruct calculationStruct = new AlarmCalculationStruct();
        calculationStruct.calculationId = struct.calculationId;
        calculationStruct.name = struct.name;
        calculationStruct.contextType = struct.contextType;
        calculationStruct.expression = struct.expression;
        newCalculation.setStruct(calculationStruct);
        return newCalculation;
    }

    @Override
    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof AlarmCalculation)
        {
            AlarmCalculation other = (AlarmCalculation)obj;
            return (getId() == null ? other.getId() == null : getId().equals(other.getId()))
                    && (getName() == null ? other.getName() == null : getName().equals(other.getName()))
                    && (getContextType() == null ? other.getContextType() == null : getContextType().equals(other.getContextType()))
                    && (getExpression() == null ? other.getExpression() == null : getExpression().equals(other.getExpression()));
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int result = 17;
        if (isSaved())
        {
            result = 31 * result + (struct.calculationId);  //using value from struct to avoid autounboxing
        }
        else
        {
            result = 31 * result + (getName() == null ? 0 : getName().hashCode());
        }
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(300);
        builder.append(getClass().getSimpleName()).append("::toString()");
        builder.append('\n');
        builder.append("Id: ").append(getId());
        builder.append('\n');
        builder.append("name: ").append(getName());
        builder.append('\n');
        builder.append("contextType: ").append(getContextType());
        builder.append('\n');
        builder.append("expression: ").append(getExpression());
        return builder.toString();
    }
}
