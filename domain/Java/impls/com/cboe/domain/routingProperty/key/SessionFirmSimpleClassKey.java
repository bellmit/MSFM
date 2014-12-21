//
// -----------------------------------------------------------------------------------
// Source file: SessionFirmSimpleClassKey.java
//
// PACKAGE: com.cboe.domain.routingProperty.key
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.key;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.domain.routingProperty.common.SimpleProductClass;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.TradingSessionName;

public class SessionFirmSimpleClassKey extends SessionFirmClassKey
{
    public static final String SIMPLE_PRODUCT_CLASS_PROPERTY_NAME = "simpleProductClass";
    private static final int SIMPLE_PRODUCT_CLASS_PROPERTY_KEY_POSITION = 0;
    public SessionFirmSimpleClassKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }


    public SessionFirmSimpleClassKey(String propertyName, String firmAcronym, String exchangeAcronym,
                               String sessionName, int classKey)
    {
        super(propertyName, firmAcronym, exchangeAcronym, sessionName, classKey);

    }

    public SessionFirmSimpleClassKey(BasePropertyType type) throws DataValidationException
    {
        super(type);
    }

    public SimpleProductClass getSimpleProductClass()
    {
        SimpleComplexProductClass temp = super.getSimpleComplexProductClass();
        return new SimpleProductClass(temp.getTradingSession(), temp.getClassKey());
    }

    public void setSimpleProductClass(SimpleProductClass productClass)
    {
        SimpleComplexProductClass temp = new SimpleComplexProductClass(productClass.getTradingSession(),
                                                                       productClass.getClassKey());

        setTradingSession(new TradingSessionName(productClass.getTradingSession()));
        this.productClass = temp;
        resetPropertyKey();
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int parentSize = 3;
        int index;
        if(keyElement.equalsIgnoreCase(SIMPLE_PRODUCT_CLASS_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + SIMPLE_PRODUCT_CLASS_PROPERTY_KEY_POSITION;
        }
        else
        {
            index = super.getMaskIndex(keyElement);
        }
        return index;
    }
}
