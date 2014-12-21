//
// -----------------------------------------------------------------------------------
// Source file: AuctionRangeTradingProperty.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.util.*;

import com.cboe.idl.tradingProperty.AuctionRangeStruct;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;
import com.cboe.domain.tradingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.util.StructBuilder;

/**
 * Represents a TradingProperty for a single AuctionRangeStruct
 */
public class AuctionRangeTradingProperty extends AbstractTradingProperty implements Comparable
{
    public static final String STRUCT_CHANGE_EVENT = "StructChange";
    public static final String AUCTION_TYPE_CHANGE_EVENT = "AuctionType";
    public static final String LOWER_LIMIT_CHANGE_EVENT = "LowerLimit";
    public static final String UPPER_LIMIT_CHANGE_EVENT = "UpperLimit";

    protected TradingPropertyType tradingPropertyType;

    private AuctionRangeStruct auctionRangeStruct;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AuctionRangeTradingProperty(String propertyName, String sessionName, int classKey)
    {
        super(propertyName, sessionName, classKey);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param auctionRangeStruct value for the trading property
     */
    public AuctionRangeTradingProperty(String propertyName, String sessionName, int classKey,
                                       AuctionRangeStruct auctionRangeStruct)
    {
        this(propertyName, sessionName, classKey);
        setAuctionRangeStruct(auctionRangeStruct);
    }

    public int hashCode()
    {
        return getAuctionType();
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        AuctionRangeTradingProperty clonedTradingProperty = (AuctionRangeTradingProperty) super.clone();
        AuctionRangeStruct clonedStruct =
                StructBuilder.cloneAuctionRangeStruct(getAuctionRangeStruct());
        clonedTradingProperty.auctionRangeStruct = clonedStruct;

        return clonedTradingProperty;
    }

    public int compareTo(Object object)
    {
        int result;
        short myValue = getAuctionType();
        short theirValue = ((AuctionRangeTradingProperty) object).getAuctionType();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    public AuctionRangeStruct getAuctionRangeStruct()
    {
        return auctionRangeStruct;
    }

    public void setAuctionRangeStruct(AuctionRangeStruct auctionRangeStruct)
    {
        AuctionRangeStruct oldValue = this.auctionRangeStruct;
        this.auctionRangeStruct = auctionRangeStruct;
        firePropertyChange(STRUCT_CHANGE_EVENT, oldValue, auctionRangeStruct);
    }

    public short getAuctionType()
    {
        short result = 0;
        if(getAuctionRangeStruct() != null)
        {
            result = getAuctionRangeStruct().auctionType;
        }
        return result;
    }

    public void setAuctionType(short auctionType)
    {
        if(getAuctionRangeStruct() == null)
        {
            setAuctionRangeStruct(new AuctionRangeStruct());
        }
        short oldValue = getAuctionRangeStruct().auctionType;
        getAuctionRangeStruct().auctionType = auctionType;
        firePropertyChange(AUCTION_TYPE_CHANGE_EVENT, oldValue, auctionType);
    }

    public int getUpperLimit()
    {
        int result = 0;
        if(getAuctionRangeStruct() != null)
        {
            result = getAuctionRangeStruct().upperLimit;
        }
        return result;
    }

    public void setUpperLimit(int upperLimit)
    {
        if(getAuctionRangeStruct() == null)
        {
            setAuctionRangeStruct(new AuctionRangeStruct());
        }
        int oldValue = getAuctionRangeStruct().upperLimit;
        getAuctionRangeStruct().upperLimit = upperLimit;
        firePropertyChange(UPPER_LIMIT_CHANGE_EVENT, oldValue, upperLimit);
    }

    public int getLowerLimit()
    {
        int result = 0;
        if(getAuctionRangeStruct() != null)
        {
            result = getAuctionRangeStruct().lowerLimit;
        }
        return result;
    }

    public void setLowerLimit(int lowerLimit)
    {
        if(getAuctionRangeStruct() == null)
        {
            setAuctionRangeStruct(new AuctionRangeStruct());
        }
        int oldValue = getAuctionRangeStruct().lowerLimit;
        getAuctionRangeStruct().lowerLimit = lowerLimit;
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
        String[] forcedEntries = {"auctionType", "lowerLimit", "upperLimit"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
