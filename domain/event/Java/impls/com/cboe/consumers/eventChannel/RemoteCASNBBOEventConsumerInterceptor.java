package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASNBBOConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RemoteCASNBBOEventConsumerInterceptor implements RemoteCASNBBOConsumer
{


    MethodInstrumentor unsubscribeNBBOForProductV27;


    MethodInstrumentor unsubscribeNBBOForProduct6;


    MethodInstrumentor unsubscribeNBBOForClassV25;


    MethodInstrumentor unsubscribeNBBOForClass4;


    MethodInstrumentor subscribeNBBOForProductV23;


    MethodInstrumentor subscribeNBBOForProduct2;


    MethodInstrumentor subscribeNBBOForClassV21;

    private RemoteCASNBBOConsumer delegate;

    MethodInstrumentor subscribeNBBOForClass0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RemoteCASNBBOEventConsumerInterceptor(Object bo)
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
            name.append("RemoteCASNBBOEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeNBBOForClass0");
            subscribeNBBOForClass0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeNBBOForClass0);
            subscribeNBBOForClass0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASNBBOEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeNBBOForClassV21");
            subscribeNBBOForClassV21 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeNBBOForClassV21);
            subscribeNBBOForClassV21.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASNBBOEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeNBBOForProduct2");
            subscribeNBBOForProduct2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeNBBOForProduct2);
            subscribeNBBOForProduct2.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASNBBOEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeNBBOForProductV23");
            subscribeNBBOForProductV23 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeNBBOForProductV23);
            subscribeNBBOForProductV23.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASNBBOEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeNBBOForClass4");
            unsubscribeNBBOForClass4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeNBBOForClass4);
            unsubscribeNBBOForClass4.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASNBBOEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeNBBOForClassV25");
            unsubscribeNBBOForClassV25 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeNBBOForClassV25);
            unsubscribeNBBOForClassV25.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASNBBOEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeNBBOForProduct6");
            unsubscribeNBBOForProduct6 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeNBBOForProduct6);
            unsubscribeNBBOForProduct6.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASNBBOEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeNBBOForProductV27");
            unsubscribeNBBOForProductV27 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeNBBOForProductV27);
            unsubscribeNBBOForProductV27.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(subscribeNBBOForClass0);
        subscribeNBBOForClass0 = null;
        getMethodInstrumentorFactory().unregister(subscribeNBBOForClassV21);
        subscribeNBBOForClassV21 = null;
        getMethodInstrumentorFactory().unregister(subscribeNBBOForProduct2);
        subscribeNBBOForProduct2 = null;
        getMethodInstrumentorFactory().unregister(subscribeNBBOForProductV23);
        subscribeNBBOForProductV23 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeNBBOForClass4);
        unsubscribeNBBOForClass4 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeNBBOForClassV25);
        unsubscribeNBBOForClassV25 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeNBBOForProduct6);
        unsubscribeNBBOForProduct6 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeNBBOForProductV27);
        unsubscribeNBBOForProductV27 = null;
    }

    /**
     */
    public void subscribeNBBOForClass(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallback.CMINBBOConsumer param4)
    {
        boolean exception = false;
        if (subscribeNBBOForClass0 != null)
        {
            subscribeNBBOForClass0.beforeMethodCall();
        }
        try
        {
            delegate.subscribeNBBOForClass(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeNBBOForClass0 != null)
            {
                subscribeNBBOForClass0.incCalls(1);
                subscribeNBBOForClass0.afterMethodCall();
                if (exception)
                {
                    subscribeNBBOForClass0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RemoteCASNBBOConsumer) delegate;
    }

    /**
     */
    public void subscribeNBBOForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer param4, short param5)
    {
        boolean exception = false;
        if (subscribeNBBOForClassV21 != null)
        {
            subscribeNBBOForClassV21.beforeMethodCall();
        }
        try
        {
            delegate.subscribeNBBOForClassV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeNBBOForClassV21 != null)
            {
                subscribeNBBOForClassV21.incCalls(1);
                subscribeNBBOForClassV21.afterMethodCall();
                if (exception)
                {
                    subscribeNBBOForClassV21.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void subscribeNBBOForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMINBBOConsumer param5)
    {
        boolean exception = false;
        if (subscribeNBBOForProduct2 != null)
        {
            subscribeNBBOForProduct2.beforeMethodCall();
        }
        try
        {
            delegate.subscribeNBBOForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeNBBOForProduct2 != null)
            {
                subscribeNBBOForProduct2.incCalls(1);
                subscribeNBBOForProduct2.afterMethodCall();
                if (exception)
                {
                    subscribeNBBOForProduct2.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void subscribeNBBOForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer param5, short param6)
    {
        boolean exception = false;
        if (subscribeNBBOForProductV23 != null)
        {
            subscribeNBBOForProductV23.beforeMethodCall();
        }
        try
        {
            delegate.subscribeNBBOForProductV2(param0, param1, param2, param3, param4, param5, param6);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeNBBOForProductV23 != null)
            {
                subscribeNBBOForProductV23.incCalls(1);
                subscribeNBBOForProductV23.afterMethodCall();
                if (exception)
                {
                    subscribeNBBOForProductV23.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeNBBOForClass(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallback.CMINBBOConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeNBBOForClass4 != null)
        {
            unsubscribeNBBOForClass4.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeNBBOForClass(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeNBBOForClass4 != null)
            {
                unsubscribeNBBOForClass4.incCalls(1);
                unsubscribeNBBOForClass4.afterMethodCall();
                if (exception)
                {
                    unsubscribeNBBOForClass4.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeNBBOForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeNBBOForClassV25 != null)
        {
            unsubscribeNBBOForClassV25.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeNBBOForClassV2(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeNBBOForClassV25 != null)
            {
                unsubscribeNBBOForClassV25.incCalls(1);
                unsubscribeNBBOForClassV25.afterMethodCall();
                if (exception)
                {
                    unsubscribeNBBOForClassV25.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeNBBOForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMINBBOConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeNBBOForProduct6 != null)
        {
            unsubscribeNBBOForProduct6.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeNBBOForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeNBBOForProduct6 != null)
            {
                unsubscribeNBBOForProduct6.incCalls(1);
                unsubscribeNBBOForProduct6.afterMethodCall();
                if (exception)
                {
                    unsubscribeNBBOForProduct6.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeNBBOForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeNBBOForProductV27 != null)
        {
            unsubscribeNBBOForProductV27.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeNBBOForProductV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeNBBOForProductV27 != null)
            {
                unsubscribeNBBOForProductV27.incCalls(1);
                unsubscribeNBBOForProductV27.afterMethodCall();
                if (exception)
                {
                    unsubscribeNBBOForProductV27.incExceptions(1);
                }
            }
        }
    }
}