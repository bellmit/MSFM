//
// -----------------------------------------------------------------------------------
// Source file: FIXAPIFactoryImpl.java
//
// PACKAGE: com.cboe.presentation.fix.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.fix.api;

import com.cboe.interfaces.presentation.api.*;

import com.cboe.presentation.fix.api.FIXMarketMakerAPIFactory;
import com.cboe.presentation.api.APIFactoryImpl;

/**
 * Implements the finders for a FIXAPIFactory for the client GUI.
 */
public class FIXAPIFactoryImpl extends APIFactoryImpl implements FIXAPIFactory
{
    /**
     * FIXAPIFactoryImpl constructor comment.
     */
    public FIXAPIFactoryImpl()
    {
        super();
    }

    public FIXMarketMakerAPI findFIXMarketMakerAPI()
    {
        return FIXMarketMakerAPIFactory.find();
    }
}

