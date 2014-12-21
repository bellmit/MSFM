package com.cboe.domain.routingProperty.key;

//-----------------------------------------------------------------------------------
//Source file: SessionFirmClassLevelKey
//
//
//Created: Dec 18, 2007 9:42:50 AM
//-----------------------------------------------------------------------------------
//Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------


import java.util.*;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.OriginCode;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.TradingSessionName;


public class SessionFirmClassOriginLevelKey extends AbstractBasePropertyKey
{
    public static final String PRODUCT_CLASS_PROPERTY_NAME = "simpleComplexProductClass";
    public static final String ORIGIN_CODE_PROPERTY_NAME = "originCode";
    public static final String LEVEL_PROPERTY_NAME = "level";
    
    public static final int PRODUCT_CLASS_PROPERTY_KEY_POSITION = 0;
    public static final int ORIGIN_CODE_PROPERTY__KEY_POSITION  = 1;
    public static final int LEVEL_PROPERTY_KEY_POSITION         = 2;

    protected SimpleComplexProductClass productClass;
    protected OriginCode originCode;
    protected int level;
    
    public SessionFirmClassOriginLevelKey(BasePropertyType type)
    {
        super(type);        
        this.originCode = new OriginCode(' ');
        this.level = 0;
    }

    public SessionFirmClassOriginLevelKey(String propertyName, String exchangeAcronym, String firmAcronym, String sessionName,
                            int classKey,char origin, int level)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.productClass = createProductClass(sessionName, classKey);
        this.originCode = new OriginCode(origin);
        this.level = level;
    }

    public SessionFirmClassOriginLevelKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmClassOriginLevelKey newKey = (SessionFirmClassOriginLevelKey) super.clone();
        newKey.productClass = createProductClass(getTradingSession().sessionName, getClassKey());
        newKey.originCode = new OriginCode(this.originCode.originCode);
        newKey.level = getLevel();
        return newKey;
    }

    public int getClassKey()
    {
        return productClass.getClassKey();
    }
    
    public void setClassKey(int classKey)
    {
        this.productClass = createProductClass(getTradingSession().sessionName, classKey);
        resetPropertyKey();
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

    public OriginCode getOriginCode()
    {
        return originCode;
    }

    public void setOriginCode(OriginCode originCode)
    {
        this.originCode = originCode;
        resetPropertyKey();
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
        resetPropertyKey();
    }
    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),
                                        getClassKey(),
                                        getOriginCode().originCode,
                                        getLevel(), getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    protected SimpleComplexProductClass createProductClass(String sessionName, int classKey)
    {
        return new SimpleComplexProductClass(sessionName, classKey);
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
         this.productClass = createProductClass(tradingSessionName.sessionName,
                                                Integer.parseInt(getKeyElement(keyElements, ++index)));
     }
     catch (NumberFormatException e)
     {
         String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "classKey",
                                                              getKeyElement(keyElements, index));
         throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT_CLASS);
     }         
     
     String originStr = getKeyElement(keyElements, ++index);
     this.originCode = new OriginCode(originStr.charAt(0));
     
     try 
     {
         this.level = Integer.parseInt(getKeyElement(keyElements, ++index));
     }
     catch (NumberFormatException e)
     {
         String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "level",
                                                              getKeyElement(keyElements, index));
         throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_TYPE);
     }

     return index;
 }

 /**
  * Allows the Routing Property to determine the order of the PropertyDescriptors.
  * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
  */
 public Comparator getPropertyDescriptorSortComparator()
 {
     String[] forcedEntries = {FIRM_PROPERTY_NAME, PRODUCT_CLASS_PROPERTY_NAME,ORIGIN_CODE_PROPERTY_NAME, LEVEL_PROPERTY_NAME };
     return new ForcedPropertyDescriptorComparator(forcedEntries);
 }

    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize() + 3;
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int parentSize = super.getMaskSize();
        int index;
        if(keyElement.equalsIgnoreCase(PRODUCT_CLASS_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + PRODUCT_CLASS_PROPERTY_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(ORIGIN_CODE_PROPERTY_NAME))
        {
            index = parentSize + ORIGIN_CODE_PROPERTY__KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(LEVEL_PROPERTY_NAME))
        {
            index = parentSize + LEVEL_PROPERTY_KEY_POSITION;
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
            case ORIGIN_CODE_PROPERTY__KEY_POSITION:
                fieldName = ORIGIN_CODE_PROPERTY_NAME;
                break;
            case LEVEL_PROPERTY_KEY_POSITION:
                fieldName = LEVEL_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
