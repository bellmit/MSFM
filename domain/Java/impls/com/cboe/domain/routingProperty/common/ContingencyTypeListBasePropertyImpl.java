package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: StringListBasePropertyImpl
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.common
// 
// Created: Jun 27, 2006 10:53:25 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.ContingencyType;
import com.cboe.interfaces.domain.routingProperty.common.ContingencyTypeListBaseProperty;
import com.cboe.interfaces.domain.routingProperty.common.Destination;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;


public class ContingencyTypeListBasePropertyImpl extends AbstractBaseProperty implements ContingencyTypeListBaseProperty
{
    public static final String CONTINGENCY_TYPE_LIST_CHANGE_EVENT = "ContingencyTypeListValue";
    private ContingencyType[] listValues = new ContingencyType[0];

    public ContingencyTypeListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                      BasePropertyType type)
    {
        super(propertyCategory, propertyName, key, type);
    }

    public ContingencyTypeListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                      BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
    }

    public ContingencyTypeListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                      BasePropertyType type, ContingencyType[] list)
    {
        this(propertyCategory, propertyName, key, type);
        setContingencyTypeListValue(list);
    }

    public ContingencyTypeListBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                      BasePropertyType type, ContingencyType[] list, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, validators);
        setContingencyTypeListValue(list);
    }

    /**
     * Used to compare another BaseProperty to this one.
     * @param object to compare this with.
     * @return as defined in java.lang.Comparable
     */
    public int compareTo(Object object)
    {
        ContingencyType[] thierList = ((ContingencyTypeListBaseProperty) object).getContingencyTypeListValue();

        boolean isEqual = Arrays.equals(listValues, thierList);
        return isEqual ? 0 : listValues.length >= thierList.length ? 1 : -1;
    }

    
    /**
     * Parses the field values from the passed String
     * @param value to parse
     */
    protected void decodeValue(String value)
    {
        if(value != null && value.length() > 0)
        {
            String[] contingencyTypelistValues = BasicPropertyParser.parseArray(value);
            int numContingencyType = contingencyTypelistValues.length;
            ArrayList<ContingencyType> contingencyTypeList = new ArrayList<ContingencyType>(numContingencyType);

            int k=0;
            for(int i=0; i<numContingencyType; i++)
            {
                try
                {
                    ContingencyType contingencyType = new ContingencyType(new Integer(contingencyTypelistValues[k]));
                    contingencyTypeList.add(contingencyType);
                    k += 1;
                }
                catch(IllegalArgumentException e)
                {
                    Log.exception(e);
                }
            }
            listValues = contingencyTypeList.toArray(new ContingencyType[contingencyTypeList.size()]);
        }
        else
        {
            listValues = new ContingencyType[0];
        }
    }

    /**
     * Get all the Property values as a List of Strings
     * @return List of Strings
     */
    protected List getEncodedValuesAsStringList()
    {
        List valueList = new ArrayList(listValues.length);
        for (int i = 0; i < listValues.length; i++)
        {
            valueList.add(listValues[i]);
        }

        return valueList;
    }

    public ContingencyType[] getContingencyTypeListValue()
    {
        return listValues;
    }

    public void setContingencyTypeListValue(ContingencyType[] contingencyTypeListValue)
    {
        ContingencyType[] oldValue = listValues;
        ContingencyType[] newArray = new ContingencyType[contingencyTypeListValue.length];
        System.arraycopy(contingencyTypeListValue, 0, newArray, 0, contingencyTypeListValue.length);
        listValues = newArray;
        firePropertyChange(CONTINGENCY_TYPE_LIST_CHANGE_EVENT, oldValue, listValues);
    }

    public ContingencyType getContingencyTypeListValue(int index)
    {
        return listValues[index];
    }

    public void setContingencyTypeListValue(int index, ContingencyType contingencyTypeValue)
    {
        int size = listValues.length;

        if (index >= size)
        {
            ContingencyType[] newArray = new ContingencyType[size + 1];
            System.arraycopy(listValues, 0, newArray, 0, size);

            listValues = newArray;
            listValues[size] = contingencyTypeValue;
        }
        else
        {
            listValues[index] = contingencyTypeValue;
        }
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(255);
        buffer.append(getPropertyName()).append("=");
        for (int i = 0; i < listValues.length; i++)
        {
            buffer.append(listValues[i].toDisplayString());
            if (i < listValues.length - 1)
            {
                buffer.append(',');
            }
        }

        return buffer.toString();
    }
}
