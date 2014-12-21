//
// -----------------------------------------------------------------------------------
// Source file: PDPMRightsScalesImpl.java
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
import com.cboe.interfaces.domain.tradingProperty.PDPMRightsScales;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * Represents a TradingProperty for PDPM Rights Scales
 */
public class PDPMRightsScalesImpl extends AbstractTradingProperty implements PDPMRightsScales
{
    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public PDPMRightsScalesImpl(String sessionName, int classKey)
    {
        super(PDPMRightsScalesGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the Property to initialize
     * the trading property data with.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public PDPMRightsScalesImpl(String sessionName, int classKey, Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param lowNbr LowNbrParticipants
     * @param highNbr HighNbrParticipants
     * @param scale ScalePercentage
     */
    public PDPMRightsScalesImpl(String sessionName, int classKey, int lowNbr, int highNbr, double scale)
    {
        this(sessionName, classKey);
        setLowNbrParticipants(lowNbr);
        setHighNbrParticipants(highNbr);
        setScalePercentage(scale);
    }

    /**
     * Constructs with the field values being parsed from the passed value
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public PDPMRightsScalesImpl(String sessionName, int classKey, String value)
    {
        super(PDPMRightsScalesGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey, value);
    }

    /**
     * Overridden to return the getLowNbrParticipants()
     */
    public int hashCode()
    {
        return getLowNbrParticipants();
    }

    /**
     * Compares based on getLowNbrParticipants()
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getLowNbrParticipants();
        int theirValue = ((PDPMRightsScales) object).getLowNbrParticipants();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overriden to return the getLowNbrParticipants(), as a String
     * @return
     */
    public String getPropertyName()
    {
        return Integer.toString(getLowNbrParticipants());
    }

    /**
     * Sets the super's integer1 attribute
     */
    public void setLowNbrParticipants(int val)
    {
        setInteger1(val);
    }

    /**
     * Sets the super's integer2 attribute
     */
    public void setHighNbrParticipants(int val)
    {
        setInteger2(val);
    }

    /**
     * Sets the super's double1 attribute
     */
    public void setScalePercentage(double val)
    {
        setDouble1(val);
    }

    /**
     * Gets the super's integer1 attribute
     */
    public int getLowNbrParticipants()
    {
        return getInteger1();
    }

    /**
     * Gets the super's integer2 attribute
     */
    public int getHighNbrParticipants()
    {
        return getInteger2();
    }

    /**
     * Gets the super's double1 attribute
     */
    public double getScalePercentage()
    {
        return getDouble1();
    }

    /**
     * Gets the TradingPropertyType for this group that identifies the type of this group.
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return PDPMRightsScalesGroup.TRADING_PROPERTY_TYPE;
    }

    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by the desired property descriptor order from the BeanInfo.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"lowNbrParticipants", "highNbrParticipants", "scalePercentage"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
