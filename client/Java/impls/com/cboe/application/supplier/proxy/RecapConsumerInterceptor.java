package com.cboe.application.supplier.proxy;

import com.cboe.domain.instrumentorExtension.MethodInstrumentorExtension;
import com.cboe.domain.instrumentorExtension.MethodInstrumentorExtensionFactory;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtension;
import com.cboe.domain.supplier.proxy.CallbackInterceptor;
import com.cboe.idl.cmiCallback.CMIRecapConsumer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.interfaces.callback.RecapConsumer;

/**
 * Generated by Java Grinder
 * @author Java Grinder
 */
public class RecapConsumerInterceptor extends CallbackInterceptor implements RecapConsumer
{

    MethodInstrumentorExtension acceptRecap0;
    CMIRecapConsumer cmiObject;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public RecapConsumerInterceptor(CMIRecapConsumer o)
    {
        this.cmiObject = o;
    }

    /**
     */
    public void startInstrumentation(String prefix, boolean privateOnly)
    {
        try
        {
            StringBuilder name = new StringBuilder(prefix.length()+Instrumentor.NAME_DELIMITER.length()+12);
            name.append(prefix).append(Instrumentor.NAME_DELIMITER).append("acceptRecap0");
            acceptRecap0 = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
        } catch (InstrumentorAlreadyCreatedException e)
        {
            Log.exception(e);
        }
    }

    /**
     *
     */
    public void removeInstrumentation()
    {
        if(acceptRecap0 != null)
        {
            MethodInstrumentorExtensionFactory.removeMethodInstrumentor(acceptRecap0.getName());
            acceptRecap0 = null;
        }
    }

    /**
     *
     */
    public void addQueueInstrumentorRelation(QueueInstrumentorExtension queueInstrumentorExtension)
    {
        if(acceptRecap0 != null)
        {
            acceptRecap0.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
    }

    /**
     */
    public void acceptRecap(com.cboe.idl.cmiMarketData.RecapStruct[] param0)
    {
        boolean exception = false;
        if (acceptRecap0 != null)
        {
            acceptRecap0.beforeMethodCall();
        }
        try
        {
            cmiObject.acceptRecap(param0);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptRecap0 != null)
            {
                acceptRecap0.incCalls(1);
                acceptRecap0.afterMethodCall();
                if (exception)
                {
                    acceptRecap0.incExceptions(1);
                }
            }
        }
    }
}