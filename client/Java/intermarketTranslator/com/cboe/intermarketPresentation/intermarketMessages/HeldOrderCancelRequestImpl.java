//
// -----------------------------------------------------------------------------------
// Source file: HeldOrderCancelRequestImpl.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelRequestStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderCancelRequest;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.presentation.order.CancelRequest;

import com.cboe.presentation.util.CBOEIdImpl;
import com.cboe.presentation.order.CancelRequestFactory;

class HeldOrderCancelRequestImpl implements HeldOrderCancelRequest
{
    protected CBOEId        cancelRequestId;
    protected CancelRequest cancelRequest;

    protected HeldOrderCancelRequestStruct heldOrderCancelRequestStruct;

    public HeldOrderCancelRequestImpl(HeldOrderCancelRequestStruct heldOrderCancelRequestStruct)
    {
        this.heldOrderCancelRequestStruct = heldOrderCancelRequestStruct;
        initialize();
    }

    private void initialize()
    {
        cancelRequestId = new CBOEIdImpl(heldOrderCancelRequestStruct.cancelReqId);
        cancelRequest = CancelRequestFactory.createCancelRequest(heldOrderCancelRequestStruct.cancelRequest);
    }

    public CBOEId getCancelRequestId()
    {
        return cancelRequestId;
    }

    public CancelRequest getCancelRequest()
    {
        return cancelRequest;
    }

    /**
     * Gets the underlying struct
     * @return HeldOrderCancelRequestStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public HeldOrderCancelRequestStruct getStruct()
    {
        return heldOrderCancelRequestStruct;
    }
}
