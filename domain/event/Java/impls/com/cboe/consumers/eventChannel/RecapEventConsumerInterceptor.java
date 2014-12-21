package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RecapConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RecapEventConsumerInterceptor implements RecapConsumer
{


    MethodInstrumentor acceptRecapForClass1;

    private RecapConsumer delegate;

    MethodInstrumentor acceptRecap0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RecapEventConsumerInterceptor(Object bo)
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
            name.append("RecapEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptRecap0");
            acceptRecap0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptRecap0);
            acceptRecap0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RecapEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptRecapForClass1");
            acceptRecapForClass1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptRecapForClass1);
            acceptRecapForClass1.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptRecap0);
        acceptRecap0 = null;
        getMethodInstrumentorFactory().unregister(acceptRecapForClass1);
        acceptRecapForClass1 = null;
    }

    /**
     */
    public void acceptRecap(int[] param0, com.cboe.idl.cmiMarketData.RecapStruct param1)
    {
        boolean exception = false;
        if (acceptRecap0 != null)
        {
            acceptRecap0.beforeMethodCall();
        }
        try
        {
            delegate.acceptRecap(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptRecap0 != null)
            {
                acceptRecap0.incCalls(1);
                acceptRecap0.afterMethodCall();
                if (exception)
                {
                    acceptRecap0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RecapConsumer) delegate;
    }

    /**
     */
    public void acceptRecapForClass(com.cboe.idl.util.RoutingParameterStruct param0, com.cboe.idl.cmiMarketData.RecapStruct[] param1)
    {
        boolean exception = false;
        if (acceptRecapForClass1 != null)
        {
            acceptRecapForClass1.beforeMethodCall();
        }
        try
        {
            delegate.acceptRecapForClass(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptRecapForClass1 != null)
            {
                acceptRecapForClass1.incCalls(1);
                acceptRecapForClass1.afterMethodCall();
                if (exception)
                {
                    acceptRecapForClass1.incExceptions(1);
                }
            }
        }
    }
}