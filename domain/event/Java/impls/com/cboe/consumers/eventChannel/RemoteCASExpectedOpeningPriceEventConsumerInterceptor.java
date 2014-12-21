package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASExpectedOpeningPriceConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RemoteCASExpectedOpeningPriceEventConsumerInterceptor implements RemoteCASExpectedOpeningPriceConsumer
{


    MethodInstrumentor unsubscribeExpectedOpeningPriceForProductV25;


    MethodInstrumentor unsubscribeExpectedOpeningPriceForClassV24;


    MethodInstrumentor unsubscribeExpectedOpeningPriceForClass3;


    MethodInstrumentor subscribeExpectedOpeningPriceForProductV22;


    MethodInstrumentor subscribeExpectedOpeningPriceForClassV21;

    private RemoteCASExpectedOpeningPriceConsumer delegate;

    MethodInstrumentor subscribeExpectedOpeningPriceForClass0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RemoteCASExpectedOpeningPriceEventConsumerInterceptor(Object bo)
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
            StringBuilder name = new StringBuilder(100);
            name.append("RemoteCASExpectedOpeningPriceEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeExpectedOpeningPriceForClass0");
            subscribeExpectedOpeningPriceForClass0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeExpectedOpeningPriceForClass0);
            subscribeExpectedOpeningPriceForClass0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASExpectedOpeningPriceEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeExpectedOpeningPriceForClassV21");
            subscribeExpectedOpeningPriceForClassV21 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeExpectedOpeningPriceForClassV21);
            subscribeExpectedOpeningPriceForClassV21.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASExpectedOpeningPriceEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeExpectedOpeningPriceForProductV22");
            subscribeExpectedOpeningPriceForProductV22 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeExpectedOpeningPriceForProductV22);
            subscribeExpectedOpeningPriceForProductV22.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASExpectedOpeningPriceEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeExpectedOpeningPriceForClass3");
            unsubscribeExpectedOpeningPriceForClass3 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeExpectedOpeningPriceForClass3);
            unsubscribeExpectedOpeningPriceForClass3.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASExpectedOpeningPriceEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeExpectedOpeningPriceForClassV24");
            unsubscribeExpectedOpeningPriceForClassV24 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeExpectedOpeningPriceForClassV24);
            unsubscribeExpectedOpeningPriceForClassV24.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASExpectedOpeningPriceEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeExpectedOpeningPriceForProductV25");
            unsubscribeExpectedOpeningPriceForProductV25 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeExpectedOpeningPriceForProductV25);
            unsubscribeExpectedOpeningPriceForProductV25.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(subscribeExpectedOpeningPriceForClass0);
        subscribeExpectedOpeningPriceForClass0 = null;
        getMethodInstrumentorFactory().unregister(subscribeExpectedOpeningPriceForClassV21);
        subscribeExpectedOpeningPriceForClassV21 = null;
        getMethodInstrumentorFactory().unregister(subscribeExpectedOpeningPriceForProductV22);
        subscribeExpectedOpeningPriceForProductV22 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeExpectedOpeningPriceForClass3);
        unsubscribeExpectedOpeningPriceForClass3 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeExpectedOpeningPriceForClassV24);
        unsubscribeExpectedOpeningPriceForClassV24 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeExpectedOpeningPriceForProductV25);
        unsubscribeExpectedOpeningPriceForProductV25 = null;
    }

    /**
     */
    public void subscribeExpectedOpeningPriceForClass(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer param4)
    {
        boolean exception = false;
        if (subscribeExpectedOpeningPriceForClass0 != null)
        {
            subscribeExpectedOpeningPriceForClass0.beforeMethodCall();
        }
        try
        {
            delegate.subscribeExpectedOpeningPriceForClass(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeExpectedOpeningPriceForClass0 != null)
            {
                subscribeExpectedOpeningPriceForClass0.incCalls(1);
                subscribeExpectedOpeningPriceForClass0.afterMethodCall();
                if (exception)
                {
                    subscribeExpectedOpeningPriceForClass0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RemoteCASExpectedOpeningPriceConsumer) delegate;
    }

    /**
     */
    public void subscribeExpectedOpeningPriceForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer param4, short param5)
    {
        boolean exception = false;
        if (subscribeExpectedOpeningPriceForClassV21 != null)
        {
            subscribeExpectedOpeningPriceForClassV21.beforeMethodCall();
        }
        try
        {
            delegate.subscribeExpectedOpeningPriceForClassV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeExpectedOpeningPriceForClassV21 != null)
            {
                subscribeExpectedOpeningPriceForClassV21.incCalls(1);
                subscribeExpectedOpeningPriceForClassV21.afterMethodCall();
                if (exception)
                {
                    subscribeExpectedOpeningPriceForClassV21.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void subscribeExpectedOpeningPriceForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer param5, short param6)
    {
        boolean exception = false;
        if (subscribeExpectedOpeningPriceForProductV22 != null)
        {
            subscribeExpectedOpeningPriceForProductV22.beforeMethodCall();
        }
        try
        {
            delegate.subscribeExpectedOpeningPriceForProductV2(param0, param1, param2, param3, param4, param5, param6);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeExpectedOpeningPriceForProductV22 != null)
            {
                subscribeExpectedOpeningPriceForProductV22.incCalls(1);
                subscribeExpectedOpeningPriceForProductV22.afterMethodCall();
                if (exception)
                {
                    subscribeExpectedOpeningPriceForProductV22.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeExpectedOpeningPriceForClass(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeExpectedOpeningPriceForClass3 != null)
        {
            unsubscribeExpectedOpeningPriceForClass3.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeExpectedOpeningPriceForClass(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeExpectedOpeningPriceForClass3 != null)
            {
                unsubscribeExpectedOpeningPriceForClass3.incCalls(1);
                unsubscribeExpectedOpeningPriceForClass3.afterMethodCall();
                if (exception)
                {
                    unsubscribeExpectedOpeningPriceForClass3.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeExpectedOpeningPriceForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeExpectedOpeningPriceForClassV24 != null)
        {
            unsubscribeExpectedOpeningPriceForClassV24.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeExpectedOpeningPriceForClassV2(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeExpectedOpeningPriceForClassV24 != null)
            {
                unsubscribeExpectedOpeningPriceForClassV24.incCalls(1);
                unsubscribeExpectedOpeningPriceForClassV24.afterMethodCall();
                if (exception)
                {
                    unsubscribeExpectedOpeningPriceForClassV24.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeExpectedOpeningPriceForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeExpectedOpeningPriceForProductV25 != null)
        {
            unsubscribeExpectedOpeningPriceForProductV25.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeExpectedOpeningPriceForProductV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeExpectedOpeningPriceForProductV25 != null)
            {
                unsubscribeExpectedOpeningPriceForProductV25.incCalls(1);
                unsubscribeExpectedOpeningPriceForProductV25.afterMethodCall();
                if (exception)
                {
                    unsubscribeExpectedOpeningPriceForProductV25.incExceptions(1);
                }
            }
        }
    }
}