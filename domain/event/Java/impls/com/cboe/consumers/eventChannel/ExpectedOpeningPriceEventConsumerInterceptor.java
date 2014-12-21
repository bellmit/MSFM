package com.cboe.consumers.eventChannel;

import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.interfaces.events.CurrentMarketConsumer;
import com.cboe.interfaces.events.ExpectedOpeningPriceConsumer;


public class ExpectedOpeningPriceEventConsumerInterceptor
    implements ExpectedOpeningPriceConsumer
{
    
    MethodInstrumentor acceptExpectedOpeningPricesForClass4;
    MethodInstrumentor acceptExpectedOpeningPrice3;
    
    private ExpectedOpeningPriceConsumer delegate;
    private MethodInstrumentorFactory methodInstrumentorFactory;
    
    public ExpectedOpeningPriceEventConsumerInterceptor(ExpectedOpeningPriceConsumer delegate)
    {
        setDelegate(delegate);
    }

    public void acceptExpectedOpeningPrice(int[] arg0, ExpectedOpeningPriceStruct arg1)
    {
        boolean exception = false;
        if (acceptExpectedOpeningPrice3 != null)
        {
            acceptExpectedOpeningPrice3.beforeMethodCall();
        }
        try
        {
            delegate.acceptExpectedOpeningPrice(arg0, arg1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptExpectedOpeningPrice3 != null)
            {
                acceptExpectedOpeningPrice3.incCalls(1);
                acceptExpectedOpeningPrice3.afterMethodCall();
                if (exception)
                {
                    acceptExpectedOpeningPrice3.incExceptions(1);
                }
            }
        }
        
    }

    public void acceptExpectedOpeningPricesForClass(RoutingParameterStruct arg0, ExpectedOpeningPriceStruct[] arg1)
    {
        boolean exception = false;
        if (acceptExpectedOpeningPricesForClass4 != null)
        {
            acceptExpectedOpeningPricesForClass4.beforeMethodCall();
        }
        try
        {
            delegate.acceptExpectedOpeningPricesForClass(arg0, arg1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptExpectedOpeningPricesForClass4 != null)
            {
                acceptExpectedOpeningPricesForClass4.incCalls(1);
                acceptExpectedOpeningPricesForClass4.afterMethodCall();
                if (exception)
                {
                    acceptExpectedOpeningPricesForClass4.incExceptions(1);
                }
            }
        }
        
    }
    
    public void startInstrumentation(boolean privateOnly)
    {
        try
        {
            StringBuilder name = new StringBuilder(85);
            name.append("ExpectedOpeningPriceEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptExpectedOpeningPrice3");
            acceptExpectedOpeningPrice3 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptExpectedOpeningPrice3);
            acceptExpectedOpeningPrice3.setPrivate(privateOnly);
            name.setLength(0);
            name.append("ExpectedOpeningPriceEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptExpectedOpeningPricesForClass4");
            acceptExpectedOpeningPricesForClass4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptExpectedOpeningPricesForClass4);
            acceptExpectedOpeningPricesForClass4.setPrivate(privateOnly);
        } catch (InstrumentorAlreadyCreatedException ex)
        {
            Log.exception(ex);
        }
    }
    
    private void setDelegate(Object delegate)
    {
        this.delegate = (ExpectedOpeningPriceConsumer) delegate;
    }
    
    private MethodInstrumentorFactory getMethodInstrumentorFactory()
    {
        if (methodInstrumentorFactory == null)
        {
            methodInstrumentorFactory = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory();
        }
        return methodInstrumentorFactory;
    }
    
    public void removeInstrumentation()
    {
        getMethodInstrumentorFactory().unregister(acceptExpectedOpeningPrice3);
        acceptExpectedOpeningPrice3 = null;
        getMethodInstrumentorFactory().unregister(acceptExpectedOpeningPricesForClass4);
        acceptExpectedOpeningPricesForClass4 = null;
    }

    
    

   

}
