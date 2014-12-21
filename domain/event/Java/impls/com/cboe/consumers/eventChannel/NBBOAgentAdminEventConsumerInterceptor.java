package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.NBBOAgentAdminConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class NBBOAgentAdminEventConsumerInterceptor implements NBBOAgentAdminConsumer
{


    MethodInstrumentor acceptReminder1;

    private NBBOAgentAdminConsumer delegate;

    MethodInstrumentor acceptForcedTakeOver0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public NBBOAgentAdminEventConsumerInterceptor(Object bo)
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
            name.append("NBBOAgentAdminEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptForcedTakeOver0");
            acceptForcedTakeOver0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptForcedTakeOver0);
            acceptForcedTakeOver0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("NBBOAgentAdminEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptReminder1");
            acceptReminder1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptReminder1);
            acceptReminder1.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptForcedTakeOver0);
        acceptForcedTakeOver0 = null;
        getMethodInstrumentorFactory().unregister(acceptReminder1);
        acceptReminder1 = null;
    }

    /**
     */
    public void acceptForcedTakeOver(java.lang.String param0, java.lang.String param1, int param2, java.lang.String param3)
    {
        boolean exception = false;
        if (acceptForcedTakeOver0 != null)
        {
            acceptForcedTakeOver0.beforeMethodCall();
        }
        try
        {
            delegate.acceptForcedTakeOver(param0, param1, param2, param3);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptForcedTakeOver0 != null)
            {
                acceptForcedTakeOver0.incCalls(1);
                acceptForcedTakeOver0.afterMethodCall();
                if (exception)
                {
                    acceptForcedTakeOver0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (NBBOAgentAdminConsumer) delegate;
    }

    /**
     */
    public void acceptReminder(java.lang.String param0, java.lang.String param1, int param2, com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct param3)
    {
        boolean exception = false;
        if (acceptReminder1 != null)
        {
            acceptReminder1.beforeMethodCall();
        }
        try
        {
            delegate.acceptReminder(param0, param1, param2, param3);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptReminder1 != null)
            {
                acceptReminder1.incCalls(1);
                acceptReminder1.afterMethodCall();
                if (exception)
                {
                    acceptReminder1.incExceptions(1);
                }
            }
        }
    }
}