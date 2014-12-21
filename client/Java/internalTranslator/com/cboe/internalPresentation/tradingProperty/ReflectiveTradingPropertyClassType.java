//
// -----------------------------------------------------------------------------------
// Source file: ReflectiveTradingPropertyClassType.java
//
// PACKAGE: com.cboe.internalPresentation.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingProperty;

import java.lang.reflect.Method;

import com.cboe.interfaces.internalPresentation.tradingProperty.OldTradingPropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.TradingPropertyClassType;

public class ReflectiveTradingPropertyClassType extends TradingPropertyClassType
{
    private Method saveMethod;
    private String[] saveMethodParmNames;
    private Method queryMethod;
    private String[] queryMethodParmNames;
    private Method queryAllClassesMethod;
    private String[] queryAllClassesMethodParmNames;

    public ReflectiveTradingPropertyClassType(TradingPropertyType tradingPropertyType, Class classType,
                                              Method queryMethod, String[] queryMethodParmNames,
                                              Method saveMethod, String[] saveMethodParmNames)
    {
        this(tradingPropertyType, classType, queryMethod, queryMethodParmNames, saveMethod, saveMethodParmNames,
             null, null);
    }


    public ReflectiveTradingPropertyClassType(TradingPropertyType tradingPropertyType, Class classType,
                                              Method queryMethod, String[] queryMethodParmNames,
                                              Method saveMethod, String[] saveMethodParmNames,
                                              Method queryAllClassesMethod, String[] queryAllClassesMethodParmNames)
    {
        super(tradingPropertyType, classType);
        if(queryMethod == null)
        {
            throw new IllegalArgumentException("queryMethod may not be null.");
        }
        if(queryMethodParmNames == null || queryMethodParmNames.length == 0)
        {
            throw new IllegalArgumentException("queryMethodParmNames may not be null or empty.");
        }
        if(saveMethod == null)
        {
            throw new IllegalArgumentException("saveMethod may not be null.");
        }
        if(saveMethodParmNames == null || saveMethodParmNames.length == 0)
        {
            throw new IllegalArgumentException("saveMethodParmNames may not be null or empty.");
        }
        if(!OldTradingPropertyGroup.class.isAssignableFrom(classType))
        {
            throw new IllegalArgumentException("classType must be a OldTradingPropertyGroup.");
        }

        this.queryMethod = queryMethod;
        this.saveMethod = saveMethod;
        this.saveMethodParmNames = saveMethodParmNames;
        this.queryMethodParmNames = queryMethodParmNames;
        this.queryAllClassesMethod = queryAllClassesMethod;
        this.queryAllClassesMethodParmNames = queryAllClassesMethodParmNames;
    }

    public String[] getQueryMethodParmNames()
    {
        return queryMethodParmNames;
    }

    public String[] getQueryAllClassesMethodParmNames()
    {
        return queryAllClassesMethodParmNames;
    }

    public String[] getSaveMethodParmNames()
    {
        return saveMethodParmNames;
    }

    public Method getQueryMethod()
    {
        return queryMethod;
    }

    public Method getQueryAllClassesMethod()
    {
        return queryAllClassesMethod;
    }

    public Method getSaveMethod()
    {
        return saveMethod;
    }

    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof ReflectiveTradingPropertyClassType))
        {
            return false;
        }
        if(!super.equals(o))
        {
            return false;
        }

        final ReflectiveTradingPropertyClassType reflectiveTradingPropertyClassType = (ReflectiveTradingPropertyClassType) o;

        if(!queryMethod.equals(reflectiveTradingPropertyClassType.queryMethod))
        {
            return false;
        }
        if(!queryAllClassesMethod.equals(reflectiveTradingPropertyClassType.queryAllClassesMethod))
        {
            return false;
        }
        if(!saveMethod.equals(reflectiveTradingPropertyClassType.saveMethod))
        {
            return false;
        }

        return true;
    }
}
