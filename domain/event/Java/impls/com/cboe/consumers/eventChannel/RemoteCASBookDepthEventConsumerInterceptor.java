package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASBookDepthConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RemoteCASBookDepthEventConsumerInterceptor implements RemoteCASBookDepthConsumer
{


    MethodInstrumentor unsubscribeBookDepthForProductV25;


    MethodInstrumentor unsubscribeBookDepthForProduct4;


    MethodInstrumentor unsubscribeBookDepthForClassV23;


    MethodInstrumentor subscribeBookDepthForProductV22;


    MethodInstrumentor subscribeBookDepthForProduct1;

    private RemoteCASBookDepthConsumer delegate;

    MethodInstrumentor subscribeBookDepthForClassV20;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RemoteCASBookDepthEventConsumerInterceptor(Object bo)
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
            name.append("RemoteCASBookDepthEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeBookDepthForClassV20");
            subscribeBookDepthForClassV20 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeBookDepthForClassV20);
            subscribeBookDepthForClassV20.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASBookDepthEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeBookDepthForProduct1");
            subscribeBookDepthForProduct1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeBookDepthForProduct1);
            subscribeBookDepthForProduct1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASBookDepthEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeBookDepthForProductV22");
            subscribeBookDepthForProductV22 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeBookDepthForProductV22);
            subscribeBookDepthForProductV22.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASBookDepthEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeBookDepthForClassV23");
            unsubscribeBookDepthForClassV23 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeBookDepthForClassV23);
            unsubscribeBookDepthForClassV23.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASBookDepthEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeBookDepthForProduct4");
            unsubscribeBookDepthForProduct4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeBookDepthForProduct4);
            unsubscribeBookDepthForProduct4.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASBookDepthEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeBookDepthForProductV25");
            unsubscribeBookDepthForProductV25 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeBookDepthForProductV25);
            unsubscribeBookDepthForProductV25.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(subscribeBookDepthForClassV20);
        subscribeBookDepthForClassV20 = null;
        getMethodInstrumentorFactory().unregister(subscribeBookDepthForProduct1);
        subscribeBookDepthForProduct1 = null;
        getMethodInstrumentorFactory().unregister(subscribeBookDepthForProductV22);
        subscribeBookDepthForProductV22 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeBookDepthForClassV23);
        unsubscribeBookDepthForClassV23 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeBookDepthForProduct4);
        unsubscribeBookDepthForProduct4 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeBookDepthForProductV25);
        unsubscribeBookDepthForProductV25 = null;
    }

    /**
     */
    public void subscribeBookDepthForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer param4, short param5)
    {
        boolean exception = false;
        if (subscribeBookDepthForClassV20 != null)
        {
            subscribeBookDepthForClassV20.beforeMethodCall();
        }
        try
        {
            delegate.subscribeBookDepthForClassV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeBookDepthForClassV20 != null)
            {
                subscribeBookDepthForClassV20.incCalls(1);
                subscribeBookDepthForClassV20.afterMethodCall();
                if (exception)
                {
                    subscribeBookDepthForClassV20.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RemoteCASBookDepthConsumer) delegate;
    }

    /**
     */
    public void subscribeBookDepthForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMIOrderBookConsumer param5)
    {
        boolean exception = false;
        if (subscribeBookDepthForProduct1 != null)
        {
            subscribeBookDepthForProduct1.beforeMethodCall();
        }
        try
        {
            delegate.subscribeBookDepthForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeBookDepthForProduct1 != null)
            {
                subscribeBookDepthForProduct1.incCalls(1);
                subscribeBookDepthForProduct1.afterMethodCall();
                if (exception)
                {
                    subscribeBookDepthForProduct1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void subscribeBookDepthForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer param5, short param6)
    {
        boolean exception = false;
        if (subscribeBookDepthForProductV22 != null)
        {
            subscribeBookDepthForProductV22.beforeMethodCall();
        }
        try
        {
            delegate.subscribeBookDepthForProductV2(param0, param1, param2, param3, param4, param5, param6);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeBookDepthForProductV22 != null)
            {
                subscribeBookDepthForProductV22.incCalls(1);
                subscribeBookDepthForProductV22.afterMethodCall();
                if (exception)
                {
                    subscribeBookDepthForProductV22.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeBookDepthForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeBookDepthForClassV23 != null)
        {
            unsubscribeBookDepthForClassV23.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeBookDepthForClassV2(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeBookDepthForClassV23 != null)
            {
                unsubscribeBookDepthForClassV23.incCalls(1);
                unsubscribeBookDepthForClassV23.afterMethodCall();
                if (exception)
                {
                    unsubscribeBookDepthForClassV23.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeBookDepthForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMIOrderBookConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeBookDepthForProduct4 != null)
        {
            unsubscribeBookDepthForProduct4.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeBookDepthForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeBookDepthForProduct4 != null)
            {
                unsubscribeBookDepthForProduct4.incCalls(1);
                unsubscribeBookDepthForProduct4.afterMethodCall();
                if (exception)
                {
                    unsubscribeBookDepthForProduct4.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeBookDepthForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeBookDepthForProductV25 != null)
        {
            unsubscribeBookDepthForProductV25.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeBookDepthForProductV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeBookDepthForProductV25 != null)
            {
                unsubscribeBookDepthForProductV25.incCalls(1);
                unsubscribeBookDepthForProductV25.afterMethodCall();
                if (exception)
                {
                    unsubscribeBookDepthForProductV25.incExceptions(1);
                }
            }
        }
    }
}