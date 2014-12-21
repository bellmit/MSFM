//
// -----------------------------------------------------------------------------------
// Source file: SessionClassKey.java
//
// PACKAGE: com.cboe.domain.routingProperty.key
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.key;

import java.util.*;
import java.beans.PropertyDescriptor;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.common.TradingSessionName;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;

/**
 * Routing Property Key that uses Session and ProductClass as key elements
 */
public class SessionClassKey extends SessionKey
{
    public static final String PRODUCT_CLASS_PROPERTY_NAME = "simpleComplexProductClass";

    private static final int PRODUCT_CLASS_PROPERTY_KEY_POSITION = 0;

    protected SimpleComplexProductClass productClass;

    public SessionClassKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionClassKey(String propertyName, String sessionName, int classKey)
    {
        super(propertyName, sessionName);
        productClass = new SimpleComplexProductClass(sessionName, classKey);
    }

    public SessionClassKey(String propertyKey)
            throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        SessionClassKey newKey = (SessionClassKey) super.clone();
        newKey.productClass = new SimpleComplexProductClass(getTradingSession().sessionName, getClassKey());
        return newKey;
    }

    public int getClassKey()
    {
        return productClass.getClassKey();
    }

    public void setClassKey(int classKey)
    {
        productClass = new SimpleComplexProductClass(getTradingSession().sessionName, classKey);
        resetPropertyKey();
    }

    /**
     * order of key elements is: session, classKey, propertyName
     */
    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {getSessionName(), getClassKey(), getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * Parses the propertyKey to find the separate key values.
     *
     * Returns the index of the last key value used from the propertyKey's
     * parts (does not count the index of propertyName, which is always the
     * last part of the propertyKey).
     *
     * @param propertyKey Property Key
     * @throws DataValidationException
     */
    @SuppressWarnings({"UnusedCatchParameter"})
    protected int parsePropertyKey(String propertyKey)
            throws DataValidationException
    {
        int index = super.parsePropertyKey(propertyKey);

        String[] keyElements = splitPropertyKey(propertyKey);

        try
        {
            productClass = new SimpleComplexProductClass(tradingSessionName.sessionName,
                                                              Integer.parseInt(getKeyElement(keyElements, ++index)));
        }
        catch(NumberFormatException e)
        {
            String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "classKey", getKeyElement(keyElements, index));
            throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT_CLASS);
        }

        return index;
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by property name.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    public Comparator<PropertyDescriptor> getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {PRODUCT_CLASS_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    public SimpleComplexProductClass getSimpleComplexProductClass()
    {
        return productClass;
    }

    public void setSimpleComplexProductClass(SimpleComplexProductClass productClass)
    {
        setTradingSession(new TradingSessionName(productClass.getTradingSession()));
        this.productClass = productClass;
        resetPropertyKey();
    }

    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize() + 1;
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int index;
        int parentSize = super.getMaskSize();
        if(keyElement.equalsIgnoreCase(PRODUCT_CLASS_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + PRODUCT_CLASS_PROPERTY_KEY_POSITION;
        }
        else
        {
            index = super.getMaskIndex(keyElement);
        }
        return index;
    }

    @Override
    public String getKeyComponentName(int maskIndex)
    {
        String fieldName;
        switch(maskIndex - super.getMaskSize())
        {
            case PRODUCT_CLASS_PROPERTY_KEY_POSITION:
                fieldName = PRODUCT_CLASS_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }

}
