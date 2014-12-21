//
// -----------------------------------------------------------------------------------
// Source file: RateLimitsFactory.java
//
// PACKAGE: com.cboe.domain.property
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.rateMonitor;

import java.util.*;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.property.PropertyGroup;
import com.cboe.interfaces.domain.property.PropertyServiceProperty;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.rateMonitor.RateLimits;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.property.PropertyFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class RateLimitsFactory
{
    public static RateLimits create(String sessionName, short rateMonitorType, int windowSize, long windowInterval)
    {
        return new RateLimitsImpl(sessionName, rateMonitorType, windowSize, windowInterval);
    }

    public static RateLimits create(PropertyServiceProperty property) throws DataValidationException
    {
        String sessionName;
        short rateMonitorType;
        int windowSize;
        long windowInterval;

        List nameList = property.getNameList();
        List valueList = property.getValueList();

        // Name is composed of session and rateMonitorType
        sessionName = (String)nameList.get(RateLimitsImpl.SESSION_NAME_INDEX);
        try
        {
            rateMonitorType = Short.parseShort((String) nameList.get(RateLimitsImpl.RATE_TYPE_INDEX));
            windowSize      = Integer.parseInt((String) valueList.get(RateLimitsImpl.SIZE_INDEX));
            windowInterval  = Integer.parseInt((String) valueList.get(RateLimitsImpl.INTERVAL_INDEX));
        }
        catch (NumberFormatException nfe)
        {
            Log.exception("Property does not contain the proper data to make a RateLimit", nfe);
            throw ExceptionBuilder.dataValidationException("Property does not contain the proper data to make a RateLimit", DataValidationCodes.INVALID_GROUP);
        }

        RateLimits rateLimits = create(sessionName, rateMonitorType, windowSize, windowInterval);
        ((RateLimitsImpl)rateLimits).setPropertyDefinition(property.getPropertyDefinition());

        return rateLimits;
    }

    public static ArrayList createRateLimitsList(PropertyGroup propertyGroup) throws DataValidationException
    {
        ArrayList list = new ArrayList(100);

        Map properties = propertyGroup.getProperties();
        Iterator iterator = properties.values().iterator();
        while (iterator.hasNext())
        {
            PropertyServiceProperty property = (PropertyServiceProperty) iterator.next();
            RateLimits rateLimits = RateLimitsFactory.create(property);
            list.add(rateLimits);
        }

        return list;
    }

    public static Property createProperty(RateLimits rateLimits)
    {
        Property property = ((RateLimitsImpl)rateLimits).getProperty();
        return property;
    }

    public static PropertyServicePropertyGroup createPropertyGroup(List rateLimits, int version, String exchange, String acronym, String userId)
    {
        PropertyServicePropertyGroup group;

        String rateLimitGroupKey = getRateMonitorKey(userId, exchange, acronym);
        group = PropertyFactory.createPropertyGroup(PropertyCategoryTypes.RATE_LIMITS, rateLimitGroupKey);
        group.setVersion(version);

        for (int i=0;i<rateLimits.size();i++)
        {
            RateLimitsImpl localRateLimits = (RateLimitsImpl) rateLimits.get(i);
            Property property = localRateLimits.getProperty();
            group.addProperty(property);
        }

        return group;
    }

    public static String getRateMonitorKey(String userId, String exchange, String acronym)
    {
        String rateMonitorKey = BasicPropertyParser.buildCompoundString(new String[]{exchange, acronym, userId});
        return rateMonitorKey;
    }

    public static RateLimits getRateLimitBySessionType(PropertyServicePropertyGroup propertyServiceGroup, String sessionName, short type)
            throws DataValidationException, NotFoundException
    {
        ArrayList rateLimits = createRateLimitsList(propertyServiceGroup);
        for(int i = 0; i < rateLimits.size(); i++)
        {
            RateLimits rateLimit = (RateLimits) rateLimits.get(i);
            if (rateLimit.getSessionName().equalsIgnoreCase(sessionName)  &&  rateLimit.getRateMonitorType() == type)
            {
                return rateLimit;
            }
        }
        throw ExceptionBuilder.notFoundException("RateLimits not found for sessionName:type::" + sessionName + ":" + type, NotFoundCodes.RESOURCE_DOESNT_EXIST);
    }
}
