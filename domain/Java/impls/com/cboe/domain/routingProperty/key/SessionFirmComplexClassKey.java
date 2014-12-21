//
// -----------------------------------------------------------------------------------
// Source file: SessionFirmComplexKey.java
//
// PACKAGE: com.cboe.domain.routingProperty.key
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.key;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.domain.routingProperty.common.ComplexProductClass;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.TradingSessionName;

public class SessionFirmComplexClassKey extends SessionFirmClassKey
{
    public static final String COMPLEX_PRODUCT_CLASS_PROPERTY_NAME = "complexProductClass";
    private static final int COMPLEX_PRODUCT_CLASS_PROPERTY_KEY_POSITION = 0;

    public SessionFirmComplexClassKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }


    public SessionFirmComplexClassKey(String propertyName, String firmAcronym, String exchangeAcronym,
                                      String sessionName, int classKey)
    {
        super(propertyName, firmAcronym, exchangeAcronym, sessionName, classKey);

    }

    public SessionFirmComplexClassKey(BasePropertyType type) throws DataValidationException
    {
        super(type);
    }

    public ComplexProductClass getComplexProductClass()
    {
        SimpleComplexProductClass temp = super.getSimpleComplexProductClass();
        return new ComplexProductClass(temp.getTradingSession(), temp.getClassKey());
    }

    public void setComplexProductClass(ComplexProductClass productClass)
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
        if(keyElement.equalsIgnoreCase(COMPLEX_PRODUCT_CLASS_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + COMPLEX_PRODUCT_CLASS_PROPERTY_KEY_POSITION;
        }
        else
        {
            index = super.getMaskIndex(keyElement);
        }
        return index;
    }
}
