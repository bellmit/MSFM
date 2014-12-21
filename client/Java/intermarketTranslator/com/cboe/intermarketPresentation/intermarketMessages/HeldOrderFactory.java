//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderFactory.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrder;

public class HeldOrderFactory
{
    public static HeldOrder createHeldOrder(HeldOrderStruct heldOrderStruct)
    {
        return new HeldOrderImpl(heldOrderStruct);
    }
}