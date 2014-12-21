//
// -----------------------------------------------------------------------------------
// Source file: SessionClassProductKey.java
//
// PACKAGE: com.cboe.domain.routingProperty.key
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.key;

import java.util.*;

import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.RoutingPropertyHelper;
import com.cboe.domain.routingProperty.common.TradingSessionName;

/**
 * Routing property key containing sessionName, classKey, productKey, and propertyName.
 */
public class SessionClassProductKey extends SessionClassKey
{
    public static final String PRODUCT_PROPERTY_NAME = "sessionProduct";

    private static final int PRODUCT_PROPERTY_KEY_POSITION = 0;

    protected int productKey;

    private SessionProductStruct sessionProductStruct;

    public SessionClassProductKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionClassProductKey(String propertyName, String sessionName, int classKey, int productKey)
    {
        super(propertyName, sessionName, classKey);
        this.productKey = productKey;
    }

    public SessionClassProductKey(String propertyKey)
            throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        SessionClassProductKey newKey = (SessionClassProductKey) super.clone();
        newKey.productKey = productKey;

        return newKey;
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
    protected int parsePropertyKey(String propertyKey)
            throws DataValidationException
    {
        int index = super.parsePropertyKey(propertyKey);

        String[] keyElements = splitPropertyKey(propertyKey);
        try
        {
            this.productKey = Integer.parseInt(getKeyElement(keyElements, ++index));
        }
        catch(NumberFormatException e)
        {
            String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "productKey", getKeyElement(keyElements, index));
            throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT);
        }

        return index;
    }

    /**
     * order of key elements is: session, classKey, originCode, propertyName
     */
    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {getTradingSession(),
                new Integer(getClassKey()), new Integer(getProductKey()),
                getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {PRODUCT_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    public int getProductKey()
    {
        return productKey;
    }

    public void setProductKey(int productKey)
    {
        this.productKey = productKey;
        resetPropertyKey();
    }

    // the following methods are by Java Reflections
    public void setSessionProduct(SessionProductStruct struct)
    {
        this.sessionProductStruct = struct;
        if(sessionProductStruct == null)
        {
            sessionProductStruct = RoutingPropertyHelper.buildSessionProductStruct();
        }
        setTradingSession(new TradingSessionName(sessionProductStruct.sessionName));
        setClassKey(sessionProductStruct.productStruct.productKeys.classKey);
        setProductKey(sessionProductStruct.productStruct.productKeys.productKey);
    }

    public SessionProductStruct getSessionProduct()
    {
        if(sessionProductStruct == null)
        {
            sessionProductStruct = RoutingPropertyHelper.buildSessionProductStruct();
            sessionProductStruct.productStruct.productKeys.classKey = getClassKey();
            sessionProductStruct.productStruct.productKeys.productKey = getProductKey();
            sessionProductStruct.sessionName = getSessionName();
        }

        return sessionProductStruct;
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
        if(keyElement.equalsIgnoreCase(PRODUCT_PROPERTY_NAME))
        {
            index = parentSize + PRODUCT_PROPERTY_KEY_POSITION;
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
            case PRODUCT_PROPERTY_KEY_POSITION:
                fieldName = PRODUCT_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
