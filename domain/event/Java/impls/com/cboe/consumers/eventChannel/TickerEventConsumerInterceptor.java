package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.TickerConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class TickerEventConsumerInterceptor implements TickerConsumer
{


    MethodInstrumentor acceptTickerForClass1;

    private TickerConsumer delegate;

    MethodInstrumentor acceptTicker0;
    private MethodInstrumentorFactory methodInstrumentorFactory;
    MethodInstrumentor acceptLargeTradeTickerDetail2;
    MethodInstrumentor acceptLargeTradeTickerDetailForClass3;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public TickerEventConsumerInterceptor(Object bo)
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
            StringBuilder name = new StringBuilder(75);
            name.append("TickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptTicker0");
            acceptTicker0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptTicker0);
            acceptTicker0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("TickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptTickerForClass1");
            acceptTickerForClass1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptTickerForClass1);
            acceptTickerForClass1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("TickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptLargeTradeTickerDetail2");
            acceptLargeTradeTickerDetail2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptLargeTradeTickerDetail2);
            acceptLargeTradeTickerDetail2.setPrivate(privateOnly);
            name.setLength(0);
            name.append("TickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptLargeTradeTickerDetailForClass3");
            acceptLargeTradeTickerDetailForClass3 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptLargeTradeTickerDetailForClass3);
            acceptLargeTradeTickerDetailForClass3.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptTicker0);
        acceptTicker0 = null;
        getMethodInstrumentorFactory().unregister(acceptTickerForClass1);
        acceptTickerForClass1 = null;
        getMethodInstrumentorFactory().unregister(acceptLargeTradeTickerDetail2);
        acceptLargeTradeTickerDetail2 = null;
        getMethodInstrumentorFactory().unregister(acceptLargeTradeTickerDetailForClass3);
        acceptLargeTradeTickerDetailForClass3 = null;
    }

    /**
     */
    public void acceptTicker(int[] param0, com.cboe.idl.marketData.InternalTickerStruct param1)
    {
        boolean exception = false;
        if (acceptTicker0 != null)
        {
            acceptTicker0.beforeMethodCall();
        }
        try
        {
            delegate.acceptTicker(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptTicker0 != null)
            {
                acceptTicker0.incCalls(1);
                acceptTicker0.afterMethodCall();
                if (exception)
                {
                    acceptTicker0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (TickerConsumer) delegate;
    }

    /**
     */
    public void acceptTickerForClass(com.cboe.idl.util.RoutingParameterStruct param0, com.cboe.idl.cmiUtil.TimeStruct[] param1, com.cboe.idl.cmiMarketData.TickerStruct[] param2)
    {
        boolean exception = false;
        if (acceptTickerForClass1 != null)
        {
            acceptTickerForClass1.beforeMethodCall();
        }
        try
        {
            delegate.acceptTickerForClass(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptTickerForClass1 != null)
            {
                acceptTickerForClass1.incCalls(1);
                acceptTickerForClass1.afterMethodCall();
                if (exception)
                {
                    acceptTickerForClass1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptLargeTradeTickerDetailForClass(com.cboe.idl.util.RoutingParameterStruct param0, com.cboe.idl.marketData.InternalTickerDetailStruct[] param1)
    {
        boolean exception = false;
        if (acceptLargeTradeTickerDetailForClass3 != null)
        {
        	acceptLargeTradeTickerDetailForClass3.beforeMethodCall();
        }
        try
        {
            delegate.acceptLargeTradeTickerDetailForClass(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptLargeTradeTickerDetailForClass3 != null)
            {
            	acceptLargeTradeTickerDetailForClass3.incCalls(1);
            	acceptLargeTradeTickerDetailForClass3.afterMethodCall();
                if (exception)
                {
                	acceptLargeTradeTickerDetailForClass3.incExceptions(1);
                }
            }
        }
    }
}