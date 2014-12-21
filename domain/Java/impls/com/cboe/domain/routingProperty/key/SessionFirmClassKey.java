package com.cboe.domain.routingProperty.key;
// -----------------------------------------------------------------------------------
// Source file: SessionFirmClassKey
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.key
// 
// Created: Jul 21, 2006 9:42:50 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.TradingSessionName;

public class SessionFirmClassKey extends AbstractBasePropertyKey
{
    public static final String PRODUCT_CLASS_PROPERTY_NAME = "simpleComplexProductClass";

    private static final int PRODUCT_CLASS_PROPERTY_KEY_POSITION = 0;

    protected SimpleComplexProductClass productClass;

    public SessionFirmClassKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionFirmClassKey(String propertyName, String firmAcronym, String exchangeAcronym, String sessionName,
                               int classKey)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.productClass = new SimpleComplexProductClass(sessionName, classKey);
    }

    public SessionFirmClassKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public int getClassKey()
    {
        return productClass.getClassKey();
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmClassKey newKey = (SessionFirmClassKey) super.clone();
        newKey.productClass = new SimpleComplexProductClass(getTradingSession().sessionName, getClassKey());
        return newKey;
    }

    public void setClassKey(int classKey)
    {
        this.productClass = new SimpleComplexProductClass(getTradingSession().sessionName, classKey);
        resetPropertyKey();
    }

    public void setSimpleComplexProductClass(SimpleComplexProductClass productClass)
    {
        setTradingSession(new TradingSessionName(productClass.getTradingSession()));
        this.productClass = productClass;
        resetPropertyKey();
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),
                                        new Integer(getClassKey()), getPropertyName()};

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
        int index = super.parsePropertyKey(propertyKey);

        String[] keyElements = splitPropertyKey(propertyKey);

        try
        {
            this.productClass = new SimpleComplexProductClass(tradingSessionName.sessionName,
                                                              Integer.parseInt(getKeyElement(keyElements, ++index)));
        }
        catch (NumberFormatException e)
        {
            String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "classKey",
                                                                 getKeyElement(keyElements, index));
            throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT_CLASS);
        }

        return index;
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {FIRM_PROPERTY_NAME, PRODUCT_CLASS_PROPERTY_NAME };
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    public SimpleComplexProductClass getSimpleComplexProductClass()
    {
        return productClass;
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
