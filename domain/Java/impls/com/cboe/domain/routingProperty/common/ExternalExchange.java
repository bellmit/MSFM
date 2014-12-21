package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: ExternalExchange
//
// PACKAGE: com.cboe.domain.routingProperty.common
// 
// Created: Aug 24, 2006 1:20:21 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class ExternalExchange
{
    public String externalExchange;

    public ExternalExchange(String exchange)
    {
        this.externalExchange = exchange;
    }

    public String toString()
    {
        return externalExchange;
    }
}