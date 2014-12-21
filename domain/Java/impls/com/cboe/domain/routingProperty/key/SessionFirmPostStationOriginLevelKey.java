package com.cboe.domain.routingProperty.key;
//-----------------------------------------------------------------------------------
//Source file: SessionFirmPostStationOriginLevelKey
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

public class SessionFirmPostStationOriginLevelKey extends AbstractBasePropertyKey
{
    public static final String POST_PROPERTY_NAME = "post";
    public static final String STATION_PROPERTY_NAME = "station";
    public static final String ORIGIN_CODE_PROPERTY_NAME = "originCode";
    public static final String LEVEL_PROPERTY_NAME = "level";

    private static final int POST_PROPERTY_NAME_KEY_POSITION   = 0;
    private static final int STATION_PROPERTY__KEY_POSITION    = 1;
    private static final int ORIGIN_CODE_PROPERTY_KEY_POSITION = 2;
    private static final int LEVEL_PROPERTY_KEY_POSITION       = 3;

    protected int post;
    protected int station;
    protected OriginCode originCode;
    protected int level;

    public SessionFirmPostStationOriginLevelKey(BasePropertyType type)
    {
        super(type);
        this.originCode = new OriginCode(' ');
    }

    public SessionFirmPostStationOriginLevelKey(String propertyName, String firmAcronym,
            String exchangeAcronym, String sessionName, int post, int station, char origin,int level)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.post = post;
        this.station = station;
        this.originCode = new OriginCode(origin);
        this.level = level;
    }

    public SessionFirmPostStationOriginLevelKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionFirmPostStationOriginLevelKey newKey = (SessionFirmPostStationOriginLevelKey) super.clone();
        newKey.post = getPost();
        newKey.station = getStation();        
        newKey.originCode = new OriginCode(this.originCode.originCode);
        newKey.level = getLevel();
        return newKey;
    }

    public int getPost()
    {
        return this.post;
    }

    public void setPost(int post)
    {
        this.post = post;
        resetPropertyKey();
    }
    
    public int getStation()
    {
        return this.station;
    }

    public void setStation(int station)
    {        
        this.station = station;
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
        Object[] propertyKeyElements = { createBasePropertyKey(), new Integer(getPost()), new Integer(getStation()),
                new Character(getOriginCode().originCode), new Integer(getLevel()),
                getPropertyName() };

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
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
            throw ExceptionBuilder.dataValidationException(detailMsg,
                    DataValidationCodes.INVALID_TYPE);
        }

        return index;
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * 
     * @return comparator to use for sorting the returned PropertyDescriptors from
     * getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = { FIRM_PROPERTY_NAME, ORIGIN_CODE_PROPERTY_NAME,
                                   POST_PROPERTY_NAME, STATION_PROPERTY_NAME, LEVEL_PROPERTY_NAME };
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize() + 4;
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int index;
        int parentSize = super.getMaskSize();
        if(keyElement.equalsIgnoreCase(POST_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + POST_PROPERTY_NAME_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(STATION_PROPERTY_NAME))
        {
            index = parentSize + STATION_PROPERTY__KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(ORIGIN_CODE_PROPERTY_NAME))
        {
            index = parentSize + ORIGIN_CODE_PROPERTY_KEY_POSITION;
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
            case POST_PROPERTY_NAME_KEY_POSITION:
                fieldName = POST_PROPERTY_NAME;
                break;
            case STATION_PROPERTY__KEY_POSITION:
                fieldName = STATION_PROPERTY_NAME;
                break;
            case ORIGIN_CODE_PROPERTY_KEY_POSITION:
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
