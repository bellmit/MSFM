package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.CurrentMarketConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class CurrentMarketEventConsumerInterceptor implements CurrentMarketConsumer
{


    MethodInstrumentor acceptNBBO5;


    MethodInstrumentor acceptExpectedOpeningPricesForClass4;


    MethodInstrumentor acceptExpectedOpeningPrice3;


    MethodInstrumentor acceptCurrentMarketsForClass2;


    MethodInstrumentor acceptCurrentMarketAndNBBO1;

    private CurrentMarketConsumer delegate;

    MethodInstrumentor acceptCurrentMarket0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public CurrentMarketEventConsumerInterceptor(Object bo)
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
            name.append("CurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptCurrentMarket0");
            acceptCurrentMarket0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptCurrentMarket0);
            acceptCurrentMarket0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("CurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptCurrentMarketAndNBBO1");
            acceptCurrentMarketAndNBBO1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptCurrentMarketAndNBBO1);
            acceptCurrentMarketAndNBBO1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("CurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptCurrentMarketsForClass2");
            acceptCurrentMarketsForClass2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptCurrentMarketsForClass2);
            acceptCurrentMarketsForClass2.setPrivate(privateOnly);
            if (Log.isDebugOn())
            {
                name.setLength(0);
                name.append("CurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptExpectedOpeningPrice3");
            	acceptExpectedOpeningPrice3 = getMethodInstrumentorFactory().create(name.toString(), null);
            	getMethodInstrumentorFactory().register(acceptExpectedOpeningPrice3);
                acceptExpectedOpeningPrice3.setPrivate(privateOnly);
                name.setLength(0);
                name.append("CurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptExpectedOpeningPricesForClass4");
                acceptExpectedOpeningPricesForClass4 = getMethodInstrumentorFactory().create(name.toString(), null);
                getMethodInstrumentorFactory().register(acceptExpectedOpeningPricesForClass4);
                acceptExpectedOpeningPricesForClass4.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("CurrentMarketEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptNBBO5");
            acceptNBBO5 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptNBBO5);
            acceptNBBO5.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptCurrentMarket0);
        acceptCurrentMarket0 = null;
        getMethodInstrumentorFactory().unregister(acceptCurrentMarketAndNBBO1);
        acceptCurrentMarketAndNBBO1 = null;
        getMethodInstrumentorFactory().unregister(acceptCurrentMarketsForClass2);
        acceptCurrentMarketsForClass2 = null;
        if (acceptExpectedOpeningPrice3 != null)
        {
        	getMethodInstrumentorFactory().unregister(acceptExpectedOpeningPrice3);
        	acceptExpectedOpeningPrice3 = null;
        }
        if (acceptExpectedOpeningPricesForClass4 != null)
        {
        	getMethodInstrumentorFactory().unregister(acceptExpectedOpeningPricesForClass4);
        	acceptExpectedOpeningPricesForClass4 = null;
        }
        getMethodInstrumentorFactory().unregister(acceptNBBO5);
        acceptNBBO5 = null;
    }

    /**
     */
    public void acceptCurrentMarket(int[] param0, com.cboe.idl.cmiMarketData.CurrentMarketStruct param1, com.cboe.idl.cmiMarketData.CurrentMarketStruct param2)
    {
        boolean exception = false;
        if (acceptCurrentMarket0 != null)
        {
            acceptCurrentMarket0.beforeMethodCall();
        }
        try
        {
            delegate.acceptCurrentMarket(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptCurrentMarket0 != null)
            {
                acceptCurrentMarket0.incCalls(1);
                acceptCurrentMarket0.afterMethodCall();
                if (exception)
                {
                    acceptCurrentMarket0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (CurrentMarketConsumer) delegate;
    }

    /**
     */
    public void acceptCurrentMarketAndNBBO(int[] param0, com.cboe.idl.cmiMarketData.CurrentMarketStruct param1, com.cboe.idl.cmiMarketData.CurrentMarketStruct param2, com.cboe.idl.cmiMarketData.NBBOStruct param3)
    {
        boolean exception = false;
        if (acceptCurrentMarketAndNBBO1 != null)
        {
            acceptCurrentMarketAndNBBO1.beforeMethodCall();
        }
        try
        {
            delegate.acceptCurrentMarketAndNBBO(param0, param1, param2, param3);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptCurrentMarketAndNBBO1 != null)
            {
                acceptCurrentMarketAndNBBO1.incCalls(1);
                acceptCurrentMarketAndNBBO1.afterMethodCall();
                if (exception)
                {
                    acceptCurrentMarketAndNBBO1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptCurrentMarketsForClass(com.cboe.idl.util.RoutingParameterStruct param0, com.cboe.idl.cmiMarketData.CurrentMarketStruct[] param1, com.cboe.idl.cmiMarketData.CurrentMarketStruct[] param2, com.cboe.idl.cmiMarketData.NBBOStruct[] param3, com.cboe.idl.cmiMarketData.CurrentMarketStructV2[] param4, com.cboe.idl.cmiMarketData.CurrentMarketStruct[] param5, com.cboe.idl.cmiMarketData.CurrentMarketStruct[] param6, boolean[] param7)
    {
        boolean exception = false;
        if (acceptCurrentMarketsForClass2 != null)
        {
            acceptCurrentMarketsForClass2.beforeMethodCall();
        }
        try
        {
            delegate.acceptCurrentMarketsForClass(param0, param1, param2, param3, param4, param5, param6, param7);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptCurrentMarketsForClass2 != null)
            {
                acceptCurrentMarketsForClass2.incCalls(1);
                acceptCurrentMarketsForClass2.afterMethodCall();
                if (exception)
                {
                    acceptCurrentMarketsForClass2.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptExpectedOpeningPrice(int[] param0, com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct param1)
    {
        boolean exception = false;
        if (acceptExpectedOpeningPrice3 != null)
        {
            acceptExpectedOpeningPrice3.beforeMethodCall();
        }
        try
        {
            delegate.acceptExpectedOpeningPrice(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptExpectedOpeningPrice3 != null)
            {
                acceptExpectedOpeningPrice3.incCalls(1);
                acceptExpectedOpeningPrice3.afterMethodCall();
                if (exception)
                {
                    acceptExpectedOpeningPrice3.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptExpectedOpeningPricesForClass(com.cboe.idl.util.RoutingParameterStruct param0, com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct[] param1)
    {
        boolean exception = false;
        if (acceptExpectedOpeningPricesForClass4 != null)
        {
            acceptExpectedOpeningPricesForClass4.beforeMethodCall();
        }
        try
        {
            delegate.acceptExpectedOpeningPricesForClass(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptExpectedOpeningPricesForClass4 != null)
            {
                acceptExpectedOpeningPricesForClass4.incCalls(1);
                acceptExpectedOpeningPricesForClass4.afterMethodCall();
                if (exception)
                {
                    acceptExpectedOpeningPricesForClass4.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptNBBO(int[] param0, com.cboe.idl.cmiMarketData.NBBOStruct param1)
    {
        boolean exception = false;
        if (acceptNBBO5 != null)
        {
            acceptNBBO5.beforeMethodCall();
        }
        try
        {
            delegate.acceptNBBO(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptNBBO5 != null)
            {
                acceptNBBO5.incCalls(1);
                acceptNBBO5.afterMethodCall();
                if (exception)
                {
                    acceptNBBO5.incExceptions(1);
                }
            }
        }
    }
}
