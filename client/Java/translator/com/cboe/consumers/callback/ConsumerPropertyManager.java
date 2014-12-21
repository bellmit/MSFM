//
// ------------------------------------------------------------------------
// Source file: ConsumerPropertyManager.java
//
// PACKAGE: com.cboe.consumers.callback
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.consumers.callback;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

import java.util.HashMap;


public class ConsumerPropertyManager
{
    private HashMap propertyMap ;
    private static ConsumerPropertyManager ourInstance;

    public synchronized static ConsumerPropertyManager getInstance()
    {
        if (ourInstance == null)
        {
            ourInstance = new ConsumerPropertyManager();
        }
        return ourInstance;
    }

    private ConsumerPropertyManager()
    {
        propertyMap = new HashMap();
    }

    public int getIntValue(String section, String propertyName, String defaultPropertyName, int defaultValue)
    {
        String propertyValue = getValue(section, propertyName, defaultPropertyName,  "" +defaultValue);
        int value = defaultValue;
        try
        {
            value = Integer.parseInt(propertyValue);
        }
        catch(NumberFormatException e)
        {
            StringBuffer msg = new StringBuffer(50);
            msg.append("Property Value for ").append(" Section=").append(section).append(" Property=").append(propertyName);
            msg.append(" is not a number.  Used Default=").append(defaultValue);
            if(GUILoggerHome.find().isInformationOn())
            {
                GUILoggerHome.find().information(msg.toString(), GUILoggerBusinessProperty.COMMON);
            }
        }
        return value;
    }
    public boolean getBooleanValue(String section, String propertyName, String defaultPropertyName, boolean defaultValue)
    {
        String propertyValue = getValue(section, propertyName, defaultPropertyName, "" + defaultValue);
        return Boolean.valueOf(propertyValue).booleanValue();
    }
    protected String getValue(String section, String propertyName, String defaultPropertyName, String defaultValue)
    {
        String propertyKey = section + "." + propertyName;
        String defaultPropertyKey = section + "." + defaultPropertyName;
        // try to get the property
        String cachedPropertyValue = (String) propertyMap.get(propertyKey);
        if(cachedPropertyValue != null)
        {
            return cachedPropertyValue;
        }
        else // property and default are not setup, so look them up in the property file
        {
            String defaultPropertyValue  = (String) propertyMap.get(defaultPropertyKey);
            if (defaultPropertyValue == null)
            {
                defaultPropertyValue = AppPropertiesFileFactory.find().getValue(section, defaultPropertyName);
                if (defaultPropertyValue == null)
                {
                    defaultPropertyValue = defaultValue;
                    StringBuffer msg = new StringBuffer(50);
                    msg.append("Did not find property value for ").append(" Section=").append(section).append(" Property=").append(defaultPropertyName);
                    msg.append(". Used Default=").append(defaultValue);
                    GUILoggerHome.find().alarm(msg.toString());
                }
                propertyMap.put(defaultPropertyKey, defaultPropertyValue);
            }

            // find property value
            String propertyValue = AppPropertiesFileFactory.find().getValue(section, propertyName);
            if(propertyValue == null)
            {
                propertyValue = defaultPropertyValue;
                StringBuffer msg = new StringBuffer(50);
                msg.append("Did not find property value for ").append(" Section=").append(section).append(" Property=").append(propertyName);
                msg.append(". Used Default=").append(defaultPropertyValue);
                if (GUILoggerHome.find().isInformationOn())
                {
                    GUILoggerHome.find().information(msg.toString(), GUILoggerBusinessProperty.COMMON);
                }
            }
            propertyMap.put(propertyKey, propertyValue);
            return propertyValue;
        }
    }
}
