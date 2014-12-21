package com.cboe.application.eventChannel;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.interfaces.events.MarketBufferConsumer;

public class MarketBufferEventConsumerInterceptor implements MarketBufferConsumer
{
    private MarketBufferConsumer delegate;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    MethodInstrumentor acceptMarketBuffer0;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public MarketBufferEventConsumerInterceptor(Object bo)
    {
        setDelegate(bo);
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (MarketBufferConsumer) delegate;
    }

    private MethodInstrumentorFactory getMethodInstrumentorFactory()
    {
        if (methodInstrumentorFactory == null)
        {
            methodInstrumentorFactory = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory();
        }
        return methodInstrumentorFactory;
    }

    public void startInstrumentation(boolean privateOnly, int channelNumber)
    {
        try
        {
            acceptMarketBuffer0 = getMethodInstrumentorFactory().create("MarketBufferEventConsumerInterceptor" + channelNumber + Instrumentor.NAME_DELIMITER + "acceptMarketBuffer0", null);
            getMethodInstrumentorFactory().register(acceptMarketBuffer0);
            acceptMarketBuffer0.setPrivate(privateOnly);
        } catch (InstrumentorAlreadyCreatedException ex)
        {
            Log.exception(ex);
        }
    }

    public void removeInstrumentation()
    {
        getMethodInstrumentorFactory().unregister(acceptMarketBuffer0);
        acceptMarketBuffer0 = null;
    }

    public void acceptMarketBuffer(int serverKey, int mdcassetKey, byte[] buffer)
    {
        boolean exception = false;
        if (acceptMarketBuffer0 != null)
        {
            acceptMarketBuffer0.beforeMethodCall();
        }
        try
        {
            delegate.acceptMarketBuffer(serverKey, mdcassetKey, buffer);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptMarketBuffer0 != null)
            {
                acceptMarketBuffer0.incCalls(1);
                acceptMarketBuffer0.afterMethodCall();
                if (exception)
                {
                    acceptMarketBuffer0.incExceptions(1);
                }
            }
        }
    }

}
