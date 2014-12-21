package com.cboe.application.supplier.proxy;

import com.cboe.domain.instrumentorExtension.MethodInstrumentorExtension;
import com.cboe.domain.instrumentorExtension.MethodInstrumentorExtensionFactory;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtension;
import com.cboe.domain.supplier.proxy.CallbackInterceptor;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.interfaces.callback.LargeTradeLastSaleConsumer;

public class LargeTradeLastSaleConsumerInterceptor
			extends CallbackInterceptor 
			implements LargeTradeLastSaleConsumer {
	
	MethodInstrumentorExtension acceptLargeTradeTickerDetailForClass0;
    TickerConsumer corbaObj;

	public LargeTradeLastSaleConsumerInterceptor(TickerConsumer lsConsumer) {
		corbaObj = lsConsumer;
	}

	@Override
	public void startInstrumentation(String prefix, boolean privateOnly) {
		try
        {
            StringBuilder name = new StringBuilder(prefix.length()+Instrumentor.NAME_DELIMITER.length()+40);
            name.append(prefix).append(Instrumentor.NAME_DELIMITER).append("acceptLargeTradeTickerDetailForClass0");
            acceptLargeTradeTickerDetailForClass0 = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
        } catch (InstrumentorAlreadyCreatedException e)
        {
            Log.exception(e);
        }	
	}

	@Override
	public void removeInstrumentation() {
		if(acceptLargeTradeTickerDetailForClass0 != null)
        {
            MethodInstrumentorExtensionFactory.removeMethodInstrumentor(acceptLargeTradeTickerDetailForClass0.getName());
            acceptLargeTradeTickerDetailForClass0 = null;
        }
		
	}

	@Override
	public void addQueueInstrumentorRelation(QueueInstrumentorExtension queueInstrumentorExtension) {
		if(acceptLargeTradeTickerDetailForClass0 != null)
        {
			acceptLargeTradeTickerDetailForClass0.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
	}

	public void acceptTicker(int[] arg0, InternalTickerStruct arg1) {
		// do nothing
	}

	public void acceptTickerForClass(RoutingParameterStruct arg0, TimeStruct[] arg1, TickerStruct[] arg2) {
		// do nothing
	}

	public void acceptLargeTradeTickerDetailForClass(RoutingParameterStruct routingParameters, InternalTickerDetailStruct[] tickerDetails) {
		boolean exception = false;
        if (acceptLargeTradeTickerDetailForClass0 != null)
        {
        	acceptLargeTradeTickerDetailForClass0.beforeMethodCall();
        }
        try
        {
        	corbaObj.acceptLargeTradeTickerDetailForClass(routingParameters, tickerDetails);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptLargeTradeTickerDetailForClass0 != null)
            {
            	acceptLargeTradeTickerDetailForClass0.incCalls(1);
            	acceptLargeTradeTickerDetailForClass0.afterMethodCall();
                if (exception)
                {
                	acceptLargeTradeTickerDetailForClass0.incExceptions(1);
                }
            }
        }
	}

}
