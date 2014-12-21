//
// -----------------------------------------------------------------------------------
// Source file: InternalizationPercentageTradingProperty.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.util.*;

import com.cboe.idl.tradingProperty.InternalizationPercentageStruct;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;
import com.cboe.domain.tradingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.util.StructBuilder;

/**
 * Represents a TradingProperty for a single InternalizationPercentageStruct
 */
public class InternalizationPercentageTradingProperty extends AbstractTradingProperty implements Comparable
{
    public static final String STRUCT_CHANGE_EVENT = "StructChange";
    public static final String AUCTION_TYPE_CHANGE_EVENT = "AuctionType";
    public static final String LOWER_RANGE_CHANGE_EVENT = "LowerRange";
    public static final String UPPER_RANGE_CHANGE_EVENT = "UpperRange";
    public static final String PERCENTAGE_CHANGE_EVENT = "Percentage";

    private InternalizationPercentageStruct internalizationPercentageStruct;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public InternalizationPercentageTradingProperty(String sessionName, int classKey)
    {
        super(InternalizationPercentageTradingPropertyGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param internalizationPercentageStruct value for the trading property
     */
    public InternalizationPercentageTradingProperty(String sessionName, int classKey,
                                                    InternalizationPercentageStruct internalizationPercentageStruct)
    {
        this(sessionName, classKey);
        setInternalizationPercentageStruct(internalizationPercentageStruct);
    }

    public int hashCode()
    {
        String auctionTypeString = Short.toString(getAuctionType());
        String lowerRangeString = Integer.toString(getLowerRange());
        String combinedKey = auctionTypeString + lowerRangeString;
        return combinedKey.hashCode();
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        InternalizationPercentageTradingProperty clonedTradingProperty =
                (InternalizationPercentageTradingProperty) super.clone();
        InternalizationPercentageStruct clonedStruct =
                StructBuilder.cloneInternalizationPercentageStruct(getInternalizationPercentageStruct());
        clonedTradingProperty.internalizationPercentageStruct = clonedStruct;

        return clonedTradingProperty;
    }

    public int compareTo(Object object)
    {
        int result;
        int myValue = getAuctionType();
        int theirValue = ((InternalizationPercentageTradingProperty) object).getAuctionType();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));

        if(result == 0)
        {
            myValue = getLowerRange();
            theirValue = ((InternalizationPercentageTradingProperty) object).getLowerRange();
            result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        }
        return result;
    }

    public InternalizationPercentageStruct getInternalizationPercentageStruct()
    {
        return internalizationPercentageStruct;
    }

    public void setInternalizationPercentageStruct(InternalizationPercentageStruct internalizationPercentageStruct)
    {
        InternalizationPercentageStruct oldValue = this.internalizationPercentageStruct;
        this.internalizationPercentageStruct = internalizationPercentageStruct;
        firePropertyChange(STRUCT_CHANGE_EVENT, oldValue, internalizationPercentageStruct);
    }

    public short getAuctionType()
    {
        short result = 0;
        if(getInternalizationPercentageStruct() != null)
        {
            result = getInternalizationPercentageStruct().auctionType;
        }
        return result;
    }

    public void setAuctionType(short auctionType)
    {
        if(getInternalizationPercentageStruct() == null)
        {
            setInternalizationPercentageStruct(new InternalizationPercentageStruct());
        }
        short oldValue = getInternalizationPercentageStruct().auctionType;
        getInternalizationPercentageStruct().auctionType = auctionType;
        firePropertyChange(AUCTION_TYPE_CHANGE_EVENT, oldValue, auctionType);
    }

    public int getLowerRange()
    {
        int result = 0;
        if(getInternalizationPercentageStruct() != null)
        {
            result = getInternalizationPercentageStruct().lowerRange;
        }
        return result;
    }

    public void setLowerRange(int lowerRange)
    {
        if(getInternalizationPercentageStruct() == null)
        {
            setInternalizationPercentageStruct(new InternalizationPercentageStruct());
        }
        int oldValue = getInternalizationPercentageStruct().lowerRange;
        getInternalizationPercentageStruct().lowerRange = lowerRange;
        firePropertyChange(LOWER_RANGE_CHANGE_EVENT, oldValue, lowerRange);
    }

    public int getUpperRange()
    {
        int result = 0;
        if(getInternalizationPercentageStruct() != null)
        {
            result = getInternalizationPercentageStruct().upperRange;
        }
        return result;
    }

    public void setUpperRange(int upperRange)
    {
        if(getInternalizationPercentageStruct() == null)
        {
            setInternalizationPercentageStruct(new InternalizationPercentageStruct());
        }
        int oldValue = getInternalizationPercentageStruct().upperRange;
        getInternalizationPercentageStruct().upperRange = upperRange;
        firePropertyChange(UPPER_RANGE_CHANGE_EVENT, oldValue, upperRange);
    }

    public double getPercentage()
    {
        double result = 0.0;
        if(getInternalizationPercentageStruct() != null)
        {
            result = getInternalizationPercentageStruct().percentage;
        }
        return result;
    }

    public void setPercentage(double percentage)
    {
        if(getInternalizationPercentageStruct() == null)
        {
            setInternalizationPercentageStruct(new InternalizationPercentageStruct());
        }
        double oldValue = getInternalizationPercentageStruct().percentage;
        getInternalizationPercentageStruct().percentage = percentage;
        firePropertyChange(PERCENTAGE_CHANGE_EVENT, oldValue, percentage);
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return InternalizationPercentageTradingPropertyGroup.TRADING_PROPERTY_TYPE;
    }

    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"auctionType", "lowerRange", "upperRange", "percentage"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
