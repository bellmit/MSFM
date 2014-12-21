//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderCancelRequestFactory.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelRequestStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderCancelRequest;

public class HeldOrderCancelRequestFactory
{
    public static HeldOrderCancelRequest createHeldOrderCancelRequest(HeldOrderCancelRequestStruct heldOrderCancelRequestStruct)
    {
        return new HeldOrderCancelRequestImpl(heldOrderCancelRequestStruct);
    }
}