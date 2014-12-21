//
// -----------------------------------------------------------------------------------
// Source file: CurrentIntermarketFactory.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.CurrentIntermarket;

public class CurrentIntermarketFactory
{
    public static CurrentIntermarket createCurrentIntermarket(CurrentIntermarketStruct currentIntermarketStruct)
    {
        return new CurrentIntermarketImpl(currentIntermarketStruct);
    }
}