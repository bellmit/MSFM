//
// -----------------------------------------------------------------------------------
// Source file: UserInputMonitorEntryImpl.java
//
// PACKAGE: com.cboe.domain.uim;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.uim;

import java.util.*;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.interfaces.domain.rateMonitor.RateLimits;
import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.uim.UserInputMonitorEntry;

import com.cboe.domain.property.PropertyDefinitionCache;
import com.cboe.domain.property.PropertyFactory;

public class UserInputMonitorEntryImpl implements UserInputMonitorEntry
{
    private String sessionName;
    private int windowInterval;

    // Used for the property implementation of the RateLimits
    private PropertyDefinition propertyDefinition;

    public UserInputMonitorEntryImpl(String sessionName, int windowInterval)
    {
        this.sessionName = sessionName;
        this.windowInterval = windowInterval;
        propertyDefinition = PropertyDefinitionCache.getInstance().getPropertyDefinition(PropertyCategoryTypes.USER_PROPERTIES,PropertyCategoryTypes.USER_PROPERTIES);
    }

    public boolean equals(Object o)
    {
        boolean isEqual;

        if( this == o )
        {
            isEqual = true;
        }
        else if( !(o instanceof UserInputMonitorEntry) )
        {
            isEqual = false;
        }
        else
        {
            UserInputMonitorEntry uimEntry = (UserInputMonitorEntry) o;
            isEqual =  (sessionName.equals(uimEntry.getSessionName())) && (windowInterval == uimEntry.getWindowInterval());
        }

        return isEqual;
    }

    public int hashCode()
    {
        return sessionName.hashCode() + windowInterval;
    }

    public String getSessionName()
    {
        return sessionName;
    }

    public int getWindowInterval()
    {
        return windowInterval;
    }

    public void setSessionName(String sessionName)
    {
        this.sessionName = sessionName;
    }

    public void setWindowInterval(int windowInterval)
    {
        this.windowInterval = windowInterval;
    }

    public Property getProperty()
    {
        Property property;

        List<String> nameList = new ArrayList<String>(1);
        List<String> valueList = new ArrayList<String>(1);

        nameList.add(sessionName);
        valueList.add(Integer.toString(windowInterval));

        property = PropertyFactory.createProperty(nameList,valueList,propertyDefinition);

        return property;
    }

    public void setPropertyDefinition(PropertyDefinition propertyDefinition)
    {
        this.propertyDefinition = propertyDefinition;
    }
}
