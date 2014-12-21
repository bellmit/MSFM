package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASRecapConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RemoteCASRecapEventConsumerInterceptor implements RemoteCASRecapConsumer
{


    MethodInstrumentor unsubscribeRecapForProductV27;


    MethodInstrumentor unsubscribeRecapForProduct6;


    MethodInstrumentor unsubscribeRecapForClassV25;


    MethodInstrumentor unsubscribeRecapForClass4;


    MethodInstrumentor subscribeRecapForProductV23;


    MethodInstrumentor subscribeRecapForProduct2;


    MethodInstrumentor subscribeRecapForClassV21;

    private RemoteCASRecapConsumer delegate;

    MethodInstrumentor subscribeRecapForClass0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RemoteCASRecapEventConsumerInterceptor(Object bo)
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
            name.append("RemoteCASRecapEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeRecapForClass0");
            subscribeRecapForClass0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeRecapForClass0);
            subscribeRecapForClass0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASRecapEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeRecapForClassV21");
            subscribeRecapForClassV21 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeRecapForClassV21);
            subscribeRecapForClassV21.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASRecapEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeRecapForProduct2");
            subscribeRecapForProduct2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeRecapForProduct2);
            subscribeRecapForProduct2.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASRecapEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeRecapForProductV23");
            subscribeRecapForProductV23 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeRecapForProductV23);
            subscribeRecapForProductV23.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASRecapEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeRecapForClass4");
            unsubscribeRecapForClass4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeRecapForClass4);
            unsubscribeRecapForClass4.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASRecapEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeRecapForClassV25");
            unsubscribeRecapForClassV25 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeRecapForClassV25);
            unsubscribeRecapForClassV25.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASRecapEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeRecapForProduct6");
            unsubscribeRecapForProduct6 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeRecapForProduct6);
            unsubscribeRecapForProduct6.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASRecapEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeRecapForProductV27");
            unsubscribeRecapForProductV27 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeRecapForProductV27);
            unsubscribeRecapForProductV27.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(subscribeRecapForClass0);
        subscribeRecapForClass0 = null;
        getMethodInstrumentorFactory().unregister(subscribeRecapForClassV21);
        subscribeRecapForClassV21 = null;
        getMethodInstrumentorFactory().unregister(subscribeRecapForProduct2);
        subscribeRecapForProduct2 = null;
        getMethodInstrumentorFactory().unregister(subscribeRecapForProductV23);
        subscribeRecapForProductV23 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeRecapForClass4);
        unsubscribeRecapForClass4 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeRecapForClassV25);
        unsubscribeRecapForClassV25 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeRecapForProduct6);
        unsubscribeRecapForProduct6 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeRecapForProductV27);
        unsubscribeRecapForProductV27 = null;
    }

    /**
     */
    public void subscribeRecapForClass(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallback.CMIRecapConsumer param4)
    {
        boolean exception = false;
        if (subscribeRecapForClass0 != null)
        {
            subscribeRecapForClass0.beforeMethodCall();
        }
        try
        {
            delegate.subscribeRecapForClass(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeRecapForClass0 != null)
            {
                subscribeRecapForClass0.incCalls(1);
                subscribeRecapForClass0.afterMethodCall();
                if (exception)
                {
                    subscribeRecapForClass0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RemoteCASRecapConsumer) delegate;
    }

    /**
     */
    public void subscribeRecapForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer param4, short param5)
    {
        boolean exception = false;
        if (subscribeRecapForClassV21 != null)
        {
            subscribeRecapForClassV21.beforeMethodCall();
        }
        try
        {
            delegate.subscribeRecapForClassV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeRecapForClassV21 != null)
            {
                subscribeRecapForClassV21.incCalls(1);
                subscribeRecapForClassV21.afterMethodCall();
                if (exception)
                {
                    subscribeRecapForClassV21.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void subscribeRecapForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMIRecapConsumer param5)
    {
        boolean exception = false;
        if (subscribeRecapForProduct2 != null)
        {
            subscribeRecapForProduct2.beforeMethodCall();
        }
        try
        {
            delegate.subscribeRecapForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeRecapForProduct2 != null)
            {
                subscribeRecapForProduct2.incCalls(1);
                subscribeRecapForProduct2.afterMethodCall();
                if (exception)
                {
                    subscribeRecapForProduct2.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void subscribeRecapForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer param5, short param6)
    {
        boolean exception = false;
        if (subscribeRecapForProductV23 != null)
        {
            subscribeRecapForProductV23.beforeMethodCall();
        }
        try
        {
            delegate.subscribeRecapForProductV2(param0, param1, param2, param3, param4, param5, param6);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeRecapForProductV23 != null)
            {
                subscribeRecapForProductV23.incCalls(1);
                subscribeRecapForProductV23.afterMethodCall();
                if (exception)
                {
                    subscribeRecapForProductV23.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeRecapForClass(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallback.CMIRecapConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeRecapForClass4 != null)
        {
            unsubscribeRecapForClass4.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeRecapForClass(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeRecapForClass4 != null)
            {
                unsubscribeRecapForClass4.incCalls(1);
                unsubscribeRecapForClass4.afterMethodCall();
                if (exception)
                {
                    unsubscribeRecapForClass4.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeRecapForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeRecapForClassV25 != null)
        {
            unsubscribeRecapForClassV25.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeRecapForClassV2(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeRecapForClassV25 != null)
            {
                unsubscribeRecapForClassV25.incCalls(1);
                unsubscribeRecapForClassV25.afterMethodCall();
                if (exception)
                {
                    unsubscribeRecapForClassV25.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeRecapForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMIRecapConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeRecapForProduct6 != null)
        {
            unsubscribeRecapForProduct6.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeRecapForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeRecapForProduct6 != null)
            {
                unsubscribeRecapForProduct6.incCalls(1);
                unsubscribeRecapForProduct6.afterMethodCall();
                if (exception)
                {
                    unsubscribeRecapForProduct6.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeRecapForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeRecapForProductV27 != null)
        {
            unsubscribeRecapForProductV27.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeRecapForProductV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeRecapForProductV27 != null)
            {
                unsubscribeRecapForProductV27.incCalls(1);
                unsubscribeRecapForProductV27.afterMethodCall();
                if (exception)
                {
                    unsubscribeRecapForProductV27.incExceptions(1);
                }
            }
        }
    }
}