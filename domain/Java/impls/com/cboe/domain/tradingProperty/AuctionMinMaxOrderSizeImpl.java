//
// -----------------------------------------------------------------------------------
// Source file: AuctionMinMaxOrderSizeImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;
import com.cboe.interfaces.domain.tradingProperty.AuctionMinMaxOrderSize;

/**
 * Represents a TradingProperty for the Minimum and Maximum Order Size for an Auction.
 */
public class AuctionMinMaxOrderSizeImpl extends AbstractTradingProperty implements AuctionMinMaxOrderSize, Comparable
{
    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AuctionMinMaxOrderSizeImpl(String sessionName, int classKey)
    {
        super(AuctionMinMaxOrderSizeGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the Property to initialize
     * the trading property data with.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public AuctionMinMaxOrderSizeImpl(String sessionName, int classKey, Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param auctionType The auction type
     * @param maxOrderSize maximum order size for this auction type
     * @param minOrderSize minimum order size for this auction type
     */
    public AuctionMinMaxOrderSizeImpl(String sessionName, int classKey, short auctionType, int minOrderSize, int maxOrderSize)
    {
        this(sessionName, classKey);
        setAuctionType(auctionType);
        setMinOrderSize(minOrderSize);
        setMaxOrderSize(maxOrderSize);
    }

    /**
     * Constructs with the field values being parsed from the passed value
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public AuctionMinMaxOrderSizeImpl(String sessionName, int classKey, String value)
    {
        super(AuctionMinMaxOrderSizeGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey, value);
    }

    /**
     * Overridden to return the auction type.
     */
    public int hashCode()
    {
        return getAuctionType();
    }

    /**
     * Compares this object with the passed object based on the auction type
     */
    public int compareTo(Object object)
    {
        int result;
        short myValue = getAuctionType();
        short theirValue = ((AuctionMinMaxOrderSize) object).getAuctionType();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overridden to return the auction type.
     */
    public String getPropertyName()
    {
        return Integer.toString(getAuctionType());
    }

    /**
     * Gets the auction type
     */
    public short getAuctionType()
    {
        return (short) getInteger1();
    }

    /**
     * Sets the auction type
     */
    public void setAuctionType(short auctionType)
    {
        setInteger1(auctionType);
    }

    /**
     * Gets the maximum order size for this auction type
     */
    public int getMaxOrderSize()
    {
        return getInteger3();
    }

    /**
     * Sets the maximum order size for this auction type
     */
    public void setMaxOrderSize(int maxOrderSize)
    {
        setInteger3(maxOrderSize);
    }

    /**
     * Gets the minimum order size for this auction type
     */
    public int getMinOrderSize()
    {
        return getInteger2();
    }

    /**
     * Sets the minimum order size for this auction type
     */
    public void setMinOrderSize(int minOrderSize)
    {
        setInteger2(minOrderSize);
    }

    /**
     * Gets the TradingPropertyType for this group that identifies the type of this group.
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return AuctionMinMaxOrderSizeGroup.TRADING_PROPERTY_TYPE;
    }

    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by the desired property descriptor order from the BeanInfo.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"auctionType", "minOrderSize", "maxOrderSize"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
