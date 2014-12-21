//
// -----------------------------------------------------------------------------------
// Source file: SimpleComplexProductClass.java
//
// PACKAGE: com.cboe.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;

/**
 * Immutable class to represent a product class in a routing property or property key.
 *
 * This can represent a specific SessionProductClass by its session and classKey, or
 * it can be a "default" to represent all "Complex" or all "Simple" ProductClasses for
 * a trading session.
 */
public class SimpleComplexProductClass
{
    public static final int DEFAULT_CLASS_KEY = com.cboe.idl.cmiConstants.ProductClass.DEFAULT_CLASS_KEY;

    private int classKey = -1;
    private String tradingSession;

    public SimpleComplexProductClass(String session)
    {
        this(session, DEFAULT_CLASS_KEY);
    }

    public SimpleComplexProductClass(String session, int classKey)
    {
        this.tradingSession = session;
        this.classKey = classKey;
    }

    public int getClassKey()
    {
        return classKey;
    }

    public String getTradingSession()
    {
        return tradingSession;
    }

    public boolean isDefaultProductClass()
    {
        return classKey == DEFAULT_CLASS_KEY;
    }

    public boolean isDefaultClassKey()
    {
        return classKey == DEFAULT_CLASS_KEY;
    }

//    public String toString()
//    {
//        return "session=" + tradingSession + ", classKey=" + getClassKey();
//    }
}