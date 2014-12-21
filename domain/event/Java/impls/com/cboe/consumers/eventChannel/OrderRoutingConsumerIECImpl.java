package com.cboe.consumers.eventChannel;


import com.cboe.idl.order.*;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.ohsEvents.OrderRoutingConsumer;

import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;

import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.util.*;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class OrderRoutingConsumerIECImpl extends BObject implements OrderRoutingConsumer
{
    private ConcurrentEventChannelAdapter internalEventChannel;

    public OrderRoutingConsumerIECImpl()
    {
        try
        {
        	internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.CAS_IEC);
        }
        catch (Exception e)
        {
        	Log.exception(this, "Exception getting CAS_IEC!", e);
        }

    }
    public void acceptManualOrders(RoutingParameterV2Struct routingParameterV2Struct,
                         OrderManualHandlingStructV2[] orders)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
        received.append("event received -> acceptManualOrders : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupParOrderStructSequenceContainer ordersContainer =
            new RoutingGroupParOrderStructSequenceContainer(routingParameterV2Struct, orders);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.PAR_ORDER_ACCEPTED, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, ordersContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptManualCancels(RoutingParameterV2Struct routingParameterV2Struct,
                                 ManualCancelRequestStructV2[] cancelRequests)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
        received.append("event received -> acceptManualCancels : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());
        

        RoutingGroupParCancelRequestStructSequenceContainer cancelRequestsContainer =
            new RoutingGroupParCancelRequestStructSequenceContainer(routingParameterV2Struct, cancelRequests);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.PAR_ORDER_CANCELED, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, cancelRequestsContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptManualCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
                                 ManualCancelReplaceStruct[] cancelReplaces)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
        received.append("event received -> acceptManualCancelReplaces : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());
        
        RoutingGroupParCancelReplaceStructSequenceContainer cancelReplacesContainer =
            new RoutingGroupParCancelReplaceStructSequenceContainer(routingParameterV2Struct, cancelReplaces);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.PAR_ORDER_CANCEL_REPLACED, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, cancelReplacesContainer);
            internalEventChannel.dispatch(event);
        }
    }


    public void acceptOrders(RoutingParameterV2Struct routingParameterV2Struct,
                             OrderRoutingStruct[] orders)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+45);
        received.append("event received -> acceptOrders : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupOrderStructSequenceContainer ordersContainer =
                new RoutingGroupOrderStructSequenceContainer(routingParameterV2Struct, orders);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_ORDER_ACCEPTED, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, ordersContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptCancels(RoutingParameterV2Struct routingParameterV2Struct,
                              CancelRoutingStruct[] cancels)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+50);
        received.append("event received -> acceptCancels : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupOrderCancelContainer cancelsContainer =
                new RoutingGroupOrderCancelContainer(routingParameterV2Struct,
                                                     cancels);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_ORDER_CANCELED, destination);
            ChannelEvent event =
                    internalEventChannel.getChannelEvent(this, channelKey,
                                                                      cancelsContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
                                     CancelReplaceRoutingStruct[] cancelReplaces)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
        received.append("event received -> acceptCancelReplaces : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupOrderCancelReplaceContainer cancelReplacesContainer =
                new RoutingGroupOrderCancelReplaceContainer(routingParameterV2Struct,
                                                            cancelReplaces);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_ORDER_CANCEL_REPLACED, destination);
            ChannelEvent event =
                    internalEventChannel.getChannelEvent(this, channelKey,
                                                                      cancelReplacesContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptFillReportReject(RoutingParameterV2Struct routingParameterV2Struct,
                                       FillReportRejectRoutingStruct[]  fillReportRejects )
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
        received.append("event received -> acceptFillReportRejects : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupFillReportRejectContainer rejectContainer =
                new RoutingGroupFillReportRejectContainer(routingParameterV2Struct, fillReportRejects);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_FILL_REPORT_REJECT, destination);
            ChannelEvent event =
                    internalEventChannel.getChannelEvent(this, channelKey,
                                                                      rejectContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptRemoveOrder(RoutingParameterV2Struct routingParameterV2Struct,
                                  OrderIdRoutingStruct[] orderIds)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+50);
        received.append("event received -> acceptRemoveOrder : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupOrderIdStructSequenceContainer ordersContainer =
                new RoutingGroupOrderIdStructSequenceContainer(routingParameterV2Struct,
                                                               orderIds);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_ORDER_REMOVED, destination);
            ChannelEvent event =
                    internalEventChannel.getChannelEvent(this, channelKey,
                                                                      ordersContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptLinkageCancelReport(RoutingParameterV2Struct routingParameterV2Struct,
                                          LinkageCancelReportRoutingStruct[] cancelReports)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
        received.append("event received -> acceptLinkageCancelReport : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupLinkageCancelReportContainer container =
                new RoutingGroupLinkageCancelReportContainer(routingParameterV2Struct,
                                                             cancelReports);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_LINKAGE_CANCEL_REPORT, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, container);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptLinkageFillReport(RoutingParameterV2Struct routingParameterV2Struct,
                                        LinkageFillReportRoutingStruct[] fillReports)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
        received.append("event received -> acceptLinkageFillReport : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupLinkageFillReportContainer container =
                new RoutingGroupLinkageFillReportContainer(routingParameterV2Struct, fillReports);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_LINKAGE_FILL_REPORT, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, container);
            internalEventChannel.dispatch(event);
        }
    }
    public void acceptOrderLocationServerResponse(OrderLocationServerResponseStruct response)
    {
        StringBuilder received = new StringBuilder(response.transactionId.userId.length()+70);
        received.append("event received -> acceptOrderLocationServerResponse : userId = ").append(response.transactionId.userId);
        Log.information(this, received.toString());

        String userId = response.transactionId.userId;
        ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_ORDERS_FOR_LOCATION, userId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, response);
        internalEventChannel.dispatch(event);

    }

    public void acceptOrderLocationSummaryServerResponse(OrderLocationSummaryServerResponseStruct response)
    {
        StringBuilder received = new StringBuilder(response.transactionId.userId.length()+75);
        received.append("event received -> acceptOrderLocationSummaryServerResponse : userId = ").append(response.transactionId.userId);
        Log.information(this, received.toString());

        String userId = response.transactionId.userId;
        ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_LOCATION_SUMMARY , userId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, response);
        internalEventChannel.dispatch(event);

    }

    /*
    *  Instruct OMT to remove a routed message from its message
    */

    public void acceptRemoveMessage(RoutingParameterV2Struct routingParameterV2Struct, long msgId)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+55);
        received.append("event received -> acceptRemoveMessage : source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupRemoveMessageContainer removeMessageContainer =
                new RoutingGroupRemoveMessageContainer(routingParameterV2Struct, msgId);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_REMOVE_MESSAGE, destination);
            ChannelEvent event =
                    internalEventChannel.getChannelEvent(this, channelKey,
                                                                      removeMessageContainer);
            internalEventChannel.dispatch(event);
        }

    }

    public void acceptTradeNotifications(RoutingParameterV2Struct routingParameterV2Struct,
                                         TradeNotificationRoutingStruct[] tradeNotifications)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
        received.append("event received -> acceptTradeNotifications, source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupTradeNotificationContainer container =
                new RoutingGroupTradeNotificationContainer(routingParameterV2Struct, tradeNotifications);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            if (Log.isDebugOn())
            {
                Log.debug("acceptTradeNotifications destination: "+destination);
            }
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_TRADE_NOTIFICATION, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, container);
            internalEventChannel.dispatch(event);
        }
    }

    public void acceptFillReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                         FillReportDropCopyRoutingStruct[] fillReportDropCopies)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
        received.append("event received -> acceptFillReportDropCopy, source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupFillReportDropCopyContainer container =
                new RoutingGroupFillReportDropCopyContainer(routingParameterV2Struct, fillReportDropCopies);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            if (Log.isDebugOn())
            {
                Log.debug("acceptFillReportDropCopy destination: "+destination);
            }
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_FILL_REPORT_DROP_COPY, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, container);
            internalEventChannel.dispatch(event);
        }
    }


     public void acceptCancelReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                            CancelReportDropCopyRoutingStruct[]  cancelRoprtDropCopies )
     {
         StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
         received.append("event received -> acceptCancelReportDropCopy, source = ").append(routingParameterV2Struct.source);
         Log.information(this, received.toString());

         RoutingGroupCancelReportDropCopyContainer container =
                new RoutingGroupCancelReportDropCopyContainer(routingParameterV2Struct, cancelRoprtDropCopies);

         String[] destinations = routingParameterV2Struct.destinations;
         for(String destination : destinations)
         {
            if (Log.isDebugOn())
            {
                Log.debug("acceptCancelReportDropCopy destination: "+destination);
            }
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_CANCEL_REPORT_DROP_COPY, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, container);
            internalEventChannel.dispatch(event);
         }
     }


    public void acceptManualOrderTimeout(RoutingParameterV2Struct routingParameterV2Struct, ManualOrderTimeoutRoutingStruct[] manualOrderTimeouts)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source.length()+60);
        received.append("event received -> acceptManualOrderTimeout, source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupManualOrderTimeoutContainer container =
                new RoutingGroupManualOrderTimeoutContainer(routingParameterV2Struct, manualOrderTimeouts);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_MANUAL_ORDER_TIMEOUT, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, container);
            internalEventChannel.dispatch(event);
        }
    }

	public void acceptManualFillTimeout(RoutingParameterV2Struct routingParameterV2Struct, ManualFillTimeoutRoutingStruct[] fillReports)
    {
        StringBuilder received = new StringBuilder(routingParameterV2Struct.source+60);
        received.append("event received -> acceptManualFillTimeout, source = ").append(routingParameterV2Struct.source);
        Log.information(this, received.toString());

        RoutingGroupManualFillTimeoutContainer container =
                new RoutingGroupManualFillTimeoutContainer(routingParameterV2Struct, fillReports);

        String[] destinations = routingParameterV2Struct.destinations;
        for(String destination : destinations)
        {
            ChannelKey channelKey = new ChannelKey(ChannelKey.OMT_MANUAL_FILL_TIMEOUT, destination);
            ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, container);
            internalEventChannel.dispatch(event);
        }
    }
}
