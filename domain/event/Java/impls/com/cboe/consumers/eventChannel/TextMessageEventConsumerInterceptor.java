package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.TextMessageConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class TextMessageEventConsumerInterceptor implements TextMessageConsumer
{


    MethodInstrumentor acceptTextMessageForUser1;

    private TextMessageConsumer delegate;

    MethodInstrumentor acceptTextMessageForProductClass0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public TextMessageEventConsumerInterceptor(Object bo)
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
            StringBuilder name = new StringBuilder(75);
            name.append("TextMessageEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptTextMessageForProductClass0");
            acceptTextMessageForProductClass0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptTextMessageForProductClass0);
            acceptTextMessageForProductClass0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("TextMessageEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptTextMessageForUser1");
            acceptTextMessageForUser1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptTextMessageForUser1);
            acceptTextMessageForUser1.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptTextMessageForProductClass0);
        acceptTextMessageForProductClass0 = null;
        getMethodInstrumentorFactory().unregister(acceptTextMessageForUser1);
        acceptTextMessageForUser1 = null;
    }

    /**
     */
    public void acceptTextMessageForProductClass(short param0, int param1, com.cboe.idl.textMessage.MessageTransportStruct param2)
    {
        boolean exception = false;
        if (acceptTextMessageForProductClass0 != null)
        {
            acceptTextMessageForProductClass0.beforeMethodCall();
        }
        try
        {
            delegate.acceptTextMessageForProductClass(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptTextMessageForProductClass0 != null)
            {
                acceptTextMessageForProductClass0.incCalls(1);
                acceptTextMessageForProductClass0.afterMethodCall();
                if (exception)
                {
                    acceptTextMessageForProductClass0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (TextMessageConsumer) delegate;
    }

    /**
     */
    public void acceptTextMessageForUser(java.lang.String param0, com.cboe.idl.textMessage.MessageTransportStruct param1)
    {
        boolean exception = false;
        if (acceptTextMessageForUser1 != null)
        {
            acceptTextMessageForUser1.beforeMethodCall();
        }
        try
        {
            delegate.acceptTextMessageForUser(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptTextMessageForUser1 != null)
            {
                acceptTextMessageForUser1.incCalls(1);
                acceptTextMessageForUser1.afterMethodCall();
                if (exception)
                {
                    acceptTextMessageForUser1.incExceptions(1);
                }
            }
        }
    }
}