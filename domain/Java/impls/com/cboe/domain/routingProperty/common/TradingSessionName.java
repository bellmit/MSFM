package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: TradingSessionName
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Aug 8, 2006 3:01:45 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
public class TradingSessionName
{
    public String sessionName;

    public TradingSessionName(String sessionName)
    {
        this.sessionName = sessionName;
    }
    
    public String toString()
    {
        return sessionName;
    }
}
