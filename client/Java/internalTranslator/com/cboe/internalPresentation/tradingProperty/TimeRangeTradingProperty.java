//
// -----------------------------------------------------------------------------------
// Source file: TimeRangeTradingProperty.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.util.*;

import com.cboe.idl.tradingProperty.TimeRangeStruct;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;
import com.cboe.domain.tradingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.util.StructBuilder;

/**
 * Represents a TradingProperty for a single TimeRangeStruct
 */
public class TimeRangeTradingProperty extends AbstractTradingProperty implements Comparable
{
    public static final String STRUCT_CHANGE_EVENT = "StructChange";
    public static final String LOWER_LIMIT_CHANGE_EVENT = "LowerLimit";
    public static final String UPPER_LIMIT_CHANGE_EVENT = "UpperLimit";

    protected TradingPropertyType tradingPropertyType;

    private TimeRangeStruct timeRangeStruct;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public TimeRangeTradingProperty(String propertyName, String sessionName, int classKey)
    {
        super(propertyName, sessionName, classKey);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param timeRangeStruct value for the trading property
     */
    public TimeRangeTradingProperty(String propertyName, String sessionName, int classKey,
                                    TimeRangeStruct timeRangeStruct)
    {
        this(propertyName, sessionName, classKey);
        setTimeRangeStruct(timeRangeStruct);
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        TimeRangeTradingProperty clonedTradingProperty = (TimeRangeTradingProperty) super.clone();
        TimeRangeStruct clonedStruct =
                StructBuilder.cloneTimeRangeStruct(getTimeRangeStruct());
        clonedTradingProperty.timeRangeStruct = clonedStruct;

        return clonedTradingProperty;
    }

    public int hashCode()
    {
        return getLowerLimit();
    }

    public int compareTo(Object object)
    {
        int result;
        int myValue = getLowerLimit();
        int theirValue = ((TimeRangeTradingProperty) object).getLowerLimit();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    public TimeRangeStruct getTimeRangeStruct()
    {
        return timeRangeStruct;
    }

    public void setTimeRangeStruct(TimeRangeStruct timeRangeStruct)
    {
        TimeRangeStruct oldValue = this.timeRangeStruct;
        this.timeRangeStruct = timeRangeStruct;
        firePropertyChange(STRUCT_CHANGE_EVENT, oldValue, timeRangeStruct);
    }

    public int getUpperLimit()
    {
        int result = 0;
        if(getTimeRangeStruct() != null)
        {
            result = getTimeRangeStruct().upperLimit;
        }
        return result;
    }

    public void setUpperLimit(int upperLimit)
    {
        if(getTimeRangeStruct() == null)
        {
            setTimeRangeStruct(new TimeRangeStruct());
        }
        int oldValue = getTimeRangeStruct().upperLimit;
        getTimeRangeStruct().upperLimit = upperLimit;
        firePropertyChange(UPPER_LIMIT_CHANGE_EVENT, oldValue, upperLimit);
    }

    public int getLowerLimit()
    {
        int result = 0;
        if(getTimeRangeStruct() != null)
        {
            result = getTimeRangeStruct().lowerLimit;
        }
        return result;
    }

    public void setLowerLimit(int lowerLimit)
    {
        if(getTimeRangeStruct() == null)
        {
            setTimeRangeStruct(new TimeRangeStruct());
        }
        int oldValue = getTimeRangeStruct().lowerLimit;
        getTimeRangeStruct().lowerLimit = lowerLimit;
        firePropertyChange(LOWER_LIMIT_CHANGE_EVENT, oldValue, lowerLimit);
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    public void setTradingPropertyType(TradingPropertyType tradingPropertyType)
    {
        this.tradingPropertyType = tradingPropertyType;
    }

    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"lowerLimit", "upperLimit"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
