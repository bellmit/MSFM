package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.BookDepthConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class BookDepthEventConsumerInterceptor implements BookDepthConsumer
{


    MethodInstrumentor acceptBookDepthForClass1;

    private BookDepthConsumer delegate;

    MethodInstrumentor acceptBookDepth0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public BookDepthEventConsumerInterceptor(Object bo)
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
            StringBuilder name = new StringBuilder(60);
            name.append("BookDepthEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptBookDepth0");
            acceptBookDepth0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptBookDepth0);
            acceptBookDepth0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("BookDepthEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptBookDepthForClass1");
            acceptBookDepthForClass1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptBookDepthForClass1);
            acceptBookDepthForClass1.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptBookDepth0);
        acceptBookDepth0 = null;
        getMethodInstrumentorFactory().unregister(acceptBookDepthForClass1);
        acceptBookDepthForClass1 = null;
    }

    /**
     */
    public void acceptBookDepth(int[] param0, com.cboe.idl.cmiMarketData.BookDepthStruct param1)
    {
        boolean exception = false;
        if (acceptBookDepth0 != null)
        {
            acceptBookDepth0.beforeMethodCall();
        }
        try
        {
            delegate.acceptBookDepth(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptBookDepth0 != null)
            {
                acceptBookDepth0.incCalls(1);
                acceptBookDepth0.afterMethodCall();
                if (exception)
                {
                    acceptBookDepth0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (BookDepthConsumer) delegate;
    }

    /**
     */
    public void acceptBookDepthForClass(com.cboe.idl.util.RoutingParameterStruct param0, com.cboe.idl.cmiMarketData.BookDepthStruct[] param1)
    {
        boolean exception = false;
        if (acceptBookDepthForClass1 != null)
        {
            acceptBookDepthForClass1.beforeMethodCall();
        }
        try
        {
            delegate.acceptBookDepthForClass(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptBookDepthForClass1 != null)
            {
                acceptBookDepthForClass1.incCalls(1);
                acceptBookDepthForClass1.afterMethodCall();
                if (exception)
                {
                    acceptBookDepthForClass1.incExceptions(1);
                }
            }
        }
    }
}