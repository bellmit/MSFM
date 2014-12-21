//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderDetailFactory.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderDetail;

public class HeldOrderDetailFactory
{
    public static HeldOrderDetail createHeldOrderDetail(HeldOrderDetailStruct heldOrderDetailStruct)
    {
        return new HeldOrderDetailImpl(heldOrderDetailStruct);
    }
}