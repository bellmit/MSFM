package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.AuctionConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.cmiOrder.AuctionStruct;

public class AuctionEventConsumerInterceptor implements AuctionConsumer
{

    private AuctionConsumer delegate;

    MethodInstrumentor acceptAuction0;
    MethodInstrumentor acceptDirectedAIMAuction0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public AuctionEventConsumerInterceptor(Object bo)
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
        StringBuilder name = new StringBuilder(60);
        try
        {
            name.append("AuctionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptAuction0");
            acceptAuction0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptAuction0);
            acceptAuction0.setPrivate(privateOnly);

            name.setLength(0);
            name.append("AuctionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptDirectedAIMAuction0");
            acceptDirectedAIMAuction0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptDirectedAIMAuction0);
            acceptDirectedAIMAuction0.setPrivate(privateOnly);
        } catch (InstrumentorAlreadyCreatedException ex)
        {
            Log.exception(ex);
        }
    }

    public void acceptAuction(RoutingParameterStruct routingParameterStruct, int[] ints, AuctionStruct auctionStruct)
    {
        boolean exception = false;
        if (acceptAuction0 != null)
        {
            acceptAuction0.beforeMethodCall();
        }
        try
        {
            delegate.acceptAuction(routingParameterStruct, ints, auctionStruct);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptAuction0 != null)
            {
                acceptAuction0.incCalls(1);
                acceptAuction0.afterMethodCall();
                if (exception)
                {
                    acceptAuction0.incExceptions(1);
                }
            }
        }
    }
    
    public void acceptDirectedAIMAuction(RoutingParameterStruct routingParameterStruct, int[] ints, AuctionStruct auctionStruct)
    {
        boolean exception = false;
        if (acceptDirectedAIMAuction0 != null)
        {
            acceptDirectedAIMAuction0.beforeMethodCall();
        }
        try
        {
            delegate.acceptDirectedAIMAuction(routingParameterStruct, ints, auctionStruct);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptDirectedAIMAuction0 != null)
            {
                acceptDirectedAIMAuction0.incCalls(1);
                acceptDirectedAIMAuction0.afterMethodCall();
                if (exception)
                {
                    acceptDirectedAIMAuction0.incExceptions(1);
                }
            }
        }
    }
    /**
     *
     */
    public void removeInstrumentation()
    {
        getMethodInstrumentorFactory().unregister(acceptAuction0);
        acceptAuction0 = null;
        
        getMethodInstrumentorFactory().unregister(acceptDirectedAIMAuction0);
        acceptDirectedAIMAuction0 = null;
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (AuctionConsumer) delegate;
    }

}