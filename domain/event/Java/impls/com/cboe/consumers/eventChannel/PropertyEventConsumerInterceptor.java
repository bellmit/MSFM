package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.PropertyConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class PropertyEventConsumerInterceptor implements PropertyConsumer
{


    MethodInstrumentor acceptPropertyUpdate1;

    private PropertyConsumer delegate;

    MethodInstrumentor acceptPropertyRemove0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public PropertyEventConsumerInterceptor(Object bo)
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
            name.append("PropertyEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptPropertyRemove0");
            acceptPropertyRemove0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptPropertyRemove0);
            acceptPropertyRemove0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("PropertyEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptPropertyUpdate1");
            acceptPropertyUpdate1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptPropertyUpdate1);
            acceptPropertyUpdate1.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptPropertyRemove0);
        acceptPropertyRemove0 = null;
        getMethodInstrumentorFactory().unregister(acceptPropertyUpdate1);
        acceptPropertyUpdate1 = null;
    }

    /**
     */
    public void acceptPropertyRemove(java.lang.String param0, java.lang.String param1)
    {
        boolean exception = false;
        if (acceptPropertyRemove0 != null)
        {
            acceptPropertyRemove0.beforeMethodCall();
        }
        try
        {
            delegate.acceptPropertyRemove(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptPropertyRemove0 != null)
            {
                acceptPropertyRemove0.incCalls(1);
                acceptPropertyRemove0.afterMethodCall();
                if (exception)
                {
                    acceptPropertyRemove0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (PropertyConsumer) delegate;
    }

    /**
     */
    public void acceptPropertyUpdate(com.cboe.idl.property.PropertyGroupStruct param0)
    {
        boolean exception = false;
        if (acceptPropertyUpdate1 != null)
        {
            acceptPropertyUpdate1.beforeMethodCall();
        }
        try
        {
            delegate.acceptPropertyUpdate(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptPropertyUpdate1 != null)
            {
                acceptPropertyUpdate1.incCalls(1);
                acceptPropertyUpdate1.afterMethodCall();
                if (exception)
                {
                    acceptPropertyUpdate1.incExceptions(1);
                }
            }
        }
    }
}