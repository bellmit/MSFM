package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RFQConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RFQEventConsumerInterceptor implements RFQConsumer
{

    private RFQConsumer delegate;

    MethodInstrumentor acceptRFQ0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RFQEventConsumerInterceptor(Object bo)
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
            StringBuilder name = new StringBuilder(40);
            name.append("RFQEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptRFQ0");
            acceptRFQ0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptRFQ0);
            acceptRFQ0.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptRFQ0);
        acceptRFQ0 = null;
    }

    /**
     */
    public void acceptRFQ(com.cboe.idl.cmiQuote.RFQStruct param0)
    {
        boolean exception = false;
        if (acceptRFQ0 != null)
        {
            acceptRFQ0.beforeMethodCall();
        }
        try
        {
            delegate.acceptRFQ(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptRFQ0 != null)
            {
                acceptRFQ0.incCalls(1);
                acceptRFQ0.afterMethodCall();
                if (exception)
                {
                    acceptRFQ0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RFQConsumer) delegate;
    }
}