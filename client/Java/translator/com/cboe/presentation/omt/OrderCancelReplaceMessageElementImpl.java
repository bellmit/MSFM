//
// -----------------------------------------------------------------------------------
// Source file: OrderCancelReplaceMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.order.CancelReplaceRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.omt.OrderCancelReplaceMessageElement;
import com.cboe.interfaces.presentation.order.CancelRequest;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.presentation.order.CancelRequestFactory;
import com.cboe.presentation.order.OrderFactory;

/**
 * An implementation of the <code>OrderCancelMessageElement</code> interface,
 * which allows access to details of a cancel order OMT message
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public class OrderCancelReplaceMessageElementImpl
        extends OrderMessageElementImpl
        implements OrderCancelReplaceMessageElement
{
    private CancelReplaceRoutingStruct cancelReplaceRoutingStruct;
    private CancelRequest cancelRequest;
    private Order replacementOrder;
    private long identifier;

    protected OrderCancelReplaceMessageElementImpl(CancelReplaceRoutingStruct cancelReplaceRoutingStruct,
                                                   RoutingParameterV2Struct routingParameterV2Struct)
    {
        super(cancelReplaceRoutingStruct.originalOrder,routingParameterV2Struct,
              MessageElement.MessageType.ORDER_CANCEL_REPLACED);
        this.cancelReplaceRoutingStruct = cancelReplaceRoutingStruct;
        cancelRequest =
                CancelRequestFactory.createCancelRequest(cancelReplaceRoutingStruct.cancelRequest);
        replacementOrder = OrderFactory.createOrder(cancelReplaceRoutingStruct.replacementOrder);
        identifier = cancelReplaceRoutingStruct.orderMaintenanceIdentifier;
        setRouteReasonStruct(cancelReplaceRoutingStruct.routeReason);
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(isEqual)
        {
            if(o != null && o instanceof OrderCancelReplaceMessageElement)
            {
                OrderCancelReplaceMessageElement that = (OrderCancelReplaceMessageElement) o;

                isEqual = getIdentifier() == that.getIdentifier();
            }
        }

        return isEqual;
    }

    public Order getOriginalOrder()
    {
        return super.getOrder();
    }

    public Order getReplacementOrder()
    {
        return replacementOrder;
    }

    public CancelRequest getCancelRequest()
    {
        return cancelRequest;
    }

    public long getIdentifier()
    {
        return identifier;
    }

    public int getProductKeyValue()
    {
        return cancelReplaceRoutingStruct.productKeys.productKey;
    }


    public String getSessionName()
    {
        return cancelRequest.getSessionName();
    }

    public CBOEId getCboeId()
    {
        return cancelRequest.getOrderId().getCboeId();
    }
}