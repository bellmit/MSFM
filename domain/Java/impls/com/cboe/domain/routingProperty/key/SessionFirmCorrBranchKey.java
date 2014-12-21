package com.cboe.domain.routingProperty.key;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;

// -----------------------------------------------------------------------------------
// Source file: SessionFirmCorrBranchKey
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Aug 24, 2006 9:58:51 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class SessionFirmCorrBranchKey extends AbstractBasePropertyKey
{

    public static final String CORRESPONDENT_PROPERTY_NAME = "correspondent";
    public static final String BRANCH_PROPERTY_NAME = "branch";

    private static final int CORRESPONDENT_PROPERTY_KEY_POSITION = 0;
    private static final int BRANCH_PROPERTY_KEY_POSITION        = 1;

    protected String branch;
    protected String correspondent;
   

    public SessionFirmCorrBranchKey(BasePropertyType type)
    {
        super(type);
        this.branch = "";
        this.correspondent = "";
    }

    public SessionFirmCorrBranchKey(String propertyName, String sessionName, String firmAcronym, String exchangeAcronym,
                                 String correspondent, String branch)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.branch = branch;
        this.correspondent  = correspondent;
    }

    public SessionFirmCorrBranchKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmCorrBranchKey newKey =  (SessionFirmCorrBranchKey) super.clone();
        newKey.branch = getBranch();
        newKey.correspondent = getCorrespondent();
        
        return newKey;
    }

    public String getBranch()
    {
        return branch;
    }

    public void setBranch(String branch)
    {
        this.branch = branch;
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

    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME, FIRM_PROPERTY_NAME,
                                  CORRESPONDENT_PROPERTY_NAME, BRANCH_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
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

        this.branch = getKeyElement(keyElements, ++index);
        
        return index;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),
                                        getCorrespondent(), getBranch(), getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
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
        else if(keyElement.equalsIgnoreCase(BRANCH_PROPERTY_NAME))
        {
            index = parentSize + BRANCH_PROPERTY_KEY_POSITION;
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
            case BRANCH_PROPERTY_KEY_POSITION:
                fieldName = BRANCH_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
