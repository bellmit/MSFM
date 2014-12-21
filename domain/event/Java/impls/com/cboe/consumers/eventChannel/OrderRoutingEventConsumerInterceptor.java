package com.cboe.consumers.eventChannel;

import com.cboe.idl.cmiTradeNotification.TradeNotificationStruct;
import com.cboe.idl.order.*;
import com.cboe.idl.util.RouteReasonStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.ohsEvents.OrderRoutingConsumer;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

@SuppressWarnings({"PackageVisibleField", "CatchGenericClass", "ProhibitedExceptionThrown"})
public class OrderRoutingEventConsumerInterceptor
        implements OrderRoutingConsumer
{
    MethodInstrumentor acceptOrders;
    MethodInstrumentor acceptCancels;
    MethodInstrumentor acceptCancelReplaces;
    MethodInstrumentor acceptFillReportReject;
    MethodInstrumentor acceptRemoveOrder;
    MethodInstrumentor acceptLinkageCancelReport;
    MethodInstrumentor acceptLinkageFillReport;
    MethodInstrumentor acceptOrderLocationServerResponse;
    MethodInstrumentor acceptOrderLocationSummaryServerResponse;
    MethodInstrumentor acceptRemoveMessage;
    MethodInstrumentor acceptManualFillTimeout;
    MethodInstrumentor acceptManualOrderTimeout;
    MethodInstrumentor acceptTradeNotifications;
    MethodInstrumentor acceptFillReportDropCopy;
    MethodInstrumentor acceptCancelReportDropCopy;
    MethodInstrumentor acceptManualOrders;
	MethodInstrumentor acceptManualCancels;
	MethodInstrumentor acceptManualCancelReplaces;

    private OrderRoutingConsumer delegate;

    private MethodInstrumentorFactory methodInstrumentorFactory;


    public OrderRoutingEventConsumerInterceptor(Object bo)
    {
        setDelegate(bo);
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (OrderRoutingConsumer)delegate;
    }

    private MethodInstrumentorFactory getMethodInstrumentorFactory()
    {
        if (methodInstrumentorFactory == null)
        {
            methodInstrumentorFactory =
                    FoundationFramework.getInstance().getInstrumentationService()
                            .getMethodInstrumentorFactory();
        }
        return methodInstrumentorFactory;
    }

    public void startInstrumentation(boolean privateOnly)
    {

    	try
        {
            StringBuilder name = new StringBuilder(80);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptOrders");
            acceptOrders = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptOrders);
            acceptOrders.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptCancels");
            acceptCancels = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptCancels);
            acceptCancels.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptCancelReplaces");
            acceptCancelReplaces = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptCancelReplaces);
            acceptCancelReplaces.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptFillReportReject");
            acceptFillReportReject = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptFillReportReject);
            acceptFillReportReject.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptRemoveOrder");
            acceptRemoveOrder = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptRemoveOrder);
            acceptRemoveOrder.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptLinkageCancelReport");
            acceptLinkageCancelReport = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptLinkageCancelReport);
            acceptLinkageCancelReport.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptLinkageFillReport");
            acceptLinkageFillReport = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptLinkageFillReport);
            acceptLinkageFillReport.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptOrderLocationServerResponse");
            acceptOrderLocationServerResponse = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptOrderLocationServerResponse);
            acceptOrderLocationServerResponse.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptOrderLocationSummaryServerResponse");
            acceptOrderLocationSummaryServerResponse = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptOrderLocationSummaryServerResponse);
            acceptOrderLocationSummaryServerResponse.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptRemoveMessage");
            acceptRemoveMessage = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptRemoveMessage);
            acceptRemoveMessage.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptTradeNotifications");
            acceptTradeNotifications = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptTradeNotifications);
            acceptTradeNotifications.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptFillReportDropCopy");
            acceptFillReportDropCopy = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptFillReportDropCopy);
            acceptFillReportDropCopy.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptCancelReportDropCopy");
            acceptCancelReportDropCopy = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptCancelReportDropCopy);
            acceptCancelReportDropCopy.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptManualOrderTimeout");
            acceptManualOrderTimeout = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptManualOrderTimeout);
            acceptManualOrderTimeout.setPrivate(privateOnly);

            name.setLength(0);
            name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
                .append("acceptManualFillTimeout");
            acceptManualFillTimeout = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptManualFillTimeout);
            acceptManualFillTimeout.setPrivate(privateOnly);

            name.setLength(0);
			name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
				.append("acceptManualOrders");
			acceptManualOrders = getMethodInstrumentorFactory().create(name.toString(), null);
			getMethodInstrumentorFactory().register(acceptManualOrders);
            acceptManualOrders.setPrivate(privateOnly);

            name.setLength(0);
			name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
				.append("acceptManualCancels");
			acceptManualCancels = getMethodInstrumentorFactory().create(name.toString(), null);
			getMethodInstrumentorFactory().register(acceptManualCancels);
            acceptManualCancels.setPrivate(privateOnly);

            name.setLength(0);
			name.append("OrderRoutingEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER)
				.append("acceptManualCancelReplaces");
			acceptManualCancelReplaces = getMethodInstrumentorFactory().create(name.toString(), null);
			getMethodInstrumentorFactory().register(acceptManualCancelReplaces);
            acceptManualCancelReplaces.setPrivate(privateOnly);

        }
        catch (InstrumentorAlreadyCreatedException ex)
        {
            Log.exception(ex);
        }
    }

    public void removeInstrumentation()
    {
        getMethodInstrumentorFactory().unregister(acceptOrders);
        acceptOrders= null;

        getMethodInstrumentorFactory().unregister(acceptCancels);
        acceptCancels= null;

        getMethodInstrumentorFactory().unregister(acceptCancelReplaces);
        acceptCancelReplaces= null;

        getMethodInstrumentorFactory().unregister(acceptFillReportReject);
        acceptFillReportReject= null;

        getMethodInstrumentorFactory().unregister(acceptRemoveOrder);
        acceptRemoveOrder = null;

        getMethodInstrumentorFactory().unregister(acceptLinkageCancelReport);
        acceptLinkageCancelReport = null;

        getMethodInstrumentorFactory().unregister(acceptLinkageFillReport);
        acceptLinkageFillReport = null;

        getMethodInstrumentorFactory().unregister(acceptOrderLocationServerResponse);
        acceptOrderLocationServerResponse = null;

        getMethodInstrumentorFactory().unregister(acceptOrderLocationSummaryServerResponse);
        acceptOrderLocationSummaryServerResponse = null;


        getMethodInstrumentorFactory().unregister(acceptRemoveMessage);
        acceptRemoveMessage = null;

        getMethodInstrumentorFactory().unregister(acceptTradeNotifications);
        acceptTradeNotifications = null;
        getMethodInstrumentorFactory().unregister(acceptManualOrderTimeout);
        acceptManualOrderTimeout = null;

        getMethodInstrumentorFactory().unregister(acceptManualFillTimeout);
        acceptManualFillTimeout = null;
    }

    public void acceptOrders(RoutingParameterV2Struct routingParameterV2Struct,
                             OrderRoutingStruct[] orders)
    {


    	boolean exception = false;
        if (acceptOrders!= null)
        {

            acceptOrders.beforeMethodCall();
        }

        try
        {
            delegate.acceptOrders(routingParameterV2Struct, orders);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptOrders != null)
            {

                acceptOrders.incCalls(1);
                acceptOrders.afterMethodCall();
                if (exception)
                {
                    acceptOrders.incExceptions(1);
                }
            }
        }
    }

    public void acceptCancels(RoutingParameterV2Struct routingParameterV2Struct,
                              CancelRoutingStruct[] cancels)
    {
        boolean exception = false;
        if (acceptCancels!= null)
        {
            acceptCancels.beforeMethodCall();
        }

        try
        {
            delegate.acceptCancels(routingParameterV2Struct, cancels);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptCancels != null)
            {
                acceptCancels.incCalls(1);
                acceptCancels.afterMethodCall();
                if (exception)
                {
                    acceptCancels.incExceptions(1);
                }
            }
        }
    }

    public void acceptCancelReplaces(RoutingParameterV2Struct routingParameterV2Struct,
                                     CancelReplaceRoutingStruct[] cancelReplaces)
    {
        boolean exception = false;
        if (acceptCancelReplaces!= null)
        {
            acceptCancelReplaces.beforeMethodCall();
        }

        try
        {
            delegate.acceptCancelReplaces(routingParameterV2Struct, cancelReplaces);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptCancelReplaces != null)
            {
                acceptCancelReplaces.incCalls(1);
                acceptCancelReplaces.afterMethodCall();
                if (exception)
                {
                    acceptCancelReplaces.incExceptions(1);
                }
            }
        }
    }

    public void acceptFillReportReject(RoutingParameterV2Struct routingParameterV2Struct,
                                       FillReportRejectRoutingStruct[]  fillReportRejects)
    {
        boolean exception = false;
        if (acceptFillReportReject!= null)
        {
            acceptFillReportReject.beforeMethodCall();
        }

        try
        {
            delegate.acceptFillReportReject(routingParameterV2Struct, fillReportRejects);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptFillReportReject != null)
            {
                acceptFillReportReject.incCalls(1);
                acceptFillReportReject.afterMethodCall();
                if (exception)
                {
                    acceptFillReportReject.incExceptions(1);
                }
            }
        }
    }

    public void acceptRemoveOrder(RoutingParameterV2Struct routingParameterV2Struct,
                                  OrderIdRoutingStruct[] orderIds)
    {
        boolean exception = false;
        if(acceptRemoveOrder != null)
        {
            acceptRemoveOrder.beforeMethodCall();
        }

        try
        {
            delegate.acceptRemoveOrder(routingParameterV2Struct, orderIds);
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
            delegate.acceptLinkageCancelReport(routingParameterV2Struct, cancelReports);
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
            delegate.acceptLinkageFillReport(routingParameterV2Struct, fillReports);
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

    public void acceptOrderLocationServerResponse(OrderLocationServerResponseStruct response)
    {

        boolean exception = false;
        if(acceptOrderLocationServerResponse != null)
        {
            acceptOrderLocationServerResponse.beforeMethodCall();
        }

        try
        {
                delegate.acceptOrderLocationServerResponse(response);
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


    public void acceptOrderLocationSummaryServerResponse(OrderLocationSummaryServerResponseStruct response)
    {
        boolean exception = false;
        if(acceptOrderLocationSummaryServerResponse != null)
        {
            acceptOrderLocationSummaryServerResponse.beforeMethodCall();
        }

        try
        {
                delegate.acceptOrderLocationSummaryServerResponse(response);
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

    public void acceptRemoveMessage(RoutingParameterV2Struct routingParameterV2Struct, long msgId)
    {
        boolean exception = false;
        if (acceptRemoveMessage!= null)
        {
            acceptRemoveMessage.beforeMethodCall();
        }

        try
        {
            delegate.acceptRemoveMessage(routingParameterV2Struct, msgId);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptRemoveMessage != null)
            {
                acceptRemoveMessage.incCalls(1);
                acceptRemoveMessage.afterMethodCall();
                if (exception)
                {
                    acceptRemoveMessage.incExceptions(1);
                }
            }
        }

    }


    public void acceptManualFillTimeout(RoutingParameterV2Struct routingParameters, ManualFillTimeoutRoutingStruct[] fillReports)
    {
        boolean exception = false;
        if (acceptManualFillTimeout!= null)
        {
            acceptManualFillTimeout.beforeMethodCall();
        }

        try
        {
            delegate.acceptManualFillTimeout(routingParameters, fillReports);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptManualFillTimeout != null)
            {
                acceptManualFillTimeout.incCalls(1);
                acceptManualFillTimeout.afterMethodCall();
                if (exception)
                {
                    acceptManualFillTimeout.incExceptions(1);
                }
            }
        }
    }

    public void acceptManualOrderTimeout(RoutingParameterV2Struct routingParameters, ManualOrderTimeoutRoutingStruct[] manualOrderTimeouts)
    {
        boolean exception = false;
        if (acceptManualOrderTimeout!= null)
        {
            acceptManualOrderTimeout.beforeMethodCall();
        }

        try
        {
            delegate.acceptManualOrderTimeout(routingParameters, manualOrderTimeouts);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptManualOrderTimeout != null)
            {
                acceptManualOrderTimeout.incCalls(1);
                acceptManualOrderTimeout.afterMethodCall();
                if (exception)
                {
                    acceptManualOrderTimeout.incExceptions(1);
                }
            }
        }
    }

    public void acceptTradeNotifications(RoutingParameterV2Struct routingParameterV2Struct, 		TradeNotificationRoutingStruct[] tradeNotifications)
    {
       boolean exception = false;
        if (acceptTradeNotifications!= null)
        {
            acceptTradeNotifications.beforeMethodCall();
        }

        try
        {
            delegate.acceptTradeNotifications(routingParameterV2Struct, tradeNotifications);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptTradeNotifications != null)
            {
                acceptTradeNotifications.incCalls(1);
                acceptTradeNotifications.afterMethodCall();
                if (exception)
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
        if (acceptFillReportDropCopy!= null)
        {
            acceptFillReportDropCopy.beforeMethodCall();
        }

        try
        {
            delegate.acceptFillReportDropCopy(routingParameterV2Struct,
                                              fillReportDropCopies);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptFillReportDropCopy != null)
            {
                acceptFillReportDropCopy.incCalls(1);
                acceptFillReportDropCopy.afterMethodCall();
                if (exception)
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
        if (acceptCancelReportDropCopy!= null)
        {
            acceptCancelReportDropCopy.beforeMethodCall();
        }

        try
        {
            delegate.acceptCancelReportDropCopy(routingParameterV2Struct,
                                                cancelRoprtDropCopies);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptCancelReportDropCopy != null)
            {
                acceptCancelReportDropCopy.incCalls(1);
                acceptCancelReportDropCopy.afterMethodCall();
                if (exception)
                {
                    acceptCancelReportDropCopy.incExceptions(1);
                }
            }
        }

    }

    public void acceptManualOrders(RoutingParameterV2Struct orderRoutingStruct, OrderManualHandlingStructV2[] orders)
    {
		boolean exception = false;
	    if (acceptManualOrders!= null)
        {
            acceptManualOrders.beforeMethodCall();
        }

        try
        {
            delegate.acceptManualOrders(orderRoutingStruct, orders);
		}
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptManualOrders != null)
            {
                acceptManualOrders.incCalls(1);
                acceptManualOrders.afterMethodCall();
                if (exception)
                {
                    acceptManualOrders.incExceptions(1);
				}
	        }
		}
    }


	public void acceptManualCancels(RoutingParameterV2Struct orderRoutingStruct, ManualCancelRequestStructV2[] cancelRequests)
	{
		boolean exception = false;
		if (acceptManualCancels!= null)
		{
			acceptManualCancels.beforeMethodCall();
		}

		try
		{
			delegate.acceptManualCancels(orderRoutingStruct, cancelRequests);
		}
		catch (RuntimeException ex)
		{
			exception = true;
			throw ex;
		}
		finally
		{
			if (acceptManualCancels != null)
			{
				acceptManualCancels.incCalls(1);
				acceptManualCancels.afterMethodCall();
				if (exception)
				{
					acceptManualCancels.incExceptions(1);
				}
			}
		}
	}


   public void acceptManualCancelReplaces(RoutingParameterV2Struct orderRoutingStruct, ManualCancelReplaceStruct[] cancelReplaces)
   {
   		boolean exception = false;
   		if (acceptManualCancelReplaces!= null)
   		{
   			acceptManualCancelReplaces.beforeMethodCall();
   		}

   		try
   		{
   			delegate.acceptManualCancelReplaces(orderRoutingStruct, cancelReplaces);
   		}
   		catch (RuntimeException ex)
   		{
   			exception = true;
   			throw ex;
   		}
   		finally
   		{
   			if (acceptManualCancelReplaces != null)
   			{
   				acceptManualCancelReplaces.incCalls(1);
   				acceptManualCancelReplaces.afterMethodCall();
   				if (exception)
   				{
   					acceptManualCancelReplaces.incExceptions(1);
   				}
   			}
   		}
	}
}