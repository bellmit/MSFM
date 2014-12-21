//
// -----------------------------------------------------------------------------------
// Source file: SimpleProductClass.java
//
// PACKAGE: com.cboe.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;


public class SimpleProductClass  
{
    public static final int DEFAULT_CLASS_KEY = com.cboe.idl.cmiConstants.ProductClass.DEFAULT_CLASS_KEY;

    private int classKey = -1;
    private String tradingSession;

    public SimpleProductClass(String session)
    {
        this(session, DEFAULT_CLASS_KEY);
    }

    public SimpleProductClass(String session, int classKey)
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
}