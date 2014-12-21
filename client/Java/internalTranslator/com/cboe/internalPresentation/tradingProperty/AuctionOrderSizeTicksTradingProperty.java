//
// -----------------------------------------------------------------------------------
// Source file: AuctionOrderSizeTicksTradingProperty.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.util.*;

import com.cboe.idl.tradingProperty.AuctionOrderSizeTicksStruct;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;
import com.cboe.domain.tradingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.util.StructBuilder;

/**
 * Represents a TradingProperty for a single AuctionOrderSizeTicksStruct
 */
public class AuctionOrderSizeTicksTradingProperty extends AbstractTradingProperty implements Comparable
{
    public static final String STRUCT_CHANGE_EVENT = "StructChange";
    public static final String AUCTION_TYPE_CHANGE_EVENT = "AuctionType";
    public static final String ORDER_SIZE_CHANGE_EVENT = "OrderSize";
    public static final String TICKS_ABOVE_NBBO_CHANGE_EVENT = "TicksAboveNBBO";


    private AuctionOrderSizeTicksStruct auctionOrderSizeTicksStruct;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AuctionOrderSizeTicksTradingProperty(String sessionName, int classKey)
    {
        super(AuctionOrderSizeTicksTradingPropertyGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param auctionOrderSizeTicksStruct value for the trading property
     */
    public AuctionOrderSizeTicksTradingProperty(String sessionName, int classKey,
                                                AuctionOrderSizeTicksStruct auctionOrderSizeTicksStruct)
    {
        this(sessionName, classKey);
        setAuctionOrderSizeTicksStruct(auctionOrderSizeTicksStruct);
    }

    public int hashCode()
    {
        return getAuctionType();
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        AuctionOrderSizeTicksTradingProperty clonedTradingProperty = (AuctionOrderSizeTicksTradingProperty) super.clone();
        AuctionOrderSizeTicksStruct clonedStruct =
                StructBuilder.cloneAuctionOrderSizeTicksStruct(getAuctionOrderSizeTicksStruct());
        clonedTradingProperty.auctionOrderSizeTicksStruct = clonedStruct;

        return clonedTradingProperty;
    }

    public int compareTo(Object object)
    {
        int result;
        short myValue = getAuctionType();
        short theirValue = ((AuctionOrderSizeTicksTradingProperty) object).getAuctionType();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    public AuctionOrderSizeTicksStruct getAuctionOrderSizeTicksStruct()
    {
        return auctionOrderSizeTicksStruct;
    }

    public void setAuctionOrderSizeTicksStruct(AuctionOrderSizeTicksStruct auctionOrderSizeTicksStruct)
    {
        AuctionOrderSizeTicksStruct oldValue = this.auctionOrderSizeTicksStruct;
        this.auctionOrderSizeTicksStruct = auctionOrderSizeTicksStruct;
        firePropertyChange(STRUCT_CHANGE_EVENT, oldValue, auctionOrderSizeTicksStruct);
    }

    public short getAuctionType()
    {
        short result = 0;
        if(getAuctionOrderSizeTicksStruct() != null)
        {
            result = getAuctionOrderSizeTicksStruct().auctionType;
        }
        return result;
    }

    public void setAuctionType(short auctionType)
    {
        if(getAuctionOrderSizeTicksStruct() == null)
        {
            setAuctionOrderSizeTicksStruct(new AuctionOrderSizeTicksStruct());
        }
        short oldValue = getAuctionOrderSizeTicksStruct().auctionType;
        getAuctionOrderSizeTicksStruct().auctionType = auctionType;
        firePropertyChange(AUCTION_TYPE_CHANGE_EVENT, oldValue, auctionType);
    }

    public int getOrderSize()
    {
        int result = 0;
        if(getAuctionOrderSizeTicksStruct() != null)
        {
            result = getAuctionOrderSizeTicksStruct().orderSize;
        }
        return result;
    }

    public void setOrderSize(int orderSize)
    {
        if(getAuctionOrderSizeTicksStruct() == null)
        {
            setAuctionOrderSizeTicksStruct(new AuctionOrderSizeTicksStruct());
        }
        int oldValue = getAuctionOrderSizeTicksStruct().orderSize;
        getAuctionOrderSizeTicksStruct().orderSize = orderSize;
        firePropertyChange(ORDER_SIZE_CHANGE_EVENT, oldValue, orderSize);
    }

    public int getTicksAboveNBBO()
    {
        int result = 0;
        if(getAuctionOrderSizeTicksStruct() != null)
        {
            result = getAuctionOrderSizeTicksStruct().ticksAboveNBBO;
        }
        return result;
    }

    public void setTicksAboveNBBO(int ticksAboveNBBO)
    {
        if(getAuctionOrderSizeTicksStruct() == null)
        {
            setAuctionOrderSizeTicksStruct(new AuctionOrderSizeTicksStruct());
        }
        int oldValue = getAuctionOrderSizeTicksStruct().ticksAboveNBBO;
        getAuctionOrderSizeTicksStruct().ticksAboveNBBO = ticksAboveNBBO;
        firePropertyChange(TICKS_ABOVE_NBBO_CHANGE_EVENT, oldValue, ticksAboveNBBO);
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return AuctionOrderSizeTicksTradingPropertyGroup.TRADING_PROPERTY_TYPE;
    }

    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"auctionType", "orderSize", "ticksAboveNBBO"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
