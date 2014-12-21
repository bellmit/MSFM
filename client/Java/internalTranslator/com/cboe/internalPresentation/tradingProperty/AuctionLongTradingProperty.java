//
// -----------------------------------------------------------------------------------
// Source file: AuctionMinPriceTradingProperty.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.util.*;

import com.cboe.idl.tradingProperty.AuctionLongStruct;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;
import com.cboe.domain.tradingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.util.StructBuilder;

/**
 * Represents a TradingProperty for a single AuctionLongStruct
 */
public class AuctionLongTradingProperty extends AbstractTradingProperty implements Comparable
{
    public static final String STRUCT_CHANGE_EVENT = "StructChange";
    public static final String AUCTION_TYPE_CHANGE_EVENT = "AuctionType";

    protected TradingPropertyType tradingPropertyType;

    private AuctionLongStruct auctionLongStruct;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AuctionLongTradingProperty(String propertyName, String sessionName, int classKey)
    {
        super(propertyName, sessionName, classKey);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param auctionLongStruct value for the trading property
     */
    public AuctionLongTradingProperty(String propertyName, String sessionName, int classKey,
                                      AuctionLongStruct auctionLongStruct)
    {
        this(propertyName, sessionName, classKey);
        setAuctionLongStruct(auctionLongStruct);
    }

    public int hashCode()
    {
        return getAuctionType();
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        AuctionLongTradingProperty clonedTradingProperty = (AuctionLongTradingProperty) super.clone();
        AuctionLongStruct clonedStruct =
                StructBuilder.cloneAuctionLongStruct(getAuctionLongStruct());
        clonedTradingProperty.auctionLongStruct = clonedStruct;

        return clonedTradingProperty;
    }

    public int compareTo(Object object)
    {
        int result;
        short myValue = getAuctionType();
        short theirValue = ((AuctionLongTradingProperty) object).getAuctionType();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    public AuctionLongStruct getAuctionLongStruct()
    {
        return auctionLongStruct;
    }

    public void setAuctionLongStruct(AuctionLongStruct auctionLongStruct)
    {
        AuctionLongStruct oldValue = this.auctionLongStruct;
        this.auctionLongStruct = auctionLongStruct;
        firePropertyChange(STRUCT_CHANGE_EVENT, oldValue, auctionLongStruct);
    }

    public short getAuctionType()
    {
        short result = 0;
        if(getAuctionLongStruct() != null)
        {
            result = getAuctionLongStruct().auctionType;
        }
        return result;
    }

    public void setAuctionType(short auctionType)
    {
        if(getAuctionLongStruct() == null)
        {
            setAuctionLongStruct(new AuctionLongStruct());
        }
        short oldValue = getAuctionLongStruct().auctionType;
        getAuctionLongStruct().auctionType = auctionType;
        firePropertyChange(AUCTION_TYPE_CHANGE_EVENT, oldValue, auctionType);
    }

    public int getIntValue()
    {
        int result = 0;
        if(getAuctionLongStruct() != null)
        {
            result = getAuctionLongStruct().value;
        }
        return result;
    }

    public void setIntValue(int intValue)
    {
        if(getAuctionLongStruct() == null)
        {
            setAuctionLongStruct(new AuctionLongStruct());
        }
        int oldValue = getAuctionLongStruct().value;
        getAuctionLongStruct().value = intValue;
        firePropertyChange(INTEGER1_CHANGE_EVENT, oldValue, intValue);
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
        String[] forcedEntries = {"auctionType", "intValue"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
