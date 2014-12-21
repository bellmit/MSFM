package com.cboe.domain.routingProperty.key;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.domain.routingProperty.common.ComplexProductClass;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;

//-----------------------------------------------------------------------------------
//Source file: SessionFirmComplexClassOriginLevelKey
//
//
//Created: Feb 11, 2008
//-----------------------------------------------------------------------------------
//Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------


public class SessionFirmComplexClassOriginLevelKey extends SessionFirmClassOriginLevelKey
{
    public static final String COMPLEX_PRODUCT_CLASS_PROPERTY_NAME = "complexProductClass";

    public SessionFirmComplexClassOriginLevelKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionFirmComplexClassOriginLevelKey(String propertyName, String exchangeAcronym, String firmAcronym, String sessionName,
                                                int classKey, char origin, int level)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym, classKey, origin, level);
    }

    public SessionFirmComplexClassOriginLevelKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    protected SimpleComplexProductClass createProductClass(String sessionName, int classKey)
    {
        return new SimpleComplexProductClass(sessionName, classKey);
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
        super.setSimpleComplexProductClass(temp);
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int parentSize = 3;
        int index;
        if(keyElement.equalsIgnoreCase(COMPLEX_PRODUCT_CLASS_PROPERTY_NAME))
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
}
