//
// -----------------------------------------------------------------------------------
// Source file: FIXAPIFactory.java
//
// PACKAGE: com.cboe.interfaces.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

/**
 * Defines a common definition for an FIXAPIFactory.
 */
public interface FIXAPIFactory extends APIFactory
{
    public FIXMarketMakerAPI findFIXMarketMakerAPI();
}

