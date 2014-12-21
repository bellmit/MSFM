//
// -----------------------------------------------------------------------------------
// Source file: RangeScaleImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.RangeScale;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

public class RangeScaleImpl extends AbstractTradingProperty
        implements RangeScale
{
    private TradingPropertyType tradingPropertyType;

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told which type
     * this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public RangeScaleImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey)
    {
        super(tradingPropertyType.getName(), sessionName, classKey);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told which type
     * this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public RangeScaleImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
                                       Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told which type
     * this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param lowRange
     * @param highRange
     * @param scale
     */
    public RangeScaleImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
                          int lowRange, int highRange, double scale)
    {
        this(tradingPropertyType, sessionName, classKey);
        setLowerRange(lowRange);
        setUpperRange(highRange);
        setPercentage(scale);
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told which type
     * this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public RangeScaleImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey, String value)
    {
        super(tradingPropertyType.getName(), sessionName, classKey, value);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Compares based on getLowerRange()
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getLowerRange();
        int theirValue = ((RangeScale) object).getLowerRange();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    public int hashCode()
    {
        return getLowerRange();
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    public String getPropertyName()
    {
        return Integer.toString(getLowerRange());
    }

    public void setLowerRange(int val)
    {
        setInteger1(val);
    }

    public void setUpperRange(int val)
    {
        setInteger2(val);
    }

    public void setPercentage(double val)
    {
        setDouble1(val);
    }

    public int getLowerRange()
    {
        return getInteger1();
    }

    public int getUpperRange()
    {
        return getInteger2();
    }

    public double getPercentage()
    {
        return getDouble1();
    }

    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"lowerRange", "upperRange", "percentage"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
