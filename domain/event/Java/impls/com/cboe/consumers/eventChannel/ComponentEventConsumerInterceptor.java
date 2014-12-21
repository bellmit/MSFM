package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.ComponentConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class ComponentEventConsumerInterceptor implements ComponentConsumer
{


    MethodInstrumentor allRegisteredComponentsNotMaster4;


    MethodInstrumentor allRegisteredComponentsFailed3;


    MethodInstrumentor acceptComponentIsMaster2;


    MethodInstrumentor acceptComponentFailed1;

    private ComponentConsumer delegate;

    MethodInstrumentor acceptComponentEstablished0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public ComponentEventConsumerInterceptor(Object bo)
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
            StringBuilder name = new StringBuilder(70);
            name.append("ComponentEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptComponentEstablished0");
            acceptComponentEstablished0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptComponentEstablished0);
            acceptComponentEstablished0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("ComponentEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptComponentFailed1");
            acceptComponentFailed1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptComponentFailed1);
            acceptComponentFailed1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("ComponentEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptComponentIsMaster2");
            acceptComponentIsMaster2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptComponentIsMaster2);
            acceptComponentIsMaster2.setPrivate(privateOnly);
            name.setLength(0);
            name.append("ComponentEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("allRegisteredComponentsFailed3");
            allRegisteredComponentsFailed3 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(allRegisteredComponentsFailed3);
            allRegisteredComponentsFailed3.setPrivate(privateOnly);
            name.setLength(0);
            name.append("ComponentEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("allRegisteredComponentsNotMaster4");
            allRegisteredComponentsNotMaster4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(allRegisteredComponentsNotMaster4);
            allRegisteredComponentsNotMaster4.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptComponentEstablished0);
        acceptComponentEstablished0 = null;
        getMethodInstrumentorFactory().unregister(acceptComponentFailed1);
        acceptComponentFailed1 = null;
        getMethodInstrumentorFactory().unregister(acceptComponentIsMaster2);
        acceptComponentIsMaster2 = null;
        getMethodInstrumentorFactory().unregister(allRegisteredComponentsFailed3);
        allRegisteredComponentsFailed3 = null;
        getMethodInstrumentorFactory().unregister(allRegisteredComponentsNotMaster4);
        allRegisteredComponentsNotMaster4 = null;
    }

    /**
     */
    public void acceptComponentEstablished(java.lang.String param0)
    {
        boolean exception = false;
        if (acceptComponentEstablished0 != null)
        {
            acceptComponentEstablished0.beforeMethodCall();
        }
        try
        {
            delegate.acceptComponentEstablished(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptComponentEstablished0 != null)
            {
                acceptComponentEstablished0.incCalls(1);
                acceptComponentEstablished0.afterMethodCall();
                if (exception)
                {
                    acceptComponentEstablished0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (ComponentConsumer) delegate;
    }

    /**
     */
    public void acceptComponentFailed(java.lang.String param0)
    {
        boolean exception = false;
        if (acceptComponentFailed1 != null)
        {
            acceptComponentFailed1.beforeMethodCall();
        }
        try
        {
            delegate.acceptComponentFailed(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptComponentFailed1 != null)
            {
                acceptComponentFailed1.incCalls(1);
                acceptComponentFailed1.afterMethodCall();
                if (exception)
                {
                    acceptComponentFailed1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptComponentIsMaster(java.lang.String param0, boolean param1)
    {
        boolean exception = false;
        if (acceptComponentIsMaster2 != null)
        {
            acceptComponentIsMaster2.beforeMethodCall();
        }
        try
        {
            delegate.acceptComponentIsMaster(param0, param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptComponentIsMaster2 != null)
            {
                acceptComponentIsMaster2.incCalls(1);
                acceptComponentIsMaster2.afterMethodCall();
                if (exception)
                {
                    acceptComponentIsMaster2.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void allRegisteredComponentsFailed()
    {
        boolean exception = false;
        if (allRegisteredComponentsFailed3 != null)
        {
            allRegisteredComponentsFailed3.beforeMethodCall();
        }
        try
        {
            delegate.allRegisteredComponentsFailed();
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (allRegisteredComponentsFailed3 != null)
            {
                allRegisteredComponentsFailed3.incCalls(1);
                allRegisteredComponentsFailed3.afterMethodCall();
                if (exception)
                {
                    allRegisteredComponentsFailed3.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void allRegisteredComponentsNotMaster()
    {
        boolean exception = false;
        if (allRegisteredComponentsNotMaster4 != null)
        {
            allRegisteredComponentsNotMaster4.beforeMethodCall();
        }
        try
        {
            delegate.allRegisteredComponentsNotMaster();
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (allRegisteredComponentsNotMaster4 != null)
            {
                allRegisteredComponentsNotMaster4.incCalls(1);
                allRegisteredComponentsNotMaster4.afterMethodCall();
                if (exception)
                {
                    allRegisteredComponentsNotMaster4.incExceptions(1);
                }
            }
        }
    }

    public void acceptComponentAdded(String componentName, int componentType, String parentComponentName, int currentState)
    {
        //Do nothing for now.
    }

    public void acceptComponentRemoved(String componentName, String[] parentComponents)
    {
        //Do nothing for now.
    }
}