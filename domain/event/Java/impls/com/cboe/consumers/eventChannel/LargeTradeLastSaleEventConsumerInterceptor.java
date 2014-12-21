package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.TickerConsumer;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class LargeTradeLastSaleEventConsumerInterceptor implements TickerConsumer {

    private TickerConsumer delegate;
    private MethodInstrumentorFactory methodInstrumentorFactory;
    MethodInstrumentor acceptLargeTradeTickerDetailForClass3;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public LargeTradeLastSaleEventConsumerInterceptor(Object bo) {
        setDelegate(bo);
    }

    private void setDelegate(Object bo) {
        this.delegate = (TickerConsumer)bo;
    }


    private MethodInstrumentorFactory getMethodInstrumentorFactory() {
        if (methodInstrumentorFactory == null) {
            methodInstrumentorFactory = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory();
        }
        return methodInstrumentorFactory;
    }

    public void startInstrumentation(boolean privateOnly) {
        try {
            StringBuilder name = new StringBuilder(85);
            name.append("LargeTradeLastSaleEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptLargeTradeTickerDetailForClass3");
            acceptLargeTradeTickerDetailForClass3 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptLargeTradeTickerDetailForClass3);
            acceptLargeTradeTickerDetailForClass3.setPrivate(privateOnly);
        } catch (InstrumentorAlreadyCreatedException ex) {
            Log.exception(ex);
        }
    }

    public void removeInstrumentation() {
        getMethodInstrumentorFactory().unregister(acceptLargeTradeTickerDetailForClass3);
        acceptLargeTradeTickerDetailForClass3 = null;
    }

    public void acceptTicker(int[] ints, InternalTickerStruct internalTickerStruct) {
    }

    public void acceptTickerForClass(RoutingParameterStruct routingParameterStruct, TimeStruct[] timeStructs, TickerStruct[] tickerStructs) {
    }

    public void acceptLargeTradeTickerDetailForClass(RoutingParameterStruct routingParameterStruct, InternalTickerDetailStruct[] internalTickerDetailStructs) {
        boolean exception = false;
        if (acceptLargeTradeTickerDetailForClass3 != null) {
            acceptLargeTradeTickerDetailForClass3.beforeMethodCall();
        }
        try {
            delegate.acceptLargeTradeTickerDetailForClass(routingParameterStruct, internalTickerDetailStructs);
        } catch (RuntimeException ex) {
            exception = true;
            throw ex;
        } finally {
            if (acceptLargeTradeTickerDetailForClass3 != null) {
                acceptLargeTradeTickerDetailForClass3.incCalls(1);
                acceptLargeTradeTickerDetailForClass3.afterMethodCall();
                if (exception) {
                    acceptLargeTradeTickerDetailForClass3.incExceptions(1);
                }
            }
        }
    }
}
