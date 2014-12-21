//
// -----------------------------------------------------------------------------------
// Source file: SessionFirmPostStationCorrBranchKey.java
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

public class SessionFirmPostStationCorrBranchKey extends SessionFirmPostStationKey
{
    public static final String CORRESPONDENT_PROPERTY_NAME = "correspondent";
    public static final String BRANCH_PROPERTY_NAME = "branch";

    private static final int CORRESPONDENT_PROPERTY_KEY_POSITION = 0;
    private static final int BRANCH_PROPERTY_KEY_POSITION = 1;

    protected String correspondent;
    protected String branch;

    public SessionFirmPostStationCorrBranchKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionFirmPostStationCorrBranchKey(String propertyName, String sessionName, String firmAcronym,
                                         String exchangeAcronym, int post, int station,
                                         String correspondent, String branch)
    {
        super(propertyName, firmAcronym, exchangeAcronym, sessionName, post, station);
        this.correspondent = correspondent;
        this.branch = branch;
    }

    public SessionFirmPostStationCorrBranchKey(String propertyKey)
            throws DataValidationException
    {
        super(propertyKey);
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

    public Object clone()
            throws CloneNotSupportedException
    {
        SessionFirmPostStationCorrBranchKey newKey = (SessionFirmPostStationCorrBranchKey) super.clone();
        newKey.correspondent = getCorrespondent();
        newKey.branch = getBranch();

        return newKey;
    }

    /**
     * Parses the propertyKey to find the separate key values.
     * <p/>
     * Returns the index of the last key value used from the propertyKey's parts (does not count the index of
     * propertyName, which is always the last part of the propertyKey).
     */
    protected int parsePropertyKey(String propertyKey)
            throws DataValidationException
    {
        int index = super.parsePropertyKey(propertyKey);
        String[] keyElements = splitPropertyKey(propertyKey);

        this.correspondent = getKeyElement(keyElements, ++index);
        this.branch = getKeyElement(keyElements, ++index);

        return index;
    }

    /*
    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),
                                        getCorrespondent(), getBranch(), getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

     */
    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),
                new Integer(getPost()), new Integer(getStation()), getCorrespondent(), getBranch(), getPropertyName() };
        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {FIRM_PROPERTY_NAME, POST_PROPERTY_NAME, STATION_PROPERTY_NAME}; // CORRESPONDENT_PROPERTY_NAME, BRANCH_PROPERTY_NAME
        return new ForcedPropertyDescriptorComparator(forcedEntries);
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
