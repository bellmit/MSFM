package com.cboe.domain.routingProperty.key;
// -----------------------------------------------------------------------------------
// Source file: SessionFirmPostStationKey
//
// PACKAGE: com.cboe.domain.routingProperty.key
// 
// Created: Oct 12, 2007 9:42:50 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;

public class SessionFirmPostStationKey extends AbstractBasePropertyKey
{   
    public static final String POST_PROPERTY_NAME = "post";
    public static final String STATION_PROPERTY_NAME = "station";

    private static final int POST_PROPERTY_KEY_POSITION    = 0;
    private static final int STATION_PROPERTY_KEY_POSITION = 1;

    private int post;
    private int station;

    public SessionFirmPostStationKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionFirmPostStationKey(String propertyName, String firmAcronym, String exchangeAcronym, String sessionName,
                               int post, int station)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);        
        this.post = post;
        this.station = station;
    }

    public SessionFirmPostStationKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public int getPost()
    {
        return this.post;
    }

    public int getStation()
    {
        return this.station;
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmPostStationKey newKey = (SessionFirmPostStationKey) super.clone();
        newKey.post = getPost();
        newKey.station = getStation();
        return newKey;
    }

    public void setPost(int post)
    {
        this.post = post;
        resetPropertyKey();
    }

    public void setStation(int station)
    {        
        this.station = station;
        resetPropertyKey();
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),
                                        new Integer(getPost()), new Integer(getStation()), getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
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
            this.post = Integer.parseInt(getKeyElement(keyElements, ++index));
            this.station = Integer.parseInt(getKeyElement(keyElements, ++index));
        }
        catch (NumberFormatException e)
        {
            String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "post or station",
                                                                 getKeyElement(keyElements, index));
            throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT_CLASS);
        }

        return index;
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
        if(keyElement.equalsIgnoreCase(POST_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + POST_PROPERTY_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(STATION_PROPERTY_NAME))
        {
            index = parentSize + STATION_PROPERTY_KEY_POSITION;
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
            case POST_PROPERTY_KEY_POSITION:
                fieldName = POST_PROPERTY_NAME;
                break;
            case STATION_PROPERTY_KEY_POSITION:
                fieldName = STATION_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME, FIRM_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}



