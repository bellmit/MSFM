package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.GroupElementConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;


public class GroupElementEventConsumerInterceptor implements GroupElementConsumer
{

    MethodInstrumentor acceptRemoveElement2;
    MethodInstrumentor acceptAddElement1;
    MethodInstrumentor acceptUpdateElement0;

    private GroupElementConsumer delegate;

    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public GroupElementEventConsumerInterceptor(Object bo)
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
            name.append("GroupElementEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptUpdateElement0");
            acceptUpdateElement0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptUpdateElement0);
            acceptUpdateElement0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("GroupElementEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptAddElement1");
            acceptAddElement1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptAddElement1);
            acceptAddElement1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("GroupElementEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptRemoveElement2");
            acceptRemoveElement2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptRemoveElement2);
            acceptRemoveElement2.setPrivate(privateOnly);

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
        getMethodInstrumentorFactory().unregister(acceptUpdateElement0);
        acceptUpdateElement0 = null;
        getMethodInstrumentorFactory().unregister(acceptAddElement1);
        acceptAddElement1 = null;
        getMethodInstrumentorFactory().unregister(acceptRemoveElement2);
        acceptRemoveElement2 = null;

    }

    /**
     */
    public void acceptUpdateElement(com.cboe.idl.groupElement.ElementStruct param0)
    {
        boolean exception = false;
        if (acceptUpdateElement0 != null)
        {
            acceptUpdateElement0.beforeMethodCall();
        }
        try
        {
            delegate.acceptUpdateElement(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptUpdateElement0 != null)
            {
                acceptUpdateElement0.incCalls(1);
                acceptUpdateElement0.afterMethodCall();
                if (exception)
                {
                    acceptUpdateElement0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (GroupElementConsumer) delegate;
    }

    /**
     */
    public void acceptAddElement(long param0 ,
								 com.cboe.idl.groupElement.ElementStruct param1)
    {
        boolean exception = false;
        if (acceptAddElement1 != null)
        {
            acceptAddElement1.beforeMethodCall();
        }
        try
        {
            delegate.acceptAddElement(param0,param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptAddElement1 != null)
            {
                acceptAddElement1.incCalls(1);
                acceptAddElement1.afterMethodCall();
                if (exception)
                {
                    acceptAddElement1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptRemoveElement(long param0 ,
											   com.cboe.idl.groupElement.ElementStruct param1,
											   boolean param2)
    {
        boolean exception = false;
        if (acceptRemoveElement2 != null)
        {
            acceptRemoveElement2.beforeMethodCall();
        }
        try
        {
            delegate.acceptRemoveElement(param0, param1, param2);
        } 
		catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } 
		finally
        {
            if (acceptRemoveElement2 != null)
            {
                acceptRemoveElement2.incCalls(1);
                acceptRemoveElement2.afterMethodCall();
                if (exception)
                {
                    acceptRemoveElement2.incExceptions(1);
                }
            }
        }
    }

}
