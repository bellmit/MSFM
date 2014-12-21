package com.cboe.domain.routingProperty.key;

//-----------------------------------------------------------------------------------
//Source file: SessionFirmCorrClassKey
//
//PACKAGE: com.cboe.domain.routingProperty.key
//
//Created: Aug 4, 2006 3:09:35 PM
//-----------------------------------------------------------------------------------
//Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.idl.firm.FirmStruct;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;

public class SessionFirmClassCorrKey extends SessionFirmClassKey
{
    public static final String CORRESPONDENT_PROPERTY_NAME = "correspondent";
    public static final String CORRESPONDENT_FIRM_PROPERTY_NAME = "correspondentFirm";

    private static final int CORRESPONDENT_PROPERTY_KEY_POSITION = 0;

    protected String correspondent;
    private FirmStruct correspondentFirm;

    public SessionFirmClassCorrKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionFirmClassCorrKey(String propertyName, String sessionName, String firmAcronym,
            String exchangeAcronym, String correspondent, int classKey)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym, classKey);

        this.correspondent = correspondent;
    }

    public SessionFirmClassCorrKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmClassCorrKey newKey = (SessionFirmClassCorrKey) super.clone();
        newKey.correspondent = getCorrespondent();
        newKey.correspondentFirm = getCorrespondentFirm();

        return newKey;
    }

    /**
     * Parses the propertyKey to find the separate key values.
     * 
     * Returns the index of the last key value used from the propertyKey's parts (does not count the
     * index of propertyName, which is always the last part of the propertyKey).
     * 
     * @param propertyKey
     * @throws DataValidationException
     */
    protected int parsePropertyKey(String propertyKey) throws DataValidationException
    {
        int index = super.parsePropertyKey(propertyKey);
        String[] keyElements = splitPropertyKey(propertyKey);

        this.correspondent = getKeyElement(keyElements, ++index);

        return index;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = { createBasePropertyKey(), new Integer(getClassKey()),
                getCorrespondent(), getPropertyName() };

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * 
     * @return comparator to use for sorting the returned PropertyDescriptors from
     * getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = { FIRM_PROPERTY_NAME, PRODUCT_CLASS_PROPERTY_NAME,
                CORRESPONDENT_PROPERTY_NAME };
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    public String getCorrespondent()
    {
        return correspondent;
    }

    public void setCorrespondent(String correspondent)
    {
        this.correspondent = correspondent;
        resetPropertyKey();
    }

    public FirmStruct getCorrespondentFirm()
    {
        return correspondentFirm;
    }

    public void setCorrespondentFirm(FirmStruct correspondentFirm)
    {
        this.correspondentFirm = correspondentFirm;
        setCorrespondent(correspondentFirm.firmAcronym);
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
        if(keyElement.equalsIgnoreCase(CORRESPONDENT_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + CORRESPONDENT_PROPERTY_KEY_POSITION;
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
            case CORRESPONDENT_PROPERTY_KEY_POSITION:
                fieldName = CORRESPONDENT_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
