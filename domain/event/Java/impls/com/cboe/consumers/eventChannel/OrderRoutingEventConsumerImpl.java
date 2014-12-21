//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingEventConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.eventChannel;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.eventChannel;

import org.omg.CORBA.Any;
import org.omg.CosEventComm.Disconnected;


import com.cboe.idl.cmiTradeNotification.TradeNotificationStruct;
import com.cboe.idl.order.*;
import com.cboe.idl.util.RouteReasonStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;
import com.cboe.idl.ohsEvents.POA_OrderRoutingEventConsumer;
import com.cboe.interfaces.ohsEvents.OrderRoutingConsumer;

import com.cboe.util.ChannelKey;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.channel.ChannelEvent;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.domain.util.RoutingGroupLinkageCancelReportContainer;

public class OrderRoutingEventConsumerImpl
        extends POA_OrderRoutingEventConsumer
        implements OrderRoutingConsumer
{
    private OrderRoutingConsumer delegate;

    public OrderRoutingEventConsumerImpl(OrderRoutingConsumer orderRoutingConsumer)
    {
        delegate = orderRoutingConsumer;
    }

    public void acceptOrders(RoutingParameterV2Struct routingParameterV2Struct,
                             OrderRoutingStruct[] orders)
    {
        delegate.acceptOrders(routingParameterV2Struct, orders);
    }

    public void acceptCancels(RoutingParameterV2Struct routingParameterV2Struct,
                              CancelRoutingStruct[] cancels)
    {
        delegate.acceptCancels(routingParameterV2Struct, cancels);
    }

    public void acceptCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
                                     CancelReplaceRoutingStruct[] cancelReplaces)
    {
        delegate.acceptCancelReplaces(routingParameterV2Struct, cancelReplaces);
    }

    public void acceptFillReportReject(RoutingParameterV2Struct routingParameterV2Struct,
                                       FillReportRejectRoutingStruct[]  fillReportRejects )
    {
        delegate.acceptFillReportReject(routingParameterV2Struct,
                                        fillReportRejects);
    }

    public void acceptRemoveOrder(RoutingParameterV2Struct routingParameterV2Struct,
                                  OrderIdRoutingStruct[] orderIds)
    {
        delegate.acceptRemoveOrder(routingParameterV2Struct, orderIds);
    }

    public void acceptLinkageCancelReport(RoutingParameterV2Struct routingParameterV2Struct,
                                          LinkageCancelReportRoutingStruct[] cancelReports)
    {
        delegate.acceptLinkageCancelReport(routingParameterV2Struct, cancelReports);
    }

    public void acceptLinkageFillReport(RoutingParameterV2Struct routingParameterV2Struct,
                                        LinkageFillReportRoutingStruct[] fillReports)
    {
        delegate.acceptLinkageFillReport(routingParameterV2Struct, fillReports);
    }

    public void acceptOrderLocationServerResponse(OrderLocationServerResponseStruct response)
    {
        delegate.acceptOrderLocationServerResponse(response);
    }

    public void acceptOrderLocationSummaryServerResponse(OrderLocationSummaryServerResponseStruct response)
    {
        delegate.acceptOrderLocationSummaryServerResponse(response);
    }

    /*
    * Instruct OMT to remove a routed message from its message list
    */
    public void acceptRemoveMessage(RoutingParameterV2Struct routingParameterV2Struct, long msgId)
    {
        delegate.acceptRemoveMessage(routingParameterV2Struct, msgId);
    }

    public void acceptManualFillTimeout(RoutingParameterV2Struct routingParameters, ManualFillTimeoutRoutingStruct[] fillReports)
    {
        delegate.acceptManualFillTimeout(routingParameters, fillReports);
    }

    public void acceptManualOrderTimeout(RoutingParameterV2Struct routingParameters, ManualOrderTimeoutRoutingStruct[] manualOrderTimeouts)
    {
        delegate.acceptManualOrderTimeout(routingParameters, manualOrderTimeouts);
    }
    public void acceptTradeNotifications(RoutingParameterV2Struct routingParameterV2Struct, TradeNotificationRoutingStruct[] tradeNotifications)
    {
        delegate.acceptTradeNotifications(routingParameterV2Struct, tradeNotifications);
    }

    public void acceptFillReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                         FillReportDropCopyRoutingStruct[] fillReportDropCopies)
    {
        delegate.acceptFillReportDropCopy(routingParameterV2Struct, fillReportDropCopies);
    }


    public void acceptCancelReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                            CancelReportDropCopyRoutingStruct[]  cancelRoprtDropCopies )
    {
        delegate.acceptCancelReportDropCopy(routingParameterV2Struct, cancelRoprtDropCopies);
    }

	public void acceptManualOrders(RoutingParameterV2Struct routingParameterV2Struct, OrderManualHandlingStructV2[] orders)
    {
        delegate.acceptManualOrders(routingParameterV2Struct, orders);
    }

    public void acceptManualCancels(RoutingParameterV2Struct routingParameterV2Struct, ManualCancelRequestStructV2[] cancelRequests)
	    {
	        delegate.acceptManualCancels(routingParameterV2Struct, cancelRequests);
    }

    public void acceptManualCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct, ManualCancelReplaceStruct[] cancelReplaces)
	    {
	        delegate.acceptManualCancelReplaces(routingParameterV2Struct, cancelReplaces);
    }


        @SuppressWarnings({"ReturnOfNull"})

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(Any data) throws Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
