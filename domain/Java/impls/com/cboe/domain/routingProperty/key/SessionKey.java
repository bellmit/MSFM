package com.cboe.domain.routingProperty.key;
// -----------------------------------------------------------------------------------
// Source file: SessionKey
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Jul 21, 2006 9:38:53 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.TradingSessionName;

/**
 * Routing Property Key that uses Session, but not Firm/Exchange
 */
public class SessionKey extends AbstractBasePropertyKey
{
    public SessionKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionKey(String propertyName, String sessionName)
    {
        super(propertyName, sessionName, "", "");
    }

    public SessionKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        return (SessionKey) super.clone();
    }

    /**
     * order of key elements is: session, propertyName
     */
    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {getSessionName(), getPropertyName()};
        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * Parses the propertyKey to find the separate key values.
     *
     * Returns the index of the last key value used from the propertyKey's
     * parts (does not count the index of propertyName, which is always the
     * last part of the propertyKey).
     *
     * @param propertyKey
     * @throws DataValidationException
     */
    protected int parsePropertyKey(String propertyKey) throws DataValidationException
    {
        String[] keyElements = splitPropertyKey(propertyKey);

        this.tradingSessionName = new TradingSessionName(getKeyElement(keyElements, 0));

        this.propertyName = getKeyElement(keyElements, keyElements.length-1);

        return 0;
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    // TODO instead of SessionKey extending AbstractBasePropertyKey, the latter should extend the former (both renamed)
    // TODO because latter adds exchange-firm to session. until then we have to play games (like -2) with the mask
    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize() - 2; // there is no exchange-firm
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int index;
        if(keyElement.equalsIgnoreCase(TRADING_SESSION_PROPERTY_NAME))
        {
            index = super.getMaskIndex(keyElement);
        }
        else
        {
            index = -1;
        }
        return index;
    }

    @Override
    public String getKeyComponentName(int maskIndex)
    {
        String fieldName;
        if (maskIndex <= getMaskSize())
        {
            fieldName = super.getKeyComponentName(maskIndex);
        }
        else
        {
            fieldName = super.getKeyComponentName(-1);
        }
        return fieldName;
    }
}
