package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.IntermarketOrderStatusConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class IntermarketOrderStatusEventConsumerInterceptor implements IntermarketOrderStatusConsumer
{


    MethodInstrumentor acceptFillRejectReport6;


    MethodInstrumentor acceptCancelHeldOrder5;


    MethodInstrumentor acceptHeldOrders4;


    MethodInstrumentor acceptNewHeldOrder3;


    MethodInstrumentor acceptHeldOrderStatus2;


    MethodInstrumentor acceptHeldOrderCancelReport1;

    private IntermarketOrderStatusConsumer delegate;

    MethodInstrumentor acceptHeldOrderFilledReport0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public IntermarketOrderStatusEventConsumerInterceptor(Object bo)
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
            StringBuilder name = new StringBuilder(80);
            name.append("IntermarketOrderStatusEventConsumerInterceptor" + Instrumentor.NAME_DELIMITER + "acceptHeldOrderFilledReport0");
            acceptHeldOrderFilledReport0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptHeldOrderFilledReport0);
            acceptHeldOrderFilledReport0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("IntermarketOrderStatusEventConsumerInterceptor" + Instrumentor.NAME_DELIMITER + "acceptHeldOrderCancelReport1");
            acceptHeldOrderCancelReport1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptHeldOrderCancelReport1);
            acceptHeldOrderCancelReport1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("IntermarketOrderStatusEventConsumerInterceptor" + Instrumentor.NAME_DELIMITER + "acceptHeldOrderStatus2");
            acceptHeldOrderStatus2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptHeldOrderStatus2);
            acceptHeldOrderStatus2.setPrivate(privateOnly);
            name.setLength(0);
            name.append("IntermarketOrderStatusEventConsumerInterceptor" + Instrumentor.NAME_DELIMITER + "acceptNewHeldOrder3");
            acceptNewHeldOrder3 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptNewHeldOrder3);
            acceptNewHeldOrder3.setPrivate(privateOnly);
            name.setLength(0);
            name.append("IntermarketOrderStatusEventConsumerInterceptor" + Instrumentor.NAME_DELIMITER + "acceptHeldOrders4");
            acceptHeldOrders4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptHeldOrders4);
            acceptHeldOrders4.setPrivate(privateOnly);
            name.setLength(0);
            name.append("IntermarketOrderStatusEventConsumerInterceptor" + Instrumentor.NAME_DELIMITER + "acceptCancelHeldOrder5");
            acceptCancelHeldOrder5 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptCancelHeldOrder5);
            acceptCancelHeldOrder5.setPrivate(privateOnly);
            name.setLength(0);
            name.append("IntermarketOrderStatusEventConsumerInterceptor" + Instrumentor.NAME_DELIMITER + "acceptFillRejectReport6");
            acceptFillRejectReport6 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptFillRejectReport6);
            acceptFillRejectReport6.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptHeldOrderFilledReport0);
        acceptHeldOrderFilledReport0 = null;
        getMethodInstrumentorFactory().unregister(acceptHeldOrderCancelReport1);
        acceptHeldOrderCancelReport1 = null;
        getMethodInstrumentorFactory().unregister(acceptHeldOrderStatus2);
        acceptHeldOrderStatus2 = null;
        getMethodInstrumentorFactory().unregister(acceptNewHeldOrder3);
        acceptNewHeldOrder3 = null;
        getMethodInstrumentorFactory().unregister(acceptHeldOrders4);
        acceptHeldOrders4 = null;
        getMethodInstrumentorFactory().unregister(acceptCancelHeldOrder5);
        acceptCancelHeldOrder5 = null;
        getMethodInstrumentorFactory().unregister(acceptFillRejectReport6);
        acceptFillRejectReport6 = null;
    }

    /**
     */
    public void acceptHeldOrderFilledReport(int[] param0, com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct param1, com.cboe.idl.cmiOrder.FilledReportStruct[] param2)
    {
        boolean exception = false;
        if (acceptHeldOrderFilledReport0 != null)
        {
            acceptHeldOrderFilledReport0.beforeMethodCall();
        }
        try
        {
            delegate.acceptHeldOrderFilledReport(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptHeldOrderFilledReport0 != null)
            {
                acceptHeldOrderFilledReport0.incCalls(1);
                acceptHeldOrderFilledReport0.afterMethodCall();
                if (exception)
                {
                    acceptHeldOrderFilledReport0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (IntermarketOrderStatusConsumer) delegate;
    }

    /**
     */
    public void acceptHeldOrderCancelReport(int[] param0, com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct param1, com.cboe.idl.cmiUtil.CboeIdStruct param2, com.cboe.idl.cmiOrder.CancelReportStruct param3)
    {
        boolean exception = false;
        if (acceptHeldOrderCancelReport1 != null)
        {
            acceptHeldOrderCancelReport1.beforeMethodCall();
        }
        try
        {
            delegate.acceptHeldOrderCancelReport(param0, param1, param2, param3);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptHeldOrderCancelReport1 != null)
            {
                acceptHeldOrderCancelReport1.incCalls(1);
                acceptHeldOrderCancelReport1.afterMethodCall();
                if (exception)
                {
                    acceptHeldOrderCancelReport1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptHeldOrderStatus(int[] param0, com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct param1)
    {
        boolean exception = false;
        if (acceptHeldOrderStatus2 != null)
        {
            acceptHeldOrderStatus2.beforeMethodCall();
        }
        try
        {
            delegate.acceptHeldOrderStatus(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptHeldOrderStatus2 != null)
            {
                acceptHeldOrderStatus2.incCalls(1);
                acceptHeldOrderStatus2.afterMethodCall();
                if (exception)
                {
                    acceptHeldOrderStatus2.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptNewHeldOrder(int[] param0, com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct param1)
    {
        boolean exception = false;
        if (acceptNewHeldOrder3 != null)
        {
            acceptNewHeldOrder3.beforeMethodCall();
        }
        try
        {
            delegate.acceptNewHeldOrder(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptNewHeldOrder3 != null)
            {
                acceptNewHeldOrder3.incCalls(1);
                acceptNewHeldOrder3.afterMethodCall();
                if (exception)
                {
                    acceptNewHeldOrder3.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptHeldOrders(int[] param0, java.lang.String param1, int param2, com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct[] param3)
    {
        boolean exception = false;
        if (acceptHeldOrders4 != null)
        {
            acceptHeldOrders4.beforeMethodCall();
        }
        try
        {
            delegate.acceptHeldOrders(param0, param1, param2, param3);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptHeldOrders4 != null)
            {
                acceptHeldOrders4.incCalls(1);
                acceptHeldOrders4.afterMethodCall();
                if (exception)
                {
                    acceptHeldOrders4.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptCancelHeldOrder(int[] param0, com.cboe.idl.cmiProduct.ProductKeysStruct param1, com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelRequestStruct param2)
    {
        boolean exception = false;
        if (acceptCancelHeldOrder5 != null)
        {
            acceptCancelHeldOrder5.beforeMethodCall();
        }
        try
        {
            delegate.acceptCancelHeldOrder(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptCancelHeldOrder5 != null)
            {
                acceptCancelHeldOrder5.incCalls(1);
                acceptCancelHeldOrder5.afterMethodCall();
                if (exception)
                {
                    acceptCancelHeldOrder5.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptFillRejectReport(int[] param0, com.cboe.idl.cmiIntermarketMessages.FillRejectStruct[] param1)
    {
        boolean exception = false;
        if (acceptFillRejectReport6 != null)
        {
            acceptFillRejectReport6.beforeMethodCall();
        }
        try
        {
            delegate.acceptFillRejectReport(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptFillRejectReport6 != null)
            {
                acceptFillRejectReport6.incCalls(1);
                acceptFillRejectReport6.afterMethodCall();
                if (exception)
                {
                    acceptFillRejectReport6.incExceptions(1);
                }
            }
        }
    }
}

