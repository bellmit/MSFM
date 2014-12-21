//
// -----------------------------------------------------------------------------------
// Source file: TestTradingProperty.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.domain.tradingProperty.AbstractTradingProperty;
import com.cboe.domain.tradingProperty.TradingPropertyTypeImpl;

public class TestTradingProperty extends AbstractTradingProperty implements TradingProperty
{
    int int1;
    double double1;
    String string1;
    int[] intArray1;

    public TestTradingProperty(String propertyName, String sessionName, int classKey,
                               double double1, int int1, int[] intArray1, String string1)
    {
        super(propertyName, sessionName, classKey);
        this.double1 = double1;
        this.int1 = int1;
        this.intArray1 = intArray1;
        this.string1 = string1;
    }

    public int compareTo(Object object)
    {
        int result;
        int myValue = getInt1();
        int theirValue = ((TestTradingProperty) object).getInt1();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    public double getDouble1()
    {
        return double1;
    }

    public void setDouble1(double double1)
    {
        this.double1 = double1;
    }

    public int getInt1()
    {
        return int1;
    }

    public void setInt1(int int1)
    {
        this.int1 = int1;
    }

    public int[] getIntArray1()
    {
        return intArray1;
    }

    public void setIntArray1(int[] intArray1)
    {
        this.intArray1 = intArray1;
    }

    public int getIntArray1(int index)
    {
        return intArray1[index];
    }

    public void setIntArray1(int index, int newInt)
    {
        this.intArray1[index] = newInt;
    }

    public String getString1()
    {
        return string1;
    }

    public void setString1(String string1)
    {
        this.string1 = string1;
    }

    //TradingProperty Interface support
    public TradingPropertyType getTradingPropertyType()
    {
        return TradingPropertyTypeImpl.BOOK_DEPTH_SIZE;
    }
}
