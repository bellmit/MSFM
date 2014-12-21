package com.cboe.consumers.eventChannel;

import com.cboe.idl.cmiIntermarketMessages.AlertStruct;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.interfaces.events.AlertConsumer;

public class AlertEventConsumerInterceptor implements AlertConsumer
{
    private AlertConsumer delegate;

    MethodInstrumentor acceptAlert0;
    MethodInstrumentor acceptAlertUpdate1;
    MethodInstrumentor acceptSatisfactionAlert2;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public AlertEventConsumerInterceptor(Object bo)
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
            name.append("AlertEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptAlert0");
            acceptAlert0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptAlert0);
            acceptAlert0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("AlertEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptAlertUpdate1");
            acceptAlertUpdate1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptAlertUpdate1);
            acceptAlertUpdate1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("AlertEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptSatisfactionAlert2");
            acceptSatisfactionAlert2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptSatisfactionAlert2);
            acceptSatisfactionAlert2.setPrivate(privateOnly);
        } catch (InstrumentorAlreadyCreatedException ex)
        {
            Log.exception(ex);
        }
    }

    public void removeInstrumentation()
    {
        getMethodInstrumentorFactory().unregister(acceptAlert0);
        acceptAlert0 = null;
        getMethodInstrumentorFactory().unregister(acceptAlertUpdate1);
        acceptAlertUpdate1 = null;
        getMethodInstrumentorFactory().unregister(acceptSatisfactionAlert2);
        acceptSatisfactionAlert2 = null;
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (AlertConsumer) delegate;
    }

    // Methods from AlertConsumer interface

    public void acceptAlert(AlertStruct alertStruct)
    {
        boolean exception = false;
        if (acceptAlert0 != null)
        {
            acceptAlert0.beforeMethodCall();
        }
        try
        {
            delegate.acceptAlert(alertStruct);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptAlert0 != null)
            {
                acceptAlert0.incCalls(1);
                acceptAlert0.afterMethodCall();
                if (exception)
                {
                    acceptAlert0.incExceptions(1);
                }
            }
        }
    }

    public void acceptAlertUpdate(AlertStruct alertStruct)
    {
        boolean exception = false;
        if (acceptAlertUpdate1 != null)
        {
            acceptAlertUpdate1.beforeMethodCall();
        }
        try
        {
            delegate.acceptAlertUpdate(alertStruct);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptAlertUpdate1 != null)
            {
                acceptAlertUpdate1.incCalls(1);
                acceptAlertUpdate1.afterMethodCall();
                if (exception)
                {
                    acceptAlertUpdate1.incExceptions(1);
                }
            }
        }
    }

    public void acceptSatisfactionAlert(SatisfactionAlertStruct satisfactionAlertStruct)
    {
        boolean exception = false;
        if (acceptSatisfactionAlert2 != null)
        {
            acceptSatisfactionAlert2.beforeMethodCall();
        }
        try
        {
            delegate.acceptSatisfactionAlert(satisfactionAlertStruct);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptSatisfactionAlert2 != null)
            {
                acceptSatisfactionAlert2.incCalls(1);
                acceptSatisfactionAlert2.afterMethodCall();
                if (exception)
                {
                    acceptSatisfactionAlert2.incExceptions(1);
                }
            }
        }
    }

}
