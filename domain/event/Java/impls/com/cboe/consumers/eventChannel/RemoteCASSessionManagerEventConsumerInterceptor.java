package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASSessionManagerConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RemoteCASSessionManagerEventConsumerInterceptor implements RemoteCASSessionManagerConsumer
{

    private RemoteCASSessionManagerConsumer delegate;

    MethodInstrumentor logout0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RemoteCASSessionManagerEventConsumerInterceptor(Object bo)
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
            name.append("RemoteCASSessionManagerEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("logout0");
            logout0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(logout0);
            logout0.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(logout0);
        logout0 = null;
    }

    /**
     */
    public void logout(java.lang.String param0, java.lang.String param1, java.lang.String param2)
    {
        boolean exception = false;
        if (logout0 != null)
        {
            logout0.beforeMethodCall();
        }
        try
        {
            delegate.logout(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (logout0 != null)
            {
                logout0.incCalls(1);
                logout0.afterMethodCall();
                if (exception)
                {
                    logout0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RemoteCASSessionManagerConsumer) delegate;
    }
}