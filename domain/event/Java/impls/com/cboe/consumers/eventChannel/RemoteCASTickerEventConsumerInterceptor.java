package com.cboe.consumers.eventChannel;

import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.RemoteCASTickerConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RemoteCASTickerEventConsumerInterceptor implements RemoteCASTickerConsumer
{


    MethodInstrumentor unsubscribeTickerForProductV25;


    MethodInstrumentor unsubscribeTickerForProduct4;


    MethodInstrumentor unsubscribeTickerForClassV23;


    MethodInstrumentor subscribeTickerForProductV22;


    MethodInstrumentor subscribeTickerForProduct1;
    
    

    private RemoteCASTickerConsumer delegate;

    MethodInstrumentor subscribeTickerForClassV20;
    private MethodInstrumentorFactory methodInstrumentorFactory;
    
    // Added for VTATS
    MethodInstrumentor subscribeLargeTradeLastSaleForClass0;
    MethodInstrumentor unsubscribeLargeTradeLastSaleForClass0;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RemoteCASTickerEventConsumerInterceptor(Object bo)
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
            name.append("RemoteCASTickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeTickerForClassV20");
            subscribeTickerForClassV20 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeTickerForClassV20);
            subscribeTickerForClassV20.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASTickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeTickerForProduct1");
            subscribeTickerForProduct1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeTickerForProduct1);
            subscribeTickerForProduct1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASTickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeTickerForProductV22");
            subscribeTickerForProductV22 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeTickerForProductV22);
            subscribeTickerForProductV22.setPrivate(privateOnly);
            // Added for VTATS
            name.setLength(0);
            name.append("RemoteCASTickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("subscribeLargeTradeLastSaleForClass0");
            subscribeLargeTradeLastSaleForClass0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(subscribeLargeTradeLastSaleForClass0);
            subscribeLargeTradeLastSaleForClass0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASTickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeTickerForClassV23");
            unsubscribeTickerForClassV23 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeTickerForClassV23);
            unsubscribeTickerForClassV23.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASTickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeTickerForProduct4");
            unsubscribeTickerForProduct4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeTickerForProduct4);
            unsubscribeTickerForProduct4.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASTickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeTickerForProductV25");
            unsubscribeTickerForProductV25 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeTickerForProductV25);
            unsubscribeTickerForProductV25.setPrivate(privateOnly);
            // Added for VTATS
            name.setLength(0);
            name.append("RemoteCASTickerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("unsubscribeLargeTradeLastSaleForClass0");
            unsubscribeLargeTradeLastSaleForClass0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(unsubscribeLargeTradeLastSaleForClass0);
            unsubscribeLargeTradeLastSaleForClass0.setPrivate(privateOnly);
            
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
        getMethodInstrumentorFactory().unregister(subscribeTickerForClassV20);
        subscribeTickerForClassV20 = null;
        getMethodInstrumentorFactory().unregister(subscribeTickerForProduct1);
        subscribeTickerForProduct1 = null;
        getMethodInstrumentorFactory().unregister(subscribeTickerForProductV22);
        subscribeTickerForProductV22 = null;
        // Added for VTATS
        getMethodInstrumentorFactory().unregister(subscribeLargeTradeLastSaleForClass0);
        subscribeLargeTradeLastSaleForClass0 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeTickerForClassV23);
        unsubscribeTickerForClassV23 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeTickerForProduct4);
        unsubscribeTickerForProduct4 = null;
        getMethodInstrumentorFactory().unregister(unsubscribeTickerForProductV25);
        unsubscribeTickerForProductV25 = null;
        // Added for VTATS
        getMethodInstrumentorFactory().unregister(unsubscribeLargeTradeLastSaleForClass0);
        unsubscribeLargeTradeLastSaleForClass0 = null;
        
    }

    /**
     */
    public void subscribeTickerForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMITickerConsumer param4, short param5)
    {
        boolean exception = false;
        if (subscribeTickerForClassV20 != null)
        {
            subscribeTickerForClassV20.beforeMethodCall();
        }
        try
        {
            delegate.subscribeTickerForClassV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeTickerForClassV20 != null)
            {
                subscribeTickerForClassV20.incCalls(1);
                subscribeTickerForClassV20.afterMethodCall();
                if (exception)
                {
                    subscribeTickerForClassV20.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RemoteCASTickerConsumer) delegate;
    }

    /**
     */
    public void subscribeTickerForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMITickerConsumer param5)
    {
        boolean exception = false;
        if (subscribeTickerForProduct1 != null)
        {
            subscribeTickerForProduct1.beforeMethodCall();
        }
        try
        {
            delegate.subscribeTickerForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeTickerForProduct1 != null)
            {
                subscribeTickerForProduct1.incCalls(1);
                subscribeTickerForProduct1.afterMethodCall();
                if (exception)
                {
                    subscribeTickerForProduct1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void subscribeTickerForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMITickerConsumer param5, short param6)
    {
        boolean exception = false;
        if (subscribeTickerForProductV22 != null)
        {
            subscribeTickerForProductV22.beforeMethodCall();
        }
        try
        {
            delegate.subscribeTickerForProductV2(param0, param1, param2, param3, param4, param5, param6);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeTickerForProductV22 != null)
            {
                subscribeTickerForProductV22.incCalls(1);
                subscribeTickerForProductV22.afterMethodCall();
                if (exception)
                {
                    subscribeTickerForProductV22.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeTickerForClassV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, com.cboe.idl.cmiCallbackV2.CMITickerConsumer param4)
    {
        boolean exception = false;
        if (unsubscribeTickerForClassV23 != null)
        {
            unsubscribeTickerForClassV23.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeTickerForClassV2(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeTickerForClassV23 != null)
            {
                unsubscribeTickerForClassV23.incCalls(1);
                unsubscribeTickerForClassV23.afterMethodCall();
                if (exception)
                {
                    unsubscribeTickerForClassV23.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeTickerForProduct(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallback.CMITickerConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeTickerForProduct4 != null)
        {
            unsubscribeTickerForProduct4.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeTickerForProduct(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeTickerForProduct4 != null)
            {
                unsubscribeTickerForProduct4.incCalls(1);
                unsubscribeTickerForProduct4.afterMethodCall();
                if (exception)
                {
                    unsubscribeTickerForProduct4.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void unsubscribeTickerForProductV2(com.cboe.idl.util.RoutingParameterStruct param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiCallbackV2.CMITickerConsumer param5)
    {
        boolean exception = false;
        if (unsubscribeTickerForProductV25 != null)
        {
            unsubscribeTickerForProductV25.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeTickerForProductV2(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeTickerForProductV25 != null)
            {
                unsubscribeTickerForProductV25.incCalls(1);
                unsubscribeTickerForProductV25.afterMethodCall();
                if (exception)
                {
                    unsubscribeTickerForProductV25.incExceptions(1);
                }
            }
        }
    }

	public void subscribeLargeTradeLastSaleForClass(RoutingParameterStruct arg0, String arg1, String arg2, String arg3, TickerConsumer arg4, short arg5) {
		boolean exception = false;
        if (subscribeLargeTradeLastSaleForClass0 != null)
        {
        	subscribeLargeTradeLastSaleForClass0.beforeMethodCall();
        }
        try
        {
            delegate.subscribeLargeTradeLastSaleForClass(arg0, arg1, arg2, arg3, arg4, arg5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (subscribeLargeTradeLastSaleForClass0 != null)
            {
            	subscribeLargeTradeLastSaleForClass0.incCalls(1);
            	subscribeLargeTradeLastSaleForClass0.afterMethodCall();
                if (exception)
                {
                	subscribeLargeTradeLastSaleForClass0.incExceptions(1);
                }
            }
        }
		
	}

	public void unsubscribeLargeTradeLastSaleForClass(RoutingParameterStruct arg0, String arg1, String arg2, String arg3, TickerConsumer arg4) {
		boolean exception = false;
        if (unsubscribeLargeTradeLastSaleForClass0 != null)
        {
        	unsubscribeLargeTradeLastSaleForClass0.beforeMethodCall();
        }
        try
        {
            delegate.unsubscribeLargeTradeLastSaleForClass(arg0, arg1, arg2, arg3, arg4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (unsubscribeLargeTradeLastSaleForClass0 != null)
            {
            	unsubscribeLargeTradeLastSaleForClass0.incCalls(1);
            	unsubscribeLargeTradeLastSaleForClass0.afterMethodCall();
                if (exception)
                {
                	unsubscribeLargeTradeLastSaleForClass0.incExceptions(1);
                }
            }
        }
		
	}
}