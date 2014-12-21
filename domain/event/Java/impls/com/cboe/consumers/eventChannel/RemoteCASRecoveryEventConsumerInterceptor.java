package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASRecoveryConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RemoteCASRecoveryEventConsumerInterceptor implements RemoteCASRecoveryConsumer
{

    private RemoteCASRecoveryConsumer delegate;

    MethodInstrumentor acceptMarketDataRecoveryForGroup0;
    MethodInstrumentor acceptMDXRecoveryForGroup0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RemoteCASRecoveryEventConsumerInterceptor(Object bo)
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
            name.append("RemoteCASRecoveryEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptMarketDataRecoveryForGroup0");
            acceptMarketDataRecoveryForGroup0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptMarketDataRecoveryForGroup0);
            acceptMarketDataRecoveryForGroup0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("RemoteCASRecoveryEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptMDXRecoveryForGroup0");
            acceptMDXRecoveryForGroup0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptMDXRecoveryForGroup0);
            acceptMDXRecoveryForGroup0.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptMarketDataRecoveryForGroup0);
        acceptMarketDataRecoveryForGroup0 = null;

        getMethodInstrumentorFactory().unregister(acceptMDXRecoveryForGroup0);
        acceptMDXRecoveryForGroup0 = null;
    }

    /**
     */
    public void acceptMarketDataRecoveryForGroup(int param0)
    {
        boolean exception = false;
        if (acceptMarketDataRecoveryForGroup0 != null)
        {
            acceptMarketDataRecoveryForGroup0.beforeMethodCall();
        }
        try
        {
            delegate.acceptMarketDataRecoveryForGroup(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptMarketDataRecoveryForGroup0 != null)
            {
                acceptMarketDataRecoveryForGroup0.incCalls(1);
                acceptMarketDataRecoveryForGroup0.afterMethodCall();
                if (exception)
                {
                    acceptMarketDataRecoveryForGroup0.incExceptions(1);
                }
            }
        }
    }

    public void acceptMDXRecoveryForGroup(int mdxGroupKey)
    {
        boolean exception = false;
        if(acceptMDXRecoveryForGroup0 != null)
        {
            acceptMDXRecoveryForGroup0.beforeMethodCall();
        }
        try
        {
            delegate.acceptMDXRecoveryForGroup(mdxGroupKey);
        }
        catch(RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if(acceptMDXRecoveryForGroup0 != null)
            {
                acceptMDXRecoveryForGroup0.incCalls(1);
                acceptMDXRecoveryForGroup0.afterMethodCall();
                if(exception)
                {
                    acceptMDXRecoveryForGroup0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RemoteCASRecoveryConsumer) delegate;
    }
}