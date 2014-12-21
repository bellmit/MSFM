//
// -----------------------------------------------------------------------------------
// Source file: SessionOriginKey.java
//
// PACKAGE: com.cboe.domain.routingProperty.key
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.key;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.OriginCode;

/**
 * Routing Property Key that uses Session and Order Orgin Code as key elements
 */
public class SessionOriginKey extends SessionKey
{
    public static final String ORIGIN_CODE_PROPERTY_NAME = "originCode";

    private static final int ORIGIN_CODE_PROPERTY_KEY_POSITION = 0;

    protected OriginCode originCode;

    public SessionOriginKey(BasePropertyType type)
    {
        super(type);
        this.originCode = new OriginCode(' ');
    }

    public SessionOriginKey(String propertyName, String sessionName, char origin)
    {
        super(propertyName, sessionName);
        this.originCode = new OriginCode(origin);
    }

    public SessionOriginKey(String propertyKey)
            throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        SessionOriginKey newKey = (SessionOriginKey) super.clone();
        newKey.originCode = new OriginCode(this.originCode.originCode);

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

        String originStr = getKeyElement(keyElements, ++index);
        this.originCode = new OriginCode(originStr.charAt(0));

        return index;
    }

    /**
     * order of key elements is: session, originCode, propertyName
     */
    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {getTradingSession(),
                new Character(getOriginCode().originCode),
                getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {ORIGIN_CODE_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
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
        if(keyElement.equalsIgnoreCase(ORIGIN_CODE_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + ORIGIN_CODE_PROPERTY_KEY_POSITION;
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
            case ORIGIN_CODE_PROPERTY_KEY_POSITION:
                fieldName = ORIGIN_CODE_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
