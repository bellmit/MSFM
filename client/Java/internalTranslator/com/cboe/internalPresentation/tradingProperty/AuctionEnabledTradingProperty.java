//
// -----------------------------------------------------------------------------------
// Source file: AuctionEnabledTradingProperty.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.util.*;

import com.cboe.idl.tradingProperty.AuctionBooleanStruct;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;
import com.cboe.domain.tradingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.util.StructBuilder;

/**
 * Represents a TradingProperty for a single AuctionBooleanStruct
 */
public class AuctionEnabledTradingProperty extends AbstractTradingProperty implements Comparable
{
    public static final String STRUCT_CHANGE_EVENT = "StructChange";
    public static final String AUCTION_TYPE_CHANGE_EVENT = "AuctionType";
    public static final String ENABLED_CHANGE_EVENT = "Enabled";

    private AuctionBooleanStruct auctionBooleanStruct;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AuctionEnabledTradingProperty(String sessionName, int classKey)
    {
        super(AuctionEnabledTradingPropertyGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param auctionBooleanStruct value for the trading property
     */
    public AuctionEnabledTradingProperty(String sessionName, int classKey,
                                         AuctionBooleanStruct auctionBooleanStruct)
    {
        this(sessionName, classKey);
        setAuctionBooleanStruct(auctionBooleanStruct);
    }

    public int hashCode()
    {
        return getAuctionType();
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        AuctionEnabledTradingProperty clonedTradingProperty = (AuctionEnabledTradingProperty) super.clone();
        AuctionBooleanStruct clonedStruct =
                StructBuilder.cloneAuctionBooleanStruct(getAuctionBooleanStruct());
        clonedTradingProperty.auctionBooleanStruct = clonedStruct;

        return clonedTradingProperty;
    }

    public int compareTo(Object object)
    {
        int result;
        short myValue = getAuctionType();
        short theirValue = ((AuctionEnabledTradingProperty) object).getAuctionType();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    public AuctionBooleanStruct getAuctionBooleanStruct()
    {
        return auctionBooleanStruct;
    }

    public void setAuctionBooleanStruct(AuctionBooleanStruct auctionBooleanStruct)
    {
        AuctionBooleanStruct oldValue = this.auctionBooleanStruct;
        this.auctionBooleanStruct = auctionBooleanStruct;
        firePropertyChange(STRUCT_CHANGE_EVENT, oldValue, auctionBooleanStruct);
    }

    public short getAuctionType()
    {
        short result = 0;
        if(getAuctionBooleanStruct() != null)
        {
            result = getAuctionBooleanStruct().auctionType;
        }
        return result;
    }

    public void setAuctionType(short auctionType)
    {
        if(getAuctionBooleanStruct() == null)
        {
            setAuctionBooleanStruct(new AuctionBooleanStruct());
        }
        short oldValue = getAuctionBooleanStruct().auctionType;
        getAuctionBooleanStruct().auctionType = auctionType;
        firePropertyChange(AUCTION_TYPE_CHANGE_EVENT, oldValue, auctionType);
    }

    public boolean getEnabled()
    {
        boolean result = false;
        if(getAuctionBooleanStruct() != null)
        {
            result = getAuctionBooleanStruct().value;
        }
        return result;
    }

    public void setEnabled(boolean enabled)
    {
        if(getAuctionBooleanStruct() == null)
        {
            setAuctionBooleanStruct(new AuctionBooleanStruct());
        }
        boolean oldValue = getAuctionBooleanStruct().value;
        getAuctionBooleanStruct().value = enabled;
        firePropertyChange(ENABLED_CHANGE_EVENT, oldValue, enabled);
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return AuctionEnabledTradingPropertyGroup.TRADING_PROPERTY_TYPE;
    }

    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"auctionType", "enabled"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
