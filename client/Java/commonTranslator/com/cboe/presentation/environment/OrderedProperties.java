//
// -----------------------------------------------------------------------------------
// Source file: OrderedProperties.java
//
// PACKAGE: com.cboe.presentation.environment
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.environment;

import java.util.*;

public class OrderedProperties extends Properties
{
    private List<Object> keyList = new ArrayList<Object>();
    public OrderedProperties()
    {
        super();
    }

    public synchronized List<Object> getKeyList()
    {
        List<Object> retVal = new ArrayList<Object>(keyList.size());
        retVal.addAll(keyList);
        return retVal;
    }

    public synchronized Object put(Object key, Object value)
    {
        keyList.add(key);
        return super.put(key, value);
    }

    public synchronized Object remove(Object key)
    {
        for(Object obj : keyList)
        {
            if(obj == key || obj.equals(key))
            {
                keyList.remove(obj);
            }
        }
        return super.remove(key);
    }
}
