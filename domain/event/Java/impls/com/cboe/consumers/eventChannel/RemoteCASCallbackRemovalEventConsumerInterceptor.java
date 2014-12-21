package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASCallbackRemovalConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class RemoteCASCallbackRemovalEventConsumerInterceptor implements RemoteCASCallbackRemovalConsumer
{

    private RemoteCASCallbackRemovalConsumer delegate;

    MethodInstrumentor acceptCallbackRemoval0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RemoteCASCallbackRemovalEventConsumerInterceptor(Object bo)
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
            name.append("RemoteCASCallbackRemovalEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptCallbackRemoval0");
            acceptCallbackRemoval0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptCallbackRemoval0);
            acceptCallbackRemoval0.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptCallbackRemoval0);
        acceptCallbackRemoval0 = null;
    }

    /**
     */
    public void acceptCallbackRemoval(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, int param4, com.cboe.idl.cmiUtil.CallbackInformationStruct param5)
    {
        boolean exception = false;
        if (acceptCallbackRemoval0 != null)
        {
            acceptCallbackRemoval0.beforeMethodCall();
        }
        try
        {
            delegate.acceptCallbackRemoval(param0, param1, param2, param3, param4, param5);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptCallbackRemoval0 != null)
            {
                acceptCallbackRemoval0.incCalls(1);
                acceptCallbackRemoval0.afterMethodCall();
                if (exception)
                {
                    acceptCallbackRemoval0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (RemoteCASCallbackRemovalConsumer) delegate;
    }
}