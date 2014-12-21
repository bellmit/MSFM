//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.callback;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;


import com.cboe.idl.order.CancelReplaceRoutingStruct;
import com.cboe.idl.order.CancelReportDropCopyRoutingStruct;
import com.cboe.idl.order.CancelRoutingStruct;
import com.cboe.idl.order.FillReportDropCopyRoutingStruct;
import com.cboe.idl.order.FillReportRejectRoutingStruct;
import com.cboe.idl.order.LinkageCancelReportRoutingStruct;
import com.cboe.idl.order.LinkageFillReportRoutingStruct;
import com.cboe.idl.order.ManualCancelReplaceStruct;
import com.cboe.idl.order.ManualCancelRequestStructV2;
import com.cboe.idl.order.ManualFillTimeoutRoutingStruct;
import com.cboe.idl.order.ManualOrderTimeoutRoutingStruct;
import com.cboe.idl.order.OrderIdRoutingStruct;
import com.cboe.idl.order.OrderLocationServerResponseStruct;
import com.cboe.idl.order.OrderLocationSummaryServerResponseStruct;
import com.cboe.idl.order.OrderManualHandlingStructV2;
import com.cboe.idl.order.OrderRoutingStruct;
import com.cboe.idl.order.TradeNotificationRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.ohsEvents.OrderRoutingConsumer;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.domain.util.RoutingGroupCancelReportDropCopyContainer;
import com.cboe.domain.util.RoutingGroupFillReportDropCopyContainer;
import com.cboe.domain.util.RoutingGroupFillReportRejectContainer;
import com.cboe.domain.util.RoutingGroupLinkageCancelReportContainer;
import com.cboe.domain.util.RoutingGroupLinkageFillReportContainer;
import com.cboe.domain.util.RoutingGroupManualFillTimeoutContainer;
import com.cboe.domain.util.RoutingGroupManualOrderTimeoutContainer;
import com.cboe.domain.util.RoutingGroupOrderCancelContainer;
import com.cboe.domain.util.RoutingGroupOrderCancelReplaceContainer;
import com.cboe.domain.util.RoutingGroupOrderIdStructSequenceContainer;
import com.cboe.domain.util.RoutingGroupOrderStructSequenceContainer;
import com.cboe.domain.util.RoutingGroupTradeNotificationContainer;

/**
 * Implementation of order management consumer, whose methods are called when relevant
 * external events are received, and which in turn publishes events to the internal event channel
 */
public class OrderRoutingConsumerImpl implements OrderRoutingConsumer
{
    private EventChannelAdapter eventChannel = null;
    private final String category = getClass().getName();
    private static final GUILoggerBusinessProperty loggingProperty = GUILoggerBusinessProperty.OMT;

    /**
     * constructor
     * @param eventChannel the event channel to publish to
     */
    public OrderRoutingConsumerImpl(EventChannelAdapter eventChannel)
    {
        this.eventChannel = eventChannel;
    }
    public void acceptManualOrders(RoutingParameterV2Struct routingParameterV2Struct,
            OrderManualHandlingStructV2[] orders)
    {

    }
    public void acceptManualCancels(RoutingParameterV2Struct routingParameterV2Struct,
            ManualCancelRequestStructV2[] cancelRequests)
    {

    }
    public void acceptManualCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
            ManualCancelReplaceStruct[] cancelReplaces)

    {

    }

    public void acceptOrders(RoutingParameterV2Struct routingParameterV2Struct,
                             OrderRoutingStruct[] orders)
    {
        logMessage("acceptOrders()", routingParameterV2Struct, orders);

        RoutingGroupOrderStructSequenceContainer orderWrapper =
                new RoutingGroupOrderStructSequenceContainer(routingParameterV2Struct, orders);

        dispatchEvent(ChannelType.CB_OMT_ORDER_ACCEPTED, 0, orderWrapper);
        dispatchEvent(ChannelType.CB_OMT_ORDER_ACCEPTED_BY_SOURCE, routingParameterV2Struct.source,
                      orderWrapper);
        dispatchEvent(ChannelType.CB_OMT_ORDER_ACCEPTED_BY_SOURCE_TYPE,
                      routingParameterV2Struct.sourceType, orderWrapper);
    }

    public void acceptCancels(RoutingParameterV2Struct routingParameterV2Struct,
                              CancelRoutingStruct[] cancelRoutingStructs)
    {
        logMessage("acceptCancels()", routingParameterV2Struct, cancelRoutingStructs);

        RoutingGroupOrderCancelContainer cancelWrapper =
                new RoutingGroupOrderCancelContainer(routingParameterV2Struct,
                                                     cancelRoutingStructs);

        dispatchEvent(ChannelType.CB_OMT_ORDER_CANCELED, 0, cancelWrapper);
        dispatchEvent(ChannelType.CB_OMT_ORDER_CANCELED_BY_SOURCE, routingParameterV2Struct.source,
                      cancelWrapper);
        dispatchEvent(ChannelType.CB_OMT_ORDER_CANCELED_BY_SOURCE_TYPE,
                      routingParameterV2Struct.sourceType, cancelWrapper);
    }

    public void acceptCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
                                     CancelReplaceRoutingStruct[] cancelReplaceRoutingStructs)
    {
        logMessage("acceptCancelReplaces()", routingParameterV2Struct, cancelReplaceRoutingStructs);

        RoutingGroupOrderCancelReplaceContainer cancelReplaceWrapper =
                new RoutingGroupOrderCancelReplaceContainer(routingParameterV2Struct,
                                                            cancelReplaceRoutingStructs);

        dispatchEvent(ChannelType.CB_OMT_ORDER_CANCEL_REPLACED, 0, cancelReplaceWrapper);
        dispatchEvent(ChannelType.CB_OMT_ORDER_CANCEL_REPLACED_BY_SOURCE,
                      routingParameterV2Struct.source, cancelReplaceWrapper);
        dispatchEvent(ChannelType.CB_OMT_ORDER_CANCEL_REPLACED_BY_SOURCE_TYPE,
                      routingParameterV2Struct.sourceType, cancelReplaceWrapper);
    }

    public void acceptFillReportReject(RoutingParameterV2Struct routingParameterV2Struct,
                                       FillReportRejectRoutingStruct[] fillReportRejects)
    {
        logMessage("acceptFillReportReject()", routingParameterV2Struct, fillReportRejects);

        RoutingGroupFillReportRejectContainer fillRejectWrapper =
                new RoutingGroupFillReportRejectContainer(routingParameterV2Struct, fillReportRejects);

        dispatchEvent(ChannelType.CB_OMT_FILL_REPORT_REJECT, 0, fillRejectWrapper);
    }

    public void acceptRemoveOrder(RoutingParameterV2Struct routingParameterV2Struct,
                                  OrderIdRoutingStruct[] orderIdStructs)
    {
        logMessage("acceptRemoveOrder()", routingParameterV2Struct, orderIdStructs);

        RoutingGroupOrderIdStructSequenceContainer orderWrapper =
                new RoutingGroupOrderIdStructSequenceContainer(routingParameterV2Struct,
                                                               orderIdStructs);

        dispatchEvent(ChannelType.CB_OMT_ORDER_REMOVED, 0, orderWrapper);
        dispatchEvent(ChannelType.CB_OMT_ORDER_REMOVED_BY_SOURCE, routingParameterV2Struct.source,
                      orderWrapper);
        dispatchEvent(ChannelType.CB_OMT_ORDER_REMOVED_BY_SOURCE_TYPE,
                      routingParameterV2Struct.sourceType, orderWrapper);
    }

    public void acceptLinkageCancelReport(RoutingParameterV2Struct routingParameterV2Struct,
                                          LinkageCancelReportRoutingStruct[] cancelReports)
    {
        logMessage("acceptLinkageCancelReport()", routingParameterV2Struct, cancelReports);

        RoutingGroupLinkageCancelReportContainer container =
                new RoutingGroupLinkageCancelReportContainer(routingParameterV2Struct,
                                                             cancelReports);

        dispatchEvent(ChannelType.CB_OMT_LINKAGE_CANCEL_REPORT, 0, container);
    }

    public void acceptLinkageFillReport(RoutingParameterV2Struct routingParameterV2Struct,
                                        LinkageFillReportRoutingStruct[] fillReports)
    {
        logMessage("acceptLinkageFillReport()", routingParameterV2Struct, fillReports);

        RoutingGroupLinkageFillReportContainer container =
                new RoutingGroupLinkageFillReportContainer(routingParameterV2Struct, fillReports);

        dispatchEvent(ChannelType.CB_OMT_LINKAGE_FILL_REPORT, 0, container);
    }

    public void acceptOrderLocationServerResponse(
            OrderLocationServerResponseStruct orderLocationServerResponseStruct)
    {
        logMessage("acceptOrderLocationServerResponse()", orderLocationServerResponseStruct, null);
        dispatchEvent(ChannelType.CB_OMT_ORDERS_FOR_LOCATION, 0, orderLocationServerResponseStruct);
    }

    public void acceptOrderLocationSummaryServerResponse(
            OrderLocationSummaryServerResponseStruct orderLocationSummaryServerResponseStruct)
    {
        logMessage("acceptOrderLocationSummaryServerResponse()", orderLocationSummaryServerResponseStruct, null);
        dispatchEvent(ChannelType.CB_OMT_LOCATION_SUMMARY, 0,
                      orderLocationSummaryServerResponseStruct);
    }

    public void acceptRemoveMessage(RoutingParameterV2Struct routingParameterV2Struct, long msgId)
    {
        logMessage("acceptRemoveMessage()", routingParameterV2Struct, msgId);
        dispatchEvent(ChannelType.CB_OMT_REMOVE_MESSAGE, 0, msgId);
    }

    private void dispatchEvent(int channelType, Object channelKeyParameter,
                               Object channelEventParameter)
    {
        dispatchEvent(new ChannelKey(channelType, channelKeyParameter), channelEventParameter);
    }

    private void dispatchEvent(ChannelKey key, Object channelEventParameter)
    {
        ChannelEvent event =
                EventChannelAdapterFactory.find().getChannelEvent(this, key, channelEventParameter);
        eventChannel.dispatch(event);
    }


    public void acceptManualOrderTimeout(RoutingParameterV2Struct p_routingParameters, ManualOrderTimeoutRoutingStruct[] p_manualOrderTimeouts)
    {
        logMessage("acceptManualOrderTimeout()", p_routingParameters, p_manualOrderTimeouts);

        RoutingGroupManualOrderTimeoutContainer orderTimeoutWrapper =
                new RoutingGroupManualOrderTimeoutContainer(p_routingParameters,
                                                           p_manualOrderTimeouts);

        dispatchEvent(ChannelType.CB_OMT_MANUAL_ORDER_TIMEOUT, 0, orderTimeoutWrapper);
    }

    public void acceptManualFillTimeout(RoutingParameterV2Struct p_routingParameters, ManualFillTimeoutRoutingStruct[] p_fillReports)
    {
        logMessage("acceptManualFillTimeout()", p_routingParameters, p_fillReports);
        RoutingGroupManualFillTimeoutContainer fillTimeoutWrapper =
                new RoutingGroupManualFillTimeoutContainer(p_routingParameters, p_fillReports);

        dispatchEvent(ChannelType.CB_OMT_MANUAL_FILL_TIMEOUT, 0, fillTimeoutWrapper);
    }

    public void acceptTradeNotifications(RoutingParameterV2Struct p_orderRoutingStruct,
                                         TradeNotificationRoutingStruct[] p_tradeNotifications)
    {
        logMessage("acceptTradeNotifications()", p_orderRoutingStruct, p_tradeNotifications);
        RoutingGroupTradeNotificationContainer container =
                new RoutingGroupTradeNotificationContainer(p_orderRoutingStruct,
                                                           p_tradeNotifications);
        dispatchEvent(ChannelType.CB_OMT_TRADE_NOTIFICATION, 0, container);
    }

    public void acceptFillReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                         FillReportDropCopyRoutingStruct[] fillReportDropCopies)
    {
        logMessage("acceptFillReportDropCopy()", routingParameterV2Struct, fillReportDropCopies);
        RoutingGroupFillReportDropCopyContainer container =
                new RoutingGroupFillReportDropCopyContainer(routingParameterV2Struct, fillReportDropCopies);

        dispatchEvent(ChannelType.CB_OMT_FILL_REPORT_DROP_COPY, 0, container);
    }


    public void acceptCancelReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                           CancelReportDropCopyRoutingStruct[] cancelReportDropCopies)
    {
        logMessage("acceptCancelReportDropCopy()", routingParameterV2Struct, cancelReportDropCopies);
        RoutingGroupCancelReportDropCopyContainer container =
                new RoutingGroupCancelReportDropCopyContainer(routingParameterV2Struct, cancelReportDropCopies);

        dispatchEvent(ChannelType.CB_OMT_CANCEL_REPORT_DROP_COPY, 0, container);
    }

    /*
     *      Logs receipt of orders and error conditions to the audit and debug logs.
     */
    private void logMessage(String methodName, Object parm1, Object parm2)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(loggingProperty))
        {
            Object[] parm = new Object[2];
            parm[0] = parm1;
            parm[1] = parm2;
            GUILoggerHome.find().debug(category + "." + methodName, loggingProperty, parm);
        }
    }
}
