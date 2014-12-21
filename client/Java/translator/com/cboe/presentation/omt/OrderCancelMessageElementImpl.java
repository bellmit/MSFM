//
// -----------------------------------------------------------------------------------
// Source file: OrderCancelMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.order.CancelRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.omt.OrderCancelMessageElement;
import com.cboe.interfaces.presentation.order.CancelRequest;
import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.presentation.order.CancelRequestFactory;

/**
 * An implementation of the <code>OrderCancelMessageElement</code> interface,
 * which allows access to details of a cancel order OMT message
 */
public class OrderCancelMessageElementImpl
        extends OrderMessageElementImpl
        implements OrderCancelMessageElement
{
    private CancelRoutingStruct cancelRoutingStruct;
    private CancelRequest cancelRequest;
    private long identifier;

    protected OrderCancelMessageElementImpl(CancelRoutingStruct cancelRoutingStruct,
                                  RoutingParameterV2Struct routingParameterV2Struct)
    {
        super(cancelRoutingStruct.order, routingParameterV2Struct,
              MessageElement.MessageType.ORDER_CANCELED);
        this.cancelRoutingStruct = cancelRoutingStruct;
        cancelRequest =
                CancelRequestFactory.createCancelRequest(cancelRoutingStruct.cancelRequest);
        identifier = cancelRoutingStruct.orderMaintenanceIdentifier;
        setRouteReasonStruct(cancelRoutingStruct.routeReason);
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(isEqual)
        {
            if(o != null && o instanceof OrderCancelMessageElement)
            {
                OrderCancelMessageElement that = (OrderCancelMessageElement) o;

                isEqual = getIdentifier() == that.getIdentifier();
            }
        }

        return isEqual;
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
        return cancelRoutingStruct.productKeys.productKey;
    }

    public CBOEId getCboeId()
    {
        return cancelRequest.getOrderId().getCboeId();
    }


    public String getSessionName()
    {
        return cancelRequest.getSessionName();
    }
}
