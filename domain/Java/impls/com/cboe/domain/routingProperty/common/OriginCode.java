package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: OriginCode
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Aug 8, 2006 3:02:49 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
public class OriginCode
{
    public char originCode;

    public OriginCode(char originCode)
    {
        this.originCode = originCode;
    }
    
    public String toString()
    {
        return Character.toString(originCode);
    }
}
