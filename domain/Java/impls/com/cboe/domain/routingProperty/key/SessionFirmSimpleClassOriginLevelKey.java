package com.cboe.domain.routingProperty.key;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.SimpleProductClass;

//-----------------------------------------------------------------------------------
//Source file: SessionFirmSimpleClassOriginLevelKey
//
//
//Created: Feb 11, 2008
//-----------------------------------------------------------------------------------
//Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------


public class SessionFirmSimpleClassOriginLevelKey extends SessionFirmClassOriginLevelKey
{
    public static final String SIMPLE_PRODUCT_CLASS_PROPERTY_NAME = "simpleProductClass";
    
    public SessionFirmSimpleClassOriginLevelKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionFirmSimpleClassOriginLevelKey(String propertyName, String exchangeAcronym, String firmAcronym, String sessionName,
                                          int classKey, char origin, int level)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym, classKey, origin,level);
    }

    public SessionFirmSimpleClassOriginLevelKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    protected SimpleComplexProductClass createProductClass(String sessionName, int classKey)
    {
        return new SimpleComplexProductClass(sessionName, classKey);
    }

    public SimpleProductClass getSimpleProductClass()
    {
        SimpleComplexProductClass temp = super.getSimpleComplexProductClass();
        return new SimpleProductClass(temp.getTradingSession(),temp.getClassKey());
    }

    public void setSimpleProductClass(SimpleProductClass productClass)
    {
        SimpleComplexProductClass temp = new SimpleComplexProductClass(productClass.getTradingSession(),productClass.getClassKey());
        super.setSimpleComplexProductClass(temp);
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int parentSize = 3;
        int index;
        if(keyElement.equalsIgnoreCase(SIMPLE_PRODUCT_CLASS_PROPERTY_NAME))
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
