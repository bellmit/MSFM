package com.cboe.publishers.eventChannel;

/**
 * RemoteCASTickerConsumerPublisherImpl.
 *
 * @author Eric J. Fredericks
 */

import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.idl.events.RemoteCASTickerEventConsumer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.RemoteCASTickerConsumer;

public class RemoteCASTickerConsumerPublisherImpl extends BObject implements RemoteCASTickerConsumer
{
    private RemoteCASTickerEventConsumer delegate;


    public RemoteCASTickerConsumerPublisherImpl(RemoteCASTickerEventConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void subscribeTickerForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeTickerForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeTickerForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener,
                actionOnQueue);
        }

    }

    public void subscribeTickerForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeTickerForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeTickerForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener,
                actionOnQueue);
        }
    }

    public void unsubscribeTickerForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeTickerForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeTickerForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

    public void unsubscribeTickerForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeTickerForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeTickerForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }

    }

    public void subscribeTickerForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeTickerForProduct" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeTickerForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }
    }

    public void unsubscribeTickerForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeTickerForProduct" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeTickerForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }

    }

	public void subscribeLargeTradeLastSaleForClass(RoutingParameterStruct routingParameters, String casOrigin, String userId, String userSessionIOR, TickerConsumer clientListener, short actionOnQueue) {
		if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeLargeTradeLastSaleForClass" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeLargeTradeLastSaleForClass(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener,
                actionOnQueue);
        }
		
	}

	public void unsubscribeLargeTradeLastSaleForClass(RoutingParameterStruct routingParameters, String casOrigin, String userId, String userSessionIOR, TickerConsumer clientListener) {
		if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeLargeTradeLastSaleForClass" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeLargeTradeLastSaleForClass(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }
		
	}
}
