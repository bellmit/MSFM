package com.cboe.domain.routingProperty.key;

import com.cboe.exceptions.DataValidationException;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.common.BoothWorkstation;
// -----------------------------------------------------------------------------------
// Source file: SessionFirmBoothKey
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Aug 24, 2006 9:55:35 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class SessionFirmBoothKey extends AbstractBasePropertyKey
{
    public static final String BOOTH_WORKSTATION_PROPERTY_NAME = "boothWorkstation";

    private static final int BOOTH_WORKSTATION_PROPERTY_KEY_POSITION = 0;

    protected BoothWorkstation boothWorkstation;
    
    public SessionFirmBoothKey(com.cboe.interfaces.domain.routingProperty.BasePropertyType type)
    {
        super(type);
        this.boothWorkstation = new BoothWorkstation(0);
    }

    public SessionFirmBoothKey(String propertyName, String sessionName, String firmAcronym, String exchangeAcronym, int booth)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.boothWorkstation = new BoothWorkstation(booth);
    }

    public SessionFirmBoothKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public BoothWorkstation getBoothWorkstation()
    {
        return boothWorkstation;
    }

    public void setBoothWorkstation(BoothWorkstation workstation)
    {
        this.boothWorkstation = workstation;
        resetPropertyKey();
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmBoothKey newKey =  (SessionFirmBoothKey) super.clone();
        newKey.boothWorkstation = new BoothWorkstation(this.boothWorkstation.workstationNumber);
        
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
    protected int parsePropertyKey(String propertyKey) throws DataValidationException
    {
        int index = super.parsePropertyKey(propertyKey);

        String[] keyElements = splitPropertyKey(propertyKey);

        try
        {
            int booth = Integer.parseInt(getKeyElement(keyElements, ++index));
            this.boothWorkstation = new BoothWorkstation(booth);
        }
        catch (NumberFormatException e)
        {
            String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "BOOTH workstation number",
                                                                 getKeyElement(keyElements, index));
            throw ExceptionBuilder.dataValidationException(detailMsg, 0);
        }

        return index;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),
                                        new Integer(getBoothWorkstation().workstationNumber), getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
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
        if(keyElement.equalsIgnoreCase(BOOTH_WORKSTATION_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + BOOTH_WORKSTATION_PROPERTY_KEY_POSITION;
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
            case BOOTH_WORKSTATION_PROPERTY_KEY_POSITION:
                fieldName = BOOTH_WORKSTATION_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
