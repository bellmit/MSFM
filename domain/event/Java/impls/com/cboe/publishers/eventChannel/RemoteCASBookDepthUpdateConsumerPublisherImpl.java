package com.cboe.publishers.eventChannel;

/**
 * RemoteCASBookDepthUpdateConsumerPublisherImpl.
 *
 * @author Eric J. Fredericks
 */

import com.cboe.idl.events.RemoteCASBookDepthUpdateEventConsumer;
import com.cboe.interfaces.events.RemoteCASBookDepthUpdateConsumer;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.util.RoutingParameterStruct;

import com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer;

public class RemoteCASBookDepthUpdateConsumerPublisherImpl extends BObject implements RemoteCASBookDepthUpdateConsumer
{
    private RemoteCASBookDepthUpdateEventConsumer delegate;


    public RemoteCASBookDepthUpdateConsumerPublisherImpl(RemoteCASBookDepthUpdateEventConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void subscribeBookDepthUpdateForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeBookDepthUpdateForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeBookDepthUpdateForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener,
                actionOnQueue);
        }

    }

    public void subscribeBookDepthUpdateForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeBookDepthUpdateForProductV2" +
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
           delegate.subscribeBookDepthUpdateForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener,
                actionOnQueue);
        }
    }

    public void unsubscribeBookDepthUpdateForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeBookDepthUpdateForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeBookDepthUpdateForClassV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                clientListener);
        }

    }

    public void unsubscribeBookDepthUpdateForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeBookDepthUpdateForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeBookDepthUpdateForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }

    }

    public void subscribeBookDepthUpdateForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing subscribeBookDepthUpdateForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        if (delegate != null)
        {
           delegate.subscribeBookDepthUpdateForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }
    }


    public void unsubscribeBookDepthUpdateForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeBookDepthUpdateForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        if (delegate != null)
        {
           delegate.unsubscribeBookDepthUpdateForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
        }

    }
}
