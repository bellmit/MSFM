//
// -----------------------------------------------------------------------------------
// Source file: EPWTradingProperty.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.util.*;

import com.cboe.idl.cmiProduct.EPWStruct;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;
import com.cboe.domain.tradingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.util.StructBuilder;

/**
 * Represents a TradingProperty for a single EPWStruct
 */
public class EPWTradingProperty extends AbstractTradingProperty implements Comparable
{
    public static final String STRUCT_CHANGE_EVENT = "StructChange";
    public static final String MINIMUM_BID_RANGE_CHANGE_EVENT = "MinBidRange";
    public static final String MAXIMUM_BID_RANGE_CHANGE_EVENT = "MaxBidRange";
    public static final String MAXIMUM_ALLOWABLE_SPREAD_CHANGE_EVENT = "MaxAllowSpread";

    private EPWStruct epwStruct;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public EPWTradingProperty(String sessionName, int classKey)
    {
        super(EPWTradingPropertyGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param epwStruct value for the trading property
     */
    public EPWTradingProperty(String sessionName, int classKey, EPWStruct epwStruct)
    {
        this(sessionName, classKey);
        setEPWStruct(epwStruct);
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        EPWTradingProperty clonedTradingProperty = (EPWTradingProperty) super.clone();
        EPWStruct clonedStruct =
                StructBuilder.cloneEPWStruct(getEPWStruct());
        clonedTradingProperty.epwStruct = clonedStruct;

        return clonedTradingProperty;
    }

    public int hashCode()
    {
        return (int)getMinimumBidRange();
    }

    public int compareTo(Object object)
    {
        int result;
        double myValue = getMinimumBidRange();
        double theirValue = ((EPWTradingProperty) object).getMinimumBidRange();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overridden to return the String representation of the minimum bid range.
     */
    public String getPropertyName()
    {
        return Double.toString(getMinimumBidRange());
    }

    public EPWStruct getEPWStruct()
    {
        return epwStruct;
    }

    public void setEPWStruct(EPWStruct epwStruct)
    {
        EPWStruct oldValue = this.epwStruct;
        this.epwStruct = epwStruct;
        firePropertyChange(STRUCT_CHANGE_EVENT, oldValue, epwStruct);
    }

    public double getMinimumBidRange()
    {
        double result = 0;
        if(getEPWStruct() != null)
        {
            result = getEPWStruct().minimumBidRange;
        }
        return result;
    }

    public void setMinimumBidRange(double minimumBidRange)
    {
        if(getEPWStruct() == null)
        {
            setEPWStruct(new EPWStruct());
        }
        double oldValue = getEPWStruct().minimumBidRange;
        getEPWStruct().minimumBidRange = minimumBidRange;
        firePropertyChange(MINIMUM_BID_RANGE_CHANGE_EVENT, oldValue, minimumBidRange);
    }

    public double getMaximumBidRange()
    {
        double result = 0;
        if(getEPWStruct() != null)
        {
            result = getEPWStruct().maximumBidRange;
        }
        return result;
    }

    public void setMaximumBidRange(double maximumBidRange)
    {
        if(getEPWStruct() == null)
        {
            setEPWStruct(new EPWStruct());
        }
        double oldValue = getEPWStruct().maximumBidRange;
        getEPWStruct().maximumBidRange = maximumBidRange;
        firePropertyChange(MAXIMUM_BID_RANGE_CHANGE_EVENT, oldValue, maximumBidRange);
    }

    public double getMaximumAllowableSpread()
    {
        double result = 0;
        if(getEPWStruct() != null)
        {
            result = getEPWStruct().maximumAllowableSpread;
        }
        return result;
    }

    public void setMaximumAllowableSpread(double maximumAllowableSpread)
    {
        if(getEPWStruct() == null)
        {
            setEPWStruct(new EPWStruct());
        }
        double oldValue = getEPWStruct().maximumAllowableSpread;
        getEPWStruct().maximumAllowableSpread = maximumAllowableSpread;
        firePropertyChange(MAXIMUM_ALLOWABLE_SPREAD_CHANGE_EVENT, oldValue, maximumAllowableSpread);
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return EPWTradingPropertyGroup.TRADING_PROPERTY_TYPE;
    }

    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"minimumBidRange", "maximumBidRange", "maximumAllowableSpread"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
