//
// -----------------------------------------------------------------------------------
// Source file: OpeningPriceValidationGroup.java
//
// PACKAGE: com.cboe.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.lang.reflect.InvocationTargetException;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.OpeningPriceValidation;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;

/**
 * Represents a TradingPropertyGroup for Opening Price Validation.
 */
public class OpeningPriceValidationGroup extends AbstractTradingPropertyGroup
{
    public static final TradingPropertyType TRADING_PROPERTY_TYPE = TradingPropertyTypeImpl.OPENING_PRICE_VALIDATION;

    private final Object defaultHashForMap = new Object();

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public OpeningPriceValidationGroup(String sessionName, int classKey)
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
    public OpeningPriceValidationGroup(String sessionName, int classKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException
    {
        super(sessionName, classKey, propertyGroup);
    }

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey    that this TradingProperty is for
     * @param versionNumber that this TradingProperty is for
     */
    public OpeningPriceValidationGroup(String sessionName, int classKey, int versionNumber)
    {
        super(sessionName, classKey, versionNumber);
    }

    /**
     * Provides a getter to get an instance of this TradingPropertyGroup, downcasted for convenience.
     * @param sessionName to get TradingPropertyGroup for
     * @param classKey to get TradingPropertyGroup for
     * @return instance of TradingPropertyGroup downcasted to this class type.
     * @throws SystemException forwarded from TradingPropertyFactory
     * @throws AuthorizationException forwarded from TradingPropertyFactory
     * @throws CommunicationException forwarded from TradingPropertyFactory
     * @throws NotFoundException forwarded from TradingPropertyFactory
     * @throws InvocationTargetException forwarded from TradingPropertyFactory
     * @throws DataValidationException forwarded from TradingPropertyFactory
     */
    public static OpeningPriceValidationGroup getGroup(String sessionName, int classKey)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyFactory factory = TradingPropertyFactoryHome.find();
        TradingPropertyGroup myGroup = factory.getTradingPropertyGroup(sessionName, classKey,
                                                                       TRADING_PROPERTY_TYPE.getName());
        return (OpeningPriceValidationGroup) myGroup;
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup#createNewTradingProperty(java.lang.String, int)
     */
    public TradingProperty createNewTradingProperty(String sessionName, int classKey)
    {
        return new OpeningPriceValidationImpl(sessionName, classKey);
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return TRADING_PROPERTY_TYPE;
    }

    /**
     * Gets all the contained OpeningPriceValidation TradingProperty's.
     */
    public OpeningPriceValidation[] getOpeningValidationTradingProperties()
    {
        TradingProperty[] allTPs = getAllTradingProperties();
        OpeningPriceValidation[] castedTPs = new OpeningPriceValidation[allTPs.length];
        System.arraycopy(allTPs, 0, castedTPs, 0, allTPs.length);
        return castedTPs;
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
