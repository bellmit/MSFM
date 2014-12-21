// -----------------------------------------------------------------------------------
// Source file: VolumeTypeImpl.java
//
// PACKAGE: com.cboe.interfaces.presentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.bookDepth;

import com.cboe.interfaces.presentation.bookDepth.OrderBookPriceViewType;
import com.cboe.idl.cmiConstants.OrderBookPriceViewTypes;

public class OrderBookPriceViewTypeImpl implements OrderBookPriceViewType
{
    public static final OrderBookPriceViewType BY_ORIGIN_TYPE = new OrderBookPriceViewTypeImpl(OrderBookPriceViewTypes.BY_ORIGIN_TYPE, "Origin");

    private static final OrderBookPriceViewType[] typeList = {BY_ORIGIN_TYPE};

    public static OrderBookPriceViewType getByName(String name)
    {
        for (int i = 0; i < typeList.length; i++)
        {
            if (typeList[i].getName().equals(name))
            {
                return typeList[i];
            }
        }
        return null;
    }

    public static OrderBookPriceViewType getByKey(int key)
    {
        for (int i = 0; i < typeList.length; i++)
        {
            if (typeList[i].getKey() == key)
            {
                return typeList[i];
            }
        }
        return null;
    }

    public static OrderBookPriceViewType[] getViewTypes()
    {
        return typeList;
    }

    
    private int viewTypeKey;
    private String viewTypeName;
    
    private OrderBookPriceViewTypeImpl(int key, String name)
    {
        this();
        this.viewTypeKey = key;
        this.viewTypeName = name;
    }

    private OrderBookPriceViewTypeImpl()
    {
        super();
    }

    public String getName()
    {
        return this.viewTypeName;
    }

    public int getKey()
    {
        return this.viewTypeKey;
    }

    public boolean equals(Object obj)
    {
        boolean result = false;
        if (obj instanceof OrderBookPriceViewType)
        {
            OrderBookPriceViewType otherType = (OrderBookPriceViewType) obj;
            result = (this.getKey() == otherType.getKey());
        }
        return result;
    }

    public String toString()
    {
        return getName();
    }

}
