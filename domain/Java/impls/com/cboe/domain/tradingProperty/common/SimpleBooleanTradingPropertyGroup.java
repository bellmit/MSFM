//
// -----------------------------------------------------------------------------------
// Source file: SimpleBooleanTradingPropertyGroup.java
//
// PACKAGE: com.cboe.domain.tradingProperty.common;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty.common;

import java.lang.reflect.InvocationTargetException;

import com.cboe.idl.cmiConstants.ProductClass;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.SimpleBooleanTradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingPropertyGroup;
import com.cboe.domain.tradingProperty.TradingPropertyFactoryHome;

/**
 * Represents a TradingPropertyGroup for a single simple boolean
 */
public class SimpleBooleanTradingPropertyGroup extends AbstractTradingPropertyGroup
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
    public SimpleBooleanTradingPropertyGroup(String sessionName, int classKey)
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
    public SimpleBooleanTradingPropertyGroup(String sessionName, int classKey, PropertyServicePropertyGroup propertyGroup)
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
    public static SimpleBooleanTradingPropertyGroup getGroup(String sessionName, int classKey,
                                                             TradingPropertyType tradingPropertyType)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        SimpleBooleanTradingPropertyGroup myGroup =
                (SimpleBooleanTradingPropertyGroup) TradingPropertyFactoryHome.find().getTradingPropertyGroup(sessionName,
                                                                                                   classKey,
                                                                                                   tradingPropertyType.getName());
        myGroup.setTradingPropertyType(tradingPropertyType);
        return myGroup;
    }

    /**
     * Provides a getter to get an instance of this TradingPropertyGroup, downcasted for convenience
     * and get default/session level value (classKey=0) on class level NotFoundException
     * @param sessionName to get TradingPropertyGroup for
     * @param classKey    to get TradingPropertyGroup for
     * @return instance of TradingPropertyGroup downcasted to this class type.
     * @throws SystemException           forwarded from TradingPropertyFactory
     * @throws AuthorizationException    forwarded from TradingPropertyFactory
     * @throws CommunicationException    forwarded from TradingPropertyFactory
     * @throws NotFoundException         forwarded from TradingPropertyFactory
     * @throws InvocationTargetException forwarded from TradingPropertyFactory
     * @throws DataValidationException   forwarded from TradingPropertyFactory
     */
    public static SimpleBooleanTradingPropertyGroup getGroupWithDefault(String sessionName, int classKey, TradingPropertyType tradingPropertyType)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        SimpleBooleanTradingPropertyGroup  myGroup = null;

        try
        {
            myGroup = getGroup( sessionName, classKey, tradingPropertyType );
        }
        catch( NotFoundException nfe )
        {
            if ( classKey != ProductClass.DEFAULT_CLASS_KEY )
            {
                myGroup = getGroup(sessionName, ProductClass.DEFAULT_CLASS_KEY, tradingPropertyType);
            }
            else
            {
                throw nfe;
            }
        }
        
        return myGroup;
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
        SimpleBooleanTradingPropertyImpl newTP =
                new SimpleBooleanTradingPropertyImpl(getTradingPropertyType().getFullName(), sessionName, classKey);
        newTP.setTradingPropertyType(getTradingPropertyType());
        return newTP;
    }

    /**
     * Gets the TradingPropertyType for this group that identifies the type of this group.
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * Allows the TradingPropertyType to be set for this TradingPropertyGroup since this implements a default
     * container for a simple boolean.
     * @param tradingPropertyType
     */
    public void setTradingPropertyType(TradingPropertyType tradingPropertyType)
    {
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Should only ever be one.
     */
    public SimpleBooleanTradingProperty getBooleanTradingProperty()
    {
        TradingProperty[] tradingProperties = getAllTradingProperties();
        if(tradingProperties.length > 0)
        {
            return (SimpleBooleanTradingProperty) tradingProperties[0];
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
