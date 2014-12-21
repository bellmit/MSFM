package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASBookDepthUpdateConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RemoteCASBookDepthUpdateEventConsumerInterceptor implements RemoteCASBookDepthUpdateConsumer
{


    MethodInstrumentor unsubscribeBookDepthUpdateForProductV25;


    MethodInstrumentor unsubscribeBookDepthUpdateForProduct4;


    MethodInstrumentor unsubscribeBookDepthUpdateForClassV23;


    MethodInstrumentor subscribeBookDepthUpdateForProductV22;


    MethodInstrumentor subscribeBookDepthUpdateForProduct1;

    private RemoteCASBookDepthUpdateConsumer delegate;

    MethodInstrumentor subscribeBookDepthUpdateForClassV20;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RemoteCASBookDepthUpdateEventConsumerInterceptor(Object bo)
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
            StringBuilder name = new StringBuilder(90);
            name.append("RemoteCASBookDepthUpdateEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeBookDepthUpdateForClassV20");
            subscribeBookDepthUpdateForClassV20 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeBookDepthUpdateForClassV20);
            subscribeBookDepthUpdateForClassV20.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASBookDepthUpdateEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeBookDepthUpdateForProduct1");
            subscribeBookDepthUpdateForProduct1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeBookDepthUpdateForProduct1);
            subscribeBookDepthUpdateForProduct1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASBookDepthUpdateEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeBookDepthUpdateForProductV22");
            subscribeBookDepthUpdateForProductV22 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeBookDepthUpdateForProductV22);
            subscribeBookDepthUpdateForProductV22.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASBookDepthUpdateEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeBookDepthUpdateForClassV23");
            unsubscribeBookDepthUpdateForClassV23 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeBookDepthUpdateForClassV23);
            unsubscribeBookDepthUpdateForClassV23.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASBookDepthUpdateEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeBookDepthUpdateForProduct4");
            unsubscribeBookDepthUpdateForProduct4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeBookDepthUpdateForProduct4);
            unsubscribeBookDepthUpdateForProduct4.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASBookDepthUpdateEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeBookDepthUpdateForProductV25");
            unsubscribeBookDepthUpdateForProductV25 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeBookDepthUpdateForProductV25);
            unsubscribeBookDepthUpdateForProductV25.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(subscribeBookDepthUpdateForClassV20);
        subscribeBookDepthUpdateForClassV20 = null;
        getMethodInstrumentorFactory().unregister(subscribeBookDepthUpdateForProduct1);
        subscribeBookDepthUpdateForProduct1 = null;
        getMethodInstrumentorFactory().unregister(subscribeBookDepthUpdateForProductV22);
        subscribeBookDepthUpdateForProductV22 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeBookDepthUpdateForClassV23);
        unsubscribeBookDepthUpdateForClassV23 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeBookDepthUpdateForProduct4);
        unsubscribeBookDepthUpdateForProduct4 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeBookDepthUpdateForProductV25);
        unsubscribeBookDepthUpdateForProductV25 = null;
    }

    /**
     */
    public void subscribeBookDepthUpdateForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer param4, short param5)
    {
        boolean exception = false;
        if (subscribeBookDepthUpdateForClassV20 != null)
        {
            subscribeBookDepthUpdateForClassV20.beforeMethodCall();
        }
        try
        {
            delegate.subscribeBookDepthUpdateForClassV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeBookDepthUpdateForClassV20 != null)
            {
                subscribeBookDepthUpdateForClassV20.incCalls(1);
                subscribeBookDepthUpdateForClassV20.afterMethodCall();
                if (exception)
                {
                    subscribeBookDepthUpdateForClassV20.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RemoteCASBookDepthUpdateConsumer) delegate;
    }

    /**
     */
    public void subscribeBookDepthUpdateForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer param5)
    {
        boolean exception = false;
        if (subscribeBookDepthUpdateForProduct1 != null)
        {
            subscribeBookDepthUpdateForProduct1.beforeMethodCall();
        }
        try
        {
            delegate.subscribeBookDepthUpdateForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeBookDepthUpdateForProduct1 != null)
            {
                subscribeBookDepthUpdateForProduct1.incCalls(1);
                subscribeBookDepthUpdateForProduct1.afterMethodCall();
                if (exception)
                {
                    subscribeBookDepthUpdateForProduct1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void subscribeBookDepthUpdateForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer param5, short param6)
    {
        boolean exception = false;
        if (subscribeBookDepthUpdateForProductV22 != null)
        {
            subscribeBookDepthUpdateForProductV22.beforeMethodCall();
        }
        try
        {
            delegate.subscribeBookDepthUpdateForProductV2(param0, param1, param2, param3, param4, param5, param6);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeBookDepthUpdateForProductV22 != null)
            {
                subscribeBookDepthUpdateForProductV22.incCalls(1);
                subscribeBookDepthUpdateForProductV22.afterMethodCall();
                if (exception)
                {
                    subscribeBookDepthUpdateForProductV22.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeBookDepthUpdateForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeBookDepthUpdateForClassV23 != null)
        {
            unsubscribeBookDepthUpdateForClassV23.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeBookDepthUpdateForClassV2(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeBookDepthUpdateForClassV23 != null)
            {
                unsubscribeBookDepthUpdateForClassV23.incCalls(1);
                unsubscribeBookDepthUpdateForClassV23.afterMethodCall();
                if (exception)
                {
                    unsubscribeBookDepthUpdateForClassV23.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeBookDepthUpdateForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeBookDepthUpdateForProduct4 != null)
        {
            unsubscribeBookDepthUpdateForProduct4.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeBookDepthUpdateForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeBookDepthUpdateForProduct4 != null)
            {
                unsubscribeBookDepthUpdateForProduct4.incCalls(1);
                unsubscribeBookDepthUpdateForProduct4.afterMethodCall();
                if (exception)
                {
                    unsubscribeBookDepthUpdateForProduct4.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeBookDepthUpdateForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeBookDepthUpdateForProductV25 != null)
        {
            unsubscribeBookDepthUpdateForProductV25.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeBookDepthUpdateForProductV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeBookDepthUpdateForProductV25 != null)
            {
                unsubscribeBookDepthUpdateForProductV25.incCalls(1);
                unsubscribeBookDepthUpdateForProductV25.afterMethodCall();
                if (exception)
                {
                    unsubscribeBookDepthUpdateForProductV25.incExceptions(1);
                }
            }
        }
    }
}