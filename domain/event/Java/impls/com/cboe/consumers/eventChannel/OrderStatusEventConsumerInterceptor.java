package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.OrderStatusConsumer;
import com.cboe.interfaces.events.OrderStatusConsumerV2;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.idl.order.BlockedOrderStatus;

public class OrderStatusEventConsumerInterceptor implements OrderStatusConsumerV2
{

    MethodInstrumentor acceptOrderStatus10;

    MethodInstrumentor acceptOrderStatusUpdate9;


    MethodInstrumentor acceptCancelReport8;


    MethodInstrumentor acceptOrderFillReport7;


    MethodInstrumentor acceptOrderAcceptedByBook6;


    MethodInstrumentor acceptNewOrder5;


    MethodInstrumentor acceptOrderUpdate4;


    MethodInstrumentor acceptOrders3;


    MethodInstrumentor acceptException2;


    MethodInstrumentor acceptOrderBustReport1;

    private OrderStatusConsumerV2 delegate;

    MethodInstrumentor acceptOrderBustReinstateReport0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public OrderStatusEventConsumerInterceptor(Object bo)
    {
        setDelegate(bo);
    }

    private MethodInstrumentorFactory getMethodInstrumentorFactory()
    {
        if (methodInstrumentorFactory == null)
        {
            methodInstrumentorFactory = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory();
        }
        return methodInstrumentorFactory;
    }

    public void startInstrumentation(boolean privateOnly)
    {
        try
        {
            StringBuilder name = new StringBuilder(70);
            name.append("OrderStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptOrderBustReinstateReport0");
            String nameString = name.toString();
            acceptOrderBustReinstateReport0 = getMethodInstrumentorFactory().find(nameString);
            if (acceptOrderBustReinstateReport0 == null)
            {
            	acceptOrderBustReinstateReport0 = getMethodInstrumentorFactory().create(nameString, null);
                getMethodInstrumentorFactory().register(acceptOrderBustReinstateReport0);
                acceptOrderBustReinstateReport0.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("OrderStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptOrderBustReport1");
            nameString = name.toString();
            acceptOrderBustReport1 = getMethodInstrumentorFactory().find(nameString);
            if (acceptOrderBustReport1 == null)
            {
	            acceptOrderBustReport1 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptOrderBustReport1);
	            acceptOrderBustReport1.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("OrderStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptException2");
            nameString = name.toString();
            acceptException2 = getMethodInstrumentorFactory().find(nameString);
            if (acceptException2 == null)
            {
	            acceptException2 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptException2);
	            acceptException2.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("OrderStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptOrders3");
            nameString = name.toString();
            acceptOrders3 = getMethodInstrumentorFactory().find(nameString);
            if (acceptOrders3 == null)
            {
	            acceptOrders3 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptOrders3);
	            acceptOrders3.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("OrderStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptOrderUpdate4");
            nameString = name.toString();
            acceptOrderUpdate4 = getMethodInstrumentorFactory().find(nameString);
            if (acceptOrderUpdate4 == null)
            {
	            acceptOrderUpdate4 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptOrderUpdate4);
	            acceptOrderUpdate4.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("OrderStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptNewOrder5");
            nameString = name.toString();
            acceptNewOrder5 = getMethodInstrumentorFactory().find(nameString);
            if (acceptNewOrder5 == null)
            {
	            acceptNewOrder5 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptNewOrder5);
	            acceptNewOrder5.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("OrderStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptOrderAcceptedByBook6");
            nameString = name.toString();
            acceptOrderAcceptedByBook6 = getMethodInstrumentorFactory().find(nameString);
            if (acceptOrderAcceptedByBook6 == null)
            {
	            acceptOrderAcceptedByBook6 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptOrderAcceptedByBook6);
	            acceptOrderAcceptedByBook6.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("OrderStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptOrderFillReport7");
            nameString = name.toString();
            acceptOrderFillReport7 = getMethodInstrumentorFactory().find(nameString);
            if (acceptOrderFillReport7 == null)
            {
	            acceptOrderFillReport7 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptOrderFillReport7);
	            acceptOrderFillReport7.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("OrderStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptCancelReport8");
            nameString = name.toString();
            acceptCancelReport8 = getMethodInstrumentorFactory().find(nameString);
            if (acceptCancelReport8 == null)
            {
	            acceptCancelReport8 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptCancelReport8);
	            acceptCancelReport8.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("OrderStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptOrderStatus10");
            nameString = name.toString();
            acceptOrderStatus10 = getMethodInstrumentorFactory().find(nameString);
            if (acceptOrderStatus10 == null)
            {
	            acceptOrderStatus10 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptOrderStatus10);
	            acceptOrderStatus10.setPrivate(privateOnly);
            }
        } catch (InstrumentorAlreadyCreatedException ex)
        {
            Log.exception(ex);
        }
    }

    /**
     *
     */
    public void removeInstrumentation()
    {
        getMethodInstrumentorFactory().unregister(acceptOrderBustReinstateReport0);
        acceptOrderBustReinstateReport0 = null;
        getMethodInstrumentorFactory().unregister(acceptOrderBustReport1);
        acceptOrderBustReport1 = null;
        getMethodInstrumentorFactory().unregister(acceptException2);
        acceptException2 = null;
        getMethodInstrumentorFactory().unregister(acceptOrders3);
        acceptOrders3 = null;
        getMethodInstrumentorFactory().unregister(acceptOrderUpdate4);
        acceptOrderUpdate4 = null;
        getMethodInstrumentorFactory().unregister(acceptNewOrder5);
        acceptNewOrder5 = null;
        getMethodInstrumentorFactory().unregister(acceptOrderAcceptedByBook6);
        acceptOrderAcceptedByBook6 = null;
        getMethodInstrumentorFactory().unregister(acceptOrderFillReport7);
        acceptOrderFillReport7 = null;
        getMethodInstrumentorFactory().unregister(acceptCancelReport8);
        acceptCancelReport8 = null;
        getMethodInstrumentorFactory().unregister(acceptOrderStatus10);
        acceptOrderStatus10 = null;
    }

    /**
     */
    public void acceptOrderBustReinstateReport(int[] param0, short param1, com.cboe.idl.cmiOrder.OrderStruct param2, com.cboe.idl.cmiOrder.BustReinstateReportStruct param3,String param4)
    {
        boolean exception = false;
        if (acceptOrderBustReinstateReport0 != null)
        {
            acceptOrderBustReinstateReport0.beforeMethodCall();
        }
        try
        {
            delegate.acceptOrderBustReinstateReport(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptOrderBustReinstateReport0 != null)
            {
                acceptOrderBustReinstateReport0.incCalls(1);
                acceptOrderBustReinstateReport0.afterMethodCall();
                if (exception)
                {
                    acceptOrderBustReinstateReport0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (OrderStatusConsumerV2) delegate;
    }

    /**
     */
    public void acceptOrderBustReport(int[] param0, short param1, com.cboe.idl.cmiOrder.OrderStruct param2, com.cboe.idl.cmiOrder.BustReportStruct[] param3,String param4)
    {
        boolean exception = false;
        if (acceptOrderBustReport1 != null)
        {
            acceptOrderBustReport1.beforeMethodCall();
        }
        try
        {
            delegate.acceptOrderBustReport(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptOrderBustReport1 != null)
            {
                acceptOrderBustReport1.incCalls(1);
                acceptOrderBustReport1.afterMethodCall();
                if (exception)
                {
                    acceptOrderBustReport1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptException(int[] param0, java.lang.String param1, int param2, java.lang.String param3)
    {
        boolean exception = false;
        if (acceptException2 != null)
        {
            acceptException2.beforeMethodCall();
        }
        try
        {
            delegate.acceptException(param0, param1, param2, param3);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptException2 != null)
            {
                acceptException2.incCalls(1);
                acceptException2.afterMethodCall();
                if (exception)
                {
                    acceptException2.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptOrders(int[] param0, java.lang.String param1, com.cboe.idl.cmiUser.ExchangeFirmStruct param2, com.cboe.idl.cmiOrder.OrderStruct[] param3)
    {
        boolean exception = false;
        if (acceptOrders3 != null)
        {
            acceptOrders3.beforeMethodCall();
        }
        try
        {
            delegate.acceptOrders(param0, param1, param2, param3);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptOrders3 != null)
            {
                acceptOrders3.incCalls(1);
                acceptOrders3.afterMethodCall();
                if (exception)
                {
                    acceptOrders3.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptOrderUpdate(int[] param0, com.cboe.idl.cmiOrder.OrderStruct param1)
    {
        boolean exception = false;
        if (acceptOrderUpdate4 != null)
        {
            acceptOrderUpdate4.beforeMethodCall();
        }
        try
        {
            delegate.acceptOrderUpdate(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptOrderUpdate4 != null)
            {
                acceptOrderUpdate4.incCalls(1);
                acceptOrderUpdate4.afterMethodCall();
                if (exception)
                {
                    acceptOrderUpdate4.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptNewOrder(int[] param0, short param1, com.cboe.idl.cmiOrder.OrderStruct param2,String param3)
    {
        boolean exception = false;
        if (acceptNewOrder5 != null)
        {
            acceptNewOrder5.beforeMethodCall();
        }
        try
        {
            delegate.acceptNewOrder(param0, param1, param2,param3);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptNewOrder5 != null)
            {
                acceptNewOrder5.incCalls(1);
                acceptNewOrder5.afterMethodCall();
                if (exception)
                {
                    acceptNewOrder5.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptOrderAcceptedByBook(int[] param0, com.cboe.idl.cmiOrder.OrderStruct param1)
    {
        boolean exception = false;
        if (acceptOrderAcceptedByBook6 != null)
        {
            acceptOrderAcceptedByBook6.beforeMethodCall();
        }
        try
        {
            delegate.acceptOrderAcceptedByBook(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptOrderAcceptedByBook6 != null)
            {
                acceptOrderAcceptedByBook6.incCalls(1);
                acceptOrderAcceptedByBook6.afterMethodCall();
                if (exception)
                {
                    acceptOrderAcceptedByBook6.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptOrderFillReport(int[] param0, short param1, com.cboe.idl.cmiOrder.OrderStruct param2, com.cboe.idl.cmiOrder.FilledReportStruct[] param3,String param4)
    {
        boolean exception = false;
        if (acceptOrderFillReport7 != null)
        {
            acceptOrderFillReport7.beforeMethodCall();
        }
        try
        {
            delegate.acceptOrderFillReport(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptOrderFillReport7 != null)
            {
                acceptOrderFillReport7.incCalls(1);
                acceptOrderFillReport7.afterMethodCall();
                if (exception)
                {
                    acceptOrderFillReport7.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptCancelReport(int[] param0, short param1, com.cboe.idl.cmiOrder.OrderStruct param2, com.cboe.idl.cmiOrder.CancelReportStruct[] param3,String param4)
    {
        boolean exception = false;
        if (acceptCancelReport8 != null)
        {
            acceptCancelReport8.beforeMethodCall();
        }
        try
        {
            delegate.acceptCancelReport(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptCancelReport8 != null)
            {
                acceptCancelReport8.incCalls(1);
                acceptCancelReport8.afterMethodCall();
                if (exception)
                {
                    acceptCancelReport8.incExceptions(1);
                }
            }
        }
    }


    /**
     */
    public void acceptOrderStatusUpdate(com.cboe.idl.util.RoutingParameterStruct param0, com.cboe.idl.cmiOrder.OrderStruct param1, short param2)
    {
        boolean exception = false;
        if (acceptOrderStatusUpdate9 != null)
        {
            acceptOrderStatusUpdate9.beforeMethodCall();
        }
        try
        {
            delegate.acceptOrderStatusUpdate(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptOrderStatusUpdate9 != null)
            {
                acceptOrderStatusUpdate9.incCalls(1);
                acceptOrderStatusUpdate9.afterMethodCall();
                if (exception)
                {
                    acceptOrderStatusUpdate9.incExceptions(1);
                }
            }
        }
    }

    public void acceptOrderStatus(BlockedOrderStatus[] messages)
    {
        boolean exception = false;
        if (acceptOrderStatus10 != null)
        {
            acceptOrderStatus10.beforeMethodCall();
        }
        try
        {
            delegate.acceptOrderStatus(messages);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptOrderStatus10 != null)
            {
                acceptOrderStatus10.incCalls(1);
                acceptOrderStatus10.afterMethodCall();
                if (exception)
                {
                    acceptOrderStatus10.incExceptions(1);
                }
            }
        }
    }
}
