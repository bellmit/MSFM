package com.cboe.consumers.eventChannel;

import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.RemoteCASBookDepthUpdateConsumer;

public class RemoteCASBookDepthUpdateEventConsumerImpl extends com.cboe.idl.events.POA_RemoteCASBookDepthUpdateEventConsumer implements RemoteCASBookDepthUpdateConsumer
{

    private RemoteCASBookDepthUpdateConsumer delegate;

    public RemoteCASBookDepthUpdateEventConsumerImpl(RemoteCASBookDepthUpdateConsumer delegate)
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
        delegate.subscribeBookDepthUpdateForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener,
            actionOnQueue);
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
        delegate.subscribeBookDepthUpdateForProductV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener,
            actionOnQueue);
    }

    public void unsubscribeBookDepthUpdateForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener)
    {
        delegate.unsubscribeBookDepthUpdateForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void unsubscribeBookDepthUpdateForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener)
    {
           delegate.unsubscribeBookDepthUpdateForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
    }

    public void subscribeBookDepthUpdateForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer clientListener)
    {
        delegate.subscribeBookDepthUpdateForProduct(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener);
    }

    public void unsubscribeBookDepthUpdateForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer clientListener)
    {
           delegate.unsubscribeBookDepthUpdateForProduct(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
        throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
