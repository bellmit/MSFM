//
// -----------------------------------------------------------------------------------
// Source file: NBBOFactory.java
//
// PACKAGE: com.cboe.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.idl.cmiMarketData.NBBOStruct;

import com.cboe.interfaces.presentation.marketData.NBBO;

public class NBBOFactory
{
    public static NBBO createNBBO(NBBOStruct nbboStruct)
    {
        return new NBBOImpl(nbboStruct);
    }
}