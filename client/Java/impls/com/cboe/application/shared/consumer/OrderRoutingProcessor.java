//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingProcessor.java
//
// PACKAGE: com.cboe.application.shared.consumer
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.shared.consumer;

import com.cboe.idl.order.OrderLocationServerResponseStruct;
import com.cboe.idl.order.OrderLocationSummaryServerResponseStruct;

import com.cboe.interfaces.application.OrderRoutingCollector;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.domain.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class OrderRoutingProcessor
        implements EventChannelListener
{
    private OrderRoutingCollector parent;

    public OrderRoutingProcessor()
    {
    }

    public void setParent(OrderRoutingCollector parent)
    {
        this.parent = parent;
    }

    public OrderRoutingCollector getParent()
    {
        return parent;
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey) event.getChannel();
        Object eventData = event.getEventData();

        if(parent != null)
        {
            switch(channelKey.channelType)
            {


                case ChannelType.OMT_ORDER_ACCEPTED:
                    RoutingGroupOrderStructSequenceContainer container1 =
                            (RoutingGroupOrderStructSequenceContainer) eventData;
                    parent.acceptOrders(container1.getRoutingParameterV2Struct(),
                                        container1.getOrderStructs());
                    break;
                case ChannelType.OMT_ORDER_CANCELED:
                    RoutingGroupOrderCancelContainer container2 =
                            (RoutingGroupOrderCancelContainer) event.getEventData();
                    parent.acceptCancels(container2.getRoutingParameterV2Struct(),
                                         container2.getCancelRoutingStructs());
                    break;
                case ChannelType.OMT_ORDER_CANCEL_REPLACED:
                    RoutingGroupOrderCancelReplaceContainer container3 =
                            (RoutingGroupOrderCancelReplaceContainer) event.getEventData();
                    parent.acceptCancelReplaces(container3.getRoutingParameterV2Struct(),
                                                container3.getCancelReplaceRoutingStructs());
                    break;
                case ChannelType.OMT_FILL_REPORT_REJECT:
                    RoutingGroupFillReportRejectContainer container4 =
                            (RoutingGroupFillReportRejectContainer) event.getEventData();
                    parent.acceptFillReportReject(container4.getRoutingParameterV2Struct(),
                                                  container4.getFillReportRejects());

                    break;
                case ChannelType.OMT_ORDER_REMOVED:
                    RoutingGroupOrderIdStructSequenceContainer container5 =
                            (RoutingGroupOrderIdStructSequenceContainer) event.getEventData();
                    parent.acceptRemoveOrder(container5.getRoutingParameterV2Struct(),
                                             container5.getOrderIdStructs());
                    break;
                case ChannelType.OMT_LINKAGE_CANCEL_REPORT:
                    RoutingGroupLinkageCancelReportContainer container6 =
                            (RoutingGroupLinkageCancelReportContainer) event.getEventData();
                    parent.acceptLinkageCancelReport(container6.getRoutingParameterV2Struct(),
                                                     container6.getLinkageCancelReportStructs());
                    break;
                case ChannelType.OMT_LINKAGE_FILL_REPORT:
                    RoutingGroupLinkageFillReportContainer container7 =
                            (RoutingGroupLinkageFillReportContainer) event.getEventData();
                    parent.acceptLinkageFillReport(container7.getRoutingParameterV2Struct(),
                                                   container7.getFillReports());
                    break;
                case ChannelType.OMT_ORDERS_FOR_LOCATION:
                    OrderLocationServerResponseStruct response1 =
                            (OrderLocationServerResponseStruct) event.getEventData();
                    parent.acceptOrderLocationServerResponse(response1);
                    break;

                case ChannelType.OMT_LOCATION_SUMMARY:
                    OrderLocationSummaryServerResponseStruct response2 =
                            (OrderLocationSummaryServerResponseStruct) event.getEventData();
                    parent.acceptOrderLocationSummaryServerResponse(response2);

                    break;
                case ChannelType.OMT_REMOVE_MESSAGE:
                    RoutingGroupRemoveMessageContainer container8 =
                            (RoutingGroupRemoveMessageContainer) event.getEventData();
                    parent.acceptRemoveMessage(container8.getRoutingParameterV2Struct(),
                                             container8.getMsgId());
                    break;
                case ChannelType.OMT_TRADE_NOTIFICATION:
                    RoutingGroupTradeNotificationContainer container9 =
                            (RoutingGroupTradeNotificationContainer) event.getEventData();
                    parent.acceptTradeNotifications(container9.getRoutingParameterV2Struct(),
                                                   container9.getTradeNotifications());
                    break;
                case ChannelType.OMT_FILL_REPORT_DROP_COPY:
                    RoutingGroupFillReportDropCopyContainer container10 =
                            (RoutingGroupFillReportDropCopyContainer) event.getEventData();
                    parent.acceptFillReportDropCopy(container10.getRoutingParameterV2Struct(),
                    								container10.getFillReportDropCopies());

                    break;
                case ChannelType.OMT_CANCEL_REPORT_DROP_COPY:
                    RoutingGroupCancelReportDropCopyContainer container11 =
                            (RoutingGroupCancelReportDropCopyContainer) event.getEventData();
                    parent.acceptCancelReportDropCopy(container11.getRoutingParameterV2Struct(),
                    								container11.getCancelRoprtDropCopies());

                    break;
                case ChannelType.OMT_MANUAL_ORDER_TIMEOUT:
                    RoutingGroupManualOrderTimeoutContainer container12 =
                            (RoutingGroupManualOrderTimeoutContainer) eventData;
                    parent.acceptManualOrderTimeout(container12.getRoutingParameterV2Struct(),
                                        container12.getManualOrderTimeouts());
                    break;
                case ChannelType.OMT_MANUAL_FILL_TIMEOUT:
                    RoutingGroupManualFillTimeoutContainer container13 =
                            (RoutingGroupManualFillTimeoutContainer) eventData;
                    parent.acceptManualFillTimeout(container13.getRoutingParameterV2Struct(),
                                        container13.getManualFillTimeouts());
                    break;

                case ChannelType.PAR_ORDER_ACCEPTED:
                    RoutingGroupParOrderStructSequenceContainer container14 =
                            (RoutingGroupParOrderStructSequenceContainer) eventData;
                    parent.acceptManualOrders(container14.getRoutingParameterV2Struct(),
                                        container14.getOrders());
                    break;

                case ChannelType.PAR_ORDER_CANCELED:
                    RoutingGroupParCancelRequestStructSequenceContainer container15 =
                            (RoutingGroupParCancelRequestStructSequenceContainer) eventData;
                    parent.acceptManualCancels(container15.getRoutingParameterV2Struct(),
                                        container15.getCancelRequests());
                    break;


                case ChannelType.PAR_ORDER_CANCEL_REPLACED:
                    RoutingGroupParCancelReplaceStructSequenceContainer container16 =
                            (RoutingGroupParCancelReplaceStructSequenceContainer) eventData;
                    parent.acceptManualCancelReplaces(container16.getRoutingParameterV2Struct(),
                                        container16.getCancelReplaces());
                    break;

                default:
                    if (Log.isDebugOn())
                    {
                        Log.debug( "OrderRoutingProcessor -> Wrong Channel : " + channelKey.channelType );
                    }
                    break;
            }
        }
    }
}
