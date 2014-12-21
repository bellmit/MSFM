//
// -----------------------------------------------------------------------------------
// Source file: RateLimitsImpl.java
//
// PACKAGE: com.cboe.domain.rateMonitor;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.rateMonitor;

import java.util.ArrayList;
import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.interfaces.domain.rateMonitor.RateLimits;
import com.cboe.interfaces.domain.property.PropertyCategoryTypes;

import com.cboe.domain.property.PropertyDefinitionCache;
import com.cboe.domain.property.PropertyFactory;

public class RateLimitsImpl implements RateLimits
{
    public static final int SESSION_NAME_INDEX = 0;
    public static final int RATE_TYPE_INDEX    = 1;
    public static final int SIZE_INDEX         = 0;
    public static final int INTERVAL_INDEX     = 1;

    private String sessionName;
    private short rateMonitorType;
    private int windowSize;
    private long windowInterval;
    private int hashCode;


    // Used for the property implementation of the RateLimits
    private PropertyDefinition propertyDefinition;

    public RateLimitsImpl(String sessionName, short rateMonitorType, int windowSize, long windowInterval)
    {
        this.sessionName = sessionName;
        this.rateMonitorType = rateMonitorType;
        this.windowSize = windowSize;
        this.windowInterval = windowInterval;
        StringBuilder sb = new StringBuilder(10);
        hashCode = (sb.append(sessionName).toString()).hashCode()+ rateMonitorType;
        propertyDefinition = PropertyDefinitionCache.getInstance().getPropertyDefinition(PropertyCategoryTypes.RATE_LIMITS,PropertyCategoryTypes.RATE_LIMITS);
    }

    public boolean equals(Object o)
    {
        if( this == o )
        {
            return true;
        }
        if( !(o instanceof RateLimits) )
        {
            return false;
        }

        final RateLimits rateLimits = ( RateLimits ) o;

        if( !getSessionName().matches( rateLimits.getSessionName()) )
        {
            return false;
        }
        if( getRateMonitorType() != rateLimits.getRateMonitorType() )
        {
            return false;
        }
        if(getWindowSize() != rateLimits.getWindowSize())
        {
            return false;
        }
        if(getWindowInterval() != rateLimits.getWindowInterval())
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return hashCode;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public short getRateMonitorType()
    {
        return rateMonitorType;
    }

    public int getWindowSize()
    {
        return windowSize;
    }

    public long getWindowInterval()
    {
        return windowInterval;
    }

    public void setSessionName(String sessionName)
    {
        this.sessionName = sessionName;
    }

    public void setRateMonitorType(short rateMonitorType)
    {
        this.rateMonitorType = rateMonitorType;
    }

    public void setWindowSize(int windowSize)
    {
        this.windowSize = windowSize;
    }

    public void setWindowInterval(long windowInterval)
    {
        this.windowInterval = windowInterval;
    }

    public Property getProperty()
    {
        Property property;

        ArrayList nameList = new ArrayList();
        ArrayList valueList = new ArrayList();

        // Namelist is session Name, rateMonitorType
        nameList.add(SESSION_NAME_INDEX, sessionName);
        nameList.add(RATE_TYPE_INDEX   , Short.toString(rateMonitorType));

        valueList.add(SIZE_INDEX       , Integer.toString(windowSize));
        valueList.add(INTERVAL_INDEX   , Long.toString(windowInterval));

        property = PropertyFactory.createProperty(nameList,valueList,propertyDefinition);

        return property;
    }

    public void setPropertyDefinition(PropertyDefinition propertyDefinition)
    {
        this.propertyDefinition = propertyDefinition;
    }
}
