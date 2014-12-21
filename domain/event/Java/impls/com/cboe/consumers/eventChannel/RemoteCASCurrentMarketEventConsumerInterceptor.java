package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASCurrentMarketConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RemoteCASCurrentMarketEventConsumerInterceptor implements RemoteCASCurrentMarketConsumer
{

    MethodInstrumentor unsubscribeCurrentMarketForProductV311;


    MethodInstrumentor subscribeCurrentMarketForProductV310;


    MethodInstrumentor unsubscribeCurrentMarketForClassV39;


    MethodInstrumentor subscribeCurrentMarketForClassV38;


    MethodInstrumentor unsubscribeCurrentMarketForProductV27;


    MethodInstrumentor unsubscribeCurrentMarketForProduct6;


    MethodInstrumentor unsubscribeCurrentMarketForClassV25;


    MethodInstrumentor unsubscribeCurrentMarketForClass4;


    MethodInstrumentor subscribeCurrentMarketForProductV23;


    MethodInstrumentor subscribeCurrentMarketForProduct2;


    MethodInstrumentor subscribeCurrentMarketForClassV21;

    private RemoteCASCurrentMarketConsumer delegate;

    MethodInstrumentor subscribeCurrentMarketForClass0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RemoteCASCurrentMarketEventConsumerInterceptor(Object bo)
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
            name.append("RemoteCASCurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeCurrentMarketForClass0");
            subscribeCurrentMarketForClass0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeCurrentMarketForClass0);
            subscribeCurrentMarketForClass0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASCurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeCurrentMarketForClassV21");
            subscribeCurrentMarketForClassV21 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeCurrentMarketForClassV21);
            subscribeCurrentMarketForClassV21.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASCurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeCurrentMarketForProduct2");
            subscribeCurrentMarketForProduct2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeCurrentMarketForProduct2);
            subscribeCurrentMarketForProduct2.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASCurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeCurrentMarketForProductV23");
            subscribeCurrentMarketForProductV23 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeCurrentMarketForProductV23);
            subscribeCurrentMarketForProductV23.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASCurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeCurrentMarketForClass4");
            unsubscribeCurrentMarketForClass4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeCurrentMarketForClass4);
            unsubscribeCurrentMarketForClass4.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASCurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeCurrentMarketForClassV25");
            unsubscribeCurrentMarketForClassV25 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeCurrentMarketForClassV25);
            unsubscribeCurrentMarketForClassV25.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASCurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeCurrentMarketForProduct6");
            unsubscribeCurrentMarketForProduct6 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeCurrentMarketForProduct6);
            unsubscribeCurrentMarketForProduct6.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASCurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeCurrentMarketForProductV27");
            unsubscribeCurrentMarketForProductV27 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeCurrentMarketForProductV27);
            unsubscribeCurrentMarketForProductV27.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(subscribeCurrentMarketForClass0);
        subscribeCurrentMarketForClass0 = null;
        getMethodInstrumentorFactory().unregister(subscribeCurrentMarketForClassV21);
        subscribeCurrentMarketForClassV21 = null;
        getMethodInstrumentorFactory().unregister(subscribeCurrentMarketForProduct2);
        subscribeCurrentMarketForProduct2 = null;
        getMethodInstrumentorFactory().unregister(subscribeCurrentMarketForProductV23);
        subscribeCurrentMarketForProductV23 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeCurrentMarketForClass4);
        unsubscribeCurrentMarketForClass4 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeCurrentMarketForClassV25);
        unsubscribeCurrentMarketForClassV25 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeCurrentMarketForProduct6);
        unsubscribeCurrentMarketForProduct6 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeCurrentMarketForProductV27);
        unsubscribeCurrentMarketForProductV27 = null;
    }

    /**
     */
    public void subscribeCurrentMarketForClass(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer param4)
    {
        boolean exception = false;
        if (subscribeCurrentMarketForClass0 != null)
        {
            subscribeCurrentMarketForClass0.beforeMethodCall();
        }
        try
        {
            delegate.subscribeCurrentMarketForClass(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeCurrentMarketForClass0 != null)
            {
                subscribeCurrentMarketForClass0.incCalls(1);
                subscribeCurrentMarketForClass0.afterMethodCall();
                if (exception)
                {
                    subscribeCurrentMarketForClass0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RemoteCASCurrentMarketConsumer) delegate;
    }

    /**
     */
    public void subscribeCurrentMarketForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer param4, short param5)
    {
        boolean exception = false;
        if (subscribeCurrentMarketForClassV21 != null)
        {
            subscribeCurrentMarketForClassV21.beforeMethodCall();
        }
        try
        {
            delegate.subscribeCurrentMarketForClassV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeCurrentMarketForClassV21 != null)
            {
                subscribeCurrentMarketForClassV21.incCalls(1);
                subscribeCurrentMarketForClassV21.afterMethodCall();
                if (exception)
                {
                    subscribeCurrentMarketForClassV21.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void subscribeCurrentMarketForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer param5)
    {
        boolean exception = false;
        if (subscribeCurrentMarketForProduct2 != null)
        {
            subscribeCurrentMarketForProduct2.beforeMethodCall();
        }
        try
        {
            delegate.subscribeCurrentMarketForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeCurrentMarketForProduct2 != null)
            {
                subscribeCurrentMarketForProduct2.incCalls(1);
                subscribeCurrentMarketForProduct2.afterMethodCall();
                if (exception)
                {
                    subscribeCurrentMarketForProduct2.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void subscribeCurrentMarketForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer param5, short param6)
    {
        boolean exception = false;
        if (subscribeCurrentMarketForProductV23 != null)
        {
            subscribeCurrentMarketForProductV23.beforeMethodCall();
        }
        try
        {
            delegate.subscribeCurrentMarketForProductV2(param0, param1, param2, param3, param4, param5, param6);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeCurrentMarketForProductV23 != null)
            {
                subscribeCurrentMarketForProductV23.incCalls(1);
                subscribeCurrentMarketForProductV23.afterMethodCall();
                if (exception)
                {
                    subscribeCurrentMarketForProductV23.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeCurrentMarketForClass(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeCurrentMarketForClass4 != null)
        {
            unsubscribeCurrentMarketForClass4.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeCurrentMarketForClass(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeCurrentMarketForClass4 != null)
            {
                unsubscribeCurrentMarketForClass4.incCalls(1);
                unsubscribeCurrentMarketForClass4.afterMethodCall();
                if (exception)
                {
                    unsubscribeCurrentMarketForClass4.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeCurrentMarketForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeCurrentMarketForClassV25 != null)
        {
            unsubscribeCurrentMarketForClassV25.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeCurrentMarketForClassV2(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeCurrentMarketForClassV25 != null)
            {
                unsubscribeCurrentMarketForClassV25.incCalls(1);
                unsubscribeCurrentMarketForClassV25.afterMethodCall();
                if (exception)
                {
                    unsubscribeCurrentMarketForClassV25.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeCurrentMarketForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeCurrentMarketForProduct6 != null)
        {
            unsubscribeCurrentMarketForProduct6.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeCurrentMarketForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeCurrentMarketForProduct6 != null)
            {
                unsubscribeCurrentMarketForProduct6.incCalls(1);
                unsubscribeCurrentMarketForProduct6.afterMethodCall();
                if (exception)
                {
                    unsubscribeCurrentMarketForProduct6.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeCurrentMarketForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeCurrentMarketForProductV27 != null)
        {
            unsubscribeCurrentMarketForProductV27.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeCurrentMarketForProductV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeCurrentMarketForProductV27 != null)
            {
                unsubscribeCurrentMarketForProductV27.incCalls(1);
                unsubscribeCurrentMarketForProductV27.afterMethodCall();
                if (exception)
                {
                    unsubscribeCurrentMarketForProductV27.incExceptions(1);
                }
            }
        }
    }


    /**
     */
    public void subscribeCurrentMarketForClassV3(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer param4, short param5)
    {
        boolean exception = false;
        if (subscribeCurrentMarketForClassV38!= null)
        {
            subscribeCurrentMarketForClassV38.beforeMethodCall();
        }
        try
        {
            delegate.subscribeCurrentMarketForClassV3(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeCurrentMarketForClassV38 != null)
            {
                subscribeCurrentMarketForClassV38.incCalls(1);
                subscribeCurrentMarketForClassV38.afterMethodCall();
                if (exception)
                {
                    subscribeCurrentMarketForClassV38.incExceptions(1);
                }
            }
        }
    }


    /**
     */
    public void unsubscribeCurrentMarketForClassV3(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeCurrentMarketForClassV39!= null)
        {
            unsubscribeCurrentMarketForClassV39.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeCurrentMarketForClassV3(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeCurrentMarketForClassV39 != null)
            {
                unsubscribeCurrentMarketForClassV39.incCalls(1);
                unsubscribeCurrentMarketForClassV39.afterMethodCall();
                if (exception)
                {
                    unsubscribeCurrentMarketForClassV39.incExceptions(1);
                }
            }
        }
    }


    /**
     */
    public void subscribeCurrentMarketForProductV3(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer param5, short param6)
    {
        boolean exception = false;
        if (subscribeCurrentMarketForProductV310!= null)
        {
            subscribeCurrentMarketForProductV310.beforeMethodCall();
        }
        try
        {
            delegate.subscribeCurrentMarketForProductV3(param0, param1, param2, param3, param4, param5, param6);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeCurrentMarketForProductV310 != null)
            {
                subscribeCurrentMarketForProductV310.incCalls(1);
                subscribeCurrentMarketForProductV310.afterMethodCall();
                if (exception)
                {
                    subscribeCurrentMarketForProductV310.incExceptions(1);
                }
            }
        }
    }


    /**
     */
    public void unsubscribeCurrentMarketForProductV3(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeCurrentMarketForProductV311!= null)
        {
            unsubscribeCurrentMarketForProductV311.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeCurrentMarketForProductV3(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeCurrentMarketForProductV311 != null)
            {
                unsubscribeCurrentMarketForProductV311.incCalls(1);
                unsubscribeCurrentMarketForProductV311.afterMethodCall();
                if (exception)
                {
                    unsubscribeCurrentMarketForProductV311.incExceptions(1);
                }
            }
        }
    }
}
