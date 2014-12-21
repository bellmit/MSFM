//
// -----------------------------------------------------------------------------------
// Source file: DpmRightsScaleTradingProperty.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.util.*;

import com.cboe.idl.tradingProperty.DpmRightsScaleStruct;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;
import com.cboe.domain.tradingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.util.StructBuilder;

/**
 * Represents a TradingProperty for a single DpmRightsScaleStruct
 */
public class DpmRightsScaleTradingProperty extends AbstractTradingProperty implements Comparable
{
    public static final String STRUCT_CHANGE_EVENT = "StructChange";
    public static final String HIGH_NBR_PARTICIPANTS_CHANGE_EVENT = "HighNBR";
    public static final String LOW_NBR_PARTICIPANTS_CHANGE_EVENT = "LowNBR";
    public static final String SCALE_PERCENTAGE_CHANGE_EVENT = "ScalePercentage";

    private DpmRightsScaleStruct dpmRightsScaleStruct;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public DpmRightsScaleTradingProperty(String sessionName, int classKey)
    {
        super(DpmRightsScaleTradingPropertyGroup.TRADING_PROPERTY_TYPE.getName(), sessionName, classKey);
    }

    /**
     * Constructor that separately initializes each data attribute.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param dpmRightsScaleStruct value for the trading property
     */
    public DpmRightsScaleTradingProperty(String sessionName, int classKey, DpmRightsScaleStruct dpmRightsScaleStruct)
    {
        this(sessionName, classKey);
        setDpmRightsScaleStruct(dpmRightsScaleStruct);
    }

    public int hashCode()
    {
        return getLowNbrParticipants();
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        DpmRightsScaleTradingProperty clonedTradingProperty = (DpmRightsScaleTradingProperty) super.clone();
        DpmRightsScaleStruct clonedStruct =
                StructBuilder.cloneDpmRightsScaleStruct(getDpmRightsScaleStruct());
        clonedTradingProperty.dpmRightsScaleStruct = clonedStruct;

        return clonedTradingProperty;
    }

    public int compareTo(Object object)
    {
        int result;
        int myValue = getLowNbrParticipants();
        int theirValue = ((DpmRightsScaleTradingProperty) object).getLowNbrParticipants();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overridden to return the String representation of the low nbr participants value.
     */
    public String getPropertyName()
    {
        return Integer.toString(getLowNbrParticipants());
    }

    public DpmRightsScaleStruct getDpmRightsScaleStruct()
    {
        return dpmRightsScaleStruct;
    }

    public void setDpmRightsScaleStruct(DpmRightsScaleStruct dpmRightsScaleStruct)
    {
        DpmRightsScaleStruct oldValue = this.dpmRightsScaleStruct;
        this.dpmRightsScaleStruct = dpmRightsScaleStruct;
        firePropertyChange(STRUCT_CHANGE_EVENT, oldValue, dpmRightsScaleStruct);
    }

    public int getLowNbrParticipants()
    {
        int result = 0;
        if(getDpmRightsScaleStruct() != null)
        {
            result = getDpmRightsScaleStruct().lowNbrParticipants;
        }
        return result;
    }

    public void setLowNbrParticipants(int lowNbrParticipants)
    {
        if(getDpmRightsScaleStruct() == null)
        {
            setDpmRightsScaleStruct(new DpmRightsScaleStruct());
        }
        int oldValue = getDpmRightsScaleStruct().lowNbrParticipants;
        getDpmRightsScaleStruct().lowNbrParticipants = lowNbrParticipants;
        firePropertyChange(LOW_NBR_PARTICIPANTS_CHANGE_EVENT, oldValue, lowNbrParticipants);
    }

    public int getHighNbrParticipants()
    {
        int result = 0;
        if(getDpmRightsScaleStruct() != null)
        {
            result = getDpmRightsScaleStruct().highNbrParticipants;
        }
        return result;
    }

    public void setHighNbrParticipants(int highNbrParticipants)
    {
        if(getDpmRightsScaleStruct() == null)
        {
            setDpmRightsScaleStruct(new DpmRightsScaleStruct());
        }
        int oldValue = getDpmRightsScaleStruct().highNbrParticipants;
        getDpmRightsScaleStruct().highNbrParticipants = highNbrParticipants;
        firePropertyChange(HIGH_NBR_PARTICIPANTS_CHANGE_EVENT, oldValue, highNbrParticipants);
    }

    public double getScalePercentage()
    {
        double result = 0;
        if(getDpmRightsScaleStruct() != null)
        {
            result = getDpmRightsScaleStruct().scalePercentage;
        }
        return result;
    }

    public void setScalePercentage(double scalePercentage)
    {
        if(getDpmRightsScaleStruct() == null)
        {
            setDpmRightsScaleStruct(new DpmRightsScaleStruct());
        }
        double oldValue = getDpmRightsScaleStruct().scalePercentage;
        getDpmRightsScaleStruct().scalePercentage = scalePercentage;
        firePropertyChange(SCALE_PERCENTAGE_CHANGE_EVENT, oldValue, scalePercentage);
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return DpmRightsScaleTradingPropertyGroup.TRADING_PROPERTY_TYPE;
    }

    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {"lowNbrParticipants", "highNbrParticipants", "scalePercentage"};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
