//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingConsumerInterceptor.java
//
// PACKAGE: com.cboe.application.supplier.proxy
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.supplier.proxy;

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

import com.cboe.domain.instrumentorExtension.MethodInstrumentorExtension;
import com.cboe.domain.instrumentorExtension.MethodInstrumentorExtensionFactory;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtension;
import com.cboe.domain.supplier.proxy.CallbackInterceptor;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.Instrumentor;

@SuppressWarnings({"CatchGenericClass", "ProhibitedExceptionThrown"})
public class OrderRoutingConsumerInterceptor
        extends CallbackInterceptor
        implements OrderRoutingConsumer
{
    private MethodInstrumentorExtension acceptOrders;
    private MethodInstrumentorExtension acceptCancels;
    private MethodInstrumentorExtension acceptCancelReplaces;
    private MethodInstrumentorExtension acceptFillReportReject;
    private MethodInstrumentorExtension acceptRemoveOrder;
    private MethodInstrumentorExtension acceptLinkageCancelReport;
    private MethodInstrumentorExtension acceptLinkageFillReport;
    private MethodInstrumentorExtension acceptOrderLocationServerResponse;
    private MethodInstrumentorExtension acceptOrderLocationSummaryServerResponse;
    private MethodInstrumentorExtension acceptRemoveMessage;
    private MethodInstrumentorExtension acceptTradeNotifications;
    private MethodInstrumentorExtension acceptFillReportDropCopy;
    private MethodInstrumentorExtension acceptCancelReportDropCopy;
    private MethodInstrumentorExtension acceptManualOrderTimeout;
    private MethodInstrumentorExtension acceptManualFillTimeout;

    private MethodInstrumentorExtension acceptManualOrders;
    private MethodInstrumentorExtension acceptManualCancels;
    private MethodInstrumentorExtension acceptManualCancelReplaces;

    private OrderRoutingConsumer cmiObject;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     * @param consumer to route callback to
     */
    public OrderRoutingConsumerInterceptor(OrderRoutingConsumer consumer)
    {
        cmiObject = consumer;
    }

    public void startInstrumentation(String prefix, boolean privateOnly)
    {
        try
        {
            StringBuilder name = new StringBuilder(prefix.length()+Instrumentor.NAME_DELIMITER.length()+40);
            name.append(prefix).append(Instrumentor.NAME_DELIMITER);
            final int headLength = name.length();

            name.append("acceptOrders");
            acceptOrders = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            name.setLength(headLength);
            name.append("acceptCancels");
            acceptCancels = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            name.setLength(headLength);
            name.append("acceptCancelReplaces");
            acceptCancelReplaces = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            name.setLength(headLength);
            name.append("acceptFillReportReject");
            acceptFillReportReject = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            name.setLength(headLength);
            name.append("acceptRemoveOrder");
            acceptRemoveOrder = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            name.setLength(headLength);
            name.append("acceptLinkageCancelReport");
            acceptLinkageCancelReport = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            name.setLength(headLength);
            name.append("acceptLinkageFillReport");
            acceptLinkageFillReport = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString() , null, privateOnly);

            name.setLength(headLength);
            name.append("acceptOrderLocationServerResponse");
            acceptOrderLocationServerResponse = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);

            name.setLength(headLength);
            name.append("acceptOrderLocationSummaryServerResponse");
            acceptOrderLocationSummaryServerResponse = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            name.setLength(headLength);
            name.append("acceptTradeNotifications");
            acceptTradeNotifications = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);

            name.setLength(headLength);
            name.append("acceptRemoveMessage");
            acceptRemoveMessage = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);

            name.setLength(headLength);
            name.append("acceptFillReportDropCopy");
            acceptFillReportDropCopy = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);

            name.setLength(headLength);
            name.append("acceptCancelReportDropCopy");
            acceptCancelReportDropCopy = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);


            name.setLength(headLength);
            name.append("acceptManualOrderTimeout");
            acceptManualOrderTimeout = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            name.setLength(headLength);
            name.append("acceptManualFillTimeout");
            acceptManualFillTimeout = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);


            name.setLength(headLength);
            name.append("acceptManualOrders");
            acceptManualOrders = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            name.setLength(headLength);
            name.append("acceptManualCancels");
            acceptManualCancels = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            name.setLength(headLength);
            name.append("acceptManualCancelReplaces");
            acceptManualCancelReplaces = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);

        }
        catch (InstrumentorAlreadyCreatedException e)
        {
            Log.exception(e);
        }
    }

    public void removeInstrumentation()
    {
        if(acceptOrders != null)
        {
            MethodInstrumentorExtensionFactory.removeMethodInstrumentor(acceptOrders.getName());
            acceptOrders = null;
        }
        if(acceptCancels != null)
        {
            MethodInstrumentorExtensionFactory.removeMethodInstrumentor(acceptCancels.getName());
            acceptCancels = null;
        }
        if(acceptCancelReplaces != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptCancelReplaces.getName());
            acceptCancelReplaces = null;
        }
        if(acceptFillReportReject != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptFillReportReject.getName());
            acceptFillReportReject = null;
        }
        if(acceptRemoveOrder != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptRemoveOrder.getName());
            acceptRemoveOrder = null;
        }
        if(acceptLinkageCancelReport != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptLinkageCancelReport.getName());
            acceptLinkageCancelReport = null;
        }
        if(acceptLinkageFillReport != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptLinkageFillReport.getName());
            acceptLinkageFillReport = null;
        }
        if(acceptOrderLocationServerResponse != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptOrderLocationServerResponse.getName());
            acceptOrderLocationServerResponse = null;
        }
        if(acceptOrderLocationSummaryServerResponse != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptOrderLocationSummaryServerResponse.getName());
            acceptOrderLocationSummaryServerResponse = null;
        }
        if(acceptRemoveMessage != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptRemoveMessage.getName());
            acceptRemoveMessage = null;
        }
        if(acceptTradeNotifications != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptTradeNotifications.getName());
            acceptTradeNotifications = null;
        }

        if(acceptFillReportDropCopy != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptFillReportDropCopy.getName());
            acceptFillReportDropCopy = null;
        }

        if(acceptCancelReportDropCopy != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptCancelReportDropCopy.getName());
            acceptCancelReportDropCopy = null;
        }
        if(acceptManualOrderTimeout != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptManualOrderTimeout.getName());
            acceptManualOrderTimeout = null;
        }

        if(acceptManualFillTimeout != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptManualFillTimeout.getName());
            acceptManualFillTimeout = null;
        }
        if(acceptManualOrders != null)
        {
            MethodInstrumentorExtensionFactory.removeMethodInstrumentor(acceptManualOrders.getName());
            acceptManualOrders = null;
        }
        if(acceptManualCancels != null)
        {
            MethodInstrumentorExtensionFactory.removeMethodInstrumentor(acceptManualCancels.getName());
            acceptManualCancels = null;
        }
        if(acceptManualCancelReplaces != null)
        {
            MethodInstrumentorExtensionFactory
                    .removeMethodInstrumentor(acceptManualCancelReplaces.getName());
            acceptManualCancelReplaces = null;
        }
    }

    public void addQueueInstrumentorRelation(QueueInstrumentorExtension queueInstrumentorExtension)
    {
        if(acceptOrders != null)
        {
            acceptOrders.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptCancels != null)
        {
            acceptCancels.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptCancelReplaces != null)
        {
            acceptCancelReplaces.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptFillReportReject != null)
        {
            acceptFillReportReject.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptRemoveOrder != null)
        {
            acceptRemoveOrder.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptLinkageCancelReport != null)
        {
            acceptLinkageCancelReport.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptLinkageFillReport != null)
        {
            acceptLinkageFillReport.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptOrderLocationServerResponse != null)
        {
            acceptOrderLocationServerResponse.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptOrderLocationSummaryServerResponse != null)
        {
            acceptOrderLocationSummaryServerResponse.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptRemoveMessage != null)
        {
            acceptRemoveMessage.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptTradeNotifications != null)
        {
            acceptTradeNotifications.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptFillReportDropCopy != null)
        {
            acceptFillReportDropCopy.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }

        if(acceptCancelReportDropCopy != null)
        {
            acceptCancelReportDropCopy.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }


        if(acceptManualOrderTimeout != null)
        {
            acceptManualOrderTimeout.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }

        if(acceptManualFillTimeout != null)
        {
            acceptManualFillTimeout.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptManualOrders != null)
        {
            acceptManualOrders.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptManualCancels != null)
        {
            acceptManualCancels.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        if(acceptManualCancelReplaces != null)
        {
            acceptManualCancelReplaces.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }

    }

    public void acceptOrders(RoutingParameterV2Struct routingParameterV2Struct,
                             OrderRoutingStruct[] orderStructs)
    {
        boolean exception = false;
        if(acceptOrders != null)
        {
            acceptOrders.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptOrders(routingParameterV2Struct, orderStructs);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptOrders != null)
            {
                acceptOrders.incCalls(1);
                acceptOrders.afterMethodCall();
                if(exception)
                {
                    acceptOrders.incExceptions(1);
                }
            }
        }
    }

    public void acceptCancels(RoutingParameterV2Struct routingParameterV2Struct,
                              CancelRoutingStruct[] cancelRoutingStructs)
    {
        boolean exception = false;
        if(acceptCancels != null)
        {
            acceptCancels.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptCancels(routingParameterV2Struct, cancelRoutingStructs);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptCancels != null)
            {
                acceptCancels.incCalls(1);
                acceptCancels.afterMethodCall();
                if(exception)
                {
                    acceptCancels.incExceptions(1);
                }
            }
        }
    }

    public void acceptCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
                                     CancelReplaceRoutingStruct[] cancelReplaceRoutingStructs)
    {
        boolean exception = false;
        if(acceptCancelReplaces != null)
        {
            acceptCancelReplaces.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptCancelReplaces(routingParameterV2Struct, cancelReplaceRoutingStructs);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptCancelReplaces != null)
            {
                acceptCancelReplaces.incCalls(1);
                acceptCancelReplaces.afterMethodCall();
                if(exception)
                {
                    acceptCancelReplaces.incExceptions(1);
                }
            }
        }
    }

    public void acceptFillReportReject(RoutingParameterV2Struct routingParameterV2Struct,
                                       FillReportRejectRoutingStruct[]  fillReportRejects )
    {
        boolean exception = false;
        if(acceptFillReportReject != null)
        {
            acceptFillReportReject.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptFillReportReject(routingParameterV2Struct, fillReportRejects);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptFillReportReject != null)
            {
                acceptFillReportReject.incCalls(1);
                acceptFillReportReject.afterMethodCall();
                if(exception)
                {
                    acceptFillReportReject.incExceptions(1);
                }
            }
        }
    }

    public void acceptRemoveOrder(RoutingParameterV2Struct routingParameterV2Struct,
                                  OrderIdRoutingStruct[] orderIdStructs)
    {
        boolean exception = false;
        if(acceptRemoveOrder != null)
        {
            acceptRemoveOrder.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptRemoveOrder(routingParameterV2Struct, orderIdStructs);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptRemoveOrder != null)
            {
                acceptRemoveOrder.incCalls(1);
                acceptRemoveOrder.afterMethodCall();
                if(exception)
                {
                    acceptRemoveOrder.incExceptions(1);
                }
            }
        }
    }

    public void acceptLinkageCancelReport(RoutingParameterV2Struct routingParameterV2Struct,
                                          LinkageCancelReportRoutingStruct[] cancelReports)
    {
        boolean exception = false;
        if(acceptLinkageCancelReport != null)
        {
            acceptLinkageCancelReport.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptLinkageCancelReport(routingParameterV2Struct, cancelReports);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptLinkageCancelReport != null)
            {
                acceptLinkageCancelReport.incCalls(1);
                acceptLinkageCancelReport.afterMethodCall();
                if(exception)
                {
                    acceptLinkageCancelReport.incExceptions(1);
                }
            }
        }
    }

    public void acceptLinkageFillReport(RoutingParameterV2Struct routingParameterV2Struct,
                                        LinkageFillReportRoutingStruct[] fillReports)
    {
        boolean exception = false;
        if(acceptLinkageFillReport != null)
        {
            acceptLinkageFillReport.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptLinkageFillReport(routingParameterV2Struct, fillReports);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptLinkageFillReport != null)
            {
                acceptLinkageFillReport.incCalls(1);
                acceptLinkageFillReport.afterMethodCall();
                if(exception)
                {
                    acceptLinkageFillReport.incExceptions(1);
                }
            }
        }
    }

    public void acceptOrderLocationServerResponse(OrderLocationServerResponseStruct orderLocationSeverResponse)
    {
        boolean exception = false;
        if(acceptOrderLocationServerResponse != null)
        {
            acceptOrderLocationServerResponse.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptOrderLocationServerResponse(orderLocationSeverResponse);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptOrderLocationServerResponse != null)
            {
                acceptOrderLocationServerResponse.incCalls(1);
                acceptOrderLocationServerResponse.afterMethodCall();
                if(exception)
                {
                    acceptOrderLocationServerResponse.incExceptions(1);
                }
            }
        }
    }

    public void acceptOrderLocationSummaryServerResponse(OrderLocationSummaryServerResponseStruct orderLocationSummarySeverResponse)
    {
        boolean exception = false;
        if(acceptOrderLocationSummaryServerResponse != null)
        {
            acceptOrderLocationSummaryServerResponse.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptOrderLocationSummaryServerResponse(orderLocationSummarySeverResponse);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptOrderLocationSummaryServerResponse != null)
            {
                acceptOrderLocationSummaryServerResponse.incCalls(1);
                acceptOrderLocationSummaryServerResponse.afterMethodCall();
                if(exception)
                {
                    acceptOrderLocationSummaryServerResponse.incExceptions(1);
                }
            }
        }
    }

    public void acceptRemoveMessage(RoutingParameterV2Struct routingParameterV2Struct,
                                  long msgId)
    {
        boolean exception = false;
        if(acceptRemoveMessage != null)
        {
            acceptRemoveMessage.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptRemoveMessage(routingParameterV2Struct, msgId);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptRemoveMessage != null)
            {
                acceptRemoveMessage.incCalls(1);
                acceptRemoveMessage.afterMethodCall();
                if(exception)
                {
                    acceptRemoveMessage.incExceptions(1);
                }
            }
        }
    }

     public void acceptTradeNotifications(RoutingParameterV2Struct routingParameterV2Struct,
                                          TradeNotificationRoutingStruct[] tradeNotifications)
    {
       boolean exception = false;
        if(acceptTradeNotifications != null)
        {
            acceptTradeNotifications.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptTradeNotifications(routingParameterV2Struct, tradeNotifications);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptTradeNotifications != null)
            {
                acceptTradeNotifications.incCalls(1);
                acceptTradeNotifications.afterMethodCall();
                if(exception)
                {
                    acceptTradeNotifications.incExceptions(1);
                }
            }
        }
    }

    public void acceptFillReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                         FillReportDropCopyRoutingStruct[] fillReportDropCopies)
    {
       boolean exception = false;
        if(acceptFillReportDropCopy != null)
        {
            acceptFillReportDropCopy.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptFillReportDropCopy(routingParameterV2Struct,
                                               fillReportDropCopies);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptFillReportDropCopy != null)
            {
                acceptFillReportDropCopy.incCalls(1);
                acceptFillReportDropCopy.afterMethodCall();
                if(exception)
                {
                    acceptFillReportDropCopy.incExceptions(1);
                }
            }
        }
    }


    public void acceptCancelReportDropCopy(RoutingParameterV2Struct routingParameterV2Struct,
                                         CancelReportDropCopyRoutingStruct[]  cancelRoprtDropCopies)
    {
       boolean exception = false;
        if(acceptCancelReportDropCopy != null)
        {
            acceptCancelReportDropCopy.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptCancelReportDropCopy(routingParameterV2Struct,
                                               cancelRoprtDropCopies);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptCancelReportDropCopy != null)
            {
                acceptCancelReportDropCopy.incCalls(1);
                acceptCancelReportDropCopy.afterMethodCall();
                if(exception)
                {
                    acceptCancelReportDropCopy.incExceptions(1);
                }
            }
        }
    }

    public void acceptManualOrderTimeout(RoutingParameterV2Struct routingParameters, ManualOrderTimeoutRoutingStruct[] manualOrderTimeouts)
    {
         boolean exception = false;
        if(acceptManualOrderTimeout != null)
        {
            acceptManualOrderTimeout.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptManualOrderTimeout(routingParameters, manualOrderTimeouts);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptManualOrderTimeout != null)
            {
                acceptManualOrderTimeout.incCalls(1);
                acceptManualOrderTimeout.afterMethodCall();
                if(exception)
                {
                    acceptManualOrderTimeout.incExceptions(1);
                }
            }
        }
    }

	public void acceptManualFillTimeout(RoutingParameterV2Struct routingParameters, ManualFillTimeoutRoutingStruct[] fillReports)
    {
        boolean exception = false;
        if(acceptManualFillTimeout != null)
        {
            acceptManualFillTimeout.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptManualFillTimeout(routingParameters, fillReports);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptManualFillTimeout != null)
            {
                acceptManualFillTimeout.incCalls(1);
                acceptManualFillTimeout.afterMethodCall();
                if(exception)
                {
                    acceptManualFillTimeout.incExceptions(1);
                }
            }
        }
    }

	public void acceptManualOrders(RoutingParameterV2Struct routingParameterV2Struct,
	        OrderManualHandlingStructV2[] orders)
    {
        boolean exception = false;
        if(acceptManualOrders != null)
        {
            acceptManualOrders.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptManualOrders(routingParameterV2Struct, orders);
        }
        catch(RuntimeException ex)
        {
                exception = true;
                throw ex;
        }
        finally
        {
            if(acceptManualOrders != null)
            {
                acceptManualOrders.incCalls(1);
                acceptManualOrders.afterMethodCall();
                if(exception)
                {
                   acceptManualOrders.incExceptions(1);
                }
            }
        }
    }

	public void acceptManualCancels(RoutingParameterV2Struct routingParameterV2Struct,
	        ManualCancelRequestStructV2[] cancelRequests)
    {
        boolean exception = false;
        if(acceptManualCancels != null)
        {
            acceptManualCancels.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptManualCancels(routingParameterV2Struct, cancelRequests);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptCancels != null)
                {
                    acceptManualCancels.incCalls(1);
                    acceptManualCancels.afterMethodCall();
                if(exception)
                {
                    acceptManualCancels.incExceptions(1);
                }
            }
        }
    }

	public void acceptManualCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
            ManualCancelReplaceStruct[] cancelReplaces)

    {
        boolean exception = false;
        if(acceptManualCancelReplaces != null)
        {
            acceptManualCancelReplaces.beforeMethodCall();
        }

        try
        {
            cmiObject.acceptManualCancelReplaces(routingParameterV2Struct, cancelReplaces);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptManualCancelReplaces != null)
            {
                acceptManualCancelReplaces.incCalls(1);
                acceptManualCancelReplaces.afterMethodCall();
                if(exception)
                {
                    acceptManualCancelReplaces.incExceptions(1);
                }
            }
        }
    }

}
