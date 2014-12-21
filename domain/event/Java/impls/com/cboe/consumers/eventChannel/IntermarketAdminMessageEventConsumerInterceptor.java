package com.cboe.consumers.eventChannel;

import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.interfaces.events.IntermarketAdminMessageConsumer;

public class IntermarketAdminMessageEventConsumerInterceptor implements IntermarketAdminMessageConsumer
{
    private IntermarketAdminMessageConsumer delegate;

    MethodInstrumentor acceptIntermarketAdminMessage0;
    MethodInstrumentor acceptBroadcastIntermarketAdminMessage1;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public IntermarketAdminMessageEventConsumerInterceptor(Object bo)
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
            name.append("IntermarketAdminMessageEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptIntermarketAdminMessage0");
            acceptIntermarketAdminMessage0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptIntermarketAdminMessage0);
            acceptIntermarketAdminMessage0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("IntermarketAdminMessageEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptBroadcastIntermarketAdminMessage1");
            acceptBroadcastIntermarketAdminMessage1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptBroadcastIntermarketAdminMessage1);
            acceptBroadcastIntermarketAdminMessage1.setPrivate(privateOnly);
        } catch (InstrumentorAlreadyCreatedException ex)
        {
            Log.exception(ex);
        }
    }

    public void removeInstrumentation()
    {
        getMethodInstrumentorFactory().unregister(acceptIntermarketAdminMessage0);
        acceptIntermarketAdminMessage0 = null;
        getMethodInstrumentorFactory().unregister(acceptBroadcastIntermarketAdminMessage1);
        acceptBroadcastIntermarketAdminMessage1 = null;
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (IntermarketAdminMessageConsumer) delegate;
    }

    // Methods from IntermarketAdminMessageConsumer interface

    public void acceptIntermarketAdminMessage(String s, String s1, ProductKeysStruct productKeysStruct, AdminStruct adminStruct)
    {
        boolean exception = false;
        if (acceptIntermarketAdminMessage0 != null)
        {
            acceptIntermarketAdminMessage0.beforeMethodCall();
        }
        try
        {
            delegate.acceptIntermarketAdminMessage(s, s1, productKeysStruct, adminStruct);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptIntermarketAdminMessage0 != null)
            {
                acceptIntermarketAdminMessage0.incCalls(1);
                acceptIntermarketAdminMessage0.afterMethodCall();
                if (exception)
                {
                    acceptIntermarketAdminMessage0.incExceptions(1);
                }
            }
        }
    }

    public void acceptBroadcastIntermarketAdminMessage(String s, String s1, AdminStruct adminStruct)
    {
        boolean exception = false;
        if (acceptBroadcastIntermarketAdminMessage1 != null)
        {
            acceptBroadcastIntermarketAdminMessage1.beforeMethodCall();
        }
        try
        {
            delegate.acceptBroadcastIntermarketAdminMessage(s, s1, adminStruct);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptBroadcastIntermarketAdminMessage1 != null)
            {
                acceptBroadcastIntermarketAdminMessage1.incCalls(1);
                acceptBroadcastIntermarketAdminMessage1.afterMethodCall();
                if (exception)
                {
                    acceptBroadcastIntermarketAdminMessage1.incExceptions(1);
                }
            }
        }

    }
}