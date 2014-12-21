package com.cboe.consumers.eventChannel;

import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.RemoteCASRecapConsumer;


public class RemoteCASRecapEventConsumerImpl extends com.cboe.idl.events.POA_RemoteCASRecapEventConsumer implements RemoteCASRecapConsumer
{

    private RemoteCASRecapConsumer delegate;

    public RemoteCASRecapEventConsumerImpl(RemoteCASRecapConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void subscribeRecapForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener,
            short actionOnQueue)
    {
        delegate.subscribeRecapForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener,
            actionOnQueue);
    }

    public void subscribeRecapForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener,
            short actionOnQueue)
    {
        delegate.subscribeRecapForProductV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener,
            actionOnQueue);
    }

    public void unsubscribeRecapForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
    {
        delegate.unsubscribeRecapForClassV2(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void unsubscribeRecapForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
    {
           delegate.unsubscribeRecapForProductV2(
                routingParameters,
                casOrigin,
                userId,
                userSessionIOR,
                productKey,
                clientListener);
    }

    public void subscribeRecapForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
    {
        delegate.subscribeRecapForClass(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void subscribeRecapForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
    {
        delegate.subscribeRecapForProduct(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            productKey,
            clientListener);
    }

    public void unsubscribeRecapForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
    {
        delegate.unsubscribeRecapForClass(
            routingParameters,
            casOrigin,
            userId,
            userSessionIOR,
            clientListener);
    }

    public void unsubscribeRecapForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
    {
           delegate.unsubscribeRecapForProduct(
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
