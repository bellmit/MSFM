//
// -----------------------------------------------------------------------------------
// Source file: OpeningExchangePrescribedWidthGroup.java
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
import com.cboe.interfaces.domain.tradingProperty.ExchangePrescribedWidth;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyFactory;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * Represents a TradingPropertyGroup for the Opening Exchange Prescribed Width.
 */
public class StrategyEPWGroup extends AbstractTradingPropertyGroup
{
    public static final TradingPropertyType TRADING_PROPERTY_TYPE =
            TradingPropertyTypeImpl.STRATEGY_EPW;
    
    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public StrategyEPWGroup(String sessionName, int classKey)
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
    public StrategyEPWGroup(String sessionName, int classKey,
                                               PropertyServicePropertyGroup propertyGroup)
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
    public StrategyEPWGroup(String sessionName, int classKey, int versionNumber)
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
    public static StrategyEPWGroup getGroup(String sessionName, int classKey)
            throws SystemException, NotFoundException, DataValidationException, CommunicationException,
                   InvocationTargetException, AuthorizationException, TransactionFailedException
    {
        TradingPropertyFactory factory = TradingPropertyFactoryHome.find();
        TradingPropertyGroup newGroup = factory.getTradingPropertyGroup(sessionName, classKey,
                                                                        TRADING_PROPERTY_TYPE.getName());
        return (StrategyEPWGroup)newGroup;
    }

    public TradingProperty createNewTradingProperty(String sessionName, int classKey)
    {
        return new ExchangePrescribedWidthImpl(getTradingPropertyType(), sessionName, classKey);
    }    
    
    /**
     * Gets all the contained OpeningExchangePrescribedWidth Trading Properties.
     */
    public ExchangePrescribedWidth[] getExchangePrescribedWidthTradingProperties()
    {
        TradingProperty[] allTPs = getAllTradingProperties();
        ExchangePrescribedWidth[] castedTPs = new ExchangePrescribedWidth[allTPs.length];
        System.arraycopy(allTPs, 0, castedTPs, 0, allTPs.length);
        return castedTPs;
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return TRADING_PROPERTY_TYPE;
    }
}
