//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderCancelRequest.java
//
// PACKAGE: com.cboe.interfaces.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelRequestStruct;

import com.cboe.interfaces.presentation.order.CancelRequest;
import com.cboe.interfaces.presentation.util.CBOEId;

public interface HeldOrderCancelRequest
{
    /**
     * Gets the underlying struct
     * @return HeldOrderCancelRequestStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public HeldOrderCancelRequestStruct getStruct();

    public CBOEId getCancelRequestId();
    public CancelRequest getCancelRequest();
}