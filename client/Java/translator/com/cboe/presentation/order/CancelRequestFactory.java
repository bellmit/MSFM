//
// -----------------------------------------------------------------------------------
// Source file: CancelRequestFactory.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.CancelRequestStruct;

import com.cboe.interfaces.presentation.order.CancelRequest;

public class CancelRequestFactory
{
    public static CancelRequest createCancelRequest(CancelRequestStruct cancelRequestStruct)
    {
        return new CancelRequestImpl(cancelRequestStruct);
    }
}