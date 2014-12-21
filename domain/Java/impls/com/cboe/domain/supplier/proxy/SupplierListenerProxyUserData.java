package com.cboe.domain.supplier.proxy;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Jing Chen
 */
public class SupplierListenerProxyUserData
{
    private static final String FIELD_DELIMITER = "\u0001";
    private static final String DEFAULT_VALUE = "n/a";
    private Map dataMap;

    public SupplierListenerProxyUserData()
    {
        dataMap = Collections.synchronizedMap(new HashMap(11));
    }

    public void addValue(String value)
    {
        if(value == null)
        {
            throw new IllegalArgumentException("value cannot be null");
        }

        value = value.trim();

        if(value.length() == 0)
        {
            throw new IllegalArgumentException("value cannot be empty");
        }

        if(value.indexOf(FIELD_DELIMITER) != -1)
        {
            throw new IllegalArgumentException("Neither key nor value can contain the field delimiter or key value delimiter:"+FIELD_DELIMITER);
        }
        dataMap.put(value, value);
    }

    public void removeValue(String value)
    {
        dataMap.remove(value);
    }

    public String toString()
    {
        if(dataMap.size() == 0)
        {
            return DEFAULT_VALUE;
        }
        StringBuffer buffer = new StringBuffer();
        synchronized(dataMap)
        {
            Iterator iterator = dataMap.values().iterator();
            while(iterator.hasNext())
            {
                buffer.append((String)iterator.next());
                if(iterator.hasNext())
                {
                    buffer.append(FIELD_DELIMITER);
                }
            }
        }

        return buffer.toString();
    }
}
