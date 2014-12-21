//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingConsumerProxy.java
//
// PACKAGE: com.cboe.application.supplier.proxy
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.supplier.proxy;

import com.cboe.idl.ohsConsumers.OrderRoutingConsumer;
import com.cboe.idl.order.OrderLocationServerResponseStruct;
import com.cboe.idl.order.OrderLocationSummaryServerResponseStruct;

import com.cboe.interfaces.domain.GMDProxyHome;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

import com.cboe.application.supplier.OrderRoutingSupplierFactory;
import com.cboe.domain.instrumentedChannel.supplier.proxy.InstrumentedGMDSupplierProxy;
import com.cboe.domain.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * OrderRoutingConsumerProxy serves as a proxy to the OrderManagementConsumer
 * object on the presentation side. The UserSessionAdminSupplier on the CAS uses this
 * proxy object to communicate to the GUI callback object.
 */

public class OrderRoutingConsumerProxy extends InstrumentedGMDSupplierProxy
{
    private OrderRoutingConsumer consumer;

    public OrderRoutingConsumerProxy(OrderRoutingConsumer consumer,
                                     BaseSessionManager sessionManager,
                                     boolean gmdProxy,
                                     GMDProxyHome home)
    {
        super(sessionManager, OrderRoutingSupplierFactory.find(sessionManager),
              gmdProxy, home, consumer);
        this.consumer = consumer;
    }

    /**
     * This method is called by ChannelThreadCommand object.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public  void channelUpdate(ChannelEvent event)
    {
        if (event != null)
        {

            //noinspection CatchGenericClass
            try
            {
                ChannelKey key = (ChannelKey)event.getChannel();
                StringBuilder calling = new StringBuilder(100);
                calling.append("calling channelUpdate for channelType: ").append(key.channelType);
                Log.information(this, calling.toString());
                calling.setLength(0);
                switch (key.channelType)
                {
                    
                        
                    case ChannelType.CB_OMT_ORDER_ACCEPTED:
                        RoutingGroupOrderStructSequenceContainer container1 =
                                (RoutingGroupOrderStructSequenceContainer) event.getEventData();
                        consumer.acceptOrders(container1.getRoutingParameterV2Struct(),
                                              container1.getOrderStructs());
                        calling.append("finished calling channelUpdate for acceptOrders: size=")
                               .append(container1.getOrderStructs().length);
                        Log.information(this, calling.toString());

                        break;
                    case ChannelType.CB_OMT_ORDER_CANCELED:
                        RoutingGroupOrderCancelContainer container2 =
                                (RoutingGroupOrderCancelContainer) event.getEventData();
                        consumer.acceptCancels(container2.getRoutingParameterV2Struct(),
                                               container2.getCancelRoutingStructs());
                        calling.append("finished calling channelUpdate for acceptCancels: size=")
                               .append(container2.getCancelRoutingStructs().length);
                        Log.information(this, calling.toString());

                        break;
                    case ChannelType.CB_OMT_ORDER_CANCEL_REPLACED:
                        RoutingGroupOrderCancelReplaceContainer container3 =
                                (RoutingGroupOrderCancelReplaceContainer) event.getEventData();
                        consumer.acceptCancelReplaces(container3.getRoutingParameterV2Struct(),
                                                      container3.getCancelReplaceRoutingStructs());
                        calling.append("finished calling channelUpdate for acceptCancelReplaces: size=")
                               .append(container3.getCancelReplaceRoutingStructs().length);
                        Log.information(this, calling.toString());

                        break;
                    case ChannelType.CB_OMT_FILL_REPORT_REJECT:
                        RoutingGroupFillReportRejectContainer container4 =
                                (RoutingGroupFillReportRejectContainer) event.getEventData();
                        consumer.acceptFillReportReject(container4.getRoutingParameterV2Struct(),
                                                        container4.getFillReportRejects());
                        calling.append("finished calling channelUpdate for acceptFillReportReject: size=")
                               .append(container4.getFillReportRejects().length);
                        Log.information(this, calling.toString());
                        break;
                    case ChannelType.CB_OMT_ORDER_REMOVED:
                        RoutingGroupOrderIdStructSequenceContainer container5 =
                                (RoutingGroupOrderIdStructSequenceContainer) event.getEventData();
                        consumer.acceptRemoveOrder(container5.getRoutingParameterV2Struct(),
                                                   container5.getOrderIdStructs());
                        calling.append("finished calling channelUpdate for acceptRemoveOrder: size=")
                               .append(container5.getOrderIdStructs().length);
                        Log.information(this, calling.toString());
                        break;
                    case ChannelType.CB_OMT_LINKAGE_CANCEL_REPORT:
                        RoutingGroupLinkageCancelReportContainer container6 =
                                (RoutingGroupLinkageCancelReportContainer) event.getEventData();
                        consumer.acceptLinkageCancelReport(container6.getRoutingParameterV2Struct(),
                                                           container6.getLinkageCancelReportStructs());
                        calling.append("finished calling channelUpdate for acceptLinkageCancelReport: size=")
                               .append(container6.getLinkageCancelReportStructs().length);
                        Log.information(this, calling.toString());
                        break;
                    case ChannelType.CB_OMT_LINKAGE_FILL_REPORT:
                        RoutingGroupLinkageFillReportContainer container7 =
                                (RoutingGroupLinkageFillReportContainer) event.getEventData();
                        consumer.acceptLinkageFillReport(container7.getRoutingParameterV2Struct(),
                                                         container7.getFillReports());
                        calling.append("finished calling channelUpdate for acceptLinkageFillReport: size=")
                               .append(container7.getFillReports().length);
                        Log.information(this, calling.toString());
                        break;
                    case ChannelType.CB_OMT_ORDERS_FOR_LOCATION:
                        OrderLocationServerResponseStruct response1 =
                            (OrderLocationServerResponseStruct) event.getEventData();
                        consumer.acceptOrderLocationServerResponse(response1);
                        calling.append("finished calling channelUpdate for acceptOrderLocationServerResponse: totalOrdersCount=")
                               .append(response1.totalOrdersCount);
                        Log.information(this, calling.toString());
                        break;
                    case ChannelType.CB_OMT_LOCATION_SUMMARY:
                        OrderLocationSummaryServerResponseStruct response2 =
                            (OrderLocationSummaryServerResponseStruct) event.getEventData();
                        consumer.acceptOrderLocationSummaryServerResponse(response2);
                        Log.information(this,"finished calling channelUpdate for acceptOrderLocationSummaryServerResponse");
                        
                    break;
                    case ChannelType.CB_OMT_REMOVE_MESSAGE:
                        RoutingGroupRemoveMessageContainer container8 =
                                (RoutingGroupRemoveMessageContainer) event.getEventData();
                        consumer.acceptRemoveMessage(container8.getRoutingParameterV2Struct(),
                                                     container8.getMsgId());
                        calling.append("finished calling channelUpdate for acceptRemoveMessage: source=")
                               .append(container8.getRoutingParameterV2Struct().source);
                        Log.information(this, calling.toString());
                    break;

                    case ChannelType.CB_OMT_TRADE_NOTIFICATION:
                        RoutingGroupTradeNotificationContainer container9 =
                                (RoutingGroupTradeNotificationContainer) event.getEventData();
                        consumer.acceptTradeNotifications(container9.getRoutingParameterV2Struct(),
                                                          container9.getTradeNotifications());
                        calling.append("finished calling channelUpdate for acceptTradeNotifications: size=")
                               .append(container9.getTradeNotifications().length);
                        Log.information(this, calling.toString());
                    break;

                    case ChannelType.CB_OMT_FILL_REPORT_DROP_COPY:
                        RoutingGroupFillReportDropCopyContainer container10 =
                            (RoutingGroupFillReportDropCopyContainer) event.getEventData();
                        consumer.acceptFillReportDropCopy(container10.getRoutingParameterV2Struct(),
                    								  container10.getFillReportDropCopies());
                        calling.append("finished calling channelUpdate for acceptFillReportDropCopy: size=")
                               .append(container10.getFillReportDropCopies().length);
                        Log.information(this, calling.toString());
                    break;

                    case ChannelType.CB_OMT_CANCEL_REPORT_DROP_COPY:
                        RoutingGroupCancelReportDropCopyContainer container11 =
                            (RoutingGroupCancelReportDropCopyContainer) event.getEventData();
                        consumer.acceptCancelReportDropCopy(container11.getRoutingParameterV2Struct(),
                    								    container11.getCancelRoprtDropCopies());
                        calling.append("finished calling channelUpdate for acceptCancelReportDropCopy: size=")
                               .append(container11.getCancelRoprtDropCopies().length);
                        Log.information(this, calling.toString());
                    break;
                    
                    case ChannelType.CB_OMT_MANUAL_ORDER_TIMEOUT:
                        RoutingGroupManualOrderTimeoutContainer container12 =
                                (RoutingGroupManualOrderTimeoutContainer) event.getEventData();
                        consumer.acceptManualOrderTimeout(container12.getRoutingParameterV2Struct(),
                                              container12.getManualOrderTimeouts());
                        calling.append("finished calling channelUpdate for acceptManualOrderTimeout: size=")
                               .append(container12.getManualOrderTimeouts().length);
                        Log.information(this, calling.toString());
                        break;
                    case ChannelType.CB_OMT_MANUAL_FILL_TIMEOUT:
                        RoutingGroupManualFillTimeoutContainer container13 =
                                (RoutingGroupManualFillTimeoutContainer) event.getEventData();
                        consumer.acceptManualFillTimeout(container13.getRoutingParameterV2Struct(),
                                              container13.getManualFillTimeouts());
                        calling.append("finished calling channelUpdate for acceptManualFillTimeout: size=")
                               .append(container13.getManualFillTimeouts().length);
                        Log.information(this, calling.toString());
                        break;
                        
                    case ChannelType.CB_PAR_ORDER_ACCEPTED:
                        RoutingGroupParOrderStructSequenceContainer container14 =
                                (RoutingGroupParOrderStructSequenceContainer) event.getEventData();
                        consumer.acceptManualOrders(container14.getRoutingParameterV2Struct(),
                                              container14.getOrders());
                        calling.append("finished calling channelUpdate for acceptManualOrders");
                        Log.information(this, calling.toString());
                        break;
                    case ChannelType.CB_PAR_ORDER_CANCELED:
                        RoutingGroupParCancelRequestStructSequenceContainer container15 =
                                (RoutingGroupParCancelRequestStructSequenceContainer) event.getEventData();
                        consumer.acceptManualCancels(container15.getRoutingParameterV2Struct(),
                                              container15.getCancelRequests());
                        calling.append("finished calling channelUpdate for acceptManualCancels");
                        Log.information(this, calling.toString());
                        break;
                        
                    case ChannelType.CB_PAR_ORDER_CANCEL_REPLACED:
                        RoutingGroupParCancelReplaceStructSequenceContainer container16 =
                                (RoutingGroupParCancelReplaceStructSequenceContainer) event.getEventData();
                        consumer.acceptManualCancelReplaces(container16.getRoutingParameterV2Struct(),
                                              container16.getCancelReplaces());
                        calling.append("finished calling channelUpdate for acceptManualCancelRepalces");
                        Log.information(this, calling.toString());
                        break;

                    default:
                        Log.alarm(this,"Wrong channelType: " + key.channelType );
                        break;
                }
            }
            catch(Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                lostConnection(event);
            }
        }
        else
        {
            Log.information(this, "Null event");
        }
    }

    public String getMethodName(ChannelEvent event)
    {
        String method;

        ChannelKey key = (ChannelKey) event.getChannel();

        switch(key.channelType)
        {
            case ChannelType.CB_OMT_ORDER_ACCEPTED:
                method = "acceptOrders";
                break;
            case ChannelType.CB_OMT_ORDER_CANCELED:
                method = "acceptCancels";
                break;
            case ChannelType.CB_OMT_ORDER_CANCEL_REPLACED:
                method = "acceptCancelReplaces";
                break;
            case ChannelType.CB_OMT_FILL_REPORT_REJECT:
                method = "acceptFillReportReject";
                break;
            case ChannelType.CB_OMT_ORDER_REMOVED:
                method = "acceptRemoveOrder";
                break;
            case ChannelType.CB_OMT_LINKAGE_CANCEL_REPORT:
                method = "acceptLinkageCancelReport";
                break;
            case ChannelType.CB_OMT_LINKAGE_FILL_REPORT:
                method = "acceptLinkageFillReport";
                break;
            case ChannelType.CB_OMT_ORDERS_FOR_LOCATION:
                method = "acceptOrderLocationServerResponse";
                break;
            case ChannelType.CB_OMT_LOCATION_SUMMARY:
                method = "acceptOrderLocationSummaryServerResponse";
                break;
            case ChannelType.CB_OMT_REMOVE_MESSAGE:
                method = "acceptRemoveMessage";
                break;
            case ChannelType.CB_OMT_TRADE_NOTIFICATION:
                method = "acceptTradeNotifications";
                break;
            case ChannelType.CB_OMT_FILL_REPORT_DROP_COPY:
                method = "acceptFillReportDropCopy";
                break;
            case ChannelType.CB_OMT_CANCEL_REPORT_DROP_COPY:
                method = "acceptCancelReportDropCopy";
                break;
            case ChannelType.CB_OMT_MANUAL_ORDER_TIMEOUT:
                method = "acceptManualOrderTimeout";
                break;
            case ChannelType.CB_OMT_MANUAL_FILL_TIMEOUT:
                method = "acceptManualFillTimeout";
                break;
            case ChannelType.CB_PAR_ORDER_ACCEPTED:
                method = "acceptManualOrders";
                break;
            case ChannelType.CB_PAR_ORDER_CANCELED:
                method = "acceptManualCancels";
                break;
            case ChannelType.CB_PAR_ORDER_CANCEL_REPLACED:
                method = "acceptManualCancelReplaces";
                break;    
            
            default:
                method = "";
                break;
        }
        return method;
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.OMT;
    }
}
