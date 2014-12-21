//
// -----------------------------------------------------------------------------------
// Source file: \client\Java\translator\com\cboe\consumers\callback\CancelReplaceOrderStructWrapper.java
//
// PACKAGE: com.cboe.consumers.callback;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.consumers.callback;

import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.OrderStruct;


/**
 * Wraps CancelRequestStruct and OrderStruct, so that a single object can be published to IEC, for example when a
 * cancel-replace OMT message is received
 * @author  Shawn Khosravani
 * @since   OMT - 5/22/2007
 * @see     OrderRoutingConsumerImpl#acceptCancelReplace
 * @see     com.cboe.presentation.omt.OMTMessageListener#processEvent
 */
@SuppressWarnings({"PublicField"})
public class CancelReplaceOrderStructWrapper
{
    public CancelRequestStruct cancelRequest;
    public OrderStruct         replacementOrder;

    public CancelReplaceOrderStructWrapper(CancelRequestStruct cancelRequest, OrderStruct replacementOrder)
    {
        this.cancelRequest    = cancelRequest;
        this.replacementOrder = replacementOrder;
    }

    @SuppressWarnings({"ObjectToString"})
    @Override
    public String toString()
    {
        return "CancelReplaceOrderStructWrapper: cancelRequest=[" + cancelRequest
               + "]\nreplacementOrder=[" + replacementOrder + "].";
    }
}
