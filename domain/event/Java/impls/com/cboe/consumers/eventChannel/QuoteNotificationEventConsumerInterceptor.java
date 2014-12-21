package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.QuoteNotificationConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class QuoteNotificationEventConsumerInterceptor implements QuoteNotificationConsumer
{

    private QuoteNotificationConsumer delegate;

    MethodInstrumentor acceptQuoteLockedNotification0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public QuoteNotificationEventConsumerInterceptor(Object bo)
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
            name.append("QuoteNotificationEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptQuoteLockedNotification0");
            acceptQuoteLockedNotification0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptQuoteLockedNotification0);
            acceptQuoteLockedNotification0.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptQuoteLockedNotification0);
        acceptQuoteLockedNotification0 = null;
    }

    /**
     */
    public void acceptQuoteLockedNotification(int[] param0, com.cboe.idl.cmiQuote.LockNotificationStruct param1)
    {
        boolean exception = false;
        if (acceptQuoteLockedNotification0 != null)
        {
            acceptQuoteLockedNotification0.beforeMethodCall();
        }
        try
        {
            delegate.acceptQuoteLockedNotification(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptQuoteLockedNotification0 != null)
            {
                acceptQuoteLockedNotification0.incCalls(1);
                acceptQuoteLockedNotification0.afterMethodCall();
                if (exception)
                {
                    acceptQuoteLockedNotification0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (QuoteNotificationConsumer) delegate;
    }
}