package com.cboe.domain.routingProperty.key;

import com.cboe.idl.firm.FirmStruct;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.domain.routingProperty.common.ParWorkstation;

// -----------------------------------------------------------------------------------
// Source file: SessionFirmCorrParKey
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Aug 24, 2006 9:56:31 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class SessionFirmCorrParKey extends AbstractBasePropertyKey
{
    public static final String CORRESPONDENT_PROPERTY_NAME = "correspondent";
    public static final String PAR_WORKSTATION_PROPERTY_NAME = "parWorkstation";
    public static final String CORRESPONDENT_FIRM_PROPERTY_NAME = "correspondentFirm";

    private static final int CORRESPONDENT_PROPERTY_KEY_POSITION   = 0;
    private static final int PAR_WORKSTATION_PROPERTY_KEY_POSITION = 1;

    protected ParWorkstation parWorkstation;
    protected String correspondent;

    private FirmStruct correspondentFirm;

    public SessionFirmCorrParKey(BasePropertyType type)
    {
        super(type);
        this.parWorkstation = new ParWorkstation(' ');
    }

    public SessionFirmCorrParKey(String propertyName, String sessionName, String firmAcronym, String exchangeAcronym,
                                 String correspondent, char par)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.parWorkstation = new ParWorkstation(par);
        this.correspondent  = correspondent;
    }

    public SessionFirmCorrParKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmCorrParKey newKey =  (SessionFirmCorrParKey) super.clone();
        newKey.parWorkstation = new ParWorkstation(this.parWorkstation.workstation);
        newKey.correspondent = getCorrespondent();
        newKey.correspondentFirm = getCorrespondentFirm();
        return newKey;
    }

    public ParWorkstation getParWorkstation()
    {
        return parWorkstation;
    }

    public void setParWorkstation(ParWorkstation workstation)
    {
        this.parWorkstation = workstation;
        resetPropertyKey();
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

        this.correspondent = getKeyElement(keyElements, ++index);

        String par = getKeyElement(keyElements, ++index);
        this.parWorkstation = new ParWorkstation(par.charAt(0));

        return index;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),
                                        getCorrespondent(), new Character(parWorkstation.workstation), getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
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
        return super.getMaskSize() + 2;
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
        else if(keyElement.equalsIgnoreCase(PAR_WORKSTATION_PROPERTY_NAME))
        {
            index = parentSize + PAR_WORKSTATION_PROPERTY_KEY_POSITION;
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
            case PAR_WORKSTATION_PROPERTY_KEY_POSITION:
                fieldName = PAR_WORKSTATION_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
