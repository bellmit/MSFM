//
// -----------------------------------------------------------------------------------
// Source file: SimpleIntegerOldTradingPropertyGroup.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty.common;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty.common;

import java.lang.reflect.InvocationTargetException;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.SimpleIntegerTradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.internalPresentation.tradingProperty.AbstractOldTradingPropertyGroup;

import com.cboe.domain.tradingProperty.TradingPropertyFactoryHome;
import com.cboe.domain.tradingProperty.common.SimpleIntegerTradingPropertyImpl;

/**
 * Represents an OldTradingPropertyGroup for a single simple integer
 */
public class SimpleIntegerOldTradingPropertyGroup extends AbstractOldTradingPropertyGroup
{
    protected TradingPropertyType tradingPropertyType;

    private final Object defaultHashForMap = new Object();

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public SimpleIntegerOldTradingPropertyGroup(String sessionName, int classKey)
    {
        super(sessionName, classKey);
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the
     * PropertyServicePropertyGroup to initialize the sub-classes trading property data with.
     * @param sessionName that this TradingPropertyGroup is for
     * @param classKey that this TradingPropertyGroup is for
     * @param propertyGroup to initialize with
     */
    public SimpleIntegerOldTradingPropertyGroup(String sessionName, int classKey,
                                                PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException
    {
        super(sessionName, classKey, propertyGroup);
    }

    /**
     * Provides a getter to get an instance of this TradingPropertyGroup, downcasted for convenience.
     * @param sessionName to get TradingPropertyGroup for
     * @param classKey to get TradingPropertyGroup for
     * @param tradingPropertyType to get TradingPropertyGroup for
     * @return instance of TradingPropertyGroup downcasted to this class type.
     * @throws com.cboe.exceptions.SystemException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.AuthorizationException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.CommunicationException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.NotFoundException forwarded from TradingPropertyFactory
     * @throws java.lang.reflect.InvocationTargetException forwarded from TradingPropertyFactory
     * @throws com.cboe.exceptions.DataValidationException forwarded from TradingPropertyFactory
     */
    public static SimpleIntegerOldTradingPropertyGroup getGroup(String sessionName, int classKey,
                                                                TradingPropertyType tradingPropertyType)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyGroup myGroup =
                TradingPropertyFactoryHome.find().getTradingPropertyGroup(sessionName, classKey,
                                                                          tradingPropertyType.getName());
        SimpleIntegerOldTradingPropertyGroup castedGroup = (SimpleIntegerOldTradingPropertyGroup) myGroup;
        castedGroup.setTradingPropertyType(tradingPropertyType);
        return castedGroup;
    }

    /**
     * Provides the maximum number of Trading Properties this particular group implementation may allow.
     * @return This implementation will always return 1.
     */
    public int getMaxTradingPropertiesAllowed()
    {
        return 1;
    }

    /**
     * Create a new implementation specific TradingProperty.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @return new TradingProperty
     */
    public TradingProperty createNewTradingProperty(String sessionName, int classKey)
    {
        SimpleIntegerTradingPropertyImpl newTP =
                new SimpleIntegerTradingPropertyImpl(getTradingPropertyType().getFullName(), sessionName, classKey);
        newTP.setTradingPropertyType(getTradingPropertyType());
        return newTP;
    }

    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    public void setTradingPropertyType(TradingPropertyType tradingPropertyType)
    {
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Should only ever be one.
     */
    public SimpleIntegerTradingProperty getIntegerTradingProperty()
    {
        TradingProperty[] tradingProperties = getAllTradingProperties();
        if(tradingProperties.length > 0)
        {
            return (SimpleIntegerTradingProperty) tradingProperties[0];
        }
        else
        {
            return null;
        }
    }

    /**
     * This method is used to get an Object to be used as the key for adding, obtaining and removing the
     * TradingProperty's from the underlying Map collection returned from getTradingPropertyMap(). This implementation
     * always returns the exact same instance of an Object per instance of this group, since this implementation may
     * only have one TradingProperty. This ensures that only one will exist per instance of this group, since a previous
     * one will always get over-written since its key will always hash the same and be equal.
     * @param tradingProperty to get key Object for
     * @return same instance of an Object per instance of this TradingPropertyGroup
     */
    protected Object getMapKeyForTradingProperty(TradingProperty tradingProperty)
    {
        return defaultHashForMap;
    }
}
