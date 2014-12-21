package com.cboe.client.util.tourist;

/**
 * AbstractTourist.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * incomplete implementation of a Tourist
 *
 */

import java.io.*;
import java.util.*;

import com.cboe.client.util.*;

public abstract class AbstractTourist implements TouristIF
{
    protected Properties properties    = new Properties();
    protected boolean    isHttp        = false;
    protected Latch      finishedLatch = new Latch();

    public void addParameter(String key, String value)
    {
        key = key.trim().toLowerCase();

        value = value.trim();

        if (value.length() == 0)
        {
            return;
        }

        String oldValue = properties.getProperty(key);

        if (oldValue != null)
        {
            if (oldValue.equals(value))
            {
                // new value is equal to old value
                return;
            }
            StringBuilder ov = new StringBuilder(oldValue.length()+value.length()+1);
            StringBuilder nv = new StringBuilder(value.length()+2);
            ov.append(',').append(oldValue).append(',');
            nv.append(',').append(value).append(',');
            if (ov.indexOf(nv.toString()) >= 0)
            {
                // new value is already part of old value
                return;
            }

            ov.setLength(0);
            ov.append(oldValue).append(',').append(value);
            value = ov.toString();
        }

        properties.put(key, value);
    }

    public boolean validateParameters(Writer writer) throws Exception
    {
        String[] mandatoryKeys = getMandatoryKeys();
        boolean  isValid       = true;

        if (mandatoryKeys != null)
        {
            StringBuilder sb = new StringBuilder(100);
            for (int i = 0; i < mandatoryKeys.length; i++)
            {
                if (properties.get(mandatoryKeys[i].toLowerCase()) == null)
                {
                    sb.setLength(0);
                    sb.append("<error type=\"parameter\" text=\"missing parameter key named '")
                      .append(mandatoryKeys[i]).append("'\"/>");
                    writer.write(sb.toString());
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    public boolean waitUntilFinished(long microseconds)
    {
        return finishedLatch.acquireAndReset(microseconds);
    }

    public void finished()
    {
        finishedLatch.release();
    }

    public void setIsHttp(boolean isHttp)
    {
        this.isHttp = isHttp;
    }

    public boolean isHttp()
    {
        return isHttp;
    }

    public String getValue(String key)
    {
        return properties.getProperty(key.toLowerCase());
    }

    public String getValue(String key, String defaultValue)
    {
        String value = properties.getProperty(key.toLowerCase());
        if (value == null)
        {
            return defaultValue;
        }

        return value;
    }

    public Properties getProperties()
    {
        return properties;
    }
}
