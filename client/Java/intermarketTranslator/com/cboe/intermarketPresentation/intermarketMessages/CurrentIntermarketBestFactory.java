//
// -----------------------------------------------------------------------------------
// Source file: CurrentIntermarketBestFactory.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketBestStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.CurrentIntermarketBest;

public class CurrentIntermarketBestFactory
{
    public static CurrentIntermarketBest createCurrentIntermarketBest(CurrentIntermarketBestStruct currentIntermarketBestStruct)
    {
        return new CurrentIntermarketBestImpl(currentIntermarketBestStruct);
    }
}